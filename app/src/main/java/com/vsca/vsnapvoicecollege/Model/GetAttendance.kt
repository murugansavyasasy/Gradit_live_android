package com.vsca.vsnapvoicecollege.Model


data class GetAttendance(
    val Status: Int,
    val Message: String,
    val data: List<Daum>,
)

data class Daum(
    val courseid: String,
    val coursename: String,
    val departmentid: String,
    val departmentname: String,
    val yearid: String,
    val yearname: String,
    val sectionid: String,
    val sectionname: String,
    val semesterid: String,
    val semestername: String,
    val subjectid: String,
    val subjectname: String,
    val subjecttype: String,
    val isedit: String,
    val add_hours: List<AttendanceHour>,
    val edit_hours: List<AttendanceHourEdit>,
    val attendance_type: String? // Here actually we get two values "nth" means Period "no" means hours

    )

data class AttendanceHour(
    val hour: Int,
    val period: Int?
)

data class AttendanceHourEdit(
    val hour: Int,
    val period: Int?,
    val title:String,
    val type:String
)

