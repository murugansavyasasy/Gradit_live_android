package com.vsca.vsnapvoicecollege.ActivitySender

import android.app.DatePickerDialog
import android.content.Intent
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
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Activities.BaseActivity.Companion.LoadWebViewContext
import com.vsca.vsnapvoicecollege.Adapters.ExamAdd_StaffAdapter
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.SenderModel.GetDepartmentData
import com.vsca.vsnapvoicecollege.SenderModel.GetDivisionData
import com.vsca.vsnapvoicecollege.SenderModel.RecipientSelected
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import java.util.Calendar

class AddExamination : ActionBarActivity() {

    var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var StaffAdapter: ExamAdd_StaffAdapter? = null
    var getsubjectlist: List<Get_staff_yourclass> = ArrayList()
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var GetDivisionData: ArrayList<GetDivisionData>? = null
    var GetDepartmentData: ArrayList<GetDepartmentData>? = null
    var Getyouurclassdata: ArrayList<Data>? = null
    var Getcoursedepartment: ArrayList<department_coursedata>? = null
    var GetSemesterSectionData: List<SemesterSectionListDetails> = ArrayList()
    var Spinningdepaerdata = ArrayList<String>()
    var SpinnerdivisionData = ArrayList<String>()
    var Spinnercoursedata = ArrayList<String>()
    var Spinneryaerdata = ArrayList<String>()
    var Spinnersemesterdata = ArrayList<String>()
    var SelectedSpinnerIDdivision: String? = null
    var SelectedSpinnerIDdepart: String? = null
    var SelectedSpinnerIDcousre: String? = null
    var SelectedSpinnerIDyear: String? = null
    var SelectedSpinnerIDsemester: String? = null
    var SelectedSpinnerIDyearhod: String? = null
    var Division: String? = null
    var Department: String? = null
    var Course: String? = null
    var Year: String? = null
    var Semester: String? = null
    var startdate = ""
    var enddate = ""
    var SeletedType = "0"
    var _pickedDate = ""
    var semesterisnotselected: String? = null

    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    @JvmField
    @BindView(R.id.start_date)
    var start_date: TextView? = null

    @JvmField
    @BindView(R.id.txtTitle)
    var txtTitle: EditText? = null

    @JvmField
    @BindView(R.id.end_date)
    var end_date: TextView? = null

    @JvmField
    @BindView(R.id.spnhod_division)
    var spnhod_division: Spinner? = null

    @JvmField
    @BindView(R.id.get_seletion_subject_button)
    var get_seletion_subject_button: Button? = null

    @JvmField
    @BindView(R.id.spn_division)
    var spn_division: Spinner? = null

    @JvmField
    @BindView(R.id.spn_department)
    var spn_department: Spinner? = null

    @JvmField
    @BindView(R.id.spn_course)
    var spn_course: Spinner? = null

    @JvmField
    @BindView(R.id.spn_year)
    var spn_year: Spinner? = null

    @JvmField
    @BindView(R.id.spn_semester)
    var spn_semester: Spinner? = null

    @JvmField
    @BindView(R.id.spnhodyear)
    var spnhodyear: Spinner? = null

    @JvmField
    @BindView(R.id.spnhodsemester)
    var spnhodsemester: Spinner? = null

    @JvmField
    @BindView(R.id.spinning_department)
    var spinning_department: TextView? = null


    @JvmField
    @BindView(R.id.con_principlelayout)
    var con_principlelayout: ConstraintLayout? = null


    @JvmField
    @BindView(R.id.constrin_selectdepaerment)
    var constrin_selectdepaerment: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.rcy_hodandstaff)
    var rcy_hodandstaff: RecyclerView? = null

    @JvmField
    @BindView(R.id.rcy_hod)
    var rcy_hod: RecyclerView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this)
        imgRefresh!!.visibility = View.GONE

        spnhodyear!!.visibility = View.GONE
        spnhodsemester!!.visibility = View.GONE
        spnhod_division!!.visibility = View.GONE
        CommonUtil.semesterid = ""
        CommonUtil.EditButtonclick = ""


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

        get_seletion_subject_button?.setOnClickListener {

            startdate = start_date!!.text.toString()
            CommonUtil.minimumdate = start_date!!.text.toString()
            enddate = end_date!!.text.toString()
            CommonUtil.maxmumdate = end_date!!.text.toString()


            CommonUtil.Examname = txtTitle!!.text.toString()
            CommonUtil.startdate = startdate
            CommonUtil.enddate = enddate

            if (CommonUtil.Examname.isEmpty()) {
                Toast.makeText(this, CommonUtil.Fill_Exam_name, Toast.LENGTH_SHORT).show()

            } else if (CommonUtil.startdate.isEmpty()) {
                Toast.makeText(this, CommonUtil._Startdate, Toast.LENGTH_SHORT).show()

            } else if (CommonUtil.enddate.isEmpty()) {
                Toast.makeText(this, CommonUtil._Enddate, Toast.LENGTH_SHORT).show()
            }else if (CommonUtil.Priority == "p7" || CommonUtil.Priority.equals("p1")) {

                if (Division.equals("1")) {

                    if (Department.equals("1")) {

                        if (Course.equals("1")) {

                            if (Year.equals("1")) {

                                if (Semester.equals("1")) {

                                    val i: Intent = Intent(this, create_Examination::class.java)
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    CommonUtil.Examination_Creation.add(
                                        Examination_Creation(
                                            CommonUtil.CollegeId,
                                            CommonUtil.enddate,
                                            "0", CommonUtil.Examname,
                                            "add",
                                            CommonUtil.MemberId,
                                            CommonUtil.startdate,
                                            CommonUtil.deptidExam,
                                            CommonUtil.Sectiondetail_ExamCreation
                                        )
                                    )
                                    startActivity(i)

                                } else {
                                    Toast.makeText(
                                        this,
                                        CommonUtil.Select_Semester,
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            } else {
                                Toast.makeText(this, CommonUtil.Select_year, Toast.LENGTH_SHORT)
                                    .show()
                            }

                        } else {
                            Toast.makeText(this, CommonUtil.Select_Course, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this, CommonUtil.Select_Department, Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    Toast.makeText(this, CommonUtil.Select_Division, Toast.LENGTH_SHORT).show()
                }

            }else if (CommonUtil.Priority.equals("p2")) {

                if (spinning_department!!.text.equals(CommonUtil.Classes_I_handle)) {

                    if (CommonUtil.semesterid.equals("") || CommonUtil.Examname.equals("")) {
                        Toast.makeText(this, CommonUtil.Select_Section, Toast.LENGTH_SHORT).show()

                    } else {
                        val i: Intent = Intent(this, create_Examination::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)
                    }

                } else {

                    if (SeletedType.equals(
                            "0"
                        )
                    ) {
                        Toast.makeText(this, CommonUtil.Select_Type, Toast.LENGTH_SHORT).show()
                    } else if (spnhodyear!!.visibility == View.GONE) {
                        Toast.makeText(this, CommonUtil.Select_Course, Toast.LENGTH_SHORT).show()

                    } else if (spnhodsemester!!.visibility == View.GONE) {
                        Toast.makeText(this, CommonUtil.Select_year, Toast.LENGTH_SHORT).show()

                    } else if (semesterisnotselected.equals("semesterisnotselected")) {
                        Toast.makeText(this, CommonUtil.Select_Semester, Toast.LENGTH_SHORT).show()
                    } else {

                        val i: Intent = Intent(this, create_Examination::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        CommonUtil.Examination_Creation.add(
                            Examination_Creation(
                                CommonUtil.CollegeId,
                                CommonUtil.enddate,
                                "0", CommonUtil.Examname,
                                "add",
                                CommonUtil.MemberId,
                                CommonUtil.startdate,
                                CommonUtil.deptidExam,
                                CommonUtil.Sectiondetail_ExamCreation
                            )
                        )
                        startActivity(i)

                    }

                }
            } else if (CommonUtil.Priority == "p3") {

                if (CommonUtil.Examname.isEmpty() && CommonUtil.startdate.isEmpty() && CommonUtil.enddate.isEmpty()) {
                    Toast.makeText(this, CommonUtil.Fill_Exam_Details, Toast.LENGTH_SHORT).show()

                } else {

                    if (CommonUtil.semesterid.equals("")) {
                        Toast.makeText(this, CommonUtil.Select_Section, Toast.LENGTH_SHORT).show()
                    } else {
                        val i: Intent = Intent(this, create_Examination::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)
                        CommonUtil.Examination_Creation.add(
                            Examination_Creation(
                                CommonUtil.CollegeId,
                                CommonUtil.enddate,
                                "0", CommonUtil.Examname,
                                "add",
                                CommonUtil.MemberId,
                                CommonUtil.startdate,
                                CommonUtil.deptidExam,
                                CommonUtil.Sectiondetail_ExamCreation
                            )
                        )
                    }
                }
            }
        }

        if (CommonUtil.Priority == "p7" || CommonUtil.Priority.equals("p1")) {
            GetDivisionRequest()
            con_principlelayout!!.visibility = View.VISIBLE
            rcy_hodandstaff!!.visibility = View.GONE
            constrin_selectdepaerment!!.visibility = View.GONE
            rcy_hod!!.visibility = View.GONE
        } else if (CommonUtil.Priority.equals("p3")) {

            rcy_hodandstaff!!.visibility = View.VISIBLE
            constrin_selectdepaerment!!.visibility = View.GONE
            con_principlelayout!!.visibility = View.GONE
            GetSubject()
            rcy_hod!!.visibility = View.GONE
        } else if (CommonUtil.Priority.equals("p2")) {

            constrin_selectdepaerment!!.visibility = View.VISIBLE
            con_principlelayout!!.visibility = View.GONE
            rcy_hodandstaff!!.visibility = View.GONE

        }


        appViewModel!!.Getsubjectdata!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    getsubjectlist = response.data!!
                    StaffAdapter = ExamAdd_StaffAdapter(getsubjectlist, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    rcy_hodandstaff!!.layoutManager = mLayoutManager
                    rcy_hodandstaff!!.itemAnimator = DefaultItemAnimator()
                    rcy_hodandstaff!!.adapter = StaffAdapter
                    rcy_hodandstaff!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    StaffAdapter!!.notifyDataSetChanged()
                }
            } else {
                CommonUtil.ApiAlert(
                    this,
                    CommonUtil.Subject_Section_Not_allocated
                )
            }
        }

        appViewModel!!.GetsubjectdatahOD!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    rcy_hod!!.visibility = View.VISIBLE
                    getsubjectlist = response.data!!
                    StaffAdapter = ExamAdd_StaffAdapter(getsubjectlist, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    rcy_hod!!.layoutManager = mLayoutManager
                    rcy_hod!!.itemAnimator = DefaultItemAnimator()
                    rcy_hod!!.adapter = StaffAdapter
                    rcy_hod!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    StaffAdapter!!.notifyDataSetChanged()
                }
            } else {
                CommonUtil.ApiAlert(
                    this,
                    CommonUtil.Subject_Section_Not_allocated
                )
            }
        }


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
                            val department =
                                RecipientSelected(it.department_id, it.department_name)
                            SelectedRecipientlist.add(department)
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

        appViewModel!!.GetCoursesByDepartmenthod!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message

                if (status == 1) {
                    Getcoursedepartment = response.data!!
                    if (Getcoursedepartment!!.size > 0) {
                        SelectedRecipientlist.clear()

                        Getcoursedepartment!!.forEach {
                            it.course_id
                            it.course_name

                            val group = RecipientSelected(it.course_id, it.course_name)
                            SelectedRecipientlist.add(group)
                        }
                        if (CommonUtil.Priority == "p7" || CommonUtil.Priority.equals("p1")) {
                            LoadcourseSpinner()

                        } else if (CommonUtil.Priority.equals("p2")) {
                            LoadcoursehodSpinner()
                        } else {

                        }
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        appViewModel!!.GetCoursesByDepartmenthodhod!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    Getyouurclassdata = (response.data as ArrayList<Data>?)!!
                    if (Getyouurclassdata!!.size > 0) {
                        SelectedRecipientlist.clear()
                        Getyouurclassdata!!.forEach {
                            it.yearid
                            it.yearname
                            val course = RecipientSelected(it.yearid.toString(), it.yearname)
                            SelectedRecipientlist.add(course)
                        }
                        if (CommonUtil.Priority == "p7" || CommonUtil.Priority.equals("p1")) {
                            LoadyearSpinner()
                        } else if (CommonUtil.Priority.equals("p2")) {
                            LoadhodyearSpinner()
                        }
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                }
            }
        }

        appViewModel!!.SemesterSectionLiveData!!.observe(this)
        { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                BaseActivity.UserMenuRequest(this)
                if (status == 1) {
                    GetSemesterSectionData = response.data!!
                    if (GetSemesterSectionData.size > 0) {
                        SelectedRecipientlist.clear()
                        GetSemesterSectionData.forEach {
                            it.clgsemesterid
                            it.semestername
                            val semester = RecipientSelected(it.clgsemesterid, it.semestername)
                            SelectedRecipientlist.add(semester)
                        }
                        if (CommonUtil.Priority == "p7" || CommonUtil.Priority.equals("p1")) {
                            LoadsemesterSpinner()
                        } else if (CommonUtil.Priority.equals("p2")) {
                            LoadsemesterhodSpinner()
                        }
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        spinning_department?.setOnClickListener {
            val popupMenu = PopupMenu(this@AddExamination, spinning_department)
            popupMenu.menuInflater.inflate(R.menu.hoddepartmentorclasses, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->

                spinning_department!!.text = menuItem.title

                if (spinning_department!!.text.equals(CommonUtil.My_Department)) {

                    SeletedType = "1"
                    spnhod_division!!.visibility = View.VISIBLE
                    spnhodyear!!.visibility = View.VISIBLE
                    spnhodsemester!!.visibility = View.VISIBLE
                    rcy_hod!!.visibility = View.GONE
                    Getcoursehoddepartment()

                } else if (spinning_department!!.text.equals(CommonUtil.Classes_I_handle)) {

                    SeletedType = "1"
                    SelectedSpinnerIDcousre = ""
                    CommonUtil.YearId = ""
                    CommonUtil.semesterid = ""
                    spnhod_division!!.visibility = View.GONE
                    spnhodyear!!.visibility = View.GONE
                    spnhodsemester!!.visibility = View.GONE
                    GetSubject()
                }
                true
            }
            popupMenu.show()
        }
    }


    //PRINCIPLE SIDE SPINNER

    private fun LoadDivisionSpinner() {

        SpinnerdivisionData.clear()
        SpinnerdivisionData.add("Select Division")
        GetDivisionData!!.forEach {
            SpinnerdivisionData.add(it.division_name!!)
        }


        val adapter = ArrayAdapter(this, R.layout.spinner_textview, SpinnerdivisionData)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
        spn_division!!.adapter = adapter
        spn_division!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                if (position != 0) {

                    Division = "1"
                    spn_department!!.visibility = View.VISIBLE
                    SelectedSpinnerIDdivision = GetDivisionData!!.get(position - 1).division_id
                    Log.d("spinnerselected", SelectedSpinnerIDdivision!!)
                    GetDivisionData!!.get(position - 1).division_name?.let {
                        Log.d("spinning data", it)
                    }
                    GetDepartmentRequest()
                } else {
                    Division = ""

                    spn_department!!.visibility = View.GONE
                    spn_course!!.visibility = View.GONE
                    spn_year!!.visibility = View.GONE
                    spn_semester!!.visibility = View.GONE

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun LoadDepartmentSpinner() {
        Spinningdepaerdata.clear()
        Spinningdepaerdata.add("Select Department")
        GetDepartmentData!!.forEach {
            Spinningdepaerdata.add(it.department_name!!)
        }
        val adapter = ArrayAdapter(this, R.layout.spinner_rextview_course, Spinningdepaerdata)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_course_layout)
        spn_department!!.adapter = adapter
        spn_department!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    if (position != 0) {
                        Department = "1"
                        spn_course!!.visibility = View.VISIBLE

                        SelectedSpinnerIDdepart =
                            GetDepartmentData!!.get(position - 1).department_id
                        GetDepartmentData!!.get(position - 1).department_name?.let {
                            CommonUtil.deptidExam = SelectedSpinnerIDdepart.toString()
                            Log.d("departmentid", CommonUtil.deptidExam)
                        }
                        Getcoursedepartment()
                    } else {
                        Department = ""

                        spn_course!!.visibility = View.GONE
                        spn_year!!.visibility = View.GONE
                        spn_semester!!.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
    }


    private fun LoadcourseSpinner() {
        Spinnercoursedata.clear()
        Spinnercoursedata.add("Select Course")
        Getcoursedepartment!!.forEach {
            Spinnercoursedata.add(it.course_name!!)
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_textview, Spinnercoursedata)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
        spn_course!!.adapter = adapter
        spn_course!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                if (position != 0) {
                    Course = "1"
                    spn_year!!.visibility = View.VISIBLE

                    SelectedSpinnerIDcousre = Getcoursedepartment!!.get(position - 1).course_id
                    Getcoursedepartment!!.get(position - 1).course_name?.let {
                    }
                    GetyearandsectionRequest()
                } else {
                    spn_year!!.visibility = View.GONE
                    spn_semester!!.visibility = View.GONE
                    Course = ""


                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun LoadyearSpinner() {

        Spinneryaerdata.clear()
        Spinneryaerdata.add("Select Year")
        Getyouurclassdata!!.forEach {
            Spinneryaerdata.add(it.yearname!!)
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_textview, Spinneryaerdata)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
        spn_year!!.adapter = adapter
        spn_year!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {

                if (position != 0) {
                    Year = "1"
                    spn_semester!!.visibility = View.VISIBLE

                    SelectedSpinnerIDyear = Getyouurclassdata!!.get(position - 1).yearid.toString()
                    Log.d("spinnerselected", SelectedSpinnerIDyear!!)
                    Getyouurclassdata!!.get(position - 1).yearname?.let {
                        CommonUtil.YearId = SelectedSpinnerIDyear.toString()
                        Log.d("spinning data", it)
                    }
                    SemesterRequest()
                } else {
                    spn_semester!!.visibility = View.GONE
                    Year = ""

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun LoadsemesterSpinner() {

        Spinnersemesterdata.clear()
        Spinnersemesterdata.add("Select Semester")
        GetSemesterSectionData.forEach {
            Spinnersemesterdata.add(it.semestername!!)
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_textview, Spinnersemesterdata)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
        spn_semester!!.adapter = adapter
        spn_semester!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {

                if (position != 0) {
                    Semester = "1"
                    SelectedSpinnerIDsemester =
                        GetSemesterSectionData.get(position - 1).clgsemesterid.toString()
                    CommonUtil.semesterid = SelectedSpinnerIDsemester.toString()
                    GetSemesterSectionData.get(position - 1).semestername?.let {
                    }
                } else {
                    Semester = ""

                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }


    // HOD CREATE EXAMINATION

    private fun LoadcoursehodSpinner() {
        Spinnercoursedata.clear()

        Spinnercoursedata.add("Select Course")
        Getcoursedepartment!!.forEach {
            Spinnercoursedata.add(it.course_name!!)
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_textview, Spinnercoursedata)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
        spnhod_division!!.adapter = adapter
        spnhod_division!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                if (position != 0) {

                    spnhodyear!!.visibility = View.VISIBLE
                    spnhodsemester!!.visibility = View.VISIBLE

                    SelectedSpinnerIDcousre = Getcoursedepartment!!.get(position - 1).course_id
                    Getcoursedepartment!!.get(position - 1).course_name?.let {
                        Log.d("spinning data", it)
                    }
                    GetyearandhodRequest()
                } else {

                    spnhodyear!!.visibility = View.GONE
                    spnhodsemester!!.visibility = View.GONE

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun LoadhodyearSpinner() {

        Spinneryaerdata.clear()

        Spinneryaerdata.add("Select Year")
        Getyouurclassdata!!.forEach {
            Spinneryaerdata.add(it.yearname!!)
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_textview, Spinneryaerdata)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
        spnhodyear!!.adapter = adapter
        spnhodyear!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                if (position != 0) {

                    spnhodsemester!!.visibility = View.VISIBLE

                    SelectedSpinnerIDyearhod =
                        Getyouurclassdata!!.get(position - 1).yearid.toString()
                    Getyouurclassdata!!.get(position - 1).yearname?.let {
                        CommonUtil.YearId = SelectedSpinnerIDyearhod.toString()

                    }
                    SemesterhodRequest()
                } else {
                    spnhodsemester!!.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun LoadsemesterhodSpinner() {

        Spinnersemesterdata.clear()
        Spinnersemesterdata.add("Select Semester")
        GetSemesterSectionData.forEach {
            Spinnersemesterdata.add(it.semestername!!)
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_textview, Spinnersemesterdata)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
        spnhodsemester!!.adapter = adapter
        spnhodsemester!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {

                if (position != 0) {

                    SelectedSpinnerIDsemester =
                        GetSemesterSectionData.get(position - 1).clgsemesterid.toString()
                    CommonUtil.semesterid = SelectedSpinnerIDsemester.toString()
                    GetSemesterSectionData.get(position - 1).semestername?.let {
                        semesterisnotselected = ""
                    }
                } else {
                    semesterisnotselected = "semesterisnotselected"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun SemesterRequest() {
        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_yearid, SelectedSpinnerIDyear)
            appViewModel!!.getSemesterAndSection(jsonObject, this)
            Log.d("SemesterSection:", jsonObject.toString())
        }
    }

    private fun SemesterhodRequest() {
        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_yearid, SelectedSpinnerIDyearhod)
            appViewModel!!.getSemesterAndSection(jsonObject, this)
            Log.d("SemesterSection:", jsonObject.toString())
        }
    }


    private fun GetyearandsectionRequest() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_clgprocessby, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_idcollege, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_idcourse, SelectedSpinnerIDcousre)
        appViewModel!!.getyearsndsection(jsonObject, this)
        Log.d("Gety&sectionRequeat", jsonObject.toString())
    }

    private fun GetyearandhodRequest() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_clgprocessby, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_idcollege, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_idcourse, SelectedSpinnerIDcousre)
        appViewModel!!.getyearsndsection(jsonObject, this)
        Log.d("Gety&sectionRequeat", jsonObject.toString())
    }

    private fun Getcoursedepartment() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_dept_id, SelectedSpinnerIDdepart)
        appViewModel!!.getcoursedepartment(jsonObject, this)
        Log.d("getdepartcousereq", jsonObject.toString())
    }

    private fun Getcoursehoddepartment() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_dept_id, CommonUtil.DepartmentId)
        appViewModel!!.getcoursedepartment(jsonObject, this)
        Log.d("getdepartcousere", jsonObject.toString())
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
        jsonObject.addProperty(ApiRequestNames.Req_div_id, SelectedSpinnerIDdivision)
        appViewModel!!.getDepartment(jsonObject, this)
        Log.d("GetDepartmentRequest", jsonObject.toString())
    }

    private fun GetSubject() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        appViewModel!!.getsubject(jsonObject, this)
        Log.d("GetstaffRequest", jsonObject.toString())
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

    override val layoutResourceId: Int
        get() = R.layout.activity_add_examination

    @OnClick(R.id.imgback)
    fun imgBackClick() {
        onBackPressed()
    }

    @OnClick(R.id.LayoutAdvertisement)
    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        AdForCollegeApi()
        CommonUtil.Subjectdetail_ExamCreation.clear()
        CommonUtil.Sectiondetail_ExamCreation.clear()
        super.onResume()
    }

    @OnClick(R.id.start_date)
    fun eventdateClick() {

        val c = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this, { view, year, month, dayOfMonth ->
                val _year = year.toString()
                val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                _pickedDate = "$_date/$_month/$year"

                CommonUtil.DateExam = _date.toInt()
                CommonUtil.Month = _month.toInt()
                CommonUtil.YearDate = _year.toInt()

                Log.e("PickedDate: ", "Date: $_pickedDate") //2019-02-12
                start_date!!.text = _pickedDate
            }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.MONTH]
        )
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()

    }

    @OnClick(R.id.end_date)
    fun endeate() {


        val c = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this, { view, year, month, dayOfMonth ->
                val _year = year.toString()
                val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                _pickedDate = "$_date/$_month/$_year"

                CommonUtil.EnddateExam = _date.toInt()
                CommonUtil.EndDateMonth = _month.toInt()
                CommonUtil.Enddateyear = _year.toInt()


                end_date!!.text = _pickedDate
            }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.MONTH]
        )

        val minDay = CommonUtil.DateExam
        val minMonth = CommonUtil.Month
        val minYear = CommonUtil.YearDate
        if (minYear != null) {
            if (minMonth != null) {
                if (minDay != null) {
                    c.set(minYear, minMonth - 1, minDay)
                }
            }
        }
        dialog.datePicker.minDate = c.timeInMillis
        dialog.show()
        CommonUtil.enddate = end_date.toString()

    }
}