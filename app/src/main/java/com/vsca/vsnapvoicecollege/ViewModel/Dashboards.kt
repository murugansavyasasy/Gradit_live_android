package com.vsca.vsnapvoicecollege.ViewModel


import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Model.DashBoardResponse
import com.vsca.vsnapvoicecollege.Model.GetNotificationsResponse
import com.vsca.vsnapvoicecollege.Model.Hallticket
import com.vsca.vsnapvoicecollege.Model.MenuResponse
import com.vsca.vsnapvoicecollege.Repository.DashboardServices


class Dashboards(application: Application) : AndroidViewModel(application) {

    private var apiRepositories: DashboardServices? = null
    var dashboardLivedata: LiveData<DashBoardResponse?>? = null
        private set
    var userMenuLiveData: LiveData<MenuResponse?>? = null
        private set
    var notificationData: LiveData<GetNotificationsResponse?>? = null
        private set

    var Hallticket: LiveData<Hallticket?>? = null
        private set

    fun init() {
        apiRepositories = DashboardServices()
        dashboardLivedata = apiRepositories!!.dashBoardLiveData
        userMenuLiveData = apiRepositories!!.userMenuLiveData
        notificationData = apiRepositories!!.notificationLiveData
    }

    fun dashboard(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.DashBoard(jsonObject, activity!!)
    }

    fun getUsermenus(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetUsermenu(jsonObject, activity!!)
    }

    fun getNotifications(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetNotification(jsonObject, activity!!)
    }

}