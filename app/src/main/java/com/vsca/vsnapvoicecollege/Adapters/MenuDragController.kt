package com.vsca.vsnapvoicecollege.Adapters

import android.content.ClipData
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.DragEvent
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.vsca.vsnapvoicecollege.Model.MenuDetailsResponse

/**
 * Drag and drop for the dashboard menu grid while editing the menu order, with a phone
 * home-screen feel (no shaking):
 *
 * - Long press a menu -> it lifts (bigger, with a shadow, small vibration) and follows the
 *   finger. Its place in the grid stays as an empty dashed slot.
 * - Move over another menu -> the menus slide aside right away and the empty slot moves
 *   there (live, animated). Works on the same page and across pages.
 * - Hold near the left / right edge -> the grid goes to the previous / next page.
 * - Over an empty part of a page -> the menu goes to the end of that page.
 * - Release -> the menu drops into the empty slot.
 *
 * The order itself is changed by [Callbacks] (DashboardParent); this class only reads the
 * drag events.
 */
class MenuDragController(
    private val viewPager: ViewPager2,
    private val callbacks: Callbacks
) {

    interface Callbacks {
        /** Dragged menu is over [targetMenuId]: take that menu's place. */
        fun onHoverMenu(draggedMenuId: Int, targetMenuId: Int)

        /** Dragged menu is over an empty part of page [pageIndex]: go to its end. */
        fun onHoverPageEnd(draggedMenuId: Int, pageIndex: Int)

        /** Finger released (or drag cancelled): show the dragged menu again. */
        fun onDragFinished(draggedMenuId: Int)
    }

    /** Menu being dragged; its tile is drawn as an empty slot. Null when not dragging. */
    var draggingMenuId: Int? = null
        private set

    private val handler = Handler(Looper.getMainLooper())
    private val density = viewPager.resources.displayMetrics.density
    private val edgePx = (EDGE_DP * density).toInt()

    /** -1 = waiting to go to the previous page, 1 = next page, 0 = not at an edge. */
    private var edgeDirection = 0

    private val switchPage = object : Runnable {
        override fun run() {
            val count = viewPager.adapter?.itemCount ?: return
            val target = viewPager.currentItem + edgeDirection
            if (edgeDirection == 0 || target !in 0 until count) return
            Log.d(TAG, "Edge hold: page ${viewPager.currentItem} -> $target")
            viewPager.setCurrentItem(target, true)
            // Still at the edge: keep moving one page at a time
            handler.postDelayed(this, PAGE_REPEAT_MS)
        }
    }

    /** Long press on a menu: lift it and start dragging. */
    fun startDrag(tile: View, menu: MenuDetailsResponse): Boolean {
        Log.d(TAG, "Drag start: menu ${menu.id} (${menu.name})")
        val clip = ClipData.newPlainText(CLIP_LABEL, menu.id.toString())
        val started = tile.startDragAndDrop(clip, LiftedShadowBuilder(tile), menu.id, 0)
        if (started) {
            draggingMenuId = menu.id
            tile.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            showAsEmptySlot(tile, true)
        }
        return started
    }

    /** Listener for one menu cell: hovering it moves the dragged menu into its place. */
    fun tileDragListener(tile: View, menuId: Int) = View.OnDragListener { view, event ->
        val draggedId = event.localState as? Int ?: return@OnDragListener false
        when (event.action) {
            DragEvent.ACTION_DRAG_ENTERED -> {
                if (menuId != draggedId) callbacks.onHoverMenu(draggedId, menuId)
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                checkEdge(view, event.x)
                // Finger resting on a menu after the slide animation: move again
                if (menuId != draggedId) callbacks.onHoverMenu(draggedId, menuId)
            }
            DragEvent.ACTION_DROP -> stopEdge()
            DragEvent.ACTION_DRAG_ENDED -> finish(draggedId)
        }
        true
    }

    /**
     * Listener for a whole page grid (gets events only where there is no menu under the
     * finger). Hovering the empty area after the page's last menu sends the menu there.
     */
    fun pageDragListener(pageIndex: Int) = View.OnDragListener { view, event ->
        val draggedId = event.localState as? Int ?: return@OnDragListener false
        when (event.action) {
            DragEvent.ACTION_DRAG_LOCATION -> {
                checkEdge(view, event.x)
                if (isAfterLastMenu(view, event.x, event.y)) callbacks.onHoverPageEnd(draggedId, pageIndex)
            }
            DragEvent.ACTION_DROP -> stopEdge()
            DragEvent.ACTION_DRAG_ENDED -> finish(draggedId)
        }
        true
    }

    /** True below the last menu's row, or on its row to the right of it (not in small gaps). */
    private fun isAfterLastMenu(grid: View, x: Float, y: Float): Boolean {
        val group = grid as? ViewGroup ?: return false
        if (group.childCount == 0) return true
        var last = group.getChildAt(0)
        for (i in 1 until group.childCount) {
            val child = group.getChildAt(i)
            if (child.top > last.top || (child.top == last.top && child.left > last.left)) last = child
        }
        return y > last.bottom || (y >= last.top && x > last.right)
    }

    /** Every listening view gets DRAG_ENDED; only the first one does the work. */
    private fun finish(draggedId: Int) {
        stopEdge()
        if (draggingMenuId == null) return
        draggingMenuId = null
        Log.d(TAG, "Drag end: menu $draggedId dropped")
        viewPager.post { callbacks.onDragFinished(draggedId) }
    }

    /** x is relative to [view]; turns it into x inside the ViewPager and checks the edges. */
    private fun checkEdge(view: View, x: Float) {
        val viewPos = IntArray(2).also { view.getLocationOnScreen(it) }
        val pagerPos = IntArray(2).also { viewPager.getLocationOnScreen(it) }
        val xInPager = viewPos[0] + x - pagerPos[0]
        val direction = when {
            xInPager < edgePx -> -1
            xInPager > viewPager.width - edgePx -> 1
            else -> 0
        }
        if (direction == edgeDirection) return
        handler.removeCallbacks(switchPage)
        edgeDirection = direction
        if (direction != 0) handler.postDelayed(switchPage, PAGE_SWITCH_DELAY_MS)
    }

    private fun stopEdge() {
        edgeDirection = 0
        handler.removeCallbacks(switchPage)
    }

    /** Edit mode ended: stop any pending page switch. */
    fun release() {
        stopEdge()
        draggingMenuId = null
    }

    /** The menu's own tile while it is dragged: icon and name hidden, dashed slot kept. */
    fun showAsEmptySlot(tile: View, empty: Boolean) {
        val group = tile as? ViewGroup ?: return
        for (i in 0 until group.childCount) group.getChildAt(i).alpha = if (empty) 0f else 1f
    }

    /** Drag shadow: the menu a bit bigger on a white rounded card with a soft shadow. */
    private inner class LiftedShadowBuilder(private val tile: View) : View.DragShadowBuilder(tile) {
        private val pad = (SHADOW_PAD_DP * density)
        private val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            setShadowLayer(6 * density, 0f, 3 * density, Color.argb(70, 0, 0, 0))
        }

        override fun onProvideShadowMetrics(outShadowSize: Point, outShadowTouchPoint: Point) {
            val w = (tile.width * LIFT_SCALE + pad * 2).toInt()
            val h = (tile.height * LIFT_SCALE + pad * 2).toInt()
            outShadowSize.set(w, h)
            outShadowTouchPoint.set(w / 2, h / 2)
        }

        override fun onDrawShadow(canvas: Canvas) {
            val w = tile.width * LIFT_SCALE
            val h = tile.height * LIFT_SCALE
            canvas.drawRoundRect(RectF(pad, pad, pad + w, pad + h), 14 * density, 14 * density, cardPaint)
            canvas.save()
            canvas.translate(pad, pad)
            canvas.scale(LIFT_SCALE, LIFT_SCALE)
            // Draw only icon + name on the card (not the dashed edit outline)
            val outline = tile.background
            tile.background = null
            tile.draw(canvas)
            tile.background = outline
            canvas.restore()
        }
    }

    companion object {
        private const val TAG = "MenuOrderEdit"
        private const val CLIP_LABEL = "menu_id"
        private const val EDGE_DP = 28
        private const val PAGE_SWITCH_DELAY_MS = 600L
        private const val PAGE_REPEAT_MS = 1000L
        private const val LIFT_SCALE = 1.12f
        private const val SHADOW_PAD_DP = 10f
    }
}
