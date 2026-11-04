package com.vsca.vsnapvoicecollege.Model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeviceListResponse {

    @SerializedName("Status")
    @Expose
    var Status = 0

    @SerializedName("Message")
    @Expose
    var Message: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<DeviceDetails>? = null
}