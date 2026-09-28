package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Data {

    @SerializedName("yearname")
    @Expose
    var yearname: String? = null

    @SerializedName("yearid")
    @Expose
    var yearid: Int? = null

    @SerializedName("sectiondetails")
    var sectiondetails: ArrayList<Sectiondetail>? = null
}