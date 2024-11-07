package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//data class Sectiondetail(
//    val sectionid: Int,
//    val sectionname: String
//)

class Sectiondetail {

    @SerializedName("sectionid")
    @Expose
    var sectionid: Int? = null

    @SerializedName("sectionname")
    @Expose
    var sectionname: String? = null

}