package com.vsca.vsnapvoicecollege.Model

data class PlacementEventResponse(  val status: Boolean,
                                    val message: String,
                                    val data: List<GetPlacementEventData>)
