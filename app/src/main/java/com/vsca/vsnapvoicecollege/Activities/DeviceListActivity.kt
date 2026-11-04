package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapter.DeviceAdapter
import com.vsca.vsnapvoicecollege.Model.DeviceDetails
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.applyPriorityColor
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.ViewModel.Auth
import com.vsca.vsnapvoicecollege.databinding.ActivityDeviceListBinding

class DeviceListActivity : BaseActivity<ActivityDeviceListBinding>() {

    override var appViewModel: App? = null
    var authViewModel: Auth? = null
    var MobileNumber: String? = null
    var Password: String? = null
    var LoginData: List<LoginDetails> = ArrayList()

    private var deviceAdapter: DeviceAdapter? = null

    private var maxDevices = 0

    override fun inflateBinding(): ActivityDeviceListBinding {
        return ActivityDeviceListBinding.inflate(layoutInflater)
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_device_list

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityDeviceListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        isToolBarPrimaryTheme1(
            mainViewId = R.id.Main,
            statusBarBgView = binding.statusBarBackground
        )
        binding.LayoutHeader.setBackgroundColor(Color.parseColor("#F3F4FA"))

        MobileNumber =CommonUtil.MobileNUmber.takeIf { it.isNotEmpty() }
                    ?: SharedPreference.getSH_MobileNumber(this)

        Password=CommonUtil.Login_Password
        Log.d("OTPDeviceList",CommonUtil.Login_OTP)

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        authViewModel = ViewModelProvider(this)[Auth::class.java]
        authViewModel!!.init()

        @Suppress("DEPRECATION")
        val deviceList = intent.getSerializableExtra(
            CommonUtil.EXTRA_DEVICE_LIST
        ) as? ArrayList<DeviceDetails> ?: arrayListOf()

        binding.imgheaderBack.setOnClickListener {
            onBackPressed()
        }

        authViewModel!!.loginResposneLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    if (response.data?.getOrNull(0)?.otpRequired==true){

                        CommonUtil.ivrnumbers = ArrayList(
                            response.data?.getOrNull(0)?.ivrnumbers ?: emptyList()
                        )

                        CommonUtil.isDeviceLimitReached =response.data?.getOrNull(0)?.otpRequired ?:false


                        Log.d("IVR", "Size: ${CommonUtil.ivrnumbers.size}")
                        CommonUtil.ivrnumbers.forEachIndexed { index, number ->
                            Log.d("IVR", "[$index] = $number")
                        }

//                        Log.d("ivrNumbers", CommonUtil.ivrnumbers.toString())
//                        CommonUtil.OptMessege = response.Message

                        val intents = Intent(this@DeviceListActivity, OtpRewamp::class.java)
                        intents.putExtra(
                            CommonUtil.EXTRA_AUTH_SOURCE,
                            CommonUtil.AUTH_SOURCE_LOGIN
                        )
                        intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                        startActivity(intents)

                    }
                    else{
                        LoginData = response.data?.getOrNull(0)?.login_data?:emptyList()

                        if (LoginData.size != 0) {
                            SharedPreference.setFirstTimeLoggedInUser(this@DeviceListActivity, true)
                            CommonUtil.UserDataList = response.data?.get(0)?.login_data as ArrayList<LoginDetails>?
                            SharedPreference.putLoginDetails(
                                this@DeviceListActivity,
                                MobileNumber,
                                Password
                            )
                            SetLoginData(LoginData)

                            Log.d("LoginDataSize", LoginData.size.toString())
                            if (LoginData.size > 1) {
                                val i = Intent(this@DeviceListActivity, LoginRolesRewamp::class.java)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(i)
                                finishAffinity()
                            } else {
                                val i = Intent(this@DeviceListActivity, DashBoardActivityRewamp::class.java)
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(i)
                                finishAffinity()
                            }
                        } else {
                            CommonUtil.ApiAlert(this@DeviceListActivity, message)
                        }
                    }
                } else {
                        CommonUtil.ApiAlert(this@DeviceListActivity, message)
                }
            } else {
                CommonUtil.ApiAlert(this@DeviceListActivity, CommonUtil.Something_went_wrong)
            }
        }

        setupList(deviceList)
    }

    private fun setupList(deviceList: ArrayList<DeviceDetails>) {
        maxDevices = deviceList.size   // limit = devices returned by the API

        deviceAdapter = DeviceAdapter(deviceList) { device, _ ->
            onRemoveDeviceClicked(device)
        }

        binding.rvDevices.apply {
            layoutManager = LinearLayoutManager(this@DeviceListActivity)
            adapter = deviceAdapter
        }

        updateUi()
    }

    private fun updateUi() {
        val used = deviceAdapter?.getCount() ?: 0

        binding.lblDevicesUsed.text = "$used of $maxDevices devices used"
        binding.progressDevices.max = maxOf(maxDevices, 1)
        binding.progressDevices.progress = used

        if (used == 0) {
            binding.ErrorMessage.visibility = View.VISIBLE
            binding.rvDevices.visibility = View.GONE
            binding.lblFooterNote.visibility = View.GONE
        } else {
            binding.ErrorMessage.visibility = View.GONE
            binding.rvDevices.visibility = View.VISIBLE
            binding.lblFooterNote.visibility = View.VISIBLE
        }
    }

    private fun SetLoginData(data: List<LoginDetails>) {

        for (i in data.indices) {
            CommonUtil.Collegename = data.get(i).colgname.toString()
            CommonUtil.Priority = data.get(i).priority!!
            CommonUtil.MemberId = data.get(i).memberid
            CommonUtil.CollegeCity = data.get(i).colgcity.toString()
            CommonUtil.MemberName = data.get(i).membername!!
            CommonUtil.MemberType = data.get(i).loginas!!
            CommonUtil.CollegeId = data.get(i).colgid
            CommonUtil.DivisionId = data.get(i).divisionId!!
            CommonUtil.deptname = data.get(i).deptname!!
            CommonUtil.Courseid = data.get(i).courseid!!
            CommonUtil.DepartmentId = data.get(i).deptid!!
            CommonUtil.SemesteName = data[i].semestername!!
            CommonUtil.YearId = data.get(i).yearid!!
            CommonUtil.isAllowtomakecall = data[i].is_allow_to_make_call
            CommonUtil.SemesterId = data.get(i).semesterid!!
            CommonUtil.SectionId = data.get(i).sectionid!!
            CommonUtil.isParentEnable = data.get(i).is_parent_target_enabled!!
            CommonUtil.CollegeLogo = data.get(i).colglogo!!

        }
    }

    private fun onRemoveDeviceClicked(device: DeviceDetails) {
        CommonUtil.ConfirmAlert(
            activity = this,
            title = "Remove Device?",
            msg = "Are you sure you want to remove \"${device.devicename}\"? " +
                    "It will be signed out of your account.",
            confirmText = "Remove"
        ) {
            callRemoveDeviceApi(device)
        }
    }


    private fun callRemoveDeviceApi(device: DeviceDetails) {
        val mobile = MobileNumber.orEmpty()
        val password = Password.orEmpty()

        if (mobile.isEmpty() || password.isEmpty()) {
            CommonUtil.ApiAlert(this, "Mobile number and password is required")
            return
        }

        val deviceId = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: ""
        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_mobileNumber, mobile)
        jsonObject.addProperty(ApiRequestNames.Req_password, password)
        jsonObject.addProperty("DeviceId", deviceId)
        jsonObject.addProperty("DeviceName", deviceName)
        jsonObject.addProperty("LogoutDeviceSessionId", device.id)
        jsonObject.addProperty("ValidatedOtp", CommonUtil.Login_OTP)

        authViewModel!!.login(jsonObject, this@DeviceListActivity)
    }
    fun isToolBarPrimaryTheme1(
        mainViewId: Int,
        statusBarBgView: View
    ) {
        enableEdgeToEdge()

        val mainView = findViewById<View>(mainViewId)

        // White status bar icons
        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = true

        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            statusBarBgView.updateLayoutParams {
                height = systemBars.top
            }

            view.updatePadding(
                left = systemBars.left,
                right = systemBars.right,
                bottom = systemBars.bottom
            )

            insets
        }

        // White status bar background
        statusBarBgView.setBackgroundColor(Color.parseColor("#F3F4FA"))

        window.statusBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )

            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )

            window.statusBarColor = Color.TRANSPARENT

            window.navigationBarColor = Color.parseColor("#F3F4FA")
        }
    }

}