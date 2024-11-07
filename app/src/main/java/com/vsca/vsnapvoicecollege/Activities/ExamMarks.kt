package com.vsca.vsnapvoicecollege.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.ExamMarkAdapter
import com.vsca.vsnapvoicecollege.Model.ExamMarkListDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App

class ExamMarks : BaseActivity() {

    var examAdapter: ExamMarkAdapter? = null
    override var appViewModel: App? = null

    @JvmField
    @BindView(R.id.recyclerCommon)
    var recyclerNoticeboard: RecyclerView? = null

    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    @JvmField
    @BindView(R.id.lbltotalsize)
    var lbltotalsize: TextView? = null

    @JvmField
    @BindView(R.id.lblMenuTitle)
    var lblMenuTitle: TextView? = null

    @JvmField
    @BindView(R.id.lblDepartment)
    var lblDepartment: TextView? = null

    @JvmField
    @BindView(R.id.lblCollege)
    var lblCollege: TextView? = null

    @JvmField
    @BindView(R.id.lblDepartmentSize)
    var lblDepartmentSize: TextView? = null

    @JvmField
    @BindView(R.id.lblCollegeSize)
    var lblCollegeSize: TextView? = null

    @JvmField
    @BindView(R.id.lblNoRecordsFound)
    var lblNoRecordsFound: TextView? = null

    @JvmField
    @BindView(R.id.LayoutExamMarks)
    var LayoutExamMarks: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.CommonLayout)
    var CommonLayout: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.lblExamTitle)
    var lblExamTitle: TextView? = null

    var ExamHeaderID: String? = ""
    var ExamName: String? = ""
    var GetStudentExamMarks: List<ExamMarkListDetails> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)

        super.onCreate(savedInstanceState)
        Log.d("onCreate", "lifecycle")
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionBarMethod(this)
        MenuBottomType()

        ExamHeaderID = intent.getStringExtra("ExamHeaderID")
        ExamName = intent.getStringExtra("Examname")

        lblExamTitle!!.text = ExamName

        CommonUtil.MenuExamination = false
        CommonUtil.MenuAssignment = true
        CommonUtil.MenuCourseDetails = true
        CommonUtil.MenuExamDetails = true
        CommonUtil.MenuNoticeBoard = true
        CommonUtil.MenuDashboardHome = true
        CommonUtil.MenuCircular = true
        CommonUtil.MenuChat = true
        CommonUtil.MenuCommunication = true
        CommonUtil.MenuExamination = true
        CommonUtil.MenuAttendance = true
        CommonUtil.MenuEvents = true
        CommonUtil.MenuFaculty = true
        CommonUtil.MenuVideo = true
        CommonUtil.MenuCategoryCredit = true
        CommonUtil.MenuSemCredit = true

        Glide.with(this)
            .load(CommonUtil.CommonAdvertisement)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imgAdvertisement!!)
        Glide.with(this)
            .load(CommonUtil.CommonAdImageSmall)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imgthumb!!)

        appViewModel!!.ExamMarkListLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message

                UserMenuRequest(this)

                if (status == 1) {
                    GetStudentExamMarks = response.data!!
                    val size = GetStudentExamMarks.size
                    if (size > 0) {

                        lblNoRecordsFound!!.visibility = View.GONE
                        recyclerNoticeboard!!.visibility = View.VISIBLE
                        examAdapter = ExamMarkAdapter(GetStudentExamMarks, this)
                        val mLayoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(this)
                        recyclerNoticeboard!!.layoutManager = mLayoutManager
                        recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                        recyclerNoticeboard!!.adapter = examAdapter
                        recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        examAdapter!!.notifyDataSetChanged()
                    } else {
                        lblNoRecordsFound!!.visibility = View.VISIBLE
                        recyclerNoticeboard!!.visibility = View.GONE
                    }
                } else {
                    lblNoRecordsFound!!.visibility = View.VISIBLE
                    recyclerNoticeboard!!.visibility = View.GONE
                }
            } else {
                UserMenuRequest(this)
                lblNoRecordsFound!!.visibility = View.VISIBLE
                recyclerNoticeboard!!.visibility = View.GONE
            }
        }

        imgRefresh!!.setOnClickListener(View.OnClickListener {
            ExamMarkListRequest()
        })

    }

    override val layoutResourceId: Int
        get() = R.layout.activity_exam_view_marks

    override fun onResume() {
        ExamMarkListRequest()
        Log.d("onResume", "lifecycle")
        super.onResume()
    }


    private fun ExamMarkListRequest() {
        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_studentid, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_examheaderid, ExamHeaderID)

            appViewModel!!.getStudentExamMarklist(jsonObject, this)
            Log.d("ExamMarksRequest:", jsonObject.toString())
        }
    }

    @OnClick(R.id.imgback)
    fun imgbackClikc() {
        onBackPressed()
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }
}