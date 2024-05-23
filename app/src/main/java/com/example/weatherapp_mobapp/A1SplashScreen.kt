package com.example.weatherapp_mobapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.weatherapp_mobapp.databinding.ActivityA1SplashScreenBinding
import com.example.weatherapp_mobapp.requestCity.CityCoordinatesRequest
import com.example.weatherapp_mobapp.sharedPreferences.CrudAPI
import com.example.weatherapp_mobapp.sharedPreferences.SHARED_PREFERENCES_KEY
import com.example.weatherapp_mobapp.sharedPreferences.SHARED_PREFERENCES_NAME
import com.example.weatherapp_mobapp.sharedPreferences.SharedPreferencesRepository
import com.example.weatherapp_mobapp.utils.DataUtils
import com.example.weatherapp_mobapp.weatherdb.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class A1SplashScreen : AppCompatActivity() {

    private val view by lazy { ActivityA1SplashScreenBinding.inflate(layoutInflater) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val repository: CrudAPI by lazy {
        SharedPreferencesRepository(
            application.getSharedPreferences(
                SHARED_PREFERENCES_NAME,
                MODE_PRIVATE
            ), SHARED_PREFERENCES_KEY
        )
    }
    private var dbHandler : DatabaseHelper = DatabaseHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }

        val scope = CoroutineScope(Dispatchers.IO)
        val jobs = mutableListOf<Job>()

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location->
            val coordRequest = CityCoordinatesRequest(location.latitude, location.longitude)

            val locationJob = scope.launch {
                val apiResponse = DataUtils.fetchWeather(coordRequest)
                val cityName = coordRequest.getName()
                DataUtils.fillCurrentCity(apiResponse)
                DataUtils.setCurrentCityName(cityName)

                // Load default cities
                val defaultCityJobs = DataUtils.defaultRequests.map { defaultRequest ->
                    launch {
                        val response = DataUtils.fetchWeather(defaultRequest)
                        DataUtils.fillCities(defaultRequest, response)
                    }
                }
                // Wait until all default cities are loaded
                defaultCityJobs.joinAll()

                // Initialise main user
                DataUtils.initUser()

                //Load Favourites cities
                DataUtils.loadFavoriteCities(repository)

                //Load in the database actual and future predictions only
                DataUtils.addUserForecastsToDatabase(dbHandler)

                // Proceed with the main activity
                withContext(Dispatchers.Main) {
                    startActivity(Intent(this@A1SplashScreen, A2MainActivity::class.java))
                    finish()
                }
            }
            jobs.add(locationJob)
        }

        scope.launch {
            // Wait until the location task is done
            jobs.joinAll()
        }
    }
}