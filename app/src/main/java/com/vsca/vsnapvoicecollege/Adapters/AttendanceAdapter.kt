package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.mackhartley.roundedprogressbar.RoundedProgressBar
import com.vsca.vsnapvoicecollege.ActivitySender.AttendanceDetailsfromstudent
import com.vsca.vsnapvoicecollege.Model.StudentAttendance
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import soup.neumorphism.NeumorphCardView

class AttendanceAdapter(
    var attendacelist: ArrayList<StudentAttendance>,
    private val context: Context?,
) : RecyclerView.Adapter<AttendanceAdapter.MyViewHolder>() {

    var Position: Int = 0

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @JvmField
        @BindView(R.id.txtSubject)
        var txtSubject: TextView? = null

        @JvmField
        @BindView(R.id.lblstaffName)
        var lblstaffName: TextView? = null

        @JvmField
        @BindView(R.id.crdattendance)
        var crdattendance: RelativeLayout? = null

        @JvmField
        @BindView(R.id.txt24hours)
        var txt24hours: TextView? = null

        @JvmField
        @BindView(R.id.txt12hours)
        var txt12hours: TextView? = null

        @JvmField
        @BindView(R.id.lblatten_presentage)
        var lblatten_presentage: TextView? = null

        @JvmField
        @BindView(R.id.customProgress)
        var customProgress: RoundedProgressBar? = null

        @JvmField
        @BindView(R.id.tblattendance)
        var tblattendance: TableLayout? = null

        @JvmField
        @BindView(R.id.crd_attendance)
        var crd_attendance: NeumorphCardView? = null

        init {
            ButterKnife.bind(this, (itemView))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.attendance_list_ui, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = attendacelist[position]

        holder.crd_attendance!!.visibility = View.VISIBLE
        holder.tblattendance!!.visibility = View.GONE
        holder.txtSubject!!.text = data.subjectname
        holder.lblstaffName!!.text = data.staff_name
        holder.txt24hours!!.text = data.attended_hour
        holder.txt12hours!!.text = data.absent_hour

        if (data.percentage == "" || data.percentage == null) {
            holder.lblatten_presentage!!.visibility = View.GONE
        } else {
            holder.lblatten_presentage!!.visibility = View.VISIBLE
            holder.lblatten_presentage!!.text = "Attendance Percentage : " + data.percentage
        }

        if (data.total_hour != 0) {
            holder.customProgress!!.visibility = View.VISIBLE
            holder.customProgress!!.setProgressPercentage(data.percentage.toDouble(), true)
            holder.customProgress!!.getProgressPercentage()
        } else {
            holder.customProgress!!.visibility = View.GONE
        }

        holder.crdattendance!!.setOnClickListener {
            val i = Intent(context, AttendanceDetailsfromstudent::class.java)
            CommonUtil.AttendanceSubjectId = data.subject_id.toString()
            CommonUtil.AttendanceStaffid = data.staff_id.toString()
            CommonUtil.AttendanceSubjectname = data.subjectname
            CommonUtil.AttendanceStaffname = data.staff_name
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return attendacelist.size

    }
}
