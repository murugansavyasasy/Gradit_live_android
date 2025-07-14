package com.vsca.vsnapvoicecollege.Model

class GetResumeBuilderAcademicDetailsData(
    val id: Int,
    val educationalDetails: List<GetEducationalDetailsData>,
    val backlogs: String,
    val numberOfArrears: String,
    val createdOn: Any,
    val modifiedOn: Any,
    val isDelete: Int,
    val idMember: Int
)