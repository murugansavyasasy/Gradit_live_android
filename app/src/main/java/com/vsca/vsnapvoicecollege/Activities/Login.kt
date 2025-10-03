package com.vsca.vsnapvoicecollege.Activities


import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.ViewModel.Auth
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    var lblcontent: TextView? = null
    var MobileNumber: String? = null
    var Password: String? = null
    var authViewModel: Auth? = null
    var LoginData: List<LoginDetails> = ArrayList()
    private var passwordvisible = true
    var appViewModel: App? = null
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authViewModel = ViewModelProvider(this)[Auth::class.java]
        authViewModel!!.init()
        CommonUtil.MenuListDashboard.clear()
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        binding.txtForgetpassword!!.setOnClickListener {
            GetOtp()
        }
        binding.imgPasswordopen.setOnClickListener { imgpasswordlockClick() }

        binding.txtNext.setOnClickListener {
            LoginbtnClick()
        }

        MobileNumber = intent.getStringExtra("MobileNumber")
        binding.phoneNumberEdt!!.text = MobileNumber

        authViewModel!!.loginResposneLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    LoginData = response.data!!
                    if (LoginData.size != 0) {
                        CommonUtil.UserDataList = response.data as ArrayList<LoginDetails>?
                        SharedPreference.putLoginDetails(this@Login, MobileNumber, Password)
                        SetLoginData(LoginData)

                        Log.d("LoginDataSize", LoginData.size.toString())
                        if (LoginData.size > 1) {
                            val i = Intent(this@Login, LoginRoles::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                            finishAffinity()
                        } else {
                            val i = Intent(this@Login, DashBoard::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                            finishAffinity()
                        }
                    } else {
                        CommonUtil.ApiAlert(this@Login, message)
                    }
                } else {
                    CommonUtil.ApiAlert(this@Login, message)
                }
            } else {
                CommonUtil.ApiAlert(this@Login, CommonUtil.Something_went_wrong)
            }
        }

        appViewModel!!.GetOtpNew!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    CommonUtil.ivrnumbers = response.data.get(0).ivrnumbers
                    Log.d("ivrNumbers", CommonUtil.ivrnumbers.toString())
                    CommonUtil.OptMessege = response.Message

                    val intents = Intent(this, Otp::class.java)
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
            authViewModel!!.login(jsonObject, this@Login)
        } else {
            CommonUtil.ApiAlert(this@Login, "Enter your password")
        }
    }

    fun imgpasswordlockClick() {
        if (passwordvisible) {
            binding.passwordEdt!!.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.imgPasswordopen!!.setImageResource(R.drawable.ic_lock)
            passwordvisible = false
        } else {
            binding.passwordEdt!!.transformationMethod = null
            passwordvisible = true
            binding.passwordEdt!!.setSelection(binding.passwordEdt!!.text.length)
            binding.imgPasswordopen!!.setImageResource(R.drawable.ic_lock_open)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
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
}