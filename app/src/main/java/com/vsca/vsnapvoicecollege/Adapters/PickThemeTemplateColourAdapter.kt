package com.vsca.vsnapvoicecollege.Adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
                val parsedColor = android.graphics.Color.parseColor(item)
                colour.setBackgroundColor(parsedColor)
            } catch (e: IllegalArgumentException) {
                colour.setBackgroundColor(android.graphics.Color.GRAY)
            }

            // Show tick if selected
            tick.visibility = if (isSelected) View.VISIBLE else View.GONE
        }
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
