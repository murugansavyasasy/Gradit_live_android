package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume


import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.vsca.vsnapvoicecollege.R

import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutBuildmyresumeBinding


class BuildMyResume : AppCompatActivity() {

    var appViewModel: App? = null
    private lateinit var binding: LayoutBuildmyresumeBinding
    private var selectedItem: PickResumeData? = null

    val sampleData = listOf(
        PickResumeData(
            "Cascade",
            "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_92x30dp.png",
            "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
        ),
        PickResumeData(
            "Crisp",
            "https://developer.android.com/images/brand/Android_Robot.png",
            "https://www.africau.edu/images/default/sample.pdf"
        ),
        PickResumeData(
            "Detailed",
            "https://upload.wikimedia.org/wikipedia/commons/7/74/Kotlin_Icon.png",
            "https://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf"
        ),
        PickResumeData(
            "Lorem ipsum",
            "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png",
            "https://www.orimi.com/pdf-test.pdf"
        ),
        PickResumeData(
            "Ipsum dto",
            "https://upload.wikimedia.org/wikipedia/commons/0/04/OpenAI_Logo.svg",
            "https://file-examples.com/storage/fe77e616925e244dc9472d1/2017/10/file-sample_150kB.pdf"
        ),
        PickResumeData(
            "Adgr we",
            "https://developer.android.com/images/jetpack/jetpack-compose-hero.svg",
            "https://www.learningcontainer.com/wp-content/uploads/2019/09/sample-pdf-file.pdf"
        ),
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutBuildmyresumeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        binding.commonBottomResumeBuilder.btnDefault2.text=resources.getString(R.string.proceed)
        binding.commonBottomResumeBuilder.imgDefault.visibility= View.GONE
        loadData()

        binding.commonBottomResumeBuilder.btnDefault2.setOnClickListener{
            selectedItem?.let { item ->
                val intent = Intent(this, ResumePreviewActivity::class.java)
                intent.putExtra("TemplateDocumentURL", item.documentUrl)
                startActivity(intent)
        }
        }



        binding.imgback!!.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadData() {
        binding.rcyPickResume.layoutManager = GridLayoutManager(this, 3)
        val adapter = PickResumeAdapter(sampleData) { selectedDocumentURL ->
            selectedItem=selectedDocumentURL

        }
        binding.rcyPickResume.adapter = adapter
    }


}