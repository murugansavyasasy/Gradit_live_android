package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Model.specificStudent_datalist
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class specificStudent_adapter constructor(data: List<specificStudent_datalist>, context: Context) :
    RecyclerView.Adapter<specificStudent_adapter.MyViewHolder>() {
    private var specificstudentdata: List<specificStudent_datalist> = ArrayList()
    var context: Context
    private var seleteddataArray = ArrayList<String>()
    private var seletedStringdata: String? = null

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {
        @JvmField
        @BindView(R.id.lblEntiredepartment)
        var lblEntiredepartment: TextView? = null

        @JvmField
        @BindView(R.id.chboxEntiredepartment)
        var chboxEntiredepartment: CheckBox? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        specificstudentdata = data
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.specific_student, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: specificStudent_datalist = specificstudentdata[position]
        holder.lblEntiredepartment!!.text = data.name

        holder.chboxEntiredepartment!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                CommonUtil.id = "1"
                CommonUtil.receivertype = "7"
                seleteddataArray.add(data.memberid.toString())
                holder.chboxEntiredepartment!!.isChecked = true

            } else {
                CommonUtil.id = "0"
                seleteddataArray.remove(data.memberid.toString())
                holder.chboxEntiredepartment!!.isChecked = false
            }

            for (i in seleteddataArray) {
                seletedStringdata = seleteddataArray.joinToString("~")
            }

            if (seleteddataArray.size > 0) {
                CommonUtil.receiverid = seletedStringdata.toString()
            } else {
                CommonUtil.receiverid = ""
            }
        })
    }

    override fun getItemCount(): Int {
        return specificstudentdata.size
    }

    fun filterList(filterlist: ArrayList<specificStudent_datalist>) {

        specificstudentdata = filterlist

        notifyDataSetChanged()
    }

}