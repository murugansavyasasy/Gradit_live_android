package com.vsca.vsnapvoicecollege.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Interfaces.ChildItemClickListener
import com.vsca.vsnapvoicecollege.Model.Sectiondetail
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil


class Selectiondata_Adapter(
    data: List<Sectiondetail>
) :
    RecyclerView.Adapter<Selectiondata_Adapter.MyViewHolder>() {
    var sectiondata: List<Sectiondetail> = ArrayList()
    var seletedStringdata: String? = null
    lateinit var valueChanges: ChildItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.child_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: Sectiondetail = sectiondata[position]

        holder.section!!.text = data.sectionname

        holder.chboxsection!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                CommonUtil.seleteddataArray.add(data.sectionid.toString())
                CommonUtil.DepartmentChooseIds.add(data.sectionid.toString())
                CommonUtil.SectionId = data.sectionid.toString()
                holder.chboxsection!!.isChecked = true
                valueChanges.onValueChanged(data.sectionid.toString())
            } else {
                CommonUtil.DepartmentChooseIds.remove(data.sectionid.toString())
                CommonUtil.seleteddataArray.remove(data.sectionid.toString())
                holder.chboxsection!!.isChecked = false
            }

            Log.d("seleteddata_size", CommonUtil.seleteddataArray.size.toString())

            for (i in CommonUtil.seleteddataArray) {
                seletedStringdata = CommonUtil.seleteddataArray.joinToString("~")
                Log.d("seletedStringdata", seletedStringdata.toString())
            }

            if (CommonUtil.seleteddataArray.size > 0) {
                CommonUtil.receiverid = seletedStringdata.toString()
            } else {
                CommonUtil.receiverid = ""
            }
        })
    }

    fun setValueChangeListener(valueChangeListener: ChildItemClickListener) {
        valueChanges = valueChangeListener
    }


    override fun getItemCount(): Int {
        return sectiondata.size
    }

    inner class MyViewHolder (itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {

        @JvmField
        @BindView(R.id.section)
        var section: TextView? = null

        @JvmField
        @BindView(R.id.chboxsection)
        var chboxsection: CheckBox? = null


        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        sectiondata = data
    }
}