package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.vsca.vsnapvoicecollege.Adapters.LoginChooseRoles
import com.vsca.vsnapvoicecollege.Interfaces.LoginRolesListener
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App

class LoginRoles : AppCompatActivity() {

    @JvmField
    @BindView(R.id.ryclerview)
    var ryclerview: RecyclerView? = null

    @JvmField
    @BindView(R.id.txt_student)
    var txt_student: TextView? = null

    @JvmField
    @BindView(R.id.txt_Principle)
    var txt_Principle: TextView? = null

    @JvmField
    @BindView(R.id.txt_Parent)
    var txt_Parent: TextView? = null

    var rolesadapter: LoginChooseRoles? = null
    var appviewModel: App? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_roles)
        ButterKnife.bind(this)
        CommonUtil.MenuListDashboard.clear()
        appviewModel = ViewModelProvider(this)[App::class.java]
        appviewModel!!.init()



        txt_student!!.setOnClickListener {

            rolesadapter!!._LoginClick("p4")
            txt_student!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
            txt_Principle!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)
            txt_Principle!!.setTextColor(resources.getColor(R.color.black))
            txt_student!!.setTextColor(resources.getColor(R.color.white))

        }

        txt_Parent!!.setOnClickListener {

            if (txt_Parent!!.text.equals(CommonUtil.Non_Teaching_staff)) {

                rolesadapter!!._LoginClick("p6")
                txt_Parent!!.setTextColor(resources.getColor(R.color.white))
                txt_Parent!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
                txt_Principle!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)
                txt_Principle!!.setTextColor(resources.getColor(R.color.black))

            } else {

                rolesadapter!!._LoginClick("p5")
                txt_Parent!!.setTextColor(resources.getColor(R.color.white))
                txt_Parent!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
                txt_Principle!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)
                txt_Principle!!.setTextColor(resources.getColor(R.color.black))
            }
        }

        txt_Principle!!.setOnClickListener {

            if (txt_Principle!!.text.equals(CommonUtil._staff)) {

                rolesadapter!!._LoginClick("p3")
                txt_Principle!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
                txt_Principle!!.setTextColor(resources.getColor(R.color.white))
                txt_student!!.setTextColor(resources.getColor(R.color.black))
                txt_student!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)

            } else if (txt_Principle!!.text.equals(CommonUtil.HOD)) {

                rolesadapter!!._LoginClick("p2")
                txt_Principle!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
                txt_Principle!!.setTextColor(resources.getColor(R.color.white))
                txt_student!!.setTextColor(resources.getColor(R.color.black))
                txt_student!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)

            } else if (txt_Principle!!.text.equals("UNIVERSITY HEAD")) {

                rolesadapter!!._LoginClick("p7")
                txt_Principle!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
                txt_Principle!!.setTextColor(resources.getColor(R.color.white))
                txt_student!!.setTextColor(resources.getColor(R.color.black))
                txt_student!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)
                txt_Parent!!.setTextColor(resources.getColor(R.color.black))
                txt_Parent!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)

            } else {

                rolesadapter!!._LoginClick("p1")
                txt_Principle!!.background = resources.getDrawable(R.drawable.login_typebackround_color)
                txt_Principle!!.setTextColor(resources.getColor(R.color.white))
                txt_student!!.setTextColor(resources.getColor(R.color.black))
                txt_student!!.background = resources.getDrawable(R.drawable.loginus_backround_whitecolor)

            }
        }

        for (i in CommonUtil.UserDataList!!.indices) {

            if (CommonUtil.UserDataList!![i].priority.equals("p1")) {
                txt_Principle!!.visibility = View.VISIBLE
            } else if (CommonUtil.UserDataList!![i].priority.equals("p7")) {
                txt_Principle!!.visibility = View.VISIBLE
                txt_Principle!!.text = "UNIVERSITY HEAD"
            } else if (CommonUtil.UserDataList!![i].priority.equals("p2")) {
                txt_Principle!!.text = "HOD"
                txt_Principle!!.visibility = View.VISIBLE
            } else if (CommonUtil.UserDataList!![i].priority.equals("p3")) {
                txt_Principle!!.text = "STAFF"
                txt_Principle!!.visibility = View.VISIBLE
            } else if (CommonUtil.UserDataList!![i].priority.equals("p4")) {
                txt_student!!.visibility = View.VISIBLE
            }

            if (CommonUtil.UserDataList!![i].priority.equals("p5")) {
                txt_Parent!!.visibility = View.VISIBLE
                txt_student!!.visibility = View.GONE
            }

            if (CommonUtil.UserDataList!![i].priority.equals("p6")) {
                txt_Parent!!.text = "NON TEACHING STAFF"
            }

        }

        if (CommonUtil.UserDataList!!.size == 1) {

            for (i in CommonUtil.UserDataList!!.indices) {

                if (CommonUtil.UserDataList!![i].priority.equals("p1")) {
                    txt_Principle!!.visibility = View.VISIBLE
                    txt_student!!.visibility = View.GONE
                    txt_Parent!!.visibility = View.GONE
                    txt_Principle!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)
                    txt_Principle!!.setTextColor(resources.getColor(R.color.white))

                } else if (CommonUtil.UserDataList!![i].priority.equals("p7")) {
                    txt_Principle!!.visibility = View.VISIBLE
                    txt_student!!.visibility = View.GONE
                    txt_Parent!!.visibility = View.GONE

                    txt_Principle!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)
                    txt_Principle!!.setTextColor(resources.getColor(R.color.white))

                } else if (CommonUtil.UserDataList!![i].priority.equals("p2")) {
                    txt_Principle!!.visibility = View.VISIBLE
                    txt_student!!.visibility = View.GONE
                    txt_Parent!!.visibility = View.GONE

                    txt_Principle!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)
                    txt_Principle!!.setTextColor(resources.getColor(R.color.white))

                } else if (CommonUtil.UserDataList!![i].priority.equals("p3")) {
                    txt_Principle!!.visibility = View.VISIBLE
                    txt_student!!.visibility = View.GONE
                    txt_Parent!!.visibility = View.GONE

                    txt_Principle!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)
                    txt_Principle!!.setTextColor(resources.getColor(R.color.white))

                } else if (CommonUtil.UserDataList!![i].priority.equals("p4")) {
                    txt_Principle!!.visibility = View.GONE
                    txt_student!!.visibility = View.VISIBLE
                    txt_Parent!!.visibility = View.GONE

                    txt_student!!.setTextColor(resources.getColor(R.color.white))
                    txt_student!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)

                } else if (CommonUtil.UserDataList!![i].priority.equals("p5")) {
                    txt_Principle!!.visibility = View.GONE
                    txt_student!!.visibility = View.GONE
                    txt_Parent!!.visibility = View.VISIBLE

                    txt_Parent!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)
                    txt_Parent!!.setTextColor(resources.getColor(R.color.white))

                } else if (CommonUtil.UserDataList!![i].priority.equals("p6")) {

                    txt_Principle!!.visibility = View.GONE
                    txt_student!!.visibility = View.GONE
                    txt_Parent!!.visibility = View.VISIBLE

                    txt_Parent!!.background =
                        resources.getDrawable(R.drawable.login_typebackround_color)
                    txt_Parent!!.setTextColor(resources.getColor(R.color.white))

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
        ryclerview!!.layoutManager = mLayoutManager
        ryclerview!!.itemAnimator = DefaultItemAnimator()
        ryclerview!!.adapter = rolesadapter
        ryclerview!!.recycledViewPool.setMaxRecycledViews(0, 80)
        rolesadapter!!.notifyDataSetChanged()

    }

    private fun SetLoginData(data: LoginDetails) {
        CommonUtil.Priority = data.priority!!
        CommonUtil.MemberId = data.memberid
        CommonUtil.MemberName = data.membername!!
        CommonUtil.Collegename = data.colgname!!
        CommonUtil.MemberType = data.loginas!!
        CommonUtil.CollegeId = data.colgid
        CommonUtil.CollegeCity = data.colgcity.toString()
        CommonUtil.DivisionId = data.divisionId!!
        CommonUtil.Courseid = data.courseid!!
        CommonUtil.DepartmentId = data.deptid!!
        CommonUtil.isAllowtomakecall = data.is_allow_to_make_call
        CommonUtil.YearId = data.yearid!!
        CommonUtil.SemesterId = data.semesterid!!
        CommonUtil.SectionId = data.sectionid!!
        CommonUtil.isParentEnable = data.is_parent_target_enabled!!
        CommonUtil.CollegeLogo = data.colglogo!!
    }

    @OnClick(R.id.btnLogout)
    fun logoutClick() {
        BaseActivity.LogoutAlert(getString(R.string.txt_logout_alert), 0, this@LoginRoles)
    }
}