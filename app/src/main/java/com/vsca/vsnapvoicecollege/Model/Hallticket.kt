package com.vsca.vsnapvoicecollege.Model

data class Hallticket(
    val Message: String,
    val Status: Int,
    val data: List<HallticketResponse>
)
