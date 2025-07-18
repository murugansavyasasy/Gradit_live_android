package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.databinding.ItemSkillBinding


class SkillSetAdapter(
    private val skillList: List<String>,
    private val onSelectionChanged: (List<String>) -> Unit
) : RecyclerView.Adapter<SkillSetAdapter.SkillViewHolder>() {

    private val selectedStates = MutableList(skillList.size) { true }

    inner class SkillViewHolder(val binding: ItemSkillBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSkillBinding.inflate(inflater, parent, false)
        return SkillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val title = skillList[position]
        holder.binding.checkbox.setOnCheckedChangeListener(null)
        holder.binding.checkbox.text = title
        holder.binding.checkbox.isChecked = selectedStates[position]

        holder.binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            selectedStates[position] = isChecked
            onSelectionChanged(getSelectedItems())
        }
    }

    override fun getItemCount(): Int = skillList.size

    fun getSelectedItems(): List<String> {
        return skillList.filterIndexed { index, _ -> selectedStates[index] }
    }

    fun setAllChecked(isChecked: Boolean) {
        for (i in selectedStates.indices) {
            selectedStates[i] = isChecked
        }
        notifyDataSetChanged()
        onSelectionChanged(getSelectedItems())
    }

    fun areAllChecked(): Boolean {
        return selectedStates.all { it }
    }
}
