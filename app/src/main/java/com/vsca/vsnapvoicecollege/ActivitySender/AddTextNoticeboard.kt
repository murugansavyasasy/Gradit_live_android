package com.vsca.vsnapvoicecollege.ActivitySender

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
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
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddTextNoticeboard : ActionBarActivity() {
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

    @JvmField
    @BindView(R.id.lbltotalfile)
    var lbltotalfile: TextView? = null

    @JvmField
    @BindView(R.id.tv_count)
    var tv_count: TextView? = null

    @JvmField
    @BindView(R.id.NestedchildlayoutRecy)
    var NestedchildlayoutRecy: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.Nestedchildlayout)
    var Nestedchildlayout: NestedScrollView? = null

    @JvmField
    @BindView(R.id.LayoutHeadernoticeboard)
    var LayoutHeadernoticeboard: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.LayoutHeaderImagePdf)
    var LayoutHeaderImagePdf: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.LayoutUploadImagePdf)
    var LayoutUploadImagePdf: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.btnConfirm)
    var btnConfirm: Button? = null

    @JvmField
    @BindView(R.id.txtTitle)
    var txtTitle: EditText? = null

    @JvmField
    @BindView(R.id.radio_group)
    var radio_group: RadioGroup? = null

    @JvmField
    @BindView(R.id.rcy_history)
    var rcy_history: RecyclerView? = null


    @JvmField
    @BindView(R.id.txtDescription)
    var txtDescription: EditText? = null

    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    var ScreenName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this)
        CommonUtil.seleteddataArraySection.clear()
        imgRefresh!!.visibility = View.GONE
        Nestedchildlayout!!.visibility = View.VISIBLE
        btnConfirm!!.visibility = View.VISIBLE
        CommonUtil.SelcetedFileList.clear()


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
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgthumb!!)
                    }
                }
            })

        txtDescription!!.addTextChangedListener(mTextEditorWatcher)
        txtDescription!!.enableScrollText()

        ScreenType = intent.getBooleanExtra("screentype", true)
        if (ScreenType!!) {
               LayoutUploadImagePdf!!.visibility = View.VISIBLE
            ScreenName = CommonUtil.Noticeboard
            LayoutHeadernoticeboard!!.visibility = View.VISIBLE
            radio_group!!.visibility = View.GONE
        } else {
            LayoutUploadImagePdf!!.visibility = View.GONE
            ScreenName = CommonUtil.Text
            LayoutHeadernoticeboard!!.visibility = View.GONE
            radio_group!!.visibility = View.VISIBLE
        }

        radio_group!!.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_B_msg -> {
                    NestedchildlayoutRecy!!.visibility = View.GONE
                    Nestedchildlayout!!.visibility = View.VISIBLE
                    btnConfirm!!.visibility = View.VISIBLE
                }

                R.id.radio_B_history -> {
                    NestedchildlayoutRecy!!.visibility = View.VISIBLE
                    btnConfirm!!.visibility = View.GONE
                    Nestedchildlayout!!.visibility = View.GONE
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
    }

    private fun loadhistory() {
        TextHistoryAdapter = TextHistoryAdapter(textdata, this)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        rcy_history!!.layoutManager = mLayoutManager
        rcy_history!!.itemAnimator = DefaultItemAnimator()
        rcy_history!!.setHasFixedSize(true)
        rcy_history!!.adapter = TextHistoryAdapter
        rcy_history!!.recycledViewPool.setMaxRecycledViews(0, 500)
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
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.Appid)
        appViewModel!!.textHistoryData(jsonObject, this)
        Log.d("_TextHistoryData:", jsonObject.toString())
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_add_text_noticeboard

    @OnClick(R.id.btnConfirm)
    fun btnNextClick() {

        MenuTitle = txtTitle!!.text.toString()
        MenuDescription = txtDescription!!.text.toString()
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

    @OnClick(R.id.btnCancel)
    fun btnCancelClick() {
        super.onBackPressed()
    }

    @OnClick(R.id.imgTextback)
    fun imgBackClick() {
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

    private val mTextEditorWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            tv_count!!.text = s.length.toString() + "/500"
        }

        override fun afterTextChanged(s: Editable) {}
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}