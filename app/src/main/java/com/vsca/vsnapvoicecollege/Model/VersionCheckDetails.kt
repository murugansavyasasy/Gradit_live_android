package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class VersionCheckDetails {

    @SerializedName("isversionupdateavailable")
    @Expose
    var versionUpdate = 0

    @SerializedName("isforceupdaterequired")
    @Expose
    var forceUpdate = 0

    @SerializedName("value")
    @Expose
    var value = 0

    @SerializedName("imagecount")
    @Expose
    var imagecount: String? = null

    @SerializedName("pdfcount")
    @Expose
    var pdfcount: String? = null

    @SerializedName("eventphotoscount")
    @Expose
    var eventphotoscount: String? = null

    @SerializedName("attendancedaycount")
    @Expose
    var attendancedaycount: String? = null

    @SerializedName("privarypolicy")
    @Expose
    var privarypolicy: String? = null

    @SerializedName("faq")
    @Expose
    var faq: String? = null

    @SerializedName("help")
    @Expose
    var help: String? = null

    @SerializedName("termsandcondition")
    @Expose
    var termsandcondition: String? = null

    @SerializedName("playstorelink")
    @Expose
    var playstorelink: String? = null

    @SerializedName("versionalerttitle")
    @Expose
    var versionalerttitle: String? = null

    @SerializedName("versionalertcontent")
    @Expose
    var versionalertcontent: String? = null

    @SerializedName("playstoremarketid")
    @Expose
    var playstoremarketid: String? = null

    @SerializedName("videojson")
    @Expose
    var videojson: String? = null

    @SerializedName("videosizelimit")
    @Expose
    var videosizelimit: String? = null

    @SerializedName("videosizealert")
    @Expose
    var videosizealert: String? = null

    @SerializedName("emergency")
    @Expose
    var emergency: String? = null

    @SerializedName("nonemergency")
    @Expose
    var nonemergency: String? = null
}