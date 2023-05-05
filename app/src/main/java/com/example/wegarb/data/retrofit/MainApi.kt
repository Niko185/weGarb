package com.example.wegarb.data.retrofit

import com.example.wegarb.domain.models.newvariant.searching.response.WeatherApiResponse
import com.example.wegarb.domain.models.newvariant.weather.cityname.CityName
import com.example.wegarb.domain.models.newvariant.weather.main.MainWeatherForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "f054c52de0a9f5d1e50b480bdd0aee4f"
//"https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$API_KEY"
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
    ) : Response<WeatherApiResponse>


}