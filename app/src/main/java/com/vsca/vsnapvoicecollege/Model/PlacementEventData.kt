package com.vsca.vsnapvoicecollege.Model

data class PlacementEventData( val eventId: Int,
                               val eventTitle: String,
                               val eventAbout: String,
                               val eventMode: String,
                               val eventDate: String,
                               val eventTime: String,
                               val eventVenue: String,
                               val eventCompanyDetails: List<CompanyDetail>,
                               val eventEligibleCourse: String, // comes as JSON string -> "[\"aeronautical engineering\"]"
                               val eventEligibleCriteria: String,
                               val eventSelectionProcess: List<String>,
                               val eventNoModeOfEvent: Int,
                               val createdOn: String,
                               val shortlistedCount: String)
