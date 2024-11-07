package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.vsca.vsnapvoicecollege.Model.ImageListView
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CustomLoading

class ImageViewer(
    private val isImages: ArrayList<ImageListView>,
    private val context: Context?
) :
    RecyclerView.Adapter<ImageViewer.MyViewHolder>() {
    lateinit var progress: ProgressBar

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @JvmField
        @BindView(R.id.imgView)
        var imgView: ImageView? = null

        @JvmField
        @BindView(R.id.webview)
        var webview: WebView? = null

        init {
            ButterKnife.bind(this, (itemView))
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewer.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.imagemultiple_viewer, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageViewer.MyViewHolder, position: Int) {
        val isImage = isImages[position]

        if (isImage.filetype.equals("image")) {
            holder.imgView!!.visibility = View.VISIBLE
            holder.webview!!.visibility = View.GONE
            Glide.with(context!!).load(isImage.filepath).into(holder.imgView!!)
        } else {
            holder.imgView!!.visibility = View.GONE
            holder.webview!!.visibility = View.VISIBLE
            val progressDialog = CustomLoading.createProgressDialog(context)
            holder.webview!!.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, progress: Int) {
                    progressDialog.show()
                    if (progress == 100) {
                        progressDialog.dismiss()
                    }
                }
            }
            holder.webview!!.settings.javaScriptEnabled = true
            holder.webview!!.setVerticalScrollbarOverlay(true)
            holder.webview!!.settings.setSupportZoom(true)
            holder.webview!!.settings.builtInZoomControls = true
            holder.webview!!.settings.displayZoomControls = false
            try {
                holder.webview!!.loadUrl("https://docs.google.com/gview?embedded=true&url=${isImage.filepath!!}")
                progressDialog.dismiss()
            } catch (Exception: Exception) {
                Log.d("Exception", Exception.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return isImages.size
    }
}