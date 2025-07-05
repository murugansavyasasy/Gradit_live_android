package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.ActivitySender.AddTextNoticeboard
import com.vsca.vsnapvoicecollege.Adapters.NoticeBoard
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.GetNoticeboardDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityNoticeboardBinding
import java.util.Locale

class Noticeboard : BaseActivity<ActivityNoticeboardBinding>() {

    var noticeboardAdapter: NoticeBoard? = null
    override var appViewModel: App? = null

    var NoticeboardType = true
    var GetNoticeboardData: ArrayList<GetNoticeboardDetails> = ArrayList()
    var GetCollegeNoticeBoardData: ArrayList<GetNoticeboardDetails> = ArrayList()
    var DepartmentCount: String? = null
    var CollegeCount: String? = null
    private lateinit var scrollListener: RecyclerView.OnScrollListener
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
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionBarMethod(this@Noticeboard)




        accessBottomViewIcons(
            binding,
            R.id.img_swipe,
            R.id.layoutbottomCurve, R.id.recyclermenusbottom, R.id.swipeUpMenus, R.id.LayoutDepartment, R.id.LayoutCollege, R.id.imgAddPlus
        )
        TabDepartmentColor()
        MenuBottomType()

        CommonUtil.OnMenuClicks("Noticeboard")

        binding.CommonLayout.LayoutAdvertisement.setOnClickListener { adclick() }
        binding.CommonLayout.imgAddPlus.setOnClickListener { imgaddclick() }
        binding.CommonLayout.LayoutDepartment.setOnClickListener { departmentClick() }
        binding.CommonLayout.LayoutCollege.setOnClickListener { collegeClick() }


        SearchList!!.visibility = View.VISIBLE

        SearchList!!.setOnClickListener {

            Search!!.visibility = View.VISIBLE

        }

        if (CommonUtil.menu_readNoticeBoard.equals("1")) {
            NoticeboardRequest(NoticeboardType)
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
                        DepartmentCount = OverAllMenuCountData[0].departmentnotice
                        CollegeCount = OverAllMenuCountData[0].collegenotice
                        CountValueSet()
                    }
                } else {
                    OverAllMenuCountData = emptyList()
                }
            }
        }

        appViewModel!!.noticeBoardResponseLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                UserMenuRequest(this@Noticeboard)
                if (CommonUtil.menu_readNoticeBoard.equals("1")) {
                    OverAllMenuCountRequest(this, CommonUtil.MenuIDNoticeboard!!)
                }
                if (status == 1) {
                    if (NoticeboardType) {
                        GetNoticeboardData = response.data!!
                        val size = GetNoticeboardData.size
                        GetCollegeNoticeBoardData.clear()
                        if (size > 0) {
                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.GONE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.VISIBLE

                            noticeboardAdapter = NoticeBoard(GetNoticeboardData, this@Noticeboard)
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@Noticeboard)
                            binding.CommonLayout.recyclerCommon!!.layoutManager = mLayoutManager
                            binding.CommonLayout.recyclerCommon!!.itemAnimator =
                                DefaultItemAnimator()
                            binding.CommonLayout.recyclerCommon!!.adapter = noticeboardAdapter
                            binding.CommonLayout.recyclerCommon!!.recycledViewPool.setMaxRecycledViews(
                                0,
                                80
                            )
                            noticeboardAdapter!!.notifyDataSetChanged()

                        } else {
                            NoDataFound()
                        }
                    } else {
                        GetCollegeNoticeBoardData = response.data!!
                        val size = GetCollegeNoticeBoardData.size
                        if (size > 0) {
                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.GONE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.VISIBLE
                            noticeboardAdapter =
                                NoticeBoard(GetCollegeNoticeBoardData, this@Noticeboard)
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@Noticeboard)
                            binding.CommonLayout.recyclerCommon!!.layoutManager = mLayoutManager
                            binding.CommonLayout.recyclerCommon!!.itemAnimator =
                                DefaultItemAnimator()
                            binding.CommonLayout.recyclerCommon!!.adapter = noticeboardAdapter
                            binding.CommonLayout.recyclerCommon!!.recycledViewPool.setMaxRecycledViews(
                                0,
                                80
                            )
                            noticeboardAdapter!!.notifyDataSetChanged()
                        } else {
                            NoDataFound()
                        }
                    }
                } else {
                    NoDataFound()
                }
            } else {
                NoDataFound()
            }
        }
        imgRefresh!!.setOnClickListener(View.OnClickListener {
            if (NoticeboardType) {
                NoticeboardType = true
                if (CommonUtil.menu_readNoticeBoard.equals("1")) {
                    NoticeboardRequest(NoticeboardType)
                }
            } else {
                NoticeboardType = false
                if (CommonUtil.menu_readNoticeBoard.equals("1")) {
                    NoticeboardRequest(NoticeboardType)
                }
            }
        })

        binding.CommonLayout.recyclerCommon!!.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    bottomsheetStateCollpased()
                }
            }
        })
    }

    private fun filter(text: String) {

        if (NoticeboardType) {

            val filteredlist: java.util.ArrayList<GetNoticeboardDetails> = java.util.ArrayList()

            for (item in GetNoticeboardData) {
                if (item.topic!!.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {

                    filteredlist.add(item)

                }
            }
            if (filteredlist.isEmpty()) {

                Toast.makeText(this, CommonUtil.No_Data_Found, Toast.LENGTH_SHORT).show()
            } else {
                noticeboardAdapter!!.filterList(filteredlist)

            }

        } else {

            val filteredlist: java.util.ArrayList<GetNoticeboardDetails> = java.util.ArrayList()

            for (item in GetCollegeNoticeBoardData) {
                if (item.topic!!.lowercase(Locale.getDefault())
                        .contains(text.lowercase(Locale.getDefault()))
                ) {

                    filteredlist.add(item)

                }
            }
            if (filteredlist.isEmpty()) {

                Toast.makeText(this, CommonUtil.No_Data_Found, Toast.LENGTH_SHORT).show()
            } else {
                noticeboardAdapter!!.filterList(filteredlist)

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

    private fun NoDataFound() {
        binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
        binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
    }

    private fun CountValueSet() {

        if (!DepartmentCount.equals("0") && !DepartmentCount.equals("")) {
            binding.CommonLayout.lblDepartmentSize!!.visibility = View.VISIBLE
            binding.CommonLayout.lblDepartmentSize!!.text = DepartmentCount
        } else {
            binding.CommonLayout.lblDepartmentSize!!.visibility = View.GONE
            DepartmentCount = "0"
        }
        if (!CollegeCount.equals("0") && !CollegeCount.equals("")) {
            binding.CommonLayout.lblCollegeSize!!.visibility = View.VISIBLE
            binding.CommonLayout.lblCollegeSize!!.text = CollegeCount
        } else {
            binding.CommonLayout.lblCollegeSize!!.visibility = View.GONE
            CollegeCount = "0"
        }

        var intdepartment = Integer.parseInt(DepartmentCount!!)
        var intCollegecount = Integer.parseInt(CollegeCount!!)
        var TotalSizeCount = intdepartment + intCollegecount
        if (TotalSizeCount > 0) {
            binding.CommonLayout.lbltotalsize!!.visibility = View.VISIBLE
            binding.CommonLayout.lbltotalsize!!.text = TotalSizeCount.toString()
        } else {
            binding.CommonLayout.lbltotalsize!!.visibility = View.GONE
        }
    }

    override val layoutResourceId: Int
        protected get() = R.layout.activity_noticeboard

    private fun NoticeboardRequest(type: Boolean) {
        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            if (CommonUtil.Priority == "p7" || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3") {
                jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId)
            } else {
                jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId)
            }
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
            if (type) {
                jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.DepartmentNotice)
            } else {
                jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.CollegeNotice)
            }
            appViewModel!!.getNoticeboardList(jsonObject, this@Noticeboard)
            Log.d("NotiboardRequest:", jsonObject.toString())
        }
    }

    fun departmentClick() {
        bottomsheetStateCollpased()
        TabDepartmentColor()
        NoticeboardType = true
        if (CommonUtil.menu_readNoticeBoard.equals("1")) {
            NoticeboardRequest(NoticeboardType)
        }
    }

    fun collegeClick() {
        bottomsheetStateCollpased()
        NoticeboardType = false
        if (CommonUtil.menu_readNoticeBoard.equals("1")) {
            NoticeboardRequest(NoticeboardType)
        }
        TabCollegeColor()
    }

    fun imgaddclick() {
        val i: Intent = Intent(this, AddTextNoticeboard::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.putExtra("screentype", true)
        startActivity(i)

    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }

    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        AdForCollegeApi()
        CommonUtil.Multipleiamge.clear()
        super.onResume()
    }

}