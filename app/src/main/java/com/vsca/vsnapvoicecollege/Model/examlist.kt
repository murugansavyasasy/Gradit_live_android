package com.vsca.vsnapvoicecollege.Model

data class examlist(
    val clgdepartmentid: String,
    val clgdepartmentname: String,
    val clgsectionid: String,
    val clgsectionname: String,
    val courseid: String,
    val coursename: String,
    val enddate: String,
    val examheaderid: String,
    val examnm: String,
    val semesterid: String,
    val semestername: String,
    val startdate: String,
    val subjectdetails: ArrayList<SubjectdetailX>,
    val yearid: String,
    val yearname: String
)