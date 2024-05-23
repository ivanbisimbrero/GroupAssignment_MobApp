package com.example.weatherapp_mobapp.model

data class Comment(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val message: String = "",
    val hour: String = "",
    var isCurrentUser: Boolean = false
)
