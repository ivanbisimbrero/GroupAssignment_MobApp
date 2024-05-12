package com.example.weatherapp_mobapp.model

data class City (
    var name: String,
    val latitude: Double,
    val longitude: Double,
    val resolvedAddress: String,
    val days: MutableList<Forecast>,
    var isFavouriteCity: Boolean
)
