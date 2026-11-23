package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VenueListResponse {
    @SerializedName("Status")
    @Expose
    var status = 0

    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("approvalnote")
    @Expose
    var approvalnote: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<VenueListData>? = null
}
