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

    fun last15MinTemperatures(): List<Entry> {
        return forecasts
            .groupBy { it.datetime }
            .mapValues { (_, values) -> values.minOf { it.tempmin } }
            .toList()
            .sortedBy { it.first } // Asume que las fechas están en formato adecuado
            .takeLast(15) // Tomar solo los últimos 15 registros
            .mapIndexed { index, pair -> Entry(index.toFloat(), pair.second.toFloat()) }
    }

    fun last15MaxTemperatures(): List<Entry> {
        return forecasts
            .groupBy { it.datetime }
            .mapValues { (_, values) -> values.maxOf { it.tempmax } }
            .toList()
            .sortedBy { it.first } // Asume que las fechas están en formato adecuado
            .takeLast(15) // Tomar solo los últimos 15 registros
            .mapIndexed { index, pair -> Entry(index.toFloat(), pair.second.toFloat()) }
    }
}
