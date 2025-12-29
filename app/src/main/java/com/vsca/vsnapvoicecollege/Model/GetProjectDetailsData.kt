package com.vsca.vsnapvoicecollege.Model

import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.AttachmentHolder

//data class GetProjectDetailsData(
//    var title: String,
//    var file_path: MutableList<FilePath>? = mutableListOf()
//    )

data class GetProjectDetailsData(
    var title: String,
    override var file_path: MutableList<FilePath>? = mutableListOf()
) : AttachmentHolder
