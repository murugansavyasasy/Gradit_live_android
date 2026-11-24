package com.vsca.vsnapvoicecollege.Adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.PlacementFeedbackItem
import com.vsca.vsnapvoicecollege.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Placement Feedback cards: topic, Replied pill, posted date, message, officer reply. */
class PlacementFeedbackAdapter : RecyclerView.Adapter<PlacementFeedbackAdapter.Holder>() {

    private val items = ArrayList<PlacementFeedbackItem>()

    fun submit(list: List<PlacementFeedbackItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val topic: TextView = view.findViewById(R.id.lblFeedbackTopic)
        val replied: TextView = view.findViewById(R.id.lblFeedbackReplied)
        val posted: TextView = view.findViewById(R.id.lblFeedbackPosted)
        val message: TextView = view.findViewById(R.id.lblFeedbackMessage)
        val replyBox: View = view.findViewById(R.id.layoutFeedbackReply)
        val replyFrom: TextView = view.findViewById(R.id.lblReplyFrom)
        val replyDate: TextView = view.findViewById(R.id.lblReplyDate)
        val replyMessage: TextView = view.findViewById(R.id.lblReplyMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_placement_feedback, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.topic.text = item.topic.orEmpty()
        holder.message.text = item.message.orEmpty()
        holder.message.visibility = if (item.message.isNullOrBlank()) View.GONE else View.VISIBLE

        // "Posted on 20/11/2026" with a calendar icon
        holder.posted.text = context.getString(R.string.txt_pf_posted_on, formatDate(item.createdOn))
        val calendar = ContextCompat.getDrawable(context, R.drawable.ic_cr_calendar)?.mutate()
        val size = (15 * context.resources.displayMetrics.density).toInt()
        calendar?.setBounds(0, 0, size, size)
        holder.posted.setCompoundDrawablesRelative(calendar, null, null, null)
        TextViewCompat.setCompoundDrawableTintList(holder.posted, ColorStateList.valueOf(Color.parseColor("#5E5E66")))

        // Replied pill (status "replied" or a reply present)
        val reply = item.lastReply
        val hasReply = item.status.equals("replied", ignoreCase = true) ||
                !reply?.message.isNullOrBlank()
        holder.replied.visibility = if (hasReply) View.VISIBLE else View.GONE
        if (hasReply) {
            val check = ContextCompat.getDrawable(context, R.drawable.ic_cr_check)?.mutate()
            val checkSize = (12 * context.resources.displayMetrics.density).toInt()
            check?.setBounds(0, 0, checkSize, checkSize)
            holder.replied.setCompoundDrawablesRelative(check, null, null, null)
            TextViewCompat.setCompoundDrawableTintList(holder.replied, ColorStateList.valueOf(Color.parseColor("#1E7B38")))
        }

        // Officer reply box (lastReply)
        if (reply != null && !reply.message.isNullOrBlank()) {
            holder.replyBox.visibility = View.VISIBLE
            holder.replyFrom.text = replyFromLabel(holder, reply.from)
            holder.replyDate.text = formatDateTime(reply.createdOn)
            holder.replyMessage.text = reply.message
        } else {
            holder.replyBox.visibility = View.GONE
        }
    }

    /** "officer" -> "Placement officer"; anything else shown as sent (capitalised). */
    private fun replyFromLabel(holder: Holder, from: String?): String {
        val context = holder.itemView.context
        return when {
            from.isNullOrBlank() || from.equals("officer", ignoreCase = true) ->
                context.getString(R.string.txt_pf_placement_officer)
            else -> from.replaceFirstChar { it.uppercase() }
        }
    }

    companion object {
        /** API dates are ISO UTC ("2026-11-23T10:15:30.000Z"); shown in the phone's time zone. */
        private fun parseIso(value: String?): Date? {
            if (value.isNullOrBlank()) return null
            for (pattern in listOf("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'")) {
                try {
                    return SimpleDateFormat(pattern, Locale.ENGLISH).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }.parse(value)
                } catch (e: Exception) {
                    // try the next pattern
                }
            }
            return null
        }

        /** "20/11/2026" (or the raw value if it cannot be read). */
        fun formatDate(value: String?): String =
            parseIso(value)?.let { SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(it) }
                ?: value.orEmpty()

        /** "23/11/2026 · 4:32 PM" */
        fun formatDateTime(value: String?): String =
            parseIso(value)?.let {
                SimpleDateFormat("dd/MM/yyyy · h:mm a", Locale.ENGLISH).format(it)
            } ?: value.orEmpty()
    }
}
