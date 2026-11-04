
package com.vsca.vsnapvoicecollege.Activities

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.content.ContextCompat
import com.vsca.vsnapvoicecollege.Adapters.TimetableAdapter
import com.vsca.vsnapvoicecollege.Model.TimetableModel
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.applyPriorityColor
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityTimetableBinding

class TimetableActivity : BaseActivity<ActivityTimetableBinding>() {

    override var appViewModel: App? = null

    override fun inflateBinding(): ActivityTimetableBinding {
        return ActivityTimetableBinding.inflate(layoutInflater)
    }
    private var timetableAdapter: TimetableAdapter? = null

    private var selectedDay = ""
    private var selectedDayId = "0"

    override val layoutResourceId: Int
        get() = R.layout.activity_timetable

    override fun onCreate(savedInstanceState: Bundle?) {

        CommonUtil.SetTheme(this, noActionBar = true)

        super.onCreate(savedInstanceState)

        binding = ActivityTimetableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CommonUtil.OnMenuClicks("Time Table")


        setupEdgeToEdgeAuto(
            rootView = binding.Main,
            statusBarBgView = binding.statusBarBackground,
            priority = CommonUtil.Priority
        )

        if (supportActionBar != null) {
            ActionBarMethod(this, true)
            fixActionBarOverlap(binding.LayoutHeader)
        }

        binding.LayoutHeader.applyPriorityColor(this, CommonUtil.Priority)

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        setupRecyclerView()

        observeTimeTableResponse()

        setupDaySelection()

        selectDay("Monday")
        isGetTimeTable()

        binding.imgheaderBack.setOnClickListener {
            onBackPressed()
        }
    }
    private fun setupRecyclerView() {

        timetableAdapter = TimetableAdapter()

        binding.rvTimetable.apply {
            layoutManager = LinearLayoutManager(this@TimetableActivity)
            adapter = timetableAdapter
            itemAnimator = null
        }
    }

    private fun setupDaySelection() {

        binding.dayMon.setOnClickListener {
            if (selectedDay != "Monday") {
                selectDay("Monday")
                isGetTimeTable()
            }
        }

        binding.dayTue.setOnClickListener {
            if (selectedDay != "Tuesday") {
                selectDay("Tuesday")
                isGetTimeTable()
            }
        }

        binding.dayWed.setOnClickListener {
            if (selectedDay != "Wednesday") {
                selectDay("Wednesday")
                isGetTimeTable()
            }
        }

        binding.dayThu.setOnClickListener {
            if (selectedDay != "Thursday") {
                selectDay("Thursday")
                isGetTimeTable()
            }
        }

        binding.dayFri.setOnClickListener {
            if (selectedDay != "Friday") {
                selectDay("Friday")
                isGetTimeTable()
            }
        }

        binding.daySat.setOnClickListener {
            if (selectedDay != "Saturday") {
                selectDay("Saturday")
                isGetTimeTable()
            }
        }

        binding.daySun.setOnClickListener {
            if (selectedDay != "Sunday") {
                selectDay("Sunday")
                isGetTimeTable()
            }
        }
    }
    private fun selectDay(day: String) {

        if (selectedDay == day) return

        selectedDay = day

        selectedDayId = when (day) {
            "Monday" -> "1"
            "Tuesday" -> "2"
            "Wednesday" -> "3"
            "Thursday" -> "4"
            "Friday" -> "5"
            "Saturday" -> "6"
            "Sunday" -> "7"
            else -> "1"
        }

        val dayViews = listOf(
            binding.dayMon,
            binding.dayTue,
            binding.dayWed,
            binding.dayThu,
            binding.dayFri,
            binding.daySat,
            binding.daySun
        )

        val selectedIndex = when (day) {
            "Monday" -> 0
            "Tuesday" -> 1
            "Wednesday" -> 2
            "Thursday" -> 3
            "Friday" -> 4
            "Saturday" -> 5
            "Sunday" -> 6
            else -> 0
        }

        binding.tvSelectedDay.text = day

        dayViews.forEachIndexed { index, textView ->
            textView.isSelected = index == selectedIndex

            textView.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (index == selectedIndex) {
                        android.R.color.white
                    } else {
                        R.color.black
                    }
                )
            )
        }
    }
    private fun isGetTimeTable() {

        appViewModel?.isGetTimeTable(
            CommonUtil.CollegeId.toString(),
            CommonUtil.Courseid,
            CommonUtil.YearId,
            CommonUtil.SemesterId,
            CommonUtil.SectionId,
            selectedDayId,
            this
        )
    }
    private fun observeTimeTableResponse() {

        appViewModel?.isTimeTable?.observe(this) { response ->

            if (response != null) {

                if (response.status == 200 || response.status == 1) {

                    if (response.data.isNotEmpty()) {

                        binding.ErrorMessage.visibility = View.GONE
                        binding.rvTimetable.visibility = View.VISIBLE

                        isLoadTimeTable(response.data)

                    } else {

                        binding.rvTimetable.visibility = View.GONE
                        binding.ErrorMessage.visibility = View.VISIBLE
                        binding.ErrorMessage.text = "No Record Found"

                        binding.tvPeriodCount.text = "0 periods · 0 breaks"
                    }

                } else {

                    binding.rvTimetable.visibility = View.GONE
                    binding.ErrorMessage.visibility = View.VISIBLE

                    binding.ErrorMessage.text =
                        response.message.ifBlank { "No Record Found" }

                    binding.tvPeriodCount.text = "0 periods · 0 breaks"
                }

            } else {

                binding.rvTimetable.visibility = View.GONE
                binding.ErrorMessage.visibility = View.VISIBLE
                binding.ErrorMessage.text = "No Record Found"

                binding.tvPeriodCount.text = "0 periods · 0 breaks"
            }
        }
    }

    private fun isLoadTimeTable(
        timetableData: List<TimetableModel>
    ) {

        val sortedData = timetableData.sortedBy { it.order }

        timetableAdapter?.setData(ArrayList(sortedData))

        binding.rvTimetable.apply {
            layoutManager = LinearLayoutManager(this@TimetableActivity)
            adapter = timetableAdapter
            visibility = View.VISIBLE
        }

        binding.ErrorMessage.visibility = View.GONE

        val periodCount = timetableAdapter?.getPeriodCount() ?: 0

        val breakCount = timetableAdapter?.getBreakCount() ?: 0

        binding.tvPeriodCount.text =
            "$periodCount periods · $breakCount break${if (breakCount != 1) "s" else ""}"
    }

}
