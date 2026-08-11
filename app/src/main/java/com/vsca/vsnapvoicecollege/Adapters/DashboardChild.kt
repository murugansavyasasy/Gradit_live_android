package com.vsca.vsnapvoicecollege.Adapters

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.*
import com.vsca.vsnapvoicecollege.Model.DashboardSubItems
import com.vsca.vsnapvoicecollege.Model.Delete_noticeboard
import com.vsca.vsnapvoicecollege.Model.MenuDetailsResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.DownloadVoice
import com.vsca.vsnapvoicecollege.albumImage.PDF_Reader
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class DashboardChild(
    newsModalArrayList: ArrayList<DashboardSubItems> = arrayListOf(),
    private val context: Context,
    val type: String,
    menuList: List<MenuDetailsResponse> = emptyList(),
    private val onMenuClick: ((MenuDetailsResponse) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_DASHBOARD = 0
        const val TYPE_MENU = 1
        var PlayPath: String? = null

        private val TIME_FORMATTER: DateTimeFormatter? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("h:mm a")
        } else null
        private val WHITESPACE_REGEX = "\\s+".toRegex()

        fun milliSecondsToTimer(milliseconds: Long): String {
            val hours = (milliseconds / (1000 * 60 * 60)).toInt()
            val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
            val seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000).toInt()

            val minutesString = if (minutes < 10) "0$minutes" else "$minutes"
            val secondsString = if (seconds < 10) "0$seconds" else "$seconds"

            return if (hours > 0) {
                "$hours:$minutesString:$secondsString"
            } else {
                "$minutesString:$secondsString"
            }
        }
    }

    // Mutable internal copies so we can update without creating new adapters
    private val newsModalArrayListInternal: ArrayList<DashboardSubItems> = ArrayList(newsModalArrayList)
    private val menuListInternal: ArrayList<MenuDetailsResponse> = ArrayList(menuList)

    fun updateList(newList: ArrayList<DashboardSubItems>) {
        newsModalArrayListInternal.clear()
        newsModalArrayListInternal.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateMenuList(newList: List<MenuDetailsResponse>) {
        menuListInternal.clear()
        menuListInternal.addAll(newList)
        notifyDataSetChanged()
    }

    private var mExpandedPosition = -1
    var msgcontent: String? = null
    var path: String? = null

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var mediaFileLengthInMilliseconds = 0
    private var iMediaDuration = 0

    private val voiceFolder = "Gradit/Voice/"
    private lateinit var pdfUri: Uri

    private val dateFormatter by lazy { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()) }

    override fun getItemViewType(position: Int): Int {
        return if (type == "Menu") TYPE_MENU else TYPE_DASHBOARD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MENU -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.home_menu_list_design, parent, false)
                MenuViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.dashboard_list_design, parent, false)
                ViewHolder(view)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MenuViewHolder -> bindMenu(holder, position)
            is ViewHolder -> bindDashboard(holder, position)
        }
    }

    // region Menu Binding
    private fun bindMenu(holder: MenuViewHolder, position: Int) {
        val data = menuListInternal[position]

        holder.LayoutHome.setOnClickListener {
            onMenuClick?.invoke(data)
        }

        holder.imgMenu.visibility = View.VISIBLE
        holder.lblMenuName.visibility = View.VISIBLE
        holder.MenuHeader.visibility = View.VISIBLE

        val (iconRes, label) = when (data.id) {
            11 -> R.drawable.discussion_board_icon_new to data.name
            17 -> R.drawable.text_message_icon_new to data.name
            16 -> R.drawable.voice_message_icon_new to data.name
            3 -> R.drawable.exam_icon_new to data.name
            4 -> R.drawable.attendance_icon_new to data.name
            5 -> R.drawable.assignment_icon_new to data.name
            6 -> R.drawable.circular_icon_new to context.getString(R.string.txt_img_pdf)
            7 -> R.drawable.noticeboard_icon_new  to data.name
            8 -> R.drawable.events_icon_new to data.name
            9 -> R.drawable.faculty_icon_new to data.name
            10 -> R.drawable.video_icon_new to data.name
            12 -> R.drawable.course_detail_icon_new to data.name
            13 -> R.drawable.category_credit_point_icon_new to data.name
            14 -> R.drawable.semester_credit_points_icon_new to data.name
            15 -> R.drawable.exam_application_details_icon_new to data.name
            19 -> R.drawable.hall_ticket_icon_new to data.name
            20 -> R.drawable.fee_details_icon_new to data.name
            21 -> R.drawable.biometric_attendance_icon_new to data.name
            22 -> R.drawable.attendance_report_icon_new to data.name
            23 -> R.drawable.resume_builder_icon_new to data.name
            24 -> R.drawable.placement_events_icon_new to data.name
            25 -> R.drawable.placement_traning_icon_new to data.name
            else -> null to null
        }

        if (iconRes != null && label != null) {
            holder.imgMenu.setImageResource(iconRes)
            holder.lblMenuName.text = label

            // Apply priority-based colors
            applyMenuColors(holder)

        } else {
            holder.MenuHeader.visibility = View.GONE
            holder.imgMenu.visibility = View.GONE
            holder.lblMenuName.visibility = View.GONE
        }
    }
    // endregion

    // region Dashboard Binding
    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindDashboard(holder: ViewHolder, position: Int) {
        val modal = newsModalArrayListInternal[position]

        holder.resetViews()

        if (type == "Emergency Notification") {
            val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels
            holder.itemView.layoutParams.width = (screenWidth * 0.75f).toInt()
        } else {
            holder.itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }

        when (type) {
            "Circular" -> bindCircular(holder, modal)
            "Upcoming Events" -> bindUpcomingEvents(holder, modal)
            "Chat" -> bindChat(holder, modal)
            "Leave Request" -> bindLeaveRequest(holder, modal)
            "Assignments" -> bindAssignments(holder, modal)
            "Notice Board" -> bindNoticeBoard(holder, modal)
            "Ad" -> bindAd(holder, modal)
            "Attendance" -> bindAttendance(holder, modal)
            "Emergency Notification" -> bindEmergencyNotification(holder, modal, position)
            "Recent Notifications" -> bindRecentNotifications(holder, modal, position)
        }
    }

    private fun bindCircular(holder: ViewHolder, modal: DashboardSubItems) {
        holder.LayoutCicular.visibility = View.VISIBLE
        holder.lnrCircularAttachment.visibility = View.VISIBLE

        holder.LayoutCicular.setBackgroundResource(
            if (holder.bindingAdapterPosition % 2 == 0) R.drawable.bg_dashboard_circular_grey
            else R.drawable.bg_dashboard_cicular
        )

        holder.lblCircularTitle.text = modal.menuTitle
        holder.lblCirculardescription.text = modal.menuDescription
        holder.lblCircularCreateTime.text = modal.createTime
        holder.lblCicularCreateDate.text = modal.createDate
        holder.lblPath.text = context.getString(R.string.txt_attachment)

        CommonUtil.MenuIDCircular = BaseActivity.CircularMenuID

        holder.LayoutCicular.setOnClickListener {
            setMenuPermissions(6) { r, w ->
                CommonUtil.menu_readCircular = r
                CommonUtil.menu_writeCircular = w
            }
            launch(Circular::class.java)
        }

        holder.lnrCircularAttachment.setOnClickListener {
            CommonUtil.Multipleiamge.clear()
            when (modal.FilepathList.size) {
                0 -> CommonUtil.ApiAlertContext(context, "file is empty")
                1 -> {
                    val url = modal.FilepathList[0]
                    if (url.contains("pdf", ignoreCase = true)) {
                        pdfUri = Uri.parse(url)
                        val intent = Intent(context, PDF_Reader::class.java).apply {
                            putExtra("PdfView", pdfUri.toString())
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        context.startActivity(intent)
                    } else {
                        val intent = Intent(context, ViewFiles::class.java).apply {
                            putExtra("images", url)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        context.startActivity(intent)
                    }
                }
                else -> {
                    CommonUtil.Multipleiamge.addAll(modal.FilepathList)
                    launch(Assignment_MultipleFileView::class.java)
                }
            }
        }
    }
    private fun applyMenuColors(holder: MenuViewHolder) {

        val colorRes = when (CommonUtil.Priority) {
            "p1" -> R.color.clr_principal
            "p2", "p3", "p6" -> R.color.clr_teachingstaff
            "p4" -> R.color.clr_receiver
            "p5" -> R.color.clr_parent
            "p7" -> R.color.cle_lightorang
            else -> R.color.black
        }

        val color = ContextCompat.getColor(context, colorRes)

        // Icon color
        holder.imgMenu.setColorFilter(color, PorterDuff.Mode.SRC_IN)

        // Text color
        holder.lblMenuName.setTextColor(color)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindUpcomingEvents(holder: ViewHolder, modal: DashboardSubItems) {
        holder.UpcomingEvent.visibility = View.VISIBLE
        holder.UpcomingEvent.setBackgroundResource(
            if (holder.bindingAdapterPosition % 2 == 0) R.drawable.bg_dashboard_circular_grey
            else R.drawable.bg_dashboard_cicular
        )

        holder.lblEventtopic.text = modal.Eventtime
        holder.lbleventDate.text = modal.EventTitle

        val time = modal.Eventdate
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && TIME_FORMATTER != null) {
            LocalTime.parse(time).format(TIME_FORMATTER)
        } else time
        holder.lblCreateTimeevent.text = result

        CommonUtil.MenuIDEvents = BaseActivity.EventsMenuID
        holder.UpcomingEvent.setOnClickListener {
            setMenuPermissions(8) { r, w ->
                CommonUtil.menu_readEvent = r
                CommonUtil.menu_writeEvent = w
            }
            launch(Events::class.java)
        }
    }

    private fun bindChat(holder: ViewHolder, modal: DashboardSubItems) {
        if (modal.message != null) {
            holder.lnrNoticeboardd.visibility = View.GONE
            return
        }

        holder.lnrNoticeboardd.visibility = View.VISIBLE
        holder.lblNoticeboardTitle.text = modal.studentname
        holder.lblNoticeboardDate.text = modal.question

        val parts = modal.createdonchat.toString().split(WHITESPACE_REGEX)
        if (parts.size >= 3) {
            holder.lblchatDate.text = parts[0]
            holder.lblCreateTimechat.text = "${parts[1]} ${parts[2]}"
        }

        CommonUtil.MenuIDChat = BaseActivity.ChatMenuID
        holder.imgarrowchat.setOnClickListener {
            setMenuPermissions(11) { r, w ->
                CommonUtil.menu_readChat = r
                CommonUtil.menu_writeChat = w
            }
            launch(ChatParent::class.java)
        }
    }

    private fun bindLeaveRequest(holder: ViewHolder, modal: DashboardSubItems) {
        if (modal.message != null) {
            holder.lnrNoticeboardd.visibility = View.GONE
            return
        }

        holder.Leave_Request_dashboard.visibility = View.VISIBLE
        holder.lnrNoticeboard.setBackgroundResource(
            if (holder.bindingAdapterPosition % 2 == 0) R.drawable.bg_dashboard_circular_grey
            else R.drawable.bg_dashboard_cicular
        )

        holder.lblLeaveCreatedDate.text = modal.appliedon
        holder.lblleaveStatus.text = modal.leavestatus
        holder.lblLeaveType.text = modal.membernameLeaveRequest
        holder.lblLeaveNoOfDays.text = modal.noofdays
        holder.department.text = modal.departmentnameLeaveRequest
        holder.departmentname.text = modal.coursenameLeaveRequest
        holder.year.text = modal.yearnameLeaveRequest
        holder.section.text = modal.sectionnameLeaveRequest
        holder.lblFromDate.text = modal.fromdate
        holder.lblToDate.text = modal.todate
        holder.lblLeaveReason.text = modal.reason

        CommonUtil.MenuIdAttendance = BaseActivity.AttendanceMeuID

        holder.Leave_Request_dashboard.setOnClickListener {
            holder.rytLeaveDescription.visibility = View.VISIBLE
        }

        holder.lblApproval.setOnClickListener {
            showLeaveDialog("Approve Leave", modal.leaveapplicationid.toString(), "1")
        }

        holder.lblRejaect.setOnClickListener {
            showLeaveDialog("Reject Leave", modal.leaveapplicationid.toString(), "0")
        }
    }

    private fun bindAssignments(holder: ViewHolder, modal: DashboardSubItems) {
        holder.Assignment.visibility = View.VISIBLE
        holder.Assignment.setBackgroundResource(
            if (holder.bindingAdapterPosition % 2 == 0) R.drawable.bg_dashboard_circular_grey
            else R.drawable.bg_dashboard_cicular
        )

        val hasFiles = modal.FilepathListAssignment.isNotEmpty() &&
                modal.FilepathListAssignment[0].isNullOrEmpty().not()

        when (modal.assignmentfiletype) {
            "text" -> holder.lnrAssignmentAttachment.visibility = View.GONE
            else -> holder.lnrAssignmentAttachment.visibility =
                if (hasFiles) View.VISIBLE else View.GONE
        }

        holder.lnrAssignmentAttachment.setOnClickListener {
            when (modal.FilepathListAssignment.size) {
                0 -> { /* no-op */ }
                1 -> openSingleAssignmentFile(modal, modal.FilepathListAssignment[0])
                else -> {
                    CommonUtil.Multipleiamge.addAll(modal.FilepathListAssignment)
                    launch(Assignment_MultipleFileView::class.java)
                }
            }
        }

        holder.lblassignmenttopic.text = modal.assignmenttopic
        holder.lblassignmentdescription.text = modal.assignmentdescription
        holder.lblassignmentDate.text = dateFormatter.format(Date())
        holder.lbldate.text = modal.submissiondate

        CommonUtil.MenuIDAssignment = BaseActivity.AssignmentMenuID
        holder.Assignment.setOnClickListener {
            setMenuPermissions(5) { r, w ->
                CommonUtil.menu_readAssignment = r
                CommonUtil.menu_writeAssignment = w
            }
            launch(Assignment::class.java)
        }
    }

    private fun bindNoticeBoard(holder: ViewHolder, modal: DashboardSubItems) {
        holder.lnrImageView.visibility = View.VISIBLE
        holder.lblNoiceboardTitle.text = modal.menuTitle
        holder.lblNoticeDescription.text = modal.menuDescription
        holder.lblCreateTime.text = modal.createTime
        holder.lblNotiCreateDate.text = modal.createDate
        holder.rytNoticeboard.setBackgroundResource(
            if (holder.bindingAdapterPosition % 2 == 0) R.drawable.noticeboard_blue
            else R.drawable.noticeboard_yellow
        )

        CommonUtil.MenuIDNoticeboard = BaseActivity.NoticeboardMenuID
        holder.lnrImageView.setOnClickListener {
            setMenuPermissions(7) { r, w ->
                CommonUtil.menu_readNoticeBoard = r
                CommonUtil.menu_writeNoticeBoard = w
            }
            launch(Noticeboard::class.java)
        }
    }

    private fun bindAd(holder: ViewHolder, modal: DashboardSubItems) {
        holder.LayoutAd.visibility = View.VISIBLE
        Glide.with(holder.itemView).load(modal.adBaackgroundImage).into(holder.imgAdvertisement)
        Glide.with(holder.itemView).load(modal.addImage).into(holder.imgthumb)

        holder.LayoutAd.setOnClickListener {
            BaseActivity.LoadWebViewContext(context, modal.Addurl)
        }
    }

    private fun bindAttendance(holder: ViewHolder, modal: DashboardSubItems) {
        if (modal.message != null) {
            holder.lnrattendance.visibility = View.GONE
            return
        }

        holder.lnrattendance.visibility = View.VISIBLE
        holder.lblattendancestatusDate.text = modal.AttendanceDate
        holder.lblsubjectnameAttendance.text = modal.SubjectName
        holder.lblattendancestatus.text = modal.AttendanceType

        holder.lblattendancestatus.setBackgroundResource(
            if (modal.AttendanceType.equals("Absent", ignoreCase = true))
                R.drawable.bg_redcolor else R.drawable.bg_available_selected_green
        )

        CommonUtil.MenuIdAttendance = BaseActivity.AttendanceMeuID
        holder.lnrImageView.setOnClickListener {
            setMenuPermissions(4) { r, w ->
                CommonUtil.menu_readAttendance = r
                CommonUtil.menu_writeAttendance = w
            }
            launch(Attendance::class.java)
        }
    }

    private fun bindEmergencyNotification(holder: ViewHolder, modal: DashboardSubItems, position: Int) {
        holder.lnrEmgVoice.visibility = View.VISIBLE
        holder.lnrEmergencyVoice.visibility = View.VISIBLE
        holder.rytSeekbarlayout.visibility = View.VISIBLE

        val minutes = (modal.duration % 3600) / 60
        val seconds = modal.duration % 60
        holder.lbltotalduration.text = String.format("%02d:%02d", minutes, seconds)
        holder.lblVoicetitle.text = modal.menuTitle
        holder.lblPostedBy.text = modal.membername

        val splitDate = modal.createdon?.split(WHITESPACE_REGEX)?.toTypedArray()
        if (splitDate != null && splitDate.size >= 2) {
            holder.lblVoiceDate.text = splitDate[0]
            holder.lblVoiceTime.text = splitDate[1]
        }

        CommonUtil.DownloadingFileDashboard = 0
        val filename = "${modal.MsgId}_Gradit.mp3"
        val basePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path
        } else {
            Environment.getExternalStorageDirectory().path
        } ?: return

        val file = File(File(basePath, voiceFolder), filename)

        // Tag the view so background thread result matches current item
        holder.itemView.tag = modal.MsgId
        Thread {
            val exists = file.exists()
            (context as? android.app.Activity)?.runOnUiThread {
                if (holder.itemView.tag == modal.MsgId) {
                    if (exists) setupExistingEmergencyFile(holder, modal, file)
                    else setupEmergencyDownload(holder, modal, filename)
                }
            }
        }.start()
    }

    private fun bindRecentNotifications(holder: ViewHolder, modal: DashboardSubItems, position: Int) {
        holder.lnrRecentNotifications.visibility = View.VISIBLE

        val isExpanded = position == mExpandedPosition
        holder.lnrRecentVoice.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.lnrRecentNotifications.isActivated = isExpanded
        holder.lblRecenttitle.text = modal.Content

        when (modal.RecentType) {
            "Voice" -> {
                holder.lnrplayvoice.visibility = View.VISIBLE
                holder.imgRecentType.setImageResource(R.drawable.dashboard_recent_voice)
                holder.imgRecentType.alpha = 0.7f
            }
            "Emergencyvoice Message" -> {
                holder.lnrplayvoice.visibility = View.VISIBLE
                holder.imgRecentType.setImageResource(R.drawable.emergency_voice)
                holder.imgRecentType.alpha = 0.7f
            }
            else -> {
                holder.lnrplayvoice.visibility = View.GONE
                holder.imgRecentType.setImageResource(R.drawable.dashboard_text)
                holder.imgRecentType.alpha = 0.7f
                holder.lblRecentDesciption.text = modal.Content
            }
        }

        val minutes = (modal.duration % 3600) / 60
        val seconds = modal.duration % 60
        holder.lblRecentTotalDuration.text = String.format("%02d:%02d", minutes, seconds)
        holder.lblRecentPostedby.text = modal.membername
        holder.lblRecentDate.text = modal.createdon
        holder.lblRecentTime.text = modal.createTime

        if (isExpanded) {
            holder.imgArrowdown.setImageResource(R.drawable.ic_arrow_up_blue)
            holder.lnrplayvoice.visibility = View.GONE
            mediaPlayer?.seekTo(0)
        } else {
            if (modal.RecentType == "Voice" || modal.RecentType == "Emergencyvoice Message") {
                holder.lnrplayvoice.visibility = View.VISIBLE
            }
            holder.imgArrowdown.setImageResource(R.drawable.ic_arrow_down_blue)
        }

        holder.rytRecentNotification.setOnClickListener {
            holder.lnrplayvoice.visibility = View.GONE
            CommonUtil.DownloadingFileDashboard = 0

            if (modal.RecentType == "Text") {
                holder.lblRecentDesciption.visibility = View.VISIBLE
                holder.lblRecenttitle.visibility = View.VISIBLE
                holder.lblRecentDesciption.text = modal.menuDescription
                holder.lblRecenttitle.text = modal.Content
                holder.recentSeekbarlayout.visibility = View.GONE

                val oldExpanded = mExpandedPosition
                mExpandedPosition = if (isExpanded) -1 else position
                if (oldExpanded != -1) notifyItemChanged(oldExpanded)
                notifyItemChanged(position)
            } else {
                val oldExpanded = mExpandedPosition
                mExpandedPosition = if (isExpanded) -1 else position
                if (oldExpanded != -1) notifyItemChanged(oldExpanded)
                notifyItemChanged(position)

                msgcontent = modal.Content
                path = modal.Content

                val filename = "${modal.MsgId}_Gradit.mp3"
                val basePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path
                } else {
                    Environment.getExternalStorageDirectory().path
                } ?: return@setOnClickListener

                val file = File(File(basePath, voiceFolder), filename)

                holder.itemView.tag = modal.MsgId
                Thread {
                    val exists = file.exists()
                    (context as? android.app.Activity)?.runOnUiThread {
                        if (holder.itemView.tag == modal.MsgId) {
                            if (exists) setupExistingRecentFile(holder, modal, file)
                            else setupRecentDownload(holder, modal, filename)
                        }
                    }
                }.start()
            }
        }
    }
    // endregion

    // region Voice Helpers
    private fun setupExistingEmergencyFile(holder: ViewHolder, modal: DashboardSubItems, file: File) {
        holder.rytSeekbarlayout.visibility = View.VISIBLE
        CommonUtil.CommunicationisExpandAdapter = false

        initMediaPlayer(file.absolutePath)
        setupEmergencySeekBar(holder)
        setupEmergencyPlayButton(holder, file.absolutePath)

        mediaPlayer?.setOnCompletionListener {
            holder.imgEmgplaypause.setImageResource(R.drawable.ic_play)
            mediaPlayer?.seekTo(0)
        }
    }

    private fun setupEmergencyDownload(holder: ViewHolder, modal: DashboardSubItems, filename: String) {
        holder.imgEmgplaypause.setOnClickListener {
            if (CommonUtil.DownloadingFileDashboard != 1 && CommonUtil.DownloadingFileDashboard != 2) {
                DownloadVoice.downloadSampleFile(
                    context, modal.voiceFilepath!!, voiceFolder, filename, holder, true
                )
            } else {
                if (CommonUtil.DownloadingFileDashboard != 2) {
                    val basePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path
                    } else {
                        Environment.getExternalStorageDirectory().path
                    } ?: return@setOnClickListener
                    val file = File(File(basePath, voiceFolder), filename)
                    initMediaPlayer(file.absolutePath)
                }
                CommonUtil.DownloadingFileDashboard = 2
                toggleEmergencyPlay(holder)
            }
        }
    }

    private fun setupExistingRecentFile(holder: ViewHolder, modal: DashboardSubItems, file: File) {
        holder.recentSeekbarlayout.visibility = View.VISIBLE
        CommonUtil.CommunicationisExpandAdapter = false

        initMediaPlayer(file.absolutePath)
        setupRecentSeekBar(holder)
        setupRecentPlayButton(holder, file.absolutePath)

        mediaPlayer?.setOnCompletionListener {
            holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
            mediaPlayer?.seekTo(0)
        }
    }

    private fun setupRecentDownload(holder: ViewHolder, modal: DashboardSubItems, filename: String) {
        holder.recentSeekbarlayout.visibility = View.VISIBLE
        holder.imgRecentEmgplaypause.setOnClickListener {
            if (CommonUtil.DownloadingFileDashboard != 1 && CommonUtil.DownloadingFileDashboard != 2) {
                DownloadVoice.downloadSampleFile(
                    context, modal.Content!!, voiceFolder, filename, holder, false
                )
            } else {
                if (CommonUtil.DownloadingFileDashboard != 2) {
                    val basePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path
                    } else {
                        Environment.getExternalStorageDirectory().path
                    } ?: return@setOnClickListener
                    val file = File(File(basePath, voiceFolder), filename)
                    initMediaPlayer(file.absolutePath)
                }
                CommonUtil.DownloadingFileDashboard = 2
                toggleRecentPlay(holder)
            }
        }
    }

    private fun initMediaPlayer(filePath: String) {
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(filePath)
            mediaPlayer?.prepare()
            mediaFileLengthInMilliseconds = mediaPlayer?.duration ?: 0
            iMediaDuration = (mediaFileLengthInMilliseconds / 1000.0).toInt()
        } catch (e: Exception) {
            Log.d("ExceptionWhilePlaying", e.toString())
        }
    }

    private fun setupEmergencySeekBar(holder: ViewHolder) {
        holder.emergencyseekbar.max = 99
        holder.emergencyseekbar.setOnTouchListener { v, _ ->
            if (v.id == R.id.emergencyseekbar) {
                val sb = v as SeekBar
                val playPosition = (mediaFileLengthInMilliseconds / 100) * sb.progress
                mediaPlayer?.seekTo(playPosition)
            }
            false
        }
    }

    private fun setupEmergencyPlayButton(holder: ViewHolder, filePath: String) {
        holder.imgEmgplaypause.setOnClickListener {
            if (mediaPlayer == null || PlayPath != filePath) {
                initMediaPlayer(filePath)
                PlayPath = filePath
            }
            toggleEmergencyPlay(holder)
        }
    }

    private fun toggleEmergencyPlay(holder: ViewHolder) {
        mediaFileLengthInMilliseconds = mediaPlayer?.duration ?: 0
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            holder.imgEmgplaypause.setImageResource(R.drawable.ic_play)
        } else {
            mediaPlayer?.start()
            holder.imgEmgplaypause.setImageResource(R.drawable.ic_pause)
            primarySeekBarProgressUpdaterEmergency(holder)
        }
    }

    private fun primarySeekBarProgressUpdaterEmergency(holder: ViewHolder) {
        val fileLength = mediaFileLengthInMilliseconds
        val current = mediaPlayer?.currentPosition ?: 0
        val progress = ((current.toFloat() / fileLength.coerceAtLeast(1)) * 100).toInt()
        holder.emergencyseekbar.progress = progress
        holder.lblEmgfromduration.text = milliSecondsToTimer(current.toLong())

        if (mediaPlayer?.isPlaying == true) {
            handler.postDelayed({ primarySeekBarProgressUpdaterEmergency(holder) }, 1000)
        }
    }

    private fun setupRecentSeekBar(holder: ViewHolder) {
        holder.recentseekbar.max = 99
        holder.recentseekbar.setOnTouchListener { v, _ ->
            if (v.id == R.id.recentseekbar) {
                val sb = v as SeekBar
                val playPosition = (mediaFileLengthInMilliseconds / 100) * sb.progress
                mediaPlayer?.seekTo(playPosition)
            }
            false
        }
    }

    private fun setupRecentPlayButton(holder: ViewHolder, filePath: String) {
        holder.imgRecentEmgplaypause.setOnClickListener {
            if (mediaPlayer == null || PlayPath != filePath) {
                initMediaPlayer(filePath)
                PlayPath = filePath
            }
            toggleRecentPlay(holder)
        }
    }

    private fun toggleRecentPlay(holder: ViewHolder) {
        mediaFileLengthInMilliseconds = mediaPlayer?.duration ?: 0
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
        } else {
            mediaPlayer?.start()
            holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_pause)
            primarySeekBarProgressUpdaterRecent(holder)
        }
    }

    private fun primarySeekBarProgressUpdaterRecent(holder: ViewHolder) {
        val fileLength = mediaFileLengthInMilliseconds
        val current = mediaPlayer?.currentPosition ?: 0
        val progress = ((current.toFloat() / fileLength.coerceAtLeast(1)) * 100).toInt()
        holder.recentseekbar.progress = progress
        holder.lblEmgRecentduration.text = milliSecondsToTimer(current.toLong())

        if (mediaPlayer?.isPlaying == true) {
            handler.postDelayed({ primarySeekBarProgressUpdaterRecent(holder) }, 1000)
        }
    }
    // endregion

    // region General Helpers
    private fun openSingleAssignmentFile(modal: DashboardSubItems, url: String) {
        when {
            modal.assignmentfiletype.equals("pdf", ignoreCase = true) -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    clipData = ClipData.newRawUri("", Uri.parse(url))
                    setDataAndType(Uri.parse(url), "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(intent)
            }
            modal.assignmentfiletype.equals("text", ignoreCase = true) -> { /* no-op */ }
            else -> {
                val intent = Intent(context, ViewFiles::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("images", url)
                }
                context.startActivity(intent)
            }
        }
    }

    private fun showLeaveDialog(title: String, leaveId: String, processType: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage("Once done can't be changed")
            .setPositiveButton("OK") { _, _ -> leaveRejectorAccept(processType, leaveId) }
            .setCancelable(false)
            .show()
    }

    private fun setMenuPermissions(menuId: Int, block: (read: String, write: String) -> Unit) {
        for (item in CommonUtil.MenuListDashboard) {
            if (item.id == menuId) {
                block(item.is_read_enabled.toString(), item.is_write_enabled.toString())
                break
            }
        }
    }

    private fun launch(clazz: Class<*>) {
        val intent = Intent(context, clazz).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        context.startActivity(intent)
    }

    fun readpdf() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            clipData = ClipData.newRawUri("", pdfUri)
            setDataAndType(pdfUri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }

    fun leaveRejectorAccept(processtype: String, Leaveid: String) {
        val jsonObject = JsonObject().apply {
            addProperty("leaveid", Leaveid)
            addProperty("userid", CommonUtil.MemberId?.toString() ?: "")
            addProperty("processtype", processtype)
        }

        RestClient.apiInterfaces.Leave_Reject(jsonObject)?.enqueue(object : Callback<Delete_noticeboard?> {
            override fun onResponse(
                call: Call<Delete_noticeboard?>,
                response: Response<Delete_noticeboard?>
            ) {
                if (response.code() == 200 || response.code() == 201) {
                    response.body()?.Message?.let { msg ->
                        AlertDialog.Builder(context)
                            .setTitle("Info")
                            .setMessage(msg)
                            .setPositiveButton("OK") { _, _ ->
                                val i = Intent(context, Attendance::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                }
                                context.startActivity(i)
                            }
                            .setCancelable(false)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<Delete_noticeboard?>, t: Throwable) {}
        })
    }
    // endregion

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is ViewHolder) {
            handler.removeCallbacksAndMessages(null)
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (type == "Menu") menuListInternal.size else newsModalArrayListInternal.size
    }

    // region ViewHolders
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lblNoiceboardTitle: TextView = itemView.findViewById(R.id.lblNoiceboardTitle)
        val lblNoticeDescription: TextView = itemView.findViewById(R.id.lblNoticeDescription)
        val lblCreateTime: TextView = itemView.findViewById(R.id.lblCreateTime)
        val lblNotiCreateDate: TextView = itemView.findViewById(R.id.lblNotiCreateDate)
        val rytNoticeboard: RelativeLayout = itemView.findViewById(R.id.rytNoticeboard)
        val lnrImageView: RelativeLayout = itemView.findViewById(R.id.lnrImageView)

        val lblCircularTitle: TextView = itemView.findViewById(R.id.lblCircularTitle)
        val lblCirculardescription: TextView = itemView.findViewById(R.id.lblCirculardescription)
        val lblCicularCreateDate: TextView = itemView.findViewById(R.id.lblCicularCreateDate)
        val lblCircularCreateTime: TextView = itemView.findViewById(R.id.lblCircularCreateTime)
        val LayoutCicular: ConstraintLayout = itemView.findViewById(R.id.LayoutCicular)
        val lnrCircularAttachment: LinearLayout = itemView.findViewById(R.id.lnrCircularAttachment)
        val lblPath: TextView = itemView.findViewById(R.id.lblPath)

        val LayoutAd: ConstraintLayout = itemView.findViewById(R.id.LayoutAdvertisement)
        val imgAdvertisement: ImageView = itemView.findViewById(R.id.imgAdvertisement)
        val imgthumb: ImageView = itemView.findViewById(R.id.imgthumb)

        val lnrEmgVoice: LinearLayout = itemView.findViewById(R.id.lnrEmgVoice)
        val lnrEmergencyVoice: LinearLayout = itemView.findViewById(R.id.lnrEmergencyVoice)
        val lblVoicetitle: TextView = itemView.findViewById(R.id.lblVoicetitle)
        val lblPostedBy: TextView = itemView.findViewById(R.id.lblPostedBy)
        val lbltotalduration: TextView = itemView.findViewById(R.id.lblTotalDuration)
        val lblVoiceTime: TextView = itemView.findViewById(R.id.lblVoiceTime)
        val lblVoiceDate: TextView = itemView.findViewById(R.id.lblVoiceDate)
        val lblEmgfromduration: TextView = itemView.findViewById(R.id.lblEmgfromduration)
        val imgEmgplaypause: ImageView = itemView.findViewById(R.id.imgEmgplaypause)
        val emergencyseekbar: SeekBar = itemView.findViewById(R.id.emergencyseekbar)
        val rytSeekbarlayout: RelativeLayout = itemView.findViewById(R.id.rytSeekbarlayout)

        val lnrRecentNotifications: LinearLayout = itemView.findViewById(R.id.lnrRecentNotifications)
        val lnrRecentVoice: LinearLayout = itemView.findViewById(R.id.lnrRecentVoice)
        val lblRecentDesciption: TextView = itemView.findViewById(R.id.lblRecentDesciption)
        val recentSeekbarlayout: RelativeLayout = itemView.findViewById(R.id.recentSeekbarlayout)
        val lblRecentDate: TextView = itemView.findViewById(R.id.lblRecentDate)
        val lblRecentTime: TextView = itemView.findViewById(R.id.lblRecentTime)
        val imgArrowdown: ImageView = itemView.findViewById(R.id.imgArrowdown)
        val imgRecentEmgplaypause: ImageView = itemView.findViewById(R.id.imgRecentEmgplaypause)
        val recentseekbar: SeekBar = itemView.findViewById(R.id.recentseekbar)
        val lblEmgRecentduration: TextView = itemView.findViewById(R.id.lblEmgRecentduration)
        val lblRecentTotalDuration: TextView = itemView.findViewById(R.id.lblRecentTotalDuration)
        val lblRecentPostedby: TextView = itemView.findViewById(R.id.lblRecentPostedby)
        val lblRecenttitle: TextView = itemView.findViewById(R.id.lblRecenttitle)
        val lnrplayvoice: LinearLayout = itemView.findViewById(R.id.lnrplayvoice)
        val rytRecentNotification: RelativeLayout = itemView.findViewById(R.id.rytRecentNotification)
        val imgRecentType: ImageView = itemView.findViewById(R.id.imgRecentType)

        val UpcomingEvent: ConstraintLayout = itemView.findViewById(R.id.UpcomingEvent)
        val lbleventDate: TextView = itemView.findViewById(R.id.lbleventDate)
        val lblCreateTimeevent: TextView = itemView.findViewById(R.id.lblCreateTimeevent)
        val lblEventtopic: TextView = itemView.findViewById(R.id.lblEventtopic)

        val Assignment: ConstraintLayout = itemView.findViewById(R.id.Assignment)
        val lblassignmenttopic: TextView = itemView.findViewById(R.id.lblassignmenttopic)
        val lblassignmentdescription: TextView = itemView.findViewById(R.id.lblassignmentdescription)
        val lblassignmentDate: TextView = itemView.findViewById(R.id.lblassignmentDate)
        val lbldate: TextView = itemView.findViewById(R.id.lbldate)
        val lnrAssignmentAttachment: LinearLayout = itemView.findViewById(R.id.lnrAssignmentAttachment)

        val lblNoticeboardTitle: TextView = itemView.findViewById(R.id.lblNoticeboardTitle)
        val lblNoticeboardDate: TextView = itemView.findViewById(R.id.lblNoticeboardDate)
        val lblchatDate: TextView = itemView.findViewById(R.id.lblchatDate)
        val lblCreateTimechat: TextView = itemView.findViewById(R.id.lblCreateTimechat)
        val lnrNoticeboardd: RelativeLayout = itemView.findViewById(R.id.lnrNoticeboardd)
        val imgarrowchat: ImageView = itemView.findViewById(R.id.imgarrowchat)

        val Leave_Request_dashboard: RelativeLayout = itemView.findViewById(R.id.Leave_Request_dashboard)
        val lnrNoticeboard: LinearLayout = itemView.findViewById(R.id.lnrNoticeboard)
        val rytLeaveDescription: RelativeLayout = itemView.findViewById(R.id.rytLeaveDescription)
        val lblLeaveReason: TextView = itemView.findViewById(R.id.lblLeaveReason)
        val lblRejaect: TextView = itemView.findViewById(R.id.lblRejaect)
        val lblApproval: TextView = itemView.findViewById(R.id.lblApproval)
        val lblLeaveCreatedDate: TextView = itemView.findViewById(R.id.lblLeaveCreatedDate)
        val lblleaveStatus: TextView = itemView.findViewById(R.id.lblleaveStatus)
        val lblLeaveType: TextView = itemView.findViewById(R.id.lblLeaveType)
        val lblLeaveNoOfDays: TextView = itemView.findViewById(R.id.lblLeaveNoOfDays)
        val department: TextView = itemView.findViewById(R.id.department)
        val departmentname: TextView = itemView.findViewById(R.id.departmentname)
        val year: TextView = itemView.findViewById(R.id.year)
        val section: TextView = itemView.findViewById(R.id.section)
        val lblFromDate: TextView = itemView.findViewById(R.id.lblFromDate)
        val lblToDate: TextView = itemView.findViewById(R.id.lblToDate)

        val lnrattendance: RelativeLayout = itemView.findViewById(R.id.lnrattendance)
        val lblsubjectnameAttendance: TextView = itemView.findViewById(R.id.lblsubjectnameAttendance)
        val lblattendancestatusDate: TextView = itemView.findViewById(R.id.lblattendancestatusDate)
        val lblattendancestatus: TextView = itemView.findViewById(R.id.lblattendancestatus)
        val constHeader: ConstraintLayout = itemView.findViewById(R.id.constHeader)
        val lblNoDataFound: TextView = itemView.findViewById(R.id.lblNoDataFound)
        val lblView: TextView = itemView.findViewById(R.id.lblView)

        fun resetViews() {
            lnrImageView.visibility = View.GONE
            LayoutAd.visibility = View.GONE
            LayoutCicular.visibility = View.GONE
            lnrEmgVoice.visibility = View.GONE
            lnrEmergencyVoice.visibility = View.GONE
            rytSeekbarlayout.visibility = View.GONE
            lnrRecentNotifications.visibility = View.GONE
            lnrRecentVoice.visibility = View.GONE
            recentSeekbarlayout.visibility = View.GONE
            lnrplayvoice.visibility = View.GONE
            UpcomingEvent.visibility = View.GONE
            Assignment.visibility = View.GONE
            lnrAssignmentAttachment.visibility = View.GONE
            lnrNoticeboardd.visibility = View.GONE
            Leave_Request_dashboard.visibility = View.GONE
            lnrattendance.visibility = View.GONE
            rytLeaveDescription.visibility = View.GONE
            lblNoDataFound.visibility = View.GONE
        }
    }

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lblMenuName: TextView = itemView.findViewById(R.id.lblMenuName)
        val imgMenu: ImageView = itemView.findViewById(R.id.imgMenu)
        val LayoutHome: RelativeLayout = itemView.findViewById(R.id.LayoutHome)
        val MenuHeader: ConstraintLayout = itemView.findViewById(R.id.MenuHeader)
    }
    // endregion
}