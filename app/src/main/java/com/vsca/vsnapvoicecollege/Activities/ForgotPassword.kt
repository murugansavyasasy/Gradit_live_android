package com.vsca.vsnapvoicecollege.Activities

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityForgotPasswordBinding

class ForgotPassword : AppCompatActivity() {

    var lblcontent: TextView? = null


    var mobilenumber: String? = null
    var appViewModel: App? = null
    var status: Int = 0
    var message: String? = null
    private lateinit var binding: ActivityForgotPasswordBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        appViewModel!!.GetOtpNew!!.observe(this) { response ->
            if (response != null) {
                status = response.Status
                message = response.Message
                if (status == 1) {
                    CommonUtil.ivrnumbers = response.data.get(0).ivrnumbers
                    Log.d("ivrNumbers", CommonUtil.ivrnumbers.toString())
                    lblcontent!!.text = message

                } else {
                    CommonUtil.ApiAlert(this, message)
                }

            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }


        binding.txtNext!!.setOnClickListener {
            GetOtp()
        }
    }
    fun GetOtp() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_mobileNumber, mobilenumber)
        appViewModel!!.GetOtp(jsonObject, this)
        Log.d("AdForCollege:", jsonObject.toString())

    }
}