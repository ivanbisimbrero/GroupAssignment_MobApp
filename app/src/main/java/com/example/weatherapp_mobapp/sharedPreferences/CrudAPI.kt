package com.example.weatherapp_mobapp.sharedPreferences

interface CrudAPI {
    fun save(value: String)

    fun delete(value: String)

    fun list(): Set<String>

    fun contains(value: String): Boolean

    fun parse(): List<String>
}