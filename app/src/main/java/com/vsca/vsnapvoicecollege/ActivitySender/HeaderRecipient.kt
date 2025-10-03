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
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
 import android.widget.TextView
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
import com.vsca.vsnapvoicecollege.databinding.ActivityHeaderRecipientBinding
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
//    var AWSUploadedFilesList = java.util.ArrayList<AWSUploadedFiles>()
    var AWSUploadedFilesList = java.util.ArrayList<String>()
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
    var isAwsUploadingPreSigned: AwsUploadingPreSigned? = null

    private lateinit var binding: ActivityHeaderRecipientBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityHeaderRecipientBinding.inflate(layoutInflater)
        setContentView(binding.root)
         ActionbarWithoutBottom(this)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        ScreenName = intent.getStringExtra("ScreenName")
        isAwsUploadingPreSigned = AwsUploadingPreSigned()
        CommonUtil.Collage_ids = ""
        CommonUtil.Collageid_ArrayList.clear()
        SendingType = CommonUtil.College
        CommonUtil.CallEnable = "0"

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        val VideoToken = SharedPreference.getVideo_Json(this).toString()

        isVideoToken = VideoToken
        Log.d("isVideoToken", VideoToken)

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
            }
        }

        binding.switchonAndoff!!.setOnClickListener {
            if (binding.switchonAndoff!!.isChecked) {
                CommonUtil.CallEnable = "1"
            } else {
                CommonUtil.CallEnable = "0"
            }
        }

        if (CommonUtil.isParentEnable == "1") {
            binding.lnrTargetParent!!.visibility = View.VISIBLE
        } else {
            binding.lnrTargetParent!!.visibility = View.GONE
        }
        binding.chHeader!!.isChecked = false
        binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
        binding.rcyHeader!!.visibility = View.GONE
        binding.chHeader!!.visibility = View.GONE
        binding.testViewCollegespinner!!.visibility = View.GONE

        binding.testViewCollegespinner!!.setOnClickListener {

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
                    binding.testViewCollegespinner!!.text = adapter.getItem(position).toString()
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


        binding.chHeader!!.setOnClickListener(View.OnClickListener {
            CommonUtil.seleteddataArrayCheckbox.clear()
            CommonUtil.SeletedStringdataReplace = ""
            if (binding.chHeader!!.isChecked) {
                SpecificCollage!!.selectAll()
                binding.txtCheckBoxtext!!.text = CommonUtil.Remove_All
            } else {
                SpecificCollage!!.unselectall()
                binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
            }
        })

        binding.chboxAll!!.setOnClickListener {
            if (binding.chboxAll!!.isChecked) {
                if (binding.lnrTargetParent!!.visibility == View.VISIBLE) {
                    binding.chboxStaff!!.isChecked = true
                    binding.chboxStudent!!.isChecked = true
                    binding.chboxParents!!.isChecked = true
                    isStaff = true
                    isParent = true
                    isStudent = true
                } else {
                    binding.chboxStaff!!.isChecked = true
                    binding.chboxStudent!!.isChecked = true
                    isStudent = true
                    isStaff = true
                    isParent = false
                }
            } else {
                if (binding.lnrTargetParent!!.visibility == View.VISIBLE) {
                    binding.chboxStaff!!.isChecked = false
                    binding.chboxStudent!!.isChecked = false
                    binding.chboxParents!!.isChecked = false
                    isStaff = false
                    isParent = false
                    isStudent = false
                } else {
                    binding.chboxStaff!!.isChecked = false
                    binding.chboxStudent!!.isChecked = false
                    isStaff = false
                    isParent = false
                    isStudent = false
                }
            }
        }

        binding.chboxStaff!!.setOnClickListener {
            if (binding.chboxStaff!!.isChecked) {
                if (binding.lnrTargetParent!!.visibility == View.VISIBLE) {
                    binding.chboxAll!!.isChecked =
                        binding.chboxStudent!!.isChecked == true && binding.chboxStaff!!.isChecked == true && binding.chboxParents!!.isChecked == true
                    isStaff = true
                } else {
                    isStaff = true
                    binding.chboxAll!!.isChecked =
                        binding.chboxStudent!!.isChecked == true && binding.chboxStaff!!.isChecked == true
                }
            } else {
                isStaff = false
                binding.chboxAll!!.isChecked = false
            }
        }

        binding.chboxStudent!!.setOnClickListener {
            if (binding.chboxStudent!!.isChecked) {
                if (binding.lnrTargetParent!!.visibility == View.VISIBLE) {
                    binding.chboxAll!!.isChecked =
                        binding.chboxStudent!!.isChecked == true && binding.chboxStaff!!.isChecked == true && binding.chboxParents!!.isChecked == true
                    isStudent = true
                } else {
                    isStudent = true
                    binding.chboxAll!!.isChecked =
                        binding.chboxStudent!!.isChecked == true && binding.chboxStaff!!.isChecked == true
                }
            } else {
                isStudent = false
                binding.chboxAll!!.isChecked = false
            }
        }

        binding.chboxParents!!.setOnClickListener {
            if (binding.chboxParents!!.isChecked) {
                isParent = true
                binding.chboxAll!!.isChecked =
                    binding.chboxStudent!!.isChecked == true && binding.chboxStaff!!.isChecked == true && binding.chboxParents!!.isChecked == true
            } else {
                isParent = false
                binding.chboxAll!!.isChecked = false
            }
        }

        binding.lblEntireDepartmentlable!!.setOnClickListener {

            SendingType = CommonUtil.College
//            lnrTargetAll!!.visibility = View.VISIBLE
            ClickType = ""
            binding.idSV!!.visibility = View.GONE
            CommonUtil.SeletedStringdataReplace = ""
            CommonUtil.courseType = ""
            reciverType = "9"
            collegeEntire = true
            binding.chHeader!!.isChecked = false
            binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
            typeOfCategory = ""
            CommonUtil.receiverid = ""
            seletedIds = ""
            CommonUtil.DepartmentChooseIds.clear()
            CommonUtil.seleteddataArrayCheckbox.clear()
            CommonUtil.Collageid_ArrayList.clear()
            CommonUtil.Collage_ids = ""
            CommonUtil.CollageIDS = true
            binding.lnrStaff!!.visibility = View.VISIBLE
            binding.spinnerDropdown!!.visibility = View.GONE
            binding.spinnerDropdowncourse!!.visibility = View.GONE
            SpinnerData.clear()
//            binding.rcyHeader!!.visibility = View.VISIBLE
//            binding.chHeader!!.visibility = View.VISIBLE
            binding.txtCheckBoxtext!!.visibility = View.VISIBLE
            getCollageList()
            binding.SpinningYourclasses!!.visibility = View.GONE
            binding.testViewCollegespinner!!.visibility = View.GONE

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

            isParent = false
            //    isStudent = false
            isStaff = false

            binding.chboxAll!!.isChecked = false
            //     binding.chboxStudent!!.isChecked = false
            binding.chboxParents!!.isChecked = false
            binding.chboxStaff!!.isChecked = false

        }

        binding.lblDivision!!.setOnClickListener {
            SendingType = CommonUtil.Division
//            lnrTargetAll!!.visibility = View.VISIBLE
            ClickType = ""
            binding.idSV!!.visibility = View.GONE
            CommonUtil.receiverid = ""
            seletedIds = ""
            CommonUtil.DepartmentChooseIds.clear()
            CommonUtil.seleteddataArrayCheckbox.clear()
            CommonUtil.courseType = ""
            SpecificCollage!!.unselectall()
            reciverType = "8"
            collegeEntire = false
            typeOfCategory = ""
            binding.lnrStaff!!.visibility = View.VISIBLE
            binding.spinnerDropdown!!.visibility = View.GONE
            binding.spinnerDropdowncourse!!.visibility = View.GONE
            binding.SpinningYourclasses!!.visibility = View.GONE
            binding.testViewCollegespinner!!.visibility = View.VISIBLE
            binding.testViewCollegespinner!!.text = "Select College"
            binding.chHeader!!.visibility = View.GONE
            binding.txtCheckBoxtext!!.visibility = View.GONE
            binding.rcyHeader!!.visibility = View.GONE
            typeOfCategory = CommonUtil.Division
            Division = true
            binding.chHeader!!.isChecked = false
            binding.txtCheckBoxtext!!.text = CommonUtil.Select_All

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
            isParent = false
            //   isStudent = false
            isStaff = false

            binding.chboxAll!!.isChecked = false
            //    binding.chboxStudent!!.isChecked = false
            binding.chboxParents!!.isChecked = false
            binding.chboxStaff!!.isChecked = false
        }

        binding.lblDepartment!!.setOnClickListener {

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
            binding.lnrStaff!!.visibility = View.VISIBLE
            CommonUtil.seleteddataArrayCheckbox.clear()
            binding.chHeader!!.isChecked = false
            binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
            typeOfCategory = ""
            typeOfCategory = CommonUtil.Department_
            Division = false
            Department = true
            binding.chHeader!!.visibility = View.GONE
            binding.txtCheckBoxtext!!.visibility = View.GONE
            binding.rcyHeader!!.visibility = View.GONE
            binding.SpinningYourclasses!!.visibility = View.GONE
            binding.spinnerDropdowncourse!!.visibility = View.GONE
            binding.spinnerDropdown!!.visibility = View.GONE
            binding.testViewCollegespinner!!.visibility = View.VISIBLE
            binding.testViewCollegespinner!!.text = "Select College"


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
            isParent = false
            //    isStudent = false
            isStaff = false

            binding.chboxAll!!.isChecked = false
            //        binding.chboxStudent!!.isChecked = false
            binding.chboxParents!!.isChecked = false
            binding.chboxStaff!!.isChecked = false
            binding.idSV!!.visibility = View.GONE
        }

        binding.lblCourse!!.setOnClickListener {

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
            binding.lnrStaff!!.visibility = View.GONE
            binding.idSV!!.visibility = View.GONE
            reciverType = "2"
            collegeEntire = false
            binding.chHeader!!.isChecked = false
            binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
            typeOfCategory = ""
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

            binding.SpinningYourclasses!!.visibility = View.GONE

            binding.chHeader!!.visibility = View.GONE
            binding.txtCheckBoxtext!!.visibility = View.GONE
            binding.rcyHeader!!.visibility = View.GONE
            binding.spinnerDropdowncourse!!.visibility = View.GONE
            binding.spinnerDropdown!!.visibility = View.GONE
            binding.testViewCollegespinner!!.visibility = View.VISIBLE
            binding.testViewCollegespinner!!.text = "Select College"
            typeOfCategory = CommonUtil.Course
            Department = false
            Division = false

            isParent = false
            //   isStudent = false
            isStaff = false

            binding.chboxAll!!.isChecked = false
            //    binding.chboxStudent!!.isChecked = false
            binding.chboxParents!!.isChecked = false
            binding.chboxStaff!!.isChecked = false
        }

        binding.lblYourClasses!!.setOnClickListener {
            typeOfCategory = ""

        }

        binding.lblGroups!!.setOnClickListener {
//            if (CommonUtil.isParentEnable == "1") {
//                lnrTargetAll!!.visibility = View.VISIBLE
//            } else {
//                lnrTargetAll!!.visibility = View.GONE
//            }
            SendingType = CommonUtil.Groups

            ClickType = ""
            binding.idSV!!.visibility = View.GONE
            CommonUtil.courseType = ""
            SpecificCollage!!.unselectall()
            CommonUtil.receiverid = ""
            seletedIds = ""
            CommonUtil.DepartmentChooseIds.clear()
            CommonUtil.seleteddataArrayCheckbox.clear()
            reciverType = "6"
            collegeEntire = false
            binding.chHeader!!.isChecked = false
            binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
            typeOfCategory = ""
            binding.lnrStaff!!.visibility = View.GONE

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

            binding.chHeader!!.visibility = View.GONE
            binding.txtCheckBoxtext!!.visibility = View.GONE
            binding.rcyHeader!!.visibility = View.GONE
            binding.spinnerDropdown!!.visibility = View.GONE
            binding.spinnerDropdowncourse!!.visibility = View.GONE
            binding.SpinningYourclasses!!.visibility = View.GONE
            binding.spinnerDropdownCollege!!.visibility = View.GONE
            binding.testViewCollegespinner!!.visibility = View.VISIBLE
            binding.testViewCollegespinner!!.text = "Select College"
            typeOfCategory = CommonUtil.Groups

            isParent = false
            //    isStudent = false
            isStaff = false

            binding.chboxAll!!.isChecked = false
            //  binding.chboxStudent!!.isChecked = false
            binding.chboxParents!!.isChecked = false
            binding.chboxStaff!!.isChecked = false
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
                    binding.rcyHeader!!.visibility = View.VISIBLE
                    binding.chHeader!!.visibility = View.VISIBLE
                    if (CollegeDataSpinning.isEmpty()) {
                        CollageListHeader.forEach {
                            it.college_name?.let { it1 -> CollegeDataSpinning.add(it1) }
                        }
                    }

                    CollageListHeader.forEach {
                        it.college_name
                        it.college_id

                        val group = RecipientSelected(it.college_id.toString(), it.college_name,"")
                        SelectedRecipientlist.add(group)
                    }

                    SpecificCollage = SelectedRecipientAdapter(SelectedRecipientlist,
                        this,
                        object : RecipientCheckListener {
                            override fun add(data: RecipientSelected?) {
                                var groupid = data!!.SelectedId

                                if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                    binding.chHeader!!.isChecked = true
                                    binding.txtCheckBoxtext!!.text = CommonUtil.Remove_All
                                } else {

                                    binding.chHeader!!.isChecked = false
                                    binding.txtCheckBoxtext!!.text = CommonUtil.Select_All

                                }
                            }

                            override fun remove(data: RecipientSelected?) {
                                var groupid = data!!.SelectedId

                                binding.chHeader!!.isChecked = false
                                binding.txtCheckBoxtext!!.setText(CommonUtil.Select_All)
                            }
                        })

                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.rcyHeader!!.layoutManager = mLayoutManager
                    binding.rcyHeader!!.itemAnimator = DefaultItemAnimator()
                    binding.rcyHeader!!.adapter = SpecificCollage
                    binding.rcyHeader!!.recycledViewPool.setMaxRecycledViews(0, 80)
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

                            val divisions = RecipientSelected(it.division_id, it.division_name,"")
                            SelectedRecipientlist.add(divisions)
                        }

                        binding.chHeader!!.isChecked = false
                        binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
                        binding.rcyHeader!!.visibility = View.VISIBLE
                        binding.chHeader!!.visibility = View.VISIBLE
                        binding.txtCheckBoxtext!!.visibility = View.VISIBLE
                        SpecificCollage = SelectedRecipientAdapter(
                            SelectedRecipientlist,
                            this,
                            object : RecipientCheckListener {
                                override fun add(data: RecipientSelected?) {
                                    var divisionId = data!!.SelectedId

                                    if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                        binding.chHeader!!.isChecked = true
                                        binding.txtCheckBoxtext!!.text = CommonUtil.Remove_All
                                    } else {
                                        binding.chHeader!!.isChecked = false
                                        binding.txtCheckBoxtext!!.text = CommonUtil.Select_All

                                    }
                                }

                                override fun remove(data: RecipientSelected?) {
                                    var divisionId = data!!.SelectedId
                                    binding.chHeader!!.isChecked = false
                                    binding.txtCheckBoxtext!!.text = CommonUtil.Select_All

                                }
                            })

                        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                        binding.rcyHeader!!.layoutManager = mLayoutManager
                        binding.rcyHeader!!.itemAnimator = DefaultItemAnimator()
                        binding.rcyHeader!!.adapter = SpecificCollage
                        binding.rcyHeader!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        SpecificCollage!!.notifyDataSetChanged()

                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                        binding.rcyHeader!!.visibility = View.GONE

                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    binding.rcyHeader!!.visibility = View.GONE

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
                        binding.rcyHeader!!.visibility = View.VISIBLE
                        CommonUtil.receiverid = ""
                        LoadDivisionSpinner()
                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                        binding.rcyHeader!!.visibility = View.GONE
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    binding.rcyHeader!!.visibility = View.GONE
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
                    binding.rcyHeader!!.visibility = View.GONE

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
                        binding.rcyHeader!!.visibility = View.VISIBLE
                        binding.chHeader!!.visibility = View.VISIBLE
                        binding.txtCheckBoxtext!!.visibility = View.VISIBLE
                        binding.chHeader!!.isChecked = false
                        binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
                        CommonUtil.receiverid = ""
                        GetGroupdata!!.forEach {
                            it.groupid
                            it.groupname

                            val group = RecipientSelected(it.groupid, it.groupname,"")

                            SelectedRecipientlist.add(group)
                        }
                        SpecificCollage = SelectedRecipientAdapter(SelectedRecipientlist,
                            this,
                            object : RecipientCheckListener {
                                override fun add(data: RecipientSelected?) {
                                    val groupid = data!!.SelectedId


                                    if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                        binding.chHeader!!.isChecked = true
                                        binding.txtCheckBoxtext!!.text = CommonUtil.Remove_All
                                    } else {
                                        binding.chHeader!!.isChecked = false
                                        binding.txtCheckBoxtext!!.text = CommonUtil.Select_All

                                    }

                                }

                                override fun remove(data: RecipientSelected?) {
                                    var groupid = data!!.SelectedId

                                    binding.chHeader!!.isChecked = false
                                    binding.txtCheckBoxtext!!.text = CommonUtil.Select_All


                                }
                            })


                        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                        binding.rcyHeader!!.layoutManager = mLayoutManager
                        binding.rcyHeader!!.itemAnimator = DefaultItemAnimator()
                        binding.rcyHeader!!.adapter = SpecificCollage
                        binding.rcyHeader!!.recycledViewPool.setMaxRecycledViews(0, 80)
                        SpecificCollage!!.notifyDataSetChanged()

                    } else {
                        CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                        binding.rcyHeader!!.visibility = View.GONE
                        binding.chHeader!!.visibility = View.GONE
                        binding.txtCheckBoxtext!!.visibility = View.GONE
                    }
                } else {
                    CommonUtil.ApiAlert(this, CommonUtil.No_Data_Found)
                    binding.rcyHeader!!.visibility = View.GONE
                    binding.chHeader!!.visibility = View.GONE
                    binding.txtCheckBoxtext!!.visibility = View.GONE
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

        binding.btnConfirm!!.setOnClickListener {

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

                if ((binding.chboxParents!!.isChecked) || (binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxParents!!.isChecked) || (binding.chboxStudent!!.isChecked && binding.chboxStaff!!.isChecked) || (binding.chboxParents!!.isChecked && binding.chboxStaff!!.isChecked && binding.chboxStudent!!.isChecked)) {
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

//                            CommonUtil.Noticeboard -> awsFileUpload(this, pathIndex)
                            CommonUtil.Noticeboard -> isUploadAWS()
                            CommonUtil.ScreenNameEvent -> Eventsend("add")
//                            CommonUtil.Image_Pdf -> awsFileUpload(this, pathIndex)
                            CommonUtil.Image_Pdf -> isUploadAWS()
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

        binding.idSV!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(msg: String): Boolean {

                filter(msg)
                return false
            }
        })

        binding.btnRecipientCancel!!.setOnClickListener {
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

            binding.spinnerDropdown!!.visibility = View.VISIBLE
            SpinnerData.clear()
            binding.rcyHeader!!.visibility = View.VISIBLE
            binding.chHeader!!.visibility = View.VISIBLE
            binding.txtCheckBoxtext!!.visibility = View.VISIBLE
            SpinnerData.add("--All Division--")
            val DivisionName = GetCourseData!!.distinctBy { it.division_name }
            DivisionName.forEach {
                SpinnerData.add(it.division_name!!)
            }
            val adapter = ArrayAdapter(this, R.layout.spinner_textview, SpinnerData)
            adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
            binding.spinnerDropdown!!.adapter = adapter
            binding.spinnerDropdown!!.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>, view: View, position: Int, id: Long
                    ) {

                        binding.idSV!!.visibility = View.VISIBLE
                        binding.idSV!!.queryHint = "Search Course"
                        if (position != 0) {
                            SearchType = "FilterType"
                            FilterDepartment.clear()
                            SelectedSpinnerID = DivisionName[position - 1].division_id
                            DivisionId = SelectedSpinnerID.toString()
                            binding.chHeader!!.visibility = View.GONE
                            binding.txtCheckBoxtext!!.visibility = View.GONE
                            binding.rcyHeader!!.visibility = View.VISIBLE
                            CommonUtil.receiverid = ""
                            for (i in GetCourseData!!.indices) {
                                if (GetCourseData!![i].division_id.toString() == SelectedSpinnerID) {
                                    val department = RecipientSelected(
                                        GetCourseData!![i].course_id, GetCourseData!![i].course_name,""
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
                            binding.rcyHeader!!.layoutManager = mLayoutManager
                            binding.rcyHeader!!.itemAnimator = DefaultItemAnimator()
                            binding.rcyHeader!!.adapter = SpecificCollage
                            binding.rcyHeader!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            SpecificCollage!!.notifyDataSetChanged()
                        } else {
                            SearchType = "DivisionPositionZero"
                            binding.spinnerDropdowncourse!!.visibility = View.GONE
                            binding.chHeader!!.visibility = View.VISIBLE
                            binding.txtCheckBoxtext!!.visibility = View.VISIBLE
                            SelectedRecipientlist.clear()
                            binding.rcyHeader!!.visibility = View.VISIBLE
                            CommonUtil.receiverid = ""
                            GetCourseData!!.forEach {
                                it.course_id
                                it.course_name
                                val department = RecipientSelected(it.course_id, it.course_name,"")
                                SelectedRecipientlist.add(department)
                            }

                            if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size) {
                                binding.chHeader!!.isChecked = true
                                binding.txtCheckBoxtext!!.text = CommonUtil.Remove_All
                            } else {
                                binding.chHeader!!.isChecked = false
                                binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
                            }

                            SpecificCollage = SelectedRecipientAdapter(SelectedRecipientlist,
                                this@HeaderRecipient,
                                object : RecipientCheckListener {
                                    override fun add(data: RecipientSelected?) {
                                        var depaetmentid = data!!.SelectedId
                                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                            binding.chHeader!!.isChecked = true
                                            binding.txtCheckBoxtext!!.text = CommonUtil.Remove_All
                                        } else {
                                            binding.chHeader!!.isChecked = false
                                            binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
                                        }
                                    }

                                    override fun remove(data: RecipientSelected?) {
                                        var departmentid = data!!.SelectedId

                                        binding.chHeader!!.isChecked = false
                                        binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
                                    }
                                })
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@HeaderRecipient)
                            binding.rcyHeader!!.layoutManager = mLayoutManager
                            binding.rcyHeader!!.itemAnimator = DefaultItemAnimator()
                            binding.rcyHeader!!.adapter = SpecificCollage
                            binding.rcyHeader!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            SpecificCollage!!.notifyDataSetChanged()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }

        } else if (ClickType == "Department") {

            binding.spinnerDropdown!!.visibility = View.VISIBLE
            SpinnerData.clear()
            binding.rcyHeader!!.visibility = View.VISIBLE
            binding.chHeader!!.visibility = View.VISIBLE
            binding.txtCheckBoxtext!!.visibility = View.VISIBLE
            SpinnerData.add("--All Division--")

            val DivisionName = GetDepartmentData!!.distinctBy { it.division_name }
            DivisionName.forEach {
                SpinnerData.add(it.division_name!!)
            }
            val adapter = ArrayAdapter(this, R.layout.spinner_textview, SpinnerData)
            adapter.setDropDownViewResource(R.layout.spinner_recipient_layout)
            binding.spinnerDropdown!!.adapter = adapter
            binding.spinnerDropdown!!.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>, view: View, position: Int, id: Long
                    ) {

                        binding.idSV!!.visibility = View.VISIBLE
                        binding.idSV!!.queryHint = "Search Department"
                        if (position != 0) {
                            SearchType = "FilterType"
                            FilterDepartment.clear()
                            SelectedSpinnerID = DivisionName[position - 1].division_id
                            DivisionId = SelectedSpinnerID.toString()
                            binding.chHeader!!.visibility = View.GONE
                            binding.txtCheckBoxtext!!.visibility = View.GONE
                            binding.rcyHeader!!.visibility = View.VISIBLE
                            CommonUtil.receiverid = ""
                            for (i in GetDepartmentData!!.indices) {
                                if (GetDepartmentData!![i].division_id.toString() == SelectedSpinnerID) {
                                    val department = RecipientSelected(
                                        GetDepartmentData!![i].department_id,
                                        GetDepartmentData!![i].department_name,""
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
                            binding.rcyHeader!!.layoutManager = mLayoutManager
                            binding.rcyHeader!!.itemAnimator = DefaultItemAnimator()
                            binding.rcyHeader!!.adapter = SpecificCollage
                            binding.rcyHeader!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            SpecificCollage!!.notifyDataSetChanged()

                        } else {

                            SearchType = "DivisionPositionZero"
                            binding.spinnerDropdowncourse!!.visibility = View.GONE
                            binding.chHeader!!.visibility = View.VISIBLE
                            binding.txtCheckBoxtext!!.visibility = View.VISIBLE
                            SelectedRecipientlist.clear()
                            binding.rcyHeader!!.visibility = View.VISIBLE
                            CommonUtil.receiverid = ""
                            GetDepartmentData!!.forEach {
                                it.department_id
                                it.department_name
                                val department =
                                    RecipientSelected(it.department_id, it.department_name,"")
                                SelectedRecipientlist.add(department)
                            }

                            if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size) {
                                binding.chHeader!!.isChecked = true
                                binding.txtCheckBoxtext!!.text = CommonUtil.Remove_All
                            } else {
                                binding.chHeader!!.isChecked = false
                                binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
                            }


                            SpecificCollage = SelectedRecipientAdapter(SelectedRecipientlist,
                                this@HeaderRecipient,
                                object : RecipientCheckListener {
                                    override fun add(data: RecipientSelected?) {
                                        var depaetmentid = data!!.SelectedId
                                        if (SelectedRecipientlist.size == CommonUtil.seleteddataArrayCheckbox.size + 1) {
                                            binding.chHeader!!.isChecked = true
                                            binding.txtCheckBoxtext!!.text = CommonUtil.Remove_All
                                        } else {
                                            binding.chHeader!!.isChecked = false
                                            binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
                                        }
                                    }

                                    override fun remove(data: RecipientSelected?) {
                                        var departmentid = data!!.SelectedId

                                        binding.chHeader!!.isChecked = false
                                        binding.txtCheckBoxtext!!.text = CommonUtil.Select_All
                                    }
                                })
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@HeaderRecipient)
                            binding.rcyHeader!!.layoutManager = mLayoutManager
                            binding.rcyHeader!!.itemAnimator = DefaultItemAnimator()
                            binding.rcyHeader!!.adapter = SpecificCollage
                            binding.rcyHeader!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            SpecificCollage!!.notifyDataSetChanged()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                    }
                }
        }
    }

    private fun LoadDepartmentSpinner() {

        binding.spinnerDropdowncourse!!.visibility = View.VISIBLE
        SpinningCoursedata.clear()
        SpinningCoursedata.add(0, "--All Department--")
        binding.rcyHeader!!.visibility = View.VISIBLE
        val DepartmentName = GetCourseData!!.distinctBy { it.department_name }
        for (i in DepartmentName.indices) {
            if (DepartmentName[i].division_id.toString() == SelectedSpinnerID) {
                SpinningCoursedata.add(DepartmentName[i].department_name.toString())
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

                    SearchType = "FilterType"
                    if (position != 0) {
                        FilterDepartment.clear()
                        binding.chHeader!!.visibility = View.GONE
                        binding.txtCheckBoxtext!!.visibility = View.GONE
                        val name: String = binding.spinnerDropdowncourse!!.selectedItem.toString()
                        for (i in DepartmentName.indices) {
                            if (name == DepartmentName[i].department_name) {
                                SelectedSpinnerID = DepartmentName[i].department_id
                                Log.d("DepartmentID", SelectedSpinnerID.toString())
                                for (j in GetCourseData!!.indices) {
                                    if (GetCourseData!![j].department_name == name) {
                                        val department = RecipientSelected(
                                            GetCourseData!![j].course_id,
                                            GetCourseData!![j].course_name,""
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
                            binding.rcyHeader!!.layoutManager = mLayoutManager
                            binding.rcyHeader!!.itemAnimator = DefaultItemAnimator()
                            binding.rcyHeader!!.adapter = SpecificCollage
                            binding.rcyHeader!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            SpecificCollage!!.notifyDataSetChanged()
                        }
                    } else {
                        FilterDepartment.clear()
                        binding.chHeader!!.visibility = View.GONE
                        binding.txtCheckBoxtext!!.visibility = View.GONE
                        for (i in GetCourseData!!.indices) {
                            if (GetCourseData!![i].division_id.toString() == DivisionId) {
                                val department = RecipientSelected(
                                    GetCourseData!![i].course_id, GetCourseData!![i].course_name,""
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
                        binding.rcyHeader!!.layoutManager = mLayoutManager
                        binding.rcyHeader!!.itemAnimator = DefaultItemAnimator()
                        binding.rcyHeader!!.adapter = SpecificCollage
                        binding.rcyHeader!!.recycledViewPool.setMaxRecycledViews(0, 80)
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
                ApiRequestNames.Req_FileName, AWSUploadedFilesList[i]
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


//    fun awsFileUpload(activity: Activity?, pathind: Int?) {
//
//        Log.d("SelcetedFileList", CommonUtil.SelcetedFileList.size.toString())
//        val s3Uploader1Obj: S3Uploader1
//        s3Uploader1Obj = S3Uploader1(activity)
//        pathIndex = pathind!!
//
//        for (index in pathIndex until CommonUtil.SelcetedFileList.size) {
//            uploadFilePath = CommonUtil.SelcetedFileList[index]
//            Log.d("uploadFilePath", uploadFilePath.toString())
//            val extension = uploadFilePath!!.substring(uploadFilePath!!.lastIndexOf("."))
//            contentType = if (extension == ".pdf") {
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
//                    SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().time)
//                fileNameDateTime = "File_$fileNameDateTime"
//                Log.d("filenamedatetime", fileNameDateTime.toString())
//                s3Uploader1Obj.initUpload(
//                    uploadFilePath, contentType, CommonUtil.CollegeId.toString(), fileNameDateTime
//                )
//                s3Uploader1Obj.setOns3UploadDone(object : S3Uploader1.S3UploadInterface {
//                    override fun onUploadSuccess(response: String?) {
//                        if (response!! == "Success") {
//
//                            CommonUtil.urlFromS3 = S3Utils.generates3ShareUrl(
//                                this@HeaderRecipient,
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
//                                fileName = uploadFilePath?.let { File(it) }
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
//        } else if (ScreenName!!.equals(CommonUtil.Noticeboard)) {
//            NoticeBoardSMSsending()
//        } else {
//            ImageOrPdfsendparticuler()
//        }
//    }

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
                        if (ScreenName!!.equals(CommonUtil.Noticeboard)) {
                            NoticeBoardSMSsending()
                        } else {
                            ImageOrPdfsendparticuler()
                        }
                    }
                }

                override fun onUploadError(error: String?) {
                    Log.e("Upload Error", error!!)
                }
            })
    }

}