package com.vsca.vsnapvoicecollege.Activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.Attendance_Edit_Adapter
import com.vsca.vsnapvoicecollege.Adapters.SelectedRecipientAdapter
import com.vsca.vsnapvoicecollege.Interfaces.Attendance_EditClickLisitiner
import com.vsca.vsnapvoicecollege.Interfaces.RecipientCheckListener
import com.vsca.vsnapvoicecollege.Model.Attendance_edit_List
import com.vsca.vsnapvoicecollege.Model.specificStudent_datalist
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.SenderModel.Attendance_Edit_Selected
import com.vsca.vsnapvoicecollege.SenderModel.RecipientSelected
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivitySpectficeTakeAttendanceBinding
import java.util.Locale

class Spectfice_TakeAttendance : ActionBarActivity() {

    var appViewModel: App? = null
    var getspecifictuterstudent: List<specificStudent_datalist> = ArrayList()
    var Attendance_Edit: List<Attendance_edit_List> = ArrayList()
    var AttendanceStatusEditPresent: ArrayList<String> = ArrayList()
    private var AttendanceStatusEditAbsent: ArrayList<String> = ArrayList()
    var AttendanceStatus: String? = null
    var SpecificStudentList: SelectedRecipientAdapter? = null
    var Attendance_Edit_Adapter: Attendance_Edit_Adapter? = null
    var SeletedHours = ""
    var addAttendance: Boolean = true
    private var isType = "General"
    private lateinit var binding: ActivitySpectficeTakeAttendanceBinding



    override fun onCreate(savedInstanceState: Bundle?) {

        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivitySpectficeTakeAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionbarWithoutBottom(this)

        AttendanceStatus = intent.getStringExtra("EditAttendance")
        Log.d("Attendance_Status", AttendanceStatus.toString())
        CommonUtil.Absentlistcount = ""
        binding.lblSubjectCredits!!.text = CommonUtil.SectionNmaeAttendance
        binding.lblSubjectType!!.text = CommonUtil.Year + " / " + CommonUtil.Semester
        binding.lblSubjectName!!.text = CommonUtil.Department
        CommonUtil.PresentlistStudent.clear()
        CommonUtil.AbsendlistStudent.clear()
        imgRefresh!!.visibility = View.GONE

        val attendanceHours = ArrayList<String>()
        attendanceHours.add(0, "Select hours")

        if (AttendanceStatus.equals("AttendanceEdit")) {
            binding.edtTopic!!.setText(CommonUtil.AttendanceHourEdit[0].title)
            val isType = CommonUtil.AttendanceHourEdit[0].type
            when (isType) {
                "General" -> {
                    binding.rdobtnGeneral!!.isChecked = true
                }
                "Theory" -> {
                    binding.rdobtnTheory!!.isChecked = true
                }
                "Practical" -> {
                    binding.rdobtnPractical!!.isChecked = true
                }
                else -> {
                    binding.rdobtnGeneral!!.isChecked = true
                }
            }
        }

        if (AttendanceStatus.equals("AttendanceEdit")) {
            for (i in CommonUtil.AttendanceHourEdit.indices) {
                attendanceHours.addAll(listOf(CommonUtil.AttendanceHourEdit[i].hour.toString()))
            }
            val adaptersection = ArrayAdapter(this, R.layout.dopdown_spinner, attendanceHours)
            binding.edtHours!!.adapter = adaptersection
        } else {
            for (i in CommonUtil.AttendanceHour.indices) {
                attendanceHours.addAll(listOf(CommonUtil.AttendanceHour[i].hour.toString()))
            }
            val adaptersection = ArrayAdapter(this, R.layout.dopdown_spinner, attendanceHours)
            binding.edtHours!!.adapter = adaptersection
        }
        binding.rdobtnPractical!!.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (binding.rdobtnPractical!!.isChecked) {
                isType = "Practical"
            }
        }

        binding.rdobtnTheory!!.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (binding.rdobtnTheory!!.isChecked) {
                isType = "Theory"
            }
        }

        binding.rdobtnGeneral!!.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            if (binding.rdobtnGeneral!!.isChecked) {
                isType = "General"
            }
        }

        binding.edtHours!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    SeletedHours = binding.edtHours!!.selectedItem.toString()
                    if (!AttendanceStatus.equals("AttendanceEdit")) {
                        if (addAttendance) {
                            addAttendance = false
                            Getspecificstudentdatasubject()
                        }
                        binding.checkRelative!!.visibility = View.VISIBLE
                        binding.recycleSpecificstudentAttendance!!.visibility = View.VISIBLE
                        binding.idSV!!.visibility = View.VISIBLE
                    } else {
                        binding.lblhours!!.text = "Choose hour to edit"
                        binding.lblSelectattendance!!.visibility = View.VISIBLE
                        binding.recycleSpecificstudentAttendance!!.visibility = View.GONE
                        binding.idSV!!.visibility = View.GONE
                        binding.checkRelative!!.visibility = View.GONE
                    }
                } else {
                    SeletedHours = binding.edtHours!!.selectedItem.toString()
                    SelectedRecipientlistAttendanceEdit.clear()
                    binding.lblSelectattendance!!.visibility = View.GONE
                    if (AttendanceStatus.equals("AttendanceEdit")) {
                        Attendance_EditStudentList()
                        binding.checkRelative!!.visibility = View.GONE
                    } else {
                        binding.checkRelative!!.visibility = View.VISIBLE
                    }
                    binding.recycleSpecificstudentAttendance!!.visibility = View.VISIBLE
                    binding.idSV!!.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        binding.chAll4!!.setOnClickListener(View.OnClickListener {

            if (AttendanceStatus.equals("AttendanceEdit")) {
                if (binding.chAll4!!.isChecked) {
                    Attendance_Edit_Adapter!!.unselectall()
                    binding.ALL4!!.text = "Mark all as absent"
                } else {
                    Attendance_Edit_Adapter!!.selectAll()
                    binding.ALL4!!.text = "Mark all as absent"
                }
            } else {
                CommonUtil.AbsendlistStudent.clear()
                CommonUtil.PresentlistStudent.clear()
                if (binding.chAll4!!.isChecked) {
                    SpecificStudentList!!.unselectall()
                    binding.ALL4!!.text = "Mark all as absent"
                } else {
                    SpecificStudentList!!.selectAll()
                    binding.ALL4!!.text = "Mark all as absent"
                }
            }
        })

        appViewModel!!.MarkAttendance!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle("Success")
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            CommonUtil.AttendanceScreen = ""
                            CommonUtil.PresentlistStudent.clear()
                            CommonUtil.AbsendlistStudent.clear()
                            val i: Intent = Intent(this, Attendance::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })
                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        appViewModel!!.GetspecificstudentAttendance!!.observe(this) { response ->

            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    getspecifictuterstudent = response.data!!
                    CommonUtil.AbsendlistStudent.clear()
                    CommonUtil.PresentlistStudent.clear()
                    getspecifictuterstudent.forEach {
                        it.memberid
                        it.name
                        val group = RecipientSelected(it.memberid, it.name,it.regno)
                        SelectedRecipientlist.add(group)
                    }
                    binding.ALL4!!.text = "Mark all as absent"
                    CommonUtil.PresentlistStudent.clear()
                    for (i in SelectedRecipientlist) {
                        CommonUtil.PresentlistStudent.add(i.SelectedId.toString())
                    }

                    SpecificStudentList = SelectedRecipientAdapter(SelectedRecipientlist,
                        this,
                        object : RecipientCheckListener {
                            override fun add(data: RecipientSelected?) {
                                val groupid = data!!.SelectedId
                                Log.d("Group_ID", groupid.toString())
                                binding.chAll4!!.isChecked = false
                                binding.ALL4!!.text = "Mark all as absent"
                            }

                            override fun remove(data: RecipientSelected?) {
                                if (SelectedRecipientlist.size == CommonUtil.AbsendlistStudent.size + 1) {
                                    binding.chAll4!!.isChecked = true
                                    binding.ALL4!!.text = "Mark all as absent"
                                } else {
                                    binding.chAll4!!.isChecked = false
                                    binding.ALL4!!.text = "Mark all as absent"
                                }
                            }
                        })

                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.recycleSpecificstudentAttendance!!.layoutManager = mLayoutManager
                    binding.recycleSpecificstudentAttendance!!.itemAnimator = DefaultItemAnimator()
                    binding.recycleSpecificstudentAttendance!!.adapter = SpecificStudentList
                    binding.recycleSpecificstudentAttendance!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    SpecificStudentList!!.notifyDataSetChanged()
                }
            }
        }

        appViewModel!!.AttendanceEdit!!.observe(this) { response ->

            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    Attendance_Edit = response.data


                    CommonUtil.PresentlistStudent.clear()
                    CommonUtil.AbsendlistStudent.clear()
                    Attendance_Edit.forEach {
                        it.memberid
                        it.attendancetype
                        it.membername
                        it.rollno

                        val group =
                            Attendance_Edit_Selected(
                                it.memberid,
                                it.attendancetype,
                                it.membername,
                                it.rollno
                            )
                        SelectedRecipientlistAttendanceEdit.add(group)

                        if (it.attendancetype == "Present") {
                            AttendanceStatusEditPresent.add(it.memberid)
                            CommonUtil.PresentlistStudent.add(it.memberid)
                        } else {
                            AttendanceStatusEditAbsent.add(it.memberid)
                            CommonUtil.AbsendlistStudent.add(it.memberid)
                        }
                    }
                    Attendance_Edit_Adapter = Attendance_Edit_Adapter(
                        SelectedRecipientlistAttendanceEdit,
                        this,
                        object : Attendance_EditClickLisitiner {
                            override fun add(data: Attendance_Edit_Selected?) {

                            }

                            override fun remove(data: Attendance_Edit_Selected?) {

                            }
                        })

                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.recycleSpecificstudentAttendance!!.layoutManager = mLayoutManager
                    binding.recycleSpecificstudentAttendance!!.itemAnimator = DefaultItemAnimator()
                    binding.recycleSpecificstudentAttendance!!.adapter = Attendance_Edit_Adapter
                    binding.recycleSpecificstudentAttendance!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    Attendance_Edit_Adapter!!.notifyDataSetChanged()
                }
            }
        }

        binding.btnTakeattendance!!.setOnClickListener {

            if (!SeletedHours.equals("Select hours")) {
                CommonUtil.Absentlistcount = CommonUtil.AbsendlistStudent.size.toString()
                if (AttendanceStatus.equals("AttendanceEdit")) {
                    val dlg = this@Spectfice_TakeAttendance.let { AlertDialog.Builder(it) }
                    dlg.setTitle("Number of students absent marked for : " + CommonUtil.Absentlistcount)
                    dlg.setMessage("Click ok to confirm")
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            TakeAttendance("edit")
                            SeletedHours = ""
                        })
                    dlg.setNegativeButton(
                        CommonUtil.CANCEL,
                        DialogInterface.OnClickListener { dialog, which ->

                        })
                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()


                } else {

                    val dlg = this@Spectfice_TakeAttendance.let { AlertDialog.Builder(it) }
                    dlg.setTitle("Number of students absent marked for :" + CommonUtil.Absentlistcount)
                    dlg.setMessage("Click ok to confirm")
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            TakeAttendance("add")
                            SeletedHours = ""
                        })
                    dlg.setNegativeButton(
                        CommonUtil.CANCEL,
                        DialogInterface.OnClickListener { dialog, which ->

                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                }
            } else {
                CommonUtil.ApiAlert(this, "Please select attendance hour")
            }
        }
        binding.idSV!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false

            }

            override fun onQueryTextChange(msg: String): Boolean {
                if (CommonUtil.isAttendanceType == "Edit") {
                    filterEdit(msg)
                } else {
                    filter(msg)
                }
                return false
            }
        })
    }

    private fun filter(text: String) {

        val filteredlist: java.util.ArrayList<RecipientSelected> = java.util.ArrayList()

        for (item in SelectedRecipientlist) {
            if (item.SelectedName!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            CommonUtil.Toast(this, "No Result")
        } else {
            SpecificStudentList!!.filterList(filteredlist, false)
        }
    }


    private fun filterEdit(text: String) {

        val filteredlist: java.util.ArrayList<Attendance_Edit_Selected> = java.util.ArrayList()

        for (item in SelectedRecipientlistAttendanceEdit) {
            if (item.membername!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {

        } else {
            Attendance_Edit_Adapter!!.filterList(filteredlist, false)

        }
    }

    private fun Getspecificstudentdatasubject() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_courseid, CommonUtil.Courseid)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_yearid, CommonUtil.YearId)
        jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.SectionId)
        jsonObject.addProperty("subjectid", CommonUtil.subjectid)

        appViewModel!!.getspecificstudentdataAttendance(jsonObject, this)
        Log.d("getSpecificsubject", jsonObject.toString())
    }

    private fun Attendance_EditStudentList() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_appid, "1")
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_subjectid, CommonUtil.subjectid)
        jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.SectionId)
        jsonObject.addProperty("attendancehour", SeletedHours)
        appViewModel!!.Attendance_Edit(jsonObject, this)
        Log.d("AttendanceEdit", jsonObject.toString())
    }

    private fun TakeAttendance(statue: String) {
        CommonUtil.AttendanceScreen = ""
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.SectionId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_subjectid, CommonUtil.subjectid)
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_date, CommonUtil.Selecteddata)
        jsonObject.addProperty(ApiRequestNames.Req_processtype, statue)
        jsonObject.addProperty("title", binding.edtTopic!!.text.toString())
        jsonObject.addProperty("type", isType)
        jsonObject.addProperty("attendance_hours", SeletedHours)

        val jsonArrayPresentlist = JsonArray()

        //PRESENT LIST

        for (i in 0 until CommonUtil.PresentlistStudent.size) {
            val jsonObjectPresentlist = JsonObject()

            jsonObjectPresentlist.addProperty(
                ApiRequestNames.Req_presentmemberid, CommonUtil.PresentlistStudent[i]
            )
            jsonArrayPresentlist.add(jsonObjectPresentlist)
        }

        //ABSENT LIST

        val ABsendArray = JsonArray()

        for (i in 0 until CommonUtil.AbsendlistStudent.size) {
            val jsonobjectabsend = JsonObject()

            jsonobjectabsend.addProperty(
                ApiRequestNames.Req_absentmemberid, CommonUtil.AbsendlistStudent[i]
            )
            ABsendArray.add(jsonobjectabsend)
        }

        jsonObject.add(ApiRequestNames.Req_presentlist, jsonArrayPresentlist)
        jsonObject.add(ApiRequestNames.Req_absentlist, ABsendArray)
        appViewModel!!.MarkAttendance(jsonObject, this)
        Log.d("MarkAttendance", jsonObject.toString())

    }

    override val layoutResourceId: Int
        get() = R.layout.activity_spectfice_take_attendance


    override fun onResume() {
        var AddId: Int = 1
        super.onResume()

    }

    override fun onBackPressed() {
        CommonUtil.AttendanceScreen = ""
        super.onBackPressed()
    }
}