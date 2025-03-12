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

            holder.txtSection!!.text = "No Records Found"
        }

        val isExpanded = position == mExpandedPosition


        holder.subjectRecyclerview!!.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.constrineFirst!!.isActivated = isExpanded

        holder.txtSection!!.text = data.sectionname

        if (CommonUtil.EditButtonclick.equals("ExamEdit")) {

            if (CommonUtil.SectionID_Exam.equals(data.sectionid)) {

            } else {
                holder.constrineFirst!!.setBackgroundColor(R.color.clr_light_pink)
            }

        }

        val itemViewchild = LinearLayoutManager(
            holder.subjectRecyclerview!!.context,
            LinearLayoutManager.VERTICAL,
            false
        )

        itemViewchild.initialPrefetchItemCount = data.subjectdetails.size

        holder.constrineFirst!!.setOnClickListener {

            if (isExpanded) {

                mExpandedPosition = if (isExpanded) -1 else position
                holder.subjectRecyclerview!!.visibility = View.GONE
                notifyDataSetChanged()
                holder.tick!!.setBackgroundResource(R.drawable.tick_white)
                holder.down!!.setBackgroundResource(R.drawable.down_arrow_white)

            } else {

                if (CommonUtil.EditButtonclick.equals("ExamEdit")) {


                    if (CommonUtil.SectionID_Exam.equals(data.sectionid)) {

                        holder.constrineFirst!!.isEnabled = true

                        CommonUtil.SectionId = data.sectionid

                        holder.tick!!.setBackgroundResource(R.drawable.examinatin_changeexpandclolur)
                        holder.down!!.setBackgroundResource(R.drawable.ic_arrow_up_white)

                        mExpandedPosition = if (isExpanded) -1 else position
                        holder.subjectRecyclerview!!.visibility = View.VISIBLE
                        notifyDataSetChanged()


                    } else {

                        mExpandedPosition = if (isExpanded) -1 else position
                        holder.subjectRecyclerview!!.visibility = View.GONE
                        notifyDataSetChanged()
                        holder.tick!!.setBackgroundResource(R.drawable.tick_white)
                        holder.down!!.setBackgroundResource(R.drawable.down_arrow_white)
                    }

                } else {

                    CommonUtil.SectionId = data.sectionid

                    holder.tick!!.setBackgroundResource(R.drawable.examinatin_changeexpandclolur)
                    holder.down!!.setBackgroundResource(R.drawable.ic_arrow_up_white)
                    mExpandedPosition = if (isExpanded) -1 else position
                    holder.subjectRecyclerview!!.visibility = View.VISIBLE
                    notifyDataSetChanged()
                }
            }

            Subject_List = Subject_List(data.subjectdetails, context)
            holder.subjectRecyclerview!!.layoutManager = itemViewchild
            holder.subjectRecyclerview!!.itemAnimator = DefaultItemAnimator()
            holder.subjectRecyclerview!!.adapter = Subject_List
            holder.subjectRecyclerview!!.recycledViewPool.setMaxRecycledViews(0, 80)
            Subject_List!!.notifyDataSetChanged()

        }
    }

    override fun getItemCount(): Int {
        return sectionlist.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {

        val constrineFirst: ConstraintLayout = itemView!!.findViewById(R.id.constrine_first)
        val txtSection: TextView = itemView!!.findViewById(R.id.txt_section)
        val down: TextView = itemView!!.findViewById(R.id.down)
        val tick: TextView = itemView!!.findViewById(R.id.tick)
        val subjectRecyclerview: RecyclerView = itemView!!.findViewById(R.id.subject_recyclerview)


    }

    init {
        sectionlist = data
        this.context = context
    }
}