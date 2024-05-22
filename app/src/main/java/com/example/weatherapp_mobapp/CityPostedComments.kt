package com.example.weatherapp_mobapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp_mobapp.databinding.ActivityA5CityDetailBinding
import com.example.weatherapp_mobapp.databinding.ActivityCityPostedCommentsBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class CityPostedComments : AppCompatActivity() {
    private val view by lazy { ActivityCityPostedCommentsBinding.inflate(layoutInflater) }
    private val database = Firebase.database("https://grouptask-mobapp-default-rtdb.europe-west1.firebasedatabase.app/")
    private lateinit var dbReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
    }
}