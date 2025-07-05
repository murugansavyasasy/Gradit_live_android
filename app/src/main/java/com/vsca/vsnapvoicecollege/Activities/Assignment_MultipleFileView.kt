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
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityNoticeboardBinding
import com.vsca.vsnapvoicecollege.databinding.MultiplefileviewLayoutBinding

class Assignment_MultipleFileView: BaseActivity<MultiplefileviewLayoutBinding>() {

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

    override fun inflateBinding(): MultiplefileviewLayoutBinding {
        return MultiplefileviewLayoutBinding.inflate(layoutInflater)
    }


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonUtil.SetTheme(this)
        binding = MultiplefileviewLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        Assignmnetview = intent.getStringExtra("Assignment")

        binding.CommonLayout.imgImagePdfback.setOnClickListener {
            imgBackClick()
        }
        accessBottomViewIcons(
            binding,
            R.id.img_swipe,
            R.id.layoutbottomCurve, R.id.recyclermenusbottom, R.id.swipeUpMenus, R.id.LayoutDepartment, R.id.LayoutCollege, R.id.imgAddPlus
        )
       // ActionBarMethod(this)
        MenuBottomType()
        UserMenuRequest(this)
        binding.CommonLayout.LayoutAdvertisement.setOnClickListener { adclick() }

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
                            .into(binding.CommonLayout.imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this)
                            .load(AdSmallImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.CommonLayout.imgthumb!!)
                    }
                }
            })

        MultipleAssignmentfile = MultipleAssignmentfile(CommonUtil.Multipleiamge, this)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.CommonLayout.recyclerMultipleAssignment!!.layoutManager = mLayoutManager
        binding.CommonLayout.recyclerMultipleAssignment!!.itemAnimator = DefaultItemAnimator()
        binding.CommonLayout.recyclerMultipleAssignment!!.recycledViewPool.setMaxRecycledViews(
            0,
            80
        )
        binding.CommonLayout.recyclerMultipleAssignment!!.adapter = MultipleAssignmentfile
        MultipleAssignmentfile!!.notifyDataSetChanged()


        if (Assignmnetview.equals("AssignmentData")) {
            binding.CommonLayout.linerFiletype!!.visibility = View.VISIBLE
            AssignmentContentView("image")

            binding.CommonLayout.lblImage.setOnClickListener {
                AssignmentContentView("image")
                type = "image"
                binding.CommonLayout.lblPdf.setBackgroundColor(
                    Color.parseColor(
                        resources.getString(
                            R.string.txt_color_white
                        )
                    )
                )
                binding.CommonLayout.lblImage!!.setBackgroundColor(
                    Color.parseColor(
                        resources.getString(
                            R.string.clr_parent_selected
                        )
                    )
                )
                binding.CommonLayout.lblImage!!.setTextColor(Color.parseColor(resources.getString(R.string.txt_color_white)))
                binding.CommonLayout.lblPdf!!.setTextColor(Color.parseColor(resources.getString(R.string.txt_color_receiver)))

            }

            binding.CommonLayout.lblPdf!!.setOnClickListener {
                AssignmentContentView("pdf")
                type = "pdf"

                binding.CommonLayout.lblImage!!.setBackgroundColor(
                    Color.parseColor(
                        resources.getString(
                            R.string.clr_parent_selected
                        )
                    )
                )
                binding.CommonLayout.lblImage!!.setBackgroundColor(
                    Color.parseColor(
                        resources.getString(
                            R.string.txt_color_white
                        )
                    )
                )

                binding.CommonLayout.lblImage!!.setTextColor(Color.parseColor(resources.getString(R.string.txt_color_receiver)))
                binding.CommonLayout.lblPdf!!.setTextColor(Color.parseColor(resources.getString(R.string.txt_color_white)))
            }


        } else if (Assignmnetview.equals("Circuler")) {
            binding.CommonLayout.linerFiletype!!.visibility = View.GONE
            binding.CommonLayout.lblMenuTitle!!.text = "Image/PdF files"
        }

        appViewModel!!.Assignmentview!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    UserMenuRequest(this)
                    AdForCollegeApi()

                    AssignmentContent_ViewData = response.data
                    binding.CommonLayout.recyclerMultipleAssignment!!.visibility = View.VISIBLE

                    Assignment_ContentViewAdapter =
                        Assignment_ContentViewAdapter(AssignmentContent_ViewData, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.CommonLayout.recyclerMultipleAssignment!!.layoutManager = mLayoutManager
                    binding.CommonLayout.recyclerMultipleAssignment!!.itemAnimator =
                        DefaultItemAnimator()
                    binding.CommonLayout.recyclerMultipleAssignment!!.adapter =
                        Assignment_ContentViewAdapter
                    binding.CommonLayout.recyclerMultipleAssignment!!.recycledViewPool.setMaxRecycledViews(
                        0,
                        80
                    )
                    Assignment_ContentViewAdapter!!.notifyDataSetChanged()
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    binding.CommonLayout.recyclerMultipleAssignment!!.visibility = View.GONE
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                binding.CommonLayout.recyclerMultipleAssignment!!.visibility = View.GONE

            }
        }

        imgRefresh!!.setOnClickListener {
            var AddId: Int = 1
            PreviousAddId = PreviousAddId + 1
            AdForCollegeApi()

            if (Assignmnetview.equals("AssignmentData")) {
                binding.CommonLayout.linerFiletype!!.visibility = View.VISIBLE
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

    fun imgBackClick() {
        super.onBackPressed()
        CommonUtil.Multipleiamge.clear()
    }

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