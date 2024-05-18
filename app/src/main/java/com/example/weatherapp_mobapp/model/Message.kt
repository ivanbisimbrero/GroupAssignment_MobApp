package com.example.weatherapp_mobapp.model

data class Message(
    val username: String,
    val email: String,
    val message: String,
    val hour: String,
    val isCurrentUser: Boolean
)

