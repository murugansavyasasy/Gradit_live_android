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

    // From GetParentUserMenuWithReadWriteAccess, available for every menu (currently used by Events).
    // Nullable on purpose: the key may be missing or null in the response, and Gson does not
    // run this class's initializers (no no-arg constructor). Read it via isApprovalRequired().
    @SerializedName("approval_required")
    @Expose
    var approval_required: Boolean? = false

    /** Null-safe: true only when the API sent approval_required = true. */
    fun isApprovalRequired(): Boolean = approval_required == true

    // Venue requests access (currently used by Events > Request List):
    // isapprove = can review/approve venue requests, isrequest = can see own requests (My Requests)
    @SerializedName("isapprove")
    @Expose
    var isapprove: Boolean? = false

    @SerializedName("isrequest")
    @Expose
    var isrequest: Boolean? = false

    /** Null-safe: true only when the API sent isapprove = true. */
    fun isApprove(): Boolean = isapprove == true

    /** Null-safe: true only when the API sent isrequest = true. */
    fun isRequest(): Boolean = isrequest == true

    constructor(
        ID: Int?,
        Name: String?,
        menuslug: String,
        is_read_enabled: Int,
        is_write_enabled: Int,
        order_id: Int,
        parentId: Int,
        approvalRequired: Boolean? = false,
        isApprove: Boolean? = false,
        isRequest: Boolean? = false
    ) {
        this.isapprove = isApprove
        this.isrequest = isRequest
        this.id = ID!!
        this.name = Name
        this.menu_slug = menuslug
        this.is_read_enabled = is_read_enabled
        this.is_write_enabled = is_write_enabled
        this.order_id = order_id
        this.parent_id = parentId
        this.approval_required = approvalRequired
    }
}