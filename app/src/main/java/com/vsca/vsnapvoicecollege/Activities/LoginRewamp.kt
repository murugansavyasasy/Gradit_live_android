package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.ViewModel.Auth
import com.vsca.vsnapvoicecollege.databinding.LoginRewampBinding

class LoginRewamp : AppCompatActivity() {

    var lblcontent: TextView? = null
    var MobileNumber: String? = null
    var Password: String? = null
    var authViewModel: Auth? = null
    var LoginData: List<LoginDetails> = ArrayList()
    var appViewModel: App? = null
    private lateinit var binding: LoginRewampBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginRewampBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authViewModel = ViewModelProvider(this)[Auth::class.java]
        authViewModel!!.init()
        CommonUtil.MenuListDashboard.clear()
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        isToolBarPrimaryTheme1(
            mainViewId = R.id.main,
            statusBarBgView = binding.statusBarBackground
        )

        binding.txtForgetpassword!!.setOnClickListener {
            GetOtp()
        }
        binding.imgPasswordopen.setOnClickListener { imgpasswordlockClick() }

        binding.txtNext.setOnClickListener {
            LoginbtnClick()
        }
        binding.btnBack.setOnClickListener { onBackPressed() }


        MobileNumber = intent.getStringExtra("MobileNumber")
            ?.takeIf { it.isNotEmpty() }
            ?: CommonUtil.MobileNUmber.takeIf { it.isNotEmpty() }
            ?: SharedPreference.getSH_MobileNumber(this)
        var countryDetails = SharedPreference.getCountryDetails(this)

        binding.phoneNumberEdt!!.text =
            "+${countryDetails.countyCode} $MobileNumber"


        authViewModel!!.loginResposneLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    LoginData = response.data!!
                    if (LoginData.size != 0) {
                        CommonUtil.UserDataList = response.data as ArrayList<LoginDetails>?
                        SharedPreference.putLoginDetails(this@LoginRewamp, MobileNumber, Password)
                        SetLoginData(LoginData)

                        Log.d("LoginDataSize", LoginData.size.toString())
                        if (LoginData.size > 1) {
                            val i = Intent(this@LoginRewamp, LoginRolesRewamp::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                            finishAffinity()
                        } else {
                            val i = Intent(this@LoginRewamp, DashBoardActivityRewamp::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                            finishAffinity()
                        }
                    } else {
                        CommonUtil.ApiAlert(this@LoginRewamp, message)
                    }
                } else {
                    CommonUtil.ApiAlert(this@LoginRewamp, message)
                }
            } else {
                CommonUtil.ApiAlert(this@LoginRewamp, CommonUtil.Something_went_wrong)
            }
        }

        appViewModel!!.GetOtpNew!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    // Assign API response
                    CommonUtil.ivrnumbers = response.data[0].ivrnumbers

                    Log.d("IVR", "Size: ${CommonUtil.ivrnumbers.size}")
                    CommonUtil.ivrnumbers.forEachIndexed { index, number ->
                        Log.d("IVR", "[$index] = $number")
                    }
                    Log.d("ivrNumbers", CommonUtil.ivrnumbers.toString())
                    CommonUtil.OptMessege = response.Message

                    val intents = Intent(this, OtpRewamp::class.java)
                    intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intents)

                } else {
                    CommonUtil.ApiAlert(this, message)
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }
    }

    fun LoginbtnClick() {
        Password = binding.passwordEdt!!.text.toString()

        Log.d("Mobilenumber", MobileNumber!!)
        if (MobileNumber != "" && Password != "") {
            CommonUtil.MobileNUmber = MobileNumber!!
            val jsonObject = JsonObject()
            jsonObject.addProperty(ApiRequestNames.Req_mobileNumber, MobileNumber)
            jsonObject.addProperty(ApiRequestNames.Req_password, Password)
            authViewModel!!.login(jsonObject, this@LoginRewamp)
        } else {
            CommonUtil.ApiAlert(this@LoginRewamp, "Enter your password")
        }
    }

    private fun imgpasswordlockClick() {

        val editText = binding.passwordEdt
        val imageView = binding.imgPasswordopen

        if (editText.transformationMethod is PasswordTransformationMethod) {

            // SHOW PASSWORD
            editText.transformationMethod = null
            imageView.setImageResource(R.drawable.ic_eye_open_2)

        } else {

            // HIDE PASSWORD
            editText.transformationMethod =
                PasswordTransformationMethod.getInstance()
            imageView.setImageResource(R.drawable.ic_eye_off_2)
        }

        editText.setSelection(editText.text.length)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun GetOtp() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_mobileNumber, MobileNumber)
        appViewModel!!.GetOtp(jsonObject, this)
        Log.d("AdForCollege:", jsonObject.toString())
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
        ).isAppearanceLightStatusBars = false

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

        window.statusBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )

            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )

            window.statusBarColor = Color.TRANSPARENT

            window.navigationBarColor =
                resources.getColor(R.color.clr_auth_gray, theme)
        }
    }
}