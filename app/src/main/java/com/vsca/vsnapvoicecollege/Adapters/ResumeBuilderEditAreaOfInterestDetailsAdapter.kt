package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.R

class ResumeBuilderEditAreaOfInterestDetailsAdapter(
    private val items: MutableList<String>,
    private val onUpdate: (List<String>) -> Unit
) : RecyclerView.Adapter<ResumeBuilderEditAreaOfInterestDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lblAreaOfInterest: TextView = view.findViewById(R.id.lblAreaOfInterest)
        val btnClose: ImageView = view.findViewById(R.id.btnClose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resume_builder_edit_area_of_interest_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.lblAreaOfInterest.text = item

        holder.btnClose.setOnClickListener {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
            onUpdate(items)
        }
    }
}
