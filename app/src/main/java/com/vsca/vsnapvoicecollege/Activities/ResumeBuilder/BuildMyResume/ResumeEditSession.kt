package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

/**
 * Keeps the resume being edited between screens (Build My Resume -> Preview -> Customize).
 *
 * Flow:
 * 1. BuildMyResume (Proceed) calls [start] with the context it sent to resume/post-resume,
 *    plus the chosen template, colour and member id.
 * 2. Customize Your Resume loads with [generateContentRequest] -> resume/generate-content.
 * 3. "Regenerate with AI" (resume/post-edited-resume) or "Apply Changes to Template"
 *    (resume/post-resume) sends [resumePdfRequest]. On success [onEditedResumeGenerated] stores
 *    the edited context, so the NEXT "Edit Resume" starts from these edits instead of the
 *    original Build My Resume data.
 *
 * [latestContext] is always kept in the generate-content request format
 * (`profile` = photo URL, no AI-only fields).
 */
object ResumeEditSession {

    private const val TAG = "ResumeEditSession"

    /** Latest resume content in the generate-content request format. */
    private var latestContext: JsonObject? = null

    var templateNumber: Int = -1
        private set
    var themeColor: String = ""
        private set
    var idMember: Int = -1
        private set

    /** False after the app was killed (globals lost) or before a resume was generated. */
    val isReady: Boolean
        get() = latestContext != null && templateNumber > 0 && themeColor.isNotEmpty()

    /** A new resume was generated from Build My Resume: start over from its data. */
    fun start(context: JsonObject, templateNumber: Int, themeColor: String, idMember: Int) {
        latestContext = context.deepCopy()
        this.templateNumber = templateNumber
        this.themeColor = themeColor
        this.idMember = idMember
        Log.d(TAG, "start: template=$templateNumber color=$themeColor idMember=$idMember")
    }

    /** Body for resume/generate-content: { "context": latestContext }. */
    fun generateContentRequest(): JsonObject? {
        val context = latestContext ?: return null
        return JsonObject().apply { add("context", context.deepCopy()) }
    }

    /**
     * Body for building a PDF from the edited context, used by both Customize buttons:
     * "Regenerate with AI" (resume/post-edited-resume) and "Apply Changes to Template"
     * (resume/post-resume). Same template, colour, bucket and member as Build My Resume;
     * only the context is the edited one (summary, role, descriptions, profileImage included).
     */
    fun resumePdfRequest(editedContext: JsonObject): JsonObject = JsonObject().apply {
        addProperty("bucket", CommonUtil.isRBBucketName)
//        addProperty("bucketPath", CommonUtil.isRBBucketPath)
        addProperty("bucketPath", CommonUtil.resumeBucketPath())
        addProperty("idMember", idMember)
        addProperty("templateNumber", templateNumber)
        addProperty("themeColor", themeColor)
        add("context", editedContext.deepCopy())
    }

    /** Regenerate succeeded: the edited data becomes the source for the next edit. */
    fun onEditedResumeGenerated(editedContext: JsonObject) {
        latestContext = toGenerateContentFormat(editedContext)
        Log.d(TAG, "latest context updated from edit: $latestContext")
    }

    /**
     * Edited (post-edited-resume) format -> generate-content request format:
     * `profileImage` goes back to `profile`, and the AI-only fields (summary, role,
     * descriptions) are dropped so the AI writes them again from the edited details.
     */
    private fun toGenerateContentFormat(edited: JsonObject): JsonObject {
        val context = edited.deepCopy()
        val photo = edited.get("profileImage")?.takeIf { it.isJsonPrimitive }?.asString.orEmpty()
        context.remove("profileImage")
        context.remove("role")
        context.addProperty("profile", photo)
        listOf("internship", "certifications", "projects").forEach { key ->
            (context.get(key) as? JsonArray)?.forEach { (it as? JsonObject)?.remove("description") }
        }
        return context
    }
}
