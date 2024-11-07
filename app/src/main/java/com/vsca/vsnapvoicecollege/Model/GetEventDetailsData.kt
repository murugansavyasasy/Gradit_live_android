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


}