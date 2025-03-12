package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.vsca.vsnapvoicecollege.Interfaces.EventClickListener
import com.vsca.vsnapvoicecollege.Model.GetAssignmentDetails
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
//        val lblNoticeboardDescription: TextView = itemView!!.findViewById(R.id.lblNoticeboardDescription)
        val lblNoticeboardDate: TextView = itemView!!.findViewById(R.id.lblNoticeboardDate)
        val lblNoticetime: TextView = itemView!!.findViewById(R.id.lblNoticetime)
        val lblNoticePostedby: TextView = itemView!!.findViewById(R.id.lblNoticePostedby)
        val imgArrowdown: ImageView = itemView!!.findViewById(R.id.imgArrowdown)
//        val imgArrowUp: ImageView = itemView!!.findViewById(R.id.imgArrowUp)
//        val lnrEventsView: LinearLayout = itemView!!.findViewById(R.id.lnrEventsView)
//        val rytNotice: RelativeLayout = itemView!!.findViewById(R.id.rytNotice)
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
        holder.lblNoticetime!!.text = data.event_time
        holder.lblNoticePostedby!!.text = data.createdbyname

        CommonUtil.Eventid = data.eventid.toString()
        if (data.isappread.equals("0")) {
            holder.lblNewCircle!!.visibility = View.VISIBLE
        } else {
            holder.lblNewCircle!!.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return eventlist.size

    }

    fun filterList(filterlist: java.util.ArrayList<GetEventDetailsData>) {

        eventlist = filterlist

        notifyDataSetChanged()
    }
}



