package com.vsca.vsnapvoicecollege.Adapters

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

import com.vsca.vsnapvoicecollege.Activities.*
import com.vsca.vsnapvoicecollege.Model.GetCircularListDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class CircularAdapter constructor(data: List<GetCircularListDetails>, context: Context) :
    RecyclerView.Adapter<CircularAdapter.MyViewHolder>() {
    var circulardata: List<GetCircularListDetails> = ArrayList()
    var context: Context
    var Position: Int = 0
    private var mExpandedPosition: Int = -1
    var filepath: String? = null
    private var pathlist: ArrayList<String>? = null
    var hide: Boolean = true
    var Imagepath: String? = null
    var Type = ""
    private lateinit var pdfUri: Uri
    var Assignmnetview: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.noticeboard_list_design, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int
    ) {
        val data: GetCircularListDetails = circulardata.get(position)
        Position = holder.absoluteAdapterPosition
        holder.lblNoticeboardTitle!!.text = data.title
        holder.lblNoticeboardDate!!.text = data.createdondate
        holder.lblNoticetime!!.text = data.createdontime
        val isExpanded: Boolean = position == mExpandedPosition
        holder.rytNotice!!.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.lnrNoticeboardd!!.isActivated = isExpanded
        holder.lnrNoticeboardd!!.visibility = View.VISIBLE
        holder.lbl_section!!.visibility = View.GONE

        if (data.isappread.equals("0")) {
            holder.lblNewCircle!!.visibility = View.VISIBLE
        } else {
            holder.lblNewCircle!!.visibility = View.GONE
        }

        if (isExpanded) {

            CommonUtil.isExpandAdapter = true
            holder.imgArrowdown!!.visibility = View.GONE
            holder.lnrAboveAttachment!!.visibility = View.GONE

        } else {

            CommonUtil.isExpandAdapter = false
            holder.imgArrowdown!!.visibility = View.VISIBLE
            holder.lnrAboveAttachment!!.visibility = View.VISIBLE
            holder.lblAboveCircularPath!!.text = "View"
        }

        holder.lnrNoticeboardd!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {

                Type = "circular"
                if (data.isappread.equals("0")) {
                    BaseActivity.AppReadStatusContext(context, Type, data.detailsid!!)
                    data.isappread = "1"
                    holder.lblNewCircle!!.visibility = View.GONE
                }

                hide = false
                holder.lnrAboveAttachment!!.visibility = View.GONE
                holder.rytCircularFiles!!.visibility = View.VISIBLE
                holder.imgArrowdown!!.visibility = View.GONE
                holder.lblNoticeboardDescription!!.text = data.description
                holder.lblNoticePostedby!!.text = data.sentbyname
                holder.lblCircularPath!!.text = "View"

                pathlist = data.newfilepath as ArrayList<String>?
                val filecount: Int = pathlist!!.size
                Log.d("filename", pathlist!!.size.toString())
                if (filecount > 1) {
                    val totalcount: Int = filecount - 1
                    holder.lblFileCount!!.visibility = View.VISIBLE
                    holder.lblFileCount!!.text = "+" + totalcount.toString()

                } else {
                    holder.lblFileCount!!.visibility = View.GONE
                }

                for (j in pathlist!!.indices) {
                    if (data.filetype.equals("2")) {

                        Imagepath = data.file_path
                        holder.lnrAttachment!!.setOnClickListener {

                            if (filecount > 1) {
                                CommonUtil.filetype = ""
                                Assignmnetview = "Circuler"
                                var i: Intent =
                                    Intent(context, Assignment_MultipleFileView::class.java)
                                CommonUtil.Multipleiamge =
                                    (data.newfilepath as ArrayList<String>?)!!
                                i.putExtra("Assignment", Assignmnetview)
                                CommonUtil.filetype = "Circular Image"
                                context.startActivity(i)

                            } else {

                                val i: Intent = Intent(context, ViewFiles::class.java)
                                i.putExtra("images", pathlist!![j])
                                context.startActivity(i)
                            }
                        }
                    } else if (data.filetype.equals("3")) {


                        Imagepath = data.file_path
                        holder.lnrAttachment!!.setOnClickListener {

                            if (filecount > 1) {

                                CommonUtil.filetype = ""

                                Assignmnetview = "Circuler"
                                val i: Intent =
                                    Intent(context, Assignment_MultipleFileView::class.java)
                                CommonUtil.Multipleiamge =
                                    (data.newfilepath as ArrayList<String>?)!!
                                i.putExtra("Assignment", Assignmnetview)
                                CommonUtil.filetype = "Circular Image"

                                context.startActivity(i)

                            } else {

                                pdfUri = Uri.parse(pathlist!![j])
                                holder.lnrAttachment!!.setOnClickListener {

                                    readpdf()

                                }
                            }
                        }
                    }
                }

                mExpandedPosition = if (isExpanded) -1 else position
                notifyDataSetChanged()
            }
        })
    }

    fun readpdf() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.clipData = ClipData.newRawUri("", pdfUri)
        intent.setDataAndType((pdfUri), "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return circulardata.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {
        val lblNoticeboardTitle: TextView = itemView!!.findViewById(R.id.lblNoticeboardTitle)
        val lblNoticeboardDescription: TextView = itemView!!.findViewById(R.id.lblNoticeboardDescription)
        val lblNoticeboardDate: TextView = itemView!!.findViewById(R.id.lblNoticeboardDate)
        val lblNoticetime: TextView = itemView!!.findViewById(R.id.lblNoticetime)
        val lblNoticePostedby: TextView = itemView!!.findViewById(R.id.lblNoticePostedby)
        val lblCircularPath: TextView = itemView!!.findViewById(R.id.lblCircularPath)
        val rytNotice: RelativeLayout = itemView!!.findViewById(R.id.rytNotice)
        val lnrNoticeboardd: LinearLayout = itemView!!.findViewById(R.id.lnrNoticeboardd)
        val imgArrowdown: ImageView = itemView!!.findViewById(R.id.imgArrowdown)
        val imgArrowUp: ImageView = itemView!!.findViewById(R.id.imgArrowUp)
        val lblAboveCircularPath: TextView = itemView!!.findViewById(R.id.lblAboveCircularPath)
        val lblFileCount: TextView = itemView!!.findViewById(R.id.lblFileCount)
        val lnrAttachment: LinearLayout = itemView!!.findViewById(R.id.lnrAttachment)
        val lnrAboveAttachment: LinearLayout = itemView!!.findViewById(R.id.lnrAboveAttachment)
        val rytCircularFiles: RelativeLayout = itemView!!.findViewById(R.id.rytCircularFiles)
        val lblNewCircle: TextView = itemView!!.findViewById(R.id.lblNewCircle)
        val lbl_section: TextView = itemView!!.findViewById(R.id.lbl_section)

    }

    init {
        circulardata = data
        this.context = context
    }

    fun filterList(filterlist: java.util.ArrayList<GetCircularListDetails>) {

        circulardata = filterlist

        notifyDataSetChanged()
    }
}