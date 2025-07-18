package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.ResumeBuilder
import com.vsca.vsnapvoicecollege.Adapters.PickThemeTemplateColourAdapter
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderThemeTemplateImage
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutBuildmyresumeBinding


class BuildMyResume : AppCompatActivity() {

    var appViewModel: App? = null
    private lateinit var binding: LayoutBuildmyresumeBinding
    private var selectedImageItem: GetResumeBuilderThemeTemplateImage? = null
    private lateinit var pickThemeTemplateColourAdapter: PickThemeTemplateColourAdapter
    private lateinit var pickResumeAdapter: PickResumeAdapter
    private var selectedColourItem: String? = null
    private var isMemeberID: Int? = -1
    private var selectedTemplateNumber: Int? =-1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = LayoutBuildmyresumeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        binding.commonBottomResumeBuilder.btnDefault2.text = resources.getString(R.string.proceed)
        binding.commonBottomResumeBuilder.imgDefault.visibility = View.GONE

        GetResumeBuilderThemeTemplate()


        binding.commonBottomResumeBuilder.btnSave.setOnClickListener {
            var isValid=true
            if (selectedImageItem == null||selectedTemplateNumber==-1) {
                isValid=false
                Toast.makeText(this, "Please select a resume template", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedColourItem.isNullOrEmpty()) {
                isValid=false
                Toast.makeText(this, "Please select a theme color", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isValid){
                showConfirmationDialog()
            }
        }


        appViewModel?.ResumeBuilderThemeTemplate!!.observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    if (response.data.isNotEmpty()) {
                        val templateList = response.data.find { it.template != null }?.template ?: emptyList()
                        val colorList = response.data.find { it.themecolor != null }?.themecolor ?: emptyList()
                        isLoadThemeTemplate(templateList, colorList)
                        binding.CommonLayout.visibility = View.VISIBLE
                        Log.d("GetResumeBuilderThemeTemplate", response.data.toString())
                    } else {
                        binding.CommonLayout.visibility = View.GONE
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    binding.CommonLayout.visibility = View.GONE
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        appViewModel?.ResumeBuilderGenerateResume!!.observe(this) { response ->
            if (response != null) {

                if (response.status) {
                    if (response.data.get(0).status==1){
                        Log.d("GeneratedResumeURl",response.data.get(0).file_url)
                            val intent = Intent(this, ResumePreviewActivity::class.java)
                            intent.putExtra("TemplateDocumentURL",response.data.get(0).file_url)
                            intent.putExtra("MemberID",isMemeberID)
                            startActivity(intent)
                        Log.d("GenerateResume", "Reusme Generattion Scucessful")
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Log.d("GenerateResume", "Reusme Generattion Failed")
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("GenerateResume", "Reusme Generattion Failed")
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.commonBottomResumeBuilder.btnDefault1!!.setOnClickListener {
            onBackPressed()
        }

        binding.imgback!!.setOnClickListener {
            onBackPressed()
        }
    }

    private fun isLoadThemeTemplate(
        templateList: List<GetResumeBuilderThemeTemplateImage>,
        themecolorList: List<String>
    ) {
        // Resume Template Image Adapter
        binding.rcyPickResume.layoutManager = GridLayoutManager(this, 3)
        pickResumeAdapter = PickResumeAdapter(templateList) { selectedImage,templateNo ->
            selectedImageItem = selectedImage
            selectedTemplateNumber=templateNo
        }
        binding.rcyPickResume.isNestedScrollingEnabled = false
        binding.rcyPickResume.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.rcyPickResume.adapter = pickResumeAdapter

        //Theme Color Adapter
        binding.rcThemeColour.layoutManager = GridLayoutManager(this, 5)
        pickThemeTemplateColourAdapter =
            PickThemeTemplateColourAdapter(themecolorList) { selectedColour ->
                selectedColourItem = selectedColour
            }
        binding.rcThemeColour.isNestedScrollingEnabled = false
        binding.rcThemeColour.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.rcThemeColour.adapter = pickThemeTemplateColourAdapter
    }

    fun GetResumeBuilderThemeTemplate() {
        appViewModel!!.GetResumeBuilderThemeTemplate(this@BuildMyResume)
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)

        val message = buildString {
            append("Are you sure you want to generate resume?")
        }

        builder.setTitle("Confirmation")
            .setMessage(message)
            .setPositiveButton("Yes") { dialog, _ ->
//                isGenerateResume()

                //manually pdf
                val intent = Intent(this, ResumePreviewActivity::class.java)
                intent.putExtra("TemplateDocumentURL","https://gradit-communication.s3.ap-south-1.amazonaws.com/2025-02-12/7033/Resume_1752815189344.pdf")
                intent.putExtra("MemberID",31146)
                intent.putExtra("ScreenName","BuildMyResume")
                startActivity(intent)

                //manual pdf

                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    fun isGenerateResume() {
        Log.d("PreviewInfo", "TemplateImage "+selectedImageItem!!.resume_template_image+
                "colourCode "+selectedColourItem+
                "templateNo "+selectedTemplateNumber)

        isMemeberID=31146
        val gson = Gson()

        // Parent JSON object
        val parentJson = JsonObject()

        // Context object (inner content)
        val contextJson = JsonObject().apply {
            // Basic info
            addProperty("name", "Virat Kohli")
            addProperty("email", "viratkohli@example.com")
            addProperty("phone", "1234567890")
            addProperty("address", "1/111,t nagar,chennai")

            // skills
            add("skills", JsonArray().apply {
                listOf("JavaScript", "Node.js", "angular", "figma", "python").forEach { add(it) }
            })

            // education
            add("education", JsonArray().apply {
                add(
                    gson.toJsonTree(
                        mapOf(
                            "class" to "10th std",
                            "percentage" to "56",
                            "institution" to "alagappa matric hr sec school"
                        )
                    )
                )
                add(
                    gson.toJsonTree(
                        mapOf(
                            "class" to "12th std",
                            "percentage" to "78",
                            "institution" to "alagappa matric hr sec school"
                        )
                    )
                )
                add(
                    gson.toJsonTree(
                        mapOf(
                            "degree" to "B.Tech",
                            "percentage" to "72",
                            "institution" to "srm education of technology"
                        )
                    )
                )
            })

            // experience
            add("internship", JsonArray().apply {
                add(
                    gson.toJsonTree(
                        mapOf(
                            "designation" to "Full stack developer",
                            "company" to "TechCorp",
                            "duration" to "2020-2023"
                        )
                    )
                )
                add(
                    gson.toJsonTree(
                        mapOf(
                            "designation" to "software engineer",
                            "company" to "ancc",
                            "duration" to "2024-2025"
                        )
                    )
                )
            })

            // area of interest
            add("areainterest", JsonArray().apply {
                listOf("Full stack developer", "UI UX Design", "data science", "AI").forEach {
                    add(
                        it
                    )
                }
            })

            // soft skills
            add("softSkill", JsonArray().apply {
                listOf("Teamwork", "Adaptability", "Communication").forEach { add(it) }
            })

            // certifications
            add("certifications", JsonArray().apply {
                add(
                    gson.toJsonTree(
                        mapOf(
                            "course" to "aws",
                            "institute" to "abc institute",
                            "duration" to "2 hours"
                        )
                    )
                )
                add(
                    gson.toJsonTree(
                        mapOf(
                            "course" to "powerbi",
                            "institute" to "technology institute",
                            "duration" to "2 hours"
                        )
                    )
                )
                add(
                    gson.toJsonTree(
                        mapOf(
                            "course" to "node.js",
                            "institute" to "xyz institute",
                            "duration" to "2 hours"
                        )
                    )
                )
            })

            // languages
            add("languages", JsonArray().apply {
                listOf("Tamil", "English", "Hindi").forEach { add(it) }
            })

            // projects
            add("projects", JsonArray().apply {
                add(gson.toJsonTree(mapOf("title" to "ticket booking website")))
                add(gson.toJsonTree(mapOf("title" to "erp website")))
            })
        }

// Final structure
        parentJson.add("context", contextJson)
        parentJson.addProperty("templateNumber", selectedTemplateNumber)
        parentJson.addProperty("themeColor", selectedColourItem)
        parentJson.addProperty("bucket", CommonUtil.isRBBucketName)
        parentJson.addProperty("bucketPath", CommonUtil.isRBBucketPath)
        parentJson.addProperty("idMember", isMemeberID)

        Log.d("GeneratedJSON", parentJson.toString())

        appViewModel!!.SendGenerateResume(parentJson, this@BuildMyResume)

    }


}