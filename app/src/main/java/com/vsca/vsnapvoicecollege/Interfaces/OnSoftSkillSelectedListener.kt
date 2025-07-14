package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.Model.GetAssessmentDetailsData
import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData
import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
import com.vsca.vsnapvoicecollege.Model.GetProjectDetailsData

interface OnSoftSkillSelectedListener {
    fun onSoftSkillsChanged(selectedSkills: List<String>)
    fun onInternshipListUpdated(updatedList: List<GetInternshipDetailsData>)
    fun onCertificateListUpdated(updatedCertificateList: List<GetCertificateDetailsData>)
    fun onAssessmentListUpdated(updatedAssessmentList: List<GetAssessmentDetailsData>)
    fun onProjectListUpdated(updatedProjectList: List<GetProjectDetailsData>)

}
