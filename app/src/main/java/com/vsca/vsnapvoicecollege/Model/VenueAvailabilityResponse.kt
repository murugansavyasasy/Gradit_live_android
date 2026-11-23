package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/** Response of api/AppDetailsBal/CheckVenueAvailability (Status 1 = available, 0 = not available). */
class VenueAvailabilityResponse {
    @SerializedName("Status")
    @Expose
    var status = 0

    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<VenueAvailabilityData>? = null
}
