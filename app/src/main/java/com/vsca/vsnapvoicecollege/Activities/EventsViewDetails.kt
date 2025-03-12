package com.vsca.vsnapvoicecollege.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.AWS.AwsUploadingPreSigned
import com.vsca.vsnapvoicecollege.AWS.UploadCallback
import com.vsca.vsnapvoicecollege.ActivitySender.AddEvents
import com.vsca.vsnapvoicecollege.Adapters.EventsPhotoAdapter
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
import com.vsca.vsnapvoicecollege.databinding.ActivityEventsBinding
import java.io.File
import java.util.*

class EventsViewDetails : ActionBarActivity() {

    //  AWS
    var Awsuploadedfile = ArrayList<String>()
    var pathIndex = 0
    var uploadFilePath: String? = null
    var contentType: String? = null

    //    var AWSUploadedFilesList = ArrayList<AWSUploadedFiles>()
    var AWSUploadedFilesList = ArrayList<String>()
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
    private lateinit var binding: ActivityEventsBinding
    var isAwsUploadingPreSigned: AwsUploadingPreSigned? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActionbarWithoutBottom(this@EventsViewDetails)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionbarWithoutBottom(this)

        binding.btnAddpic.setOnClickListener { btnAddpic() }
        binding.imgEventback.setOnClickListener { super.onBackPressed() }
        binding.LayoutAdvertisement.setOnClickListener { adclick() }
        isAwsUploadingPreSigned = AwsUploadingPreSigned()
        if (CommonUtil.EventEdit.equals("Edit") && CommonUtil.MemberId.toString() == CommonUtil.EventCreatedby) {
            binding.btnEdittevent!!!!.visibility = View.VISIBLE
        } else {
            binding.btnEdittevent!!!!.visibility = View.GONE
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
                            .into(binding.imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this)
                            .load(AdSmallImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imgthumb!!)
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

        binding.lblEventTime!!.text = eventsdata!!.event_time
        binding.lblEventVenue!!.text = eventsdata!!.venue
        binding.lblEventDate!!.text = eventsdata!!.event_date
        binding.lblEventTopic!!.text = eventsdata!!.topic
        binding.lblEventDescription!!.text = eventsdata!!.body
        eventsDetaildID = eventsdata!!.eventdetailsid
        eventID = eventsdata!!.eventid

        Log.d("EventID", eventID.toString())

        if (eventsdata!!.isappread.equals("0")) {
            AppReadStatusActionbar(this, "event", eventsdata!!.eventdetailsid!!)
        }


        try {

            eventPhotosList = eventsdata!!.newfilepath
            if (eventPhotosList.isNullOrEmpty()) {
                binding.recyleEventPhoto!!.visibility = View.GONE
                binding.lblNoPhotoFound!!.visibility = View.VISIBLE
            } else {
                eventPhotosList = eventsdata!!.newfilepath

                if (eventPhotosList!!.size > 0) {
                    binding.recyleEventPhoto!!.visibility = View.VISIBLE
                    binding.lblNoPhotoFound!!.visibility = View.GONE

                    photoAdapter = EventsPhotoAdapter(eventPhotosList!!, this)
                    val mLayoutManager: RecyclerView.LayoutManager =
                        GridLayoutManager(applicationContext, 3)
                    binding.recyleEventPhoto!!.layoutManager = mLayoutManager
                    binding.recyleEventPhoto!!.isNestedScrollingEnabled = true
                    binding.recyleEventPhoto!!.addItemDecoration(
                        GridSpacingItemDecoration(
                            4,
                            false
                        )
                    )
                    binding.recyleEventPhoto!!.itemAnimator = DefaultItemAnimator()
                    binding.recyleEventPhoto!!.adapter = photoAdapter

                } else {

                    binding.recyleEventPhoto!!.visibility = View.GONE
                    binding.lblNoPhotoFound!!.visibility = View.VISIBLE
                }
            }
        } catch (NpE: NullPointerException) {
            binding.recyleEventPhoto!!.visibility = View.GONE
            binding.lblNoPhotoFound!!.visibility = View.VISIBLE
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
            binding.btnAddpic!!.visibility = View.VISIBLE
        } else {
            binding.btnAddpic!!.visibility = View.GONE
        }

        binding.btnEdittevent!!!!.setOnClickListener {

            EventEdit = "EventEdit"
            val intent1 = Intent(this, AddEvents::class.java)
            intent1.putExtra("EventsData", eventsdata)
            intent1.putExtra("EventEdit", EventEdit)
            startActivity(intent1)

        }
    }

    fun btnAddpic() {

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

                isUploadAWS()

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

    private fun isUploadAWS() {
        progressDialog = CustomLoading.createProgressDialog(this)
        progressDialog!!.show()
        Log.d("selectedImagePath", CommonUtil.SelcetedFileList.size.toString())
        for (i in CommonUtil.SelcetedFileList.indices) {
            AwsUploadingFile(
                CommonUtil.SelcetedFileList.get(i)
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
                    AWSUploadedFilesList.add(isAwsFile!!)
                    Awsuploadedfile.add(isAwsFile!!.toString())
                    Awsaupladedfilepath = Awsuploadedfile.joinToString(separator)
                    if (CommonUtil.EventStatus.equals("Past")) {
                        CommonUtil.EventStatus = "Past"
                    } else {
                        CommonUtil.EventStatus = "Upcoming"
                    }
                    if (AWSUploadedFilesList.size == CommonUtil.SelcetedFileList.size) {
                        progressDialog!!.dismiss()
                        Eventupdateimage()
                    }
                }

                override fun onUploadError(error: String?) {
                    Log.e("Upload Error", error!!)
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
//            uploadFilePath = CommonUtil.SelcetedFileList[index]
//            Log.d("uploadFilePath", uploadFilePath.toString())
//            var extension = uploadFilePath!!.substring(uploadFilePath!!.lastIndexOf("."))
//            if (extension.equals(".pdf")) {
//                contentType = ".pdf"
//            } else {
//                contentType = ".jpg"
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
//                    SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().time)
//                fileNameDateTime = "File_" + fileNameDateTime
//                Log.d("filenamedatetime", fileNameDateTime.toString())
//                s3Uploader1Obj.initUpload(
//                    uploadFilePath, contentType, CommonUtil.CollegeId.toString(), fileNameDateTime
//                )
//
//                s3Uploader1Obj.setOns3UploadDone(object : S3Uploader1.S3UploadInterface {
//                    override fun onUploadSuccess(response: String?) {
//                        if (response!!.equals("Success")) {
//                            if (CommonUtil.EventStatus.equals("Past")) {
//                                CommonUtil.EventStatus = "Past"
//                            } else {
//                                CommonUtil.EventStatus = "Upcoming"
//                            }
//                            CommonUtil.urlFromS3 = S3Utils.generates3ShareUrl(
//                                this@EventsViewDetails,
//                                CommonUtil.CollegeId.toString(),
//                                uploadFilePath,
//                                fileNameDateTime
//                            )
//                            Log.d("urifroms3", CommonUtil.urlFromS3.toString())
//                            if (!TextUtils.isEmpty(CommonUtil.urlFromS3)) {
//                                Awsuploadedfile.add(CommonUtil.urlFromS3.toString())
//                                Awsaupladedfilepath = Awsuploadedfile.joinToString(separator)
//                                Log.d("Awsiploadfilepath", Awsaupladedfilepath.toString())
//                                fileName = File(uploadFilePath)
//                                filename = fileName!!.name
////                                AWSUploadedFilesList.add(
////                                    AWSUploadedFiles(
////                                        CommonUtil.urlFromS3!!,
////                                        filename,
////                                        contentType
////                                    )
////                                )
//                                Log.d("AWSUploadedFilesList", AWSUploadedFilesList.toString())
//                                awsFileUpload(activity, pathIndex + 1)
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
//            Eventupdateimage()
//        }
//    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}