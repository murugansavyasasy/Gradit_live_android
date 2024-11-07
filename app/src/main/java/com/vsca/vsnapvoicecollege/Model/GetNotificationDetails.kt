package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetNotificationDetails {
    @SerializedName("sent_on")
    @Expose
    var sentOn: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("notification_content")
    @Expose
    var notification_content: String? = null

    @SerializedName("module_type")
    @Expose
    var module_type: String? = null

    @SerializedName("details_id")
    @Expose
    var details_id: String? = null
}