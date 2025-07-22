package com.vsca.vsnapvoicecollege.utils

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object FileShareHelper {

    fun shareFile(activity: Activity, url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connect()

                val contentType = connection.contentType ?: "application/octet-stream"
                var fileName = url.substringAfterLast("/").substringBefore("?")
                if (!fileName.contains(".")) {
                    val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType)
                    fileName += ".${ext ?: "bin"}"
                }

                val file = File(activity.cacheDir, fileName)
                if (!file.exists()) {
                    connection.inputStream.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                }

                val uri: Uri = FileProvider.getUriForFile(
                    activity,
                    "${activity.packageName}.provider",
                    file
                )

                val mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(file.extension.lowercase()) ?: contentType

                withContext(Dispatchers.Main) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = mimeType
                        putExtra(Intent.EXTRA_STREAM, uri)
                        clipData = ClipData.newUri(activity.contentResolver, "Shared File", uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    activity.startActivity(Intent.createChooser(shareIntent, "Share File"))
                }

            } catch (e: Exception) {
                Log.e("FileShareHelper", "Error sharing file: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "Failed to share file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
