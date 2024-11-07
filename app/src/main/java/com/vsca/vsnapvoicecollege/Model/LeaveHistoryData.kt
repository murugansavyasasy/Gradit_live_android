package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LeaveHistoryData {

    @SerializedName("createdon")
    @Expose
    var createdon: String? = null

    @SerializedName("studentname")
    @Expose
    var studentname: String? = null

    @SerializedName("applicationid")
    @Expose
    var applicationid: String? = null

    @SerializedName("leaveapplicationtype")
    @Expose
    var leaveapplicationtype: String? = null

    @SerializedName("leavefromdate")
    @Expose
    var leavefromdate: String? = null

    @SerializedName("leavetodate")
    @Expose
    var leavetodate: String? = null

    @SerializedName("numofdays")
    @Expose
    var numofdays: String? = null

    @SerializedName("leavereason")
    @Expose
    var leavereason: String? = null

    @SerializedName("leavestatus")
    @Expose
    var leavestatus: String? = null

    @SerializedName("leavestatusid")
    @Expose
    var leavestatusid: String? = null


}