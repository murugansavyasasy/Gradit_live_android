package com.vsca.vsnapvoicecollege.ActivitySender

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
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.vsca.vsnapvoicecollege.Activities.ViewFiles
import com.vsca.vsnapvoicecollege.Model.ImageListView
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.albumImage.PDF_Reader
import javax.sql.DataSource

class ImageViewAdapter(
    private val noticeBoardImages: ArrayList<ImageListView>,
    private val context: Context?
) :
    RecyclerView.Adapter<ImageViewAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @JvmField
        @BindView(R.id.imgGrid)
        var imgGrid: ImageView? = null


        @JvmField
        @BindView(R.id.progress1)
        var progress1: ProgressBar? = null

        @JvmField
        @BindView(R.id.LayoutEventPhoto)
        var LayoutEventPhoto: ConstraintLayout? = null

        @JvmField
        @BindView(R.id.lmgPdf)
        var lmgPdf: ImageView? = null

        init {
            ButterKnife.bind(this, (itemView))
        }
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
