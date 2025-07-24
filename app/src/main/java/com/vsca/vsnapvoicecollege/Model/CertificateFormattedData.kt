package com.vsca.vsnapvoicecollege.Model

data class CertificateFormattedData(
    val courseName: String,
    val institute: String,
    val duration: String? = null,
    var isChecked: Boolean = false

)

