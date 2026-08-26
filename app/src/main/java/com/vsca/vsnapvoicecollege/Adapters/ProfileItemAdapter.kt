package com.vsca.vsnapvoicecollege.Adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Interfaces.ProfileClickListener
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.R

class ProfileItemAdapter(
    private val profiles: ArrayList<LoginDetails>,
    private val listener: ProfileClickListener
) : RecyclerView.Adapter<ProfileItemAdapter.ItemViewHolder>() {

    // 10 different colors – cycles for 11th, 12th... items
    private val avatarColors = intArrayOf(
        Color.parseColor("#9B59B6"), // Purple
        Color.parseColor("#3498DB"), // Blue
        Color.parseColor("#95A5A6"), // Gray
        Color.parseColor("#F39C12"), // Orange
        Color.parseColor("#1ABC9C"), // Teal
        Color.parseColor("#E74C3C"), // Red
        Color.parseColor("#2ECC71"), // Green
        Color.parseColor("#E67E22"), // Dark Orange
        Color.parseColor("#8E44AD"), // Deep Purple
        Color.parseColor("#16A085")  // Dark Teal
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.role_list_design_rewamp, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(profiles[position], position)
    }

    override fun getItemCount(): Int = profiles.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val lblAvatar: TextView = itemView.findViewById(R.id.lblAvatar)
        private val lblMemberName: TextView = itemView.findViewById(R.id.lblMemberName)
        private val lblCollegeName: TextView = itemView.findViewById(R.id.lblCollegeName)

        private val rytOverAll: View = itemView.findViewById(R.id.rytOverAll)

        fun bind(data: LoginDetails, position: Int) {
            // Name
            lblMemberName.text = data.membername

            // ===== Circular Avatar with first letter + cycling color =====
            val firstLetter = data.membername
                ?.trim()
                ?.firstOrNull()
                ?.uppercaseChar()
                ?.toString() ?: "?"

            lblAvatar.text = firstLetter

            // Color cycles every 10 items (0→9, then back to 0)
            val colorIndex = position % avatarColors.size
            val circleDrawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(avatarColors[colorIndex])
            }
            lblAvatar.background = circleDrawable

            lblCollegeName.text = listOf(
                data.colgname,
                data.coursename,
                data.yearname,
                data.sectionname,
                data.deptname,
                data.semestername
            ).filter { !it.isNullOrBlank() }
                .joinToString(" · ")

            // Click
            rytOverAll.setOnClickListener {
                listener.onProfileClick(data)
            }
        }
    }
}