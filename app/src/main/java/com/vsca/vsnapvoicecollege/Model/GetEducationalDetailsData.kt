package com.vsca.vsnapvoicecollege.Model

//class GetEducationalDetailsData (
//    val percentage: String,
//    val classDegree: String,
//    val institution: String,
//)

data class GetEducationalDetailsData (
    val percentage: String,
    val classDegree: String,
    val institution: String,
    var file_path: MutableList<FilePath>?= mutableListOf()

)
