package com.vsca.vsnapvoicecollege.Adapters

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Activities.VideoPlay
import com.vsca.vsnapvoicecollege.Activities.ViewFiles
import com.vsca.vsnapvoicecollege.Model.AssignmentContent_ViewData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class Assignment_ContentViewAdapter(
    private val listname: List<AssignmentContent_ViewData>,
    private val context: Context?
) :
    RecyclerView.Adapter<Assignment_ContentViewAdapter.MyViewHolder>() {

    var AssignmentContent_ViewData: List<AssignmentContent_ViewData> = ArrayList()
    private lateinit var pdfUri: Uri


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Assignment_ContentViewAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.assignment_multifile, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: Assignment_ContentViewAdapter.MyViewHolder,
        position: Int
    ) {
        val path: AssignmentContent_ViewData = listname[position]


        if (CommonUtil.filetype.equals("Assignment previous Submited")) {

            holder.ibisubmissiontime!!.text = path.submittedtime

            if (path.file_name.contains("/a")) {
                val filename: String = path.file_name
                val file: Array<String> = filename.split("/a".toRegex()).toTypedArray()
                val StringTwo: String = file.get(1)
                Log.d("filepath2", StringTwo)
                holder.filename!!.text = "A" + StringTwo

            } else if (path.file_name.contains("/F")) {

                val filename: String = path.file_name
                val file: Array<String> = filename.split("/F".toRegex()).toTypedArray()
                val StringTwo: String = file.get(1)
                Log.d("filepath2", StringTwo)
                holder.filename!!.text = "F" + StringTwo

            } else if (path.file_name.contains("ion")) {

                val filename: String = path.file_name
                val file: Array<String> = filename.split("ion".toRegex()).toTypedArray()
                val StringTwo: String = file.get(1)
                Log.d("filepath2", StringTwo)
                holder.filename!!.text = "File" + StringTwo

            } else {
                holder.filename!!.text = path.file_name
            }
        }


        holder.lnrNoticeboardd?.setOnClickListener {
            holder.lnrDesc!!.visibility = View.VISIBLE


        }

        holder.lblDesc!!.text = path.description

        holder.viewFile?.setOnClickListener {

            if (path.content != null) {

                if (path.content.contains("https://vimeo.com")) {

                    val filename: String = path.content
                    Log.d("File_videoName", filename.toString())

                    val file: Array<String> = filename.split("m/".toRegex()).toTypedArray()
                    val StringTwo: String = file.get(1)

                    val i: Intent = Intent(context, VideoPlay::class.java)
                    i.putExtra("iframe", StringTwo)
                    Log.d("vimeovideoid", StringTwo)
                    i.putExtra("description", path.description)
                    context?.startActivity(i)

                } else if (path.content.contains(".pdf")) {
                    pdfUri = Uri.parse(path.content)
                    readpdf()
                }
                else {
                    var i: Intent = Intent(context, ViewFiles::class.java)
                    i.putExtra("images", path.content)
                    context?.startActivity(i)

                }
            } else {
                Toast.makeText(context, "File is Empty", Toast.LENGTH_SHORT).show()
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
        @BindView(R.id.viewFile)
        var viewFile: ImageView? = null

        @JvmField
        @BindView(R.id.lnrDesc)
        var lnrDesc: LinearLayout? = null

        @JvmField
        @BindView(R.id.lblDesc)
        var lblDesc: TextView? = null

        @JvmField
        @BindView(R.id.lnrNoticeboardd)
        var lnrNoticeboardd: LinearLayout? = null

        init {
            ButterKnife.bind(this, (itemView))
            AssignmentContent_ViewData = listname
        }

    }

    fun readpdf() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.clipData = ClipData.newRawUri("", pdfUri)
        intent.setDataAndType((pdfUri), "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context?.startActivity(intent)
    }
}