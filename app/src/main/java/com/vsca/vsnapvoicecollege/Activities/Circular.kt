package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.ActivitySender.ImageOrPdf
import com.vsca.vsnapvoicecollege.Adapters.CircularAdapter
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.GetCircularListDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityNoticeboardBinding
import java.util.Locale

class Circular : BaseActivity<ActivityNoticeboardBinding>() {

    var circularadapter: CircularAdapter? = null
    override var appViewModel: App? = null

    var CircularType = true
    var GetCircularData: List<GetCircularListDetails> = ArrayList()
    var departmentSize = 0
    var TotalSize = ""
    var DepartmentCount: String? = null
    var CollegeCount: String? = null
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var AdWebURl: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var PreviousAddId: Int = 0

    override fun inflateBinding(): ActivityNoticeboardBinding {
        return ActivityNoticeboardBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        binding = ActivityNoticeboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionBarMethod(this)

        CommonUtil.OnMenuClicks("Circular")

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true


        accessBottomViewIcons(
            binding,
            R.id.img_swipe,
            R.id.layoutbottomCurve, R.id.recyclermenusbottom, R.id.swipeUpMenus, R.id.LayoutDepartment, R.id.LayoutCollege, R.id.imgAddPlus
        )
        MenuBottomType()
        TabDepartmentColor()

        SearchList!!.visibility = View.VISIBLE

        SearchList!!.setOnClickListener {

            Search!!.visibility = View.VISIBLE

        }
        binding.CommonLayout.imgAddPlus.setOnClickListener { imgaddclick() }
        binding.CommonLayout.LayoutCollege.setOnClickListener { collegeClick() }

        binding.CommonLayout.LayoutDepartment.setOnClickListener { departmentClick() }

        binding.CommonLayout.LayoutAdvertisement.setOnClickListener { adclick() }

        if (CommonUtil.menu_readCircular.equals("1")) {
            CircularRequest(CircularType)
        }

        idSV!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false

            }

            override fun onQueryTextChange(msg: String): Boolean {

                filter(msg)
                return false
            }
        })

        txt_Cancel!!.setOnClickListener {

            Search!!.visibility = View.GONE

        }


        if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                "p2"
            ) || CommonUtil.Priority.equals(
                "p3"
            )
        ) {
            binding.CommonLayout.txtNoticeLable!!.visibility = View.VISIBLE
        } else {
            binding.CommonLayout.txtNoticeLable!!.visibility = View.GONE
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
                            .into(binding.CommonLayout.imgAdvertisement!!)
                        Glide.with(this)
                            .load(AdSmallImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.CommonLayout.imgthumb!!)
                    }
                }
            })

        appViewModel!!.OverAllMenuResponseLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    AdForCollegeApi()

                    if (response.data.isNullOrEmpty()) {
                        OverAllMenuCountData = emptyList()
                    } else {
                        OverAllMenuCountData = response.data!!
                        DepartmentCount = OverAllMenuCountData[0].departmentcircular
                        CollegeCount = OverAllMenuCountData[0].collegecircular
                        CountValueSet()
                    }
                } else {
                    OverAllMenuCountData = emptyList()
                }
            }
        }

        appViewModel!!.circularResponseLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                UserMenuRequest(this)
                if (status == 1) {
                    if (CommonUtil.menu_readCircular.equals("1")) {
                        OverAllMenuCountRequest(this, CommonUtil.MenuIDCircular!!)
                    }
                    UserMenuRequest(this)

                    if (CircularType) {
                        GetCircularData = response.data!!
                        val size = GetCircularData.size
                        if (size > 0) {
                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.GONE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.VISIBLE
                            circularadapter = CircularAdapter(GetCircularData, this)
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            binding.CommonLayout.recyclerCommon!!.layoutManager = mLayoutManager
                            binding.CommonLayout.recyclerCommon!!.itemAnimator =
                                DefaultItemAnimator()
                            binding.CommonLayout.recyclerCommon!!.adapter = circularadapter
                            binding.CommonLayout.recyclerCommon!!.recycledViewPool.setMaxRecycledViews(
                                0,
                                80
                            )
                            circularadapter!!.notifyDataSetChanged()
                        } else {
                            NoDataFound()
                        }
                    } else {
                        GetCircularData = response.data!!
                        val size = GetCircularData.size
                        if (size > 0) {
                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.GONE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.VISIBLE

                            circularadapter = CircularAdapter(GetCircularData, this@Circular)
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@Circular)
                            binding.CommonLayout.recyclerCommon!!.layoutManager = mLayoutManager
                            binding.CommonLayout.recyclerCommon!!.itemAnimator =
                                DefaultItemAnimator()
                            binding.CommonLayout.recyclerCommon!!.adapter = circularadapter
                            binding.CommonLayout.recyclerCommon!!.recycledViewPool.setMaxRecycledViews(
                                0,
                                80
                            )
                            circularadapter!!.notifyDataSetChanged()
                        } else {
                            NoDataFound()
                        }
                    }
                } else {
                    if (CircularType) {
                        NoDataFound()
                    } else {
                        NoDataFound()
                    }
                }
            } else {
                NoDataFound()
            }
        }

        imgRefresh!!.setOnClickListener(View.OnClickListener {
            if (CircularType) {
                CircularType = true

                if (CommonUtil.menu_readCircular.equals("1")) {
                    CircularRequest(CircularType)
                }
            } else {
                CircularType = false
                if (CommonUtil.menu_readCircular.equals("1")) {
                    CircularRequest(CircularType)
                }
            }
        })
        binding.CommonLayout.lbltotalsize!!.setText(R.string.txt_img_pdf)
    }


    private fun filter(text: String) {


        val filteredlist: java.util.ArrayList<GetCircularListDetails> = java.util.ArrayList()

        for (item in GetCircularData) {
            if (item.title!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {

                filteredlist.add(item)

            }
        }
        if (filteredlist.isEmpty()) {

            Toast.makeText(this, CommonUtil.No_Data_Found, Toast.LENGTH_SHORT).show()
        } else {
            circularadapter!!.filterList(filteredlist)

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

    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        super.onResume()
    }

    private fun NoDataFound() {
        binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
        binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
    }

    private fun CountValueSet() {

        if (!DepartmentCount.equals("0") && !DepartmentCount.equals("")) {
            binding.CommonLayout.lblDepartmentSize!!.visibility = View.VISIBLE
            binding.CommonLayout.lblDepartmentSize!!.text = DepartmentCount
        } else {
            binding.CommonLayout.lblDepartmentSize!!.visibility = View.GONE
            DepartmentCount = "0"
        }
        if (!CollegeCount.equals("0") && !CollegeCount.equals("")) {
            binding.CommonLayout.lblCollegeSize!!.visibility = View.VISIBLE
            binding.CommonLayout.lblCollegeSize!!.text = CollegeCount
        } else {
            binding.CommonLayout.lblCollegeSize!!.visibility = View.GONE
            CollegeCount = "0"
        }
        var intdepartment = Integer.parseInt(DepartmentCount!!)
        var intCollegecount = Integer.parseInt(CollegeCount!!)
        var TotalSizeCount = intdepartment + intCollegecount
        if (TotalSizeCount > 0) {
            binding.CommonLayout.lbltotalsize!!.visibility = View.VISIBLE
            binding.CommonLayout.lbltotalsize!!.text = TotalSizeCount.toString()
        } else {
            binding.CommonLayout.lbltotalsize!!.visibility = View.GONE
        }
    }

    override val layoutResourceId: Int
        protected get() = R.layout.activity_noticeboard

    fun CircularRequest(type: Boolean) {
        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.Appid)
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
            if (type) {
                jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.DepartmentCircular)
            } else {
                jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.CollegeCircular)
            }
            appViewModel!!.getCircularList(jsonObject, this)
            Log.d("CircularRequest:", jsonObject.toString())
        }
    }

    fun departmentClick() {
        bottomsheetStateCollpased()
        CircularType = true
        if (CommonUtil.menu_readCircular.equals("1")) {
            CircularRequest(CircularType)
        }
        TabDepartmentColor()
    }

    fun collegeClick() {
        bottomsheetStateCollpased()
        CircularType = false
        if (CommonUtil.menu_readCircular.equals("1")) {
            CircularRequest(CircularType)
        }
        TabCollegeColor()

    }

    fun imgaddclick() {

        val i: Intent = Intent(this, ImageOrPdf::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)

    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }
}