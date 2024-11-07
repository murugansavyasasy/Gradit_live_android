package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
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
//        @JvmField
//        @BindView(R.id.lblEntiredepartment)
//        var lblEntiredepartment: TextView? = null

//        @JvmField
//        @BindView(R.id.chboxEntiredepartment)
//        var chboxEntiredepartment: CheckBox? = null

        @JvmField
        @BindView(R.id.img_mark_attendance)
        var img_mark_attendance: ImageView? = null

        @JvmField
        @BindView(R.id.con_attendance)
        var con_attendance: RelativeLayout? = null

        @JvmField
        @BindView(R.id.lbl_studentname)
        var lbl_studentname: TextView? = null


        @JvmField
        @BindView(R.id.layoutstudentlist)
        var layoutstudentlist: ConstraintLayout? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
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
        CommonUtil.Absentlistcount = ""
        holder.layoutstudentlist!!.visibility = View.GONE
        holder.con_attendance!!.visibility = View.VISIBLE
        if (data.attendancetype.equals("Absent")) {
            holder.img_mark_attendance!!.setImageResource(R.drawable.absend_img)
        } else if (data.attendancetype.equals("Present")) {
            holder.img_mark_attendance!!.setImageResource(R.drawable.present)
        }


        if (CommonUtil.PresentlistStudent.contains(data.memberid.toString())) {
            holder.img_mark_attendance!!.setImageResource(R.drawable.present)
        } else {
            holder.img_mark_attendance!!.setImageResource(R.drawable.absend_img)
        }


        holder.img_mark_attendance!!.setOnClickListener {

            if (holder.img_mark_attendance!!.drawable.constantState!! == context.getDrawable(R.drawable.present)!!.constantState
            ) {
                checkClick?.remove(data)
                holder.img_mark_attendance!!.setImageResource(R.drawable.absend_img)
                CommonUtil.PresentlistStudent.remove(data.memberid.toString())
                CommonUtil.AbsendlistStudent.add(data.memberid.toString())
            } else {
                checkClick?.add(data)
                holder.img_mark_attendance!!.setImageResource(R.drawable.present)
                CommonUtil.AbsendlistStudent.remove(data.memberid.toString())
                CommonUtil.PresentlistStudent.add(data.memberid.toString())
            }
        }
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

    fun unselectall() {
        isSelectedAll = false

        CommonUtil.AbsendlistStudent.clear()
        for (i in selectedList) {
            CommonUtil.AbsendlistStudent.add(i.memberid.toString())
        }
        CommonUtil.PresentlistStudent.clear()
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