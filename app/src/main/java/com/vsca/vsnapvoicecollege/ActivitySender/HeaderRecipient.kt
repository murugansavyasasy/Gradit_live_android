package com.vsca.vsnapvoicecollege.ActivitySender

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.AWS.S3Uploader
import com.vsca.vsnapvoicecollege.AWS.S3Utils
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Activities.Circular
import com.vsca.vsnapvoicecollege.Activities.Communication
import com.vsca.vsnapvoicecollege.Activities.Events
import com.vsca.vsnapvoicecollege.Activities.MessageCommunication
import com.vsca.vsnapvoicecollege.Activities.Noticeboard
import com.vsca.vsnapvoicecollege.Activities.Video
import com.vsca.vsnapvoicecollege.Adapters.SelectedRecipientAdapter
import com.vsca.vsnapvoicecollege.Interfaces.ApiInterfaces
import com.vsca.vsnapvoicecollege.Interfaces.RecipientCheckListener
import com.vsca.vsnapvoicecollege.Model.AWSUploadedFiles
import com.vsca.vsnapvoicecollege.Model.CollageListData
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

class HeaderRecipient : ActionBarActivity(), VimeoUploader.UploadCompletionListener {

    @JvmField
    @BindView(R.id.chboxAll)
    var chboxAll: CheckBox? = null

    @JvmField
    @BindView(R.id.btnConfirm)
    var btnConfirm: Button? = null

    @JvmField
    @BindView(R.id.btnRecipientCancel)
    var btnRecipientCancel: Button? = null

    @JvmField
    @BindView(R.id.txt_onandoff)
    var txt_onandoff: RelativeLayout? = null

    @JvmField
    @BindView(R.id.switchonAndoff)
    var switchonAndoff: Switch? = null

    @JvmField
    @BindView(R.id.idSV)
    var SearchView: SearchView? = null


    @JvmField
    @BindView(R.id.ch_header)
    var ch_header: CheckBox? = null

    @JvmField
    @BindView(R.id.rcy_header)
    var rcy_header: RecyclerView? = null

    @JvmField
    @BindView(R.id.chboxStudent)
    var chboxStudent: CheckBox? = null

    @JvmField
    @BindView(R.id.txt_checkBoxtext)
    var txt_checkBoxtext: TextView? = null

    @JvmField
    @BindView(R.id.chboxParents)
    var chboxParents: CheckBox? = null

    @JvmField
    @BindView(R.id.lnrTargetParent)
    var lnrTargetParent: LinearLayout? = null

    @JvmField
    @BindView(R.id.lnrTargetAll)
    var lnrTargetAll: LinearLayout? = null

    @JvmField
    @BindView(R.id.lnrStaff)
    var lnrStaff: LinearLayout? = null

    @JvmField
    @BindView(R.id.chboxStaff)
    var chboxStaff: CheckBox? = null

    @JvmField
    @BindView(R.id.lblEntireDepartmentlable)
    var lblEntireDepartmentlable: TextView? = null

    @JvmField
    @BindView(R.id.lblDivision)
    var lblDivision: TextView? = null

    @JvmField
    @BindView(R.id.testViewCollegespinner)
    var textViewspinner: TextView? = null

    @JvmField
    @BindView(R.id.lblDepartment)
    var lblDepartment: TextView? = null

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
    @BindView(R.id.spinnerDropdown)
    var spinnerDropdown: Spinner? = null

    @JvmField
    @BindView(R.id.spinnerDropdownCollege)
    var spinnerDropdownCollege: Spinner? = null

    @JvmField
    @BindView(R.id.Spinning_yourclasses)
    var Spinning_yourclasses: Spinner? = null

    @JvmField
    @BindView(R.id.spinnerDropdowncourse)
    var spinnerDropdowncourse: Spinner? = null

    var appViewModel: App? = null
    var CollageListHeader: List<CollageListData> = ArrayList()
    var SpecificCollage: SelectedRecipientAdapter? = null
    var isStaff: Boolean = false
    var isParent: Boolean = false
    var isStudent = true
    var ScreenName: String? = null
    var reciverType = "9"
    var GetDivisionData: ArrayList<GetDivisionData>? = null
    var GetDepartmentData: ArrayList<GetDepartmentData>? = null
    var SpinnerData = ArrayList<String>()
    var CollegeDataSpinning = ArrayList<String>()
    var SelectedSpinnerID: String? = null
    var Division = true
    var Course = false
    var Department = false
    var SpinningCoursedata = ArrayList<String>()
    var GetCourseData: ArrayList<GetCourseData>? = null
    var CollegeId_s: String? = null
    var GetGroupdata: ArrayList<GetGroupData>? = null
    var getsubjectlist: List<Get_staff_yourclass> = ArrayList()
    var dialog: Dialog? = null
    var typeOfCategory = ""
    var FilterDepartment: ArrayList<RecipientSelected> = ArrayList()
    var DivisionId = ""
    var Awsuploadedfile = java.util.ArrayList<String>()
    var pathIndex = 0
    var uploadFilePath: String? = null
    var contentType: String? = null
    var AWSUploadedFilesList = java.util.ArrayList<AWSUploadedFiles>()
    var progressDialog: ProgressDialog? = null
    var fileNameDateTime: String? = null
    var Awsaupladedfilepath: String? = null
    var separator = ","
    var fileName: File? = null
    var filename: String? = null
    var collegeEntire: Boolean = true
    var SearchType = ""
    var ClickType = ""
    var seletedIds = ""
    var SendingType = ""
    var isVideoToken = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        ScreenName = intent.getStringExtra("ScreenName")

        CommonUtil.Collage_ids = ""
        CommonUtil.Collageid_ArrayList.clear()
        SendingType = CommonUtil.College
        CommonUtil.CallEnable = "0"

        val VideoToken = SharedPreference.getVideo_Json(this).toString()

        isVideoToken = VideoToken
        Log.d("isVideoToken", VideoToken)

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

        if (CommonUtil.isParentEnable == "1") {
            lnrTargetParent!!.visibility = View.VISIBLE
        } else {
            lnrTargetParent!!.visibility = View.GONE
        }
        ch_header!!.isChecked = false
        txt_checkBoxtext!!.text = CommonUtil.Select_All
        rcy_header!!.visibility = View.GONE
        ch_header!!.visibility = View.GONE
        textViewspinner!!.visibility = View.GONE

        textViewspinner!!.setOnClickListener {

            dialog = Dialog(this@HeaderRecipient)
            // set custom dialog
            dialog!!.setContentView(R.layout.dialog_searchable_spinner)
            // set custom height and width
            dialog!!.window!!.setLayout(1000, 1000)
            // set transparent background
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // show dialog
            dialog!!.show()
            // Initialize and assign variable
            val editText = dialog!!.findViewById<EditText>(R.id.edit_text)
            val listView = dialog!!.findViewById<ListView>(R.id.list_view)
            val close_btn = dialog!!.findViewById<TextView>(R.id.close_btn)
            // Initialize array adapter
            val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
                this@HeaderRecipient,
                android.R.layout.simple_list_item_1,
                CollegeDataSpinning as List<Any?>
            )
            close_btn!!.setOnClickListener {
                dialog!!.dismiss()
            }
            // set adapter
            listView.adapter = adapter

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable) {}
            })

            listView.onItemClickListener =
                OnItemClickListener { parent, view, position, id -> // when item selected from list
                    textViewspinner!!.text = adapter.getItem(position).toString()
                    CollegeId_s = CollageListHeader[position].college_id.toString()
                    dialog!!.dismiss()
                    CommonUtil.DepartmentChooseIds.clear()
                    when (typeOfCategory) {

                        CommonUtil.Division -> GetDivisionRequest()
                        CommonUtil.Department_ -> {
                            SelectedSpinnerID = "0"
                            GetDepartmentRequest()
                        }

                        CommonUtil.Course -> {
                            SelectedSpinnerID = "0"
                            GetCourseRequest()
                        }

                        CommonUtil.Groups -> GetGroup()

                    }
                }
        }


        ch_header!!.setOnClickListener(View.OnClickListener {
            CommonUtil.seleteddataArrayCheckbox.clear()
            CommonUtil.SeletedStringdataReplace = ""
            if (ch_header!!.isChecked) {
                SpecificCollage!!.selectAll()
                txt_checkBoxtext!!.text = CommonUtil.Remove_All
            } else {
                SpecificCollage!!.unselectall()
                txt_checkBoxtext!!.text = CommonUtil.Select_All
            }
        })

        chboxAll!!.setOnClickListener {
            if (chboxAll!!.isChecked) {
                if (lnrTargetParent!!.visibility == View.VISIBLE) {
                    chboxStaff!!.isChecked = true
                    chboxStudent!!.isChecked = true
                    chboxParents!!.isChecked = true
                    isStaff = true
                    isParent = true
                    isStudent = true
                } else {
                    chboxStaff!!.isChecked = true
                    chboxStudent!!.isChecked = true
                    isStudent = true
                    isStaff = true
                    isParent = false
                }
            } else {
                if (lnrTargetParent!!.visibility == View.VISIBLE) {
                    chboxStaff!!.isChecked = false
                    chboxStudent!!.isChecked = false
                    chboxParents!!.isChecked = false
                    isStaff = false
                    isParent = false
                    isStudent = false
                } else {
                    chboxStaff!!.isChecked = false
                    chboxStudent!!.isChecked = false
                    isStaff = false
                    isParent = false
                    isStudent = false
                }
            }
        }

        chboxStaff!!.setOnClickListener {
            if (chboxStaff!!.isChecked) {
                if (lnrTargetParent!!.visibility == View.VISIBLE) {
                    chboxAll!!.isChecked =
                        chboxStudent!!.isChecked == true && chboxStaff!!.isChecked == true && chboxParents!!.isChecked == true
                    isStaff = true
                } else {
                    isStaff = true
                    chboxAll!!.isChecked =
                        chboxStudent!!.isChecked == true && chboxStaff!!.isChecked == true
                }
            } else {
                isStaff = false
                chboxAll!!.isChecked = false
            }
        }

        chboxStudent!!.setOnClickListener {
            if (chboxStudent!!.isChecked) {
                if (lnrTargetParent!!.visibility == View.VISIBLE) {
                    chboxAll!!.isChecked =
                        chboxStudent!!.isChecked == true && chboxStaff!!.isChecked == true && chboxParents!!.isChecked == true
                    isStudent = true
                } else {
                    isStudent = true
                    chboxAll!!.isChecked =
                        chboxStudent!!.isChecked == true && chboxStaff!!.isChecked == true
                }
            } else {
                isStudent = false
                chboxAll!!.isChecked = false
            }
        }

        chboxParents!!.setOnClickListener {
            if (chboxParents!!.isChecked) {
                isParent = true
                chboxAll!!.isChecked =
                    chboxStudent!!.isChecked == true && chboxStaff!!.isChecked == true && chboxParents!!.isChecked == true
            } else {
                isParent = false
                chboxAll!!.isChecked = false
            }
        }

        lblEntireDepartmentlable!!.setOnClickListener {

            SendingType = CommonUtil.College
//            lnrTargetAll!!.visibility = View.VISIBLE
            ClickType = ""
            SearchView!!.visibility = View.GONE
            CommonUtil.SeletedStringdataReplace = ""
            CommonUtil.courseType = ""
            reciverType = "9"
            collegeEntire = true
            ch_header!!.isChecked = false
            txt_checkBoxtext!!.text = CommonUtil.Select_All
            typeOfCategory = ""
            CommonUtil.receiverid = ""
            seletedIds = ""
            CommonUtil.DepartmentChooseIds.clear()
            CommonUtil.seleteddataArrayCheckbox.clear()
            CommonUtil.Collageid_ArrayList.clear()
            CommonUtil.Collage_ids = ""
            CommonUtil.CollageIDS = true
            lnrStaff!!.visibility = View.VISIBLE
            spinnerDropdown!!.visibility = View.GONE
            spinnerDropdowncourse!!.visibility = View.GONE
            SpinnerData.clear()
//            rcy_header!!.visibility = View.VISIBLE
//            ch_header!!.visibility = View.VISIBLE
            txt_checkBoxtext!!.visibility = View.VISIBLE
            getCollageList()
            Spinning_yourclasses!!.visibility = View.GONE
            textViewspinner!!.visibility = View.GONE

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

            isParent = false
            //    isStudent = false
            isStaff = false

            chboxAll!!.isChecked = false
            //     chboxStudent!!.isChecked = false
            chboxParents!!.isChecked = false
            chboxStaff!!.isChecked = false

        }

        lblDivision!!.setOnClickListener {
            SendingType = CommonUtil.Division
//            lnrTargetAll!!.visibility = View.VISIBLE
            ClickType = ""
            SearchView!!.visibility = View.GONE
            CommonUtil.receiverid = ""
            seletedIds = ""
            CommonUtil.DepartmentChooseIds.clear()
            CommonUtil.seleteddataArrayCheckbox.clear()
            CommonUtil.courseType = ""
            SpecificCollage!!.unselectall()
            reciverType = "8"
            collegeEntire = false
            typeOfCategory = ""
            lnrStaff!!.visibility = View.VISIBLE
            spinnerDropdown!!.visibility = View.GONE
            spinnerDropdowncourse!!.visibility = View.GONE
            Spinning_yourclasses!!.visibility = View.GONE
            textViewspinner!!.visibility = View.VISIBLE
            textViewspinner!!.text = "Select College"
            ch_header!!.visibility = View.GONE
            txt_checkBoxtext!!.visibility = View.GONE
            rcy_header!!.visibility = View.GONE
            typeOfCategory = CommonUtil.Division
            Division = true
            ch_header!!.isChecked = false
            txt_checkBoxtext!!.text = CommonUtil.Select_All

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
            isParent = false
            //   isStudent = false
            isStaff = false

            chboxAll!!.isChecked = false
            //    chboxStudent!!.isChecked = false
            chboxParents!!.isChecked = false
            chboxStaff!!.isChecked = false
        }

        lblDepartment!!.setOnClickListener {

//            lnrTargetAll!!.visibility = View.VISIBLE
            ClickType = "Department"
            SendingType = CommonUtil.Department_
            CommonUtil.courseType = "Department"
            SpecificCollage!!.unselectall()
            reciverType = "3"
            collegeEntire = false
            CommonUtil.receiverid = ""
            CommonUtil.DepartmentChooseIds.clear()
            seletedIds = ""
            lnrStaff!!.visibility = View.VISIBLE
            CommonUtil.seleteddataArrayCheckbox.clear()
            ch_header!!.isChecked = false
            txt_checkBoxtext!!.text = CommonUtil.Select_All
            typeOfCategory = ""
            typeOfCategory = CommonUtil.Department_
            Division = false
            Department = true
            ch_header!!.visibility = View.GONE
            txt_checkBoxtext!!.visibility = View.GONE
            rcy_header!!.visibility = View.GONE
            Spinning_yourclasses!!.visibility = View.GONE
            spinnerDropdowncourse!!.visibility = View.GONE
            spinnerDropdown!!.visibility = View.GONE
            textViewspinner!!.visibility = View.VISIBLE
            textViewspinner!!.text = "Select College"


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
            isParent = false
            //    isStudent = false
            isStaff = false

            chboxAll!!.isChecked = false
            //        chboxStudent!!.isChecked = false
            chboxParents!!.isChecked = false
            chboxStaff!!.isChecked = false
            SearchView!!.visibility = View.GONE
        }

        lblCourse!!.setOnClickListener {

//            if (CommonUtil.isParentEnable == "1") {
//                lnrTargetAll!!.visibility = View.VISIBLE
//            } else {
//                lnrTargetAll!!.visibility = View.GONE
//            }

            SendingType = CommonUtil.Course
            CommonUtil.courseType = "Course"
            SpecificCollage!!.unselectall()
            ClickType = "Course"
            CommonUtil.receiverid = ""
            seletedIds = ""
            CommonUtil.DepartmentChooseIds.clear()
            CommonUtil.seleteddataArrayCheckbox.clear()
            lnrStaff!!.visibility = View.GONE
            SearchView!!.visibility = View.GONE
            reciverType = "2"
            collegeEntire = false
            ch_header!!.isChecked = false
            txt_checkBoxtext!!.text = CommonUtil.Select_All
            typeOfCategory = ""
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

            Spinning_yourclasses!!.visibility = View.GONE

            ch_header!!.visibility = View.GONE
            txt_checkBoxtext!!.visibility = View.GONE
            rcy_header!!.visibility = View.GONE
            spinnerDropdowncourse!!.visibility = View.GONE
            spinnerDropdown!!.visibility = View.GONE
            textViewspinner!!.visibility = View.VISIBLE
            textViewspinner!!.text = "Select College"
            typeOfCategory = CommonUtil.Course
            Department = false
            Division = false

            isParent = false
            //   isStudent = false
            isStaff = false

            chboxAll!!.isChecked = false
            //    chboxStudent!!.isChecked = false
            chboxParents!!.isChecked = false
            chboxStaff!!.isChecked = false
        }

        lblYourClasses!!.setOnClickListener {
            typeOfCategory = ""

        }

        lblGroups!!.setOnClickListener {
//            if (CommonUtil.isParentEnable == "1") {
//                lnrTargetAll!!.visibility = View.VISIBLE
//            } else {
//                lnrTargetAll!!.visibility = View.GONE
//            }
            SendingType = CommonUtil.Groups

            ClickType = ""
            SearchView!!.visibility = View.GONE
            CommonUtil.courseType = ""
            SpecificCollage!!.unselectall()
            CommonUtil.receiverid = ""
            seletedIds = ""
            CommonUtil.DepartmentChooseIds.clear()
            CommonUtil.seleteddataArrayCheckbox.clear()
            reciverType = "6"
            collegeEntire = false
            ch_header!!.isChecked = false
            txt_checkBoxtext!!.text = CommonUtil.Select_All
            typeOfCategory = ""
            lnrStaff!!.visibility = View.GONE

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

            ch_header!!.visibility = View.GONE
            txt_checkBoxtext!!.visibility = View.GONE
            rcy_header!!.visibility = View.GONE
            spinnerDropdown!!.visibility = View.GONE
            spinnerDropdowncourse!!.visibility = View.GONE
            Spinning_yourclasses!!.visibility = View.GONE
            spinnerDropdownCollege!!.visibility = View.GONE
            textViewspinner!!.visibility = View.VISIBLE
            textViewspinner!!.text = "Select College"
            typeOfCategory = CommonUtil.Groups

            isParent = false
            //    isStudent = false
            isStaff = false

            chboxAll!!.isChecked = false
            //  chboxStudent!!.isChecked = false
            chboxParents!!.isChecked = false
            chboxStaff!!.isChecked = false
        }

        // College list

        appViewModel!!.CollageList!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    SelectedRecipientlist.clear()
                    CommonUtil.receiverid = ""
                    CommonUtil.seleteddataArrayCheckbox.clear()
                    CollageListHeader = response.data!!
                    CommonUtil.CollageIDS = true
                    rcy_header!!.visibility = View.VISIBLE
                    ch_header!!.visibility = View.VISIBLE
                    if (CollegeDataSpinning.isEmpty()) {
                        CollageListHeader.forEach {
                            it.college_name?.let { it1 -> CollegeDataSpinning.add(it1) }
                        }
                    }

                    CollageListHeader.forEach {
                        it.college_name
                        it.college_id

                        val group = RecipientSelected(it.college_id.toString(), it.college_name)
                        SelectedRecipientlist.add(group)
                    }

                    SpecificCollage = SelectedRecipientAdapter(SelectedRecipientlist,
                        this,
                        object : RecipientCheckListener {
                            override fun add(data: RecipientSelected?) {
                                var groupid = data!!.SelectedId

                                if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                    ch_header!!.isChecked = true
                                    txt_checkBoxtext!!.text = CommonUtil.Remove_All
                                } else {

                                    ch_header!!.isChecked = false
                                    txt_checkBoxtext!!.text = CommonUtil.Select_All

                                }
                            }

                            override fun remove(data: RecipientSelected?) {
                                var groupid = data!!.SelectedId

                                ch_header!!.isChecked = false
                                txt_checkBoxtext!!.setText(CommonUtil.Select_All)
                            }
                        })

                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    rcy_header!!.layoutManager = mLayoutManager
                    rcy_header!!.itemAnimator = DefaultItemAnimator()
                    rcy_header!!.adapter = SpecificCollage
                    rcy_header!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    SpecificCollage!!.notifyDataSetChanged()
                }
            }
        }


        // DIVISION

        appViewModel!!.GetDivisionMutableLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message

                if (status == 1) {
                    GetDivisionData = response.data!!
                    SelectedRecipientlist.clear()
                    CommonUtil.seleteddataArrayCheckbox.clear()
                    CommonUtil.receiverid = ""
                    CommonUtil.SeletedStringdataReplace = ""
                    if (GetDivisionData!!.size > 0) {
                        GetDivisionData!!.forEach {
                            it.division_id
                            it.division_name

                            val divisions = RecipientSelected(it.division_id, it.division_name)
                            SelectedRecipientlist.add(divisions)
                        }

                        ch_header!!.isChecked = false
                        txt_checkBoxtext!!.text = CommonUtil.Select_All
                        rcy_header!!.visibility = View.VISIBLE
                        ch_header!!.visibility = View.VISIBLE
                        txt_checkBoxtext!!.visibility = View.VISIBLE
                        SpecificCollage = SelectedRecipientAdapter(
                            SelectedRecipientlist,
                            this,
                            object : RecipientCheckListener {
                                override fun add(data: RecipientSelected?) {
                                    var divisionId = data!!.SelectedId

                                    if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                        ch_header!!.isChecked = true
                                        txt_checkBoxtext!!.text = CommonUtil.Remove_All
                                    } else {
                                        ch_header!!.isChecked = false
                                        txt_checkBoxtext!!.text = CommonUtil.Select_All

                                    }
                                }

                                override fun remove(data: RecipientSelected?) {
                                    var divisionId = data!!.SelectedId
                                    ch_header!!.isChecked = false
                                    txt_checkBoxtext!!.text = CommonUtil.Select_All

                                }
                            })

                        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                        rcy_header!!.layoutManager = mLayoutManager
                        rcy_header!!.itemAnimator = DefaultItemAnimator()
                        rcy_header!!.adapter = SpecificCollage
                        rcy_header!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        SpecificCollage!!.notifyDataSetChanged()

                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                        rcy_header!!.visibility = View.GONE

                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    rcy_header!!.visibility = View.GONE

                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        //DEPARTMENT

        appViewModel!!.GetDepartmentMutableLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    GetDepartmentData = response.data!!
                    SelectedRecipientlist.clear()
                    CommonUtil.seleteddataArrayCheckbox.clear()
                    if (GetDepartmentData!!.size > 0) {
                        rcy_header!!.visibility = View.VISIBLE
                        CommonUtil.receiverid = ""
                        LoadDivisionSpinner()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                        rcy_header!!.visibility = View.GONE
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    rcy_header!!.visibility = View.GONE
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
                            CommonUtil.DepartmentChooseIds.clear()
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


        //Course

        appViewModel!!.GetCourseDepartmentMutableLiveData!!.observe(this) { response ->

            if (response != null) {

                val status = response.status
                val message = response.message
                if (status == 1) {

                    GetCourseData = response.data
                    SelectedRecipientlist.clear()
                    CommonUtil.seleteddataArrayCheckbox.clear()
                    LoadDivisionSpinner()
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    rcy_header!!.visibility = View.GONE

                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        //Groups

        appViewModel!!.GetGrouplist!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message

                if (status == 1) {
                    GetGroupdata = response.data!!
                    SelectedRecipientlist.clear()
                    CommonUtil.seleteddataArrayCheckbox.clear()
                    if (GetGroupdata!!.size > 0) {
                        rcy_header!!.visibility = View.VISIBLE
                        ch_header!!.visibility = View.VISIBLE
                        txt_checkBoxtext!!.visibility = View.VISIBLE
                        ch_header!!.isChecked = false
                        txt_checkBoxtext!!.text = CommonUtil.Select_All
                        CommonUtil.receiverid = ""
                        GetGroupdata!!.forEach {
                            it.groupid
                            it.groupname

                            val group = RecipientSelected(it.groupid, it.groupname)

                            SelectedRecipientlist.add(group)
                        }
                        SpecificCollage = SelectedRecipientAdapter(SelectedRecipientlist,
                            this,
                            object : RecipientCheckListener {
                                override fun add(data: RecipientSelected?) {
                                    val groupid = data!!.SelectedId


                                    if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                        ch_header!!.isChecked = true
                                        txt_checkBoxtext!!.text = CommonUtil.Remove_All
                                    } else {
                                        ch_header!!.isChecked = false
                                        txt_checkBoxtext!!.text = CommonUtil.Select_All

                                    }

                                }

                                override fun remove(data: RecipientSelected?) {
                                    var groupid = data!!.SelectedId

                                    ch_header!!.isChecked = false
                                    txt_checkBoxtext!!.text = CommonUtil.Select_All


                                }
                            })


                        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                        rcy_header!!.layoutManager = mLayoutManager
                        rcy_header!!.itemAnimator = DefaultItemAnimator()
                        rcy_header!!.adapter = SpecificCollage
                        rcy_header!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        SpecificCollage!!.notifyDataSetChanged()

                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                        rcy_header!!.visibility = View.GONE
                        ch_header!!.visibility = View.GONE
                        txt_checkBoxtext!!.visibility = View.GONE
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    rcy_header!!.visibility = View.GONE
                    ch_header!!.visibility = View.GONE
                    txt_checkBoxtext!!.visibility = View.GONE
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        // sms send

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
                            CommonUtil.DepartmentChooseIds.clear()
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
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent = Intent(this, Video::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                            CommonUtil.DepartmentChooseIds.clear()
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

                            val i: Intent = Intent(this, Video::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                            CommonUtil.DepartmentChooseIds.clear()
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
                            CommonUtil.DepartmentChooseIds.clear()
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
                            val i: Intent = Intent(this, Noticeboard::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                            CommonUtil.DepartmentChooseIds.clear()
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
                    dlg.setPositiveButton(
                        CommonUtil.OK,
                        DialogInterface.OnClickListener { dialog, which ->
                            val i: Intent = Intent(this, Events::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(i)
                            CommonUtil.DepartmentChooseIds.clear()
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
                            CommonUtil.DepartmentChooseIds.clear()
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
                            CommonUtil.DepartmentChooseIds.clear()
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
                            CommonUtil.DepartmentChooseIds.clear()
                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        btnConfirm!!.setOnClickListener {

            Log.d("isStudent", isStudent.toString())
            Log.d("isParent", isParent.toString())
            Log.d("isStaff", isStaff.toString())

            for (i in CommonUtil.DepartmentChooseIds.indices) {
                seletedIds = CommonUtil.DepartmentChooseIds.joinToString { it }
            }
            Log.d("DepartmentChooseIds", CommonUtil.DepartmentChooseIds.size.toString())
            val receiverCount = CommonUtil.DepartmentChooseIds.size.toString()
            if (seletedIds != "") {
                CommonUtil.receiverid = seletedIds.replace(", ", "~")
            }

            Log.d("ReceiverID", CommonUtil.receiverid)

            if (CommonUtil.receiverid == "") {
                CommonUtil.ApiAlert(this, CommonUtil.Select_the_Receiver)
            } else {

                if ((chboxParents!!.isChecked) || (chboxStaff!!.isChecked) || (chboxStudent!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked) || (chboxStudent!!.isChecked && chboxParents!!.isChecked) || (chboxStudent!!.isChecked && chboxStaff!!.isChecked) || (chboxParents!!.isChecked && chboxStaff!!.isChecked && chboxStudent!!.isChecked)) {
                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
                    alertDialog.setTitle(CommonUtil.Submit_Alart)

                    when (SendingType) {
                        CommonUtil.College -> alertDialog.setMessage(CommonUtil.selected_College + receiverCount)
                        CommonUtil.Division -> alertDialog.setMessage(CommonUtil.selected_Division + receiverCount)
                        CommonUtil.Department_ -> alertDialog.setMessage(CommonUtil.selected_Department + receiverCount)
                        CommonUtil.Course -> alertDialog.setMessage(CommonUtil.selected_Course + receiverCount)
                        CommonUtil.Groups -> alertDialog.setMessage(CommonUtil.selected_Groups + receiverCount)
                    }

                    alertDialog.setPositiveButton(
                        CommonUtil.Yes
                    ) { _, _ ->

                        when (ScreenName) {
                            CommonUtil.Text -> SmsToParticularTypeRequest()
                            CommonUtil.TextHistory -> SmsToParticularTypeRequest()
                            CommonUtil.Communication -> VoiceSendParticuler()
                            CommonUtil.CommunicationVoice -> SendVoiceToParticulerHistory()
//                            CommonUtil.New_Video -> VimeoVideoUpload(this, CommonUtil.videofile!!)
                            CommonUtil.New_Video -> VimeoUploader.uploadVideo(
                                this,
                                CommonUtil.title,
                                CommonUtil.Description,
                                isVideoToken,
                                CommonUtil.videofile!!,
                                this
                            )

                            CommonUtil.Noticeboard -> awsFileUpload(this, pathIndex)
                            CommonUtil.ScreenNameEvent -> Eventsend("add")
                            CommonUtil.Image_Pdf -> awsFileUpload(this, pathIndex)
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

        btnRecipientCancel!!.setOnClickListener {
            onBackPressed()
            CommonUtil.DepartmentChooseIds.clear()
        }
    }

    private fun filter(text: String) {

        val filteredlist: java.util.ArrayList<RecipientSelected> = java.util.ArrayList()

        when (SearchType) {
            "FilterType" -> {
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
        }

        if (filteredlist.isEmpty()) {
            CommonUtil.Toast(this, "No records found")
        } else {
            SpecificCollage!!.filterList(filteredlist, false)
        }
    }

    private fun LoadDivisionSpinner() {

        if (ClickType == "Course") {

            spinnerDropdown!!.visibility = View.VISIBLE
            SpinnerData.clear()
            rcy_header!!.visibility = View.VISIBLE
            ch_header!!.visibility = View.VISIBLE
            txt_checkBoxtext!!.visibility = View.VISIBLE
            SpinnerData.add("--All Division--")
            val DivisionName = GetCourseData!!.distinctBy { it.division_name }
            DivisionName.forEach {
                SpinnerData.add(it.division_name!!)
            }
            val adapter = ArrayAdapter(this, R.layout.spinner_textview, SpinnerData)
            adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
            spinnerDropdown!!.adapter = adapter
            spinnerDropdown!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {

                    SearchView!!.visibility = View.VISIBLE
                    SearchView!!.queryHint = "Search Course"
                    if (position != 0) {
                        SearchType = "FilterType"
                        FilterDepartment.clear()
                        SelectedSpinnerID = DivisionName[position - 1].division_id
                        DivisionId = SelectedSpinnerID.toString()
                        ch_header!!.visibility = View.GONE
                        txt_checkBoxtext!!.visibility = View.GONE
                        rcy_header!!.visibility = View.VISIBLE
                        CommonUtil.receiverid = ""
                        for (i in GetCourseData!!.indices) {
                            if (GetCourseData!![i].division_id.toString() == SelectedSpinnerID) {
                                val department = RecipientSelected(
                                    GetCourseData!![i].course_id, GetCourseData!![i].course_name
                                )
                                FilterDepartment.add(department)
                            }
                        }
                        LoadDepartmentSpinner()
                        SpecificCollage = SelectedRecipientAdapter(FilterDepartment,
                            this@HeaderRecipient,
                            object : RecipientCheckListener {
                                override fun add(data: RecipientSelected?) {
                                    var depaetmentid = data!!.SelectedId
                                }

                                override fun remove(data: RecipientSelected?) {
                                    var departmentid = data!!.SelectedId
                                }
                            })
                        val mLayoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(this@HeaderRecipient)
                        rcy_header!!.layoutManager = mLayoutManager
                        rcy_header!!.itemAnimator = DefaultItemAnimator()
                        rcy_header!!.adapter = SpecificCollage
                        rcy_header!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        SpecificCollage!!.notifyDataSetChanged()
                    } else {
                        SearchType = "DivisionPositionZero"
                        spinnerDropdowncourse!!.visibility = View.GONE
                        ch_header!!.visibility = View.VISIBLE
                        txt_checkBoxtext!!.visibility = View.VISIBLE
                        SelectedRecipientlist.clear()
                        rcy_header!!.visibility = View.VISIBLE
                        CommonUtil.receiverid = ""
                        GetCourseData!!.forEach {
                            it.course_id
                            it.course_name
                            val department = RecipientSelected(it.course_id, it.course_name)
                            SelectedRecipientlist.add(department)
                        }

                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size) {
                            ch_header!!.isChecked = true
                            txt_checkBoxtext!!.text = CommonUtil.Remove_All
                        } else {
                            ch_header!!.isChecked = false
                            txt_checkBoxtext!!.text = CommonUtil.Select_All
                        }

                        SpecificCollage = SelectedRecipientAdapter(SelectedRecipientlist,
                            this@HeaderRecipient,
                            object : RecipientCheckListener {
                                override fun add(data: RecipientSelected?) {
                                    var depaetmentid = data!!.SelectedId
                                    if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                        ch_header!!.isChecked = true
                                        txt_checkBoxtext!!.text = CommonUtil.Remove_All
                                    } else {
                                        ch_header!!.isChecked = false
                                        txt_checkBoxtext!!.text = CommonUtil.Select_All
                                    }
                                }

                                override fun remove(data: RecipientSelected?) {
                                    var departmentid = data!!.SelectedId

                                    ch_header!!.isChecked = false
                                    txt_checkBoxtext!!.text = CommonUtil.Select_All
                                }
                            })
                        val mLayoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(this@HeaderRecipient)
                        rcy_header!!.layoutManager = mLayoutManager
                        rcy_header!!.itemAnimator = DefaultItemAnimator()
                        rcy_header!!.adapter = SpecificCollage
                        rcy_header!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        SpecificCollage!!.notifyDataSetChanged()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }

        } else if (ClickType == "Department") {

            spinnerDropdown!!.visibility = View.VISIBLE
            SpinnerData.clear()
            rcy_header!!.visibility = View.VISIBLE
            ch_header!!.visibility = View.VISIBLE
            txt_checkBoxtext!!.visibility = View.VISIBLE
            SpinnerData.add("--All Division--")

            val DivisionName = GetDepartmentData!!.distinctBy { it.division_name }
            DivisionName.forEach {
                SpinnerData.add(it.division_name!!)
            }
            val adapter = ArrayAdapter(this, R.layout.spinner_textview, SpinnerData)
            adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
            spinnerDropdown!!.adapter = adapter
            spinnerDropdown!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {

                    SearchView!!.visibility = View.VISIBLE
                    SearchView!!.queryHint = "Search Department"
                    if (position != 0) {
                        SearchType = "FilterType"
                        FilterDepartment.clear()
                        SelectedSpinnerID = DivisionName[position - 1].division_id
                        DivisionId = SelectedSpinnerID.toString()
                        ch_header!!.visibility = View.GONE
                        txt_checkBoxtext!!.visibility = View.GONE
                        rcy_header!!.visibility = View.VISIBLE
                        CommonUtil.receiverid = ""
                        for (i in GetDepartmentData!!.indices) {
                            if (GetDepartmentData!![i].division_id.toString() == SelectedSpinnerID) {
                                val department = RecipientSelected(
                                    GetDepartmentData!![i].department_id,
                                    GetDepartmentData!![i].department_name
                                )
                                FilterDepartment.add(department)
                            }
                        }

                        SpecificCollage = SelectedRecipientAdapter(FilterDepartment,
                            this@HeaderRecipient,
                            object : RecipientCheckListener {
                                override fun add(data: RecipientSelected?) {
                                    var depaetmentid = data!!.SelectedId
                                }

                                override fun remove(data: RecipientSelected?) {
                                    var departmentid = data!!.SelectedId
                                }
                            })
                        val mLayoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(this@HeaderRecipient)
                        rcy_header!!.layoutManager = mLayoutManager
                        rcy_header!!.itemAnimator = DefaultItemAnimator()
                        rcy_header!!.adapter = SpecificCollage
                        rcy_header!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        SpecificCollage!!.notifyDataSetChanged()

                    } else {

                        SearchType = "DivisionPositionZero"
                        spinnerDropdowncourse!!.visibility = View.GONE
                        ch_header!!.visibility = View.VISIBLE
                        txt_checkBoxtext!!.visibility = View.VISIBLE
                        SelectedRecipientlist.clear()
                        rcy_header!!.visibility = View.VISIBLE
                        CommonUtil.receiverid = ""
                        GetDepartmentData!!.forEach {
                            it.department_id
                            it.department_name
                            val department = RecipientSelected(it.department_id, it.department_name)
                            SelectedRecipientlist.add(department)
                        }

                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size) {
                            ch_header!!.isChecked = true
                            txt_checkBoxtext!!.text = CommonUtil.Remove_All
                        } else {
                            ch_header!!.isChecked = false
                            txt_checkBoxtext!!.text = CommonUtil.Select_All
                        }


                        SpecificCollage = SelectedRecipientAdapter(SelectedRecipientlist,
                            this@HeaderRecipient,
                            object : RecipientCheckListener {
                                override fun add(data: RecipientSelected?) {
                                    var depaetmentid = data!!.SelectedId
                                    if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                        ch_header!!.isChecked = true
                                        txt_checkBoxtext!!.text = CommonUtil.Remove_All
                                    } else {
                                        ch_header!!.isChecked = false
                                        txt_checkBoxtext!!.text = CommonUtil.Select_All
                                    }
                                }

                                override fun remove(data: RecipientSelected?) {
                                    var departmentid = data!!.SelectedId

                                    ch_header!!.isChecked = false
                                    txt_checkBoxtext!!.text = CommonUtil.Select_All
                                }
                            })
                        val mLayoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(this@HeaderRecipient)
                        rcy_header!!.layoutManager = mLayoutManager
                        rcy_header!!.itemAnimator = DefaultItemAnimator()
                        rcy_header!!.adapter = SpecificCollage
                        rcy_header!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        SpecificCollage!!.notifyDataSetChanged()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }

        }
    }

    private fun LoadDepartmentSpinner() {

        spinnerDropdowncourse!!.visibility = View.VISIBLE
        SpinningCoursedata.clear()
        SpinningCoursedata.add(0, "--All Department--")
        rcy_header!!.visibility = View.VISIBLE
        val DepartmentName = GetCourseData!!.distinctBy { it.department_name }
        for (i in DepartmentName.indices) {
            if (DepartmentName[i].division_id.toString() == SelectedSpinnerID) {
                SpinningCoursedata.add(DepartmentName[i].department_name.toString())
            }
        }
        val adapter = ArrayAdapter(this, R.layout.spinner_rextview_course, SpinningCoursedata)
        adapter.setDropDownViewResource(R.layout.spinner_recipient_course_layout)
        spinnerDropdowncourse!!.adapter = adapter
        spinnerDropdowncourse!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View, position: Int, id: Long
                ) {

                    SearchType = "FilterType"
                    if (position != 0) {
                        FilterDepartment.clear()
                        ch_header!!.visibility = View.GONE
                        txt_checkBoxtext!!.visibility = View.GONE
                        val name: String = spinnerDropdowncourse!!.selectedItem.toString()
                        for (i in DepartmentName.indices) {
                            if (name == DepartmentName[i].department_name) {
                                SelectedSpinnerID = DepartmentName[i].department_id
                                Log.d("DepartmentID", SelectedSpinnerID.toString())
                                for (j in GetCourseData!!.indices) {
                                    if (GetCourseData!![j].department_name == name) {
                                        val department = RecipientSelected(
                                            GetCourseData!![j].course_id,
                                            GetCourseData!![j].course_name
                                        )
                                        FilterDepartment.add(department)
                                    }
                                }
                            }
                            SpecificCollage = SelectedRecipientAdapter(FilterDepartment,
                                this@HeaderRecipient,
                                object : RecipientCheckListener {
                                    override fun add(data: RecipientSelected?) {
                                        var depaetmentid = data!!.SelectedId
                                    }

                                    override fun remove(data: RecipientSelected?) {
                                        var departmentid = data!!.SelectedId
                                    }
                                })
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@HeaderRecipient)
                            rcy_header!!.layoutManager = mLayoutManager
                            rcy_header!!.itemAnimator = DefaultItemAnimator()
                            rcy_header!!.adapter = SpecificCollage
                            rcy_header!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            SpecificCollage!!.notifyDataSetChanged()
                        }
                    } else {
                        FilterDepartment.clear()
                        ch_header!!.visibility = View.GONE
                        txt_checkBoxtext!!.visibility = View.GONE
                        for (i in GetCourseData!!.indices) {
                            if (GetCourseData!![i].division_id.toString() == DivisionId) {
                                val department = RecipientSelected(
                                    GetCourseData!![i].course_id, GetCourseData!![i].course_name
                                )
                                FilterDepartment.add(department)
                            }
                        }
                        SpecificCollage = SelectedRecipientAdapter(FilterDepartment,
                            this@HeaderRecipient,
                            object : RecipientCheckListener {
                                override fun add(data: RecipientSelected?) {
                                    var depaetmentid = data!!.SelectedId

                                }

                                override fun remove(data: RecipientSelected?) {
                                    var departmentid = data!!.SelectedId

                                }
                            })

                        val mLayoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(this@HeaderRecipient)
                        rcy_header!!.layoutManager = mLayoutManager
                        rcy_header!!.itemAnimator = DefaultItemAnimator()
                        rcy_header!!.adapter = SpecificCollage
                        rcy_header!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        SpecificCollage!!.notifyDataSetChanged()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
    }

    private fun GetGroup() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_idcollege, CollegeId_s)
        Log.d("GetGroupRequeat", jsonObject.toString())
        appViewModel!!.getGroup(jsonObject, this)
    }

    private fun GetCourseRequest() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CollegeId_s)
        jsonObject.addProperty(ApiRequestNames.Req_depart_id, SelectedSpinnerID)
        appViewModel!!.getCourseDepartment(jsonObject, this)
        Log.d("GetCourseRequeat", jsonObject.toString())
    }


    private fun GetDivisionRequest() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CollegeId_s)
        appViewModel!!.getDivision(jsonObject, this)
        Log.d("GetDivisionRequest", jsonObject.toString())
    }

    private fun SmsToParticularTypeRequest() {
        val jsonObject = JsonObject()

        val fileType = "1"

        if (collegeEntire) {
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CollegeId_s)
        }

        if (ScreenName.equals(CommonUtil.TextHistory)) {
            jsonObject.addProperty("forwarding_text_id", CommonUtil.forwarding_text_id)
        }

        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_filetype, fileType)
        jsonObject.addProperty(ApiRequestNames.Req_MessageContent, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.MenuDescription)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, reciverType)
        jsonObject.addProperty(ApiRequestNames.Req_receviedit, CommonUtil.receiverid)

        appViewModel!!.SendSmsToParticularType(jsonObject, this)
        Log.d("SMSJsonObjectParticular", jsonObject.toString())
    }

    private fun GetDepartmentRequest() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CollegeId_s)
        jsonObject.addProperty(ApiRequestNames.Req_div_id, SelectedSpinnerID)
        appViewModel!!.getDepartment(jsonObject, this)
        Log.d("GetDepartmentRequest", jsonObject.toString())
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
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, reciverType)
        jsonObject.addProperty(ApiRequestNames.Req_receviedit, CommonUtil.receiverid)
        jsonObject.addProperty("forwarding_voice_id", CommonUtil.voiceHeadedId)
        appViewModel!!.SendVoiceToParticulerHistory(jsonObject, this)
        Log.d("VoiceToEntireHistory", jsonObject.toString())
    }


    private fun getCollageList() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        appViewModel!!.CollageHeaderSend(jsonObject, this)
        Log.d("CollageHeaderSend", jsonObject.toString())
    }

    private fun VideosendParticular() {

        val jsonObject = JsonObject()

        if (collegeEntire) {
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CollegeId_s)
        }

        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_title, CommonUtil.title)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.Description)
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)
        jsonObject.addProperty(ApiRequestNames.iframe, CommonUtil.VimeoIframe)
        jsonObject.addProperty(ApiRequestNames.url, CommonUtil.VimeoVideoUrl)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, reciverType)
        jsonObject.addProperty(ApiRequestNames.Req_receviedit, CommonUtil.receiverid)
        appViewModel!!.VideoParticulerSend(jsonObject, this)
        Log.d("SMSJsonObjectparticuler", jsonObject.toString())

    }

    private fun NoticeBoardSMSsending() {
        val jsonObject = JsonObject()

        if (collegeEntire) {
            jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_colgid, CollegeId_s)
        }

        jsonObject.addProperty(ApiRequestNames.Req_noticeboardid, "0")
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, reciverType)
        jsonObject.addProperty(ApiRequestNames.Req_receiveridlist, CommonUtil.receiverid)
        jsonObject.addProperty(ApiRequestNames.Req_topic, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_Description, CommonUtil.MenuDescription)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_Callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_processtype, "add")
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, isStudent)
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, isStaff)
        jsonObject.addProperty(ApiRequestNames.Req_isParent, isParent)

//        Log.d("Urlfroms3", CommonUtil.urlFromS3.toString())
//        if (!CommonUtil.urlFromS3.equals(null)) {
//            if (CommonUtil.urlFromS3!!.contains(".pdf")) {
//                jsonObject.addProperty(ApiRequestNames.Req_filetype, "pdf")
//            } else {
//                jsonObject.addProperty(ApiRequestNames.Req_filetype, "image")
//            }
//            jsonObject.addProperty(ApiRequestNames.Req_fileduraction, "0")
//        }
//
//
//        val FileNameArray = JsonArray()
//        for (i in AWSUploadedFilesList.indices) {
//            val FileNameobject = JsonObject()
//            FileNameobject.addProperty(
//                ApiRequestNames.Req_FileName, AWSUploadedFilesList[i].filepath
//            )
//            FileNameArray.add(FileNameobject)
//        }
//        jsonObject.add(ApiRequestNames.Req_FileNameArray, FileNameArray)

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


    private fun Eventsend(prossertype: String) {
        val jsonObject = JsonObject()

        if (prossertype == "edit") {
            jsonObject.addProperty(ApiRequestNames.Req_eventid, CommonUtil.EventParticulerId)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_eventid, "0")
        }

        if (collegeEntire) {
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CollegeId_s)
        }
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
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, reciverType)

        appViewModel!!.Eventsend(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }

    private fun ImageOrPdfsendparticuler() {

        val jsonObject = JsonObject()

        if (collegeEntire) {
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CollegeId_s)
        }

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
        jsonObject.addProperty(ApiRequestNames.Req_receviedit, CommonUtil.receiverid)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, reciverType)

        val FileNameArray = JsonArray()

        for (i in AWSUploadedFilesList.indices) {
            val FileNameobject = JsonObject()
            FileNameobject.addProperty(
                ApiRequestNames.Req_FileName, AWSUploadedFilesList[i].filepath
            )
            FileNameArray.add(FileNameobject)
        }
        jsonObject.add(ApiRequestNames.Req_FileNameArray, FileNameArray)

        appViewModel!!.ImageorPdfparticuler(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }

    private fun VoiceSendParticuler() {
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setMessage("Loading...")
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
        val fileType = "1"
        val jsonObject = JsonObject()

        if (collegeEntire) {
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CollegeId_s)
        }

        jsonObject.addProperty("staffid", CommonUtil.MemberId)
        jsonObject.addProperty("callertype", CommonUtil.Priority)
        jsonObject.addProperty("filetype", fileType)
        jsonObject.addProperty("fileduration", CommonUtil.VoiceDuration)
        jsonObject.addProperty("isparent", isParent)
        jsonObject.addProperty("isstudent", isStudent)
        jsonObject.addProperty("isstaff", isStaff)
        jsonObject.addProperty("description", CommonUtil.voicetitle)
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, reciverType)
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

                                    val dlg = this@HeaderRecipient.let { AlertDialog.Builder(it) }
                                    dlg.setTitle(CommonUtil.Info)
                                    dlg.setMessage(message)
                                    dlg.setPositiveButton(CommonUtil.OK,
                                        DialogInterface.OnClickListener { dialog, which ->
                                            val i: Intent =

                                                Intent(
                                                    this@HeaderRecipient, Communication::class.java
                                                )
                                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(i)
                                            CommonUtil.DepartmentChooseIds.clear()
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

                        // Save the iframe and video link
                        CommonUtil.VimeoIframe = iframe
                        CommonUtil.VimeoVideoUrl = link
                        Log.d("VimeoVideoUrl", CommonUtil.VimeoVideoUrl.toString())
                        Log.d("VimeoIframe", CommonUtil.VimeoIframe.toString())

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
                        VideosendParticular()
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

    fun awsFileUpload(activity: Activity?, pathind: Int?) {

        Log.d("SelcetedFileList", CommonUtil.SelcetedFileList.size.toString())
        val s3uploaderObj: S3Uploader
        s3uploaderObj = S3Uploader(activity)
        pathIndex = pathind!!

        for (index in pathIndex until CommonUtil.SelcetedFileList.size) {
            uploadFilePath = CommonUtil.SelcetedFileList[index]
            Log.d("uploadFilePath", uploadFilePath.toString())
            val extension = uploadFilePath!!.substring(uploadFilePath!!.lastIndexOf("."))
            contentType = if (extension == ".pdf") {
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
                    SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().time)
                fileNameDateTime = "File_$fileNameDateTime"
                Log.d("filenamedatetime", fileNameDateTime.toString())
                s3uploaderObj.initUpload(
                    uploadFilePath, contentType, CommonUtil.CollegeId.toString(), fileNameDateTime
                )
                s3uploaderObj.setOns3UploadDone(object : S3Uploader.S3UploadInterface {
                    override fun onUploadSuccess(response: String?) {
                        if (response!! == "Success") {

                            CommonUtil.urlFromS3 = S3Utils.generates3ShareUrl(
                                this@HeaderRecipient,
                                CommonUtil.CollegeId.toString(),
                                uploadFilePath,
                                fileNameDateTime
                            )

                            Log.d("urifroms3", CommonUtil.urlFromS3.toString())

                            if (!TextUtils.isEmpty(CommonUtil.urlFromS3)) {


                                Awsuploadedfile.add(CommonUtil.urlFromS3.toString())
                                Awsaupladedfilepath = Awsuploadedfile.joinToString(separator)


                                fileName = uploadFilePath?.let { File(it) }

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
        } else if (ScreenName!!.equals(CommonUtil.Noticeboard)) {
            NoticeBoardSMSsending()
        } else {
            ImageOrPdfsendparticuler()
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_header_recipient

    override fun onResume() {
        getCollageList()
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
            CommonUtil.Videofile = true
            VideosendParticular()
        } else {
            CommonUtil.ApiAlertContext(this, "Video sending failed. Please try again.")
        }
    }
}