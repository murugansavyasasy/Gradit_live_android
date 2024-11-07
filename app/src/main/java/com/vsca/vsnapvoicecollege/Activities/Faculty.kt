package com.vsca.vsnapvoicecollege.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
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
import com.vsca.vsnapvoicecollege.Adapters.FacultyAdapter
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.SenderModel.GetDepartmentData
import com.vsca.vsnapvoicecollege.SenderModel.GetDivisionData
import com.vsca.vsnapvoicecollege.SenderModel.RecipientSelected
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App

class Faculty : BaseActivity() {

    var facultyAdapter: FacultyAdapter? = null
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
    @BindView(R.id.lblNoRecordsFound)
    var lblNoRecordsFound: TextView? = null

    @JvmField
    @BindView(R.id.SpinnerSemester)
    var SpinnerSemester: Spinner? = null


    @JvmField
    @BindView(R.id.SpinnerSections1)
    var SpinnerSections1: Spinner? = null

    @JvmField
    @BindView(R.id.recyclerCommon1)
    var recyclerCommon1: RecyclerView? = null


    @JvmField
    @BindView(R.id.idRVCategories)
    var ryclerCourse: RecyclerView? = null

    @JvmField
    @BindView(R.id.imgheaderBack)
    var imgheaderBack: ImageView? = null

    @JvmField
    @BindView(R.id.lblCategoryName)
    var lblCategoryName: TextView? = null

    @JvmField
    @BindView(R.id.lblMenuHeaderName)
    var lblMenuHeaderName: TextView? = null

    @JvmField
    @BindView(R.id.RadioGroup)
    var RadioGroup: RadioGroup? = null

    @JvmField
    @BindView(R.id.LayoutCountry)
    var layoutDropdown: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.lnrRadioGroup)
    var lnrRadioGroup: LinearLayout? = null

    @JvmField
    @BindView(R.id.viewLine)
    var viewLine: View? = null

    @JvmField
    @BindView(R.id.imgDropdown)
    var imgDropdown: ImageView? = null

    @JvmField
    @BindView(R.id.LayoutTable)
    var LayoutTable: TableLayout? = null


    var SelectedSpinnerID: String? = null
    var SelectedSpinnerIDdepart: String? = null
    var GetSectionData: ArrayList<SectionListDetails> = ArrayList()
    var SemesterId = ""
    var SectionId = ""
    var section: Array<String?>? = null
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var AdWebURl: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = java.util.ArrayList()
    var PreviousAddId: Int = 0
    var GetDivisionData: ArrayList<GetDivisionData>? = null
    var SpinnerData = ArrayList<String>()
    var SelectedRecipientlist: ArrayList<RecipientSelected> = ArrayList()
    var GetSemesterSectionData: ArrayList<SemesterSectionListDetails> = java.util.ArrayList()

    var GetFacultyLiveData: List<GetFacultyListDetails> = ArrayList()
    var GetDepartmentData: ArrayList<GetDepartmentData>? = null
    var SpinningCoursedata = ArrayList<String>()
    var semesternamae = ArrayList<String>()
    var semesternamId = ArrayList<String>()
    var SectionnameList = ArrayList<String>()
    var SemesterAndSection = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)

        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionBarMethod(this)
        MenuBottomType()


        CommonUtil.OnMenuClicks("Faculty")
        imgRefresh!!.visibility = View.GONE

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

        if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                "p6"
            )
        ) {

            appViewModel!!.SemesterSectionLiveData!!.observe(this) { response ->
                if (response != null) {

                    val status = response.status
                    val message = response.message
                    GetSemesterSectionData.clear()

                    UserMenuRequest(this)

                    if (status == 1) {

                        SemesterAndSection.clear()
                        semesternamae.clear()


                        GetSemesterSectionData = response.data!!

                        if (GetSemesterSectionData.size > 0) {
                            lblNoRecordsFound!!.visibility = View.GONE
                            recyclerNoticeboard!!.visibility = View.VISIBLE


                            for (j in GetSemesterSectionData.indices) {
                                semesternamae.add(GetSemesterSectionData.get(j).semestername!!)
                                semesternamId.add(GetSemesterSectionData.get(j).clgsemesterid!!)
                            }

                            SemesterAndSection.add(0, "Select Semester")

                            for (i in semesternamae.indices) {
                                SemesterAndSection.add(semesternamae[i])
                            }

                            val adapter =
                                ArrayAdapter(this, R.layout.spinner_textview, SemesterAndSection)
                            adapter.setDropDownViewResource(R.layout.dopdown_spinner)
                            SpinnerSemester!!.adapter = adapter


                            SpinnerSemester!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>,
                                        view: View,
                                        position: Int,
                                        id: Long
                                    ) {

                                        if (position != 0) {

                                            SpinnerSections1!!.visibility = View.VISIBLE
                                            recyclerNoticeboard!!.visibility = View.VISIBLE

                                            SemesterId =
                                                GetSemesterSectionData.get(position - 1).clgsemesterid!!
                                            GetSectionData =
                                                GetSemesterSectionData[position - 1].data!!

                                            SectionnameList.clear()
                                            SectionnameList.add("Select Section")
                                            for (i in GetSectionData.indices) {
                                                SectionnameList.add(GetSectionData.get(i).sectionname!!)
                                            }


                                            loadSectionSpinner()


                                        } else {
                                            SpinnerSections1!!.visibility = View.GONE
                                            recyclerNoticeboard!!.visibility = View.GONE
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {

                                    }
                                }

                        } else {
                            lblNoRecordsFound!!.visibility = View.VISIBLE
                            recyclerNoticeboard!!.visibility = View.GONE
                        }
                    } else {
                        UserMenuRequest(this)
                        lblNoRecordsFound!!.visibility = View.VISIBLE
                        recyclerNoticeboard!!.visibility = View.GONE
                    }

                } else {
                    lblNoRecordsFound!!.visibility = View.VISIBLE
                    recyclerNoticeboard!!.visibility = View.GONE
                }
            }
            appViewModel!!.facultyListRecevier!!.observe(this) { response ->

                if (response != null) {
                    val status = response.status
                    val message = response.message
                    UserMenuRequest(this)
                    if (status == 1) {

                        lblNoRecordsFound!!.visibility = View.GONE
                        recyclerNoticeboard!!.visibility = View.VISIBLE
                        GetFacultyLiveData = response.data!!


                        facultyAdapter = FacultyAdapter(GetFacultyLiveData, this)
                        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                        recyclerNoticeboard!!.layoutManager = mLayoutManager
                        recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                        recyclerNoticeboard!!.adapter = facultyAdapter
                        recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        facultyAdapter!!.notifyDataSetChanged()
                    } else {
                        lblNoRecordsFound!!.visibility = View.VISIBLE
                        recyclerNoticeboard!!.visibility = View.GONE
                    }
                } else {
                    UserMenuRequest(this)
                    lblNoRecordsFound!!.visibility = View.VISIBLE
                    recyclerNoticeboard!!.visibility = View.GONE
                }
            }

        } else if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1")) {

            GetDivisionRequest()
            appViewModel!!.GetDivisionMutableLiveData!!.observe(this) { response ->
                if (response != null) {
                    val status = response.status
                    val message = response.message

                    if (status == 1) {
                        GetDivisionData = response.data!!
                        if (GetDivisionData!!.size > 0) {
                            SelectedRecipientlist.clear()

                            GetDivisionData!!.forEach {
                                it.division_id
                                it.division_name
                                val divisions = RecipientSelected(it.division_id, it.division_name)
                                SelectedRecipientlist.add(divisions)
                            }
                            LoadDivisionSpinner()

                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
                }
            }

            appViewModel!!.GetDepartmentMutableLiveData!!.observe(this) { response ->
                if (response != null) {
                    val status = response.status
                    val message = response.message
                    if (status == 1) {
                        GetDepartmentData = response.data!!
                        if (GetDepartmentData!!.size > 0) {
                            SelectedRecipientlist.clear()
                            GetDepartmentData!!.forEach {
                                it.department_id
                                it.department_name
                                val divisions =
                                    RecipientSelected(it.department_id, it.department_name)
                                SelectedRecipientlist.add(divisions)
                            }

                            LoadDepartmentSpinner()

                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
                }
            }

            appViewModel!!.FacultyLiveData!!.observe(this) { response ->

                if (response != null) {
                    val status = response.status
                    val message = response.message
                    UserMenuRequest(this)
                    if (status == 1) {

                        lblNoRecordsFound!!.visibility = View.GONE
                        recyclerCommon1!!.visibility = View.VISIBLE
                        GetFacultyLiveData = response.data!!
                        facultyAdapter = FacultyAdapter(GetFacultyLiveData, this)
                        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                        recyclerCommon1!!.layoutManager = mLayoutManager
                        recyclerCommon1!!.itemAnimator = DefaultItemAnimator()
                        recyclerCommon1!!.adapter = facultyAdapter
                        recyclerCommon1!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        facultyAdapter!!.notifyDataSetChanged()
                    } else {
                        lblNoRecordsFound!!.visibility = View.VISIBLE
                        recyclerCommon1!!.visibility = View.GONE
                    }
                } else {
                    UserMenuRequest(this)
                    lblNoRecordsFound!!.visibility = View.VISIBLE
                    recyclerNoticeboard!!.visibility = View.GONE
                }
            }

        } else if (CommonUtil.Priority.equals("p2") || CommonUtil.Priority.equals("p3")) {
            if (CommonUtil.Priority.equals("p2")) {
            } else if (CommonUtil.Priority.equals("p3")) {
                FacultyRequeststaff()
            }
            appViewModel!!.FacultyLiveData!!.observe(this) { response ->

                if (response != null) {
                    val status = response.status
                    val message = response.message
                    UserMenuRequest(this)
                    if (status == 1) {

                        lblNoRecordsFound!!.visibility = View.GONE
                        recyclerNoticeboard!!.visibility = View.VISIBLE
                        GetFacultyLiveData = response.data!!
                        facultyAdapter = FacultyAdapter(GetFacultyLiveData, this)
                        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                        recyclerNoticeboard!!.layoutManager = mLayoutManager
                        recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                        recyclerNoticeboard!!.adapter = facultyAdapter
                        recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        facultyAdapter!!.notifyDataSetChanged()
                    } else {
                        lblNoRecordsFound!!.visibility = View.VISIBLE
                        recyclerNoticeboard!!.visibility = View.GONE
                    }
                } else {
                    UserMenuRequest(this)
                    lblNoRecordsFound!!.visibility = View.VISIBLE
                    recyclerNoticeboard!!.visibility = View.GONE
                }
            }
        }
    }

    private fun loadSectionSpinner() {

        // section list

        val adaptersection = ArrayAdapter(this, R.layout.spinner_textview, SectionnameList)
        adaptersection.setDropDownViewResource(R.layout.dopdown_spinner)

        SpinnerSections1!!.adapter = adaptersection
        SpinnerSections1!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    recyclerNoticeboard!!.visibility = View.VISIBLE
                    SectionId = GetSectionData.get(position - 1).sectionid!!
                    StudentFacultyRequest()
                } else {
                    recyclerNoticeboard!!.visibility = View.GONE

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_faculty_main

    private fun LoadDepartmentSpinner() {

        SpinningCoursedata.clear()
        SpinningCoursedata.add("Select Department")
        GetDepartmentData!!.forEach {
            SpinningCoursedata.add(it.department_name!!)
        }
        val adapter = ArrayAdapter(this, R.layout.spinner_rextview_course, SpinningCoursedata)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_course_layout)
        SpinnerSections1!!.adapter = adapter
        SpinnerSections1!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    if (position != 0) {
                        recyclerCommon1!!.visibility = View.VISIBLE

                        SelectedSpinnerIDdepart =
                            GetDepartmentData!!.get(position - 1).department_id
                        GetDepartmentData!!.get(position - 1).department_name?.let {

                        }
                        FacultyRequest()
                    } else {
                        recyclerCommon1!!.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
    }

    private fun LoadDivisionSpinner() {

        SpinnerData.clear()
        SpinnerData.add("Select Division")
        GetDivisionData!!.forEach {
            SpinnerData.add(it.division_name!!)
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_textview, SpinnerData)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
        SpinnerSemester!!.adapter = adapter
        SpinnerSemester!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {

                if (position != 0) {

                    SpinnerSections1!!.visibility = View.VISIBLE

                    SelectedSpinnerID = GetDivisionData!!.get(position - 1).division_id
                    GetDivisionData!!.get(position - 1).division_name?.let {

                    }
                    GetDepartmentRequest()
                } else {

                    SpinnerSections1!!.visibility = View.GONE
                    recyclerCommon1!!.visibility = View.GONE

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }


    private fun GetDivisionRequest() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        appViewModel!!.getDivision(jsonObject, this)
        Log.d("GetDivisionRequest", jsonObject.toString())
    }

    private fun GetDepartmentRequest() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_div_id, SelectedSpinnerID)
        appViewModel!!.getDepartment(jsonObject, this)
        Log.d("GetDepartmentRequest", jsonObject.toString())
    }

    private fun FacultyRequest() {

        val jsonObject = JsonObject()

        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.Appid)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_deptid, SelectedSpinnerIDdepart)
        jsonObject.addProperty(ApiRequestNames.Req_course_id, SelectedSpinnerID)

        appViewModel!!.getFaculty(jsonObject, this)
        Log.d("FacultyList:", jsonObject.toString())

    }

    private fun StudentFacultyRequest() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_AppId, CommonUtil.SenderAppId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_semesterid, SemesterId)
        jsonObject.addProperty(ApiRequestNames.Req_sectionid, SectionId)

        appViewModel!!.GetFacultylistSenderside(jsonObject, this)
        Log.d("FacultyList:", jsonObject.toString())

    }

    private fun FacultyRequeststaffandhod() {
        val jsonObject = JsonObject()

        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.Appid)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_deptid, CommonUtil.DepartmentId)

        appViewModel!!.getFaculty(jsonObject, this)
        Log.d("FacultyRequeststaff", jsonObject.toString())

    }


    private fun FacultyRequeststaff() {
        val jsonObject = JsonObject()

        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.Appid)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_deptid, CommonUtil.DepartmentId)

        appViewModel!!.getFacultystaff(jsonObject, this)
        Log.d("FacultyRequeststaff", jsonObject.toString())

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


    private fun SemesterSectionRequest() {
        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_yearid, CommonUtil.YearId)
            appViewModel!!.getSemesterAndSection(jsonObject, this)
            Log.d("SemesterSection:", jsonObject.toString())
        }
    }

    @OnClick(R.id.LayoutAdvertisement)
    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        if (CommonUtil.menu_readFaculty.equals("1")) {

            if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                    "p6"
                )
            ) {

                SemesterSectionRequest()

            } else if (CommonUtil.Priority.equals("p2")) {
                FacultyRequeststaffandhod()

            } else if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1")) {
                FacultyRequeststaff()
            }
        }
        AdForCollegeApi()

        super.onResume()
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }
}






