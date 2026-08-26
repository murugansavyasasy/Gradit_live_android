package com.vsca.vsnapvoicecollege.Adapters


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Interfaces.ProfileClickListener
import com.vsca.vsnapvoicecollege.Model.ProfileGroup
import com.vsca.vsnapvoicecollege.R

class LoginChooseRolesAdapter(
    private var groups: ArrayList<ProfileGroup>,
    private val context: Context,
    private val profileClickListener: ProfileClickListener
) : RecyclerView.Adapter<LoginChooseRolesAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.role_group_card, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount(): Int = groups.size

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val lblTitle: TextView = itemView.findViewById(R.id.lblHeaderTitle)
        private val lblCount: TextView = itemView.findViewById(R.id.lblHeaderCount)
        private val imgArrow: ImageView = itemView.findViewById(R.id.imgArrow)
        private val rytHeader: View = itemView.findViewById(R.id.rytHeader)
        private val ImgHeader: ImageView = itemView.findViewById(R.id.ImgHeader)
        private val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.innerRecyclerView)

        fun bind(group: ProfileGroup) {
            lblTitle.text = group.title
            lblCount.text = "(${group.count})"

            imgArrow.rotation = if (group.isExpanded) 180f else 0f

            when (group.priority.lowercase()) {

                "p1" -> {
                    ImgHeader.setImageResource(R.drawable.person_sheild_icon)
                    ImgHeader.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#1A7E57C2"))
                }

                "p4" -> {
                    ImgHeader.setImageResource(R.drawable.student_hat_icon)
                    ImgHeader.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#1A3A5C91"))
                }

                else -> {
                    ImgHeader.setImageResource(R.drawable.student_hat_icon)
                    ImgHeader.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#1A3A5C91"))
                }
            }

            if (group.isExpanded) {
                innerRecyclerView.visibility = View.VISIBLE

                val innerAdapter = ProfileItemAdapter(group.profile, profileClickListener)
                innerRecyclerView.layoutManager = LinearLayoutManager(context)
                innerRecyclerView.adapter = innerAdapter
                innerRecyclerView.isNestedScrollingEnabled = false
            } else {
                innerRecyclerView.visibility = View.GONE
            }

            rytHeader.setOnClickListener {
                group.isExpanded = !group.isExpanded
                notifyItemChanged(bindingAdapterPosition)
            }
        }
    }

}