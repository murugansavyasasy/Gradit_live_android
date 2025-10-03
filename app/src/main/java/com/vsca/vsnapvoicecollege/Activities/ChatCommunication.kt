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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityCategoryCreditWiseBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityChatCommunicationBinding


class ChatCommunication : BaseActivity<ActivityChatCommunicationBinding>() {

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

    override fun inflateBinding(): ActivityChatCommunicationBinding {
        return ActivityChatCommunicationBinding.inflate(layoutInflater)
    }



    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        binding = ActivityChatCommunicationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionBarMethod(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.LayoutBottomMenus) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // push content below the status bar
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }


        swipeRefreshLayout = findViewById(R.id.swipyrefreshlayout)
        ReplayType = "2"
        CommonUtil.OnMenuClicks("ChatCommunication")

        accessBottomViewIcons(
            binding,
            R.id.img_swipe,
            R.id.layoutbottomCurve, R.id.recyclermenusbottom, R.id.swipeUpMenus, R.id.LayoutDepartment, R.id.LayoutCollege, R.id.imgAddPlus
        )
        MenuBottomType()
        UserMenuRequest(this)


        binding.CommonLayout.imgheaderBack.setOnClickListener { imgheaderBack() }

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
            binding.CommonLayout.layoutSend!!.visibility = View.GONE
            binding.CommonLayout.lblsemester!!.visibility = View.VISIBLE
            binding.CommonLayout.lblsection!!.visibility = View.VISIBLE
            binding.CommonLayout.lblsubject!!.visibility = View.VISIBLE

            Yearname = CommonUtil.yearnamae
            semestername = CommonUtil.semestername
            sectionname = CommonUtil.sectionname
            coursename = CommonUtil.coursename
            subjectname = CommonUtil.subjectname
            Log.d("Year_Name", Yearname.toString())

            binding.CommonLayout.lblyear!!.text = Yearname
            binding.CommonLayout.lblcourse!!.text = coursename
            binding.CommonLayout.lblsection!!.text = sectionname
            binding.CommonLayout.lblsemester!!.text = semestername
            binding.CommonLayout.lblsubject!!.text = subjectname

        } else if (CommonUtil.Priority == "p4" || CommonUtil.Priority == "p5") {

            binding.CommonLayout.layoutSend.visibility = View.VISIBLE
            binding.CommonLayout.lblsemester!!.visibility = View.GONE
            binding.CommonLayout.lblsection!!.visibility = View.GONE
            binding.CommonLayout.lblsubject!!.visibility = View.GONE

            staffname = CommonUtil.staffname
            subjectname = CommonUtil.subjectname

            binding.CommonLayout.lblyear!!.text = staffname
            binding.CommonLayout.lblcourse!!.text = subjectname

        }



        binding.CommonLayout.lblContent!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                if (s!!.length == 0) {

                    binding.CommonLayout.imgSend!!.visibility = View.GONE

                } else {

                    binding.CommonLayout.imgSend!!.visibility = View.VISIBLE

                }
            }
        })

        binding.CommonLayout.imgSend!!.setOnClickListener {

            val view: View? = this.currentFocus

            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(view!!.windowToken, 0)

            textstudent = binding.CommonLayout.lblContent!!.text.toString()


            CommonUtil.Textedit = textstudent.toString()
            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3") {

                binding.CommonLayout.layoutSend.visibility = View.GONE

                ChatSender(ReplayType.toString())
                binding.CommonLayout.lblContent!!.setText("")
                ChatListSender()
                binding.CommonLayout.switchonAndoff!!.isChecked = false
                binding.CommonLayout.txtReplayConstrine!!!!.visibility = View.GONE
                binding.CommonLayout.txtOnandoff!!.visibility = View.GONE
                binding.CommonLayout.layoutSend.visibility = View.GONE
                ReplayType = "2"

            } else {

                binding.CommonLayout.layoutSend.visibility = View.VISIBLE
                ChatStudent()
                binding.CommonLayout.lblContent!!.setText("")
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


        binding.CommonLayout.switchonAndoff!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            ReplayType = if (isChecked) {
                "1"
            } else {
                "2"
            }
        })


        binding.CommonLayout.imgDelete!!.setOnClickListener {
            binding.CommonLayout.txtReplayConstrine!!!!.visibility = View.GONE
            binding.CommonLayout.txtOnandoff!!.visibility = View.GONE
            binding.CommonLayout.layoutSend.visibility = View.GONE
            ReplayType = "2"
            binding.CommonLayout.txtReplay!!.text = ""
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
                    binding.CommonLayout.recyclerChat!!.layoutManager = mLayoutManager
                    binding.CommonLayout.recyclerChat!!.itemAnimator = DefaultItemAnimator()
                    binding.CommonLayout.recyclerChat!!.adapter = Chat_Text_adapter
                    binding.CommonLayout.recyclerChat!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    if (Offset == 0 || isrefresh!!) {
                        binding.CommonLayout.recyclerChat!!.scrollToPosition(StudentChatList.size - 1)
                    }
                    Chat_Text_adapter!!.notifyDataSetChanged()

                } else {

                    binding.CommonLayout!!.txtSwipeLable!!.visibility = View.GONE
                    if (StudentChatList.size == 0) {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                        binding.CommonLayout!!.lblNoChats!!.visibility = View.VISIBLE
                    }
                }
            } else {
                binding.CommonLayout!!.txtSwipeLable!!.visibility = View.GONE
                if (StudentChatList.size == 0) {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    binding.CommonLayout!!.lblNoChats!!.visibility = View.VISIBLE
                }
            }
        }


        appViewModel!!.ChatSenderside!!.observe(this) { response ->
            if (response != null) {

                binding.CommonLayout!!.txtSwipeLable!!.visibility = View.VISIBLE
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
                                                    binding.CommonLayout.layoutSend.visibility =
                                                        View.VISIBLE
                                                    binding.CommonLayout.txtReplayConstrine!!!!.visibility =
                                                        View.VISIBLE
                                                    binding.CommonLayout.txtOnandoff!!.visibility =
                                                        View.VISIBLE
                                                    binding.CommonLayout.txtReplay!!.text =
                                                        item.question
                                                    binding.CommonLayout.lblname!!.text =
                                                        item.studentname
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
                                                    binding.CommonLayout.layoutSend.visibility =
                                                        View.VISIBLE
                                                    binding.CommonLayout.txtReplayConstrine!!!!.visibility =
                                                        View.VISIBLE
                                                    binding.CommonLayout.txtOnandoff!!.visibility =
                                                        View.VISIBLE
                                                    binding.CommonLayout.txtReplay!!.text =
                                                        item.question
                                                    binding.CommonLayout.lblname!!.text =
                                                        item.studentname
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
                                                    binding.CommonLayout.layoutSend.visibility =
                                                        View.VISIBLE
                                                    binding.CommonLayout.txtReplayConstrine!!!!.visibility =
                                                        View.VISIBLE
                                                    binding.CommonLayout.txtOnandoff!!.visibility =
                                                        View.VISIBLE
                                                    binding.CommonLayout.txtReplay!!.text =
                                                        item.question
                                                    binding.CommonLayout.lblname!!.text =
                                                        item.studentname
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
                                                    binding.CommonLayout.layoutSend.visibility =
                                                        View.VISIBLE
                                                    binding.CommonLayout.txtReplayConstrine!!!!.visibility =
                                                        View.VISIBLE
                                                    binding.CommonLayout.txtOnandoff!!.visibility =
                                                        View.VISIBLE
                                                    binding.CommonLayout.txtReplay!!.text =
                                                        item.question
                                                    binding.CommonLayout.lblname!!.text =
                                                        item.studentname
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
                    binding.CommonLayout.recyclerChat!!.layoutManager = mLayoutManager
                    binding.CommonLayout.recyclerChat!!.itemAnimator = DefaultItemAnimator()
                    binding.CommonLayout.recyclerChat!!.adapter = ChatSenderSide_Adapter
                    binding.CommonLayout.recyclerChat!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    if (Offset == 0 || isrefresh!!) {
                        Log.d("scrollToPosition", Senderside_Chatdata!!.size.toString())
                        binding.CommonLayout.recyclerChat!!.scrollToPosition(Senderside_Chatdata!!.size - 1)
                    }
                    ChatSenderSide_Adapter!!.notifyDataSetChanged()

                } else {
                    binding.CommonLayout!!.txtSwipeLable.visibility = View.GONE
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    binding.CommonLayout!!.lblNoChats!!.visibility = View.VISIBLE
                }
            } else {
                binding.CommonLayout!!.txtSwipeLable!!.visibility = View.GONE
                binding.CommonLayout!!.lblNoChats!!.visibility = View.VISIBLE
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