package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.Adapters.HomeMenuWithoutApi
import com.vsca.vsnapvoicecollege.Model.MenuDetailsResponse

interface HomeMenuClickListenerWithoutApi {
    fun onMenuClick(holder: HomeMenuWithoutApi.MyViewHolder, data: MenuDetailsResponse)

}