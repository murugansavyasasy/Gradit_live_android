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
        val img_mark_attendance: TextView = itemView!!.findViewById(R.id.img_mark_attendance)!!

//        val con_attendance: RelativeLayout = itemView!!.findViewById(R.id.con_attendance)
//        val lbl_studentname: TextView = itemView!!.findViewById(R.id.lbl_studentname)
//        val lbl_RTegNo: TextView = itemView!!.findViewById(R.id.lbl_RTegNo)
//        val layoutstudentlist: ConstraintLayout = itemView!!.findViewById(R.id.layoutstudentlist)
//
//        val switchOD: SwitchCompat = itemView!!.findViewById(R.id.switchOD)!!
//        val img_mark_attendance: TextView = itemView!!.findViewById(R.id.img_mark_attendance)!!
//

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
//        holder.lbl_studentname!!.text = data.membername
//        holder.lbl_RTegNo.text = "Register No : " + data.rollno
//        CommonUtil.Absentlistcount = ""
//        holder.layoutstudentlist!!.visibility = View.GONE
//        holder.con_attendance!!.visibility = View.VISIBLE

        holder.layoutstudentlist!!.visibility = View.GONE
        holder.con_attendance!!.visibility = View.VISIBLE
        holder.lblStudentname!!.text = data.membername
        holder.lblRegNo!!.text = "Register No : " + data.rollno
        CommonUtil.Absentlistcount = ""

        if (data.attendancetype.equals("Absent")) {
            holder.switchOD.isChecked = false
            holder.img_mark_attendance.text = "A"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_red)
        } else if (data.attendancetype.equals("Present")) {
            holder.switchOD.isChecked = false
            holder.img_mark_attendance.text = "P"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_green)
        } else {
            holder.switchOD.isChecked = true
            holder.img_mark_attendance.text = "OD"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_yellow)
        }


        if (CommonUtil.PresentlistStudent.contains(data.memberid.toString())) {
            holder.switchOD.isChecked = false
            holder.img_mark_attendance.text = "P"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_green)
        } else if (CommonUtil.AbsendlistStudent.contains(data.memberid.toString())) {
            holder.switchOD.isChecked = false
            holder.img_mark_attendance.text = "A"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_red)
        } else {
            holder.switchOD.isChecked = true
            holder.img_mark_attendance.text = "OD"
            holder.img_mark_attendance!!.background =
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_yellow)
        }


//        if (CommonUtil.PresentlistStudent.contains(data.memberid.toString())) {
//            holder.img_mark_attendance!!.setImageResource(R.drawable.present)
//        } else {
//            holder.img_mark_attendance!!.setImageResource(R.drawable.absend_img)
//        }

        holder.switchOD.setOnClickListener {
            if (holder.switchOD.isChecked) {
                holder.img_mark_attendance.setBackgroundResource(R.drawable.round_yellow)
                holder.img_mark_attendance.text = "OD"
                checkClick?.remove(data)
                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
                CommonUtil.AbsendlistStudent.remove(data.memberid.toString())
                CommonUtil.OnDutylistStudent.add(data.memberid.toString())
            } else {
                checkClick?.remove(data)
                holder.img_mark_attendance.text = "A"
                holder.img_mark_attendance!!.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_red)
                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
                CommonUtil.AbsendlistStudent.add(data.memberid.toString())
                CommonUtil.OnDutylistStudent.remove(data.memberid.toString())

            }
        }

        holder.img_mark_attendance.setOnClickListener {
            holder.switchOD.isChecked = false
            if (holder.img_mark_attendance!!.background.constantState!! == context.getDrawable(R.drawable.round_green)!!.constantState
            ) {
                checkClick?.remove(data)
                holder.img_mark_attendance.text = "A"
                holder.img_mark_attendance!!.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_red)
                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
                CommonUtil.OnDutylistStudent.remove(data.memberid.toString())
                CommonUtil.AbsendlistStudent.add(data.memberid.toString())
            } else {
                checkClick?.add(data)
                holder.img_mark_attendance.text = "P"
                holder.img_mark_attendance!!.background =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.round_green)
                CommonUtil.AbsendlistStudent.remove(data.memberid.toString())
                CommonUtil.OnDutylistStudent.remove(data.memberid.toString())
                CommonUtil.PresentlistStudent.add(data.memberid.toString())
            }
        }

//        holder.img_mark_attendance!!.setOnClickListener {
//
//            if (holder.img_mark_attendance!!.drawable.constantState!! == context.getDrawable(R.drawable.present)!!.constantState
//            ) {
//                checkClick?.remove(data)
//                holder.img_mark_attendance!!.setImageResource(R.drawable.absend_img)
//                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
//                CommonUtil.AbsendlistStudent.add(data.memberid.toString())
//            } else {
//                checkClick?.add(data)
//                holder.img_mark_attendance!!.setImageResource(R.drawable.present)
//                CommonUtil.AbsendlistStudent.remove(data.memberid.toString())
//                CommonUtil.PresentlistStudent.add(data.memberid.toString())
//            }
//        }
    }

    fun selectAll() {
        isSelectedAll = true
        CommonUtil.PresentlistStudent.clear()
        for (i in selectedList) {
            CommonUtil.PresentlistStudent.add(i.memberid.toString())
        }
        CommonUtil.AbsendlistStudent.clear()
        notifyDataSetChanged()
    }

//    fun unselectall() {
//        isSelectedAll = false
//
//        CommonUtil.AbsendlistStudent.clear()
//        for (i in selectedList) {
//            CommonUtil.AbsendlistStudent.add(i.memberid.toString())
//        }
//        CommonUtil.PresentlistStudent.clear()
//        notifyDataSetChanged()
//    }

    fun unselectall() {
        isSelectedAll = false

        for (i in selectedList) {
            CommonUtil.AbsendlistStudent.add(i.memberid.toString())
            CommonUtil.PresentlistStudent.remove(i.memberid.toString())
        }

        notifyDataSetChanged()
    }

    fun filterList(filterlist: java.util.ArrayList<Attendance_Edit_Selected>, isHandle: Boolean) {
        selectedList = filterlist
        notifyDataSetChanged()
    }

    fun isOnDuty() {
        if (CommonUtil.AbsendlistStudent.isNotEmpty()) {
            CommonUtil.AbsendlistStudent.clear()
            CommonUtil.OnDutylistStudent.clear()
            CommonUtil.PresentlistStudent.clear()
            for (i in selectedList) {
                CommonUtil.AbsendlistStudent.add(i.memberid.toString())
            }
        } else {
            CommonUtil.AbsendlistStudent.clear()
            CommonUtil.OnDutylistStudent.clear()
            CommonUtil.PresentlistStudent.clear()
            for (i in selectedList) {
                CommonUtil.OnDutylistStudent.add(i.memberid.toString())
            }
            Log.d("CommonUtil_receiverid", CommonUtil.receiverid)
        }

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return selectedList.size
    }
}