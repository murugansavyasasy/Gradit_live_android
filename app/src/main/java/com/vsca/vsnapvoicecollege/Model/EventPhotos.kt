package com.vsca.vsnapvoicecollege.Model

import java.util.ArrayList

class EventPhotos {

    var PhotoID: String? = null
    var FileList = ArrayList<String>()

    constructor(photoid: String?, filelist:  ArrayList<String>) {
        this.PhotoID = photoid
        this.FileList = filelist
    }
}