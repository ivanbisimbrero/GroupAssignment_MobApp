package com.example.weatherapp_mobapp.requestCity

interface WeatherRequest {
    fun getURL(): String
    suspend fun getName(): String
}