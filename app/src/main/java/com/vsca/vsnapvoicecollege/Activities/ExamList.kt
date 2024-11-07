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
import com.vsca.vsnapvoicecollege.ActivitySender.AddExamination
import com.vsca.vsnapvoicecollege.ActivitySender.Eyeview_Examlist
import com.vsca.vsnapvoicecollege.Adapters.ExamListAdapter
import com.vsca.vsnapvoicecollege.Interfaces.ExamMarkViewClickListener
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.GetExamListDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import java.util.Locale

class ExamList : BaseActivity() {

    var examAdapter: ExamListAdapter? = null
    override var appViewModel: App? = null
    var ExamHeaderID: String? = ""

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


    var ExamType = true
    var GetExamListData: ArrayList<GetExamListDetails> = ArrayList()
    var GetExampastListData: ArrayList<GetExamListDetails> = ArrayList()
    var UpcominExamCount: String? = null
    var PastExamCount: String? = null
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var AdWebURl: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var PreviousAddId: Int = 0
    var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionBarMethod(this)
        MenuBottomType()
        CommonUtil.OnMenuClicks("ExamList")
        TabDepartmentColor()

        lblMenuTitle!!.setText(R.string.txt_exam)
        lblDepartment!!.setText(R.string.txt_upcoming)
        lblCollege!!.setText(R.string.txt_past)
        CommonUtil.Screenname = ""
        type = ""
        CommonUtil.EditButtonclick = ""

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
                    if (CommonUtil.menu_readExamination.equals("1")) {
                        ExamListRequest(ExamType)
                    }
                    if (response.data.isNullOrEmpty()) {
                        OverAllMenuCountData = emptyList()
                    } else {
                        OverAllMenuCountData = response.data!!
                        UpcominExamCount = OverAllMenuCountData[0].upcomingexams
                        PastExamCount = OverAllMenuCountData[0].pastexams
                        CountValueSet()
                    }
                } else {
                    OverAllMenuCountData = emptyList()
                }
            }
        }
        appViewModel!!.ExamListResponseLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                UserMenuRequest(this)
                if (status == 1) {
                    if (ExamType) {
                        GetExamListData = response.data!!
                        val size = GetExamListData.size
                        if (size > 0) {
                            lblNoRecordsFound!!.visibility = View.GONE
                            recyclerNoticeboard!!.visibility = View.VISIBLE
                            examAdapter = ExamListAdapter(GetExamListData, this, true,
                                object : ExamMarkViewClickListener {
                                    override fun onExamClickListener(
                                        holder: ExamListAdapter.MyViewHolder,
                                        item: GetExamListDetails
                                    ) {
                                        CommonUtil.MenuExamination = false
                                        holder.img_Edit!!.visibility = View.VISIBLE

                                        holder.lnrEventsView!!.setOnClickListener {
                                            ExamHeaderID = item.headerid
                                            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals(
                                                    "p1"
                                                ) || CommonUtil.Priority.equals(
                                                    "p2"
                                                ) || CommonUtil.Priority.equals("p3") || (CommonUtil.Priority.equals(
                                                    "p4"
                                                ) && lblDepartment!!.text.equals("Upcoming")) ||
                                                (CommonUtil.Priority.equals("p5") && lblDepartment!!.text.equals(
                                                    "Upcoming"
                                                ))
                                            ) {
                                                val i = Intent(
                                                    this@ExamList,
                                                    Eyeview_Examlist::class.java
                                                )
                                                i.putExtra("ExamHeaderID", ExamHeaderID)
                                                i.putExtra("Examname", item.examname)
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(i)
                                            } else {
                                                val i = Intent(this@ExamList, ExamMarks::class.java)
                                                i.putExtra("ExamHeaderID", ExamHeaderID)
                                                i.putExtra("Examname", item.examname)
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(i)
                                            }
                                        }
                                    }
                                })

                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            recyclerNoticeboard!!.layoutManager = mLayoutManager
                            recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                            recyclerNoticeboard!!.adapter = examAdapter
                            recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            examAdapter!!.notifyDataSetChanged()
                        } else {
                            NoDataFound()
                        }
                    } else {
                        GetExamListData.clear()
                        GetExampastListData = response.data!!
                        val size = GetExampastListData.size

                        Log.d("testExamLog", GetExampastListData.size.toString())

                        if (size > 0) {

                            lblNoRecordsFound!!.visibility = View.GONE
                            recyclerNoticeboard!!.visibility = View.VISIBLE

                            examAdapter = ExamListAdapter(GetExampastListData, this, false,
                                object : ExamMarkViewClickListener {
                                    override fun onExamClickListener(
                                        holder: ExamListAdapter.MyViewHolder,
                                        item: GetExamListDetails
                                    ) {
                                        CommonUtil.MenuExamination = false
                                        holder.img_Edit!!.visibility = View.GONE

                                        holder.lnrEventsView!!.setOnClickListener {
                                            ExamHeaderID = item.headerid
                                            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals(
                                                    "p1"
                                                ) || CommonUtil.Priority.equals(
                                                    "p2"
                                                ) || CommonUtil.Priority.equals("p3")
                                            ) {
                                                val i =
                                                    Intent(
                                                        this@ExamList,
                                                        Eyeview_Examlist::class.java
                                                    )
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(i)
                                            } else {
                                                val i = Intent(this@ExamList, ExamMarks::class.java)
                                                i.putExtra("ExamHeaderID", ExamHeaderID)
                                                i.putExtra("Examname", item.examname)
                                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(i)
                                            }
                                        }
                                    }
                                })
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            recyclerNoticeboard!!.layoutManager = mLayoutManager
                            recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                            recyclerNoticeboard!!.adapter = examAdapter
                            recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            examAdapter!!.notifyDataSetChanged()
                        } else {
                            NoDataFound()
                        }
                    }
                } else {
                    if (ExamType) {
                        NoDataFound()
                    } else {
                        NoDataFound()
                    }
                }
            } else {
                UserMenuRequest(this)
                NoDataFound()
            }
        }

        imgRefresh!!.setOnClickListener(View.OnClickListener {
            if (ExamType) {
                ExamType = true
                if (CommonUtil.menu_readExamination.equals("1")) {
                    ExamListRequest(ExamType)
                    OverAllMenuCountRequest(this, CommonUtil.MenuIDExamination!!)
                }
            } else {
                ExamType = false
                if (CommonUtil.menu_readExamination.equals("1")) {
                    ExamListRequest(ExamType)
                    OverAllMenuCountRequest(this, CommonUtil.MenuIDExamination!!)
                }
            }
        })
    }

    private fun NoDataFound() {
        lblNoRecordsFound!!.visibility = View.VISIBLE
        recyclerNoticeboard!!.visibility = View.GONE
    }

    private fun filter(text: String) {

        val filteredlist: java.util.ArrayList<GetExamListDetails> = java.util.ArrayList()

        for (item in GetExampastListData) {
            if (item.examname!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {

                filteredlist.add(item)

            }
        }
        if (filteredlist.isEmpty()) {

            Toast.makeText(this, CommonUtil.No_Data_Found, Toast.LENGTH_SHORT).show()
        } else {
            examAdapter!!.filterList(filteredlist)

        }
    }

    private fun CountValueSet() {

        if (!UpcominExamCount.equals("0") && !UpcominExamCount.equals("")) {
            lblDepartmentSize!!.visibility = View.VISIBLE
            lblDepartmentSize!!.text = UpcominExamCount
        } else {
            lblDepartmentSize!!.visibility = View.GONE
            UpcominExamCount = "0"
        }
        if (!PastExamCount.equals("0") && !PastExamCount.equals("")) {
            lblCollegeSize!!.visibility = View.VISIBLE
            lblCollegeSize!!.text = PastExamCount
        } else {
            lblCollegeSize!!.visibility = View.GONE
            PastExamCount = "0"
        }
        val intdepartment = Integer.parseInt(UpcominExamCount!!)
        val intCollegecount = Integer.parseInt(PastExamCount!!)
        val TotalSizeCount = intdepartment + intCollegecount
        if (TotalSizeCount > 0) {
            lbltotalsize!!.visibility = View.VISIBLE
            lbltotalsize!!.text = TotalSizeCount.toString()
        } else {
            lbltotalsize!!.visibility = View.GONE
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_noticeboard

    private fun ExamListRequest(type: Boolean) {
        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)

            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority == "p1" || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                jsonObject.addProperty(ApiRequestNames.Req_sectionid, 0)
            } else {
                jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.SectionId)
            }

            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3") {
                jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId)
            } else {
                jsonObject.addProperty(ApiRequestNames.Req_appid, "1")
            }
            if (type) {
                jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.UpcomingExams)
            } else {
                jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.PastExams)
            }

            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                appViewModel!!.getExamListbyType(jsonObject, this)

            } else {

                appViewModel!!.getExamListbyTypeReciver(jsonObject, this)

            }
            Log.d("ExamRequest:", jsonObject.toString())
        }
    }

    @OnClick(R.id.LayoutDepartment)
    fun departmentClick() {
        bottomsheetStateCollpased()
        TabDepartmentColor()
        ExamType = true
        if (CommonUtil.menu_readExamination == "1") {
            ExamListRequest(ExamType)
            OverAllMenuCountRequest(this, CommonUtil.MenuIDExamination!!)
        }
        bottomsheetStateCollpased()
        type = ""
    }

    @OnClick(R.id.LayoutCollege)
    fun collegeClick() {
        bottomsheetStateCollpased()
        ExamType = false
        GetExamListData.clear()
        GetExampastListData.clear()
        if (CommonUtil.menu_readExamination.equals("1")) {
            ExamListRequest(ExamType)
            OverAllMenuCountRequest(this, CommonUtil.MenuIDExamination!!)
        }
        TabCollegeColor()
        type = "Past"
        bottomsheetStateCollpased()
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }

    override fun onResume() {
        if (CommonUtil.menu_readExamination.equals("1")) {
            OverAllMenuCountRequest(this, CommonUtil.MenuIDExamination!!)
        }
        AdForCollegeApi()
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        super.onResume()

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

    @OnClick(R.id.imgAddPlus)
    fun imgaddclick() {

        val i: Intent = Intent(this, AddExamination::class.java)
        CommonUtil.Screenname.equals("Forward")
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)

    }
}