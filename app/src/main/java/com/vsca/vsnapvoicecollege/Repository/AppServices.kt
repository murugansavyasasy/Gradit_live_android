package com.vsca.vsnapvoicecollege.Repository

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.SenderModel.GetCourseDepartmentResposne
import com.vsca.vsnapvoicecollege.SenderModel.GetDepartmentResponse
import com.vsca.vsnapvoicecollege.SenderModel.GetDivisionResponse
import com.vsca.vsnapvoicecollege.SenderModel.SenderStatusMessageData
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppServices {
    var progressDialog: ProgressDialog? = null
    var client_app: RestClient
    var GetCourseDetailsMutableData: MutableLiveData<GetCourseDetailsResposne?>
    var GetProfileDetailsMutableData: MutableLiveData<GetProfileResponse?>
    var GetExamDetailsMutableData: MutableLiveData<GetExamApplicationResponse?>
    var GetNoticeboardMutableLiveData: MutableLiveData<GetNoticeboardResposne?>
    var Deletenoticeboard: MutableLiveData<Delete_noticeboard?>
    var GetCircularMutableLiveData: MutableLiveData<GetCircularListResponse?>
    var GetAssignmentMutableLiveData: MutableLiveData<GetAssignmentListResponse?>
    var GetAssignmentCountMutableLivedata: MutableLiveData<GetAssignmentCountResponse?>
    var GetAssignmentViewContentLivedata: MutableLiveData<GetAssignmentViewContentResponse?>
    var AppReadStatusLiveData: MutableLiveData<StatusMessageResponse?>
    var ChangePasswordLiveData: MutableLiveData<StatusMessageResponse?>
    var GetOverAllMenuCountMutableLiveData: MutableLiveData<GetOverAllCountResposne?>
    var GetEventListMutableData: MutableLiveData<GetEventListbyTypeResponse?>
    var GetCommunicationMutableLiveData: MutableLiveData<GetCommunicationResponse?>
    lateinit var CommunicationNew_Button: MutableLiveData<Communication_AddNewButton?>
    var GetFacultyRecieverMutableLiveData: MutableLiveData<GetFacultyListResponse?>
    var GetFacultyListSender: MutableLiveData<GetFacultyListResponse?>
    var SemesterSectionMutableLiveData: MutableLiveData<SemesterAndSectionListResposne?>
    var ExamListMutableLiveData: MutableLiveData<GetExamListResponse?>
    var ExamMarkListMutableLiveData: MutableLiveData<ExamMarkListResponse?>
    var VideoListMutableLiveData: MutableLiveData<GetVideoListResponse?>
    var GraditContectNumber: MutableLiveData<GraditContectNumber?>
    var StaffForAppListMutableLiveData: MutableLiveData<GetStaffDetailsResponse?>
    var Staffchatlivedata: MutableLiveData<Chat_StaffList?>
    var CategoryTypeLiveData: MutableLiveData<GetCategoryTypesResponse?>
    var CategoryWiseCreditLiveData: MutableLiveData<GetCategoryWiseCreditResponse?>
    var SemesterTypeLiveData: MutableLiveData<GetSemesterWiseTypeResponse?>
    var SemesterWiseCreditLiveData: MutableLiveData<GetSemesterWiseCreditResponse?>
    var SemesterWiseCreditAllLiveData: MutableLiveData<GetSemesterWiseCreditALLResponse?>
    var AttendanceDatesiveData: MutableLiveData<AttendanceResponse?>
    lateinit var StudentlistAttendance: MutableLiveData<StudentAttendancelist?>
    var LeaveHistoryLiveData: MutableLiveData<LeaveHistoryResponse?>
    var LeaveTypeLiveData: MutableLiveData<LeaveTypeResponse?>
    var AdvertisementLiveData: MutableLiveData<GetAdvertisementResponse?>
    var UpdateDeviceTokenLiveData: MutableLiveData<StatusMessageResponse?>
    var VideoRestrictionLiveData: MutableLiveData<VideoRestrictionContentResponse?>

    //Recipient
    var GetDivisionLiveData: MutableLiveData<GetDivisionResponse?>
    var GetDepartmentLiveData: MutableLiveData<GetDepartmentResponse?>
    var GetCourseLiveData: MutableLiveData<GetCourseDepartmentResposne?>
    var SMSEntireCollegeLiveData: MutableLiveData<SenderStatusMessageData?>
    var VoiceEntireCollegeLiveData: MutableLiveData<SenderStatusMessageData?>
    var VoiceParticulerHistory: MutableLiveData<SenderStatusMessageData?>
    var VoiceEntireHistory: MutableLiveData<SenderStatusMessageData?>

    var SMSEntireCollegetutorandsubjectLiveData: MutableLiveData<SenderStatusMessageData?>
    var SMSParticularTypeLiveData: MutableLiveData<SenderStatusMessageData?>
    var Noticeboardsmssend: MutableLiveData<NoticeBoardSMSsend>
    var Eventsenddata: MutableLiveData<Evendsenddata>
    var Groupdata: MutableLiveData<GetGrouplist?>
    var Subjectpdata: MutableLiveData<staffsubject_list?>
    var GetAttendanceForStaff: MutableLiveData<GetAttendance?>
    var StudentAttendanceview: MutableLiveData<StudentAttendanceview?>
    var tuterdata: MutableLiveData<staffsubject_list?>
    var tuterspecificstudent: MutableLiveData<specificStudentdata?>
    var SpecificStudentAttendance: MutableLiveData<specificStudentdata?>
    var sublectspecificstudent: MutableLiveData<specificStudentdata?>
    var coursedepartment: MutableLiveData<department_course?>
    var Yearandsection: MutableLiveData<Yearandsection?>
    var ImageorPdf: MutableLiveData<ImageORpdfsend>
    var imageorpdfparticuler: MutableLiveData<ImageORpdfsend>
    var Assignmentdata: MutableLiveData<Assignmentsent>
    var AssignmentdataForwardText: MutableLiveData<Assignmentsent>

    var Attendancemark: MutableLiveData<AttendancemardkingResponse>
    var Manageleavesentdata: MutableLiveData<ManageLeave>
    var Leavehistoryprinciple: MutableLiveData<Leave_history>
    var LeaveRequest: MutableLiveData<LeaveRequest>
    var sectionandsubject: MutableLiveData<Section_and_Subject?>
    var Examcreation: MutableLiveData<ExamCreation_dataclass?>
    lateinit var ExamCreationEdit: MutableLiveData<ExamCreation_dataclass?>

    var ExamDeleteSection: MutableLiveData<ExamCreation_dataclass?>
    var Examviewmodel: MutableLiveData<Examlist_viewmodel?>
    var ExamviewmodelSubjectList: MutableLiveData<ExamSubjectList?>
    var Examdeleteapi: MutableLiveData<ExamDelete?>
    var MarkAttendance: MutableLiveData<AttendanceMark?>
    var VideosendEntire: MutableLiveData<VideoEntireSend?>
    var VideoParticulersend: MutableLiveData<VideoParticulerSend?>
    var Eventimageupdate: MutableLiveData<EventpicUpdate?>
    var ExamEditSection: MutableLiveData<Examlist_viewmodel?>
    var Assignmentforworddata: MutableLiveData<Assignment_Forward?>
    var Chatlistdata: MutableLiveData<Chat_Text_model?>
    var ChatStudent: MutableLiveData<Chat_Student?>
    var Sendersidechat: MutableLiveData<SenderSide_ReplayChat?>
    var SenderChat: MutableLiveData<SenderSide_ChatModel?>
    var Assignmentsubmit: MutableLiveData<Assignment_Submit?>
    var Assignmentsendersubmittion: MutableLiveData<Assignment_Submittion?>
    var StuentforAssignmensubmitted: MutableLiveData<Assignment_Submittion?>
    var AttendanceEdit: MutableLiveData<Attendance_Edit?>
    var AssignmentContent_View: MutableLiveData<AssignmentContent_View?>
    var BlackStudent: MutableLiveData<BlackStudent?>
    var UnBlackStudent: MutableLiveData<Unblack_student?>
    var Videosendtuter: MutableLiveData<VideoSendTuter?>
    var GetOtp: MutableLiveData<ExampleJson2KtKotlin?>
    var OtpVerified: MutableLiveData<Verified_OTP?>
    var _newpassword: MutableLiveData<NewPassWordCreate?>
    var Pdf_andImagesendTuter: MutableLiveData<ImageorpdfsendTuter?>
    var TextHistory_: MutableLiveData<textHistory?>
    var VoiceHistory_: MutableLiveData<voicehistory?>
    var CollageListHeaderSent: MutableLiveData<CollageList?>
    var GetHallticket: MutableLiveData<Hallticket?>


    init {
        client_app = RestClient()
        GetCourseDetailsMutableData = MutableLiveData()
        GetProfileDetailsMutableData = MutableLiveData()
        GetExamDetailsMutableData = MutableLiveData()
        GetNoticeboardMutableLiveData = MutableLiveData()
        Deletenoticeboard = MutableLiveData()
        GetCircularMutableLiveData = MutableLiveData()
        GetAssignmentMutableLiveData = MutableLiveData()
        GetAssignmentCountMutableLivedata = MutableLiveData()
        GetAssignmentViewContentLivedata = MutableLiveData()
        AppReadStatusLiveData = MutableLiveData()
        GetEventListMutableData = MutableLiveData()
        GetCommunicationMutableLiveData = MutableLiveData()
        CommunicationNew_Button = MutableLiveData()
        GetFacultyRecieverMutableLiveData = MutableLiveData()
        GetFacultyListSender = MutableLiveData()
        SemesterSectionMutableLiveData = MutableLiveData()
        GetOverAllMenuCountMutableLiveData = MutableLiveData()
        ExamListMutableLiveData = MutableLiveData()
        ExamMarkListMutableLiveData = MutableLiveData()
        VideoListMutableLiveData = MutableLiveData()
        GraditContectNumber = MutableLiveData()
        CategoryTypeLiveData = MutableLiveData()
        CategoryWiseCreditLiveData = MutableLiveData()
        SemesterTypeLiveData = MutableLiveData()
        SemesterWiseCreditLiveData = MutableLiveData()
        SemesterWiseCreditAllLiveData = MutableLiveData()
        AttendanceDatesiveData = MutableLiveData()
        StudentlistAttendance = MutableLiveData()
        LeaveHistoryLiveData = MutableLiveData()
        LeaveTypeLiveData = MutableLiveData()
        ChangePasswordLiveData = MutableLiveData()
        StaffForAppListMutableLiveData = MutableLiveData()
        Staffchatlivedata = MutableLiveData()
        AdvertisementLiveData = MutableLiveData()
        UpdateDeviceTokenLiveData = MutableLiveData()
        VideoRestrictionLiveData = MutableLiveData()
        GetDivisionLiveData = MutableLiveData()
        GetDepartmentLiveData = MutableLiveData()
        GetCourseLiveData = MutableLiveData()
        SMSEntireCollegeLiveData = MutableLiveData()
        VoiceEntireCollegeLiveData = MutableLiveData()
        VoiceParticulerHistory = MutableLiveData()
        VoiceEntireHistory = MutableLiveData()
        SMSParticularTypeLiveData = MutableLiveData()
        Groupdata = MutableLiveData()
        Subjectpdata = MutableLiveData()
        GetAttendanceForStaff = MutableLiveData()
        StudentAttendanceview = MutableLiveData()
        tuterdata = MutableLiveData()
        tuterspecificstudent = MutableLiveData()
        SpecificStudentAttendance = MutableLiveData()
        sublectspecificstudent = MutableLiveData()
        coursedepartment = MutableLiveData()
        Yearandsection = MutableLiveData()
        ImageorPdf = MutableLiveData()
        imageorpdfparticuler = MutableLiveData()
        Noticeboardsmssend = MutableLiveData()
        Eventsenddata = MutableLiveData()
        SMSEntireCollegetutorandsubjectLiveData = MutableLiveData()
        Assignmentdata = MutableLiveData()
        AssignmentdataForwardText = MutableLiveData()
        Attendancemark = MutableLiveData()
        Manageleavesentdata = MutableLiveData()
        Leavehistoryprinciple = MutableLiveData()
        LeaveRequest = MutableLiveData()
        sectionandsubject = MutableLiveData()
        Examcreation = MutableLiveData()
        ExamCreationEdit = MutableLiveData()
        ExamDeleteSection = MutableLiveData()
        Examviewmodel = MutableLiveData()
        ExamviewmodelSubjectList = MutableLiveData()
        Examdeleteapi = MutableLiveData()
        MarkAttendance = MutableLiveData()
        VideosendEntire = MutableLiveData()
        VideoParticulersend = MutableLiveData()
        Eventimageupdate = MutableLiveData()
        ExamEditSection = MutableLiveData()
        Assignmentforworddata = MutableLiveData()
        Chatlistdata = MutableLiveData()
        ChatStudent = MutableLiveData()
        SenderChat = MutableLiveData()
        Assignmentsubmit = MutableLiveData()
        Assignmentsendersubmittion = MutableLiveData()
        StuentforAssignmensubmitted = MutableLiveData()
        AttendanceEdit = MutableLiveData()
        AssignmentContent_View = MutableLiveData()
        Sendersidechat = MutableLiveData()
        BlackStudent = MutableLiveData()
        UnBlackStudent = MutableLiveData()
        Videosendtuter = MutableLiveData()
        GetOtp = MutableLiveData()
        OtpVerified = MutableLiveData()
        _newpassword = MutableLiveData()
        Pdf_andImagesendTuter = MutableLiveData()
        TextHistory_ = MutableLiveData()
        VoiceHistory_ = MutableLiveData()
        CollageListHeaderSent = MutableLiveData()
        GetHallticket = MutableLiveData()
    }

    fun GetCourseDetails(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetCourseDetails(jsonObject)
            ?.enqueue(object : Callback<GetCourseDetailsResposne?> {
                override fun onResponse(
                    call: Call<GetCourseDetailsResposne?>,
                    response: Response<GetCourseDetailsResposne?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetCourseDetailRespose",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                GetCourseDetailsMutableData.postValue(response.body())
                            } else {
                                CommonUtil.ApiAlertFinish(activity, response.body()!!.message)
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        GetCourseDetailsMutableData.postValue(null)
                    } else {
                        GetCourseDetailsMutableData.postValue(null)

                    }
                }

                override fun onFailure(call: Call<GetCourseDetailsResposne?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetCourseDetailsMutableData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val courseDetailsResposneLiveData: LiveData<GetCourseDetailsResposne?>
        get() = GetCourseDetailsMutableData

    fun GetExamApplicationDetails(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetExamApplicationDetails(jsonObject)
            ?.enqueue(object : Callback<GetExamApplicationResponse?> {
                override fun onResponse(
                    call: Call<GetExamApplicationResponse?>,
                    response: Response<GetExamApplicationResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetExamDetailRespose",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            GetExamDetailsMutableData.postValue(response.body())

                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetExamDetailsMutableData.postValue(null)

                    } else {
                        GetExamDetailsMutableData.postValue(null)

                    }
                }

                override fun onFailure(call: Call<GetExamApplicationResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetExamDetailsMutableData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val examApplicationResponseLiveData: LiveData<GetExamApplicationResponse?>
        get() = GetExamDetailsMutableData

    fun GetStudentProfile(memberid: Int, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetProfile(memberid)
            ?.enqueue(object : Callback<GetProfileResponse?> {
                override fun onResponse(
                    call: Call<GetProfileResponse?>, response: Response<GetProfileResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetProfileResponse",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                GetProfileDetailsMutableData.postValue(response.body())
                            } else {
                                CommonUtil.ApiAlertFinish(activity, response.body()!!.message)
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetProfileDetailsMutableData.postValue(null)

                    } else {
                        GetProfileDetailsMutableData.postValue(null)

                    }
                }

                override fun onFailure(call: Call<GetProfileResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetProfileDetailsMutableData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val profileResponseLiveData: LiveData<GetProfileResponse?>
        get() = GetProfileDetailsMutableData

    fun GetNoticeboradList(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetNoticeboardlistbytpe(jsonObject)
            ?.enqueue(object : Callback<GetNoticeboardResposne?> {
                override fun onResponse(
                    call: Call<GetNoticeboardResposne?>, response: Response<GetNoticeboardResposne?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetNoticeboardRsponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            GetNoticeboardMutableLiveData.postValue(response.body())

                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetNoticeboardMutableLiveData.postValue(null)
                    } else {
                        GetNoticeboardMutableLiveData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<GetNoticeboardResposne?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetNoticeboardMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val noticeboardLiveData: LiveData<GetNoticeboardResposne?>
        get() = GetNoticeboardMutableLiveData


    fun DeleteNoticeBoard(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.DeleteNoticeboard(jsonObject)
            ?.enqueue(object : Callback<Delete_noticeboard?> {
                override fun onResponse(
                    call: Call<Delete_noticeboard?>, response: Response<Delete_noticeboard?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetNoticeboardRsponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            Deletenoticeboard.postValue(response.body())


                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        Deletenoticeboard.postValue(null)
                    } else {
                        Deletenoticeboard.postValue(null)
                    }
                }

                override fun onFailure(call: Call<Delete_noticeboard?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Deletenoticeboard.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Noticeboarddelete: LiveData<Delete_noticeboard?>
        get() = Deletenoticeboard


    fun GetCircularListbyType(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetCircularListbyType(jsonObject)
            ?.enqueue(object : Callback<GetCircularListResponse?> {
                override fun onResponse(
                    call: Call<GetCircularListResponse?>,
                    response: Response<GetCircularListResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetCircularLisRsponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            GetCircularMutableLiveData.postValue(response.body())


                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetCircularMutableLiveData.postValue(null)
                    } else {
                        GetCircularMutableLiveData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<GetCircularListResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetCircularMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val circularListResponseLiveData: LiveData<GetCircularListResponse?>
        get() = GetCircularMutableLiveData

    fun GetAssignmentListbyType(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetAssignmentListbytype(jsonObject)
            ?.enqueue(object : Callback<GetAssignmentListResponse?> {
                override fun onResponse(
                    call: Call<GetAssignmentListResponse?>,
                    response: Response<GetAssignmentListResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "AssignmentLisRsponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status

                            GetAssignmentMutableLiveData.postValue(response.body())

                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetAssignmentMutableLiveData.postValue(null)
                    } else {
                        GetAssignmentMutableLiveData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<GetAssignmentListResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetAssignmentMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val assignmentListResponseLiveData: LiveData<GetAssignmentListResponse?>
        get() = GetAssignmentMutableLiveData

    fun GetAssignmentmemberCount(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetAssignmentMemberCount(jsonObject)
            ?.enqueue(object : Callback<GetAssignmentCountResponse?> {
                override fun onResponse(
                    call: Call<GetAssignmentCountResponse?>,
                    response: Response<GetAssignmentCountResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "MemberCountRsponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            GetAssignmentCountMutableLivedata.postValue(response.body())

                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetAssignmentCountMutableLivedata.postValue(null)
                    } else {
                        GetAssignmentCountMutableLivedata.postValue(null)

                    }
                }

                override fun onFailure(call: Call<GetAssignmentCountResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetAssignmentCountMutableLivedata.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val assignmentCountResponseLiveData: LiveData<GetAssignmentCountResponse?>
        get() = GetAssignmentCountMutableLivedata

    fun GetAssignmentViewContent(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.ViewAssignmentContent(jsonObject)
            ?.enqueue(object : Callback<GetAssignmentViewContentResponse?> {
                override fun onResponse(
                    call: Call<GetAssignmentViewContentResponse?>,
                    response: Response<GetAssignmentViewContentResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "EventsResponse", response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            GetAssignmentViewContentLivedata.postValue(response.body())

                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetAssignmentViewContentLivedata.postValue(null)

                    } else {
                        GetAssignmentViewContentLivedata.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<GetAssignmentViewContentResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    GetAssignmentViewContentLivedata.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val assignmentViewContentResponseLiveData: LiveData<GetAssignmentViewContentResponse?>
        get() = GetAssignmentViewContentLivedata


    fun GetAppreadStatus(jsonObject: JsonObject?, activity: Activity) {

        RestClient.apiInterfaces.AppReadStatus(jsonObject)
            ?.enqueue(object : Callback<StatusMessageResponse?> {
                override fun onResponse(
                    call: Call<StatusMessageResponse?>, response: Response<StatusMessageResponse?>
                ) {
                    Log.d(
                        "AppReadstatus", response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val status = response.body()!!.status
                            if (status == 1) {
                                AppReadStatusLiveData.postValue(response.body())
                            } else {
                                AppReadStatusLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        AppReadStatusLiveData.postValue(null)

                    } else {
                        AppReadStatusLiveData.postValue(null)
                    }
                }

                override fun onFailure(
                    call: Call<StatusMessageResponse?>, t: Throwable
                ) {
                    AppReadStatusLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    fun GetAppreadStatusContext(jsonObject: JsonObject?, activity: Context) {

        RestClient.apiInterfaces.AppReadStatus(jsonObject)
            ?.enqueue(object : Callback<StatusMessageResponse?> {
                override fun onResponse(
                    call: Call<StatusMessageResponse?>, response: Response<StatusMessageResponse?>
                ) {
                    Log.d(
                        "AppReadstatus", response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val status = response.body()!!.status
                            if (status == 1) {
                                AppReadStatusLiveData.postValue(response.body())
                            } else {
                                AppReadStatusLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        AppReadStatusLiveData.postValue(null)

                    } else {
                        AppReadStatusLiveData.postValue(null)
                    }
                }

                override fun onFailure(
                    call: Call<StatusMessageResponse?>, t: Throwable
                ) {
                    AppReadStatusLiveData.postValue(null)
                    t.printStackTrace()

                }
            })
    }

    val appreadstatusLiveData: LiveData<StatusMessageResponse?>
        get() = AppReadStatusLiveData

    fun GetOVerAllCount(jsonObject: JsonObject?, activity: Activity) {
        //  var progressDialog = CustomLoading.createProgressDialog(activity)
        //  progressDialog!!.show()
        RestClient.apiInterfaces.GetOverallcountByMenuType(jsonObject)
            ?.enqueue(object : Callback<GetOverAllCountResposne?> {
                override fun onResponse(
                    call: Call<GetOverAllCountResposne?>,
                    response: Response<GetOverAllCountResposne?>
                ) {
                    //      progressDialog!!.dismiss()
                    Log.d(
                        "GetOverAllCountRes",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val status = response.body()!!.status
                            GetOverAllMenuCountMutableLiveData.postValue(response.body())

                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {


                        GetOverAllMenuCountMutableLiveData.postValue(null)


                    } else {
                        GetOverAllMenuCountMutableLiveData.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<GetOverAllCountResposne?>, t: Throwable
                ) {
                    GetOverAllMenuCountMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val OverAllCountMenuLiveData: LiveData<GetOverAllCountResposne?>
        get() = GetOverAllMenuCountMutableLiveData

    fun GetEventListBytType(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetEventListByType(jsonObject)
            ?.enqueue(object : Callback<GetEventListbyTypeResponse?> {
                override fun onResponse(
                    call: Call<GetEventListbyTypeResponse?>,
                    response: Response<GetEventListbyTypeResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "ViewContentResAssignmet",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status

                            GetEventListMutableData.postValue(response.body())

                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetEventListMutableData.postValue(null)

                    } else {
                        GetEventListMutableData.postValue(null)

                    }
                }

                override fun onFailure(call: Call<GetEventListbyTypeResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetEventListMutableData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getEventListLiveData: LiveData<GetEventListbyTypeResponse?>
        get() = GetEventListMutableData

    fun GetCommunicationList(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetCommunicationVoiceBytype(jsonObject)
            ?.enqueue(object : Callback<GetCommunicationResponse?> {
                override fun onResponse(
                    call: Call<GetCommunicationResponse?>,
                    response: Response<GetCommunicationResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Communicationresponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status

                            if (status == 1) {
                                GetCommunicationMutableLiveData.postValue(response.body())
                            } else {
                                GetCommunicationMutableLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetCommunicationMutableLiveData.postValue(response.body())
                    } else {
                        GetCommunicationMutableLiveData.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<GetCommunicationResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetCommunicationMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getCommunicationLiveData: LiveData<GetCommunicationResponse?>
        get() = GetCommunicationMutableLiveData

    fun GetCommunicationTextList(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetCommunicationMessageBytype(jsonObject)
            ?.enqueue(object : Callback<GetCommunicationResponse?> {
                override fun onResponse(
                    call: Call<GetCommunicationResponse?>,
                    response: Response<GetCommunicationResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Communicationresponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status

                            if (status == 1) {
                                GetCommunicationMutableLiveData.postValue(response.body())
                            } else {
                                GetCommunicationMutableLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetCommunicationMutableLiveData.postValue(response.body())
                    } else {
                        GetCommunicationMutableLiveData.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<GetCommunicationResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetCommunicationMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getCommunicationLiveTextData: LiveData<GetCommunicationResponse?>
        get() = GetCommunicationMutableLiveData


    fun Communication_NewButtonVisible_Not(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog.show()
        RestClient.apiInterfaces.Communication_V_N(jsonObject)
            ?.enqueue(object : Callback<Communication_AddNewButton?> {
                override fun onResponse(
                    call: Call<Communication_AddNewButton?>,
                    response: Response<Communication_AddNewButton?>
                ) {
                    progressDialog.dismiss()
                    Log.d(
                        "Communicationresponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog.dismiss()
                            val status = response.body()!!.Status

                            if (status == 1) {
                                CommunicationNew_Button.postValue(response.body())
                            } else {
                                CommunicationNew_Button.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog.dismiss()
                        CommunicationNew_Button.postValue(response.body())
                    } else {
                        CommunicationNew_Button.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<Communication_AddNewButton?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    CommunicationNew_Button.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getCommunicationButton_VisibleOrNot: LiveData<Communication_AddNewButton?>
        get() = CommunicationNew_Button

    fun GetFacultyList(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetFacultyList(jsonObject)
            ?.enqueue(object : Callback<GetFacultyListResponse?> {
                override fun onResponse(
                    call: Call<GetFacultyListResponse?>, response: Response<GetFacultyListResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "FacultyResponse:", response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                GetFacultyRecieverMutableLiveData.postValue(response.body())
                            } else {
                                GetFacultyRecieverMutableLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetFacultyRecieverMutableLiveData.postValue(null)

                    } else {
                        GetFacultyRecieverMutableLiveData.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<GetFacultyListResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    GetFacultyRecieverMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getFacultyReceiverLiveData: LiveData<GetFacultyListResponse?>
        get() = GetFacultyRecieverMutableLiveData


    fun GetFacultyListStaff(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetFacultyListStaff(jsonObject)
            ?.enqueue(object : Callback<GetFacultyListResponse?> {
                override fun onResponse(
                    call: Call<GetFacultyListResponse?>, response: Response<GetFacultyListResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "FacultyResponse:", response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                GetFacultyRecieverMutableLiveData.postValue(response.body())
                            } else {
                                GetFacultyRecieverMutableLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetFacultyRecieverMutableLiveData.postValue(null)

                    } else {
                        GetFacultyRecieverMutableLiveData.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<GetFacultyListResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    GetFacultyRecieverMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getFacultyReceiverLiveDatastaff: LiveData<GetFacultyListResponse?>
        get() = GetFacultyRecieverMutableLiveData


    fun GetFacultyListReceiver(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.faculrtlistRecevier(jsonObject)
            ?.enqueue(object : Callback<GetFacultyListResponse?> {
                override fun onResponse(
                    call: Call<GetFacultyListResponse?>, response: Response<GetFacultyListResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "FacultyResponse:", response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                GetFacultyListSender.postValue(response.body())
                            } else {
                                GetFacultyListSender.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetFacultyListSender.postValue(null)

                    } else {
                        GetFacultyListSender.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<GetFacultyListResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    GetFacultyListSender.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val FacultyRecevierList: LiveData<GetFacultyListResponse?>
        get() = GetFacultyListSender

    fun SemesterSectionListForApp(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetSemesterSectionlist(jsonObject)
            ?.enqueue(object : Callback<SemesterAndSectionListResposne?> {
                override fun onResponse(
                    call: Call<SemesterAndSectionListResposne?>,
                    response: Response<SemesterAndSectionListResposne?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SemesterSectionRes:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                Log.d("response body", response.body().toString())
                                SemesterSectionMutableLiveData.postValue(response.body())
                            } else {
                                SemesterSectionMutableLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        SemesterSectionMutableLiveData.postValue(null)

                    } else {
                        SemesterSectionMutableLiveData.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<SemesterAndSectionListResposne?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    SemesterSectionMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getSemesterSectionListLiveData: LiveData<SemesterAndSectionListResposne?>
        get() = SemesterSectionMutableLiveData

    fun GetExamListReceiver(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetExamListByType(jsonObject)
            ?.enqueue(object : Callback<GetExamListResponse?> {
                override fun onResponse(
                    call: Call<GetExamListResponse?>, response: Response<GetExamListResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "ExamListResponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            Log.d("s", "s")
                            val status = response.body()!!.status
                            if (status == 1) {
                                ExamListMutableLiveData.postValue(response.body())
                            } else {
                                ExamListMutableLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        ExamListMutableLiveData.postValue(response.body())

                    } else {
                        ExamListMutableLiveData.postValue(response.body())

                    }
                }

                override fun onFailure(
                    call: Call<GetExamListResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    ExamListMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getExamListLiveData: LiveData<GetExamListResponse?>
        get() = ExamListMutableLiveData


    fun GetExamListReceiverside(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetExamListByTypeReciver(jsonObject)
            ?.enqueue(object : Callback<GetExamListResponse?> {
                override fun onResponse(
                    call: Call<GetExamListResponse?>, response: Response<GetExamListResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "ExamListResponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            Log.d("s", "s")
                            val status = response.body()!!.status
                            if (status == 1) {
                                ExamListMutableLiveData.postValue(response.body())
                            } else {
                                ExamListMutableLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        ExamListMutableLiveData.postValue(response.body())

                    } else {
                        ExamListMutableLiveData.postValue(response.body())

                    }
                }

                override fun onFailure(
                    call: Call<GetExamListResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    ExamListMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val ExamlistReciver: LiveData<GetExamListResponse?>
        get() = ExamListMutableLiveData

    fun ExamMarkList(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetExamMarkForStudent(jsonObject)
            ?.enqueue(object : Callback<ExamMarkListResponse?> {
                override fun onResponse(
                    call: Call<ExamMarkListResponse?>, response: Response<ExamMarkListResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "ExamMarkListRes:", response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                ExamMarkListMutableLiveData.postValue(response.body())
                            } else {
                                ExamMarkListMutableLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        ExamMarkListMutableLiveData.postValue(null)

                    } else {
                        ExamMarkListMutableLiveData.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<ExamMarkListResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    ExamMarkListMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getExamMarkListLiveData: LiveData<ExamMarkListResponse?>
        get() = ExamMarkListMutableLiveData

    fun GetVideoList(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetVideoList(jsonObject)
            ?.enqueue(object : Callback<GetVideoListResponse?> {
                override fun onResponse(
                    call: Call<GetVideoListResponse?>, response: Response<GetVideoListResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetVideoListResponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            VideoListMutableLiveData.postValue(response.body())

                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        VideoListMutableLiveData.postValue(null)

                    } else {
                        VideoListMutableLiveData.postValue(null)

                    }
                }

                override fun onFailure(call: Call<GetVideoListResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    VideoListMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getVideoListMutableLiveData: LiveData<GetVideoListResponse?>
        get() = VideoListMutableLiveData


    fun NumberDetails(jsonObject: JsonObject?, activity: Activity) {
        Log.d("JsonObject", jsonObject.toString())
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GraditContactNumberDetails(jsonObject)
            ?.enqueue(object : Callback<GraditContectNumber?> {
                override fun onResponse(
                    call: Call<GraditContectNumber?>, response: Response<GraditContectNumber?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GraditGetNumbers:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            GraditContectNumber.postValue(response.body())

                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GraditContectNumber.postValue(null)

                    } else {
                        GraditContectNumber.postValue(null)

                    }
                }

                override fun onFailure(call: Call<GraditContectNumber?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GraditContectNumber.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val ContectNumberGradit: LiveData<GraditContectNumber?>
        get() = GraditContectNumber


    fun GetStaffDetails(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetstaffdetailsForApp(jsonObject)
            ?.enqueue(object : Callback<GetStaffDetailsResponse?> {
                override fun onResponse(
                    call: Call<GetStaffDetailsResponse?>,
                    response: Response<GetStaffDetailsResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetStaffDetailsRes:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            StaffForAppListMutableLiveData.postValue(response.body())


                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        StaffForAppListMutableLiveData.postValue(null)

                    } else {
                        StaffForAppListMutableLiveData.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<GetStaffDetailsResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    StaffForAppListMutableLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getStaffListMutableLiveData: LiveData<GetStaffDetailsResponse?>
        get() = StaffForAppListMutableLiveData

    fun StaffClassesforChat(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetStaffClassesforChatForApp(jsonObject)
            ?.enqueue(object : Callback<Chat_StaffList?> {
                override fun onResponse(
                    call: Call<Chat_StaffList?>,
                    response: Response<Chat_StaffList?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetStaffDetailsRes:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            Staffchatlivedata.postValue(response.body())

                            if (status == 1) {
                                Staffchatlivedata.postValue(response.body())
                            } else {
                                Staffchatlivedata.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        Staffchatlivedata.postValue(null)

                    } else {
                        Staffchatlivedata.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<Chat_StaffList?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    Staffchatlivedata.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getStaffcatdata: LiveData<Chat_StaffList?>
        get() = Staffchatlivedata


    fun GetCategoryType(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetCategoryCreditsType(jsonObject)
            ?.enqueue(object : Callback<GetCategoryTypesResponse?> {
                override fun onResponse(
                    call: Call<GetCategoryTypesResponse?>,
                    response: Response<GetCategoryTypesResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "CategoryTypesResponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                CategoryTypeLiveData.postValue(response.body())
                            } else {
                                CategoryTypeLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        CategoryTypeLiveData.postValue(null)

                    } else {
                        CategoryTypeLiveData.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<GetCategoryTypesResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    CategoryTypeLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getCategoryTypeLiveData: LiveData<GetCategoryTypesResponse?>
        get() = CategoryTypeLiveData


    fun GetCategoryCreditwise(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetCategoryCreditWiseDetails(jsonObject)
            ?.enqueue(object : Callback<GetCategoryWiseCreditResponse?> {
                override fun onResponse(
                    call: Call<GetCategoryWiseCreditResponse?>,
                    response: Response<GetCategoryWiseCreditResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "CategoryCreditwseRes:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                CategoryWiseCreditLiveData.postValue(response.body())
                            } else {
                                CategoryWiseCreditLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        CategoryWiseCreditLiveData.postValue(null)

                    } else {
                        CategoryWiseCreditLiveData.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<GetCategoryWiseCreditResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    CategoryWiseCreditLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getCategoryWiseCreditLiveData: LiveData<GetCategoryWiseCreditResponse?>
        get() = CategoryWiseCreditLiveData

    fun GetSemesterType(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetSemesterCreditsType(jsonObject)
            ?.enqueue(object : Callback<GetSemesterWiseTypeResponse?> {
                override fun onResponse(
                    call: Call<GetSemesterWiseTypeResponse?>,
                    response: Response<GetSemesterWiseTypeResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SemesterTypesResponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                SemesterTypeLiveData.postValue(response.body())
                            } else {
                                SemesterTypeLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        SemesterTypeLiveData.postValue(null)

                    } else {
                        SemesterTypeLiveData.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<GetSemesterWiseTypeResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    SemesterTypeLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getSemesterTypeLiveData: LiveData<GetSemesterWiseTypeResponse?>
        get() = SemesterTypeLiveData


    fun GetSemesterWiseCredits(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetSemesterWiseCredits(jsonObject)
            ?.enqueue(object : Callback<GetSemesterWiseCreditResponse?> {
                override fun onResponse(
                    call: Call<GetSemesterWiseCreditResponse?>,
                    response: Response<GetSemesterWiseCreditResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SemesterCreditRes:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                SemesterWiseCreditLiveData.postValue(response.body())
                            } else {
                                SemesterWiseCreditLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        SemesterWiseCreditLiveData.postValue(null)

                    } else {
                        SemesterWiseCreditLiveData.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<GetSemesterWiseCreditResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    SemesterWiseCreditLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getSemesterwiseCreditsLiveData: LiveData<GetSemesterWiseCreditResponse?>
        get() = SemesterWiseCreditLiveData

    fun GetSemesterWiseCreditsAll(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetSemesterWiseCreditsAll(jsonObject)
            ?.enqueue(object : Callback<GetSemesterWiseCreditALLResponse?> {
                override fun onResponse(
                    call: Call<GetSemesterWiseCreditALLResponse?>,
                    response: Response<GetSemesterWiseCreditALLResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SemesterCreditRes:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                SemesterWiseCreditAllLiveData.postValue(response.body())
                            } else {
                                SemesterWiseCreditAllLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        SemesterWiseCreditAllLiveData.postValue(null)
                    } else {
                        SemesterWiseCreditAllLiveData.postValue(null)
                    }
                }

                override fun onFailure(
                    call: Call<GetSemesterWiseCreditALLResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    SemesterWiseCreditAllLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getSemesterwiseCreditsAllLiveData: LiveData<GetSemesterWiseCreditALLResponse?>
        get() = SemesterWiseCreditAllLiveData

    fun GetAttendanceForParent(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetAttendanceForParent(jsonObject)
            ?.enqueue(object : Callback<AttendanceResponse?> {
                override fun onResponse(
                    call: Call<AttendanceResponse?>, response: Response<AttendanceResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SemesterCreditRes:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status

                            AttendanceDatesiveData.postValue(response.body())

                            if (status == 1) {
                                progressDialog!!.dismiss()

                                AttendanceDatesiveData.postValue(response.body())
                            } else {
                                progressDialog!!.dismiss()

                                AttendanceDatesiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        AttendanceDatesiveData.postValue(null)

                    } else {
                        progressDialog!!.dismiss()

                        AttendanceDatesiveData.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<AttendanceResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    AttendanceDatesiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getAttendanceLiveData: LiveData<AttendanceResponse?>
        get() = AttendanceDatesiveData


    fun GetStudentAttendancelist(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetAttendanceStudentlist(jsonObject)
            ?.enqueue(object : Callback<StudentAttendancelist?> {
                override fun onResponse(
                    call: Call<StudentAttendancelist?>, response: Response<StudentAttendancelist?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SemesterCreditRes:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status

                            StudentlistAttendance.postValue(response.body())

                            if (status == 1) {
                                progressDialog!!.dismiss()

                                StudentlistAttendance.postValue(response.body())
                            } else {
                                progressDialog!!.dismiss()

                                StudentlistAttendance.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        StudentlistAttendance.postValue(null)

                    } else {
                        progressDialog!!.dismiss()

                        StudentlistAttendance.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<StudentAttendancelist?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    StudentlistAttendance.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val AttendancelistStudent: LiveData<StudentAttendancelist?>
        get() = StudentlistAttendance

    fun GetLeaveHistoryParent(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog.show()
        RestClient.apiInterfaces.GetLeaveApplicationListForReceiverApp(jsonObject)
            ?.enqueue(object : Callback<LeaveHistoryResponse?> {
                override fun onResponse(
                    call: Call<LeaveHistoryResponse?>, response: Response<LeaveHistoryResponse?>
                ) {
                    progressDialog.dismiss()
                    Log.d(
                        "LeaveHistoryResponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog.dismiss()
                            val status = response.body()!!.status

                            if (status == 1) {
                                LeaveHistoryLiveData.postValue(response.body())
                            } else {
                                LeaveHistoryLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog.dismiss()
                        LeaveHistoryLiveData.postValue(null)

                    } else {
                        LeaveHistoryLiveData.postValue(null)
                    }
                }

                override fun onFailure(
                    call: Call<LeaveHistoryResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    LeaveHistoryLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getLeaveHistoryLiveData: LiveData<LeaveHistoryResponse?>
        get() = LeaveHistoryLiveData


    fun GetLeaveType(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog.show()
        RestClient.apiInterfaces.GetLeaveType(jsonObject)
            ?.enqueue(object : Callback<LeaveTypeResponse?> {
                override fun onResponse(
                    call: Call<LeaveTypeResponse?>, response: Response<LeaveTypeResponse?>
                ) {
                    progressDialog.dismiss()
                    Log.d(
                        "LeaveHistoryResponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                LeaveTypeLiveData.postValue(response.body())
                            } else {
                                LeaveTypeLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        LeaveTypeLiveData.postValue(null)

                    } else {
                        LeaveHistoryLiveData.postValue(null)

                    }
                }

                override fun onFailure(
                    call: Call<LeaveTypeResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    LeaveTypeLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getLeaveTypeLiveData: LiveData<LeaveTypeResponse?>
        get() = LeaveTypeLiveData

    fun ChangePassword(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.ChangePassword(jsonObject)
            ?.enqueue(object : Callback<StatusMessageResponse?> {
                override fun onResponse(
                    call: Call<StatusMessageResponse?>, response: Response<StatusMessageResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "LeaveHistoryResponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                ChangePasswordLiveData.postValue(response.body())
                            } else {
                                ChangePasswordLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        ChangePasswordLiveData.postValue(null)
                    } else {
                        ChangePasswordLiveData.postValue(null)
                    }
                }

                override fun onFailure(
                    call: Call<StatusMessageResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    ChangePasswordLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getChangePawwordLiveData: LiveData<StatusMessageResponse?>
        get() = ChangePasswordLiveData

    fun getAddFoCollege(jsonObject: JsonObject?, activity: Activity) {
        // var progressDialog = CustomLoading.createProgressDialog(activity)
        //  progressDialog!!.show()
        RestClient.apiInterfaces.GetAdsForCollege(jsonObject)
            ?.enqueue(object : Callback<GetAdvertisementResponse?> {
                override fun onResponse(
                    call: Call<GetAdvertisementResponse?>,
                    response: Response<GetAdvertisementResponse?>
                ) {
                    //   progressDialog!!.dismiss()
                    Log.d(
                        "LeaveHistoryResponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            //  progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                AdvertisementLiveData.postValue(response.body())
                            } else {
                                AdvertisementLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        //   progressDialog!!.dismiss()
                        AdvertisementLiveData.postValue(null)
                    } else {
                        AdvertisementLiveData.postValue(null)
                    }
                }

                override fun onFailure(
                    call: Call<GetAdvertisementResponse?>, t: Throwable
                ) {
                    //  progressDialog!!.dismiss()
                    AdvertisementLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getAdvertisementMutableData: LiveData<GetAdvertisementResponse?>
        get() = AdvertisementLiveData

    fun UpdateDeviceToken(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.UpdateDeviceToken(jsonObject)
            ?.enqueue(object : Callback<StatusMessageResponse?> {
                override fun onResponse(
                    call: Call<StatusMessageResponse?>, response: Response<StatusMessageResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "UpdateDeviceToken:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                UpdateDeviceTokenLiveData.postValue(response.body())
                            } else {
                                UpdateDeviceTokenLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        UpdateDeviceTokenLiveData.postValue(null)
                    } else {
                        UpdateDeviceTokenLiveData.postValue(null)
                    }
                }

                override fun onFailure(
                    call: Call<StatusMessageResponse?>, t: Throwable
                ) {
                    progressDialog!!.dismiss()
                    UpdateDeviceTokenLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val UpdateDeviceTokenMutableLiveData: LiveData<StatusMessageResponse?>
        get() = UpdateDeviceTokenLiveData

    fun VideoRestriction(activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetVideoContentRestriction()
            ?.enqueue(object : Callback<VideoRestrictionContentResponse?> {
                override fun onResponse(
                    call: Call<VideoRestrictionContentResponse?>,
                    response: Response<VideoRestrictionContentResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "VideoRestriction:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                VideoRestrictionLiveData.postValue(response.body())
                            } else {
                                VideoRestrictionLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        VideoRestrictionLiveData.postValue(null)
                    } else {
                        VideoRestrictionLiveData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<VideoRestrictionContentResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    VideoRestrictionLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val VideoRestrictionMutableLiveData: LiveData<VideoRestrictionContentResponse?>
        get() = VideoRestrictionLiveData

    fun GetDivisions(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetDivisions(jsonObject)
            ?.enqueue(object : Callback<GetDivisionResponse?> {
                override fun onResponse(
                    call: Call<GetDivisionResponse?>, response: Response<GetDivisionResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetDivisionResponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                GetDivisionLiveData.postValue(response.body())
                            } else {
                                GetDivisionLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetDivisionLiveData.postValue(null)
                    } else {
                        GetDivisionLiveData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<GetDivisionResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetDivisionLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val GetDivisionMutableLiveData: LiveData<GetDivisionResponse?>
        get() = GetDivisionLiveData

    fun GetDepartmentData(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetDepartment(jsonObject)
            ?.enqueue(object : Callback<GetDepartmentResponse?> {
                override fun onResponse(
                    call: Call<GetDepartmentResponse?>, response: Response<GetDepartmentResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetDepartmentResponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                GetDepartmentLiveData.postValue(response.body())
                            } else {
                                GetDepartmentLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetDepartmentLiveData.postValue(null)
                    } else {
                        GetDepartmentLiveData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<GetDepartmentResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetDepartmentLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val GetDepartmentMutableLiveData: LiveData<GetDepartmentResponse?>
        get() = GetDepartmentLiveData

    fun GetCoursesByDepartment(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetCoursesByDepartment(jsonObject)
            ?.enqueue(object : Callback<GetCourseDepartmentResposne?> {
                override fun onResponse(
                    call: Call<GetCourseDepartmentResposne?>,
                    response: Response<GetCourseDepartmentResposne?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetCourseResponse:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {
                                GetCourseLiveData.postValue(response.body())
                            } else {
                                GetCourseLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetCourseLiveData.postValue(null)
                    } else {
                        GetCourseLiveData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<GetCourseDepartmentResposne?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetCourseLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val GetCourseMutableLiveData: LiveData<GetCourseDepartmentResposne?>
        get() = GetCourseLiveData

    fun SendSMSToEntireCollege(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.SendSMSToEntireCollege(jsonObject)
            ?.enqueue(object : Callback<SenderStatusMessageData?> {
                override fun onResponse(
                    call: Call<SenderStatusMessageData?>,
                    response: Response<SenderStatusMessageData?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSEntireCollege:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {
                                SMSEntireCollegeLiveData.postValue(response.body())
                            } else {
                                SMSEntireCollegeLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        SMSEntireCollegeLiveData.postValue(null)
                    } else {
                        SMSEntireCollegeLiveData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<SenderStatusMessageData?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    SMSEntireCollegeLiveData.postValue(null)
                    t.printStackTrace()
                    Log.d("failure", "failure")
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val SendSMStoEntireCollegeMutableData: LiveData<SenderStatusMessageData?>
        get() = SMSEntireCollegeLiveData


    fun SendVoiceToEntireCollege(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.SendVoiceToEntireCollege(jsonObject)
            ?.enqueue(object : Callback<SenderStatusMessageData?> {
                override fun onResponse(
                    call: Call<SenderStatusMessageData?>,
                    response: Response<SenderStatusMessageData?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSEntireCollege:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {
                                VoiceEntireCollegeLiveData.postValue(response.body())
                            } else {
                                VoiceEntireCollegeLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        VoiceEntireCollegeLiveData.postValue(null)
                    } else {
                        VoiceEntireCollegeLiveData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<SenderStatusMessageData?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    VoiceEntireCollegeLiveData.postValue(null)
                    t.printStackTrace()
                    Log.d("failure", "failure")
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val SendVoiceEntire: LiveData<SenderStatusMessageData?>
        get() = VoiceEntireCollegeLiveData


    fun SendVoiceToParticularTypeFromHistory(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.SendVoiceToParticularTypeFromHistory(jsonObject)
            ?.enqueue(object : Callback<SenderStatusMessageData?> {
                override fun onResponse(
                    call: Call<SenderStatusMessageData?>,
                    response: Response<SenderStatusMessageData?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSEntireCollege:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {
                                VoiceParticulerHistory.postValue(response.body())
                            } else {
                                VoiceParticulerHistory.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        VoiceParticulerHistory.postValue(null)
                    } else {
                        VoiceParticulerHistory.postValue(null)
                    }
                }

                override fun onFailure(call: Call<SenderStatusMessageData?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    VoiceParticulerHistory.postValue(null)
                    t.printStackTrace()
                    Log.d("failure", "failure")
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val SendVoiceHistoryParticuler: LiveData<SenderStatusMessageData?>
        get() = VoiceParticulerHistory

    fun SendVoiceEntireHistory(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.SendVoiceEntireFromHistory(jsonObject)
            ?.enqueue(object : Callback<SenderStatusMessageData?> {
                override fun onResponse(
                    call: Call<SenderStatusMessageData?>,
                    response: Response<SenderStatusMessageData?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSEntireCollege:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {
                                VoiceEntireHistory.postValue(response.body())
                            } else {
                                VoiceEntireHistory.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        VoiceEntireHistory.postValue(null)
                    } else {
                        VoiceEntireHistory.postValue(null)
                    }
                }

                override fun onFailure(call: Call<SenderStatusMessageData?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    VoiceEntireHistory.postValue(null)
                    t.printStackTrace()
                    Log.d("failure", "failure")
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val SendVoiceHistoryEntire: LiveData<SenderStatusMessageData?>
        get() = VoiceEntireHistory


    fun SendSMSToEntiretotorandsubjectCollege(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.SendSMSToEntiretotorandsubjectCollege(jsonObject)
            ?.enqueue(object : Callback<SenderStatusMessageData?> {
                override fun onResponse(
                    call: Call<SenderStatusMessageData?>,
                    response: Response<SenderStatusMessageData?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSEntireCollege:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {
                                SMSEntireCollegeLiveData.postValue(response.body())
                            } else {
                                SMSEntireCollegeLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        SMSEntireCollegeLiveData.postValue(null)
                    } else {
                        SMSEntireCollegeLiveData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<SenderStatusMessageData?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    SMSEntireCollegeLiveData.postValue(null)
                    t.printStackTrace()
                    Log.d("failure", "failure")
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }


    val SendSMSToEntireCollegetutoeandsubjectLiveData: LiveData<SenderStatusMessageData?>
        get() = SMSEntireCollegetutorandsubjectLiveData

    fun SendSMStoParticularType(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.SendSMSToParticularType(jsonObject)
            ?.enqueue(object : Callback<SenderStatusMessageData?> {
                override fun onResponse(
                    call: Call<SenderStatusMessageData?>,
                    response: Response<SenderStatusMessageData?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSParticularType:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                SMSParticularTypeLiveData.postValue(response.body())

                            } else {
                                SMSParticularTypeLiveData.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        SMSParticularTypeLiveData.postValue(null)
                    } else {
                        SMSParticularTypeLiveData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<SenderStatusMessageData?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    SMSParticularTypeLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val SendSMStoParticularMutableData: LiveData<SenderStatusMessageData?>
        get() = SMSParticularTypeLiveData


    fun NoticeBoardSendSMStoParticularType(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Noticeboardsms(jsonObject)
            ?.enqueue(object : Callback<NoticeBoardSMSsend?> {
                override fun onResponse(
                    call: Call<NoticeBoardSMSsend?>, response: Response<NoticeBoardSMSsend?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSParticularType:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                Noticeboardsmssend.postValue(response.body())

                            } else {
                                Noticeboardsmssend.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        Noticeboardsmssend.postValue(null)
                    } else {
                        Noticeboardsmssend.postValue(null)
                    }
                }

                override fun onFailure(call: Call<NoticeBoardSMSsend?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    SMSParticularTypeLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val NoticeBoardSendSMStoParticularMutableData: LiveData<NoticeBoardSMSsend?>
        get() = Noticeboardsmssend


    fun NoticeBoardSendSMStoParticularTypeTuter(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.NoticeboardsmsTuter(jsonObject)
            ?.enqueue(object : Callback<NoticeBoardSMSsend?> {
                override fun onResponse(
                    call: Call<NoticeBoardSMSsend?>, response: Response<NoticeBoardSMSsend?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSParticularType:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                Noticeboardsmssend.postValue(response.body())

                            } else {
                                Noticeboardsmssend.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        Noticeboardsmssend.postValue(null)
                    } else {
                        Noticeboardsmssend.postValue(null)
                    }
                }

                override fun onFailure(call: Call<NoticeBoardSMSsend?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    SMSParticularTypeLiveData.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val NoticeBoardSendSMStoParticularMutableDataTuter: LiveData<NoticeBoardSMSsend?>
        get() = Noticeboardsmssend


    fun ImageorPdfsend(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.ImageOrPdf(jsonObject)
            ?.enqueue(object : Callback<ImageORpdfsend?> {
                override fun onResponse(
                    call: Call<ImageORpdfsend?>, response: Response<ImageORpdfsend?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSParticularType:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                ImageorPdf.postValue(response.body())

                            } else {
                                ImageorPdf.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        ImageorPdf.postValue(null)
                    } else {
                        ImageorPdf.postValue(null)
                    }
                }

                override fun onFailure(call: Call<ImageORpdfsend?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    ImageorPdf.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val ImageOrPdfsend: LiveData<ImageORpdfsend?>
        get() = ImageorPdf


    fun ImageorPdfsendparticuler(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.ImageOrPdfparticuler(jsonObject)
            ?.enqueue(object : Callback<ImageORpdfsend?> {
                override fun onResponse(
                    call: Call<ImageORpdfsend?>, response: Response<ImageORpdfsend?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSParticularType:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                imageorpdfparticuler.postValue(response.body())

                            } else {
                                imageorpdfparticuler.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        imageorpdfparticuler.postValue(null)
                    } else {
                        imageorpdfparticuler.postValue(null)
                    }
                }

                override fun onFailure(call: Call<ImageORpdfsend?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    imageorpdfparticuler.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val ImageOrPdfsendparticuler: LiveData<ImageORpdfsend?>
        get() = imageorpdfparticuler

    fun Assignmentsenddata(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Assignmentsend(jsonObject)
            ?.enqueue(object : Callback<Assignmentsent?> {
                override fun onResponse(
                    call: Call<Assignmentsent?>, response: Response<Assignmentsent?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSParticularType:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                Assignmentdata.postValue(response.body())

                            } else {
                                Assignmentdata.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        Assignmentdata.postValue(null)
                    } else {
                        Assignmentdata.postValue(null)
                    }
                }

                override fun onFailure(call: Call<Assignmentsent?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Assignmentdata.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Assignment: LiveData<Assignmentsent?>
        get() = Assignmentdata


    fun AssignmentForwordText(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.AssignmentforwardText(jsonObject)
            ?.enqueue(object : Callback<Assignmentsent?> {
                override fun onResponse(
                    call: Call<Assignmentsent?>, response: Response<Assignmentsent?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSParticularType:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                AssignmentdataForwardText.postValue(response.body())

                            } else {
                                AssignmentdataForwardText.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        AssignmentdataForwardText.postValue(null)
                    } else {
                        AssignmentdataForwardText.postValue(null)
                    }
                }

                override fun onFailure(call: Call<Assignmentsent?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    AssignmentdataForwardText.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val AssignmentTextForward: LiveData<Assignmentsent?>
        get() = AssignmentdataForwardText


    fun AttendanceMarkingdata(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.AttendanceMark(jsonObject)
            ?.enqueue(object : Callback<AttendancemardkingResponse?> {
                override fun onResponse(
                    call: Call<AttendancemardkingResponse?>,
                    response: Response<AttendancemardkingResponse?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSParticularType:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                Attendancemark.postValue(response.body())

                            } else {
                                Attendancemark.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        Attendancemark.postValue(null)
                    } else {
                        Attendancemark.postValue(null)
                    }
                }

                override fun onFailure(call: Call<AttendancemardkingResponse?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Attendancemark.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Attendancemarking: LiveData<AttendancemardkingResponse?>
        get() = Attendancemark


    fun ManageLeaveapplication(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.ManageLeaveapplication(jsonObject)
            ?.enqueue(object : Callback<ManageLeave?> {
                override fun onResponse(
                    call: Call<ManageLeave?>, response: Response<ManageLeave?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSParticularType:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                Manageleavesentdata.postValue(response.body())

                            } else {
                                Manageleavesentdata.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        Manageleavesentdata.postValue(null)
                    } else {
                        Manageleavesentdata.postValue(null)
                    }
                }

                override fun onFailure(call: Call<ManageLeave?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Manageleavesentdata.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Manageleavesend: LiveData<ManageLeave?>
        get() = Manageleavesentdata

    fun Leavehistortprinciple(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Leavehistortprinciple(jsonObject)
            ?.enqueue(object : Callback<Leave_history?> {
                override fun onResponse(
                    call: Call<Leave_history?>, response: Response<Leave_history?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSParticularType:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                Leavehistoryprinciple.postValue(response.body())

                            } else {
                                Leavehistoryprinciple.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        Leavehistoryprinciple.postValue(null)
                    } else {
                        Leavehistoryprinciple.postValue(null)
                    }
                }

                override fun onFailure(call: Call<Leave_history?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Leavehistoryprinciple.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val leavehistory: LiveData<Leave_history?>
        get() = Leavehistoryprinciple


    fun EventsendData(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Eventsend(jsonObject)
            ?.enqueue(object : Callback<Evendsenddata?> {
                override fun onResponse(
                    call: Call<Evendsenddata?>, response: Response<Evendsenddata?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSParticularType:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                Eventsenddata.postValue(response.body())

                            } else {
                                Eventsenddata.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        Eventsenddata.postValue(null)
                    } else {
                        Eventsenddata.postValue(null)
                    }
                }

                override fun onFailure(call: Call<Evendsenddata?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Eventsenddata.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Eventsenddatasend: LiveData<Evendsenddata?>
        get() = Eventsenddata


    fun EventsendDataTuter(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.EventsendTuter(jsonObject)
            ?.enqueue(object : Callback<Evendsenddata?> {
                override fun onResponse(
                    call: Call<Evendsenddata?>, response: Response<Evendsenddata?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "SMSParticularType:",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                Eventsenddata.postValue(response.body())

                            } else {
                                Eventsenddata.postValue(response.body())
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        Eventsenddata.postValue(null)
                    } else {
                        Eventsenddata.postValue(null)
                    }
                }

                override fun onFailure(call: Call<Evendsenddata?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Eventsenddata.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val EventsenddatasendTuter: LiveData<Evendsenddata?>
        get() = Eventsenddata


    fun GetGroup(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetGroupData(jsonObject)
            ?.enqueue(object : Callback<GetGrouplist?> {
                override fun onResponse(
                    call: Call<GetGrouplist?>, response: Response<GetGrouplist?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "GetGroupdata:", response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {

                                Groupdata.postValue(response.body())

                            } else {
                                Groupdata.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            Groupdata.postValue(null)
                        } else {
                            Groupdata.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<GetGrouplist?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Groupdata.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }

            })
    }

    val getgroupdata: LiveData<GetGrouplist?>
        get() = Groupdata


    fun Getsubject(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Getsubject(jsonObject)
            ?.enqueue(object : Callback<staffsubject_list?> {
                override fun onResponse(
                    call: Call<staffsubject_list?>, response: Response<staffsubject_list?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Getsubjectdata:", response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {

                                Subjectpdata.postValue(response.body())

                            } else {
                                Subjectpdata.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            Subjectpdata.postValue(null)
                        } else {
                            Subjectpdata.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<staffsubject_list?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Subjectpdata.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }

            })
    }

    val getsubjectdata: LiveData<staffsubject_list?>
        get() = Subjectpdata

    fun GettingAttendance(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetAttendance(jsonObject)
            ?.enqueue(object : Callback<GetAttendance?> {
                override fun onResponse(
                    call: Call<GetAttendance?>, response: Response<GetAttendance?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Getsubjectdata:", response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                GetAttendanceForStaff.postValue(response.body())

                            } else {
                                GetAttendanceForStaff.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            GetAttendanceForStaff.postValue(null)
                        } else {
                            GetAttendanceForStaff.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<GetAttendance?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetAttendanceForStaff.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }

            })
    }

    val AttendanceGetting: LiveData<GetAttendance?>
        get() = GetAttendanceForStaff

    fun specificstudentAttendance(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.StudentSpecificstudent(jsonObject)
            ?.enqueue(object : Callback<StudentAttendanceview?> {
                override fun onResponse(
                    call: Call<StudentAttendanceview?>, response: Response<StudentAttendanceview?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Getsubjectdata:", response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {

                                StudentAttendanceview.postValue(response.body())

                            } else {
                                StudentAttendanceview.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            StudentAttendanceview.postValue(null)
                        } else {
                            StudentAttendanceview.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<StudentAttendanceview?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    StudentAttendanceview.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }

            })
    }

    val StudentAttendanceDetails: LiveData<StudentAttendanceview?>
        get() = StudentAttendanceview


    fun Gettuter(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Gettuter(jsonObject)
            ?.enqueue(object : Callback<staffsubject_list?> {
                override fun onResponse(
                    call: Call<staffsubject_list?>, response: Response<staffsubject_list?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Getsubjectdata:", response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {

                                tuterdata.postValue(response.body())

                            } else {
                                tuterdata.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            tuterdata.postValue(null)
                        } else {
                            tuterdata.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<staffsubject_list?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    tuterdata.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }

            })
    }

    val gettuterdata: LiveData<staffsubject_list?>
        get() = tuterdata


    fun Getspecificstudentdata(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Getspecificstudenttuter(jsonObject)
            ?.enqueue(object : Callback<specificStudentdata?> {
                override fun onResponse(
                    call: Call<specificStudentdata?>, response: Response<specificStudentdata?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "specifictuterstudent",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {

                                tuterspecificstudent.postValue(response.body())

                            } else {
                                tuterspecificstudent.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            tuterspecificstudent.postValue(null)
                        } else {
                            tuterspecificstudent.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<specificStudentdata?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    tuterspecificstudent.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }

            })
    }

    val getSpecificstudenttutor: LiveData<specificStudentdata?>
        get() = tuterspecificstudent


    fun Getspecificstudentdatasubject(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Getspecificstudentsubject(jsonObject)
            ?.enqueue(object : Callback<specificStudentdata?> {
                override fun onResponse(
                    call: Call<specificStudentdata?>, response: Response<specificStudentdata?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "specifictuterstudent",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {

                                tuterspecificstudent.postValue(response.body())

                            } else {
                                tuterspecificstudent.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            tuterspecificstudent.postValue(null)
                        } else {
                            tuterspecificstudent.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<specificStudentdata?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    tuterspecificstudent.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getSpecificstudentsubject: LiveData<specificStudentdata?>
        get() = sublectspecificstudent


    fun GetspecificstudentdataAttendance(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetspecificstudentAttendance(jsonObject)
            ?.enqueue(object : Callback<specificStudentdata?> {
                override fun onResponse(
                    call: Call<specificStudentdata?>, response: Response<specificStudentdata?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "specifictuterstudent",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {

                                SpecificStudentAttendance.postValue(response.body())

                            } else {
                                SpecificStudentAttendance.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            SpecificStudentAttendance.postValue(null)
                        } else {
                            SpecificStudentAttendance.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<specificStudentdata?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    SpecificStudentAttendance.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val SpecificStudentAttendanceName: LiveData<specificStudentdata?>
        get() = SpecificStudentAttendance


    fun Getdepartmentcourse(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Getdepartmentcourse(jsonObject)
            ?.enqueue(object : Callback<department_course?> {
                override fun onResponse(
                    call: Call<department_course?>, response: Response<department_course?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Getdepartmentcourse",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val status = response.body()!!.status
                            if (status == 1) {

                                coursedepartment.postValue(response.body())

                            } else {
                                coursedepartment.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            coursedepartment.postValue(null)
                        } else {
                            coursedepartment.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<department_course?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    coursedepartment.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getcoursedepartment: LiveData<department_course?>
        get() = coursedepartment


    fun Getyearandsection(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Getyearandsection(jsonObject)
            ?.enqueue(object : Callback<Yearandsection?> {
                override fun onResponse(
                    call: Call<Yearandsection?>, response: Response<Yearandsection?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Getyearandsection",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                Yearandsection.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                Yearandsection.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            Yearandsection.postValue(null)
                        } else {
                            Yearandsection.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<Yearandsection?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Yearandsection.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val getyearandsection: LiveData<Yearandsection?>
        get() = Yearandsection


    fun GetyearandsectionList(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetyearandsectionList(jsonObject)
            ?.enqueue(object : Callback<Yearandsection?> {
                override fun onResponse(
                    call: Call<Yearandsection?>, response: Response<Yearandsection?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Getyearandsection",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                Yearandsection.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                Yearandsection.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            Yearandsection.postValue(null)
                        } else {
                            Yearandsection.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<Yearandsection?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Yearandsection.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val GetYearandSection: LiveData<Yearandsection?>
        get() = Yearandsection


    fun examsectionandsubject(jsonObject: JsonObject?, activity: Activity) {
        progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Examsectionandsubject(jsonObject)
            ?.enqueue(object : Callback<Section_and_Subject?> {
                override fun onResponse(
                    call: Call<Section_and_Subject?>, response: Response<Section_and_Subject?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Getyearandsection",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                sectionandsubject.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                sectionandsubject.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            sectionandsubject.postValue(null)
                        } else {
                            sectionandsubject.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<Section_and_Subject?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    sectionandsubject.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val ExamSectionandsubject: LiveData<Section_and_Subject?>
        get() = sectionandsubject


    fun Examcreation(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Examcreation(jsonObject)
            ?.enqueue(object : Callback<ExamCreation_dataclass?> {
                override fun onResponse(
                    call: Call<ExamCreation_dataclass?>, response: Response<ExamCreation_dataclass?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Getyearandsection",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                Examcreation.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                Examcreation.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            Examcreation.postValue(null)
                        } else {
                            Examcreation.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<ExamCreation_dataclass?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Examcreation.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }


    val Examcreationdata: LiveData<ExamCreation_dataclass?>
        get() = Examcreation


    fun ExamEdit(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.ExamEditdata(jsonObject)
            ?.enqueue(object : Callback<ExamCreation_dataclass?> {
                override fun onResponse(
                    call: Call<ExamCreation_dataclass?>, response: Response<ExamCreation_dataclass?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Getyearandsection",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                ExamCreationEdit.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                ExamCreationEdit.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            ExamCreationEdit.postValue(null)
                        } else {
                            ExamCreationEdit.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<ExamCreation_dataclass?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    ExamCreationEdit.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val ExamEdit: LiveData<ExamCreation_dataclass?>
        get() = ExamCreationEdit

    fun ExamSectionDelete(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.ExamDeleteSection(jsonObject)
            ?.enqueue(object : Callback<ExamCreation_dataclass?> {
                override fun onResponse(
                    call: Call<ExamCreation_dataclass?>, response: Response<ExamCreation_dataclass?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Getyearandsection",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                ExamDeleteSection.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                ExamDeleteSection.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            ExamDeleteSection.postValue(null)
                        } else {
                            ExamDeleteSection.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<ExamCreation_dataclass?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    ExamDeleteSection.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val ExamSectionDelete: LiveData<ExamCreation_dataclass?>
        get() = ExamDeleteSection

    fun Examview(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Examview(jsonObject)
            ?.enqueue(object : Callback<Examlist_viewmodel?> {
                override fun onResponse(
                    call: Call<Examlist_viewmodel?>, response: Response<Examlist_viewmodel?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Examviewmodel",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                Examviewmodel.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                Examviewmodel.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            Examviewmodel.postValue(null)
                        } else {
                            Examviewmodel.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<Examlist_viewmodel?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Examviewmodel.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Examview: LiveData<Examlist_viewmodel?>
        get() = Examviewmodel


    fun ExamviewSubjecr(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.ExamviewSubjectList(jsonObject)
            ?.enqueue(object : Callback<ExamSubjectList?> {
                override fun onResponse(
                    call: Call<ExamSubjectList?>, response: Response<ExamSubjectList?>
                ) {
                    progressDialog!!.dismiss()

                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                ExamviewmodelSubjectList.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                ExamviewmodelSubjectList.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            ExamviewmodelSubjectList.postValue(null)
                        } else {
                            ExamviewmodelSubjectList.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<ExamSubjectList?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    ExamviewmodelSubjectList.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val ExamviewSubject: LiveData<ExamSubjectList?>
        get() = ExamviewmodelSubjectList


    fun Examdeletedata(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Examdelete(jsonObject)
            ?.enqueue(object : Callback<ExamDelete?> {
                override fun onResponse(
                    call: Call<ExamDelete?>, response: Response<ExamDelete?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Examdelete",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                Examdeleteapi.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                Examdeleteapi.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            Examdeleteapi.postValue(null)
                        } else {
                            Examdeleteapi.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<ExamDelete?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Examdeleteapi.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Examdelete: LiveData<ExamDelete?>
        get() = Examdeleteapi


    fun TakeAttendance(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Markattendance(jsonObject)
            ?.enqueue(object : Callback<AttendanceMark?> {
                override fun onResponse(
                    call: Call<AttendanceMark?>, response: Response<AttendanceMark?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Examdelete",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                MarkAttendance.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                MarkAttendance.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            MarkAttendance.postValue(null)
                        } else {
                            MarkAttendance.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<AttendanceMark?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    MarkAttendance.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val TakeAttendance: LiveData<AttendanceMark?>
        get() = MarkAttendance


    fun VideoEntireSend(jsonObject: JsonObject?, activity: Activity) {
//        val progressDialog = CustomLoading.createProgressDialog(activity)
//        progressDialog!!.show()
        RestClient.apiInterfaces.Videoentiresend(jsonObject)
            ?.enqueue(object : Callback<VideoEntireSend?> {
                override fun onResponse(
                    call: Call<VideoEntireSend?>, response: Response<VideoEntireSend?>
                ) {
//                    progressDialog!!.dismiss()
                    Log.d(
                        "Examdelete",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

//                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                VideosendEntire.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                VideosendEntire.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                         //   progressDialog!!.dismiss()
                            VideosendEntire.postValue(null)
                        } else {
                            VideosendEntire.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<VideoEntireSend?>, t: Throwable) {
//                    progressDialog!!.dismiss()
                    VideosendEntire.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Videosendentire: LiveData<VideoEntireSend?>
        get() = VideosendEntire


    fun VideoParticulerSend(jsonObject: JsonObject?, activity: Activity) {
//        val progressDialog = CustomLoading.createProgressDialog(activity)
//        progressDialog!!.show()
        RestClient.apiInterfaces.VideoParticulersend(jsonObject)
            ?.enqueue(object : Callback<VideoParticulerSend?> {
                override fun onResponse(
                    call: Call<VideoParticulerSend?>, response: Response<VideoParticulerSend?>
                ) {
//                    progressDialog!!.dismiss()
                    Log.d(
                        "Examdelete",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

//                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                VideoParticulersend.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                VideoParticulersend.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
//                            progressDialog!!.dismiss()
                            VideoParticulersend.postValue(null)
                        } else {
                            VideoParticulersend.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<VideoParticulerSend?>, t: Throwable) {
//                    progressDialog!!.dismiss()
                    VideoParticulersend.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Videosendparticuler: LiveData<VideoParticulerSend?>
        get() = VideoParticulersend


    fun EventpicUpdate(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Eventphotoupdate(jsonObject)
            ?.enqueue(object : Callback<EventpicUpdate?> {
                override fun onResponse(
                    call: Call<EventpicUpdate?>, response: Response<EventpicUpdate?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Examdelete",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                Eventimageupdate.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                Eventimageupdate.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            Eventimageupdate.postValue(null)
                        } else {
                            Eventimageupdate.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<EventpicUpdate?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Eventimageupdate.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Eventpictureupdate: LiveData<EventpicUpdate?>
        get() = Eventimageupdate


    fun ExamEditorDeleteSection(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.ExamEditOrDelete(jsonObject)
            ?.enqueue(object : Callback<Examlist_viewmodel?> {
                override fun onResponse(
                    call: Call<Examlist_viewmodel?>, response: Response<Examlist_viewmodel?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Examdelete",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                ExamEditSection.postValue(response.body())
                                Log.d("Yearand_section", Yearandsection.toString())

                            } else {
                                ExamEditSection.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            ExamEditSection.postValue(null)
                        } else {
                            ExamEditSection.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<Examlist_viewmodel?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    ExamEditSection.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val ExamEditordelete: LiveData<Examlist_viewmodel?>
        get() = ExamEditSection


    fun AssignmentForward(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Assignmentforword(jsonObject)
            ?.enqueue(object : Callback<Assignment_Forward?> {
                override fun onResponse(
                    call: Call<Assignment_Forward?>, response: Response<Assignment_Forward?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "Examdelete",
                        response.code().toString() + " - " + response.toString()

                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                progressDialog!!.dismiss()
                                Assignmentforworddata.postValue(response.body())
                                Log.d("Yearandsection", Yearandsection.toString())

                            } else {
                                Assignmentforworddata.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            Assignmentforworddata.postValue(null)
                        } else {
                            Assignmentforworddata.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<Assignment_Forward?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Assignmentforworddata.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Assignmemntforward: LiveData<Assignment_Forward?>
        get() = Assignmentforworddata

    fun Chatlistdata(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog.show()
        RestClient.apiInterfaces.Chatlist(jsonObject)
            ?.enqueue(object : Callback<Chat_Text_model?> {
                override fun onResponse(
                    call: Call<Chat_Text_model?>, response: Response<Chat_Text_model?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d("Chatlistdata", response.code().toString() + " - " + response.toString())
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                progressDialog!!.dismiss()
                                Chatlistdata.postValue(response.body())
                                Log.d("Chatlistdata", Chatlistdata.toString())

                            } else {
                                Chatlistdata.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            Chatlistdata.postValue(null)
                        } else {
                            Chatlistdata.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<Chat_Text_model?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Chatlistdata.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Chatlist: LiveData<Chat_Text_model?>
        get() = Chatlistdata


    fun ChatStudent(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Chatstudent(jsonObject)
            ?.enqueue(object : Callback<Chat_Student?> {
                override fun onResponse(
                    call: Call<Chat_Student?>, response: Response<Chat_Student?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d("ChatStudent", response.code().toString() + " - " + response.toString())
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                progressDialog!!.dismiss()
                                ChatStudent.postValue(response.body())
                                Log.d("ChatStudent", ChatStudent.toString())

                            } else {
                                progressDialog!!.dismiss()
                                ChatStudent.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            ChatStudent.postValue(null)
                        } else {
                            ChatStudent.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<Chat_Student?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    ChatStudent.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val chatstudentlist: LiveData<Chat_Student?>
        get() = ChatStudent


    fun ChatStaff(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Chatstaff(jsonObject)
            ?.enqueue(object : Callback<SenderSide_ReplayChat?> {
                override fun onResponse(
                    call: Call<SenderSide_ReplayChat?>, response: Response<SenderSide_ReplayChat?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d("ChatStudent", response.code().toString() + " - " + response.toString())
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status

                            if (Status == 1) {

                                progressDialog!!.dismiss()
                                Sendersidechat.postValue(response.body())
                                Log.d("ChatStudent123", Sendersidechat.toString())

                            } else {
                                progressDialog!!.dismiss()
                                Sendersidechat.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            Sendersidechat.postValue(null)
                        } else {
                            progressDialog!!.dismiss()
                            Sendersidechat.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<SenderSide_ReplayChat?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Sendersidechat.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val SenderSideReplay: LiveData<SenderSide_ReplayChat?>
        get() = Sendersidechat


    fun SendersideChat(jsonObject: JsonObject?, activity: Activity) {
        var progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.CharSenderside(jsonObject)
            ?.enqueue(object : Callback<SenderSide_ChatModel?> {
                override fun onResponse(
                    call: Call<SenderSide_ChatModel?>, response: Response<SenderSide_ChatModel?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d("SenderChat", response.code().toString() + " - " + response.toString())
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.result

                            if (Status.equals("1")) {

                                progressDialog!!.dismiss()
                                SenderChat.postValue(response.body())
                                Log.d("SenderChat123", SenderChat.toString())


                            } else {
                                progressDialog!!.dismiss()
                                SenderChat.postValue(response.body())
                            }

                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {

                            progressDialog.dismiss()
                            SenderChat.postValue(null)

                        } else {
                            progressDialog.dismiss()
                            SenderChat.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<SenderSide_ChatModel?>, t: Throwable) {
                    progressDialog.dismiss()
                    SenderChat.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val sendersidechat: LiveData<SenderSide_ChatModel?>
        get() = SenderChat


    fun Assignmentsubmit(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Assignmentsubmit(jsonObject)
            ?.enqueue(object : Callback<Assignment_Submit?> {
                override fun onResponse(
                    call: Call<Assignment_Submit?>, response: Response<Assignment_Submit?>
                ) {
                    progressDialog!!.dismiss()

                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {

                                Assignmentsubmit.postValue(response.body())

                            } else {
                                Assignmentsubmit.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            Assignmentsubmit.postValue(null)
                        } else {
                            Assignmentsubmit.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<Assignment_Submit?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Assignmentsubmit.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Assignment_submited: LiveData<Assignment_Submit?>
        get() = Assignmentsubmit


    fun AssignmentsubmitSender(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Assignmentsubmitbtnsender(jsonObject)
            ?.enqueue(object : Callback<Assignment_Submittion?> {
                override fun onResponse(
                    call: Call<Assignment_Submittion?>, response: Response<Assignment_Submittion?>
                ) {
                    progressDialog!!.dismiss()

                    //  if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {

                        progressDialog.dismiss()
                        val Status = response.body()!!.Status
                        if (Status == 1) {
                            progressDialog.dismiss()
                            Assignmentsendersubmittion.postValue(response.body())
                        } else {
                            Assignmentsendersubmittion.postValue(response.body())
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog.dismiss()
                        Assignmentsendersubmittion.postValue(null)
                    } else {
                        Assignmentsendersubmittion.postValue(null)
                    }
                    //    }
                }

                override fun onFailure(call: Call<Assignment_Submittion?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Assignmentsendersubmittion.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val AssignmentsubmittionSender: LiveData<Assignment_Submittion?>
        get() = Assignmentsendersubmittion


    fun GetSubmittedAssignmentForStudents(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetSubmittedAssignmentForStudents(jsonObject)
            ?.enqueue(object : Callback<Assignment_Submittion?> {
                override fun onResponse(
                    call: Call<Assignment_Submittion?>, response: Response<Assignment_Submittion?>
                ) {
                    progressDialog!!.dismiss()

                    //  if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {

                        progressDialog!!.dismiss()
                        val Status = response.body()!!.Status
                        if (Status == 1) {
                            progressDialog!!.dismiss()
                            StuentforAssignmensubmitted.postValue(response.body())

                        } else {
                            StuentforAssignmensubmitted.postValue(response.body())
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        StuentforAssignmensubmitted.postValue(null)
                    } else {
                        StuentforAssignmensubmitted.postValue(null)
                    }
                    //  }
                }

                override fun onFailure(call: Call<Assignment_Submittion?>, t: Throwable) {
                    progressDialog.dismiss()
                    StuentforAssignmensubmitted.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val AssignmentsubmittionforStudent: LiveData<Assignment_Submittion?>
        get() = StuentforAssignmensubmitted


    fun AttendanceEdit(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.AttendanceEditList(jsonObject)
            ?.enqueue(object : Callback<Attendance_Edit?> {
                override fun onResponse(
                    call: Call<Attendance_Edit?>, response: Response<Attendance_Edit?>
                ) {
                    progressDialog!!.dismiss()

                    //   if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {

                        progressDialog!!.dismiss()
                        val Status = response.body()!!.Status
                        if (Status == 1) {

                            AttendanceEdit.postValue(response.body())

                        } else {
                            AttendanceEdit.postValue(response.body())
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        AttendanceEdit.postValue(null)
                    } else {
                        AttendanceEdit.postValue(null)
                    }
                    //   }
                }

                override fun onFailure(call: Call<Attendance_Edit?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    AttendanceEdit.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val AttendanceEditList: LiveData<Attendance_Edit?>
        get() = AttendanceEdit


    fun AssignmentcontentView(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Assignmentcotentview(jsonObject)
            ?.enqueue(object : Callback<AssignmentContent_View?> {
                override fun onResponse(
                    call: Call<AssignmentContent_View?>, response: Response<AssignmentContent_View?>
                ) {
                    progressDialog!!.dismiss()

                    //  if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {

                        progressDialog!!.dismiss()
                        val Status = response.body()!!.Status
                        if (Status == 1) {

                            AssignmentContent_View.postValue(response.body())

                        } else {
                            AssignmentContent_View.postValue(response.body())
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        AssignmentContent_View.postValue(null)
                    } else {
                        AssignmentContent_View.postValue(null)
                    }
                    // }
                }

                override fun onFailure(call: Call<AssignmentContent_View?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    AssignmentContent_View.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Assignmentcontentview: LiveData<AssignmentContent_View?>
        get() = AssignmentContent_View


    fun BlackStudent(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.BlackStudent(jsonObject)
            ?.enqueue(object : Callback<BlackStudent?> {
                override fun onResponse(
                    call: Call<BlackStudent?>, response: Response<BlackStudent?>
                ) {
                    progressDialog!!.dismiss()

                    //   if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {

                        progressDialog!!.dismiss()
                        val Status = response.body()!!.Status
                        if (Status == 1) {

                            BlackStudent.postValue(response.body())

                        } else {
                            BlackStudent.postValue(response.body())
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        BlackStudent.postValue(null)
                    } else {
                        BlackStudent.postValue(null)
                    }
                    //  }
                }

                override fun onFailure(call: Call<BlackStudent?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    BlackStudent.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val BlackStudentparticuler: LiveData<BlackStudent?>
        get() = BlackStudent


    fun UnBlackStudent(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.UnBlackStudent(jsonObject)
            ?.enqueue(object : Callback<Unblack_student?> {
                override fun onResponse(
                    call: Call<Unblack_student?>, response: Response<Unblack_student?>
                ) {
                    progressDialog!!.dismiss()

                    //     if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {

                        progressDialog!!.dismiss()
                        val Status = response.body()!!.Status
                        if (Status == 1) {

                            UnBlackStudent.postValue(response.body())

                        } else {
                            UnBlackStudent.postValue(response.body())
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        UnBlackStudent.postValue(null)
                    } else {
                        UnBlackStudent.postValue(null)
                    }
                    // }
                }

                override fun onFailure(call: Call<Unblack_student?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    UnBlackStudent.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val unBlackStudent: LiveData<Unblack_student?>
        get() = UnBlackStudent

    fun VideoSendTuter(jsonObject: JsonObject?, activity: Activity) {
//        val progressDialog = CustomLoading.createProgressDialog(activity)
//        progressDialog!!.show()
        RestClient.apiInterfaces.VideoSendtuter(jsonObject)
            ?.enqueue(object : Callback<VideoSendTuter?> {
                override fun onResponse(
                    call: Call<VideoSendTuter?>, response: Response<VideoSendTuter?>
                ) {
                  //  progressDialog!!.dismiss()

                    //     if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {

//                        progressDialog!!.dismiss()
                        val Status = response.body()!!.Status
                        if (Status == 1) {

                            Videosendtuter.postValue(response.body())

                        } else {
                            Videosendtuter.postValue(response.body())
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                     //   progressDialog!!.dismiss()
                        Videosendtuter.postValue(null)
                    } else {
                        Videosendtuter.postValue(null)
                    }
                    //   }
                }

                override fun onFailure(call: Call<VideoSendTuter?>, t: Throwable) {
                 //   progressDialog!!.dismiss()
                    Videosendtuter.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val VideoTuterSend: LiveData<VideoSendTuter?>
        get() = Videosendtuter


    fun GetOpt(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.GetOtp(jsonObject)
            ?.enqueue(object : Callback<ExampleJson2KtKotlin?> {
                override fun onResponse(
                    call: Call<ExampleJson2KtKotlin?>, response: Response<ExampleJson2KtKotlin?>
                ) {
                    progressDialog!!.dismiss()

                    //     if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {

                        progressDialog!!.dismiss()
                        val Status = response.body()!!.Status
                        if (Status == 1) {

                            GetOtp.postValue(response.body())

                        } else {
                            GetOtp.postValue(response.body())
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        GetOtp.postValue(null)
                    } else {
                        GetOtp.postValue(null)
                    }
                    //   }
                }

                override fun onFailure(call: Call<ExampleJson2KtKotlin?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetOtp.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val GetOtpNew: LiveData<ExampleJson2KtKotlin?>
        get() = GetOtp


    fun VerifirdOtp(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.Otpverify(jsonObject)
            ?.enqueue(object : Callback<Verified_OTP?> {
                override fun onResponse(
                    call: Call<Verified_OTP?>, response: Response<Verified_OTP?>
                ) {
                    progressDialog!!.dismiss()

                    //  if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {

                        progressDialog!!.dismiss()
                        val Status = response.body()!!.Status
                        if (Status == 1) {
                            OtpVerified.postValue(response.body())

                        } else {
                            OtpVerified.postValue(response.body())

                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        OtpVerified.postValue(null)
                    } else {
                        OtpVerified.postValue(null)
                    }
                    //  }
                }

                override fun onFailure(call: Call<Verified_OTP?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    OtpVerified.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val OtpVerifed: LiveData<Verified_OTP?>
        get() = OtpVerified


    fun CreateNewPassword(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.CreareNewPassword(jsonObject)
            ?.enqueue(object : Callback<NewPassWordCreate?> {
                override fun onResponse(
                    call: Call<NewPassWordCreate?>, response: Response<NewPassWordCreate?>
                ) {
                    progressDialog!!.dismiss()

                    // if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {

                        progressDialog!!.dismiss()
                        val Status = response.body()!!.Status
                        if (Status == 1) {

                            _newpassword.postValue(response.body())

                        } else {
                            _newpassword.postValue(response.body())
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        _newpassword.postValue(null)
                    } else {
                        _newpassword.postValue(null)
                    }
                    // }
                }

                override fun onFailure(call: Call<NewPassWordCreate?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    _newpassword.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Passwordcreate: LiveData<NewPassWordCreate?>
        get() = _newpassword


    fun Tuterimageorpdfsend(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.imgageandpdfTuterSend(jsonObject)
            ?.enqueue(object : Callback<ImageorpdfsendTuter?> {
                override fun onResponse(
                    call: Call<ImageorpdfsendTuter?>, response: Response<ImageorpdfsendTuter?>
                ) {
                    progressDialog!!.dismiss()

                    //  if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {

                        progressDialog!!.dismiss()
                        val Status = response.body()!!.Status
                        if (Status == 1) {

                            Pdf_andImagesendTuter.postValue(response.body())

                        } else {
                            Pdf_andImagesendTuter.postValue(response.body())
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        Pdf_andImagesendTuter.postValue(null)
                    } else {
                        Pdf_andImagesendTuter.postValue(null)
                    }
                    //  }
                }

                override fun onFailure(call: Call<ImageorpdfsendTuter?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    Pdf_andImagesendTuter.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val Tuterimageorpdfsend: LiveData<ImageorpdfsendTuter?>
        get() = Pdf_andImagesendTuter

    fun Texthistory(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog.show()
        RestClient.apiInterfaces.GetTextMessageHistory(jsonObject)
            ?.enqueue(object : Callback<textHistory?> {
                override fun onResponse(
                    call: Call<textHistory?>, response: Response<textHistory?>
                ) {
                    progressDialog.dismiss()
                    Log.d("ResponseCode", response.code().toString())
                    //    if (response.code() == 200 || response.code() == 201) {
                    if (response.body() != null) {
                        progressDialog.dismiss()
                        val Status = response.body()!!.Status
                        if (Status == 1) {
                            TextHistory_.postValue(response.body())
                        } else {
                            TextHistory_.postValue(response.body())
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog.dismiss()
                        TextHistory_.postValue(response.body())
                    } else {
                        TextHistory_.postValue(response.body())
                    }
                    //   }
                }

                override fun onFailure(call: Call<textHistory?>, t: Throwable) {
                    progressDialog.dismiss()
                    TextHistory_.postValue(null)
                    t.printStackTrace()
                    Log.d("Error", t.toString())
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val text_history: LiveData<textHistory?>
        get() = TextHistory_


    fun Voicehistory(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog.show()
        RestClient.apiInterfaces.GetVoiceMessageHistory(jsonObject)
            ?.enqueue(object : Callback<voicehistory?> {
                override fun onResponse(
                    call: Call<voicehistory?>, response: Response<voicehistory?>
                ) {
                    progressDialog.dismiss()
                    Log.d("ResponseCode", response.code().toString())
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog.dismiss()
                            val Status = response.body()!!.Status
                            if (Status == 1) {
                                VoiceHistory_.postValue(response.body())
                            } else {
                                VoiceHistory_.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog.dismiss()
                            VoiceHistory_.postValue(null)
                        } else {
                            VoiceHistory_.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<voicehistory?>, t: Throwable) {
                    progressDialog.dismiss()
                    VoiceHistory_.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val voicehistory_: LiveData<voicehistory?>
        get() = VoiceHistory_

    fun CollageListdata(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog!!.show()
        RestClient.apiInterfaces.HeaderCollagelist(jsonObject)
            ?.enqueue(object : Callback<CollageList?> {
                override fun onResponse(
                    call: Call<CollageList?>, response: Response<CollageList?>
                ) {
                    progressDialog!!.dismiss()

                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {

                            progressDialog!!.dismiss()
                            val Status = response.body()!!.status
                            if (Status == 1) {

                                CollageListHeaderSent.postValue(response.body())

                            } else {
                                CollageListHeaderSent.postValue(response.body())
                            }
                        } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                            progressDialog!!.dismiss()
                            CollageListHeaderSent.postValue(null)
                        } else {
                            CollageListHeaderSent.postValue(null)
                        }
                    }
                }

                override fun onFailure(call: Call<CollageList?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    CollageListHeaderSent.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity, activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }

    val CollageListdataHeader: LiveData<CollageList?>
        get() = CollageListHeaderSent


    fun GetHallticketdata(jsonObject: JsonObject?, activity: Activity) {
        val progressDialog = CustomLoading.createProgressDialog(activity)
        progressDialog.show()
        RestClient.Companion.apiInterfaces.Get_hallticket(jsonObject)
            ?.enqueue(object : Callback<Hallticket?> {
                override fun onResponse(
                    call: Call<Hallticket?>,
                    response: Response<Hallticket?>
                ) {
                    progressDialog!!.dismiss()
                    Log.d(
                        "NotificationResponse",
                        response.code().toString() + " - " + response.toString()
                    )
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            progressDialog!!.dismiss()
                            val status = response.body()!!.Status
                            if (status == 1) {
                                GetHallticket.postValue(response.body())
                            } else {
                                CommonUtil.ApiAlertFinish(activity, response.body()!!.Message)
                            }
                        }
                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        progressDialog!!.dismiss()
                        CommonUtil.ApiAlertFinish(
                            activity,
                            activity.getString(R.string.txt_no_record_found)
                        )
                    } else {
                        CommonUtil.ApiAlertFinish(
                            activity,
                            activity.getString(R.string.txt_no_record_found)
                        )
                    }
                }

                override fun onFailure(call: Call<Hallticket?>, t: Throwable) {
                    progressDialog!!.dismiss()
                    GetHallticket.postValue(null)
                    t.printStackTrace()
                    CommonUtil.ApiAlertFinish(
                        activity,
                        activity.getString(R.string.txt_no_record_found)
                    )
                }
            })
    }


    val hallticketdataget: LiveData<Hallticket?>
        get() = GetHallticket


}