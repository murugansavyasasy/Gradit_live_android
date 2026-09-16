package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.CreatePasswordRewampBinding

class CreatePasswordRewamp : AppCompatActivity() {

    var Newpassword: String? = null
    var ConfirmNewpassword: String? = null
    var appViewModel: App? = null
    private var newPasswordVisible = false
    private var confirmPasswordVisible = false
    private lateinit var binding: CreatePasswordRewampBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreatePasswordRewampBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        isToolBarPrimaryTheme1(
            mainViewId = R.id.main,
            statusBarBgView = binding.statusBarBackground
        )

        binding.btnBack.setOnClickListener { onBackPressed() }
        binding.imgNewPasswordopen.setOnClickListener { imgpasswordlockClick() }
        binding.imgConfpasswordopen.setOnClickListener { imgpasswordconfirmlockClick()
        }

//        This tell which label should be placed whether reset password or create password
//        Because if user is coming first it will come Create Password he already created
//        now user may forget the password so user may clicked Forget Password
        val authSource = intent.getStringExtra(CommonUtil.EXTRA_AUTH_SOURCE)
        Log.d("OTP","This password request came from"+authSource)

        when (authSource) {

            CommonUtil.AUTH_SOURCE_MOBILE -> {
                binding.tvTitle.text="Create Password"
                binding.tvSubtitle.text="Set a secure password to protect your account."
                binding.txtNext.text="Create Password"

            }

            CommonUtil.AUTH_SOURCE_LOGIN -> {
                binding.tvTitle.text="Reset Password"
                binding.tvSubtitle.text="Create a new password to securely access your account."
                binding.txtNext.text="Reset Password"
            }

            else -> {
                binding.tvTitle.text="New Password"
                binding.tvSubtitle.text="Create a new password to securely access your account."
                binding.txtNext.text="New Password"

            }
        }




        appViewModel!!.CrearePassword!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    val mobileNumber = CommonUtil.MobileNUmber
                        .takeIf { it.isNotEmpty() }
                        ?: SharedPreference.getSH_MobileNumber(this)
                        ?: ""

                    if (mobileNumber.isNotEmpty() && !ConfirmNewpassword.isNullOrEmpty()) {
                        SharedPreference.putLoginDetails(
                            this@CreatePasswordRewamp,
                            mobileNumber,
                            ConfirmNewpassword
                        )
                    }

                    val dlg = AlertDialog.Builder(this)
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK) { dialog, which ->

                        val intents = Intent(this, LoginRewamp::class.java)
                        intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intents.putExtra("MobileNumber", mobileNumber)
                        startActivity(intents)
                    }
                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()
                } else {
                    CommonUtil.ApiAlert(this, message)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        updateResetButtonState()

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateResetButtonState()
            }
        }
        binding.newPasswordEdt.addTextChangedListener(watcher)
        binding.confpasswordEdt.addTextChangedListener(watcher)


            binding.txtNext!!.setOnClickListener {

            Newpassword = binding.newPasswordEdt!!.text.toString()
            ConfirmNewpassword = binding.confpasswordEdt!!.text.toString()
            if (Newpassword.equals("")) {
                CommonUtil.CustomApiAlert(
                    this@CreatePasswordRewamp,
                    "Alert",
                    "Please enter new password",
                    "Ok"
                )
            } else {
                if (ConfirmNewpassword.equals("")) {
                    CommonUtil.CustomApiAlert(
                        this@CreatePasswordRewamp,
                        "Alert",
                        "Please enter confirm password",
                        "Ok"
                    )

                } else {
                    if (Newpassword != ConfirmNewpassword) {

                        CommonUtil.CustomApiAlert(
                            this@CreatePasswordRewamp,
                            "Alert",
                            "New password and confirm password does not match",
                            "Ok"
                        )
                    } else {
                        CreateNewpassword()
                    }
                }
            }
        }
    }

    fun imgpasswordlockClick() {

        if (newPasswordVisible) {
            // Hide New Password
            binding.newPasswordEdt.transformationMethod =
                PasswordTransformationMethod.getInstance()

            binding.imgNewPasswordopen.setImageResource(
                R.drawable.ic_eye_off_2
            )

            newPasswordVisible = false

        } else {
            // Show New Password
            binding.newPasswordEdt.transformationMethod = null

            binding.imgNewPasswordopen.setImageResource(
                R.drawable.ic_eye_open_2
            )

            newPasswordVisible = true
        }

        binding.newPasswordEdt.setSelection(
            binding.newPasswordEdt.text.length
        )
    }

    private fun updateResetButtonState() {
        val newPassword = binding.newPasswordEdt?.text.toString().trim()
        val confirmPassword = binding.confpasswordEdt?.text.toString().trim()

        val isValid = newPassword.isNotEmpty() &&
                confirmPassword.isNotEmpty()

        if (isValid) {
            binding.txtNext.isEnabled = true
            binding.txtNext.isClickable = true
            binding.txtNext.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7a5af8"))
            binding.txtNext.setTextColor(Color.WHITE)
        } else {
            binding.txtNext.isEnabled = false
            binding.txtNext.isClickable = false
            binding.txtNext.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#e7e7e8"))
            binding.txtNext.setTextColor(Color.parseColor("#c4c3c8"))
        }
    }

    fun imgpasswordconfirmlockClick() {

        if (confirmPasswordVisible) {
            // Hide Confirm Password
            binding.confpasswordEdt.transformationMethod =
                PasswordTransformationMethod.getInstance()

            binding.imgConfpasswordopen.setImageResource(
                R.drawable.ic_eye_off_2
            )

            confirmPasswordVisible = false

        } else {
            // Show Confirm Password
            binding.confpasswordEdt.transformationMethod = null

            binding.imgConfpasswordopen.setImageResource(
                R.drawable.ic_eye_open_2
            )

            confirmPasswordVisible = true
        }

        binding.confpasswordEdt.setSelection(
            binding.confpasswordEdt.text.length
        )
    }


    fun CreateNewpassword() {
        val mobileNumber = CommonUtil.MobileNUmber
            .takeIf { it.isNotEmpty() }
            ?: SharedPreference.getSH_MobileNumber(this)
            ?: ""

        val jsonObject = JsonObject()
        jsonObject.addProperty(
            ApiRequestNames.Req_mobileNumber,
            mobileNumber
        )
        jsonObject.addProperty(ApiRequestNames.Req_newpassword, ConfirmNewpassword)
        appViewModel!!.CreatepasswordNew(jsonObject, this)
        Log.d("CreatepasswordNew:", jsonObject.toString())

    }

//    fun isToolBarPrimaryTheme1(
//        mainViewId: Int,
//        statusBarBgView: View
//    ) {
//        enableEdgeToEdge()
//
//        val mainView = findViewById<View>(mainViewId)
//
//        // White status bar icons
//        WindowCompat.getInsetsController(
//            window,
//            window.decorView
//        ).isAppearanceLightStatusBars = false
//
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->
//
//            val systemBars = insets.getInsets(
//                WindowInsetsCompat.Type.systemBars()
//            )
//
//            statusBarBgView.updateLayoutParams {
//                height = systemBars.top
//            }
//
//            view.updatePadding(
//                left = systemBars.left,
//                right = systemBars.right,
//                bottom = systemBars.bottom
//            )
//
//            insets
//        }
//
//        window.statusBarColor = Color.TRANSPARENT
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//            window.addFlags(
//                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
//            )
//
//            window.clearFlags(
//                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//            )
//
//            window.statusBarColor = Color.TRANSPARENT
//
//            window.navigationBarColor =
//                resources.getColor(R.color.clr_auth_gray, theme)
//        }
//    }
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
    statusBarBgView.setBackgroundColor(Color.WHITE)

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
            resources.getColor(R.color.white, theme)
    }
}
}