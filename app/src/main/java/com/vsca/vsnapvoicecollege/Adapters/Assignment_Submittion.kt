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
import butterknife.BindView
import butterknife.ButterKnife
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

            holder.tblview!!.visibility = View.GONE
            holder.cardimage!!.visibility = View.VISIBLE

            if (CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals("p2") || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                if (data.obtainedmark != "") {
                    holder.TextInputEditText!!.setText(data.obtainedmark)
                }
                holder.conlbls!!.visibility = View.VISIBLE
                holder.imgEntermark!!.visibility = View.VISIBLE
                holder.outline_edit_text!!.visibility = View.VISIBLE
                holder.lblmark!!.visibility = View.GONE
                holder.lblregisternumberSub!!.text = data.register_number
                holder.lblnameofStudent!!.text = data.studentname
            } else {
                holder.conlbls!!.visibility = View.GONE
                holder.imgEntermark!!.visibility = View.GONE
                holder.outline_edit_text!!.visibility = View.GONE
                holder.lblmark!!.visibility = View.VISIBLE
                if (data.obtainedmark != "") {
                    holder.lblmark!!.text = "Your mark : " + data.obtainedmark
                } else {
                    holder.lblmark!!.text = "Not evaluated yet!"
                }
            }

            holder.imgEntermark!!.setOnClickListener {
                if (holder.TextInputEditText!!.text.toString() != "") {
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

            // Child  View scrolling function
//            val mScrollChangeListener = object : RecyclerView.OnItemTouchListener {
//                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
//
//                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                    when (e.action) {
//                        MotionEvent.ACTION_MOVE -> {
//                            rv.parent.requestDisallowInterceptTouchEvent(true)
//                            Log.d("OnItemTouchListener", "OnItemTouchListener");
//                        }
//                    }
//                    return false
//                }
//                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
//            }
//            holder.rcyAssignSubmitted!!.addOnItemTouchListener(mScrollChangeListener)


        } else if (CommonUtil.isSubmitted == "notsubmitted") {
            holder.cardimage!!.visibility = View.GONE
            holder.tblview!!.visibility = View.VISIBLE
            holder.lblregisterno!!.text = data.register_number
            holder.lblstudentname!!.text = data.studentname
            holder.lblcourse!!.text = data.course
            holder.lblyear!!.text = data.year
            holder.lblsnumber!!.text = isSetionNumber.toString()
            isSetionNumber++
        }
    }

    private fun giveAssignmentMark(holder: MyViewHolder, subjectdata: AssignmentSubmit) {

        val jsonObject = JsonObject()

        jsonObject.addProperty("assignmentid", CommonUtil.Assignmentid)
        jsonObject.addProperty("processby", CommonUtil.MemberId)
        jsonObject.addProperty("studentid", subjectdata.studentid)
        jsonObject.addProperty("assignmentdetailsid", subjectdata.assignmentdetailsid)
        jsonObject.addProperty("marks", holder.TextInputEditText!!.text.toString())
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
        @JvmField
        @BindView(R.id.txt_financeandaccounding)
        var txt_financeandaccounding: TextView? = null

        @JvmField
        @BindView(R.id.imgEntermark)
        var imgEntermark: ImageView? = null

        @JvmField
        @BindView(R.id.TextInputEditText)
        var TextInputEditText: TextInputEditText? = null

        @JvmField
        @BindView(R.id.outline_edit_text)
        var outline_edit_text: TextInputLayout? = null

        @JvmField
        @BindView(R.id.rcyAssignSubmitted)
        var rcyAssignSubmitted: RecyclerView? = null


        @JvmField
        @BindView(R.id.cardimage)
        var cardimage: NeumorphCardView? = null

        @JvmField
        @BindView(R.id.tblview)
        var tblview: TableLayout? = null

        @JvmField
        @BindView(R.id.txt_student)
        var txt_student: TextView? = null

        @JvmField
        @BindView(R.id.lblregisternumberSub)
        var lblregisternumberSub: TextView? = null

        @JvmField
        @BindView(R.id.lblnameofStudent)
        var lblnameofStudent: TextView? = null


        @JvmField
        @BindView(R.id.lblmark)
        var lblmark: TextView? = null

        @JvmField
        @BindView(R.id.txt_bcom_Accounts)
        var txt_bcom_Accounts: TextView? = null

        @JvmField
        @BindView(R.id.rlaEntermark)
        var rlaEntermark: RelativeLayout? = null

        @JvmField
        @BindView(R.id.img_book)
        var img_book: ImageView? = null

        @JvmField
        @BindView(R.id.txt_semester1)
        var txt_semester1: TextView? = null

        @JvmField
        @BindView(R.id.lnrAttachment)
        var lnrAttachment: LinearLayout? = null


        @JvmField
        @BindView(R.id.lblsnumber)
        var lblsnumber: TextView? = null

        @JvmField
        @BindView(R.id.lblregisterno)
        var lblregisterno: TextView? = null

        @JvmField
        @BindView(R.id.lblstudentname)
        var lblstudentname: TextView? = null

        @JvmField
        @BindView(R.id.conlbls)
        var conlbls: ConstraintLayout? = null

        @JvmField
        @BindView(R.id.lblcourse)
        var lblcourse: TextView? = null

        @JvmField
        @BindView(R.id.lblyear)
        var lblyear: TextView? = null


        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        subjectdata = data
        this.context = context
    }

}