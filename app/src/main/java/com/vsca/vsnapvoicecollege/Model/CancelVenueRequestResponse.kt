package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/** Response of api/AppDetailsBal/CancelVenueRequest (Status 1 = "Booking cancelled.", 0 = e.g. "Booking not found."). */
class CancelVenueRequestResponse {
    @SerializedName("Status")
    @Expose
    var status = 0

    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<CancelVenueRequestData>? = null
}

class CancelVenueRequestData {
    @SerializedName("bookingid")
    @Expose
    var bookingid: Int? = null
}
