package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.MyProfileEdit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide

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
        binding.commonBottomResumeBuilder.imgDefault.visibility = View.GONE

        binding.imgback!!.setOnClickListener {
            onBackPressed()
        }

        val name = intent.getStringExtra("name")
        val phone = intent.getStringExtra("phone")
        val email = intent.getStringExtra("email")
        val image = intent.getStringExtra("image")

        binding.edtName.setText(name)
        binding.edtPhone.setText(phone)
        binding.edtEmail.setText(email)

        Glide.with(this)
            .load(image)
            .placeholder(com.vsca.vsnapvoicecollege.R.drawable.test_image)
            .into(binding.imgProfile)
    }
}