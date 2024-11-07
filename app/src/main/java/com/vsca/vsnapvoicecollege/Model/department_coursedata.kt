package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class department_coursedata {

    @SerializedName("course_code")
    @Expose
    var course_code: String? = null

    @SerializedName("course_name")
    var course_name: String? = null

    @SerializedName("course_id")
    var course_id: String? = null


}