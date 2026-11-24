package com.vsca.vsnapvoicecollege.Activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.PlacementFeedbackAdapter
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.applyPriorityColor
import com.vsca.vsnapvoicecollege.Utils.EdgeToEdgeUtils
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityPlacementFeedbackBinding

/**
 * Placement Feedback (Placement hub > Placement Feedback).
 * - List: GET student/feedback/list -> cards with topic, posted date, message and the
 *   officer's last reply ("Replied" pill).
 * - "Create Feedback" opens the "We're listening" sheet: Topic + Your feedback (both
 *   required) -> POST student/feedback/add. Success: sheet closes, list reloads.
 *   Failure: message shown, sheet stays open with the text kept.
 */
class PlacementFeedback : BaseActivity<ActivityPlacementFeedbackBinding>() {

    override var appViewModel: App? = null

    override fun inflateBinding(): ActivityPlacementFeedbackBinding =
        ActivityPlacementFeedbackBinding.inflate(layoutInflater)

    override val layoutResourceId: Int
        get() = R.layout.activity_placement_feedback

    private val adapter = PlacementFeedbackAdapter()
    private var feedbackSheet: Dialog? = null

    /** True while an add started here waits for its answer (ignores re-delivered values). */
    private var isAddPending = false

    private val roleColor by lazy { EdgeToEdgeUtils.getPriorityColor(this, CommonUtil.Priority) }

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this, noActionBar = true)
        super.onCreate(savedInstanceState)
        binding = ActivityPlacementFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdgeAuto(
            rootView = binding.OverallLayout,
            statusBarBgView = binding.statusBarBackground,
            priority = CommonUtil.Priority
        )
        if (supportActionBar != null) {
            ActionBarMethod(this, true)
            fixActionBarOverlap(binding.LayoutHeader)
        }
        binding.LayoutHeader.applyPriorityColor(this, CommonUtil.Priority)
        binding.imgheaderBack.setOnClickListener { onBackPressed() }

        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        binding.rcyFeedback.layoutManager = LinearLayoutManager(this)
        binding.rcyFeedback.adapter = adapter

        // "+ Create Feedback" pill in the role colour
        binding.btnCreateFeedback.background = rounded(roleColor, 26f)
        val plus = ContextCompat.getDrawable(this, R.drawable.ic_pf_plus)?.mutate()?.apply {
            setTint(Color.WHITE)
            setBounds(0, 0, dp(18), dp(18))
        }
        binding.btnCreateFeedback.setCompoundDrawablesRelative(plus, null, null, null)
        binding.btnCreateFeedback.setOnClickListener { showFeedbackSheet() }

        observeApis()
        loadFeedback()
    }

    private fun loadFeedback() {
        appViewModel!!.requestPlacementFeedbackList(this)
    }

    private fun observeApis() {
        appViewModel!!.PlacementFeedbackList!!.observe(this) { response ->
            val list = response?.data.orEmpty()
            if (response != null && response.status) {
                adapter.submit(list)
                binding.lblFeedbackCount.text =
                    if (list.size == 1) getString(R.string.txt_pf_entry)
                    else getString(R.string.txt_pf_entries, list.size)
                binding.lblFeedbackEmpty.setText(R.string.txt_pf_empty)
                binding.lblFeedbackEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                binding.rcyFeedback.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
            } else {
                // Error: show the reason in place of the list
                adapter.submit(emptyList())
                binding.lblFeedbackCount.text = ""
                binding.rcyFeedback.visibility = View.GONE
                binding.lblFeedbackEmpty.visibility = View.VISIBLE
                binding.lblFeedbackEmpty.text =
                    response?.message?.takeIf { it.isNotBlank() } ?: CommonUtil.Something_went_wrong
            }
        }

        appViewModel!!.PlacementFeedbackAdd!!.observe(this) { response ->
            if (!isAddPending) return@observe
            isAddPending = false
            if (response != null && response.status) {
                Toast.makeText(
                    this,
                    response.message?.takeIf { it.isNotBlank() } ?: getString(R.string.txt_pf_sent),
                    Toast.LENGTH_SHORT
                ).show()
                feedbackSheet?.dismiss()
                loadFeedback()   // new entry appears with the server's data
            } else {
                CommonUtil.ApiAlert(
                    this, response?.message?.takeIf { it.isNotBlank() } ?: CommonUtil.Something_went_wrong
                )
            }
        }
    }

    /** "We're listening" sheet: Topic + Your feedback -> Submit Feedback. */
    private fun showFeedbackSheet() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_placement_feedback)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        val edtTopic = dialog.findViewById<EditText>(R.id.edtFeedbackTopic)
        val edtMessage = dialog.findViewById<EditText>(R.id.edtFeedbackMessage)
        val btnSubmit = dialog.findViewById<TextView>(R.id.btnSubmitFeedback)
        btnSubmit.background = rounded(roleColor, 12f)

        // Long feedback scrolls inside its box (max 6 lines). While the finger is on the box
        // and it can still scroll, keep the sheet's own ScrollView from taking the drag.
        edtMessage.setOnTouchListener { view, event ->
            if (view.canScrollVertically(1) || view.canScrollVertically(-1)) {
                view.parent.requestDisallowInterceptTouchEvent(
                    event.actionMasked != MotionEvent.ACTION_UP &&
                            event.actionMasked != MotionEvent.ACTION_CANCEL
                )
            }
            false   // let the EditText handle the touch (cursor, selection, scrolling)
        }

        dialog.findViewById<View>(R.id.imgFeedbackClose).setOnClickListener { dialog.dismiss() }
        btnSubmit.setOnClickListener {
            val topic = edtTopic.text.toString().trim()
            val message = edtMessage.text.toString().trim()
            when {
                topic.isEmpty() -> {
                    edtTopic.error = getString(R.string.txt_pf_topic_required)
                    edtTopic.requestFocus()
                }
                message.isEmpty() -> {
                    edtMessage.error = getString(R.string.txt_pf_message_required)
                    edtMessage.requestFocus()
                }
                !isAddPending -> sendFeedback(topic, message)
            }
        }

        dialog.setOnDismissListener { feedbackSheet = null }
        feedbackSheet = dialog
        dialog.show()
    }

    /** POST student/feedback/add. collegeId / memberId identify the student, as for placement events. */
    private fun sendFeedback(topic: String, message: String) {
        val body = JsonObject().apply {
            addProperty("topic", topic)
            addProperty("message", message)
            addProperty("studentId", CommonUtil.MemberId)
        }
        Log.d("PlacementFeedback", "Send: $body")
        isAddPending = true
        appViewModel!!.requestAddPlacementFeedback(body, this)
    }

    private fun rounded(color: Int, radiusDp: Float) = GradientDrawable().apply {
        cornerRadius = radiusDp * resources.displayMetrics.density
        setColor(color)
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        feedbackSheet?.dismiss()
        super.onDestroy()
    }
}
