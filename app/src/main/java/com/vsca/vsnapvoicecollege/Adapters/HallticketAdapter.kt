package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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

        val clgcity: TextView = itemView!!.findViewById(R.id.clgcity)
        val clgname: TextView = itemView!!.findViewById(R.id.clgname)
        val txt_studentname: TextView = itemView!!.findViewById(R.id.txt_studentname)
        val txt_rollnumber: TextView = itemView!!.findViewById(R.id.txt_rollnumber)
        val txt_dob: TextView = itemView!!.findViewById(R.id.txt_dob)
        val txt_coursename: TextView = itemView!!.findViewById(R.id.txt_coursename)
        val txt_coursecode: TextView = itemView!!.findViewById(R.id.txt_coursecode)
        val txt_department: TextView = itemView!!.findViewById(R.id.txt_department)
        val txt_semester: TextView = itemView!!.findViewById(R.id.txt_semester)
        val txt_subsemnamesemnumber: TextView = itemView!!.findViewById(R.id.txt_subsemnamesemnumber)
        val txt_subject: TextView = itemView!!.findViewById(R.id.txt_subject)
        val txt_subjectcode: TextView = itemView!!.findViewById(R.id.txt_subjectcode)
        val txt_date: TextView = itemView!!.findViewById(R.id.txt_date)
        val txt_time: TextView = itemView!!.findViewById(R.id.txt_time)
        val txt_arrear_regular: TextView = itemView!!.findViewById(R.id.txt_arrear_regular)
        val txt_overallsemattendance: TextView = itemView!!.findViewById(R.id.txt_overallsemattendance)
        val txt_coursewiseattendance: TextView = itemView!!.findViewById(R.id.txt_coursewiseattendance)
        val txt_condonation_paid: TextView = itemView!!.findViewById(R.id.txt_condonation_paid)

        val lnr_name: LinearLayout = itemView!!.findViewById(R.id.lnr_name)
        val lnr_registerno: LinearLayout = itemView!!.findViewById(R.id.lnr_registerno)
        val lnr_dob: LinearLayout = itemView!!.findViewById(R.id.lnr_dob)
        val lnr_depart: LinearLayout = itemView!!.findViewById(R.id.lnr_depart)
        val lnr_Coursename: LinearLayout = itemView!!.findViewById(R.id.lnr_Coursename)
        val lnr_semester: LinearLayout = itemView!!.findViewById(R.id.lnr_semester)
        val lnr_subject: LinearLayout = itemView!!.findViewById(R.id.lnr_subject)
        val lnr_subjectcode: LinearLayout = itemView!!.findViewById(R.id.lnr_subjectcode)
        val lnr_date: LinearLayout = itemView!!.findViewById(R.id.lnr_date)
        val lnr_time: LinearLayout = itemView!!.findViewById(R.id.lnr_time)
        val lnr_arrear_regular: LinearLayout = itemView!!.findViewById(R.id.lnr_arrear_regular)
        val lnr_overall_semester_attendance: LinearLayout = itemView!!.findViewById(R.id.lnr_overall_semester_attendance)
        val lnr_course_wise_attendance: LinearLayout = itemView!!.findViewById(R.id.lnr_course_wise_attendance)
        val lnr_condonation_paid: LinearLayout = itemView!!.findViewById(R.id.lnr_condonation_paid)


    }

    override fun getItemCount(): Int {
        return Hallticket.size
    }

    init {
        Hallticketdata = Hallticket
        this.context = Context
    }
}