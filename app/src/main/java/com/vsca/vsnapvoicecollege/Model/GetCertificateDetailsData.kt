package com.vsca.vsnapvoicecollege.Model

import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.AttachmentHolder

//data class GetCertificateDetailsData (
//    var courseName: String,
//    var duration: String,
//    var institute: String,
//    var file_path: MutableList<FilePath>? = mutableListOf()
//)

data class GetCertificateDetailsData (
    var courseName: String,
    var duration: String,
    var institute: String,
    override var file_path: MutableList<FilePath>? = mutableListOf()

) : AttachmentHolder