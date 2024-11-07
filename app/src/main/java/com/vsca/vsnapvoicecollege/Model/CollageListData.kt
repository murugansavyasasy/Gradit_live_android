package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CollageListData {
    @SerializedName("college_id")
    @Expose
    var college_id: Int? = null

    @SerializedName("college_name")
    var college_name: String? = null
}