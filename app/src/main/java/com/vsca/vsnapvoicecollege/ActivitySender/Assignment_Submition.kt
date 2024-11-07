package com.vsca.vsnapvoicecollege.ActivitySender


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
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


class Assignment_Submition : ActionBarActivity() {

    var Assignment_SubmittionAdapter: Assignment_SubmittionAdapter? = null
    var appViewModel: App? = null
    var Assignmentsubmit: ArrayList<AssignmentSubmit> = ArrayList()

    @JvmField
    @BindView(R.id.recycle_AssignmentSubmition)
    var recycle_AssignmentSubmition: RecyclerView? = null

    @JvmField
    @BindView(R.id.tblnotsubmitted)
    var tblnotsubmitted: TableLayout? = null

    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null


    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    @JvmField
    @BindView(R.id.imgImagePdfback)
    var imgImagePdfback: ImageView? = null

    @JvmField
    @BindView(R.id.lblNodatafound)
    var lblNodatafound: TextView? = null

    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var AdWebURl: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var PreviousAddId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)

        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this)

        imgImagePdfback!!.setOnClickListener {
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
                            .into(imgAdvertisement!!)
                        Glide.with(this)
                            .load(AdSmallImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgthumb!!)
                    }
                }
            })

        appViewModel!!.Assignmentsubmittion!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    lblNodatafound!!.visibility = View.GONE
                    if (CommonUtil.isSubmitted == "submitted") {
                        tblnotsubmitted!!.visibility = View.GONE
                    } else {
                        tblnotsubmitted!!.visibility = View.VISIBLE
                    }
                    Assignmentsubmit = response.data
                    Assignment_SubmittionAdapter =
                        Assignment_SubmittionAdapter(Assignmentsubmit, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    recycle_AssignmentSubmition!!.layoutManager = mLayoutManager
                    recycle_AssignmentSubmition!!.itemAnimator = DefaultItemAnimator()
                    recycle_AssignmentSubmition!!.adapter = Assignment_SubmittionAdapter
                    recycle_AssignmentSubmition!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    Assignment_SubmittionAdapter!!.notifyDataSetChanged()
                } else {
                    lblNodatafound!!.visibility = View.VISIBLE
                }
            } else {
                lblNodatafound!!.visibility = View.VISIBLE
            }
        }


        appViewModel!!.AssignmentsubmittionforStudent!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    lblNodatafound!!.visibility = View.GONE
                    Assignmentsubmit = response.data
                    Assignment_SubmittionAdapter =
                        Assignment_SubmittionAdapter(Assignmentsubmit, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    recycle_AssignmentSubmition!!.layoutManager = mLayoutManager
                    recycle_AssignmentSubmition!!.itemAnimator = DefaultItemAnimator()
                    recycle_AssignmentSubmition!!.adapter = Assignment_SubmittionAdapter
                    recycle_AssignmentSubmition!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    Assignment_SubmittionAdapter!!.notifyDataSetChanged()
                } else {
                    lblNodatafound!!.visibility = View.VISIBLE
                }
            } else {
                lblNodatafound!!.visibility = View.VISIBLE
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
        //get() = R.layout.previous_assignment_submited
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