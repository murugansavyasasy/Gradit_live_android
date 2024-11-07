package com.vsca.vsnapvoicecollege.Adapters

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.*
import com.vsca.vsnapvoicecollege.Model.DashboardSubItems
import com.vsca.vsnapvoicecollege.Model.Delete_noticeboard
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.DownloadVoice
import com.vsca.vsnapvoicecollege.albumImage.PDF_Reader
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


class DashboardChild(
    private val newsModalArrayList: ArrayList<DashboardSubItems>,
    private val context: Context,
    private val type: String
) : RecyclerView.Adapter<DashboardChild.ViewHolder>() {

    private var mExpandedPosition = -1
    var msgcontent: String? = null
    var path: String? = null
    var filetype: String? = null
    var detailsid: String? = null
    var PlayPath: String? = null
    var mediaPlayer: MediaPlayer? = MediaPlayer()
    var mediaFileLengthInMilliseconds = 0
    var handler = Handler()
    var iMediaDuration = 0
    var Position: Int = 0
    var isemergencyExpanded: Boolean? = null
    private val VOICE_FOLDER: String = "Gradit/Voice/"
    private lateinit var pdfUri: Uri


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.dashboard_list_design, parent, false)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val modal = newsModalArrayList[position]
        Position = position
        Log.d("Dashboardtype", type)

        if ((type == "Circular")) {


            holder.lnrImageView.visibility = GONE
            holder.LayoutAd.visibility = GONE
            holder.LayoutCicular.visibility = VISIBLE
            holder.lnrEmgVoice.visibility = GONE

            if (position % 2 == 0) {
                holder.LayoutCicular.setBackgroundResource(R.drawable.bg_dashboard_circular_grey)
            } else {
                holder.LayoutCicular.setBackgroundResource(R.drawable.bg_dashboard_cicular)
            }

            holder.lblCircularTitle.text = modal.menuTitle
            holder.lblCirculardescription.text = modal.menuDescription
            holder.lblCircularCreateTime.text = modal.createTime
            holder.lblCicularCreateDate.text = modal.createDate

            holder.lblPath.text = context.getString(R.string.txt_attachment)

            val menuid = BaseActivity.CircularMenuID
            CommonUtil.MenuIDCircular = menuid

            holder.LayoutCicular.setOnClickListener {
                val i: Intent = Intent(context, Circular::class.java)
                context.startActivity(i)
            }

            holder.lnrCircularAttachment.setOnClickListener {

                CommonUtil.Multipleiamge.clear()

                if (modal.FilepathList.size == 0) {
                    CommonUtil.ApiAlertContext(context, "file is empty")
                } else if (modal.FilepathList.size == 1) {
                    var Imagefileurl: String? = null
                    for (k in modal.FilepathList.indices) {
                        Imagefileurl = modal.FilepathList.get(k)
                    }
                    if (Imagefileurl!!.contains("pdf")) {
                        pdfUri = Uri.parse(Imagefileurl)
                        val i: Intent = Intent(context, PDF_Reader::class.java)
                        i.putExtra("PdfView", pdfUri.toString())
                        context.startActivity(i)
                    } else {
                        val i: Intent = Intent(context, ViewFiles::class.java)
                        i.putExtra("images", Imagefileurl)
                        context.startActivity(i)

                    }

                } else {
                    for (k in modal.FilepathList.indices) {
                        CommonUtil.Multipleiamge.add(modal.FilepathList.get(k))
                    }
                    val i: Intent = Intent(context, Assignment_MultipleFileView::class.java)
                    context.startActivity(i)
                }
            }

        } else if ((type == "Upcoming Events")) {

            holder.UpcomingEvent.visibility = VISIBLE
            if (position % 2 == 0) {
                holder.UpcomingEvent.setBackgroundResource(R.drawable.bg_dashboard_circular_grey)
            } else {
                holder.UpcomingEvent.setBackgroundResource(R.drawable.bg_dashboard_cicular)
            }

            holder.lblEventtopic.text = modal.Eventtime
            holder.lbleventDate.text = modal.EventTitle

            val time = modal.Eventdate
            val result = LocalTime.parse(time).format(DateTimeFormatter.ofPattern("h:mm a"))


            holder.lblCreateTimeevent.text = result

            val menuid = BaseActivity.EventsMenuID
            CommonUtil.MenuIDEvents = menuid
            holder.UpcomingEvent.setOnClickListener {
                val i: Intent = Intent(context, Events::class.java)
                context.startActivity(i)
            }
        } else if ((type == "Chat")) {

            if (modal.message != null) {

                holder.lnrNoticeboardd.visibility = GONE

            } else {

                holder.lnrNoticeboardd.visibility = VISIBLE

                holder.lblNoticeboardTitle.text = modal.studentname
                holder.lblNoticeboardDate.text = modal.question


                val filename: String = modal.createdonchat.toString()
                val file: Array<String> = filename.split(" ".toRegex()).toTypedArray()
                val Stringone: String = file.get(0)
                val StringTwo: String = file.get(1)
                val StringThree: String = file.get(2)

                holder.lblchatDate.text = Stringone
                holder.lblCreateTimechat.text = StringTwo + " " + StringThree


                val menuid = BaseActivity.ChatMenuID
                CommonUtil.MenuIDChat = menuid
                holder.imgarrowchat.setOnClickListener {
                    val i: Intent = Intent(context, ChatParent::class.java)
                    context.startActivity(i)
                }
            }

        } else if ((type == "Leave Request")) {

            if (modal.message != null) {

                holder.lnrNoticeboardd.visibility = GONE

            } else {

                holder.Leave_Request_dashboard.visibility = VISIBLE

                if (position % 2 == 0) {
                    holder.lnrNoticeboard.setBackgroundResource(R.drawable.bg_dashboard_circular_grey)
                } else {
                    holder.lnrNoticeboard.setBackgroundResource(R.drawable.bg_dashboard_cicular)
                }

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


                val menuid = BaseActivity.AttendanceMeuID
                CommonUtil.MenuIdAttendance = menuid
                holder.Leave_Request_dashboard.setOnClickListener {

                    holder.rytLeaveDescription.visibility = VISIBLE

                }
            }

            holder.lblApproval.setOnClickListener {

                val dlg = context.let { AlertDialog.Builder(it) }
                dlg.setTitle("Approve Leave ")
                dlg.setMessage("Once done can't be changed")
                dlg.setPositiveButton("OK") { dialog, which ->

                    leaveRejectorAccept("1", modal.leaveapplicationid.toString())
                }

                dlg.setCancelable(false)
                dlg.create()
                dlg.show()


            }

            holder.lblRejaect.setOnClickListener {

                val dlg = context.let { AlertDialog.Builder(it) }
                dlg.setTitle("Reject Leave")
                dlg.setMessage("Once done can't be changed")
                dlg.setPositiveButton("OK") { dialog, which ->

                    leaveRejectorAccept("0", modal.leaveapplicationid.toString())

                }

                dlg.setCancelable(false)
                dlg.create()
                dlg.show()
            }

        } else if ((type == "Assignments")) {

            holder.Assignment.visibility = VISIBLE
            if (position % 2 == 0) {
                holder.Assignment.setBackgroundResource(R.drawable.bg_dashboard_circular_grey)
            } else {
                holder.Assignment.setBackgroundResource(R.drawable.bg_dashboard_cicular)
            }

            var Imagefileurl: String? = null
            for (k in modal.FilepathListAssignment.indices) {
                Imagefileurl = modal.FilepathListAssignment.get(k)
            }

            if (modal.FilepathListAssignment.size == 0 || Imagefileurl.equals("") || Imagefileurl == null) {

                holder.lnrAssignmentAttachment.visibility = GONE

            } else {

                holder.lnrAssignmentAttachment.visibility = VISIBLE

            }

            if (modal.assignmentfiletype.equals("pdf")) {
                holder.lnrAssignmentAttachment.visibility = View.VISIBLE
            } else if (modal.assignmentfiletype.equals("text")) {
                holder.lnrAssignmentAttachment!!.visibility = View.GONE
            } else {
                holder.lnrAssignmentAttachment!!.visibility = View.VISIBLE
            }

            holder.lnrAssignmentAttachment.setOnClickListener {

                if (modal.FilepathListAssignment.size == 0) {


                } else if (modal.FilepathListAssignment.size == 1) {

                    var Imagefileurl: String? = null
                    for (k in modal.FilepathListAssignment.indices) {
                        Imagefileurl = modal.FilepathListAssignment[k]
                    }

                    if (modal.assignmentfiletype.equals("pdf")) {
                        holder.lnrAssignmentAttachment!!.visibility = View.VISIBLE
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.clipData = ClipData.newRawUri("", Uri.parse(Imagefileurl))
                        intent.setDataAndType((Uri.parse(Imagefileurl)), "application/pdf")
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        context.startActivity(intent)

                    } else if (modal.assignmentfiletype.equals("text")) {
                        holder.lnrAssignmentAttachment!!.visibility = View.GONE
                    } else {
                        holder.lnrAssignmentAttachment!!.visibility = View.VISIBLE
                        val i: Intent = Intent(context, ViewFiles::class.java)
                        i.putExtra("images", Imagefileurl)
                        context.startActivity(i)
                    }

                } else {

                    holder.lnrAssignmentAttachment.visibility = VISIBLE

                    for (k in modal.FilepathListAssignment.indices) {
                        CommonUtil.Multipleiamge.add(modal.FilepathListAssignment.get(k))
                    }

                    val i: Intent = Intent(context, Assignment_MultipleFileView::class.java)
                    context.startActivity(i)

                }
            }


            val date = getCurrentDateTime()
            val dateInString = date.toString("yyyy/MM/dd")
            holder.lblassignmenttopic.text = modal.assignmenttopic
            holder.lblassignmentdescription.text = modal.assignmentdescription
            holder.lblassignmentDate.text = dateInString
            holder.lbldate.text = modal.submissiondate

            val menuid = BaseActivity.AssignmentMenuID
            CommonUtil.MenuIDEvents = menuid
            holder.Assignment.setOnClickListener({
                val i: Intent = Intent(context, Assignment::class.java)
                context.startActivity(i)
            })

        } else if ((type == "Notice Board")) {

            holder.lnrImageView.visibility = VISIBLE
            holder.LayoutAd.visibility = GONE
            holder.LayoutCicular.visibility = GONE
            holder.lnrEmgVoice.visibility = GONE
            holder.lblNoiceboardTitle.text = modal.menuTitle
            holder.lblNoticeDescription.text = modal.menuDescription
            holder.lblCreateTime.text = modal.createTime
            holder.lblNotiCreateDate.text = modal.createDate
            if (position % 2 == 0) {
                holder.rytNoticeboard.setBackgroundResource(R.drawable.noticeboard_blue)
            } else {
                holder.rytNoticeboard.setBackgroundResource(R.drawable.noticeboard_yellow)
            }

            var menuid = BaseActivity.NoticeboardMenuID
            CommonUtil.MenuIDNoticeboard = menuid
            holder.lnrImageView.setOnClickListener({
                val i: Intent = Intent(context, Noticeboard::class.java)
                context.startActivity(i)
            })

        } else if ((type == "Ad")) {
            holder.lnrImageView.visibility = GONE
            holder.LayoutAd.visibility = VISIBLE
            holder.LayoutCicular.visibility = GONE
            holder.lnrEmgVoice.visibility = GONE

            Glide.with(context).load(modal.adBaackgroundImage).into(holder.imgAdvertisement)
            Glide.with(context).load(modal.addImage).into(holder.imgthumb)

            holder.LayoutAd.setOnClickListener({

                BaseActivity.LoadWebViewContext(context, modal.Addurl)

            })

        } else if ((type == "Attendance")) {

            if (modal.message != null) {

                holder.lnrattendance.visibility = GONE

            } else {

                holder.lnrattendance.visibility = VISIBLE
                holder.lblattendancestatusDate.text = modal.AttendanceDate
                holder.lblsubjectnameAttendance.text = modal.SubjectName
                holder.lblattendancestatus.text = modal.AttendanceType

                if (modal.AttendanceType.equals("Absent")) {

                    holder.lblattendancestatus.setBackgroundResource(R.drawable.bg_redcolor)

                } else {

                    holder.lblattendancestatus.setBackgroundResource(R.drawable.bg_available_selected_green)

                }

                val menuid = BaseActivity.AttendanceMeuID
                CommonUtil.MenuIdAttendance = menuid
                holder.lnrImageView.setOnClickListener({
                    val i: Intent = Intent(context, Attendance::class.java)
                    context.startActivity(i)
                })
            }

        } else if ((type == "Emergency Notification")) {
            holder.lnrImageView.visibility = GONE
            holder.LayoutAd.visibility = GONE
            holder.LayoutCicular.visibility = GONE
            holder.lnrEmgVoice.visibility = VISIBLE

            isemergencyExpanded = position == mExpandedPosition

            holder.lnrEmergencyVoice.visibility = if (isemergencyExpanded!!) VISIBLE else GONE
            holder.lnrEmgVoice.isActivated = isemergencyExpanded!!
            holder.lnrEmgVoice.visibility = VISIBLE

            val voiceduration = modal.duration
            val minutes = (voiceduration % 3600) / 60
            val seconds = voiceduration % 60
            val timeString = String.format("%02d:%02d", minutes, seconds)
            holder.lbltotalduration.text = timeString
            holder.lblVoicetitle.text = modal.menuTitle
            holder.lblPostedBy.text = modal.membername
            val value = modal.createdon
            val splitDate = value!!.split("\\s+".toRegex()).toTypedArray()
            val Date = splitDate[0]
            holder.lblVoiceDate.text = Date
            val time = splitDate[1]
            holder.lblVoiceTime.text = time

            holder.rytEmgVoice.setOnClickListener(object : OnClickListener {
                override fun onClick(view: View) {
                    mExpandedPosition = if (isemergencyExpanded!!) -1 else position
                    notifyDataSetChanged()
                    holder.rytSeekbarlayout.visibility = VISIBLE
                    CommonUtil.DownloadingFileDashboard = 0

                    msgcontent = modal.voiceFilepath
                    path = modal.voiceFilepath
                    Log.d("filepath", path.toString())
                    var filename: String = modal.MsgId!! + "_" + "Gradit.mp3"
                    path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
                    } else {
                        Environment.getExternalStorageDirectory().path
                    }
                    val dir = File(path, VOICE_FOLDER)
                    val file = File(dir, filename)

                    if (file.exists()) {

                        holder.rytSeekbarlayout.visibility = VISIBLE
                        CommonUtil.CommunicationisExpandAdapter = false

                        SetUpAudioPlayer(holder)
                        fetchPathUrl(modal)

                        mExpandedPosition = if (isemergencyExpanded!!) -1 else position
                        notifyDataSetChanged()

                        Log.d("FileExist", "exist")
                        mediaPlayer!!.setOnCompletionListener(OnCompletionListener {
                            holder.imgEmgplaypause.setImageResource(R.drawable.ic_play)

                            Log.d("SeekCompleteion", "stop")
                            mediaPlayer!!.seekTo(0)
                        })

                        holder.emergencyseekbar.setOnSeekBarChangeListener(object :
                            OnSeekBarChangeListener {
                            override fun onStopTrackingTouch(seekBar: SeekBar) {}
                            override fun onStartTrackingTouch(seekBar: SeekBar) {}
                            override fun onProgressChanged(
                                seekBar: SeekBar, progress: Int, fromUser: Boolean
                            ) {

                            }
                        })

                        Log.d("FetchSong", "END***************************************")
                        holder.imgEmgplaypause.setOnClickListener(object : OnClickListener {
                            override fun onClick(view: View) {
                                mediaFileLengthInMilliseconds =
                                    mediaPlayer!!.duration // gets the song length in milliseconds from URL


                                if (!mediaPlayer!!.isPlaying) {
                                    mediaPlayer!!.start()
                                    holder.imgEmgplaypause.setImageResource(R.drawable.ic_pause)
                                } else {
                                    mediaPlayer!!.pause()
                                    holder.imgEmgplaypause.setImageResource(R.drawable.ic_play)
                                }
                                primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds)
                            }

                            private fun primarySeekBarProgressUpdater(fileLength: Int) {
                                val iProgress =
                                    ((mediaPlayer!!.currentPosition.toFloat() / fileLength) * 100).toInt()
                                holder.emergencyseekbar.progress = iProgress
                                if (mediaPlayer!!.isPlaying) {
                                    val notification: Runnable = object : Runnable {
                                        override fun run() {
                                            holder.lblEmgfromduration.text = milliSecondsToTimer(
                                                mediaPlayer!!.currentPosition.toLong()
                                            )
                                            primarySeekBarProgressUpdater(fileLength)
                                        }
                                    }
                                    handler.postDelayed(notification, 1000)
                                }
                            }
                        })

                    } else {

                        Log.d("FileExist", "notexist")

                        holder.imgEmgplaypause.setOnClickListener(object : OnClickListener {

                            override fun onClick(view: View) {


                                Log.d("isDownload", CommonUtil.DownloadingFileDashboard.toString())
                                if (CommonUtil.DownloadingFileDashboard != 1 && CommonUtil.DownloadingFileDashboard != 2) {
                                    Log.d(
                                        "isDownload",
                                        CommonUtil.DownloadingFileDashboard.toString()
                                    )

                                    DownloadVoice.downloadSampleFile(
                                        context,
                                        modal.voiceFilepath!!,
                                        VOICE_FOLDER,
                                        filename,
                                        holder,
                                        true
                                    )

                                } else {

                                    if (CommonUtil.DownloadingFileDashboard != 2) {
                                        SetUpAudioPlayer(holder)
                                        fetchPathUrl(modal)

                                    }

                                    CommonUtil.DownloadingFileDashboard = 2

                                    mediaFileLengthInMilliseconds = mediaPlayer!!.duration

                                    if (!mediaPlayer!!.isPlaying) {
                                        mediaPlayer!!.start()
                                        holder.imgEmgplaypause.setImageResource(R.drawable.ic_pause)
                                    } else {
                                        mediaPlayer!!.pause()
                                        holder.imgEmgplaypause.setImageResource(R.drawable.ic_play)
                                    }
                                }

                                primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds)

                            }

                            private fun primarySeekBarProgressUpdater(fileLength: Int) {
                                val iProgress =
                                    ((mediaPlayer!!.currentPosition.toFloat() / fileLength) * 100).toInt()
                                holder.emergencyseekbar.progress = iProgress
                                if (mediaPlayer!!.isPlaying) {
                                    val notification: Runnable = object : Runnable {
                                        override fun run() {

                                            holder.lblEmgfromduration.text =
                                                milliSecondsToTimer(mediaPlayer!!.currentPosition.toLong())
                                            primarySeekBarProgressUpdater(fileLength)
                                        }
                                    }
                                    handler.postDelayed(notification, 1000)
                                }
                            }
                        })
                    }

                    holder.emergencyseekbar.setOnSeekBarChangeListener(object :
                        OnSeekBarChangeListener {
                        override fun onStopTrackingTouch(seekBar: SeekBar) {}
                        override fun onStartTrackingTouch(seekBar: SeekBar) {}
                        override fun onProgressChanged(
                            seekBar: SeekBar, progress: Int, fromUser: Boolean
                        ) {


                        }
                    })

                    mediaPlayer!!.setOnCompletionListener {
                        holder.imgEmgplaypause.setImageResource(R.drawable.ic_play)
                        mediaPlayer!!.seekTo(0)
                    }
                }
            })

        } else if ((type == "Recent Notifications")) {

            holder.lnrImageView.visibility = GONE
            holder.LayoutAd.visibility = GONE
            holder.LayoutCicular.visibility = GONE
            holder.lnrEmgVoice.visibility = GONE
            holder.lnrRecentNotifications.visibility = VISIBLE

            val isExpanded = position == mExpandedPosition
            holder.lnrRecentVoice.visibility = if (isExpanded) VISIBLE else GONE
            holder.lnrRecentNotifications.isActivated = isExpanded
            holder.lnrRecentNotifications.visibility = VISIBLE
            holder.lblRecenttitle.text = modal.Content

            if (modal.RecentType!!.equals("Voice")) {
                holder.lnrplayvoice.visibility = VISIBLE
                holder.imgRecentType.setImageResource(R.drawable.dashboard_recent_voice)
                holder.imgRecentType.alpha = 0.7f
            }
            if (modal.RecentType!!.equals("Emergencyvoice Message")) {
                holder.lnrplayvoice.visibility = VISIBLE
                holder.imgRecentType.setImageResource(R.drawable.emergency_voice)
                holder.imgRecentType.alpha = 0.7f
            }
            if (modal.RecentType!!.equals("Text Message")) {
                holder.lnrplayvoice.visibility = GONE
                holder.imgRecentType.setImageResource(R.drawable.dashboard_text)
                holder.imgRecentType.alpha = 0.7f
                holder.lblRecentDesciption.text = modal.Content
            }
            val voiceduration = modal.duration
            val minutes = (voiceduration % 3600) / 60
            val seconds = voiceduration % 60
            val timeString = String.format("%02d:%02d", minutes, seconds)
            holder.lblRecentTotalDuration.text = timeString
            holder.lblRecentPostedby.text = modal.membername
            holder.lblRecentDate.text = modal.createdon
            holder.lblRecentTime.text = modal.createTime

            if (isExpanded) {
                holder.imgArrowdown.setImageResource(R.drawable.ic_arrow_up_blue)
                holder.lnrplayvoice.visibility = GONE
                mediaPlayer!!.seekTo(0)


            } else {

                if (modal.RecentType!!.equals("Voice")) {
                    holder.lnrplayvoice.visibility = VISIBLE
                }
                if (modal.RecentType!!.equals("Emergencyvoice Message")) {
                    holder.lnrplayvoice.visibility = VISIBLE
                }
                if (modal.RecentType!!.equals("Text Message")) {
                    holder.lnrplayvoice.visibility = GONE
                }
                holder.imgArrowdown.setImageResource(R.drawable.ic_arrow_down_blue)
            }

            holder.rytRecentNotification.setOnClickListener(object : OnClickListener {
                override fun onClick(view: View) {
                    holder.lnrplayvoice.visibility = GONE
                    CommonUtil.DownloadingFileDashboard = 0
                    if (modal.RecentType!!.equals("Text")) {
                        holder.lblRecentDesciption.visibility = VISIBLE
                        holder.lblRecenttitle.visibility = VISIBLE
                        holder.lblRecentDesciption.text = modal.menuDescription
                        holder.lblRecenttitle.text = modal.Content
                        holder.recentSeekbarlayout.visibility = GONE
                        mExpandedPosition = if (isExpanded) -1 else position
                        notifyDataSetChanged()

                    } else {

                        mExpandedPosition = if (isExpanded) -1 else position
                        notifyDataSetChanged()

                        msgcontent = modal.Content
                        Log.d("msgcontent", msgcontent!!)

                        path = modal.Content
                        Log.d("Voice_Path", path.toString())

                        Log.d("Recentpath", path!!)

                        val filename: String = modal.MsgId!! + "_" + "Gradit.mp3"
                        path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
                        } else {
                            Environment.getExternalStorageDirectory().path
                        }

                        val dir = File(path, VOICE_FOLDER)
                        val file = File(dir, filename)

                        if (file.exists()) {

                            Log.d("FileExist", "existyes")

                            holder.recentSeekbarlayout.visibility = VISIBLE

                            CommonUtil.CommunicationisExpandAdapter = false

                            SetUpRecentAudioPlayer(holder)
                            fetchRecentPathUrl(modal)

                            holder.imgRecentEmgplaypause.setOnClickListener(object :
                                OnClickListener {
                                override fun onClick(view: View) {

                                    mediaFileLengthInMilliseconds =
                                        mediaPlayer!!.duration  // gets the song length in milliseconds from URL
                                    if (!mediaPlayer!!.isPlaying) {
                                        mediaPlayer!!.start()
                                        holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_pause)

                                    } else {

                                        mediaPlayer!!.pause()
                                        holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)

                                    }
                                    primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds)
                                }

                                private fun primarySeekBarProgressUpdater(fileLength: Int) {
                                    val iProgress =
                                        ((mediaPlayer!!.currentPosition.toFloat() / fileLength) * 100).toInt()
                                    holder.recentseekbar.progress = iProgress
                                    if (mediaPlayer!!.isPlaying) {
                                        val notification: Runnable = object : Runnable {
                                            override fun run() {
                                                holder.lblEmgRecentduration.text =
                                                    milliSecondsToTimer(mediaPlayer!!.currentPosition.toLong())
                                                primarySeekBarProgressUpdater(fileLength)
                                            }
                                        }
                                        handler.postDelayed(notification, 1000)
                                    }
                                }
                            })

                            holder.recentseekbar.setOnSeekBarChangeListener(object :
                                OnSeekBarChangeListener {
                                override fun onStopTrackingTouch(seekBar: SeekBar) {}
                                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                                override fun onProgressChanged(
                                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                                ) {

                                }
                            })

                            mediaPlayer!!.setOnCompletionListener {
                                holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
                                mediaPlayer!!.seekTo(0)
                            }

                        } else {

                            Log.d("FileExist", "Not_exists")

                            holder.recentSeekbarlayout.visibility = VISIBLE


                            holder.imgRecentEmgplaypause.setOnClickListener(object :
                                OnClickListener {
                                override fun onClick(view: View) {

                                    if (CommonUtil.DownloadingFileDashboard != 1 && CommonUtil.DownloadingFileDashboard != 2) {
                                        DownloadVoice.downloadSampleFile(
                                            context, modal.Content!!, VOICE_FOLDER,
                                            filename,
                                            holder,
                                            false
                                        )

                                    } else {

                                        if (CommonUtil.DownloadingFileDashboard != 2) {
                                            SetUpRecentAudioPlayer(holder)
                                            fetchRecentPathUrl(modal)

                                        }

                                        CommonUtil.DownloadingFileDashboard = 2

                                        mediaFileLengthInMilliseconds = mediaPlayer!!.duration

                                        if (!mediaPlayer!!.isPlaying) {
                                            mediaPlayer!!.start()
                                            holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_pause)
                                        } else {
                                            mediaPlayer!!.pause()
                                            holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
                                        }
                                    }

                                    primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds)

                                }


                                private fun primarySeekBarProgressUpdater(fileLength: Int) {
                                    val iProgress =
                                        ((mediaPlayer!!.currentPosition.toFloat() / fileLength) * 100).toInt()
                                    holder.recentseekbar.progress = iProgress
                                    if (mediaPlayer!!.isPlaying) {
                                        val notification: Runnable = object : Runnable {
                                            override fun run() {
                                                holder.lblEmgRecentduration.text =
                                                    milliSecondsToTimer(
                                                        mediaPlayer!!.currentPosition.toLong()
                                                    )
                                                primarySeekBarProgressUpdater(fileLength)
                                            }
                                        }
                                        handler.postDelayed(notification, 1000)
                                    }
                                }
                            })

                            holder.recentseekbar.setOnSeekBarChangeListener(object :
                                OnSeekBarChangeListener {
                                override fun onStopTrackingTouch(seekBar: SeekBar) {}
                                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                                override fun onProgressChanged(
                                    seekBar: SeekBar, progress: Int, fromUser: Boolean
                                ) {

                                }
                            })
                            mediaPlayer!!.setOnCompletionListener {
                                holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
                                mediaPlayer!!.seekTo(0)
                            }
                        }
                    }
                }
            })

        } else {
            holder.lnrImageView.visibility = GONE
            holder.LayoutAd.visibility = GONE
            holder.LayoutCicular.visibility = GONE
            holder.lnrEmgVoice.visibility = GONE
        }
    }

    private fun SetUpRecentAudioPlayer(holder: ViewHolder) {

        holder.recentseekbar.max = 99
        holder.recentseekbar.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (v.id == R.id.recentseekbar) {
                    run {
                        val sb: SeekBar = v as SeekBar
                        val playPositionInMillisecconds: Int =
                            (mediaFileLengthInMilliseconds / 100) * sb.progress
                        mediaPlayer!!.seekTo(playPositionInMillisecconds)
                    }
                }
                return false
            }
        })
    }

    private fun fetchRecentPathUrl(modal: DashboardSubItems) {
        Log.d("PlayStart", "Start***************************************")
        try {
            var filename: String = modal.MsgId!! + "_" + "Gradit.mp3"
            val path: String
            path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
            } else {
                Environment.getExternalStorageDirectory().path
            }

            val dir = File(path, VOICE_FOLDER)
            val file = File(dir, filename)
            PlayPath = file.path
            Log.d("PlayPath", PlayPath!!)
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(PlayPath)
            mediaPlayer!!.prepare()
            iMediaDuration = (mediaPlayer!!.duration / 1000.0).toInt()
            Log.d("iMediaDuration", iMediaDuration.toString())

        } catch (e: Exception) {
            Log.d("ExceptionWhilePlaying", e.toString())
        }
        Log.d("PlayEnd", "END***************************************")
    }

    private fun fetchPathUrl(modal: DashboardSubItems) {
        Log.d("PlayStart", "Start***************************************123")
        try {
            var filename: String = modal.MsgId!! + "_" + "Gradit.mp3"
            val path: String
            path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
            } else {
                Environment.getExternalStorageDirectory().path
            }

            val dir = File(path, VOICE_FOLDER)
            val file = File(dir, filename)
            PlayPath = file.path
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(PlayPath)
            mediaPlayer!!.prepare()
            iMediaDuration = (mediaPlayer!!.duration / 1000.0).toInt()
        } catch (e: Exception) {
            Log.d("ExceptionWhilePlaying", e.toString())
        }
        Log.d("PlayEnd", "END***************************************")
    }

    private fun SetUpAudioPlayer(holder: ViewHolder) {
        holder.emergencyseekbar.max = 99
        holder.emergencyseekbar.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (v.id == R.id.emergencyseekbar) {
                    run {
                        val sb: SeekBar = v as SeekBar
                        val playPositionInMillisecconds: Int =
                            (mediaFileLengthInMilliseconds / 100) * sb.progress
                        mediaPlayer!!.seekTo(playPositionInMillisecconds)
                    }
                }
                return false
            }
        })
    }

    override fun getItemCount(): Int {
        return newsModalArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lblNoiceboardTitle: TextView
        val lblNoticeDescription: TextView
        val lblCreateTime: TextView
        val lblNotiCreateDate: TextView
        val lblCircularTitle: TextView
        val lblCirculardescription: TextView
        val lblCicularCreateDate: TextView
        val lblCircularCreateTime: TextView
        val lblVoicetitle: TextView
        val lblPostedBy: TextView
        val lbltotalduration: TextView
        val lblVoiceTime: TextView
        val lblVoiceDate: TextView
        val lblEmgfromduration: TextView
        val lnrImageView: RelativeLayout
        val rytEmgVoice: RelativeLayout
        var imgAdvertisement: ImageView
        var imgthumb: ImageView
        var imgEmgplaypause: ImageView
        var lnrRecentVoice: LinearLayout
        var LayoutAd: ConstraintLayout
        var LayoutCicular: ConstraintLayout
        var rytNoticeboard: RelativeLayout
        var lnrEmgVoice: LinearLayout
        var lnrEmergencyVoice: LinearLayout
        var lblVoicedescription: TextView
        var lnrCircularAttachment: LinearLayout
        var emergencyseekbar: SeekBar
        var lblPath: TextView
        var lblRecentDesciption: TextView
        var lnrRecentNotifications: LinearLayout
        var recentSeekbarlayout: RelativeLayout
        var lblRecentDate: TextView
        var lblRecentTime: TextView
        var imgArrowdown: ImageView
        var imgRecentEmgplaypause: ImageView
        var recentseekbar: SeekBar
        var lblEmgRecentduration: TextView
        var lblRecentTotalDuration: TextView
        var lblRecentPostedby: TextView
        var lblRecenttitle: TextView
        var lblNoDataFound: TextView
        var lnrplayvoice: LinearLayout
        var rytRecentNotification: RelativeLayout
        var imgRecentType: ImageView
        var rytSeekbarlayout: RelativeLayout
        var lblEventtopic: TextView
        var lblCreateTimeevent: TextView
        var lbleventDate: TextView
        var UpcomingEvent: ConstraintLayout
        var lblassignmenttopic: TextView
        var lblassignmentdescription: TextView
        var lblassignmentDate: TextView
        var lbldate: TextView
        var lblView: TextView
        var Assignment: ConstraintLayout
        var lnrAssignmentAttachment: LinearLayout


        //chat
        var lblNoticeboardTitle: TextView
        var lblNoticeboardDate: TextView
        var lblchatDate: TextView
        var lblCreateTimechat: TextView
        var lnrNoticeboardd: RelativeLayout
        var imgarrowchat: ImageView


        //Leave Request
        var lblLeaveCreatedDate: TextView
        var lblleaveStatus: TextView
        var lblLeaveType: TextView
        var lblLeaveNoOfDays: TextView
        var department: TextView
        var departmentname: TextView
        var year: TextView
        var section: TextView
        var lblFromDate: TextView
        var lblToDate: TextView
        var Leave_Request_dashboard: RelativeLayout
        var lnrNoticeboard: LinearLayout
        lateinit var rytLeaveDescription: RelativeLayout
        lateinit var lblLeaveReason: TextView
        lateinit var lblRejaect: TextView
        lateinit var lblApproval: TextView

        //Attendance
        var lblattendancestatusDate: TextView

        //  var lblattendance: TextView
        var lblsubjectnameAttendance: TextView
        var lblattendancestatus: TextView
        var lnrattendance: RelativeLayout

        init {

            //Attendance
            lnrattendance = itemView.findViewById(R.id.lnrattendance)
            //  lblattendance = itemView.findViewById(R.id.lblattendance)
            lblsubjectnameAttendance = itemView.findViewById(R.id.lblsubjectnameAttendance)
            lblattendancestatusDate = itemView.findViewById(R.id.lblattendancestatusDate)
            lblattendancestatus = itemView.findViewById(R.id.lblattendancestatus)

            //Leave Request
            rytLeaveDescription = itemView.findViewById(R.id.rytLeaveDescription)
            lblLeaveReason = itemView.findViewById(R.id.lblLeaveReason)
            lblRejaect = itemView.findViewById(R.id.lblRejaect)
            lblApproval = itemView.findViewById(R.id.lblApproval)


            Leave_Request_dashboard = itemView.findViewById(R.id.Leave_Request_dashboard)
            lblLeaveCreatedDate = itemView.findViewById(R.id.lblLeaveCreatedDate)
            lblleaveStatus = itemView.findViewById(R.id.lblleaveStatus)
            lblLeaveType = itemView.findViewById(R.id.lblLeaveType)
            lblLeaveNoOfDays = itemView.findViewById(R.id.lblLeaveNoOfDays)
            department = itemView.findViewById(R.id.department)
            departmentname = itemView.findViewById(R.id.departmentname)
            year = itemView.findViewById(R.id.year)
            section = itemView.findViewById(R.id.section)
            lblFromDate = itemView.findViewById(R.id.lblFromDate)
            lblToDate = itemView.findViewById(R.id.lblToDate)
            lnrNoticeboard = itemView.findViewById(R.id.lnrNoticeboard)

            //Chat
            lblNoticeboardTitle = itemView.findViewById(R.id.lblNoticeboardTitle)
            lblNoticeboardDate = itemView.findViewById(R.id.lblNoticeboardDate)
            lblchatDate = itemView.findViewById(R.id.lblchatDate)
            lblCreateTimechat = itemView.findViewById(R.id.lblCreateTimechat)
            lnrNoticeboardd = itemView.findViewById(R.id.lnrNoticeboardd)
            imgarrowchat = itemView.findViewById(R.id.imgarrowchat)

            //Event
            UpcomingEvent = itemView.findViewById(R.id.UpcomingEvent)
            lbleventDate = itemView.findViewById(R.id.lbleventDate)
            lblCreateTimeevent = itemView.findViewById(R.id.lblCreateTimeevent)
            lblEventtopic = itemView.findViewById(R.id.lblEventtopic)

            //Assignment
            lblassignmenttopic = itemView.findViewById(R.id.lblassignmenttopic)
            lblassignmentdescription = itemView.findViewById(R.id.lblassignmentdescription)
            lblassignmentDate = itemView.findViewById(R.id.lblassignmentDate)
            lbldate = itemView.findViewById(R.id.lbldate)
            lblView = itemView.findViewById(R.id.lblView)
            Assignment = itemView.findViewById(R.id.Assignment)
            lnrAssignmentAttachment = itemView.findViewById(R.id.lnrAssignmentAttachment)


            lblNoiceboardTitle = itemView.findViewById(R.id.lblNoiceboardTitle)
            rytEmgVoice = itemView.findViewById(R.id.rytEmgVoice)
            lblNoticeDescription = itemView.findViewById(R.id.lblNoticeDescription)
            lblCreateTime = itemView.findViewById(R.id.lblCreateTime)
            lblNotiCreateDate = itemView.findViewById(R.id.lblNotiCreateDate)
            lblVoicetitle = itemView.findViewById(R.id.lblVoicetitle)
            lblVoiceTime = itemView.findViewById(R.id.lblVoiceTime)
            lnrEmergencyVoice = itemView.findViewById(R.id.lnrEmergencyVoice)
            lblVoicedescription = itemView.findViewById(R.id.lblVoicedescription)
            imgEmgplaypause = itemView.findViewById(R.id.imgEmgplaypause)
            lblEmgfromduration = itemView.findViewById(R.id.lblEmgfromduration)
            lblPostedBy = itemView.findViewById(R.id.lblPostedBy)
            lblVoiceDate = itemView.findViewById(R.id.lblVoiceDate)
            lbltotalduration = itemView.findViewById(R.id.lblTotalDuration)
            emergencyseekbar = itemView.findViewById(R.id.emergencyseekbar)
            lnrEmgVoice = itemView.findViewById(R.id.lnrEmgVoice)
            lnrImageView = itemView.findViewById(R.id.lnrImageView)
            imgAdvertisement = itemView.findViewById(R.id.imgAdvertisement)
            imgthumb = itemView.findViewById(R.id.imgthumb)
            LayoutAd = itemView.findViewById(R.id.LayoutAdvertisement)
            lblCircularTitle = itemView.findViewById(R.id.lblCircularTitle)
            lblCirculardescription = itemView.findViewById(R.id.lblCirculardescription)
            lblCicularCreateDate = itemView.findViewById(R.id.lblCicularCreateDate)
            lblCircularCreateTime = itemView.findViewById(R.id.lblCircularCreateTime)
            LayoutCicular = itemView.findViewById(R.id.LayoutCicular)
            lnrCircularAttachment = itemView.findViewById(R.id.lnrCircularAttachment)
            rytNoticeboard = itemView.findViewById(R.id.rytNoticeboard)
            lblPath = itemView.findViewById(R.id.lblPath)
            lnrRecentNotifications = itemView.findViewById(R.id.lnrRecentNotifications)
            lnrRecentVoice = itemView.findViewById(R.id.lnrRecentVoice)
            lblRecentDesciption = itemView.findViewById(R.id.lblRecentDesciption)
            recentSeekbarlayout = itemView.findViewById(R.id.recentSeekbarlayout)
            lblRecentDate = itemView.findViewById(R.id.lblRecentDate)
            lblRecentTime = itemView.findViewById(R.id.lblRecentTime)
            imgArrowdown = itemView.findViewById(R.id.imgArrowdown)
            imgRecentEmgplaypause = itemView.findViewById(R.id.imgRecentEmgplaypause)
            recentseekbar = itemView.findViewById(R.id.recentseekbar)
            lblEmgRecentduration = itemView.findViewById(R.id.lblEmgRecentduration)
            lblRecentTotalDuration = itemView.findViewById(R.id.lblRecentTotalDuration)
            lblRecentPostedby = itemView.findViewById(R.id.lblRecentPostedby)
            lblRecenttitle = itemView.findViewById(R.id.lblRecenttitle)
            lnrplayvoice = itemView.findViewById(R.id.lnrplayvoice)
            rytRecentNotification = itemView.findViewById(R.id.rytRecentNotification)
            imgRecentType = itemView.findViewById(R.id.imgRecentType)
            lblNoDataFound = itemView.findViewById(R.id.lblNoDataFound)
            rytSeekbarlayout = itemView.findViewById(R.id.rytSeekbarlayout)
        }
    }

    companion object {
        fun milliSecondsToTimer(milliseconds: Long): String {
            var finalTimerString = ""
            var secondsString = ""
            var minutesString = ""

            // Convert total duration into time
            val hours = (milliseconds / (1000 * 60 * 60)).toInt()
            val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
            val seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000).toInt()
            // Add hours if there
            if (hours > 0) {
                finalTimerString = "$hours:"
            }

            // Prepending 0 to Minutes if it is one digit

            if (minutes < 10) {
                minutesString = "0$minutes"
            } else {
                minutesString = "" + minutes
            }

            // Prepending 0 to seconds if it is one digit

            if (seconds < 10) {
                secondsString = "0$seconds"
            } else {
                secondsString = "" + seconds
            }
            finalTimerString = "$finalTimerString$minutesString:$secondsString"

            // return timer string
            return finalTimerString
        }
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun readpdf() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.clipData = ClipData.newRawUri("", pdfUri)
        intent.setDataAndType((pdfUri), "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }

    fun leaveRejectorAccept(processtype: String, Leaveid: String) {

        val jsonObject = JsonObject()

        jsonObject.addProperty("leaveid", Leaveid)
        jsonObject.addProperty("userid", CommonUtil.MemberId)
        jsonObject.addProperty("processtype", processtype)

        Log.d("jsonoblect", jsonObject.toString())

        RestClient.apiInterfaces.Leave_Reject(jsonObject)
            ?.enqueue(object : Callback<Delete_noticeboard?> {
                override fun onResponse(
                    call: Call<Delete_noticeboard?>,
                    response: Response<Delete_noticeboard?>
                ) {
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val response = response.body()!!.Message
                            Log.d("message", response)


                            val dlg = context.let { AlertDialog.Builder(it) }
                            dlg.setTitle("Info")
                            dlg.setMessage(response)
                            dlg.setPositiveButton("OK") { dialog, which ->

                                val i: Intent =
                                    Intent(context, Attendance::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                context.startActivity(i)

                            }

                            dlg.setCancelable(false)
                            dlg.create()
                            dlg.show()


                        }

                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {

                    }
                }

                override fun onFailure(call: Call<Delete_noticeboard?>, t: Throwable) {

                }

            })
    }

}

