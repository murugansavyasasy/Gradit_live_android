package com.vsca.vsnapvoicecollege.Model


data class StudentAttendancelist(
    val Status: Int,
    val Message: String,
    val data: ArrayList<StudentAttendance>,
)

data class StudentAttendance(
    val subject_id: Int,
    val subjectname: String,
    val staff_id: Int,
    val staff_name: String,
    val attended_hour_no: Int,
    val absent_hour_no: Int,
    val attended_hour: String,
    val absent_hour: String,
    val total_hour: Int,
    val percentage: String,
)
