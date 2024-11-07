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
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App

class Create_Password : AppCompatActivity() {

    @JvmField
    @BindView(R.id.phone_number_edt)
    var phone_number_edt: EditText? = null

    @JvmField
    @BindView(R.id.password_edt)
    var password_edt: EditText? = null

    @JvmField
    @BindView(R.id.txt_next)
    var txt_next: TextView? = null

    @JvmField
    @BindView(R.id.img_confirmpassword)
    var img_confirmpassword: ImageView? = null

    @JvmField
    @BindView(R.id.img_passwordopen)
    var img_passwordopen: ImageView? = null


    var Newpassword: String? = null
    var ConfirmNewpassword: String? = null
    var appViewModel: App? = null
    private var passwordvisible = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_password)
        ButterKnife.bind(this)

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()


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

        txt_next!!.setOnClickListener {

            Newpassword = phone_number_edt!!.text.toString()
            ConfirmNewpassword = password_edt!!.text.toString()
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

    @OnClick(R.id.linear_mobile_no_layout)
    fun imgpasswordlockClick() {
        if (passwordvisible) {
            phone_number_edt!!.transformationMethod = PasswordTransformationMethod.getInstance()
            img_passwordopen!!.setImageResource(R.drawable.ic_lock)
            passwordvisible = false
        } else {
            phone_number_edt!!.transformationMethod = null
            passwordvisible = true
            phone_number_edt!!.setSelection(phone_number_edt!!.text.length)
            img_passwordopen!!.setImageResource(R.drawable.ic_lock_open)
        }
    }

    @OnClick(R.id.img_confirmpassword)
    fun imgpasswordconfirmlockClick() {
        if (passwordvisible) {
            password_edt!!.transformationMethod = PasswordTransformationMethod.getInstance()
            img_confirmpassword!!.setImageResource(R.drawable.ic_lock)
            passwordvisible = false
        } else {
            password_edt!!.transformationMethod = null
            passwordvisible = true
            password_edt!!.setSelection(password_edt!!.text.length)
            img_confirmpassword!!.setImageResource(R.drawable.ic_lock_open)
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