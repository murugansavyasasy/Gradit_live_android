package com.vsca.vsnapvoicecollege.Adapters

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
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
    private val newsModalArrayList: ArrayList<DashboardSubItems> = arrayListOf(),
    private val context: Context,
    private val type: String,
    private val menuList: List<MenuDetailsResponse> = emptyList(),
    private val onMenuClick: ((MenuDetailsResponse) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_DASHBOARD = 0
        const val TYPE_MENU = 1
        var PlayPath: String? = null

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

    private var mExpandedPosition = -1
    var msgcontent: String? = null
    var path: String? = null

    // Voice-related fields — lazy init so sections without audio don't allocate them
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var mediaFileLengthInMilliseconds = 0
    private var iMediaDuration = 0

    private val voiceFolder = "Gradit/Voice/"
    private lateinit var pdfUri: Uri

    // Cached formatter to avoid creating one per bind
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

    // region Menu Binding (merged from HomeMenus)
    private fun bindMenu(holder: MenuViewHolder, position: Int) {
        val data = menuList[position]

        holder.LayoutHome.setOnClickListener {
            onMenuClick?.invoke(data)
        }

        holder.imgMenu.visibility = View.VISIBLE
        holder.lblMenuName.visibility = View.VISIBLE
        holder.MenuHeader.visibility = View.VISIBLE

        val (iconRes, label) = when (data.id) {
            11 -> R.drawable.attendancenew to data.name
            17 -> R.drawable.chat to data.name
            16 -> R.drawable.communication to data.name
            3 -> R.drawable.exam to data.name
            4 -> R.drawable.attendance to data.name
            5 -> R.drawable.assignment to data.name
            6 -> R.drawable.circular to context.getString(R.string.txt_img_pdf)
            7 -> R.drawable.noticeboard to data.name
            8 -> R.drawable.events to data.name
            9 -> R.drawable.faculy_menu to data.name
            10 -> R.drawable.video to data.name
            12 -> R.drawable.bg_circle_course_details to data.name
            13 -> R.drawable.bg_circle_category_credit to data.name
            14 -> R.drawable.bg_circle_sem_credit to data.name
            15 -> R.drawable.bg_circle_exam_applications to data.name
            19 -> R.drawable.exam_hall_ticket to data.name
            20 -> R.drawable.fee_details to data.name
            21 -> R.drawable.biometric_attendance to data.name
            22 -> R.drawable.attendance_report to data.name
            23 -> R.drawable.resume_builder to data.name
            24 -> R.drawable.placement_event to data.name
            25 -> R.drawable.placement_training to data.name
            else -> null to null
        }

        if (iconRes != null && label != null) {
            holder.imgMenu.setImageResource(iconRes)
            holder.lblMenuName.text = label
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
        val modal = newsModalArrayList[position]

        // CRITICAL: Reset every conditional view to GONE first.
        holder.resetViews()

        // Reset dynamic widths
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
            "Emergency Notification" -> bindEmergencyNotification(holder, modal)
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
        val result = LocalTime.parse(time).format(DateTimeFormatter.ofPattern("h:mm a"))
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

        val parts = modal.createdonchat.toString().split("\\s+".toRegex())
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

    private fun bindEmergencyNotification(holder: ViewHolder, modal: DashboardSubItems) {
        holder.lnrEmgVoice.visibility = View.VISIBLE
        holder.lnrEmergencyVoice.visibility = View.VISIBLE
        holder.rytSeekbarlayout.visibility = View.VISIBLE

        val minutes = (modal.duration % 3600) / 60
        val seconds = modal.duration % 60
        holder.lbltotalduration.text = String.format("%02d:%02d", minutes, seconds)
        holder.lblVoicetitle.text = modal.menuTitle
        holder.lblPostedBy.text = modal.membername

        val splitDate = modal.createdon?.split("\\s+".toRegex())?.toTypedArray()
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

        if (file.exists()) {
            setupExistingEmergencyFile(holder, modal, file)
        } else {
            setupEmergencyDownload(holder, modal, filename)
        }
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

                if (file.exists()) {
                    setupExistingRecentFile(holder, modal, file)
                } else {
                    setupRecentDownload(holder, modal, filename)
                }
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

    // CRITICAL: Stop leaking MediaPlayer and Handler when dashboard ViewHolder is recycled
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
        return if (type == "Menu") menuList.size else newsModalArrayList.size
    }

    // region ViewHolders
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Notice Board
        val lblNoiceboardTitle: TextView = itemView.findViewById(R.id.lblNoiceboardTitle)
        val lblNoticeDescription: TextView = itemView.findViewById(R.id.lblNoticeDescription)
        val lblCreateTime: TextView = itemView.findViewById(R.id.lblCreateTime)
        val lblNotiCreateDate: TextView = itemView.findViewById(R.id.lblNotiCreateDate)
        val rytNoticeboard: RelativeLayout = itemView.findViewById(R.id.rytNoticeboard)
        val lnrImageView: RelativeLayout = itemView.findViewById(R.id.lnrImageView)

        // Circular
        val lblCircularTitle: TextView = itemView.findViewById(R.id.lblCircularTitle)
        val lblCirculardescription: TextView = itemView.findViewById(R.id.lblCirculardescription)
        val lblCicularCreateDate: TextView = itemView.findViewById(R.id.lblCicularCreateDate)
        val lblCircularCreateTime: TextView = itemView.findViewById(R.id.lblCircularCreateTime)
        val LayoutCicular: ConstraintLayout = itemView.findViewById(R.id.LayoutCicular)
        val lnrCircularAttachment: LinearLayout = itemView.findViewById(R.id.lnrCircularAttachment)
        val lblPath: TextView = itemView.findViewById(R.id.lblPath)

        // Ad
        val LayoutAd: ConstraintLayout = itemView.findViewById(R.id.LayoutAdvertisement)
        val imgAdvertisement: ImageView = itemView.findViewById(R.id.imgAdvertisement)
        val imgthumb: ImageView = itemView.findViewById(R.id.imgthumb)

        // Emergency Voice
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

        // Recent Notifications
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

        // Upcoming Events
        val UpcomingEvent: ConstraintLayout = itemView.findViewById(R.id.UpcomingEvent)
        val lbleventDate: TextView = itemView.findViewById(R.id.lbleventDate)
        val lblCreateTimeevent: TextView = itemView.findViewById(R.id.lblCreateTimeevent)
        val lblEventtopic: TextView = itemView.findViewById(R.id.lblEventtopic)

        // Assignments
        val Assignment: ConstraintLayout = itemView.findViewById(R.id.Assignment)
        val lblassignmenttopic: TextView = itemView.findViewById(R.id.lblassignmenttopic)
        val lblassignmentdescription: TextView = itemView.findViewById(R.id.lblassignmentdescription)
        val lblassignmentDate: TextView = itemView.findViewById(R.id.lblassignmentDate)
        val lbldate: TextView = itemView.findViewById(R.id.lbldate)
        val lnrAssignmentAttachment: LinearLayout = itemView.findViewById(R.id.lnrAssignmentAttachment)

        // Chat
        val lblNoticeboardTitle: TextView = itemView.findViewById(R.id.lblNoticeboardTitle)
        val lblNoticeboardDate: TextView = itemView.findViewById(R.id.lblNoticeboardDate)
        val lblchatDate: TextView = itemView.findViewById(R.id.lblchatDate)
        val lblCreateTimechat: TextView = itemView.findViewById(R.id.lblCreateTimechat)
        val lnrNoticeboardd: RelativeLayout = itemView.findViewById(R.id.lnrNoticeboardd)
        val imgarrowchat: ImageView = itemView.findViewById(R.id.imgarrowchat)

        // Leave Request
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

        // Attendance
        val lnrattendance: RelativeLayout = itemView.findViewById(R.id.lnrattendance)
        val lblsubjectnameAttendance: TextView = itemView.findViewById(R.id.lblsubjectnameAttendance)
        val lblattendancestatusDate: TextView = itemView.findViewById(R.id.lblattendancestatusDate)
        val lblattendancestatus: TextView = itemView.findViewById(R.id.lblattendancestatus)
        val constHeader: ConstraintLayout = itemView.findViewById(R.id.constHeader)
        val lblNoDataFound: TextView = itemView.findViewById(R.id.lblNoDataFound)
        val lblView: TextView = itemView.findViewById(R.id.lblView)

        // CRITICAL: Reset all conditional containers so recycled views don't overlap
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

    // Menu ViewHolder — merged from HomeMenus
    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lblMenuName: TextView = itemView.findViewById(R.id.lblMenuName)
        val imgMenu: CircleImageView = itemView.findViewById(R.id.imgMenu)
        val LayoutHome: RelativeLayout = itemView.findViewById(R.id.LayoutHome)
        val MenuHeader: ConstraintLayout = itemView.findViewById(R.id.MenuHeader)
    }
    // endregion
}
//package com.vsca.vsnapvoicecollege.Adapters
//
//import android.annotation.SuppressLint
//import android.content.ClipData
//import android.content.Context
//import android.content.Intent
//import android.media.MediaPlayer
//import android.media.MediaPlayer.OnCompletionListener
//import android.net.Uri
//import android.os.Build
//import android.os.Environment
//import android.os.Handler
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.View.*
//import android.view.ViewGroup
//import android.widget.*
//import android.widget.SeekBar.OnSeekBarChangeListener
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AlertDialog
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.google.gson.JsonObject
//import com.vsca.vsnapvoicecollege.Activities.*
//import com.vsca.vsnapvoicecollege.Model.DashboardSubItems
//import com.vsca.vsnapvoicecollege.Model.Delete_noticeboard
//import com.vsca.vsnapvoicecollege.R
//import com.vsca.vsnapvoicecollege.Repository.RestClient
//import com.vsca.vsnapvoicecollege.Utils.CommonUtil
//import com.vsca.vsnapvoicecollege.Utils.DownloadVoice
//import com.vsca.vsnapvoicecollege.albumImage.PDF_Reader
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.io.*
//import java.text.SimpleDateFormat
//import java.time.LocalTime
//import java.time.format.DateTimeFormatter
//import java.util.*
//
//
//class DashboardChild(
//    private val newsModalArrayList: ArrayList<DashboardSubItems>,
//    private val context: Context,
//    private val type: String
//) : RecyclerView.Adapter<DashboardChild.ViewHolder>() {
//
//    private var mExpandedPosition = -1
//    var msgcontent: String? = null
//    var path: String? = null
//    var filetype: String? = null
//    var detailsid: String? = null
//    var PlayPath: String? = null
//    var mediaPlayer: MediaPlayer? = MediaPlayer()
//    var mediaFileLengthInMilliseconds = 0
//    var handler = Handler()
//    var iMediaDuration = 0
//    var Position: Int = 0
//    var isemergencyExpanded: Boolean? = null
//    private val VOICE_FOLDER: String = "Gradit/Voice/"
//    private lateinit var pdfUri: Uri
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(
//            LayoutInflater.from(context).inflate(R.layout.dashboard_list_design, parent, false)
//        )
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
//        val modal = newsModalArrayList[position]
//        Position = position
//        Log.d("Dashboardtype", type)
//
//        if (type == "Emergency Notification") {
//            val screenWidth = holder.itemView.context.resources.displayMetrics.widthPixels
//            val params = holder.itemView.layoutParams
//
//            params.width = (screenWidth * 0.75f).toInt()
//            holder.itemView.layoutParams = params
//
//
//        } else {
//            val params = holder.itemView.layoutParams
//            params.width = ViewGroup.LayoutParams.MATCH_PARENT
//            holder.itemView.layoutParams = params
//
//        }
//
//        if ((type == "Circular")) {
//
//
//            holder.lnrImageView.visibility = GONE
//            holder.LayoutAd.visibility = GONE
//            holder.LayoutCicular.visibility = VISIBLE
//            holder.lnrEmgVoice.visibility = GONE
//
//            if (position % 2 == 0) {
//                holder.LayoutCicular.setBackgroundResource(R.drawable.bg_dashboard_circular_grey)
//            } else {
//                holder.LayoutCicular.setBackgroundResource(R.drawable.bg_dashboard_cicular)
//            }
//
//            holder.lblCircularTitle.text = modal.menuTitle
//            holder.lblCirculardescription.text = modal.menuDescription
//            holder.lblCircularCreateTime.text = modal.createTime
//            holder.lblCicularCreateDate.text = modal.createDate
//
//            holder.lblPath.text = context.getString(R.string.txt_attachment)
//
//            val menuid = BaseActivity.CircularMenuID
//            CommonUtil.MenuIDCircular = menuid
//
//            holder.LayoutCicular.setOnClickListener {
//                for (i in CommonUtil.MenuListDashboard.indices){
//                    if (6 == CommonUtil.MenuListDashboard.get(i).id){
//                        CommonUtil.menu_readCircular = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
//                        CommonUtil.menu_writeCircular = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
//                    }
//                }
//
//                val i: Intent = Intent(context, Circular::class.java)
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                context.startActivity(i)
//            }
//
//            holder.lnrCircularAttachment.setOnClickListener {
//
//                CommonUtil.Multipleiamge.clear()
//
//                if (modal.FilepathList.size == 0) {
//                    CommonUtil.ApiAlertContext(context, "file is empty")
//                } else if (modal.FilepathList.size == 1) {
//                    var Imagefileurl: String? = null
//                    for (k in modal.FilepathList.indices) {
//                        Imagefileurl = modal.FilepathList.get(k)
//                    }
//                    if (Imagefileurl!!.contains("pdf")) {
//                        pdfUri = Uri.parse(Imagefileurl)
//                        val i: Intent = Intent(context, PDF_Reader::class.java)
//                        i.putExtra("PdfView", pdfUri.toString())
//                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        context.startActivity(i)
//                    } else {
//                        val i: Intent = Intent(context, ViewFiles::class.java)
//                        i.putExtra("images", Imagefileurl)
//                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        context.startActivity(i)
//
//                    }
//
//                } else {
//                    for (k in modal.FilepathList.indices) {
//                        CommonUtil.Multipleiamge.add(modal.FilepathList.get(k))
//                    }
//                    val i: Intent = Intent(context, Assignment_MultipleFileView::class.java)
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    context.startActivity(i)
//                }
//            }
//
//        } else if ((type == "Upcoming Events")) {
//
//            holder.UpcomingEvent.visibility = VISIBLE
//            if (position % 2 == 0) {
//                holder.UpcomingEvent.setBackgroundResource(R.drawable.bg_dashboard_circular_grey)
//            } else {
//                holder.UpcomingEvent.setBackgroundResource(R.drawable.bg_dashboard_cicular)
//            }
//
//            holder.lblEventtopic.text = modal.Eventtime
//            holder.lbleventDate.text = modal.EventTitle
//
//            val time = modal.Eventdate
//            val result = LocalTime.parse(time).format(DateTimeFormatter.ofPattern("h:mm a"))
//
//
//            holder.lblCreateTimeevent.text = result
//
//            val menuid = BaseActivity.EventsMenuID
//            CommonUtil.MenuIDEvents = menuid
//            holder.UpcomingEvent.setOnClickListener {
//                for (i in CommonUtil.MenuListDashboard.indices){
//                    if (8 == CommonUtil.MenuListDashboard.get(i).id){
//                        CommonUtil.menu_readEvent = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
//                        CommonUtil.menu_writeEvent = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
//                    }
//                }
//                val i: Intent = Intent(context, Events::class.java)
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                context.startActivity(i)
//            }
//        } else if ((type == "Chat")) {
//
//            if (modal.message != null) {
//
//                holder.lnrNoticeboardd.visibility = GONE
//
//            } else {
//
//                holder.lnrNoticeboardd.visibility = VISIBLE
//
//                holder.lblNoticeboardTitle.text = modal.studentname
//                holder.lblNoticeboardDate.text = modal.question
//
//
//                val filename: String = modal.createdonchat.toString()
//                val file: Array<String> = filename.split(" ".toRegex()).toTypedArray()
//                val Stringone: String = file.get(0)
//                val StringTwo: String = file.get(1)
//                val StringThree: String = file.get(2)
//
//                holder.lblchatDate.text = Stringone
//                holder.lblCreateTimechat.text = StringTwo + " " + StringThree
//
//
//                val menuid = BaseActivity.ChatMenuID
//                CommonUtil.MenuIDChat = menuid
//                holder.imgarrowchat.setOnClickListener {
//                    for (i in CommonUtil.MenuListDashboard.indices){
//                        if (11 == CommonUtil.MenuListDashboard.get(i).id){
//                            CommonUtil.menu_readChat = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
//                            CommonUtil.menu_writeChat = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
//                        }
//                    }
//                    val i: Intent = Intent(context, ChatParent::class.java)
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    context.startActivity(i)
//                }
//            }
//
//        } else if ((type == "Leave Request")) {
//
//            if (modal.message != null) {
//
//                holder.lnrNoticeboardd.visibility = GONE
//
//            } else {
//
//                holder.Leave_Request_dashboard.visibility = VISIBLE
//
//                if (position % 2 == 0) {
//                    holder.lnrNoticeboard.setBackgroundResource(R.drawable.bg_dashboard_circular_grey)
//                } else {
//                    holder.lnrNoticeboard.setBackgroundResource(R.drawable.bg_dashboard_cicular)
//                }
//
//                holder.lblLeaveCreatedDate.text = modal.appliedon
//                holder.lblleaveStatus.text = modal.leavestatus
//                holder.lblLeaveType.text = modal.membernameLeaveRequest
//                holder.lblLeaveNoOfDays.text = modal.noofdays
//                holder.department.text = modal.departmentnameLeaveRequest
//                holder.departmentname.text = modal.coursenameLeaveRequest
//                holder.year.text = modal.yearnameLeaveRequest
//                holder.section.text = modal.sectionnameLeaveRequest
//                holder.lblFromDate.text = modal.fromdate
//                holder.lblToDate.text = modal.todate
//                holder.lblLeaveReason.text = modal.reason
//
//
//                val menuid = BaseActivity.AttendanceMeuID
//                CommonUtil.MenuIdAttendance = menuid
//                holder.Leave_Request_dashboard.setOnClickListener {
//
//                    holder.rytLeaveDescription.visibility = VISIBLE
//
//                }
//            }
//
//            holder.lblApproval.setOnClickListener {
//
//                val dlg = context.let { AlertDialog.Builder(it) }
//                dlg.setTitle("Approve Leave ")
//                dlg.setMessage("Once done can't be changed")
//                dlg.setPositiveButton("OK") { dialog, which ->
//
//                    leaveRejectorAccept("1", modal.leaveapplicationid.toString())
//                }
//
//                dlg.setCancelable(false)
//                dlg.create()
//                dlg.show()
//
//
//            }
//
//            holder.lblRejaect.setOnClickListener {
//
//                val dlg = context.let { AlertDialog.Builder(it) }
//                dlg.setTitle("Reject Leave")
//                dlg.setMessage("Once done can't be changed")
//                dlg.setPositiveButton("OK") { dialog, which ->
//
//                    leaveRejectorAccept("0", modal.leaveapplicationid.toString())
//
//                }
//
//                dlg.setCancelable(false)
//                dlg.create()
//                dlg.show()
//            }
//
//        } else if ((type == "Assignments")) {
//
//            holder.Assignment.visibility = VISIBLE
//            if (position % 2 == 0) {
//                holder.Assignment.setBackgroundResource(R.drawable.bg_dashboard_circular_grey)
//            } else {
//                holder.Assignment.setBackgroundResource(R.drawable.bg_dashboard_cicular)
//            }
//
//            var Imagefileurl: String? = null
//            for (k in modal.FilepathListAssignment.indices) {
//                Imagefileurl = modal.FilepathListAssignment.get(k)
//            }
//
//            if (modal.FilepathListAssignment.size == 0 || Imagefileurl.equals("") || Imagefileurl == null) {
//
//                holder.lnrAssignmentAttachment.visibility = GONE
//
//            } else {
//
//                holder.lnrAssignmentAttachment.visibility = VISIBLE
//
//            }
//
//            if (modal.assignmentfiletype.equals("pdf")) {
//                holder.lnrAssignmentAttachment.visibility = View.VISIBLE
//            } else if (modal.assignmentfiletype.equals("text")) {
//                holder.lnrAssignmentAttachment!!.visibility = View.GONE
//            } else {
//                holder.lnrAssignmentAttachment!!.visibility = View.VISIBLE
//            }
//
//            holder.lnrAssignmentAttachment.setOnClickListener {
//
//                if (modal.FilepathListAssignment.size == 0) {
//
//
//                } else if (modal.FilepathListAssignment.size == 1) {
//
//                    var Imagefileurl: String? = null
//                    for (k in modal.FilepathListAssignment.indices) {
//                        Imagefileurl = modal.FilepathListAssignment[k]
//                    }
//
//                    if (modal.assignmentfiletype.equals("pdf")) {
//                        holder.lnrAssignmentAttachment!!.visibility = View.VISIBLE
//                        val intent = Intent(Intent.ACTION_VIEW)
//                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                        intent.clipData = ClipData.newRawUri("", Uri.parse(Imagefileurl))
//                        intent.setDataAndType((Uri.parse(Imagefileurl)), "application/pdf")
//                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                        context.startActivity(intent)
//
//                    } else if (modal.assignmentfiletype.equals("text")) {
//                        holder.lnrAssignmentAttachment!!.visibility = View.GONE
//                    } else {
//                        holder.lnrAssignmentAttachment!!.visibility = View.VISIBLE
//                        val i: Intent = Intent(context, ViewFiles::class.java)
//                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        i.putExtra("images", Imagefileurl)
//                        context.startActivity(i)
//                    }
//
//                } else {
//
//                    holder.lnrAssignmentAttachment.visibility = VISIBLE
//
//                    for (k in modal.FilepathListAssignment.indices) {
//                        CommonUtil.Multipleiamge.add(modal.FilepathListAssignment.get(k))
//                    }
//
//                    val i: Intent = Intent(context, Assignment_MultipleFileView::class.java)
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    context.startActivity(i)
//
//                }
//            }
//
//
//            val date = getCurrentDateTime()
//            val dateInString = date.toString("yyyy/MM/dd")
//            holder.lblassignmenttopic.text = modal.assignmenttopic
//            holder.lblassignmentdescription.text = modal.assignmentdescription
//            holder.lblassignmentDate.text = dateInString
//            holder.lbldate.text = modal.submissiondate
//
//            val menuid = BaseActivity.AssignmentMenuID
//            CommonUtil.MenuIDAssignment = menuid
//            holder.Assignment.setOnClickListener({
//                for (i in CommonUtil.MenuListDashboard.indices){
//                    if (5 == CommonUtil.MenuListDashboard.get(i).id){
//                        CommonUtil.menu_readAssignment = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
//                        CommonUtil.menu_writeAssignment = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
//                    }
//                }
//                val i: Intent = Intent(context, Assignment::class.java)
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                context.startActivity(i)
//            })
//
//        } else if ((type == "Notice Board")) {
//
//            holder.lnrImageView.visibility = VISIBLE
//            holder.LayoutAd.visibility = GONE
//            holder.LayoutCicular.visibility = GONE
//            holder.lnrEmgVoice.visibility = GONE
//            holder.lblNoiceboardTitle.text = modal.menuTitle
//            holder.lblNoticeDescription.text = modal.menuDescription
//            holder.lblCreateTime.text = modal.createTime
//            holder.lblNotiCreateDate.text = modal.createDate
//            if (position % 2 == 0) {
//                holder.rytNoticeboard.setBackgroundResource(R.drawable.noticeboard_blue)
//            } else {
//                holder.rytNoticeboard.setBackgroundResource(R.drawable.noticeboard_yellow)
//            }
//
//            var menuid = BaseActivity.NoticeboardMenuID
//            CommonUtil.MenuIDNoticeboard = menuid
//            holder.lnrImageView.setOnClickListener({
//                for (i in CommonUtil.MenuListDashboard.indices){
//                    if (7 == CommonUtil.MenuListDashboard.get(i).id){
//                        CommonUtil.menu_readNoticeBoard = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
//                        CommonUtil.menu_writeNoticeBoard = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
//                    }
//                }
//                val i: Intent = Intent(context, Noticeboard::class.java)
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                context.startActivity(i)
//            })
//
//        } else if ((type == "Ad")) {
//            holder.lnrImageView.visibility = GONE
//            holder.LayoutAd.visibility = VISIBLE
//            holder.LayoutCicular.visibility = GONE
//            holder.lnrEmgVoice.visibility = GONE
//
//            Glide.with(context).load(modal.adBaackgroundImage).into(holder.imgAdvertisement)
//            Glide.with(context).load(modal.addImage).into(holder.imgthumb)
//
//            holder.LayoutAd.setOnClickListener({
//
//                BaseActivity.LoadWebViewContext(context, modal.Addurl)
//
//            })
//
//        } else if ((type == "Attendance")) {
//
//            if (modal.message != null) {
//
//                holder.lnrattendance.visibility = GONE
//
//            } else {
//
//                holder.lnrattendance.visibility = VISIBLE
//                holder.lblattendancestatusDate.text = modal.AttendanceDate
//                holder.lblsubjectnameAttendance.text = modal.SubjectName
//                holder.lblattendancestatus.text = modal.AttendanceType
//
//                if (modal.AttendanceType.equals("Absent")) {
//
//                    holder.lblattendancestatus.setBackgroundResource(R.drawable.bg_redcolor)
//
//                } else {
//
//                    holder.lblattendancestatus.setBackgroundResource(R.drawable.bg_available_selected_green)
//
//                }
//
//                val menuid = BaseActivity.AttendanceMeuID
//                CommonUtil.MenuIdAttendance = menuid
//                holder.lnrImageView.setOnClickListener({
//                    for (i in CommonUtil.MenuListDashboard.indices){
//                        if (4 == CommonUtil.MenuListDashboard.get(i).id){
//                            CommonUtil.menu_readAttendance = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
//                            CommonUtil.menu_writeAttendance = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
//                        }
//                    }
//                    val i: Intent = Intent(context, Attendance::class.java)
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                    context.startActivity(i)
//                })
//            }
//
//        }
//        else if ((type == "Emergency Notification")) {
//            holder.lnrImageView.visibility = GONE
//            holder.LayoutAd.visibility = GONE
//            holder.LayoutCicular.visibility = GONE
//            holder.lnrEmgVoice.visibility = VISIBLE
//
////            isemergencyExpanded = position == mExpandedPosition
//
////            holder.lnrEmergencyVoice.visibility = if (isemergencyExpanded!!) VISIBLE else GONE
////            holder.lnrEmgVoice.isActivated = isemergencyExpanded!!
//            holder.lnrEmergencyVoice.visibility = VISIBLE
//
//            val voiceduration = modal.duration
//            val minutes = (voiceduration % 3600) / 60
//            val seconds = voiceduration % 60
//            val timeString = String.format("%02d:%02d", minutes, seconds)
//            holder.lbltotalduration.text = timeString
//            holder.lblVoicetitle.text = modal.menuTitle
//            holder.lblPostedBy.text = modal.membername
//            val value = modal.createdon
//            val splitDate = value!!.split("\\s+".toRegex()).toTypedArray()
//            val Date = splitDate[0]
//            holder.lblVoiceDate.text = Date
//            val time = splitDate[1]
//            holder.lblVoiceTime.text = time
//
//            holder.rytSeekbarlayout.visibility = VISIBLE
//            CommonUtil.DownloadingFileDashboard = 0
//
//            msgcontent = modal.voiceFilepath
//            path = modal.voiceFilepath
//            Log.d("filepath", path.toString())
//            var filename: String = modal.MsgId!! + "_" + "Gradit.mp3"
//            path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
//            } else {
//                Environment.getExternalStorageDirectory().path
//            }
//
//            val dir = File(path, VOICE_FOLDER)
//            val file = File(dir, filename)
//
//            if (file.exists()) {
//
//                holder.rytSeekbarlayout.visibility = VISIBLE
//                CommonUtil.CommunicationisExpandAdapter = false
//
//                SetUpAudioPlayer(holder)
//                fetchPathUrl(modal)
//
////                mExpandedPosition = if (isemergencyExpanded!!) -1 else position
////                notifyDataSetChanged()
//
//                Log.d("FileExist", "exist")
//                mediaPlayer!!.setOnCompletionListener(OnCompletionListener {
//                    holder.imgEmgplaypause.setImageResource(R.drawable.ic_play)
//
//                    Log.d("SeekCompleteion", "stop")
//                    mediaPlayer!!.seekTo(0)
//                })
//
//                holder.emergencyseekbar.setOnSeekBarChangeListener(object :
//                    OnSeekBarChangeListener {
//                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
//                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
//                    override fun onProgressChanged(
//                        seekBar: SeekBar, progress: Int, fromUser: Boolean
//                    ) {
//
//                    }
//                })
//
//                Log.d("FetchSong", "END***************************************")
//                holder.imgEmgplaypause.setOnClickListener(object : OnClickListener {
//                    override fun onClick(view: View) {
//                        mediaFileLengthInMilliseconds =
//                            mediaPlayer!!.duration // gets the song length in milliseconds from URL
//
//
//                        if (!mediaPlayer!!.isPlaying) {
//                            mediaPlayer!!.start()
//                            holder.imgEmgplaypause.setImageResource(R.drawable.ic_pause)
//                        } else {
//                            mediaPlayer!!.pause()
//                            holder.imgEmgplaypause.setImageResource(R.drawable.ic_play)
//                        }
//                        primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds)
//                    }
//
//                    private fun primarySeekBarProgressUpdater(fileLength: Int) {
//                        val iProgress =
//                            ((mediaPlayer!!.currentPosition.toFloat() / fileLength) * 100).toInt()
//                        holder.emergencyseekbar.progress = iProgress
//                        if (mediaPlayer!!.isPlaying) {
//                            val notification: Runnable = object : Runnable {
//                                override fun run() {
//                                    holder.lblEmgfromduration.text = milliSecondsToTimer(
//                                        mediaPlayer!!.currentPosition.toLong()
//                                    )
//                                    primarySeekBarProgressUpdater(fileLength)
//                                }
//                            }
//                            handler.postDelayed(notification, 1000)
//                        }
//                    }
//                })
//
//            } else {
//
//                Log.d("FileExist", "notexist")
//
//                holder.imgEmgplaypause.setOnClickListener(object : OnClickListener {
//
//                    override fun onClick(view: View) {
//
//
//                        Log.d("isDownload", CommonUtil.DownloadingFileDashboard.toString())
//                        if (CommonUtil.DownloadingFileDashboard != 1 && CommonUtil.DownloadingFileDashboard != 2) {
//                            Log.d(
//                                "isDownload",
//                                CommonUtil.DownloadingFileDashboard.toString()
//                            )
//
//                            DownloadVoice.downloadSampleFile(
//                                context,
//                                modal.voiceFilepath!!,
//                                VOICE_FOLDER,
//                                filename,
//                                holder,
//                                true
//                            )
//
//                        } else {
//
//                            if (CommonUtil.DownloadingFileDashboard != 2) {
//                                SetUpAudioPlayer(holder)
//                                fetchPathUrl(modal)
//
//                            }
//
//                            CommonUtil.DownloadingFileDashboard = 2
//
//                            mediaFileLengthInMilliseconds = mediaPlayer!!.duration
//
//                            if (!mediaPlayer!!.isPlaying) {
//                                mediaPlayer!!.start()
//                                holder.imgEmgplaypause.setImageResource(R.drawable.ic_pause)
//                            } else {
//                                mediaPlayer!!.pause()
//                                holder.imgEmgplaypause.setImageResource(R.drawable.ic_play)
//                            }
//                        }
//
//                        primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds)
//
//                    }
//
//                    private fun primarySeekBarProgressUpdater(fileLength: Int) {
//                        val iProgress =
//                            ((mediaPlayer!!.currentPosition.toFloat() / fileLength) * 100).toInt()
//                        holder.emergencyseekbar.progress = iProgress
//                        if (mediaPlayer!!.isPlaying) {
//                            val notification: Runnable = object : Runnable {
//                                override fun run() {
//
//                                    holder.lblEmgfromduration.text =
//                                        milliSecondsToTimer(mediaPlayer!!.currentPosition.toLong())
//                                    primarySeekBarProgressUpdater(fileLength)
//                                }
//                            }
//                            handler.postDelayed(notification, 1000)
//                        }
//                    }
//                })
//            }
//
//            holder.emergencyseekbar.setOnSeekBarChangeListener(object :
//                OnSeekBarChangeListener {
//                override fun onStopTrackingTouch(seekBar: SeekBar) {}
//                override fun onStartTrackingTouch(seekBar: SeekBar) {}
//                override fun onProgressChanged(
//                    seekBar: SeekBar, progress: Int, fromUser: Boolean
//                ) {
//
//
//                }
//            })
//
//            mediaPlayer!!.setOnCompletionListener {
//                holder.imgEmgplaypause.setImageResource(R.drawable.ic_play)
//                mediaPlayer!!.seekTo(0)
//            }
//
////            holder.rytEmgVoice.setOnClickListener(object : OnClickListener {
////                override fun onClick(view: View) {
//////                    mExpandedPosition = if (isemergencyExpanded!!) -1 else position
//////                    notifyDataSetChanged()
////                }
////            })
//
//        } else if ((type == "Recent Notifications")) {
//
//            holder.lnrImageView.visibility = GONE
//            holder.LayoutAd.visibility = GONE
//            holder.LayoutCicular.visibility = GONE
//            holder.lnrEmgVoice.visibility = GONE
//            holder.lnrRecentNotifications.visibility = VISIBLE
//
//            val isExpanded = position == mExpandedPosition
//            holder.lnrRecentVoice.visibility = if (isExpanded) VISIBLE else GONE
//            holder.lnrRecentNotifications.isActivated = isExpanded
//            holder.lnrRecentNotifications.visibility = VISIBLE
//            holder.lblRecenttitle.text = modal.Content
//
//            if (modal.RecentType!!.equals("Voice")) {
//                holder.lnrplayvoice.visibility = VISIBLE
//                holder.imgRecentType.setImageResource(R.drawable.dashboard_recent_voice)
//                holder.imgRecentType.alpha = 0.7f
//            }
//            if (modal.RecentType!!.equals("Emergencyvoice Message")) {
//                holder.lnrplayvoice.visibility = VISIBLE
//                holder.imgRecentType.setImageResource(R.drawable.emergency_voice)
//                holder.imgRecentType.alpha = 0.7f
//            }
//            if (modal.RecentType!!.equals("Text Message")) {
//                holder.lnrplayvoice.visibility = GONE
//                holder.imgRecentType.setImageResource(R.drawable.dashboard_text)
//                holder.imgRecentType.alpha = 0.7f
//                holder.lblRecentDesciption.text = modal.Content
//            }
//            val voiceduration = modal.duration
//            val minutes = (voiceduration % 3600) / 60
//            val seconds = voiceduration % 60
//            val timeString = String.format("%02d:%02d", minutes, seconds)
//            holder.lblRecentTotalDuration.text = timeString
//            holder.lblRecentPostedby.text = modal.membername
//            holder.lblRecentDate.text = modal.createdon
//            holder.lblRecentTime.text = modal.createTime
//
//            if (isExpanded) {
//                holder.imgArrowdown.setImageResource(R.drawable.ic_arrow_up_blue)
//                holder.lnrplayvoice.visibility = GONE
//                mediaPlayer!!.seekTo(0)
//
//
//            } else {
//
//                if (modal.RecentType!!.equals("Voice")) {
//                    holder.lnrplayvoice.visibility = VISIBLE
//                }
//                if (modal.RecentType!!.equals("Emergencyvoice Message")) {
//                    holder.lnrplayvoice.visibility = VISIBLE
//                }
//                if (modal.RecentType!!.equals("Text Message")) {
//                    holder.lnrplayvoice.visibility = GONE
//                }
//                holder.imgArrowdown.setImageResource(R.drawable.ic_arrow_down_blue)
//            }
//
//            holder.rytRecentNotification.setOnClickListener(object : OnClickListener {
//                override fun onClick(view: View) {
//                    holder.lnrplayvoice.visibility = GONE
//                    CommonUtil.DownloadingFileDashboard = 0
//                    if (modal.RecentType!!.equals("Text")) {
//                        holder.lblRecentDesciption.visibility = VISIBLE
//                        holder.lblRecenttitle.visibility = VISIBLE
//                        holder.lblRecentDesciption.text = modal.menuDescription
//                        holder.lblRecenttitle.text = modal.Content
//                        holder.recentSeekbarlayout.visibility = GONE
//                        mExpandedPosition = if (isExpanded) -1 else position
//                        notifyDataSetChanged()
//
//                    } else {
//
//                        mExpandedPosition = if (isExpanded) -1 else position
//                        notifyDataSetChanged()
//
//                        msgcontent = modal.Content
//                        Log.d("msgcontent", msgcontent!!)
//
//                        path = modal.Content
//                        Log.d("Voice_Path", path.toString())
//
//                        Log.d("Recentpath", path!!)
//
//                        val filename: String = modal.MsgId!! + "_" + "Gradit.mp3"
//                        path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                            context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
//                        } else {
//                            Environment.getExternalStorageDirectory().path
//                        }
//
//                        val dir = File(path, VOICE_FOLDER)
//                        val file = File(dir, filename)
//
//                        if (file.exists()) {
//
//                            Log.d("FileExist", "existyes")
//
//                            holder.recentSeekbarlayout.visibility = VISIBLE
//
//                            CommonUtil.CommunicationisExpandAdapter = false
//
//                            SetUpRecentAudioPlayer(holder)
//                            fetchRecentPathUrl(modal)
//
//                            holder.imgRecentEmgplaypause.setOnClickListener(object :
//                                OnClickListener {
//                                override fun onClick(view: View) {
//
//                                    mediaFileLengthInMilliseconds =
//                                        mediaPlayer!!.duration  // gets the song length in milliseconds from URL
//                                    if (!mediaPlayer!!.isPlaying) {
//                                        mediaPlayer!!.start()
//                                        holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_pause)
//
//                                    } else {
//
//                                        mediaPlayer!!.pause()
//                                        holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
//
//                                    }
//                                    primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds)
//                                }
//
//                                private fun primarySeekBarProgressUpdater(fileLength: Int) {
//                                    val iProgress =
//                                        ((mediaPlayer!!.currentPosition.toFloat() / fileLength) * 100).toInt()
//                                    holder.recentseekbar.progress = iProgress
//                                    if (mediaPlayer!!.isPlaying) {
//                                        val notification: Runnable = object : Runnable {
//                                            override fun run() {
//                                                holder.lblEmgRecentduration.text =
//                                                    milliSecondsToTimer(mediaPlayer!!.currentPosition.toLong())
//                                                primarySeekBarProgressUpdater(fileLength)
//                                            }
//                                        }
//                                        handler.postDelayed(notification, 1000)
//                                    }
//                                }
//                            })
//
//                            holder.recentseekbar.setOnSeekBarChangeListener(object :
//                                OnSeekBarChangeListener {
//                                override fun onStopTrackingTouch(seekBar: SeekBar) {}
//                                override fun onStartTrackingTouch(seekBar: SeekBar) {}
//                                override fun onProgressChanged(
//                                    seekBar: SeekBar, progress: Int, fromUser: Boolean
//                                ) {
//
//                                }
//                            })
//
//                            mediaPlayer!!.setOnCompletionListener {
//                                holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
//                                mediaPlayer!!.seekTo(0)
//                            }
//
//                        } else {
//
//                            Log.d("FileExist", "Not_exists")
//
//                            holder.recentSeekbarlayout.visibility = VISIBLE
//
//
//                            holder.imgRecentEmgplaypause.setOnClickListener(object :
//                                OnClickListener {
//                                override fun onClick(view: View) {
//
//                                    if (CommonUtil.DownloadingFileDashboard != 1 && CommonUtil.DownloadingFileDashboard != 2) {
//                                        DownloadVoice.downloadSampleFile(
//                                            context, modal.Content!!, VOICE_FOLDER,
//                                            filename,
//                                            holder,
//                                            false
//                                        )
//
//                                    } else {
//
//                                        if (CommonUtil.DownloadingFileDashboard != 2) {
//                                            SetUpRecentAudioPlayer(holder)
//                                            fetchRecentPathUrl(modal)
//
//                                        }
//
//                                        CommonUtil.DownloadingFileDashboard = 2
//
//                                        mediaFileLengthInMilliseconds = mediaPlayer!!.duration
//
//                                        if (!mediaPlayer!!.isPlaying) {
//                                            mediaPlayer!!.start()
//                                            holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_pause)
//                                        } else {
//                                            mediaPlayer!!.pause()
//                                            holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
//                                        }
//                                    }
//
//                                    primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds)
//
//                                }
//
//
//                                private fun primarySeekBarProgressUpdater(fileLength: Int) {
//                                    val iProgress =
//                                        ((mediaPlayer!!.currentPosition.toFloat() / fileLength) * 100).toInt()
//                                    holder.recentseekbar.progress = iProgress
//                                    if (mediaPlayer!!.isPlaying) {
//                                        val notification: Runnable = object : Runnable {
//                                            override fun run() {
//                                                holder.lblEmgRecentduration.text =
//                                                    milliSecondsToTimer(
//                                                        mediaPlayer!!.currentPosition.toLong()
//                                                    )
//                                                primarySeekBarProgressUpdater(fileLength)
//                                            }
//                                        }
//                                        handler.postDelayed(notification, 1000)
//                                    }
//                                }
//                            })
//
//                            holder.recentseekbar.setOnSeekBarChangeListener(object :
//                                OnSeekBarChangeListener {
//                                override fun onStopTrackingTouch(seekBar: SeekBar) {}
//                                override fun onStartTrackingTouch(seekBar: SeekBar) {}
//                                override fun onProgressChanged(
//                                    seekBar: SeekBar, progress: Int, fromUser: Boolean
//                                ) {
//
//                                }
//                            })
//                            mediaPlayer!!.setOnCompletionListener {
//                                holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
//                                mediaPlayer!!.seekTo(0)
//                            }
//                        }
//                    }
//                }
//            })
//
//        } else {
//            holder.lnrImageView.visibility = GONE
//            holder.LayoutAd.visibility = GONE
//            holder.LayoutCicular.visibility = GONE
//            holder.lnrEmgVoice.visibility = GONE
//        }
//    }
//
//    private fun SetUpRecentAudioPlayer(holder: ViewHolder) {
//
//        holder.recentseekbar.max = 99
//        holder.recentseekbar.setOnTouchListener(object : OnTouchListener {
//            override fun onTouch(v: View, event: MotionEvent): Boolean {
//                if (v.id == R.id.recentseekbar) {
//                    run {
//                        val sb: SeekBar = v as SeekBar
//                        val playPositionInMillisecconds: Int =
//                            (mediaFileLengthInMilliseconds / 100) * sb.progress
//                        mediaPlayer!!.seekTo(playPositionInMillisecconds)
//                    }
//                }
//                return false
//            }
//        })
//    }
//
//    private fun fetchRecentPathUrl(modal: DashboardSubItems) {
//        Log.d("PlayStart", "Start***************************************")
//        try {
//            var filename: String = modal.MsgId!! + "_" + "Gradit.mp3"
//            val path: String
//            path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
//            } else {
//                Environment.getExternalStorageDirectory().path
//            }
//
//            val dir = File(path, VOICE_FOLDER)
//            val file = File(dir, filename)
//            PlayPath = file.path
//            Log.d("PlayPath", PlayPath!!)
//            mediaPlayer!!.reset()
//            mediaPlayer!!.setDataSource(PlayPath)
//            mediaPlayer!!.prepare()
//            iMediaDuration = (mediaPlayer!!.duration / 1000.0).toInt()
//            Log.d("iMediaDuration", iMediaDuration.toString())
//
//        } catch (e: Exception) {
//            Log.d("ExceptionWhilePlaying", e.toString())
//        }
//        Log.d("PlayEnd", "END***************************************")
//    }
//
//    private fun fetchPathUrl(modal: DashboardSubItems) {
//        Log.d("PlayStart", "Start***************************************123")
//        try {
//            var filename: String = modal.MsgId!! + "_" + "Gradit.mp3"
//            val path: String
//            path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
//            } else {
//                Environment.getExternalStorageDirectory().path
//            }
//
//            val dir = File(path, VOICE_FOLDER)
//            val file = File(dir, filename)
//            PlayPath = file.path
//            mediaPlayer!!.reset()
//            mediaPlayer!!.setDataSource(PlayPath)
//            mediaPlayer!!.prepare()
//            iMediaDuration = (mediaPlayer!!.duration / 1000.0).toInt()
//        } catch (e: Exception) {
//            Log.d("ExceptionWhilePlaying", e.toString())
//        }
//        Log.d("PlayEnd", "END***************************************")
//    }
//
//    private fun SetUpAudioPlayer(holder: ViewHolder) {
//        holder.emergencyseekbar.max = 99
//        holder.emergencyseekbar.setOnTouchListener(object : OnTouchListener {
//            override fun onTouch(v: View, event: MotionEvent): Boolean {
//                if (v.id == R.id.emergencyseekbar) {
//                    run {
//                        val sb: SeekBar = v as SeekBar
//                        val playPositionInMillisecconds: Int =
//                            (mediaFileLengthInMilliseconds / 100) * sb.progress
//                        mediaPlayer!!.seekTo(playPositionInMillisecconds)
//                    }
//                }
//                return false
//            }
//        })
//    }
//
//    override fun getItemCount(): Int {
//        return newsModalArrayList.size
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val lblNoiceboardTitle: TextView
//        val lblNoticeDescription: TextView
//        val lblCreateTime: TextView
//        val lblNotiCreateDate: TextView
//        val lblCircularTitle: TextView
//        val lblCirculardescription: TextView
//        val lblCicularCreateDate: TextView
//        val lblCircularCreateTime: TextView
//        val lblVoicetitle: TextView
//        val lblPostedBy: TextView
//        val lbltotalduration: TextView
//        val lblVoiceTime: TextView
//        val lblVoiceDate: TextView
//        val lblEmgfromduration: TextView
//        val lnrImageView: RelativeLayout
//        val rytEmgVoice: RelativeLayout
//        var imgAdvertisement: ImageView
//        var imgthumb: ImageView
//        var imgEmgplaypause: ImageView
//        var lnrRecentVoice: LinearLayout
//        var LayoutAd: ConstraintLayout
//        var LayoutCicular: ConstraintLayout
//        var rytNoticeboard: RelativeLayout
//        var lnrEmgVoice: LinearLayout
//        var lnrEmergencyVoice: LinearLayout
//        var lblVoicedescription: TextView
//        var lnrCircularAttachment: LinearLayout
//        var emergencyseekbar: SeekBar
//        var lblPath: TextView
//        var lblRecentDesciption: TextView
//        var lnrRecentNotifications: LinearLayout
//        var recentSeekbarlayout: RelativeLayout
//        var lblRecentDate: TextView
//        var lblRecentTime: TextView
//        var imgArrowdown: ImageView
//        var imgRecentEmgplaypause: ImageView
//        var recentseekbar: SeekBar
//        var lblEmgRecentduration: TextView
//        var lblRecentTotalDuration: TextView
//        var lblRecentPostedby: TextView
//        var lblRecenttitle: TextView
//        var lblNoDataFound: TextView
//        var lnrplayvoice: LinearLayout
//        var rytRecentNotification: RelativeLayout
//        var imgRecentType: ImageView
//        var rytSeekbarlayout: RelativeLayout
//        var lblEventtopic: TextView
//        var lblCreateTimeevent: TextView
//        var lbleventDate: TextView
//        var UpcomingEvent: ConstraintLayout
//        var lblassignmenttopic: TextView
//        var lblassignmentdescription: TextView
//        var lblassignmentDate: TextView
//        var lbldate: TextView
//        var lblView: TextView
//        var Assignment: ConstraintLayout
//        var lnrAssignmentAttachment: LinearLayout
//
//
//        //chat
//        var lblNoticeboardTitle: TextView
//        var lblNoticeboardDate: TextView
//        var lblchatDate: TextView
//        var lblCreateTimechat: TextView
//        var lnrNoticeboardd: RelativeLayout
//        var imgarrowchat: ImageView
//
//
//        //Leave Request
//        var lblLeaveCreatedDate: TextView
//        var lblleaveStatus: TextView
//        var lblLeaveType: TextView
//        var lblLeaveNoOfDays: TextView
//        var department: TextView
//        var departmentname: TextView
//        var year: TextView
//        var section: TextView
//        var lblFromDate: TextView
//        var lblToDate: TextView
//        var Leave_Request_dashboard: RelativeLayout
//        var lnrNoticeboard: LinearLayout
//        lateinit var rytLeaveDescription: RelativeLayout
//        lateinit var lblLeaveReason: TextView
//        lateinit var lblRejaect: TextView
//        lateinit var lblApproval: TextView
//
//        //Attendance
//        var lblattendancestatusDate: TextView
//
//        //  var lblattendance: TextView
//        var lblsubjectnameAttendance: TextView
//        var lblattendancestatus: TextView
//        var lnrattendance: RelativeLayout
//        var constHeader: ConstraintLayout
//
//        init {
//
//            //Attendance
//            lnrattendance = itemView.findViewById(R.id.lnrattendance)
//            //  lblattendance = itemView.findViewById(R.id.lblattendance)
//            lblsubjectnameAttendance = itemView.findViewById(R.id.lblsubjectnameAttendance)
//            lblattendancestatusDate = itemView.findViewById(R.id.lblattendancestatusDate)
//            lblattendancestatus = itemView.findViewById(R.id.lblattendancestatus)
//
//            //Leave Request
//            rytLeaveDescription = itemView.findViewById(R.id.rytLeaveDescription)
//            lblLeaveReason = itemView.findViewById(R.id.lblLeaveReason)
//            lblRejaect = itemView.findViewById(R.id.lblRejaect)
//            lblApproval = itemView.findViewById(R.id.lblApproval)
//
//
//            Leave_Request_dashboard = itemView.findViewById(R.id.Leave_Request_dashboard)
//            lblLeaveCreatedDate = itemView.findViewById(R.id.lblLeaveCreatedDate)
//            lblleaveStatus = itemView.findViewById(R.id.lblleaveStatus)
//            lblLeaveType = itemView.findViewById(R.id.lblLeaveType)
//            lblLeaveNoOfDays = itemView.findViewById(R.id.lblLeaveNoOfDays)
//            department = itemView.findViewById(R.id.department)
//            departmentname = itemView.findViewById(R.id.departmentname)
//            year = itemView.findViewById(R.id.year)
//            section = itemView.findViewById(R.id.section)
//            lblFromDate = itemView.findViewById(R.id.lblFromDate)
//            lblToDate = itemView.findViewById(R.id.lblToDate)
//            lnrNoticeboard = itemView.findViewById(R.id.lnrNoticeboard)
//
//            //Chat
//            lblNoticeboardTitle = itemView.findViewById(R.id.lblNoticeboardTitle)
//            lblNoticeboardDate = itemView.findViewById(R.id.lblNoticeboardDate)
//            lblchatDate = itemView.findViewById(R.id.lblchatDate)
//            lblCreateTimechat = itemView.findViewById(R.id.lblCreateTimechat)
//            lnrNoticeboardd = itemView.findViewById(R.id.lnrNoticeboardd)
//            imgarrowchat = itemView.findViewById(R.id.imgarrowchat)
//
//            //Event
//            UpcomingEvent = itemView.findViewById(R.id.UpcomingEvent)
//            lbleventDate = itemView.findViewById(R.id.lbleventDate)
//            lblCreateTimeevent = itemView.findViewById(R.id.lblCreateTimeevent)
//            lblEventtopic = itemView.findViewById(R.id.lblEventtopic)
//
//            //Assignment
//            lblassignmenttopic = itemView.findViewById(R.id.lblassignmenttopic)
//            lblassignmentdescription = itemView.findViewById(R.id.lblassignmentdescription)
//            lblassignmentDate = itemView.findViewById(R.id.lblassignmentDate)
//            lbldate = itemView.findViewById(R.id.lbldate)
//            lblView = itemView.findViewById(R.id.lblView)
//            Assignment = itemView.findViewById(R.id.Assignment)
//            lnrAssignmentAttachment = itemView.findViewById(R.id.lnrAssignmentAttachment)
//
//
//            lblNoiceboardTitle = itemView.findViewById(R.id.lblNoiceboardTitle)
//            rytEmgVoice = itemView.findViewById(R.id.rytEmgVoice)
//            lblNoticeDescription = itemView.findViewById(R.id.lblNoticeDescription)
//            lblCreateTime = itemView.findViewById(R.id.lblCreateTime)
//            lblNotiCreateDate = itemView.findViewById(R.id.lblNotiCreateDate)
//            lblVoicetitle = itemView.findViewById(R.id.lblVoicetitle)
//            lblVoiceTime = itemView.findViewById(R.id.lblVoiceTime)
//            lnrEmergencyVoice = itemView.findViewById(R.id.lnrEmergencyVoice)
//            lblVoicedescription = itemView.findViewById(R.id.lblVoicedescription)
//            imgEmgplaypause = itemView.findViewById(R.id.imgEmgplaypause)
//            lblEmgfromduration = itemView.findViewById(R.id.lblEmgfromduration)
//            lblPostedBy = itemView.findViewById(R.id.lblPostedBy)
//            lblVoiceDate = itemView.findViewById(R.id.lblVoiceDate)
//            lbltotalduration = itemView.findViewById(R.id.lblTotalDuration)
//            emergencyseekbar = itemView.findViewById(R.id.emergencyseekbar)
//            lnrEmgVoice = itemView.findViewById(R.id.lnrEmgVoice)
//            lnrImageView = itemView.findViewById(R.id.lnrImageView)
//            imgAdvertisement = itemView.findViewById(R.id.imgAdvertisement)
//            imgthumb = itemView.findViewById(R.id.imgthumb)
//            LayoutAd = itemView.findViewById(R.id.LayoutAdvertisement)
//            lblCircularTitle = itemView.findViewById(R.id.lblCircularTitle)
//            lblCirculardescription = itemView.findViewById(R.id.lblCirculardescription)
//            lblCicularCreateDate = itemView.findViewById(R.id.lblCicularCreateDate)
//            lblCircularCreateTime = itemView.findViewById(R.id.lblCircularCreateTime)
//            LayoutCicular = itemView.findViewById(R.id.LayoutCicular)
//            lnrCircularAttachment = itemView.findViewById(R.id.lnrCircularAttachment)
//            rytNoticeboard = itemView.findViewById(R.id.rytNoticeboard)
//            lblPath = itemView.findViewById(R.id.lblPath)
//            lnrRecentNotifications = itemView.findViewById(R.id.lnrRecentNotifications)
//            lnrRecentVoice = itemView.findViewById(R.id.lnrRecentVoice)
//            lblRecentDesciption = itemView.findViewById(R.id.lblRecentDesciption)
//            recentSeekbarlayout = itemView.findViewById(R.id.recentSeekbarlayout)
//            lblRecentDate = itemView.findViewById(R.id.lblRecentDate)
//            lblRecentTime = itemView.findViewById(R.id.lblRecentTime)
//            imgArrowdown = itemView.findViewById(R.id.imgArrowdown)
//            imgRecentEmgplaypause = itemView.findViewById(R.id.imgRecentEmgplaypause)
//            recentseekbar = itemView.findViewById(R.id.recentseekbar)
//            lblEmgRecentduration = itemView.findViewById(R.id.lblEmgRecentduration)
//            lblRecentTotalDuration = itemView.findViewById(R.id.lblRecentTotalDuration)
//            lblRecentPostedby = itemView.findViewById(R.id.lblRecentPostedby)
//            lblRecenttitle = itemView.findViewById(R.id.lblRecenttitle)
//            lnrplayvoice = itemView.findViewById(R.id.lnrplayvoice)
//            rytRecentNotification = itemView.findViewById(R.id.rytRecentNotification)
//            imgRecentType = itemView.findViewById(R.id.imgRecentType)
//            lblNoDataFound = itemView.findViewById(R.id.lblNoDataFound)
//            rytSeekbarlayout = itemView.findViewById(R.id.rytSeekbarlayout)
//            constHeader = itemView.findViewById(R.id.constHeader)
//        }
//    }
//
//    companion object {
//        fun milliSecondsToTimer(milliseconds: Long): String {
//            var finalTimerString = ""
//            var secondsString = ""
//            var minutesString = ""
//
//            // Convert total duration into time
//            val hours = (milliseconds / (1000 * 60 * 60)).toInt()
//            val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
//            val seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000).toInt()
//            // Add hours if there
//            if (hours > 0) {
//                finalTimerString = "$hours:"
//            }
//
//            // Prepending 0 to Minutes if it is one digit
//
//            if (minutes < 10) {
//                minutesString = "0$minutes"
//            } else {
//                minutesString = "" + minutes
//            }
//
//            // Prepending 0 to seconds if it is one digit
//
//            if (seconds < 10) {
//                secondsString = "0$seconds"
//            } else {
//                secondsString = "" + seconds
//            }
//            finalTimerString = "$finalTimerString$minutesString:$secondsString"
//
//            // return timer string
//            return finalTimerString
//        }
//    }
//
//    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
//        val formatter = SimpleDateFormat(format, locale)
//        return formatter.format(this)
//    }
//
//    fun getCurrentDateTime(): Date {
//        return Calendar.getInstance().time
//    }
//
//    fun readpdf() {
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        intent.clipData = ClipData.newRawUri("", pdfUri)
//        intent.setDataAndType((pdfUri), "application/pdf")
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        context.startActivity(intent)
//    }
//
//    fun leaveRejectorAccept(processtype: String, Leaveid: String) {
//
//        val jsonObject = JsonObject()
//
//        jsonObject.addProperty("leaveid", Leaveid)
//        jsonObject.addProperty("userid", CommonUtil.MemberId?.toString()?:"")
//        jsonObject.addProperty("processtype", processtype)
//        Log.d("jsonoblect", jsonObject.toString())
//
//        RestClient.apiInterfaces.Leave_Reject(jsonObject)
//            ?.enqueue(object : Callback<Delete_noticeboard?> {
//                override fun onResponse(
//                    call: Call<Delete_noticeboard?>,
//                    response: Response<Delete_noticeboard?>
//                ) {
//                    if (response.code() == 200 || response.code() == 201) {
//                        if (response.body() != null) {
//                            val response = response.body()!!.Message
//                            Log.d("message", response)
//
//
//                            val dlg = context.let { AlertDialog.Builder(it) }
//                            dlg.setTitle("Info")
//                            dlg.setMessage(response)
//                            dlg.setPositiveButton("OK") { dialog, which ->
//
//                                val i: Intent =
//                                    Intent(context, Attendance::class.java)
//                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                                context.startActivity(i)
//
//                            }
//
//                            dlg.setCancelable(false)
//                            dlg.create()
//                            dlg.show()
//
//
//                        }
//
//                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
//
//                    }
//                }
//
//                override fun onFailure(call: Call<Delete_noticeboard?>, t: Throwable) {
//
//                }
//
//            })
//    }
//
//}
//
