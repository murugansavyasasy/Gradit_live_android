package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetStaffDetailsData : Serializable {

    @SerializedName("subjectid")
    @Expose
    var subjectid: String? = null

    @SerializedName("coursename")
    @Expose
    var coursename: String? = null

    @SerializedName("yearname")
    @Expose
    var yearname: String? = null

    @SerializedName("semestername")
    @Expose
    var semestername: String? = null

    @SerializedName("sectionname")
    @Expose
    var sectionname: String? = null

    @SerializedName("subjectname")
    @Expose
    var subjectname: String? = null

    @SerializedName("staffid")
    @Expose
    var staffid: String? = null

    @SerializedName("staffname")
    @Expose
    var staffname: String? = null

    @SerializedName("isclassteacher")
    @Expose
    var isclassteacher: String? = null

    @SerializedName("sectionid")
    @Expose
    var sectionid: String? = null

    @SerializedName("subjectcount")
    @Expose
    var subjectcount: String? = null

}