package com.example.weatherapp_mobapp.model

data class Forecast (
    val datetime: String,
    val tempmax: Double,
    val tempmin: Double,
    val temp: Double,
    val feelslike: Double,
    val humidity: Double,
    val precipprob: Double,
    val windspeed: Double,
    val conditions: String,
    val icon: String
)