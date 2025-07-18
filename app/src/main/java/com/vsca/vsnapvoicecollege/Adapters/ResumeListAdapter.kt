package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.GetResumeTitleData
import com.vsca.vsnapvoicecollege.R

class ResumeListAdapter(
    private val context: Context,
    private val resumeList: List<GetResumeTitleData>,
    private val onItemClick: (GetResumeTitleData) -> Unit
) : RecyclerView.Adapter<ResumeListAdapter.ResumeViewHolder>() {

    inner class ResumeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lblFileName: TextView = view.findViewById(R.id.lblFileName)
        val lbldefault: TextView = view.findViewById(R.id.lbldefault)

        init {
            view.setOnClickListener {
                onItemClick(resumeList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResumeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.see_my_resume_item, parent, false)
        return ResumeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResumeViewHolder, position: Int) {
        val resume = resumeList[position]
        holder.lblFileName.text = resume.title
        holder.lbldefault.visibility = if (resume.placementOfficer) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int = resumeList.size
}
