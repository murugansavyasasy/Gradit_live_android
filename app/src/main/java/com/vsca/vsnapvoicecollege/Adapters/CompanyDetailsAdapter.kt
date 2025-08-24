package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vsca.vsnapvoicecollege.Model.CompanyDetail
import com.vsca.vsnapvoicecollege.R

class CompanyDetailsAdapter(
    var isCompanyDetail: List<CompanyDetail>,
    private val context: Context?,
) : RecyclerView.Adapter<CompanyDetailsAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgCompanyLogo: ImageView = itemView!!.findViewById(R.id.imgCompanyLogo)
        val lblCompanyName: TextView = itemView!!.findViewById(R.id.lblCompanyName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.company_details_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = isCompanyDetail[position]

        holder.lblCompanyName.text = data.companyName
        Glide.with(context!!)
            .load(data.logo)
            .into(holder.imgCompanyLogo!!)

    }

    override fun getItemCount(): Int {
        return isCompanyDetail.size

    }
}



