package com.vsca.vsnapvoicecollege.Activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App

class EnterOtp : AppCompatActivity() {

    var appViewModel: App? = null

    @JvmField
    @BindView(R.id.btnContinue)
    var btnContinue: Button? = null

    @JvmField
    @BindView(R.id.edMobilenumber)
    var edMobilenumber: EditText? = null

    @JvmField
    @BindView(R.id.edPassword)
    var edPassword: EditText? = null

    @JvmField
    @BindView(R.id.lblResendCode)
    var lblResendCode: TextView? = null

    var mobilenumber: String? = null
    var Otp: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_otp)
        ButterKnife.bind(this)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

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

        lblResendCode!!.setOnClickListener {
            GetOtp()
        }

        btnContinue!!.setOnClickListener {

            mobilenumber = CommonUtil.isForgotMobileNumber
            Otp = edPassword!!.text.toString()

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