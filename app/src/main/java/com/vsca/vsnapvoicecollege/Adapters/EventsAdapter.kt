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
import butterknife.BindView
import butterknife.ButterKnife
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
        @JvmField
        @BindView(R.id.lblNoticeboardTitle)
        var lblNoticeboardTitle: TextView? = null

        @JvmField
        @BindView(R.id.lblNoticeboardDescription)
        var lblNoticeboardDescription: TextView? = null

        @JvmField
        @BindView(R.id.lblNoticeboardDate)
        var lblNoticeboardDate: TextView? = null

        @JvmField
        @BindView(R.id.lblNoticetime)
        var lblNoticetime: TextView? = null

        @JvmField
        @BindView(R.id.lblNoticePostedby)
        var lblNoticePostedby: TextView? = null

        @JvmField
        @BindView(R.id.imgArrowdown)
        var imgArrowdown: ImageView? = null

        @JvmField
        @BindView(R.id.imgArrowUp)
        var imgArrowUp: ImageView? = null

        @JvmField
        @BindView(R.id.lnrEventsView)
        var lnrEventsView: LinearLayout? = null

        @JvmField
        @BindView(R.id.rytNotice)
        var rytNotice: RelativeLayout? = null

        @JvmField
        @BindView(R.id.lnrNoticeboardd)
        var lnrNoticeboardd: RelativeLayout? = null


        @JvmField
        @BindView(R.id.lblNewCircle)
        var lblNewCircle: TextView? = null

        init {
            ButterKnife.bind(this, (itemView))
        }
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



