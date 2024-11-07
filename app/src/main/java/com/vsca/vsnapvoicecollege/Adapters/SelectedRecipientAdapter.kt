package com.vsca.vsnapvoicecollege.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Interfaces.RecipientCheckListener
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.SenderModel.RecipientSelected
import com.vsca.vsnapvoicecollege.Utils.CommonUtil


class SelectedRecipientAdapter(
    data: List<RecipientSelected>, context: Context, var checkListener: RecipientCheckListener
) : RecyclerView.Adapter<SelectedRecipientAdapter.MyViewHolder>() {
    var selectedList: List<RecipientSelected> = ArrayList()
    var context: Context
    var Position: Int = 0
    var isCheckBoxReload: Boolean = true
    var seletedStringdata: String? = null
    var isSelectedAll = false

    companion object {
        var checkClick: RecipientCheckListener? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.recipientlist_ui, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val data: RecipientSelected = selectedList[position]
        Position = holder.absoluteAdapterPosition
        checkClick = checkListener

        if (CommonUtil.AttendanceScreen == "AttendanceScreen") {
            holder.layoutstudentlist!!.visibility = View.GONE
            holder.con_attendance!!.visibility = View.VISIBLE
            holder.lbl_studentname!!.text = data.SelectedName
            CommonUtil.Absentlistcount = ""

            if (CommonUtil.PresentlistStudent.contains(data.SelectedId.toString())) {
                holder.img_mark_attendance!!.setImageResource(R.drawable.present)
            } else {
                holder.img_mark_attendance!!.setImageResource(R.drawable.absend_img)
            }

            holder.img_mark_attendance!!.setOnClickListener {

                if (holder.img_mark_attendance!!.drawable.constantState!! == context.getDrawable(R.drawable.present)!!.constantState
                ) {
                    checkClick?.remove(data)
                    holder.img_mark_attendance!!.setImageResource(R.drawable.absend_img)
                    CommonUtil.PresentlistStudent.remove(data.SelectedId.toString())
                    CommonUtil.AbsendlistStudent.add(data.SelectedId.toString())
                } else {
                    checkClick?.add(data)
                    holder.img_mark_attendance!!.setImageResource(R.drawable.present)
                    CommonUtil.AbsendlistStudent.remove(data.SelectedId.toString())
                    CommonUtil.PresentlistStudent.add(data.SelectedId.toString())
                }
            }

        } else {

            holder.layoutstudentlist!!.visibility = View.VISIBLE
            holder.con_attendance!!.visibility = View.GONE
            holder.lblDocumentName!!.text = data.SelectedName
            holder.chbox!!.isChecked = CommonUtil.receiverid.contains(data.SelectedId.toString())

            if (CommonUtil.Priority == "p1" || CommonUtil.Priority == "p7") {
                if (CommonUtil.courseType == "Department" || CommonUtil.courseType == "Course") {
                    for (i in CommonUtil.DepartmentChooseIds.indices) {
                        if (CommonUtil.DepartmentChooseIds[i] == data.SelectedId.toString()) {
                            holder.chbox!!.isChecked = true
                            Log.d("Checked", "Checked")
                        }
                    }
                }
            }


            holder.chbox!!.setOnClickListener {

                if (holder.chbox!!.isChecked) {

                    checkClick?.add(data)
                    data.SelectedId?.let { CommonUtil.seleteddataArrayCheckbox.add(it) }
                    data.SelectedId?.let { CommonUtil.DepartmentChooseIds.add(it) }
                    for (i in CommonUtil.seleteddataArrayCheckbox) {
                        seletedStringdata = CommonUtil.seleteddataArrayCheckbox.joinToString { it }
                    }
                    CommonUtil.SeletedStringdataReplace = seletedStringdata!!.replace(", ", "~")

                    if (CommonUtil.CollageIDS) {
                        data.SelectedId?.let { CommonUtil.Collageid_ArrayList.add(it) }
                    }

                } else {

                    checkClick?.remove(data)
                    data.SelectedId?.let { CommonUtil.seleteddataArrayCheckbox.remove(it) }
                    data.SelectedId?.let { CommonUtil.DepartmentChooseIds.remove(it) }
                    for (i in CommonUtil.seleteddataArrayCheckbox) {
                        seletedStringdata = CommonUtil.seleteddataArrayCheckbox.joinToString { it }
                    }

                    CommonUtil.SeletedStringdataReplace = seletedStringdata!!.replace(", ", "~")
                    if (CommonUtil.CollageIDS) {
                        data.SelectedId?.let { CommonUtil.Collageid_ArrayList.remove(it) }
                    }
                }

                if (CommonUtil.seleteddataArrayCheckbox.size > 0) {
                    CommonUtil.receiverid = CommonUtil.SeletedStringdataReplace.toString()
                    if (CommonUtil.CollageIDS) {
                        for (i in CommonUtil.Collageid_ArrayList.indices) {
                            CommonUtil.Collage_ids = CommonUtil.Collageid_ArrayList[i]
                        }
                    }
                } else {
                    CommonUtil.receiverid = ""
                }
                Log.d("CommonUtil_ReceiverID", CommonUtil.receiverid)
            }
        }
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {
        @JvmField
        @BindView(R.id.lblRecipientData)
        var lblDocumentName: TextView? = null

        @JvmField
        @BindView(R.id.lbl_studentname)
        var lbl_studentname: TextView? = null

        @JvmField
        @BindView(R.id.layoutEntireCollege)
        var layoutstudentlist: ConstraintLayout? = null

        @JvmField
        @BindView(R.id.con_attendance)
        var con_attendance: RelativeLayout? = null

        @JvmField
        @BindView(R.id.chbox)
        var chbox: CheckBox? = null

        @JvmField
        @BindView(R.id.img_mark_attendance)
        var img_mark_attendance: ImageView? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        selectedList = data
        this.context = context
    }

    fun selectAll() {
        Log.d("SelectAll", "SelectAll")
        isSelectedAll = true

        if (CommonUtil.AttendanceScreen == "AttendanceScreen") {

            for (i in selectedList) {
                CommonUtil.AbsendlistStudent.remove(i.SelectedId.toString())
                CommonUtil.PresentlistStudent.add(i.SelectedId.toString())
            }

        } else {

            CommonUtil.seleteddataArrayCheckbox.clear()

            for (i in selectedList) {
                CommonUtil.seleteddataArrayCheckbox.add(i.SelectedId.toString())
                CommonUtil.DepartmentChooseIds.add(i.SelectedId.toString())
            }

            for (i in CommonUtil.seleteddataArrayCheckbox) {
                seletedStringdata = CommonUtil.seleteddataArrayCheckbox.joinToString { it }
            }
            CommonUtil.SeletedStringdataReplace = seletedStringdata!!.replace(", ", "~")

            if (CommonUtil.seleteddataArrayCheckbox.size > 0) {
                CommonUtil.receiverid = CommonUtil.SeletedStringdataReplace.toString()

                if (CommonUtil.CollageIDS) {

                    CommonUtil.Collage_ids = CommonUtil.receiverid
                    for (i in selectedList) {
                        CommonUtil.Collageid_ArrayList.add(i.SelectedId.toString())
                    }
                    for (i in CommonUtil.Collageid_ArrayList.indices) {
                        CommonUtil.Collage_ids = CommonUtil.Collageid_ArrayList[i]
                    }
                }
            } else {
                CommonUtil.receiverid = ""
                CommonUtil.Collageid_ArrayList.clear()
            }
        }

        Log.d("CommonUtil_receiverid", CommonUtil.receiverid)

        notifyDataSetChanged()
    }

    fun unselectall() {
        isSelectedAll = false

        if (CommonUtil.AttendanceScreen.equals("AttendanceScreen")) {
            for (i in selectedList) {
                CommonUtil.AbsendlistStudent.add(i.SelectedId.toString())
                CommonUtil.PresentlistStudent.remove(i.SelectedId.toString())
            }
        } else {
            CommonUtil.receiverid = ""
            CommonUtil.Collageid_ArrayList.clear()
            CommonUtil.seleteddataArrayCheckbox.clear()
            CommonUtil.DepartmentChooseIds.clear()
        }
        Log.d("CommonUtil_receiverid", CommonUtil.receiverid)
        notifyDataSetChanged()
    }

    fun filterList(filterlist: java.util.ArrayList<RecipientSelected>, isHandle: Boolean) {
        Log.d("Search", "Search")
        selectedList = filterlist
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return selectedList.size
    }
}