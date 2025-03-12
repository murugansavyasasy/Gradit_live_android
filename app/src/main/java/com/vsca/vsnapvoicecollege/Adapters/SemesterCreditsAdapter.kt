package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.vsca.vsnapvoicecollege.Model.GetSemesterWiseCreditDetails
import com.vsca.vsnapvoicecollege.R

class SemesterCreditsAdapter constructor(
    data: List<GetSemesterWiseCreditDetails>,
    context: Context
) :
    RecyclerView.Adapter<SemesterCreditsAdapter.MyViewHolder>() {
    var categorycreditList: List<GetSemesterWiseCreditDetails> = ArrayList()
    var context: Context
    var Position: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_table_layout_credits, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val data: GetSemesterWiseCreditDetails = categorycreditList.get(position)
        Position = holder.absoluteAdapterPosition

        holder.LayoutSemesterTable!!.visibility = View.VISIBLE
        holder.layoutCategorytable!!.visibility = View.GONE

        holder.lblTobeObtainedsem!!.text = data.to_be_obtained
        holder.lblSemesterSem!!.text = data.semester_name
        holder.lblCategory!!.text = data.category_name
        holder.lblObtainedsem!!.text = data.obtained
        holder.lblTotalCreditsSem!!.text = data.total_credits

    }

    override fun getItemCount(): Int {
        return categorycreditList.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder((itemView)!!) {

        val lblTobeObtained: TextView = itemView!!.findViewById(R.id.lblTobeObtained)!!
        val lblSemester: TextView = itemView!!.findViewById(R.id.lblSemester)!!
        val lblObtained: TextView = itemView!!.findViewById(R.id.lblObtained)!!
        val lblTotalCredits: TextView = itemView!!.findViewById(R.id.lblTotalCredits)!!
        val layoutCategorytable: TableLayout = itemView!!.findViewById(R.id.layoutCategorytable)!!
        val lblTobeObtainedsem: TextView = itemView!!.findViewById(R.id.lblTobeObtainedsem)!!
        val lblSemesterSem: TextView = itemView!!.findViewById(R.id.lblSemesterSem)!!
        val lblObtainedsem: TextView = itemView!!.findViewById(R.id.lblObtainedsem)!!
        val lblTotalCreditsSem: TextView = itemView!!.findViewById(R.id.lblTotalCreditsSem)!!
        val LayoutSemesterTable: TableLayout = itemView!!.findViewById(R.id.LayoutSemesterTable)!!
        val lblCategory: TextView = itemView!!.findViewById(R.id.lblCategory)!!

    }

    init {
        categorycreditList = data
        this.context = context


    }
}