package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume


import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.vsca.vsnapvoicecollege.R

import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutResumepreviewBinding


class ResumePreviewActivity : AppCompatActivity() {

    var appViewModel: App? = null
    private lateinit var binding: LayoutResumepreviewBinding





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutResumepreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        binding.commonBottomResumeBuilder.btnDefault2.text=resources.getString(R.string.download)
        binding.commonBottomResumeBuilder.btnDefault1.text=resources.getString(R.string.change_template)
        binding.commonBottomResumeBuilder.imgDefault.visibility=View.VISIBLE

        val pdfUrl = intent.getStringExtra("TemplateDocumentURL")

        // Enable JS and zoom
        val webSettings = binding.webviewDocument.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        // Google Docs PDF viewer URL
        val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$pdfUrl"
        binding.webviewDocument.loadUrl(googleDocsUrl)

        binding.imgback!!.setOnClickListener {
            onBackPressed()
        }
    }

}