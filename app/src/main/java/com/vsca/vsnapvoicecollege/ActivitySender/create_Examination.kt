package com.vsca.vsnapvoicecollege.ActivitySender

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Activities.ExamList
import com.vsca.vsnapvoicecollege.Adapters.Adaper_CreateExamination
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App

class create_Examination : ActionBarActivity() {

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
    @BindView(R.id.btn_conform)
    var btn_conform: Button? = null

    @JvmField
    @BindView(R.id.ibi_norecordsfound)
    var ibi_norecordsfound: TextView? = null

    var sectionnamelist: ArrayList<sectionnamelist>? = null
    var adapterExamination: Adaper_CreateExamination? = null
    var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this)
        imgRefresh!!.visibility = View.GONE

        appViewModel!!.AdvertisementLiveData?.observe(
            this,
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
                        Glide.with(this)
                            .load(AdBackgroundImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this)
                            .load(AdSmallImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgthumb!!)
                    }
                }
            })


        appViewModel!!.Examcreationdata!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message

                if (status == 1) {

                    val dlg1 = this.let { AlertDialog.Builder(it) }
                    dlg1.setTitle(CommonUtil.Info)
                    dlg1.setMessage(message)
                    dlg1.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->


                            var i = Intent(this, ExamList::class.java)

                            CommonUtil.Examination_Creation.clear()
                            CommonUtil.Subjectdetail_ExamCreation.clear()
                            CommonUtil.Sectiondetail_ExamCreation.clear()
                            CommonUtil.ExamcreationEdit.clear()
                            CommonUtil.SubjectExamcreationEDIT.clear()

                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)

                        })

                    dlg1.setCancelable(false)
                    dlg1.create()
                    dlg1.show()

                } else {
                    CommonUtil.ApiAlert(this, message)
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        appViewModel!!.ExamcreationEditData!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message

                if (status == 1) {

                    val dlg1 = this.let { AlertDialog.Builder(it) }
                    dlg1.setTitle(CommonUtil.Info)
                    dlg1.setMessage(message)
                    dlg1.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->


                            var i = Intent(this, ExamList::class.java)

                            CommonUtil.Examination_Creation.clear()
                            CommonUtil.Subjectdetail_ExamCreation.clear()
                            CommonUtil.Sectiondetail_ExamCreation.clear()
                            CommonUtil.ExamcreationEdit.clear()
                            CommonUtil.SubjectExamcreationEDIT.clear()

                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)

                        })

                    dlg1.setCancelable(false)
                    dlg1.create()
                    dlg1.show()

                } else {
                    CommonUtil.ApiAlert(this, message)
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }


        appViewModel!!.Examsectionlist!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                AdForCollegeApi()

                if (status == 1) {
                    sectionnamelist = response.data as ArrayList<sectionnamelist>

                    CommonUtil.seleteddataArray.clear()
                    adapterExamination = Adaper_CreateExamination(sectionnamelist!!, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    create_exam_recycle!!.layoutManager = mLayoutManager
                    create_exam_recycle!!.itemAnimator = DefaultItemAnimator()
                    create_exam_recycle!!.adapter = adapterExamination
                    create_exam_recycle!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    adapterExamination!!.notifyDataSetChanged()
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        btn_conform!!.setOnClickListener {

            if (CommonUtil.EditButtonclick.equals("ExamEdit")) {

                val alertDialog: AlertDialog.Builder =
                    AlertDialog.Builder(this@create_Examination)
                alertDialog.setTitle(CommonUtil.Info)
                alertDialog.setMessage(CommonUtil.upload_exam)
                alertDialog.setPositiveButton(
                    CommonUtil.Yes
                ) { _, _ ->

                    ExamcreationEdit()

                }
                alertDialog.setNegativeButton(
                    CommonUtil.No
                ) { _, _ -> }
                val alert: AlertDialog = alertDialog.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()

                CommonUtil.Venue = ""
                CommonUtil.examsyllabus = ""
                CommonUtil.examdate = ""
                CommonUtil.section = ""

            } else {

                if (CommonUtil.Venue.isNotEmpty() && CommonUtil.examsyllabus.isNotEmpty() && CommonUtil.examdate.isNotEmpty() && CommonUtil.section.isNotEmpty()) {
                    val alertDialog: AlertDialog.Builder =
                        AlertDialog.Builder(this@create_Examination)
                    alertDialog.setTitle(CommonUtil.Info)
                    alertDialog.setMessage(CommonUtil.upload_exam)
                    alertDialog.setPositiveButton(
                        CommonUtil.Yes
                    ) { _, _ ->

                        Examcreation()

                    }
                    alertDialog.setNegativeButton(
                        CommonUtil.No
                    ) { _, _ -> }
                    val alert: AlertDialog = alertDialog.create()
                    alert.setCanceledOnTouchOutside(false)
                    alert.show()

                    CommonUtil.Venue = ""
                    CommonUtil.examsyllabus = ""
                    CommonUtil.examdate = ""
                    CommonUtil.section = ""

                } else {
                    CommonUtil.ApiAlert(this, "Fill all the Exam Details")
                }
            }
        }
    }

    private fun SemesterRequest() {
        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_appid, "1")
            jsonObject.addProperty(ApiRequestNames.Req_semesterid, CommonUtil.semesterid)

            appViewModel!!.Examsectionandsubject(jsonObject, this)
            Log.d("SemesterSection:", jsonObject.toString())
        }
    }

    private fun AdForCollegeApi() {

        val mobilenumber = SharedPreference.getSH_MobileNumber(this)
        val devicetoken = SharedPreference.getSH_DeviceToken(this)
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

    private fun Examcreation() {

        val jsonObject = JsonObject()

        for (k in 0 until CommonUtil.Examination_Creation.size) {

            jsonObject.addProperty(
                ApiRequestNames.Req_collegeid,
                CommonUtil.Examination_Creation[k].collegeid
            )
            jsonObject.addProperty(
                ApiRequestNames.Req_staffid,
                CommonUtil.Examination_Creation[k].staffid
            )
            jsonObject.addProperty(
                ApiRequestNames.Req_examid,
                CommonUtil.Examination_Creation[k].examid
            )
            jsonObject.addProperty(
                ApiRequestNames.Req_departmentid,
                CommonUtil.deptidExam
            )
            jsonObject.addProperty(
                ApiRequestNames.Req_examname,
                CommonUtil.Examination_Creation[k].examname
            )
            jsonObject.addProperty(
                ApiRequestNames.Req_startdate,
                CommonUtil.Examination_Creation[k].startdate
            )
            jsonObject.addProperty(
                ApiRequestNames.Req_enddate,
                CommonUtil.Examination_Creation[k].enddate
            )
            jsonObject.addProperty(
                ApiRequestNames.Req_processtype,
                CommonUtil.Examination_Creation[k].processtype
            )

            CommonUtil.Sectiondetail_ExamCreation =
                CommonUtil.Examination_Creation[k].Sectiondetails


            val SectionDetails = JsonArray()
            var previous_section_id = ""
            var previous_subjects = ""

            for (i in CommonUtil.Subjectdetail_ExamCreation.indices) {
                val section_split: Array<String> =
                    CommonUtil.Subjectdetail_ExamCreation[i].examsubjectid!!.split(" /".toRegex())
                        .toTypedArray()
                val section_id: String = section_split[0]
                val sectiondetailsobject = JsonObject()

                if (!section_id.equals(previous_section_id)) {
                    sectiondetailsobject.addProperty(ApiRequestNames.Req_clgsectionid, section_id)
                    previous_section_id = section_id

                }
                val subjectdetailsarray = JsonArray()

                if (!section_id.equals(previous_subjects)) {
                    for (j in CommonUtil.Subjectdetail_ExamCreation.indices) {

                        val section_split: Array<String> =
                            CommonUtil.Subjectdetail_ExamCreation[j].examsubjectid!!.split(" /".toRegex())
                                .toTypedArray()
                        val sec_id: String = section_split[0]
                        val sub_id: String = section_split[1]

                        if (sec_id.equals(section_id)) {
                            val subjectobject = JsonObject()

                            subjectobject.addProperty(
                                ApiRequestNames.Req_examsubjectid,
                                sub_id
                            )
                            subjectobject.addProperty(
                                ApiRequestNames.Req_examdate,
                                CommonUtil.Subjectdetail_ExamCreation[j].examdate
                            )
                            subjectobject.addProperty(
                                ApiRequestNames.Req_examsyllabus,
                                CommonUtil.Subjectdetail_ExamCreation[j].examsyllabus
                            )
                            subjectobject.addProperty(
                                ApiRequestNames.Req_examvenue,
                                CommonUtil.Subjectdetail_ExamCreation[j].examvenue
                            )
                            subjectobject.addProperty(
                                ApiRequestNames.Req_examsession,
                                CommonUtil.Subjectdetail_ExamCreation[j].examsession
                            )

                            subjectdetailsarray.add(subjectobject)

                        }
                    }

                    sectiondetailsobject.add(
                        ApiRequestNames.Req_Subjectdetails,
                        subjectdetailsarray
                    )
                    previous_subjects = section_id
                }


                if (!sectiondetailsobject.toString().equals("{}")) {
                    SectionDetails.add(sectiondetailsobject)
                }
                jsonObject.add(ApiRequestNames.Req_sectiondetails, SectionDetails)

            }
            jsonObject.add(ApiRequestNames.Req_sectiondetails, SectionDetails)
        }
        appviewModelbase!!.Examcreation(jsonObject, this)
        Log.d("Examcreation:", jsonObject.toString())

    }


    private fun ExamcreationEdit() {

        val jsonObject = JsonObject()


        for (k in 0 until CommonUtil.ExamcreationEdit.size) {

            jsonObject.addProperty(
                ApiRequestNames.Req_colgid,
                CommonUtil.ExamcreationEdit.get(k).collegeid
            )
            jsonObject.addProperty(
                ApiRequestNames.Req_userid,
                CommonUtil.ExamcreationEdit.get(k).staffid
            )
            jsonObject.addProperty(
                ApiRequestNames.Req_Examid,
                CommonUtil.ExamcreationEdit.get(k).examid
            )
            jsonObject.addProperty(
                ApiRequestNames.Req_sectionid,
                CommonUtil.ExamcreationEdit.get(k).sectionid
            )

            jsonObject.addProperty(
                ApiRequestNames.Req_processtype,
                CommonUtil.ExamcreationEdit.get(k).processtype
            )

            CommonUtil.SubjectExamcreationEDIT =
                CommonUtil.ExamcreationEdit.get(k).SubjectExamcreationEDIT

            val subjectdetailsarray = JsonArray()
            for (i in 0 until CommonUtil.SubjectExamcreationEDIT.size) {

                val subjectdetails = JsonObject()


                subjectdetails.addProperty(
                    ApiRequestNames.Req_clgsubjectid,
                    CommonUtil.SubjectExamcreationEDIT.get(i).examsubjectid
                )
                subjectdetails.addProperty(
                    ApiRequestNames.Req_examdate,
                    CommonUtil.SubjectExamcreationEDIT.get(i).examdate
                )
                subjectdetails.addProperty(
                    ApiRequestNames.Req_examsyllabus,
                    CommonUtil.SubjectExamcreationEDIT.get(i).examsyllabus
                )
                subjectdetails.addProperty(
                    ApiRequestNames.Req_examvenue,
                    CommonUtil.SubjectExamcreationEDIT.get(i).examvenue
                )
                subjectdetails.addProperty(
                    ApiRequestNames.Req_examsession,
                    CommonUtil.SubjectExamcreationEDIT.get(i).examsession
                )
                subjectdetailsarray.add(subjectdetails)
            }
            jsonObject.add(ApiRequestNames.Req_subjectdetails, subjectdetailsarray)

        }
        appviewModelbase!!.ExamEdit(jsonObject, this)
        Log.d("ExamcreationEdit:", jsonObject.toString())

    }

    override val layoutResourceId: Int
        get() = R.layout.activity_create_examination

    @OnClick(R.id.LayoutAdvertisement)
    fun adclick() {
        BaseActivity.LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        SemesterRequest()
        super.onResume()
    }

    @OnClick(R.id.imgback)
    fun imgback() {
        onBackPressed()
        CommonUtil.Examination_Creation.clear()
        CommonUtil.Subjectdetail_ExamCreation.clear()
        CommonUtil.Sectiondetail_ExamCreation.clear()
    }
}