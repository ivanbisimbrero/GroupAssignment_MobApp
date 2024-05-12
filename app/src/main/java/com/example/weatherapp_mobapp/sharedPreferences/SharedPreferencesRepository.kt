package com.example.weatherapp_mobapp.sharedPreferences

import android.content.SharedPreferences

const val SHARED_PREFERENCES_NAME = "PREFERENCES"
const val SHARED_PREFERENCES_KEY = "FAVOURITES"

class SharedPreferencesRepository(private val sharedPreferences: SharedPreferences) :
    CrudAPI {

    override fun save(value: String) {
        val values = sharedPreferences.getStringSet(SHARED_PREFERENCES_KEY, mutableSetOf())!!
        with(sharedPreferences.edit()) {
            putStringSet(SHARED_PREFERENCES_KEY, values.plus(value))
            apply()
        }
    }

    override fun delete(value: String) {
        val values = sharedPreferences.getStringSet(SHARED_PREFERENCES_KEY, mutableSetOf())
        with(sharedPreferences.edit()) {
            putStringSet(SHARED_PREFERENCES_KEY, values!!.minus(value))
            apply()
        }
    }

    override fun list(): Set<String> {
        return sharedPreferences.getStringSet(SHARED_PREFERENCES_KEY, mutableSetOf())!!
    }

    override fun contains(value: String): Boolean {
        return list().contains(value)
    }

}