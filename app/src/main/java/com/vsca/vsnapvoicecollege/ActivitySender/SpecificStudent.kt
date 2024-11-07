package com.vsca.vsnapvoicecollege.ActivitySender

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.AWS.S3Uploader
import com.vsca.vsnapvoicecollege.AWS.S3Utils
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Activities.Assignment
import com.vsca.vsnapvoicecollege.Activities.Circular
import com.vsca.vsnapvoicecollege.Activities.Communication
import com.vsca.vsnapvoicecollege.Activities.Events
import com.vsca.vsnapvoicecollege.Activities.MessageCommunication
import com.vsca.vsnapvoicecollege.Activities.Noticeboard
import com.vsca.vsnapvoicecollege.Activities.Video
import com.vsca.vsnapvoicecollege.Adapters.SelectedRecipientAdapter
import com.vsca.vsnapvoicecollege.Adapters.specificStudent_adapter
import com.vsca.vsnapvoicecollege.Interfaces.ApiInterfaces
import com.vsca.vsnapvoicecollege.Interfaces.RecipientCheckListener
import com.vsca.vsnapvoicecollege.Model.AWSUploadedFiles
import com.vsca.vsnapvoicecollege.Model.specificStudent_datalist
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.SenderModel.RecipientSelected
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.Utils.VimeoUploader
import com.vsca.vsnapvoicecollege.ViewModel.App
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SpecificStudent : ActionBarActivity(), VimeoUploader.UploadCompletionListener {

    @JvmField
    @BindView(R.id.idSV)
    var SearchView: SearchView? = null

    @JvmField
    @BindView(R.id.recycleSpecific)
    var recycleSpecific: RecyclerView? = null

    @JvmField
    @BindView(R.id.txt_onandoff)
    var txt_onandoff: RelativeLayout? = null

    @JvmField
    @BindView(R.id.switchonAndoff)
    var switchonAndoff: Switch? = null

    @JvmField
    @BindView(R.id.ch_BoxAll)
    var ch_BoxAll: CheckBox? = null

    @JvmField
    @BindView(R.id.txt_ch_BoxAll)
    var txt_ch_BoxAll: TextView? = null

    @JvmField
    @BindView(R.id.lnrTargetParent)
    var lnrTargetParent: LinearLayout? = null

    @JvmField
    @BindView(R.id.chboxAllSpecific)
    var chboxAllSpecific: CheckBox? = null

    @JvmField
    @BindView(R.id.chboxStudent)
    var chboxStudent: CheckBox? = null

    @JvmField
    @BindView(R.id.chboxParents)
    var chboxParents: CheckBox? = null

    @JvmField
    @BindView(R.id.chboxStaff)
    var chboxStaff: CheckBox? = null

    @JvmField
    @BindView(R.id.lblSubjectName)
    var lblSubjectName: TextView? = null

    @JvmField
    @BindView(R.id.lblSubjectType)
    var lblSubjectType: TextView? = null

    @JvmField
    @BindView(R.id.lblSubjectCategory)
    var lblSubjectCategory: TextView? = null

    @JvmField
    @BindView(R.id.lblSubjectCredits)
    var lblSubjectCredits: TextView? = null


    var SpecificStudentList: SelectedRecipientAdapter? = null
    var appViewModel: App? = null
    var getspecifictuterstudent: List<specificStudent_datalist> = ArrayList()
    var specificStudent_adapter: specificStudent_adapter? = null
    var AWSUploadedFilesList = java.util.ArrayList<AWSUploadedFiles>()
    var Awsuploadedfile = java.util.ArrayList<String>()
    var pathIndex = 0
    var uploadFilePath: String? = null
    var contentType: String? = null
    var progressDialog: ProgressDialog? = null
    var fileNameDateTime: String? = null
    var Awsaupladedfilepath: String? = null
    var separator = ","
    var fileName: File? = null
    var filename: String? = null
    var FileType: String? = null
    var isParent: Boolean? = null
    var isStaff: Boolean? = null
    var isStudent = true
    var isVideoToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this)

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        CommonUtil.CallEnable = "0"

        val VideoToken = SharedPreference.getVideo_Json(this).toString()

        isVideoToken = VideoToken
        Log.d("isVideoToken", VideoToken)

        switchonAndoff!!.setOnClickListener {
            if (switchonAndoff!!.isChecked) {
                CommonUtil.CallEnable = "1"
            } else {
                CommonUtil.CallEnable = "0"
            }
        }
        lblSubjectName!!.text = CommonUtil.courseName_
        lblSubjectType!!.text = CommonUtil.year_
        lblSubjectCategory!!.text = CommonUtil.semester_
        lblSubjectCredits!!.text = CommonUtil.section_
        FileType = intent.getStringExtra("FileType")

        isStudent = true
        isStaff = false
        isParent = false

        if (CommonUtil.isParentEnable.equals("1")) {
            lnrTargetParent!!.visibility = View.VISIBLE
        } else {
            lnrTargetParent!!.visibility = View.GONE
            isParent = false
        }

        chboxParents!!.setOnClickListener {

            if (chboxParents!!.isChecked) {
                isParent = true

                if (isParent!! && isStudent!!) {
                    chboxAllSpecific!!.isChecked = true

                } else {
                    chboxAllSpecific!!.isChecked = false
                }

            } else {
                isParent = false

                chboxAllSpecific!!.isChecked = false
            }
        }

        chboxAllSpecific!!.setOnClickListener {

            if (chboxAllSpecific!!.isChecked) {

                if (CommonUtil.isParentEnable == "1") {

                    isStudent = true
                    isStaff = true
                    isParent = true

                    chboxStudent!!.isChecked = true
                    chboxParents!!.isChecked = true
                    chboxStaff!!.isChecked = true


                } else {

                    isStudent = true
                    isStaff = true
                    isParent = false

                    chboxStudent!!.isChecked = true
                    chboxParents!!.isChecked = false
                    chboxStaff!!.isChecked = true

                }
            } else {

                isParent = false
                isStudent = false
                isStaff = false

                chboxStudent!!.isChecked = false
                chboxParents!!.isChecked = false
                chboxStaff!!.isChecked = false

            }
        }
        if (CommonUtil.isAllowtomakecall == 1) {
            when (CommonUtil.ScreenType) {
                CommonUtil.Text -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.TextHistory -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.Communication -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.CommunicationVoice -> txt_onandoff!!.visibility = View.VISIBLE
                CommonUtil.New_Video -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.Noticeboard -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.ScreenNameEvent -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.Image_Pdf -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.New_Assignment -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.Forward_Assignment -> txt_onandoff!!.visibility = View.GONE
            }
        }

        ch_BoxAll!!.setOnClickListener(View.OnClickListener {

            CommonUtil.seleteddataArrayCheckbox.clear()
            if (ch_BoxAll!!.isChecked) {
                SpecificStudentList!!.selectAll()
                txt_ch_BoxAll!!.text = CommonUtil.Remove_All
            } else {
                SpecificStudentList!!.unselectall()
                txt_ch_BoxAll!!.text = CommonUtil.Select_All

            }
        })

        chboxStaff!!.setOnClickListener {
            if (CommonUtil.isParentEnable.equals("1")) {
                if (chboxStaff!!.isChecked) {
                    isStaff = true
                    if (isParent!! && isStudent!! && isStaff!!) {
                        chboxAllSpecific!!.isChecked = true
                    } else {
                        chboxAllSpecific!!.isChecked = false
                    }
                } else {
                    isStaff = false
                    chboxAllSpecific!!.isChecked = false
                }

            } else {

                if (chboxStaff!!.isChecked) {
                    isStaff = true

                    if (isStudent!! && isStaff!!) {
                        chboxAllSpecific!!.isChecked = true
                    } else {
                        chboxAllSpecific!!.isChecked = false
                    }
                } else {
                    isStaff = false
                    chboxAllSpecific!!.isChecked = false
                }
            }
        }


        chboxStudent!!.setOnClickListener {


            if (CommonUtil.isParentEnable.equals("1")) {

                if (chboxStudent!!.isChecked) {

                    isStudent = true

                    if (isParent!! && isStudent!!) {
                        chboxAllSpecific!!.isChecked = true

                    } else {
                        chboxAllSpecific!!.isChecked = false
                    }
                } else {
                    isStudent = false

                    chboxAllSpecific!!.isChecked = false
                }

            } else {

                if (chboxStudent!!.isChecked) {
                    isStudent = true

                    if (isStudent!!) {
                        chboxAllSpecific!!.isChecked = true
                    } else {
                        chboxAllSpecific!!.isChecked = false
                    }

                } else {
                    isStudent = false
                    chboxAllSpecific!!.isChecked = false
                }
            }
        }



        if (CommonUtil.SpecificButton.equals(CommonUtil.Subjects) || CommonUtil.SpecificButton.equals(
                CommonUtil.Assignment_SPECIFIC
            ) || CommonUtil.SpecificButton.equals(CommonUtil.Forward_Assignment) || CommonUtil.SpecificButton!!.equals(
                CommonUtil.New_Assignment
            )
        ) {
            getspecificstudentdata()
        } else if (CommonUtil.SpecificButton.equals(CommonUtil.Tutor)) {
            getspecificstudentdatasubject()
        }

        SearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {

                filter(msg)
                return false
            }
        })

        appViewModel!!.Getspecificstudenttutot!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                CommonUtil.receiverid = ""
                if (status == 1) {

                    getspecifictuterstudent = response.data!!
                    SearchView!!.visibility = View.VISIBLE
                    ch_BoxAll!!.visibility = View.VISIBLE
                    txt_ch_BoxAll!!.visibility = View.VISIBLE
                    SelectedRecipientlist.clear()


                    getspecifictuterstudent.forEach {
                        it.memberid
                        it.name

                        val group = RecipientSelected(it.memberid, it.name)
                        SelectedRecipientlist.add(group)
                    }

                    Log.d("GetStudentList", SelectedRecipientlist.size.toString())
                    CommonUtil.receiverid = ""

                    SpecificStudentList = SelectedRecipientAdapter(SelectedRecipientlist,
                        this,
                        object : RecipientCheckListener {
                            override fun add(data: RecipientSelected?) {
                                var groupid = data!!.SelectedId

                                Log.d("Selectedids", SelectedRecipientlist.size.toString())
                                Log.d(
                                    "Selectedids123",
                                    CommonUtil.seleteddataArrayCheckbox.size.toString()
                                )
                                if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {

                                    ch_BoxAll!!.isChecked = true
                                    txt_ch_BoxAll!!.setText(CommonUtil.Remove_All)

                                } else {

                                    ch_BoxAll!!.isChecked = false
                                    txt_ch_BoxAll!!.setText(CommonUtil.Select_All)

                                }

                            }

                            override fun remove(data: RecipientSelected?) {
                                var groupid = data!!.SelectedId

                                ch_BoxAll!!.isChecked = false
                                txt_ch_BoxAll!!.setText(CommonUtil.Select_All)
                            }
                        })

                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    recycleSpecific!!.layoutManager = mLayoutManager
                    recycleSpecific!!.itemAnimator = DefaultItemAnimator()
                    recycleSpecific!!.adapter = SpecificStudentList
                    recycleSpecific!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    SpecificStudentList!!.notifyDataSetChanged()
                } else {

                    SearchView!!.visibility = View.GONE
                    recycleSpecific!!.visibility = View.GONE
                    ch_BoxAll!!.visibility = View.GONE
                    txt_ch_BoxAll!!.visibility = View.GONE
                }
            } else {

                SearchView!!.visibility = View.GONE
                ch_BoxAll!!.visibility = View.GONE
                txt_ch_BoxAll!!.visibility = View.GONE
            }
        }

        //particular sms

        appViewModel!!.SendSMStoParticularMutableData!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, MessageCommunication::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                } else {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, MessageCommunication::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }


        //sms sent

        appViewModel!!.SendSMSToEntireCollegeLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, MessageCommunication::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                } else {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, MessageCommunication::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        //NoticeBoard sms send

        appViewModel!!.NoticeBoardSendSMStoParticularMutableData!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Noticeboard::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                } else {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Noticeboard::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        //Image or pdf particular send

        appViewModel!!.Imageorpdfparticuler!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {


                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Circular::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                } else {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Circular::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }


        //Voice send Particular History

        appViewModel!!.SendVoiceToParticulerHistory!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message

                if (status == 1) {
                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Communication::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                } else {
                    CommonUtil.ApiAlert(this, message)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        //Image send Tutor

        appViewModel!!._PdfandImagesend!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {


                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Circular::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                } else {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Circular::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        //Assignment send

        appViewModel!!.Assignment!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Assignment::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                } else {
                    CommonUtil.ApiAlert(this, message)
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        //Assignment Forward

        appViewModel!!.ForwardText!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message


                if (status == 1) {
                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Assignment::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                } else {
                    CommonUtil.ApiAlert(this, message)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }


        //Event send

        appViewModel!!.Eventsenddata!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent = Intent(this, Events::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                } else {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Events::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }


        //Video Particular send

        appViewModel!!.VideoParticulerSend!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Video::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                } else {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Video::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }


        // VIDEO SEND TUTER

        appViewModel!!.SendVideoParticulerTuter!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Video::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()

                } else {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Video::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }
    }

    private fun filter(text: String) {

        val filteredlist: java.util.ArrayList<RecipientSelected> = java.util.ArrayList()

        for (item in SelectedRecipientlist) {
            if (item.SelectedName!!.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {

        } else {
            SpecificStudentList!!.filterList(filteredlist, false)
        }
    }

    private fun getspecificstudentdata() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_courseid, CommonUtil.Courseid)
        jsonObject.addProperty(ApiRequestNames.Req_dept_id, CommonUtil.deptid)
        jsonObject.addProperty(ApiRequestNames.Req_yearid, CommonUtil.YearId)
        jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.receiverid)
        jsonObject.addProperty("subjectid", CommonUtil.SubjectID)

        appViewModel!!.getspecificstudentdata(jsonObject, this)
        Log.d("getTutorSpecificRequest", jsonObject.toString())
    }

    private fun getspecificstudentdatasubject() {

        val jsonObject = JsonObject()
        jsonObject.addProperty("staffid", CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_yearid, CommonUtil.YearId)
        jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.receiverid)

        appViewModel!!.getspecificstudentdatasubject(jsonObject, this)
        Log.d("getspecificSubject", jsonObject.toString())

    }

    @OnClick(R.id.btnRecipientCancel)
    fun cancelClick() {
        onBackPressed()
        CommonUtil.DepartmentChooseIds.clear()
        CommonUtil.DepartmentChooseIds.add(CommonUtil.SectionIdChoose)
        CommonUtil.receiverid = CommonUtil.Onbackpressed
    }

    @SuppressLint("SuspiciousIndentation")
    @OnClick(R.id.btnConfirm)
    fun SendButtonAPi() {


        Log.d("isStudent", isStudent.toString())
        Log.d("isStaff", isStaff.toString())
        Log.d("isParent", isParent.toString())
        val SelectedCount = CommonUtil.DepartmentChooseIds.size.toString()
        CommonUtil.receivertype = "7"
        if (CommonUtil.ScreenType.equals(CommonUtil.Text)) {
            if (CommonUtil.SpecificButton == CommonUtil.Subjects) {
                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

                            SmsToParticularTypeRequest()

                        }

                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            } else if (CommonUtil.SpecificButton == CommonUtil.Tutor) {

                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            SmsToEntireCollegesubjectandtuterRequest()
                        }

                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            }
        } else if (CommonUtil.ScreenType.equals(CommonUtil.TextHistory)) {
            if (CommonUtil.SpecificButton == CommonUtil.Subjects) {
                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

                            SmsToParticularTypeRequest()

                        }

                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            } else if (CommonUtil.SpecificButton == CommonUtil.Tutor) {

                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            SmsToEntireCollegesubjectandtuterRequest()
                        }
                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            }
        } else if (CommonUtil.ScreenType.equals(CommonUtil.Communication)) {
            if (CommonUtil.SpecificButton == (CommonUtil.Subjects)) {
                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            VoiceSendParticuler()
                        }

                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)

                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)

                }

            } else if (CommonUtil.SpecificButton == CommonUtil.Tutor) {
                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            VoiceSendTuter()
                        }

                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)

                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)

                }
            }
        } else if (CommonUtil.ScreenType.equals(CommonUtil.CommunicationVoice)) {
            if (CommonUtil.SpecificButton == (CommonUtil.Subjects)) {
                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            SendVoiceToParticulerHistory()
                        }
                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)

                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)

                }

            } else if (CommonUtil.SpecificButton == CommonUtil.Tutor) {
                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            SendVoiceToParticulerHistory()
                        }
                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)

                }
            }
        } else if (CommonUtil.ScreenType.equals(CommonUtil.Noticeboard)) {
            if (CommonUtil.SpecificButton == CommonUtil.Subjects) {
                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            //   NoticeBoardSMSsending()
                            awsFileUpload(this, pathIndex)
                        }

                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            } else if (CommonUtil.SpecificButton == CommonUtil.Tutor) {
                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            //   NoticeBoardSMSsendingTuter()
                            awsFileUpload(this, pathIndex)
                        }

                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            }
        } else if (CommonUtil.ScreenType.equals(CommonUtil.Image_Pdf)) {

            if (CommonUtil.SpecificButton.equals(CommonUtil.Subjects)) {

                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

                            awsFileUpload(this, pathIndex)

                        }

                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()

                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            } else if (CommonUtil.SpecificButton == CommonUtil.Tutor) {

                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            awsFileUpload(this, pathIndex)
                        }
                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()

                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            }
        } else if (CommonUtil.ScreenType.equals(CommonUtil.New_Assignment)) {

            if (CommonUtil.receiverid != "") {

                val alertDialog: AlertDialog.Builder =
                    AlertDialog.Builder(this@SpecificStudent)
                alertDialog.setTitle(CommonUtil.Submit_Alart)
                alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                alertDialog.setPositiveButton(
                    CommonUtil.Yes
                ) { _, _ ->
                    awsFileUpload(this, pathIndex)
                }
                alertDialog.setNegativeButton(
                    CommonUtil.No
                ) { _, _ -> }
                val alert: AlertDialog = alertDialog.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
            }

        } else if (CommonUtil.ScreenType.equals(CommonUtil.Forward_Assignment)) {

            if (CommonUtil.receiverid != "") {

                val alertDialog: AlertDialog.Builder =
                    AlertDialog.Builder(this@SpecificStudent)
                alertDialog.setTitle(CommonUtil.Submit_Alart)
                alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                alertDialog.setPositiveButton(
                    CommonUtil.Yes
                ) { _, _ ->
                    awsFileUpload(this, pathIndex)
                }
                alertDialog.setNegativeButton(
                    CommonUtil.No
                ) { _, _ -> }
                val alert: AlertDialog = alertDialog.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
            }

        } else if (CommonUtil.ScreenType.equals(CommonUtil.ScreenNameEvent)) {
            if (CommonUtil.SpecificButton == CommonUtil.Subjects) {
                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            Eventsend("add")
                        }

                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            } else if (CommonUtil.SpecificButton == CommonUtil.Tutor) {
                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            EventsendTuter("add")
                        }

                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            }
        } else if (CommonUtil.ScreenType.equals(CommonUtil.Event_Edit)) {

            if (CommonUtil.SpecificButton == CommonUtil.Subjects) {
                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            Eventsend("edit")

                        }

                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            } else if (CommonUtil.SpecificButton == CommonUtil.Tutor) {
                if (CommonUtil.receiverid != "") {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            EventsendTuter("edit")
                        }

                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            }
        } else if (CommonUtil.ScreenType.equals(CommonUtil.New_Video)) {

            if (!CommonUtil.receiverid.equals("")) {

                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                    if (CommonUtil.SpecificButton == CommonUtil.Subjects) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            //  VimeoVideoUpload(this, CommonUtil.videofile!!)
                            VimeoUploader.uploadVideo(
                                this,
                                CommonUtil.title,
                                CommonUtil.Description,
                                isVideoToken,
                                CommonUtil.videofile!!,
                                this
                            )

                        }
                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    } else if (CommonUtil.SpecificButton == CommonUtil.Tutor) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Hold_on)
                        alertDialog.setMessage(CommonUtil.Submit_Alart)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
//                            VimeoVideoUpload(this, CommonUtil.videofile!!)
                            VimeoUploader.uploadVideo(
                                this,
                                CommonUtil.title,
                                CommonUtil.Description,
                                isVideoToken,
                                CommonUtil.videofile!!,
                                this
                            )
                        }
                        alertDialog.setNegativeButton(
                            CommonUtil.No
                        ) { _, _ -> }
                        val alert: AlertDialog = alertDialog.create()
                        alert.setCanceledOnTouchOutside(false)
                        alert.show()
                    }
                } else {
                    CommonUtil.ApiAlert(
                        this@SpecificStudent, CommonUtil.Select_the_Target
                    )
                }
            } else {
                CommonUtil.ApiAlert(
                    this@SpecificStudent, CommonUtil.Select_the_Receiver
                )
            }
        }
    }


    fun VimeoVideoUpload(activity: Activity, file: String) {

        val strsize = file.length
        Log.d("videosize", strsize.toString())
        Log.d("File", file.toString())
        val clientinterceptor = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        clientinterceptor.interceptors().add(interceptor)
        val client1: OkHttpClient
        client1 = OkHttpClient.Builder().connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES).writeTimeout(5, TimeUnit.MINUTES).build()
        val retrofit = Retrofit.Builder().client(client1).baseUrl("https://api.vimeo.com/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val service: ApiInterfaces = retrofit.create(ApiInterfaces::class.java)
        val mProgressDialog = ProgressDialog(activity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Connecting...")
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
        val `object` = JsonObject()
        val jsonObjectclasssec = JsonObject()
        jsonObjectclasssec.addProperty("approach", "post")
        jsonObjectclasssec.addProperty("size", strsize.toString())
        val jsonprivacy = JsonObject()
        jsonprivacy.addProperty("view", "unlisted")
        val jsonshare = JsonObject()
        jsonshare.addProperty("share", "false")
        val jsonembed = JsonObject()
        jsonembed.add("buttons", jsonshare)

        `object`.add("upload", jsonObjectclasssec)
        `object`.addProperty("name", CommonUtil.title)
        `object`.addProperty("description", CommonUtil.Description)
        `object`.add("privacy", jsonprivacy)
        `object`.add("embed", jsonembed)
        val head = "Bearer " + "8d74d8bf6b5742d39971cc7d3ffbb51a"
        Log.d("header", head)
        Log.d("object", `object`.toString())


        val call: Call<JsonObject> = service.VideoUpload(`object`, head)
        Log.d("jsonOBJECT", `object`.toString())
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                val res = response.code()
                Log.d("RESPONSE", response.toString())
                if (response.isSuccessful) {


                    try {
                        val object1 = JSONObject(response.body().toString())
                        Log.d("responsevimeo", object1.toString())
                        Log.d("Response sucess", "response entered success")

                        val obj = object1.getJSONObject("upload")
                        val obj1 = object1.getJSONObject("embed")
                        val upload_link = obj.getString("upload_link")
                        val link = object1.getString("link")
                        val iframe = obj1.getString("html")

                        Log.d("upload_link", upload_link)
                        Log.d("iframe", iframe)
                        Log.d("link", link)

                        // this  below two line is my checking line

                        CommonUtil.VimeoIframe = iframe
                        CommonUtil.VimeoVideoUrl = link
                        Log.d("VimeoVideoUrl", CommonUtil.VimeoVideoUrl.toString())

                        try {
                            CommonUtil.Videofile = false
                            VIDEOUPLOAD(upload_link, file, activity)
                            CommonUtil.Videofile = true

                        } catch (e: Exception) {
                            Log.e("VIMEO Exception", e.message!!)
                            CommonUtil.Toast(activity, "Video sending failed.Retry")
                        }
                    } catch (e: Exception) {
                        Log.e("VIMEO Exception", e.message!!)
                        CommonUtil.Toast(activity, e.message)
                    }
                } else {
                    CommonUtil.Toast(activity, "Video sending failed.Retry")
                }
            }

            override fun onFailure(
                call: Call<JsonObject>, t: Throwable
            ) {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                Log.e("Response Failure", t.message!!)
                CommonUtil.Toast(activity, "Video sending failed.Retry")
            }
        })
    }

    @SuppressLint("LongLogTag")
    private fun VIDEOUPLOAD(
        upload_link: String, file: String, activity: Activity
    ) {
        Log.d("Upload_link", upload_link)
        val separated = upload_link.split("?").toTypedArray()
        val name = separated[0]
        val FileName = separated[1]
        val upload = name.replace("upload", "")
        Log.d("Upload", upload)
        val id = FileName.split("&").toTypedArray()
        val ticket_id = id[0]
        val video_file_id = id[1]
        val signature = id[2]
        val v6 = id[3]
        val redirect_url = id[4]
        val seperate1: Array<String> = ticket_id.split("=").toTypedArray()
        val ticket = seperate1[0]
        val ticket2 = seperate1[1]
        val seperate2: Array<String> = video_file_id.split("=").toTypedArray()
        val ticket1 = seperate2[0]
        val ticket3 = seperate2[1]
        val seper: Array<String> = signature.split("=").toTypedArray()
        val ticke = seper[0]
        val tick = seper[1]
        val sepera: Array<String> = v6.split("=").toTypedArray()
        val str = sepera[0]
        val str1 = sepera[1]
        val sucess: Array<String> = redirect_url.split("=").toTypedArray()
        val urlRIDERCT = sucess[0]
        val redirect_url123 = sucess[1]

        val client1: OkHttpClient
        client1 = OkHttpClient.Builder().connectTimeout(600, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.MINUTES).writeTimeout(40, TimeUnit.MINUTES).build()

        val retrofit = Retrofit.Builder().client(client1).baseUrl(upload)
            .addConverterFactory(GsonConverterFactory.create()).build()
        Log.d("Retro_Fit", retrofit.toString())

        val mProgressDialog = ProgressDialog(activity)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Uploading...")
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
        val service: ApiInterfaces = retrofit.create(ApiInterfaces::class.java)
        var requestFile: RequestBody? = null

        try {

            val filepath = file
            val `in`: InputStream = FileInputStream(filepath)
            val buf: ByteArray
            buf = ByteArray(`in`.available())
            while (`in`.read(buf) != -1);
            requestFile =
                RequestBody.create("application/offset+octet-stream".toMediaTypeOrNull(), buf)
        } catch (e: IOException) {
            e.printStackTrace()
            CommonUtil.Toast(activity, e.message)
        }

        val call: Call<ResponseBody> = service.patchVimeoVideoMetaData(
            ticket2,
            ticket3,
            tick,
            str1,
            redirect_url123 + CommonUtil.VideoSending_RedarectUrl,
            requestFile
        )

        Log.d("Redirect_Url", redirect_url123 + CommonUtil.VideoSending_RedarectUrl)

        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>, response: Response<ResponseBody?>
            ) {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                try {
                    if (response.isSuccessful) {
                        CommonUtil.Videofile = true

                        CommonUtil.selectedPaths.add(response.toString())
                        Log.d("SeletedFile_Video", CommonUtil.selectedPaths.toString())
                        CommonUtil.Toast(activity, "Successfully Uploaded")

                        if (CommonUtil.Priority.equals("p1")) {
                            if (CommonUtil.SpecificButton == CommonUtil.Subjects) {
                                VideosendParticuler()
                            } else if (CommonUtil.SpecificButton == CommonUtil.Tutor) {
                                VideosendParticulerTuter()
                            }
                        }
                    } else {
                        CommonUtil.Toast(activity, "Video sending failed.Retry")

                    }
                } catch (e: Exception) {
                    CommonUtil.Toast(activity, e.message)
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                CommonUtil.Toast(activity, "Video sending failed.Retry")
            }
        })
    }


    private fun VideosendParticuler() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_title, CommonUtil.title)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.Description)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty("iframe", CommonUtil.VimeoIframe)
        jsonObject.addProperty("url", CommonUtil.VimeoVideoUrl)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)
        jsonObject.addProperty(ApiRequestNames.Req_receviedit, CommonUtil.receiverid)
        appViewModel!!.VideoParticulerSend(jsonObject, this)
        Log.d("SMSJsonObjectparticuler", jsonObject.toString())

    }

    private fun VideosendParticulerTuter() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)
        jsonObject.addProperty(ApiRequestNames.Req_title, CommonUtil.title)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.Description)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty("iframe", CommonUtil.VimeoIframe)
        jsonObject.addProperty("url", CommonUtil.VimeoVideoUrl)
        jsonObject.addProperty(ApiRequestNames.Req_receviedit, CommonUtil.receiverid)
        appViewModel!!.VideoSendtuter(jsonObject, this)
        Log.d("VideoSendtuter", jsonObject.toString())

    }

    private fun EventsendTuter(prossertype: String) {
        val jsonObject = JsonObject()

        if (prossertype.equals("edit")) {
            jsonObject.addProperty(ApiRequestNames.Req_eventid, CommonUtil.EventParticulerId)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_eventid, "0")

        }

        jsonObject.addProperty(ApiRequestNames.Req_eventid, "0")
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_eventdate, CommonUtil.Date)
        jsonObject.addProperty(ApiRequestNames.Req_eventtime, CommonUtil.Time)
        jsonObject.addProperty(ApiRequestNames.Req_eventbody, CommonUtil.MenuDescription)
        jsonObject.addProperty(ApiRequestNames.Req_eventtopic, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_processtype, prossertype)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty(ApiRequestNames.Req_eventvenue, CommonUtil.Venuetext)
        jsonObject.addProperty(ApiRequestNames.Req_callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_receiveridlist, CommonUtil.receiverid)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)

        appViewModel!!.EventsendTuter(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }

    private fun Eventsend(prossertype: String) {
        val jsonObject = JsonObject()

        if (prossertype.equals("edit")) {
            jsonObject.addProperty(ApiRequestNames.Req_eventid, CommonUtil.EventParticulerId)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_eventid, "0")
        }

        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_eventdate, CommonUtil.Date)
        jsonObject.addProperty(ApiRequestNames.Req_eventtime, CommonUtil.Time)
        jsonObject.addProperty(ApiRequestNames.Req_eventbody, CommonUtil.MenuDescription)
        jsonObject.addProperty(ApiRequestNames.Req_eventtopic, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_processtype, prossertype)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty(ApiRequestNames.Req_eventvenue, CommonUtil.Venuetext)
        jsonObject.addProperty(ApiRequestNames.Req_callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_receiveridlist, CommonUtil.receiverid)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)

        appViewModel!!.Eventsend(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }


    private fun Assignmentforward() {


        val jsonObject = JsonObject()

        jsonObject.addProperty("collegeid", CommonUtil.CollegeId)
        jsonObject.addProperty("deptid", CommonUtil.DepartmentId)
        jsonObject.addProperty("courseid", CommonUtil.Courseid)
        jsonObject.addProperty("yearid", CommonUtil.YearId)
        jsonObject.addProperty("staffid", CommonUtil.MemberId)
        jsonObject.addProperty("callertype", CommonUtil.Priority)
        jsonObject.addProperty("sectionid", CommonUtil.SectionId)
        jsonObject.addProperty("subjectid", CommonUtil.SubjectID)
        jsonObject.addProperty("assignmenttopic", CommonUtil.Assignmenttitle)
        jsonObject.addProperty("assignmentdescription", CommonUtil.AssignmentDescription)
        jsonObject.addProperty("submissiondate", CommonUtil.startdate)
        jsonObject.addProperty("processtype", "add")
        jsonObject.addProperty("assignmentid", CommonUtil.Assignmentid)

        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)

        jsonObject.addProperty("receiverid", CommonUtil.receiverid)


        if (CommonUtil.AssignmentType.equals("text") || CommonUtil.AssignmentType.equals("Text")) {

            jsonObject.addProperty("assignmenttype", "text")

            val FileNameArray = JsonArray()
            val FileNameobject = JsonObject()

            FileNameobject.addProperty("FileName", "")
            FileNameArray.add(FileNameobject)
            jsonObject.add("FileNameArray", FileNameArray)

        } else {


            if (CommonUtil.AssignmentType.equals("image")) {

                jsonObject.addProperty("assignmenttype", "image")
                val FileNameArray = JsonArray()

                for (i in CommonUtil.ForwardAssignment.indices) {
                    val FileNameobject = JsonObject()
                    FileNameobject.addProperty(
                        "FileName", CommonUtil.ForwardAssignment[i].toString()
                    )
                    FileNameArray.add(FileNameobject)
                }
                jsonObject.add("FileNameArray", FileNameArray)

            } else if (CommonUtil.AssignmentType.equals("pdf")) {
                jsonObject.addProperty("assignmenttype", "pdf")

                val FileNameArray = JsonArray()
                for (i in CommonUtil.ForwardAssignment.indices) {
                    val FileNameobject = JsonObject()
                    FileNameobject.addProperty("FileName", CommonUtil.ForwardAssignment[i])
                    FileNameArray.add(FileNameobject)
                }
                jsonObject.add("FileNameArray", FileNameArray)

            } else if (CommonUtil.AssignmentType.equals("video")) {


                jsonObject.addProperty("assignmenttype", "video")

                val FileNameArray = JsonArray()
                val FileNameobject = JsonObject()

                FileNameobject.addProperty("FileName", CommonUtil.VimeoIframe)
                Log.d("Videoiframe", CommonUtil.VimeoIframe.toString())

                FileNameArray.add(FileNameobject)
                jsonObject.add("FileNameArray", FileNameArray)

            }

        }

        if (CommonUtil.AssignmentType.equals("text") || CommonUtil.AssignmentType.equals("Text")) {

            appViewModel!!.AssignmentsendForwardText(jsonObject, this)
            Log.d("SMSJsonObject", jsonObject.toString())

        } else {

            appViewModel!!.Assignmentsend(jsonObject, this)
            Log.d("SMSJsonObject", jsonObject.toString())

        }


    }

    private fun AssignmentsendEntireSection() {

        val jsonObject = JsonObject()

        jsonObject.addProperty("collegeid", CommonUtil.CollegeId)
        jsonObject.addProperty("deptid", CommonUtil.DepartmentId)
        jsonObject.addProperty("courseid", CommonUtil.Courseid)
        jsonObject.addProperty("yearid", CommonUtil.YearId)
        jsonObject.addProperty("staffid", CommonUtil.MemberId)
        jsonObject.addProperty("callertype", CommonUtil.Priority)
        jsonObject.addProperty("sectionid", CommonUtil.SectionId)
        jsonObject.addProperty("subjectid", CommonUtil.SubjectID)
        jsonObject.addProperty("assignmenttopic", CommonUtil.title)
        jsonObject.addProperty("assignmentdescription", CommonUtil.Description)
        jsonObject.addProperty("submissiondate", CommonUtil.startdate)
        jsonObject.addProperty("processtype", "add")
        jsonObject.addProperty("assignmentid", "0")

        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)
        jsonObject.addProperty("receiverid", CommonUtil.receiverid)

        if (FileType.equals("IMAGE")) {

            jsonObject.addProperty("assignmenttype", "image")
            val FileNameArray = JsonArray()

            for (i in AWSUploadedFilesList.indices) {
                val FileNameobject = JsonObject()
                FileNameobject.addProperty("FileName", AWSUploadedFilesList[i].filepath)
                FileNameArray.add(FileNameobject)
            }
            jsonObject.add("FileNameArray", FileNameArray)

        } else if (FileType.equals("PDF")) {
            jsonObject.addProperty("assignmenttype", "pdf")

            val FileNameArray = JsonArray()
            for (i in AWSUploadedFilesList.indices) {
                val FileNameobject = JsonObject()
                FileNameobject.addProperty("FileName", AWSUploadedFilesList[i].filepath)
                FileNameArray.add(FileNameobject)
            }
            jsonObject.add("FileNameArray", FileNameArray)

        } else if (FileType.equals("VIDEO")) {


            jsonObject.addProperty("assignmenttype", "video")

            val FileNameArray = JsonArray()
            val FileNameobject = JsonObject()

            FileNameobject.addProperty("FileName", CommonUtil.VimeoIframe)
            Log.d("Videoiframe", CommonUtil.VimeoIframe.toString())

            FileNameArray.add(FileNameobject)
            jsonObject.add("FileNameArray", FileNameArray)

        } else if (FileType.equals("TEXT")) {

            jsonObject.addProperty("assignmenttype", "text")

            val FileNameArray = JsonArray()
            val FileNameobject = JsonObject()

            FileNameobject.addProperty("FileName", "")
            FileNameArray.add(FileNameobject)
            jsonObject.add("FileNameArray", FileNameArray)
        }

        appViewModel!!.Assignmentsend(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }

    private fun SmsToParticularTypeRequest() {
        val jsonObject = JsonObject()

        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_filetype, "1")
        jsonObject.addProperty(ApiRequestNames.Req_MessageContent, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.MenuDescription)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)
        jsonObject.addProperty(ApiRequestNames.Req_receviedit, CommonUtil.receiverid)

        appViewModel!!.SendSmsToParticularType(jsonObject, this)
        Log.d("SMSJsonObjectParticuler", jsonObject.toString())
    }

    private fun NoticeBoardSMSsending() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_noticeboardid, "0")
        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)
        jsonObject.addProperty(ApiRequestNames.Req_receiveridlist, CommonUtil.receiverid)
        jsonObject.addProperty(ApiRequestNames.Req_topic, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.MenuDescription)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_processtype, "add")
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        val FileNameArray = JsonArray()
        if (!CommonUtil.urlFromS3.equals(null)) {
            for (i in AWSUploadedFilesList.indices) {
                val FileNameobject = JsonObject()
                FileNameobject.addProperty("filepath", AWSUploadedFilesList[i].filepath)
                if (CommonUtil.urlFromS3!!.contains(".pdf")) {
                    FileNameobject.addProperty(ApiRequestNames.Req_filetype, "pdf")
                } else {
                    FileNameobject.addProperty(ApiRequestNames.Req_filetype, "image")
                }
                FileNameArray.add(FileNameobject)
            }
        }
        jsonObject.add("files", FileNameArray)


        appViewModel!!.NoticeBoardsmssending(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }

    private fun NoticeBoardSMSsendingTuter() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_noticeboardid, "0")
        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)
        jsonObject.addProperty(ApiRequestNames.Req_receiveridlist, CommonUtil.receiverid)
        jsonObject.addProperty(ApiRequestNames.Req_topic, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.MenuDescription)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_processtype, "add")
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)

        val FileNameArray = JsonArray()
        if (!CommonUtil.urlFromS3.equals(null)) {
            for (i in AWSUploadedFilesList.indices) {
                val FileNameobject = JsonObject()
                FileNameobject.addProperty("filepath", AWSUploadedFilesList[i].filepath)
                if (CommonUtil.urlFromS3!!.contains(".pdf")) {
                    FileNameobject.addProperty(ApiRequestNames.Req_filetype, "pdf")
                } else {
                    FileNameobject.addProperty(ApiRequestNames.Req_filetype, "image")
                }
                FileNameArray.add(FileNameobject)
            }
        }
        jsonObject.add("files", FileNameArray)

        appViewModel!!.NoticeBoardsmssendingTuter(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }

    private fun SmsToEntireCollegesubjectandtuterRequest() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_filetype, "1")
        jsonObject.addProperty(ApiRequestNames.Req_MessageContent, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)
        jsonObject.addProperty(ApiRequestNames.Req_receviedit, CommonUtil.receiverid)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.MenuDescription)

        appViewModel!!.SendSmsToEntiretutorandsubjectCollege(jsonObject, this)
        Log.d("SMSJsonObjectEntire", jsonObject.toString())
    }


    fun awsFileUpload(activity: Activity?, pathind: Int?) {

        Log.d("SelcetedFileList", CommonUtil.SelcetedFileList.size.toString())
        val s3uploaderObj: S3Uploader
        s3uploaderObj = S3Uploader(activity)
        pathIndex = pathind!!

        for (index in pathIndex until CommonUtil.SelcetedFileList.size) {
            uploadFilePath = CommonUtil.SelcetedFileList.get(index)
            Log.d("uploadFilePath", uploadFilePath.toString())
            val extension = uploadFilePath!!.substring(uploadFilePath!!.lastIndexOf("."))
            contentType = if (extension.equals(".pdf")) {
                ".pdf"
            } else {
                ".jpg"
            }
            break
        }

        if (AWSUploadedFilesList.size < CommonUtil.SelcetedFileList.size) {
            Log.d("test", uploadFilePath!!)
            if (uploadFilePath != null) {
                progressDialog = CustomLoading.createProgressDialog(this)

                progressDialog!!.show()
                fileNameDateTime =
                    SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())
                fileNameDateTime = "File_" + fileNameDateTime
                Log.d("filenamedatetime", fileNameDateTime.toString())
                s3uploaderObj.initUpload(
                    uploadFilePath, contentType, CommonUtil.CollegeId.toString(), fileNameDateTime
                )

                s3uploaderObj.setOns3UploadDone(object : S3Uploader.S3UploadInterface {
                    override fun onUploadSuccess(response: String?) {
                        if (response!!.equals("Success")) {

                            CommonUtil.urlFromS3 = S3Utils.generates3ShareUrl(
                                this@SpecificStudent,
                                CommonUtil.CollegeId.toString(),
                                uploadFilePath,
                                fileNameDateTime
                            )

                            Log.d("urifroms3", CommonUtil.urlFromS3.toString())

                            if (!TextUtils.isEmpty(CommonUtil.urlFromS3)) {


                                Awsuploadedfile.add(CommonUtil.urlFromS3.toString())
                                Awsaupladedfilepath = Awsuploadedfile.joinToString(separator)


                                fileName = File(uploadFilePath)

                                filename = fileName!!.name
                                AWSUploadedFilesList.add(
                                    AWSUploadedFiles(
                                        CommonUtil.urlFromS3!!, filename, contentType
                                    )
                                )

                                Log.d("AWSUploadedFilesList", AWSUploadedFilesList.toString())
                                awsFileUpload(activity, pathIndex + 1)

                                if (CommonUtil.SelcetedFileList.size == AWSUploadedFilesList.size) {
                                    progressDialog!!.dismiss()
                                }
                            }
                        }
                    }

                    override fun onUploadError(response: String?) {
                        progressDialog!!.dismiss()
                    }
                })
            }

        } else {

            if (CommonUtil.ScreenType.equals(CommonUtil.Image_Pdf)) {
                CommonUtil.receivertype = "7"
                if (CommonUtil.SpecificButton.equals(CommonUtil.Subjects)) {
                    ImageOrPdfsendparticuler()
                } else if (CommonUtil.SpecificButton.equals(CommonUtil.Tutor)) {
                    ImageOrPdfsendparticulerTuter()
                }
            } else if (CommonUtil.SpecificButton.equals(CommonUtil.New_Assignment)) {
                CommonUtil.receivertype = "2"
                AssignmentsendEntireSection()
            } else if (CommonUtil.SpecificButton.equals(CommonUtil.Forward_Assignment)) {
                CommonUtil.receivertype = "2"
                Assignmentforward()


            } else if (CommonUtil.ScreenType.equals(CommonUtil.Noticeboard)) {
                CommonUtil.receivertype = "7"
                if (CommonUtil.SpecificButton.equals(CommonUtil.Subjects)) {
                    NoticeBoardSMSsending()
                } else if (CommonUtil.SpecificButton.equals(CommonUtil.Tutor)) {
                    NoticeBoardSMSsendingTuter()
                }
            }
        }
    }

    private fun ImageOrPdfsendparticuler() {

        val jsonObject = JsonObject()
        jsonObject.addProperty("collegeid", CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)

        if (CommonUtil.urlFromS3!!.contains(".pdf")) {
            jsonObject.addProperty(ApiRequestNames.Req_filetype, "3")
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_filetype, "2")
        }

        jsonObject.addProperty(ApiRequestNames.Req_isStudent, true)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, false)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, false)

        jsonObject.addProperty(ApiRequestNames.Req_fileduraction, "0")
        jsonObject.addProperty(ApiRequestNames.Req_title, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_description, CommonUtil.MenuDescription)
        jsonObject.addProperty("receiverid", CommonUtil.receiverid)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)

        val FileNameArray = JsonArray()

        for (i in AWSUploadedFilesList.indices) {
            val FileNameobject = JsonObject()
            FileNameobject.addProperty("FileName", AWSUploadedFilesList[i].filepath)
            FileNameArray.add(FileNameobject)
        }
        jsonObject.add("FileNameArray", FileNameArray)

        appViewModel!!.ImageorPdfparticuler(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }

    private fun ImageOrPdfsendparticulerTuter() {

        val jsonObject = JsonObject()
        jsonObject.addProperty("collegeid", CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)

        if (CommonUtil.urlFromS3!!.contains(".pdf")) {
            jsonObject.addProperty(ApiRequestNames.Req_filetype, "3")
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_filetype, "2")
        }
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, true)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, false)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, false)
        jsonObject.addProperty(ApiRequestNames.Req_fileduraction, "0")
        jsonObject.addProperty(ApiRequestNames.Req_title, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_description, CommonUtil.MenuDescription)

        jsonObject.addProperty("receiverid", CommonUtil.receiverid)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)

        val FileNameArray = JsonArray()

        for (i in AWSUploadedFilesList.indices) {
            val FileNameobject = JsonObject()
            FileNameobject.addProperty("FileName", AWSUploadedFilesList[i].filepath)
            FileNameArray.add(FileNameobject)
        }
        jsonObject.add("FileNameArray", FileNameArray)

        appViewModel!!.Tuterimageorpdfsend(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }

    private fun VoiceHistoryEntireSend() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.Description)
        jsonObject.addProperty("isemergencyvoice", CommonUtil.CallEnable)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty(ApiRequestNames.Req_filetype, "1")
        jsonObject.addProperty("forwarding_voice_id", CommonUtil.voiceHeadedId)
        appViewModel!!.SendVoiceToEntireHistory(jsonObject, this)
        Log.d("VoiceToEntireHistory", jsonObject.toString())
    }

    private fun SendVoiceToParticulerHistory() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.Description)
        jsonObject.addProperty("isemergencyvoice", CommonUtil.CallEnable)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty(ApiRequestNames.Req_filetype, "1")
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)
        jsonObject.addProperty(ApiRequestNames.Req_receviedit, CommonUtil.receiverid)
        jsonObject.addProperty("forwarding_voice_id", CommonUtil.voiceHeadedId)
        appViewModel!!.SendVoiceToParticulerHistory(jsonObject, this)
        Log.d("VoiceToEntireHistory", jsonObject.toString())
    }


    private fun VoiceSendParticuler() {
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()

        val jsonObject = JsonObject()

        jsonObject.addProperty("collegeid", CommonUtil.CollegeId)
        jsonObject.addProperty("staffid", CommonUtil.MemberId)
        jsonObject.addProperty("callertype", CommonUtil.Priority)
        jsonObject.addProperty("filetype", "1")
        jsonObject.addProperty("fileduration", CommonUtil.VoiceDuration)
        jsonObject.addProperty("isparent", isParent)
        jsonObject.addProperty("isstudent", isStudent)
        jsonObject.addProperty("isstaff", isStaff)
        jsonObject.addProperty("description", CommonUtil.voicetitle)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)
        jsonObject.addProperty(ApiRequestNames.Req_receviedit, CommonUtil.receiverid)
        jsonObject.addProperty("isemergencyvoice", CommonUtil.VoiceType)

        Log.d("VoiceSend:req", jsonObject.toString())

        val file: File = File(CommonUtil.futureStudioIconFile!!.path)
        Log.d("FILE_Path", CommonUtil.futureStudioIconFile!!.path)

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val bodyFile: MultipartBody.Part =
            MultipartBody.Part.createFormData("voice", file.name, requestFile)
        val requestBody: RequestBody = RequestBody.create(MultipartBody.FORM, jsonObject.toString())

        RestClient.apiInterfaces.UploadVoicefileParticuler(requestBody, bodyFile)
            .enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>, response: Response<JsonObject>
                ) {
                    try {
                        if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                        run {
                            Log.d(
                                "voicesend:code-res",
                                response.code().toString() + " - " + response.toString()
                            )

                            if (response.code() == 200 || response.code() == 201) {

                                val js = JSONObject(response.body().toString())

                                var status: String? = null
                                status = js.getString("Status")

                                if (status.equals("1")) {
                                    var message: String? = null
                                    message = js.getString("Message")

                                    val dlg = this@SpecificStudent.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent =

                                                Intent(
                                                    this@SpecificStudent, Communication::class.java
                                                )
                                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(i)
                                        })

                                    dlg.setCancelable(false)
                                    dlg.create()
                                    dlg.show()

                                } else {
                                    var message: String? = null
                                    message = js.getString("Message")

                                    val dlg = this@SpecificStudent.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent =

                                                Intent(
                                                    this@SpecificStudent, Communication::class.java
                                                )
                                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(i)
                                        })

                                    dlg.setCancelable(false)
                                    dlg.create()
                                    dlg.show()

                                }
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        Log.e("Response Exception", e.message!!)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    mProgressDialog.dismiss()
                }
            })
    }

    private fun VoiceSendTuter() {
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()

        val jsonObject = JsonObject()
        jsonObject.addProperty("collegeid", CommonUtil.CollegeId)
        jsonObject.addProperty("staffid", CommonUtil.MemberId)
        jsonObject.addProperty("callertype", CommonUtil.Priority)
        jsonObject.addProperty("filetype", "1")
        jsonObject.addProperty("fileduration", CommonUtil.VoiceDuration)
        jsonObject.addProperty("isparent", isParent)
        jsonObject.addProperty("isstudent", isStudent)
        jsonObject.addProperty("isstaff", isStaff)
        jsonObject.addProperty("description", CommonUtil.voicetitle)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)
        jsonObject.addProperty(ApiRequestNames.Req_receviedit, CommonUtil.receiverid)
        jsonObject.addProperty("isemergencyvoice", CommonUtil.VoiceType)

        Log.d("VoiceSend:req", jsonObject.toString())

        val file: File = File(CommonUtil.futureStudioIconFile!!.getPath())
        Log.d("FILE_Path", CommonUtil.futureStudioIconFile!!.getPath())

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val bodyFile: MultipartBody.Part =
            MultipartBody.Part.createFormData("voice", file.name, requestFile)
        val requestBody: RequestBody = RequestBody.create(MultipartBody.FORM, jsonObject.toString())

        RestClient.apiInterfaces.UploadVoicefileTuterSend(requestBody, bodyFile)
            .enqueue(object : Callback<JsonObject> {

                override fun onResponse(
                    call: Call<JsonObject>, response: Response<JsonObject>
                ) {
                    try {
                        if (mProgressDialog.isShowing) mProgressDialog.dismiss()
                        run {
                            Log.d(
                                "voicesend:code-res",
                                response.code().toString() + " - " + response.toString()
                            )

                            if (response.code() == 200 || response.code() == 201) {

                                val js = JSONObject(response.body().toString())

                                var status: String? = null
                                status = js.getString("Status")

                                if (status.equals("1")) {
                                    var message: String? = null
                                    message = js.getString("Message")

                                    val dlg = this@SpecificStudent.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent = Intent(
                                                this@SpecificStudent, Communication::class.java
                                            )
                                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(i)
                                        })

                                    dlg.setCancelable(false)
                                    dlg.create()
                                    dlg.show()

                                } else {
                                    var message: String? = null
                                    message = js.getString("Message")

                                    val dlg = this@SpecificStudent.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent =

                                                Intent(
                                                    this@SpecificStudent, Communication::class.java
                                                )
                                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(i)
                                        })

                                    dlg.setCancelable(false)
                                    dlg.create()
                                    dlg.show()

                                }
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        Log.e("Response Exception", e.message!!)
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    mProgressDialog.dismiss()
                }
            })
    }

    override fun onResume() {
        SelectedRecipientlist.clear()
        CommonUtil.seleteddataArrayCheckbox.clear()
        super.onResume()
    }

    override fun onUploadComplete(success: Boolean, isIframe: String, isLink: String) {
        Log.d("Vimeo_Video_upload", success.toString())
        Log.d("VimeoIframe", isIframe)
        Log.d("link", isLink)

        if (success) {
            CommonUtil.VimeoIframe = isIframe
            CommonUtil.VimeoVideoUrl = isLink
            Log.d("isIframe", CommonUtil.VimeoIframe.toString())
            Log.d("VimeoVideoUrl", CommonUtil.VimeoVideoUrl.toString())
            if (CommonUtil.Priority.equals("p1")) {
                if (CommonUtil.SpecificButton == CommonUtil.Subjects) {
                    VideosendParticuler()
                } else if (CommonUtil.SpecificButton == CommonUtil.Tutor) {
                    VideosendParticulerTuter()
                }
            }
        } else {
            CommonUtil.ApiAlertContext(this, "Video sending failed. Please try again.")
        }
    }


    override val layoutResourceId: Int
        get() = R.layout.activity_specific_student
}