package com.vsca.vsnapvoicecollege.Interfaces

import com.vsca.vsnapvoicecollege.SenderModel.Attendance_Edit_Selected

interface Attendance_EditClickLisitiner {
    fun add(data: Attendance_Edit_Selected?)
    fun remove(data: Attendance_Edit_Selected?)
}