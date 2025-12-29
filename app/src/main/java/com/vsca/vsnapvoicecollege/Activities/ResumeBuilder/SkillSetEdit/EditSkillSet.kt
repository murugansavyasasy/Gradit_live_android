package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.ResumeBuilder
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderEditAsssessmentDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderEditCertificateDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderEditInternshipDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderEditProjectDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderSoftSkillsAdapter
import com.vsca.vsnapvoicecollege.Interfaces.OnSoftSkillSelectedListener
import com.vsca.vsnapvoicecollege.Model.FilePath
import com.vsca.vsnapvoicecollege.Model.FileType
import com.vsca.vsnapvoicecollege.Model.GetAssessmentDetailsData
import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData
import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
import com.vsca.vsnapvoicecollege.Model.GetProjectDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderSkillSetSoftSkillsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.albumImage.AlbumSelectActivity
import com.vsca.vsnapvoicecollege.databinding.LayoutEditskillsetBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.compareTo
import kotlin.toString

class EditSkillSet : AppCompatActivity(),OnSoftSkillSelectedListener {



    enum class AttachmentSource {
        INTERNSHIP, CERTIFICATE, ASSESSMENT, PROJECT
    }

    // Holds current adapter's list (internship / certificate / project)
    private var attachmentList: MutableList<AttachmentHolder> = mutableListOf()

    // Which row was clicked
    private var attachmentPosition: Int = RecyclerView.NO_POSITION

    // Which adapter triggered attachment
    private var attachmentSource: AttachmentSource? = null




    val REQUEST_Camera = 1
    val SELECT_DOCUMENT = 101

    val REQUEST_GAllery = 2
    var FilePopup: PopupWindow? = null
    val SELECT_PDF = 8778

    private val MAX_FILES_PER_QUESTION = 10
    private val MAX_VIDEO_PER_QUESTION = 2
    var Totalfile: String? = null

    var imageFilePath: String? = null
    var PDFTempFileWrite: File? = null
    var photoTempFileWrite: File? = null

    var photoURI: Uri? = null
    var outputDir: File? = null

    var filename: String? = null


    var isAttachmentAdapterPosition = 0

    private var itemList: MutableList<GetInternshipDetailsData> = mutableListOf()



    ///



    var appViewModel: App? = null
    private lateinit var binding:LayoutEditskillsetBinding
    var savedSoftSkillsList: List<String> = emptyList()
    var selectedSoftSkillsList: List<String> = emptyList()
    var savedLanguage=""
    var savedAreaOfInterest=""
    var savedProgrammmingLanguage=""
    var savedToolsAndPlatform=""
    var savedMemberID=-1
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

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        binding.commonBottomResumeBuilder.imgDefault.visibility= View.GONE
        savedSoftSkillsList = CommonUtil.isSkillSetDataSending?.softSkill
            ?.takeIf { it.isNotBlank() }
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()


//        savedInternshipList = CommonUtil.isSkillSetDataSending?.internship ?: emptyList()
//        savedCertificateList = CommonUtil.isSkillSetDataSending?.certifications ?: emptyList()
//        savedAssessmentList = CommonUtil.isSkillSetDataSending?.assessmentDetails ?: emptyList()
//        savedProjectList = CommonUtil.isSkillSetDataSending?.projects ?: emptyList()

        // Make saved lists immutable so they cannot be accidentally changed
        savedInternshipList = CommonUtil.isSkillSetDataSending?.internship
            ?.map { it.deepCopy() } ?: emptyList()

        savedCertificateList = CommonUtil.isSkillSetDataSending?.certifications
            ?.map { it.deepCopy() } ?: emptyList()

        savedProjectList = CommonUtil.isSkillSetDataSending?.projects
            ?.map { it.deepCopy() } ?: emptyList()

        savedAssessmentList = CommonUtil.isSkillSetDataSending?.assessmentDetails
            ?.map { it.deepCopy() } ?: emptyList()



        savedLanguage= CommonUtil.isSkillSetDataSending?.languages.toString()
        savedToolsAndPlatform=CommonUtil.isSkillSetDataSending?.toolsPlatform.toString()
        savedProgrammmingLanguage=CommonUtil.isSkillSetDataSending?.programmingLanguage.toString()
        savedAreaOfInterest=CommonUtil.isSkillSetDataSending?.areaInterest.toString()
        savedMemberID=CommonUtil.isSkillSetDataSending?.idMember!!
        Log.d("savedMemberID",savedMemberID.toString())

        Log.d("savedInternshipList", CommonUtil.isSkillSetDataSending.toString())
        Log.d("savedInternshipList",savedInternshipList.toString())


        GetSoftSkillsDetails()
        binding.edtLanguageknown.setText(CommonUtil.isSkillSetDataSending?.languages)
        binding.edtAreaOfInterest.setText(CommonUtil.isSkillSetDataSending?.areaInterest)
        binding.edtProgrammingLanguage.setText(CommonUtil.isSkillSetDataSending?.programmingLanguage)
        binding.edtToolsAndPlatform.setText(CommonUtil.isSkillSetDataSending?.toolsPlatform)
//        editableInternshipList = savedInternshipList.map { it.copy() }.toMutableList()
//        editableCertificateList = savedCertificateList.map { it.copy() }.toMutableList()
//        editableAssessmentList = savedAssessmentList.map { it.copy() }.toMutableList()
//        editableProjectList = savedProjectList.map { it.copy() }.toMutableList()

        editableInternshipList = savedInternshipList.map { it.deepCopy() }.toMutableList()
        editableCertificateList = savedCertificateList.map { it.deepCopy() }.toMutableList()
        editableProjectList = savedProjectList.map { it.deepCopy() }.toMutableList()
        editableAssessmentList = savedAssessmentList.map { it.deepCopy() }.toMutableList()




        //Intership Adapter
        internshipAdapter = ResumeBuilderEditInternshipDetailsAdapter(this,editableInternshipList.toMutableList(), this)
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
        certificateAdapter = ResumeBuilderEditCertificateDetailsAdapter(this,editableCertificateList.toMutableList(), this)
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
        assessmentAdapter = ResumeBuilderEditAsssessmentDetailsAdapter(this,editableAssessmentList.toMutableList(), this)
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
        projectAdapter = ResumeBuilderEditProjectDetailsAdapter(this,editableProjectList.toMutableList(), this)
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
//
//            if (selectedInternshipList.toSet() != savedInternshipList.toSet()) {
//                isChanged = true
//            }
//
//            if (selectedCertificateList.toSet() != savedCertificateList.toSet()) {
//                isChanged = true
//            }
            //            if (selectedAssessmentList.toSet() != savedAssessmentList.toSet()) {
//                isChanged = true
//            }
//
//            if (selectedProjectList.toSet() != savedProjectList.toSet()) {
//                isChanged = true
//            }



            if (selectedInternshipList.hasAnyChangesComparedTo(savedInternshipList)) isChanged = true
            if (selectedCertificateList.hasAnyChangesComparedTo(savedCertificateList)) isChanged = true
            if (selectedAssessmentList.hasAnyChangesComparedTo(savedAssessmentList)) isChanged = true
            if (selectedProjectList.hasAnyChangesComparedTo(savedProjectList)) isChanged = true


            if (savedLanguage!=binding.edtLanguageknown.text.toString()) {
                isChanged = true
            }

            if (savedToolsAndPlatform!=binding.edtToolsAndPlatform.text.toString()) {
                isChanged = true
            }
            if (savedAreaOfInterest!=binding.edtAreaOfInterest.text.toString()) {
                isChanged = true
            }
            if (savedProgrammmingLanguage!=binding.edtProgrammingLanguage.text.toString()) {
                isChanged = true
            }


            if (selectedSoftSkillsList.toSet() != savedSoftSkillsList.toSet()) {
                isChanged = true
            }

            if (isChanged) {
                showConfirmationDialog()
            }
            else{
                showNoChanges()
                Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show()
                Log.d("SkillSetCheck", "No changes made")
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

        binding.commonBottomResumeBuilder.btnDefault1!!.setOnClickListener {
            onBackPressed()
        }

        binding.imgback!!.setOnClickListener {
            onBackPressed()
        }

    }

    fun AttachmentHolder.hasFileChangesComparedTo(saved: AttachmentHolder): Boolean {
        val currentFiles = this.file_path ?: mutableListOf()
        val savedFiles = saved.file_path ?: mutableListOf()

        Log.d("ChangeCheck", "Comparing files: current=${currentFiles.size}, saved=${savedFiles.size}")

        if (currentFiles.size != savedFiles.size) {
            Log.d("ChangeCheck", "File count changed")
            return true
        }

        // Compare each file by URL and type
        currentFiles.forEachIndexed { index, curr ->
            val svd = savedFiles.getOrNull(index)
            if (svd == null || curr.url != svd.url || curr.type != svd.type) {
                Log.d("ChangeCheck", "File at index $index changed: curr=$curr, saved=$svd")
                return true
            }
        }

        return false
    }

    fun AttachmentHolder.hasChangesComparedTo(saved: AttachmentHolder): Boolean {
        val changed = when (this) {
            is GetInternshipDetailsData -> {
                saved as GetInternshipDetailsData
                val mainChanged = companyName != saved.companyName ||
                        designation != saved.designation ||
                        from != saved.from ||
                        to != saved.to
                if (mainChanged) Log.d("ChangeCheck", "Internship main fields changed: $this vs $saved")
                mainChanged
            }
            is GetCertificateDetailsData -> {
                saved as GetCertificateDetailsData
                val mainChanged = courseName != saved.courseName ||
                        duration != saved.duration ||
                        institute != saved.institute
                if (mainChanged) Log.d("ChangeCheck", "Certificate main fields changed: $this vs $saved")
                mainChanged
            }
            is GetProjectDetailsData -> {
                saved as GetProjectDetailsData
                val mainChanged = title != saved.title
                if (mainChanged) Log.d("ChangeCheck", "Project main fields changed: $this vs $saved")
                mainChanged
            }
            is GetAssessmentDetailsData -> {
                saved as GetAssessmentDetailsData
                val mainChanged = assessment != saved.assessment || score != saved.score
                if (mainChanged) Log.d("ChangeCheck", "Assessment main fields changed: $this vs $saved")
                mainChanged
            }
            else -> false
        }

        val filesChanged = this.hasFileChangesComparedTo(saved)
        if (filesChanged) Log.d("ChangeCheck", "Files changed for $this")

        return changed || filesChanged
    }

    fun <T : AttachmentHolder> List<T>.hasAnyChangesComparedTo(savedList: List<T>): Boolean {
        if (this.size != savedList.size) {
            Log.d("ChangeCheck", "List size changed: current=${this.size}, saved=${savedList.size}")
            return true
        }
        for (i in this.indices) {
            if (this[i].hasChangesComparedTo(savedList[i])) {
                Log.d("ChangeCheck", "Item at index $i changed")
                return true
            }
        }
        return false
    }








    fun GetInternshipDetailsData.deepCopy(): GetInternshipDetailsData {
        return this.copy(
            file_path = this.file_path
                ?.map { it.copy() }
                ?.toMutableList()
        )
    }

    fun GetCertificateDetailsData.deepCopy(): GetCertificateDetailsData {
        return this.copy(
            file_path = this.file_path
                ?.map { it.copy() }
                ?.toMutableList()
        )
    }

    fun GetProjectDetailsData.deepCopy(): GetProjectDetailsData {
        return this.copy(
            file_path = this.file_path
                ?.map { it.copy() }
                ?.toMutableList()
        )
    }

    fun GetAssessmentDetailsData.deepCopy(): GetAssessmentDetailsData {
        return this.copy(
            file_path = this.file_path
                ?.map { it.copy() }
                ?.toMutableList()
        )
    }


    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)

        val message = buildString {
            append("Are you sure you want to save?")
        }

        builder.setTitle("Confirmation")
            .setMessage(message)
            .setPositiveButton("Yes") { dialog, _ ->

                Log.d("AfterValidationFinalInternshipListForSubmit", selectedInternshipList.toString())
                Log.d("AfterValidationFinalsCertificateListForSubmit", selectedCertificateList.toString())
                Log.d("FinalAssessmentListForSubmit", selectedAssessmentList.toString())
                Log.d("FinalselectedProjectForSubmit", selectedProjectList.toString())
                UpdateEditSkillSetDetails()
                dialog.dismiss()
                val intent = Intent(this, ResumeBuilder::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    private fun showNoChanges() {
        val builder = AlertDialog.Builder(this)

        val message = buildString {
            append("No changes made?")
        }

        builder.setTitle("Alert")
            .setMessage(message)
            .setNegativeButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }


    fun GetSoftSkillsDetails() {
        appViewModel!!.GetResumeBuilderSoftSkillsDetails(this@EditSkillSet)
    }



    private fun <T : AttachmentHolder> normalizeAttachmentList(list: List<T>): List<T> {
        return list.map { item ->
            @Suppress("UNCHECKED_CAST")
            when (item) {
                is GetInternshipDetailsData -> item.copy(
                    file_path = item.file_path?.map { it.copy() }?.toMutableList()
                ) as T

                is GetCertificateDetailsData -> item.copy(
                    file_path = item.file_path?.map { it.copy() }?.toMutableList()
                ) as T

                is GetAssessmentDetailsData -> item.copy(
                    file_path = item.file_path?.map { it.copy() }?.toMutableList()
                ) as T

                is GetProjectDetailsData -> item.copy(
                    file_path = item.file_path?.map { it.copy() }?.toMutableList()
                ) as T

                else -> item
            }
        }
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
        normalizeAttachmentList(selectedInternshipList).forEach {
            val internshipJson = gson.toJsonTree(it).asJsonObject
            internshipArray.add(internshipJson)
        }
        val certificationArray = JsonArray()
        normalizeAttachmentList(selectedCertificateList).forEach {
            val certificationJson = gson.toJsonTree(it).asJsonObject
            certificationArray.add(certificationJson)
        }
        val assessmentDetailsArray = JsonArray()
        normalizeAttachmentList(selectedAssessmentList).forEach {
            val assessmentDetailsJson = gson.toJsonTree(it).asJsonObject
            assessmentDetailsArray.add(assessmentDetailsJson)
        }
        val projectsArray = JsonArray()
        normalizeAttachmentList(selectedProjectList).forEach {
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

    }

    override fun onInternshipListUpdated(updatedList: List<GetInternshipDetailsData>) {
        selectedInternshipList = updatedList

    }

    override fun onCertificateListUpdated(updatedCertificateList: List<GetCertificateDetailsData>) {
        selectedCertificateList = updatedCertificateList
    }

    override fun onAssessmentListUpdated(updatedAssessmentList: List<GetAssessmentDetailsData>) {
        selectedAssessmentList = updatedAssessmentList

    }

    override fun onProjectListUpdated(updatedProjectList: List<GetProjectDetailsData>) {
        selectedProjectList = updatedProjectList
    }

    override fun onAttachmentPick(
        source: AttachmentSource,
        position: Int,
        list: MutableList<out AttachmentHolder>
    ) {
        attachmentSource = source
        attachmentPosition = position
        attachmentList = list.toMutableList()
        ChooseFile()
    }





    fun ChooseFile() {

        Log.d("popup", "test")
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialog: View = inflater.inflate(R.layout.popup_choose_file, null)
        FilePopup = PopupWindow(
            dialog, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true
        )
        FilePopup?.showAtLocation(dialog, Gravity.BOTTOM, 0, 0)
        FilePopup?.contentView = dialog
        FilePopup?.isOutsideTouchable = true
        FilePopup?.isFocusable = true

        val LayoutGallery = dialog.findViewById<ConstraintLayout>(R.id.LayoutGallery)
        val LayoutCamera = dialog.findViewById<ConstraintLayout>(R.id.LayoutCamera)
        val LayoutDocuments = dialog.findViewById<ConstraintLayout>(R.id.LayoutDocuments)
        val popClose = dialog.findViewById<ImageView>(R.id.popClose)
        val lblDocumentFile = dialog.findViewById<TextView>(R.id.lblDocumentFile)

        lblDocumentFile.text="Documents"

        val container = FilePopup?.contentView?.parent as View
        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.7f
        wm.updateViewLayout(container, p)

        popClose.setOnClickListener {
            FilePopup?.dismiss()
        }
        LayoutGallery.setOnClickListener {
//            CommonUtil.SelcetedFileList.clear()

            val intent1 = Intent(this, AlbumSelectActivity::class.java)
            intent1.putExtra("Gallery", "Images")
            startActivityForResult(intent1, REQUEST_GAllery)
            FilePopup!!.dismiss()
            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())

        }

        LayoutCamera.setOnClickListener {
//            CommonUtil.SelcetedFileList.clear()

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                photoTempFileWrite = createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (photoTempFileWrite != null) {
                photoURI = FileProvider.getUriForFile(
                    this, "com.vsca.vsnapvoicecollege.provider", photoTempFileWrite!!
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_Camera)
                FilePopup?.dismiss()
                Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())

            }
        }

//        LayoutDocuments.setOnClickListener({
//
////            CommonUtil.SelcetedFileList.clear()
//
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "application/pdf"
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            startActivityForResult(intent, SELECT_PDF)
//            FilePopup!!.dismiss()
//            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())
//
//
//        })

        LayoutDocuments.setOnClickListener {

            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    arrayOf(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/vnd.ms-excel",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "application/vnd.ms-powerpoint",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "text/plain"
                    )
                )
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addCategory(Intent.CATEGORY_OPENABLE)
            }

            startActivityForResult(intent, SELECT_DOCUMENT)
            FilePopup?.dismiss()

            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())

        }


    }

    private fun addPath(uri: Uri, forcedType: FileType? = null) {
        if (attachmentPosition == RecyclerView.NO_POSITION) return
        if (attachmentPosition >= attachmentList.size) return

        val item = attachmentList[attachmentPosition]
        val fileName = getFileName(uri)

        val type = forcedType ?: when {
            fileName.endsWith(".pdf", true) -> FileType.PDF
            fileName.endsWith(".doc", true) || fileName.endsWith(".docx", true) -> FileType.DOC
            fileName.endsWith(".xls", true) || fileName.endsWith(".xlsx", true) -> FileType.EXCEL
            fileName.endsWith(".ppt", true) || fileName.endsWith(".pptx", true) -> FileType.PPT
            fileName.matches(".*\\.(jpg|jpeg|png|webp)$".toRegex(RegexOption.IGNORE_CASE)) -> FileType.IMAGE
            fileName.endsWith(".txt", true) -> FileType.TXT
            else -> FileType.OTHER
        }

        if (!canAddAttachment(item, type)) return


        item.file_path?.add(FilePath(uri.toString(), type.name))
    }


    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {

            //  CAMERA
            REQUEST_Camera -> {
                imageFilePath?.let {
                    CommonUtil.SelcetedFileList.add(it)
                    addPath(Uri.fromFile(File(it)), FileType.IMAGE)
                    notifyAttachmentChanged()
                }
            }

            //  GALLERY
            REQUEST_GAllery -> {
                val images = data?.getStringArrayListExtra("images") ?: return
                images.forEach { path ->
                    CommonUtil.SelcetedFileList.add(path)
                    addPath(Uri.parse(path), FileType.IMAGE)
                }
                notifyAttachmentChanged()
            }

            // DOCUMENT
            SELECT_DOCUMENT -> {
                val itemCount = data?.clipData?.itemCount
                    ?: if (data?.data != null) 1 else 0

                for (i in 0 until itemCount) {
                    val uri = data?.clipData?.getItemAt(i)?.uri ?: data?.data ?: continue
                    CommonUtil.SelcetedFileList.add(uri.toString())
                    addPath(uri)
                }
                notifyAttachmentChanged()
            }
        }

        updateFileCountUI()
        attachmentPosition = RecyclerView.NO_POSITION
    }



    private fun notifyAttachmentChanged() {
        when (attachmentSource) {
            AttachmentSource.INTERNSHIP ->
                internshipAdapter.notifyItemChanged(attachmentPosition)

            AttachmentSource.CERTIFICATE ->
                certificateAdapter.notifyItemChanged(attachmentPosition)

            AttachmentSource.ASSESSMENT ->
                assessmentAdapter.notifyItemChanged(attachmentPosition)

            AttachmentSource.PROJECT ->
                projectAdapter.notifyItemChanged(attachmentPosition)

            null -> Unit
        }
    }


    private fun canAddAttachment(
        item: AttachmentHolder,
        newType: FileType
    ): Boolean {

        val attachments = item.file_path ?: return true

        if (attachments.size >= MAX_FILES_PER_QUESTION) {
            Toast.makeText(
                this,
                "Only $MAX_FILES_PER_QUESTION files allowed",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }




    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: ""
    }
    @Throws(IOException::class)
    fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        imageFilePath = image.absolutePath
        return image
    }



    /**
     * Updates the label showing number of selected files
     */
    private fun updateFileCountUI() {
        if (CommonUtil.SelcetedFileList.isNotEmpty()) {
            Totalfile = CommonUtil.SelcetedFileList.size.toString()
            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())
            Log.d("FinalAdapterData", internshipAdapter.getUpdatedList().toString())

        }
        else{
            Totalfile = CommonUtil.SelcetedFileList.size.toString()
            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())
            Log.d("FinalAdapterData", internshipAdapter.getUpdatedList().toString())
        }
    }


}




//package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.app.Dialog
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.provider.MediaStore
//import android.provider.OpenableColumns
//import android.util.Log
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.Window
//import android.view.WindowManager
//import android.widget.ImageView
//import android.widget.PopupWindow
//import android.widget.RelativeLayout
//import android.widget.Toast
//import androidx.activity.result.ActivityResultLauncher
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.FileProvider
//import androidx.core.view.WindowInsetsControllerCompat
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.gson.Gson
//import com.google.gson.JsonArray
//import com.google.gson.JsonObject
//import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.ResumeBuilder
//import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderEditAsssessmentDetailsAdapter
//import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderEditCertificateDetailsAdapter
//import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderEditInternshipDetailsAdapter
//import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderEditProjectDetailsAdapter
//import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderSoftSkillsAdapter
//import com.vsca.vsnapvoicecollege.Interfaces.OnSoftSkillSelectedListener
//import com.vsca.vsnapvoicecollege.Model.FilePath
//import com.vsca.vsnapvoicecollege.Model.FileType
//import com.vsca.vsnapvoicecollege.Model.GetAssessmentDetailsData
//import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData
//import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
//import com.vsca.vsnapvoicecollege.Model.GetProjectDetailsData
//import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderSkillSetSoftSkillsData
//import com.vsca.vsnapvoicecollege.R
//import com.vsca.vsnapvoicecollege.Utils.CommonUtil
//import com.vsca.vsnapvoicecollege.ViewModel.App
//import com.vsca.vsnapvoicecollege.albumImage.AlbumSelectActivity
//import com.vsca.vsnapvoicecollege.databinding.LayoutEditskillsetBinding
//import java.io.File
//import java.io.FileOutputStream
//import java.io.IOException
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//import kotlin.compareTo
//import kotlin.toString
//
//class EditSkillSet : AppCompatActivity(),OnSoftSkillSelectedListener {
//
//    val REQUEST_Camera = 1
//    val SELECT_DOCUMENT = 101
//
//    val REQUEST_GAllery = 2
//    var FilePopup: PopupWindow? = null
//    val SELECT_PDF = 8778
//
//    private val MAX_FILES_PER_QUESTION = 10
//    private val MAX_VIDEO_PER_QUESTION = 2
//    var Totalfile: String? = null
//
//    var imageFilePath: String? = null
//    var PDFTempFileWrite: File? = null
//    var photoTempFileWrite: File? = null
//
//    var photoURI: Uri? = null
//    var outputDir: File? = null
//
//    var filename: String? = null
//
//
//    var isAttachmentAdapterPosition = 0
//
//    private var itemList: MutableList<GetInternshipDetailsData> = mutableListOf()
//
//
//
//    ///
//
//
//
//    var appViewModel: App? = null
//    private lateinit var binding:LayoutEditskillsetBinding
//    var savedSoftSkillsList: List<String> = emptyList()
//    var selectedSoftSkillsList: List<String> = emptyList()
//    var savedLanguage=""
//    var savedAreaOfInterest=""
//    var savedProgrammmingLanguage=""
//    var savedToolsAndPlatform=""
//    var savedMemberID=-1
//    private lateinit var internshipAdapter: ResumeBuilderEditInternshipDetailsAdapter
//    private lateinit var savedInternshipList: List<GetInternshipDetailsData>
//    private lateinit var editableInternshipList: MutableList<GetInternshipDetailsData>
//    private var selectedInternshipList: List<GetInternshipDetailsData> = emptyList()
//    private lateinit var certificateAdapter: ResumeBuilderEditCertificateDetailsAdapter
//    private lateinit var softSkillsAdapter: ResumeBuilderSoftSkillsAdapter
//    private lateinit var assessmentAdapter: ResumeBuilderEditAsssessmentDetailsAdapter
//    private lateinit var projectAdapter: ResumeBuilderEditProjectDetailsAdapter
//    private lateinit var savedCertificateList: List<GetCertificateDetailsData>
//    private lateinit var editableCertificateList: MutableList<GetCertificateDetailsData>
//    private var selectedCertificateList: List<GetCertificateDetailsData> = emptyList()
//    private lateinit var savedAssessmentList: List<GetAssessmentDetailsData>
//    private lateinit var editableAssessmentList: MutableList<GetAssessmentDetailsData>
//    private var selectedAssessmentList: List<GetAssessmentDetailsData> = emptyList()
//    private lateinit var savedProjectList: List<GetProjectDetailsData>
//    private lateinit var editableProjectList: MutableList<GetProjectDetailsData>
//    private var selectedProjectList: List<GetProjectDetailsData> = emptyList()
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = LayoutEditskillsetBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        appViewModel = ViewModelProvider(this)[App::class.java]
//        appViewModel!!.init()
//
//        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
//        insetsController.isAppearanceLightStatusBars = true
//
//        binding.commonBottomResumeBuilder.imgDefault.visibility= View.GONE
//        savedSoftSkillsList = CommonUtil.isSkillSetDataSending?.softSkill
//            ?.takeIf { it.isNotBlank() }
//            ?.split(",")
//            ?.map { it.trim() }
//            ?.filter { it.isNotEmpty() }
//            ?: emptyList()
//
//
//        savedInternshipList = CommonUtil.isSkillSetDataSending?.internship ?: emptyList()
//        savedCertificateList = CommonUtil.isSkillSetDataSending?.certifications ?: emptyList()
//        savedAssessmentList = CommonUtil.isSkillSetDataSending?.assessmentDetails ?: emptyList()
//        savedProjectList = CommonUtil.isSkillSetDataSending?.projects ?: emptyList()
//        savedLanguage= CommonUtil.isSkillSetDataSending?.languages.toString()
//        savedToolsAndPlatform=CommonUtil.isSkillSetDataSending?.toolsPlatform.toString()
//        savedProgrammmingLanguage=CommonUtil.isSkillSetDataSending?.programmingLanguage.toString()
//        savedAreaOfInterest=CommonUtil.isSkillSetDataSending?.areaInterest.toString()
//        savedMemberID=CommonUtil.isSkillSetDataSending?.idMember!!
//        Log.d("savedMemberID",savedMemberID.toString())
//
//        Log.d("savedInternshipList", CommonUtil.isSkillSetDataSending.toString())
//        Log.d("savedInternshipList",savedInternshipList.toString())
//
//
//        GetSoftSkillsDetails()
//        binding.edtLanguageknown.setText(CommonUtil.isSkillSetDataSending?.languages)
//        binding.edtAreaOfInterest.setText(CommonUtil.isSkillSetDataSending?.areaInterest)
//        binding.edtProgrammingLanguage.setText(CommonUtil.isSkillSetDataSending?.programmingLanguage)
//        binding.edtToolsAndPlatform.setText(CommonUtil.isSkillSetDataSending?.toolsPlatform)
//        editableInternshipList = savedInternshipList.map { it.copy() }.toMutableList()
//        editableCertificateList = savedCertificateList.map { it.copy() }.toMutableList()
//        editableAssessmentList = savedAssessmentList.map { it.copy() }.toMutableList()
//        editableProjectList = savedProjectList.map { it.copy() }.toMutableList()
//
//        //Intership Adapter
//        internshipAdapter = ResumeBuilderEditInternshipDetailsAdapter(this,editableInternshipList.toMutableList(), this)
//        binding.rcInternshipExperiences.layoutManager = LinearLayoutManager(this)
//        binding.rcInternshipExperiences.isNestedScrollingEnabled = false
//        binding.rcInternshipExperiences.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
//        binding.rcInternshipExperiences.adapter = internshipAdapter
//
//        // Add an empty item only if list is empty or has 0/1 item
//        if (editableInternshipList.size <= 0) {
//            internshipAdapter.addItem()
//        }
//        binding.lblAddAnotherInternship.setOnClickListener {
//            if (internshipAdapter.showValidationErrors(binding.rcInternshipExperiences)) {
//                internshipAdapter.addItem()
//            }
//        }
//
//        //Certificate Adapter
//        certificateAdapter = ResumeBuilderEditCertificateDetailsAdapter(editableCertificateList.toMutableList(), this)
//        binding.rcCertificate.layoutManager = LinearLayoutManager(this)
//        binding.rcCertificate.isNestedScrollingEnabled = false
//        binding.rcCertificate.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
//        binding.rcCertificate.adapter = certificateAdapter
//
//        // Add an empty item only if list is empty or has 0/1 item
//        if (editableCertificateList.size <= 0) {
//            certificateAdapter.addItem()
//        }
//        binding.lblAddAnotherCertificate.setOnClickListener {
//            if (certificateAdapter.showValidationErrors(binding.rcCertificate)) {
//                certificateAdapter.addItem()
//            }
//        }
//
//        //assessment Adapter
//        assessmentAdapter = ResumeBuilderEditAsssessmentDetailsAdapter(editableAssessmentList.toMutableList(), this)
//        binding.rcAssessmentScore.layoutManager = LinearLayoutManager(this)
//        binding.rcAssessmentScore.isNestedScrollingEnabled = false
//        binding.rcAssessmentScore.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
//        binding.rcAssessmentScore.adapter = assessmentAdapter
//
//        // Add an empty item only if list is empty or has 0/1 item
//        if (editableAssessmentList.size <= 0) {
//            assessmentAdapter.addItem()
//        }
//        binding.lblAddAnotherAssessment.setOnClickListener {
//            if (assessmentAdapter.showValidationErrors(binding.rcAssessmentScore)) {
//                assessmentAdapter.addItem()
//            }
//        }
//
//        //project Adapter
//        projectAdapter = ResumeBuilderEditProjectDetailsAdapter(editableProjectList.toMutableList(), this)
//        binding.rcProject.layoutManager = LinearLayoutManager(this)
//        binding.rcProject.isNestedScrollingEnabled = false
//        binding.rcProject.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
//        binding.rcProject.adapter = projectAdapter
//
//        // Add an empty item only if list is empty or has 0/1 item
//        if (editableProjectList.size <= 0) {
//            projectAdapter.addItem()
//        }
//        binding.lblAddAnotherProject.setOnClickListener {
//            if (projectAdapter.showValidationErrors(binding.rcProject)) {
//                projectAdapter.addItem()
//            }
//        }
//
//        //Update button
//        binding.commonBottomResumeBuilder.btnDefault2.text=getString(R.string.update)
//
//        binding.commonBottomResumeBuilder.btnSave.setOnClickListener {
//
//            val isInternshipValid = internshipAdapter.showValidationErrors(binding.rcInternshipExperiences)
//            val isCertificateValid = certificateAdapter.showValidationErrors(binding.rcCertificate)
//            val isAssessmentValid = assessmentAdapter.showValidationErrors(binding.rcAssessmentScore)
//            val isProjectValid = projectAdapter.showValidationErrors(binding.rcProject)
//
//            if (!isInternshipValid || !isCertificateValid || !isAssessmentValid || !isProjectValid) {
//                // At least one list has invalid data, show validation errors
//                return@setOnClickListener
//            }
//
//            selectedInternshipList = internshipAdapter.getUpdatedList()
//            selectedCertificateList=certificateAdapter.getUpdatedList()
//            selectedAssessmentList=assessmentAdapter.getUpdatedList()
//            selectedProjectList=projectAdapter.getUpdatedList()
//            selectedSoftSkillsList=softSkillsAdapter.getUpdatedList()
//
//            Log.d("FinalInternshipListForSubmit", selectedInternshipList.toString())
//            Log.d("FinalsCertificateListForSubmit", selectedCertificateList.toString())
//            Log.d("FinalAssessmentListForSubmit", selectedAssessmentList.toString())
//            Log.d("FinalselectedProjectForSubmit", selectedProjectList.toString())
//
//
//            var isChanged = false
//
//            if (selectedInternshipList.toSet() != savedInternshipList.toSet()) {
//                isChanged = true
//            }
//
//            if (selectedCertificateList.toSet() != savedCertificateList.toSet()) {
//                isChanged = true
//            }
//
//            if (savedLanguage!=binding.edtLanguageknown.text.toString()) {
//                isChanged = true
//            }
//
//            if (savedToolsAndPlatform!=binding.edtToolsAndPlatform.text.toString()) {
//                isChanged = true
//            }
//            if (savedAreaOfInterest!=binding.edtAreaOfInterest.text.toString()) {
//                isChanged = true
//            }
//            if (savedProgrammmingLanguage!=binding.edtProgrammingLanguage.text.toString()) {
//                isChanged = true
//            }
//
//            if (selectedAssessmentList.toSet() != savedAssessmentList.toSet()) {
//                isChanged = true
//            }
//
//            if (selectedProjectList.toSet() != savedProjectList.toSet()) {
//                isChanged = true
//            }
//
//            if (selectedSoftSkillsList.toSet() != savedSoftSkillsList.toSet()) {
//                isChanged = true
//            }
//
//            if (isChanged) {
//                showConfirmationDialog()
//            }
//            else{
//                showNoChanges()
//                Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show()
//                Log.d("SkillSetCheck", "No changes made")
//            }
//        }
//
//        appViewModel?.ResumeBuilderSoftSkillsDetails!!.observe(this) { response ->
//            if (response != null) {
//                if (response.status) {
//                    isLoadSoftSkillsDetails(response.data)
//                } else {
//                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//        appViewModel?.ResumeBuilderEditSkillSetDetails!!.observe(this) { response ->
//            if (response != null) {
//
//                if (response.status) {
//                    Log.d("EditSkillSet","Scucessful EDit")
//                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
//
//                } else {
//                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        binding.commonBottomResumeBuilder.btnDefault1!!.setOnClickListener {
//            onBackPressed()
//        }
//
//        binding.imgback!!.setOnClickListener {
//            onBackPressed()
//        }
//
//    }
//
//    private fun showConfirmationDialog() {
//        val builder = AlertDialog.Builder(this)
//
//        val message = buildString {
//            append("Are you sure you want to save?")
//        }
//
//        builder.setTitle("Confirmation")
//            .setMessage(message)
//            .setPositiveButton("Yes") { dialog, _ ->
//                UpdateEditSkillSetDetails()
//                dialog.dismiss()
//                val intent = Intent(this, ResumeBuilder::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                startActivity(intent)
//                finish()
//            }
//            .setNegativeButton("No") { dialog, _ ->
//                dialog.dismiss()
//            }
//
//        builder.create().show()
//    }
//
//    private fun showNoChanges() {
//        val builder = AlertDialog.Builder(this)
//
//        val message = buildString {
//            append("No changes made?")
//        }
//
//        builder.setTitle("Alert")
//            .setMessage(message)
//            .setNegativeButton("Ok") { dialog, _ ->
//                dialog.dismiss()
//            }
//        builder.create().show()
//    }
//
//
//    fun GetSoftSkillsDetails() {
//        appViewModel!!.GetResumeBuilderSoftSkillsDetails(this@EditSkillSet)
//    }
//
//
//
//    fun UpdateEditSkillSetDetails() {
//        val jsonObject = JsonObject()
//        val gson = Gson()
//
//        val softSkillsArray = JsonArray()
//        selectedSoftSkillsList.forEach {
//            softSkillsArray.add(it)
//        }
//        val areaInterestArray = JsonArray()
//        binding.edtAreaOfInterest.text.toString().split(",").map { it.trim() }.forEach { areaofintereset ->
//            areaInterestArray.add(areaofintereset)
//        }
//        val programminglangauageArray = JsonArray()
//        binding.edtProgrammingLanguage.text.toString().split(",").map { it.trim() }.forEach { programminglanguage ->
//            programminglangauageArray.add(programminglanguage)
//        }
//        val toolandplatformArray = JsonArray()
//        binding.edtToolsAndPlatform.text.toString().split(",").map { it.trim() }.forEach { toolandplatform ->
//            toolandplatformArray.add(toolandplatform)
//        }
//        val internshipArray = JsonArray()
//        selectedInternshipList.forEach {
//            val internshipJson = gson.toJsonTree(it).asJsonObject
//            internshipArray.add(internshipJson)
//        }
//        val certificationArray = JsonArray()
//        selectedCertificateList.forEach {
//            val certificationJson = gson.toJsonTree(it).asJsonObject
//            certificationArray.add(certificationJson)
//        }
//        val assessmentDetailsArray = JsonArray()
//        selectedAssessmentList.forEach {
//            val assessmentDetailsJson = gson.toJsonTree(it).asJsonObject
//            assessmentDetailsArray.add(assessmentDetailsJson)
//        }
//        val projectsArray = JsonArray()
//        selectedProjectList.forEach {
//            val projectsJson = gson.toJsonTree(it).asJsonObject
//            projectsArray.add(projectsJson)
//        }
//
//        jsonObject.addProperty("idMember",savedMemberID )
//        jsonObject.addProperty("languages",binding.edtLanguageknown.text.toString())
//        jsonObject.add("softSkill",softSkillsArray )
//        jsonObject.add("areaInterest",areaInterestArray )
//        jsonObject.add("programmingLanguage",programminglangauageArray)
//        jsonObject.add("toolsPlatform",toolandplatformArray)
//        jsonObject.add("internship", internshipArray)
//        jsonObject.add("certifications", certificationArray)
//        jsonObject.add("assessmentDetails", assessmentDetailsArray)
//        jsonObject.add("projects",projectsArray)
//
//        appViewModel!!.SendEditSkillSetDetails(jsonObject,this@EditSkillSet)
//        Log.d("FinalJSON", jsonObject.toString())
//    }
//
//
//
//    private fun isLoadSoftSkillsDetails(data: List<GetResumeBuilderSkillSetSoftSkillsData>) {
//        softSkillsAdapter= ResumeBuilderSoftSkillsAdapter(data,savedSoftSkillsList,this)
//            binding.rcSoftSkills.layoutManager = GridLayoutManager(this, 2)
//            binding.rcSoftSkills.isNestedScrollingEnabled = false
//            binding.rcSoftSkills.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
//            binding.rcSoftSkills.adapter =softSkillsAdapter
//
//    }
//
//
//    override fun onSoftSkillsChanged(selectedSkills: List<String>) {
//        selectedSoftSkillsList = selectedSkills
//
//    }
//
//    override fun onInternshipListUpdated(updatedList: List<GetInternshipDetailsData>) {
//        selectedInternshipList = updatedList
//
//    }
//
//    override fun onCertificateListUpdated(updatedCertificateList: List<GetCertificateDetailsData>) {
//        selectedCertificateList = updatedCertificateList
//    }
//
//    override fun onAssessmentListUpdated(updatedAssessmentList: List<GetAssessmentDetailsData>) {
//        selectedAssessmentList = updatedAssessmentList
//
//    }
//
//    override fun onProjectListUpdated(updatedProjectList: List<GetProjectDetailsData>) {
//        selectedProjectList = updatedProjectList
//    }
//
//    override fun onAttachmentPick(
//        position: Int,
//        item: MutableList<GetInternshipDetailsData>?
//    ) {
//        isAttachmentAdapterPosition = position
//        itemList = item!!
//        ChooseFile()
////        showBottomDialog()
//    }
//
//
//
//
//    fun ChooseFile() {
//
//        Log.d("popup", "test")
//        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val dialog: View = inflater.inflate(R.layout.popup_choose_file, null)
//        FilePopup = PopupWindow(
//            dialog, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true
//        )
//        FilePopup?.showAtLocation(dialog, Gravity.BOTTOM, 0, 0)
//        FilePopup?.contentView = dialog
//        FilePopup?.isOutsideTouchable = true
//        FilePopup?.isFocusable = true
//
//        val LayoutGallery = dialog.findViewById<ConstraintLayout>(R.id.LayoutGallery)
//        val LayoutCamera = dialog.findViewById<ConstraintLayout>(R.id.LayoutCamera)
//        val LayoutDocuments = dialog.findViewById<ConstraintLayout>(R.id.LayoutDocuments)
//        val popClose = dialog.findViewById<ImageView>(R.id.popClose)
//
//        val container = FilePopup?.contentView?.parent as View
//        val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val p = container.layoutParams as WindowManager.LayoutParams
//        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
//        p.dimAmount = 0.7f
//        wm.updateViewLayout(container, p)
//
//        popClose.setOnClickListener {
//            FilePopup?.dismiss()
//        }
//        LayoutGallery.setOnClickListener {
////            CommonUtil.SelcetedFileList.clear()
//
//            val intent1 = Intent(this, AlbumSelectActivity::class.java)
//            intent1.putExtra("Gallery", "Images")
//            startActivityForResult(intent1, REQUEST_GAllery)
//            FilePopup!!.dismiss()
//            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())
//
//        }
//
//        LayoutCamera.setOnClickListener {
////            CommonUtil.SelcetedFileList.clear()
//
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            try {
//                photoTempFileWrite = createImageFile()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            if (photoTempFileWrite != null) {
//                photoURI = FileProvider.getUriForFile(
//                    this, "com.vsca.vsnapvoicecollege.provider", photoTempFileWrite!!
//                )
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                startActivityForResult(intent, REQUEST_Camera)
//                FilePopup?.dismiss()
//                Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())
//
//            }
//        }
//
////        LayoutDocuments.setOnClickListener({
////
//////            CommonUtil.SelcetedFileList.clear()
////
////            val intent = Intent(Intent.ACTION_GET_CONTENT)
////            intent.type = "application/pdf"
////            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
////            startActivityForResult(intent, SELECT_PDF)
////            FilePopup!!.dismiss()
////            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())
////
////
////        })
//
//        LayoutDocuments.setOnClickListener {
//
//            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                type = "*/*"
//                putExtra(
//                    Intent.EXTRA_MIME_TYPES,
//                    arrayOf(
//                        "application/pdf",
//                        "application/msword",
//                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
//                        "application/vnd.ms-excel",
//                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
//                        "application/vnd.ms-powerpoint",
//                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
//                        "text/plain"
//                    )
//                )
//                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//                addCategory(Intent.CATEGORY_OPENABLE)
//            }
//
//            startActivityForResult(intent, SELECT_DOCUMENT)
//            FilePopup?.dismiss()
//
//            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())
//
//        }
//
//
//    }
//
//
//    @SuppressLint("LongLogTag")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode != Activity.RESULT_OK) return
//
//        fun addPath(uri: Uri, forcedType: FileType? = null) {
//            if (isAttachmentAdapterPosition == RecyclerView.NO_POSITION) return
//            if (isAttachmentAdapterPosition >= itemList.size) return
//
//            val item = itemList[isAttachmentAdapterPosition]
//            val fileName = getFileName(uri)
//
//
//            val type = forcedType ?: when {
//                fileName.endsWith(".pdf", true) -> FileType.PDF
//                fileName.endsWith(".doc", true) || fileName.endsWith(".docx", true) -> FileType.DOC
//                fileName.endsWith(".xls", true) || fileName.endsWith(".xlsx", true) -> FileType.EXCEL
//                fileName.endsWith(".ppt", true) || fileName.endsWith(".pptx", true) -> FileType.PPT
//                fileName.matches(".*\\.(jpg|jpeg|png|webp)$".toRegex(RegexOption.IGNORE_CASE)) -> FileType.IMAGE
//                fileName.endsWith(".txt", true) -> FileType.TXT
//                else -> FileType.OTHER
//            }
//
//            if (!canAddAttachment(item, type)) return
//
//            item.file_path?.add(FilePath(uri.toString(), type.name))
//            CommonUtil.Remaining = CommonUtil.Remaining - 1
//        }
//
//        when (requestCode) {
//
//            // CAMERA
//            REQUEST_Camera -> {
//                imageFilePath?.let {
//                    CommonUtil.SelcetedFileList.add(it)
//                    addPath(Uri.fromFile(File(it)), FileType.IMAGE)
//                    internshipAdapter?.notifyItemChanged(isAttachmentAdapterPosition)
//                }
//            }
//
//            // GALLERY
//            REQUEST_GAllery -> {
//                val images = data?.getStringArrayListExtra("images") ?: return
//                images.forEach { path ->
//                    CommonUtil.SelcetedFileList.add(path)
//                    addPath(Uri.parse(path), FileType.IMAGE)
//                }
//                internshipAdapter?.notifyItemChanged(isAttachmentAdapterPosition)
//            }
//
//            // DOCUMENTS
//            SELECT_DOCUMENT -> {
//                data?.let { intent ->
//                    val itemCount = intent.clipData?.itemCount ?: if (intent.data != null) 1 else 0
//                    for (i in 0 until itemCount) {
//                        val uri = intent.clipData?.getItemAt(i)?.uri ?: intent.data ?: continue
//                        CommonUtil.SelcetedFileList.add(uri.toString())
//                        addPath(uri)
//                    }
//                    internshipAdapter?.notifyItemChanged(isAttachmentAdapterPosition)
//                }
//            }
//        }
//
//        updateFileCountUI()
//        isAttachmentAdapterPosition = RecyclerView.NO_POSITION
//    }
//
//
//    private fun canAddAttachment(
//        quizItem: GetInternshipDetailsData,
//        newType: FileType
//    ): Boolean {
//
//        val attachments = quizItem.file_path ?: return true
//
//        // ---- TOTAL FILE LIMIT ----
//        if (attachments.size >= MAX_FILES_PER_QUESTION) {
//            Toast.makeText(
//                this,
//                "Only $MAX_FILES_PER_QUESTION files allowed per question",
//                Toast.LENGTH_SHORT
//            ).show()
//            return false
//        }
//
//        // ---- VIDEO LIMIT ----
//        if (newType == FileType.VIDEO) {
//            val videoCount = attachments.count {
//                it.type == FileType.VIDEO.toString()
//            }
//
//            if (videoCount >= MAX_VIDEO_PER_QUESTION) {
//                Toast.makeText(
//                    this,
//                    "Only $MAX_VIDEO_PER_QUESTION videos allowed per question",
//                    Toast.LENGTH_SHORT
//                ).show()
//                return false
//            }
//        }
//
//        return true
//    }
//
//
//
//    @SuppressLint("Range")
//    private fun getFileName(uri: Uri): String {
//        var result: String? = null
//        if (uri.scheme == "content") {
//            val cursor = contentResolver.query(uri, null, null, null, null)
//            cursor?.use {
//                if (it.moveToFirst()) {
//                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                }
//            }
//        }
//        if (result == null) {
//            result = uri.path
//            val cut = result?.lastIndexOf('/')
//            if (cut != null && cut != -1) {
//                result = result?.substring(cut + 1)
//            }
//        }
//        return result ?: ""
//    }
//    @Throws(IOException::class)
//    fun createImageFile(): File? {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//        val imageFileName = "IMG_" + timeStamp + "_"
//        val storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val image = File.createTempFile(
//            imageFileName,  /* prefix */
//            ".jpg",  /* suffix */
//            storageDir /* directory */
//        )
//        imageFilePath = image.absolutePath
//        return image
//    }
//
//    fun ReadAndWriteFile(uri: Uri?, type: String) {
//        if (uri == null) return
//
//        try {
//            val inputStream = contentResolver.openInputStream(uri) ?: return
//            PDFTempFileWrite = File.createTempFile("File_", type, outputDir)
//            val pdfPath = PDFTempFileWrite!!.absolutePath
//
//            inputStream.use { input ->
//                FileOutputStream(PDFTempFileWrite!!).use { output ->
//                    input.copyTo(output)
//                }
//            }
//
//            CommonUtil.SelcetedFileList.add(pdfPath)
//            Log.d("PDFTempFileWrite", pdfPath)
//            Log.d("extensionpdf", File(pdfPath).extension)
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Log.e("ReadAndWriteFile", "Error reading file: ${e.message}")
//        }
//    }
//
//
//
//    /**
//     * Updates the label showing number of selected files
//     */
//    private fun updateFileCountUI() {
//        if (CommonUtil.SelcetedFileList.isNotEmpty()) {
//            Totalfile = CommonUtil.SelcetedFileList.size.toString()
//            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())
//            Log.d("FinalAdapterData", internshipAdapter.getUpdatedList().toString())
//
//        }
//        else{
//            Totalfile = CommonUtil.SelcetedFileList.size.toString()
//            Log.d("SelectedFileList", CommonUtil.SelcetedFileList.toString())
//            Log.d("FinalAdapterData", internshipAdapter.getUpdatedList().toString())
//        }
//    }
//
//
//}