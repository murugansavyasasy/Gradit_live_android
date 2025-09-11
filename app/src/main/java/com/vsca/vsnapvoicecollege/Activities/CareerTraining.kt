package com.vsca.vsnapvoicecollege.Activities


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Adapters.CareerTrainingAdapter
import com.vsca.vsnapvoicecollege.Model.CareerTrainingData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.CareerTrainingBinding

class CareerTraining : BaseActivity<CareerTrainingBinding>() {

    override var appViewModel: App? = null
    override fun inflateBinding(): CareerTrainingBinding {
        return CareerTrainingBinding.inflate(layoutInflater)
    }

    var isPlacementAdapter: CareerTrainingAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CareerTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()

        appViewModel!!.isPlacementCareerResponse?.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status) {
                    if (response.data.isNotEmpty()){
                        binding.ErrorMessage.visibility=View.GONE
                        binding.rcyPlacementTraining.visibility=View.VISIBLE
                        isLoadData(response.data)
                    }else{
                        binding.ErrorMessage.visibility=View.VISIBLE
                        binding.rcyPlacementTraining.visibility=View.GONE
                    }

                }
                else{
                    binding.rcyPlacementTraining.visibility=View.GONE
                    binding.ErrorMessage.visibility=View.VISIBLE
                    binding.ErrorMessage.text=response.message
                }
            }
            else{
                binding.rcyPlacementTraining.visibility=View.GONE
                binding.ErrorMessage.visibility=View.VISIBLE
                binding.ErrorMessage.text="No Record Found"
            }
        }

        appViewModel!!.isPlacementHistoricalCareerResponse?.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status) {
                    if (response.data.isNotEmpty()){
                        binding.ErrorMessage.visibility=View.GONE
                        binding.rcyPlacementTraining.visibility=View.VISIBLE
                        isLoadHistoricalData(response.data)
                    }else{
                        binding.ErrorMessage.visibility=View.VISIBLE
                        binding.rcyPlacementTraining.visibility=View.GONE
                    }

                }
                else{
                    binding.rcyPlacementTraining.visibility=View.GONE
                    binding.ErrorMessage.visibility=View.VISIBLE
                    binding.ErrorMessage.text=response.message
                }
            }
            else{
                binding.rcyPlacementTraining.visibility=View.GONE
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
            binding.rcyPlacementTraining.visibility=View.GONE
            binding.ErrorMessage.visibility=View.GONE
            binding.rcyPlacementTraining.visibility=View.VISIBLE

            isUpcomingEventData()
        }

        binding.lnrTabTwoName.setOnClickListener {
            binding.lnrTabOneName.isEnabled = true
            binding.lnrTabTwoName.isEnabled = false
            binding.tabOneName.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.tabTwoName.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
            binding.line2.setBackgroundResource(R.color.dark_blue)
            binding.line1.setBackgroundResource(R.color.light_gray_3)
            binding.rcyPlacementTraining.visibility=View.GONE
            binding.ErrorMessage.visibility=View.GONE
            binding.rcyPlacementTraining.visibility=View.VISIBLE
            isHistoricalEventData()
        }

        binding.imgheaderBack.setOnClickListener {
            onBackPressed()
        }

    }

    fun isUpcomingEventData(){
        val items =  CommonUtil.SemesteName
        val isSemeName = items.split(" ")

        val firstName = isSemeName[0]
        val lastName = isSemeName[1]

        println(firstName)
        println(lastName)
        appViewModel!!.isPlacementCareerData(CommonUtil.CollegeId.toString(),CommonUtil.deptname, lastName.toInt(), this)
    }

    fun isHistoricalEventData(){

        val items =  CommonUtil.SemesteName
        val isSemeName = items.split(" ")

        val firstName = isSemeName[0]
        val lastName = isSemeName[1]

        println(firstName)
        println(lastName)


        appViewModel!!.isPlacementHostoricalCareerData(CommonUtil.CollegeId.toString(),CommonUtil.deptname, lastName.toInt(), this)
    }

    fun isLoadData(isPlacementData: List<CareerTrainingData>) {
        isPlacementAdapter =
            CareerTrainingAdapter(isPlacementData, this)
        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        binding.rcyPlacementTraining!!.layoutManager = mLayoutManager
        binding.rcyPlacementTraining!!.adapter = isPlacementAdapter
        isPlacementAdapter!!.notifyDataSetChanged()
    }

    fun isLoadHistoricalData(isPlacementData: List<CareerTrainingData>) {
        isPlacementAdapter =
            CareerTrainingAdapter(isPlacementData, this)
        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        binding.rcyPlacementTraining!!.layoutManager = mLayoutManager
        binding.rcyPlacementTraining!!.adapter = isPlacementAdapter
        isPlacementAdapter!!.notifyDataSetChanged()
    }

    override val layoutResourceId: Int
        get() = R.layout.career_training
}