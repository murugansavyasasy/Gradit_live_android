package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetCommunicationDetails {
    @SerializedName("typename")
    @Expose
    var typename: String? = null

    @SerializedName("sentby")
    @Expose
    var sentby: String? = null

    @SerializedName("headerid")
    var headerid: String? = null

    @SerializedName("msgdetailsid")
    var msgdetailsid: String? = null

    @SerializedName("timing")
    var timing: String? = null

    @SerializedName("duration")
    var duration: String? = null

    @SerializedName("msgcontent")
    var msgcontent: String? = null

    @SerializedName("voicefile")
    var voicefile: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("isappread")
    var isappread: String? = null

    @SerializedName("isemergency")
    var isemergency: String? = null

}
