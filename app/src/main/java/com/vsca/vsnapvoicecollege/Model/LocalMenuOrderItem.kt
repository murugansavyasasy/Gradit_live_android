package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * One dashboard menu in the user's locally saved menu order (stored in SharedPreferences,
 * never sent to any API). Built from GetParentUserMenuWithReadWriteAccess, plus
 * local_menu_order_no: the position the user chose (1, 2, 3 ...).
 *
 * Only the order is taken from here. Read/write access always comes from the latest API
 * response, so a changed permission is never hidden by an old saved list.
 */
class LocalMenuOrderItem(
    @SerializedName("menu_id")
    @Expose
    var menu_id: Int = 0,

    @SerializedName("menu_name")
    @Expose
    var menu_name: String? = null,

    @SerializedName("menu_slug")
    @Expose
    var menu_slug: String? = null,

    @SerializedName("local_menu_order_no")
    @Expose
    var local_menu_order_no: Int = 0
)
