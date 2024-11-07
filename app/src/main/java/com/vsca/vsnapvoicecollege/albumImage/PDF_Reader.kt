package com.vsca.vsnapvoicecollege.albumImage

import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.vsca.vsnapvoicecollege.Utils.CustomLoading


class PDF_Reader : AppCompatActivity() {

    lateinit var webView: WebView
    lateinit var imgClose: ImageView
    var Filepath: String? = ""
    lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.vsca.vsnapvoicecollege.R.layout.activity_pdf_reader)

        webView = findViewById(com.vsca.vsnapvoicecollege.R.id.web_view)
        imgClose = findViewById(com.vsca.vsnapvoicecollege.R.id.imgClose)
        Filepath = intent.getStringExtra("PdfView")!!

        val progressDialog = CustomLoading.createProgressDialog(this@PDF_Reader)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                progressDialog.show()
                setProgress(progress * 100)
                if (progress == 100) {
                    progressDialog.dismiss()
                }
            }
        }
        webView.settings.javaScriptEnabled = true
        try {
            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$Filepath")
            progressDialog.dismiss()
        } catch (Exception: Exception) {
            Log.d("Exception", Exception.toString())
        }
        imgClose.setOnClickListener {
            onBackPressed()
        }
    }
}