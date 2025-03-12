package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.vsca.vsnapvoicecollege.Model.ExamSubjectSubList
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class SubjectListAdapter(data: List<ExamSubjectSubList>, context: Context) :
    RecyclerView.Adapter<SubjectListAdapter.MyViewHolder>() {
    var subjectdetailslist: List<ExamSubjectSubList> = ArrayList()
    var context: Context
    var Position: Int = 0
    var mExpandedPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): SubjectListAdapter.MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.subject_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: SubjectListAdapter.MyViewHolder, position: Int
    ) {
        val data: ExamSubjectSubList = subjectdetailslist.get(position)
        val isExpanded = position == mExpandedPosition


        holder.txt_financeandaccounding!!.text =  CommonUtil.isExamName
        holder.txt_testing_creating!!.text = data.examsubjectname
        holder.txt_date!!.text = data.examdate
        holder.txt_fn!!.text = data.examsession
        holder.txt_bcom_Accounts!!.text = data.examvenue
        holder.txt_Syllabus1!!.text = data.examsyllabus


        holder.examlist_constrine!!.setOnClickListener {


            if (isExpanded) {
                mExpandedPosition = if (isExpanded) -1 else position
                holder.consrin2!!.visibility = View.GONE
                notifyDataSetChanged()

            } else {
                mExpandedPosition = if (isExpanded) -1 else position
                holder.consrin2!!.visibility = View.VISIBLE
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return subjectdetailslist.size
    }


    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {

        val txt_financeandaccounding: TextView = itemView!!.findViewById(R.id.txt_financeandaccounding)!!
        val examlist_constrine: RelativeLayout = itemView!!.findViewById(R.id.examlist_constrine)!!
        val txt_date: TextView = itemView!!.findViewById(R.id.txt_date)!!
        val consrin2: ConstraintLayout = itemView!!.findViewById(R.id.consrin2)!!
        val txt_fn: TextView = itemView!!.findViewById(R.id.txt_fn)!!
        val txt_testing_creating: TextView = itemView!!.findViewById(R.id.txt_testing_creating)!!
        val txt_bcom_Accounts: TextView = itemView!!.findViewById(R.id.txt_bcom_Accounts)!!
        val txt_Syllabus: TextView = itemView!!.findViewById(R.id.txt_Syllabus)!!
        val txt_Syllabus1: TextView = itemView!!.findViewById(R.id.txt_Syllabus1)!!


    }

    init {
        subjectdetailslist = data
        this.context = context
    }
}