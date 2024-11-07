package com.vsca.vsnapvoicecollege.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Activities.EventsViewDetails
import com.vsca.vsnapvoicecollege.Activities.Noticeboard
import com.vsca.vsnapvoicecollege.Model.Delete_noticeboard
import com.vsca.vsnapvoicecollege.Model.GetNoticeboardDetails
import com.vsca.vsnapvoicecollege.Model.ImageListView
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NoticeBoard constructor(data: List<GetNoticeboardDetails>, context: Context) :
    RecyclerView.Adapter<NoticeBoard.MyViewHolder>() {
    var noticeboarddata: List<GetNoticeboardDetails> = ArrayList()
    var context: Context
    var Position: Int = 0
    var detailsid: String? = null
    private var mExpandedPosition: Int = -1
    private lateinit var pdfUri: Uri
    var Type = ""
    private var noticeName = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.assignment_list_design, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder, @SuppressLint("RecyclerView") positionindex: Int
    ) {
        val data: GetNoticeboardDetails = noticeboarddata[positionindex]
        Position = holder.absoluteAdapterPosition
        holder.lblsubmittioncount!!.visibility = View.GONE
        holder.lblNoticeboardTitle!!.text = data.topic
        holder.lblNoticeboardDescription!!.text = data.description
        holder.lblNoticeboardDate!!.text = data.createdondate
        holder.lblNoticetime!!.text = data.createdontime
        holder.lblNoticePostedby!!.text = data.sentbyname
        if (data.isappread.equals("0")) {
            holder.lblNewCircle!!.visibility = View.VISIBLE
        } else {
            holder.lblNewCircle!!.visibility = GONE
        }
        val isExpanded: Boolean = positionindex == mExpandedPosition
        holder.rytNotice!!.visibility = if (isExpanded) View.VISIBLE else GONE
        if (isExpanded) {
            holder.imgArrowdown!!.visibility = GONE
            holder.imgArrowUp!!.visibility = View.VISIBLE
        } else {
            holder.imgArrowdown!!.visibility = View.VISIBLE
            holder.imgArrowUp!!.visibility = GONE
        }
        holder.rytAssignmentFiles!!.setOnClickListener {

            CommonUtil.isImageViewList.clear()
            if (data.filearray!!.isNotEmpty()) {
                for (i in data.filearray!!.indices) {
                    CommonUtil.isImageViewList.add(
                        ImageListView(
                            data.filearray!![i].filepath.toString(),
                            data.filearray!![i].filetype.toString()
                        )
                    )
                }
            }
            val dialog = BottomSheetDialog(context)
            val view =
                LayoutInflater.from(holder.itemView.context).inflate(R.layout.image_preview, null)
            val grdImg = view.findViewById<RecyclerView>(R.id.grdImg)
            val imgClose = view.findViewById<ImageView>(R.id.imgClose)
            val lblassignName = view.findViewById<TextView>(R.id.lblassignName)
            lblassignName.text = data.topic.toString()


            val imagepreviewadapter = imagepreviewadapter(CommonUtil.isImageViewList, context)
            val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(context, 3)
            grdImg!!.layoutManager = mLayoutManager
            grdImg.isNestedScrollingEnabled = true
            grdImg.addItemDecoration(
                EventsViewDetails.GridSpacingItemDecoration(
                    4,
                    true
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


        holder.lnrNoticeboardd!!.setOnClickListener {
            CommonUtil.Multipleiamge.clear()
            if (data.filearray!!.isNotEmpty()) {
                holder.rytAssignmentFiles!!.visibility = View.VISIBLE
            } else {
                holder.rytAssignmentFiles!!.visibility = View.GONE
            }
            holder.rytNotice!!.visibility = View.VISIBLE
            holder.lnrSubmission!!.visibility = GONE
            Type = "noticeboard"
            if (data.isappread.equals("0")) {
                BaseActivity.AppReadStatusContext(context, Type, data.noticedetailsid!!)
                data.isappread = "1"
                holder.lblNewCircle!!.visibility = GONE
            }
            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                if (data.createdby.toString() != CommonUtil.MemberId.toString()) {
                    holder.LayoutSubmissions!!.visibility = GONE
                } else {
                    holder.img_AssignmentDelete!!.visibility = View.VISIBLE
                    holder.LayoutSubmissions!!.visibility = VISIBLE
                }
                holder.img_AssignmentDelete!!.setOnClickListener {
                    CommonUtil.Noticeboardid = data.noticeboardheaderID.toString()
                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
                    alertDialog.setTitle("Info")
                    alertDialog.setMessage("Are you want to delete?")
                    alertDialog.setPositiveButton(
                        "yes"
                    ) { _, _ ->
                        DeleteNoticeBoard("delete", positionindex)
                    }
                    alertDialog.setNegativeButton(
                        "No"
                    ) { _, _ -> }
                    val alert: AlertDialog = alertDialog.create()
                    alert.setCanceledOnTouchOutside(false)
                    alert.show()
                }
            }
            mExpandedPosition = if (isExpanded) -1 else positionindex
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return noticeboarddata.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {
        @JvmField
        @BindView(R.id.lnrNoticeboardd)
        var lnrNoticeboardd: LinearLayout? = null

        @JvmField
        @BindView(R.id.lblsubmittioncount)
        var lblsubmittioncount: TextView? = null

        @JvmField
        @BindView(R.id.lblNewCircle)
        var lblNewCircle: TextView? = null

        @JvmField
        @BindView(R.id.rytRecentNotification)
        var rytRecentNotification: RelativeLayout? = null

        @JvmField
        @BindView(R.id.lblNoticeboardTitle)
        var lblNoticeboardTitle: TextView? = null

        @JvmField
        @BindView(R.id.imgArrowdown)
        var imgArrowdown: ImageView? = null

        @JvmField
        @BindView(R.id.imgArrowUp)
        var imgArrowUp: ImageView? = null

        @JvmField
        @BindView(R.id.img_AssignmentDelete)
        var img_AssignmentDelete: ImageView? = null

        @JvmField
        @BindView(R.id.lblNoticeboardDate)
        var lblNoticeboardDate: TextView? = null

        @JvmField
        @BindView(R.id.lblNoticetime)
        var lblNoticetime: TextView? = null

        @JvmField
        @BindView(R.id.rytNotice)
        var rytNotice: RelativeLayout? = null

        @JvmField
        @BindView(R.id.lblNoticeboardDescription)
        var lblNoticeboardDescription: TextView? = null

        @JvmField
        @BindView(R.id.lblNoticePostedby)
        var lblNoticePostedby: TextView? = null

        @JvmField
        @BindView(R.id.lnrSubmission)
        var lnrSubmission: LinearLayout? = null

        @JvmField
        @BindView(R.id.LayoutSubmissions)
        var LayoutSubmissions: LinearLayout? = null

        @JvmField
        @BindView(R.id.rytAssignmentFiles)
        var rytAssignmentFiles: RelativeLayout? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        noticeboarddata = data
        this.context = context
    }

    fun DeleteNoticeBoard(type: String, positionindex: Int) {


        val jsonObject = JsonObject()

        jsonObject.addProperty("noticeboardid", CommonUtil.Noticeboardid)
        jsonObject.addProperty("processtype", type)
        jsonObject.addProperty("colgid", CommonUtil.CollegeId)
        jsonObject.addProperty("topic", "")
        jsonObject.addProperty("description", "")
        jsonObject.addProperty("staffid", CommonUtil.MemberId)
        jsonObject.addProperty("Callertype", "")
        jsonObject.addProperty("receiveridlist", "")
        jsonObject.addProperty("isstudent", "")
        jsonObject.addProperty("isstaff", "")
        jsonObject.addProperty("isparent", "")
        jsonObject.addProperty("receivertype", "")

        Log.d("jsonoblect", jsonObject.toString())

        RestClient.apiInterfaces.DeleteNoticeboarddata(jsonObject)
            ?.enqueue(object : Callback<Delete_noticeboard?> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<Delete_noticeboard?>, response: Response<Delete_noticeboard?>
                ) {
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val response = response.body()!!.Message
                            Log.d("message", response)
                            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
                            alertDialog.setTitle("Info")
                            alertDialog.setMessage(response)
                            alertDialog.setPositiveButton(
                                "yes"
                            ) { _, _ ->
                                val i: Intent = Intent(context, Noticeboard::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                context.startActivity(i)
                                //todoList.removeAt(positionindex)
//                                notifyItemRemoved(positionindex)
//                                notifyDataSetChanged()
                            }

                            alertDialog.setNegativeButton(
                                "No"
                            ) { _, _ -> }
                            val alert: AlertDialog = alertDialog.create()
                            alert.setCanceledOnTouchOutside(false)
                            alert.show()
                        }

                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {

                    }
                }

                override fun onFailure(call: Call<Delete_noticeboard?>, t: Throwable) {

                }

            })

    }


    fun filterList(filterlist: java.util.ArrayList<GetNoticeboardDetails>) {

        noticeboarddata = filterlist

        notifyDataSetChanged()
    }

}