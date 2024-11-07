package com.vsca.vsnapvoicecollege.Model

data class voicehistory(
    val Message: String,
    val Status: Int,
    val `data`: ArrayList<voicehistorydata>
)
