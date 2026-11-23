package com.vsca.vsnapvoicecollege.Utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vsca.vsnapvoicecollege.Model.LocalMenuOrderItem
import com.vsca.vsnapvoicecollege.Model.MenuDetailsResponse

/**
 * Saves the dashboard menu order the user picks (Edit > drag and drop > Done), per user.
 *
 * - One list per login: college id + member id + priority. The same person can have several
 *   roles/colleges (LoginFromAppNew login_data), and each one has its own menus, so each
 *   one gets its own key.
 * - First time (nothing saved yet): the GetParentUserMenuWithReadWriteAccess list is saved
 *   as it comes, with local_menu_order_no 1, 2, 3 ... in API order.
 * - Every later API response is cross-checked by menu_id: a menu the API no longer sends is
 *   removed, and a new menu is appended at the end. Then the numbers are rewritten 1..n.
 * - Logout clears every user's saved order (see SharedPreference.clearShLogin).
 *
 * Kept in its own SharedPreferences file so logout can clear it in one call.
 */
object MenuOrderPreference {

    private const val TAG = "MenuOrder"
    private const val PREF_NAME = "MenuOrderPref"
    private const val KEY_PREFIX = "local_menu_order_list_"

    private val gson = Gson()

    /** e.g. "local_menu_order_list_1_49963_p1" for the logged-in college / member / role. */
    private fun userKey(): String =
        "$KEY_PREFIX${CommonUtil.CollegeId}_${CommonUtil.MemberId}_${CommonUtil.Priority}"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /** Saved list for the current user, sorted by local_menu_order_no; null if never saved. */
    fun getSavedOrder(context: Context): List<LocalMenuOrderItem>? {
        val json = prefs(context).getString(userKey(), null) ?: return null
        return try {
            val type = object : TypeToken<List<LocalMenuOrderItem>>() {}.type
            gson.fromJson<List<LocalMenuOrderItem>>(json, type)
                ?.sortedBy { it.local_menu_order_no }
        } catch (e: Exception) {
            Log.e(TAG, "Saved menu order could not be read, ignoring it: ${e.message}")
            null
        }
    }

    private fun save(context: Context, items: List<LocalMenuOrderItem>) {
        prefs(context).edit().putString(userKey(), gson.toJson(items)).apply()
        Log.d(TAG, "Saved for ${userKey()}: ${items.map { "${it.menu_id}=${it.local_menu_order_no}" }}")
    }

    /**
     * Cross-checks the saved list with the latest API menus (by menu_id) and saves the result:
     * missing menus removed, new menus appended, numbers rewritten 1..n.
     * An empty API list changes nothing (a failed call must not wipe the user's order).
     */
    fun syncWithApi(context: Context, apiMenus: List<MenuDetailsResponse>): List<LocalMenuOrderItem> {
        val saved = getSavedOrder(context)
        Log.d(TAG, "Sync start for ${userKey()}: api=${apiMenus.map { it.id }} saved=${saved?.map { it.menu_id }}")

        if (apiMenus.isEmpty()) {
            Log.d(TAG, "Sync skipped: API menu list is empty")
            return saved.orEmpty()
        }

        val apiById = apiMenus.associateBy { it.id }

        val result: List<LocalMenuOrderItem> = if (saved == null) {
            // First time for this user: save the API list in API order
            apiMenus.map { LocalMenuOrderItem(it.id, it.name, it.menu_slug) }
        } else {
            // Keep the user's order for menus the API still sends (refresh name/slug)
            val kept = saved.filter { apiById.containsKey(it.menu_id) }.map { item ->
                val api = apiById.getValue(item.menu_id)
                LocalMenuOrderItem(item.menu_id, api.name, api.menu_slug)
            }
            val removedIds = saved.map { it.menu_id }.filterNot { apiById.containsKey(it) }
            if (removedIds.isNotEmpty()) Log.d(TAG, "Removed (no longer in API): $removedIds")

            // Menus that are new in the API go to the end, in API order
            val savedIds = saved.map { it.menu_id }.toSet()
            val added = apiMenus.filterNot { savedIds.contains(it.id) }
                .map { LocalMenuOrderItem(it.id, it.name, it.menu_slug) }
            if (added.isNotEmpty()) Log.d(TAG, "Appended (new in API): ${added.map { it.menu_id }}")

            kept + added
        }

        result.forEachIndexed { index, item -> item.local_menu_order_no = index + 1 }
        save(context, result)
        return result
    }

    /** API menus sorted by the user's local_menu_order_no (syncs first). */
    fun getOrderedMenus(context: Context, apiMenus: List<MenuDetailsResponse>): List<MenuDetailsResponse> {
        if (apiMenus.isEmpty()) return apiMenus
        val orderById = syncWithApi(context, apiMenus)
            .associate { it.menu_id to it.local_menu_order_no }
        val ordered = apiMenus.sortedBy { orderById[it.id] ?: Int.MAX_VALUE }
        Log.d(TAG, "Menus shown in order: ${ordered.map { it.id }}")
        return ordered
    }

    /** Saves a new order (Done after drag and drop): position in the list = local_menu_order_no. */
    fun saveMenuOrder(context: Context, orderedMenus: List<MenuDetailsResponse>) {
        Log.d(TAG, "Save new order (before): ${getSavedOrder(context)?.map { it.menu_id }}")
        val items = orderedMenus.mapIndexed { index, menu ->
            LocalMenuOrderItem(menu.id, menu.name, menu.menu_slug, index + 1)
        }
        save(context, items)
    }

    /** Logout: removes the saved menu order of every user on this device. */
    fun clearAll(context: Context) {
        prefs(context).edit().clear().apply()
        Log.d(TAG, "Cleared saved menu order for all users (logout)")
    }
}
