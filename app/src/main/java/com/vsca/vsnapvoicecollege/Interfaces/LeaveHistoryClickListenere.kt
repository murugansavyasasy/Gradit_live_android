package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.Adapters.Leavehistory_principleAdapter
import com.vsca.vsnapvoicecollege.Model.DataXXXX

interface LeaveHistoryClickListenere {
    fun onLeaveClick(holder: Leavehistory_principleAdapter.MyViewHolder, item: DataXXXX)
}