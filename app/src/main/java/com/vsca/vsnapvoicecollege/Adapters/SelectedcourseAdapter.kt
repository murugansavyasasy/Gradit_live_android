package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
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
import com.vsca.vsnapvoicecollege.Interfaces.RecipientCheckListenerint
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.SenderModel.RecipientSelectedint
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class SelectedcourseAdapter constructor(
    data: List<RecipientSelectedint>, context: Context, val checkListener: RecipientCheckListenerint
) : RecyclerView.Adapter<SelectedcourseAdapter.MyViewHolder>() {
    var selectedList: List<RecipientSelectedint> = ArrayList()
    var context: Context
    var Position: Int = 0
    var seleteddataArray = ArrayList<String>()
    var SeletedStringdataReplace: String? = null
    var seletedStringdata: String? = null


    companion object {
        var checkClick: RecipientCheckListenerint? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipientlist_ui, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: RecipientSelectedint = selectedList.get(position)
        Log.d("data123", data.toString())
        Position = holder.absoluteAdapterPosition

        checkClick = checkListener
        holder.lblDocumentName!!.text = data.SelectedName
        Log.d("lblDocumentName", holder.lblDocumentName.toString())


        holder.chbox!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkClick?.add(data)

                data.SelectedId?.let { seleteddataArray.add(it.toString()) }

                for (i in seleteddataArray) {
                    seletedStringdata = seleteddataArray.joinToString { it }
                }
                SeletedStringdataReplace = seletedStringdata!!.replace(", ", "~")

            } else {
                checkClick?.remove(data)
                data.SelectedId?.let { seleteddataArray.removeAt(it) }
                for (i in seleteddataArray) {
                    seletedStringdata = seleteddataArray.joinToString { it }
                }

                SeletedStringdataReplace = seletedStringdata!!.replace(", ", "~")
            }
            if (seleteddataArray.size > 0) {
                CommonUtil.receiverid = SeletedStringdataReplace.toString()
            } else {
                CommonUtil.receiverid = ""
            }
        })
    }

    override fun getItemCount(): Int {
        return selectedList.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {
        @JvmField
        @BindView(R.id.lblRecipientData)
        var lblDocumentName: TextView? = null

        @JvmField
        @BindView(R.id.chbox)
        var chbox: CheckBox? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        selectedList = data
        this.context = context
    }
}