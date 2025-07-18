package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData
import com.vsca.vsnapvoicecollege.databinding.ItemCertificateBinding

class CertificateAdapter(private val list: List<GetCertificateDetailsData>) :
    RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder>() {

    inner class CertificateViewHolder(val binding: ItemCertificateBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCertificateBinding.inflate(inflater, parent, false)
        return CertificateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvCourseName.text = item.courseName
        holder.binding.tvInstitute.text = item.institute
        holder.binding.tvDuration.text = item.duration
    }

    override fun getItemCount(): Int = list.size
}
