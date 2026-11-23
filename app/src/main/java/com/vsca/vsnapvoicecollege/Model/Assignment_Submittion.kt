package com.vsca.vsnapvoicecollege.Model

class Assignment_Submittion(
    val Message: String,
    val Status: Int,
    val `data`: ArrayList<AssignmentSubmit>
)
data class AssignmentSubmit(
    val studentid: String,
    val assignmentdetailsid: String,
    val studentname: String,
    val register_number: String,
    val course: String,
    val year: String,
    val obtainedmark: String,
    val filearray: ArrayList<Filearray>,
    // GetSubmittedAssignmentForStudents: true = the student may delete this submission.
    // May be missing or null -> treated as false (delete icon hidden).
    val isdelete: Boolean? = null,
)

data class Filearray(
    val fileurl: String,
    val filetype: String,
)
