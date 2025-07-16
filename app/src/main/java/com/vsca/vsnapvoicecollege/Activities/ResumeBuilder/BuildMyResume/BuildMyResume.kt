package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.vsca.vsnapvoicecollege.Adapters.PickThemeTemplateColourAdapter
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderThemeTemplate
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderThemeTemplateData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderThemeTemplateImage
import com.vsca.vsnapvoicecollege.R

import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutBuildmyresumeBinding


class BuildMyResume : AppCompatActivity() {

    var appViewModel: App? = null
    private lateinit var binding: LayoutBuildmyresumeBinding
    private var selectedImageItem: GetResumeBuilderThemeTemplateImage? = null
    private lateinit var pickThemeTemplateColourAdapter: PickThemeTemplateColourAdapter
    private lateinit var pickResumeAdapter: PickResumeAdapter
    private var selectedColourItem: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutBuildmyresumeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        binding.commonBottomResumeBuilder.btnDefault2.text=resources.getString(R.string.proceed)
        binding.commonBottomResumeBuilder.imgDefault.visibility= View.GONE

        GetResumeBuilderThemeTemplate()

        binding.commonBottomResumeBuilder.btnDefault2.setOnClickListener{
            selectedImageItem?.let { item ->
                Log.d("TemplateImageURL", item.templateImage)
//                val intent = Intent(this, ResumePreviewActivity::class.java)
//                intent.putExtra("TemplateDocumentURL", item.templateImage)
//                startActivity(intent)
        }
            selectedColourItem?.let { item->
                Log.d("TemplateColorCode", item)
            }
        }



        appViewModel?.ResumeBuilderThemeTemplate!!.observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    if (response.data.size>0) {
                        isLoadThemeTemplate(response.data[0])
                        binding.CommonLayout.visibility=View.VISIBLE
                        Log.d("GetResumeBuilderThemeTemplate", response.data.toString())
                    }
                    else{
                        binding.CommonLayout.visibility=View.GONE
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    binding.CommonLayout.visibility=View.GONE
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }



        binding.imgback!!.setOnClickListener {
            onBackPressed()
        }
    }

    private fun isLoadThemeTemplate(getResumeBuilderThemeTemplateData: GetResumeBuilderThemeTemplateData) {
        //Template Image Adapter
        binding.rcyPickResume.layoutManager = GridLayoutManager(this, 3)
        pickResumeAdapter = PickResumeAdapter(getResumeBuilderThemeTemplateData.templateImages) { selectedImage ->
            selectedImageItem=selectedImage

        }
        binding.rcyPickResume.adapter = pickResumeAdapter


        //Colour code Adapter
        binding.rcyPickResume.layoutManager = GridLayoutManager(this, 5)
        pickThemeTemplateColourAdapter = PickThemeTemplateColourAdapter(getResumeBuilderThemeTemplateData.themeColors) { selectedColour ->
            selectedColourItem=selectedColour

        }
        binding.rcyPickResume.adapter = pickThemeTemplateColourAdapter


    }

    fun GetResumeBuilderThemeTemplate() {
        appViewModel!!.GetResumeBuilderThemeTemplate(this@BuildMyResume)
    }


}