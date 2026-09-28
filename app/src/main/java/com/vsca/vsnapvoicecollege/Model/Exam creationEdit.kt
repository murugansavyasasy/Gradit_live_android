package com.vsca.vsnapvoicecollege.Model

class ExamcreationEdit {
    var collegeid: Int? = null
    var examid: String? = null
    var processtype: String? = null
    var staffid: Int? = null
    var sectionid: String? = null
    var deartmentid: String? = null

    var SubjectExamcreationEDIT = ArrayList<SubjectExamcreationEDIT>()


    constructor(

        collegeid: Int,
        examid: String,
        processtype: String,
        staffid: Int,
        sectionid: String,
        SubjectExamcreationEDIT: ArrayList<SubjectExamcreationEDIT>

    ) {

        this.collegeid = collegeid
        this.examid = examid
        this.processtype = processtype
        this.staffid = staffid
        this.sectionid = sectionid
        this.SubjectExamcreationEDIT = SubjectExamcreationEDIT
    }
}