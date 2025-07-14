package com.vsca.vsnapvoicecollege.Adapters

import com.vsca.vsnapvoicecollege.Model.GetProjectDetailsData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Interfaces.OnSoftSkillSelectedListener
import com.vsca.vsnapvoicecollege.Model.GetAssessmentDetailsData
import com.vsca.vsnapvoicecollege.R

class ResumeBuilderEditProjectDetailsAdapter(
    private val project: MutableList<GetProjectDetailsData>,
    val listener: OnSoftSkillSelectedListener
) : RecyclerView.Adapter<ResumeBuilderEditProjectDetailsAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resume_builder_edit_project_item, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val item = project[position]

        // Set values in EditTexts/TextViews
        holder.edtProjectTitle.setText(item.title)


        // Text change updates
        holder.edtProjectTitle.doAfterTextChanged {
            project[position].title = it.toString()
        }



        holder.lblremove.setOnClickListener {
            project.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, project.size)
            listener.onProjectListUpdated(project)
        }

    }

    override fun getItemCount(): Int = project.size

    fun addItem() {
        project.add(GetProjectDetailsData(""))
        notifyItemInserted(project.size - 1)
        listener.onProjectListUpdated(project)

    }

    fun showValidationErrors(recyclerView: RecyclerView): Boolean {
        var isAllValid = true

        project.forEachIndexed { index, item ->
            val holder = recyclerView.findViewHolderForAdapterPosition(index) as? ProjectViewHolder ?: return@forEachIndexed

            if (item.title.isBlank()) {
                holder.edtProjectTitle.error = "Project title is required!"
                if (isAllValid) holder.edtProjectTitle.requestFocus()
                isAllValid = false
            }
        }

        return isAllValid
    }

    fun getUpdatedList(): List<GetProjectDetailsData> = project

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val edtProjectTitle: EditText = itemView.findViewById(R.id.edtProjectTitle)
        val lblremove:ImageView = itemView.findViewById(R.id.lblremove)
    }
}
