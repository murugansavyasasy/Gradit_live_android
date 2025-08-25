package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.PlacementEventData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class EventPlacementAdapter (
    var isPlacementData: List<PlacementEventData>,
    private val context: Context?,
) : RecyclerView.Adapter<EventPlacementAdapter.MyViewHolder>() {


    private lateinit var isCompanyDetailsAdapter: CompanyDetailsAdapter
    private lateinit var isSelectionProcessAdapter: SelectionProcessAdapter

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        val tvEventTag: TextView = itemView!!.findViewById(R.id.tvEventTag)
        val tvTitle: TextView = itemView!!.findViewById(R.id.tvTitle)
        val tvTime: TextView = itemView!!.findViewById(R.id.tvTime)
        val tvDescription: TextView = itemView!!.findViewById(R.id.tvDescription)
        val tvMode: TextView = itemView!!.findViewById(R.id.tvMode)
        val tvLocation: TextView = itemView!!.findViewById(R.id.tvLocation)
        val rcyCompanyList: RecyclerView = itemView!!.findViewById(R.id.rcyCompanyList)
        val rcySelectionProcess: RecyclerView = itemView!!.findViewById(R.id.rcySelectionProcess)
        val tvCourses: TextView = itemView!!.findViewById(R.id.tvCourses)
        val tvCriteria: TextView = itemView!!.findViewById(R.id.tvCriteria)
        val tvSelection: TextView = itemView!!.findViewById(R.id.tvSelection)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.placement_event_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = isPlacementData!![position]

        val (day, month) = CommonUtil.formatDateForBinding(data.eventDate)
        holder.tvDay.text = day
        holder.tvMonth.text = month


        holder.tvTitle!!.text = data.eventTitle
        holder.tvTime!!.text =CommonUtil.convertTo12HourFormat(data.eventTime)
        holder.tvDescription!!.text = data.aboutEvent
        holder.tvMode!!.text = data.modeOfEvent
        holder.tvLocation!!.text = data.venue
        holder.tvCourses!!.text = data.eligibleCourses.joinToString(", ")
        holder.tvCriteria!!.text = data.eligibleCriteria


        val companyList = data.companyDetails ?: emptyList()
        if (companyList.isNotEmpty()) {
            isCompanyDetailsAdapter = CompanyDetailsAdapter(companyList, context!!)
            holder.rcyCompanyList.layoutManager = GridLayoutManager(context, 3)
            holder.rcyCompanyList.adapter = isCompanyDetailsAdapter
        } else {
            holder.rcyCompanyList.adapter = null
        }

        // ---- SelectionProcessAdapter ----
        val selectionList = data.selectionProcess ?: emptyList()
        if (selectionList.isNotEmpty()) {
            isSelectionProcessAdapter = SelectionProcessAdapter(selectionList, context!!)
            holder.rcySelectionProcess.layoutManager = GridLayoutManager(context, 2)
            holder.rcySelectionProcess.adapter = isSelectionProcessAdapter
        } else {
            holder.rcySelectionProcess.adapter = null
        }

//        isCompanyDetailsAdapter = CompanyDetailsAdapter(data.companyDetails!!, context)
//        holder.rcyCompanyList!!.layoutManager = GridLayoutManager(context, 3)
//        holder.rcyCompanyList!!.adapter = isCompanyDetailsAdapter
//        isCompanyDetailsAdapter!!.notifyDataSetChanged()
//
//
//        isSelectionProcessAdapter = SelectionProcessAdapter(data.selectionProcess!!, context)
//        holder.rcySelectionProcess!!.layoutManager = GridLayoutManager(context, 2)
//        holder.rcySelectionProcess!!.adapter = isSelectionProcessAdapter
//        isSelectionProcessAdapter!!.notifyDataSetChanged()


    }

    override fun getItemCount(): Int {
        return isPlacementData!!.size

    }
}



