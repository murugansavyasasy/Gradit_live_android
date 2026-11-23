package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetOverAllCountDetails {

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("reminders")
    @Expose
    var reminders: String? = null

    @SerializedName("read")
    @Expose
    var read: String? = null

    @SerializedName("unread")
    @Expose
    var unread: String? = null

    @SerializedName("upcomingexams")
    @Expose
    var upcomingexams: String? = null

    @SerializedName("pastexams")
    @Expose
    var pastexams: String? = null

    @SerializedName("upcomingassignment")
    @Expose
    var upcomingassignment: String? = null

    @SerializedName("pastassignment")
    @Expose
    var pastassignment: String? = null

    @SerializedName("departmentcircular")
    @Expose
    var departmentcircular: String? = null

    @SerializedName("collegecircular")
    @Expose
    var collegecircular: String? = null

    @SerializedName("departmentnotice")
    @Expose
    var departmentnotice: String? = null

    @SerializedName("collegenotice")
    @Expose
    var collegenotice: String? = null

    @SerializedName("upcomingevents")
    @Expose
    var upcomingevents: String? = null

    @SerializedName("pastevents")
    @Expose
    var pastevents: String? = null

    @SerializedName("video")
    @Expose
    var video: String? = null

    // Events "My Requests" badge count (GetOverallcountByMenuType). Nullable: may be missing.
    @SerializedName("venuerequests")
    @Expose
    var venuerequests: String? = null

    // Events request counts (GetOverallcountByMenuType). Nullable: may be missing.
    @SerializedName("pendingvenuerequests")
    @Expose
    var pendingvenuerequests: String? = null

    /** Approve Event tab count */
    @SerializedName("approvevenuerequests")
    @Expose
    var approvevenuerequests: String? = null

    @SerializedName("pendingapprovevenuerequests")
    @Expose
    var pendingapprovevenuerequests: String? = null
}