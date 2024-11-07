package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Get_staff_yourclass {

    @SerializedName("courseid")
    @Expose
    var courseid: String? = null

    @SerializedName("coursename")
    var coursename: String? = null

    @SerializedName("departmentid")
    var departmentid: String? = null

    @SerializedName("departmentname")
    var departmentname: String? = null

    @SerializedName("yearid")
    var yearid: String? = null

    @SerializedName("yearname")
    var yearname: String? = null

    @SerializedName("sectionid")
    var sectionid: String? = null

    @SerializedName("sectionname")
    var sectionname: String? = null

    @SerializedName("semesterid")
    var semesterid: String? = null

    @SerializedName("semestername")
    var semestername: String? = null

    @SerializedName("subjectid")
    var subjectid: String? = null

    @SerializedName("subjectname")
    var subjectname: String? = null

    @SerializedName("subjecttype")
    var subjecttype: String? = null
}
