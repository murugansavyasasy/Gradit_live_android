package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.ResumeBuilder
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.applyPriorityColor
import com.vsca.vsnapvoicecollege.databinding.ActivityPlacementHomeBinding
import com.vsca.vsnapvoicecollege.databinding.ItemPlOptionBinding

/**
 * Placement hub, opened from dashboard menu 23. One place for the placement screens:
 * - Placement Training -> CareerTraining   (what menu 25 opens)
 * - Placement Events   -> PlacementEvent   (what menu 24 opens)
 * - Resume Builder     -> ResumeBuilder    (what menu 23 opened before)
 * - Placement Feedback -> no screen yet ("available soon")
 * Back returns here from each screen.
 */
class PlacementHome : BaseActivity<ActivityPlacementHomeBinding>() {

    override fun inflateBinding(): ActivityPlacementHomeBinding =
        ActivityPlacementHomeBinding.inflate(layoutInflater)

    override val layoutResourceId: Int
        get() = R.layout.activity_placement_home

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

        setupOption(
            binding.cardPlacementTraining, R.drawable.ic_pl_training,
            R.string.txt_pl_training, R.string.txt_pl_training_desc,
            iconColor = "#2F4C9A", circleColor = "#E8EEFB"
        ) {
            if (CommonUtil.PlacementCareer) open(CareerTraining::class.java)
        }
        setupOption(
            binding.cardPlacementEvents, R.drawable.ic_pl_events,
            R.string.txt_pl_events, R.string.txt_pl_events_desc,
            iconColor = "#E07A2E", circleColor = "#FDEEE3"
        ) {
            if (CommonUtil.PlacementEvent) open(PlacementEvent::class.java)
        }
        setupOption(
            binding.cardResumeBuilder, R.drawable.ic_pl_resume,
            R.string.txt_pl_resume, R.string.txt_pl_resume_desc,
            iconColor = "#2E8B57", circleColor = "#E3F4EA"
        ) {
            if (CommonUtil.PlacementTraining) open(ResumeBuilder::class.java)
        }
        setupOption(
            binding.cardPlacementFeedback, R.drawable.ic_pl_feedback,
            R.string.txt_pl_feedback, R.string.txt_pl_feedback_desc,
            iconColor = "#7A4CD8", circleColor = "#EDE7FB"
        ) {
            // No feedback screen in the app yet
            Toast.makeText(this, R.string.txt_pl_coming_soon, Toast.LENGTH_SHORT).show()
        }
    }

    /** Fills one option card: coloured icon circle, texts, arrow and click. */
    private fun setupOption(
        card: ItemPlOptionBinding, iconRes: Int, titleRes: Int, descRes: Int,
        iconColor: String, circleColor: String, onClick: () -> Unit
    ) {
        val tint = ColorStateList.valueOf(Color.parseColor(iconColor))
        card.imgPlOptionIcon.setImageResource(iconRes)
        card.imgPlOptionIcon.imageTintList = tint
        card.imgPlOptionIcon.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(circleColor))
        }
        card.imgPlOptionArrow.imageTintList = tint
        card.lblPlOptionTitle.setText(titleRes)
        card.lblPlOptionDesc.setText(descRes)
        card.root.setOnClickListener { onClick() }
    }

    private fun open(screen: Class<*>) {
        Log.d("PlacementHome", "Open ${screen.simpleName}")
        startActivity(Intent(this, screen))
    }
}
