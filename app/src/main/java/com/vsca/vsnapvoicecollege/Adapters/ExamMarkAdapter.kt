package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.vsca.vsnapvoicecollege.Model.ExamMarkListDetails
import com.vsca.vsnapvoicecollege.R

class ExamMarkAdapter(
    var marklist: List<ExamMarkListDetails>,
    private val context: Context?,
) : RecyclerView.Adapter<ExamMarkAdapter.MyViewHolder>() {

    var Position: Int = 0

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val lblName: TextView = itemView!!.findViewById(R.id.lblName)
        val lblMarks: TextView = itemView!!.findViewById(R.id.lblMarks)
        val LayoutNames: ConstraintLayout = itemView!!.findViewById(R.id.LayoutNames)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.exam_mark_list_design, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = marklist[position]

        holder.lblName!!.text = data.subjectname
        holder.lblMarks!!.text = data.marks
        if (position % 2 == 0) {
            holder.LayoutNames!!.setBackgroundColor(Color.parseColor("#FFFFFF"))
        } else {
            holder.LayoutNames!!.setBackgroundColor(Color.parseColor("#DFDFD6"))
        }
    }

    override fun getItemCount(): Int {
        return marklist.size
    }
}