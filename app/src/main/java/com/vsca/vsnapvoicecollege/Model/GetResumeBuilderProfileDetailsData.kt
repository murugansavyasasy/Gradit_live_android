package com.vsca.vsnapvoicecollege.Model


data class GetResumeBuilderProfileDetailsData(
    val memberId: String?=null,
    val memberName: String?=null,
    val memberPhoneNumber: String?=null,
    val memberstudentEmail: String?=null,
    val memberImagePath: String?=null,
    val memberPlacementStatus: String?=null,
    val memberNotificationStatus: Boolean?=null,
    val memberRegno: String?=null,
    val memberDob: String?=null,
    val memberGender: String?=null,
    val memberQualification: String?=null,
    val memberPermanentAddress1: String?=null,
    val memberPermanentAddressCity: String?=null,
    val memberPermanentAddressState: String?=null,
    val memberPermanentAddressPincode: String?=null,
    val memberPermanentAddressCountry: String?=null,
    val memberAdmissionNo: String?=null,
    val departmentName: String?=null,
    val semesterName: String?=null,
    val noOfYear: Int?=null,
    val courseName: String?=null,
    )

