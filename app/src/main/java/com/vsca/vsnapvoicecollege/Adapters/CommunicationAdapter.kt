package com.vsca.vsnapvoicecollege.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Interfaces.communicationListener
import com.vsca.vsnapvoicecollege.Model.GetCommunicationDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommunicationVoiceDownload
import java.io.File


class CommunicationAdapter(
    var eventlist: List<GetCommunicationDetails>,
    private var context: Context?, Type: String,
    val Listener: communicationListener
) : RecyclerView.Adapter<CommunicationAdapter.MyViewHolder>() {
    var mExpandedPosition = -1
    var path: String? = null
    var PlayPath: String? = null
    var detailsid: String? = null
    lateinit var contextThis: Context

    var mediaPlayer: MediaPlayer? = MediaPlayer()
    var handler = Handler()
    var iMediaDuration = 0
    var Type = ""
    var mediaFileLengthInMilliseconds = 0
    var ScreenType: String? = null
    private val VOICE_FOLDER: String = "Gradit/Voice/"

    companion object {
        var communicationClick: communicationListener? = null
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        var lnrplayvoice: LinearLayout
        var rytRecentNotification: RelativeLayout
        var imgRecentType: ImageView
        var lnrRecentVoice: LinearLayout
        var lblNew: TextView
        var voiceProgressbar: ProgressBar

        init {
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
            lblNew = itemView.findViewById(R.id.lblNew)
            voiceProgressbar = itemView.findViewById(R.id.voiceProgressbar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.communication_list_design, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int
    ) {
        val modal = eventlist[position]
        communicationClick = Listener
        communicationClick?.oncommunicationClick(holder, modal)
        val isExpanded = position == mExpandedPosition

        holder.lnrRecentVoice.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.lnrRecentNotifications.isActivated = isExpanded
        holder.lnrRecentNotifications.visibility = View.VISIBLE

        Log.d("ScreenType", ScreenType.toString())
        if (ScreenType.equals("Voice")) {
            if (modal.isemergency.equals("false")) {
                holder.lnrplayvoice.visibility = View.VISIBLE
                holder.imgRecentType.setImageResource(R.drawable.dashboard_recent_voice)
                holder.imgRecentType.alpha = 0.7f
                holder.lblRecenttitle.text = modal.description
                val voiceduration = modal.duration
                val minutes = (Integer.parseInt(voiceduration!!) % 3600) / 60
                val seconds = Integer.parseInt(voiceduration) % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)
                holder.lblRecentTotalDuration.text = timeString
                holder.lblRecentPostedby.text = modal.sentby
            }
            if (modal.isemergency.equals("true")) {
                holder.lnrplayvoice.visibility = View.VISIBLE
                holder.imgRecentType.setImageResource(R.drawable.emergency_voice)
                holder.imgRecentType.alpha = 0.8f
                holder.lblRecenttitle.text = modal.description
                val voiceduration = modal.duration
                val minutes = (Integer.parseInt(voiceduration!!) % 3600) / 60
                val seconds = Integer.parseInt(voiceduration) % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)
                holder.lblRecentTotalDuration.text = timeString
                holder.lblRecentPostedby.text = modal.sentby
            }
        } else {
            holder.lnrplayvoice.visibility = View.GONE
            holder.imgRecentType.setImageResource(R.drawable.dashboard_text)
            holder.imgRecentType.alpha = 0.7f
            holder.lblRecenttitle.text = modal.description
            holder.lblRecentPostedby.text = modal.sentby
        }


//        if (modal.typename.equals("Text Message")) {
//            holder.lnrplayvoice.visibility = View.GONE
//            holder.imgRecentType.setImageResource(R.drawable.dashboard_text)
//            holder.imgRecentType.alpha = 0.7f
//            holder.lblRecenttitle.text = modal.msgcontent
//        }


        val createdDateTime: String = modal.timing.toString()
        val firstvalue: Array<String> = createdDateTime.split("-".toRegex()).toTypedArray()
        val createddate: String = firstvalue.get(0)
        holder.lblRecentDate.text = createddate
        val createdTime: String = firstvalue.get(1)
        holder.lblRecentTime.text = createdTime

        if (modal.isappread.equals("0")) {
            holder.lblNew.visibility = View.VISIBLE
        } else {
            holder.lblNew.visibility = View.GONE
        }

        if (isExpanded) {

            holder.imgArrowdown.setImageResource(R.drawable.ic_arrow_up_blue)
            if (ScreenType.equals("Text")) {
                holder.lnrplayvoice.visibility = View.GONE
                //  holder.lblRecentDesciption.visibility = View.GONE
            }
            if (ScreenType.equals("Text")) {
                Type = "Text"
            } else {
                Type = if (modal.isemergency.equals("true")) {
                    "voice"
                } else if (modal.isemergency.equals("false")) {
                    "voice"
                } else {
                    ""
                }
            }

            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
                mediaPlayer!!.seekTo(0)
            }

        } else {

//            if(ScreenType.equals("Text")) {
//                holder.lnrplayvoice.visibility = View.VISIBLE
//            }
            holder.imgArrowdown.setImageResource(R.drawable.ic_arrow_down_blue)
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
                holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
                mediaPlayer!!.seekTo(0)
            }
        }

        holder.rytRecentNotification.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                holder.lnrplayvoice.visibility = View.GONE
                CommonUtil.DownloadingFile = 0

//                Type = if (modal.isemergency.equals("true")) {
//                    "emergency"
//                } else if (modal.isemergency.equals("false")) {
//                    "voice"
//                } else {
//                    ""
//                }

                if (ScreenType.equals("Text")) {
                    Type = "Text"
                } else {
                    Type = if (modal.isemergency.equals("true")) {
                        "voice"
                    } else if (modal.isemergency.equals("false")) {
                        "voice"
                    } else {
                        ""
                    }
                }

                if (modal.isappread.equals("0")) {
                    BaseActivity.AppReadStatusContext(context, Type, modal.msgdetailsid!!)
                    modal.isappread = "1"
                    holder.lblNew.visibility = View.GONE
                }

                if (ScreenType.equals("Text")) {

                    holder.lblRecentDesciption.visibility = View.VISIBLE
                    holder.lblRecentDesciption.text = modal.description
                    holder.recentSeekbarlayout.visibility = View.GONE
                    mExpandedPosition = if (isExpanded) -1 else position
                    notifyDataSetChanged()
                } else {
                    holder.lblRecentDesciption.visibility = View.GONE
                    mExpandedPosition = if (isExpanded) -1 else position
                    notifyDataSetChanged()

                    if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                        mediaPlayer!!.stop()
                        holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
                        mediaPlayer!!.seekTo(0)
                    }

                    path = modal.voicefile

                    val filename: String = modal.msgdetailsid!! + "_" + "Gradit.mp3"

                    path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        context!!.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
                    } else {
                        Environment.getExternalStorageDirectory().path
                    }
                    val dir = File(path, VOICE_FOLDER)
                    val file = File(dir, filename)

                    if (file.exists()) {

                        holder.recentSeekbarlayout.visibility = View.VISIBLE
                        Log.d("FileExits", "NoFileExits")

                        SetUpAudioPlayer(holder)
                        fetchPathUrl(modal)

                        holder.imgRecentEmgplaypause.setOnClickListener(object :
                            View.OnClickListener {
                            override fun onClick(view: View) {

                                mediaFileLengthInMilliseconds = mediaPlayer!!.duration

                                if (!mediaPlayer!!.isPlaying) {
                                    Log.d("AgainStart", "AgainStart")
                                    mediaPlayer!!.start()
                                    holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_pause)

                                } else {

                                    mediaPlayer!!.pause()
                                    holder.imgRecentEmgplaypause.isClickable = true
                                    holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
                                }
                                primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds)
                            }

                            private fun primarySeekBarProgressUpdater(fileLength: Int) {
                                val iProgress =
                                    (mediaPlayer!!.currentPosition.toFloat() / fileLength * 100).toInt()
                                holder.recentseekbar.setProgress(iProgress) // This math construction give a percentage of "was playing"/"song length"
                                if (mediaPlayer!!.isPlaying) {
                                    val notification = Runnable {
                                        holder.lblEmgRecentduration.setText(
                                            milliSecondsToTimer(
                                                mediaPlayer!!.currentPosition.toLong()
                                            )
                                        )
                                        primarySeekBarProgressUpdater(fileLength)
                                    }
                                    handler.postDelayed(notification, 1000)
                                }
                            }
                        })

                        holder.recentseekbar.setOnSeekBarChangeListener(object :
                            SeekBar.OnSeekBarChangeListener {
                            override fun onStopTrackingTouch(seekBar: SeekBar) {}
                            override fun onStartTrackingTouch(seekBar: SeekBar) {}
                            override fun onProgressChanged(
                                seekBar: SeekBar, progress: Int, fromUser: Boolean
                            ) {

                                if (holder.lblRecentTotalDuration.text.toString() == holder.lblEmgRecentduration.text.toString()) {
                                    mediaPlayer!!.stop()
                                    seekBar.progress = 0
                                    mediaPlayer!!.seekTo(0)
                                    holder.lblEmgRecentduration.text = "00:00"
                                    mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition)
                                    holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)

                                }
                            }
                        })
                        mediaPlayer!!.setOnCompletionListener {
                            holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
                            mediaPlayer!!.seekTo(0)
                        }

                    } else {

                        Log.d("FileExits", "FileExits")

                        holder.recentSeekbarlayout.visibility = View.VISIBLE

                        holder.imgRecentEmgplaypause.setOnClickListener(object :
                            View.OnClickListener {
                            override fun onClick(view: View) {

                                if (CommonUtil.DownloadingFile!! == 0) {
                                    Log.d("Voicefilepath", VOICE_FOLDER)
                                    Log.d("modal.msgcontent!!", modal.voicefile!!)
                                    Log.d("filename", filename)

                                    CommunicationVoiceDownload.downloadSampleFile(
                                        context, modal.voicefile!!, VOICE_FOLDER, filename, holder
                                    )

                                    Log.d("ContantId", modal.msgdetailsid.toString())

                                } else {

                                    SetUpAudioPlayerDownloaded(holder)
                                    fetchDownloaded(modal)

                                    mediaFileLengthInMilliseconds = mediaPlayer!!.duration


                                    if (!mediaPlayer!!.isPlaying) {
                                        mediaPlayer!!.start()
                                        holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_pause)

                                    } else {

                                        mediaPlayer!!.pause()
                                        holder.imgRecentEmgplaypause.isClickable = true
                                        holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)

                                    }

                                    primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds)

                                }
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
                            SeekBar.OnSeekBarChangeListener {
                            override fun onStopTrackingTouch(seekBar: SeekBar) {}
                            override fun onStartTrackingTouch(seekBar: SeekBar) {}
                            override fun onProgressChanged(
                                seekBar: SeekBar, progress: Int, fromUser: Boolean
                            ) {

                                if (holder.lblRecentTotalDuration.text.toString() == holder.lblEmgRecentduration.text.toString()) {
                                    mediaPlayer!!.stop()
                                    seekBar.progress = 0
                                    mediaPlayer!!.seekTo(0)
                                    holder.lblEmgRecentduration.text = "00:00"
                                    mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition)
                                    holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)

                                }
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
    }

    init {
        ScreenType = Type
        this.contextThis = context!!
    }

    private fun fetchPathUrl(modal: GetCommunicationDetails) {
        Log.d("PlayStart", "Start***************************************")
        try {
            var filename: String = modal.msgdetailsid!! + "_" + "Gradit.mp3"
            val path: String
            path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
            } else {
                Environment.getExternalStorageDirectory().path
            }

            Log.d("Path", path)

            val dir = File(path, VOICE_FOLDER)
            val file = File(dir, filename)
            PlayPath = file.path
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(PlayPath)
            mediaPlayer!!.prepare()
            iMediaDuration = (mediaPlayer!!.duration / 1000.0).toInt()
        } catch (e: Exception) {
            Log.d("ExceptionWhilePlaying", e.toString())
        }
        Log.d("PlayEnd", "END***************************************")
    }

    private fun SetUpAudioPlayer(holder: MyViewHolder) {

        holder.recentseekbar.max = 99
        holder.recentseekbar.setOnTouchListener(object : View.OnTouchListener {
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

    private fun fetchDownloaded(modal: GetCommunicationDetails) {
        Log.d("PlayStart", "Start***************************************")
        try {
            val filename: String = modal.msgdetailsid!! + "_" + "Gradit.mp3"
            Log.d("filename", filename)
            val path: String
            path = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context!!.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
            } else {
                Environment.getExternalStorageDirectory().path
            }
            Log.d("localpath", path)

            val dir = File(path, VOICE_FOLDER)
            Log.d("__dir", dir.toString())

            val file = File(dir, filename)
            Log.d("__file", file.toString())

            PlayPath = file.path
            Log.d("testFetchpath", PlayPath!!)
            mediaPlayer!!.setDataSource(PlayPath)
            mediaPlayer!!.prepare()

            iMediaDuration = (mediaPlayer!!.duration / 1000.0).toInt()
        } catch (e: Exception) {
            Log.d("ExceptionWhilePlaying", e.toString())
        }
        Log.d("PlayEnd", "END***************************************")
    }

    private fun SetUpAudioPlayerDownloaded(holder: MyViewHolder) {
        holder.recentseekbar.max = 99
        holder.recentseekbar.setOnTouchListener(object : View.OnTouchListener {
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

    override fun getItemCount(): Int {
        return eventlist.size
    }

    fun filterList(filterlist: java.util.ArrayList<GetCommunicationDetails>) {

        eventlist = filterlist

        notifyDataSetChanged()
    }
}



