package com.vsca.vsnapvoicecollege.Adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.MenuDetailsResponse
import com.vsca.vsnapvoicecollege.R

class DashboardMenuPagerAdapter(
    private val context: Context,
    menuList: List<MenuDetailsResponse>,
    private val onMenuClick: ((MenuDetailsResponse) -> Unit)?,
    // Menu order edit mode: non-null while editing (drag and drop on), null = normal grid
    private val dragController: MenuDragController? = null
) : RecyclerView.Adapter<DashboardMenuPagerAdapter.PageViewHolder>() {

    /**
     * Split the full menu list into pages of 12 items (4 columns × 3 rows).
     * If there are ≤12 items, this produces a single page and ViewPager2
     * will not scroll because there is no next page to snap to.
     */
    private var pages: List<List<MenuDetailsResponse>> = menuList.chunked(PAGE_SIZE)

    /** Pages currently on screen / kept alive, so a move can be animated inside each one. */
    private val boundPages = mutableSetOf<PageViewHolder>()

    /**
     * Edit mode, while dragging: apply the new order to every page with a slide animation
     * (a menu that crosses pages leaves one grid and enters the other). Keeps the current page.
     */
    fun updateMenus(menuList: List<MenuDetailsResponse>) {
        val newPages = menuList.chunked(PAGE_SIZE)
        if (newPages.size != pages.size) {
            pages = newPages
            notifyDataSetChanged()
            return
        }
        pages = newPages
        boundPages.forEach { holder ->
            val position = holder.bindingAdapterPosition
            if (position in pages.indices) holder.childAdapter?.updateMenuListAnimated(pages[position])
        }
    }

    /** Edit mode: redraw the tiles of every page (e.g. show the dragged menu again). */
    fun refreshTiles() {
        boundPages.forEach { it.childAdapter?.notifyDataSetChanged() }
    }

    /**
     * Shared view pool is the critical performance optimization here.
     * Every inner page uses the same item layout (R.layout.home_menu_list_design),
     * so holders can be recycled across pages instead of inflating fresh ones.
     */
    private val sharedRecycledViewPool = RecyclerView.RecycledViewPool()

    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerGrid: RecyclerView = itemView.findViewById(R.id.recyclerPageGrid)
        var childAdapter: DashboardChild? = null

        init {
            // 4 columns, vertical orientation → items flow left-to-right, top-to-bottom.
            recyclerGrid.layoutManager = GridLayoutManager(
                context, COLUMNS, GridLayoutManager.VERTICAL, false
            )

            // Disable nested scrolling so the outer ViewPager2 owns all horizontal touch events.
            recyclerGrid.isNestedScrollingEnabled = false

            // Optimization: each inner grid has a fixed cell count (≤12) and fixed column count.
            recyclerGrid.setHasFixedSize(true)

            // Share holders across all page grids.
            recyclerGrid.setRecycledViewPool(sharedRecycledViewPool)

            // Optional: uncomment if you added GridSpacingItemDecoration above.
            // recyclerGrid.addItemDecoration(GridSpacingItemDecoration(COLUMNS, 8, true))
        }

        fun bind(pageItems: List<MenuDetailsResponse>, pageIndex: Int) {
            // Reuse your existing DashboardChild for the actual menu binding & click logic.
            val adapter = DashboardChild(
                context = context,
                type = "Menu",
                menuList = pageItems,
                onMenuClick = onMenuClick,
                menuDragController = dragController
            )
            childAdapter = adapter
            recyclerGrid.adapter = adapter
            // Short slide when menus make room for the dragged one
            recyclerGrid.itemAnimator?.moveDuration = SLIDE_DURATION_MS
            // Edit mode: hovering an empty spot of this page sends the menu to its end
            recyclerGrid.setOnDragListener(dragController?.pageDragListener(pageIndex))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_dashboard_menu_page, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position], position)
        boundPages.add(holder)
    }

    override fun onViewRecycled(holder: PageViewHolder) {
        boundPages.remove(holder)
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = pages.size

    companion object {
        private const val COLUMNS = 4
        const val PAGE_SIZE = 12 // 4 columns × 3 rows
        private const val SLIDE_DURATION_MS = 180L
    }
}
