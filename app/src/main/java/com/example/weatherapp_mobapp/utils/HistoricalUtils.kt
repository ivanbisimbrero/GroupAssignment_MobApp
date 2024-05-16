package com.example.weatherapp_mobapp.utils

import com.example.weatherapp_mobapp.model.Forecast
import com.github.mikephil.charting.data.Entry

class HistoricalUtils(private val forecasts: List<Forecast>) {

    fun printInformation() {
        println(forecasts.toString())
    }

    fun averageTemperature(): Double {
        return forecasts.map { it.temp }.average()
    }

    fun countSunnyDays(): Int {
        return forecasts.count { it.conditions.lowercase().contains("clear") }
    }

    fun maxWindSpeed(): Double {
        return forecasts.maxByOrNull { it.windspeed }?.windspeed ?: 0.0
    }

    fun minWindSpeed(): Double {
        return forecasts.minByOrNull { it.windspeed }?.windspeed ?: 0.0
    }

    fun averagePrecipitationProbability(): Double {
        return forecasts.map { it.precipprob }.average()
    }

    fun past15MinTemperatures(): List<Pair<String, Entry>> {
        return forecasts
            .groupBy { it.datetime }
            .mapValues { (_, values) -> values.minOf { it.tempmin } }
            .toList()
            .sortedBy { it.first }
            .takeLast(15)
            .mapIndexed { index, pair -> Pair(pair.first.substring(5), Entry(index.toFloat(), pair.second.toFloat())) }
    }

    fun past15MaxTemperatures(): List<Pair<String, Entry>> {
        return forecasts
            .groupBy { it.datetime }
            .mapValues { (_, values) -> values.maxOf { it.tempmax } }
            .toList()
            .sortedBy { it.first }
            .takeLast(15)
            .mapIndexed { index, pair -> Pair(pair.first.substring(5), Entry(index.toFloat(), pair.second.toFloat())) }
    }


}
