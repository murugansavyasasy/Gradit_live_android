package com.vsca.vsnapvoicecollege.Model

data class sectionnamelist(
    val sectionid: String,
    val sectionname: String,
    val subjectdetails: List<Subjectdetail>
)