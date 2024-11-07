package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Model.attendance
import com.vsca.vsnapvoicecollege.R
import soup.neumorphism.NeumorphCardView

class Attendancedetails(
    var attendacelist: List<attendance>,
    private val context: Context?,
) : RecyclerView.Adapter<Attendancedetails.MyViewHolder>() {

    var isPosition: Int = 0
    var sNumber: Int = 1

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @JvmField
        @BindView(R.id.lblsnumber)
        var lblsnumber: TextView? = null

        @JvmField
        @BindView(R.id.lbldate)
        var lbldate: TextView? = null

        @JvmField
        @BindView(R.id.lblattendhours)
        var lblattendhours: TextView? = null

        @JvmField
        @BindView(R.id.lblabsenthours)
        var lblabsenthours: TextView? = null

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

        holder.crd_attendance!!.visibility = View.GONE
        holder.tblattendance!!.visibility = View.VISIBLE
        holder.lbldate!!.text = data.attended_date
        holder.lblattendhours!!.text = data.attended_hour_no.toString()
        holder.lblabsenthours!!.text = data.absent_hour_no.toString()
        if (attendacelist.size >= sNumber) {
            holder.lblsnumber!!.text = sNumber.toString()
            sNumber++
        }
    }

    override fun getItemCount(): Int {
        return attendacelist.size

    }
}
