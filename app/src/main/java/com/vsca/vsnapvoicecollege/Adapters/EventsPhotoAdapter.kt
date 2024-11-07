package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.vsca.vsnapvoicecollege.Activities.ViewFiles
import com.vsca.vsnapvoicecollege.R

class EventsPhotoAdapter(private val listname: List<String>, private val context: Context?) :
    RecyclerView.Adapter<EventsPhotoAdapter.MyViewHolder>() {

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

        init {
            ButterKnife.bind(this, (itemView))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_photo_design, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val path = listname[position]

        Picasso.get().load(path).fit().into(holder.imgGrid, object : Callback {
            override fun onSuccess() {
                holder.progress!!.visibility = View.GONE
            }

            override fun onError(e: java.lang.Exception?) {
                holder.progress!!.visibility = View.VISIBLE
            }
        })

        holder.LayoutEventPhoto!!.setOnClickListener {
            val i: Intent = Intent(context, ViewFiles::class.java)
            i.putExtra("images", path)
            context!!.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return listname.size
    }
}




