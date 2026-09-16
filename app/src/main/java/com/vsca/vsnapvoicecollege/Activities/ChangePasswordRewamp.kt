package com.vsca.vsnapvoicecollege.Activities


import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
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
import com.vsca.vsnapvoicecollege.Activities.BaseActivity.Companion.appviewModelbase
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ChangePasswordRewampBinding

class ChangePasswordRewamp : AppCompatActivity() {

    var OldPassword: String? = null
    var NewPassword: String? = null
    var appViewModel: App? = null
    private var oldPasswordVisible = false
    private var newPasswordVisible = false
    private var confirmPasswordVisible = false
    private lateinit var binding: ChangePasswordRewampBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChangePasswordRewampBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isToolBarPrimaryTheme1(
            mainViewId = R.id.main,
            statusBarBgView = binding.statusBarBackground
        )

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()


        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        updateSubmitButtonState()

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateSubmitButtonState()
            }
        }

        binding.oldPasswordEdt.addTextChangedListener(watcher)
        binding.newPasswordEdt.addTextChangedListener(watcher)
        binding.confpasswordEdt.addTextChangedListener(watcher)

        binding.imgOldPasswordopen.setOnClickListener {
            oldPasswordVisible = passwordHideandShow(
                binding.oldPasswordEdt,
                binding.imgOldPasswordopen,
                oldPasswordVisible
            )
        }

        binding.imgNewPasswordopen.setOnClickListener {
            newPasswordVisible = passwordHideandShow(
                binding.newPasswordEdt,
                binding.imgNewPasswordopen,
                newPasswordVisible
            )
        }

        binding.imgConfpasswordopen.setOnClickListener {
            confirmPasswordVisible = passwordHideandShow(
                binding.confpasswordEdt,
                binding.imgConfpasswordopen,
                confirmPasswordVisible
            )
        }

        appviewModelbase!!.ChangePasswordLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    val mobilenumber=SharedPreference.getSH_MobileNumber(this)
                    if (!mobilenumber.isNullOrEmpty() && !NewPassword.isNullOrEmpty()) {
                        SharedPreference.putLoginDetails(
                            this@ChangePasswordRewamp,
                            mobilenumber,
                            NewPassword
                        )
                    }
                    ApiAlertOk(this, message, true)
                } else {
                    ApiAlertOk(this, message, false)
                }
            } else {
                ApiAlertOk(this, CommonUtil.Something_went_wrong, false)

            }
        }


        binding.txtNext!!.setOnClickListener {
            OldPassword = binding.oldPasswordEdt.text.toString()
            NewPassword = binding.newPasswordEdt.text.toString()
            val confirmpassword = binding.confpasswordEdt.text.toString()

            if (OldPassword!!.isEmpty()) {
                CommonUtil.ApiAlert(this, getString(R.string.lbl_enter_oldpassword))
            } else if (NewPassword!!.isEmpty()) {
                CommonUtil.ApiAlert(this, getString(R.string.lbl_enter_new_password))
            } else if (confirmpassword.isEmpty()) {
                CommonUtil.ApiAlert(this, getString(R.string.lbl_confim_password))
            } else if (OldPassword == NewPassword) {
                CommonUtil.ApiAlert(this, getString(R.string.lbl_similar_password))
            } else if (NewPassword == confirmpassword) {
                ChangePasswordRequest(this)
            } else {
                CommonUtil.ApiAlert(this, getString(R.string.lbl_pswrd_not_match))
            }
        }
    }

    private fun updateSubmitButtonState() {
        val oldPassword = binding.oldPasswordEdt?.text.toString().trim()
        val newPassword = binding.newPasswordEdt?.text.toString().trim()
        val confirmPassword = binding.confpasswordEdt?.text.toString().trim()

        val isValid = oldPassword.isNotEmpty() &&
                newPassword.isNotEmpty() &&
                confirmPassword.isNotEmpty()
        // add newPassword.length >= 6 (or your min length rule) here too, if applicable

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

    private fun passwordHideandShow(
        editText: EditText,
        imageView: ImageView,
        isVisible: Boolean
    ): Boolean {

        val newVisibility = !isVisible

        if (newVisibility) {
            editText.transformationMethod =
                HideReturnsTransformationMethod.getInstance()

            imageView.setImageResource(
                R.drawable.ic_eye_open_2
            )
        } else {
            editText.transformationMethod =
                PasswordTransformationMethod.getInstance()

            imageView.setImageResource(
                R.drawable.ic_eye_off_2
            )
        }

        editText.setSelection(editText.text.length)

        return newVisibility
    }

    fun ApiAlertOk(activity: Activity?, msg: String?, value: Boolean) {
        if (activity != null) {
            val dlg = AlertDialog.Builder(activity)
            dlg.setTitle(CommonUtil.Info)
            dlg.setMessage(msg)
            dlg.setPositiveButton(CommonUtil.OK) { dialog, which ->

                if (value) {

                    val i = Intent(activity, LoginRewamp::class.java)
                    startActivity(i)
                    finishAffinity()
                }
            }
            dlg.setCancelable(false)
            dlg.create()
            dlg.show()
        }
    }

    fun ChangePasswordRequest(activity: Activity?) {
        val mobilenumber = SharedPreference.getSH_MobileNumber(activity!!)
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_mobileNumber, mobilenumber)
        jsonObject.addProperty(ApiRequestNames.Req_oldpassword, OldPassword)
        jsonObject.addProperty(ApiRequestNames.Req_newpassword, NewPassword)
        appviewModelbase!!.getChangePassword(jsonObject, activity)
        Log.d("ChangePasswordRequest", jsonObject.toString())
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