package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.vsca.vsnapvoicecollege.Activities.ViewFiles
import com.vsca.vsnapvoicecollege.Model.ImageListView
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.albumImage.PDF_Reader

class ImageViewAdapter(
    private val noticeBoardImages: ArrayList<ImageListView>,
    private val context: Context?
) :
    RecyclerView.Adapter<ImageViewAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgGrid: ImageView = itemView!!.findViewById(R.id.imgGrid)!!
        val progress1: ProgressBar = itemView!!.findViewById(R.id.progress1)!!
        val LayoutEventPhoto: ConstraintLayout = itemView!!.findViewById(R.id.LayoutEventPhoto)!!
        val lmgPdf: ImageView = itemView!!.findViewById(R.id.lmgPdf)!!


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_previe, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val Images = noticeBoardImages[position]

        if (Images.filetype.equals("image")) {
            holder.imgGrid!!.visibility = View.VISIBLE
            holder.lmgPdf!!.visibility = View.GONE

            Glide.with(holder.imgGrid!!)
                .load(Images.filepath!!)
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        p0: GlideException?,
                        p1: Any?,
                        target: Target<Drawable>,
                        p3: Boolean
                    ): Boolean {
                        holder.progress1!!.visibility = View.GONE
                        return false
                    }
                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.progress1!!.visibility = View.GONE
                        return false
                    }
                })
                .into(holder.imgGrid!!)
        } else {
            holder.lmgPdf!!.visibility = View.VISIBLE
            holder.imgGrid!!.visibility = View.GONE
        }


        holder.LayoutEventPhoto!!.setOnClickListener {
            if (Images.filetype.equals("image")) {
                val i: Intent = Intent(context, ViewFiles::class.java)
                i.putExtra("images", Images.filepath)
                context!!.startActivity(i)
            } else {
                val i: Intent = Intent(context, PDF_Reader::class.java)
                i.putExtra("PdfView", Images.filetype)
                context!!.startActivity(i)
            }
        }
    }

    override fun getItemCount(): Int {
        return noticeBoardImages.size
    }
}
