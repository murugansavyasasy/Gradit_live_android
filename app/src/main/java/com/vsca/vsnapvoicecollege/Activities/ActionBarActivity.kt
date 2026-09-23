package com.vsca.vsnapvoicecollege.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
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


import androidx.core.view.WindowCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.view.updateLayoutParams
import androidx.core.graphics.ColorUtils
import android.view.ViewTreeObserver
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import kotlin.math.max
abstract class ActionBarActivity : AppCompatActivity() {

    var SelectedRecipientlist: ArrayList<RecipientSelected> = ArrayList()
    var SelectedRecipientlistAttendanceEdit: ArrayList<Attendance_Edit_Selected> = ArrayList()
    var Title: String? = null
    var Description: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
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

    /**
     * Call once, right after setContentView + your ActionBar setup call.
     *
     * statusBarBgView is the real surface for the status bar area — solid colour today,
     * a gradient drawable later. The window background is only a plain-colour fallback
     * for the brief moment before statusBarBgView is measured; it is never a drawable.
     *
     * statusBarBgView is optional — pass null if the screen doesn't have one.
     */
    fun AppCompatActivity.fixEdgeToEdgeActionBar(
        rootView: View,
        statusBarBgView: View?,
        priority: String?
    ) {
        fixEdgeToEdgeActionBar(rootView, statusBarBgView, getPriorityColor(priority))
    }

    fun AppCompatActivity.fixEdgeToEdgeActionBar(
        rootView: View,
        statusBarBgView: View?,
        @ColorInt fallbackColor: Int,
        statusBarDrawable: Drawable? = null
    ) {
        // Fallback only — plain colour, shown briefly before statusBarBgView is laid out
        window.setBackgroundDrawable(ColorDrawable(fallbackColor))

        // Real surface — colour today, can become a gradient drawable later
        if (statusBarDrawable != null) {
            statusBarBgView?.background = statusBarDrawable
        } else {
            statusBarBgView?.setBackgroundColor(fallbackColor)
        }

        val luminanceSource = fallbackColor  // icon contrast is based on the solid colour either way
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = ColorUtils.calculateLuminance(luminanceSource) > 0.5
            isAppearanceLightNavigationBars = ColorUtils.calculateLuminance(luminanceSource) > 0.5
        }

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            v.updatePadding(
                left = bars.left,
                right = bars.right,
                bottom = max(bars.bottom, ime.bottom)
            )

            // Gives this view the true status bar height on every version, including 16/17
            statusBarBgView?.updateLayoutParams { height = bars.top }

            WindowInsetsCompat.CONSUMED
        }

        // Push content below the ActionBar by the real measured overlap
        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val bar = window.decorView
                    .findViewById<View>(androidx.appcompat.R.id.action_bar_container)
                if (bar == null || bar.visibility != View.VISIBLE || bar.height == 0) return

                val barLoc = IntArray(2)
                val contentLoc = IntArray(2)
                bar.getLocationOnScreen(barLoc)
                rootView.getLocationOnScreen(contentLoc)

                val overlap = barLoc[1] + bar.height - contentLoc[1]
                val lp = rootView.layoutParams as? ViewGroup.MarginLayoutParams ?: return

                if (overlap > 0) {
                    lp.topMargin += overlap
                    rootView.layoutParams = lp
                }
                rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    @ColorInt
    fun Context.getPriorityColor(priority: String?): Int = ContextCompat.getColor(
        this,
        when (priority) {
            "p1" -> R.color.clr_principal
            "p2", "p3", "p6" -> R.color.clr_teachingstaff
            "p4" -> R.color.clr_receiver
            "p5" -> R.color.clr_parent
            "p7" -> R.color.cle_lightorang
            else -> R.color.black
        }
    )
    protected abstract val layoutResourceId: Int

    fun View.addActionBarMarginIfNeeded() {
        if (Build.VERSION.SDK_INT >= 35) {
            val typedArray = context.theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
            val actionBarHeight = typedArray.getDimension(0, 0f).toInt()
            typedArray.recycle()
            (layoutParams as? ViewGroup.MarginLayoutParams)?.let {
                it.topMargin = actionBarHeight
                layoutParams = it
            }
        }
    }

    fun ActionbarWithoutBottom(activity: Activity,
                               hideBackButton: Boolean = false

    ) {
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.action_bar_layout)
        val view = supportActionBar!!.customView
        val lblMemberName = view.findViewById<View>(R.id.lblMemberName) as TextView
        val layoutUserDetails = view.findViewById<View>(R.id.layoutUserDetails) as ConstraintLayout
        val lblRole = view.findViewById<View>(R.id.lblRole) as TextView
        val imgMan = view.findViewById<View>(R.id.imgProfile) as ImageView
        val imgBack = view.findViewById<View>(R.id.imgBack) as ImageView
        imgNotification = view.findViewById<View>(R.id.imgNotification) as ImageView
        imgRefresh = view.findViewById<View>(R.id.imgRefresh) as ImageView
        val imgCollegeLogo = view.findViewById<View>(R.id.imgCollegeLogo) as ImageView
        val constAction = view.findViewById<View>(R.id.constAction) as ConstraintLayout
        Log.d("LogoImage",CommonUtil.CollegeLogo.toString())
        if (CommonUtil.CollegeLogo == null || CommonUtil.CollegeLogo.isEmpty()) {
            Glide.with(activity)
                .load(R.drawable.dummy_college_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgCollegeLogo)
        } else {
            Glide.with(activity).load(CommonUtil.CollegeLogo)
                .placeholder(R.drawable.dummy_college_icon)
                .error(R.drawable.dummy_college_icon)
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
        imgBack.visibility= View.VISIBLE
        Log.d("ActivityName", "Current Activity: ${activity::class.java.simpleName}")
        if (hideBackButton){
            imgBack.visibility= View.GONE
        }

        imgBack.setOnClickListener { onBackPressed() }
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

        val menuTintRes = when (CommonUtil.Priority) {
            "p1" -> R.color.clr_principal
            "p2", "p3", "p6" -> R.color.clr_teachingstaff
            "p4" -> R.color.clr_receiver
            "p5" -> R.color.clr_parent
            "p7" -> R.color.cle_lightorang
            else -> R.color.black
        }
        val tintColor = androidx.core.content.ContextCompat.getColor(activity, menuTintRes)
        val whiteFg = androidx.core.content.ContextCompat.getColor(activity, R.color.white)
        constAction.setBackgroundColor(tintColor)
        supportActionBar!!.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(tintColor))
        (view.parent as? android.view.View)?.setBackgroundColor(tintColor)
        activity.window.statusBarColor = tintColor
        lblMemberName.setTextColor(whiteFg)
        lblRole.setTextColor(whiteFg)
        imgBack.setColorFilter(whiteFg)
        imgNotification!!.setColorFilter(whiteFg)
        imgRefresh!!.setColorFilter(whiteFg)
    }

    companion object {
        var profilePopup: PopupWindow? = null
        var popupWebview: PopupWindow? = null
        var dashboardViewModel: Dashboards? = null
        var appviewModelbase: App? = null
        var imgRefresh: ImageView? = null
        var imgNotification: ImageView? = null



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
        val i = Intent(activity, LoginRolesRewamp::class.java)
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
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_logout)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(true)

        val btnCancel = dialog.findViewById<Button>(R.id.btnLogoutCancel)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnLogoutConfirm)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnConfirm.setOnClickListener {
            dialog.dismiss()
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
            val i = Intent(activity, LoginRewamp::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(i)
            activity.finish()
        }
        dialog.show()
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

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_api_alert)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        val lblMessage = dialog.findViewById<TextView>(R.id.lblAlertMessage)
        val btnOk = dialog.findViewById<TextView>(R.id.btnAlertOk)
        lblMessage.text = Msg
        btnOk.setOnClickListener {
            if (value) {
                profilePopup!!.dismiss()
            }
            dialog.dismiss()
        }

        dialog.show()

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
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId?.toString()?:"")
        jsonObject.addProperty(ApiRequestNames.Req_msgtype, msgtype)
        jsonObject.addProperty(ApiRequestNames.Req_detailsid, detailsId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        appviewModelbase!!.getAppreadStatus(jsonObject, activity)
        Log.d("AppReadStatus", jsonObject.toString())
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