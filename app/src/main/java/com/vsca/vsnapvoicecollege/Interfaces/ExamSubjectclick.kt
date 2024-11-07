package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.Adapters.Examlist_viewAdapter
import com.vsca.vsnapvoicecollege.Model.examlist

interface ExamSubjectclick {
    fun onSubjeckClicklistener(holder: Examlist_viewAdapter.MyViewHolder, item: examlist)
}