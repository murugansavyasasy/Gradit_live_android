package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.MyProfileEdit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutEditbasicdetailsBinding

class EditBasicDetails : AppCompatActivity() {

    var appViewModel: App? = null
    private lateinit var binding: LayoutEditbasicdetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutEditbasicdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        binding.commonBottomResumeBuilder.imgDefault.visibility= View.GONE





        binding.imgback!!.setOnClickListener {
            onBackPressed()
        }
    }

}