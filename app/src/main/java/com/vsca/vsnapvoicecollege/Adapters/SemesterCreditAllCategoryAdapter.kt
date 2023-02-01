package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Model.SemesterAllCategory
import com.vsca.vsnapvoicecollege.Model.SemesterAllCategoryCredits
import com.vsca.vsnapvoicecollege.R

class SemesterCreditAllCategoryAdapter constructor(data: List<SemesterAllCategory>, context: Context) :
    RecyclerView.Adapter<SemesterCreditAllCategoryAdapter.MyViewHolder>() {
    var categorycreditList: List<SemesterAllCategory> = ArrayList()
    var context: Context
    var Position: Int = 0
    var Type: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.activity_table_layout_credits, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val data: SemesterAllCategory = categorycreditList.get(position)
        Position = holder.getAbsoluteAdapterPosition()

//        if(position == 0){
//            holder.LayoutSemesterTable!!.setBackgroundColor(Color.parseColor("#CCE5F5"))
//        }

//        if(position==4){
//            holder.lblCategory!!.text = data.category_name
//        }
//
//        if(position==13){
//            holder.lblCategory!!.text = data.category_name
//        }

        holder.LayoutSemesterTable!!.visibility=View.VISIBLE
        holder.layoutCategorytable!!.visibility=View.GONE

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
        @JvmField
        @BindView(R.id.lblTobeObtained)
        var lblTobeObtained: TextView? = null

        @JvmField
        @BindView(R.id.lblSemester)
        var lblSemester: TextView? = null

        @JvmField
        @BindView(R.id.lblObtained)
        var lblObtained: TextView? = null

        @JvmField
        @BindView(R.id.lblTotalCredits)
        var lblTotalCredits: TextView? = null

        @JvmField
        @BindView(R.id.layoutCategorytable)
        var layoutCategorytable: TableLayout? = null

        @JvmField
        @BindView(R.id.lblTobeObtainedsem)
        var lblTobeObtainedsem: TextView? = null

        @JvmField
        @BindView(R.id.lblSemesterSem)
        var lblSemesterSem: TextView? = null

        @JvmField
        @BindView(R.id.lblObtainedsem)
        var lblObtainedsem: TextView? = null

        @JvmField
        @BindView(R.id.lblTotalCreditsSem)
        var lblTotalCreditsSem: TextView? = null

        @JvmField
        @BindView(R.id.LayoutSemesterTable)
        var LayoutSemesterTable: TableLayout? = null

        @JvmField
        @BindView(R.id.lblCategory)
        var lblCategory: TextView? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        categorycreditList = data
        this.context = context


    }
}