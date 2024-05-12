package com.example.weatherapp_mobapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.weatherapp_mobapp.R
import com.example.weatherapp_mobapp.model.City
import com.example.weatherapp_mobapp.sharedPreferences.CrudAPI
import com.example.weatherapp_mobapp.utils.DataUtils

class CityAdapter(context: Context, cities: List<City>, private val displayType: String,
                  private val crudApi: CrudAPI
) : ArrayAdapter<City>(context, 0, cities) {

    companion object {
        const val TEMPERATURE = "Temperature"
        const val RAIN_PROB = "RainProb"
        const val CONDITION = "Condition"
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val cityName = view.findViewById<TextView>(R.id.tvItem)
        val cityInfo = view.findViewById<TextView>(R.id.tvSubItem)
        val btnFavorite = view.findViewById<ImageButton>(R.id.btnFavorite)

        val city = getItem(position)

        cityName.text = city?.name
        when (displayType) {
            TEMPERATURE -> cityInfo.text = city?.days?.get(0)?.temp.toString().plus("º")
            RAIN_PROB -> cityInfo.text = city?.days?.get(0)?.precipprob.toString().plus("%")
            CONDITION -> cityInfo.text = city?.days?.get(0)?.conditions
            else -> cityInfo.text = ""
        }

        btnFavorite.setOnClickListener {
            city?.isFavouriteCity = !city?.isFavouriteCity!!
            if(city.isFavouriteCity) {
                DataUtils.mainUser.favCities.add(city)
                crudApi.save(city.name)
            }
            else {
                DataUtils.mainUser.favCities.remove(city)
                crudApi.delete(city.name)
            }
            updateFavoriteButton(btnFavorite, city.isFavouriteCity)
            notifyDataSetChanged()
        }

        updateFavoriteButton(btnFavorite, city!!.isFavouriteCity)

        return view
    }

    private fun updateFavoriteButton(btnFavorite: ImageButton, isFavorite: Boolean) {
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_star_filled)  // Filled star icon
        } else {
            btnFavorite.setImageResource(R.drawable.ic_star_outline)  // Clear star icon
        }
    }
}