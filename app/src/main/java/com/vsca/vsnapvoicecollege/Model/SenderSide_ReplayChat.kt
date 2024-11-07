package com.vsca.vsnapvoicecollege.Model

data class SenderSide_ReplayChat(
    val Message: String,
    val Status: Int,
    val `data`: List<SenderSide_Chat>
)