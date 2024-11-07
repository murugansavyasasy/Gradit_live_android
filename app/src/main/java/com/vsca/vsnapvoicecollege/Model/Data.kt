package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//data class Data(
//    val sectiondetails: List<Sectiondetail>,
//    val yearid: Int,
//    val yearname: String
//)

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