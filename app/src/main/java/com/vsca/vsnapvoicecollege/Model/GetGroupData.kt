package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetGroupData {

    @SerializedName("groupid")
    @Expose
    var groupid: String? = null

    @SerializedName("groupname")
    @Expose
    var groupname: String? = null

    @SerializedName("grouptype")
    @Expose
    var grouptype: String? = null
}