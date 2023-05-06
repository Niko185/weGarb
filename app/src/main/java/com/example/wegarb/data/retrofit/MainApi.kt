package com.example.wegarb.data.retrofit

import com.example.wegarb.domain.models.main.search_request.weather_search_request.SearchWeatherForecast
import com.example.wegarb.domain.models.main.coordinate_request.cityname_request.CityName
import com.example.wegarb.domain.models.main.coordinate_request.weather_request.MainWeatherForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "f054c52de0a9f5d1e50b480bdd0aee4f"
interface MainApi {

    @GET("data/3.0/onecall?units=metric&exclude=&appid=$API_KEY")
    suspend fun getWeatherForecast(@Query("lat") latitude: Double,
                                   @Query("lon") longitude: Double
    ): Response<MainWeatherForecast>

    @GET("geo/1.0/reverse")
    suspend fun getCityName(@Query("lat") lat: Double,
                            @Query("lon") lon: Double,
                            @Query("limit") limit: Int = 1,
                            @Query("appid") appid: String = API_KEY
    ): List<CityName>





    @GET("data/2.5/weather")
    suspend fun getWeatherForecastSearching(@Query("q") cityName: String,
                                            @Query("appid") appid: String = API_KEY
    ) : Response<SearchWeatherForecast>


}