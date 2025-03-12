package com.vsca.vsnapvoicecollege.AWS;

public interface UploadCallback {
        void onUploadSuccess(String response,String isFileUploaded);
        void onUploadError(String error);
}
