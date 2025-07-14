package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Interfaces.OnSoftSkillSelectedListener
import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class ResumeBuilderEditInternshipDetailsAdapter(
    private val internships: MutableList<GetInternshipDetailsData>,
    val listener: OnSoftSkillSelectedListener
) : RecyclerView.Adapter<ResumeBuilderEditInternshipDetailsAdapter.InternshipViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InternshipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resume_builder_editskillset_internship_item, parent, false)
        return InternshipViewHolder(view)
    }

    override fun onBindViewHolder(holder: InternshipViewHolder, position: Int) {
        val item = internships[position]

        // Set values in EditTexts/TextViews
        holder.edtCompanyName.setText(item.companyName)
        holder.edtRole.setText(item.designation)
        holder.txtFromDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.from))
        holder.txtToDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.to))

        // Handle FROM date selection
        holder.txtFromDate.setOnClickListener {
            val existingText = holder.txtFromDate.text.toString()

            CommonUtil.showDatePickerWithExistingDate(
                context = holder.itemView.context,
                existingDateStr = existingText,
                minDate = null
            ) { pickedDate, _ ->
                holder.txtFromDate.setText(pickedDate)
                internships[position].from = pickedDate
            }
        }

        // Handle TO date selection (restrict to >= FROM date)
        holder.txtToDate.setOnClickListener {
            val fromDateStr = holder.txtFromDate.text.toString()
            val minMillis = CommonUtil.parseDateToMillis(fromDateStr)

            CommonUtil.showDatePickerWithExistingDate(
                context = holder.itemView.context,
                existingDateStr = holder.txtToDate.text.toString(),
                minDate = minMillis
            ) { pickedDate, _ ->
                holder.txtToDate.setText(pickedDate)
                internships[position].to = pickedDate
            }
        }

        // Text change updates
        holder.edtCompanyName.doAfterTextChanged {
            internships[position].companyName = it.toString()
        }

        holder.edtRole.doAfterTextChanged {
            internships[position].designation = it.toString()
        }

        holder.lblremove.setOnClickListener {
            internships.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, internships.size)
            listener.onInternshipListUpdated(internships)
        }

    }

    override fun getItemCount(): Int = internships.size

    fun addItem() {
        internships.add(GetInternshipDetailsData("", "", "", ""))
        notifyItemInserted(internships.size - 1)
        listener.onInternshipListUpdated(internships)

    }

fun showValidationErrors(recyclerView: RecyclerView): Boolean {
    var isAllValid = true

    internships.forEachIndexed { index, item ->
        val holder = recyclerView.findViewHolderForAdapterPosition(index) as? InternshipViewHolder ?: return@forEachIndexed

        if (item.companyName.isBlank()) {
            holder.edtCompanyName.error = "Company name is required!"
            if (isAllValid) holder.edtCompanyName.requestFocus()
            isAllValid = false
        }
        if (item.designation.isBlank()) {
            holder.edtRole.error = "Role is required!"
            if (isAllValid) holder.edtRole.requestFocus()
            isAllValid = false
        }
        if (item.from=="") {
            holder.txtFromDate.error = "From date is required!"
            if (isAllValid) holder.txtFromDate.requestFocus()
            isAllValid = false
        }
        if (item.to=="") {
            holder.txtToDate.error = "To date is required!"
            if (isAllValid) holder.txtToDate.requestFocus()
            isAllValid = false
        }
    }

    return isAllValid
}


    fun getUpdatedList(): List<GetInternshipDetailsData> = internships

    inner class InternshipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val edtCompanyName: EditText = itemView.findViewById(R.id.edtCompanyName)
        val edtRole: EditText = itemView.findViewById(R.id.edtRole)
        val txtFromDate: TextView = itemView.findViewById(R.id.txtFromDate)
        val txtToDate: TextView = itemView.findViewById(R.id.txtToDate)
        val lblremove: TextView = itemView.findViewById(R.id.lblremove)
    }
}
