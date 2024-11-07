package com.vsca.vsnapvoicecollege.Interfaces

import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Model.AssignmentContent_View
import com.vsca.vsnapvoicecollege.Model.AssignmentMark
import com.vsca.vsnapvoicecollege.Model.Assignment_Forward
import com.vsca.vsnapvoicecollege.Model.Assignment_Submit
import com.vsca.vsnapvoicecollege.Model.Assignment_Submittion
import com.vsca.vsnapvoicecollege.Model.Assignmentsent
import com.vsca.vsnapvoicecollege.Model.AttendanceMark
import com.vsca.vsnapvoicecollege.Model.AttendanceResponse
import com.vsca.vsnapvoicecollege.Model.Attendance_Checking
import com.vsca.vsnapvoicecollege.Model.Attendance_Edit
import com.vsca.vsnapvoicecollege.Model.AttendancemardkingResponse
import com.vsca.vsnapvoicecollege.Model.BlackStudent
import com.vsca.vsnapvoicecollege.Model.Chat_StaffList
import com.vsca.vsnapvoicecollege.Model.Chat_Student
import com.vsca.vsnapvoicecollege.Model.Chat_Text_model
import com.vsca.vsnapvoicecollege.Model.CollageList
import com.vsca.vsnapvoicecollege.Model.Communication_AddNewButton
import com.vsca.vsnapvoicecollege.Model.CountryDetailsResponse
import com.vsca.vsnapvoicecollege.Model.DashBoardResponse
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
import com.vsca.vsnapvoicecollege.Model.GetNotificationsResponse
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
import com.vsca.vsnapvoicecollege.Model.LeaveApplicationDelete
import com.vsca.vsnapvoicecollege.Model.LeaveHistoryResponse
import com.vsca.vsnapvoicecollege.Model.LeaveRequest
import com.vsca.vsnapvoicecollege.Model.LeaveTypeResponse
import com.vsca.vsnapvoicecollege.Model.Leave_history
import com.vsca.vsnapvoicecollege.Model.LoginResponse
import com.vsca.vsnapvoicecollege.Model.ManageLeave
import com.vsca.vsnapvoicecollege.Model.MenuResponse
import com.vsca.vsnapvoicecollege.Model.NewPassWordCreate
import com.vsca.vsnapvoicecollege.Model.NoticeBoardSMSsend
import com.vsca.vsnapvoicecollege.Model.PunchHistoryRes
import com.vsca.vsnapvoicecollege.Model.Section_and_Subject
import com.vsca.vsnapvoicecollege.Model.SemesterAndSectionListResposne
import com.vsca.vsnapvoicecollege.Model.SenderSide_ChatModel
import com.vsca.vsnapvoicecollege.Model.SenderSide_ReplayChat
import com.vsca.vsnapvoicecollege.Model.StaffAttendanceBiometricReportRes
import com.vsca.vsnapvoicecollege.Model.StaffBiometricLocationRes
import com.vsca.vsnapvoicecollege.Model.StaffListRes
import com.vsca.vsnapvoicecollege.Model.StatusMessageResponse
import com.vsca.vsnapvoicecollege.Model.StudentAttendancelist
import com.vsca.vsnapvoicecollege.Model.StudentAttendanceview
import com.vsca.vsnapvoicecollege.Model.Unblack_student
import com.vsca.vsnapvoicecollege.Model.ValidateMobileNumber
import com.vsca.vsnapvoicecollege.Model.Verified_OTP
import com.vsca.vsnapvoicecollege.Model.VersionCheckResposne
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
import com.vsca.vsnapvoicecollege.Repository.ApiMethods
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.SenderModel.GetCourseDepartmentResposne
import com.vsca.vsnapvoicecollege.SenderModel.GetDepartmentResponse
import com.vsca.vsnapvoicecollege.SenderModel.GetDivisionResponse
import com.vsca.vsnapvoicecollege.SenderModel.SenderStatusMessageData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url


interface ApiInterfaces {

    @Multipart
    @POST("api/AppDetailsBal/SendFileToEntireCollege")
    fun UploadVoicefileEntire(
        @Part("Info") requestBody: RequestBody?,
        @Part file: MultipartBody.Part
    ): Call<JsonObject>

    @Multipart
    @POST("api/AppDetailsBal/SendFileToParticularType")
    fun UploadVoicefileParticuler(
        @Part("Info") requestBody: RequestBody?,
        @Part file: MultipartBody.Part
    ): Call<JsonObject>

    @Multipart
    @POST("api/AppDetailsBal/SendFileToParticularTypeFromTutor")
    fun UploadVoicefileTuterSend(
        @Part("Info") requestBody: RequestBody?,
        @Part file: MultipartBody.Part
    ): Call<JsonObject>


    @GET(ApiMethods.Country)
    fun Getcountrylist(@Query(ApiRequestNames.Req_AppId) AppId: Int): Call<CountryDetailsResponse?>?

    @GET(ApiMethods.VersionCheck)
    fun VersionCheck(
        @Query(ApiRequestNames.Req_VersionID) versionID: Int,
        @Query(ApiRequestNames.Req_DeviceType) Devicetype: String
    ): Call<VersionCheckResposne?>?

    @GET(ApiMethods.GetProfileDetails)
    fun GetProfile(@Query(ApiRequestNames.Req_id) MemberId: Int): Call<GetProfileResponse?>?

    @POST(ApiMethods.Login)
    fun Login(@Body jsonObject: JsonObject?): Call<LoginResponse?>?

    @POST(ApiMethods.Dashboard)
    fun Dashboard(@Body jsonObject: JsonObject?): Call<DashBoardResponse?>?

    @POST(ApiMethods.UserMenus)
    fun GetUsermenu(@Body jsonObject: JsonObject?): Call<MenuResponse?>?

    @POST(ApiMethods.ValidateMobileNumber)
    fun ValidateMobileNumber(@Body jsonObject: JsonObject?): Call<ValidateMobileNumber?>?

    @POST(ApiMethods.GetNotifications)
    fun GetNotifications(@Body jsonObject: JsonObject?): Call<GetNotificationsResponse?>?

    @POST(ApiMethods.Hallticket)
    fun Get_hallticket(@Body jsonObject: JsonObject?): Call<Hallticket?>?

    @POST(ApiMethods.GetCourseDetails)
    fun GetCourseDetails(@Body jsonObject: JsonObject?): Call<GetCourseDetailsResposne?>?

    @POST(ApiMethods.GetExamApplicationDetails)
    fun GetExamApplicationDetails(@Body jsonObject: JsonObject?): Call<GetExamApplicationResponse?>?

    @POST(ApiMethods.GetNoticeboardListbyType)
    fun GetNoticeboardlistbytpe(@Body jsonObject: JsonObject?): Call<GetNoticeboardResposne?>?

    @POST(ApiMethods.DeleteNoticeboard)
    fun DeleteNoticeboard(@Body jsonObject: JsonObject?): Call<Delete_noticeboard?>?

    @POST(ApiMethods.GetCircularListbyType)
    fun GetCircularListbyType(@Body jsonObject: JsonObject?): Call<GetCircularListResponse?>?

    @POST(ApiMethods.GetAssignmentListByType)
    fun GetAssignmentListbytype(@Body jsonObject: JsonObject?): Call<GetAssignmentListResponse?>?

    @POST(ApiMethods.GetAssignmentMemberCount)
    fun GetAssignmentMemberCount(@Body jsonObject: JsonObject?): Call<GetAssignmentCountResponse?>?

    @POST(ApiMethods.ViewAssignmentContent)
    fun ViewAssignmentContent(@Body jsonObject: JsonObject?): Call<GetAssignmentViewContentResponse?>?

    @POST(ApiMethods.Appreadstatus)
    fun AppReadStatus(@Body jsonObject: JsonObject?): Call<StatusMessageResponse?>?

    @POST(ApiMethods.GetEventListByType)
    fun GetEventListByType(@Body jsonObject: JsonObject?): Call<GetEventListbyTypeResponse?>?

    @POST(ApiMethods.GetCommunicationVoiceBytype)
    fun GetCommunicationVoiceBytype(@Body jsonObject: JsonObject?): Call<GetCommunicationResponse?>?

    @POST(ApiMethods.GetCommunicationMessageBytype)
    fun GetCommunicationMessageBytype(@Body jsonObject: JsonObject?): Call<GetCommunicationResponse?>?

    @POST(ApiMethods.CommunicationVisible_Not)
    fun Communication_V_N(@Body jsonObject: JsonObject?): Call<Communication_AddNewButton?>?

    @POST(ApiMethods.GetOverallcountByMenuType)
    fun GetOverallcountByMenuType(@Body jsonObject: JsonObject?): Call<GetOverAllCountResposne?>?

    @POST(ApiMethods.FacultyList)
    fun GetFacultyList(@Body jsonObject: JsonObject?): Call<GetFacultyListResponse?>?

    @POST(ApiMethods.FacultyListStaff)
    fun GetFacultyListStaff(@Body jsonObject: JsonObject?): Call<GetFacultyListResponse?>?

    @POST(ApiMethods.Fcultylistforreveiver)
    fun faculrtlistRecevier(@Body jsonObject: JsonObject?): Call<GetFacultyListResponse?>?

    @POST(ApiMethods.semesterandsectionListforApp)
    fun GetSemesterSectionlist(@Body jsonObject: JsonObject?): Call<SemesterAndSectionListResposne?>?

    @POST(ApiMethods.GetExamListByType)
    fun GetExamListByType(@Body jsonObject: JsonObject?): Call<GetExamListResponse?>?

    @POST(ApiMethods.GetExamListByTypereciver)
    fun GetExamListByTypeReciver(@Body jsonObject: JsonObject?): Call<GetExamListResponse?>?

    @POST(ApiMethods.GetStudentMarkDetailsForApp)
    fun GetExamMarkForStudent(@Body jsonObject: JsonObject?): Call<ExamMarkListResponse?>?

    @POST(ApiMethods.GetVideoList)
    fun GetVideoList(@Body jsonObject: JsonObject?): Call<GetVideoListResponse?>?

    @POST(ApiMethods.getGraditContactNumberDetails)
    fun GraditContactNumberDetails(@Body jsonObject: JsonObject?): Call<GraditContectNumber?>?

    @POST(ApiMethods.getsemesterlistforcourseid)
    fun GetSemesterCreditsType(@Body jsonObject: JsonObject?): Call<GetSemesterWiseTypeResponse?>?

    @POST(ApiMethods.semesterwisestudentcreditdetails)
    fun GetSemesterWiseCredits(@Body jsonObject: JsonObject?): Call<GetSemesterWiseCreditResponse?>?

    @POST(ApiMethods.semesterwisestudentcreditdetailsall)
    fun GetSemesterWiseCreditsAll(@Body jsonObject: JsonObject?): Call<GetSemesterWiseCreditALLResponse?>?

    @POST(ApiMethods.getcategorylistforclgeid)
    fun GetCategoryCreditsType(@Body jsonObject: JsonObject?): Call<GetCategoryTypesResponse?>?

    @POST(ApiMethods.categorywisestudentcreditdetails)
    fun GetCategoryCreditWiseDetails(@Body jsonObject: JsonObject?): Call<GetCategoryWiseCreditResponse?>?

    @POST(ApiMethods.getattendanceforparent)
    fun GetAttendanceForParent(@Body jsonObject: JsonObject?): Call<AttendanceResponse?>?

    @POST(ApiMethods.studentAttendancelist)
    fun GetAttendanceStudentlist(@Body jsonObject: JsonObject?): Call<StudentAttendancelist?>?

    @POST(ApiMethods.GetLeaveApplicationListForReceiverApp)
    fun GetLeaveApplicationListForReceiverApp(@Body jsonObject: JsonObject?): Call<LeaveHistoryResponse?>?

    @POST(ApiMethods.GetLeaveType)
    fun GetLeaveType(@Body jsonObject: JsonObject?): Call<LeaveTypeResponse?>?

    @POST(ApiMethods.changepassword)
    fun ChangePassword(@Body jsonObject: JsonObject?): Call<StatusMessageResponse?>?

    @POST(ApiMethods.GetstaffdetailsForApp)
    fun GetstaffdetailsForApp(@Body jsonObject: JsonObject?): Call<GetStaffDetailsResponse?>?


    @POST(ApiMethods.GetStaffClassesforChatForApp)
    fun GetStaffClassesforChatForApp(@Body jsonObject: JsonObject?): Call<Chat_StaffList?>?

    @POST(ApiMethods.GetAddsForCollege)
    fun GetAdsForCollege(@Body jsonObject: JsonObject?): Call<GetAdvertisementResponse?>?


    @POST(ApiMethods.UpdateDeviceToken)
    fun UpdateDeviceToken(@Body jsonObject: JsonObject?): Call<StatusMessageResponse?>?

    @POST(ApiMethods.GetVideoContentRestriction)
    fun GetVideoContentRestriction(): Call<VideoRestrictionContentResponse?>?

    @Streaming
    @GET
    fun downloadFileWithDynamicUrlAsync(@Url fileUrl: String?): Call<ResponseBody?>?

    @Headers("Content-Type: application/json", "Accept: application/vnd.vimeo.*+json;version=3.4")
    @POST("/me/videos")
    fun VideoUpload(
        @Body jsonObject: JsonObject?, @Header("Authorization") head: String?
    ): Call<JsonObject>


    @Headers(
        "Tus-Resumable: 1.0.0",
        "Upload-Offset: 0",
        "Content-Type: application/offset+octet-stream",
        "Accept: application/vnd.vimeo.*+json;version=3.4"
    )
    @PUT("upload")
    fun patchVimeoVideoMetaData(
        @Query("ticket_id") ticketid: String?,
        @Query("video_file_id") videoid: String?,
        @Query("signature") signatureid: String?,
        @Query("v6") v6id: String?,
        @Query("redirect_url") redirecturl: String?,
        @Body file: RequestBody?
    ): Call<ResponseBody>

    //SenderApis

    @POST(ApiMethods.GetDivisions)
    fun GetDivisions(@Body jsonObject: JsonObject?): Call<GetDivisionResponse?>?

    @POST(ApiMethods.GetDepartment)
    fun GetDepartment(@Body jsonObject: JsonObject?): Call<GetDepartmentResponse?>?

    @POST(ApiMethods.GetCoursesByDepartment)
    fun GetCoursesByDepartment(@Body jsonObject: JsonObject?): Call<GetCourseDepartmentResposne?>?

    @POST(ApiMethods.SendSMSToEntireCollege)
    fun SendSMSToEntireCollege(@Body jsonObject: JsonObject?): Call<SenderStatusMessageData?>?

    @POST(ApiMethods.SendVoiceToEntireCollege)
    fun SendVoiceToEntireCollege(@Body jsonObject: JsonObject?): Call<SenderStatusMessageData?>?

    @POST(ApiMethods.SendSMSToEntiretutorandsubjectCollege)
    fun SendSMSToEntiretotorandsubjectCollege(@Body jsonObject: JsonObject?): Call<SenderStatusMessageData?>?

    @POST(ApiMethods.SendSMSToParticularType)
    fun SendSMSToParticularType(@Body jsonObject: JsonObject?): Call<SenderStatusMessageData?>?

    @POST(ApiMethods.GetGroup)
    fun GetGroupData(@Body jsonObject: JsonObject?): Call<GetGrouplist?>?

    @POST(ApiMethods.Getsubject)
    fun Getsubject(@Body jsonObject: JsonObject?): Call<staffsubject_list?>?

    @POST(ApiMethods.getAttendanceforStaff)
    fun GetAttendance(@Body jsonObject: JsonObject?): Call<GetAttendance?>?

    @POST(ApiMethods.GetStudentWiseAttendanceDetails)
    fun StudentSpecificstudent(@Body jsonObject: JsonObject?): Call<StudentAttendanceview?>?

    @POST(ApiMethods.Gettuter)
    fun Gettuter(@Body jsonObject: JsonObject?): Call<staffsubject_list?>?

    @POST(ApiMethods.Getspecificstudenttuter)
    fun Getspecificstudenttuter(@Body jsonObject: JsonObject?): Call<specificStudentdata?>?

    @POST(ApiMethods.Getsubjectspecifistudent)
    fun Getspecificstudentsubject(@Body jsonObject: JsonObject?): Call<specificStudentdata?>?

    @POST(ApiMethods.GetsubjectspecifistudentAttendance)
    fun GetspecificstudentAttendance(@Body jsonObject: JsonObject?): Call<specificStudentdata?>?

    @POST(ApiMethods.Getdepartmnetcourse)
    fun Getdepartmentcourse(@Body jsonObject: JsonObject?): Call<department_course?>?

    @POST(ApiMethods.GetYearandsection)
    fun Getyearandsection(@Body jsonObject: JsonObject?): Call<Yearandsection?>?

    @POST(ApiMethods.GetYearandsectionList)
    fun GetyearandsectionList(@Body jsonObject: JsonObject?): Call<Yearandsection?>?

    @POST(ApiMethods.Noticeboardsendsms)
    fun Noticeboardsms(@Body jsonObject: JsonObject?): Call<NoticeBoardSMSsend?>?

    @POST(ApiMethods.NoticeboardsendsmsTuter)
    fun NoticeboardsmsTuter(@Body jsonObject: JsonObject?): Call<NoticeBoardSMSsend?>?

    @POST(ApiMethods.ImageorPdf)
    fun ImageOrPdf(@Body jsonObject: JsonObject?): Call<ImageORpdfsend?>?

    @POST(ApiMethods.Imageorpdfparticuler)
    fun ImageOrPdfparticuler(@Body jsonObject: JsonObject?): Call<ImageORpdfsend?>?

    @POST(ApiMethods.Eventsenddata)
    fun Eventsend(@Body jsonObject: JsonObject?): Call<Evendsenddata?>?

    @POST(ApiMethods.EventsenddataTuter)
    fun EventsendTuter(@Body jsonObject: JsonObject?): Call<Evendsenddata?>?

    @POST(ApiMethods.Assignmentsenddata)
    fun Assignmentsend(@Body jsonObject: JsonObject?): Call<Assignmentsent?>?

    @POST(ApiMethods.AssignmentForwardText)
    fun AssignmentforwardText(@Body jsonObject: JsonObject?): Call<Assignmentsent?>?

    @POST(ApiMethods.AttendanceTaking)
    fun AttendanceMark(@Body jsonObject: JsonObject?): Call<AttendancemardkingResponse?>?

    @POST(ApiMethods.ManageLeaveApplication)
    fun ManageLeaveapplication(@Body jsonObject: JsonObject?): Call<ManageLeave?>?

    @POST(ApiMethods.GetLeaveapplicationListforsenderapp)
    fun Leavehistortprinciple(@Body jsonObject: JsonObject?): Call<Leave_history?>?

    @POST(ApiMethods.Examsectionandsubject)
    fun Examsectionandsubject(@Body jsonObject: JsonObject?): Call<Section_and_Subject?>?

    @POST(ApiMethods.ExamCreation)
    fun Examcreation(@Body jsonObject: JsonObject?): Call<ExamCreation_dataclass?>?

    @POST(ApiMethods.ExamSectionDelete)
    fun ExamEditdata(@Body jsonObject: JsonObject?): Call<ExamCreation_dataclass?>?

    @POST(ApiMethods.ExamSectionDelete)
    fun ExamDeleteSection(@Body jsonObject: JsonObject?): Call<ExamCreation_dataclass?>?

    @POST(ApiMethods.Examviewapi)
    fun Examview(@Body jsonObject: JsonObject?): Call<Examlist_viewmodel?>?

    @POST(ApiMethods.ExamviewapiSubjectList)
    fun ExamviewSubjectList(@Body jsonObject: JsonObject?): Call<ExamSubjectList?>?

    @POST(ApiMethods.Examdelete)
    fun Examdelete(@Body jsonObject: JsonObject?): Call<ExamDelete?>?

    @POST(ApiMethods.VideoEntireSend)
    fun Videoentiresend(@Body jsonObject: JsonObject?): Call<VideoEntireSend?>?

    @POST(ApiMethods.VideoParticulerSend)
    fun VideoParticulersend(@Body jsonObject: JsonObject?): Call<VideoParticulerSend?>?

    @POST("/api/AppDetailsBal/ExamCreation")
    fun Examdeletedata(@Body jsonObject: JsonObject?): Call<ExamDelete?>?

    @POST("/api/AppDetailsBal/ManageLeaveRequest")
    fun GetleaveApproidapi(@Body jsonObject: JsonObject?): Call<LeaveRequest?>?

    @POST("/api/AppDetailsBal/ManageLeaveapplication")
    fun LeaveApplicatinDelete(@Body jsonObject: JsonObject?): Call<LeaveApplicationDelete?>?

    @POST("/api/AppDetailsBal/ManageNoticeBoard")
    fun DeleteNoticeboarddata(@Body jsonObject: JsonObject?): Call<Delete_noticeboard?>?

    @POST("/api/AppDetailsBal/AssignmentDelete")
    fun AssignmentDelete(@Body jsonObject: JsonObject?): Call<Delete_noticeboard?>?

    @POST("/api/AppDetailsBal/AddMarksForAssignment")
    fun AssignmentMark(@Body jsonObject: JsonObject?): Call<AssignmentMark?>?

    @POST("/api/AppDetailsBal/ManageLeaveRequest")
    fun Leave_Reject(@Body jsonObject: JsonObject?): Call<Delete_noticeboard?>?

    @POST(ApiMethods.TakeAttendance)
    fun Markattendance(@Body jsonObject: JsonObject?): Call<AttendanceMark?>?

    @POST(ApiMethods.ExamtotalEditORDelete)
    fun ExamEditOrDelete(@Body jsonObject: JsonObject?): Call<Examlist_viewmodel?>?

    @POST(ApiMethods.Eventphotoupdate)
    fun Eventphotoupdate(@Body jsonObject: JsonObject?): Call<EventpicUpdate?>?

    @POST(ApiMethods.Assignmentforward)
    fun Assignmentforword(@Body jsonObject: JsonObject?): Call<Assignment_Forward?>?

    @POST(ApiMethods.Chatdatalist)
    fun Chatlist(@Body jsonObject: JsonObject?): Call<Chat_Text_model?>?

    @POST(ApiMethods.ChatStudent)
    fun Chatstudent(@Body jsonObject: JsonObject?): Call<Chat_Student?>?

    @POST(ApiMethods.ChatStaff)
    fun Chatstaff(@Body jsonObject: JsonObject?): Call<SenderSide_ReplayChat?>?

    @POST(ApiMethods.Sendersidechat)
    fun CharSenderside(@Body jsonObject: JsonObject?): Call<SenderSide_ChatModel?>?

    @POST(ApiMethods.Assignmentsubmited)
    fun Assignmentsubmit(@Body jsonObject: JsonObject?): Call<Assignment_Submit?>?

    @POST(ApiMethods.AssignmentSubmittion)
    fun Assignmentsubmitbtnsender(@Body jsonObject: JsonObject?): Call<Assignment_Submittion?>?

    @POST(ApiMethods.GetSubmittedAssignmentForStudents)
    fun GetSubmittedAssignmentForStudents(@Body jsonObject: JsonObject?): Call<Assignment_Submittion?>?

    @POST(ApiMethods.Attendance_Edit)
    fun AttendanceEditList(@Body jsonObject: JsonObject?): Call<Attendance_Edit?>?

    @POST(ApiMethods.Attendance_Check)
    fun Attendancecheck(@Body jsonObject: JsonObject?): Call<Attendance_Checking?>?

    @POST(ApiMethods.AssignmentView)
    fun Assignmentcotentview(@Body jsonObject: JsonObject?): Call<AssignmentContent_View?>?

    @POST(ApiMethods.BlackStudent)
    fun BlackStudent(@Body jsonObject: JsonObject?): Call<BlackStudent?>?

    @POST(ApiMethods.UnblackStudent)
    fun UnBlackStudent(@Body jsonObject: JsonObject?): Call<Unblack_student?>?

    @POST(ApiMethods.TuterVideoSend)
    fun VideoSendtuter(@Body jsonObject: JsonObject?): Call<VideoSendTuter?>?

    @POST(ApiMethods.GetOtp)
    fun GetOtp(@Body jsonObject: JsonObject?): Call<ExampleJson2KtKotlin?>?

    @POST(ApiMethods.VerifyOTP)
    fun Otpverify(@Body jsonObject: JsonObject?): Call<Verified_OTP?>?

    @POST(ApiMethods.CreateNewpassword)
    fun CreareNewPassword(@Body jsonObject: JsonObject?): Call<NewPassWordCreate?>?

    @POST(ApiMethods.imgageandpdfTuterSend)
    fun imgageandpdfTuterSend(@Body jsonObject: JsonObject?): Call<ImageorpdfsendTuter?>?

    @POST(ApiMethods.GetTextMessageHistory)
    fun GetTextMessageHistory(@Body jsonObject: JsonObject?): Call<textHistory?>?

    @POST(ApiMethods.GetVoiceMessageHistory)
    fun GetVoiceMessageHistory(@Body jsonObject: JsonObject?): Call<voicehistory?>?

    @POST(ApiMethods.HeaderCollagelist)
    fun HeaderCollagelist(@Body jsonObject: JsonObject?): Call<CollageList?>?

    @POST(ApiMethods.SendVoiceToParticularTypeFromHistory)
    fun SendVoiceToParticularTypeFromHistory(@Body jsonObject: JsonObject?): Call<SenderStatusMessageData?>?

    @POST(ApiMethods.SendVoiceToEntireCollegeFromHistory)
    fun SendVoiceEntireFromHistory(@Body jsonObject: JsonObject?): Call<SenderStatusMessageData?>?


    @GET("biometric-principal-attendance-report")
    fun getStaffWiseBiometricAttendanceReport(
        @Query("userId") userID: String?,
        @Query("attendance_month") monthID: String?,
        @Query("CollegeId") institiuteId: String?
    ): Call<StaffAttendanceBiometricReportRes?>?

    @GET("biometric-principal-attendance-report")
    fun getDayWiseBiometricAttendanceReport(
        @Query("attendance_dt") et: String?,
        @Query("CollegeId") institiuteId: String?
    ): Call<StaffAttendanceBiometricReportRes?>?

//    @POST("remove-biometric-location")
    @POST("/RemoveBiometricLocation")
    fun removeLocation(@Body jsonObject: JsonObject?): Call<JsonObject?>?

//    @POST("set-biometric-location")
    @POST("/SetBiometricLocation")
    fun addBiometricLocation(@Body jsonObject: JsonObject?): Call<JsonObject?>?

//    @GET("get-biometric-location-history")
    @GET("/GetBiometricLocationHistory")
    fun getExistingViewLocations(@Query("CollegeId") userID: String?): Call<StaffBiometricLocationRes?>?


//    @POST("update-biometric-location")
    @POST("/UpdateBiometricLocation")
    fun updateLocation(@Body jsonObject: JsonObject?): Call<JsonObject?>?

//    @GET("staff-list")
    @GET("/GetStaffListforBiometric")
    fun getStaffList(@Query("CollegeId") id: String?): Call<StaffListRes?>?


//    @GET("biometric-punch-history")
    @GET("/GetBiometricPunchHistory")
    fun viewPunchHistory(
        @Query("CollegeId") instituteId: String?,
        @Query("userId") userId: String?,
        @Query("from_date") from_date: String?,
        @Query("to_date") to_date: String?
    ): Call<PunchHistoryRes?>?


//    @GET("get-staff-biometric-location")
    @GET("/GetStaffLocationDetails")
    fun getStaffBiometricLocations(
        @Query("userId") userID: String?,
        @Query("CollegeId") schoolID: String?
    ): Call<StaffBiometricLocationRes?>?


//    @GET("biometric-staff-attendance-report")
    @GET("/GetBiometricStaffAttendance")
    fun getStaffBiometricAttendanceReport(
        @Query("userId") userID: String?,
        @Query("attendance_dt") monthID: String?,
        @Query("CollegeId") institiuteId: String?
    ): Call<StaffAttendanceBiometricReportRes?>?


//    @POST("biometric/biometric-entry-using-app")
    @POST("/BiometricEntryusingApp")
    fun BiometricEntryforAttendance(@Body jsonObject: JsonObject?): Call<JsonObject?>?

}