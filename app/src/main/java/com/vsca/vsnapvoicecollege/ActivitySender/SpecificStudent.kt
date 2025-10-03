package com.vsca.vsnapvoicecollege.ActivitySender

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.AWS.AwsUploadingPreSigned
import com.vsca.vsnapvoicecollege.AWS.UploadCallback
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
import com.vsca.vsnapvoicecollege.Interfaces.RecipientCheckListener
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
import com.vsca.vsnapvoicecollege.databinding.ActivitySpecificStudentBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SpecificStudent : ActionBarActivity(),
    VimeoUploader.UploadCompletionListener {


    var SpecificStudentList: SelectedRecipientAdapter? = null
    var appViewModel: App? = null
    var getspecifictuterstudent: List<specificStudent_datalist> = ArrayList()
    var specificStudent_adapter: specificStudent_adapter? = null
//    var AWSUploadedFilesList = java.util.ArrayList<AWSUploadedFiles>()
    var AWSUploadedFilesList = java.util.ArrayList<String>()
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
    private lateinit var binding: ActivitySpecificStudentBinding
    var isAwsUploadingPreSigned: AwsUploadingPreSigned? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivitySpecificStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
         ActionbarWithoutBottom(this)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        CommonUtil.CallEnable = "0"

        val VideoToken = SharedPreference.getVideo_Json(this).toString()
        isAwsUploadingPreSigned = AwsUploadingPreSigned()
        isVideoToken = VideoToken
        Log.d("isVideoToken", VideoToken)

        binding.btnRecipientCancel.setOnClickListener { cancelClick() }
        binding.btnConfirm.setOnClickListener { SendButtonAPi() }

        binding.switchonAndoff!!.setOnClickListener {
            if (binding.switchonAndoff!!.isChecked) {
                CommonUtil.CallEnable = "1"
            } else {
                CommonUtil.CallEnable = "0"
            }
        }
        binding.lblSubjectName!!.text = CommonUtil.courseName_
        binding.lblSubjectType!!.text = CommonUtil.year_
        binding.lblSubjectCategory!!.text = CommonUtil.semester_
        binding.lblSubjectCredits!!.text = CommonUtil.section_
        FileType = intent.getStringExtra("FileType")

        isStudent = true
        isStaff = false
        isParent = false

        if (CommonUtil.isParentEnable.equals("1")) {
            binding.lnrTargetParent!!.visibility = View.VISIBLE
        } else {
            binding.lnrTargetParent!!.visibility = View.GONE
            isParent = false
        }

        binding.chboxParents!!.setOnClickListener {

            if (binding.chboxParents!!.isChecked) {
                isParent = true

                if (isParent!! && isStudent!!) {
                    binding.chboxAllSpecific!!.isChecked = true

                } else {
                    binding.chboxAllSpecific!!.isChecked = false
                }

            } else {
                isParent = false

                binding.chboxAllSpecific!!.isChecked = false
            }
        }

        binding.chboxAllSpecific!!.setOnClickListener {

            if (binding.chboxAllSpecific!!.isChecked) {

                if (CommonUtil.isParentEnable == "1") {

                    isStudent = true
                    isStaff = true
                    isParent = true

                    binding.chboxStudent!!.isChecked = true
                    binding.chboxParents!!.isChecked = true
                    binding.chboxStaff!!.isChecked = true


                } else {

                    isStudent = true
                    isStaff = true
                    isParent = false

                    binding.chboxStudent!!.isChecked = true
                    binding.chboxParents!!.isChecked = false
                    binding.chboxStaff!!.isChecked = true

                }
            } else {

                isParent = false
                isStudent = false
                isStaff = false

                binding.chboxStudent!!.isChecked = false
                binding.chboxParents!!.isChecked = false
                binding.chboxStaff!!.isChecked = false

            }
        }
        if (CommonUtil.isAllowtomakecall == 1) {
            when (CommonUtil.ScreenType) {
                CommonUtil.Text -> binding.txtOnandoff!!.visibility = View.GONE
                CommonUtil.TextHistory -> binding.txtOnandoff!!.visibility = View.GONE
                CommonUtil.Communication -> binding.txtOnandoff!!.visibility = View.GONE
                CommonUtil.CommunicationVoice -> binding.txtOnandoff!!.visibility = View.VISIBLE
                CommonUtil.New_Video -> binding.txtOnandoff!!.visibility = View.GONE
                CommonUtil.Noticeboard -> binding.txtOnandoff!!.visibility = View.GONE
                CommonUtil.ScreenNameEvent -> binding.txtOnandoff!!.visibility = View.GONE
                CommonUtil.Image_Pdf -> binding.txtOnandoff!!.visibility = View.GONE
                CommonUtil.New_Assignment -> binding.txtOnandoff!!.visibility = View.GONE
                CommonUtil.Forward_Assignment -> binding.txtOnandoff!!.visibility = View.GONE
            }
        }

        binding.chBoxAll!!.setOnClickListener(View.OnClickListener {

            CommonUtil.seleteddataArrayCheckbox.clear()
            if (binding.chBoxAll!!.isChecked) {
                SpecificStudentList!!.selectAll()
                binding.txtChBoxAll!!.text = CommonUtil.Remove_All
            } else {
                SpecificStudentList!!.unselectall()
                binding.txtChBoxAll!!.text = CommonUtil.Select_All

            }
        })

        binding.chboxStaff!!.setOnClickListener {
            if (CommonUtil.isParentEnable.equals("1")) {
                if (binding.chboxStaff!!.isChecked) {
                    isStaff = true
                    if (isParent!! && isStudent!! && isStaff!!) {
                        binding.chboxAllSpecific!!.isChecked = true
                    } else {
                        binding.chboxAllSpecific!!.isChecked = false
                    }
                } else {
                    isStaff = false
                    binding.chboxAllSpecific!!.isChecked = false
                }

            } else {

                if (binding.chboxStaff!!.isChecked) {
                    isStaff = true

                    if (isStudent!! && isStaff!!) {
                        binding.chboxAllSpecific!!.isChecked = true
                    } else {
                        binding.chboxAllSpecific!!.isChecked = false
                    }
                } else {
                    isStaff = false
                    binding.chboxAllSpecific!!.isChecked = false
                }
            }
        }


        binding.chboxStudent!!.setOnClickListener {


            if (CommonUtil.isParentEnable.equals("1")) {

                if (binding.chboxStudent!!.isChecked) {

                    isStudent = true

                    if (isParent!! && isStudent!!) {
                        binding.chboxAllSpecific!!.isChecked = true

                    } else {
                        binding.chboxAllSpecific!!.isChecked = false
                    }
                } else {
                    isStudent = false

                    binding.chboxAllSpecific!!.isChecked = false
                }

            } else {

                if (binding.chboxStudent!!.isChecked) {
                    isStudent = true

                    if (isStudent!!) {
                        binding.chboxAllSpecific!!.isChecked = true
                    } else {
                        binding.chboxAllSpecific!!.isChecked = false
                    }

                } else {
                    isStudent = false
                    binding.chboxAllSpecific!!.isChecked = false
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

        binding.idSV!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                    binding.idSV!!.visibility = View.VISIBLE
                    binding.chBoxAll!!.visibility = View.VISIBLE
                    binding.txtChBoxAll!!.visibility = View.VISIBLE
                    SelectedRecipientlist.clear()


                    getspecifictuterstudent.forEach {
                        it.memberid
                        it.name

                        val group = RecipientSelected(it.memberid, it.name, it.regno)
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

                                    binding.chBoxAll!!.isChecked = true
                                    binding.txtChBoxAll!!.setText(CommonUtil.Remove_All)

                                } else {

                                    binding.chBoxAll!!.isChecked = false
                                    binding.txtChBoxAll!!.setText(CommonUtil.Select_All)

                                }

                            }

                            override fun remove(data: RecipientSelected?) {
                                var groupid = data!!.SelectedId

                                binding.chBoxAll!!.isChecked = false
                                binding.txtChBoxAll!!.setText(CommonUtil.Select_All)
                            }
                        })

                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.recycleSpecific!!.layoutManager = mLayoutManager
                    binding.recycleSpecific!!.itemAnimator = DefaultItemAnimator()
                    binding.recycleSpecific!!.adapter = SpecificStudentList
                    binding.recycleSpecific!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    SpecificStudentList!!.notifyDataSetChanged()
                } else {

                    binding.idSV!!.visibility = View.GONE
                    binding.recycleSpecific!!.visibility = View.GONE
                    binding.chBoxAll!!.visibility = View.GONE
                    binding.txtChBoxAll!!.visibility = View.GONE
                }
            } else {

                binding.idSV!!.visibility = View.GONE
                binding.chBoxAll!!.visibility = View.GONE
                binding.txtChBoxAll!!.visibility = View.GONE
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

     fun cancelClick() {
        onBackPressed()
        CommonUtil.DepartmentChooseIds.clear()
        CommonUtil.DepartmentChooseIds.add(CommonUtil.SectionIdChoose)
        CommonUtil.receiverid = CommonUtil.Onbackpressed
    }

    fun SendButtonAPi() {


        Log.d("isStudent", isStudent.toString())
        Log.d("isStaff", isStaff.toString())
        Log.d("isParent", isParent.toString())
        val SelectedCount = CommonUtil.DepartmentChooseIds.size.toString()
        CommonUtil.receivertype = "7"
        if (CommonUtil.ScreenType.equals(CommonUtil.Text)) {
            if (CommonUtil.SpecificButton == CommonUtil.Subjects) {
                if (CommonUtil.receiverid != "") {
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            //   NoticeBoardSMSsending()
//                            awsFileUpload(this, pathIndex)
                            isUploadAWS()
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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
                            //   NoticeBoardSMSsendingTuter()
//                            awsFileUpload(this, pathIndex)
                            isUploadAWS()
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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

//                            awsFileUpload(this, pathIndex)
                            isUploadAWS()

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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@SpecificStudent)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        alertDialog.setMessage(CommonUtil.StudentCount + SelectedCount)
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->
//                            awsFileUpload(this, pathIndex)
                            isUploadAWS()
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
//                    awsFileUpload(this, pathIndex)
                    isUploadAWS()
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
//                    awsFileUpload(this, pathIndex)
                    isUploadAWS()
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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
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
                    if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
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

                if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
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
                FileNameobject.addProperty("FileName", AWSUploadedFilesList[i])
                FileNameArray.add(FileNameobject)
            }
            jsonObject.add("FileNameArray", FileNameArray)

        } else if (FileType.equals("PDF")) {
            jsonObject.addProperty("assignmenttype", "pdf")

            val FileNameArray = JsonArray()
            for (i in AWSUploadedFilesList.indices) {
                val FileNameobject = JsonObject()
                FileNameobject.addProperty("FileName", AWSUploadedFilesList[i])
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
        Log.d("SMSJsonObject", jsonObject.toString())
        appViewModel!!.Assignmentsend(jsonObject, this)
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
                FileNameobject.addProperty("filepath", AWSUploadedFilesList[i])
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
                FileNameobject.addProperty("filepath", AWSUploadedFilesList[i])
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


//    fun awsFileUpload(activity: Activity?, pathind: Int?) {
//
//        Log.d("SelcetedFileList", CommonUtil.SelcetedFileList.size.toString())
//        val s3Uploader1Obj: S3Uploader1
//        s3Uploader1Obj = S3Uploader1(activity)
//        pathIndex = pathind!!
//
//        for (index in pathIndex until CommonUtil.SelcetedFileList.size) {
//            uploadFilePath = CommonUtil.SelcetedFileList.get(index)
//            Log.d("uploadFilePath", uploadFilePath.toString())
//            val extension = uploadFilePath!!.substring(uploadFilePath!!.lastIndexOf("."))
//            contentType = if (extension.equals(".pdf")) {
//                ".pdf"
//            } else {
//                ".jpg"
//            }
//            break
//        }
//
//        if (AWSUploadedFilesList.size < CommonUtil.SelcetedFileList.size) {
//            Log.d("test", uploadFilePath!!)
//            if (uploadFilePath != null) {
//                progressDialog = CustomLoading.createProgressDialog(this)
//
//                progressDialog!!.show()
//                fileNameDateTime =
//                    SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime())
//                fileNameDateTime = "File_" + fileNameDateTime
//                Log.d("filenamedatetime", fileNameDateTime.toString())
//                s3Uploader1Obj.initUpload(
//                    uploadFilePath, contentType, CommonUtil.CollegeId.toString(), fileNameDateTime
//                )
//
//                s3Uploader1Obj.setOns3UploadDone(object : S3Uploader1.S3UploadInterface {
//                    override fun onUploadSuccess(response: String?) {
//                        if (response!!.equals("Success")) {
//
//                            CommonUtil.urlFromS3 = S3Utils.generates3ShareUrl(
//                                this@SpecificStudent,
//                                CommonUtil.CollegeId.toString(),
//                                uploadFilePath,
//                                fileNameDateTime
//                            )
//
//                            Log.d("urifroms3", CommonUtil.urlFromS3.toString())
//
//                            if (!TextUtils.isEmpty(CommonUtil.urlFromS3)) {
//
//
//                                Awsuploadedfile.add(CommonUtil.urlFromS3.toString())
//                                Awsaupladedfilepath = Awsuploadedfile.joinToString(separator)
//
//
//                                fileName = File(uploadFilePath)
//
//                                filename = fileName!!.name
//                                AWSUploadedFilesList.add(
//                                    AWSUploadedFiles(
//                                        CommonUtil.urlFromS3!!, filename, contentType
//                                    )
//                                )
//
//                                Log.d("AWSUploadedFilesList", AWSUploadedFilesList.toString())
//                                awsFileUpload(activity, pathIndex + 1)
//
//                                if (CommonUtil.SelcetedFileList.size == AWSUploadedFilesList.size) {
//                                    progressDialog!!.dismiss()
//                                }
//                            }
//                        }
//                    }
//
//                    override fun onUploadError(response: String?) {
//                        progressDialog!!.dismiss()
//                    }
//                })
//            }
//
//        } else {
//
//            if (CommonUtil.ScreenType.equals(CommonUtil.Image_Pdf)) {
//                CommonUtil.receivertype = "7"
//                if (CommonUtil.SpecificButton.equals(CommonUtil.Subjects)) {
//                    ImageOrPdfsendparticuler()
//                } else if (CommonUtil.SpecificButton.equals(CommonUtil.Tutor)) {
//                    ImageOrPdfsendparticulerTuter()
//                }
//            } else if (CommonUtil.SpecificButton.equals(CommonUtil.New_Assignment)) {
//                CommonUtil.receivertype = "2"
//                AssignmentsendEntireSection()
//            } else if (CommonUtil.SpecificButton.equals(CommonUtil.Forward_Assignment)) {
//                CommonUtil.receivertype = "2"
//                Assignmentforward()
//
//
//            } else if (CommonUtil.ScreenType.equals(CommonUtil.Noticeboard)) {
//                CommonUtil.receivertype = "7"
//                if (CommonUtil.SpecificButton.equals(CommonUtil.Subjects)) {
//                    NoticeBoardSMSsending()
//                } else if (CommonUtil.SpecificButton.equals(CommonUtil.Tutor)) {
//                    NoticeBoardSMSsendingTuter()
//                }
//            }
//        }
//    }

    private fun isUploadAWS() {
        progressDialog = CustomLoading.createProgressDialog(this)
        progressDialog!!.show()
        Log.d("selectedImagePath", CommonUtil.SelcetedFileList.size.toString())
        for (i in CommonUtil.SelcetedFileList.indices) {
            AwsUploadingFile(
                CommonUtil.SelcetedFileList.get(i)
            )
        }
    }

    private fun AwsUploadingFile(
        isFilePath: String
    ) {
        Log.d("isFilePath",isFilePath.toString())
        isAwsUploadingPreSigned!!.getPreSignedUrl(
            this,
            isFilePath,
            CommonUtil.Collage_ids,
            object : UploadCallback {
                override fun onUploadSuccess(response: String?, isAwsFile: String?) {
                    Log.d("Upload Success", response!!)


                    AWSUploadedFilesList.add(isAwsFile!!)
                    Awsuploadedfile.add(isAwsFile!!.toString())
                    Awsaupladedfilepath = Awsuploadedfile.joinToString(separator)
                    if (CommonUtil.EventStatus.equals("Past")) {
                        CommonUtil.EventStatus = "Past"
                    } else {
                        CommonUtil.EventStatus = "Upcoming"
                    }
                    if (AWSUploadedFilesList.size == CommonUtil.SelcetedFileList.size) {
                        progressDialog!!.dismiss()
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

                override fun onUploadError(error: String?) {
                    Log.e("Upload Error", error!!)
                }
            })
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
            FileNameobject.addProperty("FileName", AWSUploadedFilesList[i])
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
            FileNameobject.addProperty("FileName", AWSUploadedFilesList[i])
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