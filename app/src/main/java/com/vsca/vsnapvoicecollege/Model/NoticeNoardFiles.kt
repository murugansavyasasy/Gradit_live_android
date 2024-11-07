package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NoticeNoardFiles {

    @SerializedName("filepath")
    @Expose
    var filepath: String? = null

    @SerializedName("filetype")
    @Expose
    var filetype: String? = null
}