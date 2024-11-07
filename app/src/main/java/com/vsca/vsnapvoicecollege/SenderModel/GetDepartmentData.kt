package com.vsca.vsnapvoicecollege.SenderModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetDepartmentData {

    @SerializedName("department_code")
    @Expose
    var department_code: String? = null

    @SerializedName("department_name")
    @Expose
    var department_name: String? = null

    @SerializedName("department_id")
    @Expose
    var department_id: String? = null

    @SerializedName("division_id")
    @Expose
    var division_id: String? = null

    @SerializedName("division_name")
    @Expose
    var division_name: String? = null


}