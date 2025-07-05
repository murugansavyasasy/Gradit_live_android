package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutEditskillsetBinding

class EditSkillSet : AppCompatActivity() {

    var appViewModel: App? = null
    private lateinit var binding:LayoutEditskillsetBinding
    private val assessmentList = listOf("Select","Aptitude","Coding","Assignment")
    private val areaofInterest = listOf("Select","Technology","Writing","Travel","Volunteering","Learning Languages")
    private val programmingLanguage = listOf("Select","Python","Java","C","C++","Kotlin","Assemble Language")
    private val toolsAndPlatform = listOf("Select","Firebase","LeetCode","JetBrains IDE","Android Studio","Swift")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutEditskillsetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        binding.commonBottomResumeBuilder.imgDefault.visibility= View.GONE
        addAssessmentRow()

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