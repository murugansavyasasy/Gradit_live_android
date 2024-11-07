package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetAssignmentCountMember {
    @SerializedName("studentid")
    @Expose
    var studentid: String? = null

    @SerializedName("studentname")
    @Expose
    var studentname: String? = null

    @SerializedName("course")
    @Expose
    var course: String? = null

    @SerializedName("year")
    @Expose
    var year: String? = null

    @SerializedName("section")
    @Expose
    var section: String? = null

    @SerializedName("semester")
    @Expose
    var semester: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
}