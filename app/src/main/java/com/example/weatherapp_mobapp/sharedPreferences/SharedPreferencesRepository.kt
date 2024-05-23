package com.example.weatherapp_mobapp.sharedPreferences

import android.content.SharedPreferences

const val SHARED_PREFERENCES_NAME = "PREFERENCES"
const val SHARED_PREFERENCES_KEY = "FAVOURITES"
const val SHARED_PREFERENCES_KEY_USER = "USER"

class SharedPreferencesRepository(private val sharedPreferences: SharedPreferences,
                                  private val key: String) : CrudAPI {

    override fun save(value: String) {
        val values = sharedPreferences.getStringSet(key, mutableSetOf())!!
        with(sharedPreferences.edit()) {
            putStringSet(key, values.plus(value))
            apply()
        }
    }

    override fun delete(value: String) {
        val values = sharedPreferences.getStringSet(key, mutableSetOf())
        with(sharedPreferences.edit()) {
            putStringSet(key, values!!.minus(value))
            apply()
        }
    }

    override fun list(): Set<String> {
        return sharedPreferences.getStringSet(key, mutableSetOf())!!
    }

    override fun contains(value: String): Boolean {
        return list().contains(value)
    }

}