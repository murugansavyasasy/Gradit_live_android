package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.vsca.vsnapvoicecollege.Model.GetResumeBuilderThemeTemplateImage
import com.vsca.vsnapvoicecollege.R

class PickResumeAdapter(private val itemList: List<GetResumeBuilderThemeTemplateImage>
,    private val onItemClick: (GetResumeBuilderThemeTemplateImage,Int) -> Unit
) :
    RecyclerView.Adapter<PickResumeAdapter.GridViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION


    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imgItem)
        val tick: ImageView = itemView.findViewById(R.id.imgTick)
        val text: TextView = itemView.findViewById(R.id.txtItem)
        val frame: FrameLayout = itemView.findViewById(R.id.frmImageLay)


        fun bind(item: GetResumeBuilderThemeTemplateImage, isSelected: Boolean) {
            text.text = item.resume_template_name

            Glide.with(itemView.context)
                .load(item.resume_template_image)
                .placeholder(R.drawable.image_placeholder)
                .into(image)

            val shapeableImageView = image as ShapeableImageView
            shapeableImageView.strokeWidth = 0f

            if (isSelected) {
                frame.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_resume_item_selected)
//                tick.visibility=View.VISIBLE
                itemView.alpha = 0.5f
            } else {
                frame.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_resume_item_unselected)
//                tick.visibility=View.GONE
                itemView.alpha = 1.0f
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pick_template_item, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item, position == selectedPosition)

        holder.itemView.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

            val previousSelected = selectedPosition
            selectedPosition = adapterPosition

            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)

            //Here we Pass the item and postion
            onItemClick(itemList[selectedPosition], selectedPosition + 1)
        }
    }



    override fun getItemCount(): Int = itemList.size
}
