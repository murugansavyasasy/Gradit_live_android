package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.VenueRequestData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.databinding.ItemVenueRequestBinding

/** Request List cards (api/AppDetailsBal/venuerequests). */
class VenueRequestAdapter : RecyclerView.Adapter<VenueRequestAdapter.Holder>() {

    private val items = ArrayList<VenueRequestData>()

    /** false = My Requests (unchanged), true = Approve Venue (requester row + Reject/Approve). */
    var approveMode = false

    // bookingids whose description is expanded via "Read more"
    private val expanded = HashSet<Int>()

    fun submit(list: List<VenueRequestData>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemVenueRequestBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemVenueRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        val b = holder.binding
        val context = b.root.context
        val style = statusStyle(item.status)

        // Title (struck through + grey when cancelled)
        b.lblTitle.text = item.title.orEmpty()
        if (style.isCancelled) {
            b.lblTitle.paintFlags = b.lblTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            b.lblTitle.setTextColor(Color.parseColor("#94A3B8"))
        } else {
            b.lblTitle.paintFlags = b.lblTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            // Expired: muted title (not struck through, the user didn't cancel it)
            b.lblTitle.setTextColor(Color.parseColor(if (style.isExpired) "#64748B" else "#0F172A"))
        }

        // Accent bar + venue colour square (grey bar for inactive cancelled / expired cards)
        val venueColor = parseColor(item.venuecolor, Color.parseColor("#94A3B8"))
        b.viewAccent.setBackgroundColor(if (style.isInactive) Color.parseColor("#CBD5E1") else venueColor)
        b.viewVenueColor.background = rounded(context, venueColor, null, 2f)

        // Status chip
        b.lblStatus.text = item.statuslabel.orEmpty()
        b.lblStatus.setTextColor(style.text)
        b.lblStatus.background = rounded(context, style.chipFill, style.chipStroke, 999f)
        val chipIcon = ContextCompat.getDrawable(context, style.icon)?.mutate()
        chipIcon?.setTint(style.text)
        b.lblStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(chipIcon, null, null, null)

        // Date / time / venue
        b.lblDate.text = item.displaydate.orEmpty()
        b.lblTime.text = item.displaytime.orEmpty()
        val venueName = item.venuename.orEmpty()
        val location = item.location.orEmpty()
        b.lblVenue.text = android.text.SpannableStringBuilder().apply {
            append(venueName, android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0)
            setSpan(
                android.text.style.ForegroundColorSpan(Color.parseColor("#1E293B")),
                0, venueName.length, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (location.isNotBlank()) append(", ").append(location)
        }

        // Description with Read more / Read less
        bindDescription(b, item)

        // Event message box
        b.layoutInfo.background = rounded(context, style.infoFill, null, 10f)
        b.imgInfo.setImageResource(style.icon)
        b.imgInfo.imageTintList = ColorStateList.valueOf(style.text)
        b.lblInfoMessage.setTextColor(style.text)
        b.lblInfoMessage.text = item.eventmessage.orEmpty()
        b.lblInfoMessage.visibility = if (item.eventmessage.isNullOrBlank()) View.GONE else View.VISIBLE

        val decidedBy = decidedText(item)
        val detail = when (style.key) {
            STATUS_PENDING -> if (approveMode) {
                // Approve Venue: who it is waiting for
                when {
                    item.canapprove == true -> context.getString(R.string.txt_waiting_your_review)
                    item.needscollegeadmin == true -> context.getString(R.string.txt_waiting_college_admin)
                    else -> context.getString(R.string.txt_waiting_dept_or_college_admin)
                }
            } else {
                item.approver?.takeIf { it.isNotBlank() }
                    ?.let { context.getString(R.string.txt_approver, it) }
            }
            STATUS_APPROVED -> decidedBy?.let { context.getString(R.string.txt_approved_by, it) }
            STATUS_REJECTED -> decidedBy?.let { context.getString(R.string.txt_rejected_by, it) }
            STATUS_CANCELLED -> decidedBy?.let { context.getString(R.string.txt_cancelled_by, it) }
            // Expired: nobody decided before the event time (same on both lists)
            STATUS_EXPIRED -> decidedBy ?: context.getString(R.string.txt_expired_no_decision)
            else -> null
        }
        b.lblInfoDetail.text = detail.orEmpty()
        b.lblInfoDetail.visibility = if (detail.isNullOrBlank()) View.GONE else View.VISIBLE

        // Reason (rejected / cancelled with remarks)
        val remarks = item.remarks?.trim().orEmpty()
        if ((style.key == STATUS_REJECTED || style.key == STATUS_CANCELLED) && remarks.isNotEmpty()) {
            b.layoutRemarks.visibility = View.VISIBLE
            b.lblRemarksLabel.setText(
                if (style.key == STATUS_REJECTED) R.string.txt_reason_rejection
                else R.string.txt_reason_cancelling
            )
            b.lblRemarks.text = remarks
            b.lblRemarks.background = rounded(context, Color.WHITE, null, 8f)
        } else {
            b.layoutRemarks.visibility = View.GONE
        }

        // Footer (booking id is not shown)
        b.lblRequestedOn.text = item.requestedon?.takeIf { it.isNotBlank() }
            ?.let { context.getString(R.string.txt_requested_on, it) }.orEmpty()

        // Approve Venue: requester + Reject/Approve (only where the API allows it)
        bindApproveSection(b, item, style)

        // "⋮" menu: only waiting (PENDING) requests in My Requests can be edited or cancelled
        if (!approveMode && style.key == STATUS_PENDING) {
            b.imgMore.visibility = View.VISIBLE
            b.imgMore.setOnClickListener { showMenu(it, item) }
        } else {
            b.imgMore.visibility = View.GONE
            b.imgMore.setOnClickListener(null)
        }
    }

    /** Edit request / Cancel request popup, anchored under the "⋮" button. */
    private fun showMenu(anchor: View, item: VenueRequestData) {
        val context = anchor.context
        val content = LayoutInflater.from(context).inflate(R.layout.popup_venue_request_menu, null)
        val popup = PopupWindow(
            content,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popup.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(Color.TRANSPARENT))
        popup.elevation = 8 * context.resources.displayMetrics.density
        popup.isOutsideTouchable = true

        content.findViewById<View>(R.id.menuEditRequest).setOnClickListener {
            popup.dismiss()
            listener?.onEditRequest(item)
        }
        content.findViewById<View>(R.id.menuCancelRequest).setOnClickListener {
            popup.dismiss()
            listener?.onCancelRequest(item)
        }

        // Right-align the popup with the "⋮" button
        content.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        popup.showAsDropDown(anchor, anchor.width - content.measuredWidth, 0)
    }

    private fun bindApproveSection(b: ItemVenueRequestBinding, item: VenueRequestData, style: StatusStyle) {
        if (!approveMode) {
            b.layoutRequester.visibility = View.GONE
            b.layoutDecision.visibility = View.GONE
            return
        }

        val name = item.requestedby?.trim().orEmpty()
        if (name.isEmpty()) {
            b.layoutRequester.visibility = View.GONE
        } else {
            b.layoutRequester.visibility = View.VISIBLE
            b.lblRequesterInitials.text = initials(name)
            b.lblRequesterName.text = name
            b.lblRequesterDepartment.text = item.department.orEmpty()
            b.lblRequesterDepartment.visibility =
                if (item.department.isNullOrBlank()) View.GONE else View.VISIBLE
        }

        val canApprove = style.key == STATUS_PENDING && item.canapprove == true
        val canReject = style.key == STATUS_PENDING && item.canreject == true
        b.layoutDecision.visibility = if (canApprove || canReject) View.VISIBLE else View.GONE
        b.btnApproveRequest.visibility = if (canApprove) View.VISIBLE else View.GONE
        b.btnRejectRequest.visibility = if (canReject) View.VISIBLE else View.GONE
        b.spaceDecision.visibility = if (canApprove && canReject) View.VISIBLE else View.GONE
        b.btnApproveRequest.setOnClickListener { listener?.onApproveRequest(item) }
        b.btnRejectRequest.setOnClickListener { listener?.onRejectRequest(item) }
    }

    /** "Sample Staff 1" -> "SS", "VRCR" -> "V". */
    private fun initials(name: String): String {
        val words = name.split(" ").filter { it.isNotBlank() && it[0].isLetter() }
        return when {
            words.size >= 2 -> "${words[0][0]}${words[1][0]}"
            words.size == 1 -> "${words[0][0]}"
            else -> name.take(1)
        }.uppercase()
    }

    /** Card actions handled by the screen. Approve/Reject are only used in Approve Venue. */
    interface RequestActionListener {
        fun onEditRequest(item: VenueRequestData)
        fun onCancelRequest(item: VenueRequestData)
        fun onApproveRequest(item: VenueRequestData) {}
        fun onRejectRequest(item: VenueRequestData) {}
    }

    var listener: RequestActionListener? = null

    private fun bindDescription(b: ItemVenueRequestBinding, item: VenueRequestData) {
        val description = item.description?.trim().orEmpty()
        val id = item.bookingid ?: -1
        b.lblDescription.visibility = if (description.isEmpty()) View.GONE else View.VISIBLE
        b.lblDescription.text = description
        b.lblReadMore.visibility = View.GONE
        if (description.isEmpty()) return

        val isExpanded = expanded.contains(id)
        b.lblDescription.maxLines = if (isExpanded) Int.MAX_VALUE else COLLAPSED_LINES

        // Show the toggle only when the text is longer than the collapsed lines
        b.lblDescription.post {
            val layout = b.lblDescription.layout ?: return@post
            val truncated = layout.lineCount > COLLAPSED_LINES ||
                    (layout.lineCount > 0 && layout.getEllipsisCount(layout.lineCount - 1) > 0)
            if (isExpanded || truncated) {
                b.lblReadMore.visibility = View.VISIBLE
                b.lblReadMore.setText(if (isExpanded) R.string.txt_read_less else R.string.txt_read_more)
            }
        }
        b.lblReadMore.setOnClickListener {
            if (expanded.contains(id)) expanded.remove(id) else expanded.add(id)
            val pos = items.indexOfFirst { (it.bookingid ?: -1) == id }
            if (pos >= 0) notifyItemChanged(pos)
        }
    }

    /** "tech (College Admin), 16 Nov 2026, 4:16 PM" from decidedby / decidedbyrole / decidedon. */
    private fun decidedText(item: VenueRequestData): String? {
        val by = item.decidedby?.trim().orEmpty()
        if (by.isEmpty()) return null
        val role = item.decidedbyrole?.trim().orEmpty()
        val on = item.decidedon?.trim().orEmpty()
        val sb = StringBuilder(by)
        if (role.isNotEmpty()) sb.append(" (").append(role).append(")")
        if (on.isNotEmpty()) sb.append(", ").append(on)
        return sb.toString()
    }

    private fun parseColor(value: String?, fallback: Int): Int =
        try {
            if (value.isNullOrBlank()) fallback else Color.parseColor(value.trim())
        } catch (e: IllegalArgumentException) {
            fallback
        }

    private fun rounded(context: Context, fill: Int, stroke: Int?, radiusDp: Float): GradientDrawable {
        val density = context.resources.displayMetrics.density
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radiusDp * density
            setColor(fill)
            if (stroke != null) setStroke((1 * density).toInt(), stroke)
        }
    }

    class StatusStyle(
        val key: String,
        val icon: Int,
        val text: Int,
        val chipFill: Int,
        val chipStroke: Int,
        val infoFill: Int
    ) {
        val isCancelled get() = key == STATUS_CANCELLED
        val isExpired get() = key == STATUS_EXPIRED
        val isInactive get() = isCancelled || isExpired
    }

    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_APPROVED = "APPROVED"
        const val STATUS_REJECTED = "REJECTED"
        const val STATUS_CANCELLED = "CANCELLED"
        const val STATUS_EXPIRED = "EXPIRED"
        private const val COLLAPSED_LINES = 2

        /** Colours/icon per status; an unknown status gets a neutral grey look (not struck through). */
        fun statusStyle(status: String?): StatusStyle = when (status?.trim()?.uppercase()) {
            STATUS_PENDING -> StatusStyle(
                STATUS_PENDING, R.drawable.ic_req_clock, Color.parseColor("#B45309"),
                Color.parseColor("#FEFCE8"), Color.parseColor("#FDE68A"), Color.parseColor("#FEFBEA")
            )
            STATUS_APPROVED -> StatusStyle(
                STATUS_APPROVED, R.drawable.ic_req_check, Color.parseColor("#047857"),
                Color.parseColor("#ECFDF5"), Color.parseColor("#A7F3D0"), Color.parseColor("#ECFDF5")
            )
            STATUS_REJECTED -> StatusStyle(
                STATUS_REJECTED, R.drawable.ic_req_cross, Color.parseColor("#BE123C"),
                Color.parseColor("#FFF1F2"), Color.parseColor("#FECDD3"), Color.parseColor("#FFF1F2")
            )
            STATUS_CANCELLED -> StatusStyle(
                STATUS_CANCELLED, R.drawable.ic_req_ban, Color.parseColor("#475569"),
                Color.parseColor("#F1F5F9"), Color.parseColor("#CBD5E1"), Color.parseColor("#F1F5F9")
            )
            STATUS_EXPIRED -> StatusStyle(
                // Same grey look as Cancelled
                STATUS_EXPIRED, R.drawable.ic_req_expired, Color.parseColor("#475569"),
                Color.parseColor("#F1F5F9"), Color.parseColor("#CBD5E1"), Color.parseColor("#F1F5F9")
            )
            else -> StatusStyle(
                "OTHER", R.drawable.ic_req_clock, Color.parseColor("#475569"),
                Color.parseColor("#F1F5F9"), Color.parseColor("#CBD5E1"), Color.parseColor("#F1F5F9")
            )
        }
    }
}
