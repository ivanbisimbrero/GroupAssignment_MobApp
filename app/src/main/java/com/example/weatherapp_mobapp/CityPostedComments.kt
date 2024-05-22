package com.example.weatherapp_mobapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp_mobapp.databinding.ActivityA5CityDetailBinding
import com.example.weatherapp_mobapp.databinding.ActivityCityPostedCommentsBinding

class CityPostedComments : AppCompatActivity() {
    private val view by lazy { ActivityCityPostedCommentsBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
    }
}