package com.vsca.vsnapvoicecollege.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.*
import com.vsca.vsnapvoicecollege.ActivitySender.AddAssignment
import com.vsca.vsnapvoicecollege.ActivitySender.Assignment_Submition
import com.vsca.vsnapvoicecollege.ActivitySender.ImageViewAdapter
import com.vsca.vsnapvoicecollege.Model.Delete_noticeboard
import com.vsca.vsnapvoicecollege.Model.GetAssignmentDetails
import com.vsca.vsnapvoicecollege.Model.ImageListView
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.isExpandAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AssignmentAdapter(data: List<GetAssignmentDetails>, context: Context) :
    RecyclerView.Adapter<AssignmentAdapter.MyViewHolder>() {
    var assignmentdata: List<GetAssignmentDetails> = ArrayList()
    var context: Context
    var Position: Int = 0
    private var mExpandedPosition: Int = -1
    private var pathlist: ArrayList<String>? = null
    val list: List<String> = ArrayList()
    var filepath: String? = null
    var ScreenName: String? = null
    var Type = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.assignment_list_design, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(
        holder: MyViewHolder, @SuppressLint("RecyclerView") positionindex: Int
    ) {
        val data: GetAssignmentDetails = assignmentdata.get(positionindex)
        Position = holder.absoluteAdapterPosition
        holder.lblNoticeboardTitle!!.text = data.topic

        if (CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority.equals(
                "p3"
            ) || CommonUtil.Priority.equals("p7")
        ) {
            holder.lblsubmittioncount!!.visibility = View.VISIBLE
            holder.lblsubmittioncount!!.text = "Submission : " + data.submittedcount
            if (data.submittedcount.equals("0")) {
                holder.lblsubmittioncount!!.setTextColor(Color.RED)
            } else {
                holder.lblsubmittioncount!!.setTextColor(Color.parseColor("#278731"))
            }
        } else {
            holder.lblsubmittioncount!!.visibility = View.GONE
        }
        try {
            val createdDateTime: String = data.createdon.toString()
            val firstvalue: Array<String> = createdDateTime.split("-".toRegex()).toTypedArray()
            val createddate: String = firstvalue.get(0)
            holder.lblNoticeboardDate!!.text = createddate
            val createdTime: String = firstvalue.get(1)
            holder.lblNoticetime!!.text = createdTime
            val isExpanded: Boolean = positionindex == mExpandedPosition
            holder.rytNotice!!.visibility = if (isExpanded) View.VISIBLE else View.GONE
            holder.lnrNoticeboardd!!.isActivated = isExpanded
            holder.lnrNoticeboardd!!.visibility = View.VISIBLE
            if (data.isappread.equals("0")) {
                holder.lblNewCircle!!.visibility = View.VISIBLE
            } else {
                holder.lblNewCircle!!.visibility = View.GONE
            }

            if (isExpanded) {
                isExpandAdapter = true
                holder.imgArrowdown!!.setImageResource(R.drawable.ic_arrow_up_blue)
                Type = "assignment"
            } else {
                isExpandAdapter = false
                holder.imgArrowdown!!.setImageResource(R.drawable.ic_arrow_down_blue)
            }



            holder.lnrNoticeboardd!!.setOnClickListener {
                Type = "assignment"
                if (data.isappread.equals("0")) {
                    BaseActivity.AppReadStatusContext(context, Type, data.assignmentdetailid!!)
                    data.isappread = "1"
                    holder.lblNewCircle!!.visibility = View.GONE
                }
                holder.lblNoticeboardDescription!!.text = data.description
                holder.lblNoticePostedby!!.text = data.sentbyname
                holder.lblSubmitedOn!!.text = data.submissiondate
                CommonUtil.Assignmentid = data.assignmentid.toString()
                if ((CommonUtil.Priority == "p4") || (CommonUtil.Priority == "p5") || (CommonUtil.Priority == "p6")) {
                    holder.LayoutSubmissions!!.visibility = View.VISIBLE
                    holder.lblAssingmentSubmit!!.visibility = View.VISIBLE
                    if (!data.submittedcount.equals("0")) {
                        holder.lblPreviousSubmission!!.visibility = View.VISIBLE
                    } else {
                        holder.lblPreviousSubmission!!.visibility = View.GONE
                    }
                }
                if ((CommonUtil.Priority == "p7") || (CommonUtil.Priority == "p1") || (CommonUtil.Priority == "p2") || (CommonUtil.Priority == "p3")) {

                    if (!data.submittedcount.equals("0")) {
                        holder.lblAssingmentSubmition!!.visibility = View.VISIBLE
                        holder.LayoutSubmissions!!.visibility = View.VISIBLE
                        holder.lblNotAssingmentSubmition!!.visibility = View.VISIBLE
                        holder.view!!.visibility = View.VISIBLE
                    } else {
                        holder.view!!.visibility = View.GONE
                        holder.lblAssingmentSubmition!!.visibility = View.GONE
                        holder.lblNotAssingmentSubmition!!.visibility = View.GONE
                    }

                    if (!data.submittedcount.equals("0")) {
                        holder.lblAssingmentSubmition!!.text = "Submission : " + data.submittedcount
                        holder.LayoutSubmissions!!.visibility = View.VISIBLE
                        holder.lblPreviousSubmission!!.visibility = View.GONE
                        holder.lblAssingmentSubmit!!.visibility = View.GONE
                        holder.lblAssingmentSubmition!!.visibility = View.VISIBLE
                    } else {
                        holder.lblAssingmentSubmition!!.visibility = View.GONE
                    }

                    if (!data.totalcount.equals("0")) {
                        holder.lblNotAssingmentSubmition!!.text =
                            "Not Submitted : " + data.totalcount
                        holder.LayoutSubmissions!!.visibility = View.VISIBLE
                        holder.lblNotAssingmentSubmition!!.visibility = View.VISIBLE
                        holder.lblPreviousSubmission!!.visibility = View.GONE
                        holder.lblAssingmentSubmit!!.visibility = View.GONE
                    } else {
                        holder.lblNotAssingmentSubmition!!.visibility = View.GONE
                    }

                    if (data.createdby == CommonUtil.MemberId.toString()) {
                        holder.lnrForward!!.visibility = View.VISIBLE
                    } else {
                        holder.lnrForward!!.visibility = View.GONE
                    }
                }
                if ((data.assignmenttype == "image") || (data.assignmenttype == "video") || (data.assignmenttype == "pdf")) {
                    if (data.newfilepath != null) {
                        holder.rytAssignmentFiles!!.visibility = View.VISIBLE
                        pathlist = data.newfilepath as ArrayList<String>?
                        val filecount: Int = pathlist!!.size
                        if (filecount > 1) {
                            val totalcount: Int = filecount - 1
                            holder.lblFileCount!!.visibility = View.VISIBLE
                            holder.lblFileCount!!.text = "+" + totalcount.toString()
                        } else {
                            holder.lblFileCount!!.visibility = View.GONE
                        }
                        holder.lnrAttachment!!.setOnClickListener {
                            CommonUtil.isImageViewList.clear()
                            var filetype = ""
                            filetype = if (data.assignmenttype.equals("image")) {
                                "image"
                            } else {
                                "pdf"
                            }
                            if (data.newfilepath!!.isNotEmpty()) {
                                for (i in data.newfilepath!!.indices) {
                                    CommonUtil.isImageViewList.add(
                                        ImageListView(
                                            data.newfilepath!![i], filetype
                                        )
                                    )
                                }
                            }
                            val dialog = BottomSheetDialog(context)
                            val view = LayoutInflater.from(holder.itemView.context)
                                .inflate(R.layout.image_preview, null)
                            val grdImg = view.findViewById<RecyclerView>(R.id.grdImg)
                            val imgClose = view.findViewById<ImageView>(R.id.imgClose)
                            val lblassignName = view.findViewById<TextView>(R.id.lblassignName)
                            lblassignName.text = data.topic.toString()
                            val imagepreviewadapter =
                                ImageViewAdapter(CommonUtil.isImageViewList, context)
                            val mLayoutManager: RecyclerView.LayoutManager =
                                GridLayoutManager(context, 3)
                            grdImg!!.layoutManager = mLayoutManager
                            grdImg.isNestedScrollingEnabled = true
                            grdImg.addItemDecoration(
                                EventsViewDetails.GridSpacingItemDecoration(
                                    4, true
                                )
                            )
                            grdImg.itemAnimator = DefaultItemAnimator()
                            grdImg.adapter = imagepreviewadapter
                            dialog.setCancelable(false)
                            dialog.setContentView(view)
                            dialog.show()

                            imgClose.setOnClickListener {
                                dialog.dismiss()
                            }
                        }
                    }
                } else if (data.assignmenttype == "All" || data.assignmenttype == "text") {
                    holder.rytAssignmentFiles!!.visibility = View.GONE
                }
                mExpandedPosition = if (isExpanded) -1 else positionindex
                notifyDataSetChanged()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        holder.lnrForward!!.setOnClickListener {
            ScreenName = "Forward Assignment"
            CommonUtil.ForwardAssignment.clear()
            val i: Intent = Intent(context, AddAssignment::class.java)
            i.putExtra("ScreenName", ScreenName)
            i.putExtra("AssignmentTitle", data.topic)

            CommonUtil.Assignmentid = data.assignmentid.toString()
            CommonUtil.ForwardAssignment.add(data.newfilepath.toString())
            CommonUtil.AssignmentType = data.assignmenttype.toString()
            CommonUtil.AssignmentDescription = data.description.toString()
            CommonUtil.Assignmenttitle = data.topic.toString()

            i.putExtra("AssignmentType", data.assignmenttype)
            context.startActivity(i)
        }

        holder.lblAssingmentSubmit!!.setOnClickListener {
            ScreenName = "Assignment Submit"
            val i: Intent = Intent(context, AddAssignment::class.java)
            i.putExtra("ScreenName", ScreenName)
            context.startActivity(i)
        }

        holder.lblAssingmentSubmition!!.setOnClickListener {

            CommonUtil.Assignmentid = data.assignmentid.toString()
            CommonUtil.AssignmentDetailsId = data.assignmentdetailid.toString()
            CommonUtil.isSubmitted = "submitted"
            val i: Intent = Intent(context, Assignment_Submition::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(i)
        }

        holder.lblNotAssingmentSubmition!!.setOnClickListener {

            CommonUtil.Assignmentid = data.assignmentid.toString()
            CommonUtil.AssignmentDetailsId = data.assignmentdetailid.toString()
            CommonUtil.isSubmitted = "notsubmitted"
            val i: Intent = Intent(context, Assignment_Submition::class.java)
            context.startActivity(i)
        }

        holder.lblPreviousSubmission!!.setOnClickListener {

            CommonUtil.Assignmentid = data.assignmentid.toString()
            val i: Intent = Intent(context, Assignment_Submition::class.java)
            CommonUtil.filetype = "Assignment previous Submited"
            CommonUtil.isSubmitted = "submitted"
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            context.startActivity(i)

        }

//        holder.img_AssignmentDelete!!.setOnClickListener {
//            AssignmentDelete()
//        }
    }

    override fun getItemCount(): Int {
        return assignmentdata.size
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
        @BindView(R.id.lblNotAssingmentSubmition)
        var lblNotAssingmentSubmition: TextView? = null

        @JvmField
        @BindView(R.id.lblSubmitedOn)
        var lblSubmitedOn: TextView? = null

        @JvmField
        @BindView(R.id.rytNotice)
        var rytNotice: RelativeLayout? = null

        @JvmField
        @BindView(R.id.lnrNoticeboardd)
        var lnrNoticeboardd: LinearLayout? = null

        @JvmField
        @BindView(R.id.LayoutSubmissions)
        var LayoutSubmissions: LinearLayout? = null

        @JvmField
        @BindView(R.id.imgArrowdown)
        var imgArrowdown: ImageView? = null


        @JvmField
        @BindView(R.id.view)
        var view: View? = null


        @JvmField
        @BindView(R.id.rytAssignmentFiles)
        var rytAssignmentFiles: RelativeLayout? = null

        @JvmField
        @BindView(R.id.lblsubmittioncount)
        var lblsubmittioncount: TextView? = null

        @JvmField
        @BindView(R.id.lblFileCount)
        var lblFileCount: TextView? = null

        @JvmField
        @BindView(R.id.lnrForward)
        var lnrForward: LinearLayout? = null

        @JvmField
        @BindView(R.id.lnrAttachment)
        var lnrAttachment: LinearLayout? = null

        @JvmField
        @BindView(R.id.lblAssignmentPath)
        var lblAssignmentPath: TextView? = null

        @JvmField
        @BindView(R.id.lblNewCircle)
        var lblNewCircle: TextView? = null

        @JvmField
        @BindView(R.id.lblPreviousSubmission)
        var lblPreviousSubmission: TextView? = null

        @JvmField
        @BindView(R.id.lblAssingmentSubmition)
        var lblAssingmentSubmition: TextView? = null

        @JvmField
        @BindView(R.id.lblAssingmentSubmit)
        var lblAssingmentSubmit: TextView? = null

        @JvmField
        @BindView(R.id.img_AssignmentDelete)
        var img_AssignmentDelete: ImageView? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        assignmentdata = data
        this.context = context
    }


    fun AssignmentDelete() {

        val jsonObject = JsonObject()

        jsonObject.addProperty("collegeid", CommonUtil.CollegeId)
        jsonObject.addProperty("deptid", "1")
        jsonObject.addProperty("courseid", "")
        jsonObject.addProperty("yearid", "")
        jsonObject.addProperty("staffid", CommonUtil.MemberId)
        jsonObject.addProperty("callertype", CommonUtil.Priority)
        jsonObject.addProperty("sectionid", "")
        jsonObject.addProperty("subjectid", "")
        jsonObject.addProperty("assignmenttopic", "")
        jsonObject.addProperty("assignmentdescription", "")
        jsonObject.addProperty("processtype", "delete")
        jsonObject.addProperty("assignmentid", CommonUtil.Assignmentid)
        jsonObject.addProperty("assignmenttype", "image")
        jsonObject.addProperty("receivertype", "")
        jsonObject.addProperty("receiverid", "")

        Log.d("jsonoblect", jsonObject.toString())

        RestClient.apiInterfaces.AssignmentDelete(jsonObject)
            ?.enqueue(object : Callback<Delete_noticeboard?> {
                override fun onResponse(
                    call: Call<Delete_noticeboard?>, response: Response<Delete_noticeboard?>
                ) {
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val response = response.body()!!.Message
                            Log.d("message", response)

                            val dlg = context.let { AlertDialog.Builder(it) }
                            dlg.setTitle("Info")
                            dlg.setMessage(response)
                            dlg.setPositiveButton("OK") { dialog, which ->

                                val i: Intent =

                                    Intent(context, Assignment::class.java)
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

                override fun onFailure(call: Call<Delete_noticeboard?>, t: Throwable) {

                }

            })
    }

    fun filterList(filterlist: java.util.ArrayList<GetAssignmentDetails>) {

        assignmentdata = filterlist

        notifyDataSetChanged()
    }
}