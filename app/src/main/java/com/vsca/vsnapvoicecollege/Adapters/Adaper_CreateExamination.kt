package com.vsca.vsnapvoicecollege.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class Adaper_CreateExamination(private val data: ArrayList<sectionnamelist>, context: Context) :
    RecyclerView.Adapter<Adaper_CreateExamination.MyViewHolder>() {
    var sectionlist: List<sectionnamelist> = ArrayList()
    var context: Context
    var Position: Int = 0
    var Subject_List: Subject_List? = null
    var mExpandedPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Adaper_CreateExamination.MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.create_examination, parent, false)
        return MyViewHolder(itemView)
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: Adaper_CreateExamination.MyViewHolder, position: Int) {

        val data: sectionnamelist = sectionlist.get(position)
        if (data.sectionname.equals("")) {

            holder.txt_section!!.text = "No Records Found"
        }

        val isExpanded = position == mExpandedPosition


        holder.subject_recyclerview!!.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.constrine_first!!.isActivated = isExpanded

        holder.txt_section!!.text = data.sectionname

        if (CommonUtil.EditButtonclick.equals("ExamEdit")) {

            if (CommonUtil.SectionID_Exam.equals(data.sectionid)) {

            } else {
                holder.constrine_first!!.setBackgroundColor(R.color.clr_light_pink)
            }

        }

        val itemViewchild = LinearLayoutManager(
            holder.subject_recyclerview!!.context,
            LinearLayoutManager.VERTICAL,
            false
        )

        itemViewchild.initialPrefetchItemCount = data.subjectdetails.size

        holder.constrine_first!!.setOnClickListener {

            if (isExpanded) {

                mExpandedPosition = if (isExpanded) -1 else position
                holder.subject_recyclerview!!.visibility = View.GONE
                notifyDataSetChanged()
                holder.tick!!.setBackgroundResource(R.drawable.tick_white)
                holder.down!!.setBackgroundResource(R.drawable.down_arrow_white)

            } else {

                if (CommonUtil.EditButtonclick.equals("ExamEdit")) {


                    if (CommonUtil.SectionID_Exam.equals(data.sectionid)) {

                        holder.constrine_first!!.isEnabled = true

                        CommonUtil.SectionId = data.sectionid

                        holder.tick!!.setBackgroundResource(R.drawable.examinatin_changeexpandclolur)
                        holder.down!!.setBackgroundResource(R.drawable.ic_arrow_up_white)

                        mExpandedPosition = if (isExpanded) -1 else position
                        holder.subject_recyclerview!!.visibility = View.VISIBLE
                        notifyDataSetChanged()


                    } else {

                        mExpandedPosition = if (isExpanded) -1 else position
                        holder.subject_recyclerview!!.visibility = View.GONE
                        notifyDataSetChanged()
                        holder.tick!!.setBackgroundResource(R.drawable.tick_white)
                        holder.down!!.setBackgroundResource(R.drawable.down_arrow_white)
                    }

                } else {

                    CommonUtil.SectionId = data.sectionid

                    holder.tick!!.setBackgroundResource(R.drawable.examinatin_changeexpandclolur)
                    holder.down!!.setBackgroundResource(R.drawable.ic_arrow_up_white)
                    mExpandedPosition = if (isExpanded) -1 else position
                    holder.subject_recyclerview!!.visibility = View.VISIBLE
                    notifyDataSetChanged()
                }
            }

            Subject_List = Subject_List(data.subjectdetails, context)
            holder.subject_recyclerview!!.layoutManager = itemViewchild
            holder.subject_recyclerview!!.itemAnimator = DefaultItemAnimator()
            holder.subject_recyclerview!!.adapter = Subject_List
            holder.subject_recyclerview!!.recycledViewPool.setMaxRecycledViews(0, 80)
            Subject_List!!.notifyDataSetChanged()

        }
    }

    override fun getItemCount(): Int {
        return sectionlist.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {

        @JvmField
        @BindView(R.id.constrine_first)
        var constrine_first: ConstraintLayout? = null

        @JvmField
        @BindView(R.id.txt_section)
        var txt_section: TextView? = null


        @JvmField
        @BindView(R.id.down)
        var down: TextView? = null


        @JvmField
        @BindView(R.id.tick)
        var tick: TextView? = null

        @JvmField
        @BindView(R.id.subject_recyclerview)
        var subject_recyclerview: RecyclerView? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        sectionlist = data
        this.context = context
    }
}