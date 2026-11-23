package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * One venue booking request (api/AppDetailsBal/GetVenueRequestList). All fields nullable: values may be missing.
 * Serializable so it can be passed to AddEvents for "Edit request".
 */
class VenueRequestData : Serializable {

    @SerializedName("bookingid")
    @Expose
    var bookingid: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("venueid")
    @Expose
    var venueid: Int? = null

    @SerializedName("venuename")
    @Expose
    var venuename: String? = null

    @SerializedName("venuecolor")
    @Expose
    var venuecolor: String? = null

    @SerializedName("location")
    @Expose
    var location: String? = null

    @SerializedName("eventdate")
    @Expose
    var eventdate: String? = null

    @SerializedName("displaydate")
    @Expose
    var displaydate: String? = null

    @SerializedName("starttime")
    @Expose
    var starttime: String? = null

    @SerializedName("endtime")
    @Expose
    var endtime: String? = null

    @SerializedName("displaytime")
    @Expose
    var displaytime: String? = null

    /** PENDING / APPROVED / REJECTED / CANCELLED / EXPIRED */
    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("statuslabel")
    @Expose
    var statuslabel: String? = null

    @SerializedName("approver")
    @Expose
    var approver: String? = null

    @SerializedName("eventstatus")
    @Expose
    var eventstatus: String? = null

    @SerializedName("eventmessage")
    @Expose
    var eventmessage: String? = null

    @SerializedName("decidedby")
    @Expose
    var decidedby: String? = null

    @SerializedName("decidedbyrole")
    @Expose
    var decidedbyrole: String? = null

    @SerializedName("decidedon")
    @Expose
    var decidedon: String? = null

    @SerializedName("remarks")
    @Expose
    var remarks: String? = null

    @SerializedName("requestedon")
    @Expose
    var requestedon: String? = null

    // ---- Only sent by GetVenueApprovalList (Approve Venue); null for My Requests ----

    @SerializedName("requestedby")
    @Expose
    var requestedby: String? = null

    @SerializedName("department")
    @Expose
    var department: String? = null

    @SerializedName("needscollegeadmin")
    @Expose
    var needscollegeadmin: Boolean? = null

    @SerializedName("hasconflict")
    @Expose
    var hasconflict: Boolean? = null

    @SerializedName("canapprove")
    @Expose
    var canapprove: Boolean? = null

    @SerializedName("canreject")
    @Expose
    var canreject: Boolean? = null
}
