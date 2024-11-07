package com.vsca.vsnapvoicecollege.Model

data class ValidateMobileNumberResponse(

    val is_redirect_otp_screen: Int,
    val resultvalue:Int,
    val resultmessage:String,
    val otp:String,
    val ivrnumbers:ArrayList<String>,

    )
