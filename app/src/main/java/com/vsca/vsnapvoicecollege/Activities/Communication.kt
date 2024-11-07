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
import java.util.Locale

class Communication : BaseActivity(), MenuCountResponseCallback {

    private var communicationAdapter: CommunicationAdapter? = null
    override var appViewModel: App? = null

    @JvmField
    @BindView(R.id.recyclerCommon)
    var recyclerNoticeboard: RecyclerView? = null

    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    @JvmField
    @BindView(R.id.lbltotalsize)
    var lbltotalsize: TextView? = null

    @JvmField
    @BindView(R.id.txt_NoticeLable)
    var txt_NoticeLable: TextView? = null

    @JvmField
    @BindView(R.id.lblMenuTitle)
    var lblMenuTitle: TextView? = null

    @JvmField
    @BindView(R.id.lblDepartment)
    var lblDepartment: TextView? = null

    @JvmField
    @BindView(R.id.lblCollege)
    var lblCollege: TextView? = null

    @JvmField
    @BindView(R.id.lblDepartmentSize)
    var lblDepartmentSize: TextView? = null

    @JvmField
    @BindView(R.id.lblCollegeSize)
    var lblCollegeSize: TextView? = null

    @JvmField
    @BindView(R.id.lblNoRecordsFound)
    var lblNoRecordsFound: TextView? = null

    @JvmField
    @BindView(R.id.imgRecordVoice)
    var imgRecordVoice: ImageView? = null

    var VoiceButton: String? = null

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


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionBarMethod(this)
        TabDepartmentColor()
        MenuBottomType()
        CommonUtil.OnMenuClicks("Voice")

        lblMenuTitle!!.setText(R.string.txt_communication)
        lblDepartment!!.setText(R.string.txt_unread)
        lblCollege!!.setText(R.string.txt_read)
        SearchList!!.visibility = View.VISIBLE
        SearchList!!.setOnClickListener {
            Search!!.visibility = View.VISIBLE
        }

        if (CommonUtil.menu_writeCommunication == "1") {
            if (CommonUtil.Priority == "p4" || CommonUtil.Priority == "p5" || CommonUtil.Priority == "p6") {
                imgRecordVoice!!.visibility = View.GONE
            } else {
                imgRecordVoice!!.visibility = View.VISIBLE
            }
        } else {
            imgRecordVoice!!.visibility = View.GONE
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
            txt_NoticeLable!!.visibility = View.VISIBLE
        } else {
            txt_NoticeLable!!.visibility = View.GONE
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
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgthumb!!)
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


//        appviewModelbase!!.communicationNew_Button!!.observe(this) { response ->
//
//            if (response != null) {
//                val status = response.Status
//                val message = response.Message
//
//                if (status == 1) {
//
//                    Communication_NewButtonResponse = response.data
//                    VoiceButton = Communication_NewButtonResponse[0].is_write_enabled
//                    isreadText = Communication_NewButtonResponse[0].is_read_enabled
//                    isreadVoice = Communication_NewButtonResponse[1].is_read_enabled
//
//                    if (Communication_NewButtonResponse.size == 3) {
//                        CommonUtil.menuslug = Communication_NewButtonResponse[2].menu_slug
//                    }
//                    if (CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals("p2") || CommonUtil.Priority.equals(
//                            "p3"
//                        ) || CommonUtil.Priority.equals("p7")
//                    ) {
//                        if (VoiceButton.equals("1")) {
//                            imgRecordVoice!!.visibility = View.VISIBLE
//                        } else {
//                            imgRecordVoice!!.visibility = View.GONE
//                        }
//
//                        if (isreadVoice == "1") {
//                            CommunicationRequest(CommunicationType)
//                        }
//                    } else {
//                        if (isreadVoice == "1") {
//                            CommunicationRequest(CommunicationType)
//                        }
//                    }
//                }
//            }
//        }

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
                            lblNoRecordsFound!!.visibility = View.GONE
                            recyclerNoticeboard!!.visibility = View.VISIBLE

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
                            recyclerNoticeboard!!.layoutManager = mLayoutManager
                            recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                            recyclerNoticeboard!!.adapter = communicationAdapter
                            recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            communicationAdapter!!.notifyDataSetChanged()
                        } else {
                            NoDataFound()
                        }
                    } else {
                        GetCommunicationdata = response.data!!
                        val size = GetCommunicationdata.size
                        if (size > 0) {

                            lblNoRecordsFound!!.visibility = View.GONE
                            recyclerNoticeboard!!.visibility = View.VISIBLE

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
                            recyclerNoticeboard!!.layoutManager = mLayoutManager
                            recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                            recyclerNoticeboard!!.adapter = communicationAdapter
                            recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
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
        lblNoRecordsFound!!.visibility = View.VISIBLE
        recyclerNoticeboard!!.visibility = View.GONE
    }

    private fun CountValueSet() {
        if (!unreadcount.equals("0") && !unreadcount.equals("")) {
            lblDepartmentSize!!.visibility = View.VISIBLE
            lblDepartmentSize!!.text = unreadcount
        } else {
            lblDepartmentSize!!.visibility = View.GONE
            unreadcount = "0"
        }
        if (!readcount.equals("0") && !readcount.equals("")) {
            lblCollegeSize!!.visibility = View.VISIBLE
            lblCollegeSize!!.text = readcount
        } else {
            lblCollegeSize!!.visibility = View.GONE
            readcount = "0"
        }
        var intdepartment = Integer.parseInt(unreadcount!!)
        var intCollegecount = Integer.parseInt(readcount!!)
        var TotalSizeCount = intdepartment + intCollegecount
        if (TotalSizeCount > 0) {
            lbltotalsize!!.visibility = View.VISIBLE
            lbltotalsize!!.text = TotalSizeCount.toString()
        } else {
            lbltotalsize!!.visibility = View.GONE
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

    @OnClick(R.id.LayoutDepartment)
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
            imgRecordVoice!!.visibility = View.GONE
        } else {
            if (CommonUtil.menu_writeCommunication.equals("1")) {
                imgRecordVoice!!.visibility = View.VISIBLE
            } else {
                imgRecordVoice!!.visibility = View.GONE
            }
        }
        if (CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals("p2") || CommonUtil.Priority.equals(
                "p3"
            ) || CommonUtil.Priority.equals("p7")
        ) {
            imgAddPlus!!.visibility = View.GONE
        }
    }

    @OnClick(R.id.LayoutCollege)
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
            imgAddPlus!!.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
        CommonUtil.mediaPlayer!!.stop()
    }

    @OnClick(R.id.imgRecordVoice)
    fun RecordVoice() {

        CommonUtil.mediaPlayer!!.stop()
        val i: Intent = Intent(this, CommunicationVoice::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.putExtra("screentype", false)
        startActivity(i)
    }

    @OnClick(R.id.imgAddPlus)
    fun plusClick() {

        CommonUtil.mediaPlayer!!.stop()
        val i: Intent = Intent(this, AddTextNoticeboard::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.putExtra("screentype", false)
        startActivity(i)
    }

    @OnClick(R.id.LayoutAdvertisement)
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
        imgAddPlus!!.visibility = View.GONE
    }
}