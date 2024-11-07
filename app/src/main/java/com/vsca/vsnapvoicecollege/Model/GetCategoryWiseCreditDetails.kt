package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetCategoryWiseCreditDetails {

    @SerializedName("category_id")
    @Expose
    var category_id: String? = null

    @SerializedName("category_name")
    @Expose
    var category_name: String? = null

    @SerializedName("semester_name")
    @Expose
    var semester_name: String? = null

    @SerializedName("total_credits")
    @Expose
    var total_credits: String? = null

    @SerializedName("obtained")
    @Expose
    var obtained: String? = null

    @SerializedName("to_be_obtained")
    @Expose
    var to_be_obtained: String? = null
}