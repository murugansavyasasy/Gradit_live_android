package com.vsca.vsnapvoicecollege.Utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.vsca.vsnapvoicecollege.Model.AttendanceHour
import com.vsca.vsnapvoicecollege.Model.AttendanceHourEdit
import com.vsca.vsnapvoicecollege.Model.ExamcreationEdit
import com.vsca.vsnapvoicecollege.Model.Examination_Creation
import com.vsca.vsnapvoicecollege.Model.ImageListView
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.Model.MenuDetailsResponse
import com.vsca.vsnapvoicecollege.Model.Sectiondetail_ExamCreation
import com.vsca.vsnapvoicecollege.Model.SubjectExamcreationEDIT
import com.vsca.vsnapvoicecollege.Model.SubjectdetailX
import com.vsca.vsnapvoicecollege.Model.Subjectdetail_ExamCreation
import com.vsca.vsnapvoicecollege.R
import java.io.File


object CommonUtil {


    @kotlin.jvm.JvmField
    var isBioMetricEnable: Int = 0

    @kotlin.jvm.JvmStatic

    //Video Send
    var selectedPaths = mutableListOf<String>()
    var Videofile: Boolean? = null
    var videofile: String? = null
    val PresentlistStudent: MutableList<String> = ArrayList()
    val AbsendlistStudent: MutableList<String> = ArrayList()
    var Onbackpressed = ""
    var iSubjectId = ArrayList<String>()

    @JvmField
    var Appid = 1

    @JvmField
    var VersionId = 33

    // MENU NAME

    var Home = "home"
    var Chat = "chat"
    var Examination = "examination"
    var Attendance = "attendance"
    var Assignment = "assignment"
    var Circular = "circular"
    var NoticeBoard = "noticeBoard"
    var Events = "events"
    var Faculty = "faculty"
    var Video = "video"
    var Collegename = ""
    var Course_Details = "course Details"
    var Category_Credit_Points = "category Credit Points"
    var Sem_Credit_Points = "sem Credit Points"
    var Exam_Application_Details = "exam Application Details"
    var Hall_Ticket = "Hall Ticket"
    var FeeDetails = "feedetails"
    var Collage_ids = ""
    var CollageIDS: Boolean = true
    var isSubmitted = ""
    var CallEnable = "0"
    var isFileCount = 0
    var UserDataList: ArrayList<LoginDetails>? = null
    var Multipleiamge = ArrayList<String>()
    var filetype: String? = null
    var mediaPlayer: MediaPlayer? = MediaPlayer()
    var futureStudioIconFile: File? = null
    var voicetitle: String? = null
    var VoiceDuration: String? = null
    var Leavetype = ""
    var CollegeCity = ""
    var voiceHeadedId = ""
    var AssignmentDescription = ""
    var OptMessege = ""
    var Assignmenttitle = ""
    var forwarding_text_id = ""
    var seleteddataArrayCheckbox = ArrayList<String>()
    var DepartmentChooseIds = ArrayList<String>()
    var Collageid_ArrayList = ArrayList<String>()
    var UserMenuListId = ArrayList<Int>()
    var MenuListDashboard = ArrayList<MenuDetailsResponse>()
    var AttendanceHour = ArrayList<AttendanceHour>()
    var AttendanceHourEdit = ArrayList<AttendanceHourEdit>()
    var ForwardAssignment = ArrayList<String>()
    var isParticuerSubjectds = ArrayList<String>()
    var menuslug: String? = null
    var isImageViewList = ArrayList<ImageListView>()
    var isSubjectIds = ""
    var DateExam: Int? = null
    var Month: Int? = null
    var ScreenType = ""
    var AttendanceStaffid = ""
    var YearDate: Int? = null
    var EnddateExam: Int? = null
    var EndDateMonth: Int? = null
    var Enddateyear: Int? = null
    var DownloadingFile: Int? = null
    var minimumdate = ""
    var maxmumdate = ""
    var LeavetypeEdit = ""
    var urlFromS3: String? = null
    var VoiceType = ""
    var AssignmentType = ""
    var courseType = ""
    var AttendanceSubjectname = ""
    var AttendanceStaffname = ""
    var SelcetedFileList = ArrayList<String>()
    var AttendanceClassData = ArrayList<String>()
    var EventParticulerId = ""
    var id = ""
    var SpecificButton = ""
    var LeaveReasion = ""
    var AttendanceSubjectId = ""
    var AttendanceStatus = ""
    var EventStatus = ""
    var isAllowtomakecall: Int? = null
    var SectionID_Exam = ""
    var Examination_Creation: ArrayList<Examination_Creation> = java.util.ArrayList()
    var Sectiondetail_ExamCreation: ArrayList<Sectiondetail_ExamCreation> = ArrayList()
    var Subjectdetail_ExamCreation: ArrayList<Subjectdetail_ExamCreation> = java.util.ArrayList()
    var ExamcreationEdit: ArrayList<ExamcreationEdit> = java.util.ArrayList()
    var SubjectExamcreationEDIT: ArrayList<SubjectExamcreationEDIT> = java.util.ArrayList()
    var EditButtonclick = ""
    var AttendanceScreen = ""
    var ExamEditStartdate = ""
    var ExamEditEnddate = ""
    var isAttendanceType = ""

    //Login details
    var Priority = ""
    var Questionid = ""
    var MobileNUmber = ""
    var MemberId = 0
    var CollegeId = 0
    var Selecteddata = ""
    var leavestartdate = ""
    var leaveenddate = ""
    var numberofday = ""
    var applicationid = ""
    var studentid = ""
    var changeanswer = ""
    var StudentBlackORunblack = ""
    var semesterid = ""
    var staffid = ""
    var Venue = ""
    var SubjectID = ""
    var yearnamae = ""
    var staffname = ""
    var examsubjectid = ""
    var examsyllabus = ""
    var examdate = ""
    var Textedit = ""
    var Description = ""
    var title = ""
    var subjectdetails: ArrayList<SubjectdetailX> = ArrayList()
    var section = ""
    var Examname = ""
    var OffsetSet = ""
    var deptidExam = ""
    var Noticeboardid = ""
    var DownloadingFileDashboard = 0
    var EventEdit = ""
    var EventCreatedby = ""
    var semestername = ""
    var sectionname = ""
    var coursename = ""
    var subjectname = ""
    var EventsendType = ""
    var LeaveApplicationID = ""
    var isclassteacher = ""
    var Screenname = ""
    var DivisionId = ""
    var Courseid = ""
    var headerid = ""
    var DepartmentId = ""
    var YearId = ""
    var Eventid = ""
    var subjectid = ""
    var Date = ""
    var Time = ""
    var startdate = ""
    var enddate = ""
    var uri = ""
    var Assignmentid = ""
    var AssignmentDetailsId = ""
    var receivertype = ""
    var deptid = ""
    var SemesterId = ""
    var receiverid = ""
    var seletedStringdata = ""
    var seleteddataArraySection = java.util.ArrayList<String>()
    var Absentlistcount = ""
    var Department = ""
    var Year = ""
    var Semester = ""
    var SectionNmaeAttendance = ""
    var SectionId = ""
    var MemberName = ""
    var MemberType = ""
    var CollegeLogo = ""
    var isParentEnable = ""
    var CommonAdvertisement = ""
    var CommonAdImageSmall = ""
    var AdWebURl = ""
    var parentMenuCourseExam = 0
    var SenderAppId = 1
    var MenuCourseDetails = true
    var MenuDashboardHome = true
    var MenuNoticeBoard = true
    var MenuExamDetails = true
    var MenuHallTicket = true
    var MenuCircular = true
    var MenuAssignment = true
    var MenuChat = true
    var MenuCommunication = true
    var MenuText = true

    var MenuFeeDetails = true
    var MarkAttendance = true
    var AttendanceReport = true

    var MenuExamination = true
    var MenuAttendance = true
    var MenuEvents = true
    var MenuFaculty = true
    var MenuVideo = true
    var MenuCategoryCredit = true
    var MenuSemCredit = true
    var MenuHallticket = true
    var HeaderMenuNotification = false
    var CollegeNotice = "collegenotice"
    var DepartmentNotice = "departmentnotice"
    var CollegeCircular = "collegecircular"
    var DepartmentCircular = "departmentcircular"
    var UpcomingAssignment = "upcomingassignments"
    var PastAssignment = "pastassignments"
    var UpcomingEvents = "upcomingevents"
    var PastEvents = "pastevents"
    var UpcomingExams = "upcomingexams"
    var PastExams = "pastexams"
    var DeviceType = "android"
    var selected_Department = "Selected department count : "
    var selected_Division = "Selected division count : "
    var selected_Course = "Selected course count : "
    var selected_Classes = "Selected classes count : "
    var selected_Section = "Selected section count : "
    var selected_Groups = "Selected groups count : "
    var selected_College = "Selected College count : "
    var Unread = "0"
    var Read = "1"
    var checkbox = 0
    var pastExam = ""
    var courseName_ = ""
    var semester_ = ""
    var year_ = ""
    var section_ = ""
    var forgetpassword_Mobilenumber = ""
    var seleteddataArray = ArrayList<String>()
    var isExpandAdapter = false
    var CommunicationisExpandAdapter = false
    var MenuIDCommunication: String? = null
    var MenuIDCommunicationText: String? = null
    var MenuIDExamination: String? = null
    var MenuIDAssignment: String? = null
    var MenuIDCircular: String? = null
    var MenuIdAttendance: String? = null
    var MenuIDNoticeboard: String? = null
    var MenuIDEvents: String? = null
    var MenuIDVideo: String? = null
    var MenuIDChat: String? = null
    var VimeoIframe: String? = null
    var VimeoVideoUrl: String? = null
    var extension: String? = null
    var MenuTitle: String? = null
    var MenuDescription: String? = null
    var Venuetext: String? = null
    var SeletedStringdataReplace: String? = null
    var isExamName: String? = ""
    var isForgotMobileNumber = ""
    var ivrnumbers = java.util.ArrayList<String>()
    var menu_readHome = ""
    var menu_writeHome = ""
    var menu_readCommunication = ""
    var menu_writeCommunication = ""
    var menu_readCommunicationText = ""
    var menu_writeCommunicationText = ""
    var SectionIdChoose = ""
    var menu_readChat = ""
    var menu_writeChat = ""
    var menu_readExamination = ""
    var menu_writeExamination = ""
    var menu_readAttendance = ""
    var menu_writeAttendance = ""
    var menu_readAssignment = ""
    var menu_writeAssignment = ""
    var menu_readCircular = ""
    var menu_writeCircular = ""
    var menu_readNoticeBoard = ""
    var menu_writeMarkAttendance = ""
    var menu_writeNoticeBoard = ""
    var menu_readEvent = ""
    var menu_writeEvent = ""
    var menu_readFaculty = ""
    var menu_writeFaculty = ""
    var menu_readVideo = ""
    var menu_writeVideo = ""
    var menu_readCourseDetails = ""
    var menu_writeCourseDetails = ""
    var menu_readCategoryCreditPoints = ""
    var menu_writeCategoryCreditPoints = ""
    var menu_readSemCreditPoints = ""
    var menu_writeSemCreditPoints = ""
    var menu_readExamApplicationDetails = ""
    var menu_writeExamApplicationDetails = ""
    var menu_readHallTicker = ""
    var menu_writeHallTicker = ""
    var VideoSending_RedarectUrl = "www.voicesnapforschools.com"
    var Non_Teaching_staff = "NON TEACHING STAFF"
    var _staff = "STAFF"
    var HOD = "HOD"
    var TextHistory = "TextHistory"
    var _OnclickScreen = "Assignment"
    var Reject_or_Approved = "Reject or Approved"
    var New_Assignment = "New Assignment"
    var Forward_Assignment = "Forward Assignment"
    var Remove_All = "Remove All"
    var Select_All = "Select All"
    var Cache_cleared = "Cache cleared"
    var Cache_not_cleared = "Cache not cleared"
    var fill_the_details = "Please fill the all details"
    var Apply_Leave = "Are you sure you want to Apply Leave ?"
    var from_date = "Select the from date"
    var _Startdate = "Select the Startdate"
    var Fill_Exam_name = "Fill the Exam name"
    var _Enddate = "Select the Enddate"
    var Enter_Details = "Kindly Enter Details"
    var Are_you_edit = "Are you sure want to edit !!"
    var My_Department = "My Department"
    var Classes_I_handle = "Classes I handle"
    var Select_Section = "Select the Section"
    var Select_Semester = "Select the Semester"
    var Select_Course = "Select the Course"
    var Select_year = "Select the year"
    var Select_Type = "Select the Type"
    var Select_Division = "Select The Division"
    var Select_Department = "Select the Department"
    var Fill_Exam_Details = "Fill the exam details"
    var StudentCount = "Selected student count : "
    var Subject_Section_Not_allocated =
        "Subject or Section Not allocated / Students not allocated to the section"
    var Record_Voice_and_Enter_Title = "Please Record the Voice and Enter Voice Title"
    var Groups = "Groups"
    var Course = "Course"
    var Division = "Division"
    var Department_ = "Department"
    var Your_Classes = "Your Classes"
    var Year_Section = "Year/Section"
    var CommunicationVoice = "CommunicationVoice"
    var Staff = "Staff"
    var Entire_Department = "Entire Department"
    var Subjects = "Subjects"
    var Tutor = "Tutor"
    var Assignment_SPECIFIC = "Assignment SPECIFIC"
    var New_Video = "New Video"
    var Entire_College = "Entire College"
    var College = "College"
    var Select_the_Target = "Select the Target"
    var Select_the_Receiver = "Select the Receiver"
    var Submit_Alart = "Are you sure you want to submit?"
    var Select_MinimumOne = "Kindly select minimum One value for each till Sections"
    var Fill_Title_and_Description = "Fill the  Title and Description"
    var Are_you_submit = "Are you sure you want to submit ?"
    var Select_OnlyOne = "Select only one Section"
    var Something_went_wrong = "Something went wrong"
    var Delete_ExamSection = "Are you Delete this ExamSection"
    var upload_exam = "Are you want to upload your exam schedule..."
    var No_Data_Found = "No Data Found..."
    var Hold_on = "Hold on!"
    var Receiver_count = "Receiver count : "
    var Yes = "Yes"
    var No = "No"
    var OK = "OK"
    var CANCEL = "CANCEL"
    var Info = "Info"
    var Clickable = "Particuler"
    var Clickable_Entire = "Entire"
    var Enter_the_Newpassword = "Enter the Newpassword"
    var Enter_the_Confirmpassword = "Enter the Confirmpassword"
    var Password_Mismatching = "The Password is Mismatching"
    var Enter_mobileNumber = "Enter the mobileNumber "
    var Enter_Otp = "Enter the Otp "
    var Text = "Text"
    var Communication = "Communication"
    var Noticeboard = "Noticeboard"
    var ScreenNameEvent = "ScreenNameEvent"
    var Event_Edit = "Event_Edit"
    var Image_Pdf = "New Image/Pdf"
    var TermsNConditionUrl = "https://gradit.voicesnap.com/Home/TermsAndConditions"
    var isDeviceTokenApiCalling: Boolean? = true
    private val REQUEST_CODE_APP_SETTINGS = 101

//    var isBioMetricEnable: Int = 0


    fun ApiAlert(activity: Activity?, msg: String?) {
        if (activity != null) {
            val dlg = AlertDialog.Builder(activity)
            dlg.setTitle("Info")
            dlg.setMessage(msg)
            dlg.setPositiveButton("OK") { dialog, which -> }
            dlg.setCancelable(false)
            dlg.create()
            dlg.show()
        }
    }

    fun ApiAlertContext(activity: Context?, msg: String?) {
        if (activity != null) {
            val dlg = AlertDialog.Builder(activity)
            dlg.setTitle("Info")
            dlg.setMessage(msg)
            dlg.setPositiveButton("OK") { dialog, which ->

                MenuAssignment = true
                MenuCourseDetails = true
                MenuExamDetails = true
                MenuNoticeBoard = true
                MenuDashboardHome = true
                MenuCircular = true
                MenuChat = true
                MenuCommunication = true
                MenuExamination = true
                MenuAttendance = true
                MenuEvents = true
                MenuFaculty = true
                MenuVideo = true
                MenuCategoryCredit = true
                MenuSemCredit = true
                MenuHallTicket = true
                MenuText = true
                MenuFeeDetails = true
                MarkAttendance = true
                AttendanceReport = true
            }
            dlg.setCancelable(false)
            dlg.create()
            dlg.show()
        }
    }

    @JvmStatic
    fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun ApiAlertFinish(activity: Activity?, msg: String?) {
        if (activity != null) {
            val dlg = AlertDialog.Builder(activity)
            dlg.setTitle("Info")
            dlg.setMessage(msg)
            dlg.setPositiveButton("OK") { dialog, which ->

                //activity.finish()
                MenuAssignment = true
                MenuCourseDetails = true
                MenuExamDetails = true
                MenuNoticeBoard = true
                MenuDashboardHome = true
                MenuCircular = true
                MenuChat = true
                MenuExamination = true
                MenuAttendance = true
                MenuEvents = true
                MenuFaculty = true
                MenuVideo = true
                MenuCategoryCredit = true
                MenuSemCredit = true
                MenuHallTicket = true
                MenuCommunication = true
                MenuText = true
                MenuFeeDetails = true
                MarkAttendance = true
                AttendanceReport = true
            }
            dlg.setCancelable(false)
            dlg.create()
            dlg.show()
        }
    }


    fun isNetworkConnected(activity: Activity): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }

    fun RequestPermission(activity: Activity?) {

        if (Priority.equals("p4") || Priority.equals("p5") || Priority.equals("p6")) {
            Log.d("testPriority", Priority)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

                Dexter.withActivity(activity).withPermissions(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.INTERNET,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else {
                Dexter.withActivity(activity).withPermissions(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET
                )
            }
        } else {
            Log.d("testPriorutyelse", Priority)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Dexter.withActivity(activity).withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else {
                Dexter.withActivity(activity).withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET
                )
            }
        }.withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    Log.d("permission", "granted")
                } else {
                    Log.d("permission", "failed")
                    activity!!.finishAffinity()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest>, token: PermissionToken
            ) {
                token.continuePermissionRequest()
            }
        }).withErrorListener { }.onSameThread().check()
    }

    fun RequestCameraPermission(activity: Activity?) {
        Dexter.withActivity(activity).withPermissions(
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {

                } else {
                    activity!!.finishAffinity()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest>, token: PermissionToken
            ) {
                token.continuePermissionRequest()
            }
        }).withErrorListener { }.onSameThread().check()
    }

    fun SetTheme(activity: Activity) {
        when (Priority) {
            "p1" -> {
                activity.setTheme(R.style.Prinipal)
            }

            "p2", "p3" -> {
                activity.setTheme(R.style.Staff)
            }

            "p4" -> {
                activity.setTheme(R.style.Student)
            }

            "p5" -> {
                activity.setTheme(R.style.Parent)
            }

            "p6" -> {
                activity.setTheme(R.style.ParentNonTeahingStaff)
            }

            "p7" -> {
                activity.setTheme(R.style.Header)
            }
        }
    }

    fun OnBackSetBottomMenuClickTrue() {

        EventStatus = "Upcoming"
        MenuCourseDetails = true
        MenuExamDetails = true
        MenuNoticeBoard = true
        MenuDashboardHome = true
        MenuCircular = true
        MenuAssignment = true
        MenuChat = true
        MenuExamination = true
        MenuAttendance = true
        MenuEvents = true
        MenuFaculty = true
        MenuVideo = true
        MenuCategoryCredit = true
        MenuSemCredit = true
        MenuHallTicket = true
        MenuCommunication = true
        MenuText = true
        MenuFeeDetails = true
        MarkAttendance = true
        AttendanceReport = true
    }

    fun OnMenuClicks(type: String) {

        if (type.equals("Home")) {
            MenuDashboardHome = false
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("Communication")) {
            MenuCommunication = false
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("Chat")) {
            MenuChat = false
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuCommunication = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("ExamList")) {
            MenuExamination = false
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuCommunication = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("Attendance")) {

            MenuAttendance = false
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("Assignment")) {
            MenuAssignment = false
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("Circular")) {
            MenuCircular = false
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("Noticeboard")) {
            MenuNoticeBoard = false
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("Events")) {
            MenuEvents = false
            MenuNoticeBoard = true
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("Faculty")) {
            MenuFaculty = false
            MenuEvents = true
            MenuNoticeBoard = true
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("Video")) {
            MenuVideo = false
            MenuEvents = true
            MenuAssignment = true
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuFaculty = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("CourseDetails")) {
            MenuCourseDetails = false
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("CategoryCredit")) {
            MenuCategoryCredit = false
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("SemCredit")) {
            MenuSemCredit = false
            MenuAssignment = true
            MenuCourseDetails = true
            MenuExamDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("ExamApp")) {
            MenuExamDetails = false
            MenuCourseDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("Hallticket")) {
            MenuExamDetails = true
            MenuCourseDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = false
            MenuCommunication = true
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("Text")) {
            MenuExamDetails = true
            MenuCourseDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = true
            MenuText = false
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true

        } else if (type.equals("Voice")) {
            MenuExamDetails = true
            MenuCourseDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = false
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("FeeDetails")) {
            MenuExamDetails = true
            MenuCourseDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = false
            MenuText = true
            MenuFeeDetails = false
            MarkAttendance = true
            AttendanceReport = true
        } else if (type.equals("Mark your attendance")) {
            MenuExamDetails = true
            MenuCourseDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = false
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = false
            AttendanceReport = true
        } else if (type.equals("Attendance report")) {
            MenuExamDetails = true
            MenuCourseDetails = true
            MenuNoticeBoard = true
            MenuDashboardHome = true
            MenuCircular = true
            MenuAssignment = true
            MenuChat = true
            MenuExamination = true
            MenuAttendance = true
            MenuEvents = true
            MenuFaculty = true
            MenuVideo = true
            MenuCategoryCredit = true
            MenuSemCredit = true
            MenuHallTicket = true
            MenuCommunication = false
            MenuText = true
            MenuFeeDetails = true
            MarkAttendance = true
            AttendanceReport = false
        }
    }

    fun Toast(activity: Activity?, msg: String?) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    fun Toast(activity: Context?, msg: String?) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}