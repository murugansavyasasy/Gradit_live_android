package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Adapters.LoginChooseRoles
import com.vsca.vsnapvoicecollege.Interfaces.LoginRolesListener
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityLoginRolesBinding

class LoginRoles : AppCompatActivity() {

    var rolesadapter: LoginChooseRoles? = null
    var appviewModel: App? = null
    private lateinit var binding: ActivityLoginRolesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginRolesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CommonUtil.MenuListDashboard.clear()
        appviewModel = ViewModelProvider(this)[App::class.java]
        appviewModel!!.init()

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true



        binding.txtStudent!!.setOnClickListener {

            rolesadapter!!._LoginClick("p4")
            binding.txtStudent!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
            binding.txtPrinciple!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)
            binding.txtPrinciple!!.setTextColor(resources.getColor(R.color.black))
            binding.txtStudent!!.setTextColor(resources.getColor(R.color.white))

        }

        binding.btnLogout.setOnClickListener{
            logoutClick()
        }

        binding.txtParent!!.setOnClickListener {

            if (binding.txtParent!!.text.equals(CommonUtil.Non_Teaching_staff)) {

                rolesadapter!!._LoginClick("p6")
                binding.txtParent!!.setTextColor(resources.getColor(R.color.white))
                binding.txtParent!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
                binding.txtPrinciple!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)
                binding.txtPrinciple!!.setTextColor(resources.getColor(R.color.black))

            } else {

                rolesadapter!!._LoginClick("p5")
                binding.txtParent!!.setTextColor(resources.getColor(R.color.white))
                binding.txtParent!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
                binding.txtPrinciple!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)
                binding.txtPrinciple!!.setTextColor(resources.getColor(R.color.black))
            }
        }

        binding.txtPrinciple!!.setOnClickListener {

            if (binding.txtPrinciple!!.text.equals(CommonUtil._staff)) {

                rolesadapter!!._LoginClick("p3")
                binding.txtPrinciple!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
                binding.txtPrinciple!!.setTextColor(resources.getColor(R.color.white))
                binding.txtStudent!!.setTextColor(resources.getColor(R.color.black))
                binding.txtStudent!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)

            } else if (binding.txtPrinciple!!.text.equals(CommonUtil.HOD)) {

                rolesadapter!!._LoginClick("p2")
                binding.txtPrinciple!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
                binding.txtPrinciple!!.setTextColor(resources.getColor(R.color.white))
                binding.txtStudent!!.setTextColor(resources.getColor(R.color.black))
                binding.txtStudent!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)

            } else if (binding.txtPrinciple!!.text.equals("UNIVERSITY HEAD")) {

                rolesadapter!!._LoginClick("p7")
                binding.txtPrinciple!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
                binding.txtPrinciple!!.setTextColor(resources.getColor(R.color.white))
                binding.txtStudent!!.setTextColor(resources.getColor(R.color.black))
                binding.txtStudent!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)
                binding.txtParent!!.setTextColor(resources.getColor(R.color.black))
                binding.txtParent!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)

            } else {

                rolesadapter!!._LoginClick("p1")
                binding.txtPrinciple!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
                binding.txtPrinciple!!.setTextColor(resources.getColor(R.color.white))
                binding.txtStudent!!.setTextColor(resources.getColor(R.color.black))
                binding.txtStudent!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)

            }
        }

        for (i in CommonUtil.UserDataList!!.indices) {

            if (CommonUtil.UserDataList!![i].priority.equals("p1")) {
                binding.txtPrinciple!!.visibility = View.VISIBLE
            } else if (CommonUtil.UserDataList!![i].priority.equals("p7")) {
                binding.txtPrinciple!!.visibility = View.VISIBLE
                binding.txtPrinciple!!.text = "UNIVERSITY HEAD"
            } else if (CommonUtil.UserDataList!![i].priority.equals("p2")) {
                binding.txtPrinciple!!.text = "HOD"
                binding.txtPrinciple!!.visibility = View.VISIBLE
            } else if (CommonUtil.UserDataList!![i].priority.equals("p3")) {
                binding.txtPrinciple!!.text = "STAFF"
                binding.txtPrinciple!!.visibility = View.VISIBLE
            } else if (CommonUtil.UserDataList!![i].priority.equals("p4")) {
                binding.txtStudent!!.visibility = View.VISIBLE
            }

            if (CommonUtil.UserDataList!![i].priority.equals("p5")) {
                binding.txtParent!!.visibility = View.VISIBLE
                binding.txtStudent!!.visibility = View.GONE
            }

            if (CommonUtil.UserDataList!![i].priority.equals("p6")) {
                binding.txtParent!!.text = "NON TEACHING STAFF"
            }

        }

        if (CommonUtil.UserDataList!!.size == 1) {

            for (i in CommonUtil.UserDataList!!.indices) {

                if (CommonUtil.UserDataList!![i].priority.equals("p1")) {
                    binding.txtPrinciple!!.visibility = View.VISIBLE
                    binding.txtStudent!!.visibility = View.GONE
                    binding.txtParent!!.visibility = View.GONE
                    binding.txtPrinciple!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)
                    binding.txtPrinciple!!.setTextColor(resources.getColor(R.color.white))

                } else if (CommonUtil.UserDataList!![i].priority.equals("p7")) {
                    binding.txtPrinciple!!.visibility = View.VISIBLE
                    binding.txtStudent!!.visibility = View.GONE
                    binding.txtParent!!.visibility = View.GONE

                    binding.txtPrinciple!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)
                    binding.txtPrinciple!!.setTextColor(resources.getColor(R.color.white))

                } else if (CommonUtil.UserDataList!![i].priority.equals("p2")) {
                    binding.txtPrinciple!!.visibility = View.VISIBLE
                    binding.txtStudent!!.visibility = View.GONE
                    binding.txtParent!!.visibility = View.GONE

                    binding.txtPrinciple!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)
                    binding.txtPrinciple!!.setTextColor(resources.getColor(R.color.white))

                } else if (CommonUtil.UserDataList!![i].priority.equals("p3")) {
                    binding.txtPrinciple!!.visibility = View.VISIBLE
                    binding.txtStudent!!.visibility = View.GONE
                    binding.txtParent!!.visibility = View.GONE

                    binding.txtPrinciple!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)
                    binding.txtPrinciple!!.setTextColor(resources.getColor(R.color.white))

                } else if (CommonUtil.UserDataList!![i].priority.equals("p4")) {
                    binding.txtPrinciple!!.visibility = View.GONE
                    binding.txtStudent!!.visibility = View.VISIBLE
                    binding.txtParent!!.visibility = View.GONE

                    binding.txtStudent!!.setTextColor(resources.getColor(R.color.white))
                    binding.txtStudent!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)

                } else if (CommonUtil.UserDataList!![i].priority.equals("p5")) {
                    binding.txtPrinciple!!.visibility = View.GONE
                    binding.txtStudent!!.visibility = View.GONE
                    binding.txtParent!!.visibility = View.VISIBLE

                    binding.txtParent!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)
                    binding.txtParent!!.setTextColor(resources.getColor(R.color.white))

                } else if (CommonUtil.UserDataList!![i].priority.equals("p6")) {

                    binding.txtPrinciple!!.visibility = View.GONE
                    binding.txtStudent!!.visibility = View.GONE
                    binding.txtParent!!.visibility = View.VISIBLE

                    binding.txtParent!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)
                    binding.txtParent!!.setTextColor(resources.getColor(R.color.white))

                }
            }
        }
        rolesadapter = CommonUtil.UserDataList?.let {

            LoginChooseRoles(it, this@LoginRoles, object : LoginRolesListener {
                override fun onroleClick(
                    holder: LoginChooseRoles.MyViewHolder, data: LoginDetails
                ) {
                    holder.rytOverAll!!.setOnClickListener(object : View.OnClickListener {

                        override fun onClick(view: View) {
                            SetLoginData(data)
                            val i: Intent = Intent(this@LoginRoles, DashBoard::class.java)
                            startActivity(i)
                        }
                    })
                }

            })
        }

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@LoginRoles)
        binding.ryclerview!!.layoutManager = mLayoutManager
        binding.ryclerview!!.itemAnimator = DefaultItemAnimator()
        binding.ryclerview!!.adapter = rolesadapter
        binding.ryclerview!!.recycledViewPool.setMaxRecycledViews(0, 80)
        rolesadapter!!.notifyDataSetChanged()

    }

    private fun SetLoginData(data: LoginDetails) {
        CommonUtil.Priority = data.priority!!
        CommonUtil.MemberId = data.memberid
        CommonUtil.MemberName = data.membername!!
        CommonUtil.Collegename = data.colgname!!
        CommonUtil.MemberType = data.loginas!!
        CommonUtil.CollegeId = data.colgid
        CommonUtil.deptname = data.deptname!!
        CommonUtil.CollegeCity = data.colgcity.toString()
        CommonUtil.DivisionId = data.divisionId!!
        CommonUtil.Courseid = data.courseid!!
        CommonUtil.DepartmentId = data.deptid!!
        CommonUtil.isAllowtomakecall = data.is_allow_to_make_call
        CommonUtil.YearId = data.yearid!!
        CommonUtil.SemesteName = data.semestername!!
        CommonUtil.SemesterId = data.semesterid!!
        CommonUtil.SectionId = data.sectionid!!
        CommonUtil.isParentEnable = data.is_parent_target_enabled!!
        CommonUtil.CollegeLogo = data.colglogo!!
    }

    fun logoutClick() {
        BaseActivity.LogoutAlert(getString(R.string.txt_logout_alert), 0, this@LoginRoles)
    }
}