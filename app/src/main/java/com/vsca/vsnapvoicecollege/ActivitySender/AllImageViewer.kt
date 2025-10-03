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
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Adapters.ImageViewer
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.databinding.ActivityAllImageViewerBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding

class AllImageViewer : ActionBarActivity() {


    lateinit var imageViewAdapter: ImageViewer
    private var isPosition = 0
    private lateinit var binding: ActivityAllImageViewerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAllImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
         ActionbarWithoutBottom(this)
        isPositionUpdate(isPosition)
        binding.lmgback!!.setOnClickListener {
            onBackPressed()
        }

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        binding.imgNxtright!!.setOnClickListener {
            if (CommonUtil.isImageViewList.size > 1) {
                binding.progressBar!!.visibility = ProgressBar.VISIBLE
                isPosition++
                isPositionUpdate(isPosition)
            }
        }

        binding.imgNxtleft!!.setOnClickListener {
            if (CommonUtil.isImageViewList.size > 1) {
                if (isPosition > 0) {
                    binding.progressBar!!.visibility = ProgressBar.VISIBLE
                    isPosition--
                    isPositionUpdate(isPosition)
                }
            }
        }
    }

    private fun isPositionUpdate(position: Int) {
        Log.d("Position", position.toString())
        if (position == 0) {
            binding.imgNxtleft!!.isClickable = false
            binding.imgNxtleft!!.setImageDrawable(resources.getDrawable(R.drawable.baseline_keyboard_arrow_left_brown))
        } else {
            binding.imgNxtleft!!.isClickable = true
            binding.imgNxtleft!!.setImageDrawable(resources.getDrawable(R.drawable.baseline_keyboard_arrow_left_24))
        }

        if (position == CommonUtil.isImageViewList.size - 1) {
            binding.imgNxtright!!.isClickable = false
            binding.imgNxtright!!.setImageDrawable(resources.getDrawable(R.drawable.baseline_chevron_right_brown))
        } else {
            binding.imgNxtright!!.isClickable = true
            binding.imgNxtright!!.setImageDrawable(resources.getDrawable(R.drawable.baseline_chevron_right_24))
        }
        isUpdateFile(position)
    }

    private fun isUpdateFile(position: Int) {
        if (CommonUtil.isImageViewList[position].filetype.equals("image")) {
            binding.progressBar!!.visibility = ProgressBar.GONE
            binding.webview!!.visibility = View.GONE
            binding.imgView!!.visibility = View.VISIBLE
            Glide.with(this)
                .load(CommonUtil.isImageViewList[position].filepath.toString())
                .apply(RequestOptions.centerInsideTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imgView!!)
        } else {
            binding.webview!!.visibility = View.VISIBLE
            binding.imgView!!.visibility = View.GONE
            binding.webview!!.settings.javaScriptEnabled = true
            binding.webview!!.setVerticalScrollbarOverlay(true)
            binding.webview!!.settings.setSupportZoom(true)
            binding.webview!!.settings.builtInZoomControls = true
            binding.webview!!.settings.displayZoomControls = false
            binding.webview!!.webViewClient = MyWebViewClient()
            binding.webview!!.webChromeClient = MyWebChromeClient()
            binding.webview!!.loadUrl("https://docs.google.com/gview?embedded=true&url=${CommonUtil.isImageViewList[position].filepath}")
        }
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            // Show ProgressBar when page starts loading
            binding.progressBar!!.visibility = ProgressBar.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            // Hide ProgressBar when page finishes loading
            binding.progressBar!!.visibility = ProgressBar.GONE
        }
    }

    private inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            // Update ProgressBar with loading progress
            binding.progressBar!!.progress = newProgress
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_all_image_viewer
}