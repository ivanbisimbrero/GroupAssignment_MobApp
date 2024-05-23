package com.example.weatherapp_mobapp.model

data class Comment(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val content: String = "",
    val timestamp: String = "",
    var isCurrentUser: Boolean = false
)
