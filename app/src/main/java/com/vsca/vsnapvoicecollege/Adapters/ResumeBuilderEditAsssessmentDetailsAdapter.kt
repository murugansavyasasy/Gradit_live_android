package com.vsca.vsnapvoicecollege.Adapters
import android.content.Context
import android.text.TextWatcher
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
import com.vsca.vsnapvoicecollege.Model.GetAssessmentDetailsData
import com.vsca.vsnapvoicecollege.R

class ResumeBuilderEditAsssessmentDetailsAdapter(
    private val context: Context,
    private val assessment: MutableList<GetAssessmentDetailsData>,
    val listener: OnSoftSkillSelectedListener
) : RecyclerView.Adapter<ResumeBuilderEditAsssessmentDetailsAdapter.AssessmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssessmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resume_builder_edit_assessment_item, parent, false)
        return AssessmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssessmentViewHolder, position: Int) {

        val item = assessment[position]

        holder.assessmentWatcher?.let {
            holder.edtAssessment.removeTextChangedListener(it)
        }
        holder.percentageWatcher?.let {
            holder.edtPercentage.removeTextChangedListener(it)
        }

        holder.edtAssessment.setText(item.assessment)
        holder.edtPercentage.setText(item.score)

        holder.assessmentWatcher = holder.edtAssessment.doAfterTextChanged {
            val adapterPos = holder.bindingAdapterPosition
            if (adapterPos != RecyclerView.NO_POSITION) {
                assessment[adapterPos].assessment = it.toString()
            }
        }

        holder.percentageWatcher = holder.edtPercentage.doAfterTextChanged {
            val adapterPos = holder.bindingAdapterPosition
            if (adapterPos != RecyclerView.NO_POSITION) {
                assessment[adapterPos].score = it.toString()
            }
        }

        holder.lblQuestionPick.setOnClickListener {
            val adapterPos = holder.bindingAdapterPosition
            if (adapterPos != RecyclerView.NO_POSITION) {
                listener.onAttachmentPick(
                    EditSkillSet.AttachmentSource.ASSESSMENT,
                    adapterPos,
                    assessment
                )
            }
        }

        val attachments = item.file_path ?: mutableListOf()

        if (attachments.isEmpty()) {
            holder.rcyAttachment.visibility = View.GONE
            holder.rcyAttachment.adapter = null
        } else {
            holder.rcyAttachment.visibility = View.VISIBLE
            holder.rcyAttachment.layoutManager =
                GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)

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

        holder.lblremove.setOnClickListener {
            val adapterPos = holder.bindingAdapterPosition
            if (adapterPos != RecyclerView.NO_POSITION) {
                assessment.removeAt(adapterPos)
                notifyItemRemoved(adapterPos)
                notifyItemRangeChanged(adapterPos, assessment.size)
                listener.onAssessmentListUpdated(assessment)
            }
        }

    }

    override fun getItemCount(): Int = assessment.size

    fun addItem() {
        assessment.add(GetAssessmentDetailsData("", ""))
        notifyItemInserted(assessment.size - 1)
        listener.onAssessmentListUpdated(assessment)

    }

    fun showValidationErrors(recyclerView: RecyclerView): Boolean {
        var isAllValid = true

        assessment.forEachIndexed { index, item ->
            val holder = recyclerView.findViewHolderForAdapterPosition(index) as? AssessmentViewHolder ?: return@forEachIndexed

            if (item.assessment.isBlank()) {
                holder.edtAssessment.error = "Assessment is required!"
                if (isAllValid) holder.edtAssessment.requestFocus()
                isAllValid = false
            }
            if (item.score.isBlank()) {
                holder.edtPercentage.error = "Score is required!"
                if (isAllValid) holder.edtPercentage.requestFocus()
                isAllValid = false
            }
        }

        return isAllValid
    }

    fun getUpdatedList(): List<GetAssessmentDetailsData> = assessment

    inner class AssessmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val edtAssessment: EditText = itemView.findViewById(R.id.edtAssessment)
        val edtPercentage: EditText = itemView.findViewById(R.id.edtPercentage)
        val lblremove: ImageView = itemView.findViewById(R.id.lblremove)
        val rcyAttachment: RecyclerView = itemView.findViewById(R.id.rcyAttachment)

        val lblQuestionPick: TextView = itemView.findViewById(R.id.lblQuestionPick)
        var assessmentWatcher: TextWatcher? = null
        var percentageWatcher: TextWatcher? = null
    }
}


//package com.vsca.vsnapvoicecollege.Adapters
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.core.widget.doAfterTextChanged
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
//class ResumeBuilderEditAsssessmentDetailsAdapter(
//    private val context: Context,
//    private val assessment: MutableList<GetAssessmentDetailsData>,
//    val listener: OnSoftSkillSelectedListener
//) : RecyclerView.Adapter<ResumeBuilderEditAsssessmentDetailsAdapter.AssessmentViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssessmentViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.resume_builder_edit_assessment_item, parent, false)
//        return AssessmentViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: AssessmentViewHolder, position: Int) {
//        val item = assessment[position]
//
//        // Set values in EditTexts/TextViews
//        holder.edtAssessment.setText(item.assessment)
//        holder.edtPercentage.setText(item.score)
//
//
//
//
//
//        holder.lblQuestionPick.setOnClickListener {
//            val adapterPos = holder.bindingAdapterPosition
//            if (adapterPos != RecyclerView.NO_POSITION) {
//                listener.onAttachmentPick(
//                    EditSkillSet.AttachmentSource.ASSESSMENT,
//                    adapterPos,
//                    assessment
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
//            val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
//                flexDirection = FlexDirection.ROW
//                flexWrap = FlexWrap.WRAP
//                justifyContent = JustifyContent.FLEX_START
//                alignItems = AlignItems.FLEX_START
//            }
//
//            holder.rcyAttachment.layoutManager = flexboxLayoutManager
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
//        }
//
//        // Text change updates
//        holder.edtAssessment.doAfterTextChanged {
//            assessment[position].assessment = it.toString()
//        }
//
//        holder.edtPercentage.doAfterTextChanged {
//            assessment[position].score = it.toString()
//        }
//
//
//
//        holder.lblremove.setOnClickListener {
//            assessment.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, assessment.size)
//            listener.onAssessmentListUpdated(assessment)
//        }
//
//    }
//
//    override fun getItemCount(): Int = assessment.size
//
//    fun addItem() {
//        assessment.add(GetAssessmentDetailsData("", ""))
//        notifyItemInserted(assessment.size - 1)
//        listener.onAssessmentListUpdated(assessment)
//
//    }
//
//    fun showValidationErrors(recyclerView: RecyclerView): Boolean {
//        var isAllValid = true
//
//        assessment.forEachIndexed { index, item ->
//            val holder = recyclerView.findViewHolderForAdapterPosition(index) as? AssessmentViewHolder ?: return@forEachIndexed
//
//            if (item.assessment.isBlank()) {
//                holder.edtAssessment.error = "Assessment is required!"
//                if (isAllValid) holder.edtAssessment.requestFocus()
//                isAllValid = false
//            }
//            if (item.score.isBlank()) {
//                holder.edtPercentage.error = "Score is required!"
//                if (isAllValid) holder.edtPercentage.requestFocus()
//                isAllValid = false
//            }
//        }
//
//        return isAllValid
//    }
//
//    fun getUpdatedList(): List<GetAssessmentDetailsData> = assessment
//
//    inner class AssessmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val edtAssessment: EditText = itemView.findViewById(R.id.edtAssessment)
//        val edtPercentage: EditText = itemView.findViewById(R.id.edtPercentage)
//        val lblremove: ImageView = itemView.findViewById(R.id.lblremove)
//        val rcyAttachment: RecyclerView = itemView.findViewById(R.id.rcyAttachment)
//
//        val lblQuestionPick: TextView = itemView.findViewById(R.id.lblQuestionPick)
//    }
//}
