package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.vsca.vsnapvoicecollege.Interfaces.EventClickListener
import com.vsca.vsnapvoicecollege.Model.GetEventDetailsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class EventsAdapter(
    var eventlist: List<GetEventDetailsData>,
    private val context: Context?,
    val eventListener: EventClickListener
) : RecyclerView.Adapter<EventsAdapter.MyViewHolder>() {

    companion object {
        var eventadapterClick: EventClickListener? = null
    }

    var Type = ""
    var Position: Int = 0
    private var mExpandedPosition: Int = -1

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val lblNoticeboardTitle: TextView = itemView!!.findViewById(R.id.lblNoticeboardTitle)
        val lblNoticeboardDate: TextView = itemView!!.findViewById(R.id.lblNoticeboardDate)
        val lblNoticetime: TextView = itemView!!.findViewById(R.id.lblNoticetime)
        val lblNoticePostedby: TextView = itemView!!.findViewById(R.id.lblNoticePostedby)
        val imgArrowdown: ImageView = itemView!!.findViewById(R.id.imgArrowdown)

        val lnrNoticeboardd: RelativeLayout = itemView!!.findViewById(R.id.lnrNoticeboardd)
        val lblNewCircle: TextView = itemView!!.findViewById(R.id.lblNewCircle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_list_ui, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = eventlist[position]
        eventadapterClick = eventListener
        eventadapterClick?.oneventClick(holder, data)

        Type = "event"
        holder.lblNoticeboardTitle!!.text = data.topic
        holder.lblNoticeboardDate!!.text = data.event_date
        holder.lblNoticetime!!.text = eventTimeText(data)
        holder.lblNoticePostedby!!.text = data.createdbyname

        CommonUtil.Eventid = data.eventid.toString()
        if (data.isappread.equals("0")) {
            holder.lblNewCircle!!.visibility = View.VISIBLE
        } else {
            holder.lblNewCircle!!.visibility = View.GONE
        }

    }

    /**
     * "10:00 AM - 12:00 PM" when the API sends both start and end time; otherwise the API's
     * displaytime, then the start time alone, then the old event_time.
     */
    private fun eventTimeText(data: GetEventDetailsData): String? {
        val start = data.starttime?.trim().takeUnless { it.isNullOrEmpty() || it == "null" }
        val end = data.endtime?.trim().takeUnless { it.isNullOrEmpty() || it == "null" }
        if (start != null && end != null) return "$start - $end"
        val display = data.displaytime?.trim().takeUnless { it.isNullOrEmpty() || it == "null" }
        return display ?: start ?: data.event_time
    }

    override fun getItemCount(): Int {
        return eventlist.size

    }

    fun filterList(filterlist: java.util.ArrayList<GetEventDetailsData>) {

        eventlist = filterlist

        notifyDataSetChanged()
    }
}



