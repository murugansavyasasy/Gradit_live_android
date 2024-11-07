package com.vsca.vsnapvoicecollege.ActivitySender

import android.annotation.SuppressLint
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
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Activities.Events
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.GetEventDetailsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AddEvents : ActionBarActivity() {

    var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var MenuTitle: String? = null
    var MenuDescription: String? = null
    var Venue: String? = null
    var ScreenNameEvent: String? = null
    var Date: String? = null
    var Time: String? = null
    var eventsdata: GetEventDetailsData? = null
    var EventEdit: String? = null
    var formatdate1 = ""
    var formatdate = ""
    var EditEvent: Boolean = false

    @JvmField
    @BindView(R.id.rytEventDate)
    var rytEventDate: RelativeLayout? = null

    @JvmField
    @BindView(R.id.rytEventTime)
    var rytEventTime: RelativeLayout? = null

    @JvmField
    @BindView(R.id.lblEventDate)
    var lblEventDate: TextView? = null

    @JvmField
    @BindView(R.id.lblEventTime)
    var lblEventTime: TextView? = null

    @JvmField
    @BindView(R.id.lblWordCount)
    var lblWordCount: TextView? = null

    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    @JvmField
    @BindView(R.id.txtTitle)
    var txtTitle: EditText? = null

    @JvmField
    @BindView(R.id.edt_venue)
    var edt_venue: EditText? = null

    @JvmField
    @BindView(R.id.btnConfirm)
    var btnConfirm: Button? = null


    @JvmField
    @BindView(R.id.txtDescription)
    var txtDescription: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this)
        imgRefresh!!.visibility = View.GONE


        eventsdata = intent.getSerializableExtra("EventsData") as? GetEventDetailsData
        EventEdit = intent.getSerializableExtra("EventEdit") as String?

        ScreenNameEvent = "ScreenNameEvent"
        if (EventEdit.equals("EventEdit")) {

            btnConfirm!!.text = "Send"
            CommonUtil.EventsendType = "Edit"
            lblEventDate!!.text = eventsdata!!.event_date
            lblEventTime!!.text = eventsdata!!.event_time
            txtTitle!!.setText(eventsdata!!.topic)
            txtDescription!!.setText(eventsdata!!.body)
            edt_venue!!.setText(eventsdata!!.venue)
            ScreenNameEvent = "Event_Edit"

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

        appViewModel!!.AdvertisementLiveData?.observe(this,
            Observer<GetAdvertisementResponse?> { response ->
                if (response != null) {
                    val status = response.status
                    val message = response.message
                    if (status == 1) {
                        GetAdForCollegeData = response.data!!
                        for (j in GetAdForCollegeData.indices) {
                            AdSmallImage = GetAdForCollegeData[j].add_image
                            AdBackgroundImage = GetAdForCollegeData[0].background_image!!
                            AdWebURl = GetAdForCollegeData[0].add_url.toString()
                        }
                        Glide.with(this).load(AdBackgroundImage)
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgthumb!!)
                    }
                }
            })


        rytEventTime!!.setOnClickListener {

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

        txtDescription!!.addTextChangedListener(mTextEditorWatcher)


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
            lblWordCount!!.text = s.length.toString() + "/500"
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private val timePickerDialogListener: TimePickerDialog.OnTimeSetListener =
        object : TimePickerDialog.OnTimeSetListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

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


                lblEventTime!!.text = formattedTime

                Time = timeCoversion12to24(formattedTime)
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

    @SuppressLint("NewApi")
    @OnClick(R.id.btnConfirm)
    fun btnNextClick() {

        Date = lblEventDate!!.text.toString()
        Log.d("date_picker", Date.toString())
        MenuTitle = txtTitle!!.text.toString()
        MenuDescription = txtDescription!!.text.toString()
        Venue = edt_venue!!.text.toString()
        Time = lblEventTime!!.text.toString()

        if (EventEdit.equals("EventEdit")) {

            if (EditEvent) {

                formatdate = Date as String
                var spf = SimpleDateFormat("dd/MM/yyyy")
                val newDate = spf.parse(formatdate)
                spf = SimpleDateFormat("dd/MM/yyyy")
                formatdate = spf.format(newDate)

            } else {

                formatdate = Date as String
                var spf = SimpleDateFormat("dd MMM yyyy")
                val newDate = spf.parse(formatdate)
                spf = SimpleDateFormat("dd/MM/yyyy")
                formatdate = spf.format(newDate)

            }

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


        if (!EventEdit.equals("EventEdit")) {

            CommonUtil.Date = Date.toString()
            if (!MenuTitle.isNullOrEmpty() && !MenuDescription.isNullOrEmpty() && !Date.isNullOrEmpty() && !Time.isNullOrEmpty() && !Venue.isNullOrEmpty()) {

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
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_eventdate, CommonUtil.Date)
        jsonObject.addProperty(ApiRequestNames.Req_eventtime, CommonUtil.Time)
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

        appViewModel!!.Eventsend(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }


    @OnClick(R.id.rytEventDate)
    fun eventdateClick() {
        //  CommonUtil.DatepickerHidePriviousdate(this, lblEventDate!!)

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
                lblEventDate!!.text = _pickedDate
            }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.MONTH]
        )
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()
    }

    @OnClick(R.id.imgImagePdfback)
    fun imgImagePdfback() {
        super.onBackPressed()
    }

    @OnClick(R.id.btnCancel)
    fun btnCancelClick() {
        super.onBackPressed()
    }

    @OnClick(R.id.LayoutAdvertisement)
    fun adclick() {
        BaseActivity.LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        AdForCollegeApi()
        super.onResume()
    }
}














