package com.vsca.vsnapvoicecollege.Activities

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App

class ForgotPassword : AppCompatActivity() {


    @JvmField
    @BindView(R.id.edMobilenumber)
    var edMobilenumber: EditText? = null

    @JvmField
    @BindView(R.id.txt_next)
    var txt_next: TextView? = null

    var lblclose: TextView? = null
    var lblcontent: TextView? = null
    var Btndial: Button? = null
    var btnDialSecond: Button? = null
    var btnDialThird: Button? = null

    var mobilenumber: String? = null
    var appViewModel: App? = null
    var status: Int = 0
    var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        ButterKnife.bind(this)
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


        txt_next!!.setOnClickListener {
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