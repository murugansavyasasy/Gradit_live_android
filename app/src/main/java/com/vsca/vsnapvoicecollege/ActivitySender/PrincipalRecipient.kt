package com.vsca.vsnapvoicecollege.ActivitySender

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
//import android.widget.binding.idSV
import androidx.appcompat.app.AlertDialog
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
import com.vsca.vsnapvoicecollege.Adapters.Subject_Adapter
import com.vsca.vsnapvoicecollege.Interfaces.ApiInterfaces
import com.vsca.vsnapvoicecollege.Interfaces.RecipientCheckListener
import com.vsca.vsnapvoicecollege.Model.AWSUploadedFiles
import com.vsca.vsnapvoicecollege.Model.GetGroupData
import com.vsca.vsnapvoicecollege.Model.Get_staff_yourclass
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.SenderModel.GetCourseData
import com.vsca.vsnapvoicecollege.SenderModel.GetDepartmentData
import com.vsca.vsnapvoicecollege.SenderModel.GetDivisionData
import com.vsca.vsnapvoicecollege.SenderModel.RecipientSelected
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.Utils.VimeoUploader
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityPrincipalRecipientBinding
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

class PrincipalRecipient : ActionBarActivity(), VimeoUploader.UploadCompletionListener {

    var appViewModel: App? = null
    var ScreenName: String? = null
    var isParent: Boolean? = null
    var DepartmentChoose = true
    var courseSelectAllDivision = true
    var courseSelectAllDepartment = true
    var FirstTime = true
    var isStaff: Boolean? = null
    var isStudent = true
    var SelecteRecipientType: String? = null
    var SelectedSpinnerID: String? = null
    var divisionadapter: SelectedRecipientAdapter? = null
    var GetDivisionData: ArrayList<GetDivisionData>? = null
    var groupAdapter: SelectedRecipientAdapter? = null
    var GetGroupdata: java.util.ArrayList<GetGroupData>? = null
    var SpinningCoursedatahod = java.util.ArrayList<String>()
    var subjectadapter: Subject_Adapter? = null
    var getsubjectlist: List<Get_staff_yourclass> = java.util.ArrayList()
    var SpinnerData = java.util.ArrayList<String>()
    var SpinnerDataDivision = java.util.ArrayList<String>()
    var GetDepartmentData: java.util.ArrayList<GetDepartmentData>? = null
    var departmentAdapter: SelectedRecipientAdapter? = null
    var SpinningCoursedata = java.util.ArrayList<String>()
    var GetCourseData: java.util.ArrayList<GetCourseData>? = null
    var SelectedcourseAdapter: SelectedRecipientAdapter? = null
    var SpinningText: String? = null

    //    var AWSUploadedFilesList = java.util.ArrayList<AWSUploadedFiles>()
    var AWSUploadedFilesList = java.util.ArrayList<String>()
    var Awsuploadedfile = java.util.ArrayList<String>()
    var pathIndex = 0
    var uploadFilePath: String? = null
    var contentType: String? = null
    var Spinner: String? = null
    var progressDialog: ProgressDialog? = null
    var fileNameDateTime: String? = null
    var Awsaupladedfilepath: String? = null
    var separator = ","
    var fileName: File? = null
    var filename: String? = null
    var FileType: String? = null
    var Card_name: String? = ""
    var seletedIds = ""
    var FilterCourse: ArrayList<RecipientSelected> = ArrayList()
    var FilterDepartment: ArrayList<RecipientSelected> = ArrayList()
    var DivisionIDFilter = ""
    var SearchType = ""
    var isVideoToken = ""

    private lateinit var binding: ActivityPrincipalRecipientBinding
    var isAwsUploadingPreSigned: AwsUploadingPreSigned? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalRecipientBinding.inflate(layoutInflater)
        setContentView(binding.root)
         ActionbarWithoutBottom(this)

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        isAwsUploadingPreSigned = AwsUploadingPreSigned()
        ScreenName = intent.getStringExtra("ScreenName")
        CommonUtil.ScreenType = ScreenName.toString()
        FileType = intent.getStringExtra("FileType")
        Log.d("Filetype", FileType.toString())
        CommonUtil.receivertype = ""
        CommonUtil.CallEnable = "0"
        CommonUtil.DepartmentChooseIds.clear()
        CommonUtil.iSubjectId.clear()

        val VideoToken = SharedPreference.getVideo_Json(this).toString()

        isVideoToken = VideoToken
        Log.d("isVideoToken", VideoToken)

        binding.switchonAndoff!!.setOnClickListener {
            if (binding.switchonAndoff!!.isChecked) {
                CommonUtil.CallEnable = "1"
            } else {
                CommonUtil.CallEnable = "0"
            }
        }

        if (CommonUtil.isAllowtomakecall == 1) {
            when (ScreenName) {
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

        if (ScreenName!! == CommonUtil.New_Assignment || ScreenName.equals(CommonUtil.Forward_Assignment)) {
            CommonUtil.receiverid = ""
            binding.LayoutRecipient!!.visibility = View.GONE
            binding.lnrTarget!!.visibility = View.GONE
            binding.txtTarget!!.visibility = View.GONE
            binding.txtAssignmentsent!!.visibility = View.VISIBLE
            GetSubjectprinciple()
        } else {
            binding.txtAssignmentsent!!.visibility = View.GONE
        }

        if (CommonUtil.isParentEnable.equals("1")) {
            binding.lnrTargetParent!!.visibility = View.VISIBLE
        } else {
            binding.lnrTargetParent!!.visibility = View.GONE
            isParent = false
        }

        binding.chBoxAll!!.setOnClickListener {

            if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                if (binding.chBoxAll!!.isChecked()) {
                    groupAdapter!!.selectAll()
                    binding.txtChBoxAll!!.text = CommonUtil.Remove_All
                } else {
                    groupAdapter!!.unselectall()
                    binding.txtChBoxAll!!.text = CommonUtil.Select_All
                    CommonUtil.seleteddataArrayCheckbox.clear()
                    CommonUtil.receiverid = ""
                    CommonUtil.SeletedStringdataReplace = ""
                }
            } else {
                if (binding.chBoxAll!!.isChecked()) {
                    divisionadapter!!.selectAll()
                    binding.txtChBoxAll!!.setText(CommonUtil.Remove_All)
                } else {
                    divisionadapter!!.unselectall()
                    binding.txtChBoxAll!!.setText(CommonUtil.Select_All)
                }
            }
        }

        binding.chAllDepartment!!.setOnClickListener {

            if (binding.chAllDepartment!!.isChecked()) {
                departmentAdapter!!.selectAll()
                binding.txtChDepartment!!.text = CommonUtil.Remove_All
            } else {
                departmentAdapter!!.unselectall()
                binding.txtChDepartment!!.text = CommonUtil.Select_All
                CommonUtil.seleteddataArrayCheckbox.clear()
                CommonUtil.receiverid = ""
                CommonUtil.SeletedStringdataReplace = ""
            }
        }

        binding.chAllcourse!!.setOnClickListener {

            if (binding.chAllcourse!!.isChecked()) {
                SelectedcourseAdapter!!.selectAll()
                binding.txtChcourse!!.setText(CommonUtil.Remove_All)
            } else {
                SelectedcourseAdapter!!.unselectall()
                binding.txtChcourse!!.setText(CommonUtil.Select_All)
                CommonUtil.seleteddataArrayCheckbox.clear()
                CommonUtil.receiverid = ""
                CommonUtil.SeletedStringdataReplace = ""
            }
        }

        isParent = false
        isStudent = true
        isStaff = false

        binding.chboxAll!!.setOnClickListener {

            if (binding.chboxAll!!.isChecked) {

                if (CommonUtil.isParentEnable.equals("1")) {

                    if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                            CommonUtil.Groups
                        ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                            CommonUtil.Staff
                        )
                    ) {

                        isStudent = true
                        isParent = true
                        isStaff = false

                        binding.chboxStudent!!.isChecked = true
                        binding.chboxParents!!.isChecked = true
                        binding.chboxStaff!!.isChecked = false

                    } else {

                        isStudent = true
                        isStaff = true
                        isParent = true

                        binding.chboxStudent!!.isChecked = true
                        binding.chboxParents!!.isChecked = true
                        binding.chboxStaff!!.isChecked = true

                    }

                } else {

                    if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                            CommonUtil.Groups
                        ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                            CommonUtil.Staff
                        )
                    ) {

                        isStudent = true
                        isStaff = true
                        isParent = false

                        binding.chboxStudent!!.isChecked = true
                        binding.chboxParents!!.isChecked = false
                        binding.chboxStaff!!.isChecked = true

                    } else {

                        isStudent = true
                        isStaff = true
                        isParent = false

                        binding.chboxStudent!!.isChecked = true
                        binding.chboxParents!!.isChecked = false
                        binding.chboxStaff!!.isChecked = true
                    }
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

        binding.chboxParents!!.setOnClickListener {

            if (binding.chboxParents!!.isChecked) {
                isParent = true
                if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                        CommonUtil.Groups
                    ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                        CommonUtil.Staff
                    )
                ) {
                    if (isParent!! && isStudent!! || isStaff!!) {
                        binding.chboxAll!!.isChecked = true

                    } else {
                        binding.chboxAll!!.isChecked = false
                    }

                } else {
                    if (isParent!! && isStudent!! && isStaff!!) {
                        binding.chboxAll!!.isChecked = true

                    } else {
                        binding.chboxAll!!.isChecked = false
                    }

                }

            } else {
                isParent = false

                binding.chboxAll!!.isChecked = false
            }

        }

        binding.chboxStaff!!.setOnClickListener {

            if (CommonUtil.isParentEnable.equals("1")) {

                if (binding.chboxStaff!!.isChecked) {
                    isStaff = true


                    if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                            CommonUtil.Groups
                        ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                            CommonUtil.Staff
                        )
                    ) {
                        if (isParent!! && isStudent!!) {
                            binding.chboxAll!!.isChecked = true
                        } else {
                            binding.chboxAll!!.isChecked = false
                        }
                    } else {
                        if (isParent!! && isStudent!! && isStaff!!) {
                            binding.chboxAll!!.isChecked = true
                        } else {
                            binding.chboxAll!!.isChecked = false
                        }
                    }
                } else {
                    isStaff = false
                    binding.chboxAll!!.isChecked = false
                }
            } else {
                if (binding.chboxStaff!!.isChecked) {
                    isStaff = true
                    if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                            CommonUtil.Groups
                        ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                            CommonUtil.Staff
                        )
                    ) {
                        if (isStudent!!) {
                            binding.chboxAll!!.isChecked = true
                        } else {
                            binding.chboxAll!!.isChecked = false
                        }
                    } else {
                        if (isStudent!! && isStaff!!) {
                            binding.chboxAll!!.isChecked = true
                        } else {
                            binding.chboxAll!!.isChecked = false
                        }
                    }
                } else {
                    isStaff = false
                    binding.chboxAll!!.isChecked = false
                }
            }
        }

        binding.chboxStudent!!.setOnClickListener {
            if (CommonUtil.isParentEnable.equals("1")) {
                if (binding.chboxStudent!!.isChecked) {
                    isStudent = true
                    if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                            CommonUtil.Groups
                        ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                            CommonUtil.Staff
                        )
                    ) {
                        if (isParent!! && isStudent!! || isStaff!!) {
                            binding.chboxAll!!.isChecked = true
                        } else {
                            binding.chboxAll!!.isChecked = false
                        }
                    } else {
                        if (isParent!! && isStudent!! && isStaff!!) {
                            binding.chboxAll!!.isChecked = true
                        } else {
                            binding.chboxAll!!.isChecked = false
                        }
                    }
                } else {
                    isStudent = false
                    binding.chboxAll!!.isChecked = false
                }
            } else {
                if (binding.chboxStudent!!.isChecked) {
                    isStudent = true
                    if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                            CommonUtil.Groups
                        ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                            CommonUtil.Staff
                        )
                    ) {
                        if (isStudent!! && isStaff!!) {
                            binding.chboxAll!!.isChecked = true
                        } else {
                            binding.chboxAll!!.isChecked = false
                        }
                    } else {
                        if (isStudent!! && isStaff!!) {
                            binding.chboxAll!!.isChecked = true
                        } else {
                            binding.chboxAll!!.isChecked = false
                        }
                    }
                } else {
                    isStudent = false
                    binding.chboxAll!!.isChecked = false
                }
            }
        }

        binding.txtSelectspecficStudent!!.setOnClickListener {
            if (!CommonUtil.receiverid.isEmpty()) {
                if (CommonUtil.seleteddataArraySection.size == 1) {
                    for (i in CommonUtil.seleteddataArraySection.indices) {
                        CommonUtil.receiverid = CommonUtil.seleteddataArraySection[i]
                    }
                    if (ScreenName!! == CommonUtil.New_Assignment) {
                        CommonUtil.SpecificButton = CommonUtil.New_Assignment
                    } else if (ScreenName!! == CommonUtil.Forward_Assignment) {
                        CommonUtil.SpecificButton = CommonUtil.Forward_Assignment
                    } else {
                        if (SpinningText!!.equals(CommonUtil.Subjects)) {
                            CommonUtil.SpecificButton = CommonUtil.Subjects
                            CommonUtil.receivertype = "7"
                            CommonUtil.seleteddataArrayCheckbox.clear()
                        } else if (SpinningText!!.equals(CommonUtil.Tutor)) {
                            CommonUtil.SpecificButton = CommonUtil.Tutor
                            CommonUtil.receivertype = "7"
                            CommonUtil.seleteddataArrayCheckbox.clear()
                        } else if (SpinningText!!.equals(CommonUtil.Assignment_SPECIFIC)) {
                            CommonUtil.SpecificButton = CommonUtil.Assignment_SPECIFIC
                            CommonUtil.seleteddataArrayCheckbox.clear()
                        }
                    }

                    val i = Intent(this, SpecificStudent::class.java)
                    CommonUtil.SectionIdChoose = CommonUtil.DepartmentChooseIds[0]
                    CommonUtil.DepartmentChooseIds.clear()
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    i.putExtra("FileType", FileType)
                    CommonUtil.Onbackpressed = CommonUtil.receiverid.toString()
                    startActivity(i)
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_OnlyOne)
                }
            } else {
                CommonUtil.ApiAlert(
                    this, CommonUtil.Select_MinimumOne
                )
            }
        }


        //Sms Entire

        appViewModel!!.SendSMSToEntireCollegeLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent = Intent(this, MessageCommunication::class.java)
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
                            val i: Intent = Intent(this, Communication::class.java)
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


        //particular sms

        appViewModel!!.SendSMStoParticularMutableData!!.observe(this) { response ->
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
                    dlg.setPositiveButton(CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent =

                                Intent(this, Communication::class.java)
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

        // division

        appViewModel!!.GetDivisionMutableLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message

                if (status == 1) {
                    GetDivisionData = response.data!!
                    if (GetDivisionData!!.size > 0) {
                        SelectedRecipientlist.clear()

                        GetDivisionData!!.forEach {
                            it.division_id
                            it.division_name

                            val divisions = RecipientSelected(it.division_id, it.division_name,"")
                            SelectedRecipientlist.add(divisions)
                        }
                        if (SelecteRecipientType.equals(CommonUtil.Division)) {

                            binding.recycleRecipients!!.visibility = View.VISIBLE
                            divisionadapter = SelectedRecipientAdapter(SelectedRecipientlist,
                                this,
                                object : RecipientCheckListener {
                                    override fun add(data: RecipientSelected?) {
                                        var divisionId = data!!.SelectedId

                                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                            binding.chBoxAll!!.isChecked = true
                                            binding.txtChBoxAll!!.setText(CommonUtil.Remove_All)
                                        } else {
                                            binding.chBoxAll!!.isChecked = false
                                            binding.txtChBoxAll!!.setText(CommonUtil.Select_All)
                                        }
                                    }

                                    override fun remove(data: RecipientSelected?) {
                                        var divisionId = data!!.SelectedId
                                        binding.chBoxAll!!.isChecked = false
                                        binding.txtChBoxAll!!.setText(CommonUtil.Select_All)

                                    }
                                })

                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            binding.recycleRecipients!!.layoutManager = mLayoutManager
                            binding.recycleRecipients!!.itemAnimator = DefaultItemAnimator()
                            binding.recycleRecipients!!.adapter = divisionadapter
                            binding.recycleRecipients!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            divisionadapter!!.notifyDataSetChanged()

                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }


        //group

        appViewModel!!.GetGrouplist!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    GetGroupdata = response.data!!
                    if (GetGroupdata!!.size > 0) {
                        SelectedRecipientlist.clear()

                        GetGroupdata!!.forEach {
                            it.groupid
                            it.groupname

                            var group = RecipientSelected(it.groupid, it.groupname,"")

                            SelectedRecipientlist.add(group)
                        }
                        if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                            groupAdapter = SelectedRecipientAdapter(SelectedRecipientlist,
                                this,
                                object : RecipientCheckListener {
                                    override fun add(data: RecipientSelected?) {
                                        var groupid = data!!.SelectedId
                                        Log.d("groupid", groupid.toString())

                                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {


                                            if (CommonUtil.seleteddataArrayCheckbox.size == 0) {
                                                binding.chBoxAll!!.isChecked = false
                                                binding.txtChBoxAll!!.setText(CommonUtil.Select_All)

                                            } else {
                                                binding.chBoxAll!!.isChecked = true
                                                binding.txtChBoxAll!!.setText(CommonUtil.Remove_All)


                                            }

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


                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            binding.recycleRecipients!!.layoutManager = mLayoutManager
                            binding.recycleRecipients!!.itemAnimator = DefaultItemAnimator()
                            binding.recycleRecipients!!.adapter = groupAdapter
                            binding.recycleRecipients!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            groupAdapter!!.notifyDataSetChanged()

                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
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
                    dlg.setPositiveButton(CommonUtil.OK,
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


        // Voice send Entire History

        appViewModel!!.SendVoicEnrirehistory!!.observe(this) { response ->
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


        //GET DEPARTMENT PRINCIPLE

        appViewModel!!.GetDepartmentMutableLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    GetDepartmentData = response.data!!
                    if (GetDepartmentData!!.size > 0) {
                        SelectedRecipientlist.clear()
                        LoadDivisionSpinner()

                        if (SelecteRecipientType.equals(CommonUtil.Course)) {
                            Log.d("Course", SelecteRecipientType!!)
                            LoadDepartmentSpinner()
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        //principle course

        appViewModel!!.GetCourseDepartmentMutableLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    GetCourseData = response.data
                    if (GetCourseData!!.size > 0) {
                        SelectedRecipientlist.clear()
                        LoadDivisionSpinner()
                        binding.SpinningYourclasses!!.visibility = View.VISIBLE
                        binding.spinnerDropdowncourse!!.visibility = View.VISIBLE
                        binding.recycleRecipientcourse!!.visibility = View.VISIBLE
                        binding.chAllcourse!!.visibility = View.VISIBLE
                        binding.txtChcourse!!.visibility = View.VISIBLE
                        binding.idSV!!.visibility = View.VISIBLE
                        binding.idSV!!.queryHint = "Search Course"

                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
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
                    dlg.setPositiveButton(CommonUtil.OK,
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
                    dlg.setPositiveButton(CommonUtil.OK,
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

        //ImageOrPdf send Entire

        appViewModel!!.ImageorPdf!!.observe(this) { response ->
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
                    dlg.setPositiveButton(CommonUtil.OK,
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

        //Image send Tutor

        appViewModel!!._PdfandImagesend!!.observe(this) { response ->
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
                    dlg.setPositiveButton(CommonUtil.OK,
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
                    dlg.setPositiveButton(CommonUtil.OK,
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
                    dlg.setPositiveButton(CommonUtil.OK,
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


        //Image or pdf particular send

        appViewModel!!.Imageorpdfparticuler!!.observe(this) { response ->
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
                    dlg.setPositiveButton(CommonUtil.OK,
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


        //Event send

        appViewModel!!.Eventsenddata!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    val dlg = this.let { AlertDialog.Builder(it) }
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK,
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
                    dlg.setPositiveButton(CommonUtil.OK,
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


        //subject data

        appViewModel!!.Getsubjectdata!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    binding.txtSelectspecficStudent!!.visibility = View.VISIBLE
                    binding.scrooling!!.visibility = View.VISIBLE
                    binding.recycleRecipientYourclasses!!.visibility = View.VISIBLE
                    getsubjectlist = response.data!!
                    subjectadapter = Subject_Adapter(getsubjectlist, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.recycleRecipientYourclasses!!.layoutManager = mLayoutManager
                    binding.recycleRecipientYourclasses!!.itemAnimator = DefaultItemAnimator()
                    binding.recycleRecipientYourclasses!!.adapter = subjectadapter
                    binding.recycleRecipientYourclasses!!.recycledViewPool.setMaxRecycledViews(
                        0,
                        80
                    )
                    subjectadapter!!.notifyDataSetChanged()
                } else {
                    CommonUtil.ApiAlert(
                        this, message
                    )
                    binding.recycleRecipientYourclasses!!.visibility = View.GONE
                    binding.scrooling!!.visibility = View.GONE
                }
            } else {
                CommonUtil.ApiAlert(
                    this, "Subject or Section Not allocated / Students not allocated to the section"
                )
                binding.recycleRecipientYourclasses!!.visibility = View.GONE
                binding.scrooling!!.visibility = View.GONE
            }
        }

        //tutor data

        appViewModel!!.Gettuter!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    binding.scrooling!!.visibility = View.VISIBLE
                    binding.recycleRecipientYourclasses!!.visibility = View.VISIBLE
                    binding.txtSelectspecficStudent!!.visibility = View.VISIBLE

                    getsubjectlist = response.data!!
                    subjectadapter = Subject_Adapter(getsubjectlist, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.recycleRecipientYourclasses!!.layoutManager = mLayoutManager
                    binding.recycleRecipientYourclasses!!.itemAnimator = DefaultItemAnimator()
                    binding.recycleRecipientYourclasses!!.adapter = subjectadapter
                    binding.recycleRecipientYourclasses!!.recycledViewPool.setMaxRecycledViews(
                        0,
                        80
                    )
                    subjectadapter!!.notifyDataSetChanged()
                } else {
                    CommonUtil.ApiAlert(
                        this, message
                    )
                    binding.recycleRecipientYourclasses!!.visibility = View.GONE
                    binding.scrooling!!.visibility = View.GONE
                }
            } else {
                CommonUtil.ApiAlert(
                    this, "Subject or Section Not allocated / Students not allocated to the section"
                )
                binding.recycleRecipientYourclasses!!.visibility = View.GONE
                binding.scrooling!!.visibility = View.GONE
            }
        }

        // Video send Entire

        appViewModel!!.VideosendEntire!!.observe(this) { response ->
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


        // VIDEO SEND TUTOR

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
                            val i: Intent = Intent(this, Video::class.java)
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

        binding.idSV!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {

                filter(msg)
                return false
            }
        })

        binding.lblDivision.setOnClickListener { divisionClick() }
        binding.lblDepartment.setOnClickListener { departmentClick() }
        binding.lblCourse.setOnClickListener { CourseClick() }
        binding.lblYourClasses.setOnClickListener { YourClassesClick() }
        binding.lblGroups.setOnClickListener { GroupsClick() }
        binding.lblEntireDepartmentlable.setOnClickListener { EntireClick() }
        binding.btnConfirm.setOnClickListener { SendButtonAPi() }
        binding.btnRecipientCancel.setOnClickListener { cancelClick() }
    }

    private fun filter(text: String) {

        val filteredlist: java.util.ArrayList<RecipientSelected> = java.util.ArrayList()

        when (SearchType) {
            "DivisionPositionnotZero" -> {
                for (item in FilterDepartment) {
                    if (item.SelectedName!!.toLowerCase().contains(text.toLowerCase())) {
                        filteredlist.add(item)
                    }
                }
            }

            "DivisionPositionZero" -> {
                for (item in SelectedRecipientlist) {
                    if (item.SelectedName!!.toLowerCase().contains(text.toLowerCase())) {
                        filteredlist.add(item)
                    }
                }
            }

            "CourseAll" -> {
                for (item in SelectedRecipientlist) {
                    if (item.SelectedName!!.toLowerCase().contains(text.toLowerCase())) {
                        filteredlist.add(item)
                    }
                }
            }

            "CourseParticular" -> {
                for (item in FilterDepartment) {
                    if (item.SelectedName!!.toLowerCase().contains(text.toLowerCase())) {
                        filteredlist.add(item)
                    }
                }
            }

            "DepartmentCourse" -> {
                for (item in FilterDepartment) {
                    if (item.SelectedName!!.toLowerCase().contains(text.toLowerCase())) {
                        filteredlist.add(item)
                    }
                }
            }

            "ParticularDepartment" -> {
                for (item in FilterDepartment) {
                    if (item.SelectedName!!.toLowerCase().contains(text.toLowerCase())) {
                        filteredlist.add(item)
                    }
                }
            }
        }

        if (filteredlist.isEmpty()) {
            CommonUtil.Toast(this, "No data found")
        } else {
            if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                departmentAdapter!!.filterList(filteredlist, false)
            } else {
                SelectedcourseAdapter!!.filterList(filteredlist, false)
            }
        }
    }

    private fun LoadDepartmentSpinner() {

        val DepartmentChooseName = GetCourseData!!.distinctBy { it.department_name }
        if (DepartmentChoose) {
            SpinningCoursedata.clear()
            SpinningCoursedata.add(0, "-- All Department --")
            DepartmentChooseName.forEach {
                SpinningCoursedata.add(it.department_name!!)
            }
        } else {
            SpinningCoursedata.clear()
            SpinningCoursedata.add(0, "-- All Department --")
            for (i in DepartmentChooseName.indices) {
                if (SelectedSpinnerID == DepartmentChooseName[i].division_id) {
                    SpinningCoursedata.add(DepartmentChooseName[i].department_name!!)
                }
            }
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_rextview_course, SpinningCoursedata)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_course_layout)
        binding.spinnerDropdowncourse!!.adapter = adapter
        binding.spinnerDropdowncourse!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    CommonUtil.receiverid = ""
                    CommonUtil.SeletedStringdataReplace = ""
                    FilterCourse.clear()
                    if (position != 0) {
                        courseSelectAllDepartment = false
                        binding.chAllcourse!!.visibility = View.GONE
                        binding.txtChcourse!!.visibility = View.GONE

                        val name: String = binding.spinnerDropdowncourse!!.selectedItem.toString()

                        for (i in DepartmentChooseName.indices) {
                            if (name == DepartmentChooseName[i].department_name) {
                                SelectedSpinnerID = DepartmentChooseName[i].department_id
                                Log.d("DepartmentID", SelectedSpinnerID.toString())
                                for (j in GetCourseData!!.indices) {
                                    if (GetCourseData!![j].department_name == name) {
                                        val department = RecipientSelected(
                                            GetCourseData!![j].course_id,
                                            GetCourseData!![j].course_name,""
                                        )
                                        FilterCourse.add(department)
                                    }
                                }

                                binding.recycleRecipientcourse!!.visibility = View.VISIBLE
                                binding.idSV!!.visibility = View.VISIBLE
                                binding.idSV!!.queryHint = "Search Department"
                                SearchType = "DepartmentCourse"
                                SelectedcourseAdapter = SelectedRecipientAdapter(FilterCourse,
                                    this@PrincipalRecipient,
                                    object : RecipientCheckListener {
                                        override fun add(data: RecipientSelected?) {
                                            var depaetmentid = data!!.SelectedId
                                            if (FilterCourse.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                                if (CommonUtil.seleteddataArrayCheckbox.size == 0) {
                                                    binding.chAllcourse!!.isChecked = false
                                                    binding.txtChcourse!!.text =
                                                        CommonUtil.Select_All
                                                } else {
                                                    binding.chAllcourse!!.isChecked = true
                                                    binding.txtChcourse!!.text =
                                                        CommonUtil.Remove_All
                                                }
                                            } else {
                                                binding.chAllcourse!!.isChecked = false
                                                binding.txtChcourse!!.text = CommonUtil.Select_All
                                            }
                                        }

                                        override fun remove(data: RecipientSelected?) {
                                            var departmentid = data!!.SelectedId

                                            binding.chAllcourse!!.isChecked = false
                                            binding.txtChcourse!!.text = CommonUtil.Select_All
                                        }
                                    })
                                val mLayoutManager: RecyclerView.LayoutManager =
                                    LinearLayoutManager(this@PrincipalRecipient)
                                binding.recycleRecipientcourse!!.layoutManager = mLayoutManager
                                binding.recycleRecipientcourse!!.itemAnimator =
                                    DefaultItemAnimator()
                                binding.recycleRecipientcourse!!.adapter = SelectedcourseAdapter
                                binding.recycleRecipientcourse!!.recycledViewPool.setMaxRecycledViews(
                                    0,
                                    80
                                )
                                SelectedcourseAdapter!!.notifyDataSetChanged()
                            }
                        }
                    } else {

                        if (FirstTime) {
                            courseSelectAllDepartment = true
                            for (i in GetCourseData!!.indices) {
                                if (DivisionIDFilter == GetCourseData!![i].division_id) {
                                    SelectedSpinnerID = GetCourseData!![i].department_id
                                    val department = RecipientSelected(
                                        GetCourseData!![i].course_id, GetCourseData!![i].course_name,""
                                    )
                                    FilterCourse.add(department)
                                    binding.recycleRecipientcourse!!.visibility = View.VISIBLE

                                    if (courseSelectAllDivision && courseSelectAllDepartment) {
                                        binding.chAllcourse!!.visibility = View.VISIBLE
                                        binding.txtChcourse!!.visibility = View.VISIBLE
                                    }

                                    binding.idSV!!.visibility = View.VISIBLE
                                    binding.idSV!!.queryHint = "Search Department"
                                    SearchType = "ParticularDepartment"
                                    SelectedcourseAdapter = SelectedRecipientAdapter(FilterCourse,
                                        this@PrincipalRecipient,
                                        object : RecipientCheckListener {
                                            override fun add(data: RecipientSelected?) {
                                                var depaetmentid = data!!.SelectedId
                                                if (FilterCourse.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                                    if (CommonUtil.seleteddataArrayCheckbox.size == 0) {
                                                        binding.chAllcourse!!.isChecked = false
                                                        binding.txtChcourse!!.text =
                                                            CommonUtil.Select_All
                                                    } else {
                                                        binding.chAllcourse!!.isChecked = true
                                                        binding.txtChcourse!!.text =
                                                            CommonUtil.Remove_All
                                                    }
                                                } else {
                                                    binding.chAllcourse!!.isChecked = false
                                                    binding.txtChcourse!!.text =
                                                        CommonUtil.Select_All

                                                }
                                            }

                                            override fun remove(data: RecipientSelected?) {
                                                var departmentid = data!!.SelectedId

                                                binding.chAllcourse!!.isChecked = false
                                                binding.txtChcourse!!.text = CommonUtil.Select_All
                                            }
                                        })
                                    val mLayoutManager: RecyclerView.LayoutManager =
                                        LinearLayoutManager(this@PrincipalRecipient)
                                    binding.recycleRecipientcourse!!.layoutManager = mLayoutManager
                                    binding.recycleRecipientcourse!!.itemAnimator =
                                        DefaultItemAnimator()
                                    binding.recycleRecipientcourse!!.adapter = SelectedcourseAdapter
                                    binding.recycleRecipientcourse!!.recycledViewPool.setMaxRecycledViews(
                                        0, 80
                                    )
                                    SelectedcourseAdapter!!.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
    }

    private fun LoadDivisionSpinner() {

        if (Spinner == "Department") {
            SpinnerDataDivision.clear()
            SpinnerDataDivision.add(0, "-- All Division --")
            val DivisionName = GetDepartmentData!!.distinctBy { it.division_name }
            DivisionName.forEach {
                SpinnerDataDivision.add(it.division_name!!)
            }
            binding.SpinningYourclasses!!.visibility = View.VISIBLE
            val adapter = ArrayAdapter(this, R.layout.spinner_textview, SpinnerDataDivision)
            adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
            binding.SpinningYourclasses!!.adapter = adapter
            binding.SpinningYourclasses!!.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>, view: View, position: Int, id: Long
                    ) {
                        CommonUtil.receiverid = ""
                        CommonUtil.SeletedStringdataReplace = ""
                        FilterDepartment.clear()
                        SelectedRecipientlist.clear()
                        if (position != 0) {
                            SearchType = "DivisionPositionnotZero"
                            SelectedSpinnerID = DivisionName.get(position - 1).division_id
                            DivisionName[position - 1].division_name?.let {}
                            binding.chAllDepartment!!.visibility = View.GONE
                            binding.txtChDepartment!!.visibility = View.GONE
                            courseSelectAllDivision = false

                            for (i in GetDepartmentData!!.indices) {
                                if (GetDepartmentData!![i].division_id.toString() == SelectedSpinnerID) {
                                    val department = RecipientSelected(
                                        GetDepartmentData!![i].department_id,
                                        GetDepartmentData!![i].department_name,""
                                    )
                                    FilterDepartment.add(department)
                                    binding.recycleRecipientYourclasses!!.visibility = View.VISIBLE
                                    binding.scrooling!!.visibility = View.VISIBLE
                                    departmentAdapter = SelectedRecipientAdapter(FilterDepartment,
                                        this@PrincipalRecipient,
                                        object : RecipientCheckListener {
                                            override fun add(data: RecipientSelected?) {
                                                var divisionId = data!!.SelectedId
                                            }

                                            override fun remove(data: RecipientSelected?) {
                                                var divisionId = data!!.SelectedId
                                                binding.chAllDepartment!!.isChecked = false
                                                binding.txtChDepartment!!.setText(CommonUtil.Select_All)
                                            }
                                        })

                                    val mLayoutManager: RecyclerView.LayoutManager =
                                        LinearLayoutManager(this@PrincipalRecipient)
                                    binding.recycleRecipientYourclasses!!.layoutManager =
                                        mLayoutManager
                                    binding.recycleRecipientYourclasses!!.itemAnimator =
                                        DefaultItemAnimator()
                                    binding.recycleRecipientYourclasses!!.adapter =
                                        departmentAdapter
                                    binding.recycleRecipientYourclasses!!.recycledViewPool.setMaxRecycledViews(
                                        0, 80
                                    )
                                    departmentAdapter!!.notifyDataSetChanged()
                                }
                            }
                        } else {
                            SearchType = "DivisionPositionZero"
                            GetDepartmentData!!.forEach {
                                it.department_id
                                it.department_name
                                val divisions =
                                    RecipientSelected(it.department_id, it.department_name,"")
                                SelectedRecipientlist.add(divisions)
                            }
                            binding.chAllDepartment!!.visibility = View.VISIBLE
                            binding.txtChDepartment!!.visibility = View.VISIBLE
                            binding.recycleRecipientYourclasses!!.visibility = View.VISIBLE
                            binding.scrooling!!.visibility = View.VISIBLE
                            if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size) {
                                binding.chAllDepartment!!.isChecked = true
                                binding.txtChDepartment!!.text = CommonUtil.Remove_All
                            } else {
                                binding.chAllDepartment!!.isChecked = false
                                binding.txtChDepartment!!.text = CommonUtil.Select_All
                            }
                            departmentAdapter = SelectedRecipientAdapter(SelectedRecipientlist,
                                this@PrincipalRecipient,
                                object : RecipientCheckListener {
                                    override fun add(data: RecipientSelected?) {
                                        var divisionId = data!!.SelectedId
                                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                            binding.chAllDepartment!!.isChecked = true
                                            binding.txtChDepartment!!.text = CommonUtil.Remove_All
                                        } else {
                                            binding.chAllDepartment!!.isChecked = false
                                            binding.txtChDepartment!!.text = CommonUtil.Select_All
                                        }
                                    }

                                    override fun remove(data: RecipientSelected?) {
                                        var divisionId = data!!.SelectedId
                                        binding.chAllDepartment!!.isChecked = false
                                        binding.txtChDepartment!!.setText(CommonUtil.Select_All)
                                    }
                                })
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@PrincipalRecipient)
                            binding.recycleRecipientYourclasses!!.layoutManager = mLayoutManager
                            binding.recycleRecipientYourclasses!!.itemAnimator =
                                DefaultItemAnimator()
                            binding.recycleRecipientYourclasses!!.adapter = departmentAdapter
                            binding.recycleRecipientYourclasses!!.recycledViewPool.setMaxRecycledViews(
                                0, 80
                            )
                            departmentAdapter!!.notifyDataSetChanged()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }

        } else if (Spinner == "Course") {

            SpinnerDataDivision.clear()
            SpinnerDataDivision.add(0, "-- All Division --")
            val DivisionName = GetCourseData!!.distinctBy { it.division_name }
            DivisionName.forEach {
                SpinnerDataDivision.add(it.division_name!!)
            }
            val adapter = ArrayAdapter(this, R.layout.spinner_textview, SpinnerDataDivision)
            adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
            binding.SpinningYourclasses!!.adapter = adapter
            binding.SpinningYourclasses!!.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>, view: View, position: Int, id: Long
                    ) {
                        CommonUtil.receiverid = ""
                        CommonUtil.SeletedStringdataReplace = ""
                        FilterDepartment.clear()
                        SelectedRecipientlist.clear()
                        binding.chAllDepartment!!.isChecked = false
                        binding.txtChDepartment!!.text = CommonUtil.Select_All
                        if (position != 0) {
                            binding.spinnerDropdowncourse!!.visibility = View.VISIBLE
                            courseSelectAllDivision = false
                            DepartmentChoose = false
                            FirstTime = true
                            SelectedSpinnerID = DivisionName[position - 1].division_id
                            DivisionName[position - 1].division_name?.let {}
                            Log.d("SelectedIDS", SelectedSpinnerID.toString())
                            DivisionIDFilter = SelectedSpinnerID.toString()
                            for (i in GetCourseData!!.indices) {
                                if (GetCourseData!![i].division_id.toString() == SelectedSpinnerID) {
                                    val department = RecipientSelected(
                                        GetCourseData!![i].course_id, GetCourseData!![i].course_name,""
                                    )
                                    FilterDepartment.add(department)
                                    binding.recycleRecipientcourse!!.visibility = View.VISIBLE
                                    binding.scrooling!!.visibility = View.GONE
                                    binding.chAllcourse!!.visibility = View.GONE
                                    binding.txtChcourse!!.visibility = View.GONE
                                    binding.idSV!!.visibility = View.VISIBLE
                                    binding.idSV!!.queryHint = "Search Course"
                                    SearchType = "CourseParticular"
                                    binding.SpinningYourclasses!!.visibility = View.VISIBLE
                                    SelectedcourseAdapter = SelectedRecipientAdapter(
                                        FilterDepartment,
                                        this@PrincipalRecipient,
                                        object : RecipientCheckListener {
                                            override fun add(data: RecipientSelected?) {
                                                var depaetmentid = data!!.SelectedId
                                                if (FilterDepartment.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                                    binding.chAllcourse!!.isChecked = true
                                                    binding.txtChcourse!!.setText(CommonUtil.Remove_All)
                                                } else {
                                                    binding.chAllcourse!!.isChecked = false
                                                    binding.txtChcourse!!.setText(CommonUtil.Select_All)
                                                }
                                            }

                                            override fun remove(data: RecipientSelected?) {
                                                var departmentid = data!!.SelectedId
                                                binding.chAllcourse!!.isChecked = false
                                                binding.txtChcourse!!.setText(CommonUtil.Select_All)
                                            }
                                        })
                                    val mLayoutManager: RecyclerView.LayoutManager =
                                        LinearLayoutManager(this@PrincipalRecipient)
                                    binding.recycleRecipientcourse!!.layoutManager = mLayoutManager
                                    binding.recycleRecipientcourse!!.itemAnimator =
                                        DefaultItemAnimator()
                                    binding.recycleRecipientcourse!!.adapter = SelectedcourseAdapter
                                    binding.recycleRecipientcourse!!.recycledViewPool.setMaxRecycledViews(
                                        0, 80
                                    )
                                    SelectedcourseAdapter!!.notifyDataSetChanged()
                                }
                            }
                            LoadDepartmentSpinner()
                        } else {
                            binding.spinnerDropdowncourse!!.visibility = View.GONE
                            FirstTime = false
                            binding.spinnerDropdowncourse!!.setSelection(0)

                            GetCourseData!!.forEach {
                                it.course_id
                                it.course_name
                                val department = RecipientSelected(it.course_id, it.course_name,"")
                                SelectedRecipientlist.add(department)
                            }
                            binding.chAllcourse!!.visibility = View.VISIBLE
                            binding.txtChcourse!!.visibility = View.VISIBLE
                            if (CommonUtil.seleteddataArrayCheckbox.size > 0) {
                                if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size) {
                                    binding.chAllcourse!!.isChecked = true
                                    binding.txtChcourse!!.setText(CommonUtil.Remove_All)
                                } else {
                                    binding.chAllcourse!!.isChecked = false
                                    binding.txtChcourse!!.setText(CommonUtil.Select_All)
                                }
                            }
                            binding.recycleRecipientcourse!!.visibility = View.VISIBLE
                            courseSelectAllDivision = true
                            binding.idSV!!.visibility = View.VISIBLE
                            binding.idSV!!.queryHint = "Search Course"
                            SearchType = "CourseAll"
                            binding.SpinningYourclasses!!.visibility = View.VISIBLE
                            SelectedcourseAdapter = SelectedRecipientAdapter(SelectedRecipientlist,
                                this@PrincipalRecipient,
                                object : RecipientCheckListener {
                                    override fun add(data: RecipientSelected?) {
                                        var depaetmentid = data!!.SelectedId
                                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                            binding.chAllcourse!!.isChecked = true
                                            binding.txtChcourse!!.setText(CommonUtil.Remove_All)
                                        } else {
                                            binding.chAllcourse!!.isChecked = false
                                            binding.txtChcourse!!.setText(CommonUtil.Select_All)
                                        }
                                    }

                                    override fun remove(data: RecipientSelected?) {
                                        var departmentid = data!!.SelectedId
                                        binding.chAllcourse!!.isChecked = false
                                        binding.txtChcourse!!.setText(CommonUtil.Select_All)
                                    }
                                })
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@PrincipalRecipient)
                            binding.recycleRecipientcourse!!.layoutManager = mLayoutManager
                            binding.recycleRecipientcourse!!.itemAnimator = DefaultItemAnimator()
                            binding.recycleRecipientcourse!!.adapter = SelectedcourseAdapter
                            binding.recycleRecipientcourse!!.recycledViewPool.setMaxRecycledViews(
                                0,
                                80
                            )
                            SelectedcourseAdapter!!.notifyDataSetChanged()
                            SelectedSpinnerID = "0"
                            DepartmentChoose = true
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {

                    }
                }
        }
    }

    private fun Yourclasses() {

        binding.SpinningYourclasses!!.visibility = View.VISIBLE
        val YourClasses = java.util.ArrayList<String>()
        SpinningCoursedatahod.clear()
        YourClasses.add("Select Your Type")
        YourClasses.add(CommonUtil.Subjects)
        YourClasses.add(CommonUtil.Tutor)
        val adapter = ArrayAdapter(this, R.layout.spinner_rextview_course, YourClasses)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_course_layout)
        binding.SpinningYourclasses!!.adapter = adapter
        binding.SpinningYourclasses!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    if (position != 0) {
                        if (position == 1) {
                            GetSubjectprinciple()
                            SpinningText = CommonUtil.Subjects
                        } else if (position == 2) {
                            Gettuterprinciple()
                            SpinningText = CommonUtil.Tutor
                        }
                    } else {
                        binding.recycleRecipientYourclasses!!.visibility = View.GONE
                        binding.scrooling!!.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
    }

    private fun SmsToEntireCollegeRequest() {

        val jsonObject = JsonObject()

        if (ScreenName.equals(CommonUtil.TextHistory)) {
            jsonObject.addProperty("forwarding_text_id", CommonUtil.forwarding_text_id)
        }
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_filetype, "1")
        jsonObject.addProperty(ApiRequestNames.Req_MessageContent, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.MenuDescription)

        appViewModel!!.SendSmsToEntireCollege(jsonObject, this)
        Log.d("SMSJsonObjectEntire", jsonObject.toString())
    }

    private fun GetDivisionRequest() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        appViewModel!!.getDivision(jsonObject, this)
        Log.d("GetDivisionRequest", jsonObject.toString())
    }

    private fun GetGroup() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_idcollege, CommonUtil.CollegeId)
        Log.d("GetGroupRequeat", jsonObject.toString())
        appViewModel!!.getGroup(jsonObject, this)
    }

    private fun GetSubjectprinciple() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        appViewModel!!.getsubject(jsonObject, this)
        Log.d("GetstaffRequest", jsonObject.toString())
    }

    private fun Gettuterprinciple() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        appViewModel!!.gettuter(jsonObject, this)
        Log.d("GettuterRequest", jsonObject.toString())
    }


    private fun GetDepartmentRequest() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_div_id, SelectedSpinnerID)
        appViewModel!!.getDepartment(jsonObject, this)
        Log.d("GetDepartmentRequest", jsonObject.toString())
    }

    private fun GetCourseRequest() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_depart_id, SelectedSpinnerID)
        appViewModel!!.getCourseDepartment(jsonObject, this)
        Log.d("GetCourseRequeat", jsonObject.toString())
    }

    private fun SmsToParticularTypeRequest() {
        val jsonObject = JsonObject()

        if (ScreenName.equals(CommonUtil.TextHistory)) {
            jsonObject.addProperty("forwarding_text_id", CommonUtil.forwarding_text_id)
        }

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
        if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {

        }

        if (SpinningText.equals(CommonUtil.Subjects)) {
            jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)
        }

        appViewModel!!.SendSmsToParticularType(jsonObject, this)
        Log.d("SMSJsonObjectParticuler", jsonObject.toString())
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


        if (SpinningText.equals(CommonUtil.Subjects)) {
            jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)
        }

        appViewModel!!.NoticeBoardsmssending(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
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

        if (SpinningText.equals(CommonUtil.Subjects)) {
            jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)
        }
        appViewModel!!.SendVoiceToParticulerHistory(jsonObject, this)
        Log.d("VoiceToEntireHistory", jsonObject.toString())
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

    private fun ImageOrPdfsendentire() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)

        if (CommonUtil.urlFromS3!!.contains(".pdf")) {
            jsonObject.addProperty(ApiRequestNames.Req_filetype, "3")
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_filetype, "2")
        }
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty(ApiRequestNames.Req_fileduraction, "0")
        jsonObject.addProperty(ApiRequestNames.Req_title, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_description, CommonUtil.MenuDescription)

        val FileNameArray = JsonArray()

        for (i in AWSUploadedFilesList.indices) {
            val FileNameobject = JsonObject()
            FileNameobject.addProperty(
                ApiRequestNames.Req_FileName, AWSUploadedFilesList[i]
            )
            FileNameArray.add(FileNameobject)
        }
        jsonObject.add(ApiRequestNames.Req_FileNameArray, FileNameArray)

        appViewModel!!.ImageorPdf(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
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

        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)

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


        if (SpinningText.equals(CommonUtil.Subjects)) {
            jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)
        }

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

        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)

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

     fun EntireClick() {

        isParent = false
        //   isStudent = false
        isStaff = false

        binding.chboxAll!!.isChecked = false
        //  binding.chboxStudent!!.isChecked = false
        binding.chboxParents!!.isChecked = false
        binding.chboxStaff!!.isChecked = false

        binding.chboxStaff!!.visibility = View.VISIBLE
//        binding.lnrTargetAll!!.visibility = View.VISIBLE
        CommonUtil.courseType = ""
        binding.txtSelectspecficStudent!!.visibility = View.GONE
        CommonUtil.seleteddataArrayCheckbox.clear()
        CommonUtil.receiverid = ""
        CommonUtil.SeletedStringdataReplace = ""
        binding.lnrStaff!!.visibility = View.VISIBLE
        binding.idSV!!.visibility = View.GONE
        CommonUtil.DepartmentChooseIds.clear()
        binding.chBoxAll!!.visibility = View.GONE
        binding.txtChBoxAll!!.visibility = View.GONE
        binding.recycleRecipients!!.visibility = View.GONE
        binding.SpinningYourclasses!!.visibility = View.GONE
        binding.recycleRecipientYourclasses!!.visibility = View.GONE
        binding.scrooling!!.visibility = View.GONE
        seletedIds = ""
        binding.chAllDepartment!!.visibility = View.GONE
        binding.txtChDepartment!!.visibility = View.GONE

        binding.recycleRecipientcourse!!.visibility = View.GONE
        binding.chAllcourse!!.visibility = View.GONE
        binding.txtChcourse!!.visibility = View.GONE
        binding.spinnerDropdowncourse!!.visibility = View.GONE

        binding.lblEntireDepartmentlable!!.setBackgroundResource(R.drawable.bg_available_selected_green)
        binding.lblEntireDepartmentlable!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_white)))

        binding.lblDivision!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblDivision!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblDepartment!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblDepartment!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblCourse!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblCourse!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblYourClasses!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblYourClasses!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblGroups!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblGroups!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))
        SelecteRecipientType = binding.lblEntireDepartmentlable!!.text.toString()
        CommonUtil.receivertype = CommonUtil.CollegeId.toString()
    }


     fun divisionClick() {

//        binding.lnrTargetAll!!.visibility = View.VISIBLE
        CommonUtil.receivertype = "8"
        isParent = false
        //   isStudent = false
        isStaff = false
        binding.chboxAll!!.isChecked = false
        //    binding.chboxStudent!!.isChecked = false
        binding.chboxParents!!.isChecked = false
        binding.chboxStaff!!.isChecked = false
        binding.chboxStaff!!.visibility = View.VISIBLE

        if (binding.lblDivision!!.text.toString().equals(CommonUtil.Course)) {
            Card_name = CommonUtil.Course
        } else {
            Card_name = ""
        }
        CommonUtil.courseType = ""
        seletedIds = ""
        binding.lnrStaff!!.visibility = View.VISIBLE
        binding.txtSelectspecficStudent!!.visibility = View.GONE
        binding.idSV!!.visibility = View.GONE
        CommonUtil.DepartmentChooseIds.clear()
        CommonUtil.seleteddataArrayCheckbox.clear()
        CommonUtil.receiverid = ""
        CommonUtil.SeletedStringdataReplace = ""
        binding.chBoxAll!!.visibility = View.VISIBLE
        binding.txtChBoxAll!!.visibility = View.VISIBLE
        binding.recycleRecipients!!.visibility = View.VISIBLE
        binding.SpinningYourclasses!!.visibility = View.GONE
        binding.recycleRecipientYourclasses!!.visibility = View.GONE
        binding.scrooling!!.visibility = View.GONE
        binding.chBoxAll!!.isChecked = false
        binding.txtChDepartment!!.text = CommonUtil.Select_All
        binding.chAllDepartment!!.visibility = View.GONE
        binding.txtChDepartment!!.visibility = View.GONE
        binding.recycleRecipientcourse!!.visibility = View.GONE
        binding.chAllcourse!!.visibility = View.GONE
        binding.txtChcourse!!.visibility = View.GONE
        binding.spinnerDropdowncourse!!.visibility = View.GONE

        binding.lblEntireDepartmentlable!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblEntireDepartmentlable!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblDivision!!.setBackgroundResource(R.drawable.bg_available_selected_green)
        binding.lblDivision!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_white)))

        binding.lblDepartment!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblDepartment!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblCourse!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblCourse!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblYourClasses!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblYourClasses!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblGroups!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblGroups!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        SelecteRecipientType = binding.lblDivision!!.text.toString()
        GetDivisionRequest()

    }

     fun departmentClick() {

        CommonUtil.receivertype = "3"
        isParent = false
        //    isStudent = false
        isStaff = false
        binding.chboxAll!!.isChecked = false
        //     binding.chboxStudent!!.isChecked = false
        binding.chboxParents!!.isChecked = false
        binding.chboxStaff!!.isChecked = false
        binding.chboxStaff!!.visibility = View.VISIBLE
//        binding.lnrTargetAll!!.visibility = View.VISIBLE
        if (binding.lblDepartment!!.text.toString().equals(CommonUtil.Year_Section)) {
            Card_name = CommonUtil.Year_Section
        } else {
            Card_name = ""
        }
        seletedIds = ""

        Spinner = "Department"
        CommonUtil.courseType = "Department"
        binding.lnrStaff!!.visibility = View.VISIBLE
        binding.idSV!!.visibility = View.VISIBLE
        binding.idSV!!.queryHint = "Search Department"
        binding.txtSelectspecficStudent!!.visibility = View.GONE

        CommonUtil.receiverid = ""
        binding.SpinningYourclasses!!.visibility = View.VISIBLE
        SelectedSpinnerID = "0"
        GetDepartmentRequest()
        CommonUtil.DepartmentChooseIds.clear()
        binding.chBoxAll!!.isChecked = false
        binding.txtChDepartment!!.text = CommonUtil.Select_All

        binding.chBoxAll!!.visibility = View.GONE
        binding.txtChBoxAll!!.visibility = View.GONE
        binding.recycleRecipients!!.visibility = View.GONE
        binding.chAllDepartment!!.visibility = View.VISIBLE
        binding.chAllDepartment!!.isChecked = false
        binding.txtChDepartment!!.visibility = View.VISIBLE
        binding.recycleRecipientYourclasses!!.visibility = View.GONE
        binding.scrooling!!.visibility = View.GONE
        binding.recycleRecipientcourse!!.visibility = View.GONE
        binding.chAllcourse!!.visibility = View.GONE
        binding.txtChcourse!!.visibility = View.GONE
        binding.spinnerDropdowncourse!!.visibility = View.GONE
        CommonUtil.seleteddataArrayCheckbox.clear()


        binding.lblEntireDepartmentlable!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblEntireDepartmentlable!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblDivision!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblDivision!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblDepartment!!.setBackgroundResource(R.drawable.bg_available_selected_green)
        binding.lblDepartment!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_white)))

        binding.lblCourse!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblCourse!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblYourClasses!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblYourClasses!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblGroups!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblGroups!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))
        SelecteRecipientType = binding.lblDepartment!!.text.toString()

    }

     fun CourseClick() {

//        if (CommonUtil.isParentEnable == "1") {
//            binding.lnrTargetAll!!.visibility = View.VISIBLE
//        } else {
//            binding.lnrTargetAll!!.visibility = View.GONE
//        }
        CommonUtil.receivertype = "2"
        isParent = false
        //   isStudent = false
        isStaff = false
        binding.chboxAll!!.isChecked = false
        //  binding.chboxStudent!!.isChecked = false
        binding.chboxParents!!.isChecked = false
        binding.chboxStaff!!.isChecked = false
        CommonUtil.courseType = "Course"
        binding.chboxStaff!!.visibility = View.GONE

        binding.chAllcourse!!.isChecked = false
        binding.txtChcourse!!.setText(CommonUtil.Select_All)
        if (binding.lblCourse!!.text.toString().equals(CommonUtil.Course)) {
            Card_name = CommonUtil.Course
        } else {
            Card_name = ""
        }
        Spinner = "Course"
        CommonUtil.DepartmentChooseIds.clear()
        binding.txtSelectspecficStudent!!.visibility = View.GONE
        seletedIds = ""
        binding.lnrStaff!!.visibility = View.GONE
        CommonUtil.receiverid = ""
        SelectedSpinnerID = "0"
        GetCourseRequest()
        binding.chAllDepartment!!.visibility = View.GONE
        binding.txtChDepartment!!.visibility = View.GONE
        binding.chBoxAll!!.visibility = View.GONE
        binding.txtChBoxAll!!.visibility = View.GONE
        binding.recycleRecipients!!.visibility = View.GONE
        binding.recycleRecipientYourclasses!!.visibility = View.GONE
        binding.scrooling!!.visibility = View.GONE
        CommonUtil.seleteddataArrayCheckbox.clear()
        binding.idSV!!.queryHint = "Search Course"

        binding.lblEntireDepartmentlable!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblEntireDepartmentlable!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblDivision!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblDivision!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblDepartment!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblDepartment!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblCourse!!.setBackgroundResource(R.drawable.bg_available_selected_green)
        binding.lblCourse!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_white)))

        binding.lblYourClasses!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblYourClasses!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblGroups!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblGroups!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))
        SelecteRecipientType = binding.lblCourse!!.text.toString()


    }

     fun YourClassesClick() {

//        if (CommonUtil.isParentEnable == "1") {
//            binding.lnrTargetAll!!.visibility = View.VISIBLE
//        } else {
//            binding.lnrTargetAll!!.visibility = View.GONE
//        }
        isParent = false
        //     isStudent = false
        isStaff = false
        binding.chboxAll!!.isChecked = false
        //    binding.chboxStudent!!.isChecked = false
        binding.chboxParents!!.isChecked = false
        binding.chboxStaff!!.isChecked = false
        if (binding.lblYourClasses!!.text.toString().equals(CommonUtil.Your_Classes)) {
            Card_name = CommonUtil.Your_Classes
        } else {
            Card_name = ""
        }
        binding.chboxStaff!!.visibility = View.GONE

        binding.idSV!!.visibility = View.GONE
        CommonUtil.courseType = ""

        binding.lnrStaff!!.visibility = View.GONE
        CommonUtil.receiverid = ""
        Yourclasses()
        CommonUtil.DepartmentChooseIds.clear()
        binding.SpinningYourclasses!!.visibility = View.VISIBLE
        binding.recycleRecipientYourclasses!!.visibility = View.VISIBLE
        binding.scrooling!!.visibility = View.VISIBLE
        seletedIds = ""

        binding.chBoxAll!!.visibility = View.GONE
        binding.txtChBoxAll!!.visibility = View.GONE
        binding.recycleRecipients!!.visibility = View.GONE

        binding.chAllDepartment!!.visibility = View.GONE
        binding.txtChDepartment!!.visibility = View.GONE

        binding.spinnerDropdowncourse!!.visibility = View.GONE
        binding.recycleRecipientcourse!!.visibility = View.GONE
        binding.chAllcourse!!.visibility = View.GONE
        binding.txtChcourse!!.visibility = View.GONE
        CommonUtil.seleteddataArrayCheckbox.clear()

        binding.lblEntireDepartmentlable!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblEntireDepartmentlable!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblDivision!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblDivision!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblDepartment!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblDepartment!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblCourse!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblCourse!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblYourClasses!!.setBackgroundResource(R.drawable.bg_available_selected_green)
        binding.lblYourClasses!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_white)))

        binding.lblGroups!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblGroups!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))
        SelecteRecipientType = binding.lblYourClasses!!.text.toString()

    }

     fun GroupsClick() {

//        if (CommonUtil.isParentEnable == "1") {
//            binding.lnrTargetAll!!.visibility = View.VISIBLE
//        } else {
//            binding.lnrTargetAll!!.visibility = View.GONE
//        }
        CommonUtil.receivertype = "6"
        isParent = false
        //    isStudent = false
        isStaff = false

        //  binding.chboxStudent!!.isChecked = false
        binding.chboxParents!!.isChecked = false
        binding.chboxStaff!!.isChecked = false
        binding.chboxAll!!.isChecked = false
        Card_name = if (binding.lblGroups!!.text.toString().equals(CommonUtil.Groups)) {
            CommonUtil.Groups
        } else {
            ""
        }
        CommonUtil.courseType = ""
        seletedIds = ""

        CommonUtil.DepartmentChooseIds.clear()
        binding.txtSelectspecficStudent!!.visibility = View.GONE
        binding.idSV!!.visibility = View.GONE
        binding.lnrStaff!!.visibility = View.GONE
        CommonUtil.receiverid = ""
        GetGroup()
        binding.chBoxAll!!.isChecked = false
        binding.txtChDepartment!!.text = CommonUtil.Select_All
        binding.chBoxAll!!.visibility = View.VISIBLE
        binding.txtChBoxAll!!.visibility = View.VISIBLE
        binding.recycleRecipients!!.visibility = View.VISIBLE
        binding.SpinningYourclasses!!.visibility = View.GONE
        binding.recycleRecipientYourclasses!!.visibility = View.GONE
        binding.scrooling!!.visibility = View.GONE
        binding.chAllDepartment!!.visibility = View.GONE
        binding.txtChDepartment!!.visibility = View.GONE
        binding.recycleRecipientcourse!!.visibility = View.GONE
        binding.chAllcourse!!.visibility = View.GONE
        binding.txtChcourse!!.visibility = View.GONE
        binding.spinnerDropdowncourse!!.visibility = View.GONE
        CommonUtil.seleteddataArrayCheckbox.clear()


        binding.lblEntireDepartmentlable!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblEntireDepartmentlable!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblDivision!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblDivision!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblDepartment!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblDepartment!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblCourse!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblCourse!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblYourClasses!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        binding.lblYourClasses!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        binding.lblGroups!!.setBackgroundResource(R.drawable.bg_available_selected_green)
        binding.lblGroups!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_white)))
        SelecteRecipientType = binding.lblGroups!!.text.toString()

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
//        jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)

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

        appViewModel!!.Assignmentsend(jsonObject, this)
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

        if (SpinningText.equals(CommonUtil.Subjects)) {
            jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)
        }

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
//        jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)

        if (CommonUtil.AssignmentType.equals("Text") || CommonUtil.AssignmentType.equals("text")) {
            jsonObject.addProperty("assignmenttype", "text")
            val FileNameArray = JsonArray()
            val FileNameobject = JsonObject()
            FileNameobject.addProperty("FileName", "")
            FileNameArray.add(FileNameobject)
            jsonObject.add("FileNameArray", FileNameArray)
        } else {
            if (CommonUtil.AssignmentType.equals("image") || CommonUtil.AssignmentType.equals("Image")) {
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

    private fun VideosendEntire() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_title, CommonUtil.title)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.Description)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty("filename", "video")
        jsonObject.addProperty("iframe", CommonUtil.VimeoIframe)
        jsonObject.addProperty("url", CommonUtil.VimeoVideoUrl)
        Log.d("Videoiframe", CommonUtil.VimeoIframe.toString())


        appViewModel!!.VideoEntireSend(jsonObject, this)
        Log.d("SMSJsonObjectEntire", jsonObject.toString())
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

        if (SpinningText.equals(CommonUtil.Subjects)) {
            jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)
        }
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

        if (SpinningText.equals(CommonUtil.Subjects)) {
            jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)
        }

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

                                    val dlg =
                                        this@PrincipalRecipient.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent =

                                                Intent(
                                                    this@PrincipalRecipient,
                                                    Communication::class.java
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

                                    val dlg =
                                        this@PrincipalRecipient.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent =

                                                Intent(
                                                    this@PrincipalRecipient,
                                                    Communication::class.java
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

    private fun VoiceSendEntire() {

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
        jsonObject.addProperty("isemergencyvoice", CommonUtil.VoiceType)

        Log.d("VoiceSend:req", jsonObject.toString())

        val file: File = File(CommonUtil.futureStudioIconFile!!.getPath())
        Log.d("FILE_Path", CommonUtil.futureStudioIconFile!!.getPath())

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val bodyFile: MultipartBody.Part =
            MultipartBody.Part.createFormData("voice", file.name, requestFile)
        val requestBody: RequestBody = RequestBody.create(MultipartBody.FORM, jsonObject.toString())

        RestClient.apiInterfaces.UploadVoicefileEntire(requestBody, bodyFile)
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

                                    val dlg =
                                        this@PrincipalRecipient.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,

                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent = Intent(
                                                this@PrincipalRecipient, Communication::class.java
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

                                    val dlg =
                                        this@PrincipalRecipient.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent =

                                                Intent(
                                                    this@PrincipalRecipient,
                                                    Communication::class.java
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

                                    val dlg =
                                        this@PrincipalRecipient.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent = Intent(
                                                this@PrincipalRecipient, Communication::class.java
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

                                    val dlg =
                                        this@PrincipalRecipient.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent =

                                                Intent(
                                                    this@PrincipalRecipient,
                                                    Communication::class.java
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
//            var extension = uploadFilePath!!.substring(uploadFilePath!!.lastIndexOf("."))
//            if (extension == ".pdf") {
//                contentType = ".pdf"
//            } else {
//                contentType = ".jpg"
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
//                                this@PrincipalRecipient,
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
//            if (ScreenName.equals(CommonUtil.Image_Pdf)) {
//                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {
//                    ImageOrPdfsendentire()
//                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {
//                    ImageOrPdfsendparticuler()
//                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
//                    ImageOrPdfsendparticuler()
//                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
//                    ImageOrPdfsendparticuler()
//                } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {
//                    if (binding.txtSelectspecficStudent!!.visibility == View.VISIBLE) {
//                        if (SpinningText.equals(CommonUtil.Subjects)) {
//                            ImageOrPdfsendparticuler()
//                        } else if (SpinningText.equals(CommonUtil.Tutor)) {
//                            ImageOrPdfsendparticulerTuter()
//                        }
//                    } else {
//                        CommonUtil.receivertype = "7"
//                        if (SpinningText.equals(CommonUtil.Subjects)) {
//                            ImageOrPdfsendparticuler()
//                        } else if (SpinningText.equals(CommonUtil.Tutor)) {
//                            ImageOrPdfsendparticulerTuter()
//                        }
//                    }
//                } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {
//                    ImageOrPdfsendparticuler()
//                }
//            } else if (ScreenName.equals(CommonUtil.New_Assignment)) {
//                CommonUtil.receivertype = "1"
//                AssignmentsendEntireSection()
//            } else if (ScreenName.equals(CommonUtil.Forward_Assignment)) {
//                CommonUtil.receivertype = "1"
//                Assignmentforward()
//
//
//            } else if (ScreenName.equals(CommonUtil.Noticeboard)) {
//                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {
//                    NoticeBoardSMSsending()
//                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {
//                    NoticeBoardSMSsending()
//                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
//                    NoticeBoardSMSsending()
//                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
//                    NoticeBoardSMSsending()
//                } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {
//                    if (SpinningText.equals(CommonUtil.Subjects)) {
//                        NoticeBoardSMSsending()
//                    } else if (SpinningText.equals(CommonUtil.Tutor)) {
//                        NoticeBoardSMSsendingTuter()
//                    }
//                } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {
//                    NoticeBoardSMSsending()
//                }
//            }
//        }
//    }

    fun SendButtonAPi() {

        Log.d("isStudent", isStudent.toString())
        Log.d("isStaff", isStaff.toString())
        Log.d("isParent", isParent.toString())
        Log.d("CommonUtil.isSubjectIds", CommonUtil.isSubjectIds.toString())

        for (i in CommonUtil.DepartmentChooseIds.indices) {
            seletedIds = CommonUtil.DepartmentChooseIds.joinToString { it }
        }
        if (!seletedIds.equals("")) {
            CommonUtil.receiverid = seletedIds.replace(", ", "~")
        }

        Log.d("DepartmentChooseId", CommonUtil.DepartmentChooseIds.size.toString())
        val ReceiverCount = CommonUtil.DepartmentChooseIds.size.toString()

        Log.d("CommonUtil.receiverid", CommonUtil.receiverid)
        Log.d("checkbox_isStaff", isStaff.toString())
        Log.d("checkbox_isStudent", isStudent.toString())
        Log.d("checkbox_isParent", isParent.toString())

        if (CommonUtil.Priority.equals("p1")) {
            if (ScreenName.equals(CommonUtil.Text)) {
                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {
                    if (!CommonUtil.receivertype.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage("You are sending the message is whole college")
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                SmsToEntireCollegeRequest()
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
                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Division + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Department + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Classes + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                if (SpinningText.equals(CommonUtil.Subjects)) {
                                    SmsToParticularTypeRequest()
                                } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                    SmsToEntireCollegesubjectandtuterRequest()
                                }
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
                } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Groups + ReceiverCount)
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
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            } else if (ScreenName.equals(CommonUtil.TextHistory)) {
                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {
                    if (!CommonUtil.receivertype.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage("You are sending the message is whole college")
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                SmsToEntireCollegeRequest()
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
                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Division + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Department + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Classes + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                if (SpinningText.equals(CommonUtil.Subjects)) {
                                    SmsToParticularTypeRequest()
                                } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                    SmsToEntireCollegesubjectandtuterRequest()
                                }
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
                } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Groups + ReceiverCount)
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
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            } else if (ScreenName.equals(CommonUtil.Communication)) {
                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {
                    if (!CommonUtil.receivertype.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage("You are sending the message is whole college")
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                VoiceSendEntire()
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
                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Division + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Department + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Classes + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                if (SpinningText.equals(CommonUtil.Subjects)) {
                                    VoiceSendParticuler()
                                } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                    VoiceSendTuter()
                                }
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
                } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Groups + ReceiverCount)
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

                } else {

                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)

                }
            } else if (ScreenName.equals(CommonUtil.CommunicationVoice)) {
                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {
                    if (!CommonUtil.receivertype.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage("You are sending the message is whole college")
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                VoiceHistoryEntireSend()
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
                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Division + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Department + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Classes + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                if (SpinningText.equals(CommonUtil.Subjects)) {
                                    SendVoiceToParticulerHistory()
                                } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                    SendVoiceToParticulerHistory()
                                }
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
                } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Groups + ReceiverCount)
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

                } else {

                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)

                }

            } else if (ScreenName.equals(CommonUtil.Noticeboard)) {

                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage("You are sending the message is whole college")
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                //  NoticeBoardSMSsending()
//                                awsFileUpload(this, pathIndex)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {


                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Division + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                //   NoticeBoardSMSsending()
//                                awsFileUpload(this, pathIndex)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {


                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Department + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                //   NoticeBoardSMSsending()
//                                awsFileUpload(this, pathIndex)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                //   NoticeBoardSMSsending()
//                                awsFileUpload(this, pathIndex)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Classes + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    //    NoticeBoardSMSsending()
//                                    awsFileUpload(this, pathIndex)
                                    isUploadAWS()

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    //  NoticeBoardSMSsendingTuter()
//                                    awsFileUpload(this, pathIndex)
                                    isUploadAWS()

                                }
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

                } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Groups + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                //   NoticeBoardSMSsending()
//                                awsFileUpload(this, pathIndex)
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

                } else {

                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)

                }
            } else if (ScreenName.equals(CommonUtil.Image_Pdf)) {

                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage("You are sending the message is whole college")
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

//                                awsFileUpload(this, pathIndex)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {


                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Division + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

//                                awsFileUpload(this, pathIndex)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {


                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Department + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

//                                awsFileUpload(this, pathIndex)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

//                                awsFileUpload(this, pathIndex)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Classes + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                if (SpinningText.equals(CommonUtil.Subjects)) {

//                                    awsFileUpload(this, pathIndex)
                                    isUploadAWS()

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

//                                    awsFileUpload(this, pathIndex)
                                    isUploadAWS()

                                }
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

                } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Groups + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

//                                awsFileUpload(this, pathIndex)
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

                } else {

                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)

                }

            } else if (ScreenName.equals(CommonUtil.New_Assignment) || ScreenName.equals(
                    CommonUtil.Forward_Assignment
                )
            ) {

                if (!CommonUtil.receiverid.equals("")) {

                    val alertDialog: AlertDialog.Builder =
                        AlertDialog.Builder(this@PrincipalRecipient)
                    alertDialog.setTitle(CommonUtil.Submit_Alart)
                    alertDialog.setMessage(CommonUtil.selected_Classes + ReceiverCount)
                    alertDialog.setPositiveButton(
                        CommonUtil.Yes
                    ) { _, _ ->

//                        awsFileUpload(this, pathIndex)
                        isUploadAWS()

                    }
                    alertDialog.setNegativeButton(
                        CommonUtil.No
                    ) { _, _ -> }
                    val alert: AlertDialog = alertDialog.create()
                    alert.setCanceledOnTouchOutside(false)
                    alert.show()

                } else {
                    CommonUtil.ApiAlert(this, "Select the Receiver")
                }
            } else if (ScreenName.equals(CommonUtil.ScreenNameEvent)) {
                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {
                    if (!CommonUtil.receivertype.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage("You are sending the message is whole college")
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
                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Division + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Department + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Classes + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                if (SpinningText.equals(CommonUtil.Subjects)) {
                                    Eventsend("add")
                                } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                    EventsendTuter("add")
                                }
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
                } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Groups + ReceiverCount)
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
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            } else if (ScreenName.equals(CommonUtil.Event_Edit)) {
                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {
                    if (!CommonUtil.receivertype.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage("You are sending the message is whole college")
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
                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {
                    if (!CommonUtil.receiverid.equals("")) {

                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Division + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Department + ReceiverCount)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
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
                } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Classes + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                if (binding.txtSelectspecficStudent!!.visibility == View.VISIBLE) {
                                    if (SpinningText.equals(CommonUtil.Subjects)) {
                                        Eventsend("edit")
                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                        EventsendTuter("edit")
                                    }
                                } else {
                                    CommonUtil.receivertype = "7"
                                    if (SpinningText.equals(CommonUtil.Subjects)) {
                                        Eventsend("edit")
                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                        EventsendTuter("edit")
                                    }
                                }
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
                } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Groups + ReceiverCount)
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
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            } else if (ScreenName.equals(CommonUtil.New_Video)) {
                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {
                    if (!CommonUtil.receivertype.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage("You are sending the message is whole college")
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                //    VimeoVideoUpload(this, CommonUtil.videofile!!)
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
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Division + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                //   VimeoVideoUpload(this, CommonUtil.videofile!!)
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
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Department + ReceiverCount)
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
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                //   VimeoVideoUpload(this, CommonUtil.videofile!!)
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
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Classes + ReceiverCount)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                if (SpinningText.equals(CommonUtil.Subjects)) {
                                    //   VimeoVideoUpload(this, CommonUtil.videofile!!)
                                    VimeoUploader.uploadVideo(
                                        this,
                                        CommonUtil.title,
                                        CommonUtil.Description,
                                        isVideoToken,
                                        CommonUtil.videofile!!,
                                        this
                                    )
                                } else if (SpinningText.equals(CommonUtil.Tutor)) {
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
                } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@PrincipalRecipient)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            alertDialog.setMessage(CommonUtil.selected_Groups + ReceiverCount)
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
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else {

                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                }
            }
        }
    }

     fun cancelClick() {
        onBackPressed()
        CommonUtil.DepartmentChooseIds.clear()
    }

    override fun onResume() {
        super.onResume()
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_principal_recipient

    override fun onUploadComplete(success: Boolean, isIframe: String, isLink: String) {
        Log.d("Vimeo_Video_upload", success.toString())
        Log.d("VimeoIframe", isIframe)
        Log.d("link", isLink)

        if (success) {
            CommonUtil.VimeoIframe = isIframe
            CommonUtil.VimeoVideoUrl = isLink
            CommonUtil.Videofile = true

            if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {
                VideosendEntire()
            } else if (SelecteRecipientType.equals(CommonUtil.Division)) {
                VideosendParticuler()
            } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                VideosendParticuler()
            } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
                VideosendParticuler()
            } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {
                if (binding.txtSelectspecficStudent!!.visibility == View.VISIBLE) {
                    if (SpinningText.equals(CommonUtil.Subjects)) {
                        VideosendParticuler()
                    } else if (SpinningText.equals(CommonUtil.Tutor)) {
                        VideosendParticulerTuter()
                    }
                } else {
                    CommonUtil.receivertype = "7"
                    if (SpinningText.equals(CommonUtil.Subjects)) {
                        VideosendParticuler()
                    } else if (SpinningText.equals(CommonUtil.Tutor)) {
                        VideosendParticulerTuter()
                    }
                }
            } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                VideosendParticuler()
            }
        } else {
            CommonUtil.ApiAlertContext(this, "Video sending failed. Please try again.")
        }
    }

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
        isAwsUploadingPreSigned!!.getPreSignedUrl(this,
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
                        if (ScreenName.equals(CommonUtil.Image_Pdf)) {
                            if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {
                                ImageOrPdfsendentire()
                            } else if (SelecteRecipientType.equals(CommonUtil.Division)) {
                                ImageOrPdfsendparticuler()
                            } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                                ImageOrPdfsendparticuler()
                            } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
                                ImageOrPdfsendparticuler()
                            } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {
                                if (binding.txtSelectspecficStudent!!.visibility == View.VISIBLE) {
                                    if (SpinningText.equals(CommonUtil.Subjects)) {
                                        ImageOrPdfsendparticuler()
                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                        ImageOrPdfsendparticulerTuter()
                                    }
                                } else {
                                    CommonUtil.receivertype = "7"
                                    if (SpinningText.equals(CommonUtil.Subjects)) {
                                        ImageOrPdfsendparticuler()
                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                        ImageOrPdfsendparticulerTuter()
                                    }
                                }
                            } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                                ImageOrPdfsendparticuler()
                            }
                        } else if (ScreenName.equals(CommonUtil.New_Assignment)) {
                            CommonUtil.receivertype = "1"
                            AssignmentsendEntireSection()
                        } else if (ScreenName.equals(CommonUtil.Forward_Assignment)) {
                            CommonUtil.receivertype = "1"
                            Assignmentforward()


                        } else if (ScreenName.equals(CommonUtil.Noticeboard)) {
                            if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {
                                NoticeBoardSMSsending()
                            } else if (SelecteRecipientType.equals(CommonUtil.Division)) {
                                NoticeBoardSMSsending()
                            } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                                NoticeBoardSMSsending()
                            } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
                                NoticeBoardSMSsending()
                            } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {
                                if (SpinningText.equals(CommonUtil.Subjects)) {
                                    NoticeBoardSMSsending()
                                } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                    NoticeBoardSMSsendingTuter()
                                }
                            } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                                NoticeBoardSMSsending()
                            }
                        }
                    }
                }

                override fun onUploadError(error: String?) {
                    Log.e("Upload Error", error!!)
                }
            })
    }
}