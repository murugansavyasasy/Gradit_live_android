package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.vsca.vsnapvoicecollege.Adapters.AcademicAdapter
import com.vsca.vsnapvoicecollege.Adapters.CertificateAdapter
import com.vsca.vsnapvoicecollege.Adapters.SkillSetAdapter
import com.vsca.vsnapvoicecollege.Model.EducationFormattedData
import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData
import com.vsca.vsnapvoicecollege.Model.GetEducationalDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderSkillSetDetailsData
import com.vsca.vsnapvoicecollege.Model.ResumeContextData
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.fullResumeData
import com.vsca.vsnapvoicecollege.databinding.ActivityBuildmyresumeBinding

class BuildResumeActivity : AppCompatActivity() {

    var isMemeberId = 0

    private lateinit var binding: ActivityBuildmyresumeBinding
    var isSkillSetData: GetResumeBuilderSkillSetDetailsData? = null
    private var selectedAcademicList: List<GetEducationalDetailsData> = emptyList()

    private var languageList: List<String> = emptyList()
    private var softSkillList: List<String> = emptyList()
    private var programmingLanguageList: List<String> = emptyList()
    private lateinit var savedAcademicEducationDetails: List<GetEducationalDetailsData>
    private var toolsPlatformList: List<String> = emptyList()
    private var areaInterestList: List<String> = emptyList()
    private var internshipList: List<String> = emptyList()
    private var projectList: List<String> = emptyList()
    private lateinit var academicAdapter: AcademicAdapter
    private lateinit var languageAdapter: SkillSetAdapter
    private lateinit var softSkillAdapter: SkillSetAdapter


    private val certificateList: List<GetCertificateDetailsData> =
        CommonUtil.isSkillSetDataSending?.certifications ?: emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuildmyresumeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("Name", CommonUtil.saveBasicDetails!!.memberName.toString())

        languageAdapter = SkillSetAdapter(languageList) {
        }
        binding.rvLanguages.layoutManager = LinearLayoutManager(this)
        binding.rvLanguages.adapter = languageAdapter

        softSkillAdapter = SkillSetAdapter(softSkillList) { }
        binding.rvSoftSkills.layoutManager = LinearLayoutManager(this)
        binding.rvSoftSkills.adapter = softSkillAdapter


        // Mark all academic records as checked by default
        val academicData = CommonUtil.saveAcademicDetails
        savedAcademicEducationDetails = academicData?.educationalDetails?.onEach {
            it.isChecked = true
        } ?: emptyList()

        academicAdapter = AcademicAdapter(savedAcademicEducationDetails) { selectedItems ->
            selectedAcademicList = selectedItems

            val allChecked = selectedItems.size == savedAcademicEducationDetails.size
            binding.cbHeaderAcademicRecords.setOnCheckedChangeListener(null)
            binding.cbHeaderAcademicRecords.isChecked = allChecked
            binding.cbHeaderAcademicRecords.setOnCheckedChangeListener { _, isChecked ->
                academicAdapter.setAllChecked(isChecked)
            }
        }

        // Initial header checkbox sync
        binding.cbHeaderAcademicRecords.setOnCheckedChangeListener { _, isChecked ->
            academicAdapter.setAllChecked(isChecked)
        }
        binding.cbHeaderAcademicRecords.setOnCheckedChangeListener(null) // Prevent loop
//        binding.cbHeaderAcademicRecords.isChecked = allChecked
        binding.cbHeaderAcademicRecords.setOnCheckedChangeListener { _, isChecked ->
            academicAdapter.setAllChecked(isChecked)
        }


        binding.rvAcademic.layoutManager = GridLayoutManager(this, 2)
        binding.rvAcademic.adapter = academicAdapter

        savedAcademicEducationDetails = academicData?.educationalDetails ?: emptyList()

        binding.rvCertifications.layoutManager = LinearLayoutManager(this)
        binding.rvCertifications.adapter = CertificateAdapter(certificateList)

        binding.commonBottomResumeBuilder.btnSave.setOnClickListener {
            selectedAcademicList = savedAcademicEducationDetails.filter { it.isChecked }
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
        programmingLanguageList =
            isSkillSetData?.programmingLanguage?.split(",")?.map { it.trim() } ?: emptyList()
        toolsPlatformList =
            isSkillSetData?.toolsPlatform?.split(",")?.map { it.trim() } ?: emptyList()
        areaInterestList =
            isSkillSetData?.areaInterest?.split(",")?.map { it.trim() } ?: emptyList()
        internshipList =
            isSkillSetData?.internship?.mapNotNull { it.companyName?.trim() } ?: emptyList()
        projectList = isSkillSetData?.projects?.mapNotNull { it.title?.trim() } ?: emptyList()

        // Setup each section
        setupRecycler(binding.rvLanguages, languageList, binding.cbHeaderLanguage) {
            languageList = it
        }
        setupRecycler(
            binding.rvSoftSkills,
            softSkillList,
            binding.cbHeaderSoftSkills
        ) { softSkillList = it }
        setupRecycler(
            binding.rvProgrammingLanguages,
            programmingLanguageList,
            binding.cbHeaderProgramming
        ) { programmingLanguageList = it }
        setupRecycler(
            binding.rvToolsPlatforms,
            toolsPlatformList,
            binding.cbHeaderToolsPlatform
        ) { toolsPlatformList = it }
        setupRecycler(
            binding.rvAreasInterest,
            areaInterestList,
            binding.cbHeaderInterestArea
        ) { areaInterestList = it }
        setupRecycler(
            binding.rvInternships,
            internshipList,
            binding.cbHeaderInternship
        ) { internshipList = it }
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

//    private fun isSaveFullResumeData() {
//        val resumeContext = ResumeContextData(
//
//            memberId = isMemeberId.toString(),
//            name = CommonUtil.saveBasicDetails?.memberName ?: "",
//            email = CommonUtil.saveBasicDetails?.memberstudentEmail ?: "",
//            phone = CommonUtil.saveBasicDetails?.memberPhoneNumber ?: "",
//            address = CommonUtil.saveBasicDetails?.memberPermanentAddress1 ?: ",",
//            skills = programmingLanguageList,
//            education = selectedAcademicList,
//            experience = isSkillSetData?.internship,
//            areainterest = areaInterestList,
//            softSkill = softSkillList,
//            certifications = certificateList,
//            languages = languageList,
//            projects = isSkillSetData?.projects
//        )
//        Log.d("ResumeData", "Resume saved: $resumeContext")
//        Log.d("ResumeData", "Saving resume...")
//
//        Log.d("Selected", "Languages: $languageList")
//        Log.d("Selected", "Soft Skills: $softSkillList")
//        Log.d("Selected", "Programming Languages: $programmingLanguageList")
//        Log.d("Selected", "Tools & Platforms: $toolsPlatformList")
//        Log.d("Selected", "Area Interests: $areaInterestList")
//        Log.d("Selected", "Internships: $internshipList")
//        Log.d("Selected", "Projects: $projectList")
//        Log.d("Selected", "Certifications: $certificateList")
//        Log.d("Selected", "Academic Details: $savedAcademicEducationDetails")
//        CommonUtil.fullResumeData = resumeContext
//
//    }

    private fun isSaveFullResumeData() {
        val basicDetails = CommonUtil.saveBasicDetails
        val educationData = CommonUtil.saveAcademicDetails?.educationalDetails ?: emptyList()

        val skillsList = programmingLanguageList + toolsPlatformList

        val formattedEducationList = educationData.map {
            if (it.classDegree.contains("10", true) || it.classDegree.contains("12", true)) {
                EducationFormattedData(
                    `class` = it.classDegree,
                    percentage = it.percentage,
                    institution = it.institution
                )
            } else {
                EducationFormattedData(
                    degree = it.classDegree,
                    percentage = it.percentage,
                    institution = it.institution
                )
            }
        }

        val formattedInternshipList = isSkillSetData?.internship ?: emptyList()

        val formattedProjectList = isSkillSetData?.projects ?: emptyList()

        val resumeContext = ResumeContextData(
            memberId = basicDetails?.memberId ?: "",
            name = basicDetails?.memberName ?: "",
            email = basicDetails?.memberstudentEmail ?: "",
            phone = basicDetails?.memberPhoneNumber ?: "",
            address = basicDetails?.memberPermanentAddress1 ?: "",
            skills = skillsList,
            education = formattedEducationList,
            experience = formattedInternshipList,
            certifications = certificateList,
            projects = formattedProjectList,
            areainterest = areaInterestList,
            softSkill = softSkillList,
            languages = languageList
        )

        fullResumeData = resumeContext
        Log.d("ResumeDebug", "Name: ${basicDetails?.memberName}")
        Log.d("ResumeDebug", "Education Count: ${educationData.size}")
        Log.d("ResumeDebug", "Skills: $skillsList")
        Log.d("ResumeDebug", "Internship: $internshipList")
        Log.d("ResumeDebug", "Projects: $projectList")

        Log.d("FULL_RESUME", Gson().toJson(resumeContext))
    }
}