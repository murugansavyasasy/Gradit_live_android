package com.vsca.vsnapvoicecollege.Adapters
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.R

class PickThemeTemplateColourAdapter(private val itemList: List<String>
                        ,    private val onItemClick: (String) -> Unit
) :
    RecyclerView.Adapter<PickThemeTemplateColourAdapter.GridViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION


    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colour: View = itemView.findViewById(R.id.colourCode)
        val tick: ImageView = itemView.findViewById(R.id.imgTick)

        fun bind(item: String, isSelected: Boolean) {
            try {
                val parsedColor = Color.parseColor(item)
                val bgDrawable = colour.background.mutate()
                if (bgDrawable is GradientDrawable) {
                    // Set fill color
                    bgDrawable.setColor(parsedColor)

                    // Set stroke color
                    val strokeColor = if (isSelected) {
                        ContextCompat.getColor(itemView.context, R.color.dark_blue)
                    } else {
                        Color.TRANSPARENT
                    }
                    bgDrawable.setStroke(4.dpToPx(itemView.context), strokeColor)
                }
            } catch (e: IllegalArgumentException) {
                (colour.background as? GradientDrawable)?.apply {
                    setColor(Color.GRAY)
                    setStroke(4.dpToPx(itemView.context), Color.TRANSPARENT)
                }
            }

            tick.visibility = if (isSelected) View.VISIBLE else View.GONE
        }


    }
    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resume_builder_theme_colour_item, parent, false)
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

            onItemClick(itemList[selectedPosition])
        }
    }



    override fun getItemCount(): Int = itemList.size
}
