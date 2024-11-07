package com.vsca.vsnapvoicecollege.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.ExamList
import com.vsca.vsnapvoicecollege.ActivitySender.Eyeview_Examlist
import com.vsca.vsnapvoicecollege.Interfaces.ExamMarkViewClickListener
import com.vsca.vsnapvoicecollege.Model.ExamDelete
import com.vsca.vsnapvoicecollege.Model.GetExamListDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExamListAdapter constructor(
    data: List<GetExamListDetails>,
    context: Context,
    type: Boolean,
    val markListener: ExamMarkViewClickListener
) :
    RecyclerView.Adapter<ExamListAdapter.MyViewHolder>() {
    var noticeboarddata: List<GetExamListDetails> = ArrayList()
    var context: Context
    var Position: Int = 0
    var Type: Boolean = true
    private var mExpandedPosition: Int = -1
    var detailsid: String? = null

    companion object {
        var viewMarksClick: ExamMarkViewClickListener? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.noticeboard_list_design, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") positionindex: Int
    ) {
        val data: GetExamListDetails = noticeboarddata.get(positionindex)
        Position = holder.absoluteAdapterPosition
        holder.lblNoticeboardTitle!!.text = data.examname
        holder.lbl_section!!.text = data.session
        holder.lnrEventsView!!.visibility = View.VISIBLE
        viewMarksClick = markListener
        viewMarksClick?.onExamClickListener(holder, data)

        try {
            if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                    "p6"
                )
            ) {
                var examDate = data.date
                Log.d("Examdate", examDate!!.length.toString())
                if (examDate.length > 15) {
                    val splitDate = examDate.split("\\s+".toRegex()).toTypedArray()
                    val Date = splitDate[0]
                    val Month = splitDate[1]
                    val Year = splitDate[2]
                    val Date_2 = splitDate[3]
                    val Month_2 = splitDate[4]
                    val Year_2 = splitDate[5]

                    holder.lblNoticeboardDate!!.text = Date + " " + Month + " " + Year
                    holder.lblNoticetime!!.text = Date_2 + " " + Month_2 + " " + Year_2
                } else {
                    holder.lblNoticeboardDate!!.text = data.date
                    holder.lblNoticetime!!.visibility = View.GONE

                }

            } else {

                holder.lblNoticeboardDate!!.text = data.createdon
                holder.lblNoticetime!!.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val isExpanded: Boolean = positionindex == mExpandedPosition
        holder.rytNotice!!.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.lnrNoticeboardd!!.isActivated = isExpanded
        holder.lnrNoticeboardd!!.visibility = View.VISIBLE
        holder.imgArrowdown!!.visibility = View.VISIBLE

        if (isExpanded) {
            holder.imgArrowdown!!.visibility = View.GONE

            if (Type) {
                holder.liner_deleteandview!!.visibility = View.VISIBLE
                CommonUtil.headerid = data.headerid.toString()
                CommonUtil.Examname = data.examname.toString()
                CommonUtil.startdate = data.date.toString()

            } else {

                holder.liner_deleteandview!!.visibility = View.VISIBLE
                if (CommonUtil.MemberId.toString() == data.createdby) {
                    holder.liner_Delete!!.visibility = View.VISIBLE
                    CommonUtil.headerid = data.headerid.toString()
                    CommonUtil.Examname = data.examname.toString()
                    CommonUtil.startdate = data.date.toString()
                }
            }

            holder.LayoutVenue!!.visibility = View.VISIBLE
            holder.layoutSyllabus!!.visibility = View.VISIBLE

        } else {

            holder.imgArrowdown!!.visibility = View.VISIBLE
            holder.liner_deleteandview!!.visibility = View.GONE
            holder.LayoutVenue!!.visibility = View.GONE
            holder.layoutSyllabus!!.visibility = View.GONE

        }



        if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                "p6"
            )
        ) {
            holder.img_Edit!!.visibility = View.GONE
            holder.liner_Delete!!.visibility = View.GONE
        } else {

            if (CommonUtil.MemberId.toString() == data.createdby) {

                holder.liner_deleteandview!!.visibility = View.VISIBLE
                holder.liner_Delete!!.visibility = View.VISIBLE
                holder.liner_Delete!!.setOnClickListener {

                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
                    alertDialog.setTitle("info")
                    alertDialog.setMessage("Are you want to Delete?")
                    alertDialog.setPositiveButton(
                        "yes"
                    ) { _, _ ->
                        DeleteRequest()
                    }
                    alertDialog.setNegativeButton(
                        "No"
                    ) { _, _ -> }
                    val alert: AlertDialog = alertDialog.create()
                    alert.setCanceledOnTouchOutside(false)
                    alert.show()
                }
            } else {
                holder.img_Edit!!.visibility = View.GONE
                holder.liner_Delete!!.visibility = View.GONE
            }
        }


        holder.lnrNoticeboardd!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                CommonUtil.headerid = ""
                CommonUtil.headerid = data.headerid.toString()

                holder.imgArrowdown!!.visibility = View.GONE
                holder.lblNoticeboardDescription!!.visibility = View.GONE
                holder.lblNoticePostedby!!.text = data.createdbyname

                if ( CommonUtil.Priority == "p7"  || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals("p2") || CommonUtil.Priority.equals(
                        "p3"
                    )
                ) {
                    holder.venue!!.text = "Start Date"
                    holder.Syllabus!!.text = "End Date"
                    holder.lblVenue!!.text = data.startdate
                    holder.lblSyllabus!!.text = data.enddate
                } else {
                    holder.lblVenue!!.text = data.examvenue
                    holder.lblSyllabus!!.text = data.syllabus
                }
                if (Type) {
                    holder.liner_deleteandview!!.visibility = View.GONE
                } else {
                    holder.liner_deleteandview!!.visibility = View.VISIBLE
                }
                holder.LayoutVenue!!.visibility = View.VISIBLE
                holder.layoutSyllabus!!.visibility = View.VISIBLE


                Log.d("headerid", CommonUtil.headerid)


                mExpandedPosition = if (isExpanded) -1 else positionindex
                notifyDataSetChanged()
            }
        })


        holder.img_Edit!!.setOnClickListener {

            val i = Intent(context, Eyeview_Examlist::class.java)
            CommonUtil.EditButtonclick = "ExamEdit"
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)


        }
    }

    override fun getItemCount(): Int {
        return noticeboarddata.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {
        @JvmField
        @BindView(R.id.lblNoticeboardTitle)
        var lblNoticeboardTitle: TextView? = null

        @JvmField
        @BindView(R.id.lblNoticeboardDescription)
        var lblNoticeboardDescription: TextView? = null


        @JvmField
        @BindView(R.id.lblNoticeboardDate)
        var lblNoticeboardDate: TextView? = null

        @JvmField
        @BindView(R.id.lblNoticetime)
        var lblNoticetime: TextView? = null

        @JvmField
        @BindView(R.id.lblNoticePostedby)
        var lblNoticePostedby: TextView? = null

        @JvmField
        @BindView(R.id.rytNotice)
        var rytNotice: RelativeLayout? = null

        @JvmField
        @BindView(R.id.img_delete)
        var img_delete: ImageView? = null

        @JvmField
        @BindView(R.id.lnrNoticeboardd)
        var lnrNoticeboardd: LinearLayout? = null

        @JvmField
        @BindView(R.id.imgArrowdown)
        var imgArrowdown: ImageView? = null

        @JvmField
        @BindView(R.id.imgArrowUp)
        var imgArrowUp: ImageView? = null

        @JvmField
        @BindView(R.id.LayoutVenue)
        var LayoutVenue: ConstraintLayout? = null

        @JvmField
        @BindView(R.id.layoutSyllabus)
        var layoutSyllabus: ConstraintLayout? = null

        @JvmField
        @BindView(R.id.lblSyllabus)
        var lblSyllabus: TextView? = null

        @JvmField
        @BindView(R.id.lblVenue)
        var lblVenue: TextView? = null


        @JvmField
        @BindView(R.id.venue)
        var venue: TextView? = null


        @JvmField
        @BindView(R.id.Syllabus)
        var Syllabus: TextView? = null

        @JvmField
        @BindView(R.id.lnrEventsView)
        var lnrEventsView: LinearLayout? = null


        @JvmField
        @BindView(R.id.liner_deleteandview)
        var liner_deleteandview: LinearLayout? = null

        @JvmField
        @BindView(R.id.liner_Delete)
        var liner_Delete: LinearLayout? = null

        @JvmField
        @BindView(R.id.lbl_section)
        var lbl_section: TextView? = null

        @JvmField
        @BindView(R.id.img_Edit)
        var img_Edit: LinearLayout? = null


        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        noticeboarddata = data
        this.context = context
        Type = type
    }

    fun DeleteRequest() {

        val jsonObject = JsonObject()

        jsonObject.addProperty("collegeid", CommonUtil.CollegeId)
        jsonObject.addProperty("examid", CommonUtil.headerid)
        jsonObject.addProperty("examname", CommonUtil.Examname)
        jsonObject.addProperty("staffid", CommonUtil.MemberId)
        jsonObject.addProperty("startdate", "")
        jsonObject.addProperty("enddate", "")
        jsonObject.addProperty("processtype", "delete")

        val jsonObjectsection = JsonArray()

        jsonObject.add("sectiondetails", jsonObjectsection)
        Log.d("jsonoblect", jsonObject.toString())

        RestClient.apiInterfaces.Examdeletedata(jsonObject)
            ?.enqueue(object : Callback<ExamDelete?> {
                override fun onResponse(
                    call: Call<ExamDelete?>,
                    response: Response<ExamDelete?>
                ) {
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val response = response.body()!!.Message
                            Log.d("message", response)

                            val dlg = context.let { AlertDialog.Builder(it) }
                            dlg!!.setTitle("Info")
                            dlg.setMessage(response)
                            dlg.setPositiveButton("OK") { dialog, which ->

                                val i: Intent =

                                    Intent(context, ExamList::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                context.startActivity(i)
                            }
                            dlg.setCancelable(false)
                            dlg.create()
                            dlg.show()
                        }

                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        Log.d("resonsemessage", response.message().toString())

                    }
                }

                override fun onFailure(call: Call<ExamDelete?>, t: Throwable) {

                }

            })
    }

    fun filterList(filterlist: java.util.ArrayList<GetExamListDetails>) {

        noticeboarddata = filterlist

        notifyDataSetChanged()
    }
}