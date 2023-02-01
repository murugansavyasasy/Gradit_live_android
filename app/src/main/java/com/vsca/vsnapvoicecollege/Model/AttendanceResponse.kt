package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AttendanceResponse {

    @SerializedName("Status")
    @Expose
    var status = 0
    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    var data: ArrayList<AttendanceData>? = null
}