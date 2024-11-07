package com.vsca.vsnapvoicecollege.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ImageListView(imagesList: String, imageType: String) {

    @SerializedName("filepath")
    @Expose
    var filepath: String? = imagesList

    @SerializedName("filetype")
    @Expose
    var filetype: String? = imageType

}