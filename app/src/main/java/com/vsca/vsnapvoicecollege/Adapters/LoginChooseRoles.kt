package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Interfaces.LoginRolesListener
import com.vsca.vsnapvoicecollege.Model.LoginDetails
import com.vsca.vsnapvoicecollege.R

class LoginChooseRoles(
    data: ArrayList<LoginDetails>, context: Context, val rolesListener: LoginRolesListener
) : RecyclerView.Adapter<LoginChooseRoles.MyViewHolder>() {
    var loginlist: ArrayList<LoginDetails> = ArrayList()
    var context: Context
    var Position: Int = 0
    var Priority_type: String? = null
    var PRIORITY: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.role_list_design, parent, false)
        return MyViewHolder(itemView)
    }

    companion object {
        var chooserolelistener: LoginRolesListener? = null
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: LoginDetails = loginlist.get(position)
        Position = holder.absoluteAdapterPosition
        chooserolelistener = rolesListener
        chooserolelistener?.onroleClick(holder, data)

        if (PRIORITY) {

            for (i in loginlist) {
                if (loginlist[0].priority.equals("p1")) {
                    Priority_type = "p1"
                } else if (loginlist[0].priority.equals("p2")) {
                    Priority_type = "p2"
                } else if (loginlist[0].priority.equals("p3")) {
                    Priority_type = "p3"
                } else if (loginlist[0].priority.equals("p4")) {
                    Priority_type = "p4"
                } else if (loginlist[0].priority.equals("p5")) {
                    Priority_type = "p5"
                } else if (loginlist[0].priority.equals("p6")) {
                    Priority_type = "p6"
                }else if (loginlist[0].priority.equals("p7")) {
                    Priority_type = "p7"
                }
            }
            PRIORITY = false
        }

        if (Priority_type.equals(data.priority)) {

            holder.rytOverAll!!.visibility = View.VISIBLE
            holder.lblMemberName!!.text = data.membername
            holder.lblCollegeName!!.text = data.colgname
            holder.viewline1!!.visibility = View.GONE

            if (data.yearname != "" || data.semestername != "" || data.deptname != "" || data.sectionname != "" || data.coursename != "") {

                if (data.yearname == "" && data.semestername == "") {
                    holder.viewline1!!.visibility = View.GONE
                } else {
                    holder.viewline1!!.visibility = View.VISIBLE
                }

                holder.lblYearName!!.text = data.yearname
                holder.semname!!.text = data.semestername
                holder.lbl_Deptname!!.text = data.deptname
                holder.lblSectionname!!.text = data.sectionname
                holder.lblCoursenname!!.text = data.coursename


            } else {

                holder.lblYearName!!.visibility = View.GONE
                holder.semname!!.visibility = View.GONE
                holder.lbl_Deptname!!.visibility = View.GONE
                holder.lblSectionname!!.visibility = View.GONE
                holder.viewline1!!.visibility = View.GONE
                holder.lblCoursenname!!.visibility = View.GONE

            }

        } else {
            holder.rytOverAll!!.visibility = View.GONE
        }

//        if (data.colglogo == null || data.colglogo!!.isEmpty()) {
//            Glide.with(context).load(R.drawable.ic_user_grey)
//                .diskCacheStrategy(DiskCacheStrategy.ALL).into((holder.imgCollegeLogo)!!)
//        } else {
//            Glide.with(context).load(data.colglogo).diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into((holder.imgCollegeLogo)!!)
//        }
    }

    override fun getItemCount(): Int {
        return loginlist.size
    }

    fun _LoginClick(priority: String) {
        Priority_type = priority
        notifyDataSetChanged()

    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {
        @JvmField
        @BindView(R.id.lblMemberName)
        var lblMemberName: TextView? = null

        @JvmField
        @BindView(R.id.lblCollegeName)
        var lblCollegeName: TextView? = null

        @JvmField
        @BindView(R.id.viewline1)
        var viewline1: View? = null


        @JvmField
        @BindView(R.id.lblYearName)
        var lblYearName: TextView? = null

        @JvmField
        @BindView(R.id.lblSemesterName)
        var lblSemesterName: TextView? = null

        @JvmField
        @BindView(R.id.imgCollegeLogo)
        var imgCollegeLogo: ImageView? = null


        @JvmField
        @BindView(R.id.semname)
        var semname: TextView? = null

        @JvmField
        @BindView(R.id.lblSectionname)
        var lblSectionname: TextView? = null

        @JvmField
        @BindView(R.id.lbl_Deptname)
        var lbl_Deptname: TextView? = null

        @JvmField
        @BindView(R.id.lblCoursenname)
        var lblCoursenname: TextView? = null

        @JvmField
        @BindView(R.id.rytOverAll)
        var rytOverAll: RelativeLayout? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        loginlist = data as ArrayList<LoginDetails>
        this.context = context
    }
}