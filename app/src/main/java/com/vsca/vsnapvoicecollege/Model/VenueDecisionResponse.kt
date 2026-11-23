package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/** Response of api/AppDetailsBal/ApproveVenueRequest and api/AppDetailsBal/RejectVenueRequest. */
class VenueDecisionResponse {
    @SerializedName("Status")
    @Expose
    var status = 0

    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: ArrayList<VenueDecisionData>? = null
}

class VenueDecisionData {
    @SerializedName("bookingid")
    @Expose
    var bookingid: Int? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("eventpublished")
    @Expose
    var eventpublished: Boolean? = null

    /** Overlapping requests rejected automatically by an approval */
    @SerializedName("autorejected")
    @Expose
    var autorejected: Int? = null
}
