package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.EventsViewDetails
import com.vsca.vsnapvoicecollege.ActivitySender.Assignment_Submition
import com.vsca.vsnapvoicecollege.Model.AssignmentMark
import com.vsca.vsnapvoicecollege.Model.AssignmentSubmit
import com.vsca.vsnapvoicecollege.Model.ImageListView
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import soup.neumorphism.NeumorphCardView

class Assignment_SubmittionAdapter(data: ArrayList<AssignmentSubmit>, context: Context) :
    RecyclerView.Adapter<Assignment_SubmittionAdapter.MyViewHolder>() {
    var subjectdata: List<AssignmentSubmit> = ArrayList()
    var context: Context
    var isSetionNumber: Int = 1

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): Assignment_SubmittionAdapter.MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.assignment_sumbittion, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: AssignmentSubmit = subjectdata.get(position)


        if (CommonUtil.isSubmitted.equals("submitted")) {

            holder.tblView!!.visibility = View.GONE
            holder.cardImage!!.visibility = View.VISIBLE

            if (CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals("p2") || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                if (data.obtainedmark != "") {
                    holder.textInputEditText!!.setText(data.obtainedmark)
                }
                holder.conLbls!!.visibility = View.VISIBLE
                holder.imgEnterMark!!.visibility = View.VISIBLE
                holder.outlineEditText!!.visibility = View.VISIBLE
                holder.lblMark!!.visibility = View.GONE
                holder.lblRegisterNumberSub!!.text = data.register_number
                holder.lblNameOfStudent!!.text = data.studentname
            } else {
                holder.conLbls!!.visibility = View.GONE
                holder.imgEnterMark!!.visibility = View.GONE
                holder.outlineEditText!!.visibility = View.GONE
                holder.lblMark!!.visibility = View.VISIBLE
                if (data.obtainedmark != "") {
                    holder.lblMark!!.text = "Your mark : " + data.obtainedmark
                } else {
                    holder.lblMark!!.text = "Not evaluated yet!"
                }
            }

            holder.imgEnterMark!!.setOnClickListener {
                if (holder.textInputEditText!!.text.toString() != "") {
                    giveAssignmentMark(holder, subjectdata[position])
                } else {
                    CommonUtil.Toast(context, "Enter the mark")
                }
            }

            CommonUtil.isImageViewList.clear()
            if (data.filearray.isNotEmpty()) {
                CommonUtil.isFileCount = data.filearray.size
                for (i in data.filearray.indices) {
                    CommonUtil.isImageViewList.add(
                        ImageListView(
                            data.filearray[i].fileurl, data.filearray[i].filetype
                        )
                    )
                }
            }

            val imagepreviewadapter = imagepreviewadapter(CommonUtil.isImageViewList, context)
            val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 3)
            holder.rcyAssignSubmitted!!.layoutManager = mLayoutManager
            holder.rcyAssignSubmitted!!.isNestedScrollingEnabled = false
            holder.rcyAssignSubmitted!!.addItemDecoration(
                EventsViewDetails.GridSpacingItemDecoration(
                    4, true
                )
            )
            holder.rcyAssignSubmitted!!.itemAnimator = DefaultItemAnimator()
            holder.rcyAssignSubmitted!!.adapter = imagepreviewadapter


        } else if (CommonUtil.isSubmitted == "notsubmitted") {
            holder.cardImage!!.visibility = View.GONE
            holder.tblView!!.visibility = View.VISIBLE
            holder.lblRegisterNo!!.text = data.register_number
            holder.lblStudentName!!.text = data.studentname
            holder.lblCourse!!.text = data.course
            holder.lblYear!!.text = data.year
            holder.lblSNumber!!.text = isSetionNumber.toString()
            isSetionNumber++
        }
    }

    private fun giveAssignmentMark(holder: MyViewHolder, subjectdata: AssignmentSubmit) {

        val jsonObject = JsonObject()

        jsonObject.addProperty("assignmentid", CommonUtil.Assignmentid)
        jsonObject.addProperty("processby", CommonUtil.MemberId)
        jsonObject.addProperty("studentid", subjectdata.studentid)
        jsonObject.addProperty("assignmentdetailsid", subjectdata.assignmentdetailsid)
        jsonObject.addProperty("marks", holder.textInputEditText!!.text.toString())
        Log.d("jsonoblect", jsonObject.toString())

        RestClient.apiInterfaces.AssignmentMark(jsonObject)
            ?.enqueue(object : Callback<AssignmentMark?> {
                override fun onResponse(
                    call: Call<AssignmentMark?>, response: Response<AssignmentMark?>
                ) {
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val response = response.body()!!.Message
                            Log.d("message", response)

                            val dlg = context.let { AlertDialog.Builder(it) }
                            dlg.setTitle("Info")
                            dlg.setMessage(response)
                            dlg.setPositiveButton("OK") { dialog, which ->

                                val i: Intent = Intent(context, Assignment_Submition::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                context.startActivity(i)

                            }

                            dlg.setCancelable(false)
                            dlg.create()
                            dlg.show()


                        }

                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {

                    }
                }

                override fun onFailure(call: Call<AssignmentMark?>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })

    }

    override fun getItemCount(): Int {
        return subjectdata.size

    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {

//        val txtFinanceAndAccounting: TextView = itemView!!.findViewById(R.id.txt_financeandaccounding)
        val imgEnterMark: ImageView = itemView!!.findViewById(R.id.imgEntermark)
        val textInputEditText: TextInputEditText = itemView!!.findViewById(R.id.TextInputEditText)
        val outlineEditText: TextInputLayout = itemView!!.findViewById(R.id.outline_edit_text)
        val rcyAssignSubmitted: RecyclerView = itemView!!.findViewById(R.id.rcyAssignSubmitted)
        val cardImage: NeumorphCardView = itemView!!.findViewById(R.id.cardimage)
        val tblView: TableLayout = itemView!!.findViewById(R.id.tblview)
//        val txtStudent: TextView = itemView!!.findViewById(R.id.txt_student)
        val lblRegisterNumberSub: TextView = itemView!!.findViewById(R.id.lblregisternumberSub)
        val lblNameOfStudent: TextView = itemView!!.findViewById(R.id.lblnameofStudent)
        val lblMark: TextView = itemView!!.findViewById(R.id.lblmark)
//        val txtBcomAccounts: TextView = itemView!!.findViewById(R.id.txt_bcom_Accounts)
        val rlaEnterMark: RelativeLayout = itemView!!.findViewById(R.id.rlaEntermark)
//        val imgBook: ImageView = itemView!!.findViewById(R.id.img_book)
//        val txtSemester1: TextView = itemView!!.findViewById(R.id.txt_semester1)
//        val lnrAttachment: LinearLayout = itemView!!.findViewById(R.id.lnrAttachment)
        val lblSNumber: TextView = itemView!!.findViewById(R.id.lblsnumber)
        val lblRegisterNo: TextView = itemView!!.findViewById(R.id.lblregisterno)
        val lblStudentName: TextView = itemView!!.findViewById(R.id.lblstudentname)
        val conLbls: ConstraintLayout = itemView!!.findViewById(R.id.conlbls)
        val lblCourse: TextView = itemView!!.findViewById(R.id.lblcourse)
        val lblYear: TextView = itemView!!.findViewById(R.id.lblyear)

    }

    init {
        subjectdata = data
        this.context = context
    }

}