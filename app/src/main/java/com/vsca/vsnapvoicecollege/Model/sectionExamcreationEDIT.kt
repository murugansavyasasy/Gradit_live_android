package com.vsca.vsnapvoicecollege.Model

class sectionExamcreationEDIT {

    var clgsectionid: String? = null
    var SubjectExamcreationEDIT = ArrayList<SubjectExamcreationEDIT>()


    constructor(
        clgsectionid: String?,
        SubjectExamcreationEDIT: ArrayList<SubjectExamcreationEDIT>

    ) {
        this.clgsectionid = clgsectionid
        this.SubjectExamcreationEDIT = SubjectExamcreationEDIT
    }
}
