package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Model.GetCategoryWiseCreditDetails
import com.vsca.vsnapvoicecollege.R

class CategoryCreditWiseAdapter constructor(
    data: List<GetCategoryWiseCreditDetails>,
    context: Context
) :
    RecyclerView.Adapter<CategoryCreditWiseAdapter.MyViewHolder>() {
    var categorycreditList: List<GetCategoryWiseCreditDetails> = ArrayList()
    var context: Context
    var Position: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_table_layout_credits, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: GetCategoryWiseCreditDetails = categorycreditList.get(position)
        Position = holder.absoluteAdapterPosition

        holder.layoutCategorytable!!.visibility = View.VISIBLE

        holder.lblTobeObtained!!.text = data.to_be_obtained
        holder.lblObtained!!.text = data.obtained
        holder.lblSemester!!.text = data.semester_name
        holder.lblTotalCredits!!.text = data.total_credits

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

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        categorycreditList = data
        this.context = context
    }
}