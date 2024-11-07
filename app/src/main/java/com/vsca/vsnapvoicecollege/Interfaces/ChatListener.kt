package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.Adapters.ChatStaffAdapter
import com.vsca.vsnapvoicecollege.Model.GetStaffDetailsData


interface ChatListener {
    fun onChatStaffCLick(holder: ChatStaffAdapter.MyViewHolder, item: GetStaffDetailsData)
}