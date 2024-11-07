package com.vsca.vsnapvoicecollege.AWS;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public
class S3Uploader {

    private static final String TAG = "S3Uploader";
    public S3UploadInterface s3UploadInterface;
    private final Context context;
    private final TransferUtility transferUtility;

    public S3Uploader(Context context) {
        this.context = context;
        transferUtility = AmazonUtil.getTransferUtility(context);

    }

    public void initUpload(String filePath, String contenttype, String collegeid, String fileNameDateTime) {

        File file = new File(filePath);
        Log.d("contenttype", contenttype);
        ObjectMetadata myObjectMetadata = new ObjectMetadata();
        myObjectMetadata.setContentType(contenttype);
        String mediaUrl = file.getName();
        Log.d("mediaUrl", mediaUrl);

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        TransferObserver observer = transferUtility.upload(AWSKeys.BUCKET_NAME, collegeid + "/" + currentDate + "/" + fileNameDateTime + "_" + mediaUrl, file, CannedAccessControlList.PublicRead);
        observer.setTransferListener(new UploadListener());

    }

    public void setOns3UploadDone(S3UploadInterface s3UploadInterface) {
        this.s3UploadInterface = s3UploadInterface;
    }

    public interface S3UploadInterface {
        void onUploadSuccess(String response);

        void onUploadError(String response);
    }

    private class UploadListener implements TransferListener {

        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
            s3UploadInterface.onUploadError(e.toString());
            s3UploadInterface.onUploadError("Error");
        }

        @Override

        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d(TAG, "onStateChanged: " + id + ", " + newState);
            if (newState == TransferState.COMPLETED) {
                Log.d("Success", "Success");
                s3UploadInterface.onUploadSuccess("Success");
            }
        }
    }
}
