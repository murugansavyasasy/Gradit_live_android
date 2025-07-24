

package com.vsca.vsnapvoicecollege.Adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.R

class SkillSetAdapter(
    private val items: List<String>,
    private val onSelectionChanged: (List<String>) -> Unit
) : RecyclerView.Adapter<SkillSetAdapter.ViewHolder>() {

    private val selectedItems = mutableSetOf<String>().apply {
        addAll(items) // ✅ All items selected by default
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox) // your checkbox ID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_skill, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.checkBox.text = item

        holder.checkBox.setOnCheckedChangeListener(null) // prevent unwanted triggering
        holder.checkBox.isChecked = selectedItems.contains(item)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedItems.add(item) else selectedItems.remove(item)
            onSelectionChanged(selectedItems.toList())
        }
    }

    fun setAllChecked(checked: Boolean) {
        selectedItems.clear()
        if (checked) selectedItems.addAll(items)
        notifyDataSetChanged()
        onSelectionChanged(selectedItems.toList())
    }

    fun getSelectedItems(): List<String> = selectedItems.toList()
}

//
//
//class SkillSetAdapter(
//    private val skillList: List<String>,
//    private val onSelectionChanged: (List<String>) -> Unit
//) : RecyclerView.Adapter<SkillSetAdapter.SkillViewHolder>() {
//
//    private val selectedItems = mutableSetOf<String>().apply {
//        addAll(skillList)
//    }
//
//    inner class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_skill, parent, false)
//        return SkillViewHolder(view)
//    }
//
//
//    override fun getItemCount(): Int = skillList.size
//    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
//        val skill = skillList[position]
//
//        holder.checkbox.setOnCheckedChangeListener(null)
//        holder.checkbox.text = skill
//        holder.checkbox.isChecked = selectedItems.contains(skill)
//
//        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                selectedItems.add(skill)
//            } else {
//                selectedItems.remove(skill)
//            }
//            onSelectionChanged(selectedItems.toList())
//        }
//    }
//
//
//    fun getSelectedItems(): List<String> = selectedItems.toList()
//
//
//    fun setAllChecked(isChecked: Boolean) {
//        selectedItems.clear()
//        if (isChecked) selectedItems.addAll(skillList)
//        notifyDataSetChanged()
//        onSelectionChanged(selectedItems.toList())
//    }
//}


