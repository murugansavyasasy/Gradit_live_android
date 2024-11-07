package com.vsca.vsnapvoicecollege.Model

class Sectiondetail_ExamCreation {

    var clgsectionid: String? = null
    var Subjectdetails = ArrayList<Subjectdetail_ExamCreation>()


    constructor(
        clgsectionid: String?,
        Subjectdetails: ArrayList<Subjectdetail_ExamCreation>

    ) {
        this.clgsectionid = clgsectionid
        this.Subjectdetails = Subjectdetails
    }
}
