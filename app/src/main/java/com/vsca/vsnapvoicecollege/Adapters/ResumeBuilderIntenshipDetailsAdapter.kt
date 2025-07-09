package com.vsca.vsnapvoicecollege.Adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class ResumeBuilderIntenshipDetailsAdapter(private val items: List<GetInternshipDetailsData>) :
    RecyclerView.Adapter<ResumeBuilderIntenshipDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lblCompanyName: TextView = view.findViewById(R.id.lblCompanyName)
        val lblDuration: TextView = view.findViewById(R.id.lblDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resume_builder_skillset_internship_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.lblCompanyName.text = item.companyName
        holder.lblDuration.text = CommonUtil.convertTimeStampToCustomFormat(item.from) +" to "+CommonUtil.convertTimeStampToCustomFormat(item.to)
    }
}
