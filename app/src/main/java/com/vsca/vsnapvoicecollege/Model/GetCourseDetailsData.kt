package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetCourseDetailsData {
    @SerializedName("subject_id")
    @Expose
    var subjectId: String? = null

    @SerializedName("subject_name")
    @Expose
    var subjectname: String? = null

    @SerializedName("subject_code")
    @Expose
    var subjectcode: String? = null

    @SerializedName("subject_type")
    @Expose
    var subjectType: String? = null

    @SerializedName("subject_credits")
    @Expose
    var subjectCredits: String? = null

    @SerializedName("subject_category")
    @Expose
    var subjectCategory: String? = null

    @SerializedName("subject_requirement")
    @Expose
    var subjectrequirement: String? = null
}