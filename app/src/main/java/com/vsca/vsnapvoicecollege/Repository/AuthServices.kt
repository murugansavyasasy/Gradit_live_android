package com.vsca.vsnapvoicecollege.Repository

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Model.CountryDetailsResponse
import com.vsca.vsnapvoicecollege.Model.LoginResponse
import com.vsca.vsnapvoicecollege.Model.ValidateMobileNumber
import com.vsca.vsnapvoicecollege.Model.VersionCheckResposne
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class AuthServices {

    var client_auth: RestClient
    var countryDetailsMutableLiveData: MutableLiveData<CountryDetailsResponse?>
    var versionCheckResposneMutableLiveData: MutableLiveData<VersionCheckResposne?>
    var LoginResposneMutableLiveData: MutableLiveData<LoginResponse?>
    var MobileNumber: MutableLiveData<ValidateMobileNumber?>

    fun GetCountryList(appid: Int, activity: Activity?) {
//        var progressDialog = CustomLoading.createProgressDialog(activity)
//        progressDialog!!.show()
        RestClient.Companion.apiInterfaces.Getcountrylist(appid)
            ?.enqueue(object : Callback<CountryDetailsResponse?> {
                override fun onResponse(
                    call: Call<CountryDetailsResponse?>, response: Response<CountryDetailsResponse?>
                ) {
                    //    progressDialog!!.dismiss()

                    Log.d(
                        "GetCountryList", response.code().toString() + " - " + response.toString()
                    )

                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val status = response.body()!!.status
                            Log.d("country_response", response.body().toString())
                            if (status == 1) {
                                countryDetailsMutableLiveData.postValue(response.body())
                            } else {
                                countryDetailsMutableLiveData.postValue(response.body())

                            }
                        }
                    } else if (response.code() == 400) {
                        // progressDialog.dismiss()
                        try {
//                            val jsonObject = JSONObject(response.errorBody()!!.string())
//                            val message = jsonObject.getString("message")
//                            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<CountryDetailsResponse?>, t: Throwable) {
                    //  progressDialog!!.dismiss()
                    countryDetailsMutableLiveData.postValue(null)
                    t.printStackTrace()
                }
            })
    }

    val countryLiveData: LiveData<CountryDetailsResponse?>
        get() = countryDetailsMutableLiveData

    fun GetVersionCheck(versionid: Int, Devicetype: String, activity: Activity?) {
//        val progressDialog = CustomLoading.createProgressDialog(activity)
//        progressDialog.show()
        RestClient.Companion.apiInterfaces.VersionCheck(versionid, Devicetype)
            ?.enqueue(object : Callback<VersionCheckResposne?> {
                override fun onResponse(
                    call: Call<VersionCheckResposne?>, response: Response<VersionCheckResposne?>
                ) {
                    //  progressDialog.dismiss()
                    Log.d(
                        "GetVersionCheck_Res",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val status = response.body()!!.status
                            if (status == 1) {
                                versionCheckResposneMutableLiveData.postValue(response.body())
                            } else {

                                versionCheckResposneMutableLiveData.postValue(response.body())

                            }
                        }
                    } else if (response.code() == 400) {
                        //  progressDialog.dismiss()
                        try {

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<VersionCheckResposne?>, t: Throwable) {
                    // progressDialog.dismiss()
                    versionCheckResposneMutableLiveData.postValue(null)
                    t.printStackTrace()
                }
            })
    }

    val versionCheckLiveData: LiveData<VersionCheckResposne?>
        get() = versionCheckResposneMutableLiveData

    fun Login(jsonObject: JsonObject?, activity: Activity?) {
        Log.d("LoginApi", "test")
//        var progressDialog = CustomLoading.createProgressDialog(activity)
//        progressDialog.show()
        RestClient.Companion.apiInterfaces.Login(jsonObject)
            ?.enqueue(object : Callback<LoginResponse?> {
                override fun onResponse(
                    call: Call<LoginResponse?>, response: Response<LoginResponse?>
                ) {
                    //    progressDialog!!.dismiss()
                    Log.d("GetLogin_Res", response.code().toString() + " - " + response.toString())
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            val status = response.body()!!.status
                            LoginResposneMutableLiveData.postValue(response.body())

                        }
                    } else if (response.code() == 400) {
                        //  progressDialog.dismiss()
                        try {
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                    //   progressDialog!!.dismiss()
                    LoginResposneMutableLiveData.postValue(null)
                    t.printStackTrace()
                }
            })
    }

    val loginResponseLiveData: LiveData<LoginResponse?>
        get() = LoginResposneMutableLiveData


    fun MobilenumberVerification(jsonObject: JsonObject?, activity: Activity?) {
        Log.d("LoginApi", "test")
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog.show()
        RestClient.apiInterfaces.ValidateMobileNumber(jsonObject)
            ?.enqueue(object : Callback<ValidateMobileNumber?> {
                override fun onResponse(
                    call: Call<ValidateMobileNumber?>, response: Response<ValidateMobileNumber?>
                ) {
                    progressDialog.dismiss()
                    Log.d("GetLogin_Res", response.code().toString() + " - " + response.toString())
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            val status = response.body()!!.Status
                            MobileNumber.postValue(response.body())

                        }
                    } else if (response.code() == 400) {
                        progressDialog.dismiss()
                        try {
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onFailure(call: Call<ValidateMobileNumber?>, t: Throwable) {
                    progressDialog.dismiss()
                    MobileNumber.postValue(null)
                    t.printStackTrace()
                }
            })
    }

    val VerificationMobilenumber: LiveData<ValidateMobileNumber?>
        get() = MobileNumber

    init {
        client_auth = RestClient()
        countryDetailsMutableLiveData = MutableLiveData()
        versionCheckResposneMutableLiveData = MutableLiveData()
        LoginResposneMutableLiveData = MutableLiveData()
        MobileNumber = MutableLiveData()
    }
}