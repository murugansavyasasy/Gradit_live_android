package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.Adapters.EventsAdapter
import com.vsca.vsnapvoicecollege.Model.GetEventDetailsData


interface EventClickListener {
    fun oneventClick(holder: EventsAdapter.MyViewHolder, item: GetEventDetailsData)
}