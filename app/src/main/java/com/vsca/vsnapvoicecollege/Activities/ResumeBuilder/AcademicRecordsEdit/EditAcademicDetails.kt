package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.AcademicRecordsEdit

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutEditacademicdetailsBinding

class EditAcademicDetails : AppCompatActivity() {

    private var appViewModel: App? = null
    private lateinit var binding: LayoutEditacademicdetailsBinding

    private val qualificationList = listOf(
        "Select", "10 th", "11 th", "12th",
        "Under Graduate", "Post Graduate", "Doctorate"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutEditacademicdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        binding.commonBottomResumeBuilder.imgDefault.visibility = View.GONE
        binding.commonBottomResumeBuilder.btnDefault2.text = getString(R.string.update)

        val backlogs = intent.getStringExtra("backlogs") ?: ""
        val arrears = intent.getStringExtra("arrears") ?: ""

        binding.edtBacklogs.setText(backlogs)
        binding.edtArrears.setText(arrears)

        addRow() // add first empty row

        binding.lblAddAnother.setOnClickListener {
            addRow()
        }

        binding.imgback.setOnClickListener {
            onBackPressed()
        }

        // observe API response
        appViewModel?.resumeBuilderAcademicAddEditResponse?.observe(this) { response ->
            if (response != null && response.status) {
                Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK) // notify parent to reload data
                finish()
            } else {
                Toast.makeText(this, "Save failed, please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.commonBottomResumeBuilder.btnSave.setOnClickListener {
            saveAcademicDetails()
        }
    }

    private fun addRow() {
        val rowView = LayoutInflater.from(this)
            .inflate(R.layout.item_qualification, binding.containerLayout, false)

        val spinner = rowView.findViewById<Spinner>(R.id.SpinnerQualification)

        val adapter = object : ArrayAdapter<String>(this, 0, qualificationList) {
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
                textView.text = qualificationList[position]
                return view
            }
        }

        spinner.adapter = adapter
        binding.containerLayout.addView(rowView)
    }

    private fun saveAcademicDetails() {
        val backlogs = binding.edtBacklogs.text.toString().trim()
        val arrears = binding.edtArrears.text.toString().trim()

        val educationalDetails = mutableListOf<Map<String, String>>()

        for (i in 0 until binding.containerLayout.childCount) {
            val row = binding.containerLayout.getChildAt(i)
            val spinner = row.findViewById<Spinner>(R.id.SpinnerQualification)
            val edtPercentage = row.findViewById<EditText>(R.id.edtPercentage)

            val qualification = spinner.selectedItem.toString()
            val percentage = edtPercentage.text.toString().trim()
            val institution = "ABC Institute of Technology" // replace later if you have input

            if (qualification != "Select" && percentage.isNotEmpty()) {
                educationalDetails.add(
                    mapOf(
                        "classDegree" to qualification,
                        "percentage" to percentage,
                        "institution" to institution
                    )
                )
            }
        }

        // build request with fixed idMember = 31145
        val request = hashMapOf<String, Any>(
            "idMember" to 31145,
            "educationalDetails" to educationalDetails,
            "backlogs" to backlogs,
            "numberOfArrears" to arrears
        )

        appViewModel?.AddEditAcademicDetails(request, this)
    }
}