package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.AcademicRecordsEdit

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vsca.vsnapvoicecollege.Model.GetEducationalDetailsData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutEditacademicdetailsBinding

class EditAcademicDetails : AppCompatActivity() {

    private var appViewModel: App? = null
    private lateinit var binding: LayoutEditacademicdetailsBinding

    private var originalBacklogs: String = ""
    private var originalArrears: String = ""
    private var originalEducationalDetails: List<GetEducationalDetailsData> = listOf()
    var memberId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutEditacademicdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        // Hide default icon and update button text
        binding.commonBottomResumeBuilder.imgDefault.visibility = View.GONE
        binding.commonBottomResumeBuilder.btnDefault2.text = getString(R.string.update)


//        // Get data from intent
//        val backlogs = intent.getStringExtra("backlogs") ?: ""
//        val arrears = intent.getStringExtra("arrears") ?: ""
//        val educationalDetailsJson = intent.getStringExtra("educationalDetails")

        val academicData = CommonUtil.saveAcademicDetails

        val backlogs = academicData?.backlogs ?: ""
        val arrears = academicData?.numberOfArrears ?: ""
        val educationalDetails = academicData?.educationalDetails ?: emptyList()
        Log.d("AcademicDebug", "Backlogs: ${academicData?.backlogs}")
        Log.d("AcademicDebug", "No of Arrears: ${academicData?.numberOfArrears}")
        Log.d("AcademicDebug", "Educational Details Count: ${academicData?.educationalDetails?.size}")


        originalBacklogs = backlogs
        originalArrears = arrears
        originalEducationalDetails = educationalDetails

        binding.edtBacklogs.setText(backlogs)
        binding.edtArrears.setText(arrears)

        if (educationalDetails.isNotEmpty()) {
            educationalDetails.forEach {
                addRow(it)
            }
        } else {
            addRow()
        }


        binding.lblAddAnother.setOnClickListener {
            if (validateCurrentRows()) {
                addRow()
            }
        }

        binding.imgback.setOnClickListener {
            onBackPressed()
        }
        binding.commonBottomResumeBuilder.btnDefault1.setOnClickListener {
            finish()
        }

        // Observe save response
        appViewModel?.resumeBuilderAcademicAddEditResponse?.observe(this) { response ->
            if (response != null && response.status) {
                Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Save failed, please try again.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.commonBottomResumeBuilder.btnSave.setOnClickListener {
            if (!validateCurrentRows()) {
                return@setOnClickListener
            }

            if (isDataChanged()) {
                showSaveConfirmationDialog()
            } else {
                showNoChangesDialog()
            }
        }
    }

    private fun validateCurrentRows(): Boolean {
        for (i in 0 until binding.containerLayout.childCount) {
            val row = binding.containerLayout.getChildAt(i)
            val edtClassDegree = row.findViewById<EditText>(R.id.edtClassDegree)
            val edtPercentage = row.findViewById<EditText>(R.id.edtPercentage)
            val edtInstitution = row.findViewById<EditText>(R.id.edtInstitution)

            val classDegree = edtClassDegree.text.toString().trim()
            val percentage = edtPercentage.text.toString().trim()
            val institution = edtInstitution.text.toString().trim()

            if (classDegree.isEmpty()) {
                edtClassDegree.error = "Enter class/degree"
                edtClassDegree.requestFocus()
                return false
            }

            if (percentage.isEmpty()) {
                edtPercentage.error = "Enter % of marks"
                edtPercentage.requestFocus()
                return false
            }

            if (institution.isEmpty()) {
                edtInstitution.error = "Enter institution/school name"
                edtInstitution.requestFocus()
                return false
            }
        }
        return true
    }


    private fun isDataChanged(): Boolean {
        val currentBacklogs = binding.edtBacklogs.text.toString().trim()
        val currentArrears = binding.edtArrears.text.toString().trim()

        if (currentBacklogs != originalBacklogs || currentArrears != originalArrears) return true

        if (binding.containerLayout.childCount != originalEducationalDetails.size) return true

        for (i in 0 until binding.containerLayout.childCount) {
            val row = binding.containerLayout.getChildAt(i)
            val edtClassDegree = row.findViewById<EditText>(R.id.edtClassDegree)
            val edtPercentage = row.findViewById<EditText>(R.id.edtPercentage)
            val edtInstitution = row.findViewById<EditText>(R.id.edtInstitution)

            val classDegree = edtClassDegree.text.toString().trim()
            val percentage = edtPercentage.text.toString().trim()
            val institution = edtInstitution.text.toString().trim()

            val originalItem = originalEducationalDetails.getOrNull(i)
            if (originalItem == null) return true

            if (classDegree != originalItem.classDegree ||
                percentage != originalItem.percentage ||
                institution != originalItem.institution
            ) {
                return true
            }
        }

        return false
    }

    private fun showSaveConfirmationDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Confirm Save")
        builder.setMessage("Are you sure you want to save these changes?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            saveAcademicDetails()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showNoChangesDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("No Changes Detected")
        builder.setMessage("No changes found. Exit without saving?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            finish()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun addRow(item: GetEducationalDetailsData? = null) {
        val rowView = LayoutInflater.from(this)
            .inflate(R.layout.item_qualification, binding.containerLayout, false)

        val edtClassDegree = rowView.findViewById<EditText>(R.id.edtClassDegree)
        val edtPercentage = rowView.findViewById<EditText>(R.id.edtPercentage)
        val edtInstitution = rowView.findViewById<EditText>(R.id.edtInstitution)
        val imgRemove = rowView.findViewById<View>(R.id.imgRemove)

        imgRemove.setOnClickListener {
            if (binding.containerLayout.childCount > 1) {
                binding.containerLayout.removeView(rowView)
            } else {
                Toast.makeText(this, "At least one entry is required.", Toast.LENGTH_SHORT).show()
            }
        }
        // Pre-fill if editing existing data
        item?.let {
            edtClassDegree.setText(it.classDegree)
            edtPercentage.setText(it.percentage)
            edtInstitution.setText(it.institution)
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.ten)
        rowView.layoutParams = layoutParams

        binding.containerLayout.addView(rowView)
    }


    private fun saveAcademicDetails() {
        val backlogs = binding.edtBacklogs.text.toString().trim()
        val arrears = binding.edtArrears.text.toString().trim()

        val educationalDetails = mutableListOf<Map<String, String>>()

        for (i in 0 until binding.containerLayout.childCount) {
            val row = binding.containerLayout.getChildAt(i)
            val edtClassDegree = row.findViewById<EditText>(R.id.edtClassDegree)
            val edtPercentage = row.findViewById<EditText>(R.id.edtPercentage)
            val edtInstitution = row.findViewById<EditText>(R.id.edtInstitution)

            val classDegree = edtClassDegree.text.toString().trim()
            val percentage = edtPercentage.text.toString().trim()
            val institution = edtInstitution.text.toString().trim()

            educationalDetails.add(
                mapOf(
                    "classDegree" to classDegree,
                    "percentage" to percentage,
                    "institution" to institution
                )
            )
        }

        val request = hashMapOf<String, Any>(
            "idMember" to CommonUtil.MemberId,
            "educationalDetails" to educationalDetails,
            "backlogs" to backlogs,
            "numberOfArrears" to arrears
        )
        appViewModel?.AddEditAcademicDetails(request, this)
    }
}
