package com.vsca.vsnapvoicecollege.Activities.FilePreviewActivity


import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.vsca.vsnapvoicecollege.Model.CommonFileData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil


class FileViewerAdapter(
    private val context: Context,
    private val fileList: List<CommonFileData>
) : RecyclerView.Adapter<FileViewerAdapter.FileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.homework_view_image_document_item, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val item = fileList[position]

        holder.documentWebView.visibility = View.GONE
        holder.imageView.visibility = View.GONE
        holder.loadingBar.visibility = View.VISIBLE

        Log.d("iscomng",item.path)
        if (item.type == CommonUtil.IMAGE) {
            holder.imageView.visibility = View.VISIBLE
            Glide.with(context)
                .load(item.path)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.loadingBar.visibility = View.GONE
                        e?.printStackTrace()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.loadingBar.visibility = View.GONE
                        holder.imageView.setImageDrawable(resource)
                        return true
                    }
                })
                .into(holder.imageView)
        } else {
            var isFile = ""
            holder.documentWebView.visibility = View.VISIBLE

            if (item.type == CommonUtil.VIDEO) {
                isFile = item.path
            } else {
                isFile = "https://docs.google.com/gview?embedded=true&url=${item.path}"
            }
            holder.documentWebView.settings.apply {
                javaScriptEnabled = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
                domStorageEnabled = true
            }


            holder.documentWebView.setInitialScale(1)
            holder.documentWebView.scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY
            holder.documentWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            holder.documentWebView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    holder.loadingBar.visibility = View.GONE
                }
            }
            holder.documentWebView.loadUrl(isFile)
        }
    }

    override fun getItemCount(): Int = fileList.size

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val documentWebView: WebView = itemView.findViewById(R.id.documentWebView)
        val loadingBar: ProgressBar = itemView.findViewById(R.id.loadingBar)
        val imageView: PhotoView = itemView.findViewById(R.id.imageView)
    }
}