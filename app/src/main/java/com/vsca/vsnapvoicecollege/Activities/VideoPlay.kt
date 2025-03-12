package com.vsca.vsnapvoicecollege.Activities

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityVideoPlayBinding

class VideoPlay : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val VideoID = intent.getStringExtra("iframe")

        if (VideoID != null) {
            Log.d("videoid", VideoID)
        }

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val appread = intent.getStringExtra("appread")
        val detailid = intent.getStringExtra("detailid")

        binding.imgheaderBack.setOnClickListener {
            super.onBackPressed()
        }

        binding.lblVideoTitle!!.text = title
        binding.lblVideoDescription!!.text = description
        if (appread.equals("0")) {
            BaseActivity.AppReadStatus(
                this, "video", detailid!!
            )
        }
        LoadVideo(VideoID!!)
    }

    private fun LoadVideo(VideoID: String) {

        Log.d("testvideo", VideoID)

        binding.mywebview!!.setBackgroundColor(resources.getColor(R.color.clr_black))
        binding.mywebview!!.settings.javaScriptEnabled = true
        binding.mywebview!!.settings.domStorageEnabled = true
        binding.mywebview!!.settings.setSupportZoom(false)
        binding.mywebview!!.settings.builtInZoomControls = false
        binding.mywebview!!.settings.displayZoomControls = false
        binding.mywebview!!.isScrollContainer = false
        binding.mywebview!!.isHorizontalScrollBarEnabled = false
        binding.mywebview!!.isVerticalScrollBarEnabled = false

        binding.mywebview!!.setOnTouchListener(View.OnTouchListener { v, event -> event.action == MotionEvent.ACTION_MOVE })
        binding.mywebview!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                webView: WebView,
                request: WebResourceRequest
            ): Boolean {
                return true
            }
        }
        binding.mywebview!!.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                setProgress(progress * 100)
                if (progress == 100) {

                }
            }
        }
        binding.mywebview!!.settings.pluginState = WebSettings.PluginState.ON
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels


        val data_html = "<!DOCTYPE html><html> " +
                "<head>" +
                " <meta charset=\"UTF-8\">" +
                "</head>" +
                " <body style=\"background:black;margin:0 0 0 0; padding:0 0 0 0;\"> " +
                "<div class=\"vimeo\">" +
                "<iframe  style=\"position:absolute;top:0;bottom:0;width:100%;height:100%\" src=\"" + VideoID + "\" frameborder=\"0\">" +
                "</iframe>" +
                " </div>" +
                " </body>" +
                " </html> "

        Log.d("datahtmlweb", data_html)
        binding.mywebview!!.loadData(data_html, "text/html", "UTF-8")


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}