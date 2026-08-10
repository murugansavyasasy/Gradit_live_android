package com.vsca.vsnapvoicecollege.ActivitySender

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Adapters.TextHistoryAdapter
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.TextHistoryData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.MenuDescription
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.MenuTitle
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.albumImage.AlbumSelectActivity
import com.vsca.vsnapvoicecollege.databinding.ActivityAddTextNoticeboardBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddTextNoticeboard: ActionBarActivity() {
    var ScreenType: Boolean? = null
    var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    private var TextHistoryAdapter: TextHistoryAdapter? = null

    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var textdata: ArrayList<TextHistoryData> = ArrayList()
    var FilePopup: PopupWindow? = null
    val REQUEST_Camera = 1
    val REQUEST_GAllery = 2
    val SELECT_PDF = 8778
    var imageFilePath: String? = null
    var PDFTempFileWrite: File? = null
    var photoTempFileWrite: File? = null
    var photoURI: Uri? = null
    var outputDir: File? = null
    var uri: Uri? = null
    var Totalfile: String? = null


    var ScreenName: String? = null
    private lateinit var binding: ActivityAddTextNoticeboardBinding
    private var pickImagesLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null
    private var pickVideoLauncher: ActivityResultLauncher<PickVisualMediaRequest>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAddTextNoticeboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionbarWithoutBottom(this,hideBackButton=true)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        findViewById<View>(R.id.Main).addActionBarMarginIfNeeded()
        CommonUtil.seleteddataArraySection.clear()
        imgRefresh!!.visibility = View.GONE
        binding.Nestedchildlayout!!.visibility = View.VISIBLE
        binding.btnConfirm!!.visibility = View.VISIBLE
        CommonUtil.SelcetedFileList.clear()

        binding.btnCancel.setOnClickListener { onBackPressed() }
        binding.imgTextback.setOnClickListener { onBackPressed() }
        binding.LayoutAdvertisement.setOnClickListener { adclick() }
        binding.btnConfirm.setOnClickListener { btnNextClick() }
        binding.LayoutUploadImagePdf.setOnClickListener { ChooseFile() }


        appViewModel!!.AdvertisementLiveData?.observe(
            this,
            Observer<GetAdvertisementResponse?> { response ->
                if (response != null) {
                    val status = response.status
                    val message = response.message

                    if (status == 1) {
                        GetAdForCollegeData = response.data!!
                        for (j in GetAdForCollegeData.indices) {
                            AdSmallImage = GetAdForCollegeData[j].add_image
                            AdBackgroundImage = GetAdForCollegeData[0].background_image!!
                            AdWebURl = GetAdForCollegeData[0].add_url.toString()
                        }
                        Glide.with(this).load(AdBackgroundImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imgthumb!!)
                    }
                }
            })

        binding.txtDescription!!.addTextChangedListener(mTextEditorWatcher)
        binding.txtDescription!!.enableScrollText()

        ScreenType = intent.getBooleanExtra("screentype", true)
        Log.d("isDatatacome",ScreenType.toString())
        if (ScreenType!!) {
               binding.LayoutUploadImagePdf!!.visibility = View.VISIBLE
            ScreenName = CommonUtil.Noticeboard
            binding.lblMenuTitle.text = "New Noticeboard"
            binding.LayoutHeadernoticeboard!!.visibility = View.VISIBLE
            binding.radioGroup!!.visibility = View.GONE
        } else {
            binding.LayoutUploadImagePdf!!.visibility = View.GONE
            ScreenName = CommonUtil.Text
            binding.LayoutHeadernoticeboard!!.visibility = View.GONE
            binding.radioGroup!!.visibility = View.VISIBLE
        }

        binding.radioGroup!!.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_B_msg -> {
                    binding.NestedchildlayoutRecy!!.visibility = View.GONE
                    binding.Nestedchildlayout!!.visibility = View.VISIBLE
                    binding.btnConfirm!!.visibility = View.VISIBLE
                }

                R.id.radio_B_history -> {
                    binding.NestedchildlayoutRecy!!.visibility = View.VISIBLE
                    binding.btnConfirm!!.visibility = View.GONE
                    binding.Nestedchildlayout!!.visibility = View.GONE
                    historyOfText()
                }
            }
        }


        appViewModel!!.Text_History!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    textdata.clear()
                    textdata = response.data
                    val size = textdata.size
                    Log.d("history_Size", size.toString())
                    if (size > 0) {
                        loadhistory()

                    }
                } else {
                    val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder1.setTitle("Info")
                    builder1.setMessage("No data found")
                    builder1.setCancelable(true)

                    builder1.setPositiveButton("Ok",
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.cancel()
                            finish()
                        })
                    val alert11: AlertDialog = builder1.create()
                    alert11.show()
                }
            } else {
                val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                builder1.setTitle("Info")
                builder1.setMessage("No data found")
                builder1.setCancelable(true)

                builder1.setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                        finish()
                    })
                val alert11: AlertDialog = builder1.create()
                alert11.show()
            }
        }

        pickImagesLauncher =
            registerForActivityResult(
                ActivityResultContracts.PickMultipleVisualMedia(10)
            ) { uris ->

                if (uris.isEmpty()) return@registerForActivityResult

                CommonUtil.SelcetedFileList.clear()

                uris.forEach { uri ->

                    val file = getImageFromUri(uri)

                    file?.let {
                        CommonUtil.SelcetedFileList.add(it.absolutePath)
                    }
                    updateFileCount()
                }

                Log.d("selectedPaths", CommonUtil.SelcetedFileList.toString())
            }
    }

    private fun getImageFromUri(imageUri: Uri?): File? {
        imageUri?.let { uri ->
            val mimeType = getMimeType(this@AddTextNoticeboard, uri)
            mimeType?.let {
                val file = createTmpFileFromUri(this, imageUri, "temp_image", ".$it")
                file?.let { Log.d("image Url = ", file.absolutePath) }
                return file
            }
        }
        return null
    }

    private fun getMimeType(context: Context, uri: Uri): String? {
        val extension: String? = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val mime = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
        }
        return extension
    }

    private fun createTmpFileFromUri(
        context: Context, uri: Uri, fileName: String, mimeType: String
    ): File? {
        return try {
            val stream = context.contentResolver.openInputStream(uri)
            val file = File.createTempFile(fileName, mimeType, cacheDir)

            FileUtils.copyInputStreamToFile(stream, file)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun loadhistory() {
        TextHistoryAdapter = TextHistoryAdapter(textdata, this)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.rcyHistory!!.layoutManager = mLayoutManager
        binding.rcyHistory!!.itemAnimator = DefaultItemAnimator()
        binding.rcyHistory!!.setHasFixedSize(true)
        binding.rcyHistory!!.adapter = TextHistoryAdapter
        binding.rcyHistory!!.recycledViewPool.setMaxRecycledViews(0, 500)
        TextHistoryAdapter!!.notifyDataSetChanged()
    }

    fun EditText.enableScrollText() {
        overScrollMode = View.OVER_SCROLL_ALWAYS
        scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        isVerticalScrollBarEnabled = true
        setOnTouchListener { view, event ->
            if (view is EditText) {
                if (!view.text.isNullOrEmpty()) {
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(
                            false
                        )
                    }
                }
            }
            false
        }
    }

    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_CANCELED) return

        when (requestCode) {

            REQUEST_Camera -> {
                imageFilePath?.let { path ->
                    CommonUtil.SelcetedFileList.add(path)
                    Log.d("CameraFilePath", path)
                    updateFileCount()
                }
            }

            REQUEST_GAllery -> {
                data?.getStringArrayListExtra("images")?.let { selectedList ->
                    CommonUtil.SelcetedFileList.clear()
                    CommonUtil.SelcetedFileList.addAll(selectedList)
                    Log.d("SelectedFileList", selectedList.toString())
                    updateFileCount()
                }
            }

            SELECT_PDF -> {
                if (resultCode == RESULT_OK && data != null) {
                    CommonUtil.SelcetedFileList.clear()

                    // CASE 1: Multiple PDFs selected
                    val clipData = data.clipData
                    if (clipData != null && clipData.itemCount > 0) {
                        for (i in 0 until clipData.itemCount) {
                            val uri = clipData.getItemAt(i).uri
                            Log.d("PDFUri[$i]", uri.toString())
                            ReadAndWriteFile(uri, ".pdf")
                        }
                    }
                    // CASE 2: Single PDF selected
                    else if (data.data != null) {
                        val uri = data.data!!
                        Log.d("SinglePDFUri", uri.toString())
                        ReadAndWriteFile(uri, ".pdf")
                    }

                    updateFileCount()
                }
            }
        }
    }

    /**
     * Copies the file content from given URI to a temporary file and adds it to the list.
     */
    private fun ReadAndWriteFile(uri: Uri?, extension: String) {
        if (uri == null) return
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return

            // Ensure output directory exists
            if (outputDir != null && !outputDir!!.exists()) {
                outputDir!!.mkdirs()
            }

            // Create a temporary file
            PDFTempFileWrite = File.createTempFile("File_", extension, outputDir)
            val pdfPath = PDFTempFileWrite!!.absolutePath
            Log.d("TempPDFPath", pdfPath)

            // Copy content
            contentResolver.openOutputStream(Uri.fromFile(PDFTempFileWrite))?.use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            inputStream.close()

            // Save file info
            CommonUtil.extension = extension
            CommonUtil.SelcetedFileList.add(pdfPath)
            Log.d("PDFTempFileWrite", PDFTempFileWrite.toString())

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ReadAndWriteFileError", e.message ?: "Unknown error")
        }
    }

    /**
     * Updates UI file count label safely.
     */
    private fun updateFileCount() {
        val totalFiles = CommonUtil.SelcetedFileList.size
        if (totalFiles > 0) {
            binding.lbltotalfile?.apply {
                text = "Number of Files : $totalFiles"
                visibility = View.VISIBLE
            }
        } else {
            binding.lbltotalfile?.visibility = View.GONE
        }
    }


     fun ChooseFile() {

        Log.d("popup", "test")
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialog: View = inflater.inflate(R.layout.popup_choose_file, null)
        FilePopup = PopupWindow(
            dialog, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true
        )
        FilePopup?.showAtLocation(dialog, Gravity.BOTTOM, 0, 0)
        FilePopup?.contentView = dialog
        FilePopup?.isOutsideTouchable = true
        FilePopup?.isFocusable = true

        val LayoutGallery = dialog.findViewById<ConstraintLayout>(R.id.LayoutGallery)
        val LayoutCamera = dialog.findViewById<ConstraintLayout>(R.id.LayoutCamera)
        val LayoutDocuments = dialog.findViewById<ConstraintLayout>(R.id.LayoutDocuments)
        val popClose = dialog.findViewById<ImageView>(R.id.popClose)

        val container = FilePopup?.contentView?.parent as View
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.7f
        wm.updateViewLayout(container, p)

        popClose.setOnClickListener {
            FilePopup?.dismiss()
        }
        LayoutGallery.setOnClickListener {
            CommonUtil.SelcetedFileList.clear()

            pickImagesLauncher?.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )

            FilePopup?.dismiss()

//            val intent1 = Intent(this, AlbumSelectActivity::class.java)
//            intent1.putExtra("Gallery", "Images")
//            startActivityForResult(intent1, REQUEST_GAllery)
//            FilePopup!!.dismiss()
        }

        LayoutCamera.setOnClickListener {
            CommonUtil.SelcetedFileList.clear()

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                photoTempFileWrite = createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (photoTempFileWrite != null) {
                photoURI = FileProvider.getUriForFile(
                    this, "com.vsca.vsnapvoicecollege.provider", photoTempFileWrite!!
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_Camera)
                FilePopup?.dismiss()
            }
        }

        LayoutDocuments.setOnClickListener({

            CommonUtil.SelcetedFileList.clear()

            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, SELECT_PDF)
            FilePopup!!.dismiss()

        })
    }

    @Throws(IOException::class)
    fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        imageFilePath = image.absolutePath
        return image
    }


    private fun AdForCollegeApi() {

        var mobilenumber = SharedPreference.getSH_MobileNumber(this)
        var devicetoken = SharedPreference.getSH_DeviceToken(this)
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_ad_device_token, devicetoken)
        jsonObject.addProperty(ApiRequestNames.Req_MemberID, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_mobileno, mobilenumber)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_previous_add_id, PreviousAddId)
        appviewModelbase!!.getAdforCollege(jsonObject, this)
        Log.d("AdForCollege:", jsonObject.toString())

        PreviousAddId = PreviousAddId + 1
        Log.d("PreviousAddId", PreviousAddId.toString())
    }

    private fun historyOfText() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId?.toString())
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.Appid?.toString())
        appViewModel!!.textHistoryData(jsonObject, this)
        Log.d("_TextHistoryData:", jsonObject.toString())
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_add_text_noticeboard

     fun btnNextClick() {

        MenuTitle = binding.txtTitle!!.text.toString()
        MenuDescription = binding.txtDescription!!.text.toString()
        Log.d("MenuDescription", MenuDescription!!)
        if (ScreenName.equals(CommonUtil.Noticeboard)) {
            if ((!MenuTitle.isNullOrEmpty()) && (!MenuDescription.isNullOrEmpty())) {
                //    if (CommonUtil.SelcetedFileList.isNotEmpty()) {
                CommonUtil.receiverid = ""
                if (CommonUtil.Priority.equals("p7")) {
                    val i: Intent = Intent(this, HeaderRecipient::class.java)
                    i.putExtra("ScreenName", ScreenName)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                } else {
                    if (CommonUtil.Priority.equals("p1")) {
                        val i: Intent = Intent(this, PrincipalRecipient::class.java)
                        i.putExtra("ScreenName", ScreenName)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)
                    } else {
                        val i: Intent = Intent(this, AddRecipients::class.java)
                        i.putExtra("ScreenName", ScreenName)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)
                    }
                }
//                } else {
//                    CommonUtil.ApiAlert(this, "Choose Minimum one File")
//                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Enter_Details)
            }
        } else {
            if ((!MenuTitle.isNullOrEmpty()) && (!MenuDescription.isNullOrEmpty())) {
                CommonUtil.receiverid = ""
                if (CommonUtil.Priority.equals("p7")) {
                    val i: Intent = Intent(this, HeaderRecipient::class.java)
                    i.putExtra("ScreenName", ScreenName)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                } else {
                    if (CommonUtil.Priority.equals("p1")) {
                        val i: Intent = Intent(this, PrincipalRecipient::class.java)
                        i.putExtra("ScreenName", ScreenName)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)
                    } else {
                        val i: Intent = Intent(this, AddRecipients::class.java)
                        i.putExtra("ScreenName", ScreenName)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)
                    }
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Enter_Details)
            }
        }
    }

    fun adclick() {
        BaseActivity.LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        AdForCollegeApi()
        super.onResume()
    }

    private val mTextEditorWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            binding.tvCount!!.text = s.length.toString() + "/500"
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}