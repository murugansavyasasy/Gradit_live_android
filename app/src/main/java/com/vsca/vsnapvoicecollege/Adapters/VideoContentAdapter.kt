package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.R


class VideoContentAdapter(
    private val contentlist: ArrayList<String>,
    private val context: Context?
) :
    RecyclerView.Adapter<VideoContentAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var lblContent: TextView

        init {
            lblContent = view.findViewById<View>(R.id.lblContent) as TextView

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_content_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val text_info = contentlist[position]
        holder.lblContent.text = text_info
        holder.lblContent.text = ((position + 1).toString() + context!!.getString(R.string.txt_dot)
                + contentlist.get(position))


    }

    override fun getItemCount(): Int {
        return contentlist.size

    }
}
