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
import com.vsca.vsnapvoicecollege.Adapters.CategoryCreditWiseAdapter
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityCategoryCreditWiseBinding

class CategoryCreditWise : BaseActivity<ActivityCategoryCreditWiseBinding>() {

    override var appViewModel: App? = null
    var categorycreditAdapter: CategoryCreditWiseAdapter? = null
    var GetCategoryTypeData: List<GetCategoryTypeDetails> = ArrayList()
    var GetCategoryCreditData: ArrayList<GetCategoryWiseCreditDetails> = ArrayList()
    var countryOpen = false
    var Categoryname: String? = null
    var CategoryId: String? = null
    var selectedCategoryID: String? = null
    var selectedradioValue: String? = null
    override fun inflateBinding(): ActivityCategoryCreditWiseBinding {
        return ActivityCategoryCreditWiseBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryCreditWiseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        if (CommonUtil.menu_readCategoryCreditPoints.equals("1")) {
            categeroyType()
        }

        accessBottomViewIcons(
            binding,
            R.id.img_swipe,
            R.id.layoutbottomCurve,
            R.id.recyclermenusbottom,
            R.id.swipeUpMenus,
            R.id.LayoutDepartment,
            R.id.LayoutCollege,
            R.id.imgAddPlus
        )
        MenuBottomType()

        CommonUtil.OnMenuClicks("CategoryCredit")

        binding.imgheaderBack.setOnClickListener { setImgheaderBackclick() }
        binding.idRVCategories!!.setBackgroundColor(Color.parseColor("#f2f2f2"))

        binding.LayoutTable!!.visibility = View.GONE

        appViewModel!!.CategoryWiseCreditLiveData?.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                GetCategoryCreditData.clear()

                if (status == 1) {
                    UserMenuRequest(this)
                    GetCategoryCreditData = response.data!!

                    var listSize = GetCategoryCreditData.size
                    if (listSize > 0) {
                        binding.lblNoRecordsFound!!.visibility = View.GONE
                        binding.idRVCategories!!.visibility = View.VISIBLE
                        binding.LayoutTable!!.visibility = View.VISIBLE

                        categorycreditAdapter =
                            CategoryCreditWiseAdapter(GetCategoryCreditData, this)
                        val mLayoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(this)
                        binding.idRVCategories!!.layoutManager = mLayoutManager
                        binding.idRVCategories!!.itemAnimator = DefaultItemAnimator()
                        binding.idRVCategories!!.adapter = categorycreditAdapter
                        binding.idRVCategories!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        categorycreditAdapter!!.notifyDataSetChanged()
                    } else {
                        binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                        binding.idRVCategories!!.visibility = View.GONE
                        binding.LayoutTable!!.visibility = View.GONE

                    }
                } else {
                    binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                    binding.idRVCategories!!.visibility = View.GONE
                    binding.LayoutTable!!.visibility = View.GONE
                }
            } else {
                binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                binding.idRVCategories!!.visibility = View.GONE
                binding.LayoutTable!!.visibility = View.GONE
            }
        }

        appViewModel!!.CategoryTypeLiveData?.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    UserMenuRequest(this)
                    GetCategoryTypeData = response.data!!
                    SetSpinnerValue()

                } else {

                    binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                    binding.idRVCategories!!.visibility = View.GONE
                    binding.LayoutTable!!.visibility = View.GONE
                }
            } else {
                binding.lblNoRecordsFound!!.visibility = View.VISIBLE
                binding.idRVCategories!!.visibility = View.GONE
                binding.LayoutTable!!.visibility = View.GONE

            }
        }

    }

    private fun SetSpinnerValue() {
        binding.layoutDropdown!!.setOnClickListener {
            if (!countryOpen) {
                binding.lnrRadioGroup!!.visibility = View.VISIBLE
                binding.viewLine!!.visibility = View.VISIBLE
                binding.imgDropdown!!.setImageResource(R.drawable.ic_arraow_up)
                countryOpen = true

            } else {
                binding.lnrRadioGroup!!.visibility = View.GONE
                binding.viewLine!!.visibility = View.GONE
                binding.imgDropdown!!.setImageResource(R.drawable.ic_arrow_down)
                countryOpen = false

            }
        }

        for (i in GetCategoryTypeData.indices) {
            Categoryname = GetCategoryTypeData[i].category_name
            CategoryId = GetCategoryTypeData[i].category_id
            Log.d("CategoryId", CategoryId!!)

            val rb = TextView(this)
            val selectedvalue = GetCategoryTypeData[i].category_name
            selectedCategoryID = GetCategoryTypeData[i].category_id
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
                val list = GetCategoryTypeData[i]
                val categoryname = list.category_name
                val categoryID = list.category_id
                binding.lblCategoryName!!.text = categoryname
                binding.lnrRadioGroup!!.visibility = View.GONE
                binding.viewLine!!.visibility = View.GONE
                binding.imgDropdown!!.setImageResource(R.drawable.ic_arrow_down)
                binding.LayoutTable!!.visibility = View.GONE

                CategoryWiseRequest(categoryID!!)

            }
        }
    }

    private fun CategoryWiseRequest(categoryID: String) {
        val jsonObject = JsonObject()

        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_i_course_id, CommonUtil.Courseid)
        jsonObject.addProperty(ApiRequestNames.Req_i_category_id, categoryID)
        jsonObject.addProperty(ApiRequestNames.Req_i_student_id, CommonUtil.MemberId)
        appViewModel!!.getCategoryWiseCredit(jsonObject, this)
        Log.d("CategroyWiseCreditRes:", jsonObject.toString())

    }

    fun setImgheaderBackclick() {
        onBackPressed()
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }

    override val layoutResourceId: Int
        protected get() = R.layout.activity_category_credit_wise

    private fun categeroyType() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_i_student_id, CommonUtil.MemberId)
        appViewModel!!.getCategoryType(jsonObject, this)
        Log.d("CategroyType:", jsonObject.toString())
    }
}


