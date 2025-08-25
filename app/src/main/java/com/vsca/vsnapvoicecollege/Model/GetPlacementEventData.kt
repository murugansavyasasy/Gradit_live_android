package com.vsca.vsnapvoicecollege.Model

data class GetPlacementEventData (
    val memberId: Int,
    val memberName: String,
    val events: List<PlacementEventData>
)