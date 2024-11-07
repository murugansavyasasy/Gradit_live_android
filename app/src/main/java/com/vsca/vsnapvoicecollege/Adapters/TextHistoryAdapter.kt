package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.ActivitySender.AddRecipients
import com.vsca.vsnapvoicecollege.ActivitySender.HeaderRecipient
import com.vsca.vsnapvoicecollege.ActivitySender.PrincipalRecipient
import com.vsca.vsnapvoicecollege.Model.TextHistoryData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class TextHistoryAdapter(
    var TextHistortData: ArrayList<TextHistoryData>,
    private var context: Context?
) : RecyclerView.Adapter<TextHistoryAdapter.MyViewHolder>() {

    var ScreenName: String? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txt_description: TextView
        var lblRecenttitle: TextView
        var lblRecentDate: TextView
        var lblRecentTime: TextView
        var txt_sendhistory: TextView

        init {
            lblRecenttitle = itemView.findViewById(R.id.lblRecenttitle)
            txt_description = itemView.findViewById(R.id.txt_description)
            lblRecentDate = itemView.findViewById(R.id.lblRecentDate)
            lblRecentTime = itemView.findViewById(R.id.lblRecentTime)
            txt_sendhistory = itemView.findViewById(R.id.txt_sendhistory)
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TextHistoryAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.text_historylayout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TextHistoryAdapter.MyViewHolder, position: Int) {
        val modal = TextHistortData[position]

        holder.txt_description.text = modal.description
        holder.lblRecenttitle.text = modal.msgcontent
        val createdDateTime: String = modal.timing
        val firstvalue: Array<String> = createdDateTime.split("-".toRegex()).toTypedArray()
        val createddate: String = firstvalue[0]
        holder.lblRecentDate.text = createddate
        val createdTime: String = firstvalue[1]
        holder.lblRecentTime.text = createdTime


        holder.txt_sendhistory.setOnClickListener {
            CommonUtil.MenuTitle = modal.msgcontent
            CommonUtil.MenuDescription = modal.description
            CommonUtil.forwarding_text_id = modal.headerid
            ScreenName = CommonUtil.TextHistory

            if (CommonUtil.Priority.equals("p7")) {
                val i: Intent = Intent(context, HeaderRecipient::class.java)
                i.putExtra("ScreenName", ScreenName)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context!!.startActivity(i)
            } else {
                if (CommonUtil.Priority.equals("p1")) {
                    val i: Intent = Intent(context, PrincipalRecipient::class.java)
                    i.putExtra("ScreenName", ScreenName)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context!!.startActivity(i)
                } else {
                    val i: Intent = Intent(context, AddRecipients::class.java)
                    i.putExtra("ScreenName", ScreenName)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context!!.startActivity(i)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return TextHistortData.size
    }
}