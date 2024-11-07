package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetNoticeboardDetails {
    @SerializedName("noticeheaderid")
    @Expose
    var noticeboardheaderID: String? = null

    @SerializedName("noticedetailsid")
    @Expose
    var noticedetailsid: String? = null

    @SerializedName("topic")
    @Expose
    var topic: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("createdondate")
    @Expose
    var createdondate: String? = null

    @SerializedName("createdontime")
    @Expose
    var createdontime: String? = null

    @SerializedName("sentbyname")
    @Expose
    var sentbyname: String? = null

    @SerializedName("createdby")
    @Expose
    var createdby: String? = null

    @SerializedName("isappread")
    @Expose
    var isappread: String? = null

    @SerializedName("filearray")
    @Expose
    var filearray: ArrayList<NoticeNoardFiles>? = null
}