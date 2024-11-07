package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ExamMarkListDetails {

    @SerializedName("subjectname")
    @Expose
    var subjectname: String? = null

    @SerializedName("marks")
    @Expose
    var marks: String? = null

}