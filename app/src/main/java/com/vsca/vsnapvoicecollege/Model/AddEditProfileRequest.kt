package com.vsca.vsnapvoicecollege.Model

data class AddEditProfileRequest(
    val idMember: Int,
    val imagePath: String,
    val memberName: String,
    val notificationPlacement: Boolean,
    val placementStatus: String,
    val primaryMobileNo: String,
    val studentEmail: String
)