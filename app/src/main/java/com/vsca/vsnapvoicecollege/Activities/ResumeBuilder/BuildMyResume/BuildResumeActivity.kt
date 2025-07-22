package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.saveAcademicDetails
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

    //    private var selectedCertificates: List<GetCertificateDetailsData> = emptyList()
    private lateinit var certificateAdapter: CertificateAdapter
    private val selectedCertificatesMutable = mutableListOf<GetCertificateDetailsData>()
    var selectedCertificates: List<GetCertificateDetailsData> = emptyList()

    val certificateList: List<GetCertificateDetailsData> =
        CommonUtil.isSkillSetDataSending?.certifications
            ?.map { it.copy(isChecked = true) }
            ?: emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuildmyresumeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateHeaderCheckboxStates()
        setupSectionVisibility()

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

//        // Initial header checkbox sync
//        binding.cbHeaderAcademicRecords.setOnCheckedChangeListener { _, isChecked ->
//            academicAdapter.setAllChecked(isChecked)
//        }
//        binding.cbHeaderAcademicRecords.setOnCheckedChangeListener(null) // Prevent loop
//        binding.cbHeaderAcademicRecords.isChecked = allChecked
        binding.cbHeaderAcademicRecords.setOnCheckedChangeListener { _, isChecked ->
            academicAdapter.setAllChecked(isChecked)
        }

        if (savedAcademicEducationDetails.size>0){

        }else{

        }

        binding.rvAcademic.layoutManager = GridLayoutManager(this, 2)
        binding.rvAcademic.adapter = academicAdapter

        savedAcademicEducationDetails = academicData?.educationalDetails ?: emptyList()

        val selectedCertificatesMutable = certificateList.toMutableList()

        val certificateAdapter = CertificateAdapter(
            list = certificateList,
            onCheckedChange = { item, isChecked ->
                if (isChecked) {
                    selectedCertificatesMutable.add(item)
                } else {
                    selectedCertificatesMutable.remove(item)
                }
            },
            onHeaderCheckSync = { allChecked ->
                binding.cbHeaderCertificates.setOnCheckedChangeListener(null)
                binding.cbHeaderCertificates.isChecked = allChecked
                binding.cbHeaderCertificates.setOnCheckedChangeListener { _, isChecked ->
                    certificateAdapter.setAllChecked(isChecked)
                }
            }
        )

        binding.rvCertifications.layoutManager = LinearLayoutManager(this)
        binding.rvCertifications.adapter = certificateAdapter

        binding.cbHeaderCertificates.setOnCheckedChangeListener { _, isChecked ->
            certificateAdapter.setAllChecked(isChecked)
        }

        binding.commonBottomResumeBuilder.btnSave.setOnClickListener {
            selectedAcademicList = savedAcademicEducationDetails.filter { it.isChecked }
            selectedCertificates = selectedCertificatesMutable.toList()
            Log.d("CertDebug", "Selected Certificates: ${Gson().toJson(selectedCertificates)}")
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


        setupSectionVisibility()

        // Setup each section
        setupRecycler(binding.rvLanguages, languageList, binding.cbHeaderLanguage) {
            languageList = it
        }
//        val softSkillList = parseJsonListString(CommonUtil.isSkillSetDataSending?.softSkill)
//        setupRecycler(
//            recyclerView = binding.rvSoftSkills,
//            items = softSkillList,
//            headerCheckbox = binding.cbHeaderSoftSkills
//        ) {
//            selectedSoftSkills = it
//        }
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
//        setupRecycler(
//            binding.rvToolsPlatforms,
//            toolsPlatformList,
//            binding.cbHeaderToolsPlatform
//        ) { toolsPlatformList = it }
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


    private fun updateHeaderCheckboxStates() {
        val skillData = CommonUtil.isSkillSetDataSending

        // String fields parsed to list
        val languageList = parseJsonListString(skillData?.languages)
        val softSkillList = parseJsonListString(skillData?.softSkill)
        val interestList = parseJsonListString(skillData?.areaInterest)
        val programmingList = parseJsonListString(skillData?.programmingLanguage)
        val toolsList = parseJsonListString(skillData?.toolsPlatform)

        // Uncheck if list is empty
        binding.cbHeaderLanguage.isChecked = languageList.isNotEmpty()
        binding.cbHeaderSoftSkills.isChecked = softSkillList.isNotEmpty()
        binding.cbHeaderInterestArea.isChecked = interestList.isNotEmpty()
        binding.cbHeaderProgramming.isChecked = programmingList.isNotEmpty()

        // For list data
        binding.cbHeaderInternship.isChecked = !skillData?.internship.isNullOrEmpty()
        binding.cbHeaderCertificates.isChecked = !skillData?.certifications.isNullOrEmpty()
        binding.cbHeaderProject.isChecked = !skillData?.projects.isNullOrEmpty()

        // Academic from another object
        binding.cbHeaderAcademicRecords.isChecked =
            !CommonUtil.saveAcademicDetails?.educationalDetails.isNullOrEmpty()
    }


    fun parseJsonListString(json: String?): List<String> {
        return try {
            if (json.isNullOrBlank()) emptyList()
            else Gson().fromJson(json, Array<String>::class.java).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }


    private fun setupSectionVisibility() {
        // Languages
        if (CommonUtil.isSkillSetDataSending?.languages.isNullOrEmpty()) {
            binding.cbHeaderLanguage.visibility = View.GONE
            binding.rvLanguages.visibility = View.GONE
            binding.languageView.visibility = View.GONE
        } else {
            binding.cbHeaderLanguage.visibility = View.VISIBLE
            binding.rvLanguages.visibility = View.VISIBLE
            binding.languageView.visibility = View.VISIBLE
        }

        // Soft Skills
        val isSoftSkillEmpty = CommonUtil.isSkillSetDataSending?.softSkill
            ?.split(",")
            ?.map { it.trim() }
            ?.all { it.isEmpty() } ?: true

        if (isSoftSkillEmpty) {
            binding.cbHeaderSoftSkills.visibility = View.GONE
            binding.rvSoftSkills.visibility = View.GONE
            binding.softSkillView.visibility = View.GONE
        } else {
            binding.cbHeaderSoftSkills.visibility = View.VISIBLE
            binding.rvSoftSkills.visibility = View.VISIBLE
            binding.softSkillView.visibility = View.VISIBLE
        }


        // Area of Interest
        if (CommonUtil.isSkillSetDataSending?.areaInterest.isNullOrEmpty()) {
            binding.cbHeaderInterestArea.visibility = View.GONE
            binding.rvAreasInterest.visibility = View.GONE
            binding.areasinterestView.visibility = View.GONE
        } else {
            binding.cbHeaderInterestArea.visibility = View.VISIBLE
            binding.rvAreasInterest.visibility = View.VISIBLE
            binding.areasinterestView.visibility = View.VISIBLE
        }

        // Programming Languages
        if (CommonUtil.isSkillSetDataSending?.programmingLanguage.isNullOrEmpty()) {
            binding.cbHeaderProgramming.visibility = View.GONE
            binding.rvProgrammingLanguages.visibility = View.GONE
            binding.programmingView.visibility = View.GONE
        } else {
            binding.cbHeaderProgramming.visibility = View.VISIBLE
            binding.rvProgrammingLanguages.visibility = View.VISIBLE
            binding.programmingView.visibility = View.VISIBLE
        }

        // Certifications
        CommonUtil.isSkillSetDataSending?.certifications?.let {
            if (it.isNotEmpty()) {
                binding.cbHeaderCertificates.visibility = View.VISIBLE
                binding.rvCertifications.visibility = View.VISIBLE
            } else {
                binding.cbHeaderCertificates.visibility = View.GONE
                binding.rvCertifications.visibility = View.GONE
            }
        }

        // Projects
        val isProjectListEmpty = CommonUtil.isSkillSetDataSending?.projects
            ?.all { it.title.isNullOrEmpty() } ?: true

        if (isProjectListEmpty) {
            binding.cbHeaderProject.visibility = View.GONE
            binding.rvProjects.visibility = View.GONE
        } else {
            binding.cbHeaderProject.visibility = View.VISIBLE
            binding.rvProjects.visibility = View.VISIBLE
        }


        // Internship / Experience
        val isInternshipListEmpty = CommonUtil.isSkillSetDataSending?.internship
            ?.all { it.companyName.isNullOrEmpty() && it.designation.isNullOrEmpty() } ?: true

        if (isInternshipListEmpty) {
            binding.cbHeaderInternship.visibility = View.GONE
            binding.rvInternships.visibility = View.GONE
        } else {
            binding.cbHeaderInternship.visibility = View.VISIBLE
            binding.rvInternships.visibility = View.VISIBLE
        }

    }

    private fun setupRecycler(
        recyclerView: RecyclerView,
        items: List<String>,
        headerCheckbox: CheckBox,
        storeList: (List<String>) -> Unit
    ): SkillSetAdapter {

        lateinit var skillSetAdapter: SkillSetAdapter

        // If items are empty → uncheck header and hide section
        if (items.isEmpty()) {
            headerCheckbox.setOnCheckedChangeListener(null)
            headerCheckbox.isChecked = false
            storeList(emptyList())

            // Optional: hide section or disable checkbox
            recyclerView.visibility = View.GONE
            headerCheckbox.isEnabled = false

            return SkillSetAdapter(emptyList()) { storeList(emptyList()) }
        }

        //  Items are not empty → show section and bind adapter
        recyclerView.visibility = View.VISIBLE
        headerCheckbox.isEnabled = true

        skillSetAdapter = SkillSetAdapter(items) { selectedItems ->
            val allChecked = selectedItems.size == items.size

            // Sync header checkbox with children
            headerCheckbox.setOnCheckedChangeListener(null)
            headerCheckbox.isChecked = allChecked
            headerCheckbox.setOnCheckedChangeListener { _, isChecked ->
                skillSetAdapter.setAllChecked(isChecked)
                storeList(if (isChecked) skillSetAdapter.getSelectedItems() else emptyList())
            }

            // Save only if header is checked
            if (headerCheckbox.isChecked) {
                storeList(selectedItems)
            } else {
                storeList(emptyList())
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = skillSetAdapter

        // Set all checked initially
        headerCheckbox.setOnCheckedChangeListener(null)
        headerCheckbox.isChecked = true
        skillSetAdapter.setAllChecked(true)
        storeList(items)

        // Handle header checkbox toggle
        headerCheckbox.setOnCheckedChangeListener { _, isChecked ->
            skillSetAdapter.setAllChecked(isChecked)
            storeList(if (isChecked) skillSetAdapter.getSelectedItems() else emptyList())
        }

        return skillSetAdapter
    }

    private fun isSaveFullResumeData() {
        val selectedAcademicList = if (binding.cbHeaderAcademicRecords.isChecked) {
            saveAcademicDetails?.educationalDetails
                ?.filter { it.isChecked }
                ?.map {
                    EducationFormattedData(
                        percentage = it.percentage,
                        classDegree = it.classDegree,
                        institution = it.institution
                    )
                } ?: emptyList()
        } else {
            emptyList()
        }

        val resumeContext = ResumeContextData(
            idMember = CommonUtil.saveBasicDetails?.memberId ?: "",
            name = CommonUtil.saveBasicDetails?.memberName ?: "",
            email = CommonUtil.saveBasicDetails?.memberstudentEmail ?: "",
            phone = CommonUtil.saveBasicDetails?.memberPhoneNumber ?: "",
            address = listOfNotNull(
                CommonUtil.saveBasicDetails?.memberPermanentAddress1,
                CommonUtil.saveBasicDetails?.memberPermanentAddressCity,
                CommonUtil.saveBasicDetails?.memberPermanentAddressState,
                CommonUtil.saveBasicDetails?.memberPermanentAddressPincode,
                CommonUtil.saveBasicDetails?.memberPermanentAddressCountry
            ).joinToString(", "),

            // Only include section data if header checkbox is checked
            skills = if (binding.cbHeaderProgramming.isChecked) programmingLanguageList else emptyList(),
            education = selectedAcademicList,
            experience = if (binding.cbHeaderInternship.isChecked) isSkillSetData?.internship
                ?: emptyList() else emptyList(),
            areainterest = if (binding.cbHeaderInterestArea.isChecked) areaInterestList else emptyList(),
            softSkill = if (binding.cbHeaderSoftSkills.isChecked) softSkillList else emptyList(),
            certifications = if (binding.cbHeaderCertificates.isChecked) selectedCertificates else emptyList(),
            languages = if (binding.cbHeaderLanguage.isChecked) languageList else emptyList(),
            projects = if (binding.cbHeaderProject.isChecked) isSkillSetData?.projects
                ?: emptyList() else emptyList()
        )

        // Logs
        Log.d("ResumeData", "===== Saving Resume Context =====")
        Log.d("ResumeData", "Member ID: ${resumeContext.idMember}")
        Log.d("ResumeData", "Name: ${resumeContext.name}")
        Log.d("ResumeData", "Email: ${resumeContext.email}")
        Log.d("ResumeData", "Phone: ${resumeContext.phone}")
        Log.d("ResumeData", "Address: ${resumeContext.address}")
        Log.d("ResumeData", "Education: ${resumeContext.education}")
        Log.d("ResumeData", "Skills: ${resumeContext.skills}")
        Log.d("ResumeData", "Experience: ${resumeContext.experience}")
        Log.d("ResumeData", "Area of Interest: ${resumeContext.areainterest}")
        Log.d("ResumeData", "Soft Skills: ${resumeContext.softSkill}")
        Log.d("ResumeData", "Languages: ${resumeContext.languages}")
        Log.d("ResumeData", "Certifications: ${resumeContext.certifications}")
        Log.d("ResumeData", "Projects: ${resumeContext.projects}")
        Log.d("ResumeData", "=================================")

        fullResumeData = resumeContext

        val gson = Gson()
        Log.d("RESUME_JSON", gson.toJson(resumeContext))
    }


//~ 2 backup
//    private fun isSaveFullResumeData() {
//        selectedCertificates = selectedCertificatesMutable.filter { true }
//
//        val selectedAcademicList = saveAcademicDetails?.educationalDetails
//            ?.filter { it.isChecked }
//            ?.map {
//                EducationFormattedData(
//                    percentage = it.percentage,
//                    classDegree = it.classDegree,
//                    institution = it.institution
//                )
//            } ?: emptyList()
//
//        val resumeContext = ResumeContextData(
//            idMember = CommonUtil.saveBasicDetails?.memberId ?: "",
//            name = CommonUtil.saveBasicDetails?.memberName ?: "",
//            email = CommonUtil.saveBasicDetails?.memberstudentEmail ?: "",
//            phone = CommonUtil.saveBasicDetails?.memberPhoneNumber ?: "",
//            address = listOfNotNull(
//                CommonUtil.saveBasicDetails?.memberPermanentAddress1,
//                CommonUtil.saveBasicDetails?.memberPermanentAddressCity,
//                CommonUtil.saveBasicDetails?.memberPermanentAddressState,
//                CommonUtil.saveBasicDetails?.memberPermanentAddressPincode,
//                CommonUtil.saveBasicDetails?.memberPermanentAddressCountry
//            ).joinToString(", "),
//            skills = programmingLanguageList,
//            education = selectedAcademicList,
//            experience = isSkillSetData?.internship,
//            areainterest = areaInterestList,
//            softSkill = softSkillList,
//            certifications = selectedCertificates,
////            certifications = isSkillSetData?.certifications,
//            languages = languageList,
//            projects = isSkillSetData?.projects
//        )
//        // Logs
//        Log.d("ResumeData", "===== Saving Resume Context =====")
//        Log.d("ResumeData", "Member ID: ${resumeContext.idMember}")
//        Log.d("ResumeData", "Name: ${resumeContext.name}")
//        Log.d("ResumeData", "Email: ${resumeContext.email}")
//        Log.d("ResumeData", "Phone: ${resumeContext.phone}")
//        Log.d("ResumeData", "Address: ${resumeContext.address}")
//        Log.d("ResumeData", "Address: ${resumeContext.education}")
//        Log.d("ResumeData", "Skills: ${resumeContext.skills}")
//        Log.d("ResumeData", "Experience: ${resumeContext.experience}")
//        Log.d("ResumeData", "Area of Interest: ${resumeContext.areainterest}")
//        Log.d("ResumeData", "Soft Skills: ${resumeContext.softSkill}")
//        Log.d("ResumeData", "Languages: ${resumeContext.languages}")
//        Log.d("ResumeData", "Certifications: ${resumeContext.certifications}")
//        Log.d("ResumeData", "Projects: ${resumeContext.projects}")
//        Log.d("FullResume", "Saved Full Resume Data: ${Gson().toJson(resumeContext)}")
//        Log.d("ResumeData", "=================================")
//
//        fullResumeData = resumeContext
//        val gson = Gson()
//        Log.d("RESUME_JSON", gson.toJson(resumeContext))
//
//    }

//    private fun isSaveFullResumeData() {
//        val basicDetails = CommonUtil.saveBasicDetails
//        val educationData = CommonUtil.saveAcademicDetails?.educationalDetails ?: emptyList()
//
//        val skillsList = programmingLanguageList + toolsPlatformList
//
//        val formattedEducationList = educationData.map {
//            if (it.classDegree.contains("10", true) || it.classDegree.contains("12", true)) {
//                EducationFormattedData(
//                    `class` = it.classDegree,
//                    percentage = it.percentage,
//                    institution = it.institution
//                )
//            } else {
//                EducationFormattedData(
//                    degree = it.classDegree,
//                    percentage = it.percentage,
//                    institution = it.institution
//                )
//            }
//        }
//
//        val formattedInternshipList = isSkillSetData?.internship ?: emptyList()
//        val formattedProjectList = isSkillSetData?.projects ?: emptyList()
//
//        // Combine address fields into a single string
//        val fullAddress = listOfNotNull(
//            basicDetails?.memberPermanentAddress1,
//            basicDetails?.memberPermanentAddressCity,
//            basicDetails?.memberPermanentAddressState,
//            basicDetails?.memberPermanentAddressPincode,
//            basicDetails?.memberPermanentAddressCountry
//        ).joinToString(", ")
//
//        val resumeContext = ResumeContextData(
//            memberId = basicDetails?.memberId ?: "",
//            name = basicDetails?.memberName ?: "",
//            email = basicDetails?.memberstudentEmail ?: "",
//            phone = basicDetails?.memberPhoneNumber ?: "",
//            address = fullAddress,
//            skills = skillsList,
//            education = formattedEducationList,
//            experience = formattedInternshipList,
//            certifications = selectedCertificates,
//            projects = formattedProjectList,
//            areainterest = areaInterestList,
//            softSkill = softSkillList,
//            languages = languageList
//        )
//
//        fullResumeData = resumeContext
//        Log.d("ResumeDebug", "Name: ${basicDetails?.memberName}")
//        Log.d("ResumeDebug", "Education Count: ${educationData.size}")
//        Log.d("ResumeDebug", "Skills: $skillsList")
//        Log.d("ResumeDebug", "Internship: $internshipList")
//        Log.d("ResumeDebug", "Projects: $projectList")
//        Log.d("FULL_RESUME", Gson().toJson(resumeContext))
//    }
}