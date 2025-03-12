package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        val imgStaffProfile: ImageView = itemView!!.findViewById(R.id.imgStaffProfile)
        val lblStaffName: TextView = itemView!!.findViewById(R.id.lblStaffName)
        val lblSubjectName: TextView = itemView!!.findViewById(R.id.lblSubjectName)
        val LayoutStaff: RelativeLayout = itemView!!.findViewById(R.id.LayoutStaff)
        val lblSemester: TextView = itemView!!.findViewById(R.id.lblSemester)
        val lblyear: TextView = itemView!!.findViewById(R.id.lblyear)
        val lblSection: TextView = itemView!!.findViewById(R.id.lblSection)


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