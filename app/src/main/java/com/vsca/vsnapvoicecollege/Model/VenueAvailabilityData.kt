package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VenueAvailabilityData {

    @SerializedName("venueid")
    @Expose
    var venueid: Int? = null

    @SerializedName("venuename")
    @Expose
    var venuename: String? = null

    @SerializedName("eventdate")
    @Expose
    var eventdate: String? = null

    @SerializedName("displaydate")
    @Expose
    var displaydate: String? = null

    @SerializedName("available")
    @Expose
    var available: Boolean? = null

    @SerializedName("availability")
    @Expose
    var availability: String? = null

    @SerializedName("starttime")
    @Expose
    var starttime: String? = null

    @SerializedName("endtime")
    @Expose
    var endtime: String? = null

    @SerializedName("displaytime")
    @Expose
    var displaytime: String? = null

    @SerializedName("clash")
    @Expose
    var clash: VenueBookedSlot? = null

    @SerializedName("bookedslots")
    @Expose
    var bookedslots: ArrayList<VenueBookedSlot>? = null
}
