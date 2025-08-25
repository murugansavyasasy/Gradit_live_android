package com.vsca.vsnapvoicecollege.Model

data class PlacementEventData( val eventId: Int,
                               val eventTitle: String,
                               val aboutEvent: String,
                               val modeOfEvent: String,
                               val eventDate: String,
                               val eventTime: String,
                               val venue: String,
                               val companyDetails: List<CompanyDetail>,
                               val eligibleCourses: List<String>, // comes as JSON string -> "[\"aeronautical engineering\"]"
                               val eligibleCriteria: String,
                               val selectionProcess: List<String>,
//                               val eventNoModeOfEvent: Int,
//                               val createdOn: String,
                               val shortlistCount: String)
