package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.EducationItem
import com.vsca.vsnapvoicecollege.R
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.vsca.vsnapvoicecollege.Model.GetEducationalDetailsData
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class ResumeBuilderAcademicDetailsAdapter(private val items: List<GetEducationalDetailsData>) :
    RecyclerView.Adapter<ResumeBuilderAcademicDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lblCatoryName: TextView = view.findViewById(R.id.lblCatoryName)
        val lblValue: TextView = view.findViewById(R.id.lblValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resume_builder_education_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.lblCatoryName.text = item.classDegree
        holder.lblValue.text = item.percentage

        val drawable = ContextCompat
            .getDrawable(holder.itemView.context, R.drawable.dark_green_radius)
            ?.mutate()

        if (drawable is GradientDrawable) {
            val percentage = item.percentage.toFloatOrNull() ?: -1f
            val color = when {
                percentage >= 85f -> R.color.light_green
                percentage >= 70f -> R.color.light_orange
                percentage >= 50f -> R.color.light_yellow
                percentage in 0f..49.99f -> R.color.light_red
                else -> R.color.very_light_gray // fallback for null or invalid values
            }

            drawable.setColor(ContextCompat.getColor(holder.itemView.context, color))
            holder.lblValue.background = drawable
        }


    }
}
