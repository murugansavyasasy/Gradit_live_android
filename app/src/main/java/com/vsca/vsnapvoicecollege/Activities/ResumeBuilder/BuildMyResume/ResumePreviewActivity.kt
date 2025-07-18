package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.ResumeListAdapter
import com.vsca.vsnapvoicecollege.Model.GetResumeTitleData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.Utils.CustomSwitch

import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutResumepreviewBinding


class ResumePreviewActivity : AppCompatActivity() {

    var appViewModel: App? = null
    private lateinit var binding: LayoutResumepreviewBinding
    var isMemberID=-1
    var isPDF_URL=""
    var isScreenName=""
    var isFileName=""
    private var savedResumeList: List<GetResumeTitleData> = emptyList()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutResumepreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        binding.commonBottomResumeBuilder.btnDefault2.text=resources.getString(R.string.download)
        binding.commonBottomResumeBuilder.btnDefault1.text=resources.getString(R.string.change_template)
        binding.commonBottomResumeBuilder.imgDefault.visibility=View.VISIBLE
        isPDF_URL= intent.getStringExtra("TemplateDocumentURL").toString()
        isMemberID= intent.getIntExtra("MemberID",-1)
        isScreenName= intent.getStringExtra("ScreenName").toString()
        isFileName= intent.getStringExtra("FileName").toString()
        GetProfileResume()


        if(isScreenName=="MyResumes"){
            binding.txtTitle.text="View My Resume"
            binding.lblPdfName.visibility=View.VISIBLE
            binding.lblPdfName.text=isFileName
            binding.lbldelete.visibility=View.VISIBLE
        }
        if (isScreenName=="BuildMyResume"){
            binding.txtTitle.text="Preview My Resume"
            binding.lblPdfName.visibility=View.GONE
            binding.lblPdfName.text=""
            binding.lbldelete.visibility=View.GONE
        }

        appViewModel?.ResumeBuilderProfileResume!!.observe(this) { response ->
            if (response != null) {

                if (response.status) {
                    savedResumeList = response.data.resumeTitle
                    Log.d("savedResumeList",savedResumeList.toString())
                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }


        val webSettings = binding.webviewDocument.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        binding.webviewDocument.setBackgroundColor(Color.TRANSPARENT)
        binding.webviewDocument.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        binding.zoomIn.setOnClickListener {
            binding.webviewDocument.zoomIn()
        }

        binding.zoomOut.setOnClickListener {
            binding.webviewDocument.zoomOut()
        }

        val progressDialog = CustomLoading.createProgressDialog(this)
        progressDialog?.show()

        binding.webviewDocument.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressDialog?.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressDialog?.dismiss()
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                progressDialog?.dismiss()
            }
        }

        if (!isPDF_URL.isNullOrEmpty()) {
            val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$isPDF_URL"
            binding.webviewDocument.loadUrl(googleDocsUrl)
        } else {
            Toast.makeText(this, "Invalid PDF URL", Toast.LENGTH_SHORT).show()
            progressDialog?.dismiss()
        }

        binding.imgback.setOnClickListener {
            onBackPressed()
        }

        binding.commonBottomResumeBuilder.btnSave.setOnClickListener{
            showResumeTitleDialog(this,isMemberID,isPDF_URL)
        }

        binding.commonBottomResumeBuilder.btnDefault1.setOnClickListener{
            if (savedResumeList.size>1){
                showResumeListDialog(this,savedResumeList)
            }
            else{
                Toast.makeText(this, "No Resume Avaiable!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun showResumeTitleDialog(
        activity: ResumePreviewActivity,
        idMember: Int,
        resumeUrl: String
    ) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.save_your_resume, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val edtResumeName = dialogView.findViewById<EditText>(R.id.edtResumeName)
        val customSwitch = dialogView.findViewById<CustomSwitch>(R.id.customSwitch)
        val btnSave = dialogView.findViewById<TextView>(R.id.btnSave)
        val btnClose = dialogView.findViewById<ImageView>(R.id.imgClose)


        val overlayLayout = dialogView.findViewById<LinearLayout>(R.id.overlayLayout)
        val cardview = dialogView.findViewById<CardView>(R.id.cardview)
        val txtOverlayTitle = dialogView.findViewById<TextView>(R.id.txtOverlayTitle)
        val txtOverlayMessage = dialogView.findViewById<TextView>(R.id.txtOverlayMessage)
        val btnOverlayOk = dialogView.findViewById<Button>(R.id.btnOverlayOk)

        cardview.visibility = View.VISIBLE
        overlayLayout.visibility = View.GONE

        customSwitch.setChecked(false)

        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }

        activity.appViewModel?.ResumeBuilderSaveTitle?.observeOnce(activity) { response ->
            response?.let {
                val isSuccess = it.status
                cardview.visibility = View.GONE
                overlayLayout.visibility = View.VISIBLE
                overlayLayout.bringToFront()

                txtOverlayTitle.text = if (isSuccess) "Success" else "Failed"

                txtOverlayMessage.text = if (isSuccess) {
                    "Successfully saved to your profile – check the 'My Resumes' tab."
                } else {
                    it.message ?: "Something went wrong while saving."
                }

                btnOverlayOk.setOnClickListener {
                    overlayLayout.visibility = View.GONE
                    cardview.visibility = View.VISIBLE
                }

            }
        }

        btnSave.setOnClickListener {
            val title = edtResumeName.text.toString().trim()


            if (title.isEmpty()) {
                edtResumeName.error = "Please enter a title"
                edtResumeName.requestFocus()
                return@setOnClickListener
            }

            val isPlacementOfficer = customSwitch.isChecked()
            val resumeObject = JsonObject().apply {
                addProperty("title", title)
                addProperty("url", resumeUrl)
                addProperty("placementOfficer", isPlacementOfficer)
            }

            val finalJson = JsonObject().apply {
                addProperty("idMember", idMember)
                val resumeArray = JsonArray()
                resumeArray.add(resumeObject)
                add("resumeTitle", resumeArray)
            }

            activity.appViewModel?.SendSaveTittle(finalJson, activity)
        }
    }

    fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, object : Observer<T> {
            override fun onChanged(t: T?) {
                removeObserver(this)
                observer.onChanged(t)
            }
        })
    }



    fun showResumeListDialog(
        activity: Activity,
        resumeList: List<GetResumeTitleData>
    ) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.see_my_resume, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val imgClose = dialogView.findViewById<ImageView>(R.id.imgClose)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rcProfileResume)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = ResumeListAdapter(activity, resumeList) {
            val intent = Intent(activity, ResumePreviewActivity::class.java)
            intent.putExtra("TemplateDocumentURL", it.url)
            intent.putExtra("ScreenName","MyResumes")
            intent.putExtra("FileName",it.title)
            activity.startActivity(intent)
        }

        imgClose.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    fun GetProfileResume() {
        appViewModel!!.GetResumeBuilderProfileResume(31146, this@ResumePreviewActivity)
    }




}