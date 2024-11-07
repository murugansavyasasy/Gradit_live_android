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
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
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
import java.util.Locale

class Noticeboard : BaseActivity() {

    var noticeboardAdapter: NoticeBoard? = null
    override var appViewModel: App? = null

    @JvmField
    @BindView(R.id.recyclerCommon)
    var recyclerNoticeboard: RecyclerView? = null

    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    @JvmField
    @BindView(R.id.lbltotalsize)
    var lbltotalsize: TextView? = null

    @JvmField
    @BindView(R.id.lblDepartmentSize)
    var lblDepartmentSize: TextView? = null

    @JvmField
    @BindView(R.id.lblCollegeSize)
    var lblCollegeSize: TextView? = null

    @JvmField
    @BindView(R.id.lblNoRecordsFound)
    var lblNoRecordsFound: TextView? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionBarMethod(this@Noticeboard)
        TabDepartmentColor()
        MenuBottomType()

        CommonUtil.OnMenuClicks("Noticeboard")


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
                            .into(imgAdvertisement!!)
                        Glide.with(this)
                            .load(AdSmallImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgthumb!!)
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
                            lblNoRecordsFound!!.visibility = View.GONE
                            recyclerNoticeboard!!.visibility = View.VISIBLE

                            noticeboardAdapter = NoticeBoard(GetNoticeboardData, this@Noticeboard)
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@Noticeboard)
                            recyclerNoticeboard!!.layoutManager = mLayoutManager
                            recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                            recyclerNoticeboard!!.adapter = noticeboardAdapter
                            recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            noticeboardAdapter!!.notifyDataSetChanged()

                        } else {
                            NoDataFound()
                        }
                    } else {
                        GetCollegeNoticeBoardData = response.data!!
                        val size = GetCollegeNoticeBoardData.size
                        if (size > 0) {
                            lblNoRecordsFound!!.visibility = View.GONE
                            recyclerNoticeboard!!.visibility = View.VISIBLE
                            noticeboardAdapter =
                                NoticeBoard(GetCollegeNoticeBoardData, this@Noticeboard)
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@Noticeboard)
                            recyclerNoticeboard!!.layoutManager = mLayoutManager
                            recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                            recyclerNoticeboard!!.adapter = noticeboardAdapter
                            recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
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

        recyclerNoticeboard!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        lblNoRecordsFound!!.visibility = View.VISIBLE
        recyclerNoticeboard!!.visibility = View.GONE
    }

    private fun CountValueSet() {

        if (!DepartmentCount.equals("0") && !DepartmentCount.equals("")) {
            lblDepartmentSize!!.visibility = View.VISIBLE
            lblDepartmentSize!!.text = DepartmentCount
        } else {
            lblDepartmentSize!!.visibility = View.GONE
            DepartmentCount = "0"
        }
        if (!CollegeCount.equals("0") && !CollegeCount.equals("")) {
            lblCollegeSize!!.visibility = View.VISIBLE
            lblCollegeSize!!.text = CollegeCount
        } else {
            lblCollegeSize!!.visibility = View.GONE
            CollegeCount = "0"
        }

        var intdepartment = Integer.parseInt(DepartmentCount!!)
        var intCollegecount = Integer.parseInt(CollegeCount!!)
        var TotalSizeCount = intdepartment + intCollegecount
        if (TotalSizeCount > 0) {
            lbltotalsize!!.visibility = View.VISIBLE
            lbltotalsize!!.text = TotalSizeCount.toString()
        } else {
            lbltotalsize!!.visibility = View.GONE
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

    @OnClick(R.id.LayoutDepartment)
    fun departmentClick() {
        bottomsheetStateCollpased()
        TabDepartmentColor()
        NoticeboardType = true
        if (CommonUtil.menu_readNoticeBoard.equals("1")) {
            NoticeboardRequest(NoticeboardType)
        }
    }

    @OnClick(R.id.LayoutCollege)
    fun collegeClick() {
        bottomsheetStateCollpased()
        NoticeboardType = false
        if (CommonUtil.menu_readNoticeBoard.equals("1")) {
            NoticeboardRequest(NoticeboardType)
        }
        TabCollegeColor()
    }

    @OnClick(R.id.imgAddPlus)
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

    @OnClick(R.id.LayoutAdvertisement)
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