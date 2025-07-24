package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.SerializedName

data class EducationFormattedData(
//    @SerializedName("class") val `class`: String? = null,
    val classDegree: String? = null,
    val percentage: String? = null,
    val institution: String? = null,
    var isChecked: Boolean = false


)
