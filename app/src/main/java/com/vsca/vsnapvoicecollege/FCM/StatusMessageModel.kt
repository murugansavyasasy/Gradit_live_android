package com.vsca.vsnapvoicecollege.FCM

import com.google.gson.annotations.SerializedName

data class StatusMessageModel(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<Any>
)
