package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.AcademicRecordsEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.LayoutEditacademicdetailsBinding

class EditAcademicDetails : AppCompatActivity() {

    var appViewModel: App? = null
    private lateinit var binding: LayoutEditacademicdetailsBinding
    private val qualificationList = listOf("Select", "10 th", "11 th", "12th","Under Graduate","Post Graduate","Doctorate")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutEditacademicdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        binding.commonBottomResumeBuilder.imgDefault.visibility= View.GONE


        addRow()

        binding.lblAddAnother.setOnClickListener {
            addRow()
        }



        binding.imgback!!.setOnClickListener {
            onBackPressed()
        }
    }
    private fun addRow() {
        val rowView = LayoutInflater.from(this).inflate(R.layout.item_qualification, binding.containerLayout, false)

        val spinner = rowView.findViewById<Spinner>(R.id.SpinnerQualification)
        val editText = rowView.findViewById<EditText>(R.id.edtPercentage)

        // Custom adapter defined inside MainActivity
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

}