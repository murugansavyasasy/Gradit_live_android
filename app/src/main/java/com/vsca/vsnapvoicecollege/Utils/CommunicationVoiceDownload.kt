package com.vsca.vsnapvoicecollege.Utils


import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.util.Log
import com.vsca.vsnapvoicecollege.Adapters.CommunicationAdapter
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.CommunicationisExpandAdapter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.sql.DriverManager


object CommunicationVoiceDownload {
    var mProgressDialog: ProgressDialog? = null
    lateinit var client_app: RestClient


    fun downloadSampleFile(
        activity: Context?,
        urldata: String,
        folder: String?,
        fileName: String,
        holder: CommunicationAdapter.MyViewHolder

    ) {
        mProgressDialog = ProgressDialog(activity)
        mProgressDialog!!.isIndeterminate = true
        mProgressDialog!!.setMessage("Please wait...")
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.show()
        Log.d("File URL", urldata)

        val call: Call<ResponseBody?>? =
            RestClient.apiInterfaces.downloadFileWithDynamicUrlAsync(urldata)
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful) {
                    mProgressDialog!!.dismiss()

                    Log.d("DOWNLOADING...", "server contacted and has file")

                    object : AsyncTask<Void?, Void?, Boolean>() {
                        protected fun doInBackground(vararg voids: Void): Boolean {
                            val writtenToDisk = writeResponseBodyToDisk(
                                activity!!,
                                response.body(),
                                folder,
                                fileName
                            )
                            Log.d("DOWNLOADING...DOINBA", writtenToDisk.toString())

                            return writtenToDisk
                        }

                        override fun onPostExecute(status: Boolean) {
                            super.onPostExecute(status)
                            if (status) {
                                Log.d("DownloadSucces", status.toString())
                                CommunicationisExpandAdapter = true
                                CommonUtil.DownloadingFile = 1
//                                holder.recentSeekbarlayout.visibility = View.VISIBLE


                            } else {
                                Log.d("DownloadFailure", status.toString())

                                CommunicationisExpandAdapter = false
                            }
                        }

                        override fun doInBackground(vararg params: Void?): Boolean {
                            val writtenToDisk = writeResponseBodyToDisk(
                                activity!!,
                                response.body(),
                                folder,
                                fileName
                            )
                            Log.d(
                                "DOWNLOADING...doin",
                                "file download was a success? $writtenToDisk"
                            )

                            return writtenToDisk
                        }
                    }.execute()
                } else {
                    mProgressDialog!!.dismiss()
                    CommunicationisExpandAdapter = false

                    Log.d("DOWNLOADING...", "server contact failed")
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                if (mProgressDialog!!.isShowing) mProgressDialog!!.dismiss()
                mProgressDialog!!.dismiss()
                CommunicationisExpandAdapter = false

                Log.e("DOWNLOADING...", "error: $t")
            }
        })
    }


    fun writeResponseBodyToDisk(
        activity: Context,
        body: ResponseBody?,
        folder: String?,
        fileName: String?
    ): Boolean {
        return try {
            val filepath: String

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                filepath =
                    activity.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.path
            } else {
                filepath = Environment.getExternalStorageDirectory().path
            }


            val file = File(filepath, folder)
            val dir = File(file.absolutePath)
            DriverManager.println("body: $body")
            if (!dir.exists()) {
                dir.mkdirs()
                DriverManager.println("Dir: $dir")
            }
            var futureStudioIconFile = File(dir, fileName) //"Hai.mp3"
            if (!futureStudioIconFile.exists()) {
                val futureStudioIconFile1 = File(dir, fileName)
                futureStudioIconFile = futureStudioIconFile1
            }
            DriverManager.println("futureStudioIconFile: $futureStudioIconFile")

            // todo change the file location/name according to your needs
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                inputStream = body!!.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
                if (mProgressDialog!!.isShowing) mProgressDialog!!.dismiss()
                mProgressDialog!!.dismiss()
            }
        } catch (e: IOException) {
            false
        }
    }

}
