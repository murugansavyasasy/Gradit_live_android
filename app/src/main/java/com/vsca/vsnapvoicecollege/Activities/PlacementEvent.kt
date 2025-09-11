package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Adapters.EventPlacementAdapter
import com.vsca.vsnapvoicecollege.Model.GetPlacementEventData
import com.vsca.vsnapvoicecollege.Model.PlacementEventData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.PlacementEventBinding

class PlacementEvent : BaseActivity<PlacementEventBinding>() {

    override var appViewModel: App? = null
    override fun inflateBinding(): PlacementEventBinding {
        return PlacementEventBinding.inflate(layoutInflater)
    }

    var isPlacementAdapter: EventPlacementAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PlacementEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        appViewModel!!.isPlacementEventResponse?.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status) {
                    binding.ErrorMessage.visibility=View.GONE
                    binding.rcyPlacementEvent.visibility=View.VISIBLE
                    isLoadData(response.data)
                }
                else{
                    binding.rcyPlacementEvent.visibility=View.GONE
                    binding.ErrorMessage.visibility=View.VISIBLE
                   binding.ErrorMessage.text=response.message
                }
            }
            else{
                binding.rcyPlacementEvent.visibility=View.GONE
                binding.ErrorMessage.visibility=View.VISIBLE
                binding.ErrorMessage.text="No Record Found"
            }
        }

//        binding.lblCareerTraining.setOnClickListener{
//            val intent = Intent(this, CareerTraining::class.java)
//            this.startActivity(intent)
//
//        }

        appViewModel!!.isPlacementHistoricalEventResponse?.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status) {
                    Log.d("response.data",response.data.size.toString())
                    if (response.data.isNotEmpty()){
                        binding.ErrorMessage.visibility=View.GONE
                        binding.rcyHistoricalEvent.visibility=View.VISIBLE
                        isLoadHistoricalData(response.data)
                    }else{
                        binding.ErrorMessage.visibility=View.VISIBLE
                        binding.rcyHistoricalEvent.visibility=View.GONE
                    }

                }
                else{
                    binding.rcyHistoricalEvent.visibility=View.GONE
                    binding.ErrorMessage.visibility=View.VISIBLE
                    binding.ErrorMessage.text=response.message
                }
            }
            else{
                binding.rcyHistoricalEvent.visibility=View.GONE
                binding.ErrorMessage.visibility=View.VISIBLE
                binding.ErrorMessage.text="No Record Found"
            }
        }

        isUpcomingEventData()

        binding.lnrTabOneName.setOnClickListener {
            binding.lnrTabOneName.isEnabled = false
            binding.lnrTabTwoName.isEnabled = true
            binding.line1.setBackgroundResource(R.color.dark_blue)
            binding.tabOneName.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
            binding.tabTwoName.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.line2.setBackgroundResource(R.color.light_gray_3)
            binding.rcyHistoricalEvent.visibility=View.GONE
            binding.ErrorMessage.visibility=View.GONE
            binding.rcyPlacementEvent.visibility=View.VISIBLE

            isUpcomingEventData()
        }

        binding.lnrTabTwoName.setOnClickListener {
            binding.lnrTabOneName.isEnabled = true
            binding.lnrTabTwoName.isEnabled = false
            binding.tabOneName.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.tabTwoName.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
            binding.line2.setBackgroundResource(R.color.dark_blue)
            binding.line1.setBackgroundResource(R.color.light_gray_3)
            binding.rcyPlacementEvent.visibility=View.GONE
            binding.ErrorMessage.visibility=View.GONE
            binding.rcyHistoricalEvent.visibility=View.VISIBLE
            isHistoricalEventData()
        }


        binding.imgheaderBack.setOnClickListener {
            onBackPressed()
        }

    }

    fun isUpcomingEventData(){
        appViewModel!!.isPlacementEventData(CommonUtil.CollegeId.toString(),CommonUtil.MemberId, this)
    }

    fun isHistoricalEventData(){
        appViewModel!!.isPlacementHistoricalEventData(CommonUtil.CollegeId.toString(),CommonUtil.MemberId, this)
    }

    fun isLoadData(isPlacementData: List<GetPlacementEventData>) {
        isPlacementAdapter =
            EventPlacementAdapter(isPlacementData.get(0).events, this)
        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        binding.rcyPlacementEvent!!.layoutManager = mLayoutManager
        binding.rcyPlacementEvent!!.adapter = isPlacementAdapter
        isPlacementAdapter!!.notifyDataSetChanged()
    }

    fun isLoadHistoricalData(isPlacementData: List<GetPlacementEventData>) {
        isPlacementAdapter =
            EventPlacementAdapter(isPlacementData.get(0).events, this)
        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        binding.rcyHistoricalEvent!!.layoutManager = mLayoutManager
        binding.rcyHistoricalEvent!!.adapter = isPlacementAdapter
        isPlacementAdapter!!.notifyDataSetChanged()
    }

    override val layoutResourceId: Int
        get() = R.layout.placement_event
}