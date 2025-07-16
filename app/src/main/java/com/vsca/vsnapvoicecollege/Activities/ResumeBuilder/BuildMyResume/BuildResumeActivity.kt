package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vsca.vsnapvoicecollege.databinding.ActivityBuildmyresumeBinding

class BuildResumeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuildmyresumeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuildmyresumeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.commonBottomResumeBuilder.btnSave.text = "Next"

        binding.commonBottomResumeBuilder.btnSave.setOnClickListener {
            val intent = Intent(this, BuildMyResume::class.java)
            startActivity(intent)
        }
        binding.cb10th.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
            } else {
            }
        }
    }
}
