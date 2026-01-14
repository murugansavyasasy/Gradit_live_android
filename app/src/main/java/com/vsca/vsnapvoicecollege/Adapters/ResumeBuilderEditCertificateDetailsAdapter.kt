package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.text.TextWatcher
import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData

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

class ResumeBuilderEditCertificateDetailsAdapter(
    private val context: Context,
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

        holder.courseWatcher?.let { holder.edtCourseName.removeTextChangedListener(it) }
        holder.instituteWatcher?.let { holder.edtInstituteName.removeTextChangedListener(it) }
        holder.durationWatcher?.let { holder.edtDuration.removeTextChangedListener(it) }

        holder.edtCourseName.setText(item.courseName)
        holder.edtInstituteName.setText(item.institute)
        holder.edtDuration.setText(item.duration)

        holder.courseWatcher = holder.edtCourseName.doAfterTextChanged {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                certificate[pos].courseName = it.toString()
            }
        }

        holder.instituteWatcher = holder.edtInstituteName.doAfterTextChanged {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                certificate[pos].institute = it.toString()
            }
        }

        holder.durationWatcher = holder.edtDuration.doAfterTextChanged {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                certificate[pos].duration = it.toString()
            }
        }

        holder.lblQuestionPick.visibility= View.GONE
        holder.lblQuestionPick.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                listener.onAttachmentPick(
                    EditSkillSet.AttachmentSource.CERTIFICATE,
                    pos,
                    certificate
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
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    attachments.removeAt(removePos)
                    notifyItemChanged(pos)
                }
            }
        }

        holder.lblremove.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                certificate.removeAt(pos)
                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos, certificate.size)
                listener.onCertificateListUpdated(certificate)
            }
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
        val rcyAttachment: RecyclerView = itemView.findViewById(R.id.rcyAttachment)

        val lblQuestionPick: TextView = itemView.findViewById(R.id.lblQuestionPick)

        var courseWatcher: TextWatcher? = null
        var instituteWatcher: TextWatcher? = null
        var durationWatcher: TextWatcher? = null
    }
}


//package com.vsca.vsnapvoicecollege.Adapters
//
//import android.content.Context
//import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData
//
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
//import com.vsca.vsnapvoicecollege.R
//
//class ResumeBuilderEditCertificateDetailsAdapter(
//    private val context: Context,
//    private val certificate: MutableList<GetCertificateDetailsData>,
//    val listener: OnSoftSkillSelectedListener
//) : RecyclerView.Adapter<ResumeBuilderEditCertificateDetailsAdapter.CertificateViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.resume_builder_edit_certificate_item, parent, false)
//        return CertificateViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
//        val item = certificate[position]
//
//        // Set values in EditTexts/TextViews
//        holder.edtCourseName.setText(item.courseName)
//        holder.edtInstituteName.setText(item.institute)
//        holder.edtDuration.setText(item.duration)
//
//
//        holder.lblQuestionPick.setOnClickListener {
//            val adapterPos = holder.bindingAdapterPosition
//            if (adapterPos != RecyclerView.NO_POSITION) {
//                listener.onAttachmentPick(
//                    EditSkillSet.AttachmentSource.CERTIFICATE,
//                    adapterPos,
//                    certificate
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
//
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
//        holder.edtCourseName.doAfterTextChanged {
//            certificate[position].courseName = it.toString()
//        }
//
//        holder.edtInstituteName.doAfterTextChanged {
//            certificate[position].institute = it.toString()
//        }
//
//        holder.edtDuration.doAfterTextChanged {
//            certificate[position].duration = it.toString()
//        }
//
//        holder.lblremove.setOnClickListener {
//            certificate.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, certificate.size)
//            listener.onCertificateListUpdated(certificate)
//        }
//
//    }
//
//    override fun getItemCount(): Int = certificate.size
//
//    fun addItem() {
//        certificate.add(GetCertificateDetailsData("", "", "",))
//        notifyItemInserted(certificate.size - 1)
//        listener.onCertificateListUpdated(certificate)
//
//    }
//
//    fun showValidationErrors(recyclerView: RecyclerView): Boolean {
//        var isAllValid = true
//
//        certificate.forEachIndexed { index, item ->
//            val holder = recyclerView.findViewHolderForAdapterPosition(index) as? CertificateViewHolder ?: return@forEachIndexed
//
//            if (item.courseName.isBlank()) {
//                holder.edtCourseName.error = "Course name is required!"
//                if (isAllValid) holder.edtCourseName.requestFocus()
//                isAllValid = false
//            }
//            if (item.institute.isBlank()) {
//                holder.edtInstituteName.error = "Institute is required!"
//                if (isAllValid) holder.edtInstituteName.requestFocus()
//                isAllValid = false
//            }
//            if (item.duration.isBlank()) {
//                holder.edtDuration.error = "Duration is required!"
//                if (isAllValid) holder.edtDuration.requestFocus()
//                isAllValid = false
//            }
//        }
//
//        return isAllValid
//    }
//
//    fun getUpdatedList(): List<GetCertificateDetailsData> = certificate
//
//    inner class CertificateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val edtCourseName: EditText = itemView.findViewById(R.id.edtCourseName)
//        val edtInstituteName: EditText = itemView.findViewById(R.id.edtInstituteName)
//        val edtDuration: TextView = itemView.findViewById(R.id.edtDuration)
//        val lblremove: ImageView = itemView.findViewById(R.id.lblremove)
//        val rcyAttachment: RecyclerView = itemView.findViewById(R.id.rcyAttachment)
//
//        val lblQuestionPick: TextView = itemView.findViewById(R.id.lblQuestionPick)
//    }
//}
