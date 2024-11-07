package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.Adapters.CommunicationAdapter
import com.vsca.vsnapvoicecollege.Model.GetCommunicationDetails

interface communicationListener {

    fun oncommunicationClick(
        holder: CommunicationAdapter.MyViewHolder,
        item: GetCommunicationDetails
    )

}