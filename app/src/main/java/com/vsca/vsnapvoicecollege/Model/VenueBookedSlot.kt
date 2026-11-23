package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/** Used for both "clash" and each item of "bookedslots" in CheckVenueAvailability. */
class VenueBookedSlot {

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("starttime")
    @Expose
    var starttime: String? = null

    @SerializedName("endtime")
    @Expose
    var endtime: String? = null

    @SerializedName("displaytime")
    @Expose
    var displaytime: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("bookedby")
    @Expose
    var bookedby: String? = null
}
