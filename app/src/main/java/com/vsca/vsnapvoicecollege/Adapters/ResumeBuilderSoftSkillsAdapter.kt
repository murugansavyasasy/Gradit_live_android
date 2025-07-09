package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class ResumeBuilderSoftSkillsAdapter(
    private val skills: List<String>
) : RecyclerView.Adapter<ResumeBuilderSoftSkillsAdapter.ViewHolder>() {

    val selectedSkills = mutableListOf<String>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lblcbSelect: CheckBox = view.findViewById(R.id.lblcbSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resume_builder_editskillset_softskills_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val skill = skills[position]
        val isSelected = selectedSkills.contains(skill)

        holder.lblcbSelect.text = skill
        holder.lblcbSelect.isChecked = isSelected

        val drawableRes = if (isSelected) R.drawable.ch_square_check else R.drawable.ch_square_uncheck
        holder.lblcbSelect.setButtonDrawable(drawableRes)

        // Remove existing listener to avoid incorrect behavior during recycling
        holder.lblcbSelect.setOnCheckedChangeListener(null)

        // Set new listener
        holder.lblcbSelect.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedSkills.add(skill)
            } else {
                selectedSkills.remove(skill)
            }
            notifyItemChanged(position) // Refresh to update the drawable
        }
    }

    override fun getItemCount(): Int = skills.size
}

