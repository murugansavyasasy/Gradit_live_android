package com.vsca.vsnapvoicecollege.Activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Adapters.EventPlacementAdapter
import com.vsca.vsnapvoicecollege.Model.PlacementEventData
import com.vsca.vsnapvoicecollege.R
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
                    isLoadData(response.data)
                }
            }
        }

        binding.imgheaderBack.setOnClickListener {
            onBackPressed()
        }

        appViewModel!!.isPlacementEventData(0, this)
    }

    fun isLoadData(isPlacementData: List<PlacementEventData>) {
        isPlacementAdapter =
            EventPlacementAdapter(isPlacementData, this)
        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        binding.rcyPlacementEvent!!.layoutManager = mLayoutManager
        binding.rcyPlacementEvent!!.adapter = isPlacementAdapter
        isPlacementAdapter!!.notifyDataSetChanged()
    }

    override val layoutResourceId: Int
        get() = R.layout.placement_event
}