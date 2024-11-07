package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Model.Get_staff_yourclass
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil


class Subject_Adapter(data: List<Get_staff_yourclass>, context: Context) :
    RecyclerView.Adapter<Subject_Adapter.MyViewHolder>() {
    var subjectdata: List<Get_staff_yourclass> = ArrayList()
    var context: Context
    private val selectCheck = ArrayList<Int>()
    var YearId = java.util.ArrayList<String>()

    //    var iSubjectId = ArrayList<String>()
    var YearId_String: String? = null
    var DepartmentId = java.util.ArrayList<String>()
    var DepartmentId_String: String? = null
    var SectionId = java.util.ArrayList<String>()
    var CourseId = java.util.ArrayList<String>()
    var CourseId_String: String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): Subject_Adapter.MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.staffselection_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: Get_staff_yourclass = subjectdata.get(position)

        CommonUtil.seleteddataArraySection.clear()

        holder.txt_financeandaccounding!!.text = data.coursename
        holder.txt_bcom_Accounts!!.text = data.yearname
        holder.txt_year1!!.text = data.semestername
        holder.txt_semester1!!.text = data.sectionname
        holder.txt_date!!.text = data.subjectname
        CommonUtil.semesterid = ""


        if (CommonUtil.Screenname == "Forward") {

            Log.d("Assignment","Forward")
            if (selectCheck[position] == 1) {
                holder.check_sections!!.isChecked = true
            } else {
                holder.check_sections!!.isChecked = false
            }


            holder.check_sections!!.setOnClickListener(View.OnClickListener {
                for (k in selectCheck.indices) {
                    if (k == position) {
                        selectCheck[k] = 1
                    } else {
                        selectCheck[k] = 0
                    }
                }
                notifyDataSetChanged()
            })

            holder.check_sections!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked) {

                    CommonUtil.receivertype = "5"
                    CommonUtil.receiverid = data.sectionid.toString()
                    CommonUtil.YearId = data.yearid.toString()
                    CommonUtil.SectionId = data.sectionid.toString()
                    CommonUtil.Courseid = data.courseid.toString()
                    CommonUtil.semesterid = data.semesterid.toString()
                    CommonUtil.SubjectID = data.subjectid.toString()
                    CommonUtil.DepartmentChooseIds.add(data.sectionid.toString())
                    holder.check_sections!!.isChecked = true
                    CommonUtil.seleteddataArraySection.add(data.sectionid.toString())
                    CommonUtil.year_ = data.yearname.toString()
                    CommonUtil.semester_ = data.semestername.toString()
                    CommonUtil.section_ = data.sectionname.toString()
                    CommonUtil.courseName_ = data.coursename.toString()
                } else {
                    CommonUtil.DepartmentChooseIds.remove(data.sectionid.toString())
                    CommonUtil.seleteddataArraySection.remove(data.sectionid.toString())
                    holder.check_sections!!.isChecked = false

                }

                for (i in CommonUtil.seleteddataArraySection) {
                    CommonUtil.seletedStringdata =
                        CommonUtil.seleteddataArraySection.joinToString("~")
                }
                CommonUtil.receiverid = CommonUtil.seletedStringdata.toString()

                Log.d("CommonUtil.receiverid", CommonUtil.receiverid)


            })

        } else {
            Log.d("Assignment","NotForward")
            holder.check_sections!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked) {

                    CommonUtil.receivertype = "5"


                    CommonUtil.receiverid = data.sectionid.toString()
                    CommonUtil.YearId = data.yearid.toString()
                    CommonUtil.SectionId = data.sectionid.toString()
                    CommonUtil.Courseid = data.courseid.toString()
                    CommonUtil.semesterid = data.semesterid.toString()
                    CommonUtil.SubjectID = data.subjectid.toString()
                    CommonUtil.deptid = data.departmentid.toString()
                    CommonUtil.seleteddataArraySection.add(data.sectionid.toString())
                    data.sectionid?.let { CommonUtil.DepartmentChooseIds.add(it) }
                    CommonUtil.iSubjectId.add(data.sectionid.toString() + ":" + data.subjectid)

                    holder.check_sections!!.isChecked = true

                    YearId.add(data.yearid.toString())
                    DepartmentId.add(data.departmentid.toString())
                    CourseId.add(data.courseid.toString())
                    CommonUtil.year_ = data.yearname.toString()
                    CommonUtil.semester_ = data.semestername.toString()
                    CommonUtil.section_ = data.sectionname.toString()
                    CommonUtil.courseName_ = data.coursename.toString()


                } else {

                    data.sectionid?.let { CommonUtil.DepartmentChooseIds.remove(it) }
                    CommonUtil.seleteddataArraySection.remove(data.sectionid.toString())
                    CommonUtil.iSubjectId.remove(data.sectionid.toString() + ":" + data.subjectid)
                    holder.check_sections!!.isChecked = false
                    YearId.remove(data.yearid.toString())
                    DepartmentId.remove(data.departmentid.toString())
                    CourseId.remove(data.courseid.toString())
                }
                Log.d("DepartmentChooseIds", CommonUtil.DepartmentChooseIds.size.toString())
                for (i in CommonUtil.seleteddataArraySection) {
                    CommonUtil.seletedStringdata =
                        CommonUtil.seleteddataArraySection.joinToString("~")
                }

                //Year Id
                for (i in YearId.indices) {
                    YearId_String = YearId.joinToString("~")
                }

                for (i in DepartmentId.indices) {
                    DepartmentId_String = DepartmentId.joinToString("~")
                }

                for (i in CourseId.indices) {
                    CourseId_String = CourseId.joinToString("~")
                }

                if (CourseId.size > 0) {
                    CommonUtil.Courseid = CourseId_String.toString()
                }
                if (DepartmentId.size > 0) {
                    CommonUtil.deptid = DepartmentId_String.toString()
                }

                if (YearId.size > 0) {
                    CommonUtil.YearId = YearId_String.toString()
                }

                if (CommonUtil.seleteddataArraySection.size > 0) {
                    CommonUtil.receiverid = CommonUtil.seletedStringdata
                    Log.d("Section_Id_", CommonUtil.receiverid)
                } else {
                    CommonUtil.receiverid = ""
                }
                for (position in CommonUtil.iSubjectId.indices) {
                    CommonUtil.isSubjectIds = CommonUtil.iSubjectId.joinToString("~")
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return subjectdata.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {
        @JvmField
        @BindView(R.id.txt_financeandaccounding)
        var txt_financeandaccounding: TextView? = null

        @JvmField
        @BindView(R.id.txt_bcom_Accounts)
        var txt_bcom_Accounts: TextView? = null

        @JvmField
        @BindView(R.id.txt_year1)
        var txt_year1: TextView? = null

        @JvmField
        @BindView(R.id.txt_semester1)
        var txt_semester1: TextView? = null

        @JvmField
        @BindView(R.id.txt_date)
        var txt_date: TextView? = null

        @JvmField
        @BindView(R.id.check_sections)
        var check_sections: CheckBox? = null

        @JvmField
        @BindView(R.id.txt_selectspecfic)
        var txt_selectspecfic: TextView? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        subjectdata = data
        this.context = context

        for (i in 0 until subjectdata.size) {
            selectCheck.add(0)
        }
    }
}