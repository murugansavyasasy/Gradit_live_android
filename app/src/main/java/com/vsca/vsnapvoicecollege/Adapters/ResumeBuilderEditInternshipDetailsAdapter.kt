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
import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class ResumeBuilderEditInternshipDetailsAdapter(
    private val context: Context,
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

        holder.companyWatcher?.let { holder.edtCompanyName.removeTextChangedListener(it) }
        holder.roleWatcher?.let { holder.edtRole.removeTextChangedListener(it) }

        holder.edtCompanyName.setText(item.companyName)
        holder.edtRole.setText(item.designation)
        holder.txtFromDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.from))
        holder.txtToDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.to))

        holder.companyWatcher = holder.edtCompanyName.doAfterTextChanged {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                internships[pos].companyName = it.toString()
            }
        }

        holder.roleWatcher = holder.edtRole.doAfterTextChanged {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                internships[pos].designation = it.toString()
            }
        }

        holder.lblQuestionPick.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                listener.onAttachmentPick(
                    EditSkillSet.AttachmentSource.INTERNSHIP,
                    pos,
                    internships
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
            holder.rcyAttachment.adapter =
                AttachmentAdapter(context, attachments) { removePos ->
                    val pos = holder.bindingAdapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        attachments.removeAt(removePos)
                        notifyItemChanged(pos)
                    }
                }
        }

        holder.txtFromDate.setOnClickListener {
            val existingToText = holder.txtToDate.text.toString()

            CommonUtil.showDatePickerWithExistingDate(
                context = holder.itemView.context,
                existingDateStr = holder.txtFromDate.text.toString(),
                minDate = null
            ) { pickedFromDate, pickedFromMillis ->
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    holder.txtFromDate.text = pickedFromDate
                    internships[pos].from = pickedFromDate

                    if (existingToText.isNotBlank()) {
                        val toMillis = CommonUtil.parseDateToMillis(existingToText)
                        if (toMillis != null && pickedFromMillis > toMillis) {
                            holder.txtToDate.text = pickedFromDate
                            internships[pos].to = pickedFromDate
                        }
                    }
                }
            }
        }

       holder.txtToDate.setOnClickListener {
            val minMillis = CommonUtil.parseDateToMillis(holder.txtFromDate.text.toString())
            CommonUtil.showDatePickerWithExistingDate(
                context = holder.itemView.context,
                existingDateStr = holder.txtToDate.text.toString(),
                minDate = minMillis
            ) { pickedDate, _ ->
                val pos = holder.bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    holder.txtToDate.text = pickedDate
                    internships[pos].to = pickedDate
                }
            }
        }

        holder.lblremove.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                internships.removeAt(pos)
                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos, internships.size)
                listener.onInternshipListUpdated(internships)
            }
        }
    }

//    Old Code
//    override fun onBindViewHolder(holder: InternshipViewHolder, position: Int) {
//        val item = internships[position]
//
//        // Set values in EditTexts/TextViews
//        holder.edtCompanyName.setText(item.companyName)
//        holder.edtRole.setText(item.designation)
//        holder.txtFromDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.from))
//        holder.txtToDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.to))
//
//        holder.lblQuestionPick.setOnClickListener {
//            listener.onAttachmentPick(position,internships)
//        }
//
//
//        val attachments = item.file_path ?: return
//
//        if (attachments.isEmpty()) {
//            holder.rcyAttachment.visibility = View.GONE
//            holder.rcyAttachment.adapter = null
//            return
//        }
//
//        holder.rcyAttachment.visibility = View.VISIBLE
//        holder.rcyAttachment.isNestedScrollingEnabled = false
//
//        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
//            flexDirection = FlexDirection.ROW          // horizontal
//            flexWrap = FlexWrap.WRAP                  // wrap to next line
//            justifyContent = JustifyContent.FLEX_START
//            alignItems = AlignItems.FLEX_START
//        }
//
//        holder.rcyAttachment.layoutManager = flexboxLayoutManager
//
//        holder.rcyAttachment.adapter = IntershipAttachmentAdapter(
//            context,
//            attachments
//        ) { removePos ->
//            val adapterPos = holder.bindingAdapterPosition
//            if (adapterPos != RecyclerView.NO_POSITION) {
//                attachments.removeAt(removePos)
//                notifyItemChanged(adapterPos)
//            }
//        }
//
//        // Handle FROM date selection
//        holder.txtFromDate.setOnClickListener {
//            val existingFromText = holder.txtFromDate.text.toString()
//            val existingToText = holder.txtToDate.text.toString()
//
//            CommonUtil.showDatePickerWithExistingDate(
//                context = holder.itemView.context,
//                existingDateStr = existingFromText,
//                minDate = null
//            ) { pickedFromDate, pickedFromMillis ->
//                holder.txtFromDate.setText(pickedFromDate)
//                internships[position].from = pickedFromDate
//
//                // Check if To Date is set and is before new From Date
//                if (!existingToText.isNullOrBlank()) {
//                    val toMillis = CommonUtil.parseDateToMillis(existingToText)
//                    if (toMillis != null && pickedFromMillis > toMillis) {
//                        // Update To Date to match new From Date
//                        holder.txtToDate.setText(pickedFromDate)
//                        internships[position].to = pickedFromDate
//                    }
//                }
//            }
//        }
//
//
//        // Handle TO date selection (restrict to >= FROM date)
//        holder.txtToDate.setOnClickListener {
//            val fromDateStr = holder.txtFromDate.text.toString()
//            val minMillis = CommonUtil.parseDateToMillis(fromDateStr)
//
//            CommonUtil.showDatePickerWithExistingDate(
//                context = holder.itemView.context,
//                existingDateStr = holder.txtToDate.text.toString(),
//                minDate = minMillis
//            ) { pickedDate, _ ->
//                holder.txtToDate.setText(pickedDate)
//                internships[position].to = pickedDate
//            }
//        }
//
//        // Text change updates
//        holder.edtCompanyName.doAfterTextChanged {
//            internships[position].companyName = it.toString()
//        }
//
//        holder.edtRole.doAfterTextChanged {
//            internships[position].designation = it.toString()
//        }
//
//        holder.lblremove.setOnClickListener {
//            internships.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, internships.size)
//            listener.onInternshipListUpdated(internships)
//        }
//
//
//
//    }


    override fun getItemCount(): Int = internships.size

    fun addItem() {
        internships.add(GetInternshipDetailsData("", "", "", "",mutableListOf()))
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
        val lblremove: ImageView = itemView.findViewById(R.id.lblremove)
        val rcyAttachment: RecyclerView = itemView.findViewById(R.id.rcyAttachment)

        val lblQuestionPick: TextView = itemView.findViewById(R.id.lblQuestionPick)


        var companyWatcher: TextWatcher? = null
        var roleWatcher: TextWatcher? = null
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
//import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
//import com.vsca.vsnapvoicecollege.R
//import com.vsca.vsnapvoicecollege.Utils.CommonUtil
//
//class ResumeBuilderEditInternshipDetailsAdapter(
//    private val context: Context,
//    private val internships: MutableList<GetInternshipDetailsData>,
//    val listener: OnSoftSkillSelectedListener
//) : RecyclerView.Adapter<ResumeBuilderEditInternshipDetailsAdapter.InternshipViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InternshipViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.resume_builder_editskillset_internship_item, parent, false)
//        return InternshipViewHolder(view)
//
//    }
//
//    override fun onBindViewHolder(holder: InternshipViewHolder, position: Int) {
//        val item = internships[position]
//
//        // Set values in EditTexts/TextViews
//        holder.edtCompanyName.setText(item.companyName)
//        holder.edtRole.setText(item.designation)
//        holder.txtFromDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.from))
//        holder.txtToDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.to))
//
//        holder.lblQuestionPick.setOnClickListener {
//            val adapterPos = holder.bindingAdapterPosition
//            if (adapterPos != RecyclerView.NO_POSITION) {
//                listener.onAttachmentPick(
//                    EditSkillSet.AttachmentSource.INTERNSHIP,
//                    adapterPos,
//                    internships
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
//        // Handle FROM date selection
//        holder.txtFromDate.setOnClickListener {
//            val existingFromText = holder.txtFromDate.text.toString()
//            val existingToText = holder.txtToDate.text.toString()
//
//            CommonUtil.showDatePickerWithExistingDate(
//                context = holder.itemView.context,
//                existingDateStr = existingFromText,
//                minDate = null
//            ) { pickedFromDate, pickedFromMillis ->
//                val adapterPos = holder.bindingAdapterPosition
//                if (adapterPos != RecyclerView.NO_POSITION) {
//                    holder.txtFromDate.setText(pickedFromDate)
//                    internships[adapterPos].from = pickedFromDate
//
//                    if (!existingToText.isNullOrBlank()) {
//                        val toMillis = CommonUtil.parseDateToMillis(existingToText)
//                        if (toMillis != null && pickedFromMillis > toMillis) {
//                            holder.txtToDate.setText(pickedFromDate)
//                            internships[adapterPos].to = pickedFromDate
//                        }
//                    }
//                }
//            }
//        }
//
//        // Handle TO date selection
//        holder.txtToDate.setOnClickListener {
//            val fromDateStr = holder.txtFromDate.text.toString()
//            val minMillis = CommonUtil.parseDateToMillis(fromDateStr)
//
//            CommonUtil.showDatePickerWithExistingDate(
//                context = holder.itemView.context,
//                existingDateStr = holder.txtToDate.text.toString(),
//                minDate = minMillis
//            ) { pickedDate, _ ->
//                val adapterPos = holder.bindingAdapterPosition
//                if (adapterPos != RecyclerView.NO_POSITION) {
//                    holder.txtToDate.setText(pickedDate)
//                    internships[adapterPos].to = pickedDate
//                }
//            }
//        }
//
//        // Text change updates safely
//        holder.edtCompanyName.doAfterTextChanged {
//            val adapterPos = holder.bindingAdapterPosition
//            if (adapterPos != RecyclerView.NO_POSITION) {
//                internships[adapterPos].companyName = it.toString()
//            }
//        }
//
//        holder.edtRole.doAfterTextChanged {
//            val adapterPos = holder.bindingAdapterPosition
//            if (adapterPos != RecyclerView.NO_POSITION) {
//                internships[adapterPos].designation = it.toString()
//            }
//        }
//
//        // Remove internship
//        holder.lblremove.setOnClickListener {
//            val adapterPos = holder.bindingAdapterPosition
//            if (adapterPos != RecyclerView.NO_POSITION) {
//                internships.removeAt(adapterPos)
//                notifyItemRemoved(adapterPos)
//                notifyItemRangeChanged(adapterPos, internships.size)
//                listener.onInternshipListUpdated(internships)
//                CommonUtil.Remaining = CommonUtil.Remaining+1
//
//            }
//        }
//    }
//
////    Old Code
////    override fun onBindViewHolder(holder: InternshipViewHolder, position: Int) {
////        val item = internships[position]
////
////        // Set values in EditTexts/TextViews
////        holder.edtCompanyName.setText(item.companyName)
////        holder.edtRole.setText(item.designation)
////        holder.txtFromDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.from))
////        holder.txtToDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.to))
////
////        holder.lblQuestionPick.setOnClickListener {
////            listener.onAttachmentPick(position,internships)
////        }
////
////
////        val attachments = item.file_path ?: return
////
////        if (attachments.isEmpty()) {
////            holder.rcyAttachment.visibility = View.GONE
////            holder.rcyAttachment.adapter = null
////            return
////        }
////
////        holder.rcyAttachment.visibility = View.VISIBLE
////        holder.rcyAttachment.isNestedScrollingEnabled = false
////
////        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
////            flexDirection = FlexDirection.ROW          // horizontal
////            flexWrap = FlexWrap.WRAP                  // wrap to next line
////            justifyContent = JustifyContent.FLEX_START
////            alignItems = AlignItems.FLEX_START
////        }
////
////        holder.rcyAttachment.layoutManager = flexboxLayoutManager
////
////        holder.rcyAttachment.adapter = IntershipAttachmentAdapter(
////            context,
////            attachments
////        ) { removePos ->
////            val adapterPos = holder.bindingAdapterPosition
////            if (adapterPos != RecyclerView.NO_POSITION) {
////                attachments.removeAt(removePos)
////                notifyItemChanged(adapterPos)
////            }
////        }
////
////        // Handle FROM date selection
////        holder.txtFromDate.setOnClickListener {
////            val existingFromText = holder.txtFromDate.text.toString()
////            val existingToText = holder.txtToDate.text.toString()
////
////            CommonUtil.showDatePickerWithExistingDate(
////                context = holder.itemView.context,
////                existingDateStr = existingFromText,
////                minDate = null
////            ) { pickedFromDate, pickedFromMillis ->
////                holder.txtFromDate.setText(pickedFromDate)
////                internships[position].from = pickedFromDate
////
////                // Check if To Date is set and is before new From Date
////                if (!existingToText.isNullOrBlank()) {
////                    val toMillis = CommonUtil.parseDateToMillis(existingToText)
////                    if (toMillis != null && pickedFromMillis > toMillis) {
////                        // Update To Date to match new From Date
////                        holder.txtToDate.setText(pickedFromDate)
////                        internships[position].to = pickedFromDate
////                    }
////                }
////            }
////        }
////
////
////        // Handle TO date selection (restrict to >= FROM date)
////        holder.txtToDate.setOnClickListener {
////            val fromDateStr = holder.txtFromDate.text.toString()
////            val minMillis = CommonUtil.parseDateToMillis(fromDateStr)
////
////            CommonUtil.showDatePickerWithExistingDate(
////                context = holder.itemView.context,
////                existingDateStr = holder.txtToDate.text.toString(),
////                minDate = minMillis
////            ) { pickedDate, _ ->
////                holder.txtToDate.setText(pickedDate)
////                internships[position].to = pickedDate
////            }
////        }
////
////        // Text change updates
////        holder.edtCompanyName.doAfterTextChanged {
////            internships[position].companyName = it.toString()
////        }
////
////        holder.edtRole.doAfterTextChanged {
////            internships[position].designation = it.toString()
////        }
////
////        holder.lblremove.setOnClickListener {
////            internships.removeAt(position)
////            notifyItemRemoved(position)
////            notifyItemRangeChanged(position, internships.size)
////            listener.onInternshipListUpdated(internships)
////        }
////
////
////
////    }
//
//
//    override fun getItemCount(): Int = internships.size
//
//    fun addItem() {
//        internships.add(GetInternshipDetailsData("", "", "", "",mutableListOf()))
//        notifyItemInserted(internships.size - 1)
//        listener.onInternshipListUpdated(internships)
//
//    }
//
//    fun showValidationErrors(recyclerView: RecyclerView): Boolean {
//        var isAllValid = true
//
//        internships.forEachIndexed { index, item ->
//            val holder = recyclerView.findViewHolderForAdapterPosition(index) as? InternshipViewHolder ?: return@forEachIndexed
//
//            if (item.companyName.isBlank()) {
//                holder.edtCompanyName.error = "Company name is required!"
//                if (isAllValid) holder.edtCompanyName.requestFocus()
//                isAllValid = false
//            }
//            if (item.designation.isBlank()) {
//                holder.edtRole.error = "Role is required!"
//                if (isAllValid) holder.edtRole.requestFocus()
//                isAllValid = false
//            }
//            if (item.from=="") {
//                holder.txtFromDate.error = "From date is required!"
//                if (isAllValid) holder.txtFromDate.requestFocus()
//                isAllValid = false
//            }
//            if (item.to=="") {
//                holder.txtToDate.error = "To date is required!"
//                if (isAllValid) holder.txtToDate.requestFocus()
//                isAllValid = false
//            }
//        }
//
//        return isAllValid
//    }
//
//
//
//
//
//    fun getUpdatedList(): List<GetInternshipDetailsData> = internships
//
//    inner class InternshipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val edtCompanyName: EditText = itemView.findViewById(R.id.edtCompanyName)
//        val edtRole: EditText = itemView.findViewById(R.id.edtRole)
//        val txtFromDate: TextView = itemView.findViewById(R.id.txtFromDate)
//        val txtToDate: TextView = itemView.findViewById(R.id.txtToDate)
//        val lblremove: ImageView = itemView.findViewById(R.id.lblremove)
//        val rcyAttachment: RecyclerView = itemView.findViewById(R.id.rcyAttachment)
//
//        val lblQuestionPick: TextView = itemView.findViewById(R.id.lblQuestionPick)
//
//    }
//}


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
//import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.IntershipAttachmentAdapter
//import com.vsca.vsnapvoicecollege.Interfaces.OnSoftSkillSelectedListener
//import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
//import com.vsca.vsnapvoicecollege.R
//import com.vsca.vsnapvoicecollege.Utils.CommonUtil
//
//class ResumeBuilderEditInternshipDetailsAdapter(
//    private val context: Context,
//    private val internships: MutableList<GetInternshipDetailsData>,
//    val listener: OnSoftSkillSelectedListener
//) : RecyclerView.Adapter<ResumeBuilderEditInternshipDetailsAdapter.InternshipViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InternshipViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.resume_builder_editskillset_internship_item, parent, false)
//        return InternshipViewHolder(view)
//
//    }
//
//    override fun onBindViewHolder(holder: InternshipViewHolder, position: Int) {
//        val item = internships[position]
//
//        // Set values in EditTexts/TextViews
//        holder.edtCompanyName.setText(item.companyName)
//        holder.edtRole.setText(item.designation)
//        holder.txtFromDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.from))
//        holder.txtToDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.to))
//
//        holder.lblQuestionPick.setOnClickListener {
//            val adapterPos = holder.bindingAdapterPosition
//            if (adapterPos != RecyclerView.NO_POSITION) {
//                listener.onAttachmentPick(adapterPos, internships)
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
//        // Handle FROM date selection
//        holder.txtFromDate.setOnClickListener {
//            val existingFromText = holder.txtFromDate.text.toString()
//            val existingToText = holder.txtToDate.text.toString()
//
//            CommonUtil.showDatePickerWithExistingDate(
//                context = holder.itemView.context,
//                existingDateStr = existingFromText,
//                minDate = null
//            ) { pickedFromDate, pickedFromMillis ->
//                val adapterPos = holder.bindingAdapterPosition
//                if (adapterPos != RecyclerView.NO_POSITION) {
//                    holder.txtFromDate.setText(pickedFromDate)
//                    internships[adapterPos].from = pickedFromDate
//
//                    if (!existingToText.isNullOrBlank()) {
//                        val toMillis = CommonUtil.parseDateToMillis(existingToText)
//                        if (toMillis != null && pickedFromMillis > toMillis) {
//                            holder.txtToDate.setText(pickedFromDate)
//                            internships[adapterPos].to = pickedFromDate
//                        }
//                    }
//                }
//            }
//        }
//
//        // Handle TO date selection
//        holder.txtToDate.setOnClickListener {
//            val fromDateStr = holder.txtFromDate.text.toString()
//            val minMillis = CommonUtil.parseDateToMillis(fromDateStr)
//
//            CommonUtil.showDatePickerWithExistingDate(
//                context = holder.itemView.context,
//                existingDateStr = holder.txtToDate.text.toString(),
//                minDate = minMillis
//            ) { pickedDate, _ ->
//                val adapterPos = holder.bindingAdapterPosition
//                if (adapterPos != RecyclerView.NO_POSITION) {
//                    holder.txtToDate.setText(pickedDate)
//                    internships[adapterPos].to = pickedDate
//                }
//            }
//        }
//
//        // Text change updates safely
//        holder.edtCompanyName.doAfterTextChanged {
//            val adapterPos = holder.bindingAdapterPosition
//            if (adapterPos != RecyclerView.NO_POSITION) {
//                internships[adapterPos].companyName = it.toString()
//            }
//        }
//
//        holder.edtRole.doAfterTextChanged {
//            val adapterPos = holder.bindingAdapterPosition
//            if (adapterPos != RecyclerView.NO_POSITION) {
//                internships[adapterPos].designation = it.toString()
//            }
//        }
//
//        // Remove internship
//        holder.lblremove.setOnClickListener {
//            val adapterPos = holder.bindingAdapterPosition
//            if (adapterPos != RecyclerView.NO_POSITION) {
//                internships.removeAt(adapterPos)
//                notifyItemRemoved(adapterPos)
//                notifyItemRangeChanged(adapterPos, internships.size)
//                listener.onInternshipListUpdated(internships)
//                CommonUtil.Remaining = CommonUtil.Remaining+1
//
//            }
//        }
//    }
//
////    Old Code
////    override fun onBindViewHolder(holder: InternshipViewHolder, position: Int) {
////        val item = internships[position]
////
////        // Set values in EditTexts/TextViews
////        holder.edtCompanyName.setText(item.companyName)
////        holder.edtRole.setText(item.designation)
////        holder.txtFromDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.from))
////        holder.txtToDate.setText(CommonUtil.convertTimeStampToCustomFormat(item.to))
////
////        holder.lblQuestionPick.setOnClickListener {
////            listener.onAttachmentPick(position,internships)
////        }
////
////
////        val attachments = item.file_path ?: return
////
////        if (attachments.isEmpty()) {
////            holder.rcyAttachment.visibility = View.GONE
////            holder.rcyAttachment.adapter = null
////            return
////        }
////
////        holder.rcyAttachment.visibility = View.VISIBLE
////        holder.rcyAttachment.isNestedScrollingEnabled = false
////
////        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
////            flexDirection = FlexDirection.ROW          // horizontal
////            flexWrap = FlexWrap.WRAP                  // wrap to next line
////            justifyContent = JustifyContent.FLEX_START
////            alignItems = AlignItems.FLEX_START
////        }
////
////        holder.rcyAttachment.layoutManager = flexboxLayoutManager
////
////        holder.rcyAttachment.adapter = IntershipAttachmentAdapter(
////            context,
////            attachments
////        ) { removePos ->
////            val adapterPos = holder.bindingAdapterPosition
////            if (adapterPos != RecyclerView.NO_POSITION) {
////                attachments.removeAt(removePos)
////                notifyItemChanged(adapterPos)
////            }
////        }
////
////        // Handle FROM date selection
////        holder.txtFromDate.setOnClickListener {
////            val existingFromText = holder.txtFromDate.text.toString()
////            val existingToText = holder.txtToDate.text.toString()
////
////            CommonUtil.showDatePickerWithExistingDate(
////                context = holder.itemView.context,
////                existingDateStr = existingFromText,
////                minDate = null
////            ) { pickedFromDate, pickedFromMillis ->
////                holder.txtFromDate.setText(pickedFromDate)
////                internships[position].from = pickedFromDate
////
////                // Check if To Date is set and is before new From Date
////                if (!existingToText.isNullOrBlank()) {
////                    val toMillis = CommonUtil.parseDateToMillis(existingToText)
////                    if (toMillis != null && pickedFromMillis > toMillis) {
////                        // Update To Date to match new From Date
////                        holder.txtToDate.setText(pickedFromDate)
////                        internships[position].to = pickedFromDate
////                    }
////                }
////            }
////        }
////
////
////        // Handle TO date selection (restrict to >= FROM date)
////        holder.txtToDate.setOnClickListener {
////            val fromDateStr = holder.txtFromDate.text.toString()
////            val minMillis = CommonUtil.parseDateToMillis(fromDateStr)
////
////            CommonUtil.showDatePickerWithExistingDate(
////                context = holder.itemView.context,
////                existingDateStr = holder.txtToDate.text.toString(),
////                minDate = minMillis
////            ) { pickedDate, _ ->
////                holder.txtToDate.setText(pickedDate)
////                internships[position].to = pickedDate
////            }
////        }
////
////        // Text change updates
////        holder.edtCompanyName.doAfterTextChanged {
////            internships[position].companyName = it.toString()
////        }
////
////        holder.edtRole.doAfterTextChanged {
////            internships[position].designation = it.toString()
////        }
////
////        holder.lblremove.setOnClickListener {
////            internships.removeAt(position)
////            notifyItemRemoved(position)
////            notifyItemRangeChanged(position, internships.size)
////            listener.onInternshipListUpdated(internships)
////        }
////
////
////
////    }
//
//
//    override fun getItemCount(): Int = internships.size
//
//    fun addItem() {
//        internships.add(GetInternshipDetailsData("", "", "", "",mutableListOf()))
//        notifyItemInserted(internships.size - 1)
//        listener.onInternshipListUpdated(internships)
//
//    }
//
//fun showValidationErrors(recyclerView: RecyclerView): Boolean {
//    var isAllValid = true
//
//    internships.forEachIndexed { index, item ->
//        val holder = recyclerView.findViewHolderForAdapterPosition(index) as? InternshipViewHolder ?: return@forEachIndexed
//
//        if (item.companyName.isBlank()) {
//            holder.edtCompanyName.error = "Company name is required!"
//            if (isAllValid) holder.edtCompanyName.requestFocus()
//            isAllValid = false
//        }
//        if (item.designation.isBlank()) {
//            holder.edtRole.error = "Role is required!"
//            if (isAllValid) holder.edtRole.requestFocus()
//            isAllValid = false
//        }
//        if (item.from=="") {
//            holder.txtFromDate.error = "From date is required!"
//            if (isAllValid) holder.txtFromDate.requestFocus()
//            isAllValid = false
//        }
//        if (item.to=="") {
//            holder.txtToDate.error = "To date is required!"
//            if (isAllValid) holder.txtToDate.requestFocus()
//            isAllValid = false
//        }
//    }
//
//    return isAllValid
//}
//
//
//
//
//
//    fun getUpdatedList(): List<GetInternshipDetailsData> = internships
//
//    inner class InternshipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val edtCompanyName: EditText = itemView.findViewById(R.id.edtCompanyName)
//        val edtRole: EditText = itemView.findViewById(R.id.edtRole)
//        val txtFromDate: TextView = itemView.findViewById(R.id.txtFromDate)
//        val txtToDate: TextView = itemView.findViewById(R.id.txtToDate)
//        val lblremove: ImageView = itemView.findViewById(R.id.lblremove)
//        val rcyAttachment: RecyclerView = itemView.findViewById(R.id.rcyAttachment)
//
//        val lblQuestionPick: TextView = itemView.findViewById(R.id.lblQuestionPick)
//
//    }
//}
