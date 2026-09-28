package com.vsca.vsnapvoicecollege.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.view.WindowInsetsControllerCompat
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
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
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

    // Single loader shown until the initial APIs (count + ads + list) all finish; the individual
    // per-call loaders are suppressed for these calls. pendingInitialApiCount tracks how many remain.
    private var isFirstLoad = true
    private var initialLoader: ProgressDialog? = null
    private var pendingInitialApiCount = 0

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
        setupEdgeToEdgeAuto(
            rootView = binding.Main,
            statusBarBgView = binding.statusBarBackground,
            priority = CommonUtil.Priority
        )

        if (supportActionBar != null) {
            ActionBarMethod(this)
            fixActionBarOverlap(binding.LayoutBottomMenus)
        }

        accessBottomViewIcons(
            binding,
            R.id.LayoutDepartment,
            R.id.LayoutCollege,
            R.id.imgAddPlus
        )
        TabDepartmentColor()

        CommonUtil.OnMenuClicks("Noticeboard")

        binding.CommonLayout.LayoutAdvertisement.setOnClickListener { adclick() }
        binding.CommonLayout.imgAddPlus.setOnClickListener { imgaddclick() }
        binding.CommonLayout.LayoutDepartment.setOnClickListener { departmentClick() }
        binding.CommonLayout.LayoutCollege.setOnClickListener { collegeClick() }

        SearchList!!.visibility = View.VISIBLE
        SearchList!!.setOnClickListener {
            Search!!.visibility = View.VISIBLE
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
                markInitialApiDone()
                if (response != null) {
                    val status = response.status
                    if (status == 1) {
                        GetAdForCollegeData = response.data!!
                        for (j in GetAdForCollegeData.indices) {
                            AdSmallImage = GetAdForCollegeData[j].add_image
                            AdBackgroundImage = GetAdForCollegeData[0].background_image!!
                            AdWebURl = GetAdForCollegeData[0].add_url.toString()
                        }
                        Glide.with(this)
                            .load(AdBackgroundImage)
                            .placeholder(R.drawable.adv_place_holder)
                            .error(R.drawable.savyasasy_ads)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.CommonLayout.imgAdvertisement!!)
                        Glide.with(this)
                            .load(AdSmallImage)
                            .placeholder(R.drawable.adv_thumb_placeholder)
                            .error(R.drawable.adv_thumb_gradit_logo)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.CommonLayout.imgthumb!!)
                    }
                }
            })

        appViewModel!!.OverAllMenuResponseLiveData!!.observe(this) { response ->
            markInitialApiDone()
            if (response != null) {
                val status = response.status
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
            markInitialApiDone()
            if (response != null) {
                val status = response.status
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
                            binding.CommonLayout.lblNoRecordsFound!!.text=getString(R.string.txt_no_data_found)
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
                            binding.CommonLayout.lblNoRecordsFound!!.text=getString(R.string.txt_no_data_found)
                            NoDataFound()
                        }
                    }
                } else {
                    binding.CommonLayout.lblNoRecordsFound!!.text=response.message
                    NoDataFound()
                }
            } else {
                binding.CommonLayout.lblNoRecordsFound!!.text=getString(R.string.error_null_cursor)
                NoDataFound()
            }
        }
        imgRefresh!!.setOnClickListener {
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
        }

        binding.CommonLayout.recyclerCommon!!.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
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

    private fun AdForCollegeApi(showLoader: Boolean = true) {

        val mobilenumber = SharedPreference.getSH_MobileNumber(this)
        val devicetoken = SharedPreference.getSH_DeviceToken(this)
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_ad_device_token, devicetoken)
        jsonObject.addProperty(ApiRequestNames.Req_MemberID, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_mobileno, mobilenumber)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_previous_add_id, PreviousAddId)
        appviewModelbase!!.getAdforCollege(jsonObject, this, showLoader)
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

        val intdepartment = Integer.parseInt(DepartmentCount!!)
        val intCollegecount = Integer.parseInt(CollegeCount!!)
        val TotalSizeCount = intdepartment + intCollegecount
        if (TotalSizeCount > 0) {
            binding.CommonLayout.lbltotalsize!!.visibility = View.VISIBLE
            binding.CommonLayout.lbltotalsize!!.text = TotalSizeCount.toString()
        } else {
            binding.CommonLayout.lbltotalsize!!.visibility = View.GONE
        }
    }

    override val layoutResourceId: Int
        protected get() = R.layout.activity_noticeboard

    private fun NoticeboardRequest(type: Boolean, showLoader: Boolean = true) {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId?.toString() ?: "")
        jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId?.toString() ?: "")
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        if (type) {
            jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.DepartmentNotice)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.CollegeNotice)
        }
        appViewModel!!.getNoticeboardList(jsonObject, this@Noticeboard, showLoader)
        Log.d("NotiboardRequest:", jsonObject.toString())
    }

    fun departmentClick() {
        if (NoticeboardType) return
        TabDepartmentColor()
        NoticeboardType = true
        if (CommonUtil.menu_readNoticeBoard.equals("1")) {
            NoticeboardRequest(NoticeboardType)
        }
    }

    fun collegeClick() {
        if (!NoticeboardType) return
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
        super.onResume()
        if (isFirstLoad) {
            isFirstLoad = false
            loadInitialData()
        }
        CommonUtil.Multipleiamge.clear()
    }

    /**
     * Fires the initial APIs once, behind a single shared loader instead of one loader per call:
     *   - GetOverallcountByMenuType (count)
     *   - GetAddsForCollege (ads)
     *   - GetNoticeboradList (list)
     * The per-call loaders are suppressed (showLoader = false); the shared loader is dismissed
     * by [markInitialApiDone] once every response has come back.
     */
    private fun loadInitialData() {
        if (CommonUtil.menu_readNoticeBoard != "1") return
        pendingInitialApiCount = 3
        initialLoader = CustomLoading.createProgressDialog(this)
        OverAllMenuCountRequest(this, CommonUtil.MenuIDNoticeboard!!, false)
        AdForCollegeApi(false)
        NoticeboardRequest(NoticeboardType, false)
    }

    /** Counts down the pending initial APIs and dismisses the shared loader when all have finished. */
    private fun markInitialApiDone() {
        if (initialLoader == null || pendingInitialApiCount <= 0) return
        pendingInitialApiCount -= 1
        if (pendingInitialApiCount <= 0) {
            initialLoader?.dismiss()
            initialLoader = null
        }
    }

    override fun onDestroy() {
        initialLoader?.dismiss()
        initialLoader = null
        super.onDestroy()
    }

}