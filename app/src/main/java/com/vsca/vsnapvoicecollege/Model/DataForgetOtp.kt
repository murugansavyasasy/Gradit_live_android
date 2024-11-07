package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.SerializedName

data class DataForgetOtp(

    @SerializedName("ivrnumbers") var ivrnumbers: ArrayList<String> = arrayListOf()

)
