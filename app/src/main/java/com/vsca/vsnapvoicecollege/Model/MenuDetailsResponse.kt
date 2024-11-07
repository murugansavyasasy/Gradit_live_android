package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MenuDetailsResponse {

    @SerializedName("menu_id")
    @Expose
    var id = 0

    @SerializedName("menu_name")
    @Expose
    var name: String? = null


    @SerializedName("menu_slug")
    @Expose
    var menu_slug: String? = null

    @SerializedName("is_read_enabled")
    @Expose
    var is_read_enabled: Int? = null


    @SerializedName("is_write_enabled")
    @Expose
    var is_write_enabled: Int? = null


    @SerializedName("order_id")
    @Expose
    var order_id: Int? = null

    @SerializedName("parent_id")
    @Expose
    var parent_id: Int? = null

    constructor(
        ID: Int?,
        Name: String?,
        menuslug: String,
        is_read_enabled: Int,
        is_write_enabled: Int,
        order_id: Int,
        parentId: Int
    ) {
        this.id = ID!!
        this.name = Name
        this.menu_slug = menuslug
        this.is_read_enabled = is_read_enabled
        this.is_write_enabled = is_write_enabled
        this.order_id = order_id
        this.parent_id = parentId
    }
}