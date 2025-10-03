package com.vsca.vsnapvoicecollege.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.databinding.FeeDetailsBinding

class FeeDetails : AppCompatActivity() {

    private lateinit var binding: FeeDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FeeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        CommonUtil.OnMenuClicks("FeeDetails")
        supportActionBar?.title = "Fee Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        loadFeeDetails(binding.webviewFeedetails)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadFeeDetails(webviewFeedetails: WebView?) {


        webviewFeedetails!!.settings.javaScriptEnabled = true
        webviewFeedetails.settings.loadWithOverviewMode = true
        webviewFeedetails.settings.useWideViewPort = true
        webviewFeedetails.settings.setSupportZoom(false)
        webviewFeedetails.settings.builtInZoomControls = false
        webviewFeedetails.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        webviewFeedetails.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webviewFeedetails.settings.domStorageEnabled = true
        webviewFeedetails.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY

        webviewFeedetails.isScrollbarFadingEnabled = true
        webviewFeedetails.webViewClient = MyWebViewClient()
        webviewFeedetails.webChromeClient = MyWebChromeClient()
        webviewFeedetails.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar!!.visibility = View.GONE
                binding.lblLoading!!.visibility = View.GONE

                if (url != null && url.contains("paymentsucccess/returnhome")) {
                    CommonUtil.MenuFeeDetails = true
                    finish()
                }
            }
        }
        webviewFeedetails.loadUrl("https://gradit.voicesnap.com/Gradit/StudentFees?" + "memberid=" + CommonUtil.MemberId)

    }

    override fun onBackPressed() {
        if (binding.webviewFeedetails!!.canGoBack()) {
            CommonUtil.MenuFeeDetails = true
            binding.webviewFeedetails!!.goBack()
        } else {
            CommonUtil.MenuFeeDetails = true
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class MyWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            // Show the progress bar when page starts loading
            binding.progressBar!!.visibility = View.VISIBLE
            binding.lblLoading!!.visibility = View.VISIBLE
        }
    }

    inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            // Update progress bar with loading progress
            binding.progressBar!!.progress = newProgress
        }
    }
}
