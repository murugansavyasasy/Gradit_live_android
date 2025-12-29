package com.vsca.vsnapvoicecollege.Model

import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.AttachmentHolder

//
//data class GetAssessmentDetailsData(
//    var assessment: String,
//    var score: String,
//    var file_path: MutableList<FilePath>? = mutableListOf()
//    )

data class GetAssessmentDetailsData(
    var assessment: String,
    var score: String,
    override var file_path: MutableList<FilePath>? = mutableListOf()
) : AttachmentHolder
