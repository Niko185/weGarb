package com.example.wegarb.data.weather


import com.example.wegarb.data.weather.remote.api.WeatherApi
import com.example.wegarb.data.weather.remote.dto.location.LocationWeatherMapper
import com.example.wegarb.data.weather.remote.dto.location.city.LocationCityNameMapper
import com.example.wegarb.data.weather.remote.dto.search.SearchWeatherMapper
import com.example.wegarb.domain.WeatherRepository
import com.example.wegarb.domain.models.LocationCityNameInfo
import com.example.wegarb.domain.models.LocationWeatherInfo
import com.example.wegarb.domain.models.SearchWeatherInfo
import java.lang.Exception

class WeatherRepositoryImpl(private val weatherApi: WeatherApi,
                            private val locationWeatherMapper: LocationWeatherMapper,
                            private val locationCityNameMapper: LocationCityNameMapper,
                            private val searchWeatherMapper: SearchWeatherMapper
): WeatherRepository {


    override suspend fun getLocationWeatherForecast(latitude: Double, longitude: Double): Result<LocationWeatherInfo> {
        return try {
            val responseDto = weatherApi.getLocationWeatherForecast(latitude, longitude)
            val responseDtoBody = responseDto.body()

            responseDtoBody?.let {
                val convertResponse = locationWeatherMapper.mapToDomain(it)
                Result.success(convertResponse)
            } ?: Result.failure(NullPointerException("LocationWeatherDto not response"))


        } catch (error: Exception) {
            Result.failure(error)
        }

    }





    override suspend fun getLocationCityName(lat: Double, lon: Double): Result<List<LocationCityNameInfo>> {
        return try {
            val responseDto = weatherApi.getLocationCityName(lat, lon)



            responseDto.map {
                locationCityNameMapper.mapToDomain(it)
            }.takeIf { it.isNotEmpty() }?.let{
                Result.success(it)

            } ?: Result.failure(NullPointerException("LocationCityNameDto not response"))


        } catch (error: Exception) {
            Result.failure(error)
        }

    }





    override suspend fun getSearchWeatherForecast(cityName: String): Result<SearchWeatherInfo> {
        return try {
            val responseDto = weatherApi.getSearchWeatherForecast(cityName)
            val responseDtoBody = responseDto.body()


            responseDtoBody?.let {
                val convertResponse = searchWeatherMapper.mapToDomain(it)
                Result.success(convertResponse)
            } ?: Result.failure(NullPointerException("SearchWeatherDto not response"))


        } catch (error: Exception) {
            Result.failure(error)
        }
    }

}








