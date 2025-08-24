package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vsca.vsnapvoicecollege.R

class SelectionProcessAdapter (
    var isCompanyDetail: List<String>,
    private val context: Context?,
) : RecyclerView.Adapter<SelectionProcessAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgSelection: ImageView = itemView!!.findViewById(R.id.imgSelection)
        val lblCompanyName: TextView = itemView!!.findViewById(R.id.lblCompanyName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.selection_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val isSelectionItem = isCompanyDetail[position]

        holder.lblCompanyName.text = isSelectionItem

        Glide.with(context!!)
            .load(R.drawable.tick_round)
            .into(holder.imgSelection)

    }

    override fun getItemCount(): Int {
        return isCompanyDetail.size

    }
}



