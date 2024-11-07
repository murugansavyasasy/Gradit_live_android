package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.Adapters.LoginChooseRoles
import com.vsca.vsnapvoicecollege.Model.LoginDetails

interface LoginRolesListener {
    fun onroleClick(holder: LoginChooseRoles.MyViewHolder, data: LoginDetails)

}