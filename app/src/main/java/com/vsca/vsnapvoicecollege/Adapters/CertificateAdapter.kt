package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData
import com.vsca.vsnapvoicecollege.databinding.ItemCertificateBinding

class CertificateAdapter(
    private val list: List<GetCertificateDetailsData>,
    private val onCheckedChange: (GetCertificateDetailsData, Boolean) -> Unit,
    private val onHeaderCheckSync: (Boolean) -> Unit
) : RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder>() {


    private val checkedItems = mutableSetOf<GetCertificateDetailsData>().apply {
        addAll(list)
    }

    inner class CertificateViewHolder(val binding: ItemCertificateBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCertificateBinding.inflate(inflater, parent, false)
        return CertificateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
        val item = list[position]
        holder.binding.tvInstitute.text = item.institute

        holder.binding.cbInstitute.setOnCheckedChangeListener(null)
        holder.binding.cbInstitute.isChecked = checkedItems.contains(item)

        holder.binding.cbInstitute.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkedItems.add(item)
            } else {
                checkedItems.remove(item)
            }

            onCheckedChange(item, isChecked)
            onHeaderCheckSync(checkedItems.size == list.size)
        }

    }

    override fun getItemCount(): Int = list.size

    fun setAllChecked(checked: Boolean) {
        checkedItems.clear()
        if (checked) {
            checkedItems.addAll(list)
        }
        notifyDataSetChanged()
        onHeaderCheckSync(checkedItems.size == list.size)
    }



    fun getCheckedItems(): List<GetCertificateDetailsData> = checkedItems.toList()
}
