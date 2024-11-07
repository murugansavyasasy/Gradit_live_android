package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class DashboardDetailsDataResponse {
    @SerializedName("add_title")
    @Expose
    var add_title: String? = null

    @SerializedName("add_content")
    @Expose
    var add_content: String? = null

    @SerializedName("company")
    @Expose
    var company: String? = null

    @SerializedName("background_image")
    @Expose
    var background_image: String? = null

    @SerializedName("add_image")
    @Expose
    var add_image: String? = null

    @SerializedName("add_url")
    @Expose
    var add_url: String? = null

    @SerializedName("video_url")
    @Expose
    var video_url: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("idfiledetails")
    @Expose
    var idfiledetails: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("voicefilepath")
    @Expose
    var voicefilepath: String? = null

    @SerializedName("detailsid")
    @Expose
    var detailsid: String? = null

    @SerializedName("membername")
    @Expose
    var membername: String? = null

    @SerializedName("createdon")
    @Expose
    var createdon: String? = null

    @SerializedName("filepaths")
    @Expose
    var filepaths: List<String>? = null

    @SerializedName("createddate")
    @Expose
    var createddate: String? = null

    @SerializedName("createdtime")
    @Expose
    var createdtime: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("typ")
    @Expose
    var typ: String? = null

    @SerializedName("sentbyname")
    @Expose
    var sentbyname: String? = null

    @SerializedName("createdondate")
    @Expose
    var createdondate: String? = null

    @SerializedName("createdontime")
    @Expose
    var createdontime: String? = null

    @SerializedName("content")
    @Expose
    var content: String? = null

    @SerializedName("duration")
    @Expose
    var duration = 0

    @SerializedName("idnoticeboarddetails")
    @Expose
    var idnoticeboarddetails: String? = null

    @SerializedName("topicheading")
    @Expose
    var topicheading: String? = null

    @SerializedName("topicbody")
    @Expose
    var topicbody: String? = null

    @SerializedName("subjectname")
    @Expose
    var subjectname: String? = null

    @SerializedName("attendancetype")
    @Expose
    var attendancetype: String? = null

    @SerializedName("attendancedate")
    @Expose
    var attendancedate: String? = null


    @SerializedName("eventtopic")
    @Expose
    var eventtopic: String? = null

    @SerializedName("eventdate")
    @Expose
    var eventdate: String? = null

    @SerializedName("eventtime")
    @Expose
    var eventtime: String? = null

    @SerializedName("ideventdetails")
    @Expose
    var ideventdetails: String? = null


    @SerializedName("idassignmentdetails")
    @Expose
    var idassignmentdetails: String? = null

    @SerializedName("assignmenttopic")
    @Expose
    var assignmenttopic: String? = null

    @SerializedName("assignmentdescription")
    @Expose
    var assignmentdescription: String? = null

    @SerializedName("submissiondate")
    @Expose
    var submissiondate: String? = null

//    @SerializedName("filepaths")
//    @Expose
//    var filepathsAssignment:List<String>?=null


    @SerializedName("coursename")
    @Expose
    var coursename: String? = null

    @SerializedName("departmentname")
    @Expose
    var departmentname: String? = null

    @SerializedName("yearname")
    @Expose
    var yearname: String? = null


    @SerializedName("sectionname")
    @Expose
    var sectionname: String? = null

    @SerializedName("studentname")
    @Expose
    var studentname: String? = null

    @SerializedName("question")
    @Expose
    var question: String? = null


    //Leave Request

    @SerializedName("leaveapplicationid")
    @Expose
    var leaveapplicationid: String? = null

    @SerializedName("studentid")
    @Expose
    var studentid: String? = null

    @SerializedName("reason")
    @Expose
    var reason: String? = null

    @SerializedName("fromdate")
    @Expose
    var fromdate: String? = null

    @SerializedName("todate")
    @Expose
    var todate: String? = null

    @SerializedName("leavestatus")
    @Expose
    var leavestatus: String? = null

    @SerializedName("noofdays")
    @Expose
    var noofdays: String? = null

    @SerializedName("appliedon")
    @Expose
    var appliedon: String? = null

    @SerializedName("file_type")
    @Expose
    var file_type: String? = null

}