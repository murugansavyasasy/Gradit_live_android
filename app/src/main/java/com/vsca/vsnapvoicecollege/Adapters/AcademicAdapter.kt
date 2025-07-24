

package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.EducationFormattedData
import com.vsca.vsnapvoicecollege.Model.GetEducationalDetailsData
import com.vsca.vsnapvoicecollege.R

class AcademicAdapter(
    private val items: List<EducationFormattedData>,
    private val onSelectionChanged: (List<EducationFormattedData>) -> Unit
) : RecyclerView.Adapter<AcademicAdapter.AcademicViewHolder>() {

    inner class AcademicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbAcademicItem: CheckBox = itemView.findViewById(R.id.cbAcademicItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcademicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_academic, parent, false)
        return AcademicViewHolder(view)
    }

    override fun onBindViewHolder(holder: AcademicViewHolder, position: Int) {
        val item = items[position]
        holder.cbAcademicItem.setOnCheckedChangeListener(null)
        holder.cbAcademicItem.text = item.classDegree
        holder.cbAcademicItem.isChecked = item.isChecked

        holder.cbAcademicItem.setOnCheckedChangeListener { _, isChecked ->
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
