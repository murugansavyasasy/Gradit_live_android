package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class PdfRendererViewer(
    private val context: Context,
    private val recyclerView: RecyclerView
) {
    private var pdfRenderer: PdfRenderer? = null
    private var fileDescriptor: ParcelFileDescriptor? = null

    fun loadPdfFromFile(filePath: String) {
        val file = File(filePath)
        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor!!)

        val adapter = PdfPageAdapter(pdfRenderer!!)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
    }

    fun release() {
        pdfRenderer?.close()
        fileDescriptor?.close()
    }

    fun zoomIn() {
        // No-op here, handled by ZoomLayout
    }

    fun zoomOut() {
        // No-op here, handled by ZoomLayout
    }
}


//working code
//package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume
//
//import android.content.Context
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import java.io.File
//
//class PdfRendererViewer(
//    private val context: Context,
//    private val recyclerView: RecyclerView
//) {
//    private var adapter: PdfPageAdapter? = null
//
//    fun loadPdfFromFile(pdfPath: String) {
//        val file = File(pdfPath)
//        adapter = PdfPageAdapter(context, file)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = adapter
//    }
//
//    fun release() {
//        adapter?.close()
//    }
//}

//package com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.BuildMyResume
//
//import android.content.Context
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import java.io.File
//
//class PdfRendererViewer(
//    private val context: Context,
//    private val recyclerView: RecyclerView
//) {
//    private var adapter: PdfPageAdapter? = null
//    private var currentScale = 1.0f
//    private val minScale = 1.0f
//    private val maxScale = 3.0f
//
//    fun loadPdfFromFile(pdfPath: String) {
//        val file = File(pdfPath)
//        adapter = PdfPageAdapter(context, file, currentScale)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = adapter
//    }
//
//    fun zoomIn() {
//        if (currentScale < maxScale) {
//            currentScale += 0.25f
//            adapter?.setScaleFactor(currentScale)
//        }
//    }
//
//    fun zoomOut() {
//        if (currentScale > minScale) {
//            currentScale -= 0.25f
//            adapter?.setScaleFactor(currentScale)
//        }
//    }
//
//    fun release() {
//        adapter?.close()
//    }
//}
