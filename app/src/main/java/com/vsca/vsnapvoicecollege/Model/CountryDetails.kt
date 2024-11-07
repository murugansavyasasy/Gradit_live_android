package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CountryDetails {
    @SerializedName("countryid")
    @Expose
    var countryid = 0

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("mobilenumberlen")
    @Expose
    var mobilenumberlen: String? = null

    @SerializedName("baseurls")
    @Expose
    var baseurl: String? = null

    @SerializedName("codecountry")
    @Expose
    var countyCode: String? = null

    @SerializedName("idapplication")
    @Expose
    var idapplication = 0
}