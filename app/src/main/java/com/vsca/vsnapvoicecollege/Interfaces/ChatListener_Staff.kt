package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.Adapters.Chat_AdapterStaff
import com.vsca.vsnapvoicecollege.Model.DataX

interface ChatListener_Staff {
    fun onChatStaffdataCLick(holder: Chat_AdapterStaff.MyViewHolder, item: DataX)

}