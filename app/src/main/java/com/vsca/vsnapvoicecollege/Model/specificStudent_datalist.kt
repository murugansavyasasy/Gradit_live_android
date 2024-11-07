package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class specificStudent_datalist {

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("memberid")
    var memberid: String? = null

    @SerializedName("admissionno")
    var admissionno: String? = null

    @SerializedName("regno")
    var regno: String? = null

    @SerializedName("course")
    var course: String? = null

    @SerializedName("year")
    var year: String? = null

    @SerializedName("section")
    var section: String? = null

}