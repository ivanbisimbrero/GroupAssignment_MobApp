package com.example.weatherapp_mobapp.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp_mobapp.databinding.GeneralNextDaysBinding
import com.example.weatherapp_mobapp.databinding.GeneralTimeDetailTableBinding
import com.example.weatherapp_mobapp.databinding.MoreDetailsTableBinding
import com.example.weatherapp_mobapp.model.City
import com.bumptech.glide.Glide
import com.example.weatherapp_mobapp.adapter.HourAdapter
import com.example.weatherapp_mobapp.databinding.ListNextHoursWeatherBinding
import com.example.weatherapp_mobapp.model.Hour
import java.time.LocalDateTime

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
            val url = "$baseIconUrl${currentDay.icon}.png" // Build the whole URL to the icon
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

        fun setNextHoursValues(listNextHoursWeatherBinding: ListNextHoursWeatherBinding, city: City,
        context: Context) {
            val next24Hours = getNext24Hours(city)
            //Change the icon on the first hour to be the same that in the current forecast
            next24Hours[0].icon = city.days[0].icon
            //We change to the temperature, to be consistent in our predictions
            next24Hours[0].temp = city.days[0].temp
            println(next24Hours)
            println()
            listNextHoursWeatherBinding.rvHourlyForecast.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = HourAdapter(next24Hours, context)
            listNextHoursWeatherBinding.rvHourlyForecast.adapter = adapter
        }

        private fun getNext24Hours(city: City): List<Hour> {
            val hoursList = mutableListOf<Hour>()
            val currentTime = LocalDateTime.now()
            val currentHour = currentTime.hour
            var hoursNeeded = 24
            var currentDayIndex = 0

            while (hoursNeeded > 0 && currentDayIndex < city.days.size) {
                val forecast = city.days[currentDayIndex]
                // Filter out the times of the current day that are greater than or equal to the current time if it is the first day.
                val relevantHours = if (currentDayIndex == 0) {
                    forecast.hours.filter { it.datetime.substring(0, 2).toInt() >= currentHour }
                } else {
                    forecast.hours
                }

                for (hour in relevantHours) {
                    if (hoursNeeded == 0) break
                    hoursList.add(hour)
                    hoursNeeded--
                }
                currentDayIndex++ // Move to the next day if more hours are still needed
            }

            return hoursList
        }

    }

}