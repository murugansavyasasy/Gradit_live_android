package com.vsca.vsnapvoicecollege.Model

data class Examination_Request(
    val collegeid: String,
    val enddate: String,
    val examid: String,
    val examname: String,
    val processtype: String,
    val sectiondetails: List<SectiondetailX>,
    val staffid: String,
    val startdate: String
)