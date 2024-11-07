package com.vsca.vsnapvoicecollege.Model

data class Chat_Student(
    val Message: String,
    val Status: Int,
    val `data`: List<ChatStudent_List>
)