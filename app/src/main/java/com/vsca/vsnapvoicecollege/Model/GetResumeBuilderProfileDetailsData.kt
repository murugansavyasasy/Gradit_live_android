package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.SerializedName

data class GetResumeBuilderProfileDetailsData(
    val memberId: String,
    val memberName: String,
    val memberPhoneNumber: String,
    val memberstudentEmail: String,
    val memberImagePath: String?=null,
    val memberPlacementStatus: String?=null,
    val memberNotificationStatus: String?=null,
    val memberRegno: String,
    val memberDob: String,
    val memberGender: String,
    val memberQualification: String?=null,
    val memberPermanentAddress1: String,
    val memberPermanentAddressCity: String,
    val memberPermanentAddressState: String,
    val memberPermanentAddressPincode: String,
    val memberPermanentAddressCountry: String,
    val memberAdmissionNo: String,
    val departmentName: String,
    val semesterName: String,
    val noOfYear: Int,
    val courseName: String,
    )
