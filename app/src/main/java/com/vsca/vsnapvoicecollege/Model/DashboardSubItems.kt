package com.vsca.vsnapvoicecollege.Model

class DashboardSubItems {

    var menuTitle: String? = null
    var menuDescription: String? = null
    var createDate: String? = null
    var createTime: String? = null
    var createBy: String? = null
    var addImage: String? = null
    var type: String? = null
    var adBaackgroundImage: String? = null
    var voiceFilepath: String? = null
    var membername: String? = null
    var duration = 0
    var createdon: String? = null
    var RecentType: String? = null
    var Content: String? = null
    var SubjectName: String? = null
    var AttendanceType: String? = null
    var AttendanceDate: String? = null
    var AttendanceSubject: String? = null
    var MsgId: String? = null
    var FilepathList = ArrayList<String>()
    var Eventdate: String? = null
    var Eventtime: String? = null
    var EventTitle: String? = null
    var ideventdetails: String? = null

    var idassignmentdetails: String? = null
    var assignmenttopic: String? = null
    var assignmentdescription: String? = null
    var submissiondate: String? = null
    var filepaths: String? = null
    var FilepathListAssignment = ArrayList<String>()
   var assignmentfiletype: String? = null

    var coursename: String? = null
    var departmentname: String? = null
    var yearname: String? = null
    var sectionname: String? = null
    var studentname: String? = null
    var question: String? = null
    var createdonchat: String? = null
    var dummy: String? = null
    var message: String? = null

    var Attendancemessage: String? = null
    //Leave Request

    var leaveapplicationid: String? = null
    var studentid: String? = null
    var membernameLeaveRequest: String? = null
    var coursenameLeaveRequest: String? = null
    var departmentnameLeaveRequest: String? = null
    var yearnameLeaveRequest: String? = null
    var sectionnameLeaveRequest: String? = null
    var reason: String? = null
    var fromdate: String? = null
    var todate: String? = null
    var leavestatus: String? = null
    var noofdays: String? = null
    var appliedon: String? = null
    var Addurl: String? = null


    constructor(addImage: String?, adBaackgroundImage: String?, addurl: String?, id: Int?) {
        this.addImage = addImage
        this.Addurl = addurl
        this.adBaackgroundImage = adBaackgroundImage
    }

    constructor(
        leaveapplicationid: String?,
        studentid: String?,
        membernameLeaveRequest: String?,
        coursenameLeaveRequest: String?,
        departmentnameLeaveRequest: String?,
        yearnameLeaveRequest: String?,
        sectionnameLeaveRequest: String?,
        reason: String?,
        fromdate: String?,
        todate: String?,
        leavestatus: String?,
        noofdays: String?,
        appliedon: String?,
        message: String?

    ) {
        this.leaveapplicationid = leaveapplicationid
        this.assignmenttopic = studentid
        this.membernameLeaveRequest = membernameLeaveRequest
        this.coursenameLeaveRequest = coursenameLeaveRequest
        this.departmentnameLeaveRequest = departmentnameLeaveRequest
        this.yearnameLeaveRequest = yearnameLeaveRequest
        this.sectionnameLeaveRequest = sectionnameLeaveRequest
        this.reason = reason
        this.fromdate = fromdate
        this.todate = todate
        this.leavestatus = leavestatus
        this.noofdays = noofdays
        this.appliedon = appliedon
        this.message = message
    }


    constructor(
        idassignmentdetails: String?,
        assignmenttopic: String?,
        assignmentdescription: String?,
        submissiondate: String?,
        filepaths: String?,
        assignmentfiletype:String?,
        FilepathListAssignment: ArrayList<String>
    ) {
        this.idassignmentdetails = idassignmentdetails
        this.assignmenttopic = assignmenttopic
        this.assignmentdescription = assignmentdescription
        this.submissiondate = submissiondate
        this.filepaths = filepaths
        this.assignmentfiletype=assignmentfiletype
        this.FilepathListAssignment = FilepathListAssignment
    }


    constructor(
        coursename: String?,
        departmentname: String?,
        yearname: String?,
        sectionname: String?,
        studentname: String?,
        question: String?,
        createdonchat: String?,
        dummy: String?,
        message: String?
    ) {
        this.coursename = coursename
        this.departmentname = departmentname
        this.yearname = yearname
        this.sectionname = sectionname
        this.studentname = studentname
        this.question = question
        this.createdonchat = createdonchat
        this.dummy = dummy
        this.message = message
    }

    constructor(
        menuTitle: String?,
        menuDescription: String?,
        createDate: String?,
        createTime: String?,
        filepath: ArrayList<String>
    ) {
        this.menuTitle = menuTitle
        this.menuDescription = menuDescription
        this.createDate = createDate
        this.createTime = createTime
        this.FilepathList = filepath
    }


    constructor(
        menuTitle: String?,
        addimage: String?,
        menuDescription: String?,
        createdate: String?,
        createtime: String?,
        createby: String?,
        type: String?
    ) {
        this.menuTitle = menuTitle
        this.menuDescription = menuDescription
        createDate = createdate
        createTime = createtime
        createBy = createby
        this.type = type
    }

    constructor(
        Eventtime: String?,
        Eventdate: String?,
        EventTitle: String?,
        ideventdetails: String?
    ) {
        this.Eventtime = Eventtime
        this.Eventdate = Eventdate
        this.EventTitle = EventTitle
        this.ideventdetails = ideventdetails
    }

    constructor(
        menuDescription: String?,
        membername: String?,
        typ: String?,
        createdon: String?,
        createTime: String?,
        content: String?,
        duartion: Int,
        type: String?,
        detailsID: String?
    ) {
        this.menuDescription = menuDescription
        this.membername = membername
        RecentType = typ
        this.createTime = createTime
        this.createdon = createdon
        Content = content
        duration = duartion
        this.type = type
        MsgId = detailsID
    }

    constructor(
        description: String?,
        voiceFilepath: String?,
        membername: String?,
        duration: Int,
        createdon: String?,
        type: String?,
        detailsid: String?
    ) {
        menuTitle = description
        this.voiceFilepath = voiceFilepath
        this.membername = membername
        this.duration = duration
        this.createdon = createdon
        this.type = type
        this.MsgId = detailsid
    }

    constructor(
         subjectName: String?,
        attendanceType: String?,
        attendancedate: String?,
        message: String?,
        AttendanceSubject:String?
    ) {
        this.SubjectName = subjectName
        this.AttendanceType = attendanceType
        this.AttendanceDate = attendancedate
        this.message = message
        this.AttendanceSubject=AttendanceSubject
    }
}