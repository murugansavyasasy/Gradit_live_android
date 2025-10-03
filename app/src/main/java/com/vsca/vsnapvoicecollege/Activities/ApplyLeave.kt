package com.vsca.vsnapvoicecollege.Activities

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.LeaveTypeData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.SetTheme
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class ApplyLeave : ActionBarActivity() {


    var appViewModel: App? = null
    var AdWebURl: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var StartDate: String? = null
    var EndDate: String? = null
    var Numberoddays: String? = null
    var Date: Int? = null
    var Month: Int? = null
    var Year: Int? = null
    var GetLeaveTypeData: List<LeaveTypeData> = ArrayList()
    var bookArrayList = ArrayList<String>()
    var LeaveName: String? = null
    var LeaveType: String? = null
    var LeaveTypeID = 0
    private lateinit var binding: ActivityApplyLeaveBinding


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityApplyLeaveBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionbarWithoutBottom(this)
        imgRefresh!!.visibility = View.GONE

        binding.txtNoofDays!!.isEnabled = false

        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }

        binding.LayoutAdvertisement.setOnClickListener { adclick() }

        binding.lnrFromDate.setOnClickListener {
            startdata()
        }

        binding.lnrToDate.setOnClickListener {
            enddate()
        }

        binding.btnConfirm.setOnClickListener {
            StartDate = binding.lblFromDate!!.text.toString()
            EndDate = binding.lblToDate!!.text.toString()
            Numberoddays = binding.txtNoofDays!!.text.toString()
            CommonUtil.LeaveReasion = binding.txtLeaveReason!!.text.toString()

            CommonUtil.leavestartdate = StartDate.toString()

            CommonUtil.leaveenddate = EndDate.toString()
            CommonUtil.numberofday = Numberoddays.toString()

            if (CommonUtil.leavestartdate.isNullOrEmpty() || CommonUtil.leaveenddate.isNullOrEmpty() || CommonUtil.numberofday.isNullOrEmpty() || CommonUtil.LeaveReasion.isNullOrEmpty() || LeaveTypeID.equals(
                    0
                )
            ) {

                CommonUtil.ApiAlert(this, CommonUtil.fill_the_details)

            } else {

                val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@ApplyLeave)
                alertDialog.setTitle(CommonUtil.Hold_on)
                alertDialog.setMessage(CommonUtil.Apply_Leave)
                alertDialog.setPositiveButton(CommonUtil.Yes) { _, _ ->


                    if (CommonUtil.Leavetype.equals("Edit")) {

                        Manageleavesend("edit", LeaveTypeID.toString())

                    } else {

                        Manageleavesend("add", LeaveTypeID.toString())

                    }
                }

                alertDialog.setNegativeButton(CommonUtil.No) { _, _ ->


                }
                val alert: AlertDialog = alertDialog.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()

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
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imgAdvertisement!!)
                        Log.d("AdBackgroundImage", AdBackgroundImage!!)

                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.imgthumb!!)
                    }
                }
            })

        try {

            appViewModel!!.LeaveTypeLiveData!!.observe(this) { response ->
                if (response != null) {
                    val status = response.status
                    val message = response.message
                    bookArrayList.clear()
                    if (status == 1) {
                        GetLeaveTypeData = response.data!!
                        if (GetLeaveTypeData.size > 0) {
                            AdForCollegeApi()

                            bookArrayList.add("Select Leave Type")
                            for (i in GetLeaveTypeData.indices) {
                                LeaveName = GetLeaveTypeData.get(i).leavetypename!!
                                bookArrayList.add(LeaveName!!)
                            }
                            val adapter: ArrayAdapter<String>
                            adapter = ArrayAdapter(
                                this, android.R.layout.simple_spinner_item, bookArrayList
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.SpinnerLeaveType!!.adapter = adapter
                            binding.SpinnerLeaveType!!.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        adapterView: AdapterView<*>?, view: View, i: Int, l: Long
                                    ) {
                                        val index: Int =
                                            binding.SpinnerLeaveType!!.selectedItemPosition
                                        LeaveTypeID = index
                                        LeaveType =
                                            binding.SpinnerLeaveType!!.selectedItem.toString()
                                    }

                                    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                                }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        appViewModel!!.Manageleave!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status != null) {
                    if (status == 1) {

                        val dlg = this.let { AlertDialog.Builder(it) }
                        dlg.setTitle(CommonUtil.Info)
                        dlg.setMessage(message)
                        dlg.setPositiveButton(CommonUtil.OK,
                            DialogInterface.OnClickListener { dialog, which ->
                                val i: Intent =

                                    Intent(this, Attendance::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                CommonUtil.Leavetype = ""
                                startActivity(i)

                            })

                        dlg.setCancelable(false)
                        dlg.create()
                        dlg.show()

                    } else {
                        CommonUtil.ApiAlert(this, message)
                    }
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        if (CommonUtil.Leavetype == "Edit") {


            val date1 = CommonUtil.leavestartdate
            var spf = SimpleDateFormat("dd MMM yyyy")
            val newDate: Date? = spf.parse(date1)
            spf = SimpleDateFormat("dd/MM/yyyy")
            val date = spf.format(newDate)

            binding.lblFromDate!!.text = date
            CommonUtil.leavestartdate = date

            val date2 = CommonUtil.leaveenddate
            var spf2 = SimpleDateFormat("dd MMM yyyy")
            val newDate2: Date? = spf2.parse(date2)
            spf2 = SimpleDateFormat("dd/MM/yyyy")
            val date4 = spf2.format(newDate2)

            binding.lblToDate!!.text = date4
            CommonUtil.leaveenddate = date4

            binding.txtNoofDays!!.setText(CommonUtil.numberofday)
            binding.txtLeaveReason!!.setText(CommonUtil.LeaveReasion)

        }

        binding.txtLeaveReason!!.addTextChangedListener(mTextEditorWatcher)
        binding.txtLeaveReason!!.enableScrollText()
    }


    private fun AdForCollegeApi() {

        val mobilenumber = SharedPreference.getSH_MobileNumber(this)
        val devicetoken = SharedPreference.getSH_DeviceToken(this)
        Log.d("_isMobilenumber", mobilenumber.toString())
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

    private fun Manageleavesend(Proccesstype: String, LeaveTypeID: String) {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_memberid, CommonUtil.MemberId)


        if (Proccesstype.equals("add")) {
            jsonObject.addProperty(ApiRequestNames.Req_applicationid, "0")
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_applicationid, CommonUtil.LeaveApplicationID)
        }

        jsonObject.addProperty(ApiRequestNames.Req_leavetypeid, LeaveTypeID)
        jsonObject.addProperty(ApiRequestNames.Req_leavefromdate, CommonUtil.leavestartdate)
        jsonObject.addProperty(ApiRequestNames.Req_leavetodate, CommonUtil.leaveenddate)
        jsonObject.addProperty(ApiRequestNames.Req_numofdays, CommonUtil.numberofday)
        jsonObject.addProperty(ApiRequestNames.Req_processtype, Proccesstype)
        jsonObject.addProperty(ApiRequestNames.Req_clgsectionid, CommonUtil.SectionId)
        jsonObject.addProperty(ApiRequestNames.Req_leavereason, CommonUtil.LeaveReasion)

        appViewModel!!.Manageleave(jsonObject, this)
        Log.d("SMSJsonObject", jsonObject.toString())
    }

    override val layoutResourceId: Int
        protected get() = R.layout.activity_apply_leave


    fun EditText.enableScrollText() {
        overScrollMode = View.OVER_SCROLL_ALWAYS
        scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
        isVerticalScrollBarEnabled = true
        setOnTouchListener { view, event ->
            if (view is EditText) {
                if (!view.text.isNullOrEmpty()) {
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(
                            false
                        )
                    }
                }
            }
            false
        }
    }

    private val mTextEditorWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            binding.tvCount!!.text = s.length.toString() + "/500"
        }

        override fun afterTextChanged(s: Editable) {}
    }

    fun startdata() {

        val c = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this, { view, year, month, dayOfMonth ->
                val _year = year.toString()
                val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                val _pickedDate = "$_date/$_month/$year"
                Log.e("PickedDate: ", "Date: $_pickedDate") //2019-02-12
                binding.lblFromDate!!.text = _pickedDate

                Date = _date.toInt()
                Month = _month.toInt()
                Year = _year.toInt()
            }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.MONTH]
        )
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000
        dialog.show()

    }

    fun enddate() {

        if (binding.lblFromDate!!.text.equals("")) {

            CommonUtil.ApiAlert(this, CommonUtil.from_date)

        } else {

            val c = Calendar.getInstance()
            val dialog = DatePickerDialog(
                this, { view, year, month, dayOfMonth ->
                    val _year = year.toString()
                    val _month = if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                    val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                    val _pickedDate = "$_date/$_month/$_year"


                    binding.lblToDate!!.text = _pickedDate

                    if (!binding.lblFromDate!!.text.isNullOrEmpty() && !binding.lblToDate!!.text.isNullOrEmpty()) {

                        StartDate = binding.lblFromDate!!.text.toString()
                        EndDate = binding.lblToDate!!.text.toString()

                        val mDateFormat = SimpleDateFormat("dd/MM/yyyy")

                        val mDate11 = StartDate?.let { mDateFormat.parse(it) }
                        val mDate22 = EndDate?.let { mDateFormat.parse(it) }

                        val mDifference = kotlin.math.abs(mDate11!!.time - mDate22!!.time)
                        val mDifferenceDates = mDifference / (24 * 60 * 60 * 1000)
                        val mDayDifference = mDifferenceDates.toString()

                        if (mDayDifference.equals("0")) {
                            binding.txtNoofDays!!.setText("1")
                        } else {
                            binding.txtNoofDays!!.setText(mDayDifference)

                        }

                    }


                }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.MONTH]
            )

            val minDay = Date
            val minMonth = Month
            val minYear = Year
            if (minYear != null) {
                if (minMonth != null) {
                    if (minDay != null) {
                        c.set(minYear, minMonth - 1, minDay)
                    }
                }
            }
            dialog.datePicker.minDate = c.timeInMillis
            dialog.show()

        }
    }

    fun adclick() {
        BaseActivity.LoadWebViewContext(this, AdWebURl)
    }

    fun Leavetype() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.Appid)
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        appViewModel!!.getLeaveType(jsonObject, this)
        Log.d("LeavetypeRequest:", jsonObject.toString())

    }

    override fun onResume() {

        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        Leavetype()
        super.onResume()

    }
}



