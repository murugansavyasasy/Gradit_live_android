package com.vsca.vsnapvoicecollege.Model

data class ExamSubjectList(
    val Status: Int,
    val Message: String,
    val data: List<ExamSubjectSubList>,
)
