package com.example.weatherapp_mobapp

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapp_mobapp.databinding.ActivityA6HistoricalDataBinding
import com.example.weatherapp_mobapp.utils.HistoricalUtils
import com.example.weatherapp_mobapp.weatherdb.DatabaseHelper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.example.weatherapp_mobapp.formatter.DateAxisValueFormatter

class A6HistoricalData : AppCompatActivity() {

    private val view by lazy { ActivityA6HistoricalDataBinding.inflate(layoutInflater) }
    private var dbHandler: DatabaseHelper = DatabaseHelper(this)
    private lateinit var historicalUtils: HistoricalUtils
    private lateinit var cityName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        cityName = intent.getStringExtra("cityName")!!
        historicalUtils = HistoricalUtils(dbHandler.getAllCityPastForecasts(cityName))
        setupHistoricalValues()
        setupCharts()
    }

    private fun setupHistoricalValues() {
        view.tvHistoricCityName.text = cityName
        view.tvAvgTemperatureValue.text = String.format("%.1f", historicalUtils.averageTemperature()).plus("º")
        view.tvSunnyDaysValue.text = historicalUtils.countSunnyDays().toString()
        view.tvMaxWindSpeedValue.text = historicalUtils.maxWindSpeed().toString().plus(" km/h")
        view.tvMinWindSpeedValue.text = historicalUtils.minWindSpeed().toString().plus(" km/h")
        view.tvAvgPrecipProbValue.text = String.format("%.1f", historicalUtils.averagePrecipitationProbability()).plus("%")
    }

    private fun setupCharts() {
        val chartMinTemp = findViewById<LineChart>(R.id.chartMinTemp)
        val chartMaxTemp = findViewById<LineChart>(R.id.chartMaxTemp)
        val boldTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val datesMin = historicalUtils.past15MinTemperatures().map { it.first }
        val datesMax = historicalUtils.past15MaxTemperatures().map { it.first }
        val entriesMin = historicalUtils.past15MinTemperatures().map { it.second }
        val entriesMax = historicalUtils.past15MaxTemperatures().map { it.second }

        val xAxisMin = chartMinTemp.xAxis
        xAxisMin.valueFormatter = DateAxisValueFormatter(datesMin)
        xAxisMin.granularity = 1f
        val xAxisMax = chartMaxTemp.xAxis
        xAxisMax.valueFormatter = DateAxisValueFormatter(datesMax)
        xAxisMax.granularity = 1f

        //For example, because both entries lists are going to have the same size
        view.tvMaxMinChartsTxt.text = "Max and Min Past " + entriesMax.size + " Days Temperature:"

        // Configure the dataset for the minimum temperature
        val dataSetMin = LineDataSet(entriesMin, "Min Temperature")
        dataSetMin.color = Color.BLUE
        dataSetMin.valueTextSize = 10f // Adjust the size of the labels text
        dataSetMin.valueTypeface = boldTypeface
        dataSetMin.setDrawValues(true) // Ensure that all values are drawn in the graphic
        val lineDataMin = LineData(dataSetMin)
        chartMinTemp.data = lineDataMin
        chartMinTemp.description.text = "Daily Minimum Temperatures"

        // Configure the dataset for the maximum temperature
        val dataSetMax = LineDataSet(entriesMax, "Max Temperature")
        dataSetMax.color = Color.RED
        dataSetMax.valueTextSize = 10f
        dataSetMax.valueTypeface = boldTypeface
        dataSetMax.setDrawValues(true)
        val lineDataMax = LineData(dataSetMax)
        chartMaxTemp.data = lineDataMax
        chartMaxTemp.description.text = "Daily Maximum Temperatures"

        // Refresh the graphics
        chartMinTemp.invalidate()
        chartMaxTemp.invalidate()
    }


}