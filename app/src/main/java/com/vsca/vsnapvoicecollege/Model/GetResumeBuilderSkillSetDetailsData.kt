package com.vsca.vsnapvoicecollege.Model

class GetResumeBuilderSkillSetDetailsData (
    val id: Int,
    val languages: String,
    val softSkill: String,
    val areaInterest: String,
    val internship: List<GetInternshipDetailsData>,
    val programmingLanguage: String,
    val toolsPlatform: String,
    val certifications: List<GetCertificateDetailsData>,
    val assessmentDetails: List<GetAssessmentDetailsData>,
    val projects: List<GetProjectDetailsData>,
    val createdOn: Any,
    val modifiedOn: String,
    val isDelete: Int,
    val idMember: Int,
)