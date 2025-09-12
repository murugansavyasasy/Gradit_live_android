package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume


import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.chrisbanes.photoview.PhotoView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.AWS.AwsUploadingPreSigned
import com.vsca.vsnapvoicecollege.AWS.UploadCallback
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.Utils.CustomSwitch
import com.vsca.vsnapvoicecollege.Utils.PdfDownloader
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutResumepreviewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class ResumePreviewActivity : AppCompatActivity() {

    var appViewModel: App? = null
    private lateinit var binding: LayoutResumepreviewBinding

    var isMemberID = -1
    var isPDF_URL = ""
    var isScreenName = ""
    var isFileName = ""
    private lateinit var pdfFile: File

    var isPlacementOfficer = false
    var isSavedResumeTitle = ""


    var progressDialog: ProgressDialog? = null
    var fileNameDateTime: String? = null
    var Awsaupladedfilepath: String? = null
    var isAwsUploadingPreSigned: AwsUploadingPreSigned? = null
    var separator = ","
    var FileType: String? = null
    var AWSUploadedFilesList = java.util.ArrayList<String>()
    var Awsuploadedfile = java.util.ArrayList<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutResumepreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        isAwsUploadingPreSigned = AwsUploadingPreSigned()



        binding.commonBottomResumeBuilder.imgDefault.visibility = View.VISIBLE

        isMemberID = intent.getIntExtra("MemberID", -1)
        isScreenName = intent.getStringExtra("ScreenName").toString()
        isFileName = intent.getStringExtra("FileName").toString()
        binding.commonBottomResumeBuilder.btnDefault1.text = "Share Resume"




        if (isScreenName == "MyResumes") {
            binding.scrollView.visibility=View.GONE
            binding.webviewDocument.visibility=View.VISIBLE
            binding.lnrZoomOptions.visibility=View.VISIBLE

            isPDF_URL = intent.getStringExtra("TemplateDocumentURL").toString()
            Log.d("isPDF_URL", isPDF_URL)

            binding.commonBottomResumeBuilder.btnDefault2.text =
                resources.getString(R.string.download)
            binding.txtTitle.text = "View My Resume"
            binding.lblPdfName.visibility = View.VISIBLE
            binding.lblPdfName.text = isFileName
            binding.lbldelete.visibility = View.VISIBLE
            binding.commonBottomResumeBuilder.btnDefault1.text = "Share"
        }

        if (isScreenName == "BuildMyResume") {
            binding.scrollView.visibility=View.GONE
            binding.webviewDocument.visibility=View.VISIBLE
            binding.lnrZoomOptions.visibility=View.VISIBLE

            isPDF_URL = intent.getStringExtra("TemplateDocumentURL").toString()
            Log.d("isPDF_URL", isPDF_URL)
            binding.commonBottomResumeBuilder.btnDefault2.text = "Save"
            binding.txtTitle.text = "Preview My Resume"
            binding.lblPdfName.visibility = View.GONE
            binding.lblPdfName.text = ""
            binding.lbldelete.visibility = View.GONE
            binding.commonBottomResumeBuilder.btnDefault1.text =
                resources.getString(R.string.change_template)
        }

        if (isScreenName == "UploadResume") {
            binding.scrollView.visibility=View.VISIBLE
            binding.webviewDocument.visibility=View.GONE
            binding.lnrZoomOptions.visibility=View.GONE

            isPDF_URL=CommonUtil.SelcetedFileList[0]
            Log.d("isPdfUrl",isPDF_URL)
               val fileUri = Uri.fromFile(File(isPDF_URL))
              renderPdf(fileUri)

            binding.commonBottomResumeBuilder.btnDefault2.text = "Save"
            binding.txtTitle.text = "Preview My Resume"
            binding.lblPdfName.visibility = View.GONE
            binding.lblPdfName.text = ""
            binding.lbldelete.visibility = View.GONE
            binding.commonBottomResumeBuilder.btnDefault1.text = "Cancel"
        }

        binding.zoomIn.setOnClickListener {
            if (isScreenName == "BuildMyResume" || isScreenName == "MyResumes") {
                binding.webviewDocument.zoomIn()
            }
        }

        binding.zoomOut.setOnClickListener {
            if (isScreenName == "BuildMyResume"||isScreenName == "MyResumes"){
                binding.webviewDocument.zoomOut()
            }

        }

        binding.lbldelete.setOnClickListener {
            showConfirmationDialog()
        }

        binding.commonBottomResumeBuilder.btnDefault1.setOnClickListener {
            if (isScreenName == "MyResumes") {
                shareFileFromUrl(isPDF_URL)
            } else if (isScreenName == "BuildMyResume" || isScreenName == "UploadResume") {
                onBackPressed()
            }

        }

        binding.commonBottomResumeBuilder.btnSave.setOnClickListener {
            if (isScreenName == "MyResumes") {
                PdfDownloader(this).downloadPdf(isPDF_URL)

            } else if (isScreenName == "BuildMyResume" || isScreenName == "UploadResume") {
                showResumeTitleDialog(this, isMemberID, isPDF_URL)
            }

        }

        appViewModel?.ResumeBuilderDeleteResume!!.observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    CommonUtil.Alert(this, "Success", response.message)
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                } else {
                    CommonUtil.Alert(this, "Failed", response.message)
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }


        appViewModel?.ResumeBuilderGenerateResume!!.observe(this) { response ->
            if (response != null) {

                if (response.status) {
                    Log.d("GenerateResume", "Reusme Generattion Scucessful")
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("GenerateResume", "Reusme Generattion Failed")
                Toast.makeText(this, response?.message, Toast.LENGTH_SHORT).show()
            }
        }

        val webSettings = binding.webviewDocument.settings
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100
                )
                return
            }
        }

        binding.webviewDocument.setBackgroundColor(Color.TRANSPARENT)
        binding.webviewDocument.setLayerType(View.LAYER_TYPE_SOFTWARE, null)


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

        if (isPDF_URL.isNotEmpty()) {
            val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$isPDF_URL"
            binding.webviewDocument.loadUrl(googleDocsUrl)
        } else {
            Toast.makeText(this, "Invalid PDF URL", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
        }

        binding.imgback.setOnClickListener {
            onBackPressed()
        }


    }

    fun deleteResume() {

        val body = JsonObject().apply {
            addProperty("resumeUrl", isPDF_URL)
        }
        appViewModel!!.GetResumeBuilderDeleteResume(isMemberID, body, this@ResumePreviewActivity)
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
        val btnDownload = dialogView.findViewById<LinearLayout>(R.id.btnDownload)
        val btnShare = dialogView.findViewById<TextView>(R.id.btnShare)
        cardview.visibility = View.VISIBLE
        overlayLayout.visibility = View.GONE

        customSwitch.setChecked(false)

        btnDownload.visibility=View.GONE
        btnShare.visibility=View.GONE

        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }

        btnDownload.setOnClickListener {
            PdfDownloader(this).downloadPdf(isPDF_URL)
        }

        btnShare.setOnClickListener {
            shareFileFromUrl(isPDF_URL)
        }

        activity.appViewModel?.ResumeBuilderSaveTitle?.observeOnce(activity) { response ->
            response?.let {
                val isSuccess = it.status
                cardview.visibility = View.GONE
                overlayLayout.visibility = View.VISIBLE
                overlayLayout.bringToFront()

                if (isSuccess){
                    btnSave.visibility=View.GONE
                    btnDownload.visibility=View.VISIBLE
                    btnShare.visibility=View.VISIBLE
                }
                else{
                    btnSave.visibility=View.VISIBLE
                    btnDownload.visibility=View.GONE
                    btnShare.visibility=View.GONE
                }
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
            isSavedResumeTitle = edtResumeName.text.toString().trim()

            if (isSavedResumeTitle.isEmpty()) {
                edtResumeName.error = "Please enter a title"
                edtResumeName.requestFocus()
                return@setOnClickListener
            }

            isPlacementOfficer = customSwitch.isChecked()

            // Case 1: UploadResume – do AWS upload, use returned URL
            if (activity.isScreenName == "UploadResume") {
                isUploadAWS()

            }

            // Case 2: BuildMyResume – directly use provided resumeUrl
            else {
                val resumeObject = JsonObject().apply {
                    addProperty("title", isSavedResumeTitle)
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


    }

    private fun showConfirmationDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)

        val message = buildString {
            append("Are you sure you want to delete this resume?")
        }

        builder.setTitle("Confirmation")
            .setMessage(message)
            .setPositiveButton("Yes") { dialog, _ ->
                deleteResume()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, object : Observer<T> {
            override fun onChanged(t: T?) {
                removeObserver(this)
                observer.onChanged(t)
            }
        })
    }



    private fun isUploadAWS() {
        Log.d("selectedImagePath", CommonUtil.SelcetedFileList.size.toString())
        for (i in CommonUtil.SelcetedFileList.indices) {
            AwsUploadingFile(
                CommonUtil.SelcetedFileList[i]
            )
        }
    }

    private fun AwsUploadingFile(
        isFilePath: String
    ) {
        isAwsUploadingPreSigned!!.getPreSignedUrl(
            this,
            isFilePath,
            CommonUtil.Collage_ids,
            object : UploadCallback {
                override fun onUploadSuccess(response: String?, isAwsFile: String?) {
                    Log.d("Upload Success", response!!)
                    AWSUploadedFilesList.add(isAwsFile!!)
                    Awsuploadedfile.add(isAwsFile.toString())
                    Awsaupladedfilepath = Awsuploadedfile.joinToString(separator)
                    isPDF_URL=Awsuploadedfile[0]
                    isSaveApiCall()
                    Log.d("isAwsFile", isAwsFile)

                }
                override fun onUploadError(error: String?) {
                    Log.e("Upload Error", error!!)
                    Toast.makeText(this@ResumePreviewActivity, "Error occured while Upload", Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun isSaveApiCall() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("idMember", isMemberID)
        val jsonArray = JsonArray()
        val isJsonObject = JsonObject()
        isJsonObject.addProperty("title", isSavedResumeTitle)
        isJsonObject.addProperty("url", isPDF_URL)
        isJsonObject.addProperty("placementOfficer", isPlacementOfficer)
        jsonArray.add(isJsonObject)
        jsonObject.add("resumeTitle", jsonArray)
        Log.d("jsonObjectSaveResumeFromUploadResume",jsonObject.toString())
        this.appViewModel?.SendSaveTittle(jsonObject, this)
    }

    private fun shareFileFromUrl(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()
                val contentType = connection.contentType ?: "application/octet-stream"
                var fileName = url.substringAfterLast("/").substringBefore("?")
                if (!fileName.contains(".")) {
                    val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType)
                    fileName += ".${ext ?: "bin"}"
                }
                val file = File(cacheDir, fileName)
                if (!file.exists()) {
                    connection.inputStream.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(
                                output
                            )
                        }
                    }
                }
                val uri = FileProvider.getUriForFile(
                    this@ResumePreviewActivity,
                    "$packageName.provider",
                    file
                )
                val mimeType =
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension.lowercase())
                        ?: contentType
                withContext(Dispatchers.Main) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        setType(mimeType)
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share File"))
                }

            } catch (e: Exception) {
                Log.e("ShareFile", "Error sharing: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ResumePreviewActivity,
                        "Failed to share file",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    private fun renderPdf(uri: Uri) {
        try {
            val fileDescriptor: ParcelFileDescriptor =
                contentResolver.openFileDescriptor(uri, "r") ?: return
            val pdfRenderer = PdfRenderer(fileDescriptor)

            for (i in 0 until pdfRenderer.pageCount) {
                val page = pdfRenderer.openPage(i)
                val bitmap = Bitmap.createBitmap(
                    page.width, page.height, Bitmap.Config.ARGB_8888
                )
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                val photoView = PhotoView(this)
                photoView.setImageBitmap(bitmap)

                photoView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                photoView.setPadding(8, 8, 8, 8)
                photoView.adjustViewBounds = true

                binding.pdfContainer.addView(photoView)

                page.close()
            }

            pdfRenderer.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to load PDF", Toast.LENGTH_SHORT).show()
        }
    }
}