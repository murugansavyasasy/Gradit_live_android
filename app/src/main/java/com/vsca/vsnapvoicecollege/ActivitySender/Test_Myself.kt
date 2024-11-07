package com.vsca.vsnapvoicecollege.ActivitySender

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.R


class Test_Myself : AppCompatActivity() {

//    @JvmField
//    @BindView(R.id.btnUpload)
//    var btnUpload: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_myself)
        ButterKnife.bind(this)


//        btnUpload!!.setOnClickListener {
//            //   uploadVideo("/storage/emulated/0/DCIM/Camera/VID_20240308_102957.mp4")
//            val accessToken = "8d74d8bf6b5742d39971cc7d3ffbb51a"
//
//            // Replace with the path to your video file
//            val videoFilePath = "/storage/emulated/0/DCIM/Camera/VID_20240308_102957.mp4"
//
//            // Call uploadVideo method
//            VimeoUploader.uploadVideo(
//                accessToken,
//                videoFilePath,
//                object : VimeoUploader.UploadCompletionListener {
//                    override fun onUploadComplete(success: Boolean) {
//                        if (success) {
//                            // Handle successful upload
//                            Log.d("VimeoUploader", "Video upload successful")
//                        } else {
//                            // Handle upload failure
//                            Log.e("VimeoUploader", "Video upload failed")
//                        }
//                    }
//                })
//        }
    }
}