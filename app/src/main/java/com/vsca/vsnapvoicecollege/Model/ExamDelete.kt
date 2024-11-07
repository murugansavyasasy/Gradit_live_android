package com.vsca.vsnapvoicecollege.Model

data class ExamDelete(
    val Message: String,
    val Status: Int,
    val `data`: List<Examdeletedata>
)