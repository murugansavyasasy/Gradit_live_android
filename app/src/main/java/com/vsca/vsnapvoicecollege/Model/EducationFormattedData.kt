package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.SerializedName

//data class EducationFormattedData(
//    val `class`: String? = null,   // for 10th, 12th
//    val degree: String? = null,    // for UG, PG, etc.
//    val percentage: String,
//    val institution: String
//)

data class EducationFormattedData(
    @SerializedName("class") val `class`: String? = null,
    val degree: String? = null,
    val percentage: String? = null,
    val institution: String? = null
)
