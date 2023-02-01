package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetOverAllCountResposne {
    @SerializedName("Status")
    @Expose
    var status = 0
    @SerializedName("Message")
    @Expose
    var message: String? = null
    @SerializedName("data")
    var data: List<GetOverAllCountDetails>? = null
}