package com.vsca.vsnapvoicecollege.Activities

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
import android.view.View
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

//        if (CommonUtil.ivrnumbers.isNotEmpty()) {
//            binding.txtHelpline!!.text = CommonUtil.ivrnumbers[0]
//        }

//        binding.lnrDialhelpline!!.setOnClickListener {
//            val intent = Intent(Intent.ACTION_DIAL)
//            intent.data = Uri.parse("tel:" + CommonUtil.ivrnumbers[0])
//            startActivity(intent)
//        }
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




        if (Allow) {
            binding.txtOtp1!!.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // TODO Auto-generated method stub
                    if (binding.txtOtp1!!.text.toString().isNotEmpty()) //size as per your requirement
                    {
                        opt_1 = binding.txtOtp1!!.text.toString()
                        binding.txtOtp2!!.requestFocus()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {
                    // TODO Auto-generated method stub
                }

                override fun afterTextChanged(s: Editable) {
                    // TODO Auto-generated method stub
                }
            })

            binding.txtOtp2!!.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // TODO Auto-generated method stub
                    if (binding.txtOtp2!!.text.toString().isNotEmpty()) //size as per your requirement
                    {
                        opt_2 = binding.txtOtp2!!.text.toString()
                        binding.txtOtp3!!.requestFocus()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {
                    // TODO Auto-generated method stub
                }

                override fun afterTextChanged(s: Editable) {
                    // TODO Auto-generated method stub
                }
            })

            binding.txtOtp3!!.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // TODO Auto-generated method stub
                    if (binding.txtOtp3!!.text.toString().isNotEmpty()) //size as per your requirement
                    {
                        opt_3 = binding.txtOtp3!!.text.toString()
                        binding.txtOtp4!!.requestFocus()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {
                    // TODO Auto-generated method stub
                }

                override fun afterTextChanged(s: Editable) {
                    // TODO Auto-generated method stub
                }
            })

            binding.txtOtp4!!.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // TODO Auto-generated method stub
                    if (binding.txtOtp4!!.text.toString().isNotEmpty()) //size as per your requirement
                    {
                        opt_4 = binding.txtOtp4!!.text.toString()
                        output = opt_1 + opt_2 + opt_3 + opt_4

                        if (Allow) {
                            VerifiedOtp()
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int, after: Int
                ) {
                    // TODO Auto-generated method stub
                }

                override fun afterTextChanged(s: Editable) {
                    // TODO Auto-generated method stub
                }
            })
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