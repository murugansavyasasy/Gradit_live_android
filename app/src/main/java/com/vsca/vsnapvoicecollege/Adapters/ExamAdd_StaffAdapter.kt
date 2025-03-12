package com.vsca.vsnapvoicecollege.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.vsca.vsnapvoicecollege.Model.Get_staff_yourclass
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil


class ExamAdd_StaffAdapter constructor(data: List<Get_staff_yourclass>, context: Context) :
    RecyclerView.Adapter<ExamAdd_StaffAdapter.MyViewHolder>() {
    var subjectdata: List<Get_staff_yourclass> = ArrayList()
    var context: Context
    var Clickable: String? = null
    private var selectedItemPosition: Int = 0
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ExamAdd_StaffAdapter.MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.examadd_staff, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor", "InvalidR2Usage")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val data: Get_staff_yourclass = subjectdata.get(position)

        holder.txt_financeandaccounding!!.text = data.coursename
        holder.txt_bcom_Accounts!!.text = data.yearname
        holder.txt_year1!!.text = data.semestername
        holder.txt_semester1!!.text = data.sectionname
        holder.txt_date!!.text = data.subjectname

        if (Clickable.equals("YES")) {

            if (selectedItemPosition == position) {
                holder.examlist_constrine!!.setBackgroundResource(R.color.clr_nb_light_blue)
                CommonUtil.semesterid = data.semesterid.toString()
                Log.d("Sectionid", CommonUtil.SectionId)
            } else {
                holder.examlist_constrine!!.setBackgroundResource(R.drawable.bg_shadow_white)
            }
        }

        holder.examlist_constrine!!.setOnClickListener {
            Clickable = "YES"
            selectedItemPosition = position
            notifyDataSetChanged()
        }
    }


    override fun getItemCount(): Int {
        return subjectdata.size

    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {

        val txt_financeandaccounding: TextView = itemView!!.findViewById(R.id.txt_financeandaccounding)
        val txt_bcom_Accounts: TextView = itemView!!.findViewById(R.id.txt_bcom_Accounts)
        val txt_year1: TextView = itemView!!.findViewById(R.id.txt_year1)
        val txt_semester1: TextView = itemView!!.findViewById(R.id.txt_semester1)
        val txt_date: TextView = itemView!!.findViewById(R.id.txt_date)
        val examlist_constrine: ConstraintLayout = itemView!!.findViewById(R.id.examlist_constrine)
        val txt_selectspecfic: TextView = itemView!!.findViewById(R.id.txt_selectspecfic)


    }

    init {
        subjectdata = data
        this.context = context
    }

}