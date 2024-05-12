package com.example.weatherapp_mobapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapp_mobapp.databinding.ActivityMainBinding
import com.example.weatherapp_mobapp.utils.DataUtils
import com.example.weatherapp_mobapp.utils.WeatherUtils

class A2MainActivity : AppCompatActivity() {

    val NEXT_DAYS_CHECK = 4

    private val view by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        WeatherUtils.setTableValues(view.tCurrentWeather, DataUtils.mainUser.currentCity, this)
        WeatherUtils.setNextDaysValues(view.llNextDays, DataUtils.mainUser.currentCity)
        WeatherUtils.setMoreDetailsValues(view.llMoreDetails, DataUtils.mainUser.currentCity)
        setButtonListeners()
    }

    private fun setButtonListeners() {
        view.btnCititesList.setOnClickListener {
            val intent = Intent(this, A4CitiesList::class.java)
            startActivity(intent)
        }
        view.btnFavCitites.setOnClickListener {
            val intent = Intent(this, A3FavouriteCitiesList::class.java)
            startActivity(intent)
        }
    }
}