package com.vsca.vsnapvoicecollege.Model


class Subjectdetail_ExamCreation {

    var examdate: String? = null
    var examsession: String? = null
    var examsubjectid: String? = null
    var examsyllabus: String? = null
    var examvenue: String? = null

    constructor(
        examdate: String?,
        examsession: String?,
        examsubjectid: String?,
        examsyllabus: String?,
        examvenue: String?,

        ) {

        this.examdate = examdate
        this.examsession = examsession
        this.examsubjectid = examsubjectid
        this.examsyllabus = examsyllabus
        this.examvenue = examvenue
    }
}