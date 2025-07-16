package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.AcademicRecordsEdit.EditAcademicDetails
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume.BuildMyResume
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume.BuildResumeActivity
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
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutResumebuilderBinding


class ResumeBuilder : AppCompatActivity(){

    var isMemeberId = 0
    private val isEducationItem = mutableListOf<GetEducationalDetailsData>()
    var isSkillSetData: GetResumeBuilderSkillSetDetailsData? = null
    var appViewModel: App? = null
    private lateinit var binding: LayoutResumebuilderBinding

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

        appViewModel?.ResumeBuilderAcademicDetails!!.observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    if (response.data.size>0) {
                        isLoadAcademicDetails(response.data[0])
                        binding.lblEditTwo.text = getString(R.string.txt_edit)
                        binding.lnrAcademicDetails.visibility = View.VISIBLE
                        Log.d("AcademicRespone", response.data.toString())
                    }
                    else{
                        binding.lnrAcademicDetails.visibility = View.GONE
                        binding.lblEditTwo.text = getString(R.string.txt_Add)
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    binding.lnrAcademicDetails.visibility = View.GONE
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        appViewModel?.ResumeBuilderSkillSetDetails!!.observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    if (response.data.size>0){
                        isLoadSkillSetDetails(response.data)
                        binding.lnrSkillSetDetails.visibility = View.VISIBLE
                        binding.btnEditThree.text = getString(R.string.txt_edit)
                        Log.d("AcademicRespone", response.data.toString())
                    }
                    else{
                        binding.lnrSkillSetDetails.visibility = View.GONE
                        binding.btnEditThree.text = getString(R.string.txt_Add)
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    binding.lnrSkillSetDetails.visibility = View.GONE
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        GetProfileDetails() //calling the Profile Details
        GetAcademicDetails()//calling the Academic Details
        GetSkillSetDetails()//calling the SkillSet Details


        CommonUtil.RequestCameraPermission(this)


        binding.btnEditOne.setOnClickListener {
            val i = Intent(this, EditBasicDetails::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(i)
        }
        binding.lblEditTwo.setOnClickListener {
            val i = Intent(this, EditAcademicDetails::class.java)
            val academicData = appViewModel?.ResumeBuilderAcademicDetails?.value?.data?.firstOrNull()
            if (academicData != null) {
                i.putExtra("backlogs", academicData.backlogs)
                i.putExtra("arrears", academicData.numberOfArrears)

                val json = Gson().toJson(academicData.educationalDetails)
                i.putExtra("educationalDetails", json)
            }
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivityForResult(i, 101)
        }
        binding.imgback.setOnClickListener{
            onBackPressed()
        }



        binding.btnEditThree.setOnClickListener {
            val i = Intent(this, EditSkillSet::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            isSaveSkillSetData()
            this.startActivity(i)
        }
        binding.lblBuildMyResume.setOnClickListener {
            val i = Intent(this, BuildResumeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(i)
        }
        Log.d("MemberID", CommonUtil.MemberId.toString())


        binding.btnEditOne.setOnClickListener {
            val i = Intent(this, EditBasicDetails::class.java)
            i.putExtra("name", binding.lblName.text.toString())
            i.putExtra("phone", binding.lblMobileNo.text.toString())
            i.putExtra("email", binding.lblGamilId.text.toString())
            Log.d("MemberId1", isMemeberId.toString())
            i.putExtra("memberId", isMemeberId)
            i.putExtra(
                "placementStatus",
                binding.lblAvailPlacement.text.toString()
            )

            i.putExtra(
                "image",
                appViewModel!!.ResumeBuilderProfileDetails!!.value?.data?.firstOrNull()?.memberImagePath
            )
           startActivity(i)
        }
    }

    private fun isSaveSkillSetData() {
        val saveSkillSetData = GetResumeBuilderSkillSetDetailsData(
            idMember =isMemeberId,
            languages=binding.lblLanguageKnown.text.toString(),
            softSkill=binding.lblSoftSkills.text .toString(),
            areaInterest=binding.lblAreasofInterest.text.toString(),
            internship=isSkillSetData?.internship,
            programmingLanguage=binding.lblProgrammingLanguages.text.toString(),
            toolsPlatform=binding.lblToolsandplatformsknown.text.toString(),
            certifications=isSkillSetData?.certifications,
            assessmentDetails=isSkillSetData?.assessmentDetails,
            projects=isSkillSetData?.projects,
        )
        Log.d("isSaveSkillSetData",saveSkillSetData.toString())
        //We are Saving all the data in Constant as List Here
        CommonUtil.isSkillSetDataSending = saveSkillSetData
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
            binding.lblNoofArrears.background = getColoredDrawable(binding.root.context, R.color.light_red)
        } else {
            binding.lblNoofArrears.text = CommonUtil.isIffin
            binding.lblNoofArrears.background =
                getColoredDrawable(binding.root.context, R.color.very_light_gray)
        }

        Log.d("isEducationItem", isEducationItem.toString())
        if (isEducationItem.size > 0 ) {
            binding.lblEditTwo.text = getString(R.string.txt_edit)
            binding.lnrAcademicDetails.visibility = View.VISIBLE
            binding.rcAcademicDetails.layoutManager = GridLayoutManager(this, 3)
            binding.rcAcademicDetails.isNestedScrollingEnabled = false
            binding.rcAcademicDetails.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            binding.rcAcademicDetails.adapter =
                ResumeBuilderAcademicDetailsAdapter(isEducationItem)
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
            isMemeberId = it.memberId.toIntOrNull() ?: 0
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
            binding.lblAddress.text = it.memberPermanentAddress1 + it.memberPermanentAddressCity + it.memberPermanentAddressState + it.memberPermanentAddressCountry
            binding.lblAdmissionNo.text = it.memberAdmissionNo
            binding.lblDepartmentName.text = it.departmentName
            binding.lblYearOfStudy.text = it.semesterName
            binding.lblSemester.text = it.noOfYear.toString()
        }
    }

    private fun isLoadSkillSetDetails(SkillSetData: List<GetResumeBuilderSkillSetDetailsData>) {
        isSkillSetData = SkillSetData.firstOrNull()
        isSkillSetData?.let {
            binding.lblLanguageKnown.text = it.languages
            binding.lblSoftSkills.text = it.softSkill
            binding.lblAreasofInterest.text = it.areaInterest
            binding.lblProgrammingLanguages.text = it.programmingLanguage
            binding.lblToolsandplatformsknown.text = it.toolsPlatform

            if (isSkillSetData!!.internship!!.isNotEmpty()) {
                binding.rcInternshipExperiences.layoutManager =
                    object : LinearLayoutManager(this, RecyclerView.VERTICAL, false) {
                        override fun canScrollVertically(): Boolean = false
                    }
                binding.rcInternshipExperiences.isNestedScrollingEnabled = false
                binding.rcInternshipExperiences.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                binding.rcInternshipExperiences.adapter =
                    ResumeBuilderIntenshipDetailsAdapter(isSkillSetData!!.internship!!)
            }

            if (isSkillSetData!!.certifications!!.isNotEmpty()) {
                binding.rcCertifications.layoutManager =
                    object : LinearLayoutManager(this, RecyclerView.VERTICAL, false) {
                        override fun canScrollVertically(): Boolean = false
                    }
                binding.rcCertifications.isNestedScrollingEnabled = false
                binding.rcCertifications.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                binding.rcCertifications.adapter =
                    ResumeBuilderCertificationDetailsAdapter(isSkillSetData!!.certifications!!)
            }

            if (isSkillSetData!!.projects!!.isNotEmpty()) {
                binding.rcProject.layoutManager =
                    object : LinearLayoutManager(this, RecyclerView.VERTICAL, false) {
                        override fun canScrollVertically(): Boolean = false
                    }
                binding.rcProject.isNestedScrollingEnabled = false
                binding.rcProject.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                binding.rcProject.adapter =
                    ResumeBuilderProjectDetailsAdapter(isSkillSetData!!.projects!!)
            }

            if (isSkillSetData!!.assessmentDetails!!.isNotEmpty()) {
                binding.rcAssessmentScore.layoutManager =
                    object : LinearLayoutManager(this, RecyclerView.VERTICAL, false) {
                        override fun canScrollVertically(): Boolean = false
                    }
                binding.rcAssessmentScore.isNestedScrollingEnabled = false
                binding.rcAssessmentScore.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                binding.rcAssessmentScore.adapter =
                    ResumeBuilderAssessmentDetailsAdapter(isSkillSetData!!.assessmentDetails!!)
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
        isMemeberId=31145
        //        appViewModel!!.GetResumeBuilderSkillSetDetails(CommonUtil.MemberId, this@ResumeBuilder)
        appViewModel!!.GetResumeBuilderSkillSetDetails(isMemeberId, this@ResumeBuilder)
    }

    override fun onResume() {
        GetProfileDetails()
        GetAcademicDetails()
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}