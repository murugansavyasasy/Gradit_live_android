package com.vsca.vsnapvoicecollege.Adapters

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Activities.ViewFiles
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

 class MultipleAssignmentfile(
    private val listname: java.util.ArrayList<String>, private val context: Context?
) : RecyclerView.Adapter<MultipleAssignmentfile.MyViewHolder>() {

    var uri: Uri? = null
    var pathlist: ArrayList<String>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): MultipleAssignmentfile.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.assignment_multifile, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MultipleAssignmentfile.MyViewHolder, position: Int) {
        val path = listname[position]

        holder.ibisubmissiontime!!.visibility = View.GONE
        if (CommonUtil.filetype.equals("Assignment")) {
            val filename: String = path
            val file: Array<String> = filename.split("/F".toRegex()).toTypedArray()
            val Stringone: String = file.get(0)
            holder.filename!!.text = Stringone

        } else if (CommonUtil.filetype.equals("Circular Image")) {

            if (path.contains("ion/")) {
                val filename: String = path
                val file: Array<String> = filename.split("ion/".toRegex()).toTypedArray()
                val Stringone: String = file.get(0)
                val StringTwo: String = file.get(1)
                Log.d("filepath2", StringTwo)
                holder.filename!!.text = StringTwo
            } else if (path.contains("/F")) {
                val filename: String = path
                val file: Array<String> = filename.split("/F".toRegex()).toTypedArray()
                val Stringone: String = file.get(0)
                val StringTwo: String = file.get(1)
                Log.d("filepath2", StringTwo)
                holder.filename!!.text = "F" + StringTwo
            } else {
                holder.filename!!.text = path
            }

        } else {
            holder.filename!!.text = path

        }

        holder.lnrNoticeboardd?.setOnClickListener {

            if (path.contains(".pdf")) {
                Log.d("pdf","pdf")
                val Uri: Uri
                Uri = android.net.Uri.parse(path)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.clipData = ClipData.newRawUri("", Uri)
                intent.setDataAndType((Uri), "application/pdf")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                context!!.startActivity(intent)

            } else if (path.contains("jpeg")) {
                Log.d("pdf","jpeg")

                val i: Intent = Intent(context, ViewFiles::class.java)
                i.putExtra("images", path)
                context?.startActivity(i)
            } else {
                Log.d("pdf","others")

                val i: Intent = Intent(context, ViewFiles::class.java)
                i.putExtra("images", path)
                context?.startActivity(i)
            }

        }
    }

    override fun getItemCount(): Int {
        return listname.size
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @JvmField
        @BindView(R.id.filename)
        var filename: TextView? = null

        @JvmField
        @BindView(R.id.ibisubmissiontime)
        var ibisubmissiontime: TextView? = null

        @JvmField
        @BindView(R.id.lnrNoticeboardd)
        var lnrNoticeboardd: LinearLayout? = null

        init {
            ButterKnife.bind(this, (itemView))

        }
    }
}