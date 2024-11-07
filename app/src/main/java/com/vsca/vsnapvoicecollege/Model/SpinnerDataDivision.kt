package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SpinnerDataDivision {

    var department_code: String? = null
    var department_name: String? = null
    var department_id: String? = null
    var division_id: String? = null
    var division_name: String? = null

    constructor(
        department_code: String?,
        department_name: String?,
        department_id: String?,
        division_id: String?,
        division_name: String?,

        ) {

        this.department_code = department_code
        this.department_name = department_name
        this.department_id = department_id
        this.division_id = division_id
        this.division_name = division_name
    }

}