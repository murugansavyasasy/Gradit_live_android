package com.vsca.vsnapvoicecollege.Model

/**
 * A New Event time preset chip, e.g. "Morning (9-10 AM)".
 * startMinutes / endMinutes are minutes from midnight (9:00 AM = 540).
 */
data class EventTimePreset(
    val label: String,
    val startMinutes: Int,
    val endMinutes: Int
)
