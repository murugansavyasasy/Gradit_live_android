package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.R

class SkillSetAdapter(
    private val skillList: List<String>,
    private val onSelectionChanged: (List<String>) -> Unit
) : RecyclerView.Adapter<SkillSetAdapter.SkillViewHolder>() {

    private val selectedItems = mutableSetOf<String>().apply {
        addAll(skillList)
    }

    inner class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_skill, parent, false)
        return SkillViewHolder(view)
    }

    override fun getItemCount(): Int = skillList.size

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill = skillList[position]

        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.text = skill
        holder.checkbox.isChecked = selectedItems.contains(skill)

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(skill)
            } else {
                selectedItems.remove(skill)
            }
            onSelectionChanged(selectedItems.toList())
        }
    }

    fun getSelectedItems(): List<String> = selectedItems.toList()

    fun setAllChecked(isChecked: Boolean) {
        selectedItems.clear()
        if (isChecked) selectedItems.addAll(skillList)
        notifyDataSetChanged()
        onSelectionChanged(selectedItems.toList())
    }
}
