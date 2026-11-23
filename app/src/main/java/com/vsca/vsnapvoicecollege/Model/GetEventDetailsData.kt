package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetEventDetailsData : Serializable {
    @SerializedName("eventid")
    @Expose
    var eventid: String? = null

    @SerializedName("event_date")
    @Expose
    var event_date: String? = null

    @SerializedName("event_time")
    @Expose
    var event_time: String? = null

    @SerializedName("topic")
    @Expose
    var topic: String? = null

    @SerializedName("body")
    @Expose
    var body: String? = null

    @SerializedName("venue")
    @Expose
    var venue: String? = null

    @SerializedName("createdbyname")
    @Expose
    var createdbyname: String? = null

    @SerializedName("createdby")
    @Expose
    var createdby: String? = null

    @SerializedName("filepath")
    @Expose
    var filepath: String? = null

    @SerializedName("eventdetailsid")
    @Expose
    var eventdetailsid: String? = null

    @SerializedName("isappread")
    @Expose
    var isappread: String? = null

    @SerializedName("newfilepath")
    @Expose
    var newfilepath: List<String>? = null

    // Venue booking fields (GetEventListByType). Nullable: older events may not send them.
    @SerializedName("isvenuebooking")
    @Expose
    var isvenuebooking: Boolean? = null

    @SerializedName("bookingid")
    @Expose
    var bookingid: Int? = null

    @SerializedName("venueid")
    @Expose
    var venueid: Int? = null

    /** e.g. "10:00 AM" */
    @SerializedName("starttime")
    @Expose
    var starttime: String? = null

    /** e.g. "12:00 PM"; empty for events without an end time */
    @SerializedName("endtime")
    @Expose
    var endtime: String? = null

    /** e.g. "10:00 AM - 12:00 PM", or just the start time */
    @SerializedName("displaytime")
    @Expose
    var displaytime: String? = null

}