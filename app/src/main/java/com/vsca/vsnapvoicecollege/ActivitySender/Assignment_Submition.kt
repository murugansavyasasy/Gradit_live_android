package com.vsca.vsnapvoicecollege.ActivitySender


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Adapters.Assignment_SubmittionAdapter
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityAssignmentSubmitionBinding


class Assignment_Submition : ActionBarActivity() {

    var Assignment_SubmittionAdapter: Assignment_SubmittionAdapter? = null
    var appViewModel: App? = null
    var Assignmentsubmit: ArrayList<AssignmentSubmit> = ArrayList()



    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var AdWebURl: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var PreviousAddId: Int = 0
    private lateinit var binding: ActivityAssignmentSubmitionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)

        super.onCreate(savedInstanceState)
        binding = ActivityAssignmentSubmitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
         ActionbarWithoutBottom(this)

        binding.imgImagePdfback!!.setOnClickListener {
            super.onBackPressed()
        }

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
                            .into(binding.imgAdvertisement!!)
                        Glide.with(this)
                            .load(AdSmallImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imgthumb!!)
                    }
                }
            })

        appViewModel!!.Assignmentsubmittion!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    binding.lblNodatafound!!.visibility = View.GONE
                    if (CommonUtil.isSubmitted == "submitted") {
                        binding.tblnotsubmitted!!.visibility = View.GONE
                    } else {
                        binding.tblnotsubmitted!!.visibility = View.VISIBLE
                    }
                    Assignmentsubmit = response.data
                    Assignment_SubmittionAdapter =
                        Assignment_SubmittionAdapter(Assignmentsubmit, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.recycleAssignmentSubmition!!.layoutManager = mLayoutManager
                    binding.recycleAssignmentSubmition!!.itemAnimator = DefaultItemAnimator()
                    binding.recycleAssignmentSubmition!!.adapter = Assignment_SubmittionAdapter
                    binding.recycleAssignmentSubmition!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    Assignment_SubmittionAdapter!!.notifyDataSetChanged()
                } else {
                    binding.lblNodatafound!!.visibility = View.VISIBLE
                }
            } else {
                binding.lblNodatafound!!.visibility = View.VISIBLE
            }
        }


        appViewModel!!.AssignmentsubmittionforStudent!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    binding.lblNodatafound!!.visibility = View.GONE
                    Assignmentsubmit = response.data
                    Assignment_SubmittionAdapter =
                        Assignment_SubmittionAdapter(Assignmentsubmit, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.recycleAssignmentSubmition!!.layoutManager = mLayoutManager
                    binding.recycleAssignmentSubmition!!.itemAnimator = DefaultItemAnimator()
                    binding.recycleAssignmentSubmition!!.adapter = Assignment_SubmittionAdapter
                    binding.recycleAssignmentSubmition!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    Assignment_SubmittionAdapter!!.notifyDataSetChanged()
                } else {
                    binding.lblNodatafound!!.visibility = View.VISIBLE
                }
            } else {
                binding.lblNodatafound!!.visibility = View.VISIBLE
            }
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

    private fun Assignmentsubmited() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_processby, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_assignmentid, CommonUtil.Assignmentid)
        jsonObject.addProperty("submissiontype", CommonUtil.isSubmitted)
        appviewModelbase!!.Assignmentsubmitedsender(jsonObject, this)
        Log.d("jsonObject:", jsonObject.toString())

    }

    private fun AssignmentsubmitedForStudent() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_processby, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_assignmentid, CommonUtil.Assignmentid)
        appviewModelbase!!.AssignmentsubmitedforStudent(jsonObject, this)
        Log.d("jsonObject:", jsonObject.toString())

    }

    override val layoutResourceId: Int
         get() = R.layout.activity_assignment_submition

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId += 1
        AdForCollegeApi()
        if (CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals("p2") || CommonUtil.Priority.equals(
                "p3"
            )
        ) {
            Assignmentsubmited()
        } else {
            AssignmentsubmitedForStudent()
        }
        super.onResume()
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }
}