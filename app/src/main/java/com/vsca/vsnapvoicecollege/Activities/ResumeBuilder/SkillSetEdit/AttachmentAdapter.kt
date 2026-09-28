package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.SkillSetEdit

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Activities.FilePreviewActivity.FilesViewActivity
import com.vsca.vsnapvoicecollege.Model.CommonFileData
import com.vsca.vsnapvoicecollege.Model.FilePath
import com.vsca.vsnapvoicecollege.Model.FileType
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import java.io.File

class AttachmentAdapter(
    private val context: Context,
    private val list: MutableList<FilePath>,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<AttachmentAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_question_attachment, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    // ===================== VIEW HOLDER =====================
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val txtFileName: TextView = itemView.findViewById(R.id.txtFileName)
        private val txtFileType: TextView = itemView.findViewById(R.id.txtFileType)
        private val imgDelete: ImageView = itemView.findViewById(R.id.imgDelete)

        fun bind(item: FilePath, position: Int) {

            // ---------- FILE NAME ----------
            txtFileName.text = getFileNameFromUri(item.url)
            Log.d("FileScreen", txtFileName.text.toString())

            // ---------- FILE TYPE BADGE ----------
            txtFileType.text = when (item.type) {
                FileType.PDF.toString() -> "PDF"
                FileType.DOC.toString(), FileType.DOCX.toString() -> "DOC"
                FileType.PPT.toString() -> "PPT"
                FileType.EXCEL.toString() -> "XLS"
                FileType.TXT.toString() -> "TXT"
                FileType.VIDEO.toString() -> "VID"
                FileType.IMAGE.toString() -> "IMG"
                else -> "FILE"
            }

            // ---------- REMOVE ----------
            imgDelete.setOnClickListener {
                onRemove(position)
            }

            Log.d("isTotal", item.url + item.type)
            Log.d("isTotalSize", list.size.toString())

            // ---------- OPTIONAL CLICK (OPEN FILE) ----------
            itemView.setOnClickListener {


                if (item.type == CommonUtil.IMAGE || item.type == CommonUtil.VIDEO || item.url.contains(
                        "amazonaws."
                    )
                ) {

                    val filteredFiles = list.filter {
                        it.type == CommonUtil.IMAGE || it.type == CommonUtil.VIDEO || it.url.contains(
                            "amazonaws."
                        )
                    }
                    Log.d("Files", " original : ${list.size} : Values ${list} ")
                    Log.d("Files", "CONTAINS AWS or LOCAL IMAGE or Local Video")
                    Log.d("FilteredFIles", filteredFiles.toString())
                    Log.d("FilteredFIlesSize", filteredFiles.size.toString())

                    CommonUtil.commonFileList = filteredFiles.map {
                        CommonFileData(
                            type = it.type.toString(),
                            path = it.url
                        )
                    }.toMutableList()

                    val clickedPath = item.url
                    val indexInFiltered = filteredFiles
                        .indexOfFirst { it.url == clickedPath }
                        .let { if (it >= 0) it else 0 }

                    CommonUtil.selectedFileIndex = indexInFiltered
                    Log.d("Selected_FileIndex", CommonUtil.selectedFileIndex.toString())
                    Log.d("Selected_File", item.url)
                    Log.d("CommonFilePath", CommonUtil.commonFileList.toString())


                    val intent = Intent(context, FilesViewActivity::class.java)
                    context.startActivity(intent)

                } else {
                    Log.d("Files", "Local_DOCUMENT")
                    Log.d("FilesURL", item.url)

                    val uri = if (item.url.startsWith("content://")) {
                        Uri.parse(item.url)
                    } else {
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            File(item.url)
                        )

                    }

                    val mimeType = getMimeTypeFromUri(uri)
                    val openIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, mimeType)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    val activities = context.packageManager.queryIntentActivities(
                        openIntent,
                        PackageManager.MATCH_DEFAULT_ONLY
                    )

                    if (activities.isNotEmpty()) {
                        context.startActivity(Intent.createChooser(openIntent, "Open with"))
                    } else {
                        Toast.makeText(
                            context,
                            "Please download an app to view this file.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


        }


    }

    private fun getMimeTypeFromUri(uri: Uri): String {
        val contentResolver = context.contentResolver
        return contentResolver.getType(uri) ?: "*/*"
    }

    private fun getFileNameFromUri(uriString: String): String {
        return try {
            val uri = Uri.parse(uriString)

            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(
                    uri,
                    null,
                    null,
                    null,
                    null
                )

                cursor?.use {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && it.moveToFirst()) {
                        return it.getString(nameIndex)
                    }
                }
            }

            // fallback for file paths
            File(uri.path ?: "").name

        } catch (e: Exception) {
            "Unknown File"
        }
    }

}

