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

class EventPlacementAdapter (
    var isPlacementData: List<PlacementEventData>,
    private val context: Context?,
) : RecyclerView.Adapter<EventPlacementAdapter.MyViewHolder>() {


    var isCompanyDetailsAdapter: CompanyDetailsAdapter? = null
    var isSelectionProcessAdapter: SelectionProcessAdapter? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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
        val data = isPlacementData[position]

        holder.tvTitle!!.text = data.eventTitle
        holder.tvTime!!.text = data.eventTime
        holder.tvDescription!!.text = data.eventAbout
        holder.tvMode!!.text = data.eventMode
        holder.tvLocation!!.text = data.eventVenue
        holder.tvCourses!!.text = data.eventEligibleCourse
        holder.tvCriteria!!.text = data.eventEligibleCriteria

        isCompanyDetailsAdapter = CompanyDetailsAdapter(data.eventCompanyDetails, context)
        val gridLayoutManager = GridLayoutManager(context, 3)
        holder.rcyCompanyList!!.layoutManager = gridLayoutManager
        holder.rcyCompanyList!!.adapter = isCompanyDetailsAdapter
        isCompanyDetailsAdapter!!.notifyDataSetChanged()


        isSelectionProcessAdapter = SelectionProcessAdapter(data.eventSelectionProcess, context)
        val isGridLayoutManager = GridLayoutManager(context, 2)
        holder.rcySelectionProcess!!.layoutManager = isGridLayoutManager
        holder.rcySelectionProcess!!.adapter = isSelectionProcessAdapter
        isSelectionProcessAdapter!!.notifyDataSetChanged()





    }

    override fun getItemCount(): Int {
        return isPlacementData.size

    }
}



