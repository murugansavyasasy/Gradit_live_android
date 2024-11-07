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
import butterknife.BindView
import butterknife.ButterKnife
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

        @JvmField
        @BindView(R.id.txt_get_subject)
        var txt_get_subject: TextView? = null

        @JvmField
        @BindView(R.id.examlist_constrine)
        var examlist_constrine: ConstraintLayout? = null

        @JvmField
        @BindView(R.id.txt_financeandaccounding)
        var txt_financeandaccounding: TextView? = null

        @JvmField
        @BindView(R.id.txt_testing_creating)
        var txt_testing_creating: TextView? = null

        @JvmField
        @BindView(R.id.txt_bcom_Accounts)
        var txt_bcom_Accounts: TextView? = null

        @JvmField
        @BindView(R.id.txt_year1)
        var txt_year1: TextView? = null

        @JvmField
        @BindView(R.id.txt_semester1)
        var txt_semester1: TextView? = null

        @JvmField
        @BindView(R.id.txt_b_section)
        var txt_b_section: TextView? = null

        @JvmField
        @BindView(R.id.txt_startdate)
        var txt_startdate: TextView? = null

        @JvmField
        @BindView(R.id.txt_enddate)
        var txt_enddate: TextView? = null

        @JvmField
        @BindView(R.id.btn_editand_delete)
        var btn_editand_delete: LinearLayout? = null

        @JvmField
        @BindView(R.id.txt_editbtn)
        var txt_editbtn: LinearLayout? = null

        @JvmField
        @BindView(R.id.txt_deletebtn)
        var txt_deletebtn: LinearLayout? = null


        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        eaxmlist = data
        this.context = context
    }
}