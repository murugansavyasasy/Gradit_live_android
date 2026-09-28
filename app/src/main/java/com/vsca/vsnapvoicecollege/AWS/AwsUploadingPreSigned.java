package com.vsca.vsnapvoicecollege.AWS;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import com.google.gson.JsonObject;
import com.vsca.vsnapvoicecollege.Interfaces.ApiInterfaces;
import com.vsca.vsnapvoicecollege.Repository.RestClient;
import com.vsca.vsnapvoicecollege.Utils.SharedPreference;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import okhttp3.MediaType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class AwsUploadingPreSigned {

    String isBucket = "";

    // =====================================================
    // MAIN METHOD (UNCHANGED FLOW)
    // =====================================================
    public void getPreSignedUrl(
            Activity activity,
            String isFilePathUrl,
            String instituteID,
            UploadCallback uploadCallback
    ) {

        try {

            File localFile = new File(isFilePathUrl);

            // 🔥 SAFETY CHECK
            if (!localFile.exists()) {
                uploadCallback.onUploadError("File not found: " + isFilePathUrl);
                return;
            }

            String bucketPath = CurrentDatePicking.getCurrentDate() + "/" + instituteID;
            isBucket = AWSKeys.BUCKET_NAME;

            String fileName = localFile.getName();
            String fileExtension = getFileExtension(fileName);

            MediaType mediaType = getMediaType(fileExtension);
            String fileType = mapBackendFileType(mediaType);

            Log.d("AWS_DEBUG", "fileName=" + fileName);
            Log.d("AWS_DEBUG", "mediaType=" + mediaType);
            Log.d("AWS_DEBUG", "fileType=" + fileType);

            // Change base URL
            String baseUrl = RestClient.Companion.getClient().baseUrl().toString();
            RestClient.Companion.changeApiBaseUrl(
                    "https://api.schoolchimes.com/nodejs/api/MergedApi/"
            );

            Retrofit retrofit = RestClient.Companion.getClient();
            ApiInterfaces apiService = retrofit.create(ApiInterfaces.class);

            Call<JsonObject> call = apiService.getPreSignedUrl(
                    isBucket,
                    fileName,
                    bucketPath,
                    fileType
            );

            call.enqueue(new Callback<JsonObject>() {

                @Override
                public void onResponse(Call<JsonObject> call,
                                       retrofit2.Response<JsonObject> response) {

                    try {
                        if (response.isSuccessful() && response.body() != null) {

                            JSONObject json =
                                    new JSONObject(response.body().toString());

                            JSONObject data =
                                    json.optJSONObject("data");

                            if (data == null) {
                                uploadCallback.onUploadError("Invalid response");
                                return;
                            }

                            String presignedUrl =
                                    data.optString("presignedUrl");

                            String fileUrl =
                                    data.optString("fileUrl");

                            String restoreBase =
                                    SharedPreference.INSTANCE.getSH_Baseurl(activity);
                            RestClient.Companion.changeApiBaseUrl(restoreBase);

                            // 🔥 AWS UPLOAD
                            isAwsUpload(
                                    presignedUrl,
                                    isFilePathUrl,
                                    fileUrl,
                                    uploadCallback,
                                    activity
                            );

                        } else {
                            uploadCallback.onUploadError(
                                    "Server error: " + response.code()
                            );
                        }

                    } catch (Exception e) {
                        uploadCallback.onUploadError(e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    uploadCallback.onUploadError(t.getMessage());
                }
            });

        } catch (Exception e) {
            uploadCallback.onUploadError(e.getMessage());
        }
    }

    // =====================================================
    // AWS UPLOAD (SAFE VERSION)
    // =====================================================
    private void isAwsUpload(
            String presignedUrl,
            String filePath,
            String isFileUploadUrl,
            UploadCallback uploadCallback,
            Activity activity
    ) {

        try {

            File file = new File(filePath);

            // 🔥 SAFETY CHECK
            if (!file.exists()) {
                uploadCallback.onUploadError("File missing: " + filePath);
                return;
            }

            byte[] imageData = getImageData(filePath);

            if (imageData == null || imageData.length == 0) {
                uploadCallback.onUploadError("Empty file data");
                return;
            }

            String extension = getFileExtension(file.getName());
            MediaType mediaType = getMediaType(extension);

            S3Uploader uploader = new S3Uploader();
            uploader.uploadImageToS3(
                    presignedUrl,
                    imageData,
                    String.valueOf(mediaType),
                    new S3Uploader.UploadCallback() {

                        @Override
                        public void onSuccess(String message) {
                            uploadCallback.onUploadSuccess(
                                    message,
                                    isFileUploadUrl
                            );
                        }

                        @Override
                        public void onError(Exception error) {
                            uploadCallback.onUploadError(error.getMessage());
                        }
                    }
            );

        } catch (Exception e) {
            uploadCallback.onUploadError(e.getMessage());
        }
    }

    // =====================================================
    // FILE BYTE READER (UNCHANGED API)
    // =====================================================
    private byte[] getImageData(String filePath) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Files.readAllBytes(new File(filePath).toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // =====================================================
    // EXTENSION
    // =====================================================
    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase();
        }
        return "";
    }

    // =====================================================
    // YOUR EXISTING MEDIA TYPE METHOD (EXTENDED)
    // =====================================================
    public MediaType getMediaType(String fileExtension) {

        switch (fileExtension) {

            case "jpg":
            case "jpeg":
                return MediaType.parse("image/jpeg");

            case "png":
                return MediaType.parse("image/png");

            case "pdf":
                return MediaType.parse("application/pdf");

            case "mp3":
                return MediaType.parse("audio/mpeg");

            case "wav":
                return MediaType.parse("audio/wav");

            case "doc":
            case "docx":
                return MediaType.parse(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                );

            case "xls":
            case "xlsx":
                return MediaType.parse(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                );

            case "ppt":
            case "pptx":
                return MediaType.parse(
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation"
                );

            case "txt":
                return MediaType.parse("text/plain");

            default:
                throw new UnsupportedOperationException(
                        "Unsupported file type: " + fileExtension
                );
        }
    }

    // =====================================================
    // 🔥 BACKEND FILE TYPE MAPPING (CRITICAL FIX)
    // =====================================================
    private String mapBackendFileType(MediaType mediaType) {

        if (mediaType == null) return "document";

        String type = mediaType.toString();

        if (type.startsWith("image")) return "image";
        if (type.startsWith("video")) return "video";
        if (type.startsWith("audio")) return "audio";
        if (type.equals("application/pdf")) return "pdf";

        return "document"; // doc, docx, ppt, xls, txt
    }
}

