package com.vsca.vsnapvoicecollege.Utils

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PdfDownloader(private val context: Context) {

    private val folderName = "Gradit/PDF"
    private var downloadId: Long = 0

    // Extracts file info (name, timestamp, date)
    class PdfInfoExtractor(private val context: Context) {
        fun extractInfoFromUrl(pdfUrl: String) {
            try {
                val fileNameWithExtension = pdfUrl.substringAfterLast("/") // e.g., Resume_1752815189344.pdf
                val fileNameWithoutExtension = fileNameWithExtension.substringBeforeLast(".pdf") // Resume_1752815189344

                val parts = fileNameWithoutExtension.split("_")
                val namePart = parts.getOrNull(0) ?: "Unknown"
                val timestampPart = parts.getOrNull(1) ?: "Unknown"

                val formattedDate = try {
                    val timestamp = timestampPart.toLong()
                    val date = Date(timestamp)
                    val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
                    sdf.format(date)
                } catch (e: Exception) {
                    "Invalid timestamp"
                }

                val message = """
                    File Name   : $namePart
                    Timestamp   : $timestampPart
                    Date & Time : $formattedDate
                """.trimIndent()

                Log.d("PdfInfoExtractor", message)

            } catch (e: Exception) {
                e.printStackTrace()
                showAlert("Error", "Failed to parse PDF information.")
            }
        }

        private fun showAlert(title: String, message: String) {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }

    fun downloadPdf(pdfUrl: String) {
        if (pdfUrl.isEmpty()) {
            Toast.makeText(context, "Invalid PDF URL", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName = pdfUrl.substringAfterLast('/')
        val filePath = getFilePath(fileName)
        val file = File(filePath)

        // Show PDF info
        PdfInfoExtractor(context).extractInfoFromUrl(pdfUrl)

        if (file.exists()) {
            Toast.makeText(context, "File already exists!", Toast.LENGTH_SHORT).show()
            showAlert("File Already Exists", "File saved at:\n$filePath")
        } else {
            downloadAndSavePdf(pdfUrl)
        }
    }

    private fun downloadAndSavePdf(fileUrl: String) {
        val fileName = fileUrl.substringAfterLast("/") // Keep original name

        try {
            val request = DownloadManager.Request(Uri.parse(fileUrl))
                .setTitle("Downloading PDF")
                .setDescription("Saving resume...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$folderName/$fileName")
                .setMimeType("application/pdf")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadId = downloadManager.enqueue(request)

            Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show()

            // Register receiver
            context.registerReceiver(
                downloadReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_NOT_EXPORTED
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Download failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private val downloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id == downloadId) {
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    val uriString = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        val savedPath = Uri.parse(uriString).path ?: "Unknown path"
                        showAlert("Download Completed", "File saved to:\n$savedPath")
                    } else {
                        showAlert("Download Failed", "Something went wrong while downloading the PDF.")
                    }
                }
                cursor.close()

                try {
                    context.unregisterReceiver(this)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun getFilePath(fileName: String): String {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return downloadDir.absolutePath + File.separator + folderName + File.separator + fileName
    }
}
