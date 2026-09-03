package com.vsca.vsnapvoicecollege.Utils


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/**
 * AnimatedGradientView
 * Renders a dynamic, visibly flowing ambient liquid mesh/aurora gradient animation.
 * Optimized with lively harmonic velocities and rich contrast so the fluid motion
 * is immediately noticeable and captivating within a 1-2 second view.
 */
class AnimatedGradientView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val orbPaint1 = Paint(Paint.ANTI_ALIAS_FLAG)
    private val orbPaint2 = Paint(Paint.ANTI_ALIAS_FLAG)
    private val orbPaint3 = Paint(Paint.ANTI_ALIAS_FLAG)
    private val orbPaint4 = Paint(Paint.ANTI_ALIAS_FLAG)

    private var baseShader: LinearGradient? = null
    private var isRunning = false
    private var startTime: Long = 0L

    init {
        startTime = SystemClock.uptimeMillis()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            baseShader = LinearGradient(
                0f, 0f,
                0f, h.toFloat(),
                intArrayOf(
                    Color.parseColor("#F2F7FE"),
                    Color.parseColor("#E0EEFE"),
                    Color.parseColor("#CFE4FE")
                ),
                floatArrayOf(0f, 0.40f, 1f),
                Shader.TileMode.CLAMP
            )
            basePaint.shader = baseShader
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            start()
        } else {
            stop()
        }
    }

    private fun start() {
        if (!isRunning) {
            isRunning = true
            postInvalidateOnAnimation()
        }
    }

    private fun stop() {
        isRunning = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        // 1. Draw crisp luminous base foundation
        canvas.drawRect(0f, 0f, w, h, basePaint)

        // Elapsed time in seconds with lively speed scaling (visible fluid motion)
        val t = (SystemClock.uptimeMillis() - startTime) / 1000f

        // 2. Dynamic Orb 1 (Right-Edge Signature Bloom): Vibrant azure glow waving prominently
        val x1 = w * (0.90f + 0.18f * sin(t * 1.35f).toFloat())
        val y1 = h * (0.52f + 0.16f * cos(t * 1.15f).toFloat())
        val r1 = w * (0.90f + 0.12f * sin(t * 1.45f).toFloat())
        orbPaint1.shader = RadialGradient(
            x1, y1, r1,
            intArrayOf(
                Color.argb(190, 88, 168, 254),  // #58A8FE vibrant rich blue
                Color.argb(100, 138, 196, 254), // #8AC4FE
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.50f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(x1, y1, r1, orbPaint1)

        // 3. Dynamic Orb 2 (Center-Left Swirl): Radiant periwinkle-blue pulsating rhythm
        val x2 = w * (0.35f + 0.22f * cos(t * 1.05f + 1.2f).toFloat())
        val y2 = h * (0.38f + 0.18f * sin(t * 1.25f + 0.6f).toFloat())
        val r2 = w * (0.80f + 0.10f * cos(t * 1.30f).toFloat())
        orbPaint2.shader = RadialGradient(
            x2, y2, r2,
            intArrayOf(
                Color.argb(160, 114, 138, 254), // #728AFE rich soft periwinkle
                Color.argb(80, 168, 198, 254),
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.55f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(x2, y2, r2, orbPaint2)

        // 4. Dynamic Orb 3 (Lower-Left Ocean Ambient): Flowing cyan-blue wave near bottom
        val x3 = w * (0.20f + 0.16f * sin(t * 0.95f + 2.0f).toFloat())
        val y3 = h * (0.75f + 0.12f * cos(t * 1.10f + 1.5f).toFloat())
        val r3 = w * (0.75f + 0.08f * sin(t * 1.20f).toFloat())
        orbPaint3.shader = RadialGradient(
            x3, y3, r3,
            intArrayOf(
                Color.argb(150, 130, 204, 254), // #82CCFE luminous cyan
                Color.argb(60, 190, 225, 254),
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.60f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(x3, y3, r3, orbPaint3)

        // 5. Dynamic Orb 4 (Top Aurora Flare): Radiant shimmer across upper header
        val x4 = w * (0.65f + 0.25f * cos(t * 0.85f + 2.8f).toFloat())
        val y4 = h * (0.16f + 0.10f * sin(t * 1.00f + 2.2f).toFloat())
        val r4 = w * (0.65f + 0.08f * cos(t * 1.15f).toFloat())
        orbPaint4.shader = RadialGradient(
            x4, y4, r4,
            intArrayOf(
                Color.argb(140, 158, 218, 254), // #9EDAFE
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(x4, y4, r4, orbPaint4)

        // Request next frame for continuous 60fps dynamic fluid flow
        if (isRunning && isAttachedToWindow) {
            postInvalidateOnAnimation()
        }
    }
}
