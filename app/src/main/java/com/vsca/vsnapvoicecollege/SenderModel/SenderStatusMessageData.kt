package com.vsca.vsnapvoicecollege.SenderModel


data class SenderStatusMessageData(
    val Status: Int,
    val Message: String,
    val data: Array<Any>,
)