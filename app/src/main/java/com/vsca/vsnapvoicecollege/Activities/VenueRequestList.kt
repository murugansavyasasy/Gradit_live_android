package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.graphics.Color
import androidx.activity.result.contract.ActivityResultContracts
import com.vsca.vsnapvoicecollege.ActivitySender.AddEvents
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.VenueRequestAdapter
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.VenueRequestData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityVenueRequestListBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Events > My Requests: the user's venue booking requests (api/AppDetailsBal/venuerequests),
 * filtered by status tabs. Ads and action bar follow the same pattern as Events.
 */
class VenueRequestList : BaseActivity<ActivityVenueRequestListBinding>() {

    override fun inflateBinding(): ActivityVenueRequestListBinding {
        return ActivityVenueRequestListBinding.inflate(layoutInflater)
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_venue_request_list

    // Ads (same fields as other screens)
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    private var useDefaultAdsUrl = false

    private val adapter = VenueRequestAdapter()
    private var allRequests: List<VenueRequestData> = emptyList()
    private var selectedTab = TAB_ALL
    private var loadFailed = false

    // ---- Sections (Events menu: isrequest -> My Requests, isapprove -> Approve Venue) ----
    private var showMy = true
    private var showApprove = false
    private var section = SECTION_MY
    private var approvals: List<VenueRequestData> = emptyList()
    private var approveTab = TAB_ALL
    private var approveLoadFailed = false

    private val isApproveSection get() = section == SECTION_APPROVE

    // Section badges from GetOverallcountByMenuType (null until loaded -> list size fallback)
    private var apiMyCount: Int? = null
    private var apiApproveCount: Int? = null

    /** Status tab of the visible section (each section keeps its own). */
    private var currentTab: String
        get() = if (isApproveSection) approveTab else selectedTab
        set(value) {
            if (isApproveSection) approveTab = value else selectedTab = value
        }

    private val currentList get() = if (isApproveSection) approvals else allRequests
    private val currentLoadFailed get() = if (isApproveSection) approveLoadFailed else loadFailed

    /** Edit request: on a successful UpdateVenueRequest, reload the list (stays on the current tab). */
    private val editRequestLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                getVenueRequests()
                loadCounts()
            }
        }

    /** Tab key -> (label, status filter). TAB_ALL shows everything. */
    private val tabs = listOf(
        TAB_ALL to R.string.txt_tab_all,
        VenueRequestAdapter.STATUS_PENDING to R.string.txt_tab_waiting,
        VenueRequestAdapter.STATUS_APPROVED to R.string.txt_tab_approved,
        VenueRequestAdapter.STATUS_REJECTED to R.string.txt_tab_rejected,
        VenueRequestAdapter.STATUS_CANCELLED to R.string.txt_tab_cancelled,
        VenueRequestAdapter.STATUS_EXPIRED to R.string.txt_tab_expired
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityVenueRequestListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        setupEdgeToEdgeAuto(
            rootView = binding.Main,
            statusBarBgView = binding.statusBarBackground,
            priority = CommonUtil.Priority
        )

        if (supportActionBar != null) {
            ActionBarMethod(this)
            fixActionBarOverlap(binding.layoutContent)
        }

        binding.LayoutAdvertisement.setOnClickListener { adclick() }

        binding.recyclerRequests.layoutManager = LinearLayoutManager(this)
        binding.recyclerRequests.adapter = adapter
        adapter.listener = object : VenueRequestAdapter.RequestActionListener {
            override fun onEditRequest(item: VenueRequestData) {
                // AddEvents in "request edit" mode: all fields editable, saves via UpdateVenueRequest
                val intent = Intent(this@VenueRequestList, AddEvents::class.java)
                intent.putExtra("EventEdit", AddEvents.MODE_REQUEST_EDIT)
                intent.putExtra(AddEvents.EXTRA_VENUE_REQUEST, item)
                editRequestLauncher.launch(intent)
            }

            override fun onCancelRequest(item: VenueRequestData) {
                confirmCancel(item)
            }

            override fun onApproveRequest(item: VenueRequestData) {
                showApproveSheet(item)
            }

            override fun onRejectRequest(item: VenueRequestData) {
                showRejectDialog(item)
            }
        }

        // Which sections this user can see (Events menu id 8)
        CommonUtil.menu_isApproveEvent = CommonUtil.isMenuApprove(8)
        CommonUtil.menu_isRequestEvent = CommonUtil.isMenuRequest(8)
        showApprove = CommonUtil.menu_isApproveEvent
        // isrequest, or neither flag set -> My Requests (previous behaviour)
        showMy = CommonUtil.menu_isRequestEvent || !showApprove
        section = if (showMy) SECTION_MY else SECTION_APPROVE

        observeAds()
        observeRequests()
        observeCancelRequest()
        observeApprovals()
        observeDecisions()
        observeCounts()

        updateHeader()
        renderSections()
        renderTabs()
        AdForCollegeApi()
        if (showMy) getVenueRequests()
        if (showApprove) getVenueApprovals(showLoader = !showMy)
        loadCounts()
    }

    // ---- Section counts (api/AppDetailsBal/GetOverallcountByMenuType, Events menu) ----

    /** Counts are only shown on the My Requests | Approve Event switch (both flags). */
    private fun loadCounts() {
        if (showMy && showApprove) OverAllMenuCountRequest(this, "8", false)
    }

    /** venuerequests -> My Requests badge, approvevenuerequests -> Approve Event badge. */
    private fun observeCounts() {
        appViewModel!!.OverAllMenuResponseLiveData!!.observe(this) { response ->
            val counts = response?.takeIf { it.status == 1 }?.data?.firstOrNull()
            apiMyCount = counts?.venuerequests?.trim()?.toIntOrNull()
            apiApproveCount = counts?.approvevenuerequests?.trim()?.toIntOrNull()
            renderSections()
        }
    }

    // ---- Requests ----

    private fun getVenueRequests() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId.toString())
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId.toString())
        appViewModel!!.getVenueRequests(jsonObject, this)
        Log.d("GetVenueRequests:", jsonObject.toString())
    }

    private fun observeRequests() {
        appViewModel!!.VenueRequestsLiveData!!.observe(this) { response ->
            when {
                // API failed (network / server error)
                response == null -> {
                    loadFailed = true
                    allRequests = emptyList()
                }
                // Status 1 with data, or Status 0 "No venue requests found." -> empty list
                else -> {
                    loadFailed = false
                    allRequests = if (response.status == 1) response.data.orEmpty() else emptyList()
                }
            }
            renderSections()
            renderTabs()
            renderList()
        }
    }

    // ---- Cancel request (api/AppDetailsBal/CancelVenueRequest) ----

    /** Booking being cancelled; also tells the observer the response belongs to this screen. */
    private var pendingCancel: VenueRequestData? = null

    /** Confirmation before the API call; Yes calls CancelVenueRequest, No does nothing. */
    private fun confirmCancel(item: VenueRequestData) {
        CommonUtil.CustomConfirm(
            this,
            getString(R.string.msg_cancel_request_confirm),
            getString(R.string.txt_cancel_request)
        ) {
            cancelVenueRequest(item)
        }
    }

    private fun cancelVenueRequest(item: VenueRequestData) {
        val bookingId = item.bookingid ?: return
        pendingCancel = item
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId.toString())
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId.toString())
        jsonObject.addProperty(ApiRequestNames.Req_bookingid, bookingId.toString())
        appViewModel!!.cancelVenueRequest(jsonObject, this)
        Log.d("CancelVenueRequest:", jsonObject.toString())
    }

    private fun observeCancelRequest() {
        appViewModel!!.CancelVenueRequestLiveData!!.observe(this) { response ->
            // Ignore values not started from this screen (e.g. LiveData re-delivery)
            val item = pendingCancel ?: return@observe
            pendingCancel = null
            when {
                response == null -> CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
                response.status == 1 -> markCancelledLocally(item)
                else -> CommonUtil.ApiAlert(
                    this,
                    response.message?.takeIf { it.isNotBlank() } ?: CommonUtil.Something_went_wrong
                )
            }
        }
    }

    /**
     * Success: no list API call. Update this booking in the existing list (Waiting -> Cancelled),
     * recount the tabs and switch to the Cancelled tab so the user sees it.
     */
    private fun markCancelledLocally(item: VenueRequestData) {
        item.status = VenueRequestAdapter.STATUS_CANCELLED
        item.statuslabel = getString(R.string.txt_tab_cancelled)
        item.eventstatus = "NOT_PUBLISHED"
        item.eventmessage = getString(R.string.msg_request_cancelled_event)
        item.decidedby = CommonUtil.MemberName
        item.decidedbyrole = CommonUtil.MemberType
        item.decidedon = SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.ENGLISH).format(Date())
        item.remarks = ""

        selectedTab = VenueRequestAdapter.STATUS_CANCELLED
        renderTabs()
        renderList()
        loadCounts()
    }

    private fun filtered(tabKey: String): List<VenueRequestData> =
        if (tabKey == TAB_ALL) currentList
        else currentList.filter { it.status?.trim()?.uppercase() == tabKey }

    private fun renderList() {
        val list = filtered(currentTab)
        adapter.approveMode = isApproveSection
        adapter.submit(list)
        if (list.isEmpty()) {
            binding.recyclerRequests.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
            if (currentLoadFailed) {
                binding.lblEmptyTitle.setText(R.string.txt_requests_load_failed_title)
                binding.lblEmptySubtitle.setText(R.string.txt_requests_load_failed_sub)
            } else {
                val (title, sub) = when {
                    isApproveSection && currentTab == TAB_ALL ->
                        R.string.txt_empty_review_all_title to R.string.txt_empty_review_all_sub
                    isApproveSection && currentTab == VenueRequestAdapter.STATUS_PENDING ->
                        R.string.txt_empty_review_waiting_title to R.string.txt_empty_review_waiting_sub
                    else -> myEmptyState(currentTab)
                }
                binding.lblEmptyTitle.setText(title)
                binding.lblEmptySubtitle.setText(sub)
            }
        } else {
            binding.layoutEmpty.visibility = View.GONE
            binding.recyclerRequests.visibility = View.VISIBLE
            binding.recyclerRequests.scrollToPosition(0)
        }
    }

    /** Empty-state title/subtitle per status tab (same texts as before). */
    private fun myEmptyState(tab: String): Pair<Int, Int> =
                when (tab) {
                    VenueRequestAdapter.STATUS_PENDING ->
                        R.string.txt_empty_waiting_title to R.string.txt_empty_waiting_sub
                    VenueRequestAdapter.STATUS_APPROVED ->
                        R.string.txt_empty_approved_title to R.string.txt_empty_approved_sub
                    VenueRequestAdapter.STATUS_REJECTED ->
                        R.string.txt_empty_rejected_title to R.string.txt_empty_rejected_sub
                    VenueRequestAdapter.STATUS_CANCELLED ->
                        R.string.txt_empty_cancelled_title to R.string.txt_empty_cancelled_sub
                    VenueRequestAdapter.STATUS_EXPIRED ->
                        R.string.txt_empty_expired_title to R.string.txt_empty_expired_sub
                    else -> R.string.txt_empty_all_title to R.string.txt_empty_all_sub
                }

    // ---- Tabs (selected tab uses the role colour from CommonUtil.getPriorityColor) ----

    private fun renderTabs() {
        val container = binding.layoutTabs
        container.removeAllViews()
        // p1 principal, p2/p3/p6 teaching staff, p4 receiver, p5 parent, p7 light orange
        val roleColor = with(CommonUtil) { this@VenueRequestList.getPriorityColor(CommonUtil.Priority) }

        tabs.forEachIndexed { index, (key, labelRes) ->
            val selected = key == currentTab
            val count = filtered(key).size
            val countStyle = if (key == TAB_ALL) null else VenueRequestAdapter.statusStyle(key)

            val tab = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(14), dp(7), dp(8), dp(7))
                background = roundedBg(
                    fill = if (selected) roleColor else Color.WHITE,
                    stroke = if (selected) roleColor else Color.parseColor("#E2E8F0")
                )
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    if (currentTab != key) {
                        currentTab = key
                        renderTabs()
                        renderList()
                    }
                }
            }

            val label = TextView(this).apply {
                setText(labelRes)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                setTextColor(if (selected) Color.WHITE else Color.parseColor("#334155"))
            }

            val badge = TextView(this).apply {
                text = count.toString()
                gravity = Gravity.CENTER
                minWidth = dp(20)
                minHeight = dp(20)
                setPadding(dp(5), 0, dp(5), 0)
                includeFontPadding = false
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                if (selected) {
                    setTextColor(Color.WHITE)
                    background = roundedBg(Color.argb(60, 255, 255, 255), null)
                } else if (countStyle != null) {
                    setTextColor(countStyle.text)
                    background = roundedBg(countStyle.chipFill, countStyle.chipStroke)
                } else {
                    setTextColor(Color.parseColor("#475569"))
                    background = roundedBg(Color.parseColor("#F1F5F9"), null)
                }
            }

            tab.addView(label)
            tab.addView(badge, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, dp(20)
            ).apply { marginStart = dp(8) })

            container.addView(tab, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { if (index > 0) marginStart = dp(8) })

            // Keep the selected tab visible (e.g. Cancelled at the end)
            if (selected) {
                binding.scrollTabs.post {
                    binding.scrollTabs.smoothScrollTo(
                        (tab.left - dp(16)).coerceAtLeast(0), 0
                    )
                }
            }
        }
    }

    // ---- Header + section switch (My Requests | Approve Venue) ----

    private fun roleColor(): Int =
        with(CommonUtil) { this@VenueRequestList.getPriorityColor(CommonUtil.Priority) }

    /** Request List / Approve Venue title and the subtitle of the visible section. */
    private fun updateHeader() {
        if (isApproveSection) {
            binding.lblRequestTitle.setText(
                if (showMy) R.string.txt_request_list else R.string.txt_approve_venue
            )
            binding.lblRequestSubtitle.setText(R.string.txt_approve_venue_subtitle)
        } else {
            binding.lblRequestTitle.setText(R.string.txt_request_list)
            binding.lblRequestSubtitle.setText(R.string.txt_request_list_subtitle)
        }
    }

    /** Shown only when the user has both isrequest and isapprove. */
    private fun renderSections() {
        val container = binding.layoutSections
        if (!(showMy && showApprove)) {
            container.visibility = View.GONE
            return
        }
        container.visibility = View.VISIBLE
        container.removeAllViews()

        // Counts from GetOverallcountByMenuType; until it loads, fall back to the lists
        val myCount = apiMyCount ?: allRequests.size
        val toReviewCount = apiApproveCount ?: approvals.count {
            it.status?.trim()?.uppercase() == VenueRequestAdapter.STATUS_PENDING && it.canapprove == true
        }
        addSection(container, SECTION_MY, R.drawable.ic_req_person, R.string.txt_my_requests,
            myCount, Color.parseColor("#E2E8F0"), Color.parseColor("#334155"))
        addSection(container, SECTION_APPROVE, R.drawable.ic_req_shield, R.string.txt_approve_venue,
            toReviewCount, Color.parseColor("#F59E0B"), Color.WHITE)
    }

    private fun addSection(
        container: LinearLayout, key: String, iconRes: Int, labelRes: Int,
        count: Int, badgeFill: Int, badgeText: Int
    ) {
        val selected = section == key
        val textColor = if (selected) roleColor() else Color.parseColor("#64748B")

        val tab = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(9), dp(8), dp(9))
            background = if (selected) GradientDrawable().apply {
                cornerRadius = dp(11).toFloat()
                setColor(Color.WHITE)
                setStroke(dp(1), Color.parseColor("#E2E8F0"))
            } else null
            isClickable = true
            isFocusable = true
            setOnClickListener {
                if (section != key) {
                    section = key
                    updateHeader()
                    renderSections()
                    renderTabs()
                    renderList()
                }
            }
        }

        val icon = android.widget.ImageView(this).apply {
            setImageResource(iconRes)
            imageTintList = android.content.res.ColorStateList.valueOf(textColor)
        }
        val label = TextView(this).apply {
            setText(labelRes)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(textColor)
        }
        val badge = TextView(this).apply {
            text = count.toString()
            gravity = Gravity.CENTER
            minWidth = dp(20)
            setPadding(dp(5), 0, dp(5), 0)
            includeFontPadding = false
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(badgeText)
            background = roundedBg(badgeFill, null)
        }

        tab.addView(icon, LinearLayout.LayoutParams(dp(16), dp(16)))
        tab.addView(label, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { marginStart = dp(6) })
        tab.addView(badge, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, dp(20)
        ).apply { marginStart = dp(8) })
        container.addView(tab, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
    }

    // ---- Approve Venue: list (api/AppDetailsBal/GetVenueApprovalList) ----

    private fun getVenueApprovals(showLoader: Boolean = true) {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId.toString())
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId.toString())
        appViewModel!!.getVenueApprovalList(jsonObject, this, showLoader)
        Log.d("GetVenueApprovalList:", jsonObject.toString())
    }

    private fun observeApprovals() {
        appViewModel!!.VenueApprovalListLiveData!!.observe(this) { response ->
            if (response == null) {
                approveLoadFailed = true
                approvals = emptyList()
            } else {
                approveLoadFailed = false
                approvals = if (response.status == 1) response.data.orEmpty() else emptyList()
            }
            renderSections()
            if (isApproveSection) {
                renderTabs()
                renderList()
            }
        }
    }

    // ---- Approve / Reject (ApproveVenueRequest / RejectVenueRequest) ----

    /** Request whose approve / reject call is in progress (also filters re-delivered LiveData). */
    private var pendingApprove: VenueRequestData? = null
    private var pendingReject: VenueRequestData? = null

    private fun decisionJson(item: VenueRequestData) = JsonObject().apply {
        addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId.toString())
        addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId.toString())
        addProperty(ApiRequestNames.Req_bookingid, item.bookingid?.toString() ?: "")
    }

    private fun observeDecisions() {
        appViewModel!!.ApproveVenueRequestLiveData!!.observe(this) { response ->
            val item = pendingApprove ?: return@observe
            pendingApprove = null
            when {
                response == null -> CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
                response.status == 1 -> {
                    val autoRejected = response.data?.firstOrNull()?.autorejected ?: 0
                    showApprovedSheet(item, if (autoRejected > 0) response.message else null)
                    // Reload: approving can also auto-reject overlapping requests
                    getVenueApprovals(showLoader = false)
                    loadCounts()
                }
                else -> CommonUtil.ApiAlert(
                    this, response.message?.takeIf { it.isNotBlank() } ?: CommonUtil.Something_went_wrong
                )
            }
        }

        appViewModel!!.RejectVenueRequestLiveData!!.observe(this) { response ->
            if (pendingReject == null) return@observe
            pendingReject = null
            when {
                response == null -> CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
                response.status == 1 -> {
                    // Show the rejected request under the Rejected tab (as in the design)
                    approveTab = VenueRequestAdapter.STATUS_REJECTED
                    getVenueApprovals(showLoader = false)
                    loadCounts()
                }
                else -> CommonUtil.ApiAlert(
                    this, response.message?.takeIf { it.isNotBlank() } ?: CommonUtil.Something_went_wrong
                )
            }
        }
    }

    /** "Approve this request?" bottom sheet. */
    private fun showApproveSheet(item: VenueRequestData) {
        val view = layoutInflater.inflate(R.layout.dialog_approve_venue, null)
        val dialog = bottomDialog(view)

        view.findViewById<View>(R.id.layoutApproveIcon).background =
            GradientDrawable().apply { cornerRadius = dp(12).toFloat(); setColor(Color.parseColor("#DCFCE7")) }
        view.findViewById<android.widget.ImageView>(R.id.imgApproveIcon).imageTintList =
            android.content.res.ColorStateList.valueOf(Color.parseColor("#15803D"))
        view.findViewById<View>(R.id.viewApproveAccent)
            .setBackgroundColor(parseColorOr(item.venuecolor, Color.parseColor("#94A3B8")))
        view.findViewById<View>(R.id.layoutApproveNote).background = GradientDrawable().apply {
            cornerRadius = dp(10).toFloat()
            setColor(Color.parseColor("#ECFDF5"))
            setStroke(dp(1), Color.parseColor("#A7F3D0"))
        }

        view.findViewById<TextView>(R.id.lblApproveTitle).text = item.title.orEmpty()
        view.findViewById<TextView>(R.id.lblApproveDate).text = item.displaydate.orEmpty()
        view.findViewById<TextView>(R.id.lblApproveTime).text = item.displaytime.orEmpty()
        view.findViewById<TextView>(R.id.lblApproveVenue).text =
            boldThenRest(item.venuename.orEmpty(), item.location?.let { ", $it" })
        val requester = view.findViewById<TextView>(R.id.lblApproveRequester)
        if (item.requestedby.isNullOrBlank()) {
            requester.visibility = View.GONE
        } else {
            requester.text = boldThenRest(item.requestedby.orEmpty(), item.department?.let { " $it" })
        }

        view.findViewById<View>(R.id.btnApproveGoBack).setOnClickListener { dialog.dismiss() }
        view.findViewById<View>(R.id.btnApproveConfirm).setOnClickListener {
            dialog.dismiss()
            pendingApprove = item
            appViewModel!!.approveVenueRequest(decisionJson(item), this)
        }
        dialog.show()
    }

    /** "Request approved" bottom sheet; apiMessage is shown when overlapping requests were auto-rejected. */
    private fun showApprovedSheet(item: VenueRequestData, apiMessage: String?) {
        val view = layoutInflater.inflate(R.layout.dialog_approve_success, null)
        val dialog = bottomDialog(view)

        view.findViewById<TextView>(R.id.lblApprovedMessage).text = getString(
            R.string.txt_request_approved_message,
            item.title.orEmpty(), item.venuename.orEmpty(),
            item.displaydate.orEmpty(), item.displaytime.orEmpty()
        )
        val lblApi = view.findViewById<TextView>(R.id.lblApprovedApiMessage)
        if (!apiMessage.isNullOrBlank()) {
            lblApi.text = apiMessage
            lblApi.visibility = View.VISIBLE
        }
        view.findViewById<View>(R.id.btnApprovedDone).apply {
            background = GradientDrawable().apply { cornerRadius = dp(10).toFloat(); setColor(roleColor()) }
            setOnClickListener { dialog.dismiss() }
        }
        dialog.show()
    }

    /** "Reject this request?" dialog: a reason (max 250) is required. */
    private fun showRejectDialog(item: VenueRequestData) {
        val view = layoutInflater.inflate(R.layout.dialog_reject_venue, null)
        val dialog = android.app.Dialog(this)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)

        view.findViewById<View>(R.id.layoutRejectIcon).background =
            GradientDrawable().apply { cornerRadius = dp(12).toFloat(); setColor(Color.parseColor("#FFE4E6")) }
        view.findViewById<android.widget.ImageView>(R.id.imgRejectIcon).imageTintList =
            android.content.res.ColorStateList.valueOf(Color.parseColor("#E11D48"))

        view.findViewById<TextView>(R.id.lblRejectTitle).text = item.title.orEmpty()
        view.findViewById<TextView>(R.id.lblRejectWhen).text = listOfNotNull(
            item.displaydate?.takeIf { it.isNotBlank() },
            item.displaytime?.takeIf { it.isNotBlank() }
        ).joinToString(", ") + (item.venuename?.takeIf { it.isNotBlank() }?.let { " at $it" } ?: "")

        val edtReason = view.findViewById<android.widget.EditText>(R.id.edtRejectReason)
        val lblCount = view.findViewById<TextView>(R.id.lblReasonCount)
        val lblError = view.findViewById<TextView>(R.id.lblReasonError)
        val lblHelper = view.findViewById<TextView>(R.id.lblReasonHelper)

        fun showError(show: Boolean) {
            lblError.visibility = if (show) View.VISIBLE else View.GONE
            lblHelper.visibility = if (show) View.GONE else View.VISIBLE
            edtReason.setBackgroundResource(
                if (show) R.drawable.bg_req_reason_input_error else R.drawable.bg_req_reason_input
            )
        }

        edtReason.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lblCount.text = "${s?.length ?: 0}/250"
                if (!s.isNullOrBlank()) showError(false)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        view.findViewById<View>(R.id.btnRejectGoBack).setOnClickListener { dialog.dismiss() }
        view.findViewById<View>(R.id.btnRejectConfirm).setOnClickListener {
            val reason = edtReason.text.toString().trim()
            if (reason.isEmpty()) {
                showError(true)
                return@setOnClickListener
            }
            dialog.dismiss()
            pendingReject = item
            val json = decisionJson(item)
            json.addProperty(ApiRequestNames.Req_reason, reason)
            appViewModel!!.rejectVenueRequest(json, this)
            Log.d("RejectVenueRequest:", json.toString())
        }

        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            android.view.WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    /** Plain Dialog shown as a full-width bottom sheet (no Material theme needed). */
    private fun bottomDialog(view: View): android.app.Dialog {
        val dialog = android.app.Dialog(this)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(Color.TRANSPARENT))
            setLayout(
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.BOTTOM)
        }
        return dialog
    }

    /** Bold first part + normal rest, e.g. "abdul kalam block, 3rd floor". */
    private fun boldThenRest(bold: String, rest: String?): CharSequence =
        android.text.SpannableStringBuilder().apply {
            append(bold, android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0)
            setSpan(
                android.text.style.ForegroundColorSpan(Color.parseColor("#1E293B")),
                0, bold.length, android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (!rest.isNullOrBlank()) append(rest)
        }

    private fun parseColorOr(value: String?, fallback: Int): Int =
        try {
            if (value.isNullOrBlank()) fallback else Color.parseColor(value.trim())
        } catch (e: IllegalArgumentException) {
            fallback
        }

    private fun roundedBg(fill: Int, stroke: Int?): GradientDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(999).toFloat()
            setColor(fill)
            if (stroke != null) setStroke(dp(1), stroke)
        }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    // ---- Ads (same as Events) ----

    private fun observeAds() {
        appViewModel!!.AdvertisementLiveData?.observe(this) { response ->
            useDefaultAdsUrl = false
            if (response != null && response.status == 1 && !response.data.isNullOrEmpty()) {
                GetAdForCollegeData = response.data!!
                for (j in GetAdForCollegeData.indices) {
                    AdSmallImage = GetAdForCollegeData[j].add_image
                    AdBackgroundImage = GetAdForCollegeData[0].background_image
                    AdWebURl = GetAdForCollegeData[0].add_url.toString()
                }
                if (AdWebURl.isNullOrBlank() || AdWebURl == "null") useDefaultAdsUrl = true

                Glide.with(this).load(AdBackgroundImage)
                    .placeholder(R.drawable.adv_place_holder)
                    .error(R.drawable.savyasasy_ads)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?, model: Any?,
                            target: Target<Drawable>, isFirstResource: Boolean
                        ): Boolean {
                            useDefaultAdsUrl = true
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable, model: Any, target: Target<Drawable>?,
                            dataSource: DataSource, isFirstResource: Boolean
                        ): Boolean = false
                    })
                    .into(binding.imgAdvertisement)

                Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.adv_thumb_placeholder)
                    .error(R.drawable.adv_thumb_gradit_logo)
                    .into(binding.imgthumb)
            } else {
                useDefaultAdsUrl = true
                Glide.with(this).load(R.drawable.savyasasy_ads)
                    .placeholder(R.drawable.adv_place_holder)
                    .error(R.drawable.savyasasy_ads)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imgAdvertisement)
                Glide.with(this).load(R.drawable.adv_thumb_gradit_logo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.adv_thumb_placeholder)
                    .error(R.drawable.adv_thumb_gradit_logo)
                    .into(binding.imgthumb)
            }
        }
    }

    private fun AdForCollegeApi() {
        val mobilenumber = SharedPreference.getSH_MobileNumber(this)
        val devicetoken = SharedPreference.getSH_DeviceToken(this)
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_ad_device_token, devicetoken)
        jsonObject.addProperty(ApiRequestNames.Req_MemberID, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_mobileno, mobilenumber)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_previous_add_id, PreviousAddId)
        appviewModelbase!!.getAdforCollege(jsonObject, this, false)
        Log.d("AdForCollege:", jsonObject.toString())
        PreviousAddId = PreviousAddId + 1
    }

    fun adclick() {
        LoadWebViewContext(this, AdWebURl, useDefaultAdsUrl)
    }

    companion object {
        private const val TAB_ALL = "ALL"
        private const val SECTION_MY = "MY"
        private const val SECTION_APPROVE = "APPROVE"
    }
}
