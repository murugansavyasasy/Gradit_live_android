package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vsca.vsnapvoicecollege.Activities.CourseDetails
import com.vsca.vsnapvoicecollege.Model.GetCourseDetailsData
import com.vsca.vsnapvoicecollege.Model.GetExamApplicationDetails
import com.vsca.vsnapvoicecollege.Model.GetProfileDetails
import com.vsca.vsnapvoicecollege.R

class CourseDetailsAdapter : RecyclerView.Adapter<CourseDetailsAdapter.MyViewHolder> {
    private var coursedetails: List<GetCourseDetailsData> = ArrayList()
    private var studentmenuExamdetails: List<GetExamApplicationDetails> = ArrayList()
    private var profileData: List<GetProfileDetails> = ArrayList()
    var context: Context
    var Position: Int = 0
    var Type: Int

    constructor(data: List<GetExamApplicationDetails>, context: Context, type: Int) {
        studentmenuExamdetails = data
        this.context = context
        Type = type
    }

    constructor(
        getCourseDetailsData: List<GetCourseDetailsData>,
        context: CourseDetails,
        type: Int
    ) {
        coursedetails = getCourseDetailsData
        this.context = context
        Type = type
    }

    constructor(context: Context, getProfileData: List<GetProfileDetails>, type: Int) {
        this.context = context
        profileData = getProfileData
        Type = type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_coursedetails_list_design, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Position = holder.absoluteAdapterPosition

        if (Type == 0) {
            if (position % 2 == 0) {
                holder.ViewSide!!.setBackgroundColor(Color.parseColor("#f3ca3a"))
            } else {
                holder.ViewSide!!.setBackgroundColor(Color.parseColor("#5daf98"))
            }
            val data: GetCourseDetailsData = coursedetails.get(position)
            holder.lblSubjectName!!.text = data.subjectname
            holder.lblSubjectType!!.text = data.subjectType
            holder.lblSubjectCategory!!.text = data.subjectCategory
            holder.lblSubjectCredits!!.text = data.subjectCredits
            holder.lblSubjectCode!!.text = data.subjectcode
            holder.lblSubjectRequirement!!.text = data.subjectrequirement
            holder.LayoutProfileOverall!!.visibility = View.GONE
            holder.LayoutCourseAndExam!!.visibility = View.VISIBLE
        } else if (Type == 1) {
            if (position % 2 == 0) {
                holder.ViewSide!!.setBackgroundColor(Color.parseColor("#f3ca3a"))
            } else {
                holder.ViewSide!!.setBackgroundColor(Color.parseColor("#5daf98"))
            }
            val data: GetExamApplicationDetails = studentmenuExamdetails.get(position)
            holder.lblSubjectName!!.text = data.subjectName
            holder.lblSubjectCode!!.text = data.subjectcode
            holder.LayoutFeeeAmount!!.visibility = View.VISIBLE
            holder.LayoutSemNumber!!.visibility = View.VISIBLE
            holder.lblSemesterName!!.text = data.semNumber
            holder.lblFeeAmount!!.text = data.amount
            holder.LayoutCategory!!.visibility = View.GONE
            holder.LayoutSubjectType!!.visibility = View.GONE
            holder.LayoutSubjectRequirement!!.visibility = View.GONE
            holder.LayoutSubjectCredits!!.visibility = View.GONE
            holder.LayoutProfileOverall!!.visibility = View.GONE
            holder.LayoutCourseAndExam!!.visibility = View.VISIBLE
        } else if (Type == 2) {
            val data: GetProfileDetails = profileData.get(position)
            holder.LayoutCourseAndExam!!.visibility = View.GONE
            if ((data.type == "text")) {
                if (data.key.isNullOrEmpty() || data.value.isNullOrEmpty()) {
                    holder.LayoutProfileOverall!!.visibility = View.GONE
                    holder.lblProfileKey!!.visibility = View.GONE
                    holder.lblProfileValue!!.visibility = View.GONE
                    holder.imgUser!!.visibility = View.GONE
                    holder.layoutOverAll!!.visibility = View.GONE
                } else {
                    holder.layoutOverAll!!.visibility = View.VISIBLE
                    holder.lblProfileKey!!.text = data.key
                    holder.lblProfileValue!!.text = data.value
                    holder.imgUser!!.visibility = View.GONE
                    holder.LayoutProfileOverall!!.visibility = View.VISIBLE
                }
            }
            if ((data.type == "img")) {
                if (data.value == null || data.value!!.isEmpty()) {
                    holder.LayoutProfileOverall!!.visibility = View.GONE
                } else {
                    if (data.key.equals("College Logo")) {
                        holder.lblProfileKey!!.text = ""
                        holder.lblcolon0!!.text = ""
                        holder.lblProfileValue!!.text = ""
                        holder.LayoutProfileOverall!!.visibility = View.VISIBLE
                        holder.imgUser!!.visibility = View.VISIBLE
                        Glide.with(context)
                            .load(data.value)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into((holder.imgUser)!!)
                    } else {
                        holder.LayoutProfileOverall!!.visibility = View.VISIBLE
                        holder.imgUser!!.visibility = View.VISIBLE
                        holder.lblProfileKey!!.text = data.key
                        Glide.with(context)
                            .load(data.value)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into((holder.imgUser)!!)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        if (Type == 0) {
            return coursedetails.size
        } else if (Type == 1) {
            return studentmenuExamdetails.size
        } else if (Type == 2) {
            return profileData.size
        }
        return 0
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {

        val lblSubjectName: TextView = itemView!!.findViewById(R.id.lblSubjectName)
        val lblSemesterName: TextView = itemView!!.findViewById(R.id.lblSemesterName)
        val lblFeeAmount: TextView = itemView!!.findViewById(R.id.lblFeeAmount)
        val lblSubjectType: TextView = itemView!!.findViewById(R.id.lblSubjectType)
        val lblSubjectCategory: TextView = itemView!!.findViewById(R.id.lblSubjectCategory)
        val lblSubjectCredits: TextView = itemView!!.findViewById(R.id.lblSubjectCredits)
        val lblSubjectCode: TextView = itemView!!.findViewById(R.id.lblSubjectCode)
        val lblSubjectRequirement: TextView = itemView!!.findViewById(R.id.lblSubjectRequirement)
        val LayoutFeeeAmount: ConstraintLayout = itemView!!.findViewById(R.id.layoutFeeeAmount)
        val LayoutSemNumber: ConstraintLayout = itemView!!.findViewById(R.id.layoutSemNumber)
        val ViewSide: View = itemView!!.findViewById(R.id.ViewSide)
        val LayoutCategory: ConstraintLayout = itemView!!.findViewById(R.id.layoutCategory)
        val LayoutSubjectType: ConstraintLayout = itemView!!.findViewById(R.id.layoutSubjectType)
        val LayoutSubjectRequirement: ConstraintLayout = itemView!!.findViewById(R.id.LayoutSubjectRequirement)
        val LayoutSubjectCredits: ConstraintLayout = itemView!!.findViewById(R.id.layoutSubjectCredits)
        val imgUser: ImageView = itemView!!.findViewById(R.id.imgUser)
        val LayoutProfileOverall: ConstraintLayout = itemView!!.findViewById(R.id.LayoutProfileOverall)
        val LayoutCourseAndExam: ConstraintLayout = itemView!!.findViewById(R.id.LayoutCourseAndExam)
        val lblProfileKey: TextView = itemView!!.findViewById(R.id.lblProfileKey)
        val lblProfileValue: TextView = itemView!!.findViewById(R.id.lblProfileValue)
        val lblcolon0: TextView = itemView!!.findViewById(R.id.lblcolon0)
        val layoutOverAll: ConstraintLayout = itemView!!.findViewById(R.id.layoutOverAll)
    }
}