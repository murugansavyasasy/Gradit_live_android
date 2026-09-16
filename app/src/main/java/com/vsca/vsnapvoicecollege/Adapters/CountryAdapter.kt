package com.vsca.vsnapvoicecollege.Adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.vsca.vsnapvoicecollege.Model.CountryDetails
import com.vsca.vsnapvoicecollege.R

class CountryAdapter(
    private val onCountryClick: (CountryDetails) -> Unit,
    private val onEmptyResult: (Boolean) -> Unit
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private val originalList = ArrayList<CountryDetails>()
    private val countryList = ArrayList<CountryDetails>()

    private var selectedCountryId: Int? = null

//    private val defaultFlagUrl = "https://www.worldometers.info//img/flags/small/tn_in-flag.gif"
    private val defaultFlagUrl = "https://flagcdn.com/w320/in.png"

    class CountryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val imgCountry: ShapeableImageView =
            itemView.findViewById(R.id.imgCountry)

        val txtCountry: TextView =
            itemView.findViewById(R.id.txtCountry)

        val imgSelected: ImageView =
            itemView.findViewById(R.id.imgSelected)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country_rewamp, parent, false)

        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CountryViewHolder,
        position: Int
    ) {
        val country = countryList[position]

        holder.txtCountry.text = country.country ?: ""

        val flagUrl = if (!country.flag_url.isNullOrBlank()) {
            country.flag_url
        } else {
            defaultFlagUrl
        }

        Glide.with(holder.itemView.context)
            .load(flagUrl)
            .placeholder(R.drawable.ic_default_image)
            .error(R.drawable.ic_default_image)
            .into(holder.imgCountry)

        val isSelected = selectedCountryId == country.countryid

        holder.imgSelected.setImageResource(
            if (isSelected) {
                R.drawable.green_tick_icon
            } else {
                R.drawable.circle_unchecked
            }
        )

        holder.imgSelected.visibility = View.VISIBLE

        holder.itemView.setOnClickListener {

            if (selectedCountryId == country.countryid) {
                selectedCountryId = null
            } else {
                selectedCountryId = country.countryid
            }

            notifyDataSetChanged()

            onCountryClick(country)
        }
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    fun setData(list: List<CountryDetails>) {

        originalList.clear()
        originalList.addAll(list)

        countryList.clear()
        countryList.addAll(list)

        notifyDataSetChanged()

        onEmptyResult(countryList.isEmpty())
    }

    fun filter(query: String) {

        countryList.clear()

        val searchText = query.trim()

        if (searchText.isEmpty()) {

            countryList.addAll(originalList)

        } else {

            countryList.addAll(
                originalList.filter {
                    it.country?.contains(
                        searchText,
                        ignoreCase = true
                    ) == true
                }
            )
        }

        notifyDataSetChanged()

        onEmptyResult(countryList.isEmpty())
    }
}