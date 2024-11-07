package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetExamApplicationDetails {
    @SerializedName("sem_number")
    @Expose
    var semNumber: String? = null

    @SerializedName("subject_name")
    @Expose
    var subjectName: String? = null

    @SerializedName("subject_code")
    @Expose
    var subjectcode: String? = null

    @SerializedName("amount")
    @Expose
    var amount: String? = null
}