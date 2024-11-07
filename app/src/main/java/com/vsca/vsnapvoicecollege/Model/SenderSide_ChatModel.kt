package com.vsca.vsnapvoicecollege.Model

data class SenderSide_ChatModel(
    val count: String,
    val data: ArrayList<Senderside_Chatdata>,
    val limit: String,
    val offset: String,
    val result: String,
    val resultmessage: String
)