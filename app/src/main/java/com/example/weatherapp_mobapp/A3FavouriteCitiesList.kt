package com.example.weatherapp_mobapp

import android.view.View
import com.example.weatherapp_mobapp.databinding.ActivityA3FavouriteCitiesListBinding
import com.example.weatherapp_mobapp.utils.DataUtils
import com.example.weatherapp_mobapp.utils.SearchUtils

class A3FavouriteCitiesList : BaseCityListActivity() {

    private val view by lazy { ActivityA3FavouriteCitiesListBinding.inflate(layoutInflater) }
    override val cityUtils = SearchUtils(DataUtils.mainUser.favCities)

    override fun provideLayout(): View = view.root
    override fun provideListViewId(): Int = R.id.lvCities
    override fun provideSearchViewId(): Int = R.id.svSearchCity
    override fun provideRadioGroupId(): Int = R.id.rbgFilters
}