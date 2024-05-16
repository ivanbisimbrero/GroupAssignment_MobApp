package com.example.weatherapp_mobapp.formatter

import com.github.mikephil.charting.formatter.ValueFormatter

class DateAxisValueFormatter(private val dates: List<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return dates[value.toInt()]
    }
}