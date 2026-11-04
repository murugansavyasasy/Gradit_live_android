package com.vsca.vsnapvoicecollege.Model

data class LogoutResponse(
    val Status: Boolean,
    val Message: String,
    val data: List<String>
)