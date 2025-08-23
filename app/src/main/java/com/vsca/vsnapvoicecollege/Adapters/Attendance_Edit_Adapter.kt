package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

        val img_mark_attendance: ImageView = itemView!!.findViewById(R.id.img_mark_attendance)
        val con_attendance: RelativeLayout = itemView!!.findViewById(R.id.con_attendance)
        val lbl_studentname: TextView = itemView!!.findViewById(R.id.lbl_studentname)
        val lbl_RTegNo: TextView = itemView!!.findViewById(R.id.lbl_RTegNo)
        val layoutstudentlist: ConstraintLayout = itemView!!.findViewById(R.id.layoutstudentlist)
        val switchOD: SwitchCompat = itemView!!.findViewById(R.id.switchOD)

    }


    init {
        selectedList = data
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.specific_student, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: Attendance_Edit_Selected = selectedList[position]
        Position = holder.absoluteAdapterPosition

        checkClick = checkListener
        holder.lbl_studentname!!.text = data.membername
        holder.lbl_RTegNo.text = "Register No : " + data.rollno
        CommonUtil.Absentlistcount = ""
        holder.layoutstudentlist!!.visibility = View.GONE
        holder.con_attendance!!.visibility = View.VISIBLE

//        holder.switchOD.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                holder.switchOD.thumbTintList = ContextCompat.getColorStateList(context, R.color.clr_principal)
//                holder.switchOD.trackTintList = ContextCompat.getColorStateList(context, R.color.clr_bg_grey)
//                holder.img_mark_attendance!!.setImageResource(R.drawable.attendance)
//                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
//                CommonUtil.AbsendlistStudent.remove(data.memberid.toString())
//                CommonUtil.ODlistStudent.add(data.memberid.toString())
//
//            } else {
//                holder.switchOD.thumbTintList = ContextCompat.getColorStateList(context, R.color.black)
//                holder.switchOD.trackTintList = ContextCompat.getColorStateList(context, R.color.clr_bg_grey)
//                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
//                CommonUtil.AbsendlistStudent.remove(data.memberid.toString())
//                CommonUtil.ODlistStudent.remove(data.memberid.toString())
//            }
//        }

        holder.switchOD.setOnCheckedChangeListener(null) // avoid unwanted triggers during recycle
        holder.switchOD.isChecked = CommonUtil.ODlistStudent.contains(data.memberid.toString())

        holder.switchOD.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Mark OD
                holder.switchOD.thumbTintList = ContextCompat.getColorStateList(context, R.color.clr_principal)
                holder.switchOD.trackTintList = ContextCompat.getColorStateList(context, R.color.clr_bg_grey)

                // Reset attendance image when OD is selected
                holder.img_mark_attendance!!.setImageResource(R.drawable.attendance)

                // Update lists
                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
                CommonUtil.AbsendlistStudent.remove(data.memberid.toString())
                CommonUtil.ODlistStudent.add(data.memberid.toString())
            } else {
                // Unmark OD
                holder.switchOD.thumbTintList = ContextCompat.getColorStateList(context, R.color.black)
                holder.switchOD.trackTintList = ContextCompat.getColorStateList(context, R.color.clr_bg_grey)


                // Keep default icon
                holder.img_mark_attendance!!.setImageResource(R.drawable.absend_img)
                CommonUtil.AbsendlistStudent.add(data.memberid.toString())
                CommonUtil.ODlistStudent.remove(data.memberid.toString())
            }
        }





        if (data.attendancetype.equals("Absent")) {
            holder.img_mark_attendance!!.setImageResource(R.drawable.absend_img)
        } else if (data.attendancetype.equals("Present")) {
            holder.img_mark_attendance!!.setImageResource(R.drawable.present)
        }
        else if (data.attendancetype.equals("OnDuty")){
            holder.img_mark_attendance!!.setImageResource(R.drawable.attendance)

        }

        if (CommonUtil.PresentlistStudent.contains(data.memberid.toString())) {
            holder.img_mark_attendance!!.setImageResource(R.drawable.present)
        }
        else  if (CommonUtil.AbsendlistStudent.contains(data.memberid.toString())) {
            holder.img_mark_attendance!!.setImageResource(R.drawable.absend_img)
        }
        else  if(CommonUtil.ODlistStudent.contains(data.memberid.toString())){
            holder.img_mark_attendance!!.setImageResource(R.drawable.attendance)
        }

        holder.img_mark_attendance!!.setOnClickListener {
            if (holder.switchOD.isChecked) {
                // If OD is ON, disable toggle
                holder.switchOD.isChecked = false
            }

            if (holder.img_mark_attendance!!.drawable.constantState == context.getDrawable(R.drawable.present)!!.constantState) {
                // Change to Absent
                checkClick?.remove(data)
                holder.img_mark_attendance!!.setImageResource(R.drawable.absend_img)

                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
                CommonUtil.AbsendlistStudent.add(data.memberid.toString())
                CommonUtil.ODlistStudent.remove(data.memberid.toString())
            } else {
                // Change to Present
                checkClick?.add(data)
                holder.img_mark_attendance!!.setImageResource(R.drawable.present)

                CommonUtil.AbsendlistStudent.remove(data.memberid.toString())
                CommonUtil.PresentlistStudent.add(data.memberid.toString())
                CommonUtil.ODlistStudent.remove(data.memberid.toString())
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
//                CommonUtil.ODlistStudent.remove(data.memberid.toString())
//
//            } else {
//                checkClick?.add(data)
//                holder.img_mark_attendance!!.setImageResource(R.drawable.present)
//                CommonUtil.AbsendlistStudent.remove(data.memberid.toString())
//                CommonUtil.PresentlistStudent.add(data.memberid.toString())
//                CommonUtil.ODlistStudent.remove(data.memberid.toString())
//            }
//        }
    }

    fun isPresentSelected() {
//        isSelectedAll = true
        CommonUtil.PresentlistStudent.clear()
        for (i in selectedList) {
            CommonUtil.PresentlistStudent.add(i.memberid.toString())
        }
        CommonUtil.AbsendlistStudent.clear()
        CommonUtil.ODlistStudent.clear()
        notifyDataSetChanged()
    }

    fun isAbsentSelected() {
//        isSelectedAll = false

        CommonUtil.AbsendlistStudent.clear()
        for (i in selectedList) {
            CommonUtil.AbsendlistStudent.add(i.memberid.toString())
        }
        CommonUtil.PresentlistStudent.clear()
        CommonUtil.ODlistStudent.clear()
        notifyDataSetChanged()
    }

    fun isODSelected() {
//        isSelectedAll = false
        CommonUtil.ODlistStudent.clear()
        for (i in selectedList) {
            CommonUtil.ODlistStudent.add(i.memberid.toString())
        }
        CommonUtil.PresentlistStudent.clear()
        CommonUtil.AbsendlistStudent.clear()

        notifyDataSetChanged()
    }

    fun filterList(filterlist: java.util.ArrayList<Attendance_Edit_Selected>, isHandle: Boolean) {
        selectedList = filterlist
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return selectedList.size
    }
}