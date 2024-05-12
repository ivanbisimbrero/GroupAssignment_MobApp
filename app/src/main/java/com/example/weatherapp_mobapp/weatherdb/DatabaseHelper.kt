package com.example.weatherapp_mobapp.weatherdb

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.weatherapp_mobapp.model.Forecast

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "WeatherDatabase"
        private const val TABLE_FORECASTS = "forecasts"
        private const val KEY_ID = "id"
        private const val KEY_CITY = "city"
        private const val KEY_DATE = "date"
        private const val KEY_TEMPMAX = "tempmax"
        private const val KEY_TEMPMIN = "tempmin"
        private const val KEY_TEMP = "temp"
        private const val KEY_FEELSLIKE = "feelslike"
        private const val KEY_HUMIDITY = "humidity"
        private const val KEY_PRECIPPROB = "precipprob"
        private const val KEY_WINDSPEED = "windspeed"
        private const val KEY_CONDITIONS = "conditions"
        private const val KEY_ICON = "icon"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_FORECASTS_TABLE = ("CREATE TABLE " + TABLE_FORECASTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CITY + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TEMPMAX + " REAL,"
                + KEY_TEMPMIN + " REAL,"
                + KEY_TEMP + " REAL,"
                + KEY_FEELSLIKE + " REAL,"
                + KEY_HUMIDITY + " REAL,"
                + KEY_PRECIPPROB + " REAL,"
                + KEY_WINDSPEED + " REAL,"
                + KEY_CONDITIONS + " TEXT,"
                + KEY_ICON + " TEXT,"
                + "UNIQUE(" + KEY_CITY + ", " + KEY_DATE + "))")
        db.execSQL(CREATE_FORECASTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FORECASTS")
        onCreate(db)
    }

    fun addOrUpdateForecast(forecast: Forecast, cityName: String) {
        val db = this.writableDatabase

        // Remove any entry that matchs with the city and the date, to update the values
        db.delete(TABLE_FORECASTS, "$KEY_CITY = ? AND $KEY_DATE = ?", arrayOf(cityName, forecast.datetime))
        db.close()

        // Now, we insert the new values
        addForecast(forecast, cityName)
    }

    fun addForecast(forecast: Forecast, cityName: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_CITY, cityName)
        values.put(KEY_DATE, forecast.datetime)
        values.put(KEY_TEMPMAX, forecast.tempmax)
        values.put(KEY_TEMPMIN, forecast.tempmin)
        values.put(KEY_TEMP, forecast.temp)
        values.put(KEY_FEELSLIKE, forecast.feelslike)
        values.put(KEY_HUMIDITY, forecast.humidity)
        values.put(KEY_PRECIPPROB, forecast.precipprob)
        values.put(KEY_WINDSPEED, forecast.windspeed)
        values.put(KEY_CONDITIONS, forecast.conditions)
        values.put(KEY_ICON, forecast.icon)

        db.insertWithOnConflict(TABLE_FORECASTS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun getAllCityForecasts(cityName: String) : List<Forecast> {
        val forecastList = ArrayList<Forecast>()
        val selectQuery = "SELECT * FROM $TABLE_FORECASTS WHERE $KEY_CITY = '$cityName'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val forecast = Forecast(
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4),
                    cursor.getDouble(5),
                    cursor.getDouble(6),
                    cursor.getDouble(7),
                    cursor.getDouble(8),
                    cursor.getDouble(9),
                    cursor.getString(10),
                    cursor.getString(11)
                )
                forecastList.add(forecast)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return forecastList
    }
}

