package com.vsca.vsnapvoicecollege.Model

data class GraditContectNumber(
    val Status: String,
    val Message: String,
    val data: ArrayList<Number>,
)

data class Number(
    val contact_alert_content: String,
    val contact_alert_title: String,
    val contact_display_name: String,
    val contact_numbers: String,
    val contact_button_content: String,
)