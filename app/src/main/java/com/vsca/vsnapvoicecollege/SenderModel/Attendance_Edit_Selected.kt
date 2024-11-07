package com.vsca.vsnapvoicecollege.SenderModel

import java.io.Serializable

class Attendance_Edit_Selected(
    val memberid: String?,
    var attendancetype: String?,
    var membername: String?,
    var rollno: String?
) :
    Serializable