package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder

import com.vsca.vsnapvoicecollege.Activities.BaseActivity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import android.graphics.Color
import android.content.Context


import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.AcademicRecordsEdit.EditAcademicDetails
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume.BuildMyResume
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.MyProfileEdit.EditBasicDetails
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit.EditSkillSet
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAssignmentDetails
import com.vsca.vsnapvoicecollege.Model.GetOverAllCountDetails
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderProfileDetails
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderProfileDetailsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Repository.RestClient.Companion.changeApiBaseUrl
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.OnBackSetBottomMenuClickTrue
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityResumebuilderBinding
import java.util.Locale

class ResumeBuilder : BaseActivity<ActivityResumebuilderBinding>() {

    override var appViewModel: App? = null


    override fun inflateBinding(): ActivityResumebuilderBinding {
        return ActivityResumebuilderBinding.inflate(layoutInflater)
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
//        calling the Profile Details
        GetProfileDetails()

        appViewModel!!.getResumeBuilderProfileDetailsLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == true) {
                        isLoadProfileDetails(response.data)
                    }
                else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        CommonUtil.RequestCameraPermission(this)
        binding.CommonLayout.btnEditOne.setOnClickListener{
            val i = Intent(this, EditBasicDetails::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(i)
        }
        binding.CommonLayout.lblEditTwo.setOnClickListener{
            val i = Intent(this, EditAcademicDetails::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(i)
        }
        binding.CommonLayout.btnEditThree.setOnClickListener{
            val i = Intent(this, EditSkillSet::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(i)
        }
        binding.CommonLayout.lblBuildMyResume.setOnClickListener{
            val i = Intent(this, BuildMyResume::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            this.startActivity(i)
        }
        Log.d("MemberID",CommonUtil.MemberId.toString())


    }

    private fun isLoadProfileDetails(profileData :List<GetResumeBuilderProfileDetailsData>) {
        val profile = profileData.firstOrNull()
        profile?.let {
            binding.CommonLayout.lblName.text = it.memberName
            binding.CommonLayout.lblMobileNo.text = it.memberPhoneNumber
            binding.CommonLayout.lblGamilId.text = it.memberstudentEmail
            Glide.with(binding.root.context)
                .load(it.memberImagePath)
                .placeholder(R.drawable.test_image)
                .error(R.drawable.test_image)
                .into(binding.CommonLayout.imgProfile)


            val drawable = ContextCompat.getDrawable(binding.root.context, R.drawable.green_radius)?.mutate() as GradientDrawable
            if (it.memberPlacementStatus==CommonUtil.isShortlisted){
                drawable.setColor(ContextCompat.getColor(binding.root.context, R.color.very_light_yellow))
            }
            if(it.memberPlacementStatus==CommonUtil.isAvailable){
                drawable.setColor(ContextCompat.getColor(binding.root.context, R.color.very_light_green))
            }
            if(it.memberPlacementStatus==CommonUtil.isPlaced){
                drawable.setColor(ContextCompat.getColor(binding.root.context, R.color.very_light_purple))
            }
            if(it.memberPlacementStatus==CommonUtil.isAttendedInterview){
                drawable.setColor(ContextCompat.getColor(binding.root.context, R.color.very_light_blue))
            }
            if(it.memberPlacementStatus==CommonUtil.isNotConsented){
                drawable.setColor(ContextCompat.getColor(binding.root.context, R.color.very_light_red))
            }
            binding.CommonLayout.lblAvailPlacement.background = drawable
            binding.CommonLayout.lblAvailPlacement.text=it.memberPlacementStatus
//            binding.CommonLayout.lblName.text = it.memberNotificationStatus

            binding.CommonLayout.lblRollno.text = it.memberRegno
            binding.CommonLayout.lblDOB.text = it.memberDob
            binding.CommonLayout.lblGender.text = it.memberGender
            binding.CommonLayout.lblQualification.text = it.courseName
            binding.CommonLayout.lblAddress.text = it.memberPermanentAddress1+it.memberPermanentAddressCity+it.memberPermanentAddressState+it.memberPermanentAddressCountry
            binding.CommonLayout.lblAdmissionNo.text = it.memberAdmissionNo
            binding.CommonLayout.lblDepartmentName.text = it.departmentName
            binding.CommonLayout.lblYearOfStudy.text = it.semesterName
            binding.CommonLayout.lblSemester.text = it.noOfYear.toString()

        }
    }

    fun GetProfileDetails() {
//        appViewModel!!.GetResumeBuilderProfileDetails(CommonUtil.MemberId, this@ResumeBuilder)
        appViewModel!!.GetResumeBuilderProfileDetails(31146, this@ResumeBuilder)
    }


    override val layoutResourceId: Int
        get() = R.layout.activity_resumebuilder


    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }
}