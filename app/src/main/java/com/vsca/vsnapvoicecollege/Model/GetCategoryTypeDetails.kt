package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetCategoryTypeDetails {

    @SerializedName("category_id")
    @Expose
    var category_id: String? = null

    @SerializedName("category_name")
    @Expose
    var category_name: String? = null

}