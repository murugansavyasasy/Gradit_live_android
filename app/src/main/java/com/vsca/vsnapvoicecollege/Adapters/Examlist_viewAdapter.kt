package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.vsca.vsnapvoicecollege.ActivitySender.SubjectList
import com.vsca.vsnapvoicecollege.Interfaces.ExamSubjectclick
import com.vsca.vsnapvoicecollege.Model.examlist
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class Examlist_viewAdapter(
    data: List<examlist>, context: Context, type: Boolean,
    val markListener: ExamSubjectclick
) :
    RecyclerView.Adapter<Examlist_viewAdapter.MyViewHolder>() {
    var eaxmlist: List<examlist> = ArrayList()


    var context: Context
    var Position: Int = 0

    companion object {
        var ExamlistClick: ExamSubjectclick? = null
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): Examlist_viewAdapter.MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.view_examlist, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: Examlist_viewAdapter.MyViewHolder, position: Int) {
        val data: examlist = eaxmlist.get(position)

        ExamlistClick = markListener
        ExamlistClick?.onSubjeckClicklistener(holder, data)

        holder.txt_financeandaccounding!!.text = data.clgdepartmentname
        holder.txt_testing_creating!!.text = data.examnm
        holder.txt_bcom_Accounts!!.text = data.coursename
        holder.txt_year1!!.text = data.yearname
        holder.txt_semester1!!.text = data.semestername
        holder.txt_b_section!!.text = data.clgsectionname
        holder.txt_startdate!!.text = data.startdate
        holder.txt_enddate!!.text = data.enddate
        CommonUtil.semesterid = data.semesterid

        if (CommonUtil.EditButtonclick.equals("ExamEdit")) {

            CommonUtil.ExamEditStartdate = data.startdate
            CommonUtil.ExamEditEnddate = data.enddate

            holder.btn_editand_delete!!.visibility = View.VISIBLE
            holder.txt_get_subject!!.visibility = View.GONE

        } else {
            holder.btn_editand_delete!!.visibility = View.GONE
            holder.txt_get_subject!!.visibility = View.VISIBLE

        }


        holder.txt_get_subject!!.setOnClickListener {
            CommonUtil.isExamName = data.examnm
            val i = Intent(context, SubjectList::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            CommonUtil.SectionId = data.clgsectionid
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }

    }

    override fun getItemCount(): Int {
        return eaxmlist.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {

        val txt_get_subject: TextView = itemView!!.findViewById(R.id.txt_get_subject)
        val examlist_constrine: ConstraintLayout = itemView!!.findViewById(R.id.examlist_constrine)
        val txt_financeandaccounding: TextView = itemView!!.findViewById(R.id.txt_financeandaccounding)
        val txt_testing_creating: TextView = itemView!!.findViewById(R.id.txt_testing_creating)
        val txt_bcom_Accounts: TextView = itemView!!.findViewById(R.id.txt_bcom_Accounts)
        val txt_year1: TextView = itemView!!.findViewById(R.id.txt_year1)
        val txt_semester1: TextView = itemView!!.findViewById(R.id.txt_semester1)
        val txt_b_section: TextView = itemView!!.findViewById(R.id.txt_b_section)
        val txt_startdate: TextView = itemView!!.findViewById(R.id.txt_startdate)
        val txt_enddate: TextView = itemView!!.findViewById(R.id.txt_enddate)
        val btn_editand_delete: LinearLayout = itemView!!.findViewById(R.id.btn_editand_delete)
        val txt_editbtn: LinearLayout = itemView!!.findViewById(R.id.txt_editbtn)
        val txt_deletebtn: LinearLayout = itemView!!.findViewById(R.id.txt_deletebtn)


    }

    init {
        eaxmlist = data
        this.context = context
    }
}