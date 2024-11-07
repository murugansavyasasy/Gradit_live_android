package com.vsca.vsnapvoicecollege.Model

import java.io.Serializable

class AWSUploadedFiles(val filepath: String, var fileName: String?, var contentype: String?) :
    Serializable