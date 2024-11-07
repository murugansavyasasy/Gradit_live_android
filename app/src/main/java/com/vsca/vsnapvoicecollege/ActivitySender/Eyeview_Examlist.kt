package com.vsca.vsnapvoicecollege.ActivitySender

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Adapters.Examlist_viewAdapter
import com.vsca.vsnapvoicecollege.Interfaces.ExamSubjectclick
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App

class Eyeview_Examlist : BaseActivity() {

    @JvmField
    @BindView(R.id.create_exam_recycle)
    var create_exam_recycle: RecyclerView? = null

    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    @JvmField
    @BindView(R.id.lblDepartmentSize)
    var lblDepartmentSize: TextView? = null


    var examviewlist: List<examlist> = ArrayList()
    var Examlist_viewAdapter: Examlist_viewAdapter? = null
    override var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var Subjectcount: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)

        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionBarMethod(this)
        MenuBottomType()

        CommonUtil.ExamcreationEdit.clear()
        CommonUtil.SubjectExamcreationEDIT.clear()

        appViewModel!!.AdvertisementLiveData?.observe(this,
            Observer<GetAdvertisementResponse?> { response ->
                if (response != null) {
                    val status = response.status
                    val message = response.message

                    if (status == 1) {
                        GetAdForCollegeData = response.data!!
                        for (j in GetAdForCollegeData.indices) {
                            AdSmallImage = GetAdForCollegeData[j].add_image
                            AdBackgroundImage = GetAdForCollegeData[0].background_image!!
                            AdWebURl = GetAdForCollegeData[0].add_url.toString()
                        }
                        Glide.with(this).load(AdBackgroundImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)


                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgthumb!!)
                    }
                }
            })


        appViewModel!!.Examview!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                UserMenuRequest(this)
                AdForCollegeApi()

                if (status == 1) {
                    examviewlist = response.data

                    val SubjectCount = examviewlist.size

                    if (examviewlist.size > 0) {
                        Subjectcount = SubjectCount.toString()
                        lblDepartmentSize!!.text = Subjectcount
                        lblDepartmentSize!!.visibility = View.VISIBLE
                    } else {
                        lblDepartmentSize!!.visibility = View.GONE
                    }

                    Examlist_viewAdapter =
                        Examlist_viewAdapter(examviewlist, this, true, object : ExamSubjectclick {
                            override fun onSubjeckClicklistener(
                                holder: Examlist_viewAdapter.MyViewHolder, item: examlist
                            ) {

                            }
                        })

                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    create_exam_recycle!!.layoutManager = mLayoutManager
                    create_exam_recycle!!.itemAnimator = DefaultItemAnimator()
                    create_exam_recycle!!.adapter = Examlist_viewAdapter
                    create_exam_recycle!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    Examlist_viewAdapter!!.notifyDataSetChanged()
                }
            }
        }

        appViewModel!!.ExamSectiondelete!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    val dlg = this.let { AlertDialog.Builder(this@Eyeview_Examlist) }
                    dlg.setTitle(message)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent = Intent(
                                this@Eyeview_Examlist, Eyeview_Examlist::class.java
                            )
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)

                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                }
            }
        }


        appViewModel!!.ExamEditORdelete!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                AdForCollegeApi()

                if (status == 1) {
                    examviewlist = response.data

                    val SubjectCount = examviewlist.size

                    if (examviewlist.size > 0) {
                        Subjectcount = SubjectCount.toString()
                        lblDepartmentSize!!.text = Subjectcount
                        lblDepartmentSize!!.visibility = View.VISIBLE
                    } else {
                        lblDepartmentSize!!.visibility = View.GONE
                    }


                    Examlist_viewAdapter =
                        Examlist_viewAdapter(examviewlist, this, true, object : ExamSubjectclick {
                            override fun onSubjeckClicklistener(
                                holder: Examlist_viewAdapter.MyViewHolder, item: examlist
                            ) {

                                holder.txt_editbtn!!.setOnClickListener {

                                    val i = Intent(
                                        this@Eyeview_Examlist,
                                        create_Examination::class.java
                                    )
                                    CommonUtil.SectionID_Exam = item.clgsectionid

                                    CommonUtil.subjectdetails = item.subjectdetails


                                    CommonUtil.ExamcreationEdit.add(
                                        ExamcreationEdit(
                                            CommonUtil.CollegeId,

                                            item.examheaderid,
                                            "edit",
                                            CommonUtil.MemberId,

                                            item.clgsectionid,
                                            CommonUtil.SubjectExamcreationEDIT
                                        )
                                    )

                                    if (item.clgsectionid.equals(CommonUtil.SectionID_Exam)) {

                                        for (i in item.subjectdetails.indices) {
                                            CommonUtil.SubjectExamcreationEDIT.add(
                                                SubjectExamcreationEDIT(
                                                    item.subjectdetails[i].examdate,
                                                    item.subjectdetails[i].examsession,
                                                    item.subjectdetails[i].examsubjectid,
                                                    item.subjectdetails[i].examsyllabus,
                                                    item.subjectdetails[i].examvenue
                                                )
                                            )
                                        }
                                    }

                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    CommonUtil.SectionId = item.clgsectionid
                                    startActivity(i)

                                }

                                holder.txt_deletebtn!!.setOnClickListener {

                                    val dlg =
                                        this.let { AlertDialog.Builder(this@Eyeview_Examlist) }
                                    dlg.setTitle(CommonUtil.Delete_ExamSection)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->

                                            ExamSectionDelete()

                                        })

                                    dlg.setNegativeButton(CommonUtil.CANCEL,
                                        DialogInterface.OnClickListener { dialog, which ->

                                        })

                                    dlg.setCancelable(false)
                                    dlg.create()
                                    dlg.show()


                                }
                            }
                        })

                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    create_exam_recycle!!.layoutManager = mLayoutManager
                    create_exam_recycle!!.itemAnimator = DefaultItemAnimator()
                    create_exam_recycle!!.adapter = Examlist_viewAdapter
                    create_exam_recycle!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    Examlist_viewAdapter!!.notifyDataSetChanged()
                }
            }
        }

        imgRefresh!!.setOnClickListener(View.OnClickListener {
            if (CommonUtil.EditButtonclick.equals("ExamEdit")) {
                ExamEditANDdelete()
                CommonUtil.ExamcreationEdit.clear()
                CommonUtil.SubjectExamcreationEDIT.clear()
            } else {
                Examviewdata()
            }
        })
    }


    private fun Examviewdata() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_Examid, CommonUtil.headerid)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        appViewModel!!.Examview(jsonObject, this)
        Log.d("GetExamview", jsonObject.toString())
    }

    private fun ExamEditANDdelete() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_Examid, CommonUtil.headerid)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        appViewModel!!.ExamEditANDdELETE(jsonObject, this)
        Log.d("ExamEditANDdELETE", jsonObject.toString())
    }

    private fun ExamSectionDelete() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_Examid, CommonUtil.headerid)
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_processtype, "delete")
        jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.SectionID_Exam)
        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        var jsonarray = JsonArray()
        jsonObject.add("subjectdetails", jsonarray)

        appViewModel!!.ExamDeleteSection(jsonObject, this)
        Log.d("ExaxSectionDelete", jsonObject.toString())
    }

    private fun AdForCollegeApi() {

        var mobilenumber = SharedPreference.getSH_MobileNumber(this)
        var devicetoken = SharedPreference.getSH_DeviceToken(this)
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_ad_device_token, devicetoken)
        jsonObject.addProperty(ApiRequestNames.Req_MemberID, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_mobileno, mobilenumber)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_previous_add_id, PreviousAddId)
        appviewModelbase!!.getAdforCollege(jsonObject, this)
        Log.d("AdForCollege:", jsonObject.toString())

        PreviousAddId = PreviousAddId + 1
        Log.d("PreviousAddId", PreviousAddId.toString())
    }

    override val layoutResourceId: Int
        get() = R.layout.examview_activity

    @OnClick(R.id.LayoutAdvertisement)
    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        super.onResume()

        if (CommonUtil.EditButtonclick.equals("ExamEdit")) {
            ExamEditANDdelete()
            CommonUtil.ExamcreationEdit.clear()
            CommonUtil.SubjectExamcreationEDIT.clear()
        } else {
            Examviewdata()
        }
    }

    @OnClick(R.id.imgback)
    fun imgback() {
        CommonUtil.EditButtonclick = ""
        onBackPressed()
    }
}