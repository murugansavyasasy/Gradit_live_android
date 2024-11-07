package com.vsca.vsnapvoicecollege.Model

data class Chat_Text_model(
    val Message: String,
    val Status: Int,
    val data: ArrayList<chat_offset_List>
)