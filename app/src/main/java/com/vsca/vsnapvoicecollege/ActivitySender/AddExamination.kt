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
import com.vsca.vsnapvoicecollege.databinding.ActivityAddExaminationBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
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

    private lateinit var binding: ActivityAddExaminationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAddExaminationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
         ActionbarWithoutBottom(this)
        imgRefresh!!.visibility = View.GONE

        binding.spnhodyear!!.visibility = View.GONE
        binding.spnhodsemester!!.visibility = View.GONE
        binding.spnhodDivision!!.visibility = View.GONE
        CommonUtil.semesterid = ""
        CommonUtil.EditButtonclick = ""

        binding.imgback.setOnClickListener { onBackPressed() }
        binding.LayoutAdvertisement.setOnClickListener { adclick() }
        binding.startDate.setOnClickListener { eventdateClick() }
        binding.endDate.setOnClickListener { endeate() }


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

                        Glide.with(this)
                            .load(AdSmallImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imgthumb!!)
                    }
                }
            })

        binding.getSeletionSubjectButton?.setOnClickListener {

            startdate = binding.startDate!!.text.toString()
            CommonUtil.minimumdate = binding.startDate!!.text.toString()
            enddate = binding.endDate!!.text.toString()
            CommonUtil.maxmumdate = binding.endDate!!.text.toString()


            CommonUtil.Examname = binding.txtTitle!!.text.toString()
            CommonUtil.startdate = startdate
            CommonUtil.enddate = enddate

            if (CommonUtil.Examname.isEmpty()) {
                Toast.makeText(this, CommonUtil.Fill_Exam_name, Toast.LENGTH_SHORT).show()

            } else if (CommonUtil.startdate.isEmpty()) {
                Toast.makeText(this, CommonUtil._Startdate, Toast.LENGTH_SHORT).show()

            } else if (CommonUtil.enddate.isEmpty()) {
                Toast.makeText(this, CommonUtil._Enddate, Toast.LENGTH_SHORT).show()
            } else if (CommonUtil.Priority == "p7" || CommonUtil.Priority.equals("p1")) {

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

            } else if (CommonUtil.Priority.equals("p2")) {

                if (binding.spinningDepartment!!.text.equals(CommonUtil.Classes_I_handle)) {

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
                    } else if (binding.spnhodyear!!.visibility == View.GONE) {
                        Toast.makeText(this, CommonUtil.Select_Course, Toast.LENGTH_SHORT).show()

                    } else if (binding.spnhodsemester!!.visibility == View.GONE) {
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
            binding.conPrinciplelayout!!.visibility = View.VISIBLE
            binding.rcyHodandstaff!!.visibility = View.GONE
            binding.constrinSelectdepaerment!!.visibility = View.GONE
            binding.rcyHod!!.visibility = View.GONE
        } else if (CommonUtil.Priority.equals("p3")) {

            binding.rcyHodandstaff!!.visibility = View.VISIBLE
            binding.constrinSelectdepaerment!!.visibility = View.GONE
            binding.conPrinciplelayout!!.visibility = View.GONE
            GetSubject()
            binding.rcyHod!!.visibility = View.GONE
        } else if (CommonUtil.Priority.equals("p2")) {

            binding.constrinSelectdepaerment!!.visibility = View.VISIBLE
            binding.conPrinciplelayout!!.visibility = View.GONE
            binding.rcyHodandstaff!!.visibility = View.GONE

        }


        appViewModel!!.Getsubjectdata!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    getsubjectlist = response.data!!
                    StaffAdapter = ExamAdd_StaffAdapter(getsubjectlist, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.rcyHodandstaff!!.layoutManager = mLayoutManager
                    binding.rcyHodandstaff!!.itemAnimator = DefaultItemAnimator()
                    binding.rcyHodandstaff!!.adapter = StaffAdapter
                    binding.rcyHodandstaff!!.recycledViewPool.setMaxRecycledViews(0, 80)
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
                    binding.rcyHod!!.visibility = View.VISIBLE
                    getsubjectlist = response.data!!
                    StaffAdapter = ExamAdd_StaffAdapter(getsubjectlist, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.rcyHod!!.layoutManager = mLayoutManager
                    binding.rcyHod!!.itemAnimator = DefaultItemAnimator()
                    binding.rcyHod!!.adapter = StaffAdapter
                    binding.rcyHod!!.recycledViewPool.setMaxRecycledViews(0, 80)
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
                            val divisions = RecipientSelected(it.division_id, it.division_name,"")
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
                                RecipientSelected(it.department_id, it.department_name,"")
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

                            val group = RecipientSelected(it.course_id, it.course_name,"")
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
                            val course = RecipientSelected(it.yearid.toString(), it.yearname,"")
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
//                BaseActivity.UserMenuRequest(this)
                if (status == 1) {
                    GetSemesterSectionData = response.data!!
                    if (GetSemesterSectionData.size > 0) {
                        SelectedRecipientlist.clear()
                        GetSemesterSectionData.forEach {
                            it.clgsemesterid
                            it.semestername
                            val semester = RecipientSelected(it.clgsemesterid, it.semestername,"")
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

        binding.spinningDepartment?.setOnClickListener {
            val popupMenu = PopupMenu(this@AddExamination, binding.spinningDepartment)
            popupMenu.menuInflater.inflate(R.menu.hoddepartmentorclasses, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->

                binding.spinningDepartment!!.text = menuItem.title

                if (binding.spinningDepartment!!.text.equals(CommonUtil.My_Department)) {

                    SeletedType = "1"
                    binding.spnhodDivision!!.visibility = View.VISIBLE
                    binding.spnhodyear!!.visibility = View.VISIBLE
                    binding.spnhodsemester!!.visibility = View.VISIBLE
                    binding.rcyHod!!.visibility = View.GONE
                    Getcoursehoddepartment()

                } else if (binding.spinningDepartment!!.text.equals(CommonUtil.Classes_I_handle)) {

                    SeletedType = "1"
                    SelectedSpinnerIDcousre = ""
                    CommonUtil.YearId = ""
                    CommonUtil.semesterid = ""
                    binding.spnhodDivision!!.visibility = View.GONE
                    binding.spnhodyear!!.visibility = View.GONE
                    binding.spnhodsemester!!.visibility = View.GONE
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
        binding.spnDivision!!.adapter = adapter
        binding.spnDivision!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                if (position != 0) {

                    Division = "1"
                    binding.spnDepartment!!.visibility = View.VISIBLE
                    SelectedSpinnerIDdivision = GetDivisionData!!.get(position - 1).division_id
                    Log.d("spinnerselected", SelectedSpinnerIDdivision!!)
                    GetDivisionData!!.get(position - 1).division_name?.let {
                        Log.d("spinning data", it)
                    }
                    GetDepartmentRequest()
                } else {
                    Division = ""

                    binding.spnDepartment!!.visibility = View.GONE
                    binding.spnCourse!!.visibility = View.GONE
                    binding.spnYear!!.visibility = View.GONE
                    binding.spnSemester!!.visibility = View.GONE

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
        binding.spnDepartment!!.adapter = adapter
        binding.spnDepartment!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    if (position != 0) {
                        Department = "1"
                        binding.spnCourse!!.visibility = View.VISIBLE

                        SelectedSpinnerIDdepart =
                            GetDepartmentData!!.get(position - 1).department_id
                        GetDepartmentData!!.get(position - 1).department_name?.let {
                            CommonUtil.deptidExam = SelectedSpinnerIDdepart.toString()
                            Log.d("departmentid", CommonUtil.deptidExam)
                        }
                        Getcoursedepartment()
                    } else {
                        Department = ""

                        binding.spnCourse!!.visibility = View.GONE
                        binding.spnYear!!.visibility = View.GONE
                        binding.spnSemester!!.visibility = View.GONE
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
        binding.spnCourse!!.adapter = adapter
        binding.spnCourse!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                if (position != 0) {
                    Course = "1"
                    binding.spnYear!!.visibility = View.VISIBLE

                    SelectedSpinnerIDcousre = Getcoursedepartment!!.get(position - 1).course_id
                    Getcoursedepartment!!.get(position - 1).course_name?.let {
                    }
                    GetyearandsectionRequest()
                } else {
                    binding.spnYear!!.visibility = View.GONE
                    binding.spnSemester!!.visibility = View.GONE
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
        binding.spnYear!!.adapter = adapter
        binding.spnYear!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {

                if (position != 0) {
                    Year = "1"
                    binding.spnSemester!!.visibility = View.VISIBLE

                    SelectedSpinnerIDyear = Getyouurclassdata!!.get(position - 1).yearid.toString()
                    Log.d("spinnerselected", SelectedSpinnerIDyear!!)
                    Getyouurclassdata!!.get(position - 1).yearname?.let {
                        CommonUtil.YearId = SelectedSpinnerIDyear.toString()
                        Log.d("spinning data", it)
                    }
                    SemesterRequest()
                } else {
                    binding.spnSemester!!.visibility = View.GONE
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
        binding.spnSemester!!.adapter = adapter
        binding.spnSemester!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        binding.spnhodDivision!!.adapter = adapter
        binding.spnhodDivision!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    if (position != 0) {

                        binding.spnhodyear!!.visibility = View.VISIBLE
                        binding.spnhodsemester!!.visibility = View.VISIBLE

                        SelectedSpinnerIDcousre = Getcoursedepartment!!.get(position - 1).course_id
                        Getcoursedepartment!!.get(position - 1).course_name?.let {
                            Log.d("spinning data", it)
                        }
                        GetyearandhodRequest()
                    } else {

                        binding.spnhodyear!!.visibility = View.GONE
                        binding.spnhodsemester!!.visibility = View.GONE

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
        binding.spnhodyear!!.adapter = adapter
        binding.spnhodyear!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                if (position != 0) {

                    binding.spnhodsemester!!.visibility = View.VISIBLE

                    SelectedSpinnerIDyearhod =
                        Getyouurclassdata!!.get(position - 1).yearid.toString()
                    Getyouurclassdata!!.get(position - 1).yearname?.let {
                        CommonUtil.YearId = SelectedSpinnerIDyearhod.toString()

                    }
                    SemesterhodRequest()
                } else {
                    binding.spnhodsemester!!.visibility = View.GONE
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
        binding.spnhodsemester!!.adapter = adapter
        binding.spnhodsemester!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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
                binding.startDate!!.text = _pickedDate
            }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.MONTH]
        )
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()

    }

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


                binding.endDate!!.text = _pickedDate
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
        CommonUtil.enddate = binding.endDate.toString()

    }
}