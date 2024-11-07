package com.vsca.vsnapvoicecollege.Activities

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import com.vsca.vsnapvoicecollege.Adapters.ChatSenderSide_Adapter
import com.vsca.vsnapvoicecollege.Adapters.Chat_Text_adapter
import com.vsca.vsnapvoicecollege.Interfaces.ChatClickListener
import com.vsca.vsnapvoicecollege.Model.ChatList
import com.vsca.vsnapvoicecollege.Model.Senderside_Chatdata
import com.vsca.vsnapvoicecollege.Model.chat_offset_List
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App


class ChatCommunication : BaseActivity() {


    @JvmField
    @BindView(R.id.txt_replayConstrine)
    var txt_replayConstrine: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.layoutSend)
    var layoutSend: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.txt_replay)
    var txt_replay: TextView? = null

    @JvmField
    @BindView(R.id.lblNoChats)
    var lblNoChats: TextView? = null

    @JvmField
    @BindView(R.id.recyclerChat)
    var recyclerChat: RecyclerView? = null

    @JvmField
    @BindView(R.id.img_delete)
    var img_delete: ImageView? = null

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
    @BindView(R.id.lblname)
    var lblname: TextView? = null

    @JvmField
    @BindView(R.id.txt_swipe_Lable)
    var txt_swipe_Lable: TextView? = null

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
    @BindView(R.id.txt_onandoff)
    var txt_onandoff: ConstraintLayout? = null


    @JvmField
    @BindView(R.id.lblContent)
    var lblContent: EditText? = null

    @JvmField
    @BindView(R.id.imgSend)
    var imgSend: ImageView? = null

    @JvmField
    @BindView(R.id.switchonAndoff)
    var switchonAndoff: Switch? = null

    @JvmField
    @BindView(R.id.lbl_switchLable)
    var lbl_switchLable: TextView? = null

    @JvmField
    @BindView(R.id.lblNoRecordsFound)
    var lblNoRecordsFound: TextView? = null

    @JvmField
    @BindView(R.id.lblyear)
    var lblyear: TextView? = null

    @JvmField
    @BindView(R.id.lblsemester)
    var lblsemester: TextView? = null

    @JvmField
    @BindView(R.id.lblsection)
    var lblsection: TextView? = null

    @JvmField
    @BindView(R.id.lblcourse)
    var lblcourse: TextView? = null

    @JvmField
    @BindView(R.id.lblsubject)
    var lblsubject: TextView? = null

    var textstudent: String? = null
    var Yearname: String? = null
    var semestername: String? = null
    var sectionname: String? = null
    var coursename: String? = null
    var subjectname: String? = null
    var staffname: String? = null
    var ReplayType: String? = null
    var Offset: Int? = 0
    val handler: Handler = Handler()
    lateinit var swipeRefreshLayout: SwipyRefreshLayout
    var Chat_Text_adapter: Chat_Text_adapter? = null
    var ChatSenderSide_Adapter: ChatSenderSide_Adapter? = null
    override var appViewModel: App? = null
    var chat_offset_List: ArrayList<chat_offset_List>? = null
    var tempSenderside_Chatdata: ArrayList<Senderside_Chatdata>? = ArrayList()
    var Senderside_Chatdata: ArrayList<Senderside_Chatdata>? = ArrayList()
    private var StudentChatList: ArrayList<ChatList> = ArrayList()
    private var tempStudentChatList: ArrayList<ChatList> = ArrayList()
    var isrefresh: Boolean? = false
    var totalChatCount: String? = ""


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)

        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionBarMethod(this)
        MenuBottomType()
        UserMenuRequest(this)
        swipeRefreshLayout = findViewById(R.id.swipyrefreshlayout)
        ReplayType = "2"
        CommonUtil.OnMenuClicks("ChatCommunication")

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.direction = SwipyRefreshLayoutDirection.TOP
            Log.d("totalChatCount", totalChatCount!!)
            Offset = Offset!! + 1
            Log.d("Offset", Offset.toString())
            isrefresh = true
            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {

                if (!totalChatCount!!.equals(Senderside_Chatdata)) {
                    ChatListSender()
                }
            } else {
                if (!totalChatCount!!.equals(StudentChatList)) {
                    ChatListStudent()
                }
            }
        }



        if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3") {
            layoutSend!!.visibility = View.GONE
            lblsemester!!.visibility = View.VISIBLE
            lblsection!!.visibility = View.VISIBLE
            lblsubject!!.visibility = View.VISIBLE

            Yearname = CommonUtil.yearnamae
            semestername = CommonUtil.semestername
            sectionname = CommonUtil.sectionname
            coursename = CommonUtil.coursename
            subjectname = CommonUtil.subjectname
            Log.d("Year_Name", Yearname.toString())

            lblyear!!.text = Yearname
            lblcourse!!.text = coursename
            lblsection!!.text = sectionname
            lblsemester!!.text = semestername
            lblsubject!!.text = subjectname

        } else if (CommonUtil.Priority == "p4" || CommonUtil.Priority == "p5") {

            layoutSend!!.visibility = View.VISIBLE
            lblsemester!!.visibility = View.GONE
            lblsection!!.visibility = View.GONE
            lblsubject!!.visibility = View.GONE

            staffname = CommonUtil.staffname
            subjectname = CommonUtil.subjectname

            lblyear!!.text = staffname
            lblcourse!!.text = subjectname

        }

        try {
            layoutTab!!.visibility = View.GONE
            lblMenuTitle!!.setText(R.string.txt_Video)

        } catch (e: Exception) {
            e.printStackTrace()
        }



        lblContent!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                if (s!!.length == 0) {

                    imgSend!!.visibility = View.GONE

                } else {

                    imgSend!!.visibility = View.VISIBLE

                }
            }
        })

        imgSend!!.setOnClickListener {

            val view: View? = this.currentFocus

            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(view!!.windowToken, 0)

            textstudent = lblContent!!.text.toString()


            CommonUtil.Textedit = textstudent.toString()
            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3") {

                layoutSend!!.visibility = View.GONE

                ChatSender(ReplayType.toString())
                lblContent!!.setText("")
                ChatListSender()
                switchonAndoff!!.isChecked = false
                txt_replayConstrine!!.visibility = View.GONE
                txt_onandoff!!.visibility = View.GONE
                layoutSend!!.visibility = View.GONE
                ReplayType = "2"

            } else {

                layoutSend!!.visibility = View.VISIBLE
                ChatStudent()
                lblContent!!.setText("")
                ChatListStudent()
            }
        }

        imgRefresh!!.setOnClickListener(View.OnClickListener {
            isrefresh = false
            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                ChatListSender()
            } else {
                ChatListStudent()
            }
        })


        switchonAndoff!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            ReplayType = if (isChecked) {
                "1"
            } else {
                "2"
            }
        })


        img_delete!!.setOnClickListener {
            txt_replayConstrine!!.visibility = View.GONE
            txt_onandoff!!.visibility = View.GONE
            layoutSend!!.visibility = View.GONE
            ReplayType = "2"
            txt_replay!!.text = ""
        }

        appViewModel!!.Chatstudentlist!!.observe(this) { response ->


            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                } else {
                    CommonUtil.ApiAlert(this, message)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }


        appViewModel!!.ChatList!!.observe(this) { response ->

            if (response != null) {
                swipeRefreshLayout.isRefreshing = false

                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    chat_offset_List = response.data

                    Log.d(
                        "total_response", Gson().toJsonTree(chat_offset_List).asJsonArray.toString()
                    )

                    tempStudentChatList.clear()

                    for (i in chat_offset_List!!.indices) {
                        tempStudentChatList = chat_offset_List!![i].List
                        totalChatCount = chat_offset_List!![i].count
                    }
                    tempStudentChatList.reverse()

                    if (isrefresh!!) {
                        StudentChatList.addAll(StudentChatList.size - 1, tempStudentChatList)
                    } else {
                        StudentChatList!!.clear()
                        StudentChatList.addAll(0, tempStudentChatList)
                    }
                    Log.d("chat_list", Gson().toJsonTree(StudentChatList).asJsonArray.toString())

                    Chat_Text_adapter = Chat_Text_adapter(StudentChatList, this)
                    val mLayoutManager = LinearLayoutManager(this)
                    recyclerChat!!.layoutManager = mLayoutManager
                    recyclerChat!!.itemAnimator = DefaultItemAnimator()
                    recyclerChat!!.adapter = Chat_Text_adapter
                    recyclerChat!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    if (Offset == 0 || isrefresh!!) {
                        recyclerChat!!.scrollToPosition(StudentChatList.size - 1)
                    }
                    Chat_Text_adapter!!.notifyDataSetChanged()

                } else {

                    txt_swipe_Lable!!.visibility = View.GONE
                    if (StudentChatList.size == 0) {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                        lblNoChats!!.visibility = View.VISIBLE
                    }
                }
            } else {
                txt_swipe_Lable!!.visibility = View.GONE
                if (StudentChatList.size == 0) {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    lblNoChats!!.visibility = View.VISIBLE
                }
            }
        }


        appViewModel!!.ChatSenderside!!.observe(this) { response ->
            if (response != null) {

                txt_swipe_Lable!!.visibility = View.VISIBLE
                swipeRefreshLayout.isRefreshing = false

                val status = response.result
                if (status == "1") {
                    if (tempSenderside_Chatdata!!.isNotEmpty()) {
                        tempSenderside_Chatdata!!.clear()
                    }
                    tempSenderside_Chatdata = response.data
                    totalChatCount = response.count

                    tempSenderside_Chatdata?.let { it.reverse() }

                    if (isrefresh!!) {
                        Senderside_Chatdata!!.addAll(
                            Senderside_Chatdata!!.size - 1, tempSenderside_Chatdata!!
                        )
                    } else {
                        Senderside_Chatdata!!.clear()
                        Senderside_Chatdata!!.addAll(0, tempSenderside_Chatdata!!)
                    }


                    Log.d(
                        "sender_chat_list",
                        Gson().toJsonTree(tempSenderside_Chatdata).asJsonArray.toString()
                    )
                    ChatSenderSide_Adapter =
                        ChatSenderSide_Adapter(Senderside_Chatdata!!, this, object :
                            ChatClickListener {
                            override fun onchatClick(
                                holder: ChatSenderSide_Adapter.MyViewHolder,
                                item: Senderside_Chatdata
                            ) {

                                holder.img_dotthree!!.setOnClickListener(View.OnClickListener {

                                    CommonUtil.studentid = item.studentid
                                    CommonUtil.Questionid = item.questionid
                                    CommonUtil.StudentBlackORunblack = item.is_student_blocked

                                    if (CommonUtil.StudentBlackORunblack == "0") {

                                        if (item.changeanswer == "0") {

                                            CommonUtil.changeanswer = item.changeanswer
                                            val popupMenu = PopupMenu(
                                                this@ChatCommunication, holder.img_dotthree
                                            )
                                            popupMenu.menuInflater.inflate(
                                                R.menu.chat_replaymenu, popupMenu.menu
                                            )
                                            popupMenu.setOnMenuItemClickListener { menuItem ->
                                                var type: String? = null
                                                type = menuItem.toString()
                                                Log.d("MenuItem", type.toString())


                                                if (type.equals("Block Student")) {


                                                    val dlg =
                                                        AlertDialog.Builder(this@ChatCommunication)
                                                    dlg.setTitle("Trying to Block student" + " " + item.studentname)
                                                    dlg.setMessage("Press OK to confirm")
                                                    dlg.setPositiveButton(CommonUtil.OK) { dialog, which ->
                                                        BlackStudent()
                                                        ChatListSender()

                                                    }

                                                    dlg.setNegativeButton(CommonUtil.CANCEL) { dialog, whick -> }
                                                    dlg.setCancelable(false)
                                                    dlg.create()
                                                    dlg.show()

                                                } else {
                                                    layoutSend!!.visibility = View.VISIBLE
                                                    txt_replayConstrine!!.visibility = View.VISIBLE
                                                    txt_onandoff!!.visibility = View.VISIBLE
                                                    txt_replay!!.text = item.question
                                                    lblname!!.text = item.studentname
                                                }
                                                true
                                            }
                                            popupMenu.show()

                                        } else {

                                            CommonUtil.changeanswer = item.changeanswer
                                            val popupMenu = PopupMenu(
                                                this@ChatCommunication, holder.img_dotthree
                                            )
                                            popupMenu.menuInflater.inflate(
                                                R.menu.chat_changereplay, popupMenu.menu
                                            )
                                            popupMenu.setOnMenuItemClickListener { menuItem ->
                                                var type: String? = null
                                                type = menuItem.toString()

                                                if (type.equals("Block Student")) {

                                                    val dlg =
                                                        AlertDialog.Builder(this@ChatCommunication)
                                                    dlg.setTitle("Trying to Block student" + " " + item.studentname)
                                                    dlg.setMessage("Press OK to confirm")
                                                    dlg.setPositiveButton(CommonUtil.OK) { dialog, which ->

                                                        BlackStudent()
                                                        ChatListSender()

                                                    }

                                                    dlg.setNegativeButton(CommonUtil.CANCEL) { dialog, whick -> }
                                                    dlg.setCancelable(false)
                                                    dlg.create()
                                                    dlg.show()

                                                } else {
                                                    layoutSend!!.visibility = View.VISIBLE
                                                    txt_replayConstrine!!.visibility = View.VISIBLE
                                                    txt_onandoff!!.visibility = View.VISIBLE
                                                    txt_replay!!.text = item.question
                                                    lblname!!.text = item.studentname
                                                }
                                                true
                                            }
                                            popupMenu.show()
                                        }

                                    } else {

                                        if (item.changeanswer.equals("0")) {

                                            CommonUtil.changeanswer = item.changeanswer

                                            val popupMenu = PopupMenu(
                                                this@ChatCommunication, holder.img_dotthree
                                            )
                                            popupMenu.menuInflater.inflate(
                                                R.menu.menublackstudent, popupMenu.menu
                                            )
                                            popupMenu.setOnMenuItemClickListener { menuItem ->

                                                var type: String? = null
                                                type = menuItem.toString()


                                                if (type.equals("UnBlock Student")) {

                                                    val dlg =
                                                        AlertDialog.Builder(this@ChatCommunication)
                                                    dlg.setTitle("Trying to UnBlock student" + " " + item.studentname)
                                                    dlg.setMessage("Press OK to confirm")
                                                    dlg.setPositiveButton(CommonUtil.OK) { dialog, which ->

                                                        UnBlackStudent()
                                                        ChatListSender()

                                                    }

                                                    dlg.setNegativeButton(CommonUtil.CANCEL) { dialog, whick -> }
                                                    dlg.setCancelable(false)
                                                    dlg.create()
                                                    dlg.show()

                                                } else {
                                                    layoutSend!!.visibility = View.VISIBLE
                                                    txt_replayConstrine!!.visibility = View.VISIBLE
                                                    txt_onandoff!!.visibility = View.VISIBLE
                                                    txt_replay!!.text = item.question
                                                    lblname!!.text = item.studentname
                                                }

                                                true
                                            }
                                            popupMenu.show()

                                        } else {

                                            CommonUtil.changeanswer = item.changeanswer
                                            val popupMenu = PopupMenu(
                                                this@ChatCommunication, holder.img_dotthree
                                            )
                                            popupMenu.menuInflater.inflate(
                                                R.menu.menublackstudent, popupMenu.menu
                                            )
                                            popupMenu.setOnMenuItemClickListener { menuItem ->

                                                var type: String? = null
                                                type = menuItem.toString()

                                                if (type.equals("UnBlock Student")) {

                                                    val dlg =
                                                        AlertDialog.Builder(this@ChatCommunication)
                                                    dlg.setTitle("Trying to UnBlock student" + " " + item.studentname)
                                                    dlg.setMessage("Press OK to confirm")
                                                    dlg.setPositiveButton(CommonUtil.OK) { dialog, which ->

                                                        UnBlackStudent()
                                                        ChatListSender()

                                                    }

                                                    dlg.setNegativeButton(CommonUtil.CANCEL) { dialog, whick -> }
                                                    dlg.setCancelable(false)
                                                    dlg.create()
                                                    dlg.show()

                                                } else {
                                                    layoutSend!!.visibility = View.VISIBLE
                                                    txt_replayConstrine!!.visibility = View.VISIBLE
                                                    txt_onandoff!!.visibility = View.VISIBLE
                                                    txt_replay!!.text = item.question
                                                    lblname!!.text = item.studentname
                                                }

                                                true
                                            }
                                            popupMenu.show()
                                        }
                                    }
                                })
                            }
                        })
                    val mLayoutManager = LinearLayoutManager(this)
                    recyclerChat!!.layoutManager = mLayoutManager
                    recyclerChat!!.itemAnimator = DefaultItemAnimator()
                    recyclerChat!!.adapter = ChatSenderSide_Adapter
                    recyclerChat!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    if (Offset == 0 || isrefresh!!) {
                        Log.d("scrollToPosition", Senderside_Chatdata!!.size.toString())
                        recyclerChat!!.scrollToPosition(Senderside_Chatdata!!.size - 1)
                    }
                    ChatSenderSide_Adapter!!.notifyDataSetChanged()

                } else {
                    txt_swipe_Lable!!.visibility = View.GONE
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    lblNoChats!!.visibility = View.VISIBLE
                }
            } else {
                txt_swipe_Lable!!.visibility = View.GONE
                lblNoChats!!.visibility = View.VISIBLE
            }
        }

        appViewModel!!.BlackStudent!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    CommonUtil.ApiAlert(this, message)
                    ChatListSender()
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        appViewModel!!.unblackStudent!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    CommonUtil.ApiAlert(this, message)
                    ChatListSender()
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5")) {
            ChatListStudent()
        } else {
            ChatListSender()
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_chat_communication

    @OnClick(R.id.imgheaderBack)
    fun imgheaderBack() {
        onBackPressed()
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }

    val Refresh: Runnable = object : Runnable {
        override fun run() {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun ChatListStudent() {

        val jsonObject = JsonObject()

        if (isrefresh!!) {
            jsonObject.addProperty(ApiRequestNames.Req_offset, Offset)
        } else {
            Offset = 0
            jsonObject.addProperty(ApiRequestNames.Req_offset, Offset)
        }
        jsonObject.addProperty(ApiRequestNames.Req_student_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_staff_id, CommonUtil.staffid)
        jsonObject.addProperty(ApiRequestNames.Req_limit, "10")
        jsonObject.addProperty(ApiRequestNames.Req_section_id, CommonUtil.SectionId)
        jsonObject.addProperty(ApiRequestNames.Req_subject_id, CommonUtil.subjectid)
        jsonObject.addProperty(ApiRequestNames.Req_is_classteacher, CommonUtil.isclassteacher)

        appViewModel!!.chatList(jsonObject, this)
        Log.d("ChatList_Student", jsonObject.toString())
    }

    private fun ChatListSender() {

        val jsonObject = JsonObject()
        if (isrefresh!!) {
            jsonObject.addProperty(ApiRequestNames.Req_offset, Offset)
        } else {
            Offset = 0
            jsonObject.addProperty(ApiRequestNames.Req_offset, 0)
        }
        jsonObject.addProperty(ApiRequestNames.Req_staff_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_limit, "10")
        jsonObject.addProperty(ApiRequestNames.Req_section_id, CommonUtil.SectionId)
        jsonObject.addProperty(ApiRequestNames.Req_subject_id, CommonUtil.subjectid)
        jsonObject.addProperty(ApiRequestNames.Req_is_classteacher, CommonUtil.isclassteacher)

        appViewModel!!.ChatSenderSide(jsonObject, this)
        Log.d("ChatSenderSide", jsonObject.toString())
    }

    private fun ChatStudent() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_question, CommonUtil.Textedit)
        jsonObject.addProperty(ApiRequestNames.Req_student_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_staff_id, CommonUtil.staffid)
        jsonObject.addProperty(ApiRequestNames.Req_section_id, CommonUtil.SectionId)
        jsonObject.addProperty(ApiRequestNames.Req_subject_id, CommonUtil.subjectid)
        jsonObject.addProperty(ApiRequestNames.Req_is_classteacher, CommonUtil.isclassteacher)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)

        appViewModel!!.ChatStudent(jsonObject, this)
        Log.d("ChatStudent", jsonObject.toString())
    }


    private fun ChatSender(replaytype: String) {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_answer, CommonUtil.Textedit)
        jsonObject.addProperty(ApiRequestNames.Req_staff_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_question_id, CommonUtil.Questionid)
        jsonObject.addProperty(ApiRequestNames.Req_is_changeanswer, CommonUtil.changeanswer)
        jsonObject.addProperty(ApiRequestNames.Req_reply_type, replaytype)

        appViewModel!!.ChatStaff(jsonObject, this)
        Log.d("ChatStaff", jsonObject.toString())
    }

    private fun BlackStudent() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_student_id, CommonUtil.studentid)
        jsonObject.addProperty(ApiRequestNames.Req_staff_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)

        appViewModel!!.StudentBlack(jsonObject, this)
        Log.d("StudentBlack", jsonObject.toString())
    }

    private fun UnBlackStudent() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_student_id, CommonUtil.studentid)
        jsonObject.addProperty(ApiRequestNames.Req_staff_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)

        appViewModel!!.StudentUnBlack(jsonObject, this)
        Log.d("UnStudentBlack", jsonObject.toString())
    }
}