package com.example.weatherapp_mobapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp_mobapp.R
import com.example.weatherapp_mobapp.model.Hour

class HourAdapter(private val hours: List<Hour>, private val context: Context) : RecyclerView.Adapter<HourAdapter.HourViewHolder>() {

    class HourViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHour: TextView = view.findViewById(R.id.tvHour)
        val ivWeatherIcon: ImageView = view.findViewById(R.id.ivWeatherIcon)
        val tvTemperature: TextView = view.findViewById(R.id.tvTemperature)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hour_item, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        val hour = hours[position]
        holder.tvHour.text = hour.datetime.substring(0, 5) // Shows only the hour and minute
        holder.tvTemperature.text = "${hour.temp}º"
        setIconResourceId(hour.icon, holder.ivWeatherIcon)
    }

    override fun getItemCount(): Int = hours.size

    private fun setIconResourceId(iconName: String, imageView: ImageView) {
        val baseIconUrl = "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/4th%20Set%20-%20Color/"
        val url = "$baseIconUrl${iconName}.png" // Build the whole URL to the icon
        Glide.with(context)
            .load(url)
            .into(imageView)
    }
}
