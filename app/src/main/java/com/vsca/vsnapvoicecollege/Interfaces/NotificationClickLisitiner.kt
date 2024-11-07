package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.Adapters.NotificationAdapter
import com.vsca.vsnapvoicecollege.Model.GetNotificationDetails

interface NotificationClickLisitiner {

    fun onNotificationClickListener(
        holder: NotificationAdapter.MyViewHolder,
        item: GetNotificationDetails
    )
}