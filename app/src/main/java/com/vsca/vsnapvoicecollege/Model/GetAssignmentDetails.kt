package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetAssignmentDetails {
    @SerializedName("assignmentid")
    @Expose
    var assignmentid: String? = null

    @SerializedName("subjectname")
    @Expose
    var subjectname: String? = null

    @SerializedName("topic")
    @Expose
    var topic: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("submissiondate")
    @Expose
    var submissiondate: String? = null

    @SerializedName("createdon")
    @Expose
    var createdon: String? = null

    @SerializedName("sentbyname")
    @Expose
    var sentbyname: String? = null

    @SerializedName("createdby")
    @Expose
    var createdby: String? = null

    @SerializedName("assignmenttype")
    @Expose
    var assignmenttype: String? = null

    @SerializedName("totalcount")
    @Expose
    var totalcount: String? = null

    @SerializedName("submittedcount")
    @Expose
    var submittedcount: String? = null

//    @SerializedName("notsubmittedcount")
//    @Expose
//    var notsubmittedcount: String? = "1"


    @SerializedName("file_path")
    @Expose
    var file_path: String? = null

    @SerializedName("newfilepath")
    @Expose
    var newfilepath: ArrayList<String>? = null

    @SerializedName("userfilename")
    @Expose
    var userfilename: String? = null

    @SerializedName("assignmentdetailid")
    @Expose
    var assignmentdetailid: String? = null

    @SerializedName("isappread")
    @Expose
    var isappread: String? = null

    @SerializedName("newuserfilename")
    @Expose
    var newuserfilename: List<String>? = null
}