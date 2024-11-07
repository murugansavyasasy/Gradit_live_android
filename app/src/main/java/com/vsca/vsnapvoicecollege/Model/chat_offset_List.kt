package com.vsca.vsnapvoicecollege.Model

data class chat_offset_List(
    val List: ArrayList<ChatList>,
    val count: String,
    val limit: String,
    val offset: String
)