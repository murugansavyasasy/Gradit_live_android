package com.vsca.vsnapvoicecollege.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.ExamMarkAdapter
import com.vsca.vsnapvoicecollege.Model.ExamMarkListDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityExamViewMarksBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityNoticeboardBinding

class ExamMarks : BaseActivity<ActivityExamViewMarksBinding>() {

    var examAdapter: ExamMarkAdapter? = null
    override var appViewModel: App? = null

    var ExamHeaderID: String? = ""
    var ExamName: String? = ""
    var GetStudentExamMarks: List<ExamMarkListDetails> = ArrayList()

    override fun inflateBinding(): ActivityExamViewMarksBinding {
        return ActivityExamViewMarksBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)

        super.onCreate(savedInstanceState)
        Log.d("onCreate", "lifecycle")
        binding = ActivityExamViewMarksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionBarMethod(this)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true


        accessBottomViewIcons(
            binding,
            R.id.img_swipe,
            R.id.layoutbottomCurve, R.id.recyclermenusbottom, R.id.swipeUpMenus, R.id.LayoutDepartment, R.id.LayoutCollege, R.id.imgAddPlus
        )
        MenuBottomType()
        binding.LayoutExamMarks.imgback.setOnClickListener { onBackPressed() }

        ExamHeaderID = intent.getStringExtra("ExamHeaderID")
        ExamName = intent.getStringExtra("Examname")

        binding.LayoutExamMarks.lblExamTitle.text = ExamName

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
            .into(binding.LayoutExamMarks.imgAdvertisement!!)
        Glide.with(this)
            .load(CommonUtil.CommonAdImageSmall)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.LayoutExamMarks.imgthumb!!)

        appViewModel!!.ExamMarkListLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message

                UserMenuRequest(this)

                if (status == 1) {
                    GetStudentExamMarks = response.data!!
                    val size = GetStudentExamMarks.size
                    if (size > 0) {

                        binding.LayoutExamMarks.lblNoRecordsFound!!.visibility = View.GONE
                        binding.LayoutExamMarks.recyclerCommon!!.visibility = View.VISIBLE
                        examAdapter = ExamMarkAdapter(GetStudentExamMarks, this)
                        val mLayoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(this)
                        binding.LayoutExamMarks.recyclerCommon!!.layoutManager = mLayoutManager
                        binding.LayoutExamMarks.recyclerCommon!!.itemAnimator =
                            DefaultItemAnimator()
                        binding.LayoutExamMarks.recyclerCommon!!.adapter = examAdapter
                        binding.LayoutExamMarks.recyclerCommon!!.recycledViewPool.setMaxRecycledViews(
                            0,
                            80
                        )
                        examAdapter!!.notifyDataSetChanged()
                    } else {
                        binding.LayoutExamMarks.lblNoRecordsFound!!.visibility = View.VISIBLE
                        binding.LayoutExamMarks.recyclerCommon!!.visibility = View.GONE
                    }
                } else {
                    binding.LayoutExamMarks.lblNoRecordsFound!!.visibility = View.VISIBLE
                    binding.LayoutExamMarks.recyclerCommon!!.visibility = View.GONE
                }
            } else {
                UserMenuRequest(this)
                binding.LayoutExamMarks.lblNoRecordsFound!!.visibility = View.VISIBLE
                binding.LayoutExamMarks.recyclerCommon!!.visibility = View.GONE
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

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }
}