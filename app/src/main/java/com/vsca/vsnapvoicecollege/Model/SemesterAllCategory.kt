package com.vsca.vsnapvoicecollege.Model

class SemesterAllCategory {

    var category_id: String? = null
    var category_name: String? = null
    var semester_name: String? = null
    var total_credits: String? = null
    var obtained: String? = null
    var to_be_obtained: String? = null

    constructor(
        CategoryId: String?,
        Categoryname: String?,
        SemsterName: String?,
        Totalcredits: String?,
        Obtained: String?,
        tobeobtained: String?,
    ) {
        this.category_id = CategoryId
        this.category_name = Categoryname
        this.semester_name = SemsterName
        this.total_credits = Totalcredits
        this.obtained = Obtained
        this.to_be_obtained = tobeobtained
    }

}