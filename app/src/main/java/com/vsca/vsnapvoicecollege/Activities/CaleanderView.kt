package com.vsca.vsnapvoicecollege.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.shrikanthravi.collapsiblecalendarview.data.Day
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar.CalendarListener
import com.vsca.vsnapvoicecollege.R


class CaleanderView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caleander_view)


        val collapsibleCalendar = findViewById<CollapsibleCalendar>(R.id.calendarView)


        collapsibleCalendar.setCalendarListener(object : CalendarListener {
            override fun onDaySelect() {
                val day: Day = collapsibleCalendar.selectedDay

                Log.i(
                    javaClass.name, "Selected Day: "
                            + day.year + "/" + (day.month + 1) + "/" + day.day
                )
            }

            override fun onItemClick(view: View) {}
            override fun onDataUpdate() {}
            override fun onMonthChange() {}
            override fun onWeekChange(i: Int) {}
        })


    }
}