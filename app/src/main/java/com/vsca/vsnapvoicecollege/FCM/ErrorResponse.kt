package com.vsca.vsnapvoicecollege.FCM

data class ErrorResponse(
    val status: Boolean,
    val message: String,
    val statusCode: Int
)