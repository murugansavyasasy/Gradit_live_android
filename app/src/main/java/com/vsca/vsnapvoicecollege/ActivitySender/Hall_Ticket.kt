package com.vsca.vsnapvoicecollege.ActivitySender

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.arindicatorview.ARIndicatorView
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.HallticketAdapter
import com.vsca.vsnapvoicecollege.Model.HallticketResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App

class Hall_Ticket : AppCompatActivity() {

    var appViewModel: App? = null
    private var hallTicketAdapter: HallticketAdapter? = null
    private var hallTicketResponse: List<HallticketResponse> = ArrayList()

    @JvmField
    @BindView(R.id.rcy_hallticket)
    var rcy_hallticket: RecyclerView? = null

    @JvmField
    @BindView(R.id.imgheaderBack)
    var imgheaderBack: ImageView? = null

    @JvmField
    @BindView(R.id.scrolling)
    var scrolling: ScrollView? = null

    private lateinit var arIndicatorView: ARIndicatorView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hall_ticket)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        ButterKnife.bind(this)
        hallTicket()
        CommonUtil.OnMenuClicks("Hallticket")
        arIndicatorView = findViewById(R.id.ar_indicator)

        appViewModel!!.Hallticket!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    hallTicketResponse = response.data
                    hallTicketAdapter =
                        HallticketAdapter(hallTicketResponse, this@Hall_Ticket)
                    val layoutManager =
                        LinearLayoutManager(this@Hall_Ticket, LinearLayoutManager.HORIZONTAL, false)

                    rcy_hallticket!!.layoutManager = layoutManager
                    rcy_hallticket!!.itemAnimator = DefaultItemAnimator()
                    rcy_hallticket!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    rcy_hallticket!!.adapter = hallTicketAdapter
                    hallTicketAdapter!!.notifyDataSetChanged()

                    arIndicatorView.attachTo(rcy_hallticket!!, true)
                    arIndicatorView.indicatorSize = 50
                    arIndicatorView.indicatorShape = R.drawable.dot_indicatorshape
                    arIndicatorView.selectionColor =
                        ContextCompat.getColor(this@Hall_Ticket, R.color.btn_clr_blue)
                    arIndicatorView.indicatorColor =
                        ContextCompat.getColor(this@Hall_Ticket, R.color.clr_bgred)
                    arIndicatorView.isScrubbingEnabled = true
                    arIndicatorView.isShouldAnimateOnScrubbing = true
                    arIndicatorView.indicatorAnimation = R.anim.dot_indicator

                } else {
                    CommonUtil.ApiAlert(this@Hall_Ticket, message)
                }
            }
        }

        imgheaderBack!!.setOnClickListener {
            onBackPressed()
        }
    }

    private fun hallTicket() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_i_course_id, CommonUtil.Courseid)
        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_i_sem_id, CommonUtil.semesterid)
        jsonObject.addProperty(ApiRequestNames.Req_i_student_id, CommonUtil.MemberId)
        appViewModel!!.getHallticket(jsonObject, this@Hall_Ticket)
        Log.d("hallTicket:", jsonObject.toString())
    }
}