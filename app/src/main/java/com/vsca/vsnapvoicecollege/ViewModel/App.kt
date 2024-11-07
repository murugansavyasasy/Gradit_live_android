package com.vsca.vsnapvoicecollege.ViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Model.AssignmentContent_View
import com.vsca.vsnapvoicecollege.Model.Assignment_Forward
import com.vsca.vsnapvoicecollege.Model.Assignment_Submit
import com.vsca.vsnapvoicecollege.Model.Assignment_Submittion
import com.vsca.vsnapvoicecollege.Model.Assignmentsent
import com.vsca.vsnapvoicecollege.Model.AttendanceMark
import com.vsca.vsnapvoicecollege.Model.AttendanceResponse
import com.vsca.vsnapvoicecollege.Model.Attendance_Edit
import com.vsca.vsnapvoicecollege.Model.AttendancemardkingResponse
import com.vsca.vsnapvoicecollege.Model.BlackStudent
import com.vsca.vsnapvoicecollege.Model.Chat_StaffList
import com.vsca.vsnapvoicecollege.Model.Chat_Student
import com.vsca.vsnapvoicecollege.Model.Chat_Text_model
import com.vsca.vsnapvoicecollege.Model.CollageList
import com.vsca.vsnapvoicecollege.Model.Communication_AddNewButton
import com.vsca.vsnapvoicecollege.Model.Delete_noticeboard
import com.vsca.vsnapvoicecollege.Model.Evendsenddata
import com.vsca.vsnapvoicecollege.Model.EventpicUpdate
import com.vsca.vsnapvoicecollege.Model.ExamCreation_dataclass
import com.vsca.vsnapvoicecollege.Model.ExamDelete
import com.vsca.vsnapvoicecollege.Model.ExamMarkListResponse
import com.vsca.vsnapvoicecollege.Model.ExamSubjectList
import com.vsca.vsnapvoicecollege.Model.Examlist_viewmodel
import com.vsca.vsnapvoicecollege.Model.ExampleJson2KtKotlin
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.GetAssignmentCountResponse
import com.vsca.vsnapvoicecollege.Model.GetAssignmentListResponse
import com.vsca.vsnapvoicecollege.Model.GetAssignmentViewContentResponse
import com.vsca.vsnapvoicecollege.Model.GetAttendance
import com.vsca.vsnapvoicecollege.Model.GetCategoryTypesResponse
import com.vsca.vsnapvoicecollege.Model.GetCategoryWiseCreditResponse
import com.vsca.vsnapvoicecollege.Model.GetCircularListResponse
import com.vsca.vsnapvoicecollege.Model.GetCommunicationResponse
import com.vsca.vsnapvoicecollege.Model.GetCourseDetailsResposne
import com.vsca.vsnapvoicecollege.Model.GetEventListbyTypeResponse
import com.vsca.vsnapvoicecollege.Model.GetExamApplicationResponse
import com.vsca.vsnapvoicecollege.Model.GetExamListResponse
import com.vsca.vsnapvoicecollege.Model.GetFacultyListResponse
import com.vsca.vsnapvoicecollege.Model.GetGrouplist
import com.vsca.vsnapvoicecollege.Model.GetNoticeboardResposne
import com.vsca.vsnapvoicecollege.Model.GetOverAllCountResposne
import com.vsca.vsnapvoicecollege.Model.GetProfileResponse
import com.vsca.vsnapvoicecollege.Model.GetSemesterWiseCreditALLResponse
import com.vsca.vsnapvoicecollege.Model.GetSemesterWiseCreditResponse
import com.vsca.vsnapvoicecollege.Model.GetSemesterWiseTypeResponse
import com.vsca.vsnapvoicecollege.Model.GetStaffDetailsResponse
import com.vsca.vsnapvoicecollege.Model.GetVideoListResponse
import com.vsca.vsnapvoicecollege.Model.GraditContectNumber
import com.vsca.vsnapvoicecollege.Model.Hallticket
import com.vsca.vsnapvoicecollege.Model.ImageORpdfsend
import com.vsca.vsnapvoicecollege.Model.ImageorpdfsendTuter
import com.vsca.vsnapvoicecollege.Model.LeaveHistoryResponse
import com.vsca.vsnapvoicecollege.Model.LeaveRequest
import com.vsca.vsnapvoicecollege.Model.LeaveTypeResponse
import com.vsca.vsnapvoicecollege.Model.Leave_history
import com.vsca.vsnapvoicecollege.Model.ManageLeave
import com.vsca.vsnapvoicecollege.Model.NewPassWordCreate
import com.vsca.vsnapvoicecollege.Model.NoticeBoardSMSsend
import com.vsca.vsnapvoicecollege.Model.Section_and_Subject
import com.vsca.vsnapvoicecollege.Model.SemesterAndSectionListResposne
import com.vsca.vsnapvoicecollege.Model.SenderSide_ChatModel
import com.vsca.vsnapvoicecollege.Model.SenderSide_ReplayChat
import com.vsca.vsnapvoicecollege.Model.StatusMessageResponse
import com.vsca.vsnapvoicecollege.Model.StudentAttendancelist
import com.vsca.vsnapvoicecollege.Model.StudentAttendanceview
import com.vsca.vsnapvoicecollege.Model.Unblack_student
import com.vsca.vsnapvoicecollege.Model.Verified_OTP
import com.vsca.vsnapvoicecollege.Model.VideoEntireSend
import com.vsca.vsnapvoicecollege.Model.VideoParticulerSend
import com.vsca.vsnapvoicecollege.Model.VideoRestrictionContentResponse
import com.vsca.vsnapvoicecollege.Model.VideoSendTuter
import com.vsca.vsnapvoicecollege.Model.Yearandsection
import com.vsca.vsnapvoicecollege.Model.department_course
import com.vsca.vsnapvoicecollege.Model.specificStudentdata
import com.vsca.vsnapvoicecollege.Model.staffsubject_list
import com.vsca.vsnapvoicecollege.Model.textHistory
import com.vsca.vsnapvoicecollege.Model.voicehistory
import com.vsca.vsnapvoicecollege.Repository.AppServices
import com.vsca.vsnapvoicecollege.SenderModel.GetCourseDepartmentResposne
import com.vsca.vsnapvoicecollege.SenderModel.GetDepartmentResponse
import com.vsca.vsnapvoicecollege.SenderModel.GetDivisionResponse
import com.vsca.vsnapvoicecollege.SenderModel.SenderStatusMessageData

class App(application: Application) : AndroidViewModel(application) {

    private var apiRepositories: AppServices? = null

    var Hallticket: LiveData<Hallticket?>? = null
        private set

    var courseDetailsResposneLiveData: LiveData<GetCourseDetailsResposne?>? = null
        private set
    private var ProfileDetailsResponseLiveData: LiveData<GetProfileResponse?>? = null
    var examApplicationResponseLiveData: LiveData<GetExamApplicationResponse?>? = null
        private set
    var noticeBoardResponseLiveData: LiveData<GetNoticeboardResposne?>? = null
        private set
    var circularResponseLiveData: LiveData<GetCircularListResponse?>? = null
        private set
    var assignmentListResponseLiveData: LiveData<GetAssignmentListResponse?>? = null
        private set
    var assignmentCountResponseLiveData: LiveData<GetAssignmentCountResponse?>? = null
        private set
    var assignmentViewContentResponseLiveData: LiveData<GetAssignmentViewContentResponse?>? = null
        private set


    var appreadstatusresponseLiveData: LiveData<StatusMessageResponse?>? = null
    var eventListbyTypeliveData: LiveData<GetEventListbyTypeResponse?>? = null
    var communicationLiveData: LiveData<GetCommunicationResponse?>? = null
    var communicationNew_Button: LiveData<Communication_AddNewButton?>? = null
    var OverAllMenuResponseLiveData: LiveData<GetOverAllCountResposne?>? = null
    var ExamListResponseLiveData: LiveData<GetExamListResponse?>? = null
    var ExamListResponseLiveDataReciver: LiveData<GetExamListResponse?>? = null
    var ExamMarkListLiveData: LiveData<ExamMarkListResponse?>? = null
    var VideoListLiveData: LiveData<GetVideoListResponse?>? = null
    var ContectNumber: LiveData<GraditContectNumber?>? = null
    var GetStaffDetailsLiveData: LiveData<GetStaffDetailsResponse?>? = null
    var GetStaffchat: LiveData<Chat_StaffList?>? = null
    var SemesterSectionLiveData: LiveData<SemesterAndSectionListResposne?>? = null
    var FacultyLiveData: LiveData<GetFacultyListResponse?>? = null
    var FacultyLiveDatastaff: LiveData<GetFacultyListResponse?>? = null
    var CategoryTypeLiveData: LiveData<GetCategoryTypesResponse?>? = null
    var CategoryWiseCreditLiveData: LiveData<GetCategoryWiseCreditResponse?>? = null
    var SemesterTypeLiveData: LiveData<GetSemesterWiseTypeResponse?>? = null
    var SemesterWiseCreditLiveData: LiveData<GetSemesterWiseCreditResponse?>? = null
    var SemesterWiseCreditAllLiveData: LiveData<GetSemesterWiseCreditALLResponse?>? = null
    var AttendanceDatesLiveData: LiveData<AttendanceResponse?>? = null
    var GetstudentlistAttendance: LiveData<StudentAttendancelist?>? = null
    var LeaveHistoryLiveData: LiveData<LeaveHistoryResponse?>? = null
    var LeaveTypeLiveData: LiveData<LeaveTypeResponse?>? = null
    var ChangePasswordLiveData: LiveData<StatusMessageResponse?>? = null
    var AdvertisementLiveData: LiveData<GetAdvertisementResponse?>? = null
    var UpdateDeviceTokenMutableLiveData: LiveData<StatusMessageResponse?>? = null
    var VideoRestrictContentMutableLiveData: LiveData<VideoRestrictionContentResponse?>? = null
    var GetDivisionMutableLiveData: LiveData<GetDivisionResponse?>? = null
    var GetDepartmentMutableLiveData: LiveData<GetDepartmentResponse?>? = null
    var GetCourseDepartmentMutableLiveData: LiveData<GetCourseDepartmentResposne?>? = null
    var SendSMSToEntireCollegeLiveData: LiveData<SenderStatusMessageData?>? = null
    var SendVoiceToEntireCollegeLiveData: LiveData<SenderStatusMessageData?>? = null
    var SendVoiceToParticulerHistory: LiveData<SenderStatusMessageData?>? = null
    var SendVoicEnrirehistory: LiveData<SenderStatusMessageData?>? = null
    var SendSMSToEntireCollegetutoeandsubjectLiveData: LiveData<SenderStatusMessageData?>? = null
    var SendSMStoParticularMutableData: LiveData<SenderStatusMessageData?>? = null
    var SendSMStoParticularMutableDataTuter: LiveData<SenderStatusMessageData?>? = null
    var NoticeBoardSendSMStoParticularMutableData: LiveData<NoticeBoardSMSsend?>? = null
    var NoticeBoardSendSMStoParticularMutableDataTuter: LiveData<NoticeBoardSMSsend?>? = null
    var Eventsenddata: LiveData<Evendsenddata?>? = null
    var EventsenddataTuter: LiveData<Evendsenddata?>? = null
    var GetGrouplist: LiveData<GetGrouplist?>? = null
    var Getsubjectdata: LiveData<staffsubject_list?>? = null
    var Getattendance: LiveData<GetAttendance?>? = null
    var StudentAttendance: LiveData<StudentAttendanceview?>? = null
    var GetsubjectdatahOD: LiveData<staffsubject_list?>? = null
    var Gettuter: LiveData<staffsubject_list?>? = null
    var Getspecificstudenttutot: LiveData<specificStudentdata?>? = null
    var Getspecificstudentsubject: LiveData<specificStudentdata?>? = null
    var GetspecificstudentAttendance: LiveData<specificStudentdata?>? = null
    var GetCoursesByDepartmenthod: LiveData<department_course?>? = null
    var GetCoursesByDepartmenthodhod: LiveData<Yearandsection?>? = null
    var GetYearandSection: LiveData<Yearandsection?>? = null
    var ImageorPdf: LiveData<ImageORpdfsend?>? = null
    var Imageorpdfparticuler: LiveData<ImageORpdfsend?>? = null
    var Assignment: LiveData<Assignmentsent?>? = null
    var ForwardText: LiveData<Assignmentsent?>? = null
    var Attendancetaking: LiveData<AttendancemardkingResponse?>? = null
    var Manageleave: LiveData<ManageLeave?>? = null
    var Leavehistory: LiveData<Leave_history?>? = null
    var LeaveRequest: LiveData<LeaveRequest?>? = null
    var Examsectionlist: LiveData<Section_and_Subject?>? = null
    var Examcreationdata: LiveData<ExamCreation_dataclass?>? = null
    var ExamcreationEditData: LiveData<ExamCreation_dataclass?>? = null
    var ExamSectiondelete: LiveData<ExamCreation_dataclass?>? = null
    var Examview: LiveData<Examlist_viewmodel?>? = null
    var ExamviewSujectList: LiveData<ExamSubjectList?>? = null
    var Examdelete: LiveData<ExamDelete?>? = null
    var VideosendEntire: LiveData<VideoEntireSend?>? = null
    var VideoParticulerSend: LiveData<VideoParticulerSend?>? = null
    var DeleteNoticeboard: LiveData<Delete_noticeboard?>? = null
    var MarkAttendance: LiveData<AttendanceMark?>? = null
    var Eventpicupdate: LiveData<EventpicUpdate?>? = null
    var ExamEditORdelete: LiveData<Examlist_viewmodel?>? = null
    var Assign_forward: LiveData<Assignment_Forward?>? = null
    var ChatList: LiveData<Chat_Text_model?>? = null
    var Chatstudentlist: LiveData<Chat_Student?>? = null
    var AssignmentSubmited: LiveData<Assignment_Submit?>? = null
    var Assignmentsubmittion: LiveData<Assignment_Submittion?>? = null
    var AssignmentsubmittionforStudent: LiveData<Assignment_Submittion?>? = null
    var facultyListRecevier: LiveData<GetFacultyListResponse?>? = null
    var AttendanceEdit: LiveData<Attendance_Edit?>? = null
    var Assignmentview: LiveData<AssignmentContent_View?>? = null
    var ChatSenderside: LiveData<SenderSide_ChatModel?>? = null
    var ChatStaff: LiveData<SenderSide_ReplayChat?>? = null
    var BlackStudent: LiveData<BlackStudent?>? = null
    var unblackStudent: LiveData<Unblack_student?>? = null

    //var AttendanceEdit: LiveData<Attendance_EditX?>? = null
    var SendVideoParticulerTuter: LiveData<VideoSendTuter?>? = null
    var GetOtpNew: LiveData<ExampleJson2KtKotlin?>? = null
    var VerifyOtp: LiveData<Verified_OTP?>? = null
    var CrearePassword: LiveData<NewPassWordCreate?>? = null
    var _PdfandImagesend: LiveData<ImageorpdfsendTuter?>? = null
    var Text_History: LiveData<textHistory?>? = null
    var _voiceHistory: LiveData<voicehistory?>? = null
    var CollageList: LiveData<CollageList?>? = null


    fun init() {
        apiRepositories = AppServices()

        Hallticket = apiRepositories!!.hallticketdataget

        courseDetailsResposneLiveData = apiRepositories!!.courseDetailsResposneLiveData
        examApplicationResponseLiveData = apiRepositories!!.examApplicationResponseLiveData
        ProfileDetailsResponseLiveData = apiRepositories!!.profileResponseLiveData
        noticeBoardResponseLiveData = apiRepositories!!.noticeboardLiveData
        DeleteNoticeboard = apiRepositories!!.Noticeboarddelete
        circularResponseLiveData = apiRepositories!!.circularListResponseLiveData
        assignmentListResponseLiveData = apiRepositories!!.assignmentListResponseLiveData
        assignmentCountResponseLiveData = apiRepositories!!.assignmentCountResponseLiveData
        assignmentViewContentResponseLiveData =
            apiRepositories!!.assignmentViewContentResponseLiveData
        appreadstatusresponseLiveData = apiRepositories!!.appreadstatusLiveData
        OverAllMenuResponseLiveData = apiRepositories!!.OverAllCountMenuLiveData
        eventListbyTypeliveData = apiRepositories!!.getEventListLiveData
        communicationLiveData = apiRepositories!!.getCommunicationLiveData
        communicationLiveData = apiRepositories!!.getCommunicationLiveTextData
        communicationNew_Button = apiRepositories!!.getCommunicationButton_VisibleOrNot
        ExamListResponseLiveData = apiRepositories!!.getExamListLiveData
        ExamListResponseLiveDataReciver = apiRepositories!!.ExamlistReciver
        ExamMarkListLiveData = apiRepositories!!.getExamMarkListLiveData
        VideoListLiveData = apiRepositories!!.getVideoListMutableLiveData
        ContectNumber = apiRepositories!!.ContectNumberGradit
        GetStaffDetailsLiveData = apiRepositories!!.getStaffListMutableLiveData

        GetStaffchat = apiRepositories!!.getStaffcatdata
        SemesterSectionLiveData = apiRepositories!!.getSemesterSectionListLiveData
        FacultyLiveData = apiRepositories!!.getFacultyReceiverLiveData
        FacultyLiveDatastaff = apiRepositories!!.getFacultyReceiverLiveDatastaff
        CategoryTypeLiveData = apiRepositories!!.getCategoryTypeLiveData
        CategoryWiseCreditLiveData = apiRepositories!!.getCategoryWiseCreditLiveData
        SemesterTypeLiveData = apiRepositories!!.getSemesterTypeLiveData
        SemesterWiseCreditLiveData = apiRepositories!!.getSemesterwiseCreditsLiveData
        SemesterWiseCreditAllLiveData = apiRepositories!!.getSemesterwiseCreditsAllLiveData
        AttendanceDatesLiveData = apiRepositories!!.getAttendanceLiveData
        GetstudentlistAttendance = apiRepositories!!.AttendancelistStudent
        LeaveHistoryLiveData = apiRepositories!!.getLeaveHistoryLiveData
        LeaveTypeLiveData = apiRepositories!!.getLeaveTypeLiveData
        ChangePasswordLiveData = apiRepositories!!.getChangePawwordLiveData
        AdvertisementLiveData = apiRepositories!!.getAdvertisementMutableData
        UpdateDeviceTokenMutableLiveData = apiRepositories!!.UpdateDeviceTokenMutableLiveData
        VideoRestrictContentMutableLiveData = apiRepositories!!.VideoRestrictionMutableLiveData

        GetDivisionMutableLiveData = apiRepositories!!.GetDivisionMutableLiveData
        GetDepartmentMutableLiveData = apiRepositories!!.GetDepartmentMutableLiveData
        GetCourseDepartmentMutableLiveData = apiRepositories!!.GetCourseMutableLiveData
        SendSMSToEntireCollegeLiveData = apiRepositories!!.SendSMStoEntireCollegeMutableData
        SendSMSToEntireCollegetutoeandsubjectLiveData =
            apiRepositories!!.SendSMStoEntireCollegeMutableData
        SendVoiceToEntireCollegeLiveData = apiRepositories!!.SendVoiceEntire
        SendVoiceToParticulerHistory = apiRepositories!!.SendVoiceHistoryParticuler
        SendVoicEnrirehistory = apiRepositories!!.SendVoiceHistoryEntire
        SendSMStoParticularMutableData = apiRepositories!!.SendSMStoParticularMutableData
        NoticeBoardSendSMStoParticularMutableData =
            apiRepositories!!.NoticeBoardSendSMStoParticularMutableData
        NoticeBoardSendSMStoParticularMutableDataTuter =
            apiRepositories!!.NoticeBoardSendSMStoParticularMutableDataTuter
        SendSMStoParticularMutableDataTuter =
            apiRepositories!!.SendSMSToEntireCollegetutoeandsubjectLiveData
        Eventsenddata = apiRepositories!!.Eventsenddatasend
        EventsenddataTuter = apiRepositories!!.EventsenddatasendTuter
        GetGrouplist = apiRepositories!!.getgroupdata
        Getsubjectdata = apiRepositories!!.getsubjectdata
        GetsubjectdatahOD = apiRepositories!!.getsubjectdata
        Getattendance = apiRepositories!!.AttendanceGetting
        StudentAttendance = apiRepositories!!.StudentAttendanceDetails
        Gettuter = apiRepositories!!.gettuterdata
        Getspecificstudenttutot = apiRepositories!!.getSpecificstudenttutor
        Getspecificstudentsubject = apiRepositories!!.getSpecificstudentsubject
        GetspecificstudentAttendance = apiRepositories!!.SpecificStudentAttendanceName
        GetCoursesByDepartmenthod = apiRepositories!!.getcoursedepartment
        GetCoursesByDepartmenthodhod = apiRepositories!!.getyearandsection
        GetYearandSection = apiRepositories!!.GetYearandSection
        ImageorPdf = apiRepositories!!.ImageOrPdfsend
        Imageorpdfparticuler = apiRepositories!!.ImageOrPdfsendparticuler
        Assignment = apiRepositories!!.Assignment
        ForwardText = apiRepositories!!.AssignmentTextForward
        Attendancetaking = apiRepositories!!.Attendancemarking
        Manageleave = apiRepositories!!.Manageleavesend
        Leavehistory = apiRepositories!!.leavehistory
        //   LeaveRequest=apiRepositories!!.Requestleave
        Examsectionlist = apiRepositories!!.ExamSectionandsubject
        Examcreationdata = apiRepositories!!.Examcreationdata
        ExamcreationEditData = apiRepositories!!.ExamEdit
        ExamSectiondelete = apiRepositories!!.ExamSectionDelete

        Examview = apiRepositories!!.Examview
        ExamviewSujectList = apiRepositories!!.ExamviewSubject
        Examdelete = apiRepositories!!.Examdelete
        VideosendEntire = apiRepositories!!.Videosendentire
        MarkAttendance = apiRepositories!!.TakeAttendance
        VideoParticulerSend = apiRepositories!!.Videosendparticuler
        Eventpicupdate = apiRepositories!!.Eventpictureupdate
        ExamEditORdelete = apiRepositories!!.ExamEditordelete
        Assign_forward = apiRepositories!!.Assignmemntforward
        ChatList = apiRepositories!!.Chatlist
        Chatstudentlist = apiRepositories!!.chatstudentlist
        ChatSenderside = apiRepositories!!.sendersidechat
        AssignmentSubmited = apiRepositories!!.Assignment_submited
        Assignmentsubmittion = apiRepositories!!.AssignmentsubmittionSender
        AssignmentsubmittionforStudent = apiRepositories!!.AssignmentsubmittionforStudent
        facultyListRecevier = apiRepositories!!.FacultyRecevierList
        AttendanceEdit = apiRepositories!!.AttendanceEditList
        Assignmentview = apiRepositories!!.Assignmentcontentview
        ChatStaff = apiRepositories!!.SenderSideReplay
        BlackStudent = apiRepositories!!.BlackStudentparticuler
        unblackStudent = apiRepositories!!.unBlackStudent
        SendVideoParticulerTuter = apiRepositories!!.VideoTuterSend
        GetOtpNew = apiRepositories!!.GetOtpNew
        VerifyOtp = apiRepositories!!.OtpVerifed
        CrearePassword = apiRepositories!!.Passwordcreate
        _PdfandImagesend = apiRepositories!!.Tuterimageorpdfsend
        Text_History = apiRepositories!!.text_history
        _voiceHistory = apiRepositories!!.voicehistory_
        CollageList = apiRepositories!!.CollageListdataHeader
    }

    fun getCourseDetails(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetCourseDetails(jsonObject, activity!!)
    }

    fun getExamApplication(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetExamApplicationDetails(jsonObject, activity!!)
    }

    fun getStudentProfile(memberid: Int, activity: Activity?) {
        apiRepositories!!.GetStudentProfile(memberid, activity!!)
    }

    fun getprofileResponseLiveData(): LiveData<GetProfileResponse?>? {
        return ProfileDetailsResponseLiveData
    }

    fun getNoticeboardList(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetNoticeboradList(jsonObject, activity!!)
    }

    fun getCircularList(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetCircularListbyType(jsonObject, activity!!)
    }

    fun getAssignmentListbyType(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetAssignmentListbyType(jsonObject, activity!!)
    }

    fun getAssignmentMemberCount(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetAssignmentmemberCount(jsonObject, activity!!)
    }

    fun getAssignmentViewContent(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetAssignmentViewContent(jsonObject, activity!!)
    }

    fun getAppreadStatus(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetAppreadStatus(jsonObject, activity!!)
    }

    fun getAppreadStatusContext(jsonObject: JsonObject?, activity: Context?) {
        apiRepositories!!.GetAppreadStatusContext(jsonObject, activity!!)
    }

    fun getOverAllMenuCount(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetOVerAllCount(jsonObject, activity!!)
    }

    fun getEventListbyType(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetEventListBytType(jsonObject, activity!!)
    }

    fun ExamEditANDdELETE(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.ExamEditorDeleteSection(jsonObject, activity!!)
    }


    fun getCommunicationListbyType(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetCommunicationList(jsonObject, activity!!)
    }

    fun getCommunicationListTextbyType(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetCommunicationTextList(jsonObject, activity!!)
    }


    fun CommunicationNew_Button(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Communication_NewButtonVisible_Not(jsonObject, activity!!)
    }

    fun getExamListbyType(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetExamListReceiver(jsonObject, activity!!)
    }

    fun getExamListbyTypeReciver(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetExamListReceiverside(jsonObject, activity!!)
    }

    fun DeleteNoticeBoard(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.DeleteNoticeBoard(jsonObject, activity!!)
    }


    fun getStudentExamMarklist(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.ExamMarkList(jsonObject, activity!!)
    }

    fun getVideoList(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetVideoList(jsonObject, activity!!)
    }

    fun NumberDetails(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.NumberDetails(jsonObject, activity!!)
    }

    fun getSemesterAndSection(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.SemesterSectionListForApp(jsonObject, activity!!)
    }

    fun getFaculty(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetFacultyList(jsonObject, activity!!)
    }

    fun getFacultystaff(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetFacultyListStaff(jsonObject, activity!!)
    }

    fun getCategoryType(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetCategoryType(jsonObject, activity!!)
    }

    fun getCategoryWiseCredit(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetCategoryCreditwise(jsonObject, activity!!)
    }

    fun getSmesterType(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetSemesterType(jsonObject, activity!!)
    }

    fun getSemesterWiseCredit(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetSemesterWiseCredits(jsonObject, activity!!)
    }

    fun getSemesterWiseCreditAll(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetSemesterWiseCreditsAll(jsonObject, activity!!)
    }

    fun getAttendanceReceiver(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetAttendanceForParent(jsonObject, activity!!)
    }

    fun attendanceListforStudent(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetStudentAttendancelist(jsonObject, activity!!)
    }

    fun getleaveHistory(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetLeaveHistoryParent(jsonObject, activity!!)
    }

    fun getLeaveType(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetLeaveType(jsonObject, activity!!)
    }

    fun getChangePassword(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.ChangePassword(jsonObject, activity!!)
    }

    fun getStaffDetailsForApp(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetStaffDetails(jsonObject, activity!!)
    }

    fun StaffClassesforChat(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.StaffClassesforChat(jsonObject, activity!!)
    }

    fun getAdforCollege(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.getAddFoCollege(jsonObject, activity!!)
    }

    fun UpdateDeviceToken(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.UpdateDeviceToken(jsonObject, activity!!)
    }

    fun VideoRestrictionContent(activity: Activity?) {
        apiRepositories!!.VideoRestriction(activity!!)
    }


    fun getDivision(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetDivisions(jsonObject, activity!!)
    }

    fun getDepartment(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetDepartmentData(jsonObject, activity!!)
    }


    fun getCourseDepartment(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetCoursesByDepartment(jsonObject, activity!!)
    }

    fun SendSmsToEntireCollege(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.SendSMSToEntireCollege(jsonObject, activity!!)
    }

    fun SendVoiceToEntireCollege(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.SendVoiceToEntireCollege(jsonObject, activity!!)
    }

    fun SendVoiceToParticulerHistory(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.SendVoiceToParticularTypeFromHistory(jsonObject, activity!!)
    }

    fun SendVoiceToEntireHistory(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.SendVoiceEntireHistory(jsonObject, activity!!)
    }


    fun SendSmsToEntiretutorandsubjectCollege(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.SendSMSToEntiretotorandsubjectCollege(jsonObject, activity!!)
    }

    fun SendSmsToParticularType(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.SendSMStoParticularType(jsonObject, activity!!)
    }

    fun getGroup(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetGroup(jsonObject, activity!!)
    }

    fun getsubject(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Getsubject(jsonObject, activity!!)
    }

    fun AttendanceGettingStaff(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GettingAttendance(jsonObject, activity!!)
    }

    fun DetailsforspecificstudentAttendance(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.specificstudentAttendance(jsonObject, activity!!)
    }

    fun gettuter(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Gettuter(jsonObject, activity!!)
    }

    fun getspecificstudentdata(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Getspecificstudentdata(jsonObject, activity!!)
    }

    fun getspecificstudentdatasubject(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Getspecificstudentdatasubject(jsonObject, activity!!)
    }

    fun getspecificstudentdataAttendance(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetspecificstudentdataAttendance(jsonObject, activity!!)
    }

    fun getcoursedepartment(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Getdepartmentcourse(jsonObject, activity!!)
    }

    fun getyearsndsection(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Getyearandsection(jsonObject, activity!!)
    }

    fun getyearsndsectionList(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetyearandsectionList(jsonObject, activity!!)
    }


    fun NoticeBoardsmssending(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.NoticeBoardSendSMStoParticularType(jsonObject, activity!!)
    }


    fun NoticeBoardsmssendingTuter(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.NoticeBoardSendSMStoParticularTypeTuter(jsonObject, activity!!)
    }

    fun ImageorPdf(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.ImageorPdfsend(jsonObject, activity!!)
    }

    fun ImageorPdfparticuler(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.ImageorPdfsendparticuler(jsonObject, activity!!)
    }

    fun Eventsend(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.EventsendData(jsonObject, activity!!)
    }

    fun EventsendTuter(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.EventsendDataTuter(jsonObject, activity!!)
    }

    fun Assignmentsend(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Assignmentsenddata(jsonObject, activity!!)
    }

    fun AssignmentsendForwardText(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.AssignmentForwordText(jsonObject, activity!!)
    }


    fun AttendanceTakedata(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.AttendanceMarkingdata(jsonObject, activity!!)
    }

    fun Manageleave(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.ManageLeaveapplication(jsonObject, activity!!)
    }

    fun Leavehistortprinciple(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Leavehistortprinciple(jsonObject, activity!!)
    }

    fun Examsectionandsubject(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.examsectionandsubject(jsonObject, activity!!)
    }

    fun Examcreation(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Examcreation(jsonObject, activity!!)
    }

    fun ExamEdit(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.ExamEdit(jsonObject, activity!!)
    }

    fun ExamDeleteSection(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.ExamSectionDelete(jsonObject, activity!!)
    }

    fun Examview(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Examview(jsonObject, activity!!)
    }

    fun ExamviewSubjectList(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.ExamviewSubjecr(jsonObject, activity!!)
    }

    fun VideoEntireSend(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.VideoEntireSend(jsonObject, activity!!)

    }

    fun VideoParticulerSend(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.VideoParticulerSend(jsonObject, activity!!)

    }

    fun MarkAttendance(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.TakeAttendance(jsonObject, activity!!)
    }

    fun getHallticket(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetHallticketdata(jsonObject, activity!!)
    }


    fun ExamDelete(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Examdeletedata(jsonObject, activity!!)

    }

//    fun LeaveRequest(jsonObject: JsonObject?, activity: Activity?) {
//        apiRepositories!!.LeaveRequest(jsonObject, activity!!)
//    }

    fun Eventimageupdate(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.EventpicUpdate(jsonObject, activity!!)

    }

    fun Assignmentfor(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.AssignmentForward(jsonObject, activity!!)
    }

    fun chatList(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Chatlistdata(jsonObject, activity!!)
    }

    fun ChatStudent(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.ChatStudent(jsonObject, activity!!)
    }


    fun ChatStaff(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.ChatStaff(jsonObject, activity!!)
    }

    fun ChatSenderSide(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.SendersideChat(jsonObject, activity!!)
    }


    fun Assignmentsubmited(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Assignmentsubmit(jsonObject, activity!!)
    }

    fun Assignmentsubmitedsender(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.AssignmentsubmitSender(jsonObject, activity!!)
    }

    fun AssignmentsubmitedforStudent(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetSubmittedAssignmentForStudents(jsonObject, activity!!)
    }

    fun GetFacultylistSenderside(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetFacultyListReceiver(jsonObject, activity!!)
    }

//    fun AttendanceCheck(jsonObject: JsonObject?, activity: Activity?) {
//        apiRepositories!!.AttendanceEdit(jsonObject, activity!!)
//    }

    fun Attendance_Edit(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.AttendanceEdit(jsonObject, activity!!)
    }

    fun AssignmentContent(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.AssignmentcontentView(jsonObject, activity!!)
    }

    fun StudentBlack(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.BlackStudent(jsonObject, activity!!)
    }

    fun StudentUnBlack(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.UnBlackStudent(jsonObject, activity!!)
    }

    fun VideoSendtuter(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.VideoSendTuter(jsonObject, activity!!)
    }

    fun GetOtp(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.GetOpt(jsonObject, activity!!)
    }

    fun OtpVerified(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.VerifirdOtp(jsonObject, activity!!)
    }

    fun CreatepasswordNew(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.CreateNewPassword(jsonObject, activity!!)
    }

    fun Tuterimageorpdfsend(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Tuterimageorpdfsend(jsonObject, activity!!)
    }

    fun textHistoryData(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Texthistory(jsonObject, activity!!)
    }

    fun _VoiceHistoryData(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.Voicehistory(jsonObject, activity!!)
    }

    fun CollageHeaderSend(jsonObject: JsonObject?, activity: Activity?) {
        apiRepositories!!.CollageListdata(jsonObject, activity!!)
    }
}