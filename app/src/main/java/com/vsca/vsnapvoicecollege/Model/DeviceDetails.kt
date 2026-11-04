package com.vsca.vsnapvoicecollege.Model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DeviceDetails : Serializable {

    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("deviceid")
    @Expose
    var deviceid: String? = null

    @SerializedName("devicename")
    @Expose
    var devicename: String? = null

    @SerializedName("created_at")
    @Expose
    var created_at: String? = null

    @SerializedName("last_active_at")
    @Expose
    var last_active_at: String? = null

    @SerializedName("isCurrentDevice")
    @Expose
    var isCurrentDevice = false
}