package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Interfaces.OnSoftSkillSelectedListener
import com.vsca.vsnapvoicecollege.Model.GetProjectDetailsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderSkillSetSoftSkillsData
class ResumeBuilderSoftSkillsAdapter(
    skills: List<GetResumeBuilderSkillSetSoftSkillsData>,
    preSelected: List<String>,
    private val listener: OnSoftSkillSelectedListener
) : RecyclerView.Adapter<ResumeBuilderSoftSkillsAdapter.ViewHolder>() {


    //Here SkillList is SoftSkills which comes from APi(How many Option there in API)
    private val skillList: List<String> = skills.firstOrNull()?.softSkills ?: emptyList()
    //Here selectedSkills,The User have this SoftSkills which we fetch from SkillSet GET APi
    private val selectedSkills = preSelected.toMutableList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lblcbSelect: CheckBox = view.findViewById(R.id.lblcbSelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resume_builder_editskillset_softskills_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val skill = skillList[position]
        val isSelected = selectedSkills.contains(skill)

        holder.lblcbSelect.text = skill
        holder.lblcbSelect.setOnCheckedChangeListener(null)
        holder.lblcbSelect.isChecked = isSelected
        holder.lblcbSelect.setButtonDrawable(
            if (isSelected) R.drawable.checkedbox else R.drawable.uncheckbox
        )

        holder.lblcbSelect.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!selectedSkills.contains(skill)) selectedSkills.add(skill)
            } else {
                selectedSkills.remove(skill)
            }
            notifyItemChanged(position) // to update drawable
            listener.onSoftSkillsChanged(selectedSkills) // 🔔 notify activity
        }
    }
    fun getUpdatedList(): List<String> = selectedSkills

    override fun getItemCount(): Int = skillList.size
}
