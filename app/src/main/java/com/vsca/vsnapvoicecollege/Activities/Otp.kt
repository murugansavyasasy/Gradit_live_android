package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App

class Otp : AppCompatActivity() {

    @JvmField
    @BindView(R.id.linear_layout)
    var linear_layout: LinearLayout? = null

    @JvmField
    @BindView(R.id.lnr_dialhelpline)
    var lnr_dialhelpline: LinearLayout? = null

    @JvmField
    @BindView(R.id.txt_otp_verification)
    var txt_otp_verification: TextView? = null

    @JvmField
    @BindView(R.id.txt_numberlable)
    var txt_numberlable: TextView? = null

    @JvmField
    @BindView(R.id.txt_helpline)
    var txt_helpline: TextView? = null

    @JvmField
    @BindView(R.id.txt_otp_1)
    var txt_otp_1: TextView? = null

    @JvmField
    @BindView(R.id.txt_otp_2)
    var txt_otp_2: TextView? = null

    @JvmField
    @BindView(R.id.txt_otp_3)
    var txt_otp_3: TextView? = null

    @JvmField
    @BindView(R.id.txt_otp_4)
    var txt_otp_4: TextView? = null

    @JvmField
    @BindView(R.id.lblResendCode)
    var lblResendCode: TextView? = null

    private var REQ_USER_CONSENT = 200
    var appViewModel: App? = null
    var output = ""
    var Allow = true
    var opt_1 = ""
    var opt_2 = ""
    var opt_3 = ""
    var opt_4 = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        ButterKnife.bind(this)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        if (CommonUtil.OptMessege != "") {
            txt_otp_verification!!.text = CommonUtil.OptMessege
        }

        if (CommonUtil.ivrnumbers.isNotEmpty()) {
            txt_helpline!!.text = CommonUtil.ivrnumbers[0]
        }

        lnr_dialhelpline!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + CommonUtil.ivrnumbers[0])
            startActivity(intent)
        }
        val mobileNumber = CommonUtil.MobileNUmber.drop(7)
        txt_numberlable!!.text =
            "We have sent a 4-digit verification code to" + "  " + "+91*******" + mobileNumber



        if (Allow) {
            txt_otp_1!!.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // TODO Auto-generated method stub
                    if (txt_otp_1!!.text.toString().isNotEmpty()) //size as per your requirement
                    {
                        opt_1 = txt_otp_1!!.text.toString()
                        txt_otp_2!!.requestFocus()
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

            txt_otp_2!!.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // TODO Auto-generated method stub
                    if (txt_otp_2!!.text.toString().isNotEmpty()) //size as per your requirement
                    {
                        opt_2 = txt_otp_2!!.text.toString()
                        txt_otp_3!!.requestFocus()
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

            txt_otp_3!!.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // TODO Auto-generated method stub
                    if (txt_otp_3!!.text.toString().isNotEmpty()) //size as per your requirement
                    {
                        opt_3 = txt_otp_3!!.text.toString()
                        txt_otp_4!!.requestFocus()
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

            txt_otp_4!!.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // TODO Auto-generated method stub
                    if (txt_otp_4!!.text.toString().isNotEmpty()) //size as per your requirement
                    {
                        opt_4 = txt_otp_4!!.text.toString()
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

        lblResendCode!!.setOnClickListener {
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