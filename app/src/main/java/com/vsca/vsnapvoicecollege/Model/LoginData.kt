package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginData {

    @SerializedName("login_data")
    @Expose
    var login_data: List<LoginDetails>? = null

    @SerializedName("ivrnumbers")
    @Expose
    var ivrnumbers: List<String>? = null

    @SerializedName("otpRequired")
    @Expose
    var otpRequired: Boolean = false

}