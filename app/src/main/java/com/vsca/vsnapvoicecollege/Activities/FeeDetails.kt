package com.vsca.vsnapvoicecollege.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class FeeDetails : AppCompatActivity() {


    @JvmField
    @BindView(R.id.imgBack)
    var imgBack: ImageView? = null

    @JvmField
    @BindView(R.id.lblLoading)
    var lblLoading: TextView? = null

    @JvmField
    @BindView(R.id.webviewFeedetails)
    var webviewFeedetails: WebView? = null

    @JvmField
    @BindView(R.id.progressBar)
    var progressBar: ProgressBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fee_details)
        ButterKnife.bind(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        CommonUtil.OnMenuClicks("FeeDetails")
        supportActionBar?.title = "Fee Details"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)

        loadFeeDetails(webviewFeedetails)
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
                progressBar!!.visibility = View.GONE
                lblLoading!!.visibility = View.GONE

                if (url != null && url.contains("paymentsucccess/returnhome")) {
                    CommonUtil.MenuFeeDetails = true
                    finish()
                }
            }
        }
        webviewFeedetails.loadUrl("https://gradit.voicesnap.com/Gradit/StudentFees?" + "memberid=" + CommonUtil.MemberId)

    }

    override fun onBackPressed() {
        if (webviewFeedetails!!.canGoBack()) {
            CommonUtil.MenuFeeDetails = true
            webviewFeedetails!!.goBack()
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
            progressBar!!.visibility = View.VISIBLE
            lblLoading!!.visibility = View.VISIBLE
        }
    }

    inner class MyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            // Update progress bar with loading progress
            progressBar!!.progress = newProgress
        }
    }
}
