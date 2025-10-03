package com.vsca.vsnapvoicecollege.ActivitySender

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Adapters.VoicehistoryAdapter
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.voicehistorydata
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityCommunicationVoiceBinding
import java.io.File
import java.io.IOException

class CommunicationVoice : ActionBarActivity() {
    var mediaPlayer: MediaPlayer? = null
    var bIsRecording = false
    var mediaFileLengthInMilliseconds = 0
    var handler = Handler()
    var recTime = 0
    var recorder: MediaRecorder? = null
    var recTimerHandler = Handler()
    var iMediaDuration = 0
    var iMaxRecDur = 0
    private val VOICE_FOLDER_NAME = "Gradit"
    val VOICE_FILE_NAME = "voice.mp3"
    var VoiceFilePath: String? = null
    var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var Voicedata: List<voicehistorydata> = ArrayList()
    var ScreenType: Boolean? = null
    var ScreenName: String? = null
    private var VoicehistoryAdapter: VoicehistoryAdapter? = null

    private lateinit var binding: ActivityCommunicationVoiceBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonUtil.SetTheme(this)
        binding = ActivityCommunicationVoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
         setupAudioPlayer()
        ActionbarWithoutBottom(this)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        CommonUtil.VoiceType = "0"
        CommonUtil.seleteddataArraySection.clear()
        imgRefresh!!.visibility = View.GONE
        binding.voiceTitle!!.visibility = View.VISIBLE

        Log.d("isAllowtomakecall", CommonUtil.isAllowtomakecall.toString())
        if (CommonUtil.isAllowtomakecall == 1) {
            binding.txtOnandoff!!.visibility = View.VISIBLE
        } else {
            binding.txtOnandoff!!.visibility = View.GONE
        }
        binding.btnConfirm!!.visibility = View.VISIBLE
        
        binding.btnConfirm.setOnClickListener { addreception() }
        binding.imgrecord.setOnClickListener { imgvoicerecordClick()
        }
        binding.btnCancel.setOnClickListener { onBackPressed() }
        binding.imgPlayPasue.setOnClickListener { recplaypause() }
        binding.LayoutAdvertisement.setOnClickListener { adclick() }

        binding.radioBMsg.setOnClickListener {
            binding.conHistory!!.visibility = View.GONE
            binding.Nestedchildlayout!!.visibility = View.VISIBLE
            binding.btnConfirm!!.visibility = View.VISIBLE
        }

        binding.radioBHistory.setOnClickListener {
            binding.btnConfirm!!.visibility = View.GONE
            binding.conHistory!!.visibility = View.VISIBLE
            binding.Nestedchildlayout!!.visibility = View.GONE
            historyOfVoice()
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
                            .into(binding.imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this)
                            .load(AdSmallImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imgthumb!!)
                    }
                }
            })

        ScreenType = intent.getBooleanExtra("screentype", true)
        if (ScreenType!!) {
            ScreenName = CommonUtil.Noticeboard
        } else {
            ScreenName = CommonUtil.Communication
        }
        Log.d("screentype", ScreenType.toString())


        binding.switchonAndoff!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                CommonUtil.VoiceType = "1"
                binding.voiceTitle!!.visibility = View.GONE
            } else {
                CommonUtil.VoiceType = "0"
                binding.voiceTitle!!.visibility = View.VISIBLE
            }
        })


        binding.btnClearR!!.setOnClickListener {
            binding.btnClearR!!.visibility = View.GONE
            binding.rytLayoutSeekPlay!!.visibility = View.GONE
            binding.lblRecordTime!!.text = "00:00"
            binding.edtVoicename!!.setText("")

        }

        appViewModel!!._voiceHistory!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    Voicedata = response.data
                    val size = Voicedata.size
                    if (size > 0) {
                        VoicehistoryAdapter = VoicehistoryAdapter(Voicedata, this)
                        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                        binding.rcyHistory!!.layoutManager = mLayoutManager
                        binding.rcyHistory!!.itemAnimator = DefaultItemAnimator()
                        binding.rcyHistory!!.adapter = VoicehistoryAdapter
                        binding.rcyHistory!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        VoicehistoryAdapter!!.notifyDataSetChanged()
                    }
                } else {
                    val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder1.setTitle("Info")
                    builder1.setMessage("No data found")
                    builder1.setCancelable(true)

                    builder1.setPositiveButton(
                        "Ok",
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.cancel()
                            finish()
                        })
                    val alert11: AlertDialog = builder1.create()
                    alert11.show()
                }
            } else {
                val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                builder1.setTitle("Info")
                builder1.setMessage("No data found")
                builder1.setCancelable(true)

                builder1.setPositiveButton(
                    "Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                        finish()
                    })
                val alert11: AlertDialog = builder1.create()
                alert11.show()

            }
        }

    }

    override val layoutResourceId: Int
        get() = R.layout.activity_communication_voice

     fun addreception() {

        CommonUtil.voicetitle = binding.edtVoicename!!.text.toString()

        if (!CommonUtil.voicetitle!!.isEmpty() && binding.lblRecordTime!!.text.toString() != "00:00") {

            if (CommonUtil.Priority.equals("p7")) {
                val i: Intent = Intent(this, HeaderRecipient::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.putExtra("ScreenName", ScreenName)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            } else {
                if (CommonUtil.Priority.equals("p1")) {
                    val i: Intent = Intent(this, PrincipalRecipient::class.java)
                    i.putExtra("ScreenName", ScreenName)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                } else {
                    val i: Intent = Intent(this, AddRecipients::class.java)
                    i.putExtra("ScreenName", ScreenName)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(i)
                }
            }
        } else {
            CommonUtil.ApiAlert(this, CommonUtil.Record_Voice_and_Enter_Title)
        }
    }

    private fun historyOfVoice() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.Appid)
        appViewModel!!._VoiceHistoryData(jsonObject, this)
        Log.d("_VoiceHistoryData:", jsonObject.toString())

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

     fun imgvoicerecordClick() {
        if (bIsRecording) {
            stop_RECORD()

        } else {
            binding.lblText!!.visibility = View.GONE
            start_RECORD()
        }
    }

    fun adclick() {
        BaseActivity.LoadWebViewContext(this, AdWebURl)
    }

    fun start_RECORD() {

        binding.btnClearR!!.visibility = View.GONE

        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
        binding.imgrecord!!.setImageResource(R.drawable.voice_stop)
        binding.imgrecord!!.isClickable = true
        binding.rytLayoutSeekPlay!!.visibility = View.GONE

        try {
            recorder = MediaRecorder()
            recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder!!.setAudioEncodingBitRate(16)
            recorder!!.setAudioSamplingRate(44100)
            recorder!!.setOutputFile(getRecFilename())
            Log.d("_Record", recorder.toString())
            recorder!!.prepare()
            recorder!!.start()
            recTimeUpdation()
            bIsRecording = true


        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stop_RECORD() {

        binding.btnClearR!!.visibility = View.VISIBLE
        recTimerHandler.removeCallbacks(runson)
        bIsRecording = false
        binding.imgrecord!!.setImageResource(R.drawable.voice_record)

        if (binding.lblRecordTime!!.text.toString() == "00:00") {
            binding.rytLayoutSeekPlay!!.visibility = View.GONE
        } else {
            binding.rytLayoutSeekPlay!!.visibility = View.VISIBLE
        }


        try {
            recorder!!.stop()
            fetchSong()
        } catch (stopException: RuntimeException) {

            Log.e("Recorder_Error", stopException.toString())

        }
    }

    fun setupAudioPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnCompletionListener {
            binding.imgPlayPasue!!.setImageResource(R.drawable.ic_play)
            mediaPlayer!!.seekTo(0)
        }
    }

    fun recplaypause() {
        mediaFileLengthInMilliseconds = mediaPlayer!!.duration

        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            binding.imgPlayPasue!!.setImageResource(R.drawable.ic_pause)

        } else {
            mediaPlayer!!.pause()
            binding.imgPlayPasue!!.isClickable = true
            binding.imgPlayPasue!!.setImageResource(R.drawable.ic_play)
        }
        primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds)
    }

    private fun primarySeekBarProgressUpdater(fileLength: Int) {
        val iProgress =
            ((mediaPlayer!!.currentPosition.toFloat() / fileLength) * 100).toInt()
        binding.seekbarplayvoice!!.progress = iProgress
        if (mediaPlayer!!.isPlaying) {
            val notification: Runnable = object : Runnable {
                override fun run() {
                    binding.lbltotalduration!!.text =
                        milliSecondsToTimer(mediaPlayer!!.currentPosition.toLong())
                    primarySeekBarProgressUpdater(fileLength)
                }
            }
            handler.postDelayed(notification, 1000)
        }
    }

    fun getRecFilename(): String? {
        val filepath: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            filepath = this.applicationContext
                .getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path


        } else {

            filepath = Environment.getExternalStorageDirectory().path

        }

        val fileDir = File(filepath, VOICE_FOLDER_NAME)
        if (!fileDir.exists()) {
            fileDir.mkdirs()

        }
        val fileNamePath = File(fileDir, VOICE_FILE_NAME)
        return fileNamePath.path


    }

    fun recTimeUpdation() {
        recTime = 1
        recTimerHandler.postDelayed(runson, 1000)
    }

    val runson: Runnable = object : Runnable {
        override fun run() {
            binding.lblRecordTime!!.text = milliSecondsToTimer(recTime * 1000.toLong())
            binding.lbltotalduration!!.text = milliSecondsToTimer(recTime * 1000.toLong())

            val timing: String = binding.lblRecordTime!!.text.toString()
            if (binding.lblRecordTime!!.text.toString() != "00:00") {
                binding.imgrecord!!.isEnabled = true
            }
            recTime = recTime + 1
            if (recTime != iMaxRecDur) {
                recTimerHandler.postDelayed(this, 1000)
            } else {
                stop_RECORD()
            }

            if (binding.lblRecordTime!!.text.equals("03:00")) {
                stop_RECORD()
            }
        }
    }

    fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""
        var minutesString = ""

        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        minutesString = if (minutes < 10) {
            "0$minutes"
        } else {
            "" + minutes
        }
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutesString:$secondsString"
        return finalTimerString
    }

    fun fetchSong() {
        Log.d("FetchSong", "Start***************************************")
        try {
            val filepath: String
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                filepath =
                    this.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
                Log.d("File 11", filepath)

            } else {

                filepath = Environment.getExternalStorageDirectory().path
                Log.d("File 10", filepath)

            }

            val fileDir = File(filepath, VOICE_FOLDER_NAME)

            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }

            CommonUtil.futureStudioIconFile = File(fileDir, VOICE_FILE_NAME)
            Log.d("Voice_File", CommonUtil.futureStudioIconFile.toString())

            VoiceFilePath = CommonUtil.futureStudioIconFile?.path
            println("FILE_PATH:" + VoiceFilePath)
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(CommonUtil.futureStudioIconFile!!.path)
            mediaPlayer!!.prepare()
            iMediaDuration = (mediaPlayer!!.duration / 1000.0).toInt()
            CommonUtil.VoiceDuration = iMediaDuration.toString()

        } catch (e: Exception) {
            Log.d("in Fetch Song", e.toString())
        }
        Log.d("FetchSong", "END***************************************")
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        AdForCollegeApi()
        super.onResume()
        super.onResume()
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer!!.isPlaying) {
            binding.imgrecord!!.isClickable = false
            mediaPlayer!!.pause()
            binding.imgPlayPasue!!.setImageResource(R.drawable.ic_play)
        }
        if (bIsRecording) binding.imgrecord!!.isClickable = true
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}