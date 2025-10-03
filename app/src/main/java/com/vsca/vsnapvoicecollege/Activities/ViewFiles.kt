package com.vsca.vsnapvoicecollege.Activities

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.widget.ZoomControls
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.DownloadImage
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityViewFilesBinding
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors

class ViewFiles : AppCompatActivity() {

    private var imagePathList = ArrayList<String>()
    var Filepath: String? = ""
    var storagepath: String? = null
    var PlayPath: String? = null
    var detailsid: String? = null
    var mImage: Bitmap? = null
    val myExecutor = Executors.newSingleThreadExecutor()
    val myHandler = Handler(Looper.getMainLooper())
    private val VOICE_FOLDER: String = "Gradit/Images/"
    private lateinit var binding: ActivityViewFilesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewFilesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Filepath = intent.getStringExtra("images")!!

        binding.imgClose.setOnClickListener {
            super.onBackPressed()
        }

        binding.Fabdownload.setOnClickListener {
            isDownload()
        }

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide.with(this)
            .load(Filepath)
            .placeholder(circularProgressDrawable)
            .into(binding.imgFile!!)

        binding.imgFile!!.setOnTouchListener { v, m -> // Perform tasks here
            binding.zoomControls!!.show()
            false
        }

        // This function will be automatically called out,when
        // zoom in button is being pressed
        binding.zoomControls!!.setOnZoomInClickListener(
            View.OnClickListener {
                val x: Float = binding.imgFile!!.scaleX
                val y: Float = binding.imgFile!!.scaleY

                // setting the new scale
                binding.imgFile!!.scaleX = (x + 0.5f)
                binding.imgFile!!.scaleY = (y + 0.5f)
                binding.zoomControls!!.hide()
            }
        )

        // This function will be called when
        // zoom out button is pressed
        binding.zoomControls!!.setOnZoomOutClickListener(
            View.OnClickListener {
                val x: Float = binding.imgFile!!.scaleX
                val y: Float = binding.imgFile!!.scaleY
                if (x == 1f && y == 1f) {
                    binding.imgFile!!.scaleX = x
                    binding.imgFile!!.scaleY = y
                    binding.zoomControls!!.hide()
                } else {
                    // setting the new scale
                    binding.imgFile!!.scaleX = (x - 0.5f)
                    binding.imgFile!!.scaleY = (y - 0.5f)
                    // hiding the zoom controls
                    binding.zoomControls!!.hide()
                }
            }
        )
    }

    fun isDownload() {

        myExecutor.execute {
            mImage = mLoad(Filepath.toString())
            myHandler.post {
                binding.imgFile!!.setImageBitmap(mImage)
                if (mImage != null) {
                    mSaveMediaToStorage(mImage)
                }
            }
        }
        ImageDownloadlocalstorage()

    }

    private fun mLoad(string: String): Bitmap? {
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
        }
        return null
    }

    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }


    private fun ImageDownloadlocalstorage() {

        val filepath: String
        filepath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator + "Gradit/" + "Images/" + Filepath!!.substring(
                Filepath!!.lastIndexOf('/') + 1
            )
        } else {
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Gradit/" + "Images/" + Filepath!!.substring(
                Filepath!!.lastIndexOf(
                    '/'
                ) + 1
            )
        }
        val fileName: String =
            Filepath!!.substring(Filepath!!.lastIndexOf('/') + 1, Filepath!!.length)
        val fileDir = File(filepath)
        if (!fileDir.exists()) {
            DownloadImage.downloadSampleFile(
                this,
                Filepath!!,
                VOICE_FOLDER,
                fileName
            )
        } else {
            CommonUtil.ApiAlertFinish(this, "File Already exists...!")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}