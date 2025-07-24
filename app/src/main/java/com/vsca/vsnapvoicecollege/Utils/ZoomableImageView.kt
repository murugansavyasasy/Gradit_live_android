package com.vsca.vsnapvoicecollege.Utils

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max
import kotlin.math.min

class ZoomableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs), ScaleGestureDetector.OnScaleGestureListener {

    private var scaleFactor = 1.0f
    private val scaleDetector = ScaleGestureDetector(context, this)
    private val imageMatrixCustom = Matrix()
    private var lastScaleFactor = 1.0f

    init {
        scaleType = ScaleType.MATRIX
        imageMatrix = imageMatrixCustom
        isClickable = true
        isFocusable = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val factor = detector.scaleFactor
        scaleFactor *= factor
        scaleFactor = max(1.0f, min(scaleFactor, 5.0f)) // Zoom range 1x - 5x

        imageMatrixCustom.postScale(
            factor,
            factor,
            detector.focusX,
            detector.focusY
        )
        imageMatrix = imageMatrixCustom
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean = true
    override fun onScaleEnd(detector: ScaleGestureDetector) {}
}


