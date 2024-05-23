package com.example.weatherapp_mobapp.utils

import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_mobapp.model.City
import com.example.weatherapp_mobapp.model.DefaultCities
import com.example.weatherapp_mobapp.model.Forecast
import com.example.weatherapp_mobapp.model.User
import com.example.weatherapp_mobapp.requestCity.CityNameRequest
import com.example.weatherapp_mobapp.requestCity.WeatherRequest
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import com.example.weatherapp_mobapp.sharedPreferences.CrudAPI
import com.example.weatherapp_mobapp.sharedPreferences.SHARED_PREFERENCES_NAME
import com.example.weatherapp_mobapp.sharedPreferences.SharedPreferencesRepository
import com.example.weatherapp_mobapp.weatherdb.DatabaseHelper
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

class DataUtils {

    companion object {
        //const val API_KEY = "UGGQU4XS3QHQ4VC3KSR2JSL3G"
        const val API_KEY = "A58D6UR5PXC67DAHDQNMJJVGD"
        //const val API_KEY = "LRH7D4ZHU7LAWANGZBRQXPPGU" //Change if necessary
        const val REVERSE_API_KEY = "73ae2d27d6034e278eaae0007c703f28"

        suspend fun fetchWeather(request: WeatherRequest): String {
            val client = HttpClient()
            val response: HttpResponse = client.get(request.getURL())
            val responseText: String = response.bodyAsText()
            client.close()
            return responseText
        }

        // Function to deserialize the JSON string into a City object
        private fun parseWeatherData(jsonResponse: String): City {
            // Parse the JSON response
            val gson = Gson()
            return gson.fromJson(jsonResponse, City::class.java)
        }

        var defaultRequests: List<WeatherRequest> = listOf(
            CityNameRequest(DefaultCities.Madrid),
            CityNameRequest(DefaultCities.Barcelona),
            CityNameRequest(DefaultCities.Zaragoza),
            CityNameRequest(DefaultCities.Bucharest),
            CityNameRequest(DefaultCities.Paris),
            CityNameRequest(DefaultCities.NewYork),
            CityNameRequest(DefaultCities.Berlin),
            CityNameRequest(DefaultCities.Tokyo),
            CityNameRequest(DefaultCities.Sydney),
            CityNameRequest(DefaultCities.RioDeJaneiro)
        )

        private lateinit var currentCity: City
        private lateinit var cities: MutableList<City>
        lateinit var mainUser: User
        private var citiesMap = ConcurrentHashMap<WeatherRequest, City>() //To avoid race conditions (Thanks teacher :))

        fun fillCurrentCity(dataFromAPI: String) {
            currentCity = parseWeatherData(dataFromAPI)
        }

        fun setCurrentCityName(name: String) {
            currentCity.name = name
        }

        suspend fun fillCities(request: WeatherRequest, dataFromAPI: String) {
            var auxCity: City = parseWeatherData(dataFromAPI)
            auxCity.name = request.getName()
            auxCity.isFavouriteCity = false
            citiesMap[request] = auxCity
        }

        fun initUser(repository: CrudAPI) {
            cities = citiesMap.values.toMutableList()
            val listUsers = repository.list().toList()
            if(listUsers.isNotEmpty()) {
                mainUser = User(listUsers[0], listUsers[1], currentCity, cities, mutableListOf())
            } else {
                mainUser = User("", "", currentCity, cities, mutableListOf())
            }
            println(mainUser)
        }

        fun loadFavoriteCities(crudApi: CrudAPI) {
            val favoriteCityNames = crudApi.list()
            mainUser.favCities.clear()
            mainUser.cities.forEach { city ->
                if (favoriteCityNames.contains(city.name)) {
                    city.isFavouriteCity = true
                    mainUser.favCities.add(city)
                } else {
                    city.isFavouriteCity = false
                }
            }
        }

        fun addUserForecastsToDatabase(dbHandler: DatabaseHelper) {
            mainUser.cities.forEach { city ->
                addFutureForecasts(city.days, city.name, dbHandler)
            }
        }
        private fun addFutureForecasts(forecasts: List<Forecast>, cityName: String, dbHandler: DatabaseHelper) {
            val currentDate = LocalDate.now()
            val futureForecasts = forecasts.filter { LocalDate.parse(it.datetime) >= currentDate }
            futureForecasts.forEach {
                dbHandler.addOrUpdateForecast(it, cityName)
            }
        }
    }
}