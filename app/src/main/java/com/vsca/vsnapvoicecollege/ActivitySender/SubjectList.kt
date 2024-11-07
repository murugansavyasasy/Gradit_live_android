package com.vsca.vsnapvoicecollege.ActivitySender

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Adapters.SubjectListAdapter
import com.vsca.vsnapvoicecollege.Model.ExamSubjectList
import com.vsca.vsnapvoicecollege.Model.ExamSubjectSubList
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.SubjectdetailX
import com.vsca.vsnapvoicecollege.Model.examlist
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App

class SubjectList : BaseActivity() {

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

    var SubjectListAdapter: SubjectListAdapter? = null
    override var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var examviewlist: List<examlist> = ArrayList()
    var examname: String? = null
    var Subjectcount: String? = null

    var SubjectdetailX: List<SubjectdetailX> = ArrayList()
    var ExamSubjectList: List<ExamSubjectList> = ArrayList()
    var ExamSubjectSubList: List<ExamSubjectSubList> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)

        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionBarMethod(this)
        MenuBottomType()


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

        appViewModel!!.ExamviewSujectList!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                UserMenuRequest(this)
                AdForCollegeApi()

                if (status == 1) {
                    ExamSubjectSubList = response.data

                    val SubjectCount = ExamSubjectSubList.size

                    if (ExamSubjectSubList.size > 0) {
                        Subjectcount = SubjectCount.toString()
                        lblDepartmentSize!!.text = Subjectcount
                        lblDepartmentSize!!.visibility = View.VISIBLE
                    } else {
                        lblDepartmentSize!!.visibility = View.GONE

                    }

                    SubjectListAdapter = SubjectListAdapter(ExamSubjectSubList, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    create_exam_recycle!!.layoutManager = mLayoutManager
                    create_exam_recycle!!.itemAnimator = DefaultItemAnimator()
                    create_exam_recycle!!.adapter = SubjectListAdapter
                    create_exam_recycle!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    SubjectListAdapter!!.notifyDataSetChanged()
                }
            }
        }

        imgRefresh!!.setOnClickListener(View.OnClickListener {
            Examviewdata()
        })
    }

    private fun Examviewdata() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_Examid, CommonUtil.headerid)
        jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.SectionId)
        appViewModel!!.ExamviewSubjectList(jsonObject, this)
        Log.d("GetExamviewSubjectList", jsonObject.toString())
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
        get() = R.layout.subject_listview

    @OnClick(R.id.LayoutAdvertisement)
    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        super.onResume()
        Examviewdata()
    }

    @OnClick(R.id.imgback)
    fun imgback() {
        onBackPressed()
    }
}