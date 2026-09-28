package com.vsca.vsnapvoicecollege.Utils

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.webkit.WebResourceRequest      // NEW
import android.webkit.WebResourceResponse     // NEW
import android.webkit.WebView
import android.webkit.WebViewClient


class MyWebViewClientContext(
    var context: Context,
    private val onLoadFailed: ((WebView) -> Boolean)? = null   // NEW
) : WebViewClient() {

    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String,
        failingUrl: String
    ) {
        if (errorCode == ERROR_TIMEOUT) {
            Log.d("============", "======ERROR======")
            view.stopLoading()
        }

        // NEW: try the second URL first; if it was started, skip the retry page
        if (onLoadFailed?.invoke(view) == true) return

        view.loadData(
            "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>" +
                    "<body><a href=\"" + view.url + "\">Turn on your network connection and click here</a></body></html>",
            "text/html",
            "utf-8"
        )
    }

    // NEW: catches 404, 500, etc. (onReceivedError does not fire for these)
    override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest,
        errorResponse: WebResourceResponse
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        if (request.isForMainFrame && errorResponse.statusCode >= 400) {
            onLoadFailed?.invoke(view)
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (isNetworkAvailable(context)) {
            view.loadUrl(url)
        } else showAlert("Connectivity", "Check Internet Connection")
        return true
    }

    private fun showAlert(title: String, msg: String) {
        val alertDialog = AlertDialog.Builder(
            context
        )
        alertDialog.setCancelable(false)
        alertDialog.setTitle(title)
        alertDialog.setMessage(msg)
        alertDialog.setNeutralButton("OK") { dialog, which -> }
        alertDialog.show()
    }

    companion object {
        fun isNetworkAvailable(activity: Context): Boolean {
            val connectivity = activity
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity == null) {
                return false
            } else {
                val info = connectivity.allNetworkInfo
                if (info != null) {
                    for (anInfo in info) {
                        if (anInfo.state == NetworkInfo.State.CONNECTED) {
                            return true
                        }
                    }
                }
            }
            return false
        }
    }
}