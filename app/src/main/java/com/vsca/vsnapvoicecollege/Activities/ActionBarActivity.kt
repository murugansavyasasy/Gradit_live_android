package com.vsca.vsnapvoicecollege.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.SenderModel.Attendance_Edit_Selected
import com.vsca.vsnapvoicecollege.SenderModel.RecipientSelected
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.Utils.MyWebViewClient
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.ViewModel.Dashboards
import java.io.File

abstract class ActionBarActivity : AppCompatActivity() {

    var SelectedRecipientlist: ArrayList<RecipientSelected> = ArrayList()
    var SelectedRecipientlistAttendanceEdit: ArrayList<Attendance_Edit_Selected> = ArrayList()
    var Title: String? = null
    var Description: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        ButterKnife.bind(this)


        appviewModelbase = ViewModelProvider(this).get(
            App::class.java
        )
        appviewModelbase!!.init()
        appviewModelbase!!.appreadstatusresponseLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    Log.d("AppReadMessageAction", message!!)
                }
            }
        }
    }

    protected abstract val layoutResourceId: Int

    fun ActionbarWithoutBottom(activity: Activity) {
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.action_bar_layout)
        val view = supportActionBar!!.customView
        val lblMemberName = view.findViewById<View>(R.id.lblMemberName) as TextView
        val layoutUserDetails = view.findViewById<View>(R.id.layoutUserDetails) as ConstraintLayout
        val lblRole = view.findViewById<View>(R.id.lblRole) as TextView
        val imgMan = view.findViewById<View>(R.id.imgProfile) as ImageView
        imgNotification = view.findViewById<View>(R.id.imgNotification) as ImageView
        imgRefresh = view.findViewById<View>(R.id.imgRefresh) as ImageView
        val imgCollegeLogo = view.findViewById<View>(R.id.imgCollegeLogo) as ImageView
        val constAction = view.findViewById<View>(R.id.constAction) as ConstraintLayout
        if (CommonUtil.CollegeLogo == null || CommonUtil.CollegeLogo.isEmpty()) {
            Glide.with(activity)
                .load(R.drawable.dummy_college_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgCollegeLogo)
        } else {
            Glide.with(activity)
                .load(CommonUtil.CollegeLogo)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgCollegeLogo)
        }
        imgNotification!!.setOnClickListener {
            CommonUtil.HeaderMenuNotification = true
            val i = Intent(activity, Notification::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(i)
        }

        imgMan.setOnClickListener { ProfilePopUp(activity) }
        imgCollegeLogo.setOnClickListener { IntentToChangeRole(activity) }
        layoutUserDetails.setOnClickListener { IntentToChangeRole(activity) }

        if (CommonUtil.Priority == "p1") {
            constAction.setBackgroundColor(Color.parseColor(getString(R.string.txt_color_white)))
            lblRole.setTextColor(Color.parseColor(getString(R.string.txt_color_prinicpal)))
            lblMemberName.text = CommonUtil.MemberName
            lblRole.text = CommonUtil.MemberType
        }

        if (CommonUtil.Priority == "p7") {
            constAction.setBackgroundColor(Color.parseColor(getString(R.string.txt_color_white)))
            lblRole.setTextColor(Color.parseColor(getString(R.string.txt_color_lightorang)))
            lblMemberName.text = CommonUtil.MemberName
            lblRole.text = CommonUtil.MemberType
        }

        if (CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3") {
            constAction.setBackgroundColor(Color.parseColor(getString(R.string.txt_color_white)))
            lblRole.setTextColor(Color.parseColor(getString(R.string.txt_clr_staff)))
            lblMemberName.text = CommonUtil.MemberName
            lblRole.text = CommonUtil.MemberType
        }
        if (CommonUtil.Priority == "p4") {
            constAction.setBackgroundColor(Color.parseColor(getString(R.string.txt_color_white)))
            lblRole.setTextColor(Color.parseColor(getString(R.string.txt_color_receiver)))
            lblMemberName.text = CommonUtil.MemberName
            lblRole.setText(R.string.txt_student)
        }
        if (CommonUtil.Priority == "p5") {
            constAction.setBackgroundColor(Color.parseColor(getString(R.string.txt_color_white)))
            lblRole.setTextColor(Color.parseColor(getString(R.string.txt_color_parent)))
            lblMemberName.text = CommonUtil.MemberName
            lblRole.setText(R.string.txt_parent)
        }
        if (CommonUtil.Priority == "p6") {
            constAction.setBackgroundColor(Color.parseColor(getString(R.string.txt_color_white)))
            lblRole.setTextColor(Color.parseColor(getString(R.string.txt_color_parent)))
            lblMemberName.text = CommonUtil.MemberName
            lblRole.setText(R.string.txt_parent)
        }
    }

    companion object {
        var profilePopup: PopupWindow? = null
        var popupWebview: PopupWindow? = null
        var dashboardViewModel: Dashboards? = null
        var appviewModelbase: App? = null
        var imgRefresh: ImageView? = null
        var imgNotification: ImageView? = null


        fun LogoutAlertUtil(title: String?, value: Int, activity: Activity) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(title)
            builder.setCancelable(false)
            builder.setPositiveButton("Yes") { dialog, which ->

                if (value == 1) {
                    profilePopup!!.dismiss()
                }

                SharedPreference.clearShLogin(activity)
                CommonUtil.Priority = ""
                CommonUtil.MemberId = 0
                CommonUtil.MemberName = ""
                CommonUtil.MemberType = ""
                CommonUtil.CollegeLogo = ""
                CommonUtil.CollegeId = 0
                CommonUtil.MobileNUmber = ""
                CommonUtil.DivisionId = ""
                CommonUtil.Courseid = ""
                CommonUtil.DepartmentId = ""
                CommonUtil.YearId = ""
                CommonUtil.SemesterId = ""
                CommonUtil.SectionId = ""
                CommonUtil.isParentEnable = ""
                val i = Intent(activity, Login::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity.startActivity(i)
                activity.finish()
            }
            builder.setNegativeButton("No") { dialog, which -> builder.setCancelable(false) }
            builder.create().show()
        }

        fun deleteDirUtil(dir: File?): Boolean {
            return if (dir != null && dir.isDirectory) {
                val children = dir.list()
                if (children != null) {
                    for (i in children.indices) {
                        val success = deleteDirUtil(File(dir, children[i]))
                        if (!success) {
                            return false
                        }
                    }
                }
                dir.delete()
            } else if (dir != null && dir.isFile) {
                dir.delete()
            } else {
                false
            }
        }
    }

    private fun IntentToChangeRole(activity: Activity) {
        val i = Intent(activity, LoginRoles::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(i)
    }

    private fun ProfilePopUp(activity: Activity) {
        val layoutInflater = activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.popup_profile_menus, null)
        val logout = view.findViewById<View>(R.id.layoutLogout) as ConstraintLayout
        val layoutChangeRoles = view.findViewById<View>(R.id.layoutChangeRole) as ConstraintLayout
        val layoutProfile = view.findViewById<View>(R.id.layoutProfile) as ConstraintLayout
        val layoutFaq = view.findViewById<View>(R.id.layoutFAQ) as ConstraintLayout
        val layoutHelp = view.findViewById<View>(R.id.layoutHelp) as ConstraintLayout
        val layoutPrivacypolicy =
            view.findViewById<View>(R.id.layoutPrivacypolicy) as ConstraintLayout
        val layoutTermsAndCondition =
            view.findViewById<View>(R.id.layoutTermsCondition) as ConstraintLayout
        val layoutChangepassword =
            view.findViewById<View>(R.id.layoutChangePassword) as ConstraintLayout
        val layoutClearCache = view.findViewById<View>(R.id.layoutClearCache) as ConstraintLayout
        if (CommonUtil.Priority == "p4" || CommonUtil.Priority == "p5") {
            layoutProfile.visibility = View.VISIBLE
        } else {
            layoutProfile.visibility = View.GONE
        }
        layoutProfile.setOnClickListener {
            profilePopup!!.dismiss()
            CommonUtil.parentMenuCourseExam = 2
            val i = Intent(activity, CourseDetails::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(i)
        }
        layoutFaq.setOnClickListener {
            val faq = SharedPreference.getSH_Faq(activity)
            LoadWebView(activity, faq, 0)
        }
        layoutHelp.setOnClickListener {
            val help = SharedPreference.getSH_Help(activity)
            LoadWebView(activity, help, 1)
        }
        layoutPrivacypolicy.setOnClickListener {
            val privacypolicy = SharedPreference.getSH_Privacypolicy(activity)
            LoadWebView(activity, privacypolicy, 2)
        }
        layoutTermsAndCondition.setOnClickListener {
            val termsCondition = SharedPreference.getSH_TermsNcondition(activity)
            LoadWebView(activity, termsCondition, 3)
        }
        logout.setOnClickListener {
            LogoutAlert(getString(R.string.txt_alert_logout), 1, activity)
        }

        layoutChangeRoles.setOnClickListener { IntentToChangeRole(activity) }
        layoutClearCache.setOnClickListener { ClearCache(activity) }
        profilePopup =
            PopupWindow(view, ListPopupWindow.WRAP_CONTENT, ListPopupWindow.WRAP_CONTENT, true)
        profilePopup!!.contentView = view
        profilePopup!!.isOutsideTouchable = false
        profilePopup!!.showAtLocation(view, Gravity.RIGHT or Gravity.TOP, 0, 225)
    }

    fun LogoutAlert(title: String?, value: Int, activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setCancelable(false)
        builder.setPositiveButton(CommonUtil.Yes) { dialog, which ->
            if (value == 1) {
                BaseActivity.profilePopup!!.dismiss()
            }
            SharedPreference.clearShLogin(activity)
            CommonUtil.Priority = ""
            CommonUtil.MemberId = 0
            CommonUtil.MemberName = ""
            CommonUtil.MemberType = ""
            CommonUtil.CollegeLogo = ""
            CommonUtil.CollegeId = 0
            CommonUtil.MobileNUmber = ""
            CommonUtil.DivisionId = ""
            CommonUtil.Courseid = ""
            CommonUtil.DepartmentId = ""
            CommonUtil.YearId = ""
            CommonUtil.SemesterId = ""
            CommonUtil.SectionId = ""
            CommonUtil.isParentEnable = ""
            val i = Intent(activity, Login::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(i)
            activity.finish()
        }
        builder.setNegativeButton(CommonUtil.No) { dialog, which -> builder.setCancelable(false) }
        builder.create().show()
    }

    fun ClearCache(activity: Activity) {
        try {
            val dir = activity.cacheDir
            if (deleteDirUtil(dir)) {
                AlertOk(activity, CommonUtil.Cache_cleared, true)
            } else {
                AlertOk(activity, CommonUtil.Cache_not_cleared, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun AlertOk(activity: Activity, Msg: String, value: Boolean) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(CommonUtil.Info)
        builder.setMessage(Msg)
        builder.setCancelable(false)
        builder.setPositiveButton(CommonUtil.OK) { dialog, which ->
            if (value) {
                profilePopup!!.dismiss()
            }
        }
        builder.create().show()
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun LoadWebView(activity: Activity?, url: String?, Type: Int) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.activity_terms_condition, null)
        popupWebview = PopupWindow(
            layout, android.app.ActionBar.LayoutParams.MATCH_PARENT,
            android.app.ActionBar.LayoutParams.MATCH_PARENT,
            true
        )
        popupWebview!!.contentView = layout
        popupWebview!!.showAtLocation(layout, Gravity.CENTER, 0, 0)
        val webview = layout.findViewById<WebView>(R.id.webview)
        val lblMenuHeaderName = layout.findViewById<TextView>(R.id.lblMenuHeaderName)
        val imgBack = layout.findViewById<ImageView>(R.id.imgBack)
        val btnTerms = layout.findViewById<Button>(R.id.btnTermsAndCondition)
        val LayoutHeader = layout.findViewById<ConstraintLayout>(R.id.LayoutHeader)
        val viewLine = layout.findViewById<View>(R.id.viewline)
        btnTerms.visibility = View.GONE
        LayoutHeader.visibility = View.VISIBLE
        viewLine.visibility = View.VISIBLE
        when (Type) {
            0 -> {
                lblMenuHeaderName.setText(R.string.txt_faq)
            }

            1 -> {
                lblMenuHeaderName.setText(R.string.txt_help)
            }

            2 -> {
                lblMenuHeaderName.setText(R.string.txt_privacy_policy)
            }

            3 -> {
                lblMenuHeaderName.setText(R.string.txt_terms_amp_condition)
            }
        }
        imgBack.setOnClickListener { popupWebview!!.dismiss() }
        val progressDialog = CustomLoading.createProgressDialog(activity)
        webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                progressDialog.show()
                setProgress(progress * 100)
                if (progress == 100) {
                    progressDialog.dismiss()
                }
            }
        }
        webview.webViewClient = MyWebViewClient(activity!!)
        webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        val webSettings = webview.settings
        webSettings.loadsImagesAutomatically = true
        webSettings.builtInZoomControls = true
        webSettings.javaScriptEnabled = true
        webview.loadUrl(url!!)
        progressDialog.dismiss()
    }

    fun AppReadStatusActionbar(activity: Activity?, msgtype: String, detailsId: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_msgtype, msgtype)
        jsonObject.addProperty(ApiRequestNames.Req_detailsid, detailsId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        appviewModelbase!!.getAppreadStatus(jsonObject, activity)
        Log.d("AppReadStatus", jsonObject.toString())
    }

    fun setMaxDate(MinimumDate: Long) {

    }

    class GridSpacingItemDecoration(private val spanCount: Int, includeEdge: Boolean) :
        RecyclerView.ItemDecoration() {
        private var spacing = 4
        private val includeEdge: Boolean
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount
            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column - 1) * spacing / spanCount
                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }

        init {
            this.includeEdge = includeEdge
        }
    }
}