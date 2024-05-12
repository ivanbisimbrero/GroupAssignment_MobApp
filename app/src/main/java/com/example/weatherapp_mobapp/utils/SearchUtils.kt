package com.example.weatherapp_mobapp.utils

import com.example.weatherapp_mobapp.model.City

class SearchUtils(private val cities: MutableList<City>) {

    private var currentCitiesList = cities

    fun searchCity(searchName: String): List<City> {
        if (searchName.isEmpty()) {
            currentCitiesList = cities
            return currentCitiesList
        }
        currentCitiesList = cities.filter { it.name.contains(searchName, ignoreCase = true) }.toMutableList()
        return currentCitiesList
    }

    fun getCities(): MutableList<City> {
        return currentCitiesList
    }

}