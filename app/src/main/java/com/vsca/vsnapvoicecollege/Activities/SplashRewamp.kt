package com.vsca.vsnapvoicecollege.Activities


import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.Model.VersionCheckDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.Auth
import com.vsca.vsnapvoicecollege.databinding.SplashRewampBinding

class SplashRewamp : AppCompatActivity() {

    private var floatAnimator: ObjectAnimator? = null


    var handler: Handler? = null
    var progressDialog: ProgressDialog? = null
    var authViewModel: Auth? = null
    var VersionData: List<VersionCheckDetails> = ArrayList()
    var LoginData: List<LoginDetails> = ArrayList()
    var ForceUpdate = 0
    var VersionUpdate = 0
    var mobilenumber: String? = null
    var password: String? = null
    var privacypolicy: String? = null
    var faq: String? = null
    var help: String? = null
    var termsandcondition: String? = null
    var imagecount: String? = null
    var pdfcount: String? = null
    var eventphotoscount: String? = null
    var attendancedaycount: String? = null
    var playstorelink: String? = null
    var versionalerttitle: String? = null
    var versionalertcontent: String? = null
    var playstoremarketid: String? = null
    var videojson: String? = null
    var videosizelimit: String? = null
    var videosizealert: String? = null
    var emergencyduration: String? = null
    var nonemergencyduration: String? = null

    private lateinit var binding: SplashRewampBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashRewampBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyPrimaryGradientTheme(
            mainViewId = R.id.main
        )


        progressDialog = CustomLoading.createProgressDialog(this@SplashRewamp)
        progressDialog!!.show()
        authViewModel = ViewModelProvider(this).get(Auth::class.java)
        authViewModel!!.init()
        CommonUtil.MenuListDashboard.clear()

        //  CommonUtil.isDeviceTokenApiCalling = true

        startImpactfulEntranceAnimation()


        if (!CommonUtil.isNetworkConnected(this@SplashRewamp)) {
            progressDialog!!.dismiss()
            val dlgAlert = AlertDialog.Builder(this@SplashRewamp)
            dlgAlert.setMessage(resources.getString(R.string.txt_network))
            dlgAlert.setTitle(resources.getString(R.string.txt_error_msg))
            dlgAlert.setPositiveButton(resources.getString(R.string.txt_Ok)) { dialog, which ->
                finish()
                dialog.dismiss()
            }
            dlgAlert.setCancelable(true)
            dlgAlert.create().show()
        } else {
            progressDialog!!.dismiss()
            handler = Handler()
            handler!!.postDelayed({
                progressDialog!!.dismiss()
                val prefrenceBaseUrl = SharedPreference.getSH_Baseurl(this@SplashRewamp)
                if (prefrenceBaseUrl != "") {
                    authViewModel!!.getVersionCheck(this@SplashRewamp)
                } else {
                    val intents = Intent(this@SplashRewamp, CountryRewamp::class.java)
                    startActivity(intents)
                    finish()
                }
            }, 2000)
        }

        authViewModel!!.versionCheckLiveData?.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    VersionData = response.data!!
                    ForceUpdate = VersionData[0].forceUpdate
                    VersionUpdate = VersionData[0].versionUpdate
                    privacypolicy = VersionData[0].privarypolicy
                    faq = VersionData[0].faq
                    help = VersionData[0].help
                    termsandcondition = VersionData[0].termsandcondition
                    imagecount = VersionData[0].imagecount
                    pdfcount = VersionData[0].pdfcount
                    eventphotoscount = VersionData[0].eventphotoscount
                    attendancedaycount = VersionData[0].attendancedaycount
                    playstorelink = VersionData[0].playstorelink
                    versionalerttitle = VersionData[0].versionalerttitle
                    versionalertcontent = VersionData[0].versionalertcontent
                    playstoremarketid = VersionData[0].playstoremarketid
                    videojson = VersionData[0].videojson
                    videosizelimit = VersionData[0].videosizelimit
                    videosizealert = VersionData[0].videosizealert
                    emergencyduration = VersionData[0].emergency
                    nonemergencyduration = VersionData[0].nonemergency
                    SharedPreference.putVersionCheckData(
                        this@SplashRewamp,
                        faq,
                        help,
                        privacypolicy,
                        termsandcondition,
                        imagecount,
                        pdfcount,
                        eventphotoscount,
                        attendancedaycount,
                        playstorelink,
                        versionalerttitle,
                        versionalertcontent,
                        playstoremarketid,
                        videojson,
                        videosizelimit,
                        videosizealert,
                        emergencyduration,
                        nonemergencyduration
                    )
                    Log.d("videojson", videojson.toString())
                    if (ForceUpdate == 0 && VersionUpdate == 0) {
                        AutoLogin()
                    } else {
                        UpdateAlert()
                    }
                } else {
                    CommonUtil.ApiAlert(this@SplashRewamp, message)
                }
            }
        }

        authViewModel!!.loginResposneLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    LoginData = response.data!!
                    if (LoginData.size != 0) {
                        CommonUtil.UserDataList = response.data as ArrayList<LoginDetails>?
                        SharedPreference.putLoginDetails(this@SplashRewamp, mobilenumber, password)

                        Log.d("LoginDataSize", LoginData.size.toString())
                        if (LoginData.size > 1) {

                            val i = Intent(this@SplashRewamp, LoginRolesRewamp::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                            finish()


                        } else {

                            SetLoginData(LoginData)
                            val i = Intent(this@SplashRewamp, DashBoardActivityRewamp::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                            finish()

                        }
                    } else {
                        CommonUtil.ApiAlert(this@SplashRewamp, message)
                    }
                } else {
                    CommonUtil.ApiAlert(this@SplashRewamp, message)
                }
            } else {
                CommonUtil.ApiAlert(this@SplashRewamp, CommonUtil.Something_went_wrong)
            }
        }
    }

    private fun AutoLogin() {
        mobilenumber = SharedPreference.getSH_MobileNumber(this@SplashRewamp)
        password = SharedPreference.getSH_Password(this@SplashRewamp)

        if (mobilenumber!!.isNotEmpty() && password!!.isNotEmpty()) {
            val jsonObject = JsonObject()
            jsonObject.addProperty(ApiRequestNames.Req_mobileNumber, mobilenumber)
            jsonObject.addProperty(ApiRequestNames.Req_password, password)
            authViewModel!!.login(jsonObject, this@SplashRewamp)
        } else {
            val i = Intent(this@SplashRewamp, MobileNumberRewamp::class.java)
            startActivity(i)
            finish()

        }
    }

    @SuppressLint("ResourceAsColor")
    private fun UpdateAlert() {
        val textView = TextView(this@SplashRewamp)
        textView.text = resources.getString(R.string.txt_update)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(R.color.white)
        textView.setTextColor(Color.WHITE)
        val builder = AlertDialog.Builder(this@SplashRewamp)
        builder.setCustomTitle(textView)
        builder.setMessage(resources.getString(R.string.txt_update_available))
        builder.setCancelable(false)
        Log.d("Package_Name", packageName)

        if (ForceUpdate == 1 && VersionUpdate == 1) {
            builder.setPositiveButton("Now") { dialog, which ->
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.data = Uri.parse(resources.getString(R.string.txt_playstore_url))
                    startActivity(intent)
                } catch (anfe: ActivityNotFoundException) {
                    anfe.printStackTrace()
                }
            }
        } else {
            builder.setPositiveButton("Now") { dialog, which ->
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.data = Uri.parse(resources.getString(R.string.txt_playstore_url))
                    startActivity(intent)
                } catch (anfe: ActivityNotFoundException) {
                    anfe.printStackTrace()
                }
            }
            builder.setNegativeButton("Later") { dialog, which ->
                AutoLogin()
            }
        }
        builder.create().show()
    }


    private fun SetLoginData(data: List<LoginDetails>) {
        Log.d("login", "setUserdata")
        for (i in data.indices) {
            CommonUtil.Priority = data[i].priority!!
            CommonUtil.MemberId = data[i].memberid
            CommonUtil.MemberName = data[i].membername!!
            CommonUtil.MemberType = data[i].loginas!!
            CommonUtil.CollegeId = data[i].colgid
            CommonUtil.DivisionId = data[i].divisionId!!
            CommonUtil.Courseid = data[i].courseid!!
            CommonUtil.DepartmentId = data[i].deptid!!
            CommonUtil.deptname = data[i].deptname!!
            CommonUtil.YearId = data[i].yearid!!
            CommonUtil.isAllowtomakecall = data[i].is_allow_to_make_call!!
            CommonUtil.Collegename = data[i].colgname.toString()
            CommonUtil.CollegeCity = data.get(i).colgcity.toString()
            CommonUtil.SemesterId = data[i].semesterid!!
            CommonUtil.SemesteName = data[i].semestername!!
            CommonUtil.SectionId = data[i].sectionid!!
            CommonUtil.isParentEnable = data[i].is_parent_target_enabled!!
            CommonUtil.CollegeLogo = data[i].colglogo!!
        }
    }


    private fun startImpactfulEntranceAnimation() {
        binding.centerBlock.alpha = 0f
        binding.centerBlock.scaleX = 0.82f
        binding.centerBlock.scaleY = 0.82f
        binding.centerBlock.translationY = 40f
        binding.centerBlock.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .translationY(0f)
            .setDuration(550)
            .setInterpolator(OvershootInterpolator(1.4f))
            .withEndAction {
                startFloatingHover()
            }
            .start()

    }

    private fun startFloatingHover() {
        floatAnimator?.cancel()
        floatAnimator = ObjectAnimator.ofFloat(
            binding.centerBlock,
            View.TRANSLATION_Y,
            0f, -14f, 0f
        ).apply {
            duration = 2400
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    fun Activity.applyPrimaryGradientTheme(
        mainViewId: Int
    ) {
        val mainView = findViewById<View>(mainViewId)

        // Draw content behind system bars
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        // Transparent status bar
        window.statusBarColor = Color.TRANSPARENT

        // White status bar icons
        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor =
                resources.getColor(R.color.clr_auth_gray, theme)
        }

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.findViewById<View>(R.id.centerBlock)?.updatePadding(
                top = systemBars.top
            )

            view.updatePadding(
                left = systemBars.left,
                right = systemBars.right,
                bottom = systemBars.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(mainView)
    }
}