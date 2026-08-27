package com.vsca.vsnapvoicecollege.Activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App

import com.vsca.vsnapvoicecollege.databinding.OtpRewampBinding
import android.os.CountDownTimer
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.vsca.vsnapvoicecollege.Utils.SharedPreference

class OtpRewamp : AppCompatActivity() {

    var appViewModel: App? = null
    var output = ""
    var Allow = true
    var opt_1 = ""
    var opt_2 = ""
    var opt_3 = ""
    var opt_4 = ""

    private var resendCountDownTimer: CountDownTimer? = null
    private var resendEndTime: Long = 0L

    private val RESEND_DURATION = 30_000L
    private lateinit var binding: OtpRewampBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = OtpRewampBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        if (CommonUtil.OptMessege != "") {
            binding.txtOtpNoRecievedMsg.text=CommonUtil.OptMessege
        }

        startResendTimer()

        binding.llHelplineNumbers.removeAllViews()

        CommonUtil.ivrnumbers.forEach { number ->

            val textView = TextView(this@OtpRewamp).apply {
                text = number
                textSize = 16f
                setPadding(0, 8, 0, 8)

                setTextColor(
                    ContextCompat.getColor(
                        this@OtpRewamp,
                        R.color.black
                    )
                )

                setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_call_icon_black,
                    0,
                    0,
                    0
                )

                compoundDrawablePadding = 12

                setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$number")
                    }
                    startActivity(intent)
                }
            }

            binding.llHelplineNumbers.addView(textView)
        }

        var countryDetails = SharedPreference.getCountryDetails(this)

        val mobileNumber = if (CommonUtil.MobileNUmber.isNotEmpty()) {
            CommonUtil.MobileNUmber
        } else {
            SharedPreference.getSH_MobileNumber(this)
        }

        val maskedNumber = mobileNumber?.let {
            if (it.length > 3) {
                "*".repeat(it.length - 3) + it.takeLast(3)
            } else {
                it
            }
        }
        binding.txtNumberlable!!.text = "We have sent a 4-digit verification code to + ${countryDetails.countyCode} ${maskedNumber}"




        val otpFilter = arrayOf(
            InputFilter.LengthFilter(1)
        )

        binding.txtOtp1.filters = otpFilter
        binding.txtOtp2.filters = otpFilter
        binding.txtOtp3.filters = otpFilter
        binding.txtOtp4.filters = otpFilter

        binding.txtOtp1.inputType = InputType.TYPE_CLASS_NUMBER
        binding.txtOtp2.inputType = InputType.TYPE_CLASS_NUMBER
        binding.txtOtp3.inputType = InputType.TYPE_CLASS_NUMBER
        binding.txtOtp4.inputType = InputType.TYPE_CLASS_NUMBER


        if (Allow) {

            binding.txtOtp1.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (!s.isNullOrEmpty()) {
                        opt_1 = s.toString()
                        binding.txtOtp2.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })


            binding.txtOtp2.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (!s.isNullOrEmpty()) {
                        opt_2 = s.toString()
                        binding.txtOtp3.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()) {
                        binding.txtOtp1.requestFocus()
                    }
                }
            })


            binding.txtOtp3.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (!s.isNullOrEmpty()) {
                        opt_3 = s.toString()
                        binding.txtOtp4.requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()) {
                        binding.txtOtp2.requestFocus()
                    }
                }
            })

            binding.txtOtp4.addTextChangedListener(object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    if (!s.isNullOrEmpty()) {
                        opt_4 = s.toString()
                        if (
                            binding.txtOtp1.text.length == 1 &&
                            binding.txtOtp2.text.length == 1 &&
                            binding.txtOtp3.text.length == 1 &&
                            binding.txtOtp4.text.length == 1
                        ) {
                            val imm = getSystemService(INPUT_METHOD_SERVICE)
                                    as InputMethodManager

                            imm.hideSoftInputFromWindow(
                                binding.txtOtp4.windowToken,
                                0
                            )
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()) {
                        binding.txtOtp3.requestFocus()
                    }
                }
            })


        }

        binding.btnVerifyContinue.setOnClickListener {

            val otp1 = binding.txtOtp1.text.toString().trim()
            val otp2 = binding.txtOtp2.text.toString().trim()
            val otp3 = binding.txtOtp3.text.toString().trim()
            val otp4 = binding.txtOtp4.text.toString().trim()

            if (otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty() || otp4.isEmpty()) {
                Toast.makeText(this, "Please enter the complete OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            output = otp1 + otp2 + otp3 + otp4

            VerifiedOtp()
        }

        appViewModel!!.VerifyOtp!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    Handler().postDelayed(Runnable {
                        intent = Intent(this@OtpRewamp, Create_Password::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }, 1000)

                } else {
                    CommonUtil.ApiAlert(this, message)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        binding.lblResend.setOnClickListener {

            GetOtp()

            startResendTimer()
        }

        appViewModel!!.GetOtpNew!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                } else {
                    CommonUtil.ApiAlert(this, message)
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }
    }

    private fun startResendTimer() {

        // Cancel previous timer if any
        resendCountDownTimer?.cancel()

        // Create a new expiry time only when starting a NEW 30-second timer
        resendEndTime = System.currentTimeMillis() + RESEND_DURATION

        binding.lblResendCode.visibility = View.VISIBLE
        binding.lblResend.visibility = View.GONE

        updateResendTimer()
    }

    override fun onResume() {
        super.onResume()

        if (resendEndTime > 0) {
            updateResendTimer()
        }
    }

    override fun onPause() {
        super.onPause()

        resendCountDownTimer?.cancel()
        resendCountDownTimer = null
    }

    override fun onDestroy() {
        resendCountDownTimer?.cancel()
        resendCountDownTimer = null
        super.onDestroy()
    }

    private fun updateResendTimer() {

        val remainingTime = resendEndTime - System.currentTimeMillis()

        if (remainingTime <= 0) {
            showResendButton()
            return
        }

        resendCountDownTimer = object : CountDownTimer(remainingTime, 1000) {

            override fun onTick(millisUntilFinished: Long) {

                val seconds = millisUntilFinished / 1000

                binding.lblResendCode.text =
                    String.format("Resend OTP in 00:%02d", seconds)
            }

            override fun onFinish() {
                showResendButton()
            }

        }.start()
    }

    private fun showResendButton() {

        resendCountDownTimer?.cancel()
        resendCountDownTimer = null

        binding.lblResendCode.visibility = View.GONE
        binding.lblResend.visibility = View.VISIBLE
    }

    fun GetOtp() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            ApiRequestNames.Req_mobileNumber,
            CommonUtil.MobileNUmber
        )
        appViewModel!!.GetOtp(jsonObject, this)
        Log.d("GetOtp:", jsonObject.toString())
    }

    fun VerifiedOtp() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_mobileNumber, CommonUtil.MobileNUmber)
        jsonObject.addProperty(ApiRequestNames.Req_otp, output)
        appViewModel!!.OtpVerified(jsonObject, this)
        Log.d("OtpVerified:", jsonObject.toString())
    }

}