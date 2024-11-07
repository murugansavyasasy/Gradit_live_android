package com.vsca.vsnapvoicecollege.Model


class Examination_Creation {

    var collegeid: Int? = null
    var enddate: String? = null
    var examid: String? = null
    var examname: String? = null
    var processtype: String? = null
    var staffid: Int? = null
    var startdate: String? = null
    var deartmentid: String? = null
    var Sectiondetails = ArrayList<Sectiondetail_ExamCreation>()

    constructor(

        collegeid: Int,
        enddate: String,
        examid: String,
        examname: String,
        processtype: String,
        staffid: Int,
        startdate: String,
        departmentid: String,
        Sectiondetails: List<Sectiondetail_ExamCreation>

    ) {

        this.collegeid = collegeid
        this.enddate = enddate
        this.examid = examid
        this.examname = examname
        this.processtype = processtype
        this.staffid = staffid
        this.startdate = startdate
        this.deartmentid = departmentid
        this.Sectiondetails = Sectiondetails as ArrayList<Sectiondetail_ExamCreation>
    }
}