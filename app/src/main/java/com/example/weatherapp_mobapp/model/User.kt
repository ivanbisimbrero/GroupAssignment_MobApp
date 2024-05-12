package com.example.weatherapp_mobapp.model

data class User (
    val name: String,
    val email: String,
    val currentCity: City,
    val cities: MutableList<City>,
    var favCities: MutableList<City>
)
