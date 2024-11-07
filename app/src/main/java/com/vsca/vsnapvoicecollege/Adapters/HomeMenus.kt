package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Interfaces.HomeMenuClickListener
import com.vsca.vsnapvoicecollege.Model.MenuDetailsResponse
import com.vsca.vsnapvoicecollege.R
import de.hdodenhof.circleimageview.CircleImageView

class HomeMenus constructor(
    context: Context,
    data: List<MenuDetailsResponse>,
    val homeMenuClickListener: HomeMenuClickListener
) :
    RecyclerView.Adapter<HomeMenus.MyViewHolder>() {
    var menulist: List<MenuDetailsResponse> = ArrayList()
    var context: Context
    var Position: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_menu_list_design, parent, false)
        return MyViewHolder(itemView)
    }

    companion object {
        var menuListener: HomeMenuClickListener? = null
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: MenuDetailsResponse = menulist.get(position)
        menuListener = homeMenuClickListener
        menuListener?.onMenuClick(holder, data)
        Position = holder.absoluteAdapterPosition

        holder.imgMenu!!.visibility = View.VISIBLE
        holder.lblMenuName!!.visibility = View.VISIBLE

        when (data.id) {
            1 -> {
                holder.imgMenu!!.setImageResource(R.drawable.home)
                holder.lblMenuName!!.text = data.name
            }

            11 -> {
                holder.imgMenu!!.setImageResource(R.drawable.attendancenew)
                holder.lblMenuName!!.text = data.name
            }

            17 -> {
                holder.imgMenu!!.setImageResource(R.drawable.chat)
                holder.lblMenuName!!.text = data.name
            }

            16 -> {
                holder.imgMenu!!.setImageResource(R.drawable.communication)
                holder.lblMenuName!!.text = data.name
            }

            //        if (data.id == 2) {
            ////            holder.imgMenu!!.setImageResource(R.drawable.communication)
            ////            holder.lblMenuName!!.text = data.name
            //        }

            3 -> {
                holder.imgMenu!!.setImageResource(R.drawable.exam)
                holder.lblMenuName!!.text = data.name
            }

            4 -> {
                holder.imgMenu!!.setImageResource(R.drawable.attendance)
                holder.lblMenuName!!.text = data.name
            }

            5 -> {
                holder.imgMenu!!.setImageResource(R.drawable.assignment)
                holder.lblMenuName!!.text = data.name
            }

            6 -> {
                holder.imgMenu!!.setImageResource(R.drawable.circular)
                holder.lblMenuName!!.text = context.getString(R.string.txt_img_pdf)
            }

            7 -> {
                holder.imgMenu!!.setImageResource(R.drawable.noticeboard)
                holder.lblMenuName!!.text = data.name
            }

            8 -> {
                holder.imgMenu!!.setImageResource(R.drawable.events)
                holder.lblMenuName!!.text = data.name
            }

            9 -> {
                holder.imgMenu!!.setImageResource(R.drawable.faculy_menu)
                holder.lblMenuName!!.text = data.name
            }

            10 -> {
                holder.imgMenu!!.setImageResource(R.drawable.video)
                holder.lblMenuName!!.text = data.name
            }

            12 -> {
                holder.imgMenu!!.setImageResource(R.drawable.bg_circle_course_details)
                holder.lblMenuName!!.text = data.name
            }

            13 -> {
                holder.imgMenu!!.setImageResource(R.drawable.bg_circle_category_credit)
                holder.lblMenuName!!.text = data.name
            }

            14 -> {
                holder.imgMenu!!.setImageResource(R.drawable.bg_circle_sem_credit)
                holder.lblMenuName!!.text = data.name
            }

            15 -> {
                holder.imgMenu!!.setImageResource(R.drawable.bg_circle_exam_applications)
                holder.lblMenuName!!.text = data.name
            }

            19 -> {
                holder.imgMenu!!.setImageResource(R.drawable.exam_hall_ticket)
                holder.lblMenuName!!.text = data.name
            }

            20 -> {
                holder.imgMenu!!.setImageResource(R.drawable.fee_details)
                holder.lblMenuName!!.text = data.name
            }

            21 -> {
                holder.imgMenu!!.setImageResource(R.drawable.biometric_attendance)
                holder.lblMenuName!!.text = data.name
            }

            22 -> {
                holder.imgMenu!!.setImageResource(R.drawable.attendance_report)
                holder.lblMenuName!!.text = data.name
            }

            else -> {
                holder.imgMenu!!.visibility = View.GONE
                holder.lblMenuName!!.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return menulist.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder((itemView)!!) {

        @JvmField
        @BindView(R.id.lblMenuName)
        var lblMenuName: TextView? = null

        @JvmField
        @BindView(R.id.imgMenu)
        var imgMenu: CircleImageView? = null

        @JvmField
        @BindView(R.id.LayoutHome)
        var LayoutHome: RelativeLayout? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        menulist = data
        this.context = context
    }
}