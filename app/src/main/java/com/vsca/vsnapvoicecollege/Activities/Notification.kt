package com.vsca.vsnapvoicecollege.Activities

import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.NotificationAdapter
import com.vsca.vsnapvoicecollege.Model.GetNotificationDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.databinding.BottomMenuSwipeBinding

class Notification : BaseActivity<BottomMenuSwipeBinding>() {


    var notificationadapter: NotificationAdapter? = null

    var GetNotificationData: List<GetNotificationDetails> = ArrayList()

    override fun inflateBinding(): BottomMenuSwipeBinding {
        return BottomMenuSwipeBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = BottomMenuSwipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        accessBottomViewIcons(
            binding,
            R.id.img_swipe,
            R.id.layoutbottomCurve, R.id.recyclermenusbottom, R.id.swipeUpMenus, R.id.LayoutDepartment, R.id.LayoutCollege, R.id.imgAddPlus
        )

        ActionBarMethod(this@Notification)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        MenuBottomType()
        NotificatonRequest()
        if (CommonUtil.HeaderMenuNotification) {
            imgNotification!!.isEnabled = false
            imgNotification!!.isClickable = false
        }

        dashboardViewModel!!.notificationData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    UserMenuRequest(this@Notification)
                    GetNotificationData = response.data!!


                    notificationadapter =
                        NotificationAdapter(GetNotificationData, this@Notification)
                    val mLayoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this@Notification)
                    binding.idRVCategories!!.layoutManager = mLayoutManager
                    binding.idRVCategories!!!!.itemAnimator = DefaultItemAnimator()
                    binding.idRVCategories!!!!.adapter = notificationadapter
                    binding.idRVCategories!!!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    notificationadapter!!.notifyDataSetChanged()
                } else {
                    CommonUtil.ApiAlert(this@Notification, message)
                }
            }
        }
        imgRefresh!!.setOnClickListener {
            NotificatonRequest()
        }
    }

    override val layoutResourceId: Int
        protected get() = R.layout.bottom_menu_swipe

    private fun NotificatonRequest() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_MemberID, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        dashboardViewModel!!.getNotifications(jsonObject, this@Notification)
        Log.d("NotificationRequest:", jsonObject.toString())
    }
}