package com.vsca.vsnapvoicecollege.Model

data class HallticketResponse(

    val course_code: String,
    val course_name: String,
    val department: String,
    val dob: String,
    val register_number: String,
    val student_name: String,
    val current_sem: String,
    val subject_sem_number: String,
    val subject_code: String,
    val subject_name: String,
    val exam_date: String,
    val exam_time: String,
    val arrear_regular: String,
    val course_wise_attendance: String,
    val overall_semester_attendance: String,
    val condonation_paid: String,
    val student_image: String,
    val college_logo: String,

    )
