package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginDetails {
    @SerializedName("colgid")
    @Expose
    var colgid = 0

    @SerializedName("memberid")
    @Expose
    var memberid = 0

    @SerializedName("priority")
    @Expose
    var priority: String? = null

    @SerializedName("membername")
    @Expose
    var membername: String? = null

    @SerializedName("colgname")
    @Expose
    var colgname: String? = null

    @SerializedName("colgcity")
    @Expose
    var colgcity: String? = null

    @SerializedName("colglogo")
    @Expose
    var colglogo: String? = null

    @SerializedName("divid")
    @Expose
    var divisionId: String? = null

    @SerializedName("divname")
    @Expose
    var divisionName: String? = null

    @SerializedName("is_allow_to_make_call")
    @Expose
    var is_allow_to_make_call: Int? = null

    @SerializedName("courseid")
    @Expose
    var courseid: String? = null

    @SerializedName("coursename")
    @Expose
    var coursename: String? = null

    @SerializedName("deptid")
    @Expose
    var deptid: String? = null

    @SerializedName("deptname")
    @Expose
    var deptname: String? = null

    @SerializedName("yearid")
    @Expose
    var yearid: String? = null

    @SerializedName("yearname")
    @Expose
    var yearname: String? = null

    @SerializedName("sectionid")
    @Expose
    var sectionid: String? = null

    @SerializedName("sectionname")
    @Expose
    var sectionname: String? = null

    @SerializedName("semesterid")
    @Expose
    var semesterid: String? = null

    @SerializedName("semestername")
    @Expose
    var semestername: String? = null

    @SerializedName("loginas")
    @Expose
    var loginas: String? = null

    @SerializedName("is_parent_target_enabled")
    @Expose
    var is_parent_target_enabled: String? = null
}