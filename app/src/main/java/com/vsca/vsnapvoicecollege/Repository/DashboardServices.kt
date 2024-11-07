package com.vsca.vsnapvoicecollege.Repository


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Model.DashBoardResponse
import com.vsca.vsnapvoicecollege.Model.GetNotificationsResponse
import com.vsca.vsnapvoicecollege.Model.Hallticket
import com.vsca.vsnapvoicecollege.Model.MenuResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardServices {
    var progressDialog: ProgressDialog? = null
    var client_auth: RestClient
    var DashboardResposneMutableLiveData: MutableLiveData<DashBoardResponse?>
    var UserMenuMutableLiveData: MutableLiveData<MenuResponse?>
    var GetNotificationMutableLiveData: MutableLiveData<GetNotificationsResponse?>

    fun DashBoard(jsonObject: JsonObject?, activity: Activity) {
        progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.Companion.apiInterfaces.Dashboard(jsonObject)
            ?.enqueue(object : Callback<DashBoardResponse?> {
                override fun onResponse(
                    call: Call<DashBoardResponse?>,
                    response: Response<DashBoardResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetDashBoardResponse",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val status = response.body()!!.status
                            if (status == 1) {
                                DashboardResposneMutableLiveData.postValue(response.body())
                            } else {
                                CommonUtil.ApiAlertFinish(activity, response.body()!!.message)
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        CommonUtil.ApiAlertFinish(
                            activity,
                            activity.getString(R.string.txt_no_record_found)
                        )
                    } else {
                        CommonUtil.ApiAlertFinish(
                            activity, activity.getString(R.string.txt_no_record_found)
                        )
                    }
                }

                override fun onFailure(call: Call<DashBoardResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    DashboardResposneMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val dashBoardLiveData: LiveData<DashBoardResponse?>
        get() = DashboardResposneMutableLiveData

    fun GetUsermenu(jsonObject: JsonObject?, activity: Activity) {

        RestClient.Companion.apiInterfaces.GetUsermenu(jsonObject)
            ?.enqueue(object : Callback<MenuResponse?> {
                override fun onResponse(
                    call: Call<MenuResponse?>,
                    response: Response<MenuResponse?>
                ) {
                    Log.d("MenuResponse", response.code().toString() + " - " + response.toString())
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val status = response.body()!!.status
                            if (status == 1) {
                                UserMenuMutableLiveData.postValue(response.body())
                            } else {
                                CommonUtil.ApiAlertFinish(activity, response.body()!!.message)
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {

                    } else {
                        CommonUtil.ApiAlertFinish(
                            activity,
                            activity.getString(R.string.txt_no_record_found)
                        )
                    }
                }

                override fun onFailure(call: Call<MenuResponse?>, t: Throwable) {
                    UserMenuMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity,
                        activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val userMenuLiveData: LiveData<MenuResponse?>
        get() = UserMenuMutableLiveData


    fun GetNotification(jsonObject: JsonObject?, activity: Activity) {
      val  progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog.show()
        RestClient.Companion.apiInterfaces.GetNotifications(jsonObject)
            ?.enqueue(object : Callback<GetNotificationsResponse?> {
                override fun onResponse(
                    call: Call<GetNotificationsResponse?>,
                    response: Response<GetNotificationsResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "NotificationResponse",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                GetNotificationMutableLiveData.postValue(response.body())
                            } else {
                                CommonUtil.ApiAlertFinish(activity, response.body()!!.message)
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        CommonUtil.ApiAlertFinish(
                            activity,
                            activity.getString(R.string.txt_no_record_found)
                        )
                    } else {
                        CommonUtil.ApiAlertFinish(
                            activity,
                            activity.getString(R.string.txt_no_record_found)
                        )
                    }
                }

                override fun onFailure(call: Call<GetNotificationsResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetNotificationMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity,
                        activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val notificationLiveData: LiveData<GetNotificationsResponse?>
        get() = GetNotificationMutableLiveData

    init {
        client_auth = RestClient()
        DashboardResposneMutableLiveData = MutableLiveData()
        UserMenuMutableLiveData = MutableLiveData()
        GetNotificationMutableLiveData = MutableLiveData()

    }
}