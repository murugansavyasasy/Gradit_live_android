package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.vsca.vsnapvoicecollege.Activities.*
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.ResumeBuilder
import com.vsca.vsnapvoicecollege.ActivitySender.Hall_Ticket
import com.vsca.vsnapvoicecollege.ActivitySender.PunchStaffAttendanceUsingFinger
import com.vsca.vsnapvoicecollege.ActivitySender.StaffWiseAttendanceReports
import com.vsca.vsnapvoicecollege.Model.DashboardOverall
import com.vsca.vsnapvoicecollege.Model.MenuDetailsResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class DashboardParent constructor(
    private val categoriesModalArrayList: ArrayList<DashboardOverall>,
    private val context: Context
) : RecyclerView.Adapter<DashboardParent.ViewHolder>() {

    // Shared pool for all child recyclers
    private val sharedViewPool = RecyclerView.RecycledViewPool()

    companion object {
        private const val MENU_COLUMNS = 4
        private const val MENU_ROWS = 3
    }

    init {
        // CRITICAL: Menu grid shows ~20 items but default pool is 5.
        // Increase so ALL menu ViewHolders survive recycling.
        sharedViewPool.setMaxRecycledViews(DashboardChild.TYPE_MENU, 23)
        sharedViewPool.setMaxRecycledViews(DashboardChild.TYPE_DASHBOARD, 9)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dashboard_parent_design, parent, false)
        return ViewHolder(view)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = categoriesModalArrayList[position]

        // Header setup
        when (modal.menuHeadings) {
            "Ad" -> {
                holder.category.setText(R.string.txt_advertisement)
                holder.lblViewAll.visibility = View.INVISIBLE
            }
            "Circular" -> {
                holder.category.text = context.getString(R.string.txt_img_pdf)
                holder.lblViewAll.visibility = View.VISIBLE
            }
            "DashBoard_Menu" -> {
                holder.category.text = "Menu"
                holder.category.visibility= View.GONE
                holder.lblViewAll.visibility = View.GONE
            }
            else -> {
                holder.category.text = modal.menuHeadings
                holder.lblViewAll.visibility = View.VISIBLE
            }
        }

        holder.lblNoRecords.text = if (modal.menuHeadings == "Attendance") {
            "Today's attendance is not yet available"
        } else ""

        // Bind section — reuse adapters, don't create new ones
        when (modal.menuHeadings) {
            "Notice Board" -> reuseChildAdapter(holder, modal, "Notice Board", LinearLayoutManager.HORIZONTAL)
            "Leave Request" -> reuseChildAdapter(holder, modal, "Leave Request", LinearLayoutManager.HORIZONTAL)
            "DashBoard_Menu" -> bindDashboardMenu(holder, modal)
            "Circular" -> reuseChildAdapter(holder, modal, "Circular", LinearLayoutManager.HORIZONTAL)
            "Chat" -> reuseChildAdapter(holder, modal, "Chat", LinearLayoutManager.HORIZONTAL)
            "Upcoming Events" -> reuseChildAdapter(holder, modal, "Upcoming Events", LinearLayoutManager.HORIZONTAL)
            "Assignments" -> reuseChildAdapter(holder, modal, "Assignments", LinearLayoutManager.HORIZONTAL)
            "Emergency Notification" -> reuseChildAdapter(holder, modal, "Emergency Notification", LinearLayoutManager.HORIZONTAL)
            "Ad" -> reuseChildAdapter(holder, modal, "Ad", LinearLayoutManager.VERTICAL)
            "Recent Notifications" -> reuseChildAdapter(holder, modal, "Recent Notifications", LinearLayoutManager.VERTICAL)
            "Attendance" -> reuseChildAdapter(holder, modal, "Attendance", LinearLayoutManager.VERTICAL)
            else -> {
                holder.recyclerDashboardTitle.adapter = null
                holder.recyclerDashboardTitle.layoutManager = null
                holder.currentChildType = null

                holder.recyclerDashboardTitle.visibility = View.VISIBLE
                holder.viewPagerDashboardMenu.visibility = View.GONE
                holder.lblSwipeForMore.visibility = View.GONE

                holder.pageChangeCallback?.let {
                    holder.viewPagerDashboardMenu.unregisterOnPageChangeCallback(it)
                    holder.pageChangeCallback = null
                }
            }
        }

        // Click listeners (set fresh every bind in case holder was recycled)
        setupClickListeners(holder, modal)
    }

    private fun setupClickListeners(holder: ViewHolder, modal: DashboardOverall) {
        when (modal.menuHeadings) {
            "Notice Board" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(7) { r, w ->
                    CommonUtil.menu_readNoticeBoard = r
                    CommonUtil.menu_writeNoticeBoard = w
                }
                context.startActivity(Intent(context, Noticeboard::class.java))
            }
            "Leave Request" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(4) { r, w ->
                    CommonUtil.menu_readAttendance = r
                    CommonUtil.menu_writeAttendance = w
                }
                CommonUtil.MenuIdAttendance = BaseActivity.AttendanceMeuID
                context.startActivity(Intent(context, Attendance::class.java))
            }
            "Circular" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(6) { r, w ->
                    CommonUtil.menu_readCircular = r
                    CommonUtil.menu_writeCircular = w
                }
                context.startActivity(Intent(context, Circular::class.java))
            }
            "Chat" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(11) { r, w ->
                    CommonUtil.menu_readChat = r
                    CommonUtil.menu_writeChat = w
                }
                context.startActivity(Intent(context, ChatParent::class.java))
            }
            "Upcoming Events" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(8) { r, w ->
                    CommonUtil.menu_readEvent = r
                    CommonUtil.menu_writeEvent = w
                }
                context.startActivity(Intent(context, Events::class.java))
            }
            "Assignments" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(5) { r, w ->
                    CommonUtil.menu_readAssignment = r
                    CommonUtil.menu_writeAssignment = w
                }
                context.startActivity(Intent(context, Assignment::class.java))
            }
            "Emergency Notification" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(16) { r, w ->
                    CommonUtil.menu_readCommunication = r
                    CommonUtil.menu_writeCommunication = w
                }
                CommonUtil.MenuIDCommunication = BaseActivity.CommunicationMenuID
                context.startActivity(Intent(context, Communication::class.java))
            }
            "Recent Notifications" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(16) { r, w ->
                    CommonUtil.menu_readCommunication = r
                    CommonUtil.menu_writeCommunication = w
                }
                CommonUtil.MenuIDCommunication = BaseActivity.CommunicationMenuID
                context.startActivity(Intent(context, Communication::class.java))
            }

            "Attendance" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(4) { r, w ->
                    CommonUtil.menu_readAttendance = r
                    CommonUtil.menu_writeAttendance = w
                }
                CommonUtil.MenuIdAttendance = BaseActivity.AttendanceMeuID
                context.startActivity(Intent(context, Attendance::class.java))
            }
            else -> holder.lblViewAll.setOnClickListener(null)
        }
    }

    private fun bindDashboardMenu(holder: ViewHolder, modal: DashboardOverall) {
        holder.recyclerDashboardTitle.visibility = View.GONE
        holder.viewPagerDashboardMenu.visibility = View.VISIBLE

        val menuList = (modal.DashboardMenuData ?: emptyList()).filter { it.id != 1 }

        if (menuList.isEmpty()) {
            holder.viewPagerDashboardMenu.visibility = View.GONE
            holder.lblSwipeForMore.visibility = View.GONE
            return
        }

        // Clean up any stale callback from a recycled holder
        holder.pageChangeCallback?.let { holder.viewPagerDashboardMenu.unregisterOnPageChangeCallback(it) }
        holder.pageChangeCallback = null

        val lp = holder.viewPagerDashboardMenu.layoutParams
        lp.height = calculateMenuGridHeight(menuList)
        holder.viewPagerDashboardMenu.layoutParams = lp

        val pagerAdapter = DashboardMenuPagerAdapter(
            context = holder.itemView.context,
            menuList = menuList,
            onMenuClick = { data -> ParticularMenuClick(data) }
        )

        holder.viewPagerDashboardMenu.adapter = pagerAdapter
        holder.viewPagerDashboardMenu.offscreenPageLimit = 1

        // Initial hint state
        val totalPages = pagerAdapter.itemCount
        holder.lblSwipeForMore.visibility = if (totalPages > 1) View.VISIBLE else View.GONE

        // Dynamic hide when user reaches the last page
        if (totalPages > 1) {
            val callback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    // Show hint if there IS a next page; hide if this is the last page
                    holder.lblSwipeForMore.visibility =
                        if (position < totalPages - 1) View.VISIBLE else View.GONE
                }
            }
            holder.pageChangeCallback = callback
            holder.viewPagerDashboardMenu.registerOnPageChangeCallback(callback)
        }
    }

    private fun reuseChildAdapter(
        holder: ViewHolder,
        modal: DashboardOverall,
        expectedType: String,
        orientation: Int
    ) {
        holder.recyclerDashboardTitle.visibility = View.VISIBLE
        holder.viewPagerDashboardMenu.visibility = View.GONE
        holder.lblSwipeForMore.visibility = View.GONE

        holder.pageChangeCallback?.let {
            holder.viewPagerDashboardMenu.unregisterOnPageChangeCallback(it)
            holder.pageChangeCallback = null
        }


        val dataList = modal.menusubitemlist ?: arrayListOf()

        if (holder.currentChildType != expectedType) {
            // First time or switched type: create layout manager + adapter
            holder.recyclerDashboardTitle.layoutManager =
                LinearLayoutManager(context, orientation, false)
            holder.recyclerDashboardTitle.setRecycledViewPool(sharedViewPool)

            holder.childAdapter = DashboardChild(
                newsModalArrayList = ArrayList(dataList),
                context = context,
                type = expectedType
            )
            holder.recyclerDashboardTitle.adapter = holder.childAdapter
            holder.currentChildType = expectedType
        } else {
            // Adapter exists — only update if item count changed
            val currentSize = holder.childAdapter?.itemCount ?: 0
            if (currentSize != dataList.size) {
                holder.childAdapter?.updateList(ArrayList(dataList))
            }
        }
    }

    private fun setMenuPermissions(menuId: Int, block: (read: String, write: String) -> Unit) {
        for (item in CommonUtil.MenuListDashboard) {
            if (item.id == menuId) {
                block(item.is_read_enabled.toString(), item.is_write_enabled.toString())
                break
            }
        }
    }

    private fun ParticularMenuClick(data: MenuDetailsResponse) {
        CommonUtil.EventStatus = "Upcoming"

        when (data.id) {
            1 -> {
                CommonUtil.menu_readHome = data.is_read_enabled.toString()
                CommonUtil.menu_writeHome = data.is_write_enabled.toString()
                if (CommonUtil.MenuDashboardHome) launch(DashBoardActivityRewamp::class.java)
            }
            3 -> {
                CommonUtil.menu_readExamination = data.is_read_enabled.toString()
                CommonUtil.menu_writeExamination = data.is_write_enabled.toString()
                CommonUtil.MenuIDExamination = data.id.toString()
                if (CommonUtil.MenuExamination) launch(ExamList::class.java)
            }
            4 -> {
                CommonUtil.MenuIdAttendance = data.id.toString()
                CommonUtil.menu_readAttendance = data.is_read_enabled.toString()
                CommonUtil.menu_writeAttendance = data.is_write_enabled.toString()
                if (CommonUtil.MenuAttendance) launch(Attendance::class.java)
            }
            5 -> {
                CommonUtil.MenuIDAssignment = data.id.toString()
                CommonUtil.menu_readAssignment = data.is_read_enabled.toString()
                CommonUtil.menu_writeAssignment = data.is_write_enabled.toString()
                if (CommonUtil.MenuAssignment) launch(Assignment::class.java)
            }
            6 -> {
                CommonUtil.MenuIDCircular = data.id.toString()
                CommonUtil.menu_readCircular = data.is_read_enabled.toString()
                CommonUtil.menu_writeCircular = data.is_write_enabled.toString()
                if (CommonUtil.MenuCircular) launch(Circular::class.java)
            }
            7 -> {
                CommonUtil.MenuIDNoticeboard = data.id.toString()
                CommonUtil.menu_readNoticeBoard = data.is_read_enabled.toString()
                CommonUtil.menu_writeNoticeBoard = data.is_write_enabled.toString()
                if (CommonUtil.MenuNoticeBoard) launch(Noticeboard::class.java)
            }
            8 -> {
                CommonUtil.MenuIDEvents = data.id.toString()
                CommonUtil.menu_readEvent = data.is_read_enabled.toString()
                CommonUtil.menu_writeEvent = data.is_write_enabled.toString()
                if (CommonUtil.MenuEvents) launch(Events::class.java)
            }
            9 -> {
                CommonUtil.menu_readFaculty = data.is_read_enabled.toString()
                CommonUtil.menu_writeFaculty = data.is_write_enabled.toString()
                if (CommonUtil.MenuFaculty) launch(Faculty::class.java)
            }
            10 -> {
                CommonUtil.menu_readVideo = data.is_read_enabled.toString()
                CommonUtil.menu_writeVideo = data.is_write_enabled.toString()
                CommonUtil.MenuIDVideo = data.id.toString()
                if (CommonUtil.MenuVideo) launch(Video::class.java)
            }
            11 -> {
                CommonUtil.menu_readChat = data.is_read_enabled.toString()
                CommonUtil.menu_writeChat = data.is_write_enabled.toString()
                CommonUtil.MenuIDChat = data.id.toString()
                if (CommonUtil.MenuChat) launch(ChatParent::class.java)
            }
            12 -> {
                CommonUtil.menu_readCourseDetails = data.is_read_enabled.toString()
                CommonUtil.menu_writeCourseDetails = data.is_write_enabled.toString()
                if (CommonUtil.MenuCourseDetails) {
                    CommonUtil.parentMenuCourseExam = 0
                    launch(CourseDetails::class.java)
                }
            }
            13 -> {
                CommonUtil.menu_readCategoryCreditPoints = data.is_read_enabled.toString()
                CommonUtil.menu_writeCategoryCreditPoints = data.is_write_enabled.toString()
                if (CommonUtil.MenuCategoryCredit) launch(CategoryCreditWise::class.java)
            }
            14 -> {
                CommonUtil.menu_readSemCreditPoints = data.is_read_enabled.toString()
                CommonUtil.menu_writeSemCreditPoints = data.is_write_enabled.toString()
                if (CommonUtil.MenuSemCredit) launch(SemesterCreditCategoryWise::class.java)
            }
            15 -> {
                CommonUtil.menu_readExamApplicationDetails = data.is_read_enabled.toString()
                CommonUtil.menu_writeExamApplicationDetails = data.is_write_enabled.toString()
                if (CommonUtil.MenuExamDetails) {
                    CommonUtil.parentMenuCourseExam = 1
                    launch(CourseDetails::class.java)
                }
            }
            16 -> {
                CommonUtil.MenuIDCommunication = data.id.toString()
                CommonUtil.menu_readCommunication = data.is_read_enabled.toString()
                CommonUtil.menu_writeCommunication = data.is_write_enabled.toString()
                if (CommonUtil.MenuCommunication) launch(Communication::class.java)
            }
            17 -> {
                CommonUtil.MenuIDCommunicationText = data.id.toString()
                CommonUtil.menu_readCommunicationText = data.is_read_enabled.toString()
                CommonUtil.menu_writeCommunicationText = data.is_write_enabled.toString()
                if (CommonUtil.MenuText) launch(MessageCommunication::class.java)
            }
            19 -> {
                CommonUtil.menu_readHallTicker = data.is_read_enabled.toString()
                CommonUtil.menu_writeHallTicker = data.is_write_enabled.toString()
                if (CommonUtil.MenuHallTicket) launch(Hall_Ticket::class.java)
            }
            20 -> {
                if (CommonUtil.MenuFeeDetails) launch(FeeDetails::class.java)
            }
            21 -> {
                CommonUtil.menu_writeMarkAttendance = data.is_write_enabled.toString()
                if (CommonUtil.MarkAttendance) launch(PunchStaffAttendanceUsingFinger::class.java)
            }
            22 -> {
                if (CommonUtil.AttendanceReport) launch(StaffWiseAttendanceReports::class.java)
            }
            23 -> {
                if (CommonUtil.PlacementTraining) launch(ResumeBuilder::class.java)
            }
            24 -> {
                if (CommonUtil.PlacementEvent) launch(PlacementEvent::class.java)
            }
            25 -> {
                if (CommonUtil.PlacementCareer) launch(CareerTraining::class.java)
            }
        }
    }

    private fun calculateMenuGridHeight(menuList: List<MenuDetailsResponse>): Int {
        if (menuList.isEmpty()) return 0

        val density = context.resources.displayMetrics.density

        // 1. Inflate off-screen
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.home_menu_list_design, null, false)

        // 2. Find and set the longest name so we measure worst-case wrapping
        val longestName = menuList.maxByOrNull { it.name?.length ?: 0 }?.name ?: "Menu"
        itemView.findViewById<TextView>(R.id.lblMenuName).text = longestName

        // 3. Measure width = screen / 4 columns, minus page padding (4dp each side)
        val screenWidth = context.resources.displayMetrics.widthPixels
        val pagePaddingPx = (8 * density).toInt()   // 4dp * 2 sides
        val columnWidth = (screenWidth - pagePaddingPx) / MENU_COLUMNS

        itemView.measure(
            View.MeasureSpec.makeMeasureSpec(columnWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        // 4. Height for 3 rows + 12dp safety margin for font-scale variations
        val singleItemHeight = itemView.measuredHeight
        val safetyPx = (12 * density).toInt()
        return (singleItemHeight * MENU_ROWS) + safetyPx
    }

    private fun launch(clazz: Class<*>) {
        val intent = Intent(context, clazz).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        context.startActivity(intent)
    }

    override fun getItemCount(): Int = categoriesModalArrayList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerDashboardTitle: RecyclerView = itemView.findViewById(R.id.recyclerDashboardTitle)
        val category: TextView = itemView.findViewById(R.id.idTVCategory)
        val lblViewAll: TextView = itemView.findViewById(R.id.lblViewAll)
        val lblNoRecords: TextView = itemView.findViewById(R.id.lblNoRecords)
        val idCVCategory: CardView = itemView.findViewById(R.id.idCVCategory)
        val viewPagerDashboardMenu: androidx.viewpager2.widget.ViewPager2 =
            itemView.findViewById(R.id.viewPagerDashboardMenu)
        val lblSwipeForMore: TextView = itemView.findViewById(R.id.lblSwipeForMore)

        var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null


        // Cache child adapter so we don't create a new one on every scroll
        var childAdapter: DashboardChild? = null

        var currentChildType: String? = null
    }
}