package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.Spectfice_TakeAttendance
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Attendance_SenderSide_Adapter(data: List<Daum>, context: Context) :
    RecyclerView.Adapter<Attendance_SenderSide_Adapter.MyViewHolder>() {
    var subjectdata: List<Daum> = ArrayList()
    var context: Context
    var AttendanceStatus: String? = null
    var Attendance: String? = null


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): Attendance_SenderSide_Adapter.MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.attendance_senderside, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val data: Daum = subjectdata.get(position)

        holder.txt_financeandaccounding!!.text = data.coursename
        holder.txt_bcom_Accounts!!.text = data.yearname
        holder.txt_year1!!.text = data.semestername
        holder.txt_semester1!!.text = data.sectionname
        holder.txt_date!!.text = data.subjectname

        if (data.isedit.equals("1")) {
            holder.btn_attendanceedit!!.visibility = View.VISIBLE
        } else {
            holder.btn_attendanceedit!!.visibility = View.GONE
        }

        holder.examlist_constrine!!.setOnClickListener {

            CommonUtil.Department = ""
            CommonUtil.Year = ""
            CommonUtil.Semester = ""
            CommonUtil.SectionNmaeAttendance = ""
            CommonUtil.AttendanceClassData.clear()
            CommonUtil.receiverid = data.sectionid.toString()
            CommonUtil.YearId = data.yearid.toString()
            CommonUtil.SectionId = data.sectionid.toString()
            CommonUtil.Courseid = data.courseid.toString()
            CommonUtil.semesterid = data.semesterid.toString()
            CommonUtil.subjectid = data.subjectid.toString()
            CommonUtil.Department = data.coursename.toString()
            CommonUtil.Year = data.yearname.toString()
            CommonUtil.Semester = data.semestername.toString()
            CommonUtil.SectionNmaeAttendance = data.sectionname.toString()
            CommonUtil.AttendanceHour.clear()
            CommonUtil.AttendanceHour.addAll(data.add_hours)
            CommonUtil.isAttendanceType = "Take"
            AttendanceStatus = "Attendance"
            CommonUtil.AttendanceScreen = "AttendanceScreen"
            val i: Intent = Intent(context, Spectfice_TakeAttendance::class.java)
            i.putExtra("EditAttendance", AttendanceStatus)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(i)

        }

        holder.btn_attendanceedit!!.setOnClickListener {

            CommonUtil.Department = ""
            CommonUtil.Year = ""
            CommonUtil.Semester = ""
            CommonUtil.SectionNmaeAttendance = ""
            CommonUtil.AttendanceClassData.clear()
            CommonUtil.receiverid = data.sectionid
            CommonUtil.YearId = data.yearid
            CommonUtil.SectionId = data.sectionid
            CommonUtil.Courseid = data.courseid
            CommonUtil.semesterid = data.semesterid
            CommonUtil.subjectid = data.subjectid
            CommonUtil.Department = data.coursename
            CommonUtil.Year = data.yearname
            CommonUtil.Semester = data.semestername
            CommonUtil.SectionNmaeAttendance = data.sectionname
            CommonUtil.AttendanceHourEdit.clear()
            CommonUtil.AttendanceHourEdit.addAll(data.edit_hours)

            val i: Intent = Intent(context, Spectfice_TakeAttendance::class.java)
            AttendanceStatus = "AttendanceEdit"
            CommonUtil.isAttendanceType = "Edit"
            Attendance = "False"
            i.putExtra("EditAttendance", AttendanceStatus)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return subjectdata.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {

        @JvmField
        @BindView(R.id.txt_financeandaccounding)
        var txt_financeandaccounding: TextView? = null

        @JvmField
        @BindView(R.id.txt_bcom_Accounts)
        var txt_bcom_Accounts: TextView? = null

        @JvmField
        @BindView(R.id.txt_year1)
        var txt_year1: TextView? = null

        @JvmField
        @BindView(R.id.txt_semester1)
        var txt_semester1: TextView? = null

        @JvmField
        @BindView(R.id.txt_date)
        var txt_date: TextView? = null

        @JvmField
        @BindView(R.id.txt_selectspecfic)
        var txt_selectspecfic: TextView? = null

        @JvmField
        @BindView(R.id.examlist_constrine)
        var examlist_constrine: ConstraintLayout? = null

        @JvmField
        @BindView(R.id.lbl_takeattendance)
        var lbl_takeattendance: TextView? = null

        @JvmField
        @BindView(R.id.btn_attendanceedit)
        var btn_attendanceedit: TextView? = null
        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        subjectdata = data
        this.context = context
    }

    fun AttendanceChecking() {

        val jsonObject = JsonObject()

        jsonObject.addProperty("sectionid", CommonUtil.SectionId)
        jsonObject.addProperty("subjectid", CommonUtil.subjectid)
        jsonObject.addProperty("date", CommonUtil.Selecteddata)
        Log.d("jsonoblect", jsonObject.toString())

        RestClient.apiInterfaces.Attendancecheck(jsonObject)
            ?.enqueue(object : Callback<Attendance_Checking?> {
                override fun onResponse(
                    call: Call<Attendance_Checking?>,
                    response: Response<Attendance_Checking?>
                ) {
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val status = response.body()!!.Status
                            val response = response.body()!!.Message
                            if (status == 1) {
                                val dlg = context.let { AlertDialog.Builder(it) }
                                dlg.setTitle("Info")
                                dlg.setMessage(response)
                                dlg.setPositiveButton("OK",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        CommonUtil.isAttendanceType = "Edit"

                                        val i: Intent =
                                            Intent(context, Spectfice_TakeAttendance::class.java)
                                        AttendanceStatus = "AttendanceEdit"
                                        Attendance = "False"
                                        i.putExtra("Attendance", Attendance)
                                        i.putExtra("EditAttendance", AttendanceStatus)
                                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        context.startActivity(i)

                                    })
                                dlg.setNegativeButton("CANCEL",
                                    DialogInterface.OnClickListener { dialog, which ->

                                    })

                                dlg.setCancelable(false)
                                dlg.create()
                                dlg.show()
                            } else {
                                CommonUtil.isAttendanceType = "Take"
                                CommonUtil.AttendanceScreen = "AttendanceScreen"
                                val i: Intent =
                                    Intent(context, Spectfice_TakeAttendance::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                context.startActivity(i)
                            }
                        }

                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        Log.d("resonsemessage", response.message().toString())

                        CommonUtil.ApiAlertContext(context, response.message())

                    }
                }

                override fun onFailure(call: Call<Attendance_Checking?>, t: Throwable) {

                }

            })
    }
}