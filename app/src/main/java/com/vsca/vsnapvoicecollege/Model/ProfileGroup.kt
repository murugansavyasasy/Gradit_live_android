package com.vsca.vsnapvoicecollege.Model


data class ProfileGroup(
    val priority: String,               // "p1", "p2", ...
    val title: String,                  // "Principal", "Student", ...
    val count: Int,
    var isExpanded: Boolean = true,
    val profile: ArrayList<LoginDetails> // original API data for this group
)