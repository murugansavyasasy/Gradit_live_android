package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vsca.vsnapvoicecollege.Activities.ChatParent
import com.vsca.vsnapvoicecollege.Model.GetFacultyListDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class FacultyAdapter constructor(data: List<GetFacultyListDetails>, context: Context) :
    RecyclerView.Adapter<FacultyAdapter.MyViewHolder>() {
    var loginlist: List<GetFacultyListDetails> = ArrayList()
    var context: Context
    var Position: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.faculty_list_design, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: GetFacultyListDetails = loginlist.get(position)
        Position = holder.absoluteAdapterPosition

        holder.lblStaffType!!.text = data.stafftype
        holder.lblSubjectName!!.text = data.subjectname
        holder.lblStaffName!!.text = data.staffname

        if (data.facultyphoto.equals("") || data.facultyphoto.equals("null")) {
            Glide.with(context)
                .load(R.drawable.faculty_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgFaclutyPhoto!!)
        } else {
            Glide.with(context)
                .load(data.facultyphoto)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgFaclutyPhoto!!)
        }

        if (CommonUtil.Priority.equals("p4")) {
            holder.btn_intreact!!.visibility = View.VISIBLE
            holder.layoutSubjecthandled!!.visibility = View.VISIBLE

        } else {
            holder.btn_intreact!!.visibility = View.GONE
            holder.layoutSubjecthandled!!.visibility = View.GONE
        }

        holder.btn_intreact!!.setOnClickListener {
            var i: Intent = Intent(context, ChatParent::class.java)
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return loginlist.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {
        @JvmField
        @BindView(R.id.lblStaffType)
        var lblStaffType: TextView? = null

        @JvmField
        @BindView(R.id.lblSubjectName)
        var lblSubjectName: TextView? = null

        @JvmField
        @BindView(R.id.lblStaffName)
        var lblStaffName: TextView? = null

        @JvmField
        @BindView(R.id.btn_intreact)
        var btn_intreact: TextView? = null

        @JvmField
        @BindView(R.id.imgFaclutyPhoto)
        var imgFaclutyPhoto: ImageView? = null

        @JvmField
        @BindView(R.id.layoutSubjecthandled)
        var layoutSubjecthandled: LinearLayout? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        loginlist = data
        this.context = context
    }
}