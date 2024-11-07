package com.vsca.vsnapvoicecollege.Model

class ExamcreationEdit {
    var collegeid: Int? = null
    var examid: String? = null
    var processtype: String? = null
    var staffid: Int? = null
    var sectionid: String? = null
    var deartmentid: String? = null

    //  var sectionExamcreationEDIT = ArrayList<sectionExamcreationEDIT>()
    var SubjectExamcreationEDIT = ArrayList<SubjectExamcreationEDIT>()


    constructor(

        collegeid: Int,
        //  enddate: String,
        examid: String,
        //   examname: String,
        processtype: String,
        staffid: Int,
        sectionid: String,
        //  startdate: String,
        //  departmentid: String,
        //  sectionExamcreationEDIT: List<sectionExamcreationEDIT>
        SubjectExamcreationEDIT: ArrayList<SubjectExamcreationEDIT>

    ) {

        this.collegeid = collegeid
        //   this.enddate = enddate
        this.examid = examid
        //  this.examname = examname
        this.processtype = processtype
        this.staffid = staffid
        this.sectionid = sectionid
        //  this.startdate = startdate
        //   this.deartmentid = departmentid
        this.SubjectExamcreationEDIT = SubjectExamcreationEDIT
        //   this.sectionExamcreationEDIT = sectionExamcreationEDIT as ArrayList<sectionExamcreationEDIT>
    }
}