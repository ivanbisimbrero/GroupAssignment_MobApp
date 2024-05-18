package com.example.weatherapp_mobapp.model

data class User (
    var name: String,
    var email: String,
    val currentCity: City,
    val cities: MutableList<City>,
    var favCities: MutableList<City>
)
