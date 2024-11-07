package com.vsca.vsnapvoicecollege.Model

data class SenderSide_Chat(
    val answer: String,
    val answeredon: String,
    val changeanswer: String,
    val createdon: String,
    val is_student_blocked: Any,
    val question: String,
    val questionid: String,
    val replytype: String,
    val studentid: String,
    val studentname: String
)