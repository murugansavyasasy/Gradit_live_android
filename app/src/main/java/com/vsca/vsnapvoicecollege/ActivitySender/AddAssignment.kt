package com.vsca.vsnapvoicecollege.ActivitySender

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.AWS.AwsUploadingPreSigned
import com.vsca.vsnapvoicecollege.AWS.UploadCallback
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Activities.Assignment
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Interfaces.ApiInterfaces
import com.vsca.vsnapvoicecollege.Model.AWSUploadedFiles
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.VideoAlbum.AlbumVideoSelectVideoActivity
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.AddImageNewBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class AddAssignment : ActionBarActivity() {

    var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    private val SELECT_VIDEO = 2
    val SELECT_PDF = 8778
    var videofile: String? = null
    var PDFTempFileWrite: File? = null
    var outputDir: File? = null
    var selectedPaths = mutableListOf<String>()
    var uri: Uri? = null
    var ScreenName: String? = null
    var AssignmentTitle: String? = null
    val REQUEST_Camera = 1
    var imageFilePath: String? = null

    //AWS
    var Awsuploadedfile = java.util.ArrayList<String>()
    var pathIndex = 0
    var uploadFilePath: String? = null
    var contentType: String? = null
    var AWSUploadedFilesList = ArrayList<AWSUploadedFiles>()
    var progressDialog: ProgressDialog? = null
    var fileNameDateTime: String? = null
    var fileName: File? = null
    var Awsaupladedfilepath: String? = null
    var separator = ","
    var FilePopup: PopupWindow? = null
    var photoTempFileWrite: File? = null
    var isAwsUploadingPreSigned: AwsUploadingPreSigned? = null

    var filename: String? = null
    var FileType: String? = null
    var photoURI: Uri? = null
    var AssignmentDate: String? = null
    var AssignmentTitleForward: String? = null
    var AssignmentType: String? = null
    var AssignmentDescription: String? = null
    private lateinit var binding: AddImageNewBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = AddImageNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionbarWithoutBottom(this)
        isAwsUploadingPreSigned = AwsUploadingPreSigned()
        imgRefresh!!.visibility = View.GONE

        ScreenName = intent.getStringExtra("ScreenName")
        AssignmentTitleForward = intent.getStringExtra("AssignmentTitle")
        AssignmentType = intent.getStringExtra("AssignmentType")
        Log.d("AssignmentForward", ScreenName.toString())

        if (ScreenName.equals(CommonUtil.Forward_Assignment)) {

            binding.lblMenuTitle!!.text = CommonUtil.Forward_Assignment
            if (AssignmentType.equals("image")) {

                binding.edtDevision!!.text = "IMAGE"

            } else if (AssignmentType.equals("video")) {

                binding.edtDevision!!.text = "VIDEO"

            } else if (AssignmentType.equals("pdf")) {

                binding.edtDevision!!.text = "PDF"

            } else if (AssignmentType.equals("text")) {

                binding.edtDevision!!.text = "TEXT"

            } else if (AssignmentType.equals("All")) {

            }
            binding.edtTitle!!.setText(AssignmentTitleForward)
            binding.edtTitle!!.setEnabled(false)
            binding.edtDevision!!.setEnabled(false)
            binding.txtDescription!!.visibility = View.GONE
            binding.LayoutUploadVideo!!.visibility = View.GONE
            binding.txtDecription!!.visibility = View.GONE
        } else {
            binding.txtDescription!!.visibility = View.VISIBLE
            binding.LayoutUploadVideo!!.visibility = View.VISIBLE
            binding.txtDecription!!.visibility = View.VISIBLE
        }

        binding.LayoutUploadVideo.setOnClickListener { PICKFILE() }
        binding.imgImagePdfback.setOnClickListener { onBackPressed() }
        binding.startDate.setOnClickListener { eventdateClick() }
        binding.btnConfirm.setOnClickListener { selectreciption() }
        binding.btnCancel.setOnClickListener { btnCancel() }
        binding.LayoutAdvertisement.setOnClickListener { adclick() }

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


        if (ScreenName.equals("Assignment Submit")) {

            binding.edtTitle!!.visibility = View.GONE
            binding.txtTitle!!.visibility = View.GONE
            binding.edtDevision!!.visibility = View.GONE
            binding.startDate!!.visibility = View.GONE
            binding.txtAssignmenttype!!.visibility = View.GONE
            binding.txtSubmissionon!!.visibility = View.GONE
            binding.view!!.visibility = View.GONE
            binding.btnConfirm!!.text = "Submit"

        }

        appViewModel!!.AssignmentSubmited!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message

                if (status == 1) {
                    val dlg = this.let { androidx.appcompat.app.AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Assignment::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()
                } else {
                    CommonUtil.ApiAlert(this, message)
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        binding.txtDescription!!.addTextChangedListener(mTextEditorWatcher)
        binding.txtDescription!!.enableScrollText()


        binding.edtDevision?.setOnClickListener {
            val popupMenu = PopupMenu(this@AddAssignment, binding.edtDevision)
            popupMenu.menuInflater.inflate(R.menu.assignment_typemenu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->

                binding.edtDevision!!.setText(menuItem.title)
                if (menuItem.title!!.equals("PDF")) {
                    binding.lblUploadFiles?.setText("Upload Pdf")
                    FileType = "PDF"
                    binding.LayoutUploadVideo!!.visibility = View.VISIBLE

                } else if (menuItem.title!!.equals("IMAGE")) {
                    binding.lblUploadFiles?.setText("Upload Image")
                    FileType = "IMAGE"
                    binding.LayoutUploadVideo!!.visibility = View.VISIBLE

                } else if (menuItem.title!!.equals("VIDEO")) {
                    binding.lblUploadFiles?.text = "Upload Video"
                    FileType = "VIDEO"
                    binding.LayoutUploadVideo!!.visibility = View.VISIBLE

                } else if (menuItem.title!!.equals("TEXT")) {
                    FileType = "TEXT"
                    binding.lblFileselectedstate!!.visibility = View.GONE
                    binding.LayoutUploadVideo!!.visibility = View.GONE
                }
                true
            }
            popupMenu.show()
        }

    }


    val selectImagesActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data

                //If multiple image selected
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0

                    for (i in 0 until count) {
                        val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri
                        val file = getImageFromUri(imageUri)
                        file?.let {
                            CommonUtil.SelcetedFileList.add(it.absolutePath)
                            Log.d("selectedPaths", CommonUtil.SelcetedFileList.toString())
                            var Count: String? = null
                            if (CommonUtil.SelcetedFileList != null) {
                                Count = CommonUtil.SelcetedFileList.size.toString()
                                binding.lblFileselectedstate!!.visibility = View.VISIBLE
                                binding.lblFileselectedstate!!.text = "Number of file selected : " + Count
                            } else {
                                binding.lblFileselectedstate!!.visibility = View.GONE
                            }

                        }
                    }
                }

                //If single image selected
                else if (data?.data != null) {
                    CommonUtil.SelcetedFileList.clear()

                    val imageUri: Uri? = data.data
                    val file = getImageFromUri(imageUri)
                    file?.let {
                        CommonUtil.SelcetedFileList.add(it.absolutePath)
                        Log.d("selectedPaths", CommonUtil.SelcetedFileList.toString())
                        var Count: String? = null
                        if (CommonUtil.SelcetedFileList != null) {
                            Count = CommonUtil.SelcetedFileList.size.toString()
                            binding.lblFileselectedstate!!.visibility = View.VISIBLE
                            binding.lblFileselectedstate!!.setText("Nmber of file selected : " + Count)

                        } else {
                            binding.lblFileselectedstate!!.visibility = View.GONE

                        }
                    }
                }
            }
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

    private fun getImageFromUri(imageUri: Uri?): File? {
        imageUri?.let { uri ->
            val mimeType = getMimeType(this@AddAssignment, uri)
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


    private fun AdForCollegeApi() {

        val mobilenumber = SharedPreference.getSH_MobileNumber(this)
        val devicetoken = SharedPreference.getSH_DeviceToken(this)
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

    private fun AssignmentsendEntireSection() {

        val jsonObject = JsonObject()

        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_assignmentid, CommonUtil.Assignmentid)
        jsonObject.addProperty(ApiRequestNames.Req_processby, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.Description)


        if (FileType.equals("IMAGE")) {
            Log.d("Awsuploadedfile", Awsuploadedfile.size.toString())
            jsonObject.addProperty(ApiRequestNames.Req_filetype, "image")
            val FileNameArray = JsonArray()
            for (i in Awsuploadedfile.indices) {
                val FileNameobject = JsonObject()
                FileNameobject.addProperty(ApiRequestNames.Req_FileName, Awsuploadedfile[i])
                FileNameArray.add(FileNameobject)
            }
            jsonObject.add(ApiRequestNames.Req_FileNameArray, FileNameArray)

        } else if (FileType.equals("PDF")) {
            jsonObject.addProperty(ApiRequestNames.Req_filetype, "pdf")
            val FileNameArray = JsonArray()
            for (i in Awsuploadedfile.indices) {
                val FileNameobject = JsonObject()
                FileNameobject.addProperty(ApiRequestNames.Req_FileName, Awsuploadedfile[i])
                FileNameArray.add(FileNameobject)
            }

            jsonObject.add(ApiRequestNames.Req_FileNameArray, FileNameArray)

        } else if (FileType.equals("VIDEO")) {


            jsonObject.addProperty(ApiRequestNames.Req_filetype, "video")

            val FileNameArray = JsonArray()
            val FileNameobject = JsonObject()

            FileNameobject.addProperty(
                ApiRequestNames.Req_FileName, CommonUtil.VimeoVideoUrl.toString()
            )
            FileNameArray.add(FileNameobject)
            jsonObject.add(ApiRequestNames.Req_FileNameArray, FileNameArray)

        }

        appViewModel!!.Assignmentsubmited(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }

    override val layoutResourceId: Int
        get() = R.layout.add_image_new

    fun PICKFILE() {

        CommonUtil.SelcetedFileList.clear()
        if (ScreenName.equals("Assignment Submit")) {
            val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val dialog: View = inflater.inflate(R.layout.popup_choose_file, null)
            FilePopup = PopupWindow(
                dialog,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
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

                FileType = "IMAGE"
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                selectImagesActivityResult.launch(intent)
                FilePopup!!.dismiss()
            }

            LayoutDocuments.setOnClickListener({

                FileType = "PDF"
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "application/pdf"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startActivityForResult(intent, SELECT_PDF)
                FilePopup!!.dismiss()
            })

            LayoutCamera.setOnClickListener {

                FileType = "IMAGE"
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

        } else {

            filename = binding.lblUploadFiles.toString()
            if (binding.lblUploadFiles!!.text.equals("Upload Image")) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                selectImagesActivityResult.launch(intent)
            } else if (binding.lblUploadFiles!!.text.equals("Upload Pdf")) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "application/pdf"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                startActivityForResult(intent, SELECT_PDF)
            } else if (binding.lblUploadFiles!!.text.equals("Upload Video")) {
                val intent1 = Intent(this, AlbumVideoSelectVideoActivity::class.java)
                intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startActivityForResult(intent1, SELECT_VIDEO)
            }
        }
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


    fun eventdateClick() {

        val c = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this, { view, year, month, dayOfMonth ->

                val _year = year.toString()
                val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                val _pickedDate = "$_date/$_month/$_year"
                Log.e("PickedDate: ", "Date: $_pickedDate") //2019-02-12
                binding.startDate!!.text = _pickedDate
            }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.MONTH]
        )
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()

    }

    fun btnCancel() {
        CommonUtil.SelcetedFileList.clear()
        onBackPressed()
    }

    fun selectreciption() {


        if (ScreenName.equals("Assignment Submit")) {

            CommonUtil.startdate = binding.startDate!!.text.toString()
            CommonUtil.Description = binding.txtDescription!!.text.toString()
            CommonUtil.title = binding.edtTitle!!.text.toString()

            if (ScreenName.equals("Assignment Submit")) {
                AssignmentDescription = binding.txtDescription!!.text.toString()

                if (!AssignmentDescription.isNullOrEmpty() && CommonUtil.SelcetedFileList.size > 0) {
                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@AddAssignment)
                    alertDialog.setTitle(CommonUtil.Hold_on)
                    alertDialog.setMessage(CommonUtil.Are_you_submit)
                    alertDialog.setPositiveButton(CommonUtil.Yes) { _, _ ->
                        isUploadAWS()
                    }
                    alertDialog.setNegativeButton(CommonUtil.No) { _, _ ->
                    }
                    val alert: AlertDialog = alertDialog.create()
                    alert.setCanceledOnTouchOutside(false)
                    alert.show()
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.fill_the_details)
                }
            }

        } else if (ScreenName.equals(CommonUtil.Forward_Assignment)) {

            ScreenName = CommonUtil.Forward_Assignment

            if (CommonUtil.Priority.equals("p7")) {
                val i: Intent = Intent(this, HeaderRecipient::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            } else if (CommonUtil.Priority.equals("p2") || CommonUtil.Priority.equals("p3")) {
                val i: Intent = Intent(this, AddRecipients::class.java)
                CommonUtil.startdate = binding.startDate!!.text.toString()
                i.putExtra("ScreenName", ScreenName)
                startActivity(i)
            } else {
                val i: Intent = Intent(this, PrincipalRecipient::class.java)
                CommonUtil.startdate = binding.startDate!!.text.toString()
                i.putExtra("ScreenName", ScreenName)
                startActivity(i)
            }

        } else {

            AssignmentDate = binding.startDate!!.text.toString()
            AssignmentTitle = binding.edtTitle!!.text.toString()
            AssignmentDescription = binding.txtDescription!!.text.toString()

            if (!AssignmentDate.isNullOrEmpty() && !AssignmentTitle.isNullOrEmpty() && !AssignmentDescription.isNullOrEmpty()) {

                if (CommonUtil.SelcetedFileList.isEmpty() && !FileType.equals("TEXT")) {

                    CommonUtil.ApiAlert(this, "Select the file")

                } else {

                    CommonUtil.startdate = binding.startDate!!.text.toString()
                    CommonUtil.Description = binding.txtDescription!!.text.toString()
                    CommonUtil.title = binding.edtTitle!!.text.toString()
                    ScreenName = CommonUtil.New_Assignment


                    if (CommonUtil.Priority.equals("p7")) {
                        val i: Intent = Intent(this, HeaderRecipient::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)
                    } else if (CommonUtil.Priority.equals("p1")) {
                        val i: Intent = Intent(this, PrincipalRecipient::class.java)
                        i.putExtra("ScreenName", ScreenName)
                        i.putExtra("FileType", FileType)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)
                    } else if (CommonUtil.Priority.equals("p3") || CommonUtil.Priority.equals("p2")) {
                        val i: Intent = Intent(this, AddRecipients::class.java)
                        i.putExtra("ScreenName", ScreenName)
                        i.putExtra("FileType", FileType)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)
                    }
                }

            } else {
                Toast.makeText(this, "Fill the all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isUploadAWS() {
        progressDialog = CustomLoading.createProgressDialog(this)
        progressDialog!!.show()
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
            isFilePath,
            CommonUtil.Collage_ids,
            object : UploadCallback {
                override fun onUploadSuccess(response: String?, isAwsFile: String?) {
                    Log.d("Upload Success", response!!)
                    Awsuploadedfile.add(isAwsFile!!)
                    if (CommonUtil.EventStatus.equals("Past")) {
                        CommonUtil.EventStatus = "Past"
                    } else {
                        CommonUtil.EventStatus = "Upcoming"
                    }
                    if (Awsuploadedfile.size == CommonUtil.SelcetedFileList.size) {
                        progressDialog!!.dismiss()
                        AssignmentsendEntireSection()
                    }
                }

                override fun onUploadError(error: String?) {
                    Log.e("Upload Error", error!!)
                }
            })
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

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_PDF && resultCode == RESULT_OK && data != null) {

            if (resultCode == RESULT_OK) {
                uri = data.data!!
                Log.d("uri", uri.toString())
                val uriString: String = uri.toString()

                var Count: String? = null
                if (CommonUtil.SelcetedFileList != null) {
                    Count = CommonUtil.SelcetedFileList.size.toString()
                    binding.lblFileselectedstate!!.visibility = View.VISIBLE
                    binding.lblFileselectedstate!!.setText("Nmber of file selected :" + Count)

                } else {
                    binding.lblFileselectedstate!!.visibility = View.GONE
                }

                if (uriString.startsWith("content://")) {
                    ReadAndWriteFile(uri, ".pdf")

                }
            }
        }

        if (requestCode == SELECT_VIDEO) {
            if (data != null) {
                var SelcetedFileList = data.getStringArrayListExtra("images")!!
                var VideoFilePath = SelcetedFileList.get(0)

                uri = VideoFilePath.toUri()
                videofile = VideoFilePath
                Log.d("video file", videofile.toString())
                VimeoVideoUpload(this, videofile!!)
                VimeoVideoUpload(this, VideoFilePath)
                Log.d("VideoFilePath", VideoFilePath)

            }

        }

        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == REQUEST_Camera) {
                CommonUtil.SelcetedFileList.add(imageFilePath!!)

                Log.d("imageFilePath", imageFilePath.toString())

                var Count: String? = null
                if (CommonUtil.SelcetedFileList != null) {
                    Count = CommonUtil.SelcetedFileList.size.toString()
                    binding.lblFileselectedstate!!.visibility = View.VISIBLE
                    binding.lblFileselectedstate!!.setText("Nmber of file selected :" + Count)

                } else {
                    binding.lblFileselectedstate!!.visibility = View.GONE
                }

                CommonUtil.SelcetedFileList.forEach {
                    var path = it

                }

            }
        }
    }


    fun ReadAndWriteFile(uri: Uri?, type: String) {
        try {

            uri?.let {
                this.contentResolver?.openInputStream(it).use { `in` ->
                    if (`in` == null) return
                    try {
                        PDFTempFileWrite = File.createTempFile("File_", type, outputDir)
                        var pdfPath: String = PDFTempFileWrite?.path!!
                        CommonUtil.extension = pdfPath.substring(pdfPath.lastIndexOf("."))
                        Log.d("extensionpdf", CommonUtil.extension!!)
                        Log.d("PDFTempFileWrite", PDFTempFileWrite.toString())
                        CommonUtil.SelcetedFileList.add(pdfPath)
                        CommonUtil.SelcetedFileList.forEach {
                            var Count: String? = null
                            if (CommonUtil.SelcetedFileList != null) {
                                Count = CommonUtil.SelcetedFileList.size.toString()
                                binding.lblFileselectedstate!!.visibility = View.VISIBLE
                                binding.lblFileselectedstate!!.setText("Nmber of file selected :" + Count)

                            } else {
                                binding.lblFileselectedstate!!.visibility = View.GONE

                            }
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    this.contentResolver?.openOutputStream(Uri.fromFile(PDFTempFileWrite))
                        .use { out ->
                            if (out == null) return
                            val buf = ByteArray(1024)
                            var len = 0
                            while (true) {
                                try {
                                    if (`in`.read(buf).also({ len = it }) <= 0) break
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                try {
                                    out.write(buf, 0, len)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun VimeoVideoUpload(activity: Activity, file: String) {
        val strsize = file.length
        Log.d("size", strsize.toString())
        val clientinterceptor = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        clientinterceptor.interceptors().add(interceptor)
        val client1: OkHttpClient
        client1 = OkHttpClient.Builder().connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES).writeTimeout(5, TimeUnit.MINUTES).build()
        val retrofit = Retrofit.Builder().client(client1).baseUrl("https://api.vimeo.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service: ApiInterfaces = retrofit.create(ApiInterfaces::class.java)
        val mProgressDialog = ProgressDialog(activity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Connecting...")
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
        val `object` = JsonObject()
        val jsonObjectclasssec = JsonObject()
        jsonObjectclasssec.addProperty("approach", "post")
        jsonObjectclasssec.addProperty("size", strsize.toString())
        val jsonprivacy = JsonObject()
        jsonprivacy.addProperty("view", "unlisted")
        val jsonshare = JsonObject()
        jsonshare.addProperty("share", "false")
        val jsonembed = JsonObject()
        jsonembed.add("buttons", jsonshare)

        `object`.add("upload", jsonObjectclasssec)
        `object`.addProperty("name", binding.edtTitle!!.text.toString())
        `object`.addProperty("description", binding.txtDescription!!.text.toString())
        `object`.add("privacy", jsonprivacy)
        `object`.add("embed", jsonembed)
        val head = "Bearer " + "8d74d8bf6b5742d39971cc7d3ffbb51a"
        Log.d("header", head)
        val call: Call<JsonObject> = service.VideoUpload(`object`, head)
        Log.d("jsonOBJECT", `object`.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>, response: Response<JsonObject>
            ) {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                val res = response.code()
                Log.d("RESPONSE", res.toString())
                if (response.isSuccessful) {
                    try {
                        val object1 = JSONObject(response.body().toString())
                        Log.d("Response sucess", "response entered success")
                        val obj = object1.getJSONObject("upload")
                        val obj1 = object1.getJSONObject("embed")
                        val upload_link = obj.getString("upload_link")
                        val link = object1.getString("link")
                        val iframe = obj1.getString("html")
                        Log.d("c", upload_link)
                        Log.d("iframe", iframe)


                        // this  below two line is my checking line

                        CommonUtil.VimeoIframe = iframe
                        CommonUtil.VimeoVideoUrl = link
                        Log.d("VimeoVideoUrl", CommonUtil.VimeoIframe.toString())

                        try {
                            VIDEOUPLOAD(upload_link, file, activity)
                        } catch (e: Exception) {
                            Log.e("VIMEO Exception", e.message!!)
                            CommonUtil.Toast(activity, "Video sending failed.Retry")
                        }
                    } catch (e: Exception) {
                        Log.e("VIMEO Exception", e.message!!)
                        CommonUtil.Toast(activity, e.message)
                    }
                } else {
                    Log.d("Response fail", "fail")
                    CommonUtil.Toast(activity, "Video sending failed.Retry")
                }
            }

            override fun onFailure(
                call: Call<JsonObject>, t: Throwable
            ) {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                Log.e("Response Failure", t.message!!)
                CommonUtil.Toast(activity, "Video sending failed.Retry")
            }
        })
    }

    private fun VIDEOUPLOAD(
        upload_link: String, file: String, activity: Activity
    ) {
        val separated = upload_link.split("?").toTypedArray()

        val name = separated[0] //"/"
        val FileName = separated[1]
        val upload = name.replace("upload", "")
        val id = FileName.split("&").toTypedArray()
        val ticket_id = id[0]
        val video_file_id = id[1]
        val signature = id[2]
        val v6 = id[3]
        val redirect_url = id[4]
        val seperate1: Array<String> = ticket_id.split("=").toTypedArray()
        val ticket = seperate1[0] //"/"
        val ticket2 = seperate1[1]
        val seperate2: Array<String> = video_file_id.split("=").toTypedArray()
        val ticket1 = seperate2[0] //"/"
        val ticket3 = seperate2[1]
        val seper: Array<String> = signature.split("=").toTypedArray()
        val ticke = seper[0] //"/"
        val tick = seper[1]
        val sepera: Array<String> = v6.split("=").toTypedArray()
        val str = sepera[0] //"/"
        val str1 = sepera[1]
        val sucess: Array<String> = redirect_url.split("=").toTypedArray()
        val urlRIDERCT = sucess[0] //"/"
        val redirect_url123 = sucess[1]
        val client1: OkHttpClient
        client1 = OkHttpClient.Builder().connectTimeout(600, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.MINUTES).writeTimeout(40, TimeUnit.MINUTES).build()
        val retrofit = Retrofit.Builder().client(client1).baseUrl(upload)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val mProgressDialog = ProgressDialog(activity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Uploading...")
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
        val service: ApiInterfaces = retrofit.create(ApiInterfaces::class.java)
        var requestFile: RequestBody? = null
        try {
            val filepath = file
            Log.d("filepath", filepath)
            val `in`: InputStream = FileInputStream(filepath)
            val buf: ByteArray
            buf = ByteArray(`in`.available())
            while (`in`.read(buf) != -1);
            requestFile = RequestBody.create(
                "application/offset+octet-stream".toMediaTypeOrNull(), buf
            )
        } catch (e: IOException) {
            e.printStackTrace()
            CommonUtil.Toast(activity, e.message)
        }
        val call: Call<ResponseBody> = service.patchVimeoVideoMetaData(
            ticket2,
            ticket3,
            tick,
            str1,
            redirect_url123 + "www.voicesnapforschools.com",
            requestFile
        )
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>, response: Response<ResponseBody?>
            ) {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                try {
                    if (response.isSuccessful) {

                        selectedPaths.add(response.code().toString())
                        Log.d("Seletedfilevideo", selectedPaths.toString())

                    } else {

                        CommonUtil.Toast(activity, "Video sending failed.Retry")
                    }
                } catch (e: Exception) {
                    CommonUtil.Toast(activity, e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                CommonUtil.Toast(activity, "Video sending failed.Retry")
            }
        })
    }

//    fun awsFileUpload(activity: Activity?, pathind: Int?) {
//
//        Log.d("SelcetedFileList", CommonUtil.SelcetedFileList.size.toString())
//        val s3Uploader1Obj: S3Uploader1
//        s3Uploader1Obj = S3Uploader1(activity)
//        pathIndex = pathind!!
//
//        for (index in pathIndex until CommonUtil.SelcetedFileList.size) {
//            uploadFilePath = CommonUtil.SelcetedFileList.get(index)
//            Log.d("uploadFilePath", uploadFilePath.toString())
//            val extension = uploadFilePath!!.substring(uploadFilePath!!.lastIndexOf("."))
//            contentType = if (extension.equals(".pdf")) {
//                ".pdf"
//            } else {
//                ".jpg"
//            }
//            break
//        }
//
//        if (AWSUploadedFilesList.size < CommonUtil.SelcetedFileList.size) {
//            Log.d("test", uploadFilePath!!)
//            if (uploadFilePath != null) {
//                progressDialog = CustomLoading.createProgressDialog(this)
//
//                progressDialog!!.show()
//                fileNameDateTime =
//                    SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())
//                fileNameDateTime = "File_" + fileNameDateTime
//                Log.d("filenamedatetime", fileNameDateTime.toString())
//                s3Uploader1Obj.initUpload(
//                    uploadFilePath, contentType, CommonUtil.CollegeId.toString(), fileNameDateTime
//                )
//
//                s3Uploader1Obj.setOns3UploadDone(object : S3Uploader1.S3UploadInterface {
//                    override fun onUploadSuccess(response: String?) {
//                        if (response!!.equals("Success")) {
//
//                            CommonUtil.urlFromS3 = S3Utils.generates3ShareUrl(
//                                this@AddAssignment,
//                                CommonUtil.CollegeId.toString(),
//                                uploadFilePath,
//                                fileNameDateTime
//                            )
//
//                            Log.d("urifroms3", CommonUtil.urlFromS3.toString())
//
//                            if (!TextUtils.isEmpty(CommonUtil.urlFromS3)) {
//
//
//                                Awsuploadedfile.add(CommonUtil.urlFromS3.toString())
//                                Awsaupladedfilepath = Awsuploadedfile.joinToString(separator)
//
//
//                                fileName = File(uploadFilePath)
//
//                                filename = fileName!!.name
//                                AWSUploadedFilesList.add(
//                                    AWSUploadedFiles(
//                                        CommonUtil.urlFromS3!!, filename, contentType
//                                    )
//                                )
//
//                                Log.d("AWSUploadedFilesList", AWSUploadedFilesList.toString())
//                                awsFileUpload(activity, pathIndex + 1)
//
//                                if (CommonUtil.SelcetedFileList.size == AWSUploadedFilesList.size) {
//                                    progressDialog!!.dismiss()
//                                }
//                            }
//                        }
//                    }
//
//                    override fun onUploadError(response: String?) {
//                        progressDialog!!.dismiss()
//                        Log.d("error", "Error Uploading")
//                    }
//                })
//            }
//        } else {
//            AssignmentsendEntireSection()
//        }
//    }

    private val mTextEditorWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            binding.tvCount!!.text = s.length.toString() + "/500"
        }

        override fun afterTextChanged(s: Editable) {}
    }
}