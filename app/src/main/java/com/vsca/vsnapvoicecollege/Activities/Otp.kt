package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityOtpBinding

class Otp : AppCompatActivity() {

    var appViewModel: App? = null
    var output = ""
    var Allow = true
    var opt_1 = ""
    var opt_2 = ""
    var opt_3 = ""
    var opt_4 = ""
    private lateinit var binding: ActivityOtpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        if (CommonUtil.OptMessege != "") {
            binding.txtOtpVerification!!!!.text = CommonUtil.OptMessege
        }

        if (CommonUtil.ivrnumbers.isNotEmpty()) {
            binding.txtHelpline!!.text = CommonUtil.ivrnumbers[0]
        }

        binding.lnrDialhelpline!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + CommonUtil.ivrnumbers[0])
            startActivity(intent)
        }
        val mobileNumber = CommonUtil.MobileNUmber.drop(7)
        binding.txtNumberlable!!.text =
            "We have sent a 4-digit verification code to" + "  " + "+91*******" + mobileNumber



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
                        intent = Intent(this@Otp, Create_Password::class.java)
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

        binding.lblResendCode!!.setOnClickListener {
            GetOtp()
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