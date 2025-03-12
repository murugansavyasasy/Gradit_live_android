package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.vsca.vsnapvoicecollege.Interfaces.ChatListener
import com.vsca.vsnapvoicecollege.Model.GetStaffDetailsData
import com.vsca.vsnapvoicecollege.R

class ChatStaffAdapter(
    private val listname: List<GetStaffDetailsData>,
    private val context: Context?,
    val eventListener: ChatListener
) : RecyclerView.Adapter<ChatStaffAdapter.MyViewHolder>() {

    companion object {
        var eventadapterClick: ChatListener? = null
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgStaffProfile: ImageView = itemView!!.findViewById(R.id.imgStaffProfile)
        val lblStaffName: TextView = itemView!!.findViewById(R.id.lblStaffName)
        val lblSemester: TextView = itemView!!.findViewById(R.id.lblSemester)
        val lblyear: TextView = itemView!!.findViewById(R.id.lblyear)
        val lblSection: TextView = itemView!!.findViewById(R.id.lblSection)
        val lblSubjectName: TextView = itemView!!.findViewById(R.id.lblSubjectName)
        val LayoutStaff: RelativeLayout = itemView!!.findViewById(R.id.LayoutStaff)
        val lblview: ImageView = itemView!!.findViewById(R.id.lblview)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_chat_parent, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: GetStaffDetailsData = listname.get(position)
        eventadapterClick = eventListener
        eventadapterClick?.onChatStaffCLick(holder, data)

        holder.lblStaffName!!.text = data.staffname
        holder.lblSubjectName!!.text = data.subjectname

        holder.lblSection!!.visibility = View.GONE
        holder.lblyear!!.visibility = View.GONE
        holder.lblSemester!!.visibility = View.GONE
        holder.lblview!!.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return listname.size
    }
}
