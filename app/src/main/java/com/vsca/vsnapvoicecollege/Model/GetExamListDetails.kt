package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetExamListDetails {
    @SerializedName("headerid")
    @Expose
    var headerid: String? = null

    @SerializedName("createdby")
    @Expose
    var createdby: String? = null

    @SerializedName("examname")
    @Expose
    var examname: String? = null

    @SerializedName("subjectname")
    @Expose
    var subjectname: String? = null

    @SerializedName("examvenue")
    @Expose
    var examvenue: String? = null

    @SerializedName("session")
    @Expose
    var session: String? = null

    @SerializedName("startdate")
    @Expose
    var startdate: String? = null

    @SerializedName("enddate")
    @Expose
    var enddate: String? = null

    @SerializedName("createdon")
    @Expose
    var createdon: String? = null

    @SerializedName("syllabus")
    @Expose
    var syllabus: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("createdbyname")
    @Expose
    var createdbyname: String? = null
}