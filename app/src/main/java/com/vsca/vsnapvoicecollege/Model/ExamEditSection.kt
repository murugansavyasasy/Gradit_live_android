package com.vsca.vsnapvoicecollege.Model

data class ExamEditSection(

    val Message: String,
    val Status: Int,
    val `data`: ArrayList<ExamSectionEditorDelete>
)
