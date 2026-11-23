package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/** Response of api/AppDetailsBal/venuerequests (Status 1 = list, 0 = "No venue requests found."). */
class VenueRequestResponse {
    @SerializedName("Status")
    @Expose
    var status = 0

    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<VenueRequestData>? = null
}
