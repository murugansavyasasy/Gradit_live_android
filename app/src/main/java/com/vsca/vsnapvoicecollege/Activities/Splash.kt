package com.vsca.vsnapvoicecollege.Activities

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Model.CountryDetails
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.Model.VersionCheckDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.TermsNConditionUrl
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import com.vsca.vsnapvoicecollege.Utils.MyWebViewClient
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.Auth

class Splash : AppCompatActivity() {

    var btnNext: Button? = null
    var TermsAgreed = false
    var rg: RadioGroup? = null
    var handler: Handler? = null
    var progressDialog: ProgressDialog? = null
    var authViewModel: Auth? = null
    var CountryData: List<CountryDetails> = ArrayList()
    var VersionData: List<VersionCheckDetails> = ArrayList()
    var LoginData: List<LoginDetails> = ArrayList()
    var baseurl: String? = null
    var countryname: String? = null
    var mobilelength: String? = null
    var countryCode: String? = null
    var selectedradioValue: String? = null
    var countryid = 0
    var idapplication = 0
    var countryOpen = false
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
    var popuptermsNcondition: PopupWindow? = null
    var countryPopup: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        progressDialog = CustomLoading.createProgressDialog(this@Splash)
        progressDialog!!.show()
        authViewModel = ViewModelProvider(this).get(Auth::class.java)
        authViewModel!!.init()
        CommonUtil.MenuListDashboard.clear()

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        //  CommonUtil.isDeviceTokenApiCalling = true

        if (!CommonUtil.isNetworkConnected(this@Splash)) {
            progressDialog!!.dismiss()
            val dlgAlert = AlertDialog.Builder(this@Splash)
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
                TermsAgreed = SharedPreference.getSH_agreed(this@Splash)
                if (!TermsAgreed) {
                    progressDialog!!.dismiss()
                    TermsAndConditions()
                } else {
                    progressDialog!!.dismiss()
                    TermsAgreed = SharedPreference.getSH_agreed(this@Splash)
                    val prefrenceBaseUrl = SharedPreference.getSH_Baseurl(this@Splash)
                    if (prefrenceBaseUrl != "") {
                        authViewModel!!.getVersionCheck(this@Splash)
                    } else {
                        authViewModel!!.getcountryList(this@Splash)
                    }
                }
            }, 2000)
        }

        authViewModel!!.countryDetailsResponseLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    CountryData = response.data!!
                    CountryListPopUp()
                } else {
                    CommonUtil.ApiAlert(this@Splash, message)
                }
            }
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
                        this@Splash,
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
                    Log.d("videojson",videojson.toString())
                    if (ForceUpdate == 0 && VersionUpdate == 0) {
                        AutoLogin()
                    } else {
                        UpdateAlert()
                    }
                } else {
                    CommonUtil.ApiAlert(this@Splash, message)
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
                        SharedPreference.putLoginDetails(this@Splash, mobilenumber, password)

                        Log.d("LoginDataSize", LoginData.size.toString())
                        if (LoginData.size > 1) {

                            val i = Intent(this@Splash, LoginRolesRewamp::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                            finishAffinity()

                        } else {

                            SetLoginData(LoginData)
                            val i = Intent(this@Splash, DashBoardActivityRewamp::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                            finishAffinity()
                        }
                    } else {
                        CommonUtil.ApiAlert(this@Splash, message)
                    }
                } else {
                    CommonUtil.ApiAlert(this@Splash, message)
                }
            } else {
                CommonUtil.ApiAlert(this@Splash, CommonUtil.Something_went_wrong)
            }
        }
    }

    private fun AutoLogin() {
        mobilenumber = SharedPreference.getSH_MobileNumber(this@Splash)
        password = SharedPreference.getSH_Password(this@Splash)

        if (mobilenumber!!.isNotEmpty() && password!!.isNotEmpty()) {
            val jsonObject = JsonObject()
            jsonObject.addProperty(ApiRequestNames.Req_mobileNumber, mobilenumber)
            jsonObject.addProperty(ApiRequestNames.Req_password, password)
            authViewModel!!.login(jsonObject, this@Splash)
        } else {
            val i = Intent(this@Splash, MobileNumber::class.java)
            startActivity(i)
            finishAffinity()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun UpdateAlert() {
        val textView = TextView(this@Splash)
        textView.text = resources.getString(R.string.txt_update)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(R.color.white)
        textView.setTextColor(Color.WHITE)
        val builder = AlertDialog.Builder(this@Splash)
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

    private fun getFlagEmoji(country: String?): String {
        val flagMap = mapOf(
            "united kingdom" to "\uD83C\uDDEC\uD83C\uDDE7",
            "uk" to "\uD83C\uDDEC\uD83C\uDDE7",
            "united states" to "\uD83C\uDDFA\uD83C\uDDF8",
            "usa" to "\uD83C\uDDFA\uD83C\uDDF8",
            "canada" to "\uD83C\uDDE8\uD83C\uDDE6",
            "australia" to "\uD83C\uDDE6\uD83C\uDDFA",
            "india" to "\uD83C\uDDEE\uD83C\uDDF3",
            "pakistan" to "\uD83C\uDDF5\uD83C\uDDF0",
            "new zealand" to "\uD83C\uDDF3\uD83C\uDDFF",
            "ireland" to "\uD83C\uDDEE\uD83C\uDDEA",
            "germany" to "\uD83C\uDDE9\uD83C\uDDEA",
            "france" to "\uD83C\uDDEB\uD83C\uDDF7",
            "uae" to "\uD83C\uDDE6\uD83C\uDDEA",
            "united arab emirates" to "\uD83C\uDDE6\uD83C\uDDEA",
            "singapore" to "\uD83C\uDDF8\uD83C\uDDEC"
        )
        return flagMap[country?.trim()?.lowercase()] ?: "\uD83C\uDF0D"
    }

    private fun buildCountryRadioButton(country: CountryDetails, id: Int): RadioButton {

        val rb = RadioButton(this@Splash)

        rb.id = id
        rb.text = "  ${getFlagEmoji(country.country)}   ${country.country}"
        rb.textSize = 15f
        rb.setTextColor(resources.getColor(R.color.clr_light_black))
        rb.layoutDirection = View.LAYOUT_DIRECTION_LTR
        rb.gravity = Gravity.CENTER_VERTICAL

        // Hide the default leading indicator; we draw our own at the end.
        rb.buttonDrawable = null

        val radioIndicator = resources.getDrawable(R.drawable.radio_country_selector)
        val indicatorSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 22f, resources.displayMetrics
        ).toInt()
        radioIndicator.setBounds(0, 0, indicatorSizePx, indicatorSizePx)
        rb.setCompoundDrawables(null, null, radioIndicator, null)

        val paddingPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics
        ).toInt()
        rb.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
        rb.compoundDrawablePadding = paddingPx

        return rb
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun TermsAndConditions() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.activity_terms_condition, null)
        popuptermsNcondition = PopupWindow(
            layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true
        )
        popuptermsNcondition!!.contentView = layout
        popuptermsNcondition!!.showAtLocation(layout, Gravity.CENTER, 0, 0)
        val webview = layout.findViewById<WebView>(R.id.webview)
        val btnTerms = layout.findViewById<Button>(R.id.btnTermsAndCondition)

        webview.webViewClient = MyWebViewClient(this@Splash)
        webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        val webSettings = webview.settings
        webSettings.loadsImagesAutomatically = true
        webSettings.builtInZoomControls = true
        webSettings.javaScriptEnabled = true
        webview.loadUrl(TermsNConditionUrl)
        // progressDialog.dismiss()
        btnTerms.setOnClickListener {
            TermsAgreed = true
            SharedPreference.putagreed(this@Splash, TermsAgreed)
            popuptermsNcondition!!.dismiss()

            val prefrenceBaseUrl = SharedPreference.getSH_Baseurl(this@Splash)
            Log.d("preference_BaseUrl", prefrenceBaseUrl!!)
            if (prefrenceBaseUrl != "") {
                authViewModel!!.getVersionCheck(this@Splash)
            } else {
                authViewModel!!.getcountryList(this@Splash)
            }
        }
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

    private fun CountryListPopUp() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.activity_county_choose, null)
        countryPopup = PopupWindow(
            layout,
            androidx.appcompat.app.ActionBar.LayoutParams.MATCH_PARENT,
            androidx.appcompat.app.ActionBar.LayoutParams.MATCH_PARENT,
            true
        )
        countryPopup!!.contentView = layout
        countryPopup!!.showAtLocation(layout, Gravity.CENTER, 0, 0)

        val btnBack = layout.findViewById<ImageView>(R.id.btnBack)
        val edtSearchCountry = layout.findViewById<EditText>(R.id.edtSearchCountry)
        val txtTermsCountry = layout.findViewById<TextView>(R.id.txtTermsCountry)
        rg = layout.findViewById<View>(R.id.RadioGroup) as RadioGroup
        btnNext = layout.findViewById(R.id.btnNext)


        btnBack.setOnClickListener {
            countryPopup!!.dismiss()
            finish()
        }

        txtTermsCountry.setOnClickListener {
            TermsAndConditions()
        }

        for (i in CountryData.indices) {
            baseurl = CountryData[i].baseurl
            countryid = CountryData[i].countryid
            countryname = CountryData[i].country
            mobilelength = CountryData[i].mobilenumberlen
            countryCode = CountryData[i].countyCode
            idapplication = CountryData[i].idapplication

            val list = CountryData[i]
            val rb = buildCountryRadioButton(list, i)
            selectedradioValue = " " + "+" + list.countyCode + "  " + list.country

            val params = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT
            )
            rg!!.addView(rb, params)

            rb.setOnClickListener {
                btnNext!!.isEnabled = true
                btnNext!!.isClickable = true
                btnNext!!.setBackgroundResource(R.drawable.bg_btn_blue)
                btnNext!!.setTextColor(Color.parseColor("#FFFFFF"))
            }
        }

        edtSearchCountry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString().orEmpty().trim().lowercase()

                for (i in CountryData.indices) {
                    val row = rg?.findViewById<RadioButton>(i) ?: continue
                    val matches = query.isEmpty() ||
                            (CountryData[i].country?.lowercase()?.contains(query) == true)
                    row.visibility = if (matches) View.VISIBLE else View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnNext!!.setOnClickListener {
            val selectedPosition = rg!!.checkedRadioButtonId
            if (selectedPosition == -1) {
                return@setOnClickListener
            }
            val list = CountryData[selectedPosition]
            val BASE_URL = list.baseurl
            Log.d("BASEURL", BASE_URL!!)
            val countryid = list.countryid
            val countryID = countryid.toString()
            val countryname = list.country
            val mobilelength = list.mobilenumberlen
            SharedPreference.putCountryDetails(
                this@Splash, countryID, countryname, mobilelength, BASE_URL
            )
            RestClient.changeApiBaseUrl(BASE_URL)
            countryPopup!!.dismiss()
            authViewModel!!.getVersionCheck(this@Splash)
        }
    }
}