package com.vsca.vsnapvoicecollege.Repository

object ApiMethods {

    const val Country = "api/AppDetailsBal/Getcountrylist"
    const val VersionCheck = "api/AppDetailsBal/VersionCheck"
    const val Login = "api/AppDetailsBal/LoginFromApp"
    const val Dashboard = "api/AppDetailsBal/DashboardApi"

    // const val UserMenus = "api/AppDetailsBal/GetUsermenu"
    const val UserMenus = "api/AppDetailsBal/GetParentUserMenuWithReadWriteAccess"
    const val ValidateMobileNumber = "api/AppDetailsBal/ValidateMobileNumber"

    const val GetNotifications = "api/AppDetailsBal/GetNotificationForCollege"
    const val Hallticket = "api/AppDetailsBal/getHallTicketDetails"
    const val GetCourseDetails = "api/AppDetailsBal/GetSubjectDetailsForSemester"
    const val GetProfileDetails = "api/AppDetailsBal/GetProfileDetails"
    const val GetExamApplicationDetails = "api/AppDetailsBal/getExamApplicationDetails"
    const val GetNoticeboardListbyType = "api/AppDetailsBal/GetNoticeListByType"
    const val DeleteNoticeboard = "api/AppDetailsBal/ManageNoticeBoard"
    const val GetCircularListbyType = "api/AppDetailsBal/GetCircularListByType"
    const val GetAssignmentListByType = "api/AppDetailsBal/GetAssignmentListByType"
    const val GetAssignmentMemberCount = "api/AppDetailsBal/GetAssignmentMemberCount"
    const val ViewAssignmentContent = "api/AppDetailsBal/ViewAssignmentContent"
    const val Appreadstatus = "api/AppDetailsBal/Appreadstatus"
    const val GetEventListByType = "api/AppDetailsBal/GetEventListByType"
    const val GetCommunicationMessageBytype = "api/AppDetailsBal/GetTextMessageBytype"
    const val GetCommunicationVoiceBytype = "api/AppDetailsBal/GetVoiceMessageBytype"
    const val CommunicationVisible_Not = "api/AppDetailsBal/GetChildUserMenuWithReadWriteAccess"
    const val GetOverallcountByMenuType = "api/AppDetailsBal/GetOverallcountByMenuType"
    const val FacultyList = "api/AppDetailsBal/FacultyListforPrincipalLoginSenderApp"
    const val FacultyListStaff = "api/AppDetailsBal/FacultyListForSenderApp"
    const val Fcultylistforreveiver = "api/AppDetailsBal/FacultyList"
    const val semesterandsectionListforApp = "api/AppDetailsBal/semesterandsectionListforApp"
    const val GetExamListByType = "api/AppDetailsBal/GetExamListByTypeforsenderapp"
    const val GetExamListByTypereciver = "api/AppDetailsBal/GetExamListByType"
    const val GetStudentMarkDetailsForApp = "api/AppDetailsBal/GetStudentMarkDetailsForApp"
    const val GetVideoList = "api/AppDetailsBal/GetVideoList"
    const val getGraditContactNumberDetails = "api/AppDetailsBal/getGraditContactNumberDetails"
    const val getsemesterlistforcourseid = "api/AppDetailsBal/getsemesterlistforcourseid"
    const val semesterwisestudentcreditdetails =
        "api/AppDetailsBal/semesterwisestudentcreditdetails"
    const val semesterwisestudentcreditdetailsall =
        "api/AppDetailsBal/semesterwisestudentcreditdetailsall"
    const val getcategorylistforclgeid = "api/AppDetailsBal/getcategorylistforclgeid"
    const val categorywisestudentcreditdetails =
        "api/AppDetailsBal/categorywisestudentcreditdetails"
    const val getattendanceforparent = "api/AppDetailsBal/getattendanceforparent"
    const val studentAttendancelist = "api/AppDetailsBal/GetStudentWiseAttendanceSummary"


    const val GetLeaveApplicationListForReceiverApp =
        "api/AppDetailsBal/GetLeaveApplicationListForReceiverApp"

    const val GetLeaveType = "api/AppDetailsBal/GetLeaveType"
    const val changepassword = "api/AppDetailsBal/changepassword"
    const val GetstaffdetailsForApp = "api/AppDetailsBal/GetstaffdetailsForApp"
    const val GetStaffClassesforChatForApp = "api/AppDetailsBal/GetStaffClassesforChatForApp"
    const val GetAddsForCollege = "api/AppDetailsBal/GetAddsForCollege"
    const val UpdateDeviceToken = "api/AppDetailsBal/DeviceToken"
    const val GetVideoContentRestriction = "api/AppDetailsBal/GetVideoContentRestriction"
    const val ExamSectionDelete = "api/AppDetailsBal/EditSectionWiseExamForApp "
    //SenderApi

    const val GetDivisions = "api/AppDetailsBal/GetDivisions"
    const val GetDepartment = "api/AppDetailsBal/GetDepartmentsbyDivision"
    const val GetCoursesByDepartment = "api/AppDetailsBal/GetCoursesByDepartment"
    const val SendSMSToParticularType = "api/AppDetailsBal/SendSMSToParticularType"
    const val SendSMSToEntireCollege = "api/AppDetailsBal/SendSMSToEntireCollege"
    const val SendVoiceToEntireCollege = "api/AppDetailsBal/SendFileToEntireCollege"
    const val SendSMSToEntiretutorandsubjectCollege =
        "api/AppDetailsBal/SendSMSToParticularTypeFromTutor"
    const val GetGroup = "api/AppDetailsBal/GetGrouplist"
    const val Getsubject = "api/AppDetailsBal/GetSubjectListforparticularstaff"
    const val getAttendanceforStaff = "api/AppDetailsBal/GetAttendanceClassListForStaff"
    const val GetStudentWiseAttendanceDetails = "api/AppDetailsBal/GetStudentWiseAttendanceDetails"
    const val Gettuter = "api/AppDetailsBal/GetClassListForTutor"
    const val TuterVideoSend = "api/AppDetailsBal/SendVideoToParticularTypeFromTutor"
    const val GetsubjectspecifistudentAttendance = "api/AppDetailsBal/GetstudentListforapp"
    const val Getspecificstudenttuter = "api/AppDetailsBal/GetstudentListforapp"
    const val Getsubjectspecifistudent = "api/AppDetailsBal/GetMentorstudentListforapp"
    const val NoticeboardsendsmsTuter = "api/AppDetailsBal/ManageNoticeBoardFromTutor"
    const val Getdepartmnetcourse = "api/AppDetailsBal/GetCoursesByDepartment"
    const val GetYearandsectionList = "api/AppDetailsBal/YearAndSectionListforApp"
    const val GetYearandsection = "api/AppDetailsBal/GetYearListforApp"
    const val Noticeboardsendsms = "api/AppDetailsBal/ManageNoticeBoard"
    const val EventsenddataTuter = "api/AppDetailsBal/ManageEventsFromTutor"
    const val Eventsenddata = "api/AppDetailsBal/ManageEvents"
    const val ImageorPdf = "api/AppDetailsBal/SendImageOrPDFToEntireCollegeWithCloudURL"
    const val Imageorpdfparticuler = "api/AppDetailsBal/SendImageOrPDFToParticularTypeWithCloudURL"
    const val Assignmentsenddata = "api/AppDetailsBal/ManageAssignmentWithCloudURL"
    const val AttendanceTaking = "api/AppDetailsBal/MarkAttendance"
    const val ManageLeaveApplication = "api/AppDetailsBal/ManageLeaveapplication"
    const val GetLeaveapplicationListforsenderapp =
        "api/AppDetailsBal/GetLeaveApplicationListForSenderApp"
    const val Examsectionandsubject = "api/AppDetailsBal/getsectionwisesubjectlist"
    const val ExamCreation = "api/AppDetailsBal/ExamCreation"
    const val Examviewapi = "api/AppDetailsBal/GetDetailsForExamEdit"
    const val ExamviewapiSubjectList = "api/AppDetailsBal/GetSectionWiseSubjectListForExamEdit"
    const val Examdelete = "api/AppDetailsBal/ExamCreation"
  //  const val TakeAttendance = "api/AppDetailsBal/MarkAttendance"
    const val TakeAttendance = "api/AppDetailsBal/MarkHourWiseAttendance"
    const val ExamtotalEditORDelete = "api/AppDetailsBal/GetDetailsForExamEdit"
    const val VideoEntireSend = "api/AppDetailsBal/SendVideoToEntireCollege"
    const val VideoParticulerSend = "api/AppDetailsBal/SendVideoToParticularType"
    const val Eventphotoupdate = "api/AppDetailsBal/AddeventphotosWithCloudURL"
    const val Assignmentforward = "api/AppDetailsBal/ForwardAssignment"
    const val Chatdatalist = "api/AppDetailsBal/GetStudentChatScreenForApp"
    const val ChatStudent = "api/AppDetailsBal/StudentAskQuestionForApp"
    const val Sendersidechat = "api/AppDetailsBal/GetStaffChatScreenForApp"
    const val ChatStaff = "api/AppDetailsBal/AnswerStudentQuestionForApp"
    const val Assignmentsubmited = "api/AppDetailsBal/SubmitAssignmentFromAppWithCloudURL"

//    const val AssignmentSubmittion = "api/AppDetailsBal/GetAssignmentMemberCount"
    const val AssignmentSubmittion = "api/AppDetailsBal/GetAssignmentSubmissions"
    const val GetSubmittedAssignmentForStudents = "api/AppDetailsBal/GetSubmittedAssignmentForStudents"

    const val Attendance_Check = "api/AppDetailsBal/CheckForAttendanceMarking"
  //  const val Attendance_Edit = "api/AppDetailsBal/Getlistforattendanceedit"
    const val Attendance_Edit = "api/AppDetailsBal/GetStudentsForHourwiseAttendanceEdit"
    const val AssignmentView = "api/AppDetailsBal/ViewAssignmentContent"
    const val BlackStudent = "api/AppDetailsBal/BlockStudentForApp"
    const val UnblackStudent = "api/AppDetailsBal/UnblockStudentForApp"
    const val GetOtp = "api/AppDetailsBal/forgetpassword"
    const val VerifyOTP = "api/AppDetailsBal/VerifyOTP"
    const val CreateNewpassword = "api/AppDetailsBal/forgetpasswordReset"
    const val AssignmentForwardText = "api/AppDetailsBal/ManageAssignmentForText"
    const val HeaderCollagelist = "api/AppDetailsBal/GetBranchCollegeList"
    const val imgageandpdfTuterSend =
        "api/AppDetailsBal/SendImageOrPDFToParticularTypeWithCloudURLFromTutor"

    const val GetTextMessageHistory =
        "api/AppDetailsBal/GetTextMessageHistory"

    const val GetVoiceMessageHistory =
        "api/AppDetailsBal/GetVoiceMessageHistory"

    const val SendVoiceToParticularTypeFromHistory =
        "api/AppDetailsBal/SendVoiceToParticularTypeFromHistory"

    const val SendVoiceToEntireCollegeFromHistory =
        "api/AppDetailsBal/SendVoiceToEntireCollegeFromHistory"

}