package com.vsca.vsnapvoicecollege.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.vsca.vsnapvoicecollege.R

class VideoPlay : AppCompatActivity() {

    @JvmField
    @BindView(R.id.lblVideoTitle)
    var lblVideoTitle: TextView? = null

    @JvmField
    @BindView(R.id.lblVideoDescription)
    var lblVideoDescription: TextView? = null

    @JvmField
    @BindView(R.id.mywebview)
    var mywebview: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        ButterKnife.bind(this)


        val VideoID = intent.getStringExtra("iframe")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val appread = intent.getStringExtra("appread")
        val detailid = intent.getStringExtra("detailid")

        lblVideoTitle!!.text = title
        lblVideoDescription!!.text = description
        if (appread.equals("0")) {

            BaseActivity.AppReadStatus(
                this,
                "video",
                detailid!!
            )
        }


        LoadVideo(VideoID!!)

    }

    @OnClick(R.id.imgheaderBack)
    fun imgbackClick() {
        onBackPressed();
    }

    private fun LoadVideo(VideoID: String) {
        Log.d("testvideo", "video")
        mywebview!!.setBackgroundColor(resources.getColor(R.color.clr_black))
        mywebview!!.getSettings().setJavaScriptEnabled(true)
        mywebview!!.getSettings().setAppCacheEnabled(true)
        mywebview!!.getSettings().setDomStorageEnabled(true)
        mywebview!!.getSettings().setSupportZoom(false)
        mywebview!!.getSettings().setBuiltInZoomControls(false)
        mywebview!!.getSettings().setDisplayZoomControls(false)
        mywebview!!.setScrollContainer(false)
        mywebview!!.setHorizontalScrollBarEnabled(false)
        mywebview!!.setVerticalScrollBarEnabled(false)

        mywebview!!.setOnTouchListener(View.OnTouchListener { v, event -> event.action == MotionEvent.ACTION_MOVE })
        mywebview!!.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                webView: WebView,
                request: WebResourceRequest
            ): Boolean {

                return true
            }
        })
        mywebview!!.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                setProgress(progress * 100)
                if (progress == 100) {
                }
            }
        })
        mywebview!!.getSettings().setPluginState(WebSettings.PluginState.ON)
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

        mywebview!!.loadData(data_html, "text/html", "UTF-8")
        Log.d("load", data_html)
    }
}