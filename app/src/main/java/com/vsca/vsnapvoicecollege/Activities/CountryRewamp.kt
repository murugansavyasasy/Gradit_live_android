package com.vsca.vsnapvoicecollege.Activities

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapter.CountryAdapter
import com.vsca.vsnapvoicecollege.Model.CountryDetails
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.Model.VersionCheckDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.TermsNConditionUrl
import com.vsca.vsnapvoicecollege.Utils.MyWebViewClient
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.Utils.ToastManager
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.ViewModel.Auth
import com.vsca.vsnapvoicecollege.databinding.CountryRewampBinding

class CountryRewamp : AppCompatActivity() {

    private lateinit var binding: CountryRewampBinding

    private var authViewModel: Auth? = null
    private var appViewModel: App? = null

    private lateinit var countryAdapter: CountryAdapter

    private var countryData: List<CountryDetails> = emptyList()

    private var selectedCountry: CountryDetails? = null
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
    var popuptermsNcondition: PopupWindow? = null


    private var isAgree = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = CountryRewampBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isToolBarPrimaryTheme1(
            mainViewId = R.id.main,
            statusBarBgView = binding.statusBarBackground
        )


        authViewModel = ViewModelProvider(this)[Auth::class.java]
        authViewModel!!.init()

        CommonUtil.MenuListDashboard.clear()

        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        setupCountryRecyclerView()


        binding.termsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            isAgree = isChecked
        }

        binding.lblTermsAndConditions.setOnClickListener {

            if (popuptermsNcondition?.isShowing == true) {
                return@setOnClickListener
            }

            val inflater =
                getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val layout = inflater.inflate(
                R.layout.terms_and_conditions_rewamp,
                null
            )

            popuptermsNcondition = PopupWindow(
                layout,
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                true
            )

            popuptermsNcondition!!.contentView = layout

            popuptermsNcondition!!.showAtLocation(
                binding.root,
                Gravity.CENTER,
                0,
                0
            )

            val webview =
                layout.findViewById<WebView>(R.id.webview)


            val imgBack = layout.findViewById<ImageView>(R.id.imgBack)

            imgBack.setOnClickListener {
                popuptermsNcondition?.dismiss()
                popuptermsNcondition = null

            }

            webview.webViewClient =
                MyWebViewClient(this@CountryRewamp)

            webview.scrollBarStyle =
                View.SCROLLBARS_INSIDE_OVERLAY

            val webSettings = webview.settings

            webSettings.loadsImagesAutomatically = true
            webSettings.builtInZoomControls = true
            webSettings.javaScriptEnabled = true

            webview.loadUrl(TermsNConditionUrl)
        }

        setTermsAndConditionsText()

        setupSearch()

        getCountryList()

        authViewModel!!.loginResposneLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    LoginData = response.data!!
                    if (LoginData.size != 0) {
                        CommonUtil.UserDataList = response.data as ArrayList<LoginDetails>?
                        SharedPreference.putLoginDetails(this@CountryRewamp, mobilenumber, password)

                        Log.d("LoginDataSize", LoginData.size.toString())
                        if (LoginData.size > 1) {

                            val i = Intent(this@CountryRewamp, LoginRolesRewamp::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)

                        } else {

                            SetLoginData(LoginData)
                            val i = Intent(this@CountryRewamp, DashBoardActivityRewamp::class.java)
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(i)
                        }
                    } else {
                        CommonUtil.ApiAlert(this@CountryRewamp, message)
                    }
                } else {
                    CommonUtil.ApiAlert(this@CountryRewamp, message)
                }
            } else {
                CommonUtil.ApiAlert(this@CountryRewamp, CommonUtil.Something_went_wrong)
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
                        this@CountryRewamp,
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
                    CommonUtil.ApiAlert(this@CountryRewamp, message)
                }
            }
        }


        authViewModel!!.countryDetailsResponseLiveData!!.observe(this) { response ->

            if (response != null) {

                val status = response.status
                val message = response.message

                if (status == 1) {

                    countryData = response.data ?: emptyList()

                    if (countryData.isEmpty()) {

                        binding.rcCountryList.visibility = View.GONE
                        binding.rlaSearch.visibility = View.GONE

                        binding.lytList.visibility = View.VISIBLE

                        binding.txtNoData.text =
                            message ?: getString(R.string.txt_no_data_found)

                    } else {
                        binding.rlaSearch.visibility = View.VISIBLE

                        binding.rcCountryList.visibility = View.VISIBLE

                        binding.lytList.visibility = View.GONE

                        countryAdapter.setData(countryData)
                    }

                } else {

                    binding.rlaSearch.visibility = View.GONE

                    binding.rcCountryList.visibility = View.GONE
                    binding.lytList.visibility = View.VISIBLE

                    binding.txtNoData.text =
                        message ?: getString(R.string.txt_no_data_found)
                }

            } else {
                binding.rlaSearch.visibility = View.GONE

                binding.rcCountryList.visibility = View.GONE
                binding.lytList.visibility = View.VISIBLE

                binding.txtNoData.text =
                    getString(R.string.error_null_cursor)
            }
        }


        binding.txtNext.setOnClickListener {

            if (selectedCountry == null) {

                ToastManager.showToast(
                    this,
                    R.string.choose_your_country
                )

                return@setOnClickListener
            }

            if (!isAgree) {

                ToastManager.showToast(
                    this,
                    R.string.Please_accept_the_Terms_and_Conditions_to_proceed
                )

                return@setOnClickListener
            }

            SharedPreference.putagreed(this@CountryRewamp, isAgree)

            val country = selectedCountry!!
            val BASE_URL = country.baseurl
            val countryid = country.countryid
            val countryID = countryid.toString()
            val countryname = country.country
            val mobilelength = country.mobilenumberlen
            val codecountry = country.countyCode
            val idapplication = country.idapplication
            SharedPreference.putCountryDetails(
                this@CountryRewamp, countryID, countryname, mobilelength, BASE_URL,codecountry,idapplication
            )

            RestClient.changeApiBaseUrl(BASE_URL?:"")
            authViewModel!!.getVersionCheck(this@CountryRewamp)
            ToastManager.cancelToast()
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun UpdateAlert() {
        val textView = TextView(this@CountryRewamp)
        textView.text = resources.getString(R.string.txt_update)
        textView.setPadding(20, 30, 20, 30)
        textView.textSize = 20f
        textView.setBackgroundColor(R.color.white)
        textView.setTextColor(Color.WHITE)
        val builder = AlertDialog.Builder(this@CountryRewamp)
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
    private fun setupCountryRecyclerView() {

        countryAdapter = CountryAdapter(

            onCountryClick = { country ->
                selectedCountry = country
            },


            onEmptyResult = { isEmpty ->

                if (isEmpty) {

                    binding.rcCountryList.visibility = View.GONE
                    binding.lytList.visibility = View.VISIBLE
                    binding.txtNoData.text = getString(R.string.txt_no_data_found)


                } else {

                    binding.rcCountryList.visibility = View.VISIBLE
                    binding.lytList.visibility = View.GONE
                }
            }
        )


        binding.rcCountryList.apply {

            layoutManager =
                LinearLayoutManager(this@CountryRewamp)

            adapter = countryAdapter
        }
    }



    private fun setupSearch() {

        binding.edtSearch.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }


                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    countryAdapter.filter(
                        s?.toString() ?: ""
                    )
                }


                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )
    }


    private fun AutoLogin() {
        mobilenumber = SharedPreference.getSH_MobileNumber(this@CountryRewamp)
        password = SharedPreference.getSH_Password(this@CountryRewamp)

        if (mobilenumber!!.isNotEmpty() && password!!.isNotEmpty()) {
            val jsonObject = JsonObject()
            jsonObject.addProperty(ApiRequestNames.Req_mobileNumber, mobilenumber)
            jsonObject.addProperty(ApiRequestNames.Req_password, password)
            authViewModel!!.login(jsonObject, this@CountryRewamp)
        } else {
            val i = Intent(this@CountryRewamp, MobileNumberRewamp::class.java)
            startActivity(i)
        }
    }


    private fun getCountryList() {

        authViewModel!!.getcountryList(
            this@CountryRewamp
        )
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




    private fun setTermsAndConditionsText() {

        val text =
            "I agree to the Terms and Conditions"

        val spannable =
            SpannableString(text)

        val start =
            text.indexOf("Terms and Conditions")

        val end =
            start + "Terms and Conditions".length


        // Blue color
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    this,
                    R.color.btn_clr_blue
                )
            ),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        // Underline
        spannable.setSpan(
            UnderlineSpan(),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        binding.lblTermsAndConditions.text =
            spannable
    }

    fun isToolBarPrimaryTheme1(
        mainViewId: Int,
        statusBarBgView: View
    ) {
        enableEdgeToEdge()

        val mainView = findViewById<View>(mainViewId)

        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = false

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
                resources.getColor(R.color.clr_auth_gray, theme)
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
    }
}