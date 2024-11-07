package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
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
import com.vsca.vsnapvoicecollege.ActivitySender.AddEvents
import com.vsca.vsnapvoicecollege.Adapters.EventsAdapter
import com.vsca.vsnapvoicecollege.Interfaces.EventClickListener
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.GetEventDetailsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import java.util.Locale

class Events : BaseActivity() {

    var eventsAdapter: EventsAdapter? = null
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
    @BindView(R.id.lblMenuTitle)
    var lblMenuTitle: TextView? = null

    @JvmField
    @BindView(R.id.lblDepartment)
    var lblDepartment: TextView? = null

    @JvmField
    @BindView(R.id.lblCollege)
    var lblCollege: TextView? = null

    @JvmField
    @BindView(R.id.lblDepartmentSize)
    var lblDepartmentSize: TextView? = null

    @JvmField
    @BindView(R.id.lblCollegeSize)
    var lblCollegeSize: TextView? = null

    @JvmField
    @BindView(R.id.lblNoRecordsFound)
    var lblNoRecordsFound: TextView? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)

        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionBarMethod(this)
        MenuBottomType()
        CommonUtil.OnMenuClicks("Events")
        TabDepartmentColor()

        SearchList!!.visibility = View.VISIBLE

        SearchList!!.setOnClickListener {
            Search!!.visibility = View.VISIBLE
        }

        if (CommonUtil.menu_readEvent.equals("1")) {
            EventRequest(EventType)
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


        lblMenuTitle!!.setText(R.string.txt_events)
        lblDepartment!!.setText(R.string.txt_upcoming)
        lblCollege!!.setText(R.string.txt_past)
        CommonUtil.EventEdit = "Edit"

        if (CommonUtil.EventStatus.equals("Past")) {

            bottomsheetStateCollpased()
            EventType = false
            if (CommonUtil.menu_readEvent.equals("1")) {
                EventRequest(EventType)
            }
            CommonUtil.EventEdit = "Add"
            TabCollegeColor()


        } else {

            bottomsheetStateCollpased()
            TabDepartmentColor()
            EventType = true

            if (CommonUtil.menu_readEvent == "1") {
                EventRequest(EventType)
            }

            CommonUtil.EventEdit = "Edit"
            bottomsheetStateCollpased()
        }

        appViewModel!!.AdvertisementLiveData?.observe(this,
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
                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgthumb!!)
                    }
                }
            })

        appViewModel!!.OverAllMenuResponseLiveData!!.observe(this) { response ->

            if (response != null) {

                val status = response.status
                val message = response.message
                if (status == 1) {
                    AdForCollegeApi()
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
            if (response != null) {
                CommonUtil.EventParticulerId = ""
                val status = response.status
                val message = response.message
                UserMenuRequest(this)
                if (status == 1) {

                    if (CommonUtil.menu_readEvent.equals("1")) {
                        OverAllMenuCountRequest(this, "8")
                    }
                    if (EventType) {

                        GetEvetnsData = response.data!!
                        val size = GetEvetnsData.size
                        if (size > 0) {
                            lblNoRecordsFound!!.visibility = View.GONE
                            recyclerNoticeboard!!.visibility = View.VISIBLE
                            eventsAdapter =
                                EventsAdapter(GetEvetnsData, this, object : EventClickListener {
                                    override fun oneventClick(
                                        holder: EventsAdapter.MyViewHolder,
                                        item: GetEventDetailsData
                                    ) {
                                        holder.lnrNoticeboardd?.setOnClickListener(View.OnClickListener {
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
                                        })
                                    }
                                })
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            recyclerNoticeboard!!.layoutManager = mLayoutManager
                            recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                            recyclerNoticeboard!!.adapter = eventsAdapter
                            recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            eventsAdapter!!.notifyDataSetChanged()
                        } else {
                            NoDataFound()
                        }

                    } else {

                        GetEvetnsData = response.data!!
                        val size = GetEvetnsData.size
                        Log.d("datasizeElse", size.toString())


                        if (size > 0) {

                            lblNoRecordsFound!!.visibility = View.GONE
                            recyclerNoticeboard!!.visibility = View.VISIBLE
                            eventsAdapter =
                                EventsAdapter(GetEvetnsData, this, object : EventClickListener {
                                    override fun oneventClick(
                                        holder: EventsAdapter.MyViewHolder,
                                        item: GetEventDetailsData
                                    ) {

                                        holder.lnrNoticeboardd!!.setOnClickListener(View.OnClickListener {

                                            var Type: String = "event"
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

                                        })
                                    }
                                })

                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            recyclerNoticeboard!!.layoutManager = mLayoutManager
                            recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                            recyclerNoticeboard!!.adapter = eventsAdapter
                            recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            eventsAdapter!!.notifyDataSetChanged()
                        } else {
                            NoDataFound()
                        }
                    }

                } else {
                    if (CommonUtil.menu_readEvent.equals("1")) {
                        OverAllMenuCountRequest(this, "8")
                    }
                    UserMenuRequest(this)
                    if (EventType) {
                        NoDataFound()
                        GetEvetnsData = response.data!!
                        val size = GetEvetnsData.size
                        upcomingcount = size.toString()


                        val intdepartment = Integer.parseInt(upcomingcount!!)

                        CountUpcoming = intdepartment.toString()

                        departmentcount = Integer.parseInt(CountUpcoming!!)

                        if (intdepartment > 0) {
                            lblDepartmentSize!!.text = CountUpcoming
                            lblDepartmentSize!!.visibility = View.VISIBLE
                        } else {
                            lblDepartmentSize!!.text = CountUpcoming
                            lblDepartmentSize!!.visibility = View.VISIBLE
                        }

                    } else {

                        NoDataFound()
                        GetEvetnsData = response.data!!
                        val size = GetEvetnsData.size

                        pastcount = size.toString()
                        val intcollage = Integer.parseInt(pastcount!!)
                        Countpast = intcollage.toString()

                        Collegecount = Integer.parseInt(Countpast!!)

                        if (intcollage > 0) {
                            lblCollegeSize!!.text = Countpast
                            lblCollegeSize!!.visibility = View.VISIBLE
                        } else {
                            lblCollegeSize!!.text = Countpast
                            lblCollegeSize!!.visibility = View.VISIBLE
                        }
                    }
                }
            } else {
                UserMenuRequest(this)
                NoDataFound()
            }
        }

        imgRefresh!!.setOnClickListener(View.OnClickListener {
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
        })
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


    @OnClick(R.id.LayoutAdvertisement)
    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }


    private fun NoDataFound() {
        lblNoRecordsFound!!.visibility = View.VISIBLE
        recyclerNoticeboard!!.visibility = View.GONE
    }

    private fun CountValueSet() {

        if (!CountUpcoming.equals("0") && !CountUpcoming.equals("")) {
            lblDepartmentSize!!.visibility = View.VISIBLE
            lblDepartmentSize!!.text = CountUpcoming
        } else {
            lblDepartmentSize!!.visibility = View.GONE
            CountUpcoming = "0"
        }
        if (!Countpast.equals("0") && !Countpast.equals("")) {
            lblCollegeSize!!.visibility = View.VISIBLE
            lblCollegeSize!!.text = Countpast
        } else {
            lblCollegeSize!!.visibility = View.GONE
            Countpast = "0"
        }

        var intdepartment = Integer.parseInt(CountUpcoming!!)
        var intCollegecount = Integer.parseInt(Countpast!!)
        var TotalSizeCount = intdepartment + intCollegecount
        if (TotalSizeCount > 0) {
            lbltotalsize!!.visibility = View.VISIBLE
            lbltotalsize!!.text = TotalSizeCount.toString()
        } else {
            lbltotalsize!!.visibility = View.GONE
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_noticeboard

    private fun EventRequest(type: Boolean) {
        val jsonObject = JsonObject()
        run {

            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3") {
                jsonObject.addProperty(ApiRequestNames.Req_sectionid, 0)
                jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId)
            } else {
                jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.SectionId)
                jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId)
            }
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
            if (type) {
                jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.UpcomingEvents)
            } else {
                jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.PastEvents)
            }
            appViewModel!!.getEventListbyType(jsonObject, this)
            Log.d("EventListRequest:", jsonObject.toString())
        }
    }

    @OnClick(R.id.LayoutDepartment)
    fun departmentClick() {

        CommonUtil.EventStatus = "Upcoming"
        bottomsheetStateCollpased()
        TabDepartmentColor()
        EventType = true
        if (CommonUtil.menu_readEvent.equals("1")) {
            EventRequest(EventType)
        }
        CommonUtil.EventEdit = "Edit"
        bottomsheetStateCollpased()

    }

    @OnClick(R.id.LayoutCollege)
    fun collegeClick() {

        CommonUtil.EventStatus = "Past"
        bottomsheetStateCollpased()
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
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        super.onResume()
    }


    @OnClick(R.id.imgAddPlus)
    fun plusClick() {

        val i: Intent = Intent(this, AddEvents::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }
}