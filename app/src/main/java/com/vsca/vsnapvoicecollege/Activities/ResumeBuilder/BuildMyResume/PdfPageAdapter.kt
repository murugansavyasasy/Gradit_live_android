package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PdfPageAdapter(
    private val pdfRenderer: PdfRenderer
) : RecyclerView.Adapter<PdfPageAdapter.PdfPageViewHolder>() {

    class PdfPageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfPageViewHolder {
        val imageView = ImageView(parent.context)
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        imageView.adjustViewBounds = true
        imageView.scaleType = ImageView.ScaleType.FIT_START
        return PdfPageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: PdfPageViewHolder, position: Int) {
        val page = pdfRenderer.openPage(position)
        val width = page.width
        val height = page.height

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()

        holder.imageView.setImageBitmap(bitmap)
        holder.imageView.layoutParams = RecyclerView.LayoutParams(width, height)
    }

    override fun getItemCount(): Int = pdfRenderer.pageCount
}


//workimng code
//package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.pdf.PdfRenderer
//import android.os.ParcelFileDescriptor
//import android.view.ViewGroup
//import android.widget.ImageView
//import androidx.recyclerview.widget.RecyclerView
//import java.io.File
//class PdfPageAdapter(
//    private val context: Context,
//    private val pdfFile: File
//) : RecyclerView.Adapter<PdfPageAdapter.PageViewHolder>() {
//
//    private var pdfRenderer: PdfRenderer? = null
//    private var fileDescriptor: ParcelFileDescriptor? = null
//    private var pageCount = 0
//
//    init {
//        try {
//            fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
//            pdfRenderer = PdfRenderer(fileDescriptor!!)
//            pageCount = pdfRenderer?.pageCount ?: 0
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    inner class PageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
//        val imageView = ImageView(context).apply {
//            layoutParams = RecyclerView.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            adjustViewBounds = true
//            scaleType = ImageView.ScaleType.FIT_CENTER
//        }
//        return PageViewHolder(imageView)
//    }
//
//    override fun getItemCount(): Int = pageCount
//
//    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
//        val page = pdfRenderer?.openPage(position)
//        page?.let {
//            val scale = 2.5f // Increase this for higher quality (2.5x = webview-like clarity)
//            val width = (it.width * scale).toInt()
//            val height = (it.height * scale).toInt()
//            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//            it.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//            holder.imageView.setImageBitmap(bitmap)
//            it.close()
//        }
//    }
//
//    fun close() {
//        pdfRenderer?.close()
//        fileDescriptor?.close()
//    }
//}


//package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.pdf.PdfRenderer
//import android.os.ParcelFileDescriptor
//import android.view.ViewGroup
//import android.widget.ImageView
//import androidx.recyclerview.widget.RecyclerView
//import android.view.ViewGroup.LayoutParams.MATCH_PARENT
//import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
//import java.io.File
//class PdfPageAdapter(
//    private val context: Context,
//    private val pdfFile: File,
//    private var scaleFactor: Float
//) : RecyclerView.Adapter<PdfPageAdapter.PageViewHolder>() {
//
//    private var pdfRenderer: PdfRenderer? = null
//    private var fileDescriptor: ParcelFileDescriptor? = null
//    private var pageCount = 0
//
//    init {
//        try {
//            fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
//            pdfRenderer = PdfRenderer(fileDescriptor!!)
//            pageCount = pdfRenderer?.pageCount ?: 0
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    inner class PageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
//        val imageView = ImageView(context)
//        imageView.layoutParams = ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        imageView.adjustViewBounds = true
//        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
//        return PageViewHolder(imageView)
//    }
//
//    override fun getItemCount(): Int = pageCount
//
//    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
//        val page = pdfRenderer?.openPage(position)
//        page?.let {
//            val width = (page.width * scaleFactor).toInt()
//            val height = (page.height * scaleFactor).toInt()
//            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//            holder.imageView.setImageBitmap(bitmap)
//
//            // Apply scaled height to ImageView
//            holder.imageView.layoutParams = holder.imageView.layoutParams.apply {
//                this.height = height
//                this.width = ViewGroup.LayoutParams.MATCH_PARENT
//            }
//
//            page.close()
//        }
//    }
//
//    fun setScaleFactor(newScale: Float) {
//        scaleFactor = newScale
//        notifyDataSetChanged()
//    }
//
//    fun close() {
//        pdfRenderer?.close()
//        fileDescriptor?.close()
//    }
//}
//
