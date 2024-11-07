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
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
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
import com.vsca.vsnapvoicecollege.Adapters.Subject_Adapter
import com.vsca.vsnapvoicecollege.Adapters.sectionandyear_Adapter
import com.vsca.vsnapvoicecollege.Adapters.specificStudent_adapter
import com.vsca.vsnapvoicecollege.Interfaces.ApiInterfaces
import com.vsca.vsnapvoicecollege.Interfaces.RecipientCheckListener
import com.vsca.vsnapvoicecollege.Model.AWSUploadedFiles
import com.vsca.vsnapvoicecollege.Model.Data
import com.vsca.vsnapvoicecollege.Model.GetGroupData
import com.vsca.vsnapvoicecollege.Model.Get_staff_yourclass
import com.vsca.vsnapvoicecollege.Model.department_coursedata
import com.vsca.vsnapvoicecollege.Model.specificStudent_datalist
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
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


class AddRecipients : ActionBarActivity(), VimeoUploader.UploadCompletionListener {

    @JvmField
    @BindView(R.id.ch_all)
    var ch_all: CheckBox? = null

    @JvmField
    @BindView(R.id.lblEntireDepartmentlable)
    var lblEntireDepartmentlable: TextView? = null

    @JvmField
    @BindView(R.id.btnRecipientCancel)
    var btnRecipientCancel: Button? = null

    @JvmField
    @BindView(R.id.btnConfirm)
    var btnConfirm: Button? = null

    @JvmField
    @BindView(R.id.layoutButton)
    var layoutButton: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.lblDivision)
    var lblDivision: TextView? = null

    @JvmField
    @BindView(R.id.Division_All)
    var Division_All: TextView? = null

    @JvmField
    @BindView(R.id.txt_onandoff)
    var txt_onandoff: RelativeLayout? = null

    @JvmField
    @BindView(R.id.switchonAndoff)
    var switchonAndoff: Switch? = null

    @JvmField
    @BindView(R.id.ALL2)
    var ALL2: TextView? = null

    @JvmField
    @BindView(R.id.ch_all1)
    var ch_all1: CheckBox? = null

    @JvmField
    @BindView(R.id.ALL3)
    var ALL3: TextView? = null

    @JvmField
    @BindView(R.id.ch_all2)
    var ch_all2: CheckBox? = null

    @JvmField
    @BindView(R.id.ALL4)
    var ALL4: TextView? = null

    @JvmField
    @BindView(R.id.ch_all4)
    var ch_all4: CheckBox? = null


    @JvmField
    @BindView(R.id.txtTarget)
    var txtTarget: TextView? = null

    @JvmField
    @BindView(R.id.lnrTarget)
    var lnrTarget: LinearLayout? = null

    @JvmField
    @BindView(R.id.edt_selectiontuterorsubject)
    var edt_selectiontuterorsubject: TextView? = null

    @JvmField
    @BindView(R.id.NestedScrollView)
    var NestedScrollView: NestedScrollView? = null


    @JvmField
    @BindView(R.id.LayoutRecipient)
    var LayoutRecipient: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.lblDepartment)
    var lblDepartment: TextView? = null

    @JvmField
    @BindView(R.id.txt_selectsubortutor)
    var txt_selectsubortutor: TextView? = null

    @JvmField
    @BindView(R.id.lblCourse)
    var lblCourse: TextView? = null

    @JvmField
    @BindView(R.id.lblYourClasses)
    var lblYourClasses: TextView? = null

    @JvmField
    @BindView(R.id.lblGroups)
    var lblGroups: TextView? = null


    @JvmField
    @BindView(R.id.recycleRecipients)
    var recycleRecipients: RecyclerView? = null

    @JvmField
    @BindView(R.id.lblSelectedRecipient)
    var lblSelectedRecipient: TextView? = null


    @JvmField
    @BindView(R.id.spinnerDropdown)
    var spinnerDropdown: Spinner? = null


    @JvmField
    @BindView(R.id.Spinning_yourclasses)
    var Spinning_yourclasses: Spinner? = null


    @JvmField
    @BindView(R.id.spinnerDropdowncourse)
    var spinnerDropdowncourse: Spinner? = null

    @JvmField
    @BindView(R.id.chboxParents)
    var chboxParents: CheckBox? = null

    @JvmField
    @BindView(R.id.Viewlineone)
    var Viewlineone: View? = null

    @JvmField
    @BindView(R.id.chboxStaff)
    var chboxStaff: CheckBox? = null

    @JvmField
    @BindView(R.id.lnrTargetParent)
    var lnrTargetParent: LinearLayout? = null


    @JvmField
    @BindView(R.id.recycleRecipientscourse)
    var recycleRecipientscourse: RecyclerView? = null

    @JvmField
    @BindView(R.id.recycleyearandsection)
    var recycleyearandsection: RecyclerView? = null

    @JvmField
    @BindView(R.id.chboxStudent)
    var chboxStudent: CheckBox? = null

    @JvmField
    @BindView(R.id.chboxAll)
    var chboxAll: CheckBox? = null

    @JvmField
    @BindView(R.id.Viewlinetwo)
    var Viewlinetwo: View? = null


    @JvmField
    @BindView(R.id.recycle_Staffrecipients)
    var recycle_Staffrecipients: RecyclerView? = null

    @JvmField
    @BindView(R.id.txt_selectspecfic)
    var txt_selectspecfic: TextView? = null

    @JvmField
    @BindView(R.id.layoutstudentlist)
    var layoutstudentlist: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.txt_department)
    var txt_department: TextView? = null

    @JvmField
    @BindView(R.id.txt_selectspecfic_YearandSecrion)
    var txt_selectspecfic_YearandSecrion: TextView? = null

    @JvmField
    @BindView(R.id.Viewlinethree)
    var Viewlinethree: View? = null

    @JvmField
    @BindView(R.id.txt_mydepartment)
    var txt_mydepartment: TextView? = null

    @JvmField
    @BindView(R.id.txt_myclass)
    var txt_myclass: TextView? = null

    @JvmField
    @BindView(R.id.lbl_select_student)
    var lbl_select_student: TextView? = null

    @JvmField
    @BindView(R.id.recycle_specificstudent)
    var recycle_specificstudent: RecyclerView? = null

    @JvmField
    @BindView(R.id.relative_Recycle)
    var relative_Recycle: RelativeLayout? = null

    @JvmField
    @BindView(R.id.idSV)
    var SearchView: SearchView? = null

    @JvmField
    @BindView(R.id.Viewlinefour)
    var Viewlinefour: View? = null

    @JvmField
    @BindView(R.id.lnrStaff)
    var lnrStaff: LinearLayout? = null

    var isVideoToken = ""


    //AWS
    var Awsuploadedfile = ArrayList<String>()
    var pathIndex = 0
    var uploadFilePath: String? = null
    var contentType: String? = null
    var AWSUploadedFilesList = ArrayList<AWSUploadedFiles>()
    var progressDialog: ProgressDialog? = null
    var fileNameDateTime: String? = null
    var SenderType = ""
    var Specific: String? = null
    var fileName: File? = null
    var filename: String? = null
    var Awsaupladedfilepath: String? = null
    var separator = ","
    var getsubjectlist: List<Get_staff_yourclass> = ArrayList()
    var getspecifictuterstudent: List<specificStudent_datalist> = ArrayList()
    var appViewModel: App? = null
    var SelecteRecipientType: String? = null
    var GetDivisionData: ArrayList<GetDivisionData>? = null
    var GetGroupdata: ArrayList<GetGroupData>? = null
    var GetDepartmentData: ArrayList<GetDepartmentData>? = null
    var Getyouurclassdata: ArrayList<Data>? = null
    var GetCourseData: ArrayList<GetCourseData>? = null
    var Getcoursedepartment: ArrayList<department_coursedata>? = null
    var SpinnerData = ArrayList<String>()
    var SpinningCoursedata = ArrayList<String>()
    var SpinningCoursedatahod = ArrayList<String>()
    var SelectedSpinnerID: String? = null
    var SelectedSpinnerIDhod: String? = null
    var ScreenName: String? = null
    var FileType: String? = null
    var isStaff: Boolean? = null
    var yearSection = false
    var isStudent = true
    var isParent: Boolean? = null
    var recivertype: String? = null
    var Clickable: String? = null
    var SpinningText: String? = null
    var My_Department: String? = null
    var Card_name: String? = ""

    // ADAPTERS
    var divisionadapter: SelectedRecipientAdapter? = null
    var SelectedcourseAdapter: SelectedRecipientAdapter? = null
    var departmentAdapter: SelectedRecipientAdapter? = null
    var SpecificStudentList: SelectedRecipientAdapter? = null
    var groupAdapter: SelectedRecipientAdapter? = null
    var specificStudent_adapter: specificStudent_adapter? = null
    var subjectadapter: Subject_Adapter? = null
    var sectionandyear_Adapter: sectionandyear_Adapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this)
        CommonUtil.DepartmentChooseIds.clear()
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        CommonUtil.iSubjectId.clear()
        CommonUtil.receiverid = ""
        CommonUtil.Screenname = ""
        ScreenName = intent.getStringExtra("ScreenName")
        Log.d("ScreenName", ScreenName.toString())
        FileType = intent.getStringExtra("FileType")
        CommonUtil.seleteddataArrayCheckbox.clear()
        imgRefresh!!.visibility = View.GONE
        Division_All!!.visibility = View.GONE
        ch_all!!.visibility = View.GONE
        ALL2!!.visibility = View.GONE
        ch_all1!!.visibility = View.GONE
        ALL3!!.visibility = View.GONE
        ch_all2!!.visibility = View.GONE
        ALL4!!.visibility = View.GONE
        ch_all4!!.visibility = View.GONE
        Clickable = ""
        CommonUtil.receivertype = ""
        val VideoToken = SharedPreference.getVideo_Json(this).toString()

        isVideoToken = VideoToken
        Log.d("isVideoToken", VideoToken)
        if (CommonUtil.isParentEnable.equals("1")) {
            lnrTargetParent!!.visibility = View.VISIBLE
        } else {
            lnrTargetParent!!.visibility = View.GONE
            isParent = false
        }

        Card_name = if (CommonUtil.Priority.equals("p3")) {
            CommonUtil.Staff
        } else {
            ""
        }

        CommonUtil.CallEnable = "0"

        if (CommonUtil.isAllowtomakecall == 1) {
            when (ScreenName) {
                CommonUtil.Text -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.TextHistory -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.Communication -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.CommunicationVoice -> txt_onandoff!!.visibility = View.VISIBLE
                CommonUtil.New_Video -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.Noticeboard -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.ScreenNameEvent -> txt_onandoff!!.visibility = View.GONE
                CommonUtil.Image_Pdf -> txt_onandoff!!.visibility = View.GONE
            }
        }

        switchonAndoff!!.setOnClickListener {
            if (switchonAndoff!!.isChecked) {
                CommonUtil.CallEnable = "1"
            } else {
                CommonUtil.CallEnable = "0"
            }
        }


        if (ScreenName!!.equals(CommonUtil.New_Assignment) || ScreenName.equals(CommonUtil.Forward_Assignment)) {
            if (CommonUtil.Priority.equals("p1")) {
                GetSubjectprinciple()
            } else if (CommonUtil.Priority.equals("p2")) {
                GetSubjecthod()
            } else if (CommonUtil.Priority.equals("p3")) {
                GetSubjectstaff()
            }
            lblEntireDepartmentlable!!.visibility = View.GONE
            SpinningText = CommonUtil.Assignment_SPECIFIC
        }



        if (ScreenName!!.equals(CommonUtil.New_Assignment) || ScreenName.equals(CommonUtil.Forward_Assignment)) {

            lblYourClasses!!.visibility = View.GONE
            txt_selectsubortutor!!.visibility = View.VISIBLE
            Viewlinetwo!!.visibility = View.VISIBLE
            recycle_Staffrecipients!!.visibility = View.VISIBLE
            txt_selectspecfic!!.visibility = View.VISIBLE
            lblDivision!!.visibility = View.GONE
            lblCourse!!.visibility = View.GONE
            lblDepartment!!.visibility = View.GONE
            lblEntireDepartmentlable!!.visibility = View.GONE
            lblGroups!!.visibility = View.GONE
            lblYourClasses!!.visibility = View.GONE
            lnrTarget!!.visibility = View.GONE
            txtTarget!!.visibility = View.GONE
            CommonUtil.Screenname = "Forward"

        } else {
            lnrTarget!!.visibility = View.VISIBLE
            txtTarget!!.visibility = View.VISIBLE
        }

        if (ScreenName.equals(CommonUtil.Forward_Assignment)) {

            txt_mydepartment!!.visibility = View.GONE
            CommonUtil.Screenname = "Forward"

        } else {
            txt_mydepartment!!.visibility = View.VISIBLE
            txt_onandoff!!.visibility = View.GONE
        }

        if (txt_mydepartment!!.visibility == View.VISIBLE) {
            txt_onandoff!!.visibility = View.GONE
        }

        ch_all!!.setOnClickListener(View.OnClickListener {

            if (SelecteRecipientType.equals(CommonUtil.Groups)) {

                if (ch_all!!.isChecked()) {
                    groupAdapter!!.selectAll()
                    Division_All!!.setText(CommonUtil.Remove_All)
                } else {
                    groupAdapter!!.unselectall()
                    Division_All!!.setText(CommonUtil.Select_All)
                }

            } else if (SelecteRecipientType.equals(CommonUtil.Course)) {

                if (ch_all!!.isChecked()) {
                    SelectedcourseAdapter!!.selectAll()
                    Division_All!!.setText(CommonUtil.Remove_All)
                } else {
                    SelectedcourseAdapter!!.unselectall()
                    Division_All!!.setText(CommonUtil.Select_All)
                }

            } else {

                if (ch_all!!.isChecked()) {
                    divisionadapter!!.selectAll()
                    Division_All!!.setText(CommonUtil.Remove_All)
                } else {
                    divisionadapter!!.unselectall()
                    Division_All!!.setText(CommonUtil.Select_All)
                }
            }
        })

        ch_all1!!.setOnClickListener(View.OnClickListener {

            if (ch_all1!!.isChecked()) {
                departmentAdapter!!.selectAll()
                ALL2!!.setText(CommonUtil.Remove_All)
            } else {
                departmentAdapter!!.unselectall()
                ALL2!!.setText(CommonUtil.Select_All)

            }
        })


        ch_all2!!.setOnClickListener(View.OnClickListener {

            if (ch_all2!!.isChecked()) {
                SelectedcourseAdapter!!.selectAll()
                ALL3!!.setText(CommonUtil.Remove_All)
            } else {
                SelectedcourseAdapter!!.unselectall()
                ALL3!!.setText(CommonUtil.Select_All)

            }
        })

        ch_all4!!.setOnClickListener(View.OnClickListener {

            CommonUtil.seleteddataArrayCheckbox.clear()
            if (ch_all4!!.isChecked) {
                SpecificStudentList!!.selectAll()
                ALL4!!.text = CommonUtil.Remove_All
            } else {
                SpecificStudentList!!.unselectall()
                ALL4!!.text = CommonUtil.Select_All

            }
        })


        isParent = false
        isStudent = true
        isStaff = false



        chboxParents!!.setOnClickListener {

            if (chboxParents!!.isChecked) {
                isParent = true


                if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                        CommonUtil.Groups
                    ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                        CommonUtil.Staff
                    )
                ) {
                    if (isParent!! && isStudent) {
                        chboxAll!!.isChecked = true

                    } else {
                        chboxAll!!.isChecked = false
                    }

                } else {
                    if (isParent!! && isStudent && isStaff!!) {
                        chboxAll!!.isChecked = true

                    } else {
                        chboxAll!!.isChecked = false
                    }

                }

            } else {
                isParent = false

                chboxAll!!.isChecked = false
            }

        }

        chboxStaff!!.setOnClickListener {

            if (CommonUtil.isParentEnable.equals("1")) {

                if (chboxStaff!!.isChecked) {
                    isStaff = true


                    if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                            CommonUtil.Groups
                        ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                            CommonUtil.Staff
                        )
                    ) {
                        if (isParent!! && isStudent) {
                            chboxAll!!.isChecked = true

                        } else {
                            chboxAll!!.isChecked = false
                        }
                    } else {
                        if (isParent!! && isStudent && isStaff!!) {
                            chboxAll!!.isChecked = true

                        } else {
                            chboxAll!!.isChecked = false
                        }
                    }

                } else {
                    isStaff = false
                    chboxAll!!.isChecked = false
                }
            } else {

                if (chboxStaff!!.isChecked) {
                    isStaff = true

                    if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                            CommonUtil.Groups
                        ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                            CommonUtil.Staff
                        )
                    ) {
                        if (isStudent!!) {
                            chboxAll!!.isChecked = true

                        } else {
                            chboxAll!!.isChecked = false

                        }
                    } else {

                        if (isStudent!! && isStaff!!) {
                            chboxAll!!.isChecked = true
                        } else {
                            chboxAll!!.isChecked = false
                        }
                    }

                } else {
                    isStaff = false
                    chboxAll!!.isChecked = false
                }

            }

        }

        chboxStudent!!.setOnClickListener {


            if (CommonUtil.isParentEnable.equals("1")) {

                if (chboxStudent!!.isChecked) {

                    isStudent = true

                    if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                            CommonUtil.Groups
                        ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                            CommonUtil.Staff
                        )
                    ) {

                        if (isParent!! && isStudent!!) {
                            chboxAll!!.isChecked = true

                        } else {
                            chboxAll!!.isChecked = false

                        }

                    } else {
                        if (isParent!! && isStudent!! && isStaff!!) {
                            chboxAll!!.isChecked = true

                        } else {
                            chboxAll!!.isChecked = false
                        }
                    }

                } else {
                    isStudent = false

                    chboxAll!!.isChecked = false
                }

            } else {

                if (chboxStudent!!.isChecked) {
                    isStudent = true

                    if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                            CommonUtil.Groups
                        ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                            CommonUtil.Staff
                        )
                    ) {

                        if (isStudent!!) {
                            chboxAll!!.isChecked = true
                        } else {
                            chboxAll!!.isChecked = false

                        }
                    } else {

                        if (isStudent!! && isStaff!!) {
                            chboxAll!!.isChecked = true
                        } else {
                            chboxAll!!.isChecked = false
                        }
                    }

                } else {
                    isStudent = false
                    chboxAll!!.isChecked = false
                }
            }
        }

        chboxAll!!.setOnClickListener {
            if (chboxAll!!.isChecked) {

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

                        chboxStudent!!.isChecked = true
                        chboxParents!!.isChecked = true
                        chboxStaff!!.isChecked = false

                    } else {

                        isStudent = true
                        isStaff = true
                        isParent = true

                        chboxStudent!!.isChecked = true
                        chboxParents!!.isChecked = true
                        chboxStaff!!.isChecked = true

                    }

                } else {

                    if (Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Your_Classes) || Card_name.equals(
                            CommonUtil.Groups
                        ) || Card_name.equals(CommonUtil.Course) || Card_name.equals(CommonUtil.Year_Section) || Card_name.equals(
                            CommonUtil.Staff
                        )
                    ) {

                        isStudent = true
                        isStaff = false
                        isParent = false

                        chboxStudent!!.isChecked = true
                        chboxParents!!.isChecked = false
                        chboxStaff!!.isChecked = false

                    } else {

                        isStudent = true
                        isStaff = true
                        isParent = false

                        chboxStudent!!.isChecked = true
                        chboxParents!!.isChecked = false
                        chboxStaff!!.isChecked = true
                    }
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




        if (CommonUtil.Priority == "p1") {
            lnrStaff!!.visibility = View.VISIBLE
        } else if (lblEntireDepartmentlable!!.text.equals(CommonUtil.Entire_Department)) {
            lnrStaff!!.visibility = View.VISIBLE
        } else if (CommonUtil.Priority == "p3") {
            lnrStaff!!.visibility = View.GONE
        }

        if (CommonUtil.Priority == "p1") {

            lblEntireDepartmentlable!!.visibility = View.VISIBLE
            if (ScreenName!!.equals(CommonUtil.New_Assignment) || ScreenName.equals(CommonUtil.Forward_Assignment)) {
                LayoutRecipient!!.visibility = View.GONE
            } else {
                LayoutRecipient!!.visibility = View.VISIBLE
            }
            NestedScrollView!!.visibility = View.VISIBLE

            txt_department!!.visibility = View.GONE
            Viewlinethree!!.visibility = View.GONE
            txt_mydepartment!!.visibility = View.GONE
            txt_myclass!!.visibility = View.GONE

            lbl_select_student!!.visibility = View.GONE
            ch_all4!!.isChecked = false
            ALL4!!.setText(CommonUtil.Select_All)
            Viewlinefour!!.visibility = View.GONE
            recycle_specificstudent!!.visibility = View.GONE
            relative_Recycle!!.visibility = View.GONE
            ALL4!!.visibility = View.GONE
            ch_all4!!.visibility = View.GONE
            SearchView!!.visibility = View.GONE


        } else if (CommonUtil.Priority.equals("p3")) {

            lblDivision!!.visibility = View.GONE
            lblCourse!!.visibility = View.GONE
            lblDepartment!!.visibility = View.GONE
            lblEntireDepartmentlable!!.visibility = View.GONE
            lblGroups!!.visibility = View.GONE
            lblYourClasses!!.visibility = View.GONE


            if (ScreenName!!.equals(CommonUtil.New_Assignment) || ScreenName.equals(CommonUtil.Forward_Assignment)) {
                LayoutRecipient!!.visibility = View.GONE
            } else {
                LayoutRecipient!!.visibility = View.VISIBLE
                Spinning_yourclasses!!.visibility = View.VISIBLE
                Yourclasses()
            }
            Viewlineone!!.visibility = View.GONE
            NestedScrollView!!.visibility = View.GONE

            txt_selectsubortutor!!.visibility = View.VISIBLE
            Viewlinetwo!!.visibility = View.VISIBLE
            recycle_Staffrecipients!!.visibility = View.VISIBLE
            txt_selectspecfic!!.visibility = View.VISIBLE
            txt_department!!.visibility = View.GONE
            Viewlinethree!!.visibility = View.GONE
            txt_mydepartment!!.visibility = View.GONE
            txt_myclass!!.visibility = View.GONE

            if (ScreenName.equals(CommonUtil.CommunicationVoice)) {
                if (CommonUtil.isAllowtomakecall == 1) {
                    txt_onandoff!!.visibility = View.VISIBLE
                }
            }

        } else if (CommonUtil.Priority.equals("p2")) {

            txt_department!!.visibility = View.VISIBLE
            Viewlinethree!!.visibility = View.VISIBLE
            if (ScreenName.equals(CommonUtil.Forward_Assignment) || ScreenName!!.equals(CommonUtil.New_Assignment)) {

                txt_mydepartment!!.visibility = View.GONE
                txt_myclass!!.visibility = View.GONE
                txt_department!!.visibility = View.GONE
                Viewlinethree!!.visibility = View.GONE
                txt_selectspecfic!!.visibility = View.VISIBLE

            } else {
                txt_mydepartment!!.visibility = View.VISIBLE
                txt_onandoff!!.visibility = View.GONE
                txt_myclass!!.visibility = View.VISIBLE
                txt_selectspecfic!!.visibility = View.GONE
            }

            lblEntireDepartmentlable!!.visibility = View.GONE
            LayoutRecipient!!.visibility = View.GONE
            Viewlineone!!.visibility = View.GONE
            NestedScrollView!!.visibility = View.GONE
            txt_selectsubortutor!!.visibility = View.GONE
            Viewlinetwo!!.visibility = View.GONE
            Spinning_yourclasses!!.visibility = View.GONE
            recycle_Staffrecipients!!.visibility = View.GONE
            lbl_select_student!!.visibility = View.GONE
            Viewlinefour!!.visibility = View.GONE
            recycle_specificstudent!!.visibility = View.GONE
            relative_Recycle!!.visibility = View.GONE
            ALL4!!.visibility = View.GONE
            ch_all4!!.visibility = View.GONE
            SearchView!!.visibility = View.GONE
            ch_all4!!.isChecked = false
            ALL4!!.setText(CommonUtil.Select_All)

        }


        txt_selectspecfic_YearandSecrion!!.setOnClickListener {

            if (CommonUtil.seleteddataArray.size == 1) {

                for (i in CommonUtil.DepartmentChooseIds.indices) {
                    CommonUtil.SectionIdChoose = CommonUtil.DepartmentChooseIds[i].toString()
                }
                CommonUtil.DepartmentChooseIds.clear()
                Log.d("ReciverId", CommonUtil.receiverid)
                Specific = "Student"
                lbl_select_student!!.visibility = View.VISIBLE
                Viewlinefour!!.visibility = View.VISIBLE
                relative_Recycle!!.visibility = View.VISIBLE
                ALL4!!.visibility = View.VISIBLE
                ch_all4!!.visibility = View.VISIBLE
                SearchView!!.visibility = View.VISIBLE
                ch_all4!!.isChecked = false
                ALL4!!.setText(CommonUtil.Select_All)

                txt_selectsubortutor!!.visibility = View.GONE
                Viewlinetwo!!.visibility = View.GONE
                Spinning_yourclasses!!.visibility = View.GONE

                recycle_Staffrecipients!!.visibility = View.GONE
                txt_selectspecfic!!.visibility = View.GONE

                lblEntireDepartmentlable!!.visibility = View.GONE

                LayoutRecipient!!.visibility = View.GONE
                Viewlineone!!.visibility = View.GONE
                NestedScrollView!!.visibility = View.GONE

                txt_department!!.visibility = View.GONE
                Viewlinethree!!.visibility = View.GONE
                txt_mydepartment!!.visibility = View.GONE
                txt_myclass!!.visibility = View.GONE

                yearSection = false
                getspecificstudentdata()
                txt_selectspecfic_YearandSecrion!!.visibility = View.GONE

                CommonUtil.receiverid = ""
            } else if (CommonUtil.seleteddataArray.size > 1) {

                CommonUtil.ApiAlert(
                    this, CommonUtil.Select_MinimumOne
                )

            } else {
                CommonUtil.ApiAlert(
                    this, CommonUtil.Select_OnlyOne
                )
            }
        }


        txt_selectspecfic!!.setOnClickListener {

            if (ScreenName.equals(CommonUtil.Forward_Assignment) || ScreenName!!.equals(CommonUtil.New_Assignment)) {

                if (!CommonUtil.receiverid.isEmpty()) {

                    for (i in CommonUtil.DepartmentChooseIds.indices) {
                        CommonUtil.SectionIdChoose = CommonUtil.DepartmentChooseIds[i]
                    }
                    CommonUtil.DepartmentChooseIds.clear()
                    lbl_select_student!!.visibility = View.VISIBLE
                    Viewlinefour!!.visibility = View.VISIBLE
                    recycle_specificstudent!!.visibility = View.VISIBLE
                    relative_Recycle!!.visibility = View.VISIBLE
                    ALL4!!.visibility = View.VISIBLE
                    ch_all4!!.visibility = View.VISIBLE
                    SearchView!!.visibility = View.VISIBLE
                    ch_all4!!.isChecked = false
                    ALL4!!.setText(CommonUtil.Select_All)

                    txt_selectsubortutor!!.visibility = View.GONE
                    Viewlinetwo!!.visibility = View.GONE
                    Spinning_yourclasses!!.visibility = View.GONE
                    recycle_Staffrecipients!!.visibility = View.GONE
                    txt_selectspecfic!!.visibility = View.GONE
                    lblEntireDepartmentlable!!.visibility = View.GONE
                    LayoutRecipient!!.visibility = View.GONE
                    Viewlineone!!.visibility = View.GONE
                    NestedScrollView!!.visibility = View.GONE
                    txt_department!!.visibility = View.GONE
                    Viewlinethree!!.visibility = View.GONE
                    txt_mydepartment!!.visibility = View.GONE
                    txt_myclass!!.visibility = View.GONE


                    if (SpinningText!!.equals(CommonUtil.Subjects)) {
                        SenderType = ""
                        // yearSection = true
                        getspecificstudentdata()
                        CommonUtil.receiverid = ""
                        CommonUtil.seleteddataArrayCheckbox.clear()

                    } else if (SpinningText!!.equals(CommonUtil.Tutor)) {

                        SenderType = CommonUtil.Tutor
                        getspecificstudentdatasubject()
                        CommonUtil.receiverid = ""
                        CommonUtil.seleteddataArrayCheckbox.clear()


                    } else if (SpinningText!!.equals(CommonUtil.Assignment_SPECIFIC)) {

                        getspecificstudentdata()
                        CommonUtil.receiverid = ""
                        CommonUtil.seleteddataArrayCheckbox.clear()

                    }

                } else {
                    CommonUtil.ApiAlert(
                        this, CommonUtil.Select_MinimumOne
                    )
                }

            } else {

                if (CommonUtil.seleteddataArraySection.size == 1) {

                    for (i in CommonUtil.seleteddataArraySection.indices) {
                        CommonUtil.receiverid = CommonUtil.seleteddataArraySection[i]
                    }
                    for (i in CommonUtil.DepartmentChooseIds.indices) {
                        CommonUtil.SectionIdChoose = CommonUtil.DepartmentChooseIds[i].toString()
                    }
                    CommonUtil.DepartmentChooseIds.clear()
                    lbl_select_student!!.visibility = View.VISIBLE
                    Viewlinefour!!.visibility = View.VISIBLE
                    recycle_specificstudent!!.visibility = View.VISIBLE
                    relative_Recycle!!.visibility = View.VISIBLE
                    ALL4!!.visibility = View.VISIBLE
                    ch_all4!!.visibility = View.VISIBLE
                    SearchView!!.visibility = View.VISIBLE
                    ch_all4!!.isChecked = false
                    ALL4!!.setText(CommonUtil.Select_All)

                    txt_selectsubortutor!!.visibility = View.GONE
                    Viewlinetwo!!.visibility = View.GONE
                    Spinning_yourclasses!!.visibility = View.GONE

                    recycle_Staffrecipients!!.visibility = View.GONE
                    txt_selectspecfic!!.visibility = View.GONE

                    lblEntireDepartmentlable!!.visibility = View.GONE

                    LayoutRecipient!!.visibility = View.GONE
                    Viewlineone!!.visibility = View.GONE
                    NestedScrollView!!.visibility = View.GONE

                    txt_department!!.visibility = View.GONE
                    Viewlinethree!!.visibility = View.GONE
                    txt_mydepartment!!.visibility = View.GONE
                    txt_myclass!!.visibility = View.GONE


                    if (SpinningText!!.equals(CommonUtil.Subjects)) {
                        SenderType = ""
                        yearSection = true
                        getspecificstudentdata()
                        CommonUtil.receiverid = ""
                        CommonUtil.receivertype = "7"
                        CommonUtil.seleteddataArrayCheckbox.clear()


                    } else if (SpinningText!!.equals(CommonUtil.Tutor)) {

                        SenderType = CommonUtil.Tutor
                        CommonUtil.receivertype = "7"
                        getspecificstudentdatasubject()
                        CommonUtil.receiverid = ""
                        CommonUtil.seleteddataArrayCheckbox.clear()


                    } else if (SpinningText!!.equals(CommonUtil.Assignment_SPECIFIC)) {

                        getspecificstudentdata()
                        CommonUtil.receiverid = ""
                        CommonUtil.seleteddataArrayCheckbox.clear()


                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.Select_OnlyOne)
                }
            }
        }

        txt_mydepartment!!.setOnClickListener {

            My_Department = "Yes"
            if (ScreenName.equals(CommonUtil.CommunicationVoice)) {
                if (CommonUtil.isAllowtomakecall == 1) {
                    txt_onandoff!!.visibility = View.VISIBLE
                }
            }
            lblEntireDepartmentlable!!.setText(CommonUtil.Entire_Department)
            lblDepartment!!.text = CommonUtil.Year_Section
            lblDivision!!.text = CommonUtil.Course

            lblEntireDepartmentlable!!.visibility = View.VISIBLE

            if (ScreenName!!.equals(CommonUtil.New_Assignment) || ScreenName.equals(CommonUtil.Forward_Assignment)) {
                LayoutRecipient!!.visibility = View.GONE
            } else {
                LayoutRecipient!!.visibility = View.VISIBLE
            }
            Viewlineone!!.visibility = View.VISIBLE
            lblDivision!!.visibility = View.VISIBLE
            lblDepartment!!.visibility = View.VISIBLE

            lblYourClasses!!.visibility = View.GONE
            lblGroups!!.visibility = View.GONE
            lblCourse!!.visibility = View.GONE

            txt_department!!.visibility = View.GONE
            Viewlinethree!!.visibility = View.GONE
            txt_mydepartment!!.visibility = View.GONE
            txt_myclass!!.visibility = View.GONE

        }

        txt_myclass!!.setOnClickListener {
            if (ScreenName.equals(CommonUtil.CommunicationVoice)) {
                if (CommonUtil.isAllowtomakecall == 1) {
                    txt_onandoff!!.visibility = View.VISIBLE
                }
            }
            Yourclasses()
            lblEntireDepartmentlable!!.visibility = View.GONE
            LayoutRecipient!!.visibility = View.GONE
            Viewlineone!!.visibility = View.GONE
            NestedScrollView!!.visibility = View.GONE
            txt_selectsubortutor!!.visibility = View.VISIBLE
            Viewlinetwo!!.visibility = View.VISIBLE
            recycle_Staffrecipients!!.visibility = View.VISIBLE
            txt_selectspecfic!!.visibility = View.VISIBLE

            txt_department!!.visibility = View.GONE
            Viewlinethree!!.visibility = View.GONE
            txt_mydepartment!!.visibility = View.GONE
            txt_myclass!!.visibility = View.GONE

            My_Department = "No"

        }

        //specific student subject

        appViewModel!!.Getspecificstudentsubject!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {

                    getspecifictuterstudent = response.data!!
                    SearchView!!.visibility = View.VISIBLE

                    ALL4!!.visibility = View.VISIBLE
                    ch_all4!!.visibility = View.VISIBLE
                    specificStudent_adapter = specificStudent_adapter(getspecifictuterstudent, this)

                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    recycle_specificstudent!!.layoutManager = mLayoutManager
                    recycle_specificstudent!!.itemAnimator = DefaultItemAnimator()
                    recycle_specificstudent!!.adapter = specificStudent_adapter
                    recycle_specificstudent!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    specificStudent_adapter!!.notifyDataSetChanged()
                }
            }
        }



        appViewModel!!.Getspecificstudenttutot!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                CommonUtil.receiverid = ""
                if (status == 1) {

                    getspecifictuterstudent = response.data!!
                    SearchView!!.visibility = View.VISIBLE
                    ALL4!!.visibility = View.VISIBLE
                    ch_all4!!.visibility = View.VISIBLE
                    recycle_specificstudent!!.visibility = View.VISIBLE

                    SelectedRecipientlist.clear()


                    getspecifictuterstudent.forEach {
                        it.memberid
                        it.name

                        val group = RecipientSelected(it.memberid, it.name)
                        SelectedRecipientlist.add(group)
                    }

                    Log.d("GetStudentList", SelectedRecipientlist.size.toString())


                    SpecificStudentList = SelectedRecipientAdapter(
                        SelectedRecipientlist,
                        this,
                        object : RecipientCheckListener {
                            override fun add(data: RecipientSelected?) {
                                var groupid = data!!.SelectedId

                                if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {

                                    if (CommonUtil.seleteddataArrayCheckbox.size + 1 == 0) {
                                        ch_all4!!.isChecked = false

                                        ALL4!!.setText(CommonUtil.Select_All)
                                    } else {
                                        ch_all4!!.isChecked = true
                                        ALL4!!.setText(CommonUtil.Remove_All)
                                    }

                                } else {

                                    ch_all4!!.isChecked = false
                                    ALL4!!.setText(CommonUtil.Select_All)

                                }

                            }

                            override fun remove(data: RecipientSelected?) {
                                var groupid = data!!.SelectedId

                                ch_all4!!.isChecked = false
                                ALL4!!.setText(CommonUtil.Select_All)
                            }
                        })

                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    recycle_specificstudent!!.layoutManager = mLayoutManager
                    recycle_specificstudent!!.itemAnimator = DefaultItemAnimator()
                    recycle_specificstudent!!.adapter = SpecificStudentList
                    recycle_specificstudent!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    SpecificStudentList!!.notifyDataSetChanged()
                } else {

                    SearchView!!.visibility = View.GONE
                    recycle_specificstudent!!.visibility = View.GONE
                    ch_all4!!.visibility = View.GONE
                    ALL4!!.visibility = View.GONE
                }
            } else {

                SearchView!!.visibility = View.GONE
                ch_all4!!.visibility = View.GONE
                ALL4!!.visibility = View.GONE
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

        appViewModel!!.Assign_forward!!.observe(this) { response ->
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

        //subject data

        appViewModel!!.Getsubjectdata!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    recycle_Staffrecipients!!.visibility = View.VISIBLE
                    getsubjectlist = response.data!!
                    subjectadapter = Subject_Adapter(getsubjectlist, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    recycle_Staffrecipients!!.layoutManager = mLayoutManager
                    recycle_Staffrecipients!!.itemAnimator = DefaultItemAnimator()
                    recycle_Staffrecipients!!.adapter = subjectadapter
                    recycle_Staffrecipients!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    subjectadapter!!.notifyDataSetChanged()
                } else {
                    CommonUtil.ApiAlert(
                        this, message
                    )
                    recycle_Staffrecipients!!.visibility = View.GONE
                }
            } else {
                CommonUtil.ApiAlert(
                    this, "Subject or Section Not allocated / Students not allocated to the section"
                )
                recycle_Staffrecipients!!.visibility = View.GONE
            }
        }

        //tutor data

        appViewModel!!.Gettuter!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    recycle_Staffrecipients!!.visibility = View.VISIBLE

                    getsubjectlist = response.data!!
                    subjectadapter = Subject_Adapter(getsubjectlist, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    recycle_Staffrecipients!!.layoutManager = mLayoutManager
                    recycle_Staffrecipients!!.itemAnimator = DefaultItemAnimator()
                    recycle_Staffrecipients!!.adapter = subjectadapter
                    recycle_Staffrecipients!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    subjectadapter!!.notifyDataSetChanged()
                } else {
                    CommonUtil.ApiAlert(
                        this, message
                    )
                    recycle_Staffrecipients!!.visibility = View.GONE
                }
            } else {
                CommonUtil.ApiAlert(
                    this, "Subject or Section Not allocated / Students not allocated to the section"
                )
                recycle_Staffrecipients!!.visibility = View.GONE

            }
        }

        //HOD COURSE DATA

        appViewModel!!.GetCoursesByDepartmenthod!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message

                if (status == 1) {
                    Getcoursedepartment = response.data!!
                    if (Getcoursedepartment!!.size > 0) {


                        SelectedRecipientlist.clear()

                        Getcoursedepartment!!.forEach {
                            it.course_id
                            it.course_name

                            val group = RecipientSelected(it.course_id, it.course_name)
                            SelectedRecipientlist.add(group)
                        }


                        if (SelecteRecipientType.equals(CommonUtil.Course)) {
                            Log.d("SelectedRecipientlist", SelectedRecipientlist.toString())
                            SelectedcourseAdapter = SelectedRecipientAdapter(
                                SelectedRecipientlist,
                                this,
                                object : RecipientCheckListener {
                                    override fun add(data: RecipientSelected?) {
                                        val groupid = data!!.SelectedId
                                        Log.d("groupid", groupid.toString())

                                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {


                                            if (CommonUtil.seleteddataArrayCheckbox.size == 0) {
                                                ch_all!!.isChecked = false
                                                Division_All!!.setText(CommonUtil.Select_All)
                                            } else {
                                                ch_all!!.isChecked = true
                                                Division_All!!.setText(CommonUtil.Remove_All)
                                            }

                                        } else {

                                            ch_all!!.isChecked = false
                                            Division_All!!.setText(CommonUtil.Select_All)

                                        }

                                    }

                                    override fun remove(data: RecipientSelected?) {
                                        var groupid = data!!.SelectedId

                                        ch_all!!.isChecked = false
                                        Division_All!!.setText(CommonUtil.Select_All)
                                    }
                                })

                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            recycleRecipients!!.layoutManager = mLayoutManager
                            recycleRecipients!!.itemAnimator = DefaultItemAnimator()
                            recycleRecipients!!.adapter = SelectedcourseAdapter
                            recycleRecipients!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            SelectedcourseAdapter!!.notifyDataSetChanged()

                        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {
                            LoadyearandsectionSpinnerhod()
                            Log.d("SelecteRecipientType", SelecteRecipientType.toString())
                        }
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                }
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

                            val group = RecipientSelected(it.groupid, it.groupname)

                            SelectedRecipientlist.add(group)
                        }
                        if (SelecteRecipientType.equals(CommonUtil.Groups)) {
                            groupAdapter = SelectedRecipientAdapter(
                                SelectedRecipientlist,
                                this,
                                object : RecipientCheckListener {
                                    override fun add(data: RecipientSelected?) {
                                        val groupid = data!!.SelectedId
                                        Log.d("groupid", groupid.toString())

                                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {


                                            if (CommonUtil.seleteddataArrayCheckbox.size == 0) {
                                                ch_all!!.isChecked = false
                                                Division_All!!.setText(CommonUtil.Select_All)

                                            } else {
                                                ch_all!!.isChecked = true
                                                Division_All!!.setText(CommonUtil.Remove_All)


                                            }

                                        } else {

                                            ch_all!!.isChecked = false
                                            Division_All!!.setText(CommonUtil.Select_All)


                                        }

                                    }

                                    override fun remove(data: RecipientSelected?) {
                                        var groupid = data!!.SelectedId

                                        ch_all!!.isChecked = false
                                        Division_All!!.setText(CommonUtil.Select_All)


                                    }
                                })


                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            recycleRecipients!!.layoutManager = mLayoutManager
                            recycleRecipients!!.itemAnimator = DefaultItemAnimator()
                            recycleRecipients!!.adapter = groupAdapter
                            recycleRecipients!!.recycledViewPool.setMaxRecycledViews(0, 80)
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

        //year and section

        appViewModel!!.GetCoursesByDepartmenthodhod!!.observe(this) { response ->
            if (response != null) {
                recycleyearandsection!!.visibility = View.VISIBLE
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    txt_selectspecfic_YearandSecrion!!.visibility = View.VISIBLE
                    recycleyearandsection!!.visibility = View.VISIBLE
                    Getyouurclassdata = (response.data as ArrayList<Data>?)!!

                    CommonUtil.seleteddataArray.clear()
                    sectionandyear_Adapter = sectionandyear_Adapter(Getyouurclassdata!!, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    recycleyearandsection!!.layoutManager = mLayoutManager
                    recycleyearandsection!!.itemAnimator = DefaultItemAnimator()
                    recycleyearandsection!!.adapter = sectionandyear_Adapter
                    recycleyearandsection!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    sectionandyear_Adapter!!.notifyDataSetChanged()
                } else {

                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    recycleyearandsection!!.visibility = View.GONE
                    txt_selectspecfic_YearandSecrion!!.visibility = View.GONE
                }
            } else {

                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
                recycleyearandsection!!.visibility = View.GONE
                txt_selectspecfic_YearandSecrion!!.visibility = View.GONE
            }
        }

        //division

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

                            val divisions = RecipientSelected(it.division_id, it.division_name)
                            SelectedRecipientlist.add(divisions)
                        }
                        if (SelecteRecipientType.equals(CommonUtil.Division)) {

                            recycleRecipients!!.visibility = View.VISIBLE
                            divisionadapter = SelectedRecipientAdapter(SelectedRecipientlist,
                                this,
                                object : RecipientCheckListener {
                                    override fun add(data: RecipientSelected?) {
                                        var divisionId = data!!.SelectedId

                                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {


                                            if (CommonUtil.seleteddataArrayCheckbox.size == 0) {
                                                ch_all!!.isChecked = false
                                                Division_All!!.setText(CommonUtil.Select_All)
                                            } else {
                                                ch_all!!.isChecked = true
                                                Division_All!!.setText(CommonUtil.Remove_All)
                                            }

                                        } else {

                                            ch_all!!.isChecked = false
                                            Division_All!!.setText(CommonUtil.Select_All)

                                        }
                                    }

                                    override fun remove(data: RecipientSelected?) {
                                        var divisionId = data!!.SelectedId
                                        ch_all!!.isChecked = false
                                        Division_All!!.setText(CommonUtil.Select_All)

                                    }
                                })

                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            recycleRecipients!!.layoutManager = mLayoutManager
                            recycleRecipients!!.itemAnimator = DefaultItemAnimator()
                            recycleRecipients!!.adapter = divisionadapter
                            recycleRecipients!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            divisionadapter!!.notifyDataSetChanged()

                        } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                            LoadDivisionSpinner()

                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {

                            LoadDivisionSpinner()
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
                        GetCourseData!!.forEach {
                            it.course_id
                            it.course_name
                            var department = RecipientSelected(it.course_id, it.course_name)
                            SelectedRecipientlist.add(department)
                        }

                        if (SelecteRecipientType.equals(CommonUtil.Course)) {
                            SelectedcourseAdapter = SelectedRecipientAdapter(
                                SelectedRecipientlist,
                                this,
                                object : RecipientCheckListener {
                                    override fun add(data: RecipientSelected?) {
                                        var depaetmentid = data!!.SelectedId

                                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {

                                            if (CommonUtil.seleteddataArrayCheckbox.size == 0) {
                                                ch_all2!!.isChecked = false
                                                ALL3!!.setText(CommonUtil.Select_All)

                                            } else {

                                                ch_all2!!.isChecked = true
                                                ALL3!!.setText(CommonUtil.Remove_All)

                                            }

                                        } else {

                                            ch_all2!!.isChecked = false
                                            ALL3!!.setText(CommonUtil.Select_All)


                                        }
                                    }

                                    override fun remove(data: RecipientSelected?) {
                                        var departmentid = data!!.SelectedId

                                        ch_all2!!.isChecked = false
                                        ALL3!!.setText(CommonUtil.Select_All)

                                    }
                                })

                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            recycleRecipientscourse!!.layoutManager = mLayoutManager
                            recycleRecipientscourse!!.itemAnimator = DefaultItemAnimator()
                            recycleRecipientscourse!!.adapter = SelectedcourseAdapter
                            recycleRecipientscourse!!.recycledViewPool.setMaxRecycledViews(
                                0, 80
                            )
                            SelectedcourseAdapter!!.notifyDataSetChanged()
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

        //GET DEPARTMENT PRINCIPLE

        appViewModel!!.GetDepartmentMutableLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    GetDepartmentData = response.data!!
                    if (GetDepartmentData!!.size > 0) {
                        SelectedRecipientlist.clear()
                        GetDepartmentData!!.forEach {
                            it.department_id
                            it.department_name
                            val divisions = RecipientSelected(it.department_id, it.department_name)
                            SelectedRecipientlist.add(divisions)
                        }
                        if (SelecteRecipientType.equals(CommonUtil.Department_)) {
                            departmentAdapter = SelectedRecipientAdapter(SelectedRecipientlist,
                                this,
                                object : RecipientCheckListener {
                                    override fun add(data: RecipientSelected?) {
                                        var divisionId = data!!.SelectedId

                                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {

                                            if (CommonUtil.seleteddataArrayCheckbox.size == 0) {
                                                ch_all1!!.isChecked = false
                                                ALL2!!.setText(CommonUtil.Select_All)
                                            } else {
                                                ch_all1!!.isChecked = true
                                                ALL2!!.setText(CommonUtil.Remove_All)
                                            }

                                        } else {

                                            ch_all1!!.isChecked = false
                                            ALL2!!.setText(CommonUtil.Select_All)

                                        }
                                    }

                                    override fun remove(data: RecipientSelected?) {
                                        var divisionId = data!!.SelectedId
                                        ch_all1!!.isChecked = false
                                        ALL2!!.setText(CommonUtil.Select_All)


                                    }
                                })

                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this)
                            recycleRecipients!!.layoutManager = mLayoutManager
                            recycleRecipients!!.itemAnimator = DefaultItemAnimator()
                            recycleRecipients!!.adapter = departmentAdapter
                            recycleRecipients!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            departmentAdapter!!.notifyDataSetChanged()

                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
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


        //sms entire

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

        SearchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {

                filter(msg)
                return false
            }
        })

    }

    private fun filter(text: String) {

        val filteredlist: ArrayList<RecipientSelected> = ArrayList()

        for (item in SelectedRecipientlist) {
            if (item.SelectedName!!.toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            CommonUtil.Toast(this, "No records found.")
        } else {
            SpecificStudentList!!.filterList(filteredlist, false)
        }
    }

    //-------------SPINNING DATA----------

    private fun LoadDivisionSpinner() {

        SpinnerData.clear()
        SpinnerData.add("Select Division")
        GetDivisionData!!.forEach {
            SpinnerData.add(it.division_name!!)
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_textview, SpinnerData)

        adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
        spinnerDropdown!!.adapter = adapter

        spinnerDropdown!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {

                CommonUtil.seleteddataArrayCheckbox.clear()
                CommonUtil.receiverid = ""
                CommonUtil.SeletedStringdataReplace = ""

                ch_all1!!.isChecked = false
                ALL2!!.setText(CommonUtil.Select_All)

                if (position != 0) {

                    SelectedSpinnerID = GetDivisionData!!.get(position - 1).division_id
                    GetDivisionData!!.get(position - 1).division_name?.let {

                        if (SelecteRecipientType.equals(CommonUtil.Department_)) {

                            ALL2!!.visibility = View.VISIBLE
                            ch_all1!!.visibility = View.VISIBLE
                            recycleRecipients!!.visibility = View.VISIBLE
                            spinnerDropdowncourse!!.visibility = View.GONE

                        } else {
                            recycleRecipients!!.visibility = View.GONE
                            ALL2!!.visibility = View.GONE
                            ch_all1!!.visibility = View.GONE
                            spinnerDropdowncourse!!.visibility = View.VISIBLE

                        }
                    }
                    GetDepartmentRequest()

                } else {

                    ALL2!!.visibility = View.GONE
                    ch_all1!!.visibility = View.GONE
                    ALL3!!.visibility = View.GONE
                    ch_all2!!.visibility = View.GONE

                    recycleRecipients!!.visibility = View.GONE
                    recycleRecipientscourse!!.visibility = View.GONE
                    spinnerDropdowncourse!!.visibility = View.GONE

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }

        }
    }

    private fun LoadDepartmentSpinner() {

        SpinningCoursedata.clear()
        SpinningCoursedata.add("Select Department")
        GetDepartmentData!!.forEach {
            SpinningCoursedata.add(it.department_name!!)
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_rextview_course, SpinningCoursedata)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_course_layout)
        spinnerDropdowncourse!!.adapter = adapter

        spinnerDropdowncourse!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {

                    CommonUtil.seleteddataArrayCheckbox.clear()
                    CommonUtil.receiverid = ""
                    CommonUtil.SeletedStringdataReplace = ""

                    ch_all2!!.isChecked = false
                    ALL3!!.setText(CommonUtil.Select_All)

                    if (position != 0) {

                        recycleRecipientscourse!!.visibility = View.VISIBLE
                        SelectedSpinnerID = GetDepartmentData!!.get(position - 1).department_id
                        Log.d("spinnerselected", SelectedSpinnerID!!)
                        GetDepartmentData!!.get(position - 1).department_name?.let {
                            Log.d("spinning data", it)

                            ALL3!!.visibility = View.VISIBLE
                            ch_all2!!.visibility = View.VISIBLE

                        }

                        GetCourseRequesr()

                    } else {

                        ALL3!!.visibility = View.GONE
                        ch_all2!!.visibility = View.GONE
                        recycleRecipientscourse!!.visibility = View.GONE

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
    }

    private fun LoadyearandsectionSpinnerhod() {

        spinnerDropdown!!.visibility = View.VISIBLE
        SpinningCoursedatahod.clear()
        SpinningCoursedatahod.add("Select Course")
        Getcoursedepartment!!.forEach {
            SpinningCoursedatahod.add(it.course_name!!)
            Log.d("SpinningCoursedata", SpinningCoursedatahod.toString())
        }


        val adapter = ArrayAdapter(this, R.layout.spinner_rextview_course, SpinningCoursedatahod)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_course_layout)
        spinnerDropdown!!.adapter = adapter

        spinnerDropdown!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {

                CommonUtil.receiverid = ""
                if (position != 0) {

                    CommonUtil.DepartmentChooseIds.clear()
                    recycleyearandsection!!.visibility = View.VISIBLE
                    txt_selectspecfic_YearandSecrion!!.visibility = View.VISIBLE
                    SelectedSpinnerIDhod = Getcoursedepartment!![position - 1].course_id

                    Getcoursedepartment!![position - 1].course_name?.let {
                        Log.d("spinning data", it)
                    }
                    GetyearandsectionRequest()

                } else {
                    recycleyearandsection!!.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun Yourclasses() {

        Spinning_yourclasses!!.visibility = View.VISIBLE

        val YourClasses = ArrayList<String>()

        SpinningCoursedatahod.clear()
        YourClasses.add("Select Your Type")
        YourClasses.add(CommonUtil.Subjects)
        YourClasses.add(CommonUtil.Tutor)

        val adapter = ArrayAdapter(this, R.layout.spinner_rextview_course, YourClasses)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_course_layout)
        Spinning_yourclasses!!.adapter = adapter

        Spinning_yourclasses!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {
                    CommonUtil.receiverid = ""
                    SenderType = ""
                    if (CommonUtil.Priority.equals("p3")) {


                        if (position != 0) {
                            CommonUtil.DepartmentChooseIds.clear()
                            if (position == 1) {

                                recycle_Staffrecipients!!.visibility = View.VISIBLE
                                GetSubjectstaff()
                                SenderType = ""
                                SpinningText = CommonUtil.Subjects
                            } else if (position == 2) {

                                Gettuter()
                                SenderType = CommonUtil.Tutor
                                SpinningText = CommonUtil.Tutor


                            }
                        }
                    } else if (CommonUtil.Priority.equals("p2")) {


                        if (position != 0) {
                            CommonUtil.DepartmentChooseIds.clear()
                            if (position == 1) {

                                recycle_Staffrecipients!!.visibility = View.VISIBLE
                                GetSubjecthod()
                                SenderType = ""
                                SpinningText = CommonUtil.Subjects

                            } else if (position == 2) {

                                Gettuterhod()
                                recycle_Staffrecipients!!.visibility = View.VISIBLE
                                SenderType = CommonUtil.Tutor
                                SpinningText = CommonUtil.Tutor

                            }
                        }

                    } else if (CommonUtil.Priority.equals("p1")) {


                        if (position != 0) {

                            if (position == 1) {

                                recycle_Staffrecipients!!.visibility = View.VISIBLE
                                CommonUtil.receivertype = "5"
                                GetSubjectprinciple()
                                SenderType = ""
                                SpinningText = CommonUtil.Subjects


                            } else if (position == 2) {

                                CommonUtil.receivertype = "5"
                                Gettuterprinciple()
                                SenderType = CommonUtil.Tutor
                                SpinningText = CommonUtil.Tutor

                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
    }

    //----------CONFORM AND CANCLE BUTTON ------------


    @SuppressLint("SuspiciousIndentation")
    @OnClick(R.id.btnConfirm)
    fun SendButtonAPi() {

        Log.d("checkbox_isStaff", isStaff.toString())
        Log.d("checkbox_isStudent", isStudent.toString())
        Log.d("checkbox_isParent", isParent.toString())
        val ReceiverCount = CommonUtil.DepartmentChooseIds.size.toString()
        if (CommonUtil.Priority.equals("p1")) {

            if (ScreenName.equals(CommonUtil.Text)) {

                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        SmsToParticularTypeRequest()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        SmsToEntireCollegesubjectandtuterRequest()

                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        SmsToParticularTypeRequest()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        SmsToEntireCollegesubjectandtuterRequest()

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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        VoiceSendParticuler()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        VoiceSendTuter()

                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        VoiceSendParticuler()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        VoiceSendTuter()

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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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
            } else if (ScreenName.equals(CommonUtil.Noticeboard)) {

                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                NoticeBoardSMSsending()

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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                NoticeBoardSMSsending()


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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                NoticeBoardSMSsending()

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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                NoticeBoardSMSsending()

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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        NoticeBoardSMSsending()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        NoticeBoardSMSsendingTuter()

                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        NoticeBoardSMSsending()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        NoticeBoardSMSsendingTuter()
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                NoticeBoardSMSsending()

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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {


                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {


                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        awsFileUpload(this, pathIndex)

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        awsFileUpload(this, pathIndex)

                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        awsFileUpload(this, pathIndex)

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        awsFileUpload(this, pathIndex)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                } else {

                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)

                }

            } else if (ScreenName!!.equals(CommonUtil.New_Assignment) || ScreenName.equals(
                    CommonUtil.Forward_Assignment
                )
            ) {

                if (!CommonUtil.receiverid.equals("")) {

                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@AddRecipients)
                    alertDialog.setTitle(CommonUtil.Hold_on)
                    alertDialog.setMessage(CommonUtil.Submit_Alart)
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
                    CommonUtil.ApiAlert(this, "Select the Receiver")
                }
            } else if (ScreenName.equals(CommonUtil.ScreenNameEvent)) {

                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        Eventsend("add")

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        EventsendTuter("add")

                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        Eventsend("add")

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        EventsendTuter("add")
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                //   VimeoVideoUpload(this, CommonUtil.videofile!!)
//                                val isVideoToken = SharedPreference.getVideo_Json(this)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {


                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

//                                VimeoVideoUpload(this, CommonUtil.videofile!!)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

//                                VimeoVideoUpload(this, CommonUtil.videofile!!)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

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

                                        //VimeoVideoUpload(this, CommonUtil.videofile!!)
                                        VimeoUploader.uploadVideo(
                                            this,
                                            CommonUtil.title,
                                            CommonUtil.Description,
                                            isVideoToken,
                                            CommonUtil.videofile!!,
                                            this
                                        )

                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        //  VimeoVideoUpload(this, CommonUtil.videofile!!)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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
            } else if (ScreenName.equals(CommonUtil.CommunicationVoice)) {

                if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                } else if (SelecteRecipientType.equals(CommonUtil.Division)) {


                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        SendVoiceToParticulerHistory()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        SendVoiceToParticulerHistory()

                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        SendVoiceToParticulerHistory()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        SendVoiceToParticulerHistory()
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

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Hold_on)
                            alertDialog.setMessage(CommonUtil.Submit_Alart)
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
            }

        } else if (CommonUtil.Priority.equals("p3")) {

            if (ScreenName.equals(CommonUtil.Text)) {
                if (!CommonUtil.receiverid.equals("")) {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@AddRecipients)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)

                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                            alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                        } else {
                            alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                        }

                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                CommonUtil.receivertype = "5"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    SmsToParticularTypeRequest()

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    SmsToEntireCollegesubjectandtuterRequest()

                                }

                            } else {

                                CommonUtil.receivertype = "7"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    SmsToParticularTypeRequest()

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    SmsToEntireCollegesubjectandtuterRequest()

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
            } else if (ScreenName.equals(CommonUtil.TextHistory)) {
                if (!CommonUtil.receiverid.equals("")) {
                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@AddRecipients)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)

                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                            alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                        } else {
                            alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                        }

                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                CommonUtil.receivertype = "5"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    SmsToParticularTypeRequest()

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    SmsToEntireCollegesubjectandtuterRequest()

                                }

                            } else {

                                CommonUtil.receivertype = "7"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    SmsToParticularTypeRequest()

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    SmsToEntireCollegesubjectandtuterRequest()

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
            } else if (ScreenName.equals(CommonUtil.Communication)) {
                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@AddRecipients)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                            alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                        } else {
                            alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                        }
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                CommonUtil.receivertype = "5"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    VoiceSendParticuler()

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    VoiceSendTuter()

                                }

                            } else {

                                CommonUtil.receivertype = "7"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    VoiceSendParticuler()

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    VoiceSendTuter()
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
            } else if (ScreenName.equals(CommonUtil.CommunicationVoice)) {
                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@AddRecipients)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                            alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                        } else {
                            alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                        }
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                CommonUtil.receivertype = "5"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    SendVoiceToParticulerHistory()

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    SendVoiceToParticulerHistory()

                                }

                            } else {

                                CommonUtil.receivertype = "7"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    SendVoiceToParticulerHistory()

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    SendVoiceToParticulerHistory()
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
            } else if (ScreenName.equals(CommonUtil.Noticeboard)) {

                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@AddRecipients)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                            alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                        } else {
                            alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                        }
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                CommonUtil.receivertype = "5"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    //  NoticeBoardSMSsending()
                                    awsFileUpload(this, pathIndex)
                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    //  NoticeBoardSMSsendingTuter()
                                    awsFileUpload(this, pathIndex)
                                }

                            } else {

                                CommonUtil.receivertype = "7"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    //  NoticeBoardSMSsending()
                                    awsFileUpload(this, pathIndex)
                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    //  NoticeBoardSMSsendingTuter()
                                    awsFileUpload(this, pathIndex)
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
            } else if (ScreenName.equals(CommonUtil.Image_Pdf)) {


                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@AddRecipients)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                            alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                        } else {
                            alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                        }
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                CommonUtil.receivertype = "5"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    awsFileUpload(this, pathIndex)

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    awsFileUpload(this, pathIndex)
                                }

                            } else {

                                CommonUtil.receivertype = "7"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    awsFileUpload(this, pathIndex)

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    awsFileUpload(this, pathIndex)
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
            } else if (ScreenName!!.equals(CommonUtil.New_Assignment) || ScreenName.equals(
                    CommonUtil.Forward_Assignment
                )
            ) {

                if (!CommonUtil.receiverid.equals("")) {

                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@AddRecipients)
                    alertDialog.setTitle(CommonUtil.Submit_Alart)
                    if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                        alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                    } else {
                        alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                    }
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
            } else if (ScreenName.equals(CommonUtil.ScreenNameEvent)) {


                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@AddRecipients)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                            alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                        } else {
                            alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                        }
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                CommonUtil.receivertype = "5"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    Eventsend("add")

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    EventsendTuter("add")

                                }

                            } else {

                                CommonUtil.receivertype = "7"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    Eventsend("add")

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                    EventsendTuter("add")
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
            } else if (ScreenName.equals(CommonUtil.Event_Edit)) {


                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@AddRecipients)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                            alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                        } else {
                            alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                        }
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                CommonUtil.receivertype = "5"

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
            } else if (ScreenName.equals(CommonUtil.New_Video)) {

                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        val alertDialog: AlertDialog.Builder =
                            AlertDialog.Builder(this@AddRecipients)
                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                            alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                        } else {
                            alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                        }
                        alertDialog.setPositiveButton(
                            CommonUtil.Yes
                        ) { _, _ ->

                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    //  VimeoVideoUpload(this, CommonUtil.videofile!!)
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

                            } else {

                                CommonUtil.receivertype = "7"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    // VimeoVideoUpload(this, CommonUtil.videofile!!)
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

        } else if (CommonUtil.Priority.equals("p2")) {
            if (My_Department.equals("Yes")) {
                if (ScreenName.equals(CommonUtil.Text)) {
                    if (!CommonUtil.receivertype.equals("")) {
                        if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {
                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                val alertDialog: AlertDialog.Builder =
                                    AlertDialog.Builder(this@AddRecipients)
                                alertDialog.setTitle(CommonUtil.Submit_Alart)
                                alertDialog.setMessage("This message is sending whole department")
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {


                            if (!CommonUtil.receiverid.equals("")) {

                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                    val alertDialog: AlertDialog.Builder =
                                        AlertDialog.Builder(this@AddRecipients)
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

                            if (txt_selectspecfic_YearandSecrion!!.visibility == View.GONE) {

                                CommonUtil.receivertype = "7"
                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
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

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
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
                            }
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (ScreenName.equals(CommonUtil.TextHistory)) {
                    if (!CommonUtil.receivertype.equals("")) {
                        if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {
                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                                val alertDialog: AlertDialog.Builder =
                                    AlertDialog.Builder(this@AddRecipients)
                                alertDialog.setTitle(CommonUtil.Submit_Alart)
                                alertDialog.setMessage("This message is sending whole department")
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {


                            if (!CommonUtil.receiverid.equals("")) {

                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                    val alertDialog: AlertDialog.Builder =
                                        AlertDialog.Builder(this@AddRecipients)
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

                            if (txt_selectspecfic_YearandSecrion!!.visibility == View.GONE) {

                                CommonUtil.receivertype = "7"
                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
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

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
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
                            }
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (ScreenName.equals(CommonUtil.Communication)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                val alertDialog: AlertDialog.Builder =
                                    AlertDialog.Builder(this@AddRecipients)
                                alertDialog.setTitle(CommonUtil.Submit_Alart)
                                alertDialog.setMessage("This message is sending whole department")
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {


                            if (!CommonUtil.receiverid.equals("")) {

                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                    val alertDialog: AlertDialog.Builder =
                                        AlertDialog.Builder(this@AddRecipients)
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

                            if (txt_selectspecfic_YearandSecrion!!.visibility == View.GONE) {

                                CommonUtil.receivertype = "7"
                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
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

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
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
                            }
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (ScreenName.equals(CommonUtil.CommunicationVoice)) {
                    if (!CommonUtil.receivertype.equals("")) {
                        if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                val alertDialog: AlertDialog.Builder =
                                    AlertDialog.Builder(this@AddRecipients)
                                alertDialog.setTitle(CommonUtil.Submit_Alart)
                                alertDialog.setMessage("This message is sending whole department")
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {


                            if (!CommonUtil.receiverid.equals("")) {

                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                    val alertDialog: AlertDialog.Builder =
                                        AlertDialog.Builder(this@AddRecipients)
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

                            if (txt_selectspecfic_YearandSecrion!!.visibility == View.GONE) {

                                CommonUtil.receivertype = "7"
                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
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

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
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
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (ScreenName.equals(CommonUtil.Noticeboard)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                                val alertDialog: AlertDialog.Builder =
                                    AlertDialog.Builder(this@AddRecipients)
                                alertDialog.setTitle(CommonUtil.Submit_Alart)
                                alertDialog.setMessage("This message is sending whole department")
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {


                            if (!CommonUtil.receiverid.equals("")) {

                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                    val alertDialog: AlertDialog.Builder =
                                        AlertDialog.Builder(this@AddRecipients)
                                    alertDialog.setTitle(CommonUtil.Submit_Alart)
                                    alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

                            if (txt_selectspecfic_YearandSecrion!!.visibility == View.GONE) {

                                CommonUtil.receivertype = "7"
                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
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


                            } else {

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
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
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (ScreenName.equals(CommonUtil.Image_Pdf)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                val alertDialog: AlertDialog.Builder =
                                    AlertDialog.Builder(this@AddRecipients)
                                alertDialog.setTitle(CommonUtil.Submit_Alart)
                                alertDialog.setMessage("This message is sending whole department")
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {


                            if (!CommonUtil.receiverid.equals("")) {

                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                    val alertDialog: AlertDialog.Builder =
                                        AlertDialog.Builder(this@AddRecipients)
                                    alertDialog.setTitle(CommonUtil.Submit_Alart)
                                    alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

                            if (txt_selectspecfic_YearandSecrion!!.visibility == View.GONE) {

                                CommonUtil.receivertype = "7"
                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
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


                            } else {

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
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
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (ScreenName.equals(CommonUtil.ScreenNameEvent)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                val alertDialog: AlertDialog.Builder =
                                    AlertDialog.Builder(this@AddRecipients)
                                alertDialog.setTitle(CommonUtil.Submit_Alart)
                                alertDialog.setMessage("This message is sending whole department")
                                alertDialog.setPositiveButton(
                                    CommonUtil.Yes
                                ) { _, _ ->

                                    EventsendHod("add")

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
                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {


                            if (!CommonUtil.receiverid.equals("")) {

                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                    val alertDialog: AlertDialog.Builder =
                                        AlertDialog.Builder(this@AddRecipients)
                                    alertDialog.setTitle(CommonUtil.Submit_Alart)
                                    alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
                                    alertDialog.setPositiveButton(
                                        CommonUtil.Yes
                                    ) { _, _ ->

                                        EventsendHod("add")


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
                        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

                            if (txt_selectspecfic_YearandSecrion!!.visibility == View.GONE) {

                                CommonUtil.receivertype = "7"
                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                                        alertDialog.setPositiveButton(
                                            CommonUtil.Yes
                                        ) { _, _ ->

                                            EventsendHod("add")

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

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                                        alertDialog.setPositiveButton(
                                            CommonUtil.Yes
                                        ) { _, _ ->

                                            EventsendHod("add")

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
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (ScreenName.equals(CommonUtil.Event_Edit)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                val alertDialog: AlertDialog.Builder =
                                    AlertDialog.Builder(this@AddRecipients)
                                alertDialog.setTitle(CommonUtil.Submit_Alart)
                                alertDialog.setMessage("This message is sending whole department")
                                alertDialog.setPositiveButton(
                                    CommonUtil.Yes
                                ) { _, _ ->

                                    EventsendHod("edit")

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
                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {


                            if (!CommonUtil.receiverid.equals("")) {

                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                    val alertDialog: AlertDialog.Builder =
                                        AlertDialog.Builder(this@AddRecipients)
                                    alertDialog.setTitle(CommonUtil.Submit_Alart)
                                    alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
                                    alertDialog.setPositiveButton(
                                        CommonUtil.Yes
                                    ) { _, _ ->

                                        EventsendHod("edit")


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
                        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

                            if (txt_selectspecfic_YearandSecrion!!.visibility == View.GONE) {

                                CommonUtil.receivertype = "7"
                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                                        alertDialog.setPositiveButton(
                                            CommonUtil.Yes
                                        ) { _, _ ->

                                            EventsendHod("edit")

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

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                                        alertDialog.setPositiveButton(
                                            CommonUtil.Yes
                                        ) { _, _ ->

                                            EventsendHod("edit")

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
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (ScreenName.equals(CommonUtil.New_Video)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                val alertDialog: AlertDialog.Builder =
                                    AlertDialog.Builder(this@AddRecipients)
                                alertDialog.setTitle(CommonUtil.Submit_Alart)
                                alertDialog.setMessage("This message is sending whole department")
                                alertDialog.setPositiveButton(
                                    CommonUtil.Yes
                                ) { _, _ ->

//                                    VimeoVideoUpload(this, CommonUtil.videofile!!)
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
                            if (!CommonUtil.receiverid.equals("")) {

                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                    val alertDialog: AlertDialog.Builder =
                                        AlertDialog.Builder(this@AddRecipients)
                                    alertDialog.setTitle(CommonUtil.Submit_Alart)
                                    alertDialog.setMessage(CommonUtil.selected_Course + ReceiverCount)
                                    alertDialog.setPositiveButton(
                                        CommonUtil.Yes
                                    ) { _, _ ->

//                                        VimeoVideoUpload(this, CommonUtil.videofile!!)
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
                        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

                            if (txt_selectspecfic_YearandSecrion!!.visibility == View.GONE) {

                                CommonUtil.receivertype = "7"
                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                                        alertDialog.setPositiveButton(
                                            CommonUtil.Yes
                                        ) { _, _ ->

//                                            VimeoVideoUpload(this, CommonUtil.videofile!!)
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

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        val alertDialog: AlertDialog.Builder =
                                            AlertDialog.Builder(this@AddRecipients)
                                        alertDialog.setTitle(CommonUtil.Submit_Alart)
                                        alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                                        alertDialog.setPositiveButton(
                                            CommonUtil.Yes
                                        ) { _, _ ->

//                                            VimeoVideoUpload(this, CommonUtil.videofile!!)
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
                            }
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                }

            } else if (My_Department.equals("No")) {
                if (ScreenName.equals(CommonUtil.Text)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)

                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                                alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                            } else {
                                alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                            }

                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    CommonUtil.receivertype = "5"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        SmsToParticularTypeRequest()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        SmsToEntireCollegesubjectandtuterRequest()

                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        SmsToParticularTypeRequest()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        SmsToEntireCollegesubjectandtuterRequest()

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
                } else if (ScreenName.equals(CommonUtil.TextHistory)) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)

                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                                alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                            } else {
                                alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                            }

                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    CommonUtil.receivertype = "5"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        SmsToParticularTypeRequest()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        SmsToEntireCollegesubjectandtuterRequest()

                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        SmsToParticularTypeRequest()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        SmsToEntireCollegesubjectandtuterRequest()

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
                } else if (ScreenName.equals(CommonUtil.Communication)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                                alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                            } else {
                                alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                            }
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    CommonUtil.receivertype = "5"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        VoiceSendParticuler()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        VoiceSendTuter()
                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        VoiceSendParticuler()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        VoiceSendTuter()

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
                } else if (ScreenName.equals(CommonUtil.CommunicationVoice)) {
                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                                alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                            } else {
                                alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                            }
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    CommonUtil.receivertype = "5"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        SendVoiceToParticulerHistory()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        SendVoiceToParticulerHistory()
                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        SendVoiceToParticulerHistory()

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        SendVoiceToParticulerHistory()

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
                } else if (ScreenName.equals(CommonUtil.Noticeboard)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                                alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                            } else {
                                alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                            }
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->
                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                                    CommonUtil.receivertype = "5"
                                    if (SpinningText.equals(CommonUtil.Subjects)) {
                                        awsFileUpload(this, pathIndex)
                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                        awsFileUpload(this, pathIndex)
                                    }
                                } else {
                                    CommonUtil.receivertype = "7"
                                    if (SpinningText.equals(CommonUtil.Subjects)) {
                                        awsFileUpload(this, pathIndex)
                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                        awsFileUpload(this, pathIndex)
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
                } else if (ScreenName.equals(CommonUtil.Image_Pdf)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                                alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                            } else {
                                alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                            }
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    CommonUtil.receivertype = "5"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        awsFileUpload(this, pathIndex)

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        awsFileUpload(this, pathIndex)
                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        awsFileUpload(this, pathIndex)

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        awsFileUpload(this, pathIndex)

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
                } else if (ScreenName.equals(CommonUtil.ScreenNameEvent)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                                alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                            } else {
                                alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                            }
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    CommonUtil.receivertype = "5"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        Eventsend("add")

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        EventsendTuter("add")
                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        Eventsend("add")

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        EventsendTuter("add")

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
                } else if (ScreenName.equals(CommonUtil.Event_Edit)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                                alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                            } else {
                                alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                            }
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    CommonUtil.receivertype = "5"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        Eventsend("edit")

                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {

                                        EventsendTuter("edit")
                                    }

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        EventsendTuter("edit")

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
                } else if (ScreenName.equals(CommonUtil.New_Video)) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            val alertDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@AddRecipients)
                            alertDialog.setTitle(CommonUtil.Submit_Alart)
                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                                alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                            } else {
                                alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                            }
                            alertDialog.setPositiveButton(
                                CommonUtil.Yes
                            ) { _, _ ->

                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                                    CommonUtil.receivertype = "5"

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

                                } else {

                                    CommonUtil.receivertype = "7"

                                    if (SpinningText.equals(CommonUtil.Subjects)) {

                                        //  VimeoVideoUpload(this, CommonUtil.videofile!!)
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
            } else if (ScreenName!!.equals(CommonUtil.New_Assignment) || ScreenName.equals(
                    CommonUtil.Forward_Assignment
                )
            ) {
                if (!CommonUtil.receiverid.equals("")) {

                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@AddRecipients)
                    alertDialog.setTitle(CommonUtil.Submit_Alart)
                    if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                        alertDialog.setMessage(CommonUtil.selected_Section + ReceiverCount)
                    } else {
                        alertDialog.setMessage(CommonUtil.StudentCount + ReceiverCount)
                    }
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
            }
        }
    }

    @OnClick(R.id.btnRecipientCancel)
    fun cancelClick() {

        CommonUtil.DepartmentChooseIds.clear()

        if (lbl_select_student!!.visibility == View.VISIBLE && CommonUtil.Priority == "p3") {

            if (ScreenName.equals(CommonUtil.New_Assignment)) {
                CommonUtil.seleteddataArrayCheckbox.clear()
                CommonUtil.receiverid = ""
                CommonUtil.SeletedStringdataReplace = ""
                if (!CommonUtil.seletedStringdata.equals("")) {
                    CommonUtil.receiverid = CommonUtil.seletedStringdata
                }
            }

            CommonUtil.DepartmentChooseIds.add(CommonUtil.SectionIdChoose)
            CommonUtil.receiverid = CommonUtil.DepartmentChooseIds[0]
            txt_selectsubortutor!!.visibility = View.VISIBLE
            Viewlinetwo!!.visibility = View.VISIBLE
            Spinning_yourclasses!!.visibility = View.VISIBLE

            recycle_Staffrecipients!!.visibility = View.VISIBLE
            txt_selectspecfic!!.visibility = View.VISIBLE

            lbl_select_student!!.visibility = View.GONE
            Viewlinefour!!.visibility = View.GONE
            recycle_specificstudent!!.visibility = View.GONE
            relative_Recycle!!.visibility = View.GONE
            ALL4!!.visibility = View.GONE
            ch_all4!!.visibility = View.GONE
            SearchView!!.visibility = View.GONE
            ch_all4!!.isChecked = false
            ALL4!!.setText(CommonUtil.Select_All)

        } else if (txt_selectsubortutor!!.visibility == View.VISIBLE && CommonUtil.Priority.equals(
                "p1"
            )
        ) {
            onBackPressed()

        } else if (lbl_select_student!!.visibility == View.VISIBLE && CommonUtil.Priority.equals(
                "p1"
            )
        ) {

            txt_selectsubortutor!!.visibility = View.VISIBLE
            Viewlinetwo!!.visibility = View.VISIBLE
            Spinning_yourclasses!!.visibility = View.VISIBLE
            recycle_Staffrecipients!!.visibility = View.VISIBLE
            txt_selectspecfic!!.visibility = View.VISIBLE
            lbl_select_student!!.visibility = View.GONE
            Viewlinefour!!.visibility = View.GONE
            recycle_specificstudent!!.visibility = View.GONE
            relative_Recycle!!.visibility = View.GONE
            ALL4!!.visibility = View.GONE
            ch_all4!!.visibility = View.GONE
            SearchView!!.visibility = View.GONE
            ch_all4!!.isChecked = false
            ALL4!!.setText(CommonUtil.Select_All)

            if (ScreenName.equals(CommonUtil.New_Assignment)) {

                CommonUtil.seleteddataArrayCheckbox.clear()
                CommonUtil.receiverid = ""
                CommonUtil.SeletedStringdataReplace = ""
                if (!CommonUtil.seletedStringdata.equals("")) {
                    CommonUtil.receiverid = CommonUtil.seletedStringdata
                }

            } else {

                for (i in CommonUtil.seleteddataArraySection.indices) {
                    CommonUtil.receiverid = CommonUtil.seleteddataArraySection[i]
                }
            }

            Log.d("reciverId", CommonUtil.receiverid)

        } else if (lbl_select_student!!.visibility == View.VISIBLE && CommonUtil.Priority.equals(
                "p2"
            )
        ) {

            if (Specific.equals("Student")) {

                CommonUtil.seleteddataArrayCheckbox.clear()
                CommonUtil.receiverid = ""
                lblEntireDepartmentlable!!.setText(CommonUtil.Entire_Department)
                lblDepartment!!.setText(CommonUtil.Year_Section)
                lblDivision!!.setText(CommonUtil.Course)
                txt_selectspecfic_YearandSecrion!!.visibility = View.GONE

                CommonUtil.receivertype = "5"

                NestedScrollView!!.visibility = View.VISIBLE

                recycleyearandsection!!.visibility = View.VISIBLE
                recycleRecipientscourse!!.visibility = View.GONE
                recycleRecipients!!.visibility = View.GONE
                spinnerDropdowncourse!!.visibility = View.VISIBLE
                spinnerDropdown!!.visibility = View.VISIBLE

                getcoursedepartment()
                lnrStaff!!.visibility = View.GONE

                lblEntireDepartmentlable!!.visibility = View.VISIBLE

                if (ScreenName!!.equals(CommonUtil.New_Assignment) || ScreenName.equals(
                        CommonUtil.Forward_Assignment
                    )
                ) {
                    LayoutRecipient!!.visibility = View.GONE
                } else {
                    LayoutRecipient!!.visibility = View.VISIBLE
                }
                Viewlineone!!.visibility = View.VISIBLE
                lblDivision!!.visibility = View.VISIBLE
                lblDepartment!!.visibility = View.VISIBLE

                lblYourClasses!!.visibility = View.GONE
                lblGroups!!.visibility = View.GONE
                lblCourse!!.visibility = View.GONE

                txt_department!!.visibility = View.GONE
                Viewlinethree!!.visibility = View.GONE
                txt_mydepartment!!.visibility = View.GONE
                txt_myclass!!.visibility = View.GONE

                txt_selectsubortutor!!.visibility = View.GONE
                Viewlinetwo!!.visibility = View.GONE
                Spinning_yourclasses!!.visibility = View.GONE

                recycle_Staffrecipients!!.visibility = View.GONE
                txt_selectspecfic!!.visibility = View.GONE

                lbl_select_student!!.visibility = View.GONE
                Viewlinefour!!.visibility = View.GONE
                recycle_specificstudent!!.visibility = View.GONE
                relative_Recycle!!.visibility = View.GONE
                ALL4!!.visibility = View.GONE
                ch_all4!!.visibility = View.GONE
                SearchView!!.visibility = View.GONE
                ch_all4!!.isChecked = false
                ALL4!!.setText(CommonUtil.Select_All)
                Specific = ""

            } else {

                if (ScreenName.equals(CommonUtil.New_Assignment)) {

                    CommonUtil.seleteddataArrayCheckbox.clear()
                    CommonUtil.receiverid = ""
                    CommonUtil.SeletedStringdataReplace = ""
                    if (!CommonUtil.seletedStringdata.equals("")) {
                        CommonUtil.receiverid = CommonUtil.seletedStringdata
                    }

                } else {

                    for (i in CommonUtil.seleteddataArraySection.indices) {
                        CommonUtil.receiverid = CommonUtil.seleteddataArraySection[i]
                    }
                }

                CommonUtil.DepartmentChooseIds.add(CommonUtil.SectionIdChoose)
                CommonUtil.receiverid = CommonUtil.DepartmentChooseIds[0].toString()
                txt_selectsubortutor!!.visibility = View.VISIBLE
                Viewlinetwo!!.visibility = View.VISIBLE
                Spinning_yourclasses!!.visibility = View.VISIBLE

                recycle_Staffrecipients!!.visibility = View.VISIBLE
                txt_selectspecfic!!.visibility = View.VISIBLE

                lbl_select_student!!.visibility = View.GONE
                Viewlinefour!!.visibility = View.GONE
                recycle_specificstudent!!.visibility = View.GONE
                relative_Recycle!!.visibility = View.GONE
                ALL4!!.visibility = View.GONE
                ch_all4!!.visibility = View.GONE
                SearchView!!.visibility = View.GONE
                ch_all4!!.isChecked = false
                ALL4!!.setText(CommonUtil.Select_All)

            }

        } else if (txt_selectsubortutor!!.getVisibility() == View.VISIBLE && CommonUtil.Priority.equals(
                "p2"
            )
        ) {

            if (ScreenName!!.equals(CommonUtil.New_Assignment) && txt_selectspecfic!!.getVisibility() == View.VISIBLE) {
                onBackPressed()
            } else if (ScreenName.equals(CommonUtil.Forward_Assignment)) {
                onBackPressed()
                txt_mydepartment!!.visibility = View.GONE
            } else {

                txt_department!!.visibility = View.VISIBLE
                Viewlinethree!!.visibility = View.VISIBLE
                txt_myclass!!.visibility = View.VISIBLE
                txt_mydepartment!!.visibility = View.VISIBLE
                txt_onandoff!!.visibility = View.GONE
            }
            lblEntireDepartmentlable!!.visibility = View.GONE
            LayoutRecipient!!.visibility = View.GONE
            Viewlineone!!.visibility = View.GONE
            NestedScrollView!!.visibility = View.GONE
            txt_selectsubortutor!!.visibility = View.GONE
            Viewlinetwo!!.visibility = View.GONE
            Spinning_yourclasses!!.visibility = View.GONE
            recycle_Staffrecipients!!.visibility = View.GONE
            txt_selectspecfic!!.visibility = View.GONE
            lbl_select_student!!.visibility = View.GONE
            Viewlinefour!!.visibility = View.GONE
            recycle_specificstudent!!.visibility = View.GONE
            relative_Recycle!!.visibility = View.GONE
            ALL4!!.visibility = View.GONE
            ch_all4!!.visibility = View.GONE
            SearchView!!.visibility = View.GONE
            ch_all4!!.isChecked = false
            ALL4!!.setText(CommonUtil.Select_All)
            onBackPressed()

        } else if (CommonUtil.Priority.equals("p2") && txt_department!!.visibility == View.GONE) {
            if (ScreenName!!.equals(CommonUtil.New_Assignment) && txt_selectspecfic!!.getVisibility() == View.VISIBLE) {

                onBackPressed()
            } else if (ScreenName.equals(CommonUtil.Forward_Assignment)) {
                txt_mydepartment!!.visibility = View.GONE
                onBackPressed()
            } else {
                txt_department!!.visibility = View.VISIBLE
                Viewlinethree!!.visibility = View.VISIBLE
                txt_myclass!!.visibility = View.VISIBLE
                txt_mydepartment!!.visibility = View.VISIBLE
                txt_onandoff!!.visibility = View.GONE
            }

            lblEntireDepartmentlable!!.visibility = View.GONE
            LayoutRecipient!!.visibility = View.GONE
            Viewlineone!!.visibility = View.GONE
            NestedScrollView!!.visibility = View.GONE
            txt_selectsubortutor!!.visibility = View.GONE
            Viewlinetwo!!.visibility = View.GONE
            Spinning_yourclasses!!.visibility = View.GONE
            recycle_Staffrecipients!!.visibility = View.GONE
            txt_selectspecfic!!.visibility = View.GONE
            lbl_select_student!!.visibility = View.GONE
            Viewlinefour!!.visibility = View.GONE
            recycle_specificstudent!!.visibility = View.GONE
            relative_Recycle!!.visibility = View.GONE
            ALL4!!.visibility = View.GONE
            ch_all4!!.visibility = View.GONE
            SearchView!!.visibility = View.GONE
            ch_all4!!.isChecked = false
            ALL4!!.setText(CommonUtil.Select_All)

        } else {
            onBackPressed()
        }
    }

//----------------------API REQUESTS-------------------------//


    private fun GetDivisionRequest() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        appViewModel!!.getDivision(jsonObject, this)
        Log.d("GetDivisionRequest", jsonObject.toString())
    }

    private fun GetSubjectstaff() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        appViewModel!!.getsubject(jsonObject, this)
        Log.d("GetstaffRequest", jsonObject.toString())
    }

    private fun GetSubjecthod() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        appViewModel!!.getsubject(jsonObject, this)
        Log.d("GetstaffRequest", jsonObject.toString())
    }

    private fun GetSubjectprinciple() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        appViewModel!!.getsubject(jsonObject, this)
        Log.d("GetstaffRequest", jsonObject.toString())
    }

    private fun Gettuter() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        appViewModel!!.gettuter(jsonObject, this)
        Log.d("GettuterRequest", jsonObject.toString())
    }

    private fun Gettuterhod() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        appViewModel!!.gettuter(jsonObject, this)
        Log.d("GettuterRequest", jsonObject.toString())
    }

    private fun Gettuterprinciple() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        appViewModel!!.gettuter(jsonObject, this)
        Log.d("GettuterRequest", jsonObject.toString())
    }

    private fun getcoursedepartment() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_dept_id, CommonUtil.DepartmentId)
        appViewModel!!.getcoursedepartment(jsonObject, this)
        Log.d("getdepartcousereq", jsonObject.toString())
    }

    private fun getspecificstudentdata() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_courseid, CommonUtil.Courseid)
        jsonObject.addProperty(ApiRequestNames.Req_dept_id, CommonUtil.deptid)
        jsonObject.addProperty(ApiRequestNames.Req_yearid, CommonUtil.YearId)
        jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.receiverid)
        if (yearSection) {
            jsonObject.addProperty("subjectid", CommonUtil.SubjectID)
        }

        appViewModel!!.getspecificstudentdata(jsonObject, this)
        Log.d("GettuterspecificRequest", jsonObject.toString())
    }

    private fun getspecificstudentdatasubject() {

        val jsonObject = JsonObject()
        jsonObject.addProperty("staffid", CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_yearid, CommonUtil.YearId)
        jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.receiverid)

        appViewModel!!.getspecificstudentdatasubject(jsonObject, this)
        Log.d("Getspecificsubject", jsonObject.toString())
    }

    private fun GetDepartmentRequest() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_div_id, SelectedSpinnerID)
        appViewModel!!.getDepartment(jsonObject, this)
        Log.d("GetDepartmentRequest", jsonObject.toString())
    }

    private fun GetCourseRequesr() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_depart_id, SelectedSpinnerID)
        appViewModel!!.getCourseDepartment(jsonObject, this)
        Log.d("GetCourseRequeat", jsonObject.toString())
    }

    private fun GetyearandsectionRequest() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_clgprocessby, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_idcollege, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_idcourse, SelectedSpinnerIDhod)
        appViewModel!!.getyearsndsectionList(jsonObject, this)
        Log.d("Gety&sectionRequeat", jsonObject.toString())
    }

    private fun GetGroup() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_idcollege, CommonUtil.CollegeId)
        Log.d("GetGroupRequeat", jsonObject.toString())
        appViewModel!!.getGroup(jsonObject, this)
    }

    private fun SmsToEntireCollegeRequest() {

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

        appViewModel!!.SendSmsToEntireCollege(jsonObject, this)
        Log.d("SMSJsonObjectEntire", jsonObject.toString())
    }


    private fun VoiceToEntireCollegeRequest() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_filetype, "1")
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty("fileduration", "20")
        jsonObject.addProperty("isemergencyvoice", "0")
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.MenuDescription)
        appViewModel!!.SendVoiceToEntireCollege(jsonObject, this)
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
        jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)

        appViewModel!!.SendVoiceToParticulerHistory(jsonObject, this)
        Log.d("VoiceToEntireHistory", jsonObject.toString())
    }

    private fun SmsToEntireCollegesubjectandtuterRequest() {

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
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)
        jsonObject.addProperty(ApiRequestNames.Req_receviedit, CommonUtil.receiverid)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.MenuDescription)

        appViewModel!!.SendSmsToEntiretutorandsubjectCollege(jsonObject, this)
        Log.d("SMSJsonObjectEntire", jsonObject.toString())
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

        jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)


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

        jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)


        appViewModel!!.NoticeBoardsmssending(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }


    private fun NoticeBoardSMSsendingHOD() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_noticeboardid, "0")
        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, "3")
        jsonObject.addProperty(ApiRequestNames.Req_receiveridlist, CommonUtil.DepartmentId)
        jsonObject.addProperty(ApiRequestNames.Req_topic, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.MenuDescription)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_processtype, "add")
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)


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
        jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)


        appViewModel!!.Eventsend(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }

    private fun EventsendHod(prossertype: String) {
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
        jsonObject.addProperty(ApiRequestNames.Req_receiveridlist, CommonUtil.DepartmentId)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, CommonUtil.receivertype)

        appViewModel!!.Eventsend(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
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
                ApiRequestNames.Req_FileName, AWSUploadedFilesList[i].filepath
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

        jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)


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
            FileNameobject.addProperty("FileName", AWSUploadedFilesList[i].filepath)
            FileNameArray.add(FileNameobject)
        }
        jsonObject.add("FileNameArray", FileNameArray)

        appViewModel!!.Tuterimageorpdfsend(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
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


        if (CommonUtil.AssignmentType.equals("text")) {

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

        if (CommonUtil.AssignmentType.equals("text")) {

            appViewModel!!.AssignmentsendForwardText(jsonObject, this)
            Log.d("SMSJsonObject", jsonObject.toString())

        } else {

            appViewModel!!.Assignmentsend(jsonObject, this)
            Log.d("SMSJsonObject", jsonObject.toString())

        }


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
        jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)

        appViewModel!!.VideoParticulerSend(jsonObject, this)
        Log.d("SMSJsonObjectparticuler", jsonObject.toString())

    }


    private fun VideosendEntireTuter() {

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
        Log.d("Videoiframe", CommonUtil.VimeoIframe.toString())
        jsonObject.addProperty("url", CommonUtil.VimeoVideoUrl)


        appViewModel!!.VideoSendtuter(jsonObject, this)
        Log.d("SMSJsonObjectEntire", jsonObject.toString())
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

    private fun Attendancemarking() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, recivertype)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty(ApiRequestNames.Req_fileduraction, "0")
        jsonObject.addProperty(ApiRequestNames.Req_topic, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.MenuDescription)
        jsonObject.addProperty(ApiRequestNames.Req_FileNameArray, CommonUtil.receiverid)
        jsonObject.addProperty(ApiRequestNames.Req_processtype, "add")

        appViewModel!!.AttendanceTakedata(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }

//--------CLICK THE PARTICULER DATA


    @OnClick(R.id.lblEntireDepartmentlable)
    fun EntireClick() {

        //    chboxStudent!!.isChecked = false
        chboxParents!!.isChecked = false
        chboxStaff!!.isChecked = false
        chboxAll!!.isChecked = false

        isStaff = false
        isParent = false
        //   isStudent = false

        txt_selectspecfic_YearandSecrion!!.visibility = View.GONE
        Clickable = CommonUtil.Clickable_Entire
        lnrStaff!!.visibility = View.VISIBLE
        Division_All!!.visibility = View.GONE
        CommonUtil.DepartmentChooseIds.clear()
        ch_all!!.visibility = View.GONE
        ALL2!!.visibility = View.GONE
        ch_all1!!.visibility = View.GONE
        ALL3!!.visibility = View.GONE
        ch_all2!!.visibility = View.GONE

        CommonUtil.receiverid = CommonUtil.CollegeId.toString()
        recycleRecipients!!.visibility = View.GONE

        spinnerDropdowncourse!!.visibility = View.GONE
        spinnerDropdown!!.visibility = View.GONE
        recycleRecipientscourse!!.visibility = View.GONE
        recycleyearandsection!!.visibility = View.GONE
        lblEntireDepartmentlable!!.setBackgroundResource(R.drawable.bg_available_selected_green)
        lblEntireDepartmentlable!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_white)))


        lblDivision!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblDivision!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblDepartment!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblDepartment!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblCourse!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblCourse!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblYourClasses!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblYourClasses!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblGroups!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblGroups!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        SelecteRecipientType = lblEntireDepartmentlable!!.text.toString()

        if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {

            CommonUtil.receivertype = "3"
            CommonUtil.receiverid = CommonUtil.DepartmentId

        } else if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {

            CommonUtil.receivertype = "1"

        }
    }


    @OnClick(R.id.lblDivision)
    fun divisionClick() {


        if (lblDivision!!.text.toString().equals(CommonUtil.Course)) {
            Card_name = CommonUtil.Course
        } else {
            Card_name = ""
        }

        //    chboxStudent!!.isChecked = false
        chboxParents!!.isChecked = false
        chboxStaff!!.isChecked = false
        chboxAll!!.isChecked = false
        isStaff = false
        isParent = false
        //   isStudent = false
        CommonUtil.DepartmentChooseIds.clear()

        txt_selectspecfic_YearandSecrion!!.visibility = View.GONE

        Clickable = CommonUtil.Clickable
        CommonUtil.receiverid = ""
        ch_all!!.isChecked = false
        ch_all1!!.isChecked = false
        ch_all2!!.isChecked = false
        Division_All!!.setText(CommonUtil.Select_All)

        CommonUtil.seleteddataArrayCheckbox.clear()
        Division_All!!.visibility = View.VISIBLE
        ch_all!!.visibility = View.VISIBLE
        ALL2!!.visibility = View.GONE
        ch_all1!!.visibility = View.GONE
        ALL3!!.visibility = View.GONE
        ch_all2!!.visibility = View.GONE

        if (CommonUtil.Priority.equals("p1")) {

            lnrStaff!!.visibility = View.VISIBLE
            CommonUtil.receivertype = "8"
            spinnerDropdowncourse!!.visibility = View.GONE
            spinnerDropdown!!.visibility = View.GONE
            recycleRecipientscourse!!.visibility = View.GONE
            GetDivisionRequest()

        } else if (CommonUtil.Priority.equals("p2")) {


            recycleyearandsection!!.visibility = View.GONE
            NestedScrollView!!.visibility = View.VISIBLE
            spinnerDropdowncourse!!.visibility = View.GONE
            spinnerDropdown!!.visibility = View.GONE
            recycleRecipients!!.visibility = View.VISIBLE
            recycleRecipientscourse!!.visibility = View.VISIBLE
            getcoursedepartment()
            lnrStaff!!.visibility = View.GONE
        }


        lblEntireDepartmentlable!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblEntireDepartmentlable!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblDivision!!.setBackgroundResource(R.drawable.bg_available_selected_green)
        lblDivision!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_white)))

        lblDepartment!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblDepartment!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblCourse!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblCourse!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblYourClasses!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblYourClasses!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblGroups!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblGroups!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        SelecteRecipientType = lblDivision!!.text.toString()



        if (SelecteRecipientType.equals(CommonUtil.Division)) {

            CommonUtil.receivertype = "8"

        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {

            CommonUtil.receivertype = "2"

        }
    }

    @OnClick(R.id.lblDepartment)
    fun departmentClick() {

        if (lblDepartment!!.text.toString().equals(CommonUtil.Year_Section)) {
            Card_name = CommonUtil.Year_Section
        } else {
            Card_name = ""
        }
        CommonUtil.DepartmentChooseIds.clear()

        //    chboxStudent!!.isChecked = false
        chboxParents!!.isChecked = false
        chboxStaff!!.isChecked = false
        chboxAll!!.isChecked = false

        isStaff = false
        isParent = false
        //  isStudent = false

        Clickable = CommonUtil.Clickable
        CommonUtil.receiverid = ""
        ch_all!!.isChecked = false
        ch_all1!!.isChecked = false
        ch_all2!!.isChecked = false
        ALL2!!.setText(CommonUtil.Select_All)
        txt_selectspecfic_YearandSecrion!!.visibility = View.GONE

        CommonUtil.seleteddataArrayCheckbox.clear()
        Division_All!!.visibility = View.GONE
        ch_all!!.visibility = View.GONE
        ALL3!!.visibility = View.GONE
        ch_all2!!.visibility = View.GONE

        if (CommonUtil.Priority.equals("p1")) {
            lnrStaff!!.visibility = View.VISIBLE

            recycleRecipients!!.visibility = View.VISIBLE
            recycleRecipientscourse!!.visibility = View.GONE
            spinnerDropdowncourse!!.visibility = View.GONE
            spinnerDropdown!!.visibility = View.VISIBLE
            GetDivisionRequest()

        } else if (CommonUtil.Priority.equals("p2")) {

            NestedScrollView!!.visibility = View.VISIBLE

            recycleyearandsection!!.visibility = View.VISIBLE
            recycleRecipientscourse!!.visibility = View.GONE
            recycleRecipients!!.visibility = View.GONE
            spinnerDropdowncourse!!.visibility = View.VISIBLE
            spinnerDropdown!!.visibility = View.VISIBLE

            getcoursedepartment()
            lnrStaff!!.visibility = View.GONE

        }


        lblEntireDepartmentlable!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblEntireDepartmentlable!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblDivision!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblDivision!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblDepartment!!.setBackgroundResource(R.drawable.bg_available_selected_green)
        lblDepartment!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_white)))

        lblCourse!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblCourse!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblYourClasses!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblYourClasses!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblGroups!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblGroups!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        SelecteRecipientType = lblDepartment!!.text.toString()
        Log.d("SeleteRecipienttype", SelecteRecipientType.toString())


        if (SelecteRecipientType.equals(CommonUtil.Department_)) {

            CommonUtil.receivertype = "3"

        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

            CommonUtil.receivertype = "5"

        }
    }

    @OnClick(R.id.lblCourse)
    fun CourseClick() {

        if (lblCourse!!.text.toString().equals(CommonUtil.Course)) {
            Card_name = CommonUtil.Course
        } else {
            Card_name = ""
        }
        CommonUtil.DepartmentChooseIds.clear()

        //     chboxStudent!!.isChecked = false
        chboxParents!!.isChecked = false
        chboxStaff!!.isChecked = false
        chboxAll!!.isChecked = false
        isStaff = false
        isParent = false
        //  isStudent = false

        Clickable = CommonUtil.Clickable
        CommonUtil.receiverid = ""
        ch_all!!.isChecked = false
        ch_all1!!.isChecked = false
        ch_all2!!.isChecked = false
        ALL3!!.text = CommonUtil.Select_All
        CommonUtil.seleteddataArrayCheckbox.clear()
        Division_All!!.visibility = View.GONE
        ch_all!!.visibility = View.GONE
        ALL2!!.visibility = View.GONE
        ch_all1!!.visibility = View.GONE
        ALL3!!.visibility = View.GONE
        ch_all2!!.visibility = View.GONE
        lnrStaff!!.visibility = View.GONE
        recycleRecipients!!.visibility = View.GONE
        spinnerDropdown!!.visibility = View.VISIBLE
        spinnerDropdowncourse!!.visibility = View.VISIBLE
        recycleRecipientscourse!!.visibility = View.VISIBLE
        GetDivisionRequest()

        txt_selectspecfic_YearandSecrion!!.visibility = View.GONE

        lblEntireDepartmentlable!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblEntireDepartmentlable!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblDivision!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblDivision!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblDepartment!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblDepartment!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblCourse!!.setBackgroundResource(R.drawable.bg_available_selected_green)
        lblCourse!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_white)))

        lblYourClasses!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblYourClasses!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblGroups!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblGroups!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        SelecteRecipientType = lblCourse!!.text.toString()
        Log.d("SeleteRecipienttype", SelecteRecipientType.toString())

        if (SelecteRecipientType.equals(CommonUtil.Course)) {
            CommonUtil.receivertype = "2"
        }
    }

    @OnClick(R.id.lblYourClasses)
    fun YourClassesClick() {

        if (lblYourClasses!!.text.toString().equals(CommonUtil.Your_Classes)) {
            Card_name = CommonUtil.Your_Classes
        } else {
            Card_name = ""
        }

        CommonUtil.DepartmentChooseIds.clear()

        //    chboxStudent!!.isChecked = false
        chboxParents!!.isChecked = false
        chboxStaff!!.isChecked = false
        chboxAll!!.isChecked = false
        isStaff = false
        isParent = false
        //  isStudent = false

        isStaff = false
        Yourclasses()
        Clickable = CommonUtil.Clickable
        CommonUtil.receiverid = ""
        ch_all!!.isChecked = false
        ch_all1!!.isChecked = false
        ch_all2!!.isChecked = false
        CommonUtil.seleteddataArrayCheckbox.clear()
        Division_All!!.visibility = View.GONE
        ch_all!!.visibility = View.GONE
        ALL2!!.visibility = View.GONE
        ch_all1!!.visibility = View.GONE
        ALL3!!.visibility = View.GONE
        ch_all2!!.visibility = View.GONE
        CommonUtil.receivertype = "5"
        lnrStaff!!.visibility = View.GONE
        recycleRecipients!!.visibility = View.GONE
        spinnerDropdown!!.visibility = View.GONE
        spinnerDropdowncourse!!.visibility = View.GONE
        recycleRecipientscourse!!.visibility = View.GONE
        SelecteRecipientType = lblYourClasses!!.text.toString()
        lblEntireDepartmentlable!!.visibility = View.GONE
        LayoutRecipient!!.visibility = View.GONE
        Viewlineone!!.visibility = View.GONE
        NestedScrollView!!.visibility = View.GONE
        txt_selectsubortutor!!.visibility = View.VISIBLE
        Viewlinetwo!!.visibility = View.VISIBLE
        Spinning_yourclasses!!.visibility = View.VISIBLE
        recycle_Staffrecipients!!.visibility = View.VISIBLE
        txt_selectspecfic!!.visibility = View.VISIBLE
        txt_department!!.visibility = View.GONE
        Viewlinethree!!.visibility = View.GONE
        txt_mydepartment!!.visibility = View.GONE
        txt_myclass!!.visibility = View.GONE

        txt_selectspecfic_YearandSecrion!!.visibility = View.GONE

        lblEntireDepartmentlable!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblEntireDepartmentlable!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))


        lblDivision!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblDivision!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblDepartment!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblDepartment!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblCourse!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblCourse!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblYourClasses!!.setBackgroundResource(R.drawable.bg_available_selected_green)
        lblYourClasses!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_white)))

        lblGroups!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblGroups!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

    }

    @OnClick(R.id.lblGroups)
    fun GroupsClick() {
        if (lblGroups!!.text.toString().equals(CommonUtil.Groups)) {
            Card_name = CommonUtil.Groups
        } else {
            Card_name = ""
        }

        CommonUtil.DepartmentChooseIds.clear()

        //  chboxStudent!!.isChecked = false
        chboxParents!!.isChecked = false
        chboxStaff!!.isChecked = false
        chboxAll!!.isChecked = false

        isStaff = false
        isParent = false
        //    isStudent = false

        Clickable = CommonUtil.Clickable
        CommonUtil.receiverid = ""
        ch_all!!.isChecked = false
        ch_all1!!.isChecked = false
        ch_all2!!.isChecked = false
        Division_All!!.text = CommonUtil.Select_All

        CommonUtil.seleteddataArrayCheckbox.clear()
        Division_All!!.visibility = View.VISIBLE
        ch_all!!.visibility = View.VISIBLE
        ALL2!!.visibility = View.GONE
        ch_all1!!.visibility = View.GONE
        ALL3!!.visibility = View.GONE
        ch_all2!!.visibility = View.GONE
        txt_selectspecfic_YearandSecrion!!.visibility = View.GONE


        lnrStaff!!.visibility = View.GONE
        isStaff = false
        GetGroup()
        CommonUtil.receivertype = "6"
        recycleRecipients!!.visibility = View.VISIBLE
        spinnerDropdown!!.visibility = View.GONE
        spinnerDropdowncourse!!.visibility = View.GONE
        recycleRecipientscourse!!.visibility = View.GONE
        SelecteRecipientType = lblGroups!!.text.toString()



        lblEntireDepartmentlable!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblEntireDepartmentlable!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))


        lblDivision!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblDivision!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblDepartment!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblDepartment!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblCourse!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblCourse!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblYourClasses!!.setBackgroundResource(R.drawable.bg_available_outline_red)
        lblYourClasses!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_red)))

        lblGroups!!.setBackgroundResource(R.drawable.bg_available_selected_green)
        lblGroups!!.setTextColor(Color.parseColor(getString(R.string.lbl_clr_white)))

    }


// ----------------AWS UPLOAD FUNCTION--------------//

    fun awsFileUpload(activity: Activity?, pathind: Int?) {

        Log.d("SelcetedFileList", CommonUtil.SelcetedFileList.size.toString())
        val s3uploaderObj: S3Uploader
        s3uploaderObj = S3Uploader(activity)
        pathIndex = pathind!!

        for (index in pathIndex until CommonUtil.SelcetedFileList.size) {
            uploadFilePath = CommonUtil.SelcetedFileList.get(index)
            Log.d("uploadFilePath", uploadFilePath.toString())
            var extension = uploadFilePath!!.substring(uploadFilePath!!.lastIndexOf("."))
            if (extension.equals(".pdf")) {
                contentType = ".pdf"
            } else {
                contentType = ".jpg"
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
                                this@AddRecipients,
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

            if (ScreenName.equals(CommonUtil.Image_Pdf)) {

                if (CommonUtil.Priority.equals("p1")) {

                    if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {

                        if (!CommonUtil.receivertype.equals("")) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                ImageOrPdfsendentire()

                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                            }
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                        }

                    } else if (SelecteRecipientType.equals(CommonUtil.Division)) {


                        if (!CommonUtil.receiverid.equals("")) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                ImageOrPdfsendparticuler()

                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                            }
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                        }

                    } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {


                        if (!CommonUtil.receiverid.equals("")) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {


                                ImageOrPdfsendparticuler()

                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                            }
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                        }

                    } else if (SelecteRecipientType.equals(CommonUtil.Course)) {

                        if (!CommonUtil.receiverid.equals("")) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                ImageOrPdfsendparticuler()

                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                            }

                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                        }

                    } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {

                        if (!CommonUtil.receiverid.equals("")) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {


                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

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


                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                            }
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                        }

                    } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {

                        if (!CommonUtil.receiverid.equals("")) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                ImageOrPdfsendparticuler()

                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                            }
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                        }

                    } else {

                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)

                    }
                } else if (CommonUtil.Priority.equals("p3")) {

                    if (!CommonUtil.receiverid.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {


                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {

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


                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (CommonUtil.Priority.equals("p2")) {

                    if (My_Department.equals("Yes")) {

                        if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {

                            if (!CommonUtil.receivertype.equals("")) {

                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                    ImageOrPdfsendparticuler()

                                } else {
                                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                                }

                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                            }
                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {

                            if (!CommonUtil.receivertype.equals("")) {

                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                    ImageOrPdfsendparticuler()

                                } else {
                                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                                }

                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                            }

                        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

                            if (!CommonUtil.receivertype.equals("")) {

                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                    ImageOrPdfsendparticuler()

                                } else {
                                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                                }
                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                            }
                        }

                    } else {

                        if (!CommonUtil.receiverid.equals("")) {

                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {


                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

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

                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                            }
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                        }

                    }

                }

            } else if (ScreenName.equals(CommonUtil.Noticeboard)) {

                if (CommonUtil.Priority.equals("p3")) {
                    if (!CommonUtil.receiverid.equals("")) {
                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                            if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    NoticeBoardSMSsending()

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                    NoticeBoardSMSsendingTuter()

                                }

                            } else {

                                CommonUtil.receivertype = "7"

                                if (SpinningText.equals(CommonUtil.Subjects)) {

                                    NoticeBoardSMSsending()

                                } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                    NoticeBoardSMSsendingTuter()
                                }
                            }
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                        }
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                    }
                } else if (CommonUtil.Priority.equals("p2")) {
                    if (My_Department.equals("Yes")) {
                        if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {
                            if (!CommonUtil.receivertype.equals("")) {
                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                                    NoticeBoardSMSsending()
                                } else {
                                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                                }
                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                            }
                        } else if (SelecteRecipientType.equals(CommonUtil.Course)) {
                            if (!CommonUtil.receivertype.equals("")) {
                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                                    NoticeBoardSMSsending()
                                } else {
                                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                                }
                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                            }
                        } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {
                            if (!CommonUtil.receivertype.equals("")) {
                                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                                    NoticeBoardSMSsending()
                                } else {
                                    CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                                }
                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                            }
                        }
                    } else {
                        if (!CommonUtil.receiverid.equals("")) {
                            if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                                if (txt_selectspecfic!!.visibility == View.VISIBLE) {
                                    if (SpinningText.equals(CommonUtil.Subjects)) {
                                        NoticeBoardSMSsending()
                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                        NoticeBoardSMSsending()
                                    }
                                } else {
                                    CommonUtil.receivertype = "7"
                                    if (SpinningText.equals(CommonUtil.Subjects)) {
                                        NoticeBoardSMSsending()
                                    } else if (SpinningText.equals(CommonUtil.Tutor)) {
                                        NoticeBoardSMSsending()
                                    }
                                }
                            } else {
                                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Target)
                            }
                        } else {
                            CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
                        }
                    }
                }

            } else if (ScreenName!!.equals(CommonUtil.New_Assignment)) {


                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                    CommonUtil.receivertype = "5"
                    AssignmentsendEntireSection()

                } else {

                    CommonUtil.receivertype = "7"
                    AssignmentsendEntireSection()

                }
            } else if (ScreenName.equals(CommonUtil.Forward_Assignment)) {


                if (txt_selectspecfic!!.visibility == View.VISIBLE) {

                    CommonUtil.receivertype = "5"
                    Assignmentforward()
                } else {

                    CommonUtil.receivertype = "7"
                    Assignmentforward()
                }
            }
        }
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
        val bodyFile: MultipartBody.Part = createFormData("voice", file.name, requestFile)
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

                                    val dlg = this@AddRecipients.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent =

                                                Intent(
                                                    this@AddRecipients, Communication::class.java
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

                                    val dlg = this@AddRecipients.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent =

                                                Intent(
                                                    this@AddRecipients, Communication::class.java
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

        jsonObject.addProperty("subjectid", CommonUtil.isSubjectIds)


        Log.d("VoiceSend:req", jsonObject.toString())

        val file: File = File(CommonUtil.futureStudioIconFile!!.path)
        Log.d("FILE_Path", CommonUtil.futureStudioIconFile!!.path)

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val bodyFile: MultipartBody.Part = createFormData("voice", file.name, requestFile)
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

                                    val dlg = this@AddRecipients.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent =

                                                Intent(
                                                    this@AddRecipients, Communication::class.java
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

                                    val dlg = this@AddRecipients.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent = Intent(
                                                this@AddRecipients, Communication::class.java
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

        val file: File = File(CommonUtil.futureStudioIconFile!!.path)
        Log.d("FILE_Path", CommonUtil.futureStudioIconFile!!.path)

        val requestFile: RequestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val bodyFile: MultipartBody.Part = createFormData("voice", file.name, requestFile)
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

                                    val dlg = this@AddRecipients.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent = Intent(
                                                this@AddRecipients, Communication::class.java
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

                                    val dlg = this@AddRecipients.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent = Intent(
                                                this@AddRecipients, Communication::class.java
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

                        CommonUtil.Toast(activity, "Successfull Uploaded")

                        if (CommonUtil.Priority.equals("p1")) {

                            if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {

                                if (!CommonUtil.receivertype.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        VideosendEntire()

                                    } else {
                                        CommonUtil.ApiAlert(
                                            this@AddRecipients, CommonUtil.Select_the_Target
                                        )
                                    }
                                } else {
                                    CommonUtil.ApiAlert(
                                        this@AddRecipients, CommonUtil.Select_the_Receiver
                                    )
                                }

                            } else if (SelecteRecipientType.equals(CommonUtil.Division)) {

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        VideosendParticuler()

                                    } else {
                                        CommonUtil.ApiAlert(
                                            this@AddRecipients, CommonUtil.Select_the_Target
                                        )
                                    }
                                } else {
                                    CommonUtil.ApiAlert(
                                        this@AddRecipients, CommonUtil.Select_the_Receiver
                                    )
                                }

                            } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {


                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {


                                        VideosendParticuler()

                                    } else {
                                        CommonUtil.ApiAlert(
                                            this@AddRecipients, CommonUtil.Select_the_Target
                                        )
                                    }
                                } else {
                                    CommonUtil.ApiAlert(
                                        this@AddRecipients, CommonUtil.Select_the_Receiver
                                    )
                                }

                            } else if (SelecteRecipientType.equals(CommonUtil.Course)) {

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        VideosendParticuler()

                                    } else {
                                        CommonUtil.ApiAlert(
                                            this@AddRecipients, CommonUtil.Select_the_Target
                                        )
                                    }
                                } else {
                                    CommonUtil.ApiAlert(
                                        this@AddRecipients, CommonUtil.Select_the_Receiver
                                    )
                                }

                            } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {


                                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {

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


                                    } else {
                                        CommonUtil.ApiAlert(
                                            this@AddRecipients, CommonUtil.Select_the_Target
                                        )
                                    }
                                } else {
                                    CommonUtil.ApiAlert(
                                        this@AddRecipients, CommonUtil.Select_the_Receiver
                                    )
                                }

                            } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        VideosendParticuler()

                                    } else {
                                        CommonUtil.ApiAlert(
                                            this@AddRecipients, CommonUtil.Select_the_Target
                                        )
                                    }
                                } else {
                                    CommonUtil.ApiAlert(
                                        this@AddRecipients, CommonUtil.Select_the_Receiver
                                    )
                                }

                            } else {

                                CommonUtil.ApiAlert(
                                    this@AddRecipients, CommonUtil.Select_the_Receiver
                                )

                            }

                        } else if (CommonUtil.Priority.equals("p3")) {


                            if (ScreenName.equals(CommonUtil.New_Video)) {

                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {

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

                                    } else {
                                        CommonUtil.ApiAlert(
                                            this@AddRecipients, CommonUtil.Select_the_Target
                                        )
                                    }
                                } else {
                                    CommonUtil.ApiAlert(
                                        this@AddRecipients, CommonUtil.Select_the_Receiver
                                    )
                                }

                            }
                        } else if (CommonUtil.Priority.equals("p2")) {

                            if (My_Department.equals("Yes")) {

                                if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {

                                    if (!CommonUtil.receivertype.equals("")) {

                                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                            VideosendParticuler()

                                        } else {
                                            CommonUtil.ApiAlert(
                                                this@AddRecipients, CommonUtil.Select_the_Target
                                            )
                                        }

                                    } else {
                                        CommonUtil.ApiAlert(
                                            this@AddRecipients, CommonUtil.Select_the_Receiver
                                        )
                                    }
                                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {

                                    if (!CommonUtil.receivertype.equals("")) {

                                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                            VideosendParticuler()

                                        } else {
                                            CommonUtil.ApiAlert(
                                                this@AddRecipients, CommonUtil.Select_the_Target
                                            )
                                        }

                                    } else {
                                        CommonUtil.ApiAlert(
                                            this@AddRecipients, CommonUtil.Select_the_Receiver
                                        )
                                    }
                                } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

                                    if (!CommonUtil.receivertype.equals("")) {

                                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                                            VideosendParticuler()

                                        } else {
                                            CommonUtil.ApiAlert(
                                                this@AddRecipients, CommonUtil.Select_the_Target
                                            )
                                        }
                                    } else {
                                        CommonUtil.ApiAlert(
                                            this@AddRecipients, CommonUtil.Select_the_Receiver
                                        )
                                    }
                                }

                            } else {


                                if (!CommonUtil.receiverid.equals("")) {

                                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {


                                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {

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

                                    } else {
                                        CommonUtil.ApiAlert(
                                            this@AddRecipients, CommonUtil.Select_the_Target
                                        )
                                    }
                                } else {
                                    CommonUtil.ApiAlert(
                                        this@AddRecipients, CommonUtil.Select_the_Receiver
                                    )
                                }

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

    override fun onResume() {
        super.onResume()
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_add_recipients

    override fun onUploadComplete(success: Boolean, isIframe: String, isLink: String) {
        Log.d("Vimeo_Video_upload", success.toString())
        Log.d("VimeoIframe", isIframe)
        Log.d("link", isLink)

        if (success) {
            CommonUtil.VimeoIframe = isIframe
            CommonUtil.VimeoVideoUrl = isLink
            Log.d("isIframe", CommonUtil.VimeoIframe.toString())
            Log.d("VimeoVideoUrl", CommonUtil.VimeoVideoUrl.toString())
            isVideoSendingApis()

        } else {
            CommonUtil.ApiAlertContext(this, "Video sending faild. Please try again.")
        }
    }

    private fun isVideoSendingApis() {
        CommonUtil.Videofile = true

        Log.d("SeletedFile_Video", CommonUtil.selectedPaths.toString())

//        CommonUtil.Toast(this, "Successfull Uploaded")

        if (CommonUtil.Priority.equals("p1")) {

            if (SelecteRecipientType.equals(CommonUtil.Entire_College)) {

                if (!CommonUtil.receivertype.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        VideosendEntire()

                    } else {
                        CommonUtil.ApiAlert(
                            this@AddRecipients, CommonUtil.Select_the_Target
                        )
                    }
                } else {
                    CommonUtil.ApiAlert(
                        this@AddRecipients, CommonUtil.Select_the_Receiver
                    )
                }

            } else if (SelecteRecipientType.equals(CommonUtil.Division)) {

                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        VideosendParticuler()

                    } else {
                        CommonUtil.ApiAlert(
                            this@AddRecipients, CommonUtil.Select_the_Target
                        )
                    }
                } else {
                    CommonUtil.ApiAlert(
                        this@AddRecipients, CommonUtil.Select_the_Receiver
                    )
                }

            } else if (SelecteRecipientType.equals(CommonUtil.Department_)) {


                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {


                        VideosendParticuler()

                    } else {
                        CommonUtil.ApiAlert(
                            this@AddRecipients, CommonUtil.Select_the_Target
                        )
                    }
                } else {
                    CommonUtil.ApiAlert(
                        this@AddRecipients, CommonUtil.Select_the_Receiver
                    )
                }

            } else if (SelecteRecipientType.equals(CommonUtil.Course)) {

                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        VideosendParticuler()

                    } else {
                        CommonUtil.ApiAlert(
                            this@AddRecipients, CommonUtil.Select_the_Target
                        )
                    }
                } else {
                    CommonUtil.ApiAlert(
                        this@AddRecipients, CommonUtil.Select_the_Receiver
                    )
                }

            } else if (SelecteRecipientType.equals(CommonUtil.Your_Classes)) {

                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {


                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {

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


                    } else {
                        CommonUtil.ApiAlert(
                            this@AddRecipients, CommonUtil.Select_the_Target
                        )
                    }
                } else {
                    CommonUtil.ApiAlert(
                        this@AddRecipients, CommonUtil.Select_the_Receiver
                    )
                }

            } else if (SelecteRecipientType.equals(CommonUtil.Groups)) {

                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        VideosendParticuler()

                    } else {
                        CommonUtil.ApiAlert(
                            this@AddRecipients, CommonUtil.Select_the_Target
                        )
                    }
                } else {
                    CommonUtil.ApiAlert(
                        this@AddRecipients, CommonUtil.Select_the_Receiver
                    )
                }

            } else {

                CommonUtil.ApiAlert(
                    this@AddRecipients, CommonUtil.Select_the_Receiver
                )

            }

        } else if (CommonUtil.Priority.equals("p3")) {


            if (ScreenName.equals(CommonUtil.New_Video)) {

                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {

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

                    } else {
                        CommonUtil.ApiAlert(
                            this@AddRecipients, CommonUtil.Select_the_Target
                        )
                    }
                } else {
                    CommonUtil.ApiAlert(
                        this@AddRecipients, CommonUtil.Select_the_Receiver
                    )
                }

            }
        } else if (CommonUtil.Priority.equals("p2")) {

            if (My_Department.equals("Yes")) {

                if (SelecteRecipientType.equals(CommonUtil.Entire_Department)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            VideosendParticuler()

                        } else {
                            CommonUtil.ApiAlert(
                                this@AddRecipients, CommonUtil.Select_the_Target
                            )
                        }

                    } else {
                        CommonUtil.ApiAlert(
                            this@AddRecipients, CommonUtil.Select_the_Receiver
                        )
                    }
                } else if (SelecteRecipientType.equals(CommonUtil.Course)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            VideosendParticuler()

                        } else {
                            CommonUtil.ApiAlert(
                                this@AddRecipients, CommonUtil.Select_the_Target
                            )
                        }

                    } else {
                        CommonUtil.ApiAlert(
                            this@AddRecipients, CommonUtil.Select_the_Receiver
                        )
                    }
                } else if (SelecteRecipientType.equals(CommonUtil.Year_Section)) {

                    if (!CommonUtil.receivertype.equals("")) {

                        if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {

                            VideosendParticuler()

                        } else {
                            CommonUtil.ApiAlert(
                                this@AddRecipients, CommonUtil.Select_the_Target
                            )
                        }
                    } else {
                        CommonUtil.ApiAlert(
                            this@AddRecipients, CommonUtil.Select_the_Receiver
                        )
                    }
                }

            } else {


                if (!CommonUtil.receiverid.equals("")) {

                    if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {


                        if (txt_selectspecfic!!.visibility == View.VISIBLE) {

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

                    } else {
                        CommonUtil.ApiAlert(
                            this@AddRecipients, CommonUtil.Select_the_Target
                        )
                    }
                } else {
                    CommonUtil.ApiAlert(
                        this@AddRecipients, CommonUtil.Select_the_Receiver
                    )
                }

            }
        }
    }
}


