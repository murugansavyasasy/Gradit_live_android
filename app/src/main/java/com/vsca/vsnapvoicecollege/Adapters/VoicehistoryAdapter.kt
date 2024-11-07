package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.vsca.vsnapvoicecollege.ActivitySender.AddRecipients
import com.vsca.vsnapvoicecollege.ActivitySender.HeaderRecipient
import com.vsca.vsnapvoicecollege.ActivitySender.PrincipalRecipient
import com.vsca.vsnapvoicecollege.Model.voicehistorydata
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class VoicehistoryAdapter(
    var voicehistorydata: List<voicehistorydata>, private var context: Context?
) : RecyclerView.Adapter<VoicehistoryAdapter.MyViewHolder>() {

    val contextThis: Context
    var mediaPlayer: MediaPlayer? = MediaPlayer()
    var path: String? = null
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler()
    var mediaFileLengthInMilliseconds = 0
    var ScreenName: String? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var lblRecenttitle: TextView
        var lblRecentDate: TextView
        var lblRecentTime: TextView
        var txt_sendhistory: TextView
        var lblRecentTotalDuration: TextView
        var lblEmgRecentduration: TextView
        var imgRecentEmgplaypause: ImageView
        var recentseekbar: SeekBar
        var recentSeekbarlayout: RelativeLayout

        init {
            lblRecenttitle = itemView.findViewById(R.id.lblRecenttitle)
            lblRecentDate = itemView.findViewById(R.id.lblRecentDate)
            lblRecentTime = itemView.findViewById(R.id.lblRecentTime)
            imgRecentEmgplaypause = itemView.findViewById(R.id.imgRecentEmgplaypause)
            recentseekbar = itemView.findViewById(R.id.recentseekbar)
            lblEmgRecentduration = itemView.findViewById(R.id.lblEmgRecentduration)
            lblRecentTotalDuration = itemView.findViewById(R.id.lblRecentTotalDuration)
            txt_sendhistory = itemView.findViewById(R.id.txt_sendhistory)
            recentSeekbarlayout = itemView.findViewById(R.id.recentSeekbarlayout)
        }
    }

    init {
        this.contextThis = context!!
    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): VoicehistoryAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.voicehistory, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VoicehistoryAdapter.MyViewHolder, position: Int) {
        val modal = voicehistorydata[position]

        holder.lblRecenttitle.text = modal.description

        val createdDateTime: String = modal.timing
        val firstvalue: Array<String> = createdDateTime.split("-".toRegex()).toTypedArray()
        val createddate: String = firstvalue[0]
        holder.lblRecentDate.text = createddate
        val createdTime: String = firstvalue[1]
        holder.lblRecentTime.text = createdTime

        if (modal.voicefile.isNotEmpty()) {

            holder.lblRecentTotalDuration.visibility = View.VISIBLE
            holder.lblRecentTotalDuration.text = modal.duration
            holder.recentSeekbarlayout.visibility = View.VISIBLE
            holder.imgRecentEmgplaypause.setOnClickListener {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition)
                    mediaPlayer!!.pause()
                    holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
                } else {
                    mediaFileLengthInMilliseconds = mediaPlayer!!.duration
                    holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_pause)
                    mediaPlayer!!.reset()
                    mediaPlayer!!.setDataSource(modal.voicefile)
                    mediaPlayer!!.prepare()
                    mediaPlayer!!.start()
                    primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds, holder)
                }
                initializeSeekBar(holder)
            }

            holder.recentseekbar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                    if (b) {
                        mediaPlayer!!.seekTo(i * 1000)
                    }
                    mediaPlayer!!.setOnCompletionListener {
                        holder.imgRecentEmgplaypause.setImageResource(R.drawable.ic_play)
                        mediaPlayer!!.seekTo(0)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }
            })
        } else {
            holder.recentSeekbarlayout!!.visibility = View.GONE
        }

        holder.txt_sendhistory.setOnClickListener {
            CommonUtil.voiceHeadedId = modal.headerid
            CommonUtil.Description = modal.description
           // showPopup(modal)
            gotoRecipient()
        }
    }


    override fun getItemCount(): Int {
        return voicehistorydata.size
    }

    private fun primarySeekBarProgressUpdater(fileLength: Int, holder: MyViewHolder) {
        val iProgress = (mediaPlayer!!.currentPosition.toFloat() / fileLength * 100).toInt()
        holder.recentseekbar.setProgress(iProgress) // This math construction give a percentage of "was playing"/"song length"
        if (mediaPlayer!!.isPlaying) {
            val notification = Runnable {
                holder.lblEmgRecentduration.text = milliSecondsToTimer(
                    mediaPlayer!!.currentPosition.toLong()
                )
                primarySeekBarProgressUpdater(fileLength, holder)
            }
            handler.postDelayed(notification, 1000)
        }
    }


    private fun initializeSeekBar(holder: VoicehistoryAdapter.MyViewHolder) {
        holder.recentseekbar.max = mediaPlayer!!.seconds

        runnable = Runnable {
            holder.recentseekbar.progress = mediaPlayer!!.currentSeconds
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
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
        minutesString = if (minutes < 10) {
            "0$minutes"
        } else {
            "" + minutes
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutesString:$secondsString"

        // return timer string
        return finalTimerString
    }

    fun gotoRecipient() {

        ScreenName = CommonUtil.CommunicationVoice
        if (CommonUtil.Priority.equals("p7")) {
            val i: Intent = Intent(contextThis, HeaderRecipient::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("ScreenName", ScreenName)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            contextThis.startActivity(i)
        } else {
            if (CommonUtil.Priority.equals("p1")) {
                val i: Intent = Intent(contextThis, PrincipalRecipient::class.java)
                i.putExtra("ScreenName", ScreenName)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                contextThis.startActivity(i)
            } else {
                val i: Intent = Intent(contextThis, AddRecipients::class.java)
                i.putExtra("ScreenName", ScreenName)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                contextThis.startActivity(i)
            }
        }
    }

}

val MediaPlayer.seconds: Int
    get() {
        return this.duration / 1000
    }

// Creating an extension property to get media player current position in seconds
val MediaPlayer.currentSeconds: Int
    get() {
        return this.currentPosition / 1000
    }
