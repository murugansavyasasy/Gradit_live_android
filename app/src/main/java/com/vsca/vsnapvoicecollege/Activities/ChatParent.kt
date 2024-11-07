package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.ChatStaffAdapter
import com.vsca.vsnapvoicecollege.Adapters.Chat_AdapterStaff
import com.vsca.vsnapvoicecollege.Interfaces.ChatListener
import com.vsca.vsnapvoicecollege.Interfaces.ChatListener_Staff
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App

class ChatParent : BaseActivity() {

    var chatAdapter: ChatStaffAdapter? = null
    var Chat_AdapterStaff: Chat_AdapterStaff? = null
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
    @BindView(R.id.lblMenuTitle)
    var lblMenuTitle: TextView? = null

    @JvmField
    @BindView(R.id.lblDepartment)
    var lblDepartment: TextView? = null

    @JvmField
    @BindView(R.id.layoutTab)
    var layoutTab: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.lblNoRecordsFound)
    var lblNoRecordsFound: TextView? = null


    var GetStaffListData: List<GetStaffDetailsData> = ArrayList()
    var GetStaffchatData: List<DataX> = ArrayList()
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
        ActionBarMethod(this)
        MenuBottomType()
        layoutTab!!.visibility = View.GONE
        CommonUtil.OnMenuClicks("Chat")
        lblMenuTitle!!.text = "Chat"

        if (CommonUtil.menu_readChat.equals("1")) {

            if (CommonUtil.Priority == "p7" || CommonUtil.Priority.equals("p1")) {
                StaffClassesforChat()
            } else if (CommonUtil.Priority.equals("p2")) {
                StaffClassesforChat()
            } else if (CommonUtil.Priority.equals("p3")) {
                StaffClassesforChat()
            } else if (CommonUtil.Priority.equals("p4")) {
                Student_Side_Chat_list()
            } else if (CommonUtil.Priority.equals("p5")) {
                Student_Side_Chat_list()
            } else if (CommonUtil.Priority.equals("p6")) {
                Student_Side_Chat_list()
            }
        } else {
            lblNoRecordsFound!!.visibility = View.VISIBLE
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


        try {

            // STUDENT SIDE

            appViewModel!!.GetStaffDetailsLiveData!!.observe(this) { response ->
                if (response != null) {
                    val status = response.status
                    val message = response.message
                    UserMenuRequest(this)

                    if (status == 1) {
                        AdForCollegeApi()

                        GetStaffListData = response.data!!
                        chatAdapter =
                            ChatStaffAdapter(GetStaffListData, this, object : ChatListener {
                                override fun onChatStaffCLick(
                                    holder: ChatStaffAdapter.MyViewHolder, data: GetStaffDetailsData
                                ) {
                                    holder.LayoutStaff!!.setOnClickListener(object :
                                        View.OnClickListener {
                                        override fun onClick(view: View) {

                                            val i = Intent(
                                                this@ChatParent,
                                                ChatCommunication::class.java
                                            )

                                            i.putExtra("ChatStaff", data)

                                            CommonUtil.staffid = data.staffid.toString()
                                            CommonUtil.subjectid = data.subjectid.toString()
                                            CommonUtil.SectionId = data.sectionid.toString()
                                            CommonUtil.isclassteacher =
                                                data.isclassteacher.toString()

                                            CommonUtil.staffname = data.staffname.toString()
                                            CommonUtil.subjectname = data.subjectname.toString()

                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(i)
                                        }
                                    })
                                }
                            })

                        val mLayoutManager: RecyclerView.LayoutManager =
                            GridLayoutManager(applicationContext, 2)
                        recyclerNoticeboard!!.layoutManager = mLayoutManager
                        recyclerNoticeboard!!.isNestedScrollingEnabled = true
                        recyclerNoticeboard!!.addItemDecoration(GridSpacingItemDecoration(2, false))
                        recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                        recyclerNoticeboard!!.adapter = chatAdapter
                    } else {

                        lblNoRecordsFound!!.visibility = View.VISIBLE
                        recyclerNoticeboard!!.visibility = View.GONE

                    }
                } else {

                    UserMenuRequest(this)
                    lblNoRecordsFound!!.visibility = View.VISIBLE
                    recyclerNoticeboard!!.visibility = View.GONE

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {

            // STAFF SIDE

            appViewModel!!.GetStaffchat!!.observe(this) { response ->
                if (response != null) {
                    val status = response.Status
                    val message = response.Message
                    UserMenuRequest(this)

                    if (status == 1) {
                        GetStaffchatData = response.data
                        AdForCollegeApi()

                        Chat_AdapterStaff =
                            Chat_AdapterStaff(GetStaffchatData, this, object : ChatListener_Staff {
                                override fun onChatStaffdataCLick(
                                    holder: Chat_AdapterStaff.MyViewHolder, data: DataX
                                ) {
                                    holder.LayoutStaff!!.setOnClickListener {
                                        val intent = Intent(
                                            this@ChatParent, ChatCommunication::class.java
                                        )

                                        CommonUtil.subjectid = data.subjectid
                                        CommonUtil.SectionId = data.sectionid
                                        CommonUtil.isclassteacher = data.isclassteacher
                                        CommonUtil.yearnamae = data.yearname
                                        CommonUtil.semestername = data.semestername
                                        CommonUtil.sectionname = data.sectionname
                                        CommonUtil.coursename = data.coursename
                                        CommonUtil.subjectname = data.subjectname


                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    }
                                }
                            })

                        val mLayoutManager: RecyclerView.LayoutManager =
                            GridLayoutManager(applicationContext, 2)
                        recyclerNoticeboard!!.layoutManager = mLayoutManager
                        recyclerNoticeboard!!.isNestedScrollingEnabled = true
                        recyclerNoticeboard!!.addItemDecoration(GridSpacingItemDecoration(2, false))
                        recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                        recyclerNoticeboard!!.adapter = Chat_AdapterStaff

                    } else {

                        lblNoRecordsFound!!.visibility = View.VISIBLE
                        recyclerNoticeboard!!.visibility = View.GONE

                    }
                } else {

                    UserMenuRequest(this)
                    lblNoRecordsFound!!.visibility = View.VISIBLE
                    recyclerNoticeboard!!.visibility = View.GONE

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        imgRefresh!!.setOnClickListener(View.OnClickListener {

            if (CommonUtil.menu_readChat.equals("1")) {

                if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1")) {
                    StaffClassesforChat()
                } else if (CommonUtil.Priority.equals("p2")) {
                    StaffClassesforChat()
                } else if (CommonUtil.Priority.equals("p3")) {
                    StaffClassesforChat()
                } else if (CommonUtil.Priority.equals("p4")) {
                    Student_Side_Chat_list()
                } else if (CommonUtil.Priority.equals("p5")) {
                    Student_Side_Chat_list()
                } else if (CommonUtil.Priority.equals("p6")) {
                    Student_Side_Chat_list()
                }
            }
        })
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_noticeboard


    private fun Student_Side_Chat_list() {
        val jsonObject = JsonObject()
        run {

            jsonObject.addProperty(ApiRequestNames.Req_student_id, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
            appViewModel!!.getStaffDetailsForApp(jsonObject, this)
            Log.d("ChatStaff:", jsonObject.toString())
        }
    }

    private fun StaffClassesforChat() {
        val jsonObject = JsonObject()
        run {

            jsonObject.addProperty(ApiRequestNames.Req_staff_id, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
            appViewModel!!.StaffClassesforChat(jsonObject, this)
            Log.d("StaffClassesforChat:", jsonObject.toString())
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

    @OnClick(R.id.LayoutAdvertisement)
    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    class GridSpacingItemDecoration(private val spanCount: Int, includeEdge: Boolean) :
        RecyclerView.ItemDecoration() {
        private var spacing = 2
        private val includeEdge: Boolean
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount
            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column - 1) * spacing / spanCount
                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }

        init {
            this.includeEdge = includeEdge
        }
    }

    override fun onResume() {

        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        super.onResume()
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }
}