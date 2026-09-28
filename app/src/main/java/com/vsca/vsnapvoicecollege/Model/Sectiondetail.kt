package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Sectiondetail {

    @SerializedName("sectionid")
    @Expose
    var sectionid: Int? = null

    @SerializedName("sectionname")
    @Expose
    var sectionname: String? = null

}