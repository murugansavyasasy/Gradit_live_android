package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Activities.Spectfice_TakeAttendance
import com.vsca.vsnapvoicecollege.Interfaces.Attendance_EditClickLisitiner
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.SenderModel.Attendance_Edit_Selected
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class Attendance_Edit_Adapter constructor(
    data: List<Attendance_Edit_Selected>,
    context: Context,
    var checkListener: Attendance_EditClickLisitiner
) :
    RecyclerView.Adapter<Attendance_Edit_Adapter.MyViewHolder>() {
    var context: Context
    var isSelectedAll = false
    var Number: String? = "0"
    var Position: Int = 0
    var selectedList: List<Attendance_Edit_Selected> = ArrayList()

    companion object {
        var checkClick: Attendance_EditClickLisitiner? = null
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {

        val lblDocumentName: TextView = itemView!!.findViewById(R.id.lblRecipientData)!!
        val chbox: CheckBox = itemView!!.findViewById(R.id.chbox)!!
        val lblStudentname: TextView = itemView!!.findViewById(R.id.lbl_studentname)!!
        val lblRegNo1: TextView = itemView!!.findViewById(R.id.lblRegNo1)!!
        val lblRegNo: TextView = itemView!!.findViewById(R.id.lblRegNo)!!
        val layoutstudentlist: ConstraintLayout = itemView!!.findViewById(R.id.layoutEntireCollege)!!
        val con_attendance: RelativeLayout = itemView!!.findViewById(R.id.con_attendance)!!
        val switchOD: SwitchCompat = itemView!!.findViewById(R.id.switchOD)!!
        val switchOnLeave: SwitchCompat = itemView!!.findViewById(R.id.switchOnLeave)!!
        val img_mark_attendance: TextView = itemView!!.findViewById(R.id.img_mark_attendance)!!

    }


    init {
        selectedList = data
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

//        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.specific_student, parent, false)
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.recipientlist_ui, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: Attendance_Edit_Selected = selectedList[position]
        Position = holder.absoluteAdapterPosition

        checkClick = checkListener
        (context as? Spectfice_TakeAttendance)?.updateMasterCheckboxesEdit()
        holder.layoutstudentlist!!.visibility = View.GONE
        holder.con_attendance!!.visibility = View.VISIBLE
        holder.lblStudentname!!.text = data.membername
        holder.lblRegNo!!.text = "Register No : " + data.rollno
        CommonUtil.Absentlistcount = ""
        if (data.attendancetype.equals("Absent")) {
            holder.switchOD.isChecked = false
            holder.switchOnLeave.isChecked =false
            holder.img_mark_attendance.text = "A"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_red)
        } else if (data.attendancetype.equals("Present")) {
            holder.switchOD.isChecked = false
            holder.switchOnLeave.isChecked =false
            holder.img_mark_attendance.text = "P"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_green)
        } else if (data.attendancetype.equals("OnDuty")) {
            holder.switchOD.isChecked = true
            holder.switchOnLeave.isChecked =false
            holder.img_mark_attendance.text = "OD"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_yellow)
        }else{
            holder.switchOnLeave.isChecked = true
            holder.switchOD.isChecked = false
            holder.img_mark_attendance.text = "L"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_blue)
        }

        if (CommonUtil.PresentlistStudent.contains(data.memberid.toString())) {
            holder.switchOD.isChecked = false
            holder.switchOnLeave.isChecked = false
            holder.img_mark_attendance.text = "P"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_green)
        } else if (CommonUtil.AbsendlistStudent.contains(data.memberid.toString())) {
            holder.switchOD.isChecked = false
            holder.switchOnLeave.isChecked = false
            holder.img_mark_attendance.text = "A"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_red)
        } else if (CommonUtil.isOnLeaveStudentList.contains(data.memberid.toString())) {
            holder.switchOD.isChecked = false
            holder.switchOnLeave.isChecked = true
            holder.img_mark_attendance.text = "L"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_blue)
        } else if (CommonUtil.OnDutylistStudent.contains(data.memberid.toString())) {
            holder.switchOD.isChecked = true
            holder.switchOnLeave.isChecked = false
            holder.img_mark_attendance.text = "OD"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_yellow)
        }

        holder.switchOnLeave.setOnClickListener {
            holder.switchOD.isChecked=false
            if (holder.switchOnLeave.isChecked) {
                holder.img_mark_attendance.setBackgroundResource(R.drawable.round_blue)
                holder.img_mark_attendance.text = "L"
                checkClick?.remove(data)
                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
                CommonUtil.AbsendlistStudent.remove(data.memberid.toString())
                CommonUtil.OnDutylistStudent.remove(data.memberid.toString())
                CommonUtil.isOnLeaveStudentList.add(data.memberid.toString())
            } else {
                checkClick?.remove(data)
                holder.img_mark_attendance.text = "A"
                holder.img_mark_attendance!!.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_red)
                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
                CommonUtil.AbsendlistStudent.add(data.memberid.toString())
                CommonUtil.OnDutylistStudent.remove(data.memberid.toString())
                CommonUtil.isOnLeaveStudentList.remove(data.memberid.toString())
            }
            (context as? Spectfice_TakeAttendance)?.updateMasterCheckboxesEdit()
        }


        holder.switchOD.setOnClickListener {
            holder.switchOnLeave.isChecked=false
            if (holder.switchOD.isChecked) {
                holder.img_mark_attendance.setBackgroundResource(R.drawable.round_yellow)
                holder.img_mark_attendance.text = "OD"
                checkClick?.remove(data)
                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
                CommonUtil.AbsendlistStudent.remove(data.memberid.toString())
                CommonUtil.OnDutylistStudent.add(data.memberid.toString())
                CommonUtil.isOnLeaveStudentList.remove(data.memberid.toString())
            } else {
                checkClick?.remove(data)
                holder.img_mark_attendance.text = "A"
                holder.img_mark_attendance!!.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_red)
                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
                CommonUtil.AbsendlistStudent.add(data.memberid.toString())
                CommonUtil.OnDutylistStudent.remove(data.memberid.toString())
                CommonUtil.isOnLeaveStudentList.remove(data.memberid.toString())
            }
            (context as? Spectfice_TakeAttendance)?.updateMasterCheckboxesEdit()
        }

        holder.img_mark_attendance!!.setOnClickListener {
            holder.switchOD.isChecked = false
            holder.switchOnLeave.isChecked = false
            if (holder.img_mark_attendance!!.background.constantState!! == context.getDrawable(R.drawable.round_green)!!.constantState
            ) {
                checkClick?.remove(data)
                holder.img_mark_attendance.text = "A"
                holder.img_mark_attendance!!.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_red)
                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
                CommonUtil.OnDutylistStudent.remove(data.memberid.toString())
                CommonUtil.AbsendlistStudent.add(data.memberid.toString())
                CommonUtil.isOnLeaveStudentList.remove(data.memberid.toString())
            } else {
               checkClick?.add(data)
                holder.img_mark_attendance.text = "P"
                holder.img_mark_attendance!!.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_green)
                CommonUtil.AbsendlistStudent.remove(data.memberid.toString())
                CommonUtil.OnDutylistStudent.remove(data.memberid.toString())
                CommonUtil.isOnLeaveStudentList.remove(data.memberid.toString())
                CommonUtil.PresentlistStudent.add(data.memberid.toString())
            }
            (context as? Spectfice_TakeAttendance)?.updateMasterCheckboxesEdit()
        }
    }

    fun selectAll() {
        isSelectedAll = true
        CommonUtil.PresentlistStudent.clear()
        for (i in selectedList) {
            CommonUtil.PresentlistStudent.add(i.memberid.toString())
        }
        (context as? Spectfice_TakeAttendance)?.updateMasterCheckboxesEdit()
        CommonUtil.AbsendlistStudent.clear()
        notifyDataSetChanged()
    }

    fun unselectall() {
        isSelectedAll = false

        for (i in selectedList) {
            CommonUtil.AbsendlistStudent.add(i.memberid.toString())
            CommonUtil.PresentlistStudent.remove(i.memberid.toString())
        }
        (context as? Spectfice_TakeAttendance)?.updateMasterCheckboxesEdit()
        notifyDataSetChanged()
    }

    fun filterList(filterlist: java.util.ArrayList<Attendance_Edit_Selected>, isHandle: Boolean) {
        selectedList = filterlist
        notifyDataSetChanged()
    }

    fun isOnDuty(isOnDutyChecked: Boolean) {

        CommonUtil.AbsendlistStudent.clear()
        CommonUtil.OnDutylistStudent.clear()
        CommonUtil.PresentlistStudent.clear()
        CommonUtil.isOnLeaveStudentList.clear()
        if (isOnDutyChecked) {
            for (i in selectedList) {
                CommonUtil.OnDutylistStudent.add(i.memberid.toString())
            }
        } else {
            for (i in selectedList) {
                CommonUtil.AbsendlistStudent.add(i.memberid.toString())
            }
        }

        notifyDataSetChanged()
    }

    fun isOnLeave(isOnLeaveChecked: Boolean) {

        CommonUtil.AbsendlistStudent.clear()
        CommonUtil.OnDutylistStudent.clear()
        CommonUtil.PresentlistStudent.clear()
        CommonUtil.isOnLeaveStudentList.clear()
        if (isOnLeaveChecked) {
            for (i in selectedList) {
                CommonUtil.isOnLeaveStudentList.add(i.memberid.toString())
            }
        } else {
            for (i in selectedList) {
                CommonUtil.AbsendlistStudent.add(i.memberid.toString())
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return selectedList.size
    }
}