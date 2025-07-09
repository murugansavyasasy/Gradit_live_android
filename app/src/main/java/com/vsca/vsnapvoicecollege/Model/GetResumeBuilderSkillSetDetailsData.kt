package com.vsca.vsnapvoicecollege.Model

class GetResumeBuilderSkillSetDetailsData (
    val id: Int?=null,
    val languages: String?=null,
    val softSkill: String?=null,
    val areaInterest: String?=null,
    val internship: List<GetInternshipDetailsData>?=null,
    val programmingLanguage: String?=null,
    val toolsPlatform: String?=null,
    val certifications: List<GetCertificateDetailsData>?=null,
    val assessmentDetails: List<GetAssessmentDetailsData>?=null,
    val projects: List<GetProjectDetailsData>?=null,
    val createdOn: Any?=null,
    val modifiedOn: Any?=null,
    val isDelete: Int?=null,
    val idMember: Int?=null,
)