package com.example.wegarb.data.requests.search


import com.example.wegarb.data.models.SearchWeatherModel
import com.example.wegarb.presentation.vm.MainViewModel
import com.example.wegarb.utils.formatterUnix
import org.json.JSONObject


const val API_KEY = "f054c52de0a9f5d1e50b480bdd0aee4f"
class SearchRequest() {


    fun getSearchResponse(response: JSONObject): SearchWeatherModel {

        val weather = response.getJSONArray("weather").getJSONObject(0)

        val coordinates = response.getJSONObject("coord")
        val latitude = coordinates.getDouble("lat")
        val longitude = coordinates.getDouble("lon")
        val currentCoordinateHead = "$latitude / $longitude"


        val currentDataHeadUnix = response.getLong("dt").toString()
        val currentDataHead = formatterUnix(currentDataHeadUnix)

        val currentWindHead = response.getJSONObject("wind").getDouble("speed").toInt()
        val currentConditionHead = weather.getString("description")
        val currentCityNameHead = response.getString("name")

        val temperatureInKelvin = response.getJSONObject("main").getDouble("temp")
        val currentTemperatureHead = temperatureInKelvin - 273.15 // convert to Celsius

        val searchModelHead = SearchWeatherModel(
            currentDataHead,
            currentTemperatureHead.toInt(),
            currentWindHead.toString(),
            currentConditionHead,
            currentCityNameHead,
            currentCoordinateHead
        )
        return searchModelHead
    }


}