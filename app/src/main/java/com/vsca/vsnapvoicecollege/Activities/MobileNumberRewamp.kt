package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Model.ValidateMobileNumberResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.ViewModel.Auth
import com.vsca.vsnapvoicecollege.databinding.MobileNumberRewampBinding

class MobileNumberRewamp : AppCompatActivity() {

    private var mobileNumber: String? = null
    private var authViewModel: Auth? = null
    private var validateMobileNumberResponse: List<ValidateMobileNumberResponse> = ArrayList()
    var appViewModel: App? = null

    private lateinit var binding: MobileNumberRewampBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mobile_number_rewamp)
        binding = MobileNumberRewampBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authViewModel = ViewModelProvider(this).get(Auth::class.java)
        authViewModel!!.init()
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        var countryDetails = SharedPreference.getCountryDetails(this)
        binding.tvCountryCode.text="+${countryDetails.countyCode}"

        val countryCode = countryDetails.countyCode
            ?.filter { it.isDigit() }
            ?: ""

        val mobileLength = countryDetails.mobilenumberlen
            ?.toIntOrNull()
            ?: 10

        var isUpdating = false

        binding.phoneNumberEdt.addTextChangedListener(object : TextWatcher {

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
            }

            override fun afterTextChanged(s: Editable?) {

                if (isUpdating || s == null) return

                var number = s.toString()

                // Remove +, spaces, -, brackets, etc.
                number = number.filter { it.isDigit() }

                // Remove country code ONLY when the complete
                // international number is pasted.
                if (
                    countryCode.isNotEmpty() &&
                    number.length == countryCode.length + mobileLength &&
                    number.startsWith(countryCode)
                ) {
                    number = number.removePrefix(countryCode)
                }

                // Limit to configured mobile number length
                if (number.length > mobileLength) {
                    number = number.take(mobileLength)
                }

                if (s.toString() != number) {

                    isUpdating = true

                    binding.phoneNumberEdt.setText(number)
                    binding.phoneNumberEdt.setSelection(number.length)

                    isUpdating = false
                }
            }
        })


        binding.txtNext.setOnClickListener { txt_next() }
        binding.btnBack.setOnClickListener { onBackPressed() }

        authViewModel!!.Mobilenumber!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    validateMobileNumberResponse = response.data
                    val is_redirect_otp_screen =
                        validateMobileNumberResponse[0].is_redirect_otp_screen

                    if (is_redirect_otp_screen == 0) {
                        val i = Intent(this@MobileNumberRewamp, LoginRewamp::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.putExtra("MobileNumber", mobileNumber)
                        startActivity(i)
                        finishAffinity()
                    } else {
                        CommonUtil.OptMessege = validateMobileNumberResponse[0].resultmessage
                        CommonUtil.ivrnumbers = ArrayList(validateMobileNumberResponse[0].ivrnumbers)
                        val i = Intent(this@MobileNumberRewamp, OtpRewamp::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)
                        finishAffinity()
                    }
                } else {
                    CommonUtil.ApiAlert(this@MobileNumberRewamp, message)
                }
            } else {
                CommonUtil.ApiAlert(this@MobileNumberRewamp, CommonUtil.No_Data_Found)
            }
        }


    }

    fun txt_next() {
        mobileNumber = binding.phoneNumberEdt!!.text.toString()
        Log.d("mobileNumber", mobileNumber!!)
        if (mobileNumber != "") {
            CommonUtil.MobileNUmber = mobileNumber!!
            val jsonObject = JsonObject()
            jsonObject.addProperty(ApiRequestNames.Req_mobile_number, mobileNumber)
            authViewModel!!.VerifityMobile(jsonObject, this@MobileNumberRewamp)
        } else {
            CommonUtil.ApiAlert(this@MobileNumberRewamp, CommonUtil.Enter_mobileNumber)
        }
    }
}
