package com.vsca.vsnapvoicecollege.Activities

import android.annotation.SuppressLint
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
import com.vsca.vsnapvoicecollege.ActivitySender.AddTextNoticeboard
import com.vsca.vsnapvoicecollege.ActivitySender.CommunicationVoice
import com.vsca.vsnapvoicecollege.Adapters.CommunicationAdapter
import com.vsca.vsnapvoicecollege.Interfaces.MenuCountResponseCallback
import com.vsca.vsnapvoicecollege.Interfaces.communicationListener
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityNoticeboardBinding
import java.util.Locale

class Communication : BaseActivity<ActivityNoticeboardBinding>(), MenuCountResponseCallback {


    private var communicationAdapter: CommunicationAdapter? = null
    override var appViewModel: App? = null

    var TextButton: String? = null
    var CommunicationType = true
    var GetCommunicationdata: List<GetCommunicationDetails> = ArrayList()
    var readcount: String? = null
    var unreadcount: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var AdWebURl: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var Communication_NewButtonResponse: List<Communication_NewButtonResponse> = ArrayList()
    private var isreadText = ""
    private var isreadVoice = ""

    override fun inflateBinding(): ActivityNoticeboardBinding {
        return ActivityNoticeboardBinding.inflate(layoutInflater)
    }


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)

        binding = ActivityNoticeboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionBarMethod(this)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true


        CommonUtil.OnMenuClicks("Voice")
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
        TabDepartmentColor()
        UserMenuRequest(this)

        binding.CommonLayout.lblMenuTitle!!.setText(R.string.txt_communication)
        binding.CommonLayout.lblDepartment!!.setText(R.string.txt_unread)
        binding.CommonLayout.lblCollege!!.setText(R.string.txt_read)
        SearchList!!.visibility = View.VISIBLE
        SearchList!!.setOnClickListener {
            Search!!.visibility = View.VISIBLE
        }
        binding.CommonLayout.imgAddPlus.setOnClickListener { plusClick() }

        binding.CommonLayout.LayoutAdvertisement.setOnClickListener { adclick() }
        binding.CommonLayout.imgRecordVoice.setOnClickListener { RecordVoice() }
        binding.CommonLayout.LayoutCollege.setOnClickListener { collegeClick() }
        binding.CommonLayout.LayoutDepartment.setOnClickListener { departmentClick() }

        if (CommonUtil.menu_writeCommunication == "1") {
            if (CommonUtil.Priority == "p4" || CommonUtil.Priority == "p5" || CommonUtil.Priority == "p6") {
                binding.CommonLayout.imgRecordVoice!!.visibility = View.GONE
            } else {
                binding.CommonLayout.imgRecordVoice!!.visibility = View.VISIBLE
            }
        } else {
            binding.CommonLayout.imgRecordVoice!!.visibility = View.GONE
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

        if (CommonUtil.Priority == "p7" || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority.equals(
                "p3"
            )
        ) {
            binding.CommonLayout.txtNoticeLable!!.visibility = View.VISIBLE
        } else {
            binding.CommonLayout.txtNoticeLable!!.visibility = View.GONE
        }
        appViewModel!!.AdvertisementLiveData?.observe(this,
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

                        Glide.with(this).load(AdBackgroundImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.CommonLayout.imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.CommonLayout.imgthumb!!)
                    }
                }
            })

        appviewModelbase!!.OverAllMenuResponseLiveData!!.observe(this) { response ->

            if (response != null) {
                val status = response.status
                val message = response.message
                AdForCollegeApi()
                if (status == 1) {
                    if (response.data.isNullOrEmpty()) {
                        OverAllMenuCountData = emptyList()
                    } else {
                        OverAllMenuCountData = response.data!!
                        unreadcount = OverAllMenuCountData[0].unread
                        readcount = OverAllMenuCountData[0].read
                        CountValueSet()
                    }
                } else {
                    OverAllMenuCountData = emptyList()
                }
            }
        }

        appViewModel!!.communicationLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                UserMenuRequest(this)
                if (CommonUtil.menu_readCommunication.equals("1")) {
                    OverAllMenuCountRequest(this, CommonUtil.MenuIDCommunication!!)
                }
                if (status == 1) {

                    if (CommunicationType) {
                        GetCommunicationdata = response.data!!
                        val size = GetCommunicationdata.size
                        if (size > 0) {
                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.GONE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.VISIBLE

                            communicationAdapter = CommunicationAdapter(
                                GetCommunicationdata,
                                this, "Voice",
                                object : communicationListener {
                                    override fun oncommunicationClick(
                                        holder: CommunicationAdapter.MyViewHolder,
                                        item: GetCommunicationDetails
                                    ) {
                                        holder.rytRecentNotification.setOnClickListener {
                                            AppReadStatus(
                                                this@Communication,
                                                "sms",
                                                item.msgdetailsid!!
                                            )
                                        }
                                    }
                                })

                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            binding.CommonLayout.recyclerCommon!!.layoutManager = mLayoutManager
                            binding.CommonLayout.recyclerCommon!!.itemAnimator =
                                DefaultItemAnimator()
                            binding.CommonLayout.recyclerCommon!!.adapter = communicationAdapter
                            binding.CommonLayout.recyclerCommon!!.recycledViewPool.setMaxRecycledViews(
                                0,
                                80
                            )
                            communicationAdapter!!.notifyDataSetChanged()
                        } else {
                            NoDataFound()
                        }
                    } else {
                        GetCommunicationdata = response.data!!
                        val size = GetCommunicationdata.size
                        if (size > 0) {

                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.GONE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.VISIBLE

                            communicationAdapter = CommunicationAdapter(
                                GetCommunicationdata,
                                this, "Voice",
                                object : communicationListener {
                                    override fun oncommunicationClick(
                                        holder: CommunicationAdapter.MyViewHolder,
                                        item: GetCommunicationDetails
                                    ) {
                                        holder.rytRecentNotification.setOnClickListener {
                                            AppReadStatus(
                                                this@Communication, "sms", item.msgdetailsid!!
                                            )
                                        }
                                    }
                                })
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            binding.CommonLayout.recyclerCommon!!.layoutManager = mLayoutManager
                            binding.CommonLayout.recyclerCommon!!.itemAnimator =
                                DefaultItemAnimator()
                            binding.CommonLayout.recyclerCommon!!.adapter = communicationAdapter
                            binding.CommonLayout.recyclerCommon!!.recycledViewPool.setMaxRecycledViews(
                                0,
                                80
                            )
                            communicationAdapter!!.notifyDataSetChanged()
                        } else {
                            NoDataFound()
                        }
                    }
                } else {
                    UserMenuRequest(this)
                    NoDataFound()

                }

            } else {
                UserMenuRequest(this)
                NoDataFound()
            }
        }


        imgRefresh!!.setOnClickListener(View.OnClickListener {
            if (CommunicationType) {
                CommunicationType = true
                if (CommonUtil.menu_readCommunication == "1") {
                    CommunicationRequest(CommunicationType)
                }
            } else {
                CommunicationType = false
                if (CommonUtil.menu_readCommunication == "1") {
                    CommunicationRequest(CommunicationType)
                }
            }
        })
    }

    private fun filter(text: String) {

        val filteredlist: java.util.ArrayList<GetCommunicationDetails> = java.util.ArrayList()

        for (item in GetCommunicationdata) {
            if (item.description!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {

                filteredlist.add(item)


            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, CommonUtil.No_Data_Found, Toast.LENGTH_SHORT).show()
        } else {
            communicationAdapter!!.filterList(filteredlist)

        }
    }

    fun NoDataFound() {
        binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
        binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
    }

    private fun CountValueSet() {
        if (!unreadcount.equals("0") && !unreadcount.equals("")) {
            binding.CommonLayout.lblDepartmentSize!!.visibility = View.VISIBLE
            binding.CommonLayout.lblDepartmentSize!!.text = unreadcount
        } else {
            binding.CommonLayout.lblDepartmentSize!!.visibility = View.GONE
            unreadcount = "0"
        }
        if (!readcount.equals("0") && !readcount.equals("")) {
            binding.CommonLayout.lblCollegeSize!!.visibility = View.VISIBLE
            binding.CommonLayout.lblCollegeSize!!.text = readcount
        } else {
            binding.CommonLayout.lblCollegeSize!!.visibility = View.GONE
            readcount = "0"
        }
        var intdepartment = Integer.parseInt(unreadcount!!)
        var intCollegecount = Integer.parseInt(readcount!!)
        var TotalSizeCount = intdepartment + intCollegecount
        if (TotalSizeCount > 0) {
            binding.CommonLayout.lbltotalsize!!.visibility = View.VISIBLE
            binding.CommonLayout.lbltotalsize!!.text = TotalSizeCount.toString()
        } else {
            binding.CommonLayout.lbltotalsize!!.visibility = View.GONE
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

    override val layoutResourceId: Int
        get() = R.layout.activity_noticeboard

    fun CommunicationRequest(readtype: Boolean) {
        val jsonObject = JsonObject()
        run {

            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)

            if (readtype) {
                Log.d("unread", readtype.toString())
                jsonObject.addProperty(ApiRequestNames.Req_readtype, CommonUtil.Unread)
            } else {
                jsonObject.addProperty(ApiRequestNames.Req_readtype, CommonUtil.Read)
                Log.d("read", readtype.toString())
            }

            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3") {
                jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId)
            } else {
                jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId)
            }

            appViewModel!!.getCommunicationListbyType(jsonObject, this)
            Log.d("CommunicationRequest:", jsonObject.toString())
        }
    }

    fun Add_Button_VisibleOrNot() {
        val jsonObject = JsonObject()
        run {

            jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
            jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)

            appViewModel!!.CommunicationNew_Button(jsonObject, this)
            Log.d("CommunicationRequest:", jsonObject.toString())
        }
    }

    fun departmentClick() {
        bottomsheetStateCollpased()
        TabDepartmentColor()
        CommunicationType = true
        if (CommonUtil.menu_readCommunication == "1") {
            CommunicationRequest(CommunicationType)
        }
        if (CommonUtil.Priority.equals("p6") || CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals(
                "p5"
            )
        ) {
            binding.CommonLayout.imgRecordVoice!!.visibility = View.GONE
        } else {
            if (CommonUtil.menu_writeCommunication.equals("1")) {
                binding.CommonLayout.imgRecordVoice!!.visibility = View.VISIBLE
            } else {
                binding.CommonLayout.imgRecordVoice!!.visibility = View.GONE
            }
        }
        if (CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals("p2") || CommonUtil.Priority.equals(
                "p3"
            ) || CommonUtil.Priority.equals("p7")
        ) {
            binding.CommonLayout.imgAddPlus!!.visibility = View.GONE
        }
    }

    fun collegeClick() {
        //   imgAddPlus!!.visibility = View.GONE
        bottomsheetStateCollpased()
        CommunicationType = false
        if (CommonUtil.menu_readCommunication == "1") {
            CommunicationRequest(CommunicationType)
        }
        TabCollegeColor()
        if (CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals("p2") || CommonUtil.Priority.equals(
                "p3"
            ) || CommonUtil.Priority.equals("p7")
        ) {
            binding.CommonLayout.imgAddPlus!!.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
        CommonUtil.mediaPlayer!!.stop()
    }

    fun RecordVoice() {

        CommonUtil.mediaPlayer!!.stop()
        val i: Intent = Intent(this, CommunicationVoice::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.putExtra("screentype", false)
        startActivity(i)
    }

    fun plusClick() {

        CommonUtil.mediaPlayer!!.stop()
        val i: Intent = Intent(this, AddTextNoticeboard::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.putExtra("screentype", false)
        startActivity(i)
    }

    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    override fun menucountcallback(responseBody: MenuDetailsResponse) {
        UserMenuRequest(this)
    }

    override fun onResume() {

        if (CommonUtil.menu_readCommunication == "1") {
            CommunicationRequest(CommunicationType)
        }
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        super.onResume()
        binding.CommonLayout.imgAddPlus!!.visibility = View.GONE
    }
}