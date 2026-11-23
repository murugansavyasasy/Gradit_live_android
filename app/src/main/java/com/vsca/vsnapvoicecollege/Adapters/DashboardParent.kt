package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.SystemClock
import android.graphics.drawable.GradientDrawable
import android.widget.Toast
import androidx.core.widget.TextViewCompat
import com.vsca.vsnapvoicecollege.Utils.EdgeToEdgeUtils
import com.vsca.vsnapvoicecollege.Utils.MenuOrderPreference
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.vsca.vsnapvoicecollege.Activities.Assignment
import com.vsca.vsnapvoicecollege.Activities.Attendance
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Activities.CareerTraining
import com.vsca.vsnapvoicecollege.Activities.CategoryCreditWise
import com.vsca.vsnapvoicecollege.Activities.ChatParent
import com.vsca.vsnapvoicecollege.Activities.Circular
import com.vsca.vsnapvoicecollege.Activities.Communication
import com.vsca.vsnapvoicecollege.Activities.CourseDetails
import com.vsca.vsnapvoicecollege.Activities.DashBoardActivityRewamp
import com.vsca.vsnapvoicecollege.Activities.Events
import com.vsca.vsnapvoicecollege.Activities.ExamList
import com.vsca.vsnapvoicecollege.Activities.Faculty
import com.vsca.vsnapvoicecollege.Activities.FeeDetails
import com.vsca.vsnapvoicecollege.Activities.MessageCommunication
import com.vsca.vsnapvoicecollege.Activities.Noticeboard
import com.vsca.vsnapvoicecollege.Activities.PlacementEvent
import com.vsca.vsnapvoicecollege.Activities.PlacementHome
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.ResumeBuilder
import com.vsca.vsnapvoicecollege.Activities.SemesterCreditCategoryWise
import com.vsca.vsnapvoicecollege.Activities.TimetableActivity
import com.vsca.vsnapvoicecollege.Activities.Video
import com.vsca.vsnapvoicecollege.ActivitySender.Hall_Ticket
import com.vsca.vsnapvoicecollege.ActivitySender.PunchStaffAttendanceUsingFinger
import com.vsca.vsnapvoicecollege.ActivitySender.StaffWiseAttendanceReports
import com.vsca.vsnapvoicecollege.Model.DashboardOverall
import com.vsca.vsnapvoicecollege.Model.MenuDetailsResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class DashboardParent constructor(
    private val categoriesModalArrayList: ArrayList<DashboardOverall>,
    private val context: Context,
    // Told when menu order edit mode starts / ends (the dashboard guards back press and taps)
    private val menuEditListener: MenuEditListener? = null
) : RecyclerView.Adapter<DashboardParent.ViewHolder>() {

    // Shared pool for all child recyclers
    private val sharedViewPool = RecyclerView.RecycledViewPool()

    // ---- Menu order edit (Edit > drag and drop > Done / Cancel) ----

    interface MenuEditListener {
        fun onMenuEditModeChanged(isEditing: Boolean)
    }

    /** The "DashBoard_Menu" row: its data, view holder and the pager adapter showing it. */
    private var menuModal: DashboardOverall? = null
    private var menuHolder: ViewHolder? = null
    private var menuPagerAdapter: DashboardMenuPagerAdapter? = null
    private var dragController: MenuDragController? = null

    /** Working copy of the grid menus while editing; null = not editing. */
    private var editMenuOrder: MutableList<MenuDetailsResponse>? = null

    val isMenuEditMode: Boolean get() = editMenuOrder != null

    /** Menu row view, so the dashboard can tell taps on the grid from taps elsewhere. */
    val menuRowView: View?
        get() = menuHolder
            ?.takeIf { it.bindingAdapterPosition == categoriesModalArrayList.indexOf(menuModal) }
            ?.itemView

    /** Menus shown in the grid (Home, id 1, is never shown). */
    private fun gridMenus(modal: DashboardOverall): List<MenuDetailsResponse> =
        (modal.DashboardMenuData ?: emptyList()).filter { it.id != 1 }

    companion object {
        private const val MENU_COLUMNS = 4
        private const val MENU_ROWS = 3
        private const val MENU_EDIT_TAG = "MenuOrderEdit"
        // A little longer than the slide animation, so menus moving under the finger
        // do not trigger another move (no back-and-forth jumping)
        private const val MOVE_COOLDOWN_MS = 250L
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

        // Sort / Cancel / Done and the hint are only for the menu row (bindDashboardMenu shows them)
        holder.layoutMenuEditBar.visibility = View.GONE
        holder.lblMenuEditHint.visibility = View.GONE

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
                // "Menu" title on the left; the Sort button takes the place of View all
                holder.category.setText(R.string.txt_menu_section_title)
                holder.category.visibility = View.VISIBLE
                holder.lblViewAll.visibility = View.GONE
            }

            else -> {
                holder.category.visibility = View.VISIBLE
                holder.category.text = modal.menuHeadings
                holder.lblViewAll.visibility = View.VISIBLE
            }
        }

        if (modal.menuHeadings == "Attendance") {
            holder.lblNoRecords.visibility = View.VISIBLE
            holder.lblNoRecords.text = "Today's attendance is not yet available"
        } else {
            holder.lblNoRecords.visibility = View.GONE
        }

        // Bind section — reuse adapters, don't create new ones
        when (modal.menuHeadings) {
            "Notice Board" -> reuseChildAdapter(
                holder,
                modal,
                "Notice Board",
                LinearLayoutManager.HORIZONTAL
            )

            "Leave Request" -> reuseChildAdapter(
                holder,
                modal,
                "Leave Request",
                LinearLayoutManager.HORIZONTAL
            )

            "DashBoard_Menu" -> bindDashboardMenu(holder, modal)
            "Circular" -> reuseChildAdapter(
                holder,
                modal,
                "Circular",
                LinearLayoutManager.HORIZONTAL
            )

            "Chat" -> reuseChildAdapter(holder, modal, "Chat", LinearLayoutManager.HORIZONTAL)
            "Upcoming Events" -> reuseChildAdapter(
                holder,
                modal,
                "Upcoming Events",
                LinearLayoutManager.HORIZONTAL
            )

            "Assignments" -> reuseChildAdapter(
                holder,
                modal,
                "Assignments",
                LinearLayoutManager.HORIZONTAL
            )

            "Emergency Notification" -> reuseChildAdapter(
                holder,
                modal,
                "Emergency Notification",
                LinearLayoutManager.HORIZONTAL
            )

            "Ad" -> reuseChildAdapter(holder, modal, "Ad", LinearLayoutManager.VERTICAL)
            "Recent Notifications" -> reuseChildAdapter(
                holder,
                modal,
                "Recent Notifications",
                LinearLayoutManager.VERTICAL
            )

            "Attendance" -> reuseChildAdapter(
                holder,
                modal,
                "Attendance",
                LinearLayoutManager.VERTICAL
            )

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
                CommonUtil.MenuIDNoticeboard="7"
                context.startActivity(Intent(context, Noticeboard::class.java))
            }

            "Leave Request" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(4) { r, w ->
                    CommonUtil.menu_readAttendance = r
                    CommonUtil.menu_writeAttendance = w
                }
                CommonUtil.MenuIdAttendance = "4"
                context.startActivity(Intent(context, Attendance::class.java))
            }

            "Circular" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(6) { r, w ->
                    CommonUtil.menu_readCircular = r
                    CommonUtil.menu_writeCircular = w
                }
                CommonUtil.MenuIDCircular="6"
                context.startActivity(Intent(context, Circular::class.java))
            }

            "Chat" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(11) { r, w ->
                    CommonUtil.menu_readChat = r
                    CommonUtil.menu_writeChat = w
                }
                CommonUtil.MenuIDChat="11"

                context.startActivity(Intent(context, ChatParent::class.java))
            }

            "Upcoming Events" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(8) { r, w ->
                    CommonUtil.menu_readEvent = r
                    CommonUtil.menu_writeEvent = w
                }
                CommonUtil.MenuIDEvents="8"
                Log.d("BaseMenuID",BaseActivity.EventsMenuID)

                context.startActivity(Intent(context, Events::class.java))
            }

            "Assignments" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(5) { r, w ->
                    CommonUtil.menu_readAssignment = r
                    CommonUtil.menu_writeAssignment = w
                }
                CommonUtil.MenuIDAssignment="5"

                context.startActivity(Intent(context, Assignment::class.java))
            }

            "Emergency Notification" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(16) { r, w ->
                    CommonUtil.menu_readCommunication = r
                    CommonUtil.menu_writeCommunication = w
                }
                CommonUtil.MenuIDCommunication = "16"
                context.startActivity(Intent(context, Communication::class.java))
            }

            "Recent Notifications" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(16) { r, w ->
                    CommonUtil.menu_readCommunication = r
                    CommonUtil.menu_writeCommunication = w
                }
                CommonUtil.MenuIDCommunication = "16"
                context.startActivity(Intent(context, Communication::class.java))
            }

            "Attendance" -> holder.lblViewAll.setOnClickListener {
                setMenuPermissions(4) { r, w ->
                    CommonUtil.menu_readAttendance = r
                    CommonUtil.menu_writeAttendance = w
                }
                CommonUtil.MenuIdAttendance = "4"
                context.startActivity(Intent(context, Attendance::class.java))
            }

            else -> holder.lblViewAll.setOnClickListener(null)
        }
    }

    private fun bindDashboardMenu(holder: ViewHolder, modal: DashboardOverall) {
        holder.recyclerDashboardTitle.visibility = View.GONE
        holder.viewPagerDashboardMenu.visibility = View.VISIBLE

        menuModal = modal
        menuHolder = holder

        // While editing, show the working order; otherwise the saved order
        val menuList = editMenuOrder ?: gridMenus(modal)

        if (menuList.isEmpty()) {
            holder.viewPagerDashboardMenu.visibility = View.GONE
            holder.lblSwipeForMore.visibility = View.GONE
            holder.category.visibility = View.GONE   // no "Menu" title without menus
            return
        }

        bindMenuEditBar(holder)

        // Clean up any stale callback from a recycled holder
        holder.pageChangeCallback?.let {
            holder.viewPagerDashboardMenu.unregisterOnPageChangeCallback(
                it
            )
        }
        holder.pageChangeCallback = null

        val lp = holder.viewPagerDashboardMenu.layoutParams
        lp.height = calculateMenuGridHeight(menuList)
        holder.viewPagerDashboardMenu.layoutParams = lp

        // Edit mode: drag and drop on (the controller moves menus via moveMenu)
        dragController?.release()
        dragController = if (isMenuEditMode) {
            MenuDragController(holder.viewPagerDashboardMenu, menuDragCallbacks)
        } else null

        val pagerAdapter = DashboardMenuPagerAdapter(
            context = holder.itemView.context,
            menuList = menuList,
            onMenuClick = { data ->
                // While editing, a tap does not open the menu
                if (isMenuEditMode) {
                    Toast.makeText(context, R.string.msg_menu_order_tap_done, Toast.LENGTH_SHORT).show()
                } else {
                    ParticularMenuClick(data)
                }
            },
            dragController = dragController
        )
        menuPagerAdapter = pagerAdapter

        holder.viewPagerDashboardMenu.adapter = pagerAdapter
        // Edit mode keeps every page alive so a dragged menu can reach any page
        holder.viewPagerDashboardMenu.offscreenPageLimit =
            if (isMenuEditMode) pagerAdapter.itemCount.coerceAtLeast(1) else 1

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

    // ---- Menu order edit ----

    /** Normal: "Edit". Editing: hint + "Cancel" + "Done". Colours follow the role. */
    private fun bindMenuEditBar(holder: ViewHolder) {
        val roleColor = EdgeToEdgeUtils.getPriorityColor(context, CommonUtil.Priority)
        val editing = isMenuEditMode
        holder.layoutMenuEditBar.visibility = View.VISIBLE
        holder.lblMenuEditHint.visibility = if (editing) View.VISIBLE else View.GONE
        holder.btnMenuEdit.visibility = if (editing) View.GONE else View.VISIBLE
        holder.btnMenuCancel.visibility = if (editing) View.VISIBLE else View.GONE
        holder.btnMenuDone.visibility = if (editing) View.VISIBLE else View.GONE

        holder.btnMenuEdit.setTextColor(roleColor)
        TextViewCompat.setCompoundDrawableTintList(holder.btnMenuEdit, ColorStateList.valueOf(roleColor))
        holder.btnMenuDone.background = GradientDrawable().apply {
            cornerRadius = 999f
            setColor(roleColor)
        }

        holder.btnMenuEdit.setOnClickListener { startMenuEdit() }
        holder.btnMenuCancel.setOnClickListener { cancelMenuEdit() }
        holder.btnMenuDone.setOnClickListener { saveMenuEdit() }
    }

    /** "Edit": copy the current order and switch the grid to drag and drop. */
    fun startMenuEdit() {
        val modal = menuModal ?: return
        editMenuOrder = gridMenus(modal).toMutableList()
        Log.d(MENU_EDIT_TAG, "Edit start, order: ${editMenuOrder?.map { it.id }}")
        refreshMenuRow()
        menuEditListener?.onMenuEditModeChanged(true)
    }

    /** Time of the last live move; the next one waits until the slide animation is over. */
    private var lastMoveAt = 0L

    /** Live moves while dragging (the grid slides as the finger moves). */
    private val menuDragCallbacks = object : MenuDragController.Callbacks {
        override fun onHoverMenu(draggedMenuId: Int, targetMenuId: Int) {
            val to = editMenuOrder?.indexOfFirst { it.id == targetMenuId } ?: return
            moveMenu(draggedMenuId, to)
        }

        override fun onHoverPageEnd(draggedMenuId: Int, pageIndex: Int) {
            val size = editMenuOrder?.size ?: return
            val pageEnd = (pageIndex + 1) * DashboardMenuPagerAdapter.PAGE_SIZE - 1
            moveMenu(draggedMenuId, minOf(pageEnd, size - 1))
        }

        override fun onDragFinished(draggedMenuId: Int) {
            Log.d(MENU_EDIT_TAG, "Drag finished, order now: ${editMenuOrder?.map { it.id }}")
            // Show the dropped menu again in its slot
            menuPagerAdapter?.refreshTiles()
        }
    }

    /** Moves [menuId] to [toIndex] in the working order (any page); other menus slide aside. */
    private fun moveMenu(menuId: Int, toIndex: Int) {
        val list = editMenuOrder ?: return
        val from = list.indexOfFirst { it.id == menuId }
        if (from < 0 || toIndex < 0) return
        val to = toIndex.coerceAtMost(list.size - 1)
        if (from == to) return
        val now = SystemClock.uptimeMillis()
        if (now - lastMoveAt < MOVE_COOLDOWN_MS) return
        lastMoveAt = now

        Log.d(MENU_EDIT_TAG, "Move menu $menuId: $from -> $to, before: ${list.map { it.id }}")
        list.add(to, list.removeAt(from))
        Log.d(MENU_EDIT_TAG, "Move menu $menuId: after: ${list.map { it.id }}")
        menuPagerAdapter?.updateMenus(list)
    }

    /** True when the working order differs from the saved one. */
    fun hasMenuOrderChanged(): Boolean {
        val modal = menuModal ?: return false
        val edited = editMenuOrder ?: return false
        return edited.map { it.id } != gridMenus(modal).map { it.id }
    }

    /** "Done": save the new order for this user (SharedPreferences) and leave edit mode. */
    fun saveMenuEdit() {
        val modal = menuModal
        val edited = editMenuOrder
        if (modal != null && edited != null && hasMenuOrderChanged()) {
            // Menus not shown in the grid (Home) keep their place at the start
            val fullOrder = (modal.DashboardMenuData ?: emptyList()).filter { it.id == 1 } + edited
            Log.d(MENU_EDIT_TAG, "Done: saving order ${fullOrder.map { it.id }}")
            MenuOrderPreference.saveMenuOrder(context, fullOrder)
            modal.DashboardMenuData = fullOrder
            Toast.makeText(context, R.string.msg_menu_order_saved, Toast.LENGTH_SHORT).show()
        } else {
            Log.d(MENU_EDIT_TAG, "Done: order not changed, nothing to save")
        }
        finishMenuEdit()
    }

    /** "Cancel" / discard: drop the working order, the saved order is shown again. */
    fun cancelMenuEdit() {
        Log.d(MENU_EDIT_TAG, "Cancel: order not saved")
        finishMenuEdit()
    }

    private fun finishMenuEdit() {
        editMenuOrder = null
        dragController?.release()
        refreshMenuRow()
        menuEditListener?.onMenuEditModeChanged(false)
        Log.d(MENU_EDIT_TAG, "Edit end")
    }

    /** Redraws the menu row on the same page (or rebinds it if it was scrolled away). */
    private fun refreshMenuRow() {
        val modal = menuModal ?: return
        val index = categoriesModalArrayList.indexOf(modal)
        val holder = menuHolder
        if (holder != null && holder.bindingAdapterPosition == index) {
            val page = holder.viewPagerDashboardMenu.currentItem
            bindDashboardMenu(holder, modal)
            val lastPage = (menuPagerAdapter?.itemCount ?: 1) - 1
            holder.viewPagerDashboardMenu.setCurrentItem(page.coerceIn(0, lastPage.coerceAtLeast(0)), false)
        } else if (index >= 0) {
            notifyItemChanged(index)
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
                // Old: opened Resume Builder directly
                // if (CommonUtil.PlacementTraining) launch(ResumeBuilder::class.java)

                // Placement hub: Placement Training / Events / Resume Builder / Feedback
                launch(PlacementHome::class.java)
            }

            24 -> {
                if (CommonUtil.PlacementEvent) launch(PlacementEvent::class.java)
            }

            25 -> {
                if (CommonUtil.PlacementCareer) launch(CareerTraining::class.java)
            }

            26 -> {
                CommonUtil.menu_readTimetable = data.is_read_enabled.toString()
                CommonUtil.menu_writeTimetable = data.is_write_enabled.toString()
                if (CommonUtil.TimeTable) launch(TimetableActivity::class.java)
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
        val recyclerDashboardTitle: RecyclerView =
            itemView.findViewById(R.id.recyclerDashboardTitle)
        val category: TextView = itemView.findViewById(R.id.idTVCategory)
        val lblViewAll: TextView = itemView.findViewById(R.id.lblViewAll)
        val lblNoRecords: TextView = itemView.findViewById(R.id.lblNoRecords)
        val idCVCategory: CardView = itemView.findViewById(R.id.idCVCategory)
        val viewPagerDashboardMenu: androidx.viewpager2.widget.ViewPager2 =
            itemView.findViewById(R.id.viewPagerDashboardMenu)
        val lblSwipeForMore: TextView = itemView.findViewById(R.id.lblSwipeForMore)

        // Menu order bar (menu row only)
        val layoutMenuEditBar: View = itemView.findViewById(R.id.layoutMenuEditBar)
        val lblMenuEditHint: TextView = itemView.findViewById(R.id.lblMenuEditHint)
        val btnMenuEdit: TextView = itemView.findViewById(R.id.btnMenuEdit)
        val btnMenuCancel: TextView = itemView.findViewById(R.id.btnMenuCancel)
        val btnMenuDone: TextView = itemView.findViewById(R.id.btnMenuDone)

        var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null


        // Cache child adapter so we don't create a new one on every scroll
        var childAdapter: DashboardChild? = null

        var currentChildType: String? = null
    }
}