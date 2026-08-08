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
    private val onMenuClick: ((MenuDetailsResponse) -> Unit)?
) : RecyclerView.Adapter<DashboardMenuPagerAdapter.PageViewHolder>() {

    /**
     * Split the full menu list into pages of 12 items (4 columns × 3 rows).
     * If there are ≤12 items, this produces a single page and ViewPager2
     * will not scroll because there is no next page to snap to.
     */
    private val pages: List<List<MenuDetailsResponse>> = menuList.chunked(PAGE_SIZE)

    /**
     * Shared view pool is the critical performance optimization here.
     * Every inner page uses the same item layout (R.layout.home_menu_list_design),
     * so holders can be recycled across pages instead of inflating fresh ones.
     */
    private val sharedRecycledViewPool = RecyclerView.RecycledViewPool()

    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerGrid: RecyclerView = itemView.findViewById(R.id.recyclerPageGrid)

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

        fun bind(pageItems: List<MenuDetailsResponse>) {
            // Reuse your existing DashboardChild for the actual menu binding & click logic.
            recyclerGrid.adapter = DashboardChild(
                context = context,
                type = "Menu",
                menuList = pageItems,
                onMenuClick = onMenuClick
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_dashboard_menu_page, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    companion object {
        private const val COLUMNS = 4
        private const val PAGE_SIZE = 12 // 4 columns × 3 rows
    }
}