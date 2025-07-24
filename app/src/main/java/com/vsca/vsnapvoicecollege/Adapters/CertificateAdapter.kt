package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.CertificateFormattedData
import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData
import com.vsca.vsnapvoicecollege.R

class CertificateAdapter(
    private val items: List<CertificateFormattedData>,
    private val onSelectionChanged: (List<CertificateFormattedData>) -> Unit
) : RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder>() {

    inner class CertificateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbInstitute: CheckBox = itemView.findViewById(R.id.cbInstitute)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_certificate, parent, false)
        return CertificateViewHolder(view)
    }

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
        val item = items[position]

        holder.cbInstitute.setOnCheckedChangeListener(null)
        holder.cbInstitute.text = item.institute
        holder.cbInstitute.isChecked = item.isChecked

        holder.cbInstitute.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
            onSelectionChanged(items.filter { it.isChecked })
        }
    }

    override fun getItemCount(): Int = items.size

    fun setAllChecked(isChecked: Boolean) {
        items.forEach { it.isChecked = isChecked }
        notifyDataSetChanged()
        onSelectionChanged(items.filter { it.isChecked })
    }
}


