package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VenueListData {

    @SerializedName("venueid")
    @Expose
    var venueid: Int? = null

    @SerializedName("venuename")
    @Expose
    var venuename: String? = null

    @SerializedName("venuecode")
    @Expose
    var venuecode: String? = null

    @SerializedName("capacity")
    @Expose
    var capacity: Int? = null

    @SerializedName("location")
    @Expose
    var location: String? = null

    @SerializedName("facilities")
    @Expose
    var facilities: String? = null

    @SerializedName("departments")
    @Expose
    var departments: String? = null

    @SerializedName("displayname")
    @Expose
    var displayname: String? = null
}
