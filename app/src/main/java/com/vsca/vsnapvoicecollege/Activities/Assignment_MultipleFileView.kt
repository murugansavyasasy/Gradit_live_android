package com.vsca.vsnapvoicecollege.Activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.vsca.vsnapvoicecollege.Adapters.Assignment_ContentViewAdapter
import com.vsca.vsnapvoicecollege.Adapters.MultipleAssignmentfile
import com.vsca.vsnapvoicecollege.Model.AssignmentContent_ViewData
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App

class Assignment_MultipleFileView : BaseActivity() {

    override var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var AssignmentContent_ViewData: List<AssignmentContent_ViewData> = ArrayList()
    var Assignmnetview: String? = null
    var type = "image"
    var MultipleAssignmentfile: MultipleAssignmentfile? = null
    var Assignment_ContentViewAdapter: Assignment_ContentViewAdapter? = null
    var filename: List<String>? = null

    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.recyclerMultipleAssignment)
    var recyclerMultipleAssignment: RecyclerView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    @JvmField
    @BindView(R.id.lblMenuTitle)
    var lblMenuTitle: TextView? = null

    @JvmField
    @BindView(R.id.lbl_image)
    var lbl_image: TextView? = null

    @JvmField
    @BindView(R.id.lbl_pdf)
    var lbl_pdf: TextView? = null


    @JvmField
    @BindView(R.id.liner_filetype)
    var liner_filetype: LinearLayout? = null


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)

        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionBarMethod(this)
        MenuBottomType()

        Assignmnetview = intent.getStringExtra("Assignment")



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

        MultipleAssignmentfile = MultipleAssignmentfile(CommonUtil.Multipleiamge, this)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerMultipleAssignment!!.layoutManager = mLayoutManager
        recyclerMultipleAssignment!!.itemAnimator = DefaultItemAnimator()
        recyclerMultipleAssignment!!.recycledViewPool.setMaxRecycledViews(0, 80)
        recyclerMultipleAssignment!!.adapter = MultipleAssignmentfile
        MultipleAssignmentfile!!.notifyDataSetChanged()


        if (Assignmnetview.equals("AssignmentData")) {
            liner_filetype!!.visibility = View.VISIBLE
            AssignmentContentView("image")

            lbl_image!!.setOnClickListener {
                AssignmentContentView("image")
                type = "image"
                lbl_pdf!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.txt_color_white)))
                lbl_image!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_parent_selected)))
                lbl_image!!.setTextColor(Color.parseColor(resources.getString(R.string.txt_color_white)))
                lbl_pdf!!.setTextColor(Color.parseColor(resources.getString(R.string.txt_color_receiver)))

            }

            lbl_pdf!!.setOnClickListener {
                AssignmentContentView("pdf")
                type = "pdf"

                lbl_pdf!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_parent_selected)))
                lbl_image!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.txt_color_white)))

                lbl_image!!.setTextColor(Color.parseColor(resources.getString(R.string.txt_color_receiver)))
                lbl_pdf!!.setTextColor(Color.parseColor(resources.getString(R.string.txt_color_white)))
            }


        } else if (Assignmnetview.equals("Circuler")) {
            liner_filetype!!.visibility = View.GONE
            lblMenuTitle!!.text = "Image/PdF files"
        }

        appViewModel!!.Assignmentview!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    UserMenuRequest(this)
                    AdForCollegeApi()

                    AssignmentContent_ViewData = response.data
                    recyclerMultipleAssignment!!.visibility = View.VISIBLE

                    Assignment_ContentViewAdapter =
                        Assignment_ContentViewAdapter(AssignmentContent_ViewData, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    recyclerMultipleAssignment!!.layoutManager = mLayoutManager
                    recyclerMultipleAssignment!!.itemAnimator = DefaultItemAnimator()
                    recyclerMultipleAssignment!!.adapter = Assignment_ContentViewAdapter
                    recyclerMultipleAssignment!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    Assignment_ContentViewAdapter!!.notifyDataSetChanged()
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    recyclerMultipleAssignment!!.visibility = View.GONE
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                recyclerMultipleAssignment!!.visibility = View.GONE

            }
        }

        imgRefresh!!.setOnClickListener {
            var AddId: Int = 1
            PreviousAddId = PreviousAddId + 1
            AdForCollegeApi()

            if (Assignmnetview.equals("AssignmentData")) {
                liner_filetype!!.visibility = View.VISIBLE
                type.let { it1 -> AssignmentContentView(it1) }

            }
        }
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

    private fun AssignmentContentView(filetype: String) {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_assignmentid, CommonUtil.Assignmentid)
        jsonObject.addProperty(ApiRequestNames.Req_processby, CommonUtil.studentid)
        jsonObject.addProperty(ApiRequestNames.Req_filetype, filetype)
        appviewModelbase!!.AssignmentContent(jsonObject, this)
        Log.d("AssignmentContent:", jsonObject.toString())

    }

    override val layoutResourceId: Int
        get() = R.layout.multiplefileview_layout

    @OnClick(R.id.imgImagePdfback)
    fun imgBackClick() {
        super.onBackPressed()
        CommonUtil.Multipleiamge.clear()
    }

    @OnClick(R.id.LayoutAdvertisement)
    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        CommonUtil.Multipleiamge.clear()
    }
}