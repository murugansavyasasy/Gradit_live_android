package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.vsca.vsnapvoicecollege.AWS.AwsUploadingPreSigned
import com.vsca.vsnapvoicecollege.AWS.UploadCallback
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.AcademicRecordsEdit.EditAcademicDetails
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume.BuildMyResume
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume.BuildResumeActivity
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume.ResumePreviewActivity
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.MyProfileEdit.EditBasicDetails
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.EditSkillSet
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderAcademicDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderAssessmentDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderCertificationDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderIntenshipDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderProjectDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeListAdapter
import com.vsca.vsnapvoicecollege.Model.GetEducationalDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderAcademicDetails
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderAcademicDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderProfileDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderSkillSetDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeTitleData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutResumebuilderBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


class ResumeBuilder : AppCompatActivity() {

    var isMemeberId = 0
    private val isEducationItem = mutableListOf<GetEducationalDetailsData>()
    var isSkillSetData: GetResumeBuilderSkillSetDetailsData? = null
    var eduList: List<GetEducationalDetailsData> = emptyList()
    var appViewModel: App? = null
    private lateinit var binding: LayoutResumebuilderBinding
    val SELECT_PDF = 8778
    private var savedResumeList: List<GetResumeTitleData> = emptyList()


    var uri: Uri? = null
    var outputDir: File? = null
    var PDFTempFileWrite: File? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutResumebuilderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        appViewModel?.ResumeBuilderProfileDetails!!.observe(this) { response ->
            if (response != null) {

                if (response.status) {
                    isLoadProfileDetails(response.data)
                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        appViewModel?.ResumeBuilderProfileResume!!.observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    savedResumeList = response.data.resumeTitle
                    Log.d("savedResumeList", savedResumeList.toString())

                    if (savedResumeList.size > 0) {
                        binding.lnrBuildMyResume.visibility = View.GONE
                        binding.lblViewMyResumes.visibility = View.VISIBLE
                    } else {
                        binding.lnrBuildMyResume.visibility = View.VISIBLE
                        binding.lblViewMyResumes.visibility = View.GONE
                    }

                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }


        appViewModel?.ResumeBuilderAcademicDetails!!.observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    if (response.data.size > 0) {
                        isLoadAcademicDetails(response.data[0])
//                        binding.lblEditTwo.text = getString(R.string.txt_edit)
//                        binding.lnrAcademicDetails.visibility = View.VISIBLE
                        Log.d("AcademicRespone", response.data.toString())
                    } else {
//                        binding.lnrAcademicDetails.visibility = View.GONE
//                        binding.lblEditTwo.text = getString(R.string.txt_Add)
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.lnrAcademicDetails.visibility = View.GONE
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.btnUploadResume.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            startActivityForResult(intent, SELECT_PDF)
        }

        appViewModel?.ResumeBuilderSkillSetDetails!!.observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    if (response.data.size > 0) {
                        isLoadSkillSetDetails(response.data)

                        Log.d("AcademicRespone", response.data.toString())
                    } else {

                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.lnrSkillSetDetails.visibility = View.GONE
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        GetProfileDetails() //calling the Profile Details
        GetAcademicDetails()//calling the Academic Details
        GetSkillSetDetails()//calling the SkillSet Details
        GetProfileResume()


        binding.lblViewMyResumes.setOnClickListener {
            if (savedResumeList.size > 0) {
                showResumeListDialog(this, savedResumeList)
            } else {
                Toast.makeText(this, "No Resume Avaiable!", Toast.LENGTH_SHORT).show()
            }
        }



        CommonUtil.RequestCameraPermission(this)


        binding.btnEditOne.setOnClickListener {
            val i = Intent(this, EditBasicDetails::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(i)
        }
//        binding.lblEditTwo.setOnClickListener {
//            val i = Intent(this, EditAcademicDetails::class.java)
//            val academicData = appViewModel?.ResumeBuilderAcademicDetails?.value?.data?.firstOrNull()
//            if (academicData != null) {
//                i.putExtra("backlogs", academicData.backlogs)
//                i.putExtra("arrears", academicData.numberOfArrears)
//
//                val json = Gson().toJson(academicData.educationalDetails)
//                i.putExtra("educationalDetails", json)
//            }
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            startActivityForResult(i, 101)
//        }

        binding.lblEditTwo.setOnClickListener {
            val i = Intent(this, EditAcademicDetails::class.java)
            isSaveAcademicDetails()
            this.startActivity(i)
        }
        binding.imgback.setOnClickListener {
            onBackPressed()
        }



        binding.btnEditThree.setOnClickListener {
            val i = Intent(this, EditSkillSet::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            isSaveSkillSetData()
            this.startActivity(i)
        }
        binding.lblBuildMyResume.setOnClickListener {
            saveBasicDetails()
            isSaveSkillSetData()
            isSaveAcademicDetails()
            val i = Intent(this, BuildResumeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(i)
        }
        Log.d("MemberID", CommonUtil.MemberId.toString())


        binding.btnEditOne.setOnClickListener {
            saveBasicDetails()
            startActivity(Intent(this, EditBasicDetails::class.java))
        }


//        binding.btnEditOne.setOnClickListener {
//            val i = Intent(this, EditBasicDetails::class.java)
//            i.putExtra("name", binding.lblName.text.toString())
//            i.putExtra("phone", binding.lblMobileNo.text.toString())
//            i.putExtra("email", binding.lblGamilId.text.toString())
//            Log.d("MemberId1", isMemeberId.toString())
//            i.putExtra("memberId", isMemeberId)
//            i.putExtra(
//                "placementStatus",
//                binding.lblAvailPlacement.text.toString()
//            )
//
//            i.putExtra(
//                "image",
//                appViewModel!!.ResumeBuilderProfileDetails!!.value?.data?.firstOrNull()?.memberImagePath
//            )
//           startActivity(i)
//        }
    }
//
//    private fun isSaveAcademicDetails() {
//        val academicData = GetResumeBuilderAcademicDetails(
//        )
//
//    }


    private fun isSaveAcademicDetails() {
        val academicDetails = GetResumeBuilderAcademicDetailsData(
            backlogs = binding.lblBackLogs.text.toString(),
            numberOfArrears = binding.lblNoofArrears.text.toString(),
            educationalDetails = eduList,
        )

        Log.d("academicDetails", academicDetails.toString())

        Log.d("saveAcademicDetails", "Saving academic details:")
        Log.d("saveAcademicDetails", "Backlogs: ${academicDetails.backlogs}")
        Log.d("saveAcademicDetails", "Arrears: ${academicDetails.numberOfArrears}")
        Log.d("saveAcademicDetails", "Arrears: ${academicDetails.educationalDetails}")

        Log.d("academicDetails", "Educational Details:")
        academicDetails.educationalDetails.forEachIndexed { index, detail ->
            Log.d("academicDetails", "Item $index: $detail")
        }


        // Save to CommonUtil
        CommonUtil.saveAcademicDetails = academicDetails
        Log.d(
            "AcademicDetails-----",
            CommonUtil.saveAcademicDetails!!.educationalDetails.toString()
        )

    }

    private fun saveBasicDetails() {
        val profile = appViewModel?.ResumeBuilderProfileDetails?.value?.data?.firstOrNull()

        val basicDetails = GetResumeBuilderProfileDetailsData(
            memberId = isMemeberId.toString(),
            memberName = binding.lblName.text.toString(),
            memberPhoneNumber = binding.lblMobileNo.text.toString(),
            memberstudentEmail = binding.lblGamilId.text.toString(),
            memberPlacementStatus = binding.lblAvailPlacement.text.toString(),
            memberImagePath = profile?.memberImagePath,
            memberPermanentAddress1 = profile?.memberPermanentAddress1,
            memberPermanentAddressCity = profile?.memberPermanentAddressCity,
            memberPermanentAddressState = profile?.memberPermanentAddressState,
            memberPermanentAddressPincode = profile?.memberPermanentAddressPincode,
            memberPermanentAddressCountry = profile?.memberPermanentAddressCountry
        )

        Log.d("saveBasicDetails", "Saving basic details:")
        Log.d("saveBasicDetails", "ID: ${basicDetails.memberId}")
        Log.d("saveBasicDetails", "Name: ${basicDetails.memberName}")
        Log.d("saveBasicDetails", "Phone: ${basicDetails.memberPhoneNumber}")
        Log.d("saveBasicDetails", "Email: ${basicDetails.memberstudentEmail}")
        Log.d("saveBasicDetails", "Address1: ${basicDetails.memberPermanentAddress1}")
        Log.d("saveBasicDetails", "City: ${basicDetails.memberPermanentAddressCity}")
        Log.d("saveBasicDetails", "State: ${basicDetails.memberPermanentAddressState}")
        Log.d("saveBasicDetails", "Pincode: ${basicDetails.memberPermanentAddressPincode}")
        Log.d("saveBasicDetails", "Country: ${basicDetails.memberPermanentAddressCountry}")

        CommonUtil.saveBasicDetails = basicDetails
    }


    private fun isSaveSkillSetData() {
        val saveSkillSetData = GetResumeBuilderSkillSetDetailsData(
            idMember = isMemeberId,
            languages = binding.lblLanguageKnown.text.toString(),
            softSkill = binding.lblSoftSkills.text.toString(),
            areaInterest = binding.lblAreasofInterest.text.toString(),
            internship = isSkillSetData?.internship,
            programmingLanguage = binding.lblProgrammingLanguages.text.toString(),
            toolsPlatform = binding.lblToolsandplatformsknown.text.toString(),
            certifications = isSkillSetData?.certifications,
            assessmentDetails = isSkillSetData?.assessmentDetails,
            projects = isSkillSetData?.projects,
        )
        Log.d("binding.lblLanguageKnown.text",binding.lblLanguageKnown.text.toString())
        Log.d("binding.lblLanguageKnown.text++++",saveSkillSetData.languages.toString())
        //We are Saving all the data in Constant as List Here
        CommonUtil.isSkillSetDataSending = saveSkillSetData
        Log.d("isComingData",CommonUtil.isSkillSetDataSending!!.languages.toString())
    }

    private fun isLoadAcademicDetails(AcademicData: GetResumeBuilderAcademicDetailsData) {
        isEducationItem.clear()
        eduList = AcademicData.educationalDetails
        if (eduList.isNotEmpty()) {
            eduList.forEach { item ->
                val degree = item.classDegree?.trim()
                val percentage = item.percentage?.trim()
                val institution = item.institution?.trim()
                if (!degree.isNullOrEmpty() && !percentage.isNullOrEmpty() && !institution.isNullOrEmpty()) {
                    isEducationItem.add(item)
                }
            }
        }

        Log.d("isEducationItem", isEducationItem.toString())
        if (isEducationItem.size > 0 || AcademicData.numberOfArrears != ""||AcademicData.backlogs != "") {
            binding.lblEditTwo.text = getString(R.string.txt_edit)
            binding.lnrAcademicDetails.visibility = View.VISIBLE
            binding.rcAcademicDetails.layoutManager = GridLayoutManager(this, 3)
            binding.rcAcademicDetails.isNestedScrollingEnabled = false
            binding.rcAcademicDetails.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            binding.rcAcademicDetails.adapter =
                ResumeBuilderAcademicDetailsAdapter(isEducationItem)

            if (AcademicData.backlogs != "") {
                binding.lblBackLogs.text = AcademicData.backlogs
                binding.lblBackLogs.background =
                    getColoredDrawable(binding.root.context, R.color.light_red)
            } else {
                binding.lblBackLogs.text = CommonUtil.isIffin
                binding.lblBackLogs.background =
                    getColoredDrawable(binding.root.context, R.color.very_light_gray)
            }

            if (AcademicData.numberOfArrears != "") {
                binding.lblNoofArrears.text = AcademicData.numberOfArrears
                binding.lblNoofArrears.background =
                    getColoredDrawable(binding.root.context, R.color.light_red)
            } else {
                binding.lblNoofArrears.text = CommonUtil.isIffin
                binding.lblNoofArrears.background =
                    getColoredDrawable(binding.root.context, R.color.very_light_gray)
            }

        } else {
            binding.lnrAcademicDetails.visibility = View.GONE
            binding.lblEditTwo.text = getString(R.string.txt_Add)
        }

    }

    fun getColoredDrawable(context: Context, @ColorRes colorResId: Int): GradientDrawable? {
        val drawable = ContextCompat
            .getDrawable(context, R.drawable.dark_green_radius)
            ?.mutate()

        if (drawable is GradientDrawable) {
            drawable.setColor(ContextCompat.getColor(context, colorResId))
            return drawable
        }
        return null
    }

    private fun isLoadProfileDetails(profileData: List<GetResumeBuilderProfileDetailsData>) {
        val profile = profileData.firstOrNull()
        profile?.let {


            binding.lblName.text = it.memberName
            isMemeberId = it.memberId?.toIntOrNull() ?: 0
            binding.lblMobileNo.text = it.memberPhoneNumber
            binding.lblGamilId.text = it.memberstudentEmail



            Glide.with(this)
                .load(it.memberImagePath)
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .into(binding.imgProfile)


            val drawable = ContextCompat.getDrawable(binding.root.context, R.drawable.green_radius)
                ?.mutate() as GradientDrawable
            if (it.memberPlacementStatus == CommonUtil.isShortlisted) {
                drawable.setColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.very_light_yellow
                    )
                )
            }
            if (it.memberPlacementStatus == CommonUtil.isAvailable) {
                drawable.setColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.very_light_green
                    )
                )
            }
            if (it.memberPlacementStatus == CommonUtil.isPlaced) {
                drawable.setColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.very_light_purple
                    )
                )
            }
            if (it.memberPlacementStatus == CommonUtil.isAttendedInterview) {
                drawable.setColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.very_light_blue
                    )
                )
            }
            if (it.memberPlacementStatus == CommonUtil.isNotConsented) {
                drawable.setColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.very_light_red
                    )
                )
            }
            binding.lblAvailPlacement.background = drawable
            binding.lblAvailPlacement.text = it.memberPlacementStatus
//            binding.CommonLayout.lblName.text = it.memberNotificationStatus
            binding.lblRollno.text = it.memberRegno
            binding.lblDOB.text = it.memberDob
            binding.lblGender.text = it.memberGender
            binding.lblQualification.text = it.courseName
            binding.lblAddress.text =
                it.memberPermanentAddress1 +" "+ it.memberPermanentAddressCity +" "+it.memberPermanentAddressPincode +" "+it.memberPermanentAddressState +" "+ it.memberPermanentAddressCountry
            binding.lblAdmissionNo.text = it.memberAdmissionNo
            binding.lblDepartmentName.text = it.departmentName
            binding.lblYearOfStudy.text = it.semesterName
            binding.lblSemester.text = it.noOfYear.toString()
        }
    }

    private fun isLoadSkillSetDetails(SkillSetData: List<GetResumeBuilderSkillSetDetailsData>) {
        isSkillSetData = SkillSetData.firstOrNull()

        val data = isSkillSetData

        if (data != null) {
            // Check if there is any data to show
            val hasTextData = listOf(
                data.languages?.trim(),
                data.softSkill?.trim(),
                data.areaInterest?.trim(),
                data.programmingLanguage?.trim(),
                data.toolsPlatform?.trim()
            ).any { !it.isNullOrEmpty() }

            val hasListData = listOf(
                data.internship,
                data.certifications,
                data.projects,
                data.assessmentDetails
            ).any { !it.isNullOrEmpty() }

            val hasAnyData = hasTextData || hasListData

            if (hasAnyData) {
                // Show layout and set edit text
                binding.lnrSkillSetDetails.visibility = View.VISIBLE
                binding.btnEditThree.text = getString(R.string.txt_edit)

                // Text fields with fallback
                binding.lblLanguageKnown.text = data.languages ?: CommonUtil.isIffin
                binding.lblSoftSkills.text = data.softSkill ?: CommonUtil.isIffin
                binding.lblAreasofInterest.text = data.areaInterest ?: CommonUtil.isIffin
                binding.lblProgrammingLanguages.text = data.programmingLanguage ?: CommonUtil.isIffin
                binding.lblToolsandplatformsknown.text = data.toolsPlatform ?: CommonUtil.isIffin

                // Internship
                if (!data.internship.isNullOrEmpty()) {
                    binding.rcInternshipExperiences.apply {
                        layoutManager = LinearLayoutManager(this@ResumeBuilder, RecyclerView.VERTICAL, false)
                        adapter = ResumeBuilderIntenshipDetailsAdapter(data.internship)
                        isNestedScrollingEnabled = false
                        overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                        visibility = View.VISIBLE
                    }
                } else {
                    binding.rcInternshipExperiences.visibility = View.GONE
                }

                // Certifications
                if (!data.certifications.isNullOrEmpty()) {
                    binding.rcCertifications.apply {
                        layoutManager = LinearLayoutManager(this@ResumeBuilder, RecyclerView.VERTICAL, false)
                        adapter = ResumeBuilderCertificationDetailsAdapter(data.certifications)
                        isNestedScrollingEnabled = false
                        overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                        visibility = View.VISIBLE
                    }
                } else {
                    binding.rcCertifications.visibility = View.GONE
                }

                // Projects
                if (!data.projects.isNullOrEmpty()) {
                    binding.rcProject.apply {
                        layoutManager = LinearLayoutManager(this@ResumeBuilder, RecyclerView.VERTICAL, false)
                        adapter = ResumeBuilderProjectDetailsAdapter(data.projects)
                        isNestedScrollingEnabled = false
                        overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                        visibility = View.VISIBLE
                    }
                } else {
                    binding.rcProject.visibility = View.GONE
                }

                // Assessment
                if (!data.assessmentDetails.isNullOrEmpty()) {
                    binding.rcAssessmentScore.apply {
                        layoutManager = LinearLayoutManager(this@ResumeBuilder, RecyclerView.VERTICAL, false)
                        adapter = ResumeBuilderAssessmentDetailsAdapter(data.assessmentDetails)
                        isNestedScrollingEnabled = false
                        overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                        visibility = View.VISIBLE
                    }
                } else {
                    binding.rcAssessmentScore.visibility = View.GONE
                }

            } else {
                // No data means we are hiding the layout
                binding.lnrSkillSetDetails.visibility = View.GONE
                binding.btnEditThree.text = getString(R.string.txt_Add)
            }

        } else {
            // Null object — hide layout
            binding.lnrSkillSetDetails.visibility = View.GONE
            binding.btnEditThree.text = getString(R.string.txt_Add)
        }

    }

    fun GetProfileDetails() {
        appViewModel!!.GetResumeBuilderProfileDetails(31145, this@ResumeBuilder)
    }

    fun GetAcademicDetails() {
//        appViewModel!!.GetResumeBuilderAcademicDetails(CommonUtil.MemberId, this@ResumeBuilder)
        appViewModel!!.GetResumeBuilderAcademicDetails(31145, this@ResumeBuilder)
    }

    fun GetSkillSetDetails() {
        isMemeberId = 31145
        //        appViewModel!!.GetResumeBuilderSkillSetDetails(CommonUtil.MemberId, this@ResumeBuilder)
        appViewModel!!.GetResumeBuilderSkillSetDetails(isMemeberId, this@ResumeBuilder)
    }


    fun showResumeListDialog(
        activity: Activity,
        resumeList: List<GetResumeTitleData>
    ) {
        if (activity.isFinishing || activity.isDestroyed) return

        val dialogView = LayoutInflater.from(activity).inflate(R.layout.see_my_resume, null)
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Check again before showing
        if (!activity.isFinishing && !activity.isDestroyed) {
            alertDialog.show()
        }

        val imgClose = dialogView.findViewById<ImageView>(R.id.imgClose)
        val lblBuildMyResume = dialogView.findViewById<TextView>(R.id.lblBuildMyResume)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rcProfileResume)
        val btnUploadResume = dialogView.findViewById<TextView>(R.id.btnUploadResume)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = ResumeListAdapter(activity, resumeList) {
            alertDialog.dismiss()
            val intent = Intent(activity, ResumePreviewActivity::class.java)
            intent.putExtra("TemplateDocumentURL", it.url)
            intent.putExtra("ScreenName","MyResumes")
            intent.putExtra("FileName",it.title)
            intent.putExtra("MemberID",31145)
            activity.startActivity(intent)
        }

        btnUploadResume.setOnClickListener {
            CommonUtil.SelcetedFileList.clear()

            alertDialog.dismiss() // dismiss before starting file picker
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            activity.startActivityForResult(intent, SELECT_PDF)
        }

        imgClose.setOnClickListener {
            alertDialog.dismiss()
        }

        lblBuildMyResume.setOnClickListener {
            alertDialog.dismiss()
            if (!activity.isFinishing && !activity.isDestroyed) {
                saveBasicDetails()
                isSaveSkillSetData()
                isSaveAcademicDetails()
                val i = Intent(activity, BuildResumeActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity.startActivity(i)
            }
        }
    }


    fun GetProfileResume() {
        appViewModel!!.GetResumeBuilderProfileResume(31145, this@ResumeBuilder)
    }

    override fun onResume() {
        GetProfileDetails()
        GetAcademicDetails()
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_PDF && resultCode == RESULT_OK && data != null) {

            if (resultCode == RESULT_OK) {
                uri = data.data!!
                Log.d("uri", uri.toString())
                val uriString: String = uri.toString()

                var Count: String? = null
                if (CommonUtil.SelcetedFileList != null) {
                    Count = CommonUtil.SelcetedFileList.size.toString()
                    Toast.makeText(this, Count, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, Count, Toast.LENGTH_SHORT).show()
                }

                if (uriString.startsWith("content://")) {
                    ReadAndWriteFile(uri, ".pdf")
                }
            }
        }

    }

    fun ReadAndWriteFile(uri: Uri?, type: String) {
        try {
            uri?.let {
                contentResolver?.openInputStream(it).use { `in` ->
                    if (`in` == null) {
                        Toast.makeText(this, "Failed to open file", Toast.LENGTH_SHORT).show()
                        return
                    }

                    try {
                        PDFTempFileWrite = File.createTempFile("File_", type, outputDir)
                        val pdfPath = PDFTempFileWrite?.path ?: ""

                        if (pdfPath.isNotEmpty()) {
                            CommonUtil.extension = pdfPath.substring(pdfPath.lastIndexOf("."))
                            Log.d("extensionpdf", CommonUtil.extension!!)
                            Log.d("PDFTempFileWrite", pdfPath)

                            this.contentResolver?.openOutputStream(Uri.fromFile(PDFTempFileWrite))
                                .use { out ->
                                    if (out == null) return
                                    val buf = ByteArray(1024)
                                    var len = 0
                                    while (true) {
                                        try {
                                            if (`in`.read(buf).also({ len = it }) <= 0) break
                                        } catch (e: IOException) {
                                            e.printStackTrace()
                                        }
                                        try {
                                            out.write(buf, 0, len)
                                        } catch (e: IOException) {
                                            e.printStackTrace()
                                        }
                                    }
                                }


                            // Add path to list
                            CommonUtil.SelcetedFileList.add(pdfPath)

                            //  Success - Redirect here
                            val intent = Intent(this,ResumePreviewActivity::class.java) // replace with your destination activity
                            intent.putExtra("ScreenName","UploadResume")
                            intent.putExtra("MemberID",isMemeberId)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(this, "Invalid file path", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Error creating file", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                Toast.makeText(this, "Invalid file URI", Toast.LENGTH_SHORT).show()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "IO Error", Toast.LENGTH_SHORT).show()
        }
    }




}