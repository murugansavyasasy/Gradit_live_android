package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetCircularListDetails {
    @SerializedName("headerid")
    @Expose
    var headerid: String? = null

    @SerializedName("detailsid")
    @Expose
    var detailsid: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("details_id")
    @Expose
    var details_id: String? = null

    @SerializedName("file_path")
    @Expose
    var file_path: String? = null

    @SerializedName("createdondate")
    @Expose
    var createdondate: String? = null

    @SerializedName("createdontime")
    @Expose
    var createdontime: String? = null

    @SerializedName("sentbyname")
    @Expose
    var sentbyname: String? = null

    @SerializedName("filetype")
    @Expose
    var filetype: String? = null

    @SerializedName("userfilename")
    @Expose
    var userfilename: String? = null

    @SerializedName("isappread")
    @Expose
    var isappread: String? = null

    @SerializedName("newfilepath")
    @Expose
    var newfilepath: List<String>? = null

    @SerializedName("newuserfilename")
    @Expose
    var newuserfilename: List<String>? = null
}