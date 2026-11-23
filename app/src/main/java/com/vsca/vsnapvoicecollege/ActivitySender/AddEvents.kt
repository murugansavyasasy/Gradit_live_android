package com.vsca.vsnapvoicecollege.ActivitySender

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Activities.Events
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.GetEventDetailsData
import com.vsca.vsnapvoicecollege.Model.VenueListData
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Toast
import android.graphics.Color
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.vsca.vsnapvoicecollege.Model.VenueAvailabilityResponse
import com.vsca.vsnapvoicecollege.Model.VenueBookedSlot
import com.vsca.vsnapvoicecollege.Model.EventTimePreset
import com.vsca.vsnapvoicecollege.Model.VenueRequestData
import java.util.Locale
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityAddEventsBinding
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class AddEvents : ActionBarActivity() {

    var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var MenuTitle: String? = null
    var MenuDescription: String? = null
    private var useDefaultAdsUrl = false

    var Venue: String? = null
    var ScreenNameEvent: String? = null
    var Date: String? = null
    var Time: String? = null
    var eventsdata: GetEventDetailsData? = null
    var EventEdit: String? = null
    var formatdate1 = ""
    var formatdate = ""
    var EditEvent: Boolean = false

    // Venue dropdown (GetVenueList API)
    var venueList: List<VenueListData> = ArrayList()
    var selectedVenue: VenueListData? = null

    // Event schedule (minutes from midnight) for duration + validation
    var fromMinutes: Int? = null
    var toMinutes: Int? = null

    // True while a CheckVenueAvailability call started here is waiting for its response
    private var isCheckingAvailability = false

    // ---- Edit modes ----
    // EventEdit extra: null = new event | "EventEdit" = edit from Events (Upcoming) > Details
    //                  | "VenueRequestEdit" = edit from My Requests (waiting request)
    private var venueRequest: VenueRequestData? = null
    private val isEventEdit get() = EventEdit.equals(MODE_EVENT_EDIT)
    private val isRequestEdit get() = EventEdit.equals(MODE_REQUEST_EDIT)

    // True while an UpdateVenueRequest call started here is waiting for its response
    private var isUpdatingRequest = false

    private lateinit var binding: ActivityAddEventsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionbarWithoutBottom(this, hideBackButton = true)

        fixEdgeToEdgeActionBar(
            rootView = binding.Main,
            statusBarBgView = binding.statusBarBackground,
            priority = CommonUtil.Priority
        )

        imgRefresh!!.visibility = View.GONE
        binding.lblMenuTitle.text = "New Event"


        eventsdata = intent.getSerializableExtra("EventsData") as? GetEventDetailsData
        EventEdit = intent.getSerializableExtra("EventEdit") as String?
        venueRequest = intent.getSerializableExtra(EXTRA_VENUE_REQUEST) as? VenueRequestData

        ScreenNameEvent = "ScreenNameEvent"
        if (EventEdit.equals("EventEdit")) {

            binding.btnConfirm!!.text = "Send"
            CommonUtil.EventsendType = "Edit"
            binding.lblEventDate!!.text = eventsdata!!.event_date
            binding.lblEventTime!!.text = eventsdata!!.event_time
            binding.txtTitle!!.setText(eventsdata!!.topic)
            binding.txtDescription!!.setText(eventsdata!!.body)
            binding.edtVenue!!.setText(eventsdata!!.venue)
            ScreenNameEvent = "Event_Edit"

        }

        // approval_required of the Events menu (id 8) from GetParentUserMenuWithReadWriteAccess.
        // Read here so it is correct however Events was opened (dashboard, notification, etc.).
        CommonUtil.menu_approvalEvent = CommonUtil.isMenuApprovalRequired(8)

        if (!EventEdit.equals("EventEdit")) {
            binding.btnConfirm.text = if (CommonUtil.menu_approvalEvent)
                getString(R.string.txt_check_venue_availability)
            else
                getString(R.string.txt_select_recipients)
        }


        binding.btnConfirm.setOnClickListener { btnNextClick() }
        binding.rytEventDate.setOnClickListener { eventdateClick() }
        binding.imgImagePdfback.setOnClickListener { onBackPressed() }
        binding.btnCancel.setOnClickListener { onBackPressed() }
        binding.LayoutAdvertisement.setOnClickListener { adclick() }
        binding.edtVenue.setOnClickListener { showVenueDropdown() }

        // Clear values kept from a previous New Event screen
        CommonUtil.EventStartTime = ""
        CommonUtil.EventEndTime = ""
        CommonUtil.EventDurationMinutes = 0
        CommonUtil.EventVenueId = null

        // Edit modes (after the reset above so the prefilled values are kept)
        if (isEventEdit) setupEventEdit()
        if (isRequestEdit) setupRequestEdit()

        binding.lblSelectVenuePill.setOnClickListener { showVenueDropdown() }
        binding.btnVenueRefresh.setOnClickListener { getVenueListApi() }

        appViewModel!!.VenueListLiveData!!.observe(this) { response ->
            when {
                // API failed (network / server error)
                response == null -> {
                    venueList = ArrayList()
                    showVenueEmptyState(getString(R.string.msg_venue_load_failed))
                }
                // Venues available
                response.status == 1 && !response.data.isNullOrEmpty() -> {
                    venueList = response.data!!
                    showVenueContent()
                }
                // Status 0 or empty list
                else -> {
                    venueList = ArrayList()
                    val apiMessage = response.message
                    showVenueEmptyState(
                        if (response.status != 1 && !apiMessage.isNullOrBlank()) apiMessage
                        else getString(R.string.msg_no_venue_for_department)
                    )
                }
            }
        }
        // Event edit: venue/date/time are locked, so no venue list or presets are needed
        if (!isEventEdit) {
            getVenueListApi()
            refreshTimePresets()
        }

        appViewModel!!.UpdateVenueRequestLiveData!!.observe(this) { response ->
            // Only react to an update started from this screen (ignore re-delivered values)
            if (!isUpdatingRequest) return@observe
            isUpdatingRequest = false
            when {
                response == null -> CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
                // Success: back to My Requests, which reloads its list
                response.status == 1 -> {
                    setResult(RESULT_OK)
                    finish()
                }
                else -> CommonUtil.ApiAlert(
                    this,
                    response.message?.takeIf { it.isNotBlank() } ?: CommonUtil.Something_went_wrong
                )
            }
        }

        appViewModel!!.VenueAvailabilityLiveData!!.observe(this) { response ->
            // Only react to a check started from this screen (ignore re-delivered values)
            if (!isCheckingAvailability) return@observe
            isCheckingAvailability = false
            showVenueAvailabilityDialog(response)
        }

        appViewModel!!.Eventsenddata!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    alertShow(status, message)
                } else {
                    alertShow(status, message)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        appViewModel!!.AdvertisementLiveData?.observe(
            this,
            Observer<GetAdvertisementResponse?> { response ->
                if (response != null) {
                    useDefaultAdsUrl = false
                    val status = response.status
                    val message = response.message
                    if (status == 1) {
                        GetAdForCollegeData = response.data!!
                        for (j in GetAdForCollegeData.indices) {
                            AdSmallImage = GetAdForCollegeData[j].add_image
                            AdBackgroundImage = GetAdForCollegeData[0].background_image!!
                            AdWebURl = GetAdForCollegeData[0].add_url.toString()
                        }
                        if (AdWebURl.isNullOrBlank() || AdWebURl == "null") useDefaultAdsUrl = true

                        Glide.with(this).load(AdBackgroundImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.adv_place_holder)
                            .error(R.drawable.savyasasy_ads)

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
                            .into(binding.imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.adv_thumb_placeholder)
                            .error(R.drawable.adv_thumb_gradit_logo)
                            .into(binding.imgthumb!!)
                    } else {
                        useDefaultAdsUrl = true

                        Glide.with(this).load(R.drawable.adv_place_holder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.adv_place_holder)
                            .error(R.drawable.savyasasy_ads)
                            .into(binding.imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this).load(R.drawable.adv_thumb_gradit_logo)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.adv_thumb_placeholder)
                            .error(R.drawable.adv_thumb_gradit_logo)
                            .into(binding.imgthumb!!)
                    }
                } else {
                    useDefaultAdsUrl = true

                    Glide.with(this).load(R.drawable.adv_place_holder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.adv_place_holder)
                        .error(R.drawable.savyasasy_ads)
                        .into(binding.imgAdvertisement!!)
                    Log.d("AdBackgroundImage", AdBackgroundImage!!)

                    Glide.with(this).load(R.drawable.adv_thumb_gradit_logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.adv_thumb_placeholder)
                        .error(R.drawable.adv_thumb_gradit_logo)
                        .into(binding.imgthumb!!)
                }
            })


        binding.rytEventTime!!.setOnClickListener {

            val timePicker: TimePickerDialog = TimePickerDialog(
                // pass the Context
                this,
                // listener to perform task
                // when time is picked
                timePickerDialogListener,
                // default hour when the time picker
                // dialog is opened
                12,
                // default minute when the time picker
                // dialog is opened
                10,
                // 24 hours time picker is
                // false (varies according to the region)
                false
            )

            // then after building the timepicker
            // dialog show the dialog to user
            timePicker.show()

        }

        // To (End) time picker
        binding.rytEventToTime.setOnClickListener {
            val startHour = (toMinutes ?: fromMinutes ?: 13 * 60) / 60
            val startMinute = (toMinutes ?: fromMinutes ?: 0) % 60
            TimePickerDialog(this, { _, hourOfDay, minute ->
                toMinutes = hourOfDay * 60 + minute
                binding.lblEventToTime.text = formatTime12(hourOfDay, minute)
                updateDuration()
                updatePresetSelection()
            }, startHour, startMinute, false).show()
        }

        binding.txtDescription!!.addTextChangedListener(mTextEditorWatcher)


    }

    private fun alertShow(status: Int, message: String) {

        val dlg = this.let { AlertDialog.Builder(it) }
        dlg.setTitle("Info")
        dlg.setMessage(message)
        dlg.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            if (status == 1) {
                val i: Intent = Intent(this, Events::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(i)
            } else {
                dialog.dismiss()
            }
        })

        dlg.setCancelable(false)
        dlg.create()
        dlg.show()
    }

    // Word limit Method

    private val mTextEditorWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            binding.lblWordCount!!.text = s.length.toString() + "/500"
            // Design: badge turns amber from 450 characters
            val warn = s.length >= 450
            binding.lblWordCount.setTextColor(Color.parseColor(if (warn) "#D97706" else "#475569"))
            binding.lblWordCount.background?.mutate()
                ?.setTint(Color.parseColor(if (warn) "#FFFBEB" else "#F1F5F9"))
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private val timePickerDialogListener: TimePickerDialog.OnTimeSetListener =
        object : TimePickerDialog.OnTimeSetListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

                // New event on today's date: start time must be after the current time
                if (!EventEdit.equals("EventEdit") && isPastTimeForSelectedDate(hourOfDay * 60 + minute)) {
                    showPastTimeMessage()
                    return
                }

                val formattedTime: String = when {
                    hourOfDay == 0 -> {
                        if (minute < 10) {
                            "${hourOfDay + 12}:0${minute} am"
                        } else {
                            "${hourOfDay + 12}:${minute} am"
                        }
                    }

                    hourOfDay > 12 -> {
                        if (minute < 10) {
                            "${hourOfDay - 12}:0${minute} pm"
                        } else {
                            "${hourOfDay - 12}:${minute} pm"
                        }
                    }

                    hourOfDay == 12 -> {
                        if (minute < 10) {
                            "${hourOfDay}:0${minute} pm"
                        } else {
                            "${hourOfDay}:${minute} pm"
                        }
                    }

                    else -> {
                        if (minute < 10) {
                            "${hourOfDay}:${minute} am"
                        } else {
                            "${hourOfDay}:${minute} am"
                        }
                    }
                }


                binding.lblEventTime!!.text = formattedTime

                Time = timeCoversion12to24(formattedTime)

                fromMinutes = hourOfDay * 60 + minute
                updateDuration()
                updatePresetSelection()
            }
        }

    @Throws(ParseException::class)
    fun timeCoversion12to24(twelveHoursTime: String?): String? {

        val df: DateFormat = SimpleDateFormat("hh:mm a")

        val outputformat: DateFormat = SimpleDateFormat("HH:mm")
        var date: Date? = null
        var output: String? = null

        //Returns Date object
        date = df.parse(twelveHoursTime)

        //old date format to new date format
        output = outputformat.format(date)
        return output
    }

    private fun AdForCollegeApi() {

        var mobilenumber = SharedPreference.getSH_MobileNumber(this)
        var devicetoken = SharedPreference.getSH_DeviceToken(this)
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_ad_device_token, devicetoken)
        jsonObject.addProperty(ApiRequestNames.Req_MemberID, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_mobileno, mobilenumber)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_previous_add_id, PreviousAddId)
        appviewModelbase!!.getAdforCollege(jsonObject, this)
        Log.d("AdForCollege:", jsonObject.toString())

        PreviousAddId = PreviousAddId + 1
        Log.d("PreviousAddId", PreviousAddId.toString())
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_add_events

    // ---- New Event schedule & validation (design) ----

    private fun formatTime12(hourOfDay: Int, minute: Int): String {
        val hour12 = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
        val amPm = if (hourOfDay < 12) "am" else "pm"
        return String.format("%d:%02d %s", hour12, minute, amPm)
    }

    private fun updateDuration() {
        val from = fromMinutes
        val to = toMinutes
        if (from == null || to == null) {
            binding.lblDuration.text = getString(R.string.txt_duration_empty)
            binding.lblDuration.setTextColor(Color.parseColor("#7E22CE"))
            CommonUtil.EventDurationMinutes = 0
            return
        }
        val diff = to - from
        // Same From and To (0 min) is not a valid schedule
        if (diff <= 0) {
            binding.lblDuration.text = getString(R.string.txt_duration_invalid)
            binding.lblDuration.setTextColor(Color.parseColor("#E11D48"))
            CommonUtil.EventDurationMinutes = 0
            return
        }
        val h = diff / 60
        val m = diff % 60
        binding.lblDuration.text = when {
            h > 0 && m > 0 -> "${h}h ${m}m"
            h > 0 -> "${h}h"
            else -> "${m}m"
        }
        binding.lblDuration.setTextColor(Color.parseColor("#7E22CE"))
        CommonUtil.EventDurationMinutes = diff
    }

    private fun showValidation(messageRes: Int) {
        Toast.makeText(this, getString(messageRes), Toast.LENGTH_SHORT).show()
    }

    /** Validation for a new event only; EventEdit keeps its original flow. */
    private fun validateNewEvent(): Boolean {
        if (binding.txtTitle.text.toString().trim().isEmpty()) {
            showValidation(R.string.msg_event_enter_title)
            binding.txtTitle.requestFocus()
            return false
        }
        if (binding.txtDescription.text.toString().trim().isEmpty()) {
            showValidation(R.string.msg_event_enter_description)
            binding.txtDescription.requestFocus()
            return false
        }
        if (selectedVenue == null) {
            showValidation(R.string.msg_event_select_venue)
            showVenueDropdown()
            return false
        }
        if (binding.lblEventDate.text.toString().isEmpty()) {
            showValidation(R.string.msg_event_select_date)
            return false
        }
        if (fromMinutes == null) {
            showValidation(R.string.msg_event_select_from_time)
            return false
        }
        if (toMinutes == null) {
            showValidation(R.string.msg_event_select_to_time)
            return false
        }
        if (isPastTimeForSelectedDate(fromMinutes!!)) {
            showPastTimeMessage()
            return false
        }
        // From and To must not be the same (0 min) and To must be after From
        if (toMinutes!! <= fromMinutes!!) {
            showValidation(R.string.msg_event_invalid_time_range)
            return false
        }
        return true
    }

    private fun isSelectedDateToday(): Boolean {
        val today = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(java.util.Date())
        return binding.lblEventDate.text.toString() == today
    }

    /** First allowed start minute today: the next minute after now (e.g. 3:58 pm -> 3:59 pm). */
    private fun minAllowedMinutesToday(): Int {
        val now = Calendar.getInstance()
        return now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE) + 1
    }

    private fun isPastTimeForSelectedDate(minutesOfDay: Int): Boolean {
        return isSelectedDateToday() && minutesOfDay < minAllowedMinutesToday()
    }

    private fun showPastTimeMessage() {
        val min = minAllowedMinutesToday()
        Toast.makeText(
            this,
            getString(R.string.msg_event_past_time, formatTime12(min / 60 % 24, min % 60)),
            Toast.LENGTH_SHORT
        ).show()
    }

    // ---- Check Venue Availability ----

    /** "13:05" style minutes -> "01:05 PM" as expected by CheckVenueAvailability. */
    private fun apiTime(minutesOfDay: Int): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, minutesOfDay / 60)
        cal.set(Calendar.MINUTE, minutesOfDay % 60)
        return SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(cal.time).uppercase(Locale.ENGLISH)
    }

    private fun checkVenueAvailabilityApi() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId.toString())
        jsonObject.addProperty("venueid", selectedVenue?.venueid?.toString() ?: "")
        jsonObject.addProperty(
            ApiRequestNames.Req_eventdate,
            binding.lblEventDate.text.toString().replace("/", "-")
        )
        jsonObject.addProperty("starttime", apiTime(fromMinutes!!))
        jsonObject.addProperty("endtime", apiTime(toMinutes!!))
        isCheckingAvailability = true
        appViewModel!!.checkVenueAvailability(jsonObject, this)
        Log.d("CheckVenueAvailability:", jsonObject.toString())
    }

    private fun showVenueAvailabilityDialog(response: VenueAvailabilityResponse?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_venue_availability, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)

        val imgStatus = view.findViewById<ImageView>(R.id.imgAvailabilityStatus)
        val lblTitle = view.findViewById<TextView>(R.id.lblAvailabilityTitle)
        val lblMessage = view.findViewById<TextView>(R.id.lblAvailabilityMessage)
        val layoutDetails = view.findViewById<View>(R.id.layoutAvailabilityDetails)
        val lblVenue = view.findViewById<TextView>(R.id.lblAvailabilityVenue)
        val lblDate = view.findViewById<TextView>(R.id.lblAvailabilityDate)
        val lblTime = view.findViewById<TextView>(R.id.lblAvailabilityTime)
        val layoutClash = view.findViewById<View>(R.id.layoutClash)
        val itemClash = view.findViewById<View>(R.id.itemClash)
        val lblBookedSlots = view.findViewById<TextView>(R.id.lblBookedSlots)
        val layoutBookedSlots = view.findViewById<LinearLayout>(R.id.layoutBookedSlots)
        val btnCancel = view.findViewById<Button>(R.id.btnAvailabilityCancel)
        val btnPositive = view.findViewById<Button>(R.id.btnAvailabilityPositive)

        val data = response?.data?.firstOrNull()
        val isAvailable = response != null && response.status == 1

        when {
            response == null -> {
                // API failed (network / server error)
                imgStatus.setImageResource(R.drawable.ic_venue_not_available)
                lblTitle.text = getString(R.string.txt_venue_check_failed)
                lblMessage.text = CommonUtil.Something_went_wrong
                layoutDetails.visibility = View.GONE
            }

            isAvailable -> {
                imgStatus.setImageResource(R.drawable.ic_venue_available)
                lblTitle.text = getString(R.string.txt_venue_available)
                lblMessage.text = response.message ?: ""
            }

            else -> {
                imgStatus.setImageResource(R.drawable.ic_venue_not_available)
                lblTitle.text = getString(R.string.txt_venue_not_available)
                lblMessage.text = response.message ?: ""
            }
        }

        if (response != null) {
            // Requested venue/date/time (fall back to what the user picked)
            lblVenue.text = data?.venuename ?: selectedVenue?.venuename ?: ""
            lblDate.text = data?.displaydate ?: binding.lblEventDate.text.toString()
            lblTime.text = data?.displaytime
                ?: "${apiTime(fromMinutes!!)} - ${apiTime(toMinutes!!)}"

            val clash = data?.clash
            if (!isAvailable && clash != null) {
                layoutClash.visibility = View.VISIBLE
                bindBookedSlot(itemClash, clash)
            }

            val slots = data?.bookedslots.orEmpty()
            if (slots.isNotEmpty()) {
                lblBookedSlots.visibility = View.VISIBLE
                layoutBookedSlots.visibility = View.VISIBLE
                slots.forEachIndexed { index, slot ->
                    val row = LayoutInflater.from(this)
                        .inflate(R.layout.item_venue_booked_slot, layoutBookedSlots, false)
                    bindBookedSlot(row, slot)
                    layoutBookedSlots.addView(row)
                    if (index < slots.size - 1) {
                        val divider = View(this)
                        divider.setBackgroundColor(Color.parseColor("#E2E8F0"))
                        layoutBookedSlots.addView(
                            divider,
                            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
                        )
                    }
                }
            }
        }

        if (isAvailable) {
            // Available: Cancel + Select Recipients (existing recipients flow)
            btnCancel.visibility = View.VISIBLE
            btnPositive.text = getString(R.string.txt_select_recipients)
            btnCancel.setOnClickListener { dialog.dismiss() }
            btnPositive.setOnClickListener {
                dialog.dismiss()
                goToRecipients()
            }
        } else {
            // Not available / failed: OK only, stay on this screen
            btnCancel.visibility = View.GONE
            btnPositive.text = getString(R.string.txt_ok)
            btnPositive.setOnClickListener { dialog.dismiss() }
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        // 90% screen width; cap height at 85% so long slot lists scroll inside the popup
        val metrics = resources.displayMetrics
        val width = (metrics.widthPixels * 0.9).toInt()
        val maxHeight = (metrics.heightPixels * 0.85).toInt()
        view.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val height = if (view.measuredHeight > maxHeight) maxHeight
        else WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.setLayout(width, height)
    }

    private fun bindBookedSlot(row: View, slot: VenueBookedSlot) {
        row.findViewById<TextView>(R.id.lblSlotTitle).text = slot.title ?: ""
        row.findViewById<TextView>(R.id.lblSlotTime).text = slot.displaytime ?: ""
        val lblBookedBy = row.findViewById<TextView>(R.id.lblSlotBookedBy)
        if (slot.bookedby.isNullOrBlank()) {
            lblBookedBy.visibility = View.GONE
        } else {
            lblBookedBy.text = getString(R.string.txt_booked_by, slot.bookedby)
        }
        val lblStatus = row.findViewById<TextView>(R.id.lblSlotStatus)
        if (slot.status.isNullOrBlank()) {
            lblStatus.visibility = View.GONE
        } else {
            lblStatus.text = slot.status
            val isRequested = slot.status.equals("Requested", ignoreCase = true)
            lblStatus.setTextColor(Color.parseColor(if (isRequested) "#B45309" else "#BE123C"))
            lblStatus.background?.mutate()
                ?.setTint(Color.parseColor(if (isRequested) "#FEF3C7" else "#FFE4E6"))
        }
    }

    /** Existing recipients navigation, unchanged (moved here so the availability popup can reuse it). */
    private fun goToRecipients() {
        if (CommonUtil.Priority.equals("p7")) {
            val i: Intent = Intent(this, HeaderRecipient::class.java)
            i.putExtra("ScreenName", ScreenNameEvent)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(i)
        } else {
            if (CommonUtil.Priority.equals("p1")) {
                val i: Intent = Intent(this, PrincipalRecipient::class.java)
                i.putExtra("ScreenName", ScreenNameEvent)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            } else {
                val i: Intent = Intent(this, AddRecipients::class.java)
                i.putExtra("ScreenName", ScreenNameEvent)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }
    }

    fun btnNextClick() {

        // Edit from My Requests: every field is editable, same validation as a new event,
        // then UpdateVenueRequest (no recipients screen)
        if (isRequestEdit) {
            if (validateNewEvent()) updateVenueRequestApi()
            return
        }

        if (!EventEdit.equals("EventEdit") && !validateNewEvent()) {
            return
        }

        Date = binding.lblEventDate!!.text.toString()
        Log.d("date_picker", Date.toString())
        MenuTitle = binding.txtTitle!!.text.toString()
        MenuDescription = binding.txtDescription!!.text.toString()
        // Selected venue sends its venuename; otherwise keep the field text (e.g. EventEdit prefill)
        Venue = selectedVenue?.venuename ?: binding.edtVenue!!.text.toString()
        Time = binding.lblEventTime!!.text.toString()

        if (EventEdit.equals("EventEdit")) {

            // event_date can arrive as "dd/MM/yyyy" (current API) or "dd MMM yyyy" (older);
            // a re-picked date is "dd/MM/yyyy". Output is always dd/MM/yyyy as before.
            formatdate = toDdMmYyyy(Date as String)

        } else {

            formatdate1 = Date as String
            var spf = SimpleDateFormat("dd/MM/yyyy")
            val newDate = spf.parse(formatdate1)
            spf = SimpleDateFormat("dd/MM/yyyy")
            formatdate1 = spf.format(newDate)

        }


        CommonUtil.Time = Time.toString()
        CommonUtil.MenuTitle = MenuTitle.toString()
        CommonUtil.MenuDescription = MenuDescription.toString()
        CommonUtil.Venuetext = Venue.toString()

        // starttime / endtime ("4:30 pm") sent with every event request.
        // EventEdit without a newly picked From time keeps the existing event time text.
        CommonUtil.EventStartTime =
            fromMinutes?.let { formatTime12(it / 60, it % 60) } ?: Time.toString()
        CommonUtil.EventEndTime = toMinutes?.let { formatTime12(it / 60, it % 60) } ?: ""


        if (!EventEdit.equals("EventEdit")) {

            CommonUtil.Date = Date.toString()
            if (!MenuTitle.isNullOrEmpty() && !MenuDescription.isNullOrEmpty() && !Date.isNullOrEmpty() && !Time.isNullOrEmpty() && !Venue.isNullOrEmpty()) {

                if (CommonUtil.menu_approvalEvent) {
                    // Check the venue first; the popup's Select Recipients continues to goToRecipients()
                    checkVenueAvailabilityApi()
                } else {
                    goToRecipients()
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Enter_Details)
            }
        } else {

            CommonUtil.Date = formatdate

            val dlg = this.let { AlertDialog.Builder(it) }
            dlg.setTitle(CommonUtil.Info)
            dlg.setMessage(CommonUtil.Are_you_edit)
            dlg.setPositiveButton(CommonUtil.Yes, DialogInterface.OnClickListener { dialog, which ->
                editSendEvents()
            })

            dlg.setNegativeButton(
                CommonUtil.CANCEL,
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })

            dlg.setCancelable(false)
            dlg.create()
            dlg.show()

        }
    }

    private fun editSendEvents() {


        val jsonObject = JsonObject()

        jsonObject.addProperty(ApiRequestNames.Req_eventid, CommonUtil.EventParticulerId)
        jsonObject.addProperty(
            ApiRequestNames.Req_collegeid,
            CommonUtil.CollegeId?.toString() ?: ""
        )
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId?.toString() ?: "")
        jsonObject.addProperty(ApiRequestNames.Req_eventdate, CommonUtil.Date)
//        jsonObject.addProperty(ApiRequestNames.Req_eventtime, CommonUtil.Time)
        jsonObject.addProperty(ApiRequestNames.Req_eventbody, CommonUtil.MenuDescription)
        jsonObject.addProperty(ApiRequestNames.Req_eventtopic, CommonUtil.MenuTitle)
        jsonObject.addProperty(ApiRequestNames.Req_processtype, "edit")
        jsonObject.addProperty(ApiRequestNames.Req_isStudent, "")
        jsonObject.addProperty(ApiRequestNames.Req_isStaff, "")
        jsonObject.addProperty(ApiRequestNames.Req_isParent, "")
        jsonObject.addProperty(ApiRequestNames.Req_eventvenue, CommonUtil.Venuetext)
        jsonObject.addProperty(ApiRequestNames.Req_callertye, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_receiveridlist, "")
        jsonObject.addProperty(ApiRequestNames.Req_receivertype, "")
        CommonUtil.addEventScheduleFields(jsonObject)

        appViewModel!!.Eventsend(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }


    fun eventdateClick() {

        val c = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { view, year, month, dayOfMonth ->
                val _year = year.toString()
                val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                val _pickedDate = "$_date/$_month/$_year"
                EditEvent = true
                Log.e("PickedDate: ", "Date: $_pickedDate") //2019-02-12
                binding.lblEventDate!!.text = _pickedDate
                // Today: hide presets that have already started
                refreshTimePresets()
            }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.MONTH]
        )
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()
    }

    fun adclick() {
        BaseActivity.LoadWebViewContext(this, AdWebURl,useDefaultAdsUrl)
    }

    private fun getVenueListApi() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId.toString())
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId.toString())
        jsonObject.addProperty(ApiRequestNames.Req_departmentid, CommonUtil.DepartmentId)
        appViewModel!!.getVenueList(jsonObject, this)
        Log.d("GetVenueList:", jsonObject.toString())
    }

    private fun showVenueDropdown() {
        if (venueList.isEmpty()) {
            Toast.makeText(this, getString(R.string.txt_no_venues_available), Toast.LENGTH_SHORT)
                .show()
            return
        }
        val names = venueList.map { it.displayname ?: it.venuename ?: "" }
        val popup = ListPopupWindow(this)
        popup.anchorView = binding.edtVenue
        popup.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        )
        popup.isModal = true
        popup.setOnItemClickListener { _, _, position, _ ->
            selectVenue(venueList[position])
            popup.dismiss()
        }
        popup.show()
    }

    /** Single place to select a venue (dropdown and Quick chips): updates field, id and chips. */
    private fun selectVenue(venue: VenueListData) {
        selectedVenue = venue
        CommonUtil.EventVenueId = venue.venueid
        binding.edtVenue.text = venue.displayname ?: venue.venuename ?: ""
        val flex = binding.flexQuickVenue
        for (i in 0 until flex.childCount) {
            val chip = flex.getChildAt(i)
            chip.isSelected = chip.tag == venue.venueid
        }
    }

    private fun showVenueContent() {
        binding.layoutVenueEmpty.visibility = View.GONE
        binding.edtVenue.visibility = View.VISIBLE
        binding.lblSelectVenuePill.visibility = View.VISIBLE
        bindQuickVenues()
        // Request edit: swap the prefilled venue for the matching one from the list
        // (gives its full displayname and highlights its Quick chip)
        selectedVenue?.venueid?.let { id ->
            venueList.firstOrNull { it.venueid == id }?.let { selectVenue(it) }
        }
    }

    /** Empty / failed state. Edit modes keep the venue field visible so the prefilled value stays usable. */
    private fun showVenueEmptyState(message: String) {
        binding.layoutQuickVenue.visibility = View.GONE
        if (EventEdit.equals("EventEdit") || isRequestEdit) {
            binding.layoutVenueEmpty.visibility = View.GONE
            binding.edtVenue.visibility = View.VISIBLE
            return
        }
        binding.lblVenueEmptyMessage.text = message
        binding.layoutVenueEmpty.visibility = View.VISIBLE
        binding.edtVenue.visibility = View.GONE
        binding.lblSelectVenuePill.visibility = View.GONE
        // A previously selected venue is no longer valid
        selectedVenue = null
        CommonUtil.EventVenueId = null
        binding.edtVenue.text = ""
    }

    /** Quick chips: first CommonUtil.QuickVenueCount venues (fewer if the API returns fewer). */
    private fun bindQuickVenues() {
        val flex = binding.flexQuickVenue
        flex.removeAllViews()
        val quick = venueList.take(CommonUtil.QuickVenueCount.coerceAtLeast(0))
        if (quick.isEmpty()) {
            binding.layoutQuickVenue.visibility = View.GONE
            return
        }
        quick.forEach { venue ->
            val chip = LayoutInflater.from(this)
                .inflate(R.layout.item_event_chip, flex, false) as TextView
            chip.text = venue.venuename ?: venue.displayname ?: ""
            chip.tag = venue.venueid
            chip.isSelected = selectedVenue?.venueid == venue.venueid
            chip.setOnClickListener { selectVenue(venue) }
            flex.addView(chip)
        }
        binding.layoutQuickVenue.visibility = View.VISIBLE
    }

    // ---- Time presets ----

    /** Shows CommonUtil.EventTimePresets; for today, hides presets whose start time has passed. */
    private fun refreshTimePresets() {
        val flex = binding.flexTimePresets
        flex.removeAllViews()
        val presets = CommonUtil.EventTimePresets.filter { preset ->
            preset.endMinutes > preset.startMinutes && !isPastTimeForSelectedDate(preset.startMinutes)
        }
        if (presets.isEmpty()) {
            binding.layoutTimePresets.visibility = View.GONE
            return
        }
        presets.forEach { preset ->
            val chip = LayoutInflater.from(this)
                .inflate(R.layout.item_event_chip, flex, false) as TextView
            chip.text = preset.label
            chip.tag = preset
            chip.setOnClickListener { applyTimePreset(preset) }
            flex.addView(chip)
        }
        binding.layoutTimePresets.visibility = View.VISIBLE
        updatePresetSelection()
    }

    private fun applyTimePreset(preset: EventTimePreset) {
        fromMinutes = preset.startMinutes
        toMinutes = preset.endMinutes
        binding.lblEventTime.text = formatTime12(preset.startMinutes / 60, preset.startMinutes % 60)
        binding.lblEventToTime.text = formatTime12(preset.endMinutes / 60, preset.endMinutes % 60)
        updateDuration()
        updatePresetSelection()
    }

    /** Highlights the preset chip that matches the current From/To. */
    private fun updatePresetSelection() {
        val flex = binding.flexTimePresets
        for (i in 0 until flex.childCount) {
            val chip = flex.getChildAt(i)
            val preset = chip.tag as? EventTimePreset
            chip.isSelected = preset != null &&
                    preset.startMinutes == fromMinutes && preset.endMinutes == toMinutes
        }
    }

    // ---- Edit modes ----

    /**
     * Edit from Events (Upcoming) > Details: only Title and Description can change.
     * Venue, date and times are shown but locked; the old ManageEvents edit is used.
     */
    private fun setupEventEdit() {
        val event = eventsdata ?: return
        binding.lblMenuTitle.text = getString(R.string.txt_edit_event_title)
        binding.btnConfirm.text = getString(R.string.txt_update_event)
        fromMinutes = parseTimeToMinutes(event.starttime) ?: parseTimeToMinutes(event.event_time)
        toMinutes = parseTimeToMinutes(event.endtime)
        toMinutes?.let { binding.lblEventToTime.text = formatTime12(it / 60, it % 60) }
        updateDuration()
        // Original venue id is sent back unchanged with the edit
        CommonUtil.EventVenueId = event.venueid?.takeIf { it > 0 }

        listOf(binding.rytEventDate, binding.rytEventTime, binding.rytEventToTime, binding.edtVenue)
            .forEach { lockField(it) }
        binding.lblSelectVenuePill.visibility = View.GONE
        binding.layoutQuickVenue.visibility = View.GONE
        binding.layoutVenueEmpty.visibility = View.GONE
        binding.layoutTimePresets.visibility = View.GONE
    }

    /** Disabled views ignore taps (even with a click listener); dimmed so it reads as read-only. */
    private fun lockField(view: View) {
        view.isEnabled = false
        view.alpha = 0.6f
    }

    /** Edit from My Requests (waiting request): every field is prefilled and editable. */
    private fun setupRequestEdit() {
        val request = venueRequest ?: return
        binding.lblMenuTitle.text = getString(R.string.txt_edit_request_title)
        binding.btnConfirm.text = getString(R.string.txt_update_request)

        binding.txtTitle.setText(request.title.orEmpty())
        binding.txtDescription.setText(request.description.orEmpty())
        binding.lblEventDate.text = request.eventdate.orEmpty()   // dd/MM/yyyy

        fromMinutes = parseTimeToMinutes(request.starttime)       // "14:00"
        toMinutes = parseTimeToMinutes(request.endtime)
        fromMinutes?.let { binding.lblEventTime.text = formatTime12(it / 60, it % 60) }
        toMinutes?.let { binding.lblEventToTime.text = formatTime12(it / 60, it % 60) }
        updateDuration()

        // Prefilled venue until the venue list loads (showVenueContent then swaps in the list item)
        if (request.venueid != null) {
            selectedVenue = VenueListData().apply {
                venueid = request.venueid
                venuename = request.venuename
                location = request.location
                displayname = listOfNotNull(
                    request.venuename?.takeIf { it.isNotBlank() },
                    request.location?.takeIf { it.isNotBlank() }
                ).joinToString(" · ")
            }
            CommonUtil.EventVenueId = request.venueid
            binding.edtVenue.text = selectedVenue?.displayname.orEmpty()
        }
    }

    /** api/AppDetailsBal/UpdateVenueRequest with the edited values. */
    private fun updateVenueRequestApi() {
        val request = venueRequest ?: return
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId.toString())
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId.toString())
        jsonObject.addProperty(ApiRequestNames.Req_bookingid, request.bookingid?.toString() ?: "")
        jsonObject.addProperty(ApiRequestNames.Req_venueid, selectedVenue?.venueid?.toString() ?: "")
        jsonObject.addProperty(ApiRequestNames.Req_title, binding.txtTitle.text.toString().trim())
        jsonObject.addProperty(
            ApiRequestNames.Req_Description, binding.txtDescription.text.toString().trim()
        )
        jsonObject.addProperty(ApiRequestNames.Req_eventdate, binding.lblEventDate.text.toString())
        jsonObject.addProperty(ApiRequestNames.Req_starttime, requestApiTime(fromMinutes!!))
        jsonObject.addProperty(ApiRequestNames.Req_endtime, requestApiTime(toMinutes!!))
        isUpdatingRequest = true
        appViewModel!!.updateVenueRequest(jsonObject, this)
        Log.d("UpdateVenueRequest:", jsonObject.toString())
    }

    /** Minutes -> "3:00 PM" (UpdateVenueRequest format). */
    private fun requestApiTime(minutesOfDay: Int): String {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, minutesOfDay / 60)
        cal.set(Calendar.MINUTE, minutesOfDay % 60)
        return SimpleDateFormat("h:mm a", Locale.ENGLISH).format(cal.time).uppercase(Locale.ENGLISH)
    }

    /** "14:00", "2:00 PM", "02:00 pm" -> minutes from midnight; null if blank/unparseable. */
    private fun parseTimeToMinutes(value: String?): Int? {
        val text = value?.trim()?.uppercase(Locale.ENGLISH)
        if (text.isNullOrEmpty()) return null
        for (pattern in listOf("h:mm a", "H:mm")) {
            try {
                val format = SimpleDateFormat(pattern, Locale.ENGLISH).apply { isLenient = false }
                val date = format.parse(text) ?: continue
                val cal = Calendar.getInstance().apply { time = date }
                return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
            } catch (e: ParseException) {
                // try the next pattern
            }
        }
        return null
    }

    /** "dd/MM/yyyy" or "dd MMM yyyy" -> "dd/MM/yyyy"; unchanged if neither matches. */
    private fun toDdMmYyyy(value: String): String {
        val output = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        for (pattern in listOf("dd/MM/yyyy", "dd MMM yyyy")) {
            try {
                val format = SimpleDateFormat(pattern, Locale.ENGLISH).apply { isLenient = false }
                format.parse(value.trim())?.let { return output.format(it) }
            } catch (e: ParseException) {
                // try the next pattern
            }
        }
        return value
    }

    companion object {
        const val MODE_EVENT_EDIT = "EventEdit"
        const val MODE_REQUEST_EDIT = "VenueRequestEdit"
        const val EXTRA_VENUE_REQUEST = "VenueRequestData"
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        AdForCollegeApi()
        super.onResume()
    }
}