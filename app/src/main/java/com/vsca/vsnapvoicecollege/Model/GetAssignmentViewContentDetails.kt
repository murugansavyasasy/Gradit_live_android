package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetAssignmentViewContentDetails {
    @SerializedName("content")
    @Expose
    var content: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("submittedtime")
    @Expose
    var submittedtime: String? = null

    @SerializedName("file_name")
    @Expose
    var file_name: String? = null
}