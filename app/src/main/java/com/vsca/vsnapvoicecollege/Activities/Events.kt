package com.vsca.vsnapvoicecollege.Activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.ActivitySender.AddEvents
import com.vsca.vsnapvoicecollege.Adapters.EventsAdapter
import com.vsca.vsnapvoicecollege.Interfaces.EventClickListener
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.GetEventDetailsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityNoticeboardBinding
import java.util.Locale

class Events : BaseActivity<ActivityNoticeboardBinding>() {

    var eventsAdapter: EventsAdapter? = null
    override var appViewModel: App? = null

    var EventType = true
    var GetEvetnsData: List<GetEventDetailsData> = ArrayList()
    var CountUpcoming: String? = null
    var Countpast: String? = null
    var pastcount: String? = null
    var upcomingcount: String? = null
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var AdWebURl: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var PreviousAddId: Int = 0
    var departmentcount: Int? = null
    var Collegecount: Int? = null

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

        CommonUtil.OnMenuClicks("Events")

        accessBottomViewIcons(
            binding,
            R.id.LayoutDepartment,
            R.id.LayoutCollege,
            R.id.imgAddPlus
        )
        TabDepartmentColor()

        binding.CommonLayout.LayoutAdvertisement.setOnClickListener { adclick() }
        binding.CommonLayout.LayoutDepartment.setOnClickListener { departmentClick() }
        binding.CommonLayout.LayoutCollege.setOnClickListener { collegeClick() }
        binding.CommonLayout.imgAddPlus.setOnClickListener { plusClick() }

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

        binding.CommonLayout.lblMenuTitle!!.setText(R.string.txt_events)
        binding.CommonLayout.lblDepartment!!.setText(R.string.txt_upcoming)
        binding.CommonLayout.lblCollege!!.setText(R.string.txt_past)
        CommonUtil.EventEdit = "Edit"

        if (CommonUtil.EventStatus.equals("Past")) {
            EventType = false
            CommonUtil.EventEdit = "Add"
            TabCollegeColor()
        } else {
            TabDepartmentColor()
            EventType = true
            CommonUtil.EventEdit = "Edit"
        }

        appViewModel!!.AdvertisementLiveData?.observe(this,
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
                        Glide.with(this).load(AdBackgroundImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.CommonLayout.imgAdvertisement!!)
                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
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
                        CountUpcoming = OverAllMenuCountData[0].upcomingevents
                        Countpast = OverAllMenuCountData[0].pastevents
                        CountValueSet()
                    }
                } else {
                    OverAllMenuCountData = emptyList()
                }
            }
        }

        appViewModel!!.eventListbyTypeliveData!!.observe(this) { response ->
            markInitialApiDone()
            if (response != null) {
                CommonUtil.EventParticulerId = ""
                val status = response.status
                if (status == 1) {
                    if (EventType) {

                        GetEvetnsData = response.data!!
                        val size = GetEvetnsData.size
                        if (size > 0) {
                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.GONE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.VISIBLE
                            eventsAdapter =
                                EventsAdapter(GetEvetnsData, this, object : EventClickListener {
                                    override fun oneventClick(
                                        holder: EventsAdapter.MyViewHolder,
                                        item: GetEventDetailsData
                                    ) {
                                        holder.lnrNoticeboardd?.setOnClickListener {
                                            CommonUtil.EventParticulerId = item.eventid.toString()
                                            val Type: String = "event"
                                            if (item.isappread.equals("0")) {
                                                AppReadStatus(
                                                    this@Events,
                                                    Type,
                                                    item.eventdetailsid!!
                                                )
                                                item.isappread = "1"
                                                holder.lblNewCircle!!.visibility = View.GONE
                                            }
                                            val i =
                                                Intent(this@Events, EventsViewDetails::class.java)
                                            CommonUtil.EventCreatedby = item.createdby.toString()
                                            i.putExtra("EventsData", item)
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(i)
                                        }
                                    }
                                })
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            binding.CommonLayout.recyclerCommon!!.layoutManager = mLayoutManager
                            binding.CommonLayout.recyclerCommon!!.itemAnimator = DefaultItemAnimator()
                            binding.CommonLayout.recyclerCommon!!.adapter = eventsAdapter
                            binding.CommonLayout.recyclerCommon!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            eventsAdapter!!.notifyDataSetChanged()
                        } else {
                            binding.CommonLayout.lblNoRecordsFound!!.text=getString(R.string.txt_no_data_found)
                            NoDataFound()
                        }

                    } else {

                        GetEvetnsData = response.data!!
                        val size = GetEvetnsData.size
                        Log.d("datasizeElse", size.toString())

                        if (size > 0) {

                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.GONE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.VISIBLE
                            eventsAdapter =
                                EventsAdapter(GetEvetnsData, this, object : EventClickListener {
                                    override fun oneventClick(
                                        holder: EventsAdapter.MyViewHolder,
                                        item: GetEventDetailsData
                                    ) {

                                        holder.lnrNoticeboardd!!.setOnClickListener {

                                            val Type: String = "event"
                                            CommonUtil.EventParticulerId = item.eventid.toString()

                                            if (item.isappread.equals("0")) {
                                                AppReadStatus(
                                                    this@Events,
                                                    Type,
                                                    item.eventdetailsid!!
                                                )
                                                item.isappread = "1"
                                                holder.lblNewCircle!!.visibility = View.GONE
                                            }

                                            val i =
                                                Intent(this@Events, EventsViewDetails::class.java)
                                            i.putExtra("EventsData", item)
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(i)

                                        }
                                    }
                                })

                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            binding.CommonLayout.recyclerCommon!!.layoutManager = mLayoutManager
                            binding.CommonLayout.recyclerCommon!!.itemAnimator = DefaultItemAnimator()
                            binding.CommonLayout.recyclerCommon!!.adapter = eventsAdapter
                            binding.CommonLayout.recyclerCommon!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            eventsAdapter!!.notifyDataSetChanged()
                        } else {
                            binding.CommonLayout.lblNoRecordsFound!!.text=getString(R.string.txt_no_data_found)
                            NoDataFound()
                        }
                    }

                } else {
                    if (EventType) {
                        binding.CommonLayout.lblNoRecordsFound!!.text=response.message?:getString(R.string.txt_no_data_found)
                        NoDataFound()
                        GetEvetnsData = response.data!!
                        val size = GetEvetnsData.size
                        upcomingcount = size.toString()

                        val intdepartment = Integer.parseInt(upcomingcount!!)

                        CountUpcoming = intdepartment.toString()

                        departmentcount = Integer.parseInt(CountUpcoming!!)

                        if (intdepartment > 0) {
                            binding.CommonLayout.lblDepartmentSize!!.text = CountUpcoming
                            binding.CommonLayout.lblDepartmentSize!!.visibility = View.VISIBLE
                        } else {
                            binding.CommonLayout.lblDepartmentSize!!.text = CountUpcoming
                            binding.CommonLayout.lblDepartmentSize!!.visibility = View.VISIBLE
                        }

                    } else {
                        binding.CommonLayout.lblNoRecordsFound!!.text=response.message?:getString(R.string.txt_no_data_found)

                        NoDataFound()
                        GetEvetnsData = response.data!!
                        val size = GetEvetnsData.size

                        pastcount = size.toString()
                        val intcollage = Integer.parseInt(pastcount!!)
                        Countpast = intcollage.toString()

                        Collegecount = Integer.parseInt(Countpast!!)

                        if (intcollage > 0) {
                            binding.CommonLayout.lblCollegeSize!!.text = Countpast
                            binding.CommonLayout.lblCollegeSize!!.visibility = View.VISIBLE
                        } else {
                            binding.CommonLayout.lblCollegeSize!!.text = Countpast
                            binding.CommonLayout.lblCollegeSize!!.visibility = View.VISIBLE
                        }
                    }
                }
            } else {
                NoDataFound()
                binding.CommonLayout.lblNoRecordsFound!!.text=getString(R.string.error_null_cursor)
            }
        }

        imgRefresh!!.setOnClickListener {
            if (EventType) {
                EventType = true
                if (CommonUtil.menu_readEvent.equals("1")) {
                    EventRequest(EventType)
                }
            } else {
                EventType = false
                if (CommonUtil.menu_readEvent.equals("1")) {
                    EventRequest(EventType)
                }
            }
        }
    }

    private fun filter(text: String) {

        val filteredlist: java.util.ArrayList<GetEventDetailsData> = java.util.ArrayList()

        for (item in GetEvetnsData) {
            if (item.topic!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {

                filteredlist.add(item)

            }
        }
        if (filteredlist.isEmpty()) {

            Toast.makeText(this, CommonUtil.No_Data_Found, Toast.LENGTH_SHORT).show()
        } else {
            eventsAdapter!!.filterList(filteredlist)

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

    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    private fun NoDataFound() {
        binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
        binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
    }

    private fun CountValueSet() {

        if (!CountUpcoming.equals("0") && !CountUpcoming.equals("")) {
            binding.CommonLayout.lblDepartmentSize!!.visibility = View.VISIBLE
            binding.CommonLayout.lblDepartmentSize!!.text = CountUpcoming
        } else {
            binding.CommonLayout.lblDepartmentSize!!.visibility = View.GONE
            CountUpcoming = "0"
        }
        if (!Countpast.equals("0") && !Countpast.equals("")) {
            binding.CommonLayout.lblCollegeSize!!.visibility = View.VISIBLE
            binding.CommonLayout.lblCollegeSize!!.text = Countpast
        } else {
            binding.CommonLayout.lblCollegeSize!!.visibility = View.GONE
            Countpast = "0"
        }

        val intdepartment = Integer.parseInt(CountUpcoming!!)
        val intCollegecount = Integer.parseInt(Countpast!!)
        val TotalSizeCount = intdepartment + intCollegecount
        if (TotalSizeCount > 0) {
            binding.CommonLayout.lbltotalsize!!.visibility = View.VISIBLE
            binding.CommonLayout.lbltotalsize!!.text = TotalSizeCount.toString()
        } else {
            binding.CommonLayout.lbltotalsize!!.visibility = View.GONE
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_noticeboard

    private fun EventRequest(type: Boolean, showLoader: Boolean = true) {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3") {
            jsonObject.addProperty(ApiRequestNames.Req_sectionid, "0")
            jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId?.toString()?:"")
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.SectionId)
            jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId?.toString()?:"")
        }
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        if (type) {
            jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.UpcomingEvents)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.PastEvents)
        }
        appViewModel!!.getEventListbyType(jsonObject, this, showLoader)
        Log.d("EventListRequest:", jsonObject.toString())
    }

    fun departmentClick() {
        if (EventType) return
        CommonUtil.EventStatus = "Upcoming"
        TabDepartmentColor()
        EventType = true
        if (CommonUtil.menu_readEvent.equals("1")) {
            EventRequest(EventType)
        }
        CommonUtil.EventEdit = "Edit"
    }

    fun collegeClick() {
        if (!EventType) return
        CommonUtil.EventStatus = "Past"
        EventType = false
        if (CommonUtil.menu_readEvent.equals("1")) {
            EventRequest(EventType)
        }
        CommonUtil.EventEdit = "Add"
        TabCollegeColor()
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        CommonUtil.EventStatus = "Upcoming"
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        if (isFirstLoad) {
            isFirstLoad = false
            loadInitialData()
        }
    }

    /**
     * Fires the initial APIs once, behind a single shared loader instead of one loader per call:
     *   - GetOverallcountByMenuType (count)
     *   - GetAddsForCollege (ads)
     *   - GetEventListByType (list)
     * The per-call loaders are suppressed (showLoader = false); the shared loader is dismissed
     * by [markInitialApiDone] once every response has come back.
     */
    private fun loadInitialData() {
        if (CommonUtil.menu_readEvent != "1") return
        pendingInitialApiCount = 3
        initialLoader = CustomLoading.createProgressDialog(this)
        OverAllMenuCountRequest(this, "8", false)
        AdForCollegeApi(false)
        EventRequest(EventType, false)
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

    fun plusClick() {

        val i: Intent = Intent(this, AddEvents::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }
}