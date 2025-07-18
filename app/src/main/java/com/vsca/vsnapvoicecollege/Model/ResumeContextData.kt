package com.vsca.vsnapvoicecollege.Model

data class ResumeContextData(
    val memberId: String,
    val memberName: String,
    val memberstudentEmail: String,
    val memberPhoneNumber: String,
    val memberPermanentAddress1: String?=null,
    val skills: List<String> = emptyList(),
    val education: List<GetEducationalDetailsData> = emptyList(),
    val internship: List<GetInternshipDetailsData>?=null,
    val areaInterest: List<String> = emptyList(),
    val softSkill: List<String> = emptyList(),
    val certifications: List<GetCertificateDetailsData>? = emptyList(),
    val languages: List<String> = emptyList(),
    val projects: List<GetProjectDetailsData>? = emptyList()
)


//
//data class ResumeContextData(
//    val memberId: String?=null,
//    val memberName: String?=null,
//    val memberPhoneNumber: String?=null,
//    val memberstudentEmail: String?=null,
//    val softSkill: String?=null,
//    val educationalDetails: List<GetEducationalDetailsData>,
//    val internship: List<GetInternshipDetailsData>?=null,
//    val areaInterest: String?=null,
//    val certifications: List<GetCertificateDetailsData>?=null,
//    val languages: String?=null,
//    val projects: List<GetProjectDetailsData>?=null,
//
//)
