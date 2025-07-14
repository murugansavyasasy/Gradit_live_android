package com.vsca.vsnapvoicecollege.Adapters

import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Interfaces.OnSoftSkillSelectedListener
import com.vsca.vsnapvoicecollege.R

class ResumeBuilderEditCertificateDetailsAdapter(
    private val certificate: MutableList<GetCertificateDetailsData>,
    val listener: OnSoftSkillSelectedListener
) : RecyclerView.Adapter<ResumeBuilderEditCertificateDetailsAdapter.CertificateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resume_builder_edit_certificate_item, parent, false)
        return CertificateViewHolder(view)
    }

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
        val item = certificate[position]

        // Set values in EditTexts/TextViews
        holder.edtCourseName.setText(item.courseName)
        holder.edtInstituteName.setText(item.institute)
        holder.edtDuration.setText(item.duration)


        // Text change updates
        holder.edtCourseName.doAfterTextChanged {
            certificate[position].courseName = it.toString()
        }

        holder.edtInstituteName.doAfterTextChanged {
            certificate[position].institute = it.toString()
        }

        holder.edtDuration.doAfterTextChanged {
            certificate[position].duration = it.toString()
        }

        holder.lblremove.setOnClickListener {
            certificate.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, certificate.size)
            listener.onCertificateListUpdated(certificate)
        }

    }

    override fun getItemCount(): Int = certificate.size

    fun addItem() {
        certificate.add(GetCertificateDetailsData("", "", "",))
        notifyItemInserted(certificate.size - 1)
        listener.onCertificateListUpdated(certificate)

    }

    fun showValidationErrors(recyclerView: RecyclerView): Boolean {
        var isAllValid = true

        certificate.forEachIndexed { index, item ->
            val holder = recyclerView.findViewHolderForAdapterPosition(index) as? CertificateViewHolder ?: return@forEachIndexed

            if (item.courseName.isBlank()) {
                holder.edtCourseName.error = "Course name is required!"
                if (isAllValid) holder.edtCourseName.requestFocus()
                isAllValid = false
            }
            if (item.institute.isBlank()) {
                holder.edtInstituteName.error = "Institute is required!"
                if (isAllValid) holder.edtInstituteName.requestFocus()
                isAllValid = false
            }
            if (item.duration.isBlank()) {
                holder.edtDuration.error = "Duration is required!"
                if (isAllValid) holder.edtDuration.requestFocus()
                isAllValid = false
            }
        }

        return isAllValid
    }

    fun getUpdatedList(): List<GetCertificateDetailsData> = certificate

    inner class CertificateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val edtCourseName: EditText = itemView.findViewById(R.id.edtCourseName)
        val edtInstituteName: EditText = itemView.findViewById(R.id.edtInstituteName)
        val edtDuration: TextView = itemView.findViewById(R.id.edtDuration)
        val lblremove: ImageView = itemView.findViewById(R.id.lblremove)
    }
}
