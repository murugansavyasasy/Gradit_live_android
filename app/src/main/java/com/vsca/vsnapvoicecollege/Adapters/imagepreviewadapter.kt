package com.vsca.vsnapvoicecollege.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.vsca.vsnapvoicecollege.ActivitySender.AllImageViewer
import com.vsca.vsnapvoicecollege.Model.ImageListView
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.albumImage.Image


class imagepreviewadapter(
    private val noticeBoardImages: ArrayList<ImageListView>,
    private val context: Context?
) :
    RecyclerView.Adapter<imagepreviewadapter.MyViewHolder>() {
    var isImagesCount = 1
    var isFileCount = 0
    var isAllowedFileCount = 6
    var isPlus = "+"

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @JvmField
        @BindView(R.id.imgGrid)
        var imgGrid: ImageView? = null

        @JvmField
        @BindView(R.id.progress)
        var progress: ProgressBar? = null

        @JvmField
        @BindView(R.id.LayoutEventPhoto)
        var LayoutEventPhoto: ConstraintLayout? = null

        @JvmField
        @BindView(R.id.lmgPdf)
        var lmgPdf: ImageView? = null

        @JvmField
        @BindView(R.id.pdfCount)
        var pdfCount: TextView? = null

        @JvmField
        @BindView(R.id.imgCount)
        var imgCount: TextView? = null

        @JvmField
        @BindView(R.id.progress1)
        var progress1: ProgressBar? = null

        init {
            ButterKnife.bind(this, (itemView))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_previe, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val Images = noticeBoardImages[position]

        if (Images.filetype.equals("image")) {
            holder.imgGrid!!.visibility = View.VISIBLE
            holder.lmgPdf!!.visibility = View.GONE
            if (isImagesCount <= isAllowedFileCount) {
                holder.imgCount!!.visibility = View.GONE
                Glide.with(holder.imgGrid!!)
                    .load(Images.filepath!!)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable> {

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            TODO("Not yet implemented")
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            holder.progress1!!.visibility = View.GONE
                            return false
                        }
                    })
                    .into(holder.imgGrid!!)
                if (isImagesCount == isAllowedFileCount) {
                    isFileCount = CommonUtil.isImageViewList.size - isAllowedFileCount
                    Log.d("isFailCount", isFileCount.toString())
                    if (isFileCount != 0) {
                        holder.imgCount!!.text = isPlus + isFileCount.toString()
                        holder.imgCount!!.visibility = View.VISIBLE
                    }
                } else {
                    holder.imgCount!!.visibility = View.GONE
                }
                isImagesCount++
            } else {
                holder.imgGrid!!.visibility = View.GONE
                holder.lmgPdf!!.visibility = View.GONE
                holder.LayoutEventPhoto!!.visibility = View.GONE
            }
        } else {
            if (isImagesCount <= isAllowedFileCount) {
                holder.pdfCount!!.visibility = View.GONE
                holder.lmgPdf!!.visibility = View.VISIBLE
                holder.imgGrid!!.visibility = View.GONE
                if (isImagesCount == isAllowedFileCount) {
                    isFileCount = CommonUtil.isImageViewList.size - isAllowedFileCount
                    Log.d("isFailCount", isFileCount.toString())
                    if (isFileCount != 0) {
                        holder.pdfCount!!.text = isPlus + isFileCount.toString()
                        holder.pdfCount!!.visibility = View.VISIBLE
                    }
                } else {
                    holder.pdfCount!!.visibility = View.GONE
                }
                isImagesCount++
            } else {
                holder.imgGrid!!.visibility = View.GONE
                holder.lmgPdf!!.visibility = View.GONE
                holder.LayoutEventPhoto!!.visibility = View.GONE
            }
        }

        holder.LayoutEventPhoto!!.setOnClickListener {

            val i: Intent = Intent(context, AllImageViewer::class.java)
            CommonUtil.isImageViewList.removeAt(position)
            CommonUtil.isImageViewList.add(0, Images)
            Log.d("Images", Images.filepath.toString())
            context!!.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return noticeBoardImages.size
    }
}





