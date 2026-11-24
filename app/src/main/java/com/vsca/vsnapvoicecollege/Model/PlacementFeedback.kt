package com.vsca.vsnapvoicecollege.Model

/**
 * Placement Feedback (Placement hub > Placement Feedback), resume/placement backend.
 * GET student/feedback/list  -> PlacementFeedbackListResponse
 * POST student/feedback/add  -> PlacementFeedbackAddResponse
 * Everything nullable: fields may be missing.
 */
class PlacementFeedbackListResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: List<PlacementFeedbackItem>? = null
)

class PlacementFeedbackAddResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: PlacementFeedbackItem? = null
)

class PlacementFeedbackItem(
    val feedbackId: Int? = null,
    val topic: String? = null,
    val message: String? = null,
    val status: String? = null,          // "new" / "replied"
    val createdOn: String? = null,       // ISO UTC, e.g. 2026-11-23T10:15:30.000Z
    val updatedOn: String? = null,
    val replyCount: Int? = null,
    val lastReply: PlacementFeedbackReply? = null,
    val unread: Boolean? = null          // a reply the student has not seen yet
)

class PlacementFeedbackReply(
    val message: String? = null,
    val from: String? = null,            // e.g. "officer"
    val createdOn: String? = null
)
