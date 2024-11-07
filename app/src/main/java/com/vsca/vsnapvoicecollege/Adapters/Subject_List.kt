package com.vsca.vsnapvoicecollege.Adapters

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import java.util.Calendar


class Subject_List(data: List<Subjectdetail>, context: Context) :
    RecyclerView.Adapter<Subject_List.MyViewHolder>() {
    var subjectlist: List<Subjectdetail> = ArrayList()
    var context: Context
    var Position: Int = 0
    var mExpandedPosition = -1
    var Examdate: String? = null
    var ExamVenue: String? = null
    var Examsylleble: String? = null
    var ExamSession: String? = null
    var IDS: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Subject_List.MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.subjectlist_particuler, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: Subject_List.MyViewHolder, position: Int) {

        val data: Subjectdetail = subjectlist[position]

        val isExpanded = position == mExpandedPosition

        holder.txt_test!!.text = data.subjectname

        if (CommonUtil.EditButtonclick.equals("ExamEdit")) {

            for (k in 0 until CommonUtil.subjectdetails.size) {
                var IDS: String? = null

                IDS = CommonUtil.subjectdetails[k].examsubjectid

                if (IDS.equals(data.subjectid)) {

                    holder.btn_Delete!!.visibility = View.VISIBLE
                    holder.tick1!!.setBackgroundResource(R.drawable.examinatin_changeexpandclolur)

                }
            }
        }


        holder.constrine_test!!.setOnClickListener {

            if (isExpanded) {

                holder.tick1!!.setBackgroundResource(R.drawable.black_backroundtick)
                holder.down1!!.setBackgroundResource(R.drawable.ic_arrow_down)
                mExpandedPosition = if (isExpanded) -1 else position
                holder.constrin_second!!.visibility = View.GONE
                notifyDataSetChanged()

            } else {

                CommonUtil.examsubjectid = data.subjectid

                if (CommonUtil.EditButtonclick.equals("ExamEdit")) {

                    holder.btn_Delete!!.visibility = View.GONE
                    CommonUtil.examsubjectid = data.subjectid
                    holder.constrine_test!!.isEnabled = true
                    holder.tick1!!.setBackgroundResource(R.drawable.black_backroundtick)
                    mExpandedPosition = if (isExpanded) -1 else position
                    holder.constrin_second!!.visibility = View.VISIBLE
                    notifyDataSetChanged()

                    for (k in 0 until CommonUtil.subjectdetails.size) {

                        IDS = CommonUtil.subjectdetails[k].examsubjectid

                        if (IDS.equals(data.subjectid)) {

                            CommonUtil.Venue = holder.txtTitle!!.text.toString()
                            CommonUtil.examsyllabus = holder.txtDescription!!.text.toString()
                            CommonUtil.examdate = holder.start_date!!.text.toString()
                            CommonUtil.section = holder.txt_session1!!.text.toString()

                            holder.btn_Delete!!.visibility = View.VISIBLE
                            CommonUtil.examsubjectid = data.subjectid

                            holder.txtTitle!!.setText(CommonUtil.subjectdetails[k].examvenue)
                            holder.txtDescription!!.setText(CommonUtil.subjectdetails[k].examsyllabus)
                            holder.start_date!!.text = CommonUtil.subjectdetails[k].examdate
                            holder.txt_session1!!.text = CommonUtil.subjectdetails[k].examsession

                            Examdate = CommonUtil.subjectdetails[k].examdate
                            ExamVenue = CommonUtil.subjectdetails[k].examvenue
                            Examsylleble = CommonUtil.subjectdetails[k].examsyllabus
                            ExamSession = CommonUtil.subjectdetails[k].examsession

                            holder.constrine_test!!.isEnabled = true
                            holder.tick1!!.setBackgroundResource(R.drawable.examinatin_changeexpandclolur)
                            mExpandedPosition = if (isExpanded) -1 else position
                            holder.constrin_second!!.visibility = View.VISIBLE
                            notifyDataSetChanged()

                        } else {

                            holder.btn_Delete!!.visibility = View.GONE
                            CommonUtil.examsubjectid = data.subjectid
                            holder.constrine_test!!.isEnabled = true
                            holder.tick1!!.setBackgroundResource(R.drawable.black_backroundtick)
                            mExpandedPosition = if (isExpanded) -1 else position
                            holder.constrin_second!!.visibility = View.VISIBLE
                            notifyDataSetChanged()
                        }
                    }

                } else {

                    if (CommonUtil.Subjectdetail_ExamCreation.size > 0) {

                        for (i in CommonUtil.Subjectdetail_ExamCreation.indices) {

                            val Ids = CommonUtil.Subjectdetail_ExamCreation[i].examsubjectid

                            if (Ids.equals(CommonUtil.SectionId + " /" + data.subjectid)) {

                                val venue = CommonUtil.Subjectdetail_ExamCreation[i].examvenue
                                val examsyllabus = CommonUtil.Subjectdetail_ExamCreation[i].examsyllabus
                                val start_date = CommonUtil.Subjectdetail_ExamCreation[i].examdate
                                val examsession = CommonUtil.Subjectdetail_ExamCreation[i].examsession

                                holder.txtTitle!!.setText(venue)
                                holder.txtDescription!!.setText(examsyllabus)
                                holder.start_date!!.text = start_date
                                holder.txt_session1!!.text = examsession
                            }
                        }
                    }

                    holder.tick1!!.setBackgroundResource(R.drawable.examinatin_changeexpandclolur)
                    holder.down1!!.setBackgroundResource(R.drawable.ic_arraow_up)
                    mExpandedPosition = if (isExpanded) -1 else position
                    holder.constrin_second!!.visibility = View.VISIBLE
                    notifyDataSetChanged()

                }
            }
        }

        holder.start_date!!.setOnClickListener {

            if (CommonUtil.EditButtonclick.equals("ExamEdit")) {


                val c = Calendar.getInstance()
                val dialog = DatePickerDialog(
                    context, { view, year, month, dayOfMonth ->
                        val _year = year.toString()
                        val _month =
                            if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                        val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                         holder.start_date!!.text = "$_date/$_month/$_year"

                    }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.MONTH]
                )

                val ExamEditStartdate: String = CommonUtil.ExamEditStartdate
                val firstvalue: Array<String> =
                    ExamEditStartdate.split("/".toRegex()).toTypedArray()
                val createddate1: String = firstvalue[0]
                val createddate2: String = firstvalue[1]
                val createddate3: String = firstvalue.get(2)


                val minDay = createddate1.toInt()
                val minMonth = createddate2.toInt()
                val minYear = createddate3.toInt()


                c[minYear, minMonth - 1] = minDay
                dialog.datePicker.minDate = c.timeInMillis


                val ExamEditEnddate: String = CommonUtil.ExamEditEnddate
                val firstvalue1: Array<String> = ExamEditEnddate.split("/".toRegex()).toTypedArray()
                val createddateend1: String = firstvalue1[0]
                val createddateend2: String = firstvalue1[1]
                val createddateend3: String = firstvalue1[2]

                val maxDay = createddateend1.toInt()
                val maxMonth = createddateend2.toInt()
                val maxYear = createddateend3.toInt()

                c[maxYear, maxMonth - 1] = maxDay
                dialog.datePicker.maxDate = c.timeInMillis
                dialog.show()

            } else {

                val c = Calendar.getInstance()
                val dialog = DatePickerDialog(
                    context, { view, year, month, dayOfMonth ->
                        val _year = year.toString()
                        val _month =
                            if (month + 1 < 10) "0" + (month + 1) else (month + 1).toString()
                        val _date = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                        holder.start_date!!.text = "$_date/$_month/$_year"

                    }, c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.MONTH]
                )


                val minDay = CommonUtil.DateExam!!.toInt()
                val minMonth = CommonUtil.Month!!.toInt()
                val minYear = CommonUtil.YearDate!!.toInt()


                c[minYear, minMonth - 1] = minDay
                dialog.datePicker.minDate = c.timeInMillis


                val maxDay = CommonUtil.EnddateExam!!.toInt()
                val maxMonth = CommonUtil.EndDateMonth!!.toInt()
                val maxYear = CommonUtil.Enddateyear!!.toInt()

                c[maxYear, maxMonth - 1] = maxDay
                dialog.datePicker.maxDate = c.timeInMillis
                dialog.show()

            }
        }

        holder.txt_session1!!.setOnClickListener {

            val popupMenu = PopupMenu(context, holder.txt_session1)
            popupMenu.menuInflater.inflate(R.menu.fn_or_an, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->

                holder.txt_session1!!.text = menuItem.title

                true
            }
            popupMenu.show()
        }


        holder.btn_save!!.setOnClickListener {


            if (CommonUtil.EditButtonclick.equals("ExamEdit")) {

                CommonUtil.Venue = holder.txtTitle!!.text.toString()
                CommonUtil.examsyllabus = holder.txtDescription!!.text.toString()
                CommonUtil.examdate = holder.start_date!!.text.toString()
                CommonUtil.section = holder.txt_session1!!.text.toString()

                if (CommonUtil.Venue.isEmpty() || CommonUtil.examsyllabus.isEmpty() || CommonUtil.examdate.isEmpty() || CommonUtil.section.isEmpty()) {

                    Toast.makeText(
                        context,
                        "Fill the all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    var message: String? = null
                    message = "Are you want to save... "
                    val dlg = context.let { AlertDialog.Builder(it) }
                    dlg.setTitle("Info")
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        "Yes",
                        DialogInterface.OnClickListener { dialog, which ->


                            for (i in 0 until CommonUtil.SubjectExamcreationEDIT.size) {
                                if (CommonUtil.SubjectExamcreationEDIT.get(i).examsubjectid == CommonUtil.examsubjectid) {
                                    CommonUtil.SubjectExamcreationEDIT.removeAt(i)
                                    break
                                }
                            }

                            CommonUtil.SubjectExamcreationEDIT.add(
                                SubjectExamcreationEDIT(
                                    CommonUtil.examdate,
                                    CommonUtil.section,
                                    CommonUtil.examsubjectid,
                                    CommonUtil.examsyllabus,
                                    CommonUtil.Venue
                                )
                            )


                            var message1: String? = null
                            message1 = "Save Successfully..."
                            val dlg1 = context.let { AlertDialog.Builder(it) }
                            dlg1.setTitle("Info")
                            dlg1.setMessage(message1)
                            dlg1.setPositiveButton(
                                "OK",
                                DialogInterface.OnClickListener { dialog, which ->


                                    holder.tick1!!.setBackgroundResource(R.drawable.examinatin_changeexpandclolur)
                                    holder.down1!!.setBackgroundResource(R.drawable.ic_arrow_down)
                                    holder.constrin_second!!.visibility = View.GONE


                                })

                            dlg1.setCancelable(false)
                            dlg1.create()
                            dlg1.show()


                        })

                    dlg.setNegativeButton(
                        "CANCEL",
                        DialogInterface.OnClickListener { dialog, which ->


                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()
                }

            } else {


                CommonUtil.Venue = holder.txtTitle!!.text.toString()
                CommonUtil.examsyllabus = holder.txtDescription!!.text.toString()
                CommonUtil.examdate = holder.start_date!!.text.toString()
                CommonUtil.section = holder.txt_session1!!.text.toString()


                if (CommonUtil.Venue.isEmpty() || CommonUtil.examsyllabus.isEmpty() || CommonUtil.examdate.isEmpty() || CommonUtil.section.isEmpty()) {

                    Toast.makeText(
                        context,
                        "Fill the all fields",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    var message: String? = null
                    message = "Are you want to save... "
                    val dlg = context.let { AlertDialog.Builder(it) }
                    dlg.setTitle("Info")
                    dlg.setMessage(message)
                    dlg.setPositiveButton(
                        "Yes",
                        DialogInterface.OnClickListener { dialog, which ->


                            for (i in 0 until CommonUtil.Subjectdetail_ExamCreation.size) {
                                if (CommonUtil.Subjectdetail_ExamCreation.get(i).examsubjectid == CommonUtil.SectionId + " /" + CommonUtil.examsubjectid) {
                                    CommonUtil.Subjectdetail_ExamCreation.removeAt(i)
                                    break
                                }
                            }

                            CommonUtil.Subjectdetail_ExamCreation.add(
                                Subjectdetail_ExamCreation(
                                    CommonUtil.examdate,
                                    CommonUtil.section,
                                    CommonUtil.SectionId + " /" + CommonUtil.examsubjectid,
                                    CommonUtil.examsyllabus,
                                    CommonUtil.Venue
                                )
                            )

                            var message1: String? = null
                            message1 = "Save Successfully..."
                            val dlg1 = context.let { AlertDialog.Builder(it) }
                            dlg1.setTitle("Info")
                            dlg1.setMessage(message1)
                            dlg1.setPositiveButton(
                                "OK",
                                DialogInterface.OnClickListener { dialog, which ->


                                    holder.constrine_test!!.isEnabled = true
                                    holder.tick1!!.setBackgroundResource(R.drawable.examinatin_changeexpandclolur)
                                    mExpandedPosition = if (isExpanded) -1 else position
                                    holder.down1!!.setBackgroundResource(R.drawable.ic_arrow_down)
                                    holder.constrin_second!!.visibility = View.GONE
                                    notifyDataSetChanged()


                                })

                            dlg1.setCancelable(false)
                            dlg1.create()
                            dlg1.show()


                        })

                    dlg.setNegativeButton(
                        "CANCEL",
                        DialogInterface.OnClickListener { dialog, which ->


                        })

                    dlg.setCancelable(false)
                    dlg.create()
                    dlg.show()
                }
            }
        }

        holder.btn_Delete!!.setOnClickListener {


            var message1: String? = null
            message1 = "Are you delete the subject..."
            val dlg1 = context.let { AlertDialog.Builder(it) }
            dlg1.setTitle("Info")
            dlg1.setMessage(message1)
            dlg1.setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialog, which ->

                    holder.txtTitle!!.setText("")
                    holder.txtDescription!!.setText("")
                    holder.start_date!!.text = ""
                    holder.txt_session1!!.text = ""

                    CommonUtil.SubjectExamcreationEDIT.removeIf {
                        it.examsubjectid == IDS && it.examdate == Examdate &&
                                it.examsyllabus == Examsylleble
                                && it.examvenue == ExamVenue && it.examsession == ExamSession
                    }


                    holder.tick1!!.setBackgroundResource(R.drawable.black_backroundtick)
                    holder.down1!!.setBackgroundResource(R.drawable.ic_arrow_down)
                    mExpandedPosition = if (isExpanded) -1 else position
                    holder.constrin_second!!.visibility = View.GONE
                    notifyDataSetChanged()
                })

            dlg1.setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->

            })

            dlg1.setCancelable(false)
            dlg1.create()
            dlg1.show()

        }

    }


    override fun getItemCount(): Int {
        return subjectlist.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {

        @JvmField
        @BindView(R.id.constrine_test)
        var constrine_test: ConstraintLayout? = null

        @JvmField
        @BindView(R.id.constrin_second)
        var constrin_second: ConstraintLayout? = null

        @JvmField
        @BindView(R.id.txtTitle)
        var txtTitle: EditText? = null

        @JvmField
        @BindView(R.id.txtDescription)
        var txtDescription: EditText? = null

        @JvmField
        @BindView(R.id.start_date)
        var start_date: TextView? = null


        @JvmField
        @BindView(R.id.down1)
        var down1: TextView? = null

        @JvmField
        @BindView(R.id.txt_session1)
        var txt_session1: TextView? = null

        @JvmField
        @BindView(R.id.txt_test)
        var txt_test: TextView? = null

        @JvmField
        @BindView(R.id.tick1)
        var tick1: TextView? = null


        @JvmField
        @BindView(R.id.btn_save)
        var btn_save: Button? = null

        @JvmField
        @BindView(R.id.btn_Delete)
        var btn_Delete: Button? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        if (data != null) {
            subjectlist = data
        }
        this.context = context
    }
}