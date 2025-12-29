package com.vsca.vsnapvoicecollege.Model

data class CommonFileData(
    val type: String,
    val path: String
)

enum class FileType {
    IMAGE, PDF, DOC, DOCX, EXCEL, PPT, TXT, VIDEO, AUDIO, OTHER
}
data class FilePath(
    var url: String,
    val type: String
)
