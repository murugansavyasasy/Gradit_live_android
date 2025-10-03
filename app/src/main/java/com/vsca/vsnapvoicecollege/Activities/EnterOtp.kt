package com.vsca.vsnapvoicecollege.Activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityEnterOtpBinding

class EnterOtp : AppCompatActivity() {
    var appViewModel: App? = null
    var mobilenumber: String? = null
    var Otp: String? = null
    private lateinit var binding: ActivityEnterOtpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        appViewModel!!.VerifyOtp!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    val dlg = AlertDialog.Builder(this)
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK) { dialog, which ->

                        val intents = Intent(this, Create_Password::class.java)
                        intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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

        binding.lblResendCode!!.setOnClickListener {
            GetOtp()
        }

        binding.btnContinue!!.setOnClickListener {

            mobilenumber = CommonUtil.isForgotMobileNumber
            Otp = binding.edPassword!!.text.toString()

            if (mobilenumber.equals("")) {
                CommonUtil.ApiAlert(this, CommonUtil.Enter_mobileNumber)
            } else {
                if (Otp.equals("")) {
                    CommonUtil.ApiAlert(this, CommonUtil.Enter_Otp)
                } else {
                    VerifiedOtp()
                }
            }
        }
    }

    fun VerifiedOtp() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_mobileNumber, mobilenumber)
        jsonObject.addProperty(ApiRequestNames.Req_otp, Otp)
        appViewModel!!.OtpVerified(jsonObject, this)
        Log.d("OtpVerified:", jsonObject.toString())
    }

    fun GetOtp() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(
            ApiRequestNames.Req_mobileNumber,
            CommonUtil.forgetpassword_Mobilenumber
        )
        appViewModel!!.GetOtp(jsonObject, this)
        Log.d("GetOtp:", jsonObject.toString())
    }
}