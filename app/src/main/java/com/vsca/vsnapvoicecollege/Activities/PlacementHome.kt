package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.ResumeBuilder
import com.vsca.vsnapvoicecollege.Model.PlacementMenuItem
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.applyPriorityColor
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityPlacementHomeBinding
import com.vsca.vsnapvoicecollege.databinding.ItemPlOptionBinding

/**
 * Placement hub, opened from dashboard menu 23.
 * Options come from GET student/placement-menu (name, description, sortOrder) and are shown
 * as cards, 2 per row. The icon, colours and the screen each card opens are fixed in the app
 * per menu id (see [styleFor]):
 *   1 Placement Training -> CareerTraining
 *   2 Placement Events   -> PlacementEvent
 *   3 Resume Builder     -> ResumeBuilder
 *   4 Placement Feedback -> PlacementFeedback
 * An unknown id still shows (default icon) and tells the student it is not available yet.
 * Empty list or API error -> message with Retry.
 */
class PlacementHome : BaseActivity<ActivityPlacementHomeBinding>() {

    override var appViewModel: App? = null

    override fun inflateBinding(): ActivityPlacementHomeBinding =
        ActivityPlacementHomeBinding.inflate(layoutInflater)

    override val layoutResourceId: Int
        get() = R.layout.activity_placement_home

    /** Look and destination of one option (by menu id). [open] null = no screen for it yet. */
    private class OptionStyle(
        val iconRes: Int,
        val iconColor: String,
        val circleColor: String,
        val open: (() -> Unit)?
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this, noActionBar = true)
        super.onCreate(savedInstanceState)
        binding = ActivityPlacementHomeBinding.inflate(layoutInflater)
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

        binding.btnPlRetry.setOnClickListener { loadMenu() }

        appViewModel!!.PlacementMenu!!.observe(this) { response ->
            if (response == null || !response.status) {
                showMessage(response?.message?.takeIf { it.isNotBlank() } ?: CommonUtil.Something_went_wrong)
                return@observe
            }
            val options = response.data.orEmpty()
                .filter { it.id != null }
                .sortedBy { it.sortOrder ?: Int.MAX_VALUE }
            if (options.isEmpty()) {
                // Success but nothing to show (the API message is "retrieved", so use our own text)
                showMessage(getString(R.string.txt_pl_menu_empty))
            } else {
                showOptions(options)
            }
        }

        loadMenu()
    }

    private fun loadMenu() {
        binding.layoutPlMessage.visibility = View.GONE
        appViewModel!!.requestPlacementMenu(this)
    }

    /** Cards 2 per row, in sortOrder. */
    private fun showOptions(options: List<PlacementMenuItem>) {
        Log.d("PlacementHome", "Options: ${options.map { it.id }}")
        binding.layoutPlMessage.visibility = View.GONE
        val container = binding.layoutPlOptions
        container.removeAllViews()
        container.visibility = View.VISIBLE

        options.chunked(2).forEach { pair ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                isBaselineAligned = false
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            pair.forEach { item ->
                val card = ItemPlOptionBinding.inflate(layoutInflater, row, false)
                bindOption(card, item)
                row.addView(card.root)
            }
            // Odd count: keep the last card at half width
            if (pair.size == 1) {
                row.addView(View(this), LinearLayout.LayoutParams(0, 0, 1f))
            }
            container.addView(row)
        }
    }

    private fun showMessage(message: String) {
        Log.d("PlacementHome", "Message: $message")
        binding.layoutPlOptions.removeAllViews()
        binding.layoutPlOptions.visibility = View.GONE
        binding.lblPlMessage.text = message
        binding.layoutPlMessage.visibility = View.VISIBLE
    }

    /** API name/description on the card; icon, colours and click by menu id. */
    private fun bindOption(card: ItemPlOptionBinding, item: PlacementMenuItem) {
        val style = styleFor(item.id)
        val tint = ColorStateList.valueOf(Color.parseColor(style.iconColor))
        card.imgPlOptionIcon.setImageResource(style.iconRes)
        card.imgPlOptionIcon.imageTintList = tint
        card.imgPlOptionIcon.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(style.circleColor))
        }
        card.imgPlOptionArrow.imageTintList = tint
        card.lblPlOptionTitle.text = item.name.orEmpty()
        card.lblPlOptionDesc.text = item.description.orEmpty()
        card.root.setOnClickListener {
            val open = style.open
            if (open != null) open()
            else Toast.makeText(this, R.string.txt_pl_option_unavailable, Toast.LENGTH_SHORT).show()
        }
    }

    private fun styleFor(id: Int?): OptionStyle = when (id) {
        1 -> OptionStyle(R.drawable.ic_pl_training, "#2F4C9A", "#E8EEFB") {
            if (CommonUtil.PlacementCareer) open(CareerTraining::class.java)
        }
        2 -> OptionStyle(R.drawable.ic_pl_events, "#E07A2E", "#FDEEE3") {
            if (CommonUtil.PlacementEvent) open(PlacementEvent::class.java)
        }
        3 -> OptionStyle(R.drawable.ic_pl_resume, "#2E8B57", "#E3F4EA") {
            if (CommonUtil.PlacementTraining) open(ResumeBuilder::class.java)
        }
        4 -> OptionStyle(R.drawable.ic_pl_feedback, "#7A4CD8", "#EDE7FB") {
            open(PlacementFeedback::class.java)
        }
        // New option the app does not know yet: default look, no screen
        else -> OptionStyle(R.drawable.ic_cr_brief, "#475569", "#EEF0F4", null)
    }

    private fun open(screen: Class<*>) {
        Log.d("PlacementHome", "Open ${screen.simpleName}")
        startActivity(Intent(this, screen))
    }
}
