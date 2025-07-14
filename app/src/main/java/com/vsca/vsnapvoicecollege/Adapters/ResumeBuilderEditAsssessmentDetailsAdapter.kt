package com.vsca.vsnapvoicecollege.Adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Interfaces.OnSoftSkillSelectedListener
import com.vsca.vsnapvoicecollege.Model.GetAssessmentDetailsData
import com.vsca.vsnapvoicecollege.R

class ResumeBuilderEditAsssessmentDetailsAdapter(
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

        // Set values in EditTexts/TextViews
        holder.edtAssessment.setText(item.assessment)
        holder.edtPercentage.setText(item.score)


        // Text change updates
        holder.edtAssessment.doAfterTextChanged {
            assessment[position].assessment = it.toString()
        }

        holder.edtPercentage.doAfterTextChanged {
            assessment[position].score = it.toString()
        }


        holder.lblremove.setOnClickListener {
            assessment.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, assessment.size)
            listener.onAssessmentListUpdated(assessment)
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
        val lblremove: TextView = itemView.findViewById(R.id.lblremove)
    }
}
