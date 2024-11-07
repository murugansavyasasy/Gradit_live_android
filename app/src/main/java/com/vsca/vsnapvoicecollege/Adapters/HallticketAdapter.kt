package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Model.HallticketResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class HallticketAdapter(
    var Hallticket: List<HallticketResponse>, private var Context: Context
) : RecyclerView.Adapter<HallticketAdapter.MyViewHolder>() {

    var Hallticketdata: List<HallticketResponse> = ArrayList()
    var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): HallticketAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.hallticketlayout, parent, false)
        return MyViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: HallticketAdapter.MyViewHolder, position: Int) {
        val data: HallticketResponse = Hallticket[position]

        holder.clgname!!.text = CommonUtil.Collegename
        holder.clgcity!!.text = CommonUtil.CollegeCity


        if (data.student_name.isNotEmpty()) {
            holder.txt_studentname!!.text = data.student_name
            holder.lnr_name!!.visibility = View.VISIBLE
        } else {
            holder.lnr_name!!.visibility = View.GONE
        }

        if (data.register_number.isNotEmpty()) {
            holder.lnr_registerno!!.visibility = View.VISIBLE
            holder.txt_rollnumber!!.text = data.register_number
        } else {
            holder.lnr_registerno!!.visibility = View.GONE
        }

        if (data.dob.isNotEmpty()) {
            holder.lnr_dob!!.visibility = View.VISIBLE
            holder.txt_dob!!.text = data.dob
        } else {
            holder.lnr_dob!!.visibility = View.GONE
        }

        if (data.department.isNotEmpty()) {
            holder.lnr_depart!!.visibility = View.VISIBLE
            holder.txt_department!!.text = data.department
        } else {
            holder.lnr_depart!!.visibility = View.GONE
        }

        if (data.course_name.isNotEmpty()) {
            holder.lnr_Coursename!!.visibility = View.VISIBLE
            holder.txt_coursename!!.text = data.course_name
        } else {
            holder.lnr_Coursename!!.visibility = View.GONE
        }

        if (data.current_sem.isNotEmpty()) {
            holder.lnr_semester!!.visibility = View.VISIBLE
            holder.txt_semester!!.text = data.current_sem
        } else {
            holder.lnr_semester!!.visibility = View.GONE
        }

        if (data.subject_name.isNotEmpty()) {
            holder.lnr_subject!!.visibility = View.VISIBLE
            holder.txt_subject!!.text = data.subject_name
        } else {
            holder.lnr_subject!!.visibility = View.GONE
        }

        if (data.subject_code.isNotEmpty()) {
            holder.lnr_subjectcode!!.visibility = View.VISIBLE
            holder.txt_subjectcode!!.text = data.subject_code
        } else {
            holder.lnr_subjectcode!!.visibility = View.GONE
        }

        if (data.exam_date.isNotEmpty()) {
            holder.lnr_date!!.visibility = View.VISIBLE
            holder.txt_date!!.text = data.exam_date
        } else {
            holder.lnr_date!!.visibility = View.GONE
        }

        if (data.exam_time.isNotEmpty()) {
            holder.lnr_time!!.visibility = View.VISIBLE
            holder.txt_time!!.text = data.exam_time
        } else {
            holder.lnr_time!!.visibility = View.GONE
        }

        if (data.arrear_regular.isNotEmpty()) {
            holder.lnr_arrear_regular!!.visibility = View.VISIBLE
            holder.txt_arrear_regular!!.text = data.arrear_regular
        } else {
            holder.lnr_arrear_regular!!.visibility = View.GONE
        }

        if (data.overall_semester_attendance.isNotEmpty()) {
            holder.lnr_overall_semester_attendance!!.visibility = View.VISIBLE
            holder.txt_overallsemattendance!!.text = data.overall_semester_attendance
        } else {
            holder.lnr_overall_semester_attendance!!.visibility = View.GONE
        }

        if (data.course_wise_attendance.isNotEmpty()) {
            holder.lnr_course_wise_attendance!!.visibility = View.VISIBLE
            holder.txt_coursewiseattendance!!.text = data.course_wise_attendance
        } else {
            holder.lnr_course_wise_attendance!!.visibility = View.GONE
        }

        if (data.condonation_paid.isNotEmpty()) {
            holder.lnr_condonation_paid!!.visibility = View.VISIBLE
            holder.txt_condonation_paid!!.text = data.condonation_paid
        } else {
            holder.lnr_condonation_paid!!.visibility = View.GONE
        }
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @JvmField
        @BindView(R.id.clgcity)
        var clgcity: TextView? = null

        @JvmField
        @BindView(R.id.clgname)
        var clgname: TextView? = null

        @JvmField
        @BindView(R.id.txt_studentname)
        var txt_studentname: TextView? = null

        @JvmField
        @BindView(R.id.txt_rollnumber)
        var txt_rollnumber: TextView? = null

        @JvmField
        @BindView(R.id.txt_dob)
        var txt_dob: TextView? = null

        @JvmField
        @BindView(R.id.txt_coursename)
        var txt_coursename: TextView? = null

        @JvmField
        @BindView(R.id.txt_coursecode)
        var txt_coursecode: TextView? = null


        @JvmField
        @BindView(R.id.txt_department)
        var txt_department: TextView? = null

        @JvmField
        @BindView(R.id.txt_semester)
        var txt_semester: TextView? = null

        @JvmField
        @BindView(R.id.txt_subsemnamesemnumber)
        var txt_subsemnamesemnumber: TextView? = null

        @JvmField
        @BindView(R.id.txt_subject)
        var txt_subject: TextView? = null

        @JvmField
        @BindView(R.id.txt_subjectcode)
        var txt_subjectcode: TextView? = null

        @JvmField
        @BindView(R.id.txt_date)
        var txt_date: TextView? = null

        @JvmField
        @BindView(R.id.txt_time)
        var txt_time: TextView? = null

        @JvmField
        @BindView(R.id.txt_arrear_regular)
        var txt_arrear_regular: TextView? = null

        @JvmField
        @BindView(R.id.txt_overallsemattendance)
        var txt_overallsemattendance: TextView? = null

        @JvmField
        @BindView(R.id.txt_coursewiseattendance)
        var txt_coursewiseattendance: TextView? = null

        @JvmField
        @BindView(R.id.txt_condonation_paid)
        var txt_condonation_paid: TextView? = null

        @JvmField
        @BindView(R.id.lnr_name)
        var lnr_name: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_registerno)
        var lnr_registerno: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_dob)
        var lnr_dob: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_depart)
        var lnr_depart: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_Coursename)
        var lnr_Coursename: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_semester)
        var lnr_semester: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_subject)
        var lnr_subject: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_subjectcode)
        var lnr_subjectcode: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_date)
        var lnr_date: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_time)
        var lnr_time: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_arrear_regular)
        var lnr_arrear_regular: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_overall_semester_attendance)
        var lnr_overall_semester_attendance: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_course_wise_attendance)
        var lnr_course_wise_attendance: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnr_condonation_paid)
        var lnr_condonation_paid: LinearLayout? = null


        init {
            ButterKnife.bind(this, (view))
        }

    }

    override fun getItemCount(): Int {
        return Hallticket.size
    }

    init {
        Hallticketdata = Hallticket
        this.context = Context
    }
}