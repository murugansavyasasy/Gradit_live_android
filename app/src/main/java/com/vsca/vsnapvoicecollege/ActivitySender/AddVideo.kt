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
import androidx.core.view.WindowInsetsControllerCompat
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
import com.vsca.vsnapvoicecollege.databinding.ActivityAddVideoBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
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
    private lateinit var binding: ActivityAddVideoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAddVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
         ActionbarWithoutBottom(this)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        binding.LayoutAdvertisement.setOnClickListener { adclick() }
        binding.btnConfirm.setOnClickListener { btnConfirm() }
        binding.btnCancel.setOnClickListener { super.onBackPressed() }
        binding.imgImagePdfback.setOnClickListener { super.onBackPressed() }
        binding.LayoutUploadVideo.setOnClickListener { OpenGallery() }

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

        appViewModel!!.VideoRestrictContentMutableLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
//                BaseActivity.UserMenuRequest(this)
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

     fun OpenGallery() {

        VideoTitle = binding.txtTitle!!.text.toString()
        VideoDescription = binding.txtDescription!!.text.toString()

        if (!VideoTitle.isNullOrEmpty() && !VideoDescription.isNullOrEmpty()) {

            appViewModel!!.VideoRestrictionContent(this)

        } else {
            Toast.makeText(this, CommonUtil.Fill_Title_and_Description, Toast.LENGTH_SHORT).show()
        }
    }


    fun btnConfirm() {

        if (!binding.txtDescription!!.text.isNullOrEmpty() && !binding.txtTitle!!.text.isNullOrEmpty() && binding.txtSelected!!.getVisibility() == View.VISIBLE) {

            ScreenName = "New Video"
            CommonUtil.title = binding.txtTitle!!.text.toString()
            CommonUtil.Description = binding.txtDescription!!.text.toString()
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
                    binding.txtSelected!!.visibility = View.VISIBLE
                    Log.d("video file", CommonUtil.videofile.toString())
                    Log.d("VideoFilePath", VideoFilePath)

                }
            }
        }
    }
}