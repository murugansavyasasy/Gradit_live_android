package com.vsca.vsnapvoicecollege.Model

data class VoiceSendCommunication(
    val Message: String,
    val Status: Int,
    val `data`: List<VoiceSendModelClass>
)