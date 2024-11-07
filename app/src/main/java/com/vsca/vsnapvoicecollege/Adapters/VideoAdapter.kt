package com.vsca.vsnapvoicecollege.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Activities.VideoPlay
import com.vsca.vsnapvoicecollege.Interfaces.VideoListener
import com.vsca.vsnapvoicecollege.Model.GetVideoListDetails
import com.vsca.vsnapvoicecollege.R


class VideoAdapter(
    var eventlist: List<GetVideoListDetails>,
    private val context: Context?,
    val Listener: VideoListener
) : RecyclerView.Adapter<VideoAdapter.MyViewHolder>() {
    private var mExpandedPosition = -1
    var Type = ""

    companion object {
        var videoClick: VideoListener? = null
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lnrVideo: LinearLayout
        var lnrExpandVideo: LinearLayout
        var lblVideoDate: TextView
        var lblVideoTime: TextView
        var lblVideoTitle: TextView
        var lblPostedBy: TextView
        var imgArrowdown: ImageView
        var imgArrowUp: ImageView
        var lblVideoDescription: TextView
        var lnrplayvideo: LinearLayout
        var lnrplayvideo1: LinearLayout
        var lnrRecentNotifications: LinearLayout
        var lblNewCircle: TextView

        init {
            lnrVideo = itemView.findViewById(R.id.lnrVideo)
            lnrplayvideo = itemView.findViewById(R.id.lnrplayvideo)
            lnrplayvideo1 = itemView.findViewById(R.id.lnrplayvideo1)
            lnrExpandVideo = itemView.findViewById(R.id.lnrExpandVideo)
            lblVideoDate = itemView.findViewById(R.id.lblVideoDate)
            lblVideoTime = itemView.findViewById(R.id.lblVideoTime)
            lblVideoTitle = itemView.findViewById(R.id.lblVideoTitle)
            lblPostedBy = itemView.findViewById(R.id.lblPostedBy)
            lblVideoDescription = itemView.findViewById(R.id.lblVideoDescription)
            imgArrowdown = itemView.findViewById(R.id.imgArrowdown1)
            imgArrowUp = itemView.findViewById(R.id.imgArrowUp)
            lblNewCircle = itemView.findViewById(R.id.lblNewCircle)
            lnrRecentNotifications = itemView.findViewById(R.id.lnrRecentNotifications)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.communication_list_design, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val modal = eventlist[position]
        videoClick = Listener
        videoClick?.onVideoClick(holder, modal)

        holder.lnrVideo.visibility = View.VISIBLE
        holder.lnrRecentNotifications.visibility = View.GONE

        val isExpanded = position == mExpandedPosition

        holder.lnrExpandVideo.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.lnrVideo.isActivated = isExpanded
        holder.lnrVideo.visibility = View.VISIBLE


        try {
            val videoDate = modal.createdon
            val splitDate = videoDate!!.split("\\s+".toRegex()).toTypedArray()
            val Date = splitDate[0]
            val Month = splitDate[1]
            val Year = splitDate[2]
            val Date_2 = splitDate[3]
            val Month_2 = splitDate[4]

            holder.lblVideoTitle.text = modal.title
            holder.lblVideoDescription.text = modal.description
            holder.lblPostedBy.text = modal.createdby
            holder.lblVideoDate.text = Date + " " + Month + " " + Year
            holder.lblVideoTime.text = Date_2 + " " + Month_2

        } catch (e: Exception) {
            e.printStackTrace()
        }

        Type = "video"

        if (modal.isappviewed.equals("0")) {
            holder.lblNewCircle.visibility = View.VISIBLE

        } else {
            holder.lblNewCircle.visibility = View.GONE
        }


        if (isExpanded) {
            holder.imgArrowdown.visibility = View.GONE
            holder.lnrplayvideo.visibility = View.GONE

        } else {

            holder.imgArrowdown.visibility = View.VISIBLE
            holder.lnrplayvideo.visibility = View.VISIBLE

        }

        holder.lnrVideo.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

                Type = "video"
                holder.imgArrowdown.visibility = View.GONE
                holder.lblNewCircle.visibility = View.GONE

                if (modal.isappviewed.equals("0")) {

                    holder.lblNewCircle.visibility = View.GONE
                    modal.isappviewed = "1"
                    BaseActivity.AppReadStatusContext(context, Type, modal.detailid!!)

                }

                holder.lnrplayvideo1.setOnClickListener {

                    val i: Intent = Intent(context, VideoPlay::class.java)
                    i.putExtra("iframe", modal.iframe)
                    i.putExtra("title", modal.title)
                    i.putExtra("description", modal.description)
                    i.putExtra("appread", modal.isappviewed)
                    i.putExtra("detailid", modal.detailid)
                    context!!.startActivity(i)

                }
                mExpandedPosition = if (isExpanded) -1 else position
                notifyDataSetChanged()
            }
        })
    }

    override fun getItemCount(): Int {
        return eventlist.size

    }

    fun filterList(filterlist: java.util.ArrayList<GetVideoListDetails>) {

        eventlist = filterlist

        notifyDataSetChanged()
    }
}



