package com.vsca.vsnapvoicecollege.Model

/**
 * resume/generate-content (Customize Your Resume screen).
 * Response: { status, message, data: { context: ResumeAiContext } }.
 *
 * Note the names differ from the request: in the response `profile` is the AI summary text and
 * the photo URL is `profileImage` (in the generate-content request, `profile` is the photo URL).
 * Everything is nullable/var: the server may leave fields out, and the screen edits them in place.
 */
class GenerateResumeContentResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: GenerateResumeContentData? = null
)

class GenerateResumeContentData(
    val context: ResumeAiContext? = null
)

class ResumeAiContext(
    var name: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var profileImage: String? = null,   // photo URL
    var profile: String? = null,        // AI professional summary
    var role: String? = null,           // AI career role
    var skills: List<String>? = null,
    var areainterest: List<String>? = null,
    var softSkill: List<String>? = null,
    var languages: List<String>? = null,
    var education: List<ResumeAiEducation>? = null,
    var internship: List<ResumeAiInternship>? = null,
    var certifications: List<ResumeAiCertificate>? = null,
    var projects: List<ResumeAiProject>? = null
)

class ResumeAiEducation(
    var classDegree: String? = null,
    var institution: String? = null,
    var percentage: String? = null
)

class ResumeAiInternship(
    var designation: String? = null,
    var companyName: String? = null,
    var from: String? = null,
    var to: String? = null,
    var description: String? = null
)

class ResumeAiCertificate(
    var courseName: String? = null,
    var institute: String? = null,
    var duration: String? = null,
    var description: String? = null
)

class ResumeAiProject(
    var title: String? = null,
    var description: String? = null
)
