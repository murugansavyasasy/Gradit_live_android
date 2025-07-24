package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.CheckProjectDetailsData
import com.vsca.vsnapvoicecollege.R

class ProjectAdapter(
    private val items: List<CheckProjectDetailsData>,
    private val onSelectionChanged: (List<CheckProjectDetailsData>) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbProjectItem: CheckBox = itemView.findViewById(R.id.cbProject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val item = items[position]

        holder.cbProjectItem.setOnCheckedChangeListener(null)
        holder.cbProjectItem.text = item.title
        holder.cbProjectItem.isChecked = item.isChecked

        holder.cbProjectItem.setOnCheckedChangeListener { _, isChecked ->
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
