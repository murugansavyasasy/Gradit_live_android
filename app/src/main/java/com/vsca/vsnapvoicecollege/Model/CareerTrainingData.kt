package com.vsca.vsnapvoicecollege.Model

data class CareerTrainingData(
    val careerId: Int,
    val trainingTitle: String,
    val trainingAbout: String,
    val trainingDate: String,
    val modeTraining: String,
    val startTime: String,
    val endTime: String,
    val venue: String,
    val trainerName: String,
    val applicableBatches: List<String>,
    val recursiveTraining: Boolean,
    val repeatTraining: String,
    val selectDay: String,
    val repeatUntill: String,
    val createdOn: String,
    val departmentName: List<String>,
    val semesterNo: List<String>
)