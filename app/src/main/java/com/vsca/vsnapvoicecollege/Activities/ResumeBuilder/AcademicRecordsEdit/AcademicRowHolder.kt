package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.AcademicRecordsEdit

import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.AttachmentAdapter
import com.vsca.vsnapvoicecollege.Model.FilePath

data class AcademicRowHolder(
    val attachments: MutableList<FilePath>,
    val adapter: AttachmentAdapter,
    val recyclerView: RecyclerView
)
