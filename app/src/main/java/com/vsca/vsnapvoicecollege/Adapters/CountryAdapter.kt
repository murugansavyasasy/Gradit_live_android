package com.vsca.vsnapvoicecollege.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

    private val defaultFlagUrl = "https://www.worldometers.info//img/flags/small/tn_in-flag.gif"

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

        // Selected country
        if (selectedCountryId == country.countryid) {
            holder.imgSelected.visibility = View.VISIBLE
        } else {
            holder.imgSelected.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {

            selectedCountryId = country.countryid

            notifyDataSetChanged()

            // Pass selected CountryDetails to Activity
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

        // Tell Activity whether filtered data is empty
        onEmptyResult(countryList.isEmpty())
    }
}