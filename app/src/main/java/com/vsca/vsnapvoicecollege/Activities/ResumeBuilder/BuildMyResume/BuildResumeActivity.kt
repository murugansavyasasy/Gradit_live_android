package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.URLUtil
import android.widget.CheckBox
import androidx.core.widget.TextViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Adapters.AcademicAdapter
import com.vsca.vsnapvoicecollege.Adapters.CertificateAdapter
import com.vsca.vsnapvoicecollege.Adapters.InternshipAdapter
import com.vsca.vsnapvoicecollege.Adapters.ProjectAdapter
import com.vsca.vsnapvoicecollege.Adapters.SkillSetAdapter
import com.vsca.vsnapvoicecollege.Model.CertificateFormattedData
import com.vsca.vsnapvoicecollege.Model.CheckProjectDetailsData
import com.vsca.vsnapvoicecollege.Model.EducationFormattedData
import com.vsca.vsnapvoicecollege.Model.GetCertificateDetailsData
import com.vsca.vsnapvoicecollege.Model.GetEducationalDetailsData
import com.vsca.vsnapvoicecollege.Model.GetInternshipDetailsData
import com.vsca.vsnapvoicecollege.Model.GetProjectDetailsData
import com.vsca.vsnapvoicecollege.Model.InternshipFormattedData
import com.vsca.vsnapvoicecollege.Model.ResumeContextData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.applyPriorityColor
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.fullResumeData
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityBuildmyresumeBinding

class BuildResumeActivity : BaseActivity<ActivityBuildmyresumeBinding>() {
    override var appViewModel: App? = null

    override fun inflateBinding(): ActivityBuildmyresumeBinding {
        return ActivityBuildmyresumeBinding.inflate(layoutInflater)
    }

    private var selectedAcademicList: List<EducationFormattedData> = emptyList()
    private var languageList: List<String> = emptyList()
    private var softSkillList: List<String> = emptyList()
    private var programmingLanguageList: List<String> = emptyList()
    private lateinit var savedAcademicEducationDetails: List<EducationFormattedData>
    private lateinit var savedProjectDetails: List<CheckProjectDetailsData>
    private var selectedProject: List<CheckProjectDetailsData> = emptyList()
    private lateinit var internshipList: List<InternshipFormattedData>
    var selectedinternship: List<InternshipFormattedData> = emptyList()
    private var toolsPlatformList: List<String> = emptyList()
    private var areaInterestList: List<String> = emptyList()
    private lateinit var academicAdapter: AcademicAdapter
    private lateinit var intershipAdapter: InternshipAdapter
    private lateinit var projectAdapter: ProjectAdapter
    private lateinit var languageAdapter: SkillSetAdapter
    private lateinit var softSkillAdapter: SkillSetAdapter
    private var savedCertificates: List<CertificateFormattedData> = emptyList()
    private lateinit var certificateAdapter: CertificateAdapter
    var selectedCertificates: List<CertificateFormattedData> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this, noActionBar = true)
        super.onCreate(savedInstanceState)
        binding = ActivityBuildmyresumeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupEdgeToEdgeAuto(
            rootView = binding.OverallLayout,
            statusBarBgView = binding.statusBarBackground,
            priority = CommonUtil.Priority
        )

        //see right now ,we don't have the actionbar so in future when it comes here we need to give the additional padding for that so that is handled here
        if (supportActionBar != null) {
            ActionBarMethod(this, true)
            fixActionBarOverlap(binding.lblBuildMyResumeHeader)
        }
        binding.lblBuildMyResumeHeader.applyPriorityColor(this, CommonUtil.Priority)

        // Personal information: profile photo from CommonUtil.saveBasicDetails.memberImagePath
        setupProfilePhoto()



        languageAdapter = SkillSetAdapter(languageList) {}
        binding.rvLanguages.layoutManager = LinearLayoutManager(this)
        binding.rvLanguages.adapter = languageAdapter
        softSkillAdapter = SkillSetAdapter(softSkillList) { }
        binding.rvSoftSkills.layoutManager = LinearLayoutManager(this)
        binding.rvSoftSkills.adapter = softSkillAdapter
        binding.commonBottomResumeBuilder.btnDefault2.text = getString(R.string.next_)



        savedAcademicEducationDetails = CommonUtil.saveAcademicDetails?.educationalDetails
            ?.map {
                EducationFormattedData(
                    classDegree = it.classDegree,
                    percentage = it.percentage,
                    institution = it.institution,
                    isChecked = true
                )
            }
            ?: emptyList()

        savedCertificates = CommonUtil.isSkillSetDataSending?.certifications
            ?.map {
                CertificateFormattedData(
                    courseName = it.courseName,
                    institute = it.institute,
                    duration = it.duration,
                    isChecked = true
                )
            }
            ?: emptyList()


        internshipList = CommonUtil.isSkillSetDataSending?.internship
            ?.map {
                InternshipFormattedData(
                    companyName = it.companyName,
                    from = it.from,
                    designation = it.designation,
                    to = it.to,
                    isChecked = true
                )
            }
            ?: emptyList()

        savedProjectDetails = CommonUtil.isSkillSetDataSending?.projects
            ?.map { CheckProjectDetailsData(title = it.title, isChecked = true) }
            ?: emptyList()

        academicAdapter = AcademicAdapter(savedAcademicEducationDetails) { selectedItems ->
            selectedAcademicList = selectedItems
            val allChecked = selectedItems.size == savedAcademicEducationDetails.size
            binding.cbHeaderAcademicRecords.setOnCheckedChangeListener(null)
            binding.cbHeaderAcademicRecords.isChecked = allChecked
            binding.cbHeaderAcademicRecords.setOnCheckedChangeListener { _, isChecked ->
                academicAdapter.setAllChecked(isChecked)
            }
        }
        selectedAcademicList =
            savedAcademicEducationDetails//At initial setting default all as Checked so assigning the original list


        certificateAdapter = CertificateAdapter(savedCertificates) { isSelectedCertifcate ->
            selectedCertificates = isSelectedCertifcate
            val allChecked = isSelectedCertifcate.size == savedCertificates.size
            binding.cbHeaderCertificates.setOnCheckedChangeListener(null)
            binding.cbHeaderCertificates.isChecked = allChecked
            binding.cbHeaderCertificates.setOnCheckedChangeListener { _, isChecked ->
                certificateAdapter.setAllChecked(isChecked)
            }
        }
        selectedCertificates =
            savedCertificates//At initial setting default all as Checked so assigning the original list


        intershipAdapter = InternshipAdapter(internshipList) { isSelectedIntership ->
            selectedinternship = isSelectedIntership
            val allChecked = isSelectedIntership.size == internshipList.size
            binding.cbHeaderInternship.setOnCheckedChangeListener(null)
            binding.cbHeaderInternship.isChecked = allChecked
            binding.cbHeaderInternship.setOnCheckedChangeListener { _, isChecked ->
                intershipAdapter.setAllChecked(isChecked)
            }
        }
        selectedinternship =
            internshipList//At initial setting default all as Checked so assigning the original list

        binding.rvInternships.layoutManager = LinearLayoutManager(this)
        binding.rvInternships.adapter = intershipAdapter

        binding.cbHeaderInternship.setOnCheckedChangeListener { _, isChecked ->
            intershipAdapter.setAllChecked(isChecked)
        }


        projectAdapter = ProjectAdapter(savedProjectDetails) { isSelectedProject ->
            selectedProject = isSelectedProject

            val allChecked = isSelectedProject.size == savedProjectDetails.size
            binding.cbHeaderProject.setOnCheckedChangeListener(null)
            binding.cbHeaderProject.isChecked = allChecked
            binding.cbHeaderProject.setOnCheckedChangeListener { _, isChecked ->
                projectAdapter.setAllChecked(isChecked)
            }
        }

// At initial setting default all as Checked so assigning the original list
        selectedProject = savedProjectDetails

        binding.rvProjects.layoutManager = LinearLayoutManager(this)
        binding.rvProjects.adapter = projectAdapter

        binding.cbHeaderProject.setOnCheckedChangeListener { _, isChecked ->
            projectAdapter.setAllChecked(isChecked)
        }


        binding.cbHeaderAcademicRecords.setOnCheckedChangeListener { _, isChecked ->
            academicAdapter.setAllChecked(isChecked)
        }

        binding.rvAcademic.layoutManager = LinearLayoutManager(this)
        binding.rvAcademic.adapter = academicAdapter


        binding.rvCertifications.layoutManager = LinearLayoutManager(this)
        binding.rvCertifications.adapter = certificateAdapter

        binding.cbHeaderCertificates.setOnCheckedChangeListener { _, isChecked ->
            certificateAdapter.setAllChecked(isChecked)
        }


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


        setupSectionVisibility()

        setupRecycler(
            binding.rvLanguages, languageList, binding.cbHeaderLanguage, binding.layoutLanguage
        ) {
            languageList = it
        }
        setupRecycler(
            binding.rvSoftSkills,
            softSkillList,
            binding.cbHeaderSoftSkills,
            binding.layoutSoftSkills
        ) {
            softSkillList = it
        }
        setupRecycler(
            binding.rvAreasInterest,
            areaInterestList,
            binding.cbHeaderInterestArea,
            binding.layoutInterestArea
        ) {
            areaInterestList = it

        }

    }

    // ---- Personal information: profile photo ----

    /** Photo URL that can go into the resume; null when there is no usable photo. */
    private var profilePhotoUrl: String? = null

    /**
     * Shows the student's photo (memberImagePath) in the circle.
     * - Empty / null: dashed circle, "No photo uploaded", checkbox disabled.
     * - Not a web URL, or the image fails to load: placeholder, "Photo could not be loaded",
     *   checkbox disabled (a broken link must not be sent to the resume).
     * - Loads fine: photo (any size, cropped to the circle), checkbox ticked by default.
     */
    private fun setupProfilePhoto() {
        val url = CommonUtil.saveBasicDetails?.memberImagePath?.trim()
        Log.d(PHOTO_TAG, "Profile photo url: $url")

        if (url.isNullOrEmpty() || url.equals("null", ignoreCase = true)) {
            showNoProfilePhoto()
            return
        }
        if (!URLUtil.isNetworkUrl(url)) {
            Log.d(PHOTO_TAG, "Not a valid web URL, showing placeholder")
            showProfilePhotoFailed()
            return
        }

        profilePhotoUrl = url
        binding.imgResumeProfile.setBackgroundResource(R.drawable.bg_resume_photo_filled)
        binding.imgResumeProfile.setPadding(0, 0, 0, 0)
        binding.imgResumeProfile.imageTintList = null
        binding.lblProfilePhotoHint.visibility = View.GONE

        binding.cbProfilePhoto.setOnCheckedChangeListener(null)
        binding.cbProfilePhoto.isEnabled = true
        binding.cbProfilePhoto.alpha = 1f
        binding.cbProfilePhoto.isChecked = true   // included by default, like the other sections
        updateProfilePhotoStatus(true)
        binding.cbProfilePhoto.setOnCheckedChangeListener { _, isChecked ->
            updateProfilePhotoStatus(isChecked)
        }

        // Glide sizes the image to the 48dp view, so big or small photos both fit the circle
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.default_profile)
            .error(R.drawable.default_profile)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Drawable>, isFirstResource: Boolean
                ): Boolean {
                    Log.d(PHOTO_TAG, "Photo failed to load: ${e?.message}")
                    // Placeholder is shown by error(); just disable including it
                    binding.root.post { showProfilePhotoFailed(loadPlaceholder = false) }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable, model: Any, target: Target<Drawable>?,
                    dataSource: DataSource, isFirstResource: Boolean
                ): Boolean {
                    Log.d(PHOTO_TAG, "Photo loaded")
                    return false
                }
            })
            .into(binding.imgResumeProfile)
    }

    /** "Photo selected" (green) when ticked, "Photo will not be included" (grey) when not. */
    private fun updateProfilePhotoStatus(isChecked: Boolean) {
        if (isChecked) {
            setPhotoStatus(R.string.txt_photo_selected, R.drawable.ic_req_check, "#16A34A")
        } else {
            setPhotoStatus(R.string.txt_photo_not_included, null, "#64748B")
        }
    }

    /** Second design: no photo at all. */
    private fun showNoProfilePhoto() {
        profilePhotoUrl = null
        binding.imgResumeProfile.setBackgroundResource(R.drawable.bg_resume_photo_empty)
        val pad = (12 * resources.displayMetrics.density).toInt()
        binding.imgResumeProfile.setPadding(pad, pad, pad, pad)
        binding.imgResumeProfile.setImageResource(R.drawable.ic_req_person)
        binding.imgResumeProfile.imageTintList =
            ColorStateList.valueOf(Color.parseColor("#94A3B8"))
        disableProfilePhotoCheckbox()
        setPhotoStatus(R.string.txt_no_photo_uploaded, R.drawable.ic_req_info, "#C2410C")
        binding.lblProfilePhotoHint.visibility = View.VISIBLE
    }

    /** URL present but unusable: placeholder image, checkbox disabled. */
    private fun showProfilePhotoFailed(loadPlaceholder: Boolean = true) {
        if (isFinishing || isDestroyed) return
        profilePhotoUrl = null
        if (loadPlaceholder) {
            binding.imgResumeProfile.setBackgroundResource(R.drawable.bg_resume_photo_filled)
            binding.imgResumeProfile.setPadding(0, 0, 0, 0)
            binding.imgResumeProfile.imageTintList = null
            Glide.with(this).load(R.drawable.default_profile).circleCrop()
                .into(binding.imgResumeProfile)
        }
        disableProfilePhotoCheckbox()
        setPhotoStatus(R.string.txt_photo_load_failed, R.drawable.ic_req_info, "#C2410C")
        binding.lblProfilePhotoHint.visibility = View.GONE
    }

    private fun disableProfilePhotoCheckbox() {
        binding.cbProfilePhoto.setOnCheckedChangeListener(null)
        binding.cbProfilePhoto.isChecked = false
        binding.cbProfilePhoto.isEnabled = false
        binding.cbProfilePhoto.alpha = 0.4f
    }

    private fun setPhotoStatus(textRes: Int, iconRes: Int?, color: String) {
        val tint = Color.parseColor(color)
        binding.lblProfilePhotoStatus.setText(textRes)
        binding.lblProfilePhotoStatus.setTextColor(tint)
        binding.lblProfilePhotoStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
            iconRes ?: 0, 0, 0, 0
        )
        TextViewCompat.setCompoundDrawableTintList(
            binding.lblProfilePhotoStatus, ColorStateList.valueOf(tint)
        )
    }

    /** Photo URL to send with the resume: only when available and ticked, otherwise "". */
    private fun selectedProfilePhoto(): String =
        if (binding.cbProfilePhoto.isChecked) profilePhotoUrl.orEmpty() else ""

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
        if (savedProjectDetails.isNullOrEmpty()) {
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
            GetEducationalDetailsData(
                percentage = it.percentage!!,
                classDegree = it.classDegree!!,
                institution = it.institution!!
            )
        } ?: emptyList()

        val selectedCertificates =
            selectedCertificates?.filter { it.isChecked }?.map {
                GetCertificateDetailsData(
                    courseName = it.courseName,
                    institute = it.institute,
                    duration = it.duration!!
                )
            } ?: emptyList()

        val selectedInternshipList =
            selectedinternship.filter { it.isChecked }.map {
                GetInternshipDetailsData(
                    companyName = it.companyName,
                    from = it.from,
                    designation = it.designation,
                    to = it.to
                )
            } ?: emptyList()

        val selectedProjectList =
            selectedProject.filter { it.isChecked }.map {
                GetProjectDetailsData(
                    title = it.title
                )
            } ?: emptyList()

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
            // Profile photo URL if the student ticked it (and a usable photo exists), else ""
            profile = selectedProfilePhoto(),

            skills = programmingLanguageList,
            education = selectedAcademicList,
            experience = selectedInternshipList,
            areainterest = areaInterestList,
            softSkill = softSkillList,
            certifications = selectedCertificates,
            languages = languageList,
            projects = selectedProjectList
        )
        fullResumeData = resumeContext
        val gson = Gson()
        Log.d("RESUME_JSON", gson.toJson(resumeContext))
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_buildmyresume

    companion object {
        private const val PHOTO_TAG = "ResumeProfilePhoto"
    }
}
