package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Model.ValidateMobileNumberResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.ViewModel.Auth

class MobileNumber : AppCompatActivity() {

    private var mobileNumber: String? = null
    private var authViewModel: Auth? = null
    private var validateMobileNumberResponse: List<ValidateMobileNumberResponse> = ArrayList()
    var appViewModel: App? = null

    @JvmField
    @BindView(R.id.phone_number_edt)
    var phoneNumberEdt: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_number)
        ButterKnife.bind(this)
        authViewModel = ViewModelProvider(this).get(Auth::class.java)
        authViewModel!!.init()
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        authViewModel!!.Mobilenumber!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    validateMobileNumberResponse = response.data
                    val is_redirect_otp_screen =
                        validateMobileNumberResponse[0].is_redirect_otp_screen

                    if (is_redirect_otp_screen == 0) {
                        val i = Intent(this@MobileNumber, Login::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.putExtra("MobileNumber", mobileNumber)
                        startActivity(i)
                        finishAffinity()
                    } else {
                        CommonUtil.OptMessege = validateMobileNumberResponse[0].resultmessage
                        CommonUtil.ivrnumbers.add(validateMobileNumberResponse[0].ivrnumbers[0])
                        val i = Intent(this@MobileNumber, Otp::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)
                        finishAffinity()
                    }
                } else {
                    CommonUtil.ApiAlert(this@MobileNumber, message)
                }
            } else {
                CommonUtil.ApiAlert(this@MobileNumber, CommonUtil.No_Data_Found)
            }
        }

        phoneNumberEdt!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    Log.d("Length",s.toString())
                    if (s.startsWith("+")) {
                        when (s.length) {

                            12 -> {
                                val isMobileNumber: String = s.drop(2).dropLast(0).toString()
                                phoneNumberEdt!!.setText(isMobileNumber)
                            }

                            13 -> {
                                val isMobileNumber: String = s.drop(3).dropLast(0).toString()
                                phoneNumberEdt!!.setText(isMobileNumber)
                            }

                            14 -> {
                                val isMobileNumber: String = s.drop(4).dropLast(0).toString()
                                phoneNumberEdt!!.setText(isMobileNumber)
                            }

                            else -> {
                                phoneNumberEdt!!.setText(s)
                            }
                        }
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 10) {
                    phoneNumberEdt!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))
                }
                if (s.isEmpty()) {
                    phoneNumberEdt!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15))
                }
            }
        })
    }

    @OnClick(R.id.txt_next)
    fun txt_next(view: View?) {
        mobileNumber = phoneNumberEdt!!.text.toString()
        Log.d("mobileNumber", mobileNumber!!)
        if (mobileNumber != "") {
            CommonUtil.MobileNUmber = mobileNumber!!
            val jsonObject = JsonObject()
            jsonObject.addProperty(ApiRequestNames.Req_mobile_number, mobileNumber)
            authViewModel!!.VerifityMobile(jsonObject, this@MobileNumber)
        } else {
            CommonUtil.ApiAlert(this@MobileNumber, CommonUtil.Enter_mobileNumber)
        }
    }
}
