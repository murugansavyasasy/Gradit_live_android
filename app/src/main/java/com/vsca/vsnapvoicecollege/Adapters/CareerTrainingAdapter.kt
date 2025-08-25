package com.vsca.vsnapvoicecollege.Adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.CareerTrainingData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class CareerTrainingAdapter(
    private val trainingList: List<CareerTrainingData>,
    private val context: Context
) : RecyclerView.Adapter<CareerTrainingAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.career_training, parent, false)
        return MyViewHolder(view)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: TextView = itemView.findViewById(R.id.tvDay1)
        val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvMode: TextView = itemView.findViewById(R.id.tvMode)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvScheduledTime: TextView = itemView.findViewById(R.id.tvScheduled_Time)
        val lblRepeatTxt: TextView = itemView.findViewById(R.id.lblRepeatTxt)
        val tvCourses: TextView = itemView.findViewById(R.id.tvCourses)
        val tvCriteria: TextView = itemView.findViewById(R.id.tvCriteria)
        val tvEventTag: TextView = itemView.findViewById(R.id.tvEventTag)
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = trainingList[position]

        // Split trainingDate into day/month
        val dateParts = data.trainingDate.split("/")
        if (dateParts.size == 3) {
            holder.tvDay.text = dateParts[0]
            holder.tvMonth.text = CommonUtil.getMonthName(dateParts[1].toInt()) // helper to get month name
        }

        holder.tvTime.text = CommonUtil.convertTo12HourFormat(data.startTime)  // "17:57:00" → "05:57 PM"
        holder.tvTitle.text = data.trainingTitle
        holder.tvDescription.text = data.trainingAbout
        holder.tvMode.text = data.modeTraining
        holder.tvLocation.text = data.venue
        holder.tvScheduledTime.text =
            "${CommonUtil.convertTo12HourFormat(data.startTime)} to ${CommonUtil.convertTo12HourFormat(data.endTime)}"

        // Repeat info
      if (data.recursiveTraining) {
          holder.lblRepeatTxt.visibility=View.VISIBLE
          holder.lblRepeatTxt.text= "Repeats every ${data.repeatTraining} ${data.selectDay} until ${CommonUtil.isoToDisplay(data.repeatUntill)}"
      }else{
          holder.lblRepeatTxt.visibility=View.GONE

      }

        // Courses
        holder.tvCourses.text = data.applicableBatches
            .map { it.replace("Batch ", "") }   // remove "Batch " prefix
            .joinToString(", ")

        // Trainer
        holder.tvCriteria.text = data.trainerName

        // Event Tag
        holder.tvEventTag.text = "Placement Training"
    }

    override fun getItemCount(): Int = trainingList.size
}
