package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Interfaces.ChatListener_Staff
import com.vsca.vsnapvoicecollege.Model.DataX
import com.vsca.vsnapvoicecollege.R

class Chat_AdapterStaff(
    private val listname: List<DataX>,
    private val context: Context?,
    val eventListener: ChatListener_Staff
) : RecyclerView.Adapter<Chat_AdapterStaff.MyViewHolder>() {
    companion object {
        var eventadapterClick: ChatListener_Staff? = null
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @JvmField
        @BindView(R.id.imgStaffProfile)
        var imgStaffProfile: ImageView? = null

        @JvmField
        @BindView(R.id.lblStaffName)
        var lblStaffName: TextView? = null

        @JvmField
        @BindView(R.id.lblSubjectName)
        var lblSubjectName: TextView? = null

        @JvmField
        @BindView(R.id.LayoutStaff)
        var LayoutStaff: RelativeLayout? = null

        @JvmField
        @BindView(R.id.lblSemester)
        var lblSemester: TextView? = null

        @JvmField
        @BindView(R.id.lblyear)
        var lblyear: TextView? = null

        @JvmField
        @BindView(R.id.lblSection)
        var lblSection: TextView? = null

        init {
            ButterKnife.bind(this, (itemView))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_chat_parent, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: DataX = listname.get(position)
        eventadapterClick = eventListener
        eventadapterClick?.onChatStaffdataCLick(holder, data)

        holder.lblStaffName!!.text = data.coursename
        holder.lblSubjectName!!.text = data.subjectname
        holder.lblSection!!.text = data.sectionname
        holder.lblyear!!.text = data.yearname
        holder.lblSemester!!.text = data.semestername
    }

    override fun getItemCount(): Int {
        return listname.size
    }
}