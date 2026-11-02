
package com.vsca.vsnapvoicecollege.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Model.TimetableModel
import com.vsca.vsnapvoicecollege.databinding.ItemTimetableBreakBinding
import com.vsca.vsnapvoicecollege.databinding.ItemTimetablePeriodBinding
import java.util.Locale

class TimetableAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    sealed class TimetableItem {
        data class Period(val data: TimetableModel) : TimetableItem()
        data class Break(val data: TimetableModel) : TimetableItem()
    }

    private val displayList = ArrayList<TimetableItem>()

    companion object {
        private const val TYPE_PERIOD = 1
        private const val TYPE_BREAK = 2
    }

    fun setData(list: ArrayList<TimetableModel>) {

        displayList.clear()

        val sortedList = list.sortedBy { it.order }

        sortedList.forEach { item ->

            when (item.hourType) {

                "1" -> {
                    displayList.add(
                        TimetableItem.Period(item)
                    )
                }

                "2" -> {
                    displayList.add(
                        TimetableItem.Break(item)
                    )
                }
            }
        }

        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {

        return when (displayList[position]) {
            is TimetableItem.Period -> TYPE_PERIOD
            is TimetableItem.Break -> TYPE_BREAK
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return if (viewType == TYPE_PERIOD) {

            val binding = ItemTimetablePeriodBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            PeriodViewHolder(binding)

        } else {

            val binding = ItemTimetableBreakBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            BreakViewHolder(binding)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        when (val item = displayList[position]) {

            is TimetableItem.Period -> {
                (holder as PeriodViewHolder).bind(item.data)
            }

            is TimetableItem.Break -> {
                (holder as BreakViewHolder).bind(item.data)
            }
        }
    }

    override fun getItemCount(): Int = displayList.size

    fun getBreakCount(): Int {
        return displayList.count {
            it is TimetableItem.Break
        }
    }

    fun getPeriodCount(): Int {
        return displayList.count {
            it is TimetableItem.Period
        }
    }


    class PeriodViewHolder(
        private val binding: ItemTimetablePeriodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TimetableModel) {

            binding.tvStartTime.text = data.startTime.orEmpty()

            binding.tvEndTime.text = data.endTime.orEmpty()

            binding.tvPeriodName.text =
                data.name.orEmpty().uppercase(Locale.ENGLISH)

            binding.tvDuration.text = data.duration.orEmpty()

            binding.tvSubject.text =
                data.subjectName
                    ?.takeIf { it.isNotBlank() }
                    ?: "No subject"

            binding.tvFaculty.text =
                data.staffName
                    ?.takeIf { it.isNotBlank() }
                    ?: data.facultyName
                        ?.takeIf { it.isNotBlank() }
                            ?: "Faculty not assigned"
        }
    }



    class BreakViewHolder(
        private val binding: ItemTimetableBreakBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: TimetableModel) {

            binding.tvBreakTime.text =
                "${data.startTime.orEmpty()} – ${data.endTime.orEmpty()}"

            binding.tvBreakDuration.text =
                data.duration.orEmpty()
        }
    }
}
