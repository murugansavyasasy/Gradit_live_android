package com.vsca.vsnapvoicecollege.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.DeviceDetails
import com.vsca.vsnapvoicecollege.databinding.ItemdeviceBinding

/**
 * onRemoveClick gives the activity the exact DeviceDetails that was tapped,
 * so the activity can call the remove API with it.
 */
class DeviceAdapter(
    private val devices: MutableList<DeviceDetails>,
    private val onRemoveClick: (device: DeviceDetails, position: Int) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    inner class DeviceViewHolder(val binding: ItemdeviceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ItemdeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding)
    }

    override fun getItemCount(): Int = devices.size

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]

        // TODO: change these field names to match your DeviceDetails model
        holder.binding.tvDeviceName.text = device.devicename
        holder.binding.tvDeviceTime.text = device.created_at

        holder.binding.btnRemove.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onRemoveClick(devices[pos], pos)
            }
        }
    }

    /** Call after the remove API succeeds. */
    fun removeDevice(device: DeviceDetails) {
        val index = devices.indexOf(device)
        if (index != -1) {
            devices.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun getCount(): Int = devices.size
}