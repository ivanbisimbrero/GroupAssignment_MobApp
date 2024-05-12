package com.example.weatherapp_mobapp.utils

import android.content.Context
import com.example.weatherapp_mobapp.databinding.GeneralNextDaysBinding
import com.example.weatherapp_mobapp.databinding.GeneralTimeDetailTableBinding
import com.example.weatherapp_mobapp.databinding.MoreDetailsTableBinding
import com.example.weatherapp_mobapp.model.City
import com.bumptech.glide.Glide

class WeatherUtils {
    companion object {
        fun setTableValues(tableBinding: GeneralTimeDetailTableBinding, city: City, context: Context) {
            tableBinding.tvCityName.text = city.name
            val currentDay = city.days[0]
            tableBinding.tvCurrentTemperature.text = currentDay.temp.toString().plus("º")
            tableBinding.tvWeatherCondition.text = currentDay.conditions
            tableBinding.tvMinValue.text = currentDay.tempmin.toString().plus("º")
            tableBinding.tvMaxValue.text = currentDay.tempmax.toString().plus("º")
            val baseIconUrl = "https://raw.githubusercontent.com/visualcrossing/WeatherIcons/main/PNG/4th%20Set%20-%20Color/"
            val url = "$baseIconUrl${currentDay.icon}.png" // Construye la URL completa del icono
            Glide.with(context)
                .load(url)
                .into(tableBinding.ivConditionIcon)
        }

        fun setNextDaysValues(nextDaysBinding: GeneralNextDaysBinding, city: City) {
            val days = listOf(nextDaysBinding.tvDayPlusOne, nextDaysBinding.tvDayPlusTwo,
                nextDaysBinding.tvDayPlusThree, nextDaysBinding.tvDayPlusFour)
            val minTemperatures = listOf(nextDaysBinding.tvDayPlusOneMinValue, nextDaysBinding.tvDayPlusTwoMinValue,
                nextDaysBinding.tvDayPlusThreeMinValue, nextDaysBinding.tvDayPlusFourMinValue)
            val maxTemperatures = listOf(nextDaysBinding.tvDayPlusOneMaxValue, nextDaysBinding.tvDayPlusTwoMaxValue,
                nextDaysBinding.tvDayPlusThreeMaxValue, nextDaysBinding.tvDayPlusFourMaxValue)

            for (i in 1..4) {
                val auxDay = city.days[i]
                days[i-1].text = auxDay.datetime
                minTemperatures[i-1].text = auxDay.tempmin.toString().plus("º")
                maxTemperatures[i-1].text = auxDay.tempmax.toString().plus("º")
            }
        }

        fun setMoreDetailsValues(moreDetailsTableBinding: MoreDetailsTableBinding, city: City) {
            val currentDay = city.days[0]
            moreDetailsTableBinding.tvFeelLikeValue.text = currentDay.feelslike.toString().plus("º")
            moreDetailsTableBinding.tvHumidityValue.text = currentDay.humidity.toString().plus("%")
            moreDetailsTableBinding.tvRainProbValue.text = currentDay.precipprob.toString().plus("%")
            moreDetailsTableBinding.tvWindSpeedValue.text = currentDay.windspeed.toString().plus(" km/h")
        }
    }

}