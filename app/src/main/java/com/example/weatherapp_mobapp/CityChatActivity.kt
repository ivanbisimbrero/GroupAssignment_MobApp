package com.example.weatherapp_mobapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp_mobapp.databinding.ActivityCityChatBinding

class CityChatActivity : AppCompatActivity() {
    private val view by lazy { ActivityCityChatBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

    }
}