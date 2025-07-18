package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Adapters.AcademicAdapter
import com.vsca.vsnapvoicecollege.Adapters.CertificateAdapter
import com.vsca.vsnapvoicecollege.Adapters.SkillSetAdapter
import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData
import com.vsca.vsnapvoicecollege.Model.GetEducationalDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderSkillSetDetailsData
import com.vsca.vsnapvoicecollege.Model.ResumeContextData
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.databinding.ActivityBuildmyresumeBinding

class BuildResumeActivity : AppCompatActivity() {

    var isMemeberId = 0

    private lateinit var binding: ActivityBuildmyresumeBinding
    var isSkillSetData: GetResumeBuilderSkillSetDetailsData? = null

    private var languageList: List<String> = emptyList()
    private var softSkillList: List<String> = emptyList()
    private var programmingLanguageList: List<String> = emptyList()
    private lateinit var savedAcademicEducationDetails: List<GetEducationalDetailsData>
    private var toolsPlatformList: List<String> = emptyList()
    private var areaInterestList: List<String> = emptyList()
    private var internshipList: List<String> = emptyList()
    private var projectList: List<String> = emptyList()

    private val certificateList: List<GetCertificateDetailsData> =
        CommonUtil.isSkillSetDataSending?.certifications ?: emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuildmyresumeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val academicData = CommonUtil.saveAcademicDetails
        savedAcademicEducationDetails = academicData?.educationalDetails ?: emptyList()

        binding.rvAcademic.layoutManager = LinearLayoutManager(this)
        binding.rvAcademic.adapter = AcademicAdapter(savedAcademicEducationDetails)

        binding.rvCertifications.layoutManager = LinearLayoutManager(this)
        binding.rvCertifications.adapter = CertificateAdapter(certificateList)

        binding.commonBottomResumeBuilder.btnSave.setOnClickListener {
            isSaveFullResumeData()
            val intent = Intent(this, BuildMyResume::class.java)
            startActivity(intent)
        }

        binding.imgback.setOnClickListener {
            onBackPressed()
        }

        binding.commonBottomResumeBuilder.btnDefault1.setOnClickListener {
            finish()
        }

        // Load data
        isSkillSetData = CommonUtil.isSkillSetDataSending

        languageList = isSkillSetData?.languages?.split(",")?.map { it.trim() } ?: emptyList()
        softSkillList = isSkillSetData?.softSkill?.split(",")?.map { it.trim() } ?: emptyList()
        programmingLanguageList = isSkillSetData?.programmingLanguage?.split(",")?.map { it.trim() } ?: emptyList()
        toolsPlatformList = isSkillSetData?.toolsPlatform?.split(",")?.map { it.trim() } ?: emptyList()
        areaInterestList = isSkillSetData?.areaInterest?.split(",")?.map { it.trim() } ?: emptyList()
        internshipList = isSkillSetData?.internship?.mapNotNull { it.companyName?.trim() } ?: emptyList()
        projectList = isSkillSetData?.projects?.mapNotNull { it.title?.trim() } ?: emptyList()

        // Setup each section
        setupRecycler(binding.rvLanguages, languageList, binding.cbHeaderLanguage) { languageList = it }
        setupRecycler(binding.rvSoftSkills, softSkillList, binding.cbHeaderSoftSkills) { softSkillList = it }
        setupRecycler(binding.rvProgrammingLanguages, programmingLanguageList, binding.cbHeaderProgramming) { programmingLanguageList = it }
        setupRecycler(binding.rvToolsPlatforms, toolsPlatformList, binding.cbHeaderToolsPlatform) { toolsPlatformList = it }
        setupRecycler(binding.rvAreasInterest, areaInterestList, binding.cbHeaderInterestArea) { areaInterestList = it }
        setupRecycler(binding.rvInternships, internshipList, binding.cbHeaderInternship) { internshipList = it }
        setupRecycler(binding.rvProjects, projectList, binding.cbHeaderProject) { projectList = it }
    }

    private fun setupRecycler(
        recyclerView: RecyclerView,
        items: List<String>,
        headerCheckbox: CheckBox,
        storeList: (List<String>) -> Unit
    ) {
        lateinit var adapter: SkillSetAdapter

        adapter = SkillSetAdapter(items) { selectedItems ->
            val allChecked = selectedItems.size == items.size
            storeList(selectedItems)
            headerCheckbox.setOnCheckedChangeListener(null)
            headerCheckbox.isChecked = allChecked
            headerCheckbox.setOnCheckedChangeListener { _, isChecked ->
                adapter.setAllChecked(isChecked)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        headerCheckbox.setOnCheckedChangeListener { _, isChecked ->
            adapter.setAllChecked(isChecked)
        }


    recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        headerCheckbox.setOnCheckedChangeListener { _, isChecked ->
            adapter.setAllChecked(isChecked)
        }
    }

    private fun isSaveFullResumeData() {
        val resumeContext = ResumeContextData(
            memberId = isMemeberId.toString(),
            memberName = CommonUtil.saveBasicDetails?.memberName ?: "",
            memberstudentEmail = CommonUtil.saveBasicDetails?.memberstudentEmail ?: "",
            memberPhoneNumber = CommonUtil.saveBasicDetails?.memberPhoneNumber ?: "",
            memberPermanentAddress1 = CommonUtil.saveBasicDetails?.memberPermanentAddress1 ?: ",",
            skills = programmingLanguageList,
            education = savedAcademicEducationDetails,
            internship = isSkillSetData?.internship,
            areaInterest = areaInterestList,
            softSkill = softSkillList,
            certifications = certificateList,
            languages = languageList,
            projects = isSkillSetData?.projects
        )

        CommonUtil.fullResumeData = resumeContext
        Log.d("ResumeData", "Resume saved: $resumeContext")
    }
}
