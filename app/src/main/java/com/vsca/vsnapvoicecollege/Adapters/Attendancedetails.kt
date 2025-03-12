package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.attendance
import com.vsca.vsnapvoicecollege.R
import soup.neumorphism.NeumorphCardView

class Attendancedetails(
    var attendacelist: List<attendance>,
    private val context: Context?,
) : RecyclerView.Adapter<Attendancedetails.MyViewHolder>() {

    var isPosition: Int = 0
    private var sNumber: Int = 1
    private var isCount = true

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val lblsnumber: TextView = itemView!!.findViewById(R.id.lblsnumber)
        val lbldate: TextView = itemView!!.findViewById(R.id.lbldate)
        val lblattendhours: TextView = itemView!!.findViewById(R.id.lblattendhours)
        val lblabsenthours: TextView = itemView!!.findViewById(R.id.lblabsenthours)
        val tblattendance: TableLayout = itemView!!.findViewById(R.id.tblattendance)
        val crd_attendance: NeumorphCardView = itemView!!.findViewById(R.id.crd_attendance)


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
        holder.lblsnumber!!.text = (position + 1).toString()

    }

    override fun getItemCount(): Int {
        return attendacelist.size

    }
}
