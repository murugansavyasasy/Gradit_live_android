package com.vsca.vsnapvoicecollege.ActivitySender

import android.app.ActionBar
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.vsca.vsnapvoicecollege.Adapters.VideoContentAdapter
import com.vsca.vsnapvoicecollege.Interfaces.ApiInterfaces
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.VideoRestrictionData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.VideoAlbum.AlbumVideoSelectVideoActivity
import com.vsca.vsnapvoicecollege.ViewModel.App
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.util.concurrent.TimeUnit

class AddVideo : ActionBarActivity() {

    @JvmField
    @BindView(R.id.LayoutUploadVideo)
    var LayoutUploadVideo: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.txtTitle)
    var txtTitle: EditText? = null

    @JvmField
    @BindView(R.id.txt_Selected)
    var txt_Selected: TextView? = null

    @JvmField
    @BindView(R.id.txtDescription)
    var txtDescription: EditText? = null

    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null


    private val REQUEST = 1
    private val SELECT_VIDEO = 2
    var uri: Uri? = null

    var videofile: String? = null

    var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var selectedPaths = mutableListOf<String>()
    var GetContentData: ArrayList<VideoRestrictionData>? = null
    var videoAdapter: VideoContentAdapter? = null
    var videoContentlist = ArrayList<String>()
    var ScreenName: String? = null
    var VideoTitle: String? = null
    var VideoDescription: String? = null
    var Videofile: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this)

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

        appViewModel!!.VideoRestrictContentMutableLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                BaseActivity.UserMenuRequest(this)
                if (status == 1) {
                    GetContentData = response.data!!
                    for (i in GetContentData!!.indices) {
                        val content = GetContentData!!.get(i).content
                        videoContentlist.add(content!!)
                    }
                    ShowVideoRestrictionPopUp()
                } else {
                    CommonUtil.ApiAlert(this, message)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }
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


    private fun ShowVideoRestrictionPopUp() {

        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = inflater.inflate(R.layout.popup_video_content_ui, null)
        val popupWindow = PopupWindow(
            layout,
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT,
            true
        )
        popupWindow.setContentView(layout)
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0)
        val ryccontent = layout.findViewById<View>(R.id.rycContent) as RecyclerView
        val btnAgree = layout.findViewById<View>(R.id.btnAgree) as Button
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        ryccontent.layoutManager = layoutManager
        ryccontent.itemAnimator = DefaultItemAnimator()
        videoAdapter = VideoContentAdapter(videoContentlist, this)
        ryccontent.adapter = videoAdapter
        btnAgree.setOnClickListener {
            popupWindow.dismiss()
            val intent1 = Intent(this, AlbumVideoSelectVideoActivity::class.java)
            intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            startActivityForResult(intent1, SELECT_VIDEO)
        }
    }


    override val layoutResourceId: Int
        get() = R.layout.activity_add_video

    @OnClick(R.id.LayoutUploadVideo)
    fun OpenGallery() {

        VideoTitle = txtTitle!!.text.toString()
        VideoDescription = txtDescription!!.text.toString()

        if (!VideoTitle.isNullOrEmpty() && !VideoDescription.isNullOrEmpty()) {

            appViewModel!!.VideoRestrictionContent(this)

        } else {
            Toast.makeText(this, CommonUtil.Fill_Title_and_Description, Toast.LENGTH_SHORT).show()
        }
    }

    @OnClick(R.id.btnCancel)
    fun imgBackClick() {
        super.onBackPressed()
    }

    @OnClick(R.id.imgImagePdfback)
    fun imgImagePdfback() {
        super.onBackPressed()
    }

    @OnClick(R.id.btnConfirm)
    fun Click() {

        if (!txtDescription!!.text.isNullOrEmpty() && !txtTitle!!.text.isNullOrEmpty() && txt_Selected!!.getVisibility() == View.VISIBLE) {

            ScreenName = "New Video"
            CommonUtil.title = txtTitle!!.text.toString()
            CommonUtil.Description = txtDescription!!.text.toString()
            if (CommonUtil.Priority == "p7") {

                val i: Intent = Intent(this, HeaderRecipient::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.putExtra("ScreenName", ScreenName)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)

            } else {

                if (CommonUtil.Priority == "p1") {

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
            Toast.makeText(this, "Video file is Empty", Toast.LENGTH_SHORT).show()
        }
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_CANCELED) {
            if (requestCode == SELECT_VIDEO) {
                if (data != null) {
                    val SelcetedFileList = data.getStringArrayListExtra("images")!!
                    val VideoFilePath = SelcetedFileList.get(0)
                    Log.d("filepath", VideoFilePath)
                    CommonUtil.videofile = VideoFilePath
                    txt_Selected!!.visibility = View.VISIBLE
                    Log.d("video file", CommonUtil.videofile.toString())
                    Log.d("VideoFilePath", VideoFilePath)

                }
            }
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
        `object`.addProperty("name", txtTitle!!.text.toString())
        `object`.addProperty("description", txtDescription!!.text.toString())
        `object`.add("privacy", jsonprivacy)
        `object`.add("embed", jsonembed)
        val head = "Bearer " + "8d74d8bf6b5742d39971cc7d3ffbb51a"
        Log.d("header", head)
        val call: Call<JsonObject> = service.VideoUpload(`object`, head)
        Log.d("jsonOBJECT", `object`.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                val res = response.code()
                Log.d("RESPONSE", response.toString())
                if (response.isSuccessful) {
                    try {
                        Log.d("try", "testtry")
                        val object1 = JSONObject(response.body().toString())
                        Log.d("responsevimeo", object1.toString())
                        Log.d("Response sucess", "response entered success")
                        val obj = object1.getJSONObject("upload")
                        val obj1 = object1.getJSONObject("embed")
                        val upload_link = obj.getString("upload_link")
                        Log.d("uploadlind", upload_link)
                        val link = object1.getString("link")
                        val iframe = obj1.getString("html")
                        Log.d("c", upload_link)
                        Log.d("iframe", iframe)


                        // this  below two line is Get Video url and iframe
                        CommonUtil.VimeoIframe = iframe
                        CommonUtil.VimeoVideoUrl = link
                        Log.d("VimeoVideoUrl", CommonUtil.VimeoVideoUrl.toString())

                        try {
                            Videofile = false
                            VIDEOUPLOAD(upload_link, file, activity)
                            Videofile = true

                        } catch (e: Exception) {
                            Log.e("VIMEO Exception", e.message!!)
                            CommonUtil.Toast(activity, "Video sending failed.Retry")
                        }
                    } catch (e: Exception) {
                        Log.e("VIMEO Exception", e.message!!)
                        CommonUtil.Toast(activity, e.message)
                    }
                } else {
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
        Log.d("uploadlink", upload_link)
        val separated = upload_link.split("?").toTypedArray()

        val name = separated[0] //"/"
        val FileName = separated[1]
        val upload = name.replace("upload", "")
        Log.d("uploadbaseurl", upload)
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
        Log.d("redirecturl", redirect_url)
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
            Log.d(
                "filepath",
                filepath
            )
            val `in`: InputStream = FileInputStream(filepath)
            Log.d("infile", `in`.toString())
            val buf: ByteArray
            buf = ByteArray(`in`.available())
            while (`in`.read(buf) != -1);
            Log.d("buf", buf.toString())
            requestFile =
                RequestBody.create("application/offset+octet-stream".toMediaTypeOrNull(), buf)
            Log.d(
                "requestfile",
                requestFile.toString()
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
                        Videofile = true
                        selectedPaths.add(response.code().toString())
                        Log.d("SeletedFilevideo", selectedPaths.toString())
                        CommonUtil.Toast(activity, "Successfull Uploaded")

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
}