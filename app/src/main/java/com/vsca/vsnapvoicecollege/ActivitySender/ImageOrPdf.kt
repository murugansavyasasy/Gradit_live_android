package com.vsca.vsnapvoicecollege.ActivitySender


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.MenuDescription
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.MenuTitle
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.albumImage.AlbumSelectActivity
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageOrPdf : ActionBarActivity() {


    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    @JvmField
    @BindView(R.id.LayoutUploadImagePdf)
    var LayoutUploadImagePdf: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.btnConfirm)
    var btnConfirm: Button? = null


    @JvmField
    @BindView(R.id.edImgTitle)
    var edImgTitle: EditText? = null


    @JvmField
    @BindView(R.id.txt_imgpdfdescription)
    var txt_imgpdfdescription: EditText? = null

    @JvmField
    @BindView(R.id.tv_count)
    var tv_count: TextView? = null

    @JvmField
    @BindView(R.id.iblfileseletedotnot)
    var iblfileseletedotnot: TextView? = null

    @JvmField
    @BindView(R.id.lbltotalfile)
    var lbltotalfile: TextView? = null


    var FilePopup: PopupWindow? = null
    val REQUEST_Camera = 1
    val REQUEST_GAllery = 2
    val SELECT_PDF = 8778
    var imageFilePath: String? = null
    var PDFTempFileWrite: File? = null
    var photoTempFileWrite: File? = null
    var photoURI: Uri? = null
    var outputDir: File? = null

    var filename: String? = null
    var progressDialog: ProgressDialog? = null
    var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var uri: Uri? = null
    var Totalfile: String? = null
    var ScreenName: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionbarWithoutBottom(this)
        ButterKnife.bind(this)
        CommonUtil.SelcetedFileList.clear()
        imgRefresh!!.visibility = View.GONE

        appViewModel!!.AdvertisementLiveData?.observe(this,
            androidx.lifecycle.Observer<GetAdvertisementResponse?> { response ->
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
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgthumb!!)
                    }
                }
            })

        btnConfirm!!.setOnClickListener {

            if (!edImgTitle!!.text.isNullOrEmpty() && !txt_imgpdfdescription!!.text.isNullOrEmpty()) {
                MenuTitle = edImgTitle!!.text.toString()
                MenuDescription = txt_imgpdfdescription!!.text.toString()
                ScreenName = CommonUtil.Image_Pdf

                if (CommonUtil.SelcetedFileList.isNotEmpty()) {

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
                    CommonUtil.ApiAlert(this, "Choose Minimum one File")

                }

            } else {
                CommonUtil.ApiAlert(this, "Fill Title and Description")
            }
        }

        txt_imgpdfdescription!!.addTextChangedListener(mTextEditorWatcher)
        txt_imgpdfdescription!!.enableScrollText()
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

    @OnClick(R.id.LayoutUploadImagePdf)
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

            val intent1 = Intent(this, AlbumSelectActivity::class.java)
            intent1.putExtra("Gallery", "Images")
            startActivityForResult(intent1, REQUEST_GAllery)
            FilePopup!!.dismiss()
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

    private val mTextEditorWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            tv_count!!.text = s.length.toString() + "/500"
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_image_or_pdf

    @OnClick(R.id.imgImagePdfback)
    fun backClick() {
        super.onBackPressed()
    }

    @OnClick(R.id.btnCancel)
    fun btnCancel() {
        super.onBackPressed()
    }

    @OnClick(R.id.LayoutAdvertisement)
    fun adclick() {
        BaseActivity.LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        AdForCollegeApi()
        super.onResume()
    }

    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_CANCELED) {

            if (requestCode == REQUEST_Camera) {
                CommonUtil.SelcetedFileList.add(imageFilePath!!)
                Log.d("imageFilePath", imageFilePath.toString())

                CommonUtil.SelcetedFileList.forEach {
                    var path = it
                    if (CommonUtil.SelcetedFileList != null) {
                        Totalfile = CommonUtil.SelcetedFileList.size.toString()
                        lbltotalfile!!.text = "Number of Files : " + Totalfile
                        lbltotalfile!!.visibility = View.VISIBLE
                    } else {
                        lbltotalfile!!.visibility = View.GONE

                    }
                }

            } else if (requestCode == REQUEST_GAllery) {
                if (data != null) {
                    CommonUtil.SelcetedFileList = data.getStringArrayListExtra("images")!!
                    Log.d("SelectedFileListSize", CommonUtil.SelcetedFileList.size.toString())
                    Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())

                    CommonUtil.SelcetedFileList.forEach {
                        var path = it
                        if (CommonUtil.SelcetedFileList != null) {
                            Totalfile = CommonUtil.SelcetedFileList.size.toString()
                            lbltotalfile!!.text = "Number of Files : " + Totalfile
                            lbltotalfile!!.visibility = View.VISIBLE
                        } else {
                            lbltotalfile!!.visibility = View.GONE

                        }
                    }
                }
            } else if (requestCode == SELECT_PDF && resultCode == RESULT_OK && data != null) {

                if (resultCode == RESULT_OK) {
                    uri = data.data!!
                    Log.d("uri", uri.toString())
                    val uriString: String = uri.toString()
                    if (uriString.startsWith("content://")) {
                        var myCursor: Cursor? = null
                        ReadAndWriteFile(uri, ".pdf")
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
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

    fun ReadAndWriteFile(uri: Uri?, type: String) {
        try {
            uri?.let {
                this.contentResolver?.openInputStream(it).use { `in` ->
                    if (`in` == null) return
                    try {
                        PDFTempFileWrite = File.createTempFile("File_", type, outputDir)
                        var pdfPath: String = PDFTempFileWrite?.path!!
                        CommonUtil.extension =
                            pdfPath.substring(pdfPath.lastIndexOf("."))
                        Log.d("extensionpdf", CommonUtil.extension!!)
                        Log.d("PDFTempFileWrite", PDFTempFileWrite.toString())
                        CommonUtil.SelcetedFileList.add(pdfPath)
                        CommonUtil.SelcetedFileList.forEach {
                            var path = it
                            if (CommonUtil.SelcetedFileList != null) {
                                Totalfile = CommonUtil.SelcetedFileList.size.toString()
                                lbltotalfile!!.text = "Number of Files : " + Totalfile
                                lbltotalfile!!.visibility = View.VISIBLE
                            } else {
                                lbltotalfile!!.visibility = View.GONE

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
}