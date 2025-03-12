package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.ActivitySender.AddVideo
import com.vsca.vsnapvoicecollege.Adapters.VideoAdapter
import com.vsca.vsnapvoicecollege.Interfaces.VideoListener
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.GetVideoListDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityNoticeboardBinding
import com.vsca.vsnapvoicecollege.databinding.ActivitySemesterCreditTableBinding
import java.util.Locale

class Video: BaseActivity<ActivityNoticeboardBinding>() {

    var videoAdapter: VideoAdapter? = null
    override var appViewModel: App? = null

    var GetVideoListData: List<GetVideoListDetails> = ArrayList()
    var CountVideo = "0"
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var AdWebURl: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var PreviousAddId: Int = 0
    override fun inflateBinding(): ActivityNoticeboardBinding {
        return ActivityNoticeboardBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        ActionBarMethod(this)


        accessBottomViewIcons(
            binding,
            R.id.img_swipe,
            R.id.layoutbottomCurve, R.id.recyclermenusbottom, R.id.swipeUpMenus, R.id.LayoutDepartment, R.id.LayoutCollege, R.id.imgAddPlus
        )
        TabDepartmentColor()
        MenuBottomType()

        binding.CommonLayout.layoutTab!!.visibility = View.GONE
        binding.CommonLayout.lblMenuTitle!!.setText(R.string.txt_Video)
        CommonUtil.OnMenuClicks("Video")


        binding.CommonLayout.imgAddPlus.setOnClickListener { addVideo() }
        binding.CommonLayout.LayoutAdvertisement.setOnClickListener { adclick() }



        SearchList!!.visibility = View.VISIBLE

        SearchList!!.setOnClickListener {

            Search!!.visibility = View.VISIBLE

        }

        if (CommonUtil.menu_readVideo == "1") {
            VideoRequest()
        }

        idSV!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false

            }

            override fun onQueryTextChange(msg: String): Boolean {

                filter(msg)
                return false
            }
        })

        txt_Cancel!!.setOnClickListener {

            Search!!.visibility = View.GONE

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
                            .into(binding.CommonLayout.imgAdvertisement!!)
                        Glide.with(this)
                            .load(AdSmallImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.CommonLayout.imgthumb!!)
                    }
                }
            })

        appViewModel!!.OverAllMenuResponseLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    if (response.data.isNullOrEmpty()) {
                        OverAllMenuCountData = emptyList()
                    } else {
                        OverAllMenuCountData = response.data!!
                        CountVideo = OverAllMenuCountData[0].video!!
                    }
                } else {
                    OverAllMenuCountData = emptyList()
                }
            }
        }

        appViewModel!!.VideoListLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                UserMenuRequest(this)
                AdForCollegeApi()
                if (status == 1) {
                    GetVideoListData = response.data!!
                    CountValueSet()
                    if (GetVideoListData.size > 0) {
                        binding.CommonLayout.lblNoRecordsFound!!.visibility = View.GONE
                        binding.CommonLayout.recyclerCommon!!.visibility = View.VISIBLE

                        videoAdapter = VideoAdapter(GetVideoListData, this, object : VideoListener {
                            override fun onVideoClick(
                                holder: VideoAdapter.MyViewHolder, data: GetVideoListDetails
                            ) {
                                holder.lnrplayvideo.setOnClickListener {
                                    val i: Intent = Intent(this@Video, VideoPlay::class.java)
                                    i.putExtra("iframe", data.iframe)
                                    i.putExtra("title", data.title)
                                    i.putExtra("description", data.description)
                                    i.putExtra("appread", data.isappviewed)
                                    i.putExtra("detailid", data.detailid)
                                    startActivity(i)
                                }
                            }
                        })

                        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                        binding.CommonLayout.recyclerCommon!!.layoutManager = mLayoutManager
                        binding.CommonLayout.recyclerCommon!!.itemAnimator = DefaultItemAnimator()
                        binding.CommonLayout.recyclerCommon!!.adapter = videoAdapter
                        binding.CommonLayout.recyclerCommon!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        videoAdapter!!.notifyDataSetChanged()
                    } else {
                        binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
                        binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
                    }
                } else {

                    binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
                    binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
                }

            } else {

                binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
                binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
            }
        }

        imgRefresh!!.setOnClickListener(View.OnClickListener {

            if (CommonUtil.menu_readVideo == "1") {
                VideoRequest()
            }
        })
    }


    private fun filter(text: String) {


        val filteredlist: java.util.ArrayList<GetVideoListDetails> = java.util.ArrayList()

        for (item in GetVideoListData) {
            if (item.title!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {

                filteredlist.add(item)

            }
        }
        if (filteredlist.isEmpty()) {

            Toast.makeText(this, CommonUtil.No_Data_Found, Toast.LENGTH_SHORT).show()
        } else {
            videoAdapter!!.filterList(filteredlist)

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

    private fun CountValueSet() {

        var intdepartment: Int? = null
        intdepartment = GetVideoListData.size
        if (intdepartment > 0) {
            binding.CommonLayout.lbltotalsize!!.visibility = View.VISIBLE
            binding.CommonLayout.lbltotalsize!!.text = intdepartment.toString()
        } else {
            binding.CommonLayout.lbltotalsize!!.visibility = View.GONE
        }

    }

    override val layoutResourceId: Int
        get() = R.layout.activity_noticeboard

    private fun VideoRequest() {
        val jsonObject = JsonObject()
        run {

            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
            appViewModel!!.getVideoList(jsonObject, this)
            Log.d("VideoRequest:", jsonObject.toString())
        }
    }

    fun addVideo() {
        val i: Intent = Intent(this, AddVideo::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        super.onResume()
    }
}