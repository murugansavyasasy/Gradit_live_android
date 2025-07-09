package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderAcademicDetailsAdapter
import com.vsca.vsnapvoicecollege.Adapters.ResumeBuilderSoftSkillsAdapter
import com.vsca.vsnapvoicecollege.Model.GetEducationalDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderSkillSetDetailsData
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderSkillSetSoftSkills

import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutEditskillsetBinding

class EditSkillSet : AppCompatActivity() {

    var appViewModel: App? = null
    private lateinit var binding:LayoutEditskillsetBinding
    private val assessmentList = listOf("Select","Aptitude","Coding","Assignment")
    private val areaofInterest = listOf("Select","Technology","Writing","Travel","Volunteering","Learning Languages")
    private val programmingLanguage = listOf("Select","Python","Java","C","C++","Kotlin","Assemble Language")
    private val toolsAndPlatform = listOf("Select","Firebase","LeetCode","JetBrains IDE","Android Studio","Swift")
    val isActuallSoftSkills = mutableListOf<GetResumeBuilderSkillSetSoftSkills>()
    val isCurrentSoftSkills = mutableListOf<GetResumeBuilderSkillSetSoftSkills>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutEditskillsetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        binding.commonBottomResumeBuilder.imgDefault.visibility= View.GONE
        addAssessmentRow()
        GetSoftSkillsDetails()




        appViewModel?.ResumeBuilderSoftSkillsDetails!!.observe(this) { response ->
            if (response != null) {
                if (response.status) {
                    isLoadSoftSkillsDetails(response.data)
                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        appViewModel?.ResumeBuilderEditSkillSetDetails!!.observe(this) { response ->
            if (response != null) {

                if (response.status) {
                    Log.d("EditSkillSet","Scucessful EDit")
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.lblAddAnother!!.setOnClickListener {
            addAssessmentRow()
        }


        binding.imgback!!.setOnClickListener {
            onBackPressed()
        }


        //Area of Interest Spinner
        val adapterAreaOfInterest = object : ArrayAdapter<String>(this, 0, areaofInterest) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = createCustomView(position, convertView, parent)
                val arrow = view.findViewById<ImageView>(R.id.imgArrow)
                arrow.visibility = View.VISIBLE // Show arrow when Spinner is closed
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = createCustomView(position, convertView, parent)
                val arrow = view.findViewById<ImageView>(R.id.imgArrow)
                arrow.visibility = View.GONE // Hide arrow when dropdown is open
                return view
            }

            private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.custom_item_txt_spinner, parent, false)

                val textView = view.findViewById<TextView>(R.id.txtDefault)
                val arrow = view.findViewById<ImageView>(R.id.imgArrow)
                textView.text = areaofInterest[position]
                return view
            }
        }
        binding.spinnerAreaOfInterest.adapter = adapterAreaOfInterest

        //ProgrammingLanguage
        val adapterProgrammingLanguage = object : ArrayAdapter<String>(this, 0, programmingLanguage) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = createCustomView(position, convertView, parent)
                val arrow = view.findViewById<ImageView>(R.id.imgArrow)
                arrow.visibility = View.VISIBLE // Show arrow when Spinner is closed
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = createCustomView(position, convertView, parent)
                val arrow = view.findViewById<ImageView>(R.id.imgArrow)
                arrow.visibility = View.GONE // Hide arrow when dropdown is open
                return view
            }

            private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.custom_item_txt_spinner, parent, false)

                val textView = view.findViewById<TextView>(R.id.txtDefault)
                val arrow = view.findViewById<ImageView>(R.id.imgArrow)

                textView.text = programmingLanguage[position]

                // Only show arrow if not the first/default item
                arrow.visibility = if (position != 0) View.VISIBLE else View.GONE

                return view
            }
        }
        binding.spinnerProgrammingLanguage.adapter = adapterProgrammingLanguage

        //Tools and Platform
        val adapterToolsAndPlatform = object : ArrayAdapter<String>(this, 0, toolsAndPlatform) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = createCustomView(position, convertView, parent)
                val arrow = view.findViewById<ImageView>(R.id.imgArrow)
                arrow.visibility = View.VISIBLE // Show arrow when Spinner is closed
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = createCustomView(position, convertView, parent)
                val arrow = view.findViewById<ImageView>(R.id.imgArrow)
                arrow.visibility = View.GONE // Hide arrow when dropdown is open
                return view
            }

            private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.custom_item_txt_spinner, parent, false)

                val textView = view.findViewById<TextView>(R.id.txtDefault)
                val arrow = view.findViewById<ImageView>(R.id.imgArrow)

                textView.text = toolsAndPlatform[position]

                // Only show arrow if not the first/default item
                arrow.visibility = if (position != 0) View.VISIBLE else View.GONE

                return view
            }
        }
        binding.spinnerToolAndPlatform.adapter = adapterToolsAndPlatform

    }
    fun GetSoftSkillsDetails() {
//        appViewModel!!.GetResumeBuilderSoftSkillsDetails(this@EditSkillSet)
        appViewModel!!.GetResumeBuilderSoftSkillsDetails(this@EditSkillSet)
    }

    fun UpdateEditSkillSetDetails() {
        val jsonObject = JsonObject().apply {
        }
        appViewModel!!.SendEditSkillSetDetails(jsonObject,this@EditSkillSet)
    }



    private fun isLoadSoftSkillsDetails(data: List<String>) {
        if (data.size>0){
            binding.rcSoftSkills.layoutManager = GridLayoutManager(this, 2)
            binding.rcSoftSkills.isNestedScrollingEnabled = false
            binding.rcSoftSkills.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            binding.rcSoftSkills.adapter =
                ResumeBuilderSoftSkillsAdapter(data)
        }


    }


    private fun addAssessmentRow() {
        val rowView = LayoutInflater.from(this).inflate(R.layout.item_qualification, binding.AssessmentcontainerLayout, false)

        val spinner = rowView.findViewById<Spinner>(R.id.SpinnerQualification)
        val editText = rowView.findViewById<EditText>(R.id.edtPercentage)

        // Custom adapter defined inside MainActivity
        val adapter = object : ArrayAdapter<String>(this, 0, assessmentList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createCustomView(position, convertView, parent)
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createCustomView(position, convertView, parent)
            }

            private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.item_spinner_qualification, parent, false)
                val textView = view.findViewById<TextView>(R.id.textQualification)
                textView.text = assessmentList[position]
                return view
            }
        }

        spinner.adapter = adapter
        binding.AssessmentcontainerLayout.addView(rowView)
    }

}