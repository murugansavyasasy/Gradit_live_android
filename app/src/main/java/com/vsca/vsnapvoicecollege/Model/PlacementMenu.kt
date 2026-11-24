package com.vsca.vsnapvoicecollege.Model

/** GET student/placement-menu (Placement hub options). Fields nullable: may be missing. */
class PlacementMenuResponse(
    val status: Boolean = false,
    val message: String? = null,
    val data: List<PlacementMenuItem>? = null
)

class PlacementMenuItem(
    val id: Int? = null,               // 1 Training, 2 Events, 3 Resume Builder, 4 Feedback
    val name: String? = null,
    val description: String? = null,
    val sortOrder: Int? = null
)
