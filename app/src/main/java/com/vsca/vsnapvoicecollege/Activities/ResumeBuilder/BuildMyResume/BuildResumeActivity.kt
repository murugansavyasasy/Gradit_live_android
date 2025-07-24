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
import com.vsca.vsnapvoicecollege.Adapters.InternshipAdapter
import com.vsca.vsnapvoicecollege.Adapters.SkillSetAdapter
import com.vsca.vsnapvoicecollege.Model.CertificateFormattedData
import com.vsca.vsnapvoicecollege.Model.EducationFormattedData
import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData
import com.vsca.vsnapvoicecollege.Model.GetEducationalDetailsData
import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
import com.vsca.vsnapvoicecollege.Model.InternshipFormattedData
import com.vsca.vsnapvoicecollege.Model.ResumeContextData
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.fullResumeData
import com.vsca.vsnapvoicecollege.databinding.ActivityBuildmyresumeBinding

class BuildResumeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBuildmyresumeBinding
    private var selectedAcademicList: List<GetEducationalDetailsData> = emptyList()
    private lateinit var saveCertificateList: List<GetCertificateDetailsData>
    private var languageList: List<String> = emptyList()
    private var softSkillList: List<String> = emptyList()
    private var programmingLanguageList: List<String> = emptyList()
    private lateinit var savedAcademicEducationDetails: List<GetEducationalDetailsData>
    private lateinit var internshipList: List<GetInternshipDetailsData>
    var selectedinternship: List<GetInternshipDetailsData> = emptyList()
    private var toolsPlatformList: List<String> = emptyList()
    private var areaInterestList: List<String> = emptyList()
    private var projectList: List<String> = emptyList()
    private lateinit var academicAdapter: AcademicAdapter
    private lateinit var intershipAdapter: InternshipAdapter
    private lateinit var languageAdapter: SkillSetAdapter
    private lateinit var softSkillAdapter: SkillSetAdapter
    private var savedCertificates: List<GetCertificateDetailsData> = emptyList()
    private lateinit var certificateAdapter: CertificateAdapter
    var selectedCertificates: List<GetCertificateDetailsData> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuildmyresumeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        savedCertificates = CommonUtil.isSkillSetDataSending?.certifications!!
        internshipList = CommonUtil.isSkillSetDataSending?.internship!!
        languageAdapter = SkillSetAdapter(languageList) {}
        binding.rvLanguages.layoutManager = LinearLayoutManager(this)
        binding.rvLanguages.adapter = languageAdapter
        softSkillAdapter = SkillSetAdapter(softSkillList) { }
        binding.rvSoftSkills.layoutManager = LinearLayoutManager(this)
        binding.rvSoftSkills.adapter = softSkillAdapter
        val academicData = CommonUtil.saveAcademicDetails
        savedAcademicEducationDetails = academicData?.educationalDetails?.onEach {
            it.isChecked = true
        } ?: emptyList()
        savedCertificates = CommonUtil.isSkillSetDataSending?.certifications?.onEach {
            it.isChecked = true
        } ?: emptyList()

        internshipList = CommonUtil.isSkillSetDataSending?.internship?.onEach {
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

        certificateAdapter = CertificateAdapter(savedCertificates) { isSelectedCertifcate ->
            selectedCertificates = isSelectedCertifcate
            val allChecked = isSelectedCertifcate.size == savedCertificates.size
            binding.cbHeaderCertificates.setOnCheckedChangeListener(null)
            binding.cbHeaderCertificates.isChecked = allChecked
            binding.cbHeaderCertificates.setOnCheckedChangeListener { _, isChecked ->
                certificateAdapter.setAllChecked(isChecked)
            }
        }

        intershipAdapter = InternshipAdapter(internshipList) { isSelectedIntership ->
            selectedinternship = isSelectedIntership
            val allChecked = isSelectedIntership.size == internshipList.size
            binding.cbHeaderInternship.setOnCheckedChangeListener(null)
            binding.cbHeaderInternship.isChecked = allChecked
            binding.cbHeaderInternship.setOnCheckedChangeListener { _, isChecked ->
                intershipAdapter.setAllChecked(isChecked)
            }
        }

        binding.rvInternships.layoutManager = LinearLayoutManager(this)
        binding.rvInternships.adapter = intershipAdapter

        binding.cbHeaderInternship.setOnCheckedChangeListener { _, isChecked ->
            intershipAdapter.setAllChecked(isChecked)
        }

        binding.cbHeaderAcademicRecords.setOnCheckedChangeListener { _, isChecked ->
            academicAdapter.setAllChecked(isChecked)
        }

        binding.rvAcademic.layoutManager = GridLayoutManager(this, 2)
        binding.rvAcademic.adapter = academicAdapter

        savedAcademicEducationDetails = academicData?.educationalDetails ?: emptyList()

        binding.rvCertifications.layoutManager = LinearLayoutManager(this)
        binding.rvCertifications.adapter = certificateAdapter

        binding.cbHeaderCertificates.setOnCheckedChangeListener { _, isChecked ->
            certificateAdapter.setAllChecked(isChecked)
        }

        saveCertificateList = CommonUtil.isSkillSetDataSending?.certifications ?: emptyList()

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

        languageList = CommonUtil.isSkillSetDataSending?.languages?.split(",")?.map { it.trim() }
            ?.filter { it.isNotBlank() } ?: emptyList()
        softSkillList = CommonUtil.isSkillSetDataSending?.softSkill?.split(",")?.map { it.trim() }
            ?.filter { it.isNotBlank() } ?: emptyList()
        programmingLanguageList =
            CommonUtil.isSkillSetDataSending?.programmingLanguage?.split(",")?.map { it.trim() }
                ?.filter { it.isNotBlank() } ?: emptyList()
        toolsPlatformList =
            CommonUtil.isSkillSetDataSending?.toolsPlatform?.split(",")?.map { it.trim() }
                ?.filter { it.isNotBlank() } ?: emptyList()
        areaInterestList =
            CommonUtil.isSkillSetDataSending?.areaInterest?.split(",")?.map { it.trim() }
                ?.filter { it.isNotBlank() } ?: emptyList()


        projectList = CommonUtil.isSkillSetDataSending?.projects?.mapNotNull { it.title?.trim() }
            ?: emptyList()

        setupSectionVisibility()

        setupRecycler(
            binding.rvLanguages, languageList, binding.cbHeaderLanguage, binding.layoutLanguage) {
            languageList = it
        }
        setupRecycler(binding.rvSoftSkills, softSkillList, binding.cbHeaderSoftSkills, binding.layoutSoftSkills) {
            softSkillList = it
        }
        setupRecycler(binding.rvAreasInterest, areaInterestList, binding.cbHeaderInterestArea, binding.layoutInterestArea) {
            areaInterestList = it

        }
        setupRecycler(binding.rvProjects, projectList, binding.cbHeaderProject, binding.layoutProject) {
            projectList = it
        }
    }

    private fun setupSectionVisibility() {
        // Languages
        if (CommonUtil.isSkillSetDataSending!!.languages.isNullOrEmpty()) {
            binding.layoutLanguage.visibility = View.GONE
            binding.cbHeaderLanguage.isChecked = false

        } else {
            binding.layoutLanguage.visibility = View.VISIBLE
        }

        // Soft Skills
        if (softSkillList.isNullOrEmpty()) {
            binding.layoutSoftSkills.visibility = View.GONE
        } else {
            binding.layoutSoftSkills.visibility = View.VISIBLE
        }
        // Area of Interest
        if (areaInterestList.isNullOrEmpty()) {
            binding.layoutInterestArea.visibility = View.GONE
        } else {
            binding.layoutInterestArea.visibility = View.VISIBLE
        }
        // Projects
        if (projectList.isNullOrEmpty()) {
            binding.layoutProject.visibility = View.GONE
        } else {
            binding.layoutProject.visibility = View.VISIBLE
        }
        // Internship / Experience
        if (internshipList.isNullOrEmpty()) {
            binding.layoutInternship.visibility = View.GONE
        } else {
            binding.layoutInternship.visibility = View.VISIBLE
        }
    }

    private fun setupRecycler(
        recyclerView: RecyclerView,
        items: List<String>,
        headerCheckbox: CheckBox,
        sectionLayout: View,
        storeList: (List<String>) -> Unit
    ) {
        if (items.isEmpty()) {
            sectionLayout.visibility = View.GONE
            headerCheckbox.setOnCheckedChangeListener(null)
            headerCheckbox.isChecked = false
            headerCheckbox.isEnabled = false
            recyclerView.visibility = View.GONE
            storeList(emptyList())
            return
        }

        sectionLayout.visibility = View.VISIBLE
        recyclerView.visibility = View.VISIBLE
        headerCheckbox.isEnabled = true

        lateinit var adapter: SkillSetAdapter

        adapter = SkillSetAdapter(items) { selectedItems ->
            storeList(selectedItems)
            val allChecked = selectedItems.size == items.size
            headerCheckbox.setOnCheckedChangeListener(null)
            headerCheckbox.isChecked = allChecked
            headerCheckbox.setOnCheckedChangeListener { _, isChecked ->
                adapter.setAllChecked(isChecked)
                storeList(if (isChecked) items else emptyList())
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter

        headerCheckbox.setOnCheckedChangeListener(null)
        headerCheckbox.isChecked = true //  All checked initially
        headerCheckbox.setOnCheckedChangeListener { _, isChecked ->
            adapter.setAllChecked(isChecked)
            storeList(if (isChecked) items else emptyList())
        }

        storeList(items) // store all by default
    }

    private fun isSaveFullResumeData() {

    val selectedAcademicList = selectedAcademicList.filter { it.isChecked }?.map {
                    EducationFormattedData(
                        percentage = it.percentage,
                        classDegree = it.classDegree,
                        institution = it.institution
                    )
                } ?: emptyList()

        val selectedCertificates =
            saveCertificateList?.filter { it.isChecked }?.map {
                    CertificateFormattedData(
                        certificateName = it.courseName,
                        institute = it.institute,
                        duration = it.duration
                    )
                } ?: emptyList()

        val selectedInternshipList =
            internshipList.filter { it.isChecked }.map {
                InternshipFormattedData(
                    companyName = it.companyName,
                    from = it.from,
                    designation = it.designation,
                    to = it.to
                )
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

            skills = programmingLanguageList,
            education = selectedAcademicList,
            experience = selectedInternshipList,
            areainterest = areaInterestList,
            softSkill = softSkillList,
            certifications = selectedCertificates,
            languages = languageList,
            projects = projectList
        )
        fullResumeData = resumeContext
        val gson = Gson()
        Log.d("RESUME_JSON", gson.toJson(resumeContext))
    }
}
