package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.text.TextWatcher
import com.vsca.vsnapvoicecollege.Model.GetProjectDetailsData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.EditSkillSet
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.AttachmentAdapter
import com.vsca.vsnapvoicecollege.Interfaces.OnSoftSkillSelectedListener
import com.vsca.vsnapvoicecollege.R

class ResumeBuilderEditProjectDetailsAdapter(
    private val context: Context,
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

        //  Remove old watcher before setText
        holder.titleWatcher?.let {
            holder.edtProjectTitle.removeTextChangedListener(it)
        }

        holder.edtProjectTitle.setText(item.title)

        //Add new watcher
        holder.titleWatcher = holder.edtProjectTitle.doAfterTextChanged {
            val adapterPos = holder.bindingAdapterPosition
            if (adapterPos != RecyclerView.NO_POSITION) {
                project[adapterPos].title = it.toString()
            }
        }

        holder.lblQuestionPick.setOnClickListener {
            val adapterPos = holder.bindingAdapterPosition
            if (adapterPos != RecyclerView.NO_POSITION) {
                listener.onAttachmentPick(
                    EditSkillSet.AttachmentSource.PROJECT,
                    adapterPos,
                    project
                )
            }
        }

        val attachments = item.file_path ?: mutableListOf()

        if (attachments.isEmpty()) {
            holder.rcyAttachment.visibility = View.GONE
            holder.rcyAttachment.adapter = null
        } else {
            holder.rcyAttachment.visibility = View.VISIBLE
            holder.rcyAttachment.layoutManager = GridLayoutManager(context, 2)
            holder.rcyAttachment.adapter = AttachmentAdapter(
                context,
                attachments
            ) { removePos ->
                val adapterPos = holder.bindingAdapterPosition
                if (adapterPos != RecyclerView.NO_POSITION) {
                    attachments.removeAt(removePos)
                    notifyItemChanged(adapterPos)
                }
            }
        }

        // Remove item
        holder.lblremove.setOnClickListener {
            val adapterPos = holder.bindingAdapterPosition
            if (adapterPos != RecyclerView.NO_POSITION) {
                project.removeAt(adapterPos)
                notifyItemRemoved(adapterPos)
                notifyItemRangeChanged(adapterPos, project.size)
                listener.onProjectListUpdated(project)
            }
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

        val rcyAttachment: RecyclerView = itemView.findViewById(R.id.rcyAttachment)

        val lblQuestionPick: TextView = itemView.findViewById(R.id.lblQuestionPick)

        var titleWatcher: TextWatcher? = null

    }
}


//package com.vsca.vsnapvoicecollege.Adapters
//
//import android.content.Context
//import com.vsca.vsnapvoicecollege.Model.GetProjectDetailsData
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.core.widget.doAfterTextChanged
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.flexbox.AlignItems
//import com.google.android.flexbox.FlexDirection
//import com.google.android.flexbox.FlexWrap
//import com.google.android.flexbox.FlexboxLayoutManager
//import com.google.android.flexbox.JustifyContent
//import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.EditSkillSet
//import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.IntershipAttachmentAdapter
//import com.vsca.vsnapvoicecollege.Interfaces.OnSoftSkillSelectedListener
//import com.vsca.vsnapvoicecollege.Model.GetAssessmentDetailsData
//import com.vsca.vsnapvoicecollege.R
//
//class ResumeBuilderEditProjectDetailsAdapter(
//    private val context: Context,
//    private val project: MutableList<GetProjectDetailsData>,
//    val listener: OnSoftSkillSelectedListener
//) : RecyclerView.Adapter<ResumeBuilderEditProjectDetailsAdapter.ProjectViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.resume_builder_edit_project_item, parent, false)
//        return ProjectViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
//        val item = project[position]
//
//        // Set values in EditTexts/TextViews
//        holder.edtProjectTitle.setText(item.title)
//
//
//
//
//        holder.lblQuestionPick.setOnClickListener {
//            val adapterPos = holder.bindingAdapterPosition
//            if (adapterPos != RecyclerView.NO_POSITION) {
//                listener.onAttachmentPick(
//                    EditSkillSet.AttachmentSource.PROJECT,
//                    adapterPos,
//                    project
//                )
//            }
//        }
//
//        val attachments = item.file_path ?: return
//
//        if (attachments.isEmpty()) {
//            holder.rcyAttachment.visibility = View.GONE
//            holder.rcyAttachment.adapter = null
//        } else {
//            holder.rcyAttachment.visibility = View.VISIBLE
//            holder.rcyAttachment.isNestedScrollingEnabled = false
//
//            holder.rcyAttachment.layoutManager = GridLayoutManager(
//                context,
//                2,
//                RecyclerView.VERTICAL,
//                false
//            )
//
//            holder.rcyAttachment.adapter = IntershipAttachmentAdapter(
//                context,
//                attachments
//            ) { removePos ->
//                val adapterPos = holder.bindingAdapterPosition
//                if (adapterPos != RecyclerView.NO_POSITION) {
//                    attachments.removeAt(removePos)
//                    notifyItemChanged(adapterPos)
//                }
//            }
//
//        }
//
//
//        // Text change updates
//        holder.edtProjectTitle.doAfterTextChanged {
//            project[position].title = it.toString()
//        }
//
//
//
//        holder.lblremove.setOnClickListener {
//            project.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, project.size)
//            listener.onProjectListUpdated(project)
//        }
//
//    }
//
//    override fun getItemCount(): Int = project.size
//
//    fun addItem() {
//        project.add(GetProjectDetailsData(""))
//        notifyItemInserted(project.size - 1)
//        listener.onProjectListUpdated(project)
//
//    }
//
//    fun showValidationErrors(recyclerView: RecyclerView): Boolean {
//        var isAllValid = true
//
//        project.forEachIndexed { index, item ->
//            val holder = recyclerView.findViewHolderForAdapterPosition(index) as? ProjectViewHolder ?: return@forEachIndexed
//
//            if (item.title.isBlank()) {
//                holder.edtProjectTitle.error = "Project title is required!"
//                if (isAllValid) holder.edtProjectTitle.requestFocus()
//                isAllValid = false
//            }
//        }
//
//        return isAllValid
//    }
//
//    fun getUpdatedList(): List<GetProjectDetailsData> = project
//
//    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val edtProjectTitle: EditText = itemView.findViewById(R.id.edtProjectTitle)
//        val lblremove:ImageView = itemView.findViewById(R.id.lblremove)
//
//        val rcyAttachment: RecyclerView = itemView.findViewById(R.id.rcyAttachment)
//
//        val lblQuestionPick: TextView = itemView.findViewById(R.id.lblQuestionPick)
//
//
//    }
//}
