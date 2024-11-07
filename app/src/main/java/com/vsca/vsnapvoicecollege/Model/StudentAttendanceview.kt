package com.vsca.vsnapvoicecollege.Model

data class StudentAttendanceview(
    val Status: Int,
    val Message: String,
    val data: ArrayList<attendance>,
)

data class attendance(
    val attended_hour_no: Int,
    val absent_hour_no: Int,
    val attended_date: String,
)