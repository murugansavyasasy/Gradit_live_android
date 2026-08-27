package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Adapters.LoginChooseRolesAdapter
import com.vsca.vsnapvoicecollege.Interfaces.ProfileClickListener
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.Model.ProfileGroup
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LoginRolesRewampBinding

class LoginRolesRewamp : AppCompatActivity(), ProfileClickListener {

    private var rolesadapter: LoginChooseRolesAdapter? = null
    private var appviewModel: App? = null
    private lateinit var binding: LoginRolesRewampBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = LoginRolesRewampBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CommonUtil.MenuListDashboard.clear()
        appviewModel = ViewModelProvider(this)[App::class.java]
        appviewModel!!.init()

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        insetsController.isAppearanceLightNavigationBars = true


        binding.btnLogout.setOnClickListener {
            logoutClick()
        }


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

    private fun processLoginResponse(originalList: ArrayList<LoginDetails>): ArrayList<ProfileGroup> {
        val orderedPriorities = listOf("p1", "p2", "p3", "p4", "p5", "p6", "p7")
        val groupedMap = originalList.groupBy { it.priority }
        val result = ArrayList<ProfileGroup>()

        orderedPriorities.forEach { priority ->
            val profiles = groupedMap[priority]
            if (!profiles.isNullOrEmpty()) {

//                val title = when {
//                    profiles.first().loginas?.contains("Principal", ignoreCase = true) == true -> "Principal"
//                    profiles.first().loginas?.contains("Student", ignoreCase = true) == true   -> "Student"
//                    profiles.first().loginas?.contains("Staff", ignoreCase = true) == true     -> "Staff"
//                    profiles.first().loginas?.contains("Parent", ignoreCase = true) == true    -> "Parent"
//                    profiles.first().loginas?.contains("HOD", ignoreCase = true) == true       -> "HOD"
//                    profiles.first().loginas?.contains("Non", ignoreCase = true) == true       -> "Non Teaching Staff"
//                    profiles.first().loginas?.contains("University", ignoreCase = true) == true -> "University Head"
//                    else -> profiles.first().loginas ?: "Other"
//                }

                result.add(
                    ProfileGroup(
                        priority = priority!!,
                        title = profiles.firstOrNull()?.loginas?:"",
                        count = profiles.size,
                        isExpanded = true,
                        profile = ArrayList(profiles)
                    )
                )
            }
        }
        return result
    }

    fun logoutClick() {
        BaseActivity.LogoutAlert(getString(R.string.txt_logout_alert), 0, this@LoginRolesRewamp)
    }
}