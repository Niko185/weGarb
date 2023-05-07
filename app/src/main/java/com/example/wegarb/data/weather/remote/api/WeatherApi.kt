package com.example.wegarb.data.weather.remote.api

import com.example.wegarb.data.weather.remote.dto.search.SearchWeatherDto
import com.example.wegarb.data.weather.remote.dto.location.city.LocationCityNameDto
import com.example.wegarb.data.weather.remote.dto.location.LocationWeatherDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "f054c52de0a9f5d1e50b480bdd0aee4f"
interface WeatherApi {

    @GET("data/3.0/onecall?units=metric&exclude=&appid=$API_KEY")
    suspend fun getLocationWeatherForecast(@Query("lat") latitude: Double,
                                           @Query("lon") longitude: Double
    ): Response<LocationWeatherDto>

    @GET("geo/1.0/reverse")
    suspend fun getLocationCityName(@Query("lat") lat: Double,
                                    @Query("lon") lon: Double,
                                    @Query("limit") limit: Int = 1,
                                    @Query("appid") appid: String = API_KEY
    ): List<LocationCityNameDto>





    @GET("data/2.5/weather")
    suspend fun getSearchWeatherForecast(@Query("q") cityName: String,
                                         @Query("appid") appid: String = API_KEY
    ) : Response<SearchWeatherDto>


}