package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderEditAsssessmentDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderEditCertificateDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderEditInternshipDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderEditProjectDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderSoftSkillsAdapter
import com.vsca.vsnapvoicecollege.Interfaces.OnSoftSkillSelectedListener
import com.vsca.vsnapvoicecollege.Model.GetAssessmentDetailsData
import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData
import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
import com.vsca.vsnapvoicecollege.Model.GetProjectDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderSkillSetSoftSkillsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutEditskillsetBinding

class EditSkillSet : AppCompatActivity(),OnSoftSkillSelectedListener {

    var appViewModel: App? = null
    private lateinit var binding:LayoutEditskillsetBinding
    var savedSoftSkillsList: List<String> = emptyList()
    var selectedSoftSkillsList: List<String> = emptyList()
    var savedLanguage=""
    var savedAreaOfInterest=""
    var savedProgrammmingLanguage=""
    var savedToolsAndPlatform=""
    var savedMemberID=""
    private lateinit var internshipAdapter: ResumeBuilderEditInternshipDetailsAdapter
    private lateinit var savedInternshipList: List<GetInternshipDetailsData>
    private lateinit var editableInternshipList: MutableList<GetInternshipDetailsData>
    private var selectedInternshipList: List<GetInternshipDetailsData> = emptyList()
    private lateinit var certificateAdapter: ResumeBuilderEditCertificateDetailsAdapter
    private lateinit var softSkillsAdapter: ResumeBuilderSoftSkillsAdapter
    private lateinit var assessmentAdapter: ResumeBuilderEditAsssessmentDetailsAdapter
    private lateinit var projectAdapter: ResumeBuilderEditProjectDetailsAdapter
    private lateinit var savedCertificateList: List<GetCertificateDetailsData>
    private lateinit var editableCertificateList: MutableList<GetCertificateDetailsData>
    private var selectedCertificateList: List<GetCertificateDetailsData> = emptyList()
    private lateinit var savedAssessmentList: List<GetAssessmentDetailsData>
    private lateinit var editableAssessmentList: MutableList<GetAssessmentDetailsData>
    private var selectedAssessmentList: List<GetAssessmentDetailsData> = emptyList()
    private lateinit var savedProjectList: List<GetProjectDetailsData>
    private lateinit var editableProjectList: MutableList<GetProjectDetailsData>
    private var selectedProjectList: List<GetProjectDetailsData> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutEditskillsetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        binding.commonBottomResumeBuilder.imgDefault.visibility= View.GONE
        savedSoftSkillsList = CommonUtil.isSkillSetDataSending?.softSkill?.split(",")?.map { it.trim() } ?: emptyList()
        savedInternshipList = CommonUtil.isSkillSetDataSending?.internship ?: emptyList()
        savedCertificateList = CommonUtil.isSkillSetDataSending?.certifications ?: emptyList()
        savedAssessmentList = CommonUtil.isSkillSetDataSending?.assessmentDetails ?: emptyList()
        savedProjectList = CommonUtil.isSkillSetDataSending?.projects ?: emptyList()
        savedLanguage= CommonUtil.isSkillSetDataSending?.languages.toString()
        savedToolsAndPlatform=CommonUtil.isSkillSetDataSending?.toolsPlatform.toString()
        savedProgrammmingLanguage=CommonUtil.isSkillSetDataSending?.programmingLanguage.toString()
        savedAreaOfInterest=CommonUtil.isSkillSetDataSending?.areaInterest.toString()
        savedMemberID=CommonUtil.isSkillSetDataSending?.id.toString()
        Log.d("savedMemberID",savedMemberID)

        GetSoftSkillsDetails()
        binding.edtLanguageknown.setText(CommonUtil.isSkillSetDataSending?.languages)
        binding.edtAreaOfInterest.setText(CommonUtil.isSkillSetDataSending?.areaInterest)
        binding.edtProgrammingLanguage.setText(CommonUtil.isSkillSetDataSending?.programmingLanguage)
        binding.edtToolsAndPlatform.setText(CommonUtil.isSkillSetDataSending?.toolsPlatform)
        editableInternshipList = savedInternshipList.map { it.copy() }.toMutableList()
        editableCertificateList = savedCertificateList.map { it.copy() }.toMutableList()
        editableAssessmentList = savedAssessmentList.map { it.copy() }.toMutableList()
        editableProjectList = savedProjectList.map { it.copy() }.toMutableList()

        //Intership Adapter
        internshipAdapter = ResumeBuilderEditInternshipDetailsAdapter(editableInternshipList.toMutableList(), this)
        binding.rcInternshipExperiences.layoutManager = LinearLayoutManager(this)
        binding.rcInternshipExperiences.isNestedScrollingEnabled = false
        binding.rcInternshipExperiences.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.rcInternshipExperiences.adapter = internshipAdapter

        // Add an empty item only if list is empty or has 0/1 item
        if (editableInternshipList.size <= 0) {
            internshipAdapter.addItem()
        }
        binding.lblAddAnotherInternship.setOnClickListener {
            if (internshipAdapter.showValidationErrors(binding.rcInternshipExperiences)) {
                internshipAdapter.addItem()
            }
        }

        //Certificate Adapter
        certificateAdapter = ResumeBuilderEditCertificateDetailsAdapter(editableCertificateList.toMutableList(), this)
        binding.rcCertificate.layoutManager = LinearLayoutManager(this)
        binding.rcCertificate.isNestedScrollingEnabled = false
        binding.rcCertificate.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.rcCertificate.adapter = certificateAdapter

        // Add an empty item only if list is empty or has 0/1 item
        if (editableCertificateList.size <= 0) {
            certificateAdapter.addItem()
        }
        binding.lblAddAnotherCertificate.setOnClickListener {
            if (certificateAdapter.showValidationErrors(binding.rcCertificate)) {
                certificateAdapter.addItem()
            }
        }

        //assessment Adapter
        assessmentAdapter = ResumeBuilderEditAsssessmentDetailsAdapter(editableAssessmentList.toMutableList(), this)
        binding.rcAssessmentScore.layoutManager = LinearLayoutManager(this)
        binding.rcAssessmentScore.isNestedScrollingEnabled = false
        binding.rcAssessmentScore.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.rcAssessmentScore.adapter = assessmentAdapter

        // Add an empty item only if list is empty or has 0/1 item
        if (editableAssessmentList.size <= 0) {
            assessmentAdapter.addItem()
        }
        binding.lblAddAnotherAssessment.setOnClickListener {
            if (assessmentAdapter.showValidationErrors(binding.rcAssessmentScore)) {
                assessmentAdapter.addItem()
            }
        }

        //project Adapter
        projectAdapter = ResumeBuilderEditProjectDetailsAdapter(editableProjectList.toMutableList(), this)
        binding.rcProject.layoutManager = LinearLayoutManager(this)
        binding.rcProject.isNestedScrollingEnabled = false
        binding.rcProject.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.rcProject.adapter = projectAdapter

        // Add an empty item only if list is empty or has 0/1 item
        if (editableProjectList.size <= 0) {
            projectAdapter.addItem()
        }
        binding.lblAddAnotherProject.setOnClickListener {
            if (projectAdapter.showValidationErrors(binding.rcProject)) {
                projectAdapter.addItem()
            }
        }

        //Update button
        binding.commonBottomResumeBuilder.btnDefault2.text=getString(R.string.update)

        binding.commonBottomResumeBuilder.btnSave.setOnClickListener {

            val isInternshipValid = internshipAdapter.showValidationErrors(binding.rcInternshipExperiences)
            val isCertificateValid = certificateAdapter.showValidationErrors(binding.rcCertificate)
            val isAssessmentValid = assessmentAdapter.showValidationErrors(binding.rcAssessmentScore)
            val isProjectValid = projectAdapter.showValidationErrors(binding.rcProject)

            if (!isInternshipValid || !isCertificateValid || !isAssessmentValid || !isProjectValid) {
                // At least one list has invalid data, show validation errors
                return@setOnClickListener
            }

            selectedInternshipList = internshipAdapter.getUpdatedList()
            selectedCertificateList=certificateAdapter.getUpdatedList()
            selectedAssessmentList=assessmentAdapter.getUpdatedList()
            selectedProjectList=projectAdapter.getUpdatedList()
            selectedSoftSkillsList=softSkillsAdapter.getUpdatedList()

            Log.d("FinalInternshipListForSubmit", selectedInternshipList.toString())
            Log.d("FinalsCertificateListForSubmit", selectedCertificateList.toString())
            Log.d("FinalAssessmentListForSubmit", selectedAssessmentList.toString())
            Log.d("FinalselectedProjectForSubmit", selectedProjectList.toString())


            var isChanged = false
            val changeLog = mutableListOf<String>()

            if (selectedInternshipList.toSet() != savedInternshipList.toSet()) {
                changeLog.add("Internship")
                Log.d("SkillSetCheck", "Internship changed")
                isChanged = true
            }

            if (selectedCertificateList.toSet() != savedCertificateList.toSet()) {
                changeLog.add("Certificate")
                Log.d("SkillSetCheck", "Certificate changed")
                isChanged = true
            }

            if (savedLanguage!=binding.edtLanguageknown.text.toString()) {
                changeLog.add("Language")
                Log.d("SkillSetCheck", "Language changed")
                isChanged = true
            }

            if (savedToolsAndPlatform!=binding.edtToolsAndPlatform.text.toString()) {
                changeLog.add("Tools And Platform")
                Log.d("SkillSetCheck", "ToolsAndPlatform changed")
                isChanged = true
            }
            if (savedAreaOfInterest!=binding.edtAreaOfInterest.text.toString()) {
                changeLog.add("Area of Intereset")
                Log.d("SkillSetCheck", " Area of Intereset changed")
                isChanged = true
            }
            if (savedProgrammmingLanguage!=binding.edtProgrammingLanguage.text.toString()) {
                changeLog.add("Programming Language")

                Log.d("SkillSetCheck", " Programming Language changed")
                isChanged = true
            }

            if (selectedAssessmentList.toSet() != savedAssessmentList.toSet()) {
                changeLog.add("Assessment")

                Log.d("SkillSetCheck", "Assessment changed")
                isChanged = true
            }

            if (selectedProjectList.toSet() != savedProjectList.toSet()) {
                changeLog.add("Project")

                Log.d("SkillSetCheck", "Project changed")
                isChanged = true
            }

            if (selectedSoftSkillsList.toSet() != savedSoftSkillsList.toSet()) {
                changeLog.add("SoftSkills")

                Log.d("SkillSetCheck", "SoftSkills changed")
                isChanged = true
            }

            if (isChanged) {
                showConfirmationDialog(changeLog)
            }
            else{
                Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show()
                Log.d("SkillSetCheck", "❌ No changes made")
            }
        }


        appViewModel?.ResumeBuilderSoftSkillsDetails!!.observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    isLoadSoftSkillsDetails(response.data)
                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        appViewModel?.ResumeBuilderEditSkillSetDetails!!.observe(this) { response ->
            if (response != null) {

                if (response.status) {
                    Log.d("EditSkillSet","Scucessful EDit")
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.imgback!!.setOnClickListener {
            onBackPressed()
        }

    }

    private fun showConfirmationDialog(changedFields: List<String>) {
        val builder = AlertDialog.Builder(this)

        val message = buildString {
            append("You have changed the following:\n")
            changedFields.forEach {
                append("- $it\n")
            }
            append("\nAre you sure you want to save?")
        }

        builder.setTitle("Confirmation")
            .setMessage(message)
            .setPositiveButton("Yes") { dialog, _ ->
                UpdateEditSkillSetDetails()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    fun GetSoftSkillsDetails() {
//        appViewModel!!.GetResumeBuilderSoftSkillsDetails(this@EditSkillSet)
        appViewModel!!.GetResumeBuilderSoftSkillsDetails(this@EditSkillSet)
    }



    fun UpdateEditSkillSetDetails() {
        val jsonObject = JsonObject()
        val gson = Gson()

        val softSkillsArray = JsonArray()
        selectedSoftSkillsList.forEach {
            softSkillsArray.add(it)
        }
        val areaInterestArray = JsonArray()
        binding.edtAreaOfInterest.text.toString().split(",").map { it.trim() }.forEach { areaofintereset ->
            areaInterestArray.add(areaofintereset)
        }
        val programminglangauageArray = JsonArray()
        binding.edtProgrammingLanguage.text.toString().split(",").map { it.trim() }.forEach { programminglanguage ->
            programminglangauageArray.add(programminglanguage)
        }
        val toolandplatformArray = JsonArray()
        binding.edtToolsAndPlatform.text.toString().split(",").map { it.trim() }.forEach { toolandplatform ->
            toolandplatformArray.add(toolandplatform)
        }
        val internshipArray = JsonArray()
        selectedInternshipList.forEach {
            val internshipJson = gson.toJsonTree(it).asJsonObject
            internshipArray.add(internshipJson)
        }
        val certificationArray = JsonArray()
        selectedCertificateList.forEach {
            val certificationJson = gson.toJsonTree(it).asJsonObject
            certificationArray.add(certificationJson)
        }
        val assessmentDetailsArray = JsonArray()
        selectedAssessmentList.forEach {
            val assessmentDetailsJson = gson.toJsonTree(it).asJsonObject
            assessmentDetailsArray.add(assessmentDetailsJson)
        }
        val projectsArray = JsonArray()
        selectedProjectList.forEach {
            val projectsJson = gson.toJsonTree(it).asJsonObject
            projectsArray.add(projectsJson)
        }

        jsonObject.addProperty("idMember",savedMemberID )
        jsonObject.addProperty("languages",binding.edtLanguageknown.text.toString())
        jsonObject.add("softSkill",softSkillsArray )
        jsonObject.add("areaInterest",areaInterestArray )
        jsonObject.add("programmingLanguage",programminglangauageArray)
        jsonObject.add("toolsPlatform",toolandplatformArray)
        jsonObject.add("internship", internshipArray)
        jsonObject.add("certifications", certificationArray)
        jsonObject.add("assessmentDetails", assessmentDetailsArray)
        jsonObject.add("projects",projectsArray)

        appViewModel!!.SendEditSkillSetDetails(jsonObject,this@EditSkillSet)
        Log.d("FinalJSON", jsonObject.toString())
    }



    private fun isLoadSoftSkillsDetails(data: List<GetResumeBuilderSkillSetSoftSkillsData>) {
        softSkillsAdapter= ResumeBuilderSoftSkillsAdapter(data,savedSoftSkillsList,this)
            binding.rcSoftSkills.layoutManager = GridLayoutManager(this, 2)
            binding.rcSoftSkills.isNestedScrollingEnabled = false
            binding.rcSoftSkills.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            binding.rcSoftSkills.adapter =softSkillsAdapter

    }


    override fun onSoftSkillsChanged(selectedSkills: List<String>) {
        selectedSoftSkillsList = selectedSkills
        Log.d("savedSoftSkillsList",savedSoftSkillsList.toString())
        Log.d("UpdatedSoftSkillsList",selectedSoftSkillsList.toString())
    }

    override fun onInternshipListUpdated(updatedList: List<GetInternshipDetailsData>) {
        selectedInternshipList = updatedList
        Log.d("savedInternshipList",savedInternshipList.toString())
        Log.d("UpdatedInternshipList",selectedInternshipList.toString())
    }

    override fun onCertificateListUpdated(updatedCertificateList: List<GetCertificateDetailsData>) {
        selectedCertificateList = updatedCertificateList
        Log.d("savedCertificateList",savedCertificateList.toString())
        Log.d("UpdatedCertificateList",selectedCertificateList.toString())
    }

    override fun onAssessmentListUpdated(updatedAssessmentList: List<GetAssessmentDetailsData>) {
        selectedAssessmentList = updatedAssessmentList
        Log.d("savedAssessmentList",savedCertificateList.toString())
        Log.d("UpdatedAssessmentList",selectedAssessmentList.toString())
    }

    override fun onProjectListUpdated(updatedProjectList: List<GetProjectDetailsData>) {
        selectedProjectList = updatedProjectList
        Log.d("savedProjectList",savedProjectList.toString())
        Log.d("UpdatedProjectList",selectedProjectList.toString())
    }

}