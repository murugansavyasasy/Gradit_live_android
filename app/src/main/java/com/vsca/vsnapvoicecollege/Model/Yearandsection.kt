package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//data class Yearandsection(
//    val Message: String,
//    val Status: Int,
//    val `data`: ArrayList<Data>
//)

class Yearandsection {

    @SerializedName("Status")
    @Expose
    var Status: Int? = null

    @SerializedName("Message")
    @Expose
    var Message: String? = null

    @SerializedName("data")
    var data: List<Data>? = null
}