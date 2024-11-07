package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CountryDetailsResponse {
    @SerializedName("Status")
    @Expose
    var status: Int? = null

    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    var data: List<CountryDetails>? = null
}