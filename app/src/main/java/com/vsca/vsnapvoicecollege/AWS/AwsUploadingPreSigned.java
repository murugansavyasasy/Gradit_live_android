package com.vsca.vsnapvoicecollege.AWS;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.vsca.vsnapvoicecollege.Interfaces.ApiInterfaces;
import com.vsca.vsnapvoicecollege.R;
import com.vsca.vsnapvoicecollege.Repository.RestClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;

public class AwsUploadingPreSigned {

    String isBucket = "";

    public void getPreSignedUrl(String isFilePathUrl, String instituteID, UploadCallback uploadCallback) {


        String bucketPath = "";
        String currentDate = CurrentDatePicking.getCurrentDate();

        isBucket = AWSKeys.BUCKET_NAME;
        bucketPath = currentDate + "/" + instituteID;

        Log.d("isBucket", isBucket);
        File isFilePth = new File(isFilePathUrl);
        Log.d("isFilePth.getName()", isFilePth.getName().toString());
        String fileExtension = getFileExtension(isFilePth.getName());
        MediaType mediaType = null;

        try {
            mediaType = getMediaType(fileExtension);
            System.out.println("MediaType: " + mediaType);
        } catch (UnsupportedOperationException e) {
            System.err.println(e.getMessage());
        }

        String[] parts = String.valueOf(mediaType).split("/");
        String isFileType = "";
        if (parts.length == 2) {
            String type = parts[0];   // "image"
            String subtype = parts[1]; // "jpeg"
            isFileType = type;
        }


        String GET_PRESIGNED_BASE_URL = "https://gradit.voicesnap.com/api/AppDetailsBal/";
        RestClient.Companion.changeApiBaseUrl(GET_PRESIGNED_BASE_URL);

        String isFileName = getFileNameFromPath(isFilePathUrl);

        ApiInterfaces apiService = RestClient.Companion.getClient().create(ApiInterfaces.class);
        Call<JsonArray> call = apiService.getPreSignedUrl(isBucket, isFileName, bucketPath, String.valueOf(isFileType));

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                Log.d("attendance:code-res", response.code() + " - " + response);
                try {
                    JSONArray jsonArray = new JSONArray(response.body().toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        String ispresignedurl = jsonObject.getString("presignedurl");
                        String isfileurl = jsonObject.getString("fileurl");
                        isAwsUpload(ispresignedurl, isFilePathUrl, isfileurl, uploadCallback);
                    }
                } catch (Exception e) {
                    String errorMessage = response.message(); // Get the error message from the response
                    Log.e("Response Error", errorMessage != null ? errorMessage : "Unknown error occurred");
                    uploadCallback.onUploadError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
//                Toast.makeText(activity, activity.getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                uploadCallback.onUploadError(t.getMessage());
            }
        });
    }

    public String getFileNameFromPath(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }

    private void isAwsUpload(String presignedUrl, String filePath, String isFileUploadUrl, UploadCallback uploadCallback) {

        byte[] imageData = getImageData(filePath); // Replace with the actual byte array of your image
        File isFilePth = new File(filePath);
        String fileExtension = getFileExtension(isFilePth.getName());
        MediaType mediaType = null;
        try {
            mediaType = getMediaType(fileExtension);
            System.out.println("MediaType: " + mediaType);
        } catch (UnsupportedOperationException e) {
            System.err.println(e.getMessage());
        }

//        String[] parts = String.valueOf(mediaType).split("/");
//        String isFileType = "";
//        if (parts.length == 2) {
//            String type = parts[0];   // "image"
//            String subtype = parts[1]; // "jpeg"
//            isFileType = type;
//        }

        S3Uploader uploader = new S3Uploader();
        uploader.uploadImageToS3(presignedUrl, imageData, String.valueOf(mediaType), new S3Uploader.UploadCallback() {
            @Override
            public void onSuccess(String message) {
                Log.d("S3Upload", message);
                uploadCallback.onUploadSuccess(message, isFileUploadUrl);
            }

            @Override
            public void onError(Exception error) {
                Log.e("S3Upload", "Error: " + error.getMessage(), error);
            }
        });
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot > 0 && lastIndexOfDot < fileName.length() - 1) {
            return fileName.substring(lastIndexOfDot + 1).toLowerCase();
        }
        return ""; // Return empty string if no extension found
    }

    private byte[] getImageData(String filePath) {
        File imageFile = new File(filePath);
        byte[] imageData = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imageData = Files.readAllBytes(imageFile.toPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageData;
    }

    public MediaType getMediaType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
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
            // Add more file types as needed
            default:
                throw new UnsupportedOperationException("Unsupported file type: " + fileExtension);
        }
    }
}
