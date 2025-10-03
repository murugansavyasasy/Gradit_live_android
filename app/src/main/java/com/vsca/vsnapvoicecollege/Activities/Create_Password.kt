package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityCreatePasswordBinding

class Create_Password : AppCompatActivity() {

    var Newpassword: String? = null
    var ConfirmNewpassword: String? = null
    var appViewModel: App? = null
    private var passwordvisible = true
    private lateinit var binding: ActivityCreatePasswordBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        binding.linearMobileNoLayout.setOnClickListener { imgpasswordlockClick() }
        binding.linearPasswordNoLayout.setOnClickListener { imgpasswordconfirmlockClick() }

        appViewModel!!.CrearePassword!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    val dlg = AlertDialog.Builder(this)
                    dlg.setTitle(CommonUtil.Info)
                    dlg.setMessage(message)
                    dlg.setPositiveButton(CommonUtil.OK) { dialog, which ->

                        val intents = Intent(this, Login::class.java)
                        intents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intents.putExtra("MobileNumber", CommonUtil.MobileNUmber)
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

        binding.txtNext!!.setOnClickListener {

            Newpassword = binding.phoneNumberEdt!!.text.toString()
            ConfirmNewpassword = binding.passwordEdt!!.text.toString()
            if (Newpassword.equals("")) {
                CommonUtil.ApiAlert(this, CommonUtil.Enter_the_Newpassword)
            } else {
                if (ConfirmNewpassword.equals("")) {
                    CommonUtil.ApiAlert(this, CommonUtil.Enter_the_Confirmpassword)

                } else {
                    if (Newpassword != ConfirmNewpassword) {

                        CommonUtil.ApiAlert(this, CommonUtil.Password_Mismatching)

                    } else {
                        CreateNewpassword()
                    }
                }
            }
        }
    }

    fun imgpasswordlockClick() {
        if (passwordvisible) {
            binding.phoneNumberEdt!!.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.imgPasswordopen!!.setImageResource(R.drawable.ic_lock)
            passwordvisible = false
        } else {
            binding.phoneNumberEdt!!.transformationMethod = null
            passwordvisible = true
            binding.phoneNumberEdt!!.setSelection(binding.phoneNumberEdt!!.text.length)
            binding.imgPasswordopen!!.setImageResource(R.drawable.ic_lock_open)
        }
    }

    fun imgpasswordconfirmlockClick() {
        if (passwordvisible) {
            binding.passwordEdt!!.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.imgPasswordopen!!!!.setImageResource(R.drawable.ic_lock)
            passwordvisible = false
        } else {
            binding.passwordEdt!!.transformationMethod = null
            passwordvisible = true
            binding.passwordEdt!!.setSelection(binding.passwordEdt!!.text.length)
            binding.imgPasswordopen!!!!.setImageResource(R.drawable.ic_lock_open)
        }
    }


    fun CreateNewpassword() {

        val jsonObject = JsonObject()
        jsonObject.addProperty(
            ApiRequestNames.Req_mobileNumber,
            CommonUtil.MobileNUmber
        )
        jsonObject.addProperty(ApiRequestNames.Req_newpassword, ConfirmNewpassword)
        appViewModel!!.CreatepasswordNew(jsonObject, this)
        Log.d("CreatepasswordNew:", jsonObject.toString())

    }
}