package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.Adapters.ChatSenderSide_Adapter
import com.vsca.vsnapvoicecollege.Model.Senderside_Chatdata

interface ChatClickListener {

    fun onchatClick(holder: ChatSenderSide_Adapter.MyViewHolder, item: Senderside_Chatdata)
}