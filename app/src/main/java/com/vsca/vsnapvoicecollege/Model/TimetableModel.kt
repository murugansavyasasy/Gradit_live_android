
package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.SerializedName

data class TimetableResponse(

    @SerializedName("status")
    val status: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ArrayList<TimetableModel>
)

data class TimetableModel(

    @SerializedName("name")
    val name: String? = "",

    @SerializedName("start_time")
    val startTime: String? = "",

    @SerializedName("end_time")
    val endTime: String? = "",

    @SerializedName("duration")
    val duration: String? = "",

    @SerializedName("order")
    val order: Int = 0,

    @SerializedName("hour_type")
    val hourType: String? = "",

    @SerializedName("subject_name")
    val subjectName: String? = "",

    @SerializedName("staff_name")
    val staffName: String? = "",

    @SerializedName("facalty_name")
    val facultyName: String? = ""
)
