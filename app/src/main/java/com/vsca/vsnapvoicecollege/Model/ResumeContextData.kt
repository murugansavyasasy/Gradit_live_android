package com.vsca.vsnapvoicecollege.Model

data class ResumeContextData(
    val idMember: String,
    val name: String,
    val email: String,
    val phone: String,
    val address: String?=null,
    val skills: List<String> = emptyList(),
    val education: List<EducationFormattedData> = emptyList(),
    val experience: List<InternshipFormattedData>,
    val areainterest: List<String> = emptyList(),
    val softSkill: List<String> = emptyList(),
    val certifications: List<CertificateFormattedData> = emptyList(),
    val languages: List<String> = emptyList(),
    val projects: List<String> = emptyList()
)



