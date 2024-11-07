package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
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
        @JvmField
        @BindView(R.id.txt_financeandaccounding)
        var txt_financeandaccounding: TextView? = null

        @JvmField
        @BindView(R.id.examlist_constrine)
        var examlist_constrine: RelativeLayout? = null

        @JvmField
        @BindView(R.id.txt_date)
        var txt_date: TextView? = null


        @JvmField
        @BindView(R.id.consrin2)
        var consrin2: ConstraintLayout? = null

        @JvmField
        @BindView(R.id.txt_fn)
        var txt_fn: TextView? = null


        @JvmField
        @BindView(R.id.txt_testing_creating)
        var txt_testing_creating: TextView? = null


        @JvmField
        @BindView(R.id.txt_bcom_Accounts)
        var txt_bcom_Accounts: TextView? = null


        @JvmField
        @BindView(R.id.txt_Syllabus)
        var txt_Syllabus: TextView? = null


        @JvmField
        @BindView(R.id.txt_Syllabus1)
        var txt_Syllabus1: TextView? = null


        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        subjectdetailslist = data
        this.context = context
    }
}