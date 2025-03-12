package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.vsca.vsnapvoicecollege.Model.ChatList
import com.vsca.vsnapvoicecollege.R
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class Chat_Text_adapter(
    var marklist: ArrayList<ChatList>, private val context: Context?,
) : RecyclerView.Adapter<Chat_Text_adapter.MyViewHolder>() {

    var Position: Int = 0
    var Livedate: String? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txt_financeandaccounding: TextView = itemView!!.findViewById(R.id.txt_financeandaccounding)
        val txt_date: TextView = itemView!!.findViewById(R.id.txt_date)
        val txt_testing_creating: TextView = itemView!!.findViewById(R.id.txt_testing_creating)
        val txt_testing_creatingans: TextView = itemView!!.findViewById(R.id.txt_testing_creatingans)
        val txt_financeandaccoundingans: TextView = itemView!!.findViewById(R.id.txt_financeandaccoundingans)
        val examlist_constrineans: ConstraintLayout = itemView!!.findViewById(R.id.examlist_constrineans)
        val txt_dateans: TextView = itemView!!.findViewById(R.id.txt_dateans)
        val txt_question: TextView = itemView!!.findViewById(R.id.txt_question)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_list, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val data = marklist.get(position)

        val filename: String = data.createdon
        val file: Array<String> =
            filename.split(" ".toRegex()).toTypedArray()
        val Stringone: String = file[0]
        val StringTwo: String = file[1]

        val result = LocalTime.parse(StringTwo).format(DateTimeFormatter.ofPattern("h:mm a"))

        val date = Stringone
        var spf = SimpleDateFormat("yyyy-MM-dd")
        val newDate = spf.parse(date)
        spf = SimpleDateFormat("dd MMM yyyy")
        val newDateString = spf.format(newDate)

        Livedate = newDateString + " " + result

        holder.txt_date!!.text = Livedate
        holder.txt_testing_creating!!.text = data.question
        holder.txt_financeandaccounding!!.text = data.studentname



        if (data.answer.equals("Not answered yet")) {
            holder.examlist_constrineans!!.visibility = View.GONE
        } else {
            holder.examlist_constrineans!!.visibility = View.VISIBLE
            holder.txt_testing_creatingans!!.text = data.answer

            val filename1: String = data.answeredon
            val file1: Array<String> =
                filename1.split(" ".toRegex()).toTypedArray()
            val Stringone1: String = file1.get(0)
            val StringTwo1: String = file1.get(1)

            val result = LocalTime.parse(StringTwo1).format(DateTimeFormatter.ofPattern("h:mm a"))

            val date = Stringone1
            var spf = SimpleDateFormat("yyyy-MM-dd")
            val newDate = spf.parse(date)
            spf = SimpleDateFormat("dd MMM yyyy")
            val newDateString = spf.format(newDate)

            Livedate = newDateString + " " + result


            holder.txt_dateans!!.text = Livedate
            holder.txt_financeandaccoundingans!!.text = data.studentname
            holder.txt_question!!.text = data.question
        }
    }

    override fun getItemCount(): Int {
        return marklist.size
    }
}