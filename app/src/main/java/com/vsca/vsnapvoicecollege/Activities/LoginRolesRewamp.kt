package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.LoginChooseRolesAdapter
import com.vsca.vsnapvoicecollege.Interfaces.ProfileClickListener
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.Model.ProfileGroup
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.ViewModel.Auth
import com.vsca.vsnapvoicecollege.databinding.LoginRolesRewampBinding

class LoginRolesRewamp : AppCompatActivity(), ProfileClickListener {

    private var rolesadapter: LoginChooseRolesAdapter? = null
    private var appviewModel: App? = null
    private lateinit var binding: LoginRolesRewampBinding
    var authViewModel: Auth? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = LoginRolesRewampBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CommonUtil.MenuListDashboard.clear()
        appviewModel = ViewModelProvider(this)[App::class.java]
        appviewModel!!.init()
        authViewModel = ViewModelProvider(this)[Auth::class.java]
        authViewModel!!.init()



        isToolBarPrimaryTheme1(
            mainViewId = R.id.main,
            statusBarBgView = binding.statusBarBackground
        )


        authViewModel!!.isLogout!!.observe(this) { response ->
            if (response != null) {
                if (response.Status == true) {
                    BaseActivity.performLogout(0, this@LoginRolesRewamp)   // clear + redirect
                } else {
                    CommonUtil.ApiAlert(this@LoginRolesRewamp, response.Message)
                }
            } else {
                CommonUtil.ApiAlert(this@LoginRolesRewamp, CommonUtil.Something_went_wrong)
            }
        }

        binding.btnLogout.setOnClickListener {
            logoutClick()
        }
        binding.cardInstitutionDetails.visibility= View.GONE

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val progressDialog = CustomLoading.createProgressDialog(this)
        progressDialog.show()
        val originalList = CommonUtil.UserDataList ?: ArrayList()

        val groups = processLoginResponse(originalList)

        rolesadapter = LoginChooseRolesAdapter(
            groups = groups,
            context = this,
            profileClickListener = this
        )

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.rvProfiles.layoutManager = mLayoutManager
        binding.rvProfiles.itemAnimator = DefaultItemAnimator()
        binding.rvProfiles.adapter = rolesadapter
        binding.rvProfiles.recycledViewPool.setMaxRecycledViews(0, 80)
        progressDialog.dismiss()

    }

    override fun onProfileClick(profile: LoginDetails) {
        CommonUtil.MenuListDashboard.clear()
        SetLoginData(profile)

        val intent = Intent(this@LoginRolesRewamp, DashBoardActivityRewamp::class.java)
        startActivity(intent)
    }

    private fun SetLoginData(data: LoginDetails) {
        CommonUtil.Priority = data.priority ?: ""
        CommonUtil.MemberId = data.memberid
        CommonUtil.MemberName = data.membername ?: ""
        CommonUtil.Collegename = data.colgname ?: ""
        CommonUtil.MemberType = data.loginas ?: ""
        CommonUtil.CollegeId = data.colgid
        CommonUtil.deptname = data.deptname ?: ""
        CommonUtil.CollegeCity = data.colgcity ?: ""
        CommonUtil.DivisionId = data.divisionId ?: ""
        CommonUtil.Courseid = data.courseid ?: ""
        CommonUtil.DepartmentId = data.deptid ?: ""
        CommonUtil.isAllowtomakecall = data.is_allow_to_make_call
        CommonUtil.YearId = data.yearid ?: ""
        CommonUtil.SemesteName = data.semestername ?: ""
        CommonUtil.SemesterId = data.semesterid ?: ""
        CommonUtil.SectionId = data.sectionid ?: ""
        CommonUtil.isParentEnable = data.is_parent_target_enabled ?: ""
        CommonUtil.CollegeLogo = data.colglogo ?: ""
    }


    fun logoutClick() {
        BaseActivity.LogoutAlert(
            getString(R.string.txt_logout_alert), 0, this@LoginRolesRewamp
        )
        {
            val mobilenumber = SharedPreference.getSH_MobileNumber(this)
            val deviceId = Settings.Secure.getString(
                contentResolver, Settings.Secure.ANDROID_ID
            ) ?: ""

            val jsonObject = JsonObject().apply {
                addProperty("mobilenumber", mobilenumber)
                addProperty("secure_id", deviceId)
                addProperty("device_type", CommonUtil.ANDROID)
            }

            authViewModel!!.Logout(jsonObject, this@LoginRolesRewamp)
        }
    }
    private fun processLoginResponse(originalList: ArrayList<LoginDetails>): ArrayList<ProfileGroup> {
        val orderedPriorities = listOf("p1", "p2", "p3", "p4", "p5", "p6", "p7")
        val groupedMap = originalList.groupBy { it.priority }
        val result = ArrayList<ProfileGroup>()

        orderedPriorities.forEach { priority ->
            val profiles = groupedMap[priority]
            if (!profiles.isNullOrEmpty()) {


                result.add(
                    ProfileGroup(
                        priority = priority!!,
                        title = profiles.firstOrNull()?.loginas ?: "",
                        count = profiles.size,
                        isExpanded = true,
                        profile = ArrayList(profiles)
                    )
                )
            }
        }
        return result
    }

//    fun logoutClick() {
//        BaseActivity.LogoutAlert(getString(R.string.txt_logout_alert), 0, this@LoginRolesRewamp)
//
//        val mobilenumber = SharedPreference.getSH_MobileNumber(this)
//        val deviceId = Settings.Secure.getString(
//            contentResolver,
//            Settings.Secure.ANDROID_ID
//        ) ?: ""
//        Log.d("DeviceID",deviceId)
//
//        val jsonObject = JsonObject()
//        jsonObject.addProperty("mobilenumber", mobilenumber)
//        jsonObject.addProperty("secure_id", deviceId)
//        authViewModel!!.login(jsonObject, this@LoginRolesRewamp)
//    }

    fun isToolBarPrimaryTheme1(
        mainViewId: Int,
        statusBarBgView: View
    ) {
        enableEdgeToEdge()

        val mainView = findViewById<View>(mainViewId)

        // White status bar icons (unchanged)
        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = true

        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            statusBarBgView.updateLayoutParams {
                height = systemBars.top
            }

            view.updatePadding(
                left = systemBars.left,
                right = systemBars.right,
                bottom = systemBars.bottom
            )

            insets
        }

        // White status bar background
        statusBarBgView.setBackgroundColor(Color.WHITE)

        window.statusBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )

            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            )

            window.statusBarColor = Color.TRANSPARENT

            window.navigationBarColor =
                resources.getColor(R.color.white, theme)
        }
    }
}