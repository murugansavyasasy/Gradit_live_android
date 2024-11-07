package com.vsca.vsnapvoicecollege.Model

data class ExamSectionEditorDelete(

    val examnm: String,
    val startdate: String,
    val enddate: String,
    val courseid: String,
    val coursename: String,
    val clgdepartmentid: String,
    val clgdepartmentname: String,
    val yearid: String,
    val yearname: String,
    val semesterid: String,
    val semestername: String,
    val clgsectionid: String,
    val clgsectionname: String,
    val examheaderid: String,
    val subjectdetails: List<ExamEditSubjectDetails>
)
