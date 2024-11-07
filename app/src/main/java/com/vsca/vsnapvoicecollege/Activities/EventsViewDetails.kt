package com.vsca.vsnapvoicecollege.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.AWS.S3Uploader
import com.vsca.vsnapvoicecollege.AWS.S3Utils
import com.vsca.vsnapvoicecollege.ActivitySender.AddEvents
import com.vsca.vsnapvoicecollege.Adapters.EventsPhotoAdapter
import com.vsca.vsnapvoicecollege.Model.AWSUploadedFiles
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.GetEventDetailsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.albumImage.AlbumSelectActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EventsViewDetails : ActionBarActivity() {


    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    @JvmField
    @BindView(R.id.lblEventDate)
    var lblEventDate: TextView? = null

    @JvmField
    @BindView(R.id.lblEventDescription)
    var lblEventDescription: TextView? = null

    @JvmField
    @BindView(R.id.lblEventTopic)
    var lblEventTopic: TextView? = null

    @JvmField
    @BindView(R.id.lblEventVenue)
    var lblEventVenue: TextView? = null

    @JvmField
    @BindView(R.id.btn_addpic)
    var btn_addpic: TextView? = null

    @JvmField
    @BindView(R.id.lblEventTime)
    var lblEventTime: TextView? = null

    @JvmField
    @BindView(R.id.recyleEventPhoto)
    var recyleEventPhoto: RecyclerView? = null

    @JvmField
    @BindView(R.id.lblNoPhotoFound)
    var lblNoPhotoFound: TextView? = null

    @JvmField
    @BindView(R.id.btn_edittevent)
    var btn_edittevent: TextView? = null

    @JvmField
    @BindView(R.id.UPLOADIMAGE)
    var UPLOADIMAGE: Button? = null


    //  AWS
    var Awsuploadedfile = ArrayList<String>()
    var pathIndex = 0
    var uploadFilePath: String? = null
    var contentType: String? = null
    var AWSUploadedFilesList = ArrayList<AWSUploadedFiles>()
    var progressDialog: ProgressDialog? = null
    var fileNameDateTime: String? = null

    var fileName: File? = null
    var filename: String? = null
    var Awsaupladedfilepath: String? = null
    var separator = ","
    var EventEdit: String? = null
    var eventsdata: GetEventDetailsData? = null
    var photoAdapter: EventsPhotoAdapter? = null
    private var eventPhotosList: List<String>? = null
    var eventsDetaildID: String? = null
    var eventID: String? = null
    var LoginMemberid: String? = null
    var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    val REQUEST_GAllery = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this@EventsViewDetails)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this)
        BaseActivity.UserMenuRequest(this)



        if (CommonUtil.EventEdit.equals("Edit") && CommonUtil.MemberId.toString() == CommonUtil.EventCreatedby) {
            btn_edittevent!!.visibility = View.VISIBLE
        } else {
            btn_edittevent!!.visibility = View.GONE
        }

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
                        Glide.with(this)
                            .load(AdBackgroundImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this)
                            .load(AdSmallImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgthumb!!)
                    }
                }
            })

        appViewModel!!.Eventpicupdate!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Events::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                } else {


                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Events::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }



        eventsdata = intent.getSerializableExtra("EventsData") as? GetEventDetailsData

        lblEventTime!!.text = eventsdata!!.event_time
        lblEventVenue!!.text = eventsdata!!.venue
        lblEventDate!!.text = eventsdata!!.event_date
        lblEventTopic!!.text = eventsdata!!.topic
        lblEventDescription!!.text = eventsdata!!.body
        eventsDetaildID = eventsdata!!.eventdetailsid
        eventID = eventsdata!!.eventid

        Log.d("EventID", eventID.toString())

        if (eventsdata!!.isappread.equals("0")) {
            AppReadStatusActionbar(this, "event", eventsdata!!.eventdetailsid!!)
        }


        try {

            eventPhotosList = eventsdata!!.newfilepath
            if (eventPhotosList.isNullOrEmpty()) {
                recyleEventPhoto!!.visibility = View.GONE
                lblNoPhotoFound!!.visibility = View.VISIBLE
            } else {
                eventPhotosList = eventsdata!!.newfilepath

                if (eventPhotosList!!.size > 0) {
                    recyleEventPhoto!!.visibility = View.VISIBLE
                    lblNoPhotoFound!!.visibility = View.GONE

                    photoAdapter = EventsPhotoAdapter(eventPhotosList!!, this)
                    val mLayoutManager: RecyclerView.LayoutManager =
                        GridLayoutManager(applicationContext, 3)
                    recyleEventPhoto!!.layoutManager = mLayoutManager
                    recyleEventPhoto!!.isNestedScrollingEnabled = true
                    recyleEventPhoto!!.addItemDecoration(GridSpacingItemDecoration(4, false))
                    recyleEventPhoto!!.itemAnimator = DefaultItemAnimator()
                    recyleEventPhoto!!.adapter = photoAdapter

                } else {

                    recyleEventPhoto!!.visibility = View.GONE
                    lblNoPhotoFound!!.visibility = View.VISIBLE
                }
            }
        } catch (NpE: NullPointerException) {
            recyleEventPhoto!!.visibility = View.GONE
            lblNoPhotoFound!!.visibility = View.VISIBLE
            NpE.printStackTrace()
        }

        if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                "p2"
            ) || CommonUtil.Priority.equals(
                "p3"
            ) || CommonUtil.Priority.equals(
                "p6"
            )
        ) {
            btn_addpic!!.visibility = View.VISIBLE
        } else {
            btn_addpic!!.visibility = View.GONE
        }

        btn_edittevent!!.setOnClickListener {

            EventEdit = "EventEdit"
            val intent1 = Intent(this, AddEvents::class.java)
            intent1.putExtra("EventsData", eventsdata)
            intent1.putExtra("EventEdit", EventEdit)
            startActivity(intent1)

        }
    }

    @OnClick(R.id.btn_addpic)
    fun btn_addpic() {

        val intent1 = Intent(this, AlbumSelectActivity::class.java)
        intent1.putExtra("Gallery", "Images")
        startActivityForResult(intent1, REQUEST_GAllery)

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

    private fun Eventupdateimage() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_Eventheaderid, eventID)
        jsonObject.addProperty(ApiRequestNames.Req_Userid, CommonUtil.MemberId)

        val jsonimageupdate = JsonArray()
        val jsonimageobject = JsonObject()

        jsonimageobject.addProperty(ApiRequestNames.Req_FileName, Awsaupladedfilepath)
        jsonimageupdate.add(jsonimageobject)

        jsonObject.add(ApiRequestNames.Req_FileNameArray, jsonimageupdate)

        appviewModelbase!!.Eventimageupdate(jsonObject, this)
        Log.d("Eventimagejsonobject:", jsonObject.toString())

    }

    override val layoutResourceId: Int
        protected get() = R.layout.activity_events

    @OnClick(R.id.imgEventback)
    fun eventclickback() {
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

    class GridSpacingItemDecoration(private val spanCount: Int, includeEdge: Boolean) :
        RecyclerView.ItemDecoration() {
        private var spacing = 4
        private val includeEdge: Boolean
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount
            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column - 1) * spacing / spanCount
                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }

        init {
            this.includeEdge = includeEdge
        }
    }

    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_CANCELED) {

            if (requestCode == REQUEST_GAllery) {
                if (data != null) {
                    CommonUtil.SelcetedFileList = data.getStringArrayListExtra("images")!!
                    Log.d("SelectedFileListSize", CommonUtil.SelcetedFileList.size.toString())
                }
            }
        }

        if (!CommonUtil.SelcetedFileList.isEmpty()) {

            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@EventsViewDetails)
            alertDialog.setTitle(CommonUtil.Info)
            alertDialog.setMessage("Are you want to Upload the Image?")
            alertDialog.setPositiveButton(
                CommonUtil.Yes
            ) { _, _ ->

                awsFileUpload(this, pathIndex)

            }
            alertDialog.setNegativeButton(
                CommonUtil.OK
            ) { _, _ -> }
            val alert: AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        } else {
            Toast.makeText(this, "Select The Image", Toast.LENGTH_SHORT).show()
        }

    }

    fun awsFileUpload(activity: Activity?, pathind: Int?) {

        Log.d("SelcetedFileList", CommonUtil.SelcetedFileList.size.toString())
        val s3uploaderObj: S3Uploader
        s3uploaderObj = S3Uploader(activity)
        pathIndex = pathind!!

        for (index in pathIndex until CommonUtil.SelcetedFileList.size) {
            uploadFilePath = CommonUtil.SelcetedFileList[index]
            Log.d("uploadFilePath", uploadFilePath.toString())
            var extension = uploadFilePath!!.substring(uploadFilePath!!.lastIndexOf("."))
            if (extension.equals(".pdf")) {
                contentType = ".pdf"
            } else {
                contentType = ".jpg"
            }
            break
        }

        if (AWSUploadedFilesList.size < CommonUtil.SelcetedFileList.size) {
            Log.d("test", uploadFilePath!!)
            if (uploadFilePath != null) {
                progressDialog = CustomLoading.createProgressDialog(this)

                progressDialog!!.show()
                fileNameDateTime =
                    SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().time)
                fileNameDateTime = "File_" + fileNameDateTime
                Log.d("filenamedatetime", fileNameDateTime.toString())
                s3uploaderObj.initUpload(
                    uploadFilePath, contentType, CommonUtil.CollegeId.toString(), fileNameDateTime
                )

                s3uploaderObj.setOns3UploadDone(object : S3Uploader.S3UploadInterface {
                    override fun onUploadSuccess(response: String?) {
                        if (response!!.equals("Success")) {
                            if (CommonUtil.EventStatus.equals("Past")) {
                                CommonUtil.EventStatus = "Past"
                            } else {
                                CommonUtil.EventStatus = "Upcoming"
                            }
                            CommonUtil.urlFromS3 = S3Utils.generates3ShareUrl(
                                this@EventsViewDetails,
                                CommonUtil.CollegeId.toString(),
                                uploadFilePath,
                                fileNameDateTime
                            )
                            Log.d("urifroms3", CommonUtil.urlFromS3.toString())
                            if (!TextUtils.isEmpty(CommonUtil.urlFromS3)) {
                                Awsuploadedfile.add(CommonUtil.urlFromS3.toString())
                                Awsaupladedfilepath = Awsuploadedfile.joinToString(separator)
                                Log.d("Awsiploadfilepath", Awsaupladedfilepath.toString())
                                fileName = File(uploadFilePath)
                                filename = fileName!!.name
                                AWSUploadedFilesList.add(
                                    AWSUploadedFiles(
                                        CommonUtil.urlFromS3!!,
                                        filename,
                                        contentType
                                    )
                                )
                                Log.d("AWSUploadedFilesList", AWSUploadedFilesList.toString())
                                awsFileUpload(activity, pathIndex + 1)
                                if (CommonUtil.SelcetedFileList.size == AWSUploadedFilesList.size) {
                                    progressDialog!!.dismiss()
                                }
                            }
                        }
                    }

                    override fun onUploadError(response: String?) {
                        progressDialog!!.dismiss()
                        Log.d("error", "Error Uploading")
                    }
                })
            }
        } else {
            Eventupdateimage()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}