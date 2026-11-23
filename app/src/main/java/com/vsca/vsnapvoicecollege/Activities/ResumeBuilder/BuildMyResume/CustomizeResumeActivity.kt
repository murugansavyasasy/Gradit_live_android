package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume

import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Model.ResumeAiCertificate
import com.vsca.vsnapvoicecollege.Model.ResumeAiContext
import com.vsca.vsnapvoicecollege.Model.ResumeAiEducation
import com.vsca.vsnapvoicecollege.Model.ResumeAiInternship
import com.vsca.vsnapvoicecollege.Model.ResumeAiProject
import com.vsca.vsnapvoicecollege.Model.ResumeBuilderGenerateResumeResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.applyPriorityColor
import com.vsca.vsnapvoicecollege.Utils.CustomSwitch
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityCustomizeResumeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Customize Your Resume (Preview in BuildMyResume mode > "Edit Resume").
 *
 * 1. Loads AI content: resume/generate-content with ResumeEditSession.generateContentRequest()
 *    (the Build My Resume data the first time, the last saved edit after that).
 * 2. The student edits it in 8 collapsible sections. Each section can be Included / Hidden,
 *    and entries / chips can be ticked on or off.
 * 3. "Regenerate with AI" (after a confirmation) sends the edits to resume/post-edited-resume.
 *    On success the new PDF URL goes back to ResumePreviewActivity (RESULT_OK + EXTRA_FILE_URL)
 *    and ResumeEditSession remembers these edits for the next "Edit Resume".
 *    On failure the student stays here and sees the error.
 *    "Apply Changes to Template" does the same with resume/post-resume (edits used as-is).
 *
 * Rules: every visible field of an included section is required; a hidden section, an unticked
 * entry or an unticked chip is simply left out ("" for text, [] for lists).
 */
class CustomizeResumeActivity : BaseActivity<ActivityCustomizeResumeBinding>() {

    override var appViewModel: App? = null

    override fun inflateBinding(): ActivityCustomizeResumeBinding =
        ActivityCustomizeResumeBinding.inflate(layoutInflater)

    override val layoutResourceId: Int
        get() = R.layout.activity_customize_resume

    companion object {
        private const val TAG = "CustomizeResume"

        /** RESULT_OK extra: URL of the PDF built from the edits. */
        const val EXTRA_FILE_URL = "EditedResumeFileUrl"

        private const val DATE_PATTERN = "dd MMM yyyy"   // e.g. "01 Sep 2026"
        private const val TINT = "#F0EFFD"
    }

    // ---------------------------------------------------------------------------------------
    // Screen state
    // ---------------------------------------------------------------------------------------

    private enum class SectionId {
        PERSONAL, SUMMARY, ROLE, EDUCATION, SKILLS, INTERNSHIP, CERTIFICATIONS, PROJECTS
    }

    /** One section card: its views, whether it is open and whether it goes into the resume. */
    private inner class Section(
        val id: SectionId, val card: View, val hasAiBadge: Boolean, val mandatory: Boolean = false
    ) {
        val header: View = card.findViewById(R.id.layoutSectionHeader)
        val icon: ImageView = card.findViewById(R.id.imgSectionIcon)
        val title: TextView = card.findViewById(R.id.lblSectionTitle)
        val aiBadge: TextView = card.findViewById(R.id.lblSectionAiBadge)
        val sub: TextView = card.findViewById(R.id.lblSectionSub)
        val includePill: TextView = card.findViewById(R.id.btnSectionInclude)
        val chevron: ImageView = card.findViewById(R.id.imgSectionChevron)
        val body: View = card.findViewById(R.id.layoutSectionBody)
        val content: LinearLayout = card.findViewById(R.id.layoutSectionContent)
        var open = false
        var included = true
    }

    private val sections = LinkedHashMap<SectionId, Section>()

    // Personal information
    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtAddress: EditText
    private lateinit var imgPhoto: ImageView
    private lateinit var switchPhoto: CustomSwitch
    private lateinit var lblPhotoStatus: TextView
    private lateinit var lblPhotoHint: TextView
    private var photoUrl = ""
    private var photoAvailable = false
    private var photoLoadFailed = false

    // Summary / role
    private lateinit var edtSummary: EditText
    private lateinit var edtRole: EditText

    // Entries: the data plus the checkbox / inputs that edit it
    private class EducationRow(val data: ResumeAiEducation, val check: CheckBox)
    private class InternshipForm(
        val check: CheckBox, val designation: EditText, val company: EditText,
        val from: EditText, val to: EditText, val description: EditText
    )
    private class CertificateForm(
        val data: ResumeAiCertificate, val check: CheckBox,
        val courseName: EditText, val description: EditText
    )
    private class ProjectForm(val check: CheckBox, val title: EditText, val description: EditText)
    private class SkillChip(val label: String, var on: Boolean = true)

    private val educationRows = mutableListOf<EducationRow>()
    private val internshipForms = mutableListOf<InternshipForm>()
    private val certificateForms = mutableListOf<CertificateForm>()
    private val projectForms = mutableListOf<ProjectForm>()

    /** Skill chips by request key (skills / areainterest / languages / softSkill). */
    private val skillChips = linkedMapOf<String, MutableList<SkillChip>>()

    private var isContentLoaded = false

    /**
     * The two ways to build the PDF from the edits. Both validate, confirm, send
     * ResumeEditSession.resumePdfRequest() and handle the result the same way; only the
     * endpoint differs.
     */
    private enum class PdfAction(val titleRes: Int, val confirmRes: Int) {
        REGENERATE(R.string.txt_cr_regenerate, R.string.txt_cr_regenerate_confirm),   // post-edited-resume
        APPLY(R.string.txt_cr_apply, R.string.txt_cr_apply_confirm)                   // post-resume
    }

    /** Action waiting for its API answer (null = none); also ignores re-delivered results. */
    private var pendingAction: PdfAction? = null
    private var pendingEditedContext: JsonObject? = null

    /** Edited context right after loading; compared on exit to detect changes. */
    private var initialSnapshot: String? = null

    private val accent by lazy { ContextCompat.getColor(this, R.color.dark_blue) }

    // ---------------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this, noActionBar = true)
        super.onCreate(savedInstanceState)
        binding = ActivityCustomizeResumeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdgeAuto(
            rootView = binding.OverallLayout,
            statusBarBgView = binding.statusBarBackground,
            priority = CommonUtil.Priority
        )
        if (supportActionBar != null) {
            ActionBarMethod(this, true)
            fixActionBarOverlap(binding.lblCustomizeHeader)
        }
        binding.lblCustomizeHeader.applyPriorityColor(this, CommonUtil.Priority)

        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        iconStart(binding.btnCrRegenerate, R.drawable.ic_cr_spark, 17, accent)
        iconStart(binding.btnCrApply, R.drawable.ic_cr_template, 17, Color.WHITE)
        iconStart(binding.lblBeforeFinish, R.drawable.ic_cr_info, 16, Color.parseColor("#3A3A40"))

        binding.imgback.setOnClickListener { handleExit() }
        binding.btnCrCancel.setOnClickListener { handleExit() }
        binding.btnRetry.setOnClickListener { loadContent() }
        binding.btnCrRegenerate.setOnClickListener { onPdfActionClicked(PdfAction.REGENERATE) }
        binding.btnCrApply.setOnClickListener { onPdfActionClicked(PdfAction.APPLY) }

        observeApis()
        loadContent()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        handleExit()
    }

    // ---------------------------------------------------------------------------------------
    // APIs
    // ---------------------------------------------------------------------------------------

    private fun observeApis() {
        appViewModel!!.GenerateResumeContent!!.observe(this) { response ->
            val context = response?.data?.context
            if (response != null && response.status && context != null) {
                Log.d(TAG, "Content loaded")
                showContent(context)
            } else {
                showLoadError(response?.message)
            }
        }

        appViewModel!!.PostEditedResume!!.observe(this) { response ->
            onPdfResult(PdfAction.REGENERATE, response)
        }
        appViewModel!!.ApplyResumeChanges!!.observe(this) { response ->
            onPdfResult(PdfAction.APPLY, response)
        }
    }

    /**
     * PDF built from the edits (either action). Success: remember the edits for the next
     * "Edit Resume" and hand the new URL back to the preview. Failure: stay here and show why.
     */
    private fun onPdfResult(action: PdfAction, response: ResumeBuilderGenerateResumeResponse?) {
        // Only the answer to an action started on this screen
        if (pendingAction != action) return
        pendingAction = null

        val result = response?.data?.firstOrNull()
        val fileUrl = result?.file_url
        if (response != null && response.status && result?.status == 1 && !fileUrl.isNullOrBlank()) {
            Log.d(TAG, "$action success: $fileUrl")
            pendingEditedContext?.let { ResumeEditSession.onEditedResumeGenerated(it) }
            setResult(RESULT_OK, Intent().putExtra(EXTRA_FILE_URL, fileUrl))
            finish()
        } else {
            val message = result?.message?.takeIf { it.isNotBlank() }
                ?: response?.message?.takeIf { it.isNotBlank() }
                ?: CommonUtil.Something_went_wrong
            Log.d(TAG, "$action failed: $message")
            CommonUtil.ApiAlert(this, message)
        }
    }

    /** resume/generate-content with the latest resume data. */
    private fun loadContent() {
        val request = ResumeEditSession.generateContentRequest()
        if (request == null) {
            Toast.makeText(this, R.string.txt_cr_session_missing, Toast.LENGTH_LONG).show()
            finish()
            return
        }
        binding.layoutLoadError.visibility = View.GONE
        appViewModel!!.requestGenerateResumeContent(request, this)
    }

    private fun showLoadError(message: String?) {
        Log.d(TAG, "Content load failed: $message")
        isContentLoaded = false
        binding.layoutSections.removeAllViews()
        binding.layoutLoadError.visibility = View.VISIBLE
        binding.lblLoadError.text = message?.takeIf { it.isNotBlank() }
            ?: getString(R.string.txt_cr_load_failed)
    }

    // ---------------------------------------------------------------------------------------
    // Build the screen from the AI content
    // ---------------------------------------------------------------------------------------

    private fun showContent(context: ResumeAiContext) {
        binding.layoutLoadError.visibility = View.GONE
        binding.layoutSections.removeAllViews()
        sections.clear()
        educationRows.clear()
        internshipForms.clear()
        certificateForms.clear()
        projectForms.clear()
        skillChips.clear()

        // Personal information is mandatory: always included (no Hidden), all 4 fields required.
        // Opened right away when the API left any of them empty, so the student sees what to fill.
        val personalMissing = listOf(context.name, context.email, context.phone, context.address)
            .any { it.isNullOrBlank() }
        addSection(
            SectionId.PERSONAL, R.string.txt_cr_personal, R.drawable.ic_cr_person,
            mandatory = true, startOpen = personalMissing
        ) {
            buildPersonal(it, context)
        }
        addSection(SectionId.SUMMARY, R.string.txt_cr_summary, R.drawable.ic_cr_spark, ai = true) {
            edtSummary = addField(
                it, R.string.txt_cr_summary_label, context.profile, minLines = 8, first = true
            )
        }
        addSection(SectionId.ROLE, R.string.txt_cr_role, R.drawable.ic_cr_brief, ai = true) {
            edtRole = addField(
                it, R.string.txt_cr_role_label, context.role,
                helper = R.string.txt_cr_role_hint, first = true
            )
        }
        // List sections: not shown at all when the API sends an empty list or null.
        // (A section that is not shown is sent as [] — see buildEditedContext / validate.)
        if (!context.education.isNullOrEmpty()) {
            addSection(SectionId.EDUCATION, R.string.txt_cr_education, R.drawable.ic_cr_cap) {
                buildEducation(it, context.education.orEmpty())
            }
        }
        val hasSkills = listOf(context.skills, context.areainterest, context.languages, context.softSkill)
            .any { list -> list.orEmpty().any { it.isNotBlank() } }
        if (hasSkills) {
            addSection(SectionId.SKILLS, R.string.txt_cr_skills, R.drawable.ic_cr_code) {
                buildSkills(it, context)
            }
        }
        if (!context.internship.isNullOrEmpty()) {
            addSection(SectionId.INTERNSHIP, R.string.txt_cr_internship, R.drawable.ic_cr_building) {
                buildInternships(it, context.internship.orEmpty())
            }
        }
        if (!context.certifications.isNullOrEmpty()) {
            addSection(SectionId.CERTIFICATIONS, R.string.txt_cr_certifications, R.drawable.ic_cr_award) {
                buildCertificates(it, context.certifications.orEmpty())
            }
        }
        if (!context.projects.isNullOrEmpty()) {
            addSection(SectionId.PROJECTS, R.string.txt_cr_projects, R.drawable.ic_cr_layers) {
                buildProjects(it, context.projects.orEmpty())
            }
        }
        Log.d(TAG, "Sections shown: ${sections.keys}")

        isContentLoaded = true
        initialSnapshot = buildEditedContext().toString()
    }

    /**
     * Adds one section card; [build] fills its body.
     * [mandatory]: always included — shows a fixed "Required" pill instead of Included/Hidden.
     */
    private fun addSection(
        id: SectionId, titleRes: Int, iconRes: Int, ai: Boolean = false,
        mandatory: Boolean = false, startOpen: Boolean = false,
        build: (LinearLayout) -> Unit
    ) {
        val card = LayoutInflater.from(this)
            .inflate(R.layout.item_cr_section, binding.layoutSections, false)
        val section = Section(id, card, ai, mandatory)
        section.open = startOpen
        sections[id] = section

        section.icon.setImageResource(iconRes)
        section.icon.imageTintList = ColorStateList.valueOf(accent)
        section.title.setText(titleRes)
        iconStart(section.aiBadge, R.drawable.ic_cr_spark, 11, accent)
        build(section.content)

        section.header.setOnClickListener {
            section.open = !section.open
            renderSection(section)
        }
        section.includePill.setOnClickListener {
            if (section.mandatory) return@setOnClickListener   // cannot be hidden
            section.included = !section.included
            Log.d(TAG, "${section.id} included=${section.included}")
            renderSection(section)
        }

        binding.layoutSections.addView(card)
        renderSection(section)
    }

    /** Open/closed body, Included/Hidden pill, title colour and subtitle. */
    private fun renderSection(section: Section) {
        section.body.visibility = if (section.open) View.VISIBLE else View.GONE
        section.chevron.rotation = if (section.open) 180f else 0f

        val inc = section.included
        section.title.setTextColor(Color.parseColor(if (inc) "#000000" else "#8E8E93"))

        if (section.mandatory) {
            // Fixed "Required" pill (accent), not a toggle
            section.includePill.setText(R.string.txt_cr_required_section)
            section.includePill.setTextColor(accent)
            section.includePill.background = rounded(Color.parseColor(TINT), null, 14f)
            section.includePill.setCompoundDrawablesRelative(null, null, null, null)
            section.includePill.isClickable = false
            section.aiBadge.visibility = if (section.hasAiBadge) View.VISIBLE else View.GONE
            section.sub.visibility = if (section.hasAiBadge) View.GONE else View.VISIBLE
            section.sub.text = subtitleFor(section)
            return
        }

        val pillColor = Color.parseColor(if (inc) "#1E7B38" else "#6B6B73")
        section.includePill.setText(if (inc) R.string.txt_cr_included else R.string.txt_cr_hidden)
        section.includePill.setTextColor(pillColor)
        section.includePill.background = rounded(
            Color.parseColor(if (inc) "#E7F6EC" else "#F2F2F5"), null, 14f
        )
        section.includePill.setCompoundDrawablesRelative(
            sizedIcon(if (inc) R.drawable.ic_cr_eye else R.drawable.ic_cr_eye_off, 14), null, null, null
        )
        TextViewCompat.setCompoundDrawableTintList(section.includePill, ColorStateList.valueOf(pillColor))

        section.aiBadge.visibility = if (section.hasAiBadge) View.VISIBLE else View.GONE
        section.sub.visibility = if (section.hasAiBadge) View.GONE else View.VISIBLE
        section.sub.text = subtitleFor(section)
    }

    private fun refreshSubtitle(id: SectionId) {
        sections[id]?.let { it.sub.text = subtitleFor(it) }
    }

    private fun subtitleFor(section: Section): String = when (section.id) {
        SectionId.PERSONAL ->
            if (section.open || !this::edtName.isInitialized) getString(R.string.txt_cr_update_basic)
            else listOf(edtName.text, edtPhone.text).filter { it.isNotBlank() }.joinToString(" · ")
        SectionId.EDUCATION -> countText(educationRows.count { it.check.isChecked }, educationRows.size)
        SectionId.INTERNSHIP -> countText(internshipForms.count { it.check.isChecked }, internshipForms.size)
        SectionId.CERTIFICATIONS -> countText(certificateForms.count { it.check.isChecked }, certificateForms.size)
        SectionId.PROJECTS -> countText(projectForms.count { it.check.isChecked }, projectForms.size)
        SectionId.SKILLS -> getString(
            R.string.txt_cr_skills_summary,
            selectedChips("skills").size, selectedChips("areainterest").size,
            selectedChips("languages").size
        )
        else -> ""
    }

    /** "3 entries", "1 entry" or "2 of 3 selected". */
    private fun countText(selected: Int, total: Int): String = when {
        selected == total && total == 1 -> getString(R.string.txt_cr_entry, total)
        selected == total -> getString(R.string.txt_cr_entries, total)
        else -> getString(R.string.txt_cr_selected_of, selected, total)
    }

    // ---- Personal information ----

    private fun buildPersonal(parent: LinearLayout, context: ResumeAiContext) {
        val photoRow = LayoutInflater.from(this).inflate(R.layout.item_cr_photo, parent, false)
        parent.addView(photoRow)
        imgPhoto = photoRow.findViewById(R.id.imgCrPhoto)
        switchPhoto = photoRow.findViewById(R.id.switchCrPhoto)
        lblPhotoStatus = photoRow.findViewById(R.id.lblCrPhotoStatus)
        lblPhotoHint = photoRow.findViewById(R.id.lblCrPhotoHint)
        setupPhoto(context.profileImage)

        edtName = addField(parent, R.string.txt_cr_full_name, context.name)
        edtEmail = addField(
            parent, R.string.txt_cr_email, context.email,
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        )
        edtPhone = addField(parent, R.string.txt_cr_phone, context.phone, inputType = InputType.TYPE_CLASS_PHONE)
        edtAddress = addField(parent, R.string.txt_cr_address, context.address, minLines = 3)
    }

    /**
     * Photo from profileImage. No URL, a bad URL or a failed load -> dashed circle,
     * "No photo uploaded" and the switch locked off. Otherwise the photo with the switch on.
     */
    private fun setupPhoto(url: String?) {
        photoUrl = url?.trim().orEmpty().takeUnless { it.equals("null", ignoreCase = true) }.orEmpty()
        photoAvailable = photoUrl.isNotEmpty() && URLUtil.isNetworkUrl(photoUrl)
        photoLoadFailed = photoUrl.isNotEmpty() && !photoAvailable

        // Locked when there is no photo (CustomSwitch toggles on any touch otherwise)
        switchPhoto.setOnTouchListener { _, _ -> !photoAvailable }
        switchPhoto.setOnClickListener { renderPhoto() }
        switchPhoto.setChecked(photoAvailable)
        renderPhoto()

        if (!photoAvailable) return
        Glide.with(this)
            .load(photoUrl)
            .placeholder(R.drawable.default_profile)
            .error(R.drawable.default_profile)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Drawable>, isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "Photo failed to load: ${e?.message}")
                    binding.root.post {
                        if (isFinishing || isDestroyed) return@post
                        photoAvailable = false
                        photoLoadFailed = true
                        switchPhoto.setChecked(false)
                        renderPhoto()
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable, model: Any, target: Target<Drawable>?,
                    dataSource: DataSource, isFirstResource: Boolean
                ): Boolean = false
            })
            .into(imgPhoto)
    }

    private fun renderPhoto() {
        val on = photoAvailable && switchPhoto.isChecked()
        switchPhoto.alpha = if (photoAvailable) 1f else 0.4f
        lblPhotoHint.visibility = if (photoAvailable) View.GONE else View.VISIBLE

        if (photoAvailable) {
            imgPhoto.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#E7E5FB"))
                setStroke(dp(2), if (on) accent else Color.WHITE)
            }
            imgPhoto.setPadding(dp(2), dp(2), dp(2), dp(2))
            imgPhoto.imageTintList = null
        } else {
            imgPhoto.setBackgroundResource(R.drawable.bg_resume_photo_empty)
            imgPhoto.setPadding(dp(22), dp(22), dp(22), dp(22))
            imgPhoto.setImageResource(R.drawable.ic_cr_person)
            imgPhoto.imageTintList = ColorStateList.valueOf(Color.parseColor("#8E8E93"))
        }

        when {
            !photoAvailable -> setStatus(
                if (photoLoadFailed) R.string.txt_photo_load_failed else R.string.txt_no_photo_uploaded,
                R.drawable.ic_cr_info, "#B4530A", "#FF9500"
            )
            on -> setStatus(R.string.txt_photo_selected, R.drawable.ic_cr_check, "#1E7B38", "#34C759")
            else -> setStatus(R.string.txt_cr_photo_not_selected, R.drawable.ic_cr_info, "#6B6B73", "#FF9500")
        }
    }

    private fun setStatus(textRes: Int, iconRes: Int, textColor: String, iconColor: String) {
        lblPhotoStatus.setText(textRes)
        lblPhotoStatus.setTextColor(Color.parseColor(textColor))
        lblPhotoStatus.setCompoundDrawablesRelative(sizedIcon(iconRes, 13), null, null, null)
        TextViewCompat.setCompoundDrawableTintList(
            lblPhotoStatus, ColorStateList.valueOf(Color.parseColor(iconColor))
        )
    }

    // ---- Education (tick only, as in the design) ----

    private fun buildEducation(parent: LinearLayout, list: List<ResumeAiEducation>) {
        list.forEachIndexed { index, edu ->
            val row = inflateEntry(parent, editable = false, index = index)
            val check = row.findViewById<CheckBox>(R.id.cbEntry)
            row.findViewById<TextView>(R.id.lblEntryTitle).text = edu.classDegree.orEmpty()
            setEntrySub(row, listOfNotNull(
                edu.institution?.takeIf { it.isNotBlank() },
                edu.percentage?.takeIf { it.isNotBlank() }?.let { "$it%" }
            ).joinToString(" · "))
            educationRows.add(EducationRow(edu, check))
            onEntryToggle(row, check, SectionId.EDUCATION)
        }
    }

    // ---- Skills & interests (chips) ----

    private fun buildSkills(parent: LinearLayout, context: ResumeAiContext) {
        val groups = listOf(
            Triple("skills", R.string.txt_cr_technical_skills, context.skills),
            Triple("areainterest", R.string.txt_cr_areas_interest, context.areainterest),
            Triple("languages", R.string.txt_cr_languages, context.languages),
            Triple("softSkill", R.string.txt_cr_soft_skills, context.softSkill)
        )
        var first = true
        groups.forEach { (key, labelRes, values) ->
            val chips = values.orEmpty().filter { it.isNotBlank() }.map { SkillChip(it.trim()) }.toMutableList()
            skillChips[key] = chips
            if (chips.isEmpty()) return@forEach

            parent.addView(TextView(this).apply {
                setText(labelRes)
                setTextColor(Color.parseColor("#3A3A40"))
                textSize = 13f
                typeface = ResourcesCompat.getFont(this@CustomizeResumeActivity, R.font.poppins_medium)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { if (!first) topMargin = dp(16) }
            })
            first = false

            val flex = FlexboxLayout(this).apply {
                flexWrap = com.google.android.flexbox.FlexWrap.WRAP
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(8) }
            }
            chips.forEach { chip -> flex.addView(createChip(chip)) }
            parent.addView(flex)
        }
    }

    private fun createChip(chip: SkillChip): TextView = TextView(this).apply {
        text = chip.label
        textSize = 13f
        typeface = ResourcesCompat.getFont(this@CustomizeResumeActivity, R.font.poppins_medium)
        gravity = Gravity.CENTER_VERTICAL
        compoundDrawablePadding = dp(5)
        setPadding(dp(12), 0, dp(12), 0)
        layoutParams = FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(34)).apply {
            setMargins(0, 0, dp(8), dp(8))
        }
        fun render() {
            val color = if (chip.on) accent else Color.parseColor("#3A3A40")
            setTextColor(color)
            background = rounded(
                if (chip.on) Color.parseColor(TINT) else Color.WHITE,
                if (chip.on) accent else Color.parseColor("#D9D9E0"), 17f
            )
            setCompoundDrawablesRelative(
                if (chip.on) sizedIcon(R.drawable.ic_cr_check, 12) else null, null, null, null
            )
            TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(accent))
        }
        render()
        setOnClickListener {
            chip.on = !chip.on
            render()
            refreshSubtitle(SectionId.SKILLS)
        }
    }

    private fun selectedChips(key: String): List<String> =
        skillChips[key].orEmpty().filter { it.on }.map { it.label }

    // ---- Internships ----

    private fun buildInternships(parent: LinearLayout, list: List<ResumeAiInternship>) {
        list.forEachIndexed { index, item ->
            val card = inflateEntry(parent, editable = true, index = index)
            val check = card.findViewById<CheckBox>(R.id.cbEntry)
            val title = card.findViewById<TextView>(R.id.lblEntryTitle)
            val fields = card.findViewById<LinearLayout>(R.id.layoutEntryFields)

            val row1 = twoColumnRow(fields)
            val designation = addField(row1, R.string.txt_cr_designation, item.designation, weighted = true)
            val company = addField(row1, R.string.txt_cr_company, item.companyName, weighted = true, startGap = true)
            val row2 = twoColumnRow(fields)
            val from = addDateField(row2, R.string.txt_cr_from, item.from, startGap = false)
            val to = addDateField(row2, R.string.txt_cr_to, item.to, startGap = true)
            val description = addField(fields, R.string.txt_cr_description, item.description, minLines = 6, ai = true)

            // Header shows the designation / company as they are edited
            title.text = item.designation.orEmpty()
            setEntrySub(card, item.companyName.orEmpty())
            designation.onChange { title.text = it }
            company.onChange { setEntrySub(card, it) }

            internshipForms.add(InternshipForm(check, designation, company, from, to, description))
            onEntryToggle(card, check, SectionId.INTERNSHIP)
        }
    }

    // ---- Certifications ----

    private fun buildCertificates(parent: LinearLayout, list: List<ResumeAiCertificate>) {
        list.forEachIndexed { index, item ->
            val card = inflateEntry(parent, editable = true, index = index)
            val check = card.findViewById<CheckBox>(R.id.cbEntry)
            val title = card.findViewById<TextView>(R.id.lblEntryTitle)
            val fields = card.findViewById<LinearLayout>(R.id.layoutEntryFields)

            title.text = item.courseName.orEmpty()
            setEntrySub(card, listOfNotNull(
                item.institute?.takeIf { it.isNotBlank() }, item.duration?.takeIf { it.isNotBlank() }
            ).joinToString(" · "))
            val courseName = addField(fields, R.string.txt_cr_course_name, item.courseName)
            val description = addField(fields, R.string.txt_cr_description, item.description, minLines = 5, ai = true)
            courseName.onChange { title.text = it }

            certificateForms.add(CertificateForm(item, check, courseName, description))
            onEntryToggle(card, check, SectionId.CERTIFICATIONS)
        }
    }

    // ---- Projects ----

    private fun buildProjects(parent: LinearLayout, list: List<ResumeAiProject>) {
        list.forEachIndexed { index, item ->
            val card = inflateEntry(parent, editable = true, index = index)
            val check = card.findViewById<CheckBox>(R.id.cbEntry)
            val title = card.findViewById<TextView>(R.id.lblEntryTitle)
            val fields = card.findViewById<LinearLayout>(R.id.layoutEntryFields)

            title.text = item.title.orEmpty()
            val projectTitle = addField(fields, R.string.txt_cr_project_title, item.title)
            val description = addField(fields, R.string.txt_cr_description, item.description, minLines = 5, ai = true)
            projectTitle.onChange { title.text = it }

            projectForms.add(ProjectForm(check, projectTitle, description))
            onEntryToggle(card, check, SectionId.PROJECTS)
        }
    }

    // ---------------------------------------------------------------------------------------
    // Regenerate with AI / Apply Changes to Template
    // ---------------------------------------------------------------------------------------

    /** Validate, confirm, then build the PDF from the edits. */
    private fun onPdfActionClicked(action: PdfAction) {
        if (!isContentLoaded || pendingAction != null) return
        if (!validate()) return
        AlertDialog.Builder(this)
            .setTitle(action.titleRes)
            .setMessage(action.confirmRes)
            .setPositiveButton(CommonUtil.Yes) { dialog, _ ->
                dialog.dismiss()
                sendPdfAction(action)
            }
            .setNegativeButton(R.string.txt_cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    /**
     * Same body for both (template, colour, bucket, member + the full edited context incl.
     * summary, role, descriptions and photo); only the endpoint differs:
     * Regenerate -> resume/post-edited-resume, Apply -> resume/post-resume.
     */
    private fun sendPdfAction(action: PdfAction) {
        val edited = buildEditedContext()
        val request = ResumeEditSession.resumePdfRequest(edited)
        pendingEditedContext = edited
        pendingAction = action
        Log.d(TAG, "$action: $request")
        when (action) {
            PdfAction.REGENERATE -> appViewModel!!.requestPostEditedResume(request, this)
            PdfAction.APPLY -> appViewModel!!.requestApplyResumeChanges(request, this)
        }
    }

    /**
     * The edited resume in the post-edited-resume `context` format.
     * Hidden sections, unticked entries and unticked chips are left out ("" / []).
     */
    private fun buildEditedContext(): JsonObject {
        fun included(id: SectionId) = sections[id]?.included == true
        val personal = included(SectionId.PERSONAL)

        return JsonObject().apply {
            addProperty("name", if (personal) edtName.value() else "")
            addProperty("email", if (personal) edtEmail.value() else "")
            addProperty("phone", if (personal) edtPhone.value() else "")
            addProperty("address", if (personal) edtAddress.value() else "")
            addProperty(
                "profileImage",
                if (personal && photoAvailable && switchPhoto.isChecked()) photoUrl else ""
            )
            addProperty("profile", if (included(SectionId.SUMMARY)) edtSummary.value() else "")
            addProperty("role", if (included(SectionId.ROLE)) edtRole.value() else "")

            val skillsOn = included(SectionId.SKILLS)
            listOf("skills", "areainterest", "softSkill", "languages").forEach { key ->
                add(key, stringArray(if (skillsOn) selectedChips(key) else emptyList()))
            }

            add("education", JsonArray().apply {
                if (included(SectionId.EDUCATION)) educationRows.filter { it.check.isChecked }.forEach { row ->
                    add(JsonObject().apply {
                        addProperty("classDegree", row.data.classDegree.orEmpty())
                        addProperty("institution", row.data.institution.orEmpty())
                        addProperty("percentage", row.data.percentage.orEmpty())
                        add("file_path", JsonArray())
                    })
                }
            })
            add("internship", JsonArray().apply {
                if (included(SectionId.INTERNSHIP)) internshipForms.filter { it.check.isChecked }.forEach { f ->
                    add(JsonObject().apply {
                        addProperty("designation", f.designation.value())
                        addProperty("companyName", f.company.value())
                        addProperty("from", f.from.value())
                        addProperty("to", f.to.value())
                        addProperty("description", f.description.value())
                    })
                }
            })
            add("certifications", JsonArray().apply {
                if (included(SectionId.CERTIFICATIONS)) certificateForms.filter { it.check.isChecked }.forEach { f ->
                    add(JsonObject().apply {
                        addProperty("courseName", f.courseName.value())
                        addProperty("institute", f.data.institute.orEmpty())
                        addProperty("duration", f.data.duration.orEmpty())
                        addProperty("description", f.description.value())
                    })
                }
            })
            add("projects", JsonArray().apply {
                if (included(SectionId.PROJECTS)) projectForms.filter { it.check.isChecked }.forEach { f ->
                    add(JsonObject().apply {
                        addProperty("title", f.title.value())
                        addProperty("description", f.description.value())
                    })
                }
            })
        }
    }

    /**
     * Every visible field of an included section must be filled (ticked entries only).
     * Shows the first problem: opens its section, marks the field and scrolls to it.
     */
    private fun validate(): Boolean {
        fun included(id: SectionId) = sections[id]?.included == true
        val missing = getString(R.string.txt_cr_required)
        val checks = mutableListOf<Triple<SectionId, EditText, String?>>()   // field + error (null = ok)

        fun required(id: SectionId, field: EditText) =
            checks.add(Triple(id, field, if (field.value().isEmpty()) missing else null))

        if (included(SectionId.PERSONAL)) {
            required(SectionId.PERSONAL, edtName)
            val email = edtEmail.value()
            checks.add(Triple(SectionId.PERSONAL, edtEmail, when {
                email.isEmpty() -> missing
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.txt_cr_invalid_email)
                else -> null
            }))
            required(SectionId.PERSONAL, edtPhone)
            required(SectionId.PERSONAL, edtAddress)
        }
        if (included(SectionId.SUMMARY)) required(SectionId.SUMMARY, edtSummary)
        if (included(SectionId.ROLE)) required(SectionId.ROLE, edtRole)
        if (included(SectionId.INTERNSHIP)) internshipForms.filter { it.check.isChecked }.forEach { f ->
            required(SectionId.INTERNSHIP, f.designation)
            required(SectionId.INTERNSHIP, f.company)
            required(SectionId.INTERNSHIP, f.from)
            val from = parseDate(f.from.value())
            val to = parseDate(f.to.value())
            checks.add(Triple(SectionId.INTERNSHIP, f.to, when {
                f.to.value().isEmpty() -> missing
                from != null && to != null && to.before(from) -> getString(R.string.txt_cr_to_before_from)
                else -> null
            }))
            required(SectionId.INTERNSHIP, f.description)
        }
        if (included(SectionId.CERTIFICATIONS)) certificateForms.filter { it.check.isChecked }.forEach { f ->
            required(SectionId.CERTIFICATIONS, f.courseName)
            required(SectionId.CERTIFICATIONS, f.description)
        }
        if (included(SectionId.PROJECTS)) projectForms.filter { it.check.isChecked }.forEach { f ->
            required(SectionId.PROJECTS, f.title)
            required(SectionId.PROJECTS, f.description)
        }

        checks.forEach { (_, field, error) -> field.error = error }
        val firstError = checks.firstOrNull { it.third != null } ?: return true

        Log.d(TAG, "Validation failed in ${firstError.first}")
        sections[firstError.first]?.let { section ->
            if (!section.open) {
                section.open = true
                renderSection(section)
            }
        }
        scrollTo(firstError.second)
        Toast.makeText(this, R.string.txt_cr_fill_required, Toast.LENGTH_SHORT).show()
        return false
    }

    // ---------------------------------------------------------------------------------------
    // Leaving the screen
    // ---------------------------------------------------------------------------------------

    /** Cancel / back: ask before throwing away edits. */
    private fun handleExit() {
        val changed = isContentLoaded && initialSnapshot != null &&
                buildEditedContext().toString() != initialSnapshot
        if (!changed) {
            finish()
            return
        }
        AlertDialog.Builder(this)
            .setTitle(R.string.txt_cr_discard_title)
            .setMessage(R.string.txt_cr_discard_message)
            .setPositiveButton(R.string.txt_cr_discard) { _, _ -> finish() }
            .setNegativeButton(R.string.txt_cr_keep_editing) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // ---------------------------------------------------------------------------------------
    // View helpers
    // ---------------------------------------------------------------------------------------

    /** Label + input (item_cr_field). Multi-line when [minLines] > 1. */
    private fun addField(
        parent: ViewGroup, labelRes: Int, value: String?,
        minLines: Int = 1, ai: Boolean = false, helper: Int? = null,
        inputType: Int = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES,
        first: Boolean = false, weighted: Boolean = false, startGap: Boolean = false
    ): EditText {
        val view = LayoutInflater.from(this).inflate(R.layout.item_cr_field, parent, false)
        view.findViewById<TextView>(R.id.lblFieldLabel).setText(labelRes)
        val badge = view.findViewById<TextView>(R.id.lblFieldAiBadge)
        badge.visibility = if (ai) View.VISIBLE else View.GONE
        iconStart(badge, R.drawable.ic_cr_spark, 11, accent)
        helper?.let {
            view.findViewById<TextView>(R.id.lblFieldHelper).apply {
                setText(it)
                visibility = View.VISIBLE
            }
        }
        val edit = view.findViewById<EditText>(R.id.edtField)
        if (minLines > 1) {
            edit.inputType = inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            edit.minLines = minLines
            edit.gravity = Gravity.TOP or Gravity.START
            edit.isSingleLine = false
        } else {
            edit.inputType = inputType
            edit.isSingleLine = true
        }
        edit.setText(value.orEmpty())

        view.layoutParams = if (weighted) {
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                topMargin = dp(14)
                if (startGap) marginStart = dp(10)
            }
        } else {
            (view.layoutParams as ViewGroup.MarginLayoutParams).apply { if (first) topMargin = 0 }
        }
        parent.addView(view)
        return edit
    }

    /** Read-only date input with a calendar icon; tap opens a date picker ("01 Sep 2026"). */
    private fun addDateField(parent: ViewGroup, labelRes: Int, value: String?, startGap: Boolean): EditText {
        val edit = addField(parent, labelRes, value, weighted = true, startGap = startGap)
        edit.isFocusable = false
        edit.isCursorVisible = false
        edit.setCompoundDrawablesRelative(sizedIcon(R.drawable.ic_cr_calendar, 15), null, null, null)
        edit.compoundDrawablePadding = dp(8)
        TextViewCompat.setCompoundDrawableTintList(edit, ColorStateList.valueOf(Color.parseColor("#6B6B73")))
        edit.setOnClickListener {
            val cal = Calendar.getInstance()
            parseDate(edit.value())?.let { cal.time = it }
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day)
                edit.setText(SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH).format(cal.time))
                edit.error = null
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
        return edit
    }

    private fun parseDate(text: String): Date? = try {
        SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH).apply { isLenient = false }.parse(text)
    } catch (e: Exception) {
        null
    }

    private fun twoColumnRow(parent: LinearLayout): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        parent.addView(this)
    }

    /** One entry (item_cr_entry). Editable entries get the bordered card look. */
    private fun inflateEntry(parent: LinearLayout, editable: Boolean, index: Int): View {
        val entry = LayoutInflater.from(this).inflate(R.layout.item_cr_entry, parent, false)
        val params = entry.layoutParams as ViewGroup.MarginLayoutParams
        if (editable) {
            entry.setBackgroundResource(R.drawable.bg_cr_entry)
            entry.setPadding(dp(12), dp(12), dp(12), dp(12))
            if (index > 0) params.topMargin = dp(10)
        } else {
            entry.setPadding(dp(2), dp(12), dp(2), dp(12))
            if (index > 0) {
                parent.addView(View(this).apply {
                    setBackgroundColor(Color.parseColor("#F0F0F3"))
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
                })
            }
        }
        entry.layoutParams = params
        parent.addView(entry)
        return entry
    }

    private fun setEntrySub(entry: View, text: String) {
        entry.findViewById<TextView>(R.id.lblEntrySub).apply {
            this.text = text
            visibility = if (text.isBlank()) View.GONE else View.VISIBLE
        }
    }

    /** Tapping the entry header ticks / unticks it. */
    private fun onEntryToggle(entry: View, check: CheckBox, sectionId: SectionId) {
        entry.findViewById<View>(R.id.layoutEntryHeader).setOnClickListener {
            check.isChecked = !check.isChecked
            refreshSubtitle(sectionId)
        }
    }

    private fun scrollTo(target: View) {
        binding.scrollCustomize.post {
            val rect = Rect()
            target.getDrawingRect(rect)
            binding.scrollCustomize.offsetDescendantRectToMyCoords(target, rect)
            binding.scrollCustomize.smoothScrollTo(0, (rect.top - dp(80)).coerceAtLeast(0))
            target.requestFocus()
        }
    }

    private fun rounded(fill: Int, stroke: Int?, radiusDp: Float) = GradientDrawable().apply {
        cornerRadius = radiusDp * resources.displayMetrics.density
        setColor(fill)
        if (stroke != null) setStroke(dp(1), stroke)
    }

    private fun sizedIcon(res: Int, sizeDp: Int): Drawable? =
        ContextCompat.getDrawable(this, res)?.mutate()?.apply { setBounds(0, 0, dp(sizeDp), dp(sizeDp)) }

    /** Start icon of a TextView at [sizeDp], tinted (the design icons are 24dp vectors). */
    private fun iconStart(view: TextView, res: Int, sizeDp: Int, color: Int) {
        view.setCompoundDrawablesRelative(sizedIcon(res, sizeDp), null, null, null)
        TextViewCompat.setCompoundDrawableTintList(view, ColorStateList.valueOf(color))
    }

    private fun stringArray(values: List<String>) = JsonArray().apply { values.forEach { add(it) } }

    private fun EditText.value(): String = text.toString().trim()

    private fun EditText.onChange(block: (String) -> Unit) {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) = block(s?.toString().orEmpty())
        })
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
