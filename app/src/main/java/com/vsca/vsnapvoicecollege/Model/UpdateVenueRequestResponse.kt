package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/** Response of api/AppDetailsBal/UpdateVenueRequest (Status 1 = "Request updated."). */
class UpdateVenueRequestResponse {
    @SerializedName("Status")
    @Expose
    var status = 0

    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<UpdateVenueRequestData>? = null
}

class UpdateVenueRequestData {
    @SerializedName("bookingid")
    @Expose
    var bookingid: Int? = null
}
