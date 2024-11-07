package com.vsca.vsnapvoicecollege.Model

data class AssignmentContent_View(
    val Message: String,
    val Status: Int,
    val `data`: List<AssignmentContent_ViewData>
)