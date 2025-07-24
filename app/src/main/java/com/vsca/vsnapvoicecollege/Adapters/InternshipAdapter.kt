package com.vsca.vsnapvoicecollege.Adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
import com.vsca.vsnapvoicecollege.Model.InternshipFormattedData
import com.vsca.vsnapvoicecollege.R

class InternshipAdapter(
    private val items: List<InternshipFormattedData>,
    private val onSelectionChanged: (List<InternshipFormattedData>) -> Unit
) : RecyclerView.Adapter<InternshipAdapter.InternshipViewHolder>() {

    inner class InternshipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbInternshipItem: CheckBox = itemView.findViewById(R.id.cbInternshipItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InternshipViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_internship, parent, false)
        return InternshipViewHolder(view)
    }

    override fun onBindViewHolder(holder: InternshipViewHolder, position: Int) {
        val item = items[position]
        holder.cbInternshipItem.setOnCheckedChangeListener(null)

        // Display company name + designation (you can customize as needed)
        holder.cbInternshipItem.text = item.companyName

        holder.cbInternshipItem.isChecked = item.isChecked

        holder.cbInternshipItem.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
            onSelectionChanged(items.filter { it.isChecked })
        }
    }

    override fun getItemCount(): Int = items.size

    fun setAllChecked(isChecked: Boolean) {
        items.forEach { it.isChecked = isChecked }
        notifyDataSetChanged()
        onSelectionChanged(items.filter { it.isChecked })
    }
}
