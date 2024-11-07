package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.ActivitySender.AddRecipients
import com.vsca.vsnapvoicecollege.Interfaces.ChildItemClickListener
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import java.util.*

class sectionandyear_Adapter(data: ArrayList<Data>, context: AddRecipients) :
    RecyclerView.Adapter<sectionandyear_Adapter.MyViewHolder>() {
    var datainlist: List<Data> = ArrayList()
    var context: Context
    var Selectiondata_Adapter: Selectiondata_Adapter? = null
    private val valueChangeListener: ChildItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.yearandsection, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: Data = datainlist.get(position)
        holder.year!!.text = data.yearname

        val itemViewchild = LinearLayoutManager(
            holder.child_recyclerview!!.context, LinearLayoutManager.VERTICAL, false
        )


        itemViewchild.initialPrefetchItemCount = data.sectiondetails!!.size

        Log.d("section_size", data.sectiondetails!!.size.toString())

        Selectiondata_Adapter = Selectiondata_Adapter(data.sectiondetails!!)
        holder.child_recyclerview!!.layoutManager = itemViewchild
        holder.child_recyclerview!!.itemAnimator = DefaultItemAnimator()
        holder.child_recyclerview!!.adapter = Selectiondata_Adapter
        holder.child_recyclerview!!.recycledViewPool.setMaxRecycledViews(0, 80)
        Selectiondata_Adapter!!.notifyDataSetChanged()

        Selectiondata_Adapter!!.setValueChangeListener(object : ChildItemClickListener {
            override fun onValueChanged(value: String) {
                for (i in data.sectiondetails!!.indices) {
                    if (data.sectiondetails!![i].sectionid.toString() == value) {
                        CommonUtil.YearId = data.yearid.toString()
                        Log.d("CommonUtil.YearId", CommonUtil.YearId)
                    }
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return datainlist.size
    }

    inner class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {
        @JvmField
        @BindView(R.id.year)
        var year: TextView? = null

        @JvmField
        @BindView(R.id.child_recyclerview)
        var child_recyclerview: RecyclerView? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        datainlist = data
        this.context = context
    }
}

