package com.vsca.vsnapvoicecollege.SenderModel

//class SenderStatusMessageData {

//    @SerializedName("Status")
//    @Expose
//    var Status: Int? = null
//
//    @SerializedName("Message")
//    @Expose
//    var Message: String? = null
//
//    @SerializedName("data")
//    @Expose
//    var data: String? = null
//}
data class SenderStatusMessageData(
    val Status: Int,
    val Message: String,
    val data: Array<Any>,
)