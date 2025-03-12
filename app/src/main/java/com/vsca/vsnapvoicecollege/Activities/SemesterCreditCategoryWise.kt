package com.vsca.vsnapvoicecollege.Activities

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.SemesterCreditAllCategoryAdapter
import com.vsca.vsnapvoicecollege.Adapters.SemesterCreditsAdapter
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivitySemesterCreditTableBinding
import com.vsca.vsnapvoicecollege.databinding.BottomMenuSwipeBinding

class SemesterCreditCategoryWise : BaseActivity<ActivitySemesterCreditTableBinding>() {

    override var appViewModel: App? = null
    var semesterCreditsAdapter: SemesterCreditsAdapter? = null
    var semesterAllCreditsAdapter: SemesterCreditAllCategoryAdapter? = null
    var GetSemesterTypeData: List<GetSemesterWiseDetails> = ArrayList()
    var GetSemesterCreditWiseData: ArrayList<GetSemesterWiseCreditDetails> = ArrayList()
    var GetSemesterCreditWiseAllData: ArrayList<GetSemesterCreditCategeroy> = ArrayList()
    var GetSemesterAllListData: ArrayList<GetSemsterFinalCategoryAllDetails> = ArrayList()
    val SemAllCreditList = ArrayList<SemesterAllCategory>()
    val SemesterCategoeryCreditsAllList = ArrayList<SemesterAllCategoryCredits>()
    var Categoryidhead: String? = null
    var categoryNamehead: String? = null
    var countryOpen = false
    var Semestername: String? = null
    var CategoryId: String? = null
    var selectedCategoryID: String? = null
    var selectedradioValue: String? = null

    override fun inflateBinding(): ActivitySemesterCreditTableBinding {
        return ActivitySemesterCreditTableBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySemesterCreditTableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lblMenuHeaderName!!.text = getString(R.string.txt_semester_credit_points)

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        if (CommonUtil.menu_readSemCreditPoints.equals("1")) {
            SemesterType()
        }
        accessBottomViewIcons(
            binding,
            R.id.img_swipe,
            R.id.layoutbottomCurve, R.id.recyclermenusbottom, R.id.swipeUpMenus, R.id.LayoutDepartment, R.id.LayoutCollege, R.id.imgAddPlus
        )
        MenuBottomType()

        CommonUtil.OnMenuClicks("SemCredit")
        binding.imgheaderBack.setOnClickListener {
            onBackPressed()
        }

        binding.idRVCategories!!.setBackgroundColor(Color.parseColor("#f2f2f2"))

        appViewModel!!.SemesterWiseCreditAllLiveData?.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                GetSemesterCreditWiseData.clear()
                GetSemesterAllListData.clear()
                if (status == 1) {
                    UserMenuRequest(this)
                    SemAllCreditList.clear()
                    GetSemesterCreditWiseAllData = response.data!!
                    var listSize = GetSemesterCreditWiseAllData.size
                    if (listSize > 0) {
                        binding.lblNoRecordsFound!!.visibility = View.GONE
                        binding.idRVCategories!!.visibility = View.VISIBLE
                        binding.TableSemCategory!!.visibility = View.GONE
                        binding.LayoutSemesterTable!!.visibility = View.VISIBLE

                        for (i in GetSemesterCreditWiseAllData.indices) {
                            categoryNamehead = GetSemesterCreditWiseAllData[i].category_name
                            Categoryidhead = GetSemesterCreditWiseAllData[i].category_id
                            GetSemesterAllListData = GetSemesterCreditWiseAllData[i].list!!


                            for (j in GetSemesterAllListData.indices) {

                                val categorid = GetSemesterAllListData[j].category_id
                                val categoryName = GetSemesterAllListData[j].category_name
                                val semestername = GetSemesterAllListData[j].semester_name
                                val totalCredits = GetSemesterAllListData[j].total_credits
                                val obtained = GetSemesterAllListData[j].obtained
                                val to_be_obtained = GetSemesterAllListData[j].to_be_obtained
                                SemAllCreditList.add(
                                    SemesterAllCategory(
                                        categorid,
                                        categoryName,
                                        semestername,
                                        totalCredits,
                                        obtained,
                                        to_be_obtained
                                    )
                                )
                            }

                            SemesterCategoeryCreditsAllList.add(
                                SemesterAllCategoryCredits(
                                    categoryNamehead!!,
                                    SemAllCreditList
                                )
                            )

                            semesterAllCreditsAdapter =
                                SemesterCreditAllCategoryAdapter(SemAllCreditList, this)
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            binding.idRVCategories!!.layoutManager = mLayoutManager
                            binding.idRVCategories!!.itemAnimator = DefaultItemAnimator()
                            binding.idRVCategories!!.adapter = semesterAllCreditsAdapter
                            binding.idRVCategories!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            semesterAllCreditsAdapter!!.notifyDataSetChanged()
                        }


                    } else {
                        binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                        binding.idRVCategories!!.visibility = View.GONE
                    }
                } else {
                    binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                    binding.idRVCategories!!.visibility = View.GONE
                }
            } else {
                binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                binding.idRVCategories!!.visibility = View.GONE
            }


        }

        appViewModel!!.SemesterWiseCreditLiveData?.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                GetSemesterCreditWiseData.clear()

                if (status == 1) {
                    UserMenuRequest(this)
                    GetSemesterCreditWiseData = response.data!!

                    var listSize = GetSemesterCreditWiseData.size
                    if (listSize > 0) {
                        binding.lblNoRecordsFound!!.visibility = View.GONE
                        binding.idRVCategories!!.visibility = View.VISIBLE
                        binding.LayoutSemesterTable!!.visibility = View.VISIBLE
                        binding.TableSemCategory!!.visibility = View.GONE

                        semesterCreditsAdapter =
                            SemesterCreditsAdapter(GetSemesterCreditWiseData, this)
                        val mLayoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(this)
                        binding.idRVCategories!!.layoutManager = mLayoutManager
                        binding.idRVCategories!!.itemAnimator = DefaultItemAnimator()
                        binding.idRVCategories!!.adapter = semesterCreditsAdapter
                        binding.idRVCategories!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        semesterCreditsAdapter!!.notifyDataSetChanged()

                    } else {
                        binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                        binding.idRVCategories!!.visibility = View.GONE
                    }

                } else {
                    binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                    binding.idRVCategories!!.visibility = View.GONE
                }

            } else {
                binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                binding.idRVCategories!!.visibility = View.GONE
            }
        }

        appViewModel!!.SemesterTypeLiveData?.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    UserMenuRequest(this)
                    GetSemesterTypeData = response.data!!
                    SetSpinnerValue()

                } else {
                    binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                    binding.idRVCategories!!.visibility = View.GONE
                }
            } else {
                binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                binding.idRVCategories!!.visibility = View.GONE
            }
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_semester_credit_table

    private fun SetSpinnerValue() {
        binding.layoutDropdown!!.setOnClickListener {
            if (!countryOpen) {
                binding.layoutDropdown!!.visibility = View.VISIBLE
                binding.viewLine!!.visibility = View.VISIBLE
                binding.imgDropdown!!.setImageResource(R.drawable.ic_arraow_up)
                countryOpen = true
                binding.Layoutoverall!!.visibility = View.GONE

                Log.d("Open", countryOpen.toString())
            } else {
                binding.layoutDropdown!!.visibility = View.GONE
                binding.viewLine!!.visibility = View.GONE
                binding.imgDropdown!!.setImageResource(R.drawable.ic_arrow_down)
                countryOpen = false
                binding.Layoutoverall!!.visibility = View.GONE
                Log.d("Close", countryOpen.toString())

            }
        }

        for (i in GetSemesterTypeData.indices) {
            Semestername = GetSemesterTypeData[i].semseter_name
            CategoryId = GetSemesterTypeData[i].semester_id
            val rb = TextView(this)
            val selectedvalue = GetSemesterTypeData[i].semseter_name
            rb.text = selectedvalue
            rb.textSize = 16f
            rb.setTextColor(resources.getColor(R.color.clr_black))
            rb.layoutDirection = View.LAYOUT_DIRECTION_LTR
            rb.id = i
            selectedradioValue = rb.text.toString()
            val params = android.widget.RadioGroup.LayoutParams(
                android.widget.RadioGroup.LayoutParams.MATCH_PARENT,
                android.widget.RadioGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(15, 15, 10, 15)

            binding.RadioGroup!!.addView(rb, params)
            rb.setOnClickListener {
                val list = GetSemesterTypeData[i]
                val semestername = list.semseter_name
                selectedCategoryID = list.semester_id
                binding.lblCategoryName!!.text = semestername
                binding.layoutDropdown!!.visibility = View.GONE
                binding.viewLine!!.visibility = View.GONE
                binding.imgDropdown!!.setImageResource(R.drawable.ic_arrow_down)
                binding.Layoutoverall!!.visibility = View.VISIBLE
                countryOpen = false
                SemesterWiseRequest(selectedCategoryID!!)

            }
        }
    }

    private fun SemesterWiseRequest(SemesterID: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_i_course_id, CommonUtil.Courseid)
        jsonObject.addProperty(ApiRequestNames.Req_i_sem_id, SemesterID)
        jsonObject.addProperty(ApiRequestNames.Req_i_student_id, CommonUtil.MemberId)
        if (SemesterID.equals("-5")) {
            appViewModel!!.getSemesterWiseCreditAll(jsonObject, this)
        } else {
            appViewModel!!.getSemesterWiseCredit(jsonObject, this)
        }
    }

    override fun onBackPressed() {
        CommonUtil.MenuCourseDetails = true
        CommonUtil.MenuExamDetails = true
        CommonUtil.MenuNoticeBoard = true
        CommonUtil.MenuDashboardHome = true
        CommonUtil.MenuCircular = true
        CommonUtil.MenuAssignment = true
        CommonUtil.MenuChat = true
        CommonUtil.MenuCommunication = true
        CommonUtil.MenuExamination = true
        CommonUtil.MenuAttendance = true
        CommonUtil.MenuEvents = true
        CommonUtil.MenuFaculty = true
        CommonUtil.MenuVideo = true
        CommonUtil.MenuCategoryCredit = true
        CommonUtil.MenuSemCredit = true
        super.onBackPressed()
    }


    private fun SemesterType() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_i_course_id, CommonUtil.Courseid)
        appViewModel!!.getSmesterType(jsonObject, this)
        Log.d("SemsterType:", jsonObject.toString())
    }

}