package com.vsca.vsnapvoicecollege.ActivitySender

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Adapters.ImageViewer
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading

class AllImageViewer : ActionBarActivity() {

    @JvmField
    @BindView(R.id.lmgback)
    var lmgback: ImageView? = null

    @JvmField
    @BindView(R.id.imgNxtright)
    var imgNxtright: ImageView? = null

    @JvmField
    @BindView(R.id.imgNxtleft)
    var imgNxtleft: ImageView? = null

    @JvmField
    @BindView(R.id.progressBar)
    var progressBar: ProgressBar? = null

    @JvmField
    @BindView(R.id.webview)
    var webview: WebView? = null

    @JvmField
    @BindView(R.id.imgView)
    var imgView: PhotoView? = null

    lateinit var imageViewAdapter: ImageViewer
    private var isPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this)
        isPositionUpdate(isPosition)
        lmgback!!.setOnClickListener {
            onBackPressed()
        }

        imgNxtright!!.setOnClickListener {
            if (CommonUtil.isImageViewList.size > 1) {
                progressBar!!.visibility = ProgressBar.VISIBLE
                isPosition++
                isPositionUpdate(isPosition)
            }
        }

        imgNxtleft!!.setOnClickListener {
            if (CommonUtil.isImageViewList.size > 1) {
                if (isPosition > 0) {
                    progressBar!!.visibility = ProgressBar.VISIBLE
                    isPosition--
                    isPositionUpdate(isPosition)
                }
            }
        }
    }

    private fun isPositionUpdate(position: Int) {
        Log.d("Position", position.toString())
        if (position == 0) {
            imgNxtleft!!.isClickable = false
            imgNxtleft!!.setImageDrawable(resources.getDrawable(R.drawable.baseline_keyboard_arrow_left_brown))
        } else {
            imgNxtleft!!.isClickable = true
            imgNxtleft!!.setImageDrawable(resources.getDrawable(R.drawable.baseline_keyboard_arrow_left_24))
        }

        if (position == CommonUtil.isImageViewList.size - 1) {
            imgNxtright!!.isClickable = false
            imgNxtright!!.setImageDrawable(resources.getDrawable(R.drawable.baseline_chevron_right_brown))
        } else {
            imgNxtright!!.isClickable = true
            imgNxtright!!.setImageDrawable(resources.getDrawable(R.drawable.baseline_chevron_right_24))
        }
        isUpdateFile(position)
    }

    private fun isUpdateFile(position: Int) {
        if (CommonUtil.isImageViewList[position].filetype.equals("image")) {
            progressBar!!.visibility = ProgressBar.GONE
            webview!!.visibility = View.GONE
            imgView!!.visibility = View.VISIBLE
            Glide.with(this)
                .load(CommonUtil.isImageViewList[position].filepath.toString())
                .apply(RequestOptions.centerInsideTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imgView!!)
        } else {
            webview!!.visibility = View.VISIBLE
            imgView!!.visibility = View.GONE
            webview!!.settings.javaScriptEnabled = true
            webview!!.setVerticalScrollbarOverlay(true)
            webview!!.settings.setSupportZoom(true)
            webview!!.settings.builtInZoomControls = true
            webview!!.settings.displayZoomControls = false
            webview!!.webViewClient = MyWebViewClient()
            webview!!.webChromeClient = MyWebChromeClient()
            webview!!.loadUrl("https://docs.google.com/gview?embedded=true&url=${CommonUtil.isImageViewList[position].filepath}")
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            // Show ProgressBar when page starts loading
            progressBar!!.visibility = ProgressBar.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            // Hide ProgressBar when page finishes loading
            progressBar!!.visibility = ProgressBar.GONE
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            // Update ProgressBar with loading progress
            progressBar!!.progress = newProgress
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_all_image_viewer
}