package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.AcademicRecordsEdit.EditAcademicDetails
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume.BuildMyResume
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.MyProfileEdit.EditBasicDetails
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.EditSkillSet
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderAcademicDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderAssessmentDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderCertificationDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderIntenshipDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderProjectDetailsAdapter
import com.vsca.vsnapvoicecollege.Model.GetEducationalDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderAcademicDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderProfileDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderSkillSetDetailsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.OnBackSetBottomMenuClickTrue
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityResumebuilderBinding


class ResumeBuilder : BaseActivity<ActivityResumebuilderBinding>() {


    override var appViewModel: App? = null
    var isMemeberId = 0
    private val isEducationItem = mutableListOf<GetEducationalDetailsData>()


    override fun inflateBinding(): ActivityResumebuilderBinding {
        return ActivityResumebuilderBinding.inflate(layoutInflater)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            val updatedName = data.getStringExtra("updatedName")
            val updatedPhone = data.getStringExtra("updatedPhone")
            val updatedEmail = data.getStringExtra("updatedEmail")
            val updatedImage = data.getStringExtra("updatedImage")


            binding.CommonLayout.lblName.text = updatedName ?: ""
            binding.CommonLayout.lblMobileNo.text = updatedPhone ?: ""
            binding.CommonLayout.lblGamilId.text = updatedEmail ?: ""


            Glide.with(this)
                .load(updatedImage)
                .placeholder(R.drawable.test_image)
                .error(R.drawable.test_image)
                .into(binding.CommonLayout.imgProfile)

            GetProfileDetails()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityResumebuilderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        accessBottomViewIcons(
            binding,
            R.id.img_swipe,
            R.id.layoutbottomCurve,
            R.id.recyclermenusbottom,
            R.id.swipeUpMenus,
            R.id.LayoutDepartment,
            R.id.LayoutCollege,
            R.id.imgAddPlus
        )


        ActionBarMethod(this@ResumeBuilder)
        UserMenuRequest(this)
        MenuBottomType()

        appViewModel?.resumeBuilderAcademicAddEditResponse?.observe(this) { response ->
            if (response != null && response.status) {
                Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show()

                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Save failed, please try again.", Toast.LENGTH_SHORT).show()
            }
        }


        appViewModel?.ResumeBuilderAcademicDetails!!.observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    isLoadAcademicDetails(response.data[0])
                    binding.CommonLayout.lnrAcademicDetails.visibility = View.VISIBLE
                    Log.d("AcademicRespone", response.data.toString())
                } else {
                    binding.CommonLayout.lnrAcademicDetails.visibility = View.GONE
                    binding.CommonLayout.lblEditTwo.text = getString(R.string.txt_Add)
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        appViewModel?.ResumeBuilderSkillSetDetails!!.observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    isLoadSkillSetDetails(response.data)
                    Log.d("AcademicRespone", response.data.toString())
                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        GetProfileDetails() //calling the Profile Details
        GetAcademicDetails()//calling the Academic Details
        GetSkillSetDetails()//calling the SkillSet Details


        CommonUtil.RequestCameraPermission(this)

        binding.CommonLayout.btnEditOne.setOnClickListener {
            val i = Intent(this, EditBasicDetails::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(i)
        }
        binding.CommonLayout.lblEditTwo.setOnClickListener {
            val i = Intent(this, EditAcademicDetails::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Pass backlogs & arrears
            i.putExtra("backlogs", binding.CommonLayout.lblBackLogs.text.toString())
            i.putExtra("arrears", binding.CommonLayout.lblNoofArrears.text.toString())
            this.startActivity(i)
        }
        binding.CommonLayout.btnEditThree.setOnClickListener {
            val i = Intent(this, EditSkillSet::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(i)
        }
        binding.CommonLayout.lblBuildMyResume.setOnClickListener {
            val i = Intent(this, BuildMyResume::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(i)
        }
        Log.d("MemberID", CommonUtil.MemberId.toString())

        binding.CommonLayout.btnEditOne.setOnClickListener {
            val i = Intent(this, EditBasicDetails::class.java)

            i.putExtra("name", binding.CommonLayout.lblName.text.toString())
            i.putExtra("phone", binding.CommonLayout.lblMobileNo.text.toString())
            i.putExtra("email", binding.CommonLayout.lblGamilId.text.toString())
            Log.d("MemberId1", isMemeberId.toString())
            i.putExtra("memberId", isMemeberId)

            i.putExtra(
                "image",
                appViewModel!!.ResumeBuilderProfileDetails!!.value?.data?.firstOrNull()?.memberImagePath
            )
            i.putExtra("placementStatus", appViewModel!!.ResumeBuilderProfileDetails!!.value?.data?.firstOrNull()?.memberPlacementStatus)
            i.putExtra("notificationStatus", appViewModel!!.ResumeBuilderProfileDetails!!.value?.data?.firstOrNull()?.memberNotificationStatus ?: false)
//            startActivityForResult(i, 123)
            startActivity(i)
        }
    }


    private fun isLoadAcademicDetails(AcademicData: GetResumeBuilderAcademicDetailsData) {
        isEducationItem.clear()
        val eduList = AcademicData.educationalDetails
        if (eduList.isNotEmpty()) {
            eduList.forEach { item ->
                val degree = item.classDegree?.trim()
                val percentage = item.percentage?.trim()
                val institution = item.institution?.trim()
                if (!degree.isNullOrEmpty() && !percentage.isNullOrEmpty()&& !institution.isNullOrEmpty()) {
                    isEducationItem.add(item)
                }
            }
        }


        if (AcademicData.backlogs != "") {
            binding.CommonLayout.lblBackLogs.text = AcademicData.backlogs
            binding.CommonLayout.lblBackLogs.background =
                getColoredDrawable(binding.root.context, R.color.light_red)
        } else {
            binding.CommonLayout.lblBackLogs.text = CommonUtil.isIffin
            binding.CommonLayout.lblBackLogs.background =
                getColoredDrawable(binding.root.context, R.color.very_light_gray)
        }

        if (AcademicData.numberOfArrears != "") {
            binding.CommonLayout.lblNoofArrears.text = AcademicData.numberOfArrears
            binding.CommonLayout.lblNoofArrears.background = getColoredDrawable(binding.root.context, R.color.light_red)
        } else {
            binding.CommonLayout.lblNoofArrears.text = CommonUtil.isIffin
            binding.CommonLayout.lblNoofArrears.background =
                getColoredDrawable(binding.root.context, R.color.very_light_gray)
        }

        Log.d("isEducationItem", isEducationItem.toString())
        if (isEducationItem.size > 0 ) {
            binding.CommonLayout.lblEditTwo.text = getString(R.string.txt_edit)
            binding.CommonLayout.lnrAcademicDetails.visibility = View.VISIBLE
            binding.CommonLayout.rcAcademicDetails.layoutManager = GridLayoutManager(this, 3)
            binding.CommonLayout.rcAcademicDetails.isNestedScrollingEnabled = false
            binding.CommonLayout.rcAcademicDetails.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            binding.CommonLayout.rcAcademicDetails.adapter =
                ResumeBuilderAcademicDetailsAdapter(isEducationItem)
        } else {
            binding.CommonLayout.lnrAcademicDetails.visibility = View.GONE
            binding.CommonLayout.lblEditTwo.text = getString(R.string.txt_Add)
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

            binding.CommonLayout.lblName.text = it.memberName
            isMemeberId = it.memberId.toIntOrNull() ?: 0
            binding.CommonLayout.lblMobileNo.text = it.memberPhoneNumber
            binding.CommonLayout.lblGamilId.text = it.memberstudentEmail



            Glide.with(this)
                .load(it.memberImagePath)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .into(binding.CommonLayout.imgProfile)


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
            binding.CommonLayout.lblAvailPlacement.background = drawable
            binding.CommonLayout.lblAvailPlacement.text = it.memberPlacementStatus
//            binding.CommonLayout.lblName.text = it.memberNotificationStatus
            binding.CommonLayout.lblRollno.text = it.memberRegno
            binding.CommonLayout.lblDOB.text = it.memberDob
            binding.CommonLayout.lblGender.text = it.memberGender
            binding.CommonLayout.lblQualification.text = it.courseName
            binding.CommonLayout.lblAddress.text = it.memberPermanentAddress1 + it.memberPermanentAddressCity + it.memberPermanentAddressState + it.memberPermanentAddressCountry

            binding.CommonLayout.lblAdmissionNo.text = it.memberAdmissionNo
            binding.CommonLayout.lblDepartmentName.text = it.departmentName
            binding.CommonLayout.lblYearOfStudy.text = it.semesterName
            binding.CommonLayout.lblSemester.text = it.noOfYear.toString()
        }
    }

    private fun isLoadSkillSetDetails(SkillSetData: List<GetResumeBuilderSkillSetDetailsData>) {
        val SkillSet = SkillSetData.firstOrNull()
        SkillSet?.let {
            binding.CommonLayout.lblLanguageKnown.text = it.languages
            binding.CommonLayout.lblSoftSkills.text = it.softSkill
            binding.CommonLayout.lblAreasofInterest.text = it.areaInterest
            binding.CommonLayout.lblProgrammingLanguages.text = it.programmingLanguage
            binding.CommonLayout.lblToolsandplatformsknown.text = it.toolsPlatform

            if (SkillSet.internship.isNotEmpty()) {
                binding.CommonLayout.rcInternshipExperiences.layoutManager =
                    object : LinearLayoutManager(this, RecyclerView.VERTICAL, false) {
                        override fun canScrollVertically(): Boolean = false
                    }
                binding.CommonLayout.rcInternshipExperiences.isNestedScrollingEnabled = false
                binding.CommonLayout.rcInternshipExperiences.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                binding.CommonLayout.rcInternshipExperiences.adapter =
                    ResumeBuilderIntenshipDetailsAdapter(SkillSet.internship)
            }

            if (SkillSet.certifications.isNotEmpty()) {
                binding.CommonLayout.rcCertifications.layoutManager =
                    object : LinearLayoutManager(this, RecyclerView.VERTICAL, false) {
                        override fun canScrollVertically(): Boolean = false
                    }
                binding.CommonLayout.rcCertifications.isNestedScrollingEnabled = false
                binding.CommonLayout.rcCertifications.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                binding.CommonLayout.rcCertifications.adapter =
                    ResumeBuilderCertificationDetailsAdapter(SkillSet.certifications)
            }

            if (SkillSet.projects.isNotEmpty()) {
                binding.CommonLayout.rcProject.layoutManager =
                    object : LinearLayoutManager(this, RecyclerView.VERTICAL, false) {
                        override fun canScrollVertically(): Boolean = false
                    }
                binding.CommonLayout.rcProject.isNestedScrollingEnabled = false
                binding.CommonLayout.rcProject.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                binding.CommonLayout.rcProject.adapter =
                    ResumeBuilderProjectDetailsAdapter(SkillSet.projects)
            }

            if (SkillSet.assessmentDetails.isNotEmpty()) {
                binding.CommonLayout.rcAssessmentScore.layoutManager =
                    object : LinearLayoutManager(this, RecyclerView.VERTICAL, false) {
                        override fun canScrollVertically(): Boolean = false
                    }
                binding.CommonLayout.rcAssessmentScore.isNestedScrollingEnabled = false
                binding.CommonLayout.rcAssessmentScore.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                binding.CommonLayout.rcAssessmentScore.adapter =
                    ResumeBuilderAssessmentDetailsAdapter(SkillSet.assessmentDetails)
            }

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
//        appViewModel!!.GetResumeBuilderSkillSetDetails(CommonUtil.MemberId, this@ResumeBuilder)
        appViewModel!!.GetResumeBuilderSkillSetDetails(31146, this@ResumeBuilder)
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_resumebuilder

    override fun onResume() {
        GetProfileDetails()
        GetAcademicDetails()
        super.onResume()
    }

    override fun onBackPressed() {
        OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }
}

