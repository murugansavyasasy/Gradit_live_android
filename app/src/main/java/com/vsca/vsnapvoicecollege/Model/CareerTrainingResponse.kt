package com.vsca.vsnapvoicecollege.Model

data class CareerTrainingResponse(
    val status: Boolean,
    val message: String,
    val data: List<CareerTrainingData>
)