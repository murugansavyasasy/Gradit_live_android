package com.vsca.vsnapvoicecollege.Model

data class Attendance_Edit(
    val Message: String,
    val Status: Int,
    val `data`: List<Attendance_edit_List>
)