package com.example.weatherapp_mobapp.model

data class Message(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val message: String = "",
    val hour: String = "",
    var isCurrentUser: Boolean = false,
    val isImage: Boolean = false
)

