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
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
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

    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    @JvmField
    @BindView(R.id.imgrecord)
    var imgrecord: ImageView? = null

    @JvmField
    @BindView(R.id.switchonAndoff)
    var switchonAndoff: Switch? = null

    @JvmField
    @BindView(R.id.lbl_switchLable)
    var lbl_switchLable: TextView? = null

    @JvmField
    @BindView(R.id.rcy_history)
    var rcy_history: RecyclerView? = null

    @JvmField
    @BindView(R.id.imgPlayPasue)
    var imgPlayPasue: ImageView? = null

    @JvmField
    @BindView(R.id.radio_group)
    var radio_group: RadioGroup? = null

    @JvmField
    @BindView(R.id.conHistory)
    var conHistory: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.Nestedchildlayout)
    var Nestedchildlayout: NestedScrollView? = null

    @JvmField
    @BindView(R.id.lblRecordTime)
    var lblRecordTime: TextView? = null

    @JvmField
    @BindView(R.id.voice_title)
    var voice_title: TextView? = null

    @JvmField
    @BindView(R.id.seekbarplayvoice)
    var seekbarplayvoice: SeekBar? = null

    @JvmField
    @BindView(R.id.lbltotalduration)
    var lbltotalduration: TextView? = null

    @JvmField
    @BindView(R.id.rytLayoutSeekPlay)
    var rytLayoutSeekPlay: RelativeLayout? = null

    @JvmField
    @BindView(R.id.txt_onandoff)
    var txt_onandoff: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.btnConfirm)
    var btnConfirm: Button? = null

    @JvmField
    @BindView(R.id.btn_Clear_r)
    var btn_Clear_r: Button? = null

    @JvmField
    @BindView(R.id.lblText)
    var lblText: TextView? = null


    @JvmField
    @BindView(R.id.edt_voicename)
    var edt_voicename: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonUtil.SetTheme(this)

        setContentView(R.layout.activity_communication_voice)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        setupAudioPlayer()
        ActionbarWithoutBottom(this)
        CommonUtil.VoiceType = "0"
        CommonUtil.seleteddataArraySection.clear()
        imgRefresh!!.visibility = View.GONE
        voice_title!!.visibility = View.VISIBLE

        Log.d("isAllowtomakecall", CommonUtil.isAllowtomakecall.toString())
        if (CommonUtil.isAllowtomakecall == 1) {
            txt_onandoff!!.visibility = View.VISIBLE
        } else {
            txt_onandoff!!.visibility = View.GONE
        }
        btnConfirm!!.visibility = View.VISIBLE

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
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this)
                            .load(AdSmallImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgthumb!!)
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


        switchonAndoff!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                CommonUtil.VoiceType = "1"
                voice_title!!.visibility = View.GONE
            } else {
                CommonUtil.VoiceType = "0"
                voice_title!!.visibility = View.VISIBLE
            }
        })

        radio_group!!.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_B_msg -> {
                    conHistory!!.visibility = View.GONE
                    Nestedchildlayout!!.visibility = View.VISIBLE
                    btnConfirm!!.visibility = View.VISIBLE
                }

                R.id.radio_B_history -> {
                    btnConfirm!!.visibility = View.GONE
                    conHistory!!.visibility = View.VISIBLE
                    Nestedchildlayout!!.visibility = View.GONE
                    historyOfVoice()
                }
            }
        }


        btn_Clear_r!!.setOnClickListener {

            btn_Clear_r!!.visibility = View.GONE
            rytLayoutSeekPlay!!.visibility = View.GONE
            lblRecordTime!!.text = "00:00"
            edt_voicename!!.setText("")

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
                        rcy_history!!.layoutManager = mLayoutManager
                        rcy_history!!.itemAnimator = DefaultItemAnimator()
                        rcy_history!!.adapter = VoicehistoryAdapter
                        rcy_history!!.recycledViewPool.setMaxRecycledViews(0, 80)
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

    @OnClick(R.id.btnConfirm)
    fun addreception() {

        CommonUtil.voicetitle = edt_voicename!!.text.toString()

        if (!CommonUtil.voicetitle!!.isEmpty() && lblRecordTime!!.text.toString() != "00:00") {

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

    @OnClick(R.id.imgrecord)
    fun imgvoicerecordClick() {
        if (bIsRecording) {
            stop_RECORD()

        } else {
            lblText!!.visibility = View.GONE
            start_RECORD()
        }
    }

//    @OnClick(R.id.imgEventback)
//    fun imgEventback() {
//        onBackPressed()
//    }

    @OnClick(R.id.btnCancel)
    fun btncancleclick() {
        onBackPressed()
    }

    @OnClick(R.id.imgPlayPasue)
    fun playvoiceatseekbar() {
        recplaypause()
    }

    @OnClick(R.id.LayoutAdvertisement)
    fun adclick() {
        BaseActivity.LoadWebViewContext(this, AdWebURl)
    }

    fun start_RECORD() {

        btn_Clear_r!!.visibility = View.GONE


        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
        imgrecord!!.setImageResource(R.drawable.voice_stop)
        imgrecord!!.isClickable = true
        rytLayoutSeekPlay!!.visibility = View.GONE

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

        btn_Clear_r!!.visibility = View.VISIBLE
        recTimerHandler.removeCallbacks(runson)
        bIsRecording = false
        imgrecord!!.setImageResource(R.drawable.voice_record)

        if (lblRecordTime!!.text.toString() == "00:00") {
            rytLayoutSeekPlay!!.visibility = View.GONE
        } else {
            rytLayoutSeekPlay!!.visibility = View.VISIBLE
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
            imgPlayPasue!!.setImageResource(R.drawable.ic_play)
            mediaPlayer!!.seekTo(0)
        }
    }

    fun recplaypause() {
        mediaFileLengthInMilliseconds = mediaPlayer!!.duration

        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            imgPlayPasue!!.setImageResource(R.drawable.ic_pause)

        } else {
            mediaPlayer!!.pause()
            imgPlayPasue!!.isClickable = true
            imgPlayPasue!!.setImageResource(R.drawable.ic_play)
        }
        primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds)
    }

    private fun primarySeekBarProgressUpdater(fileLength: Int) {
        val iProgress =
            ((mediaPlayer!!.currentPosition.toFloat() / fileLength) * 100).toInt()
        seekbarplayvoice!!.progress = iProgress
        if (mediaPlayer!!.isPlaying) {
            val notification: Runnable = object : Runnable {
                override fun run() {
                    lbltotalduration!!.text =
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
            lblRecordTime!!.text = milliSecondsToTimer(recTime * 1000.toLong())
            lbltotalduration!!.text = milliSecondsToTimer(recTime * 1000.toLong())

            val timing: String = lblRecordTime!!.text.toString()
            if (lblRecordTime!!.text.toString() != "00:00") {
                imgrecord!!.isEnabled = true
            }
            recTime = recTime + 1
            if (recTime != iMaxRecDur) {
                recTimerHandler.postDelayed(this, 1000)
            } else {
                stop_RECORD()
            }

            if (lblRecordTime!!.text.equals("03:00")) {
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
            imgrecord!!.isClickable = false
            mediaPlayer!!.pause()
            imgPlayPasue!!.setImageResource(R.drawable.ic_play)
        }
        if (bIsRecording) imgrecord!!.isClickable = true
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}