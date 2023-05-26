package com.example.wegarb.presentation.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.wegarb.databinding.DialogHeadBinding
import com.example.wegarb.domain.models.weather.LocationWeather
import com.example.wegarb.domain.models.weather.Weather

object AdditionalWeatherDialog {

    @SuppressLint("SetTextI18n")
    fun start(context: Context, weather: Weather) {

        val builder = AlertDialog.Builder(context)
        val binding = DialogHeadBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialog = builder.create()


            binding.cTemp.text = "Current temperature: ${weather.temperature}°C"
            binding.feellsLike.text = "Felt temperature: ${weather.feltTemperature}°C"
            binding.wind.text ="Wind speed: ${weather.windSpeed} m/c"
            binding.windVariant.text = getWindDirection(weather.windDirection.toInt())
            binding.humidity.text = "Humidity: ${weather.humidity}%"

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

     fun getWindDirection(windDirection: Int): String {
        val statusWind: String?
        if(windDirection in 349 ..361 || windDirection in 0 .. 11 ) {
            statusWind = "North"
        } else if(windDirection in 12 .. 56) {
            statusWind = "North/East"
        } else if(windDirection in 57 .. 123) {
            statusWind = "East"
        } else if(windDirection in 124 .. 168) {
            statusWind = "South/East"
        } else if(windDirection in 169 .. 213) {
            statusWind = "South"
        } else if(windDirection in 214 .. 258) {
            statusWind = "South/West"
        } else if(windDirection in 259 .. 303) {
            statusWind = "West"
        } else if(windDirection in 304 .. 348){
            statusWind = "North/West"
        } else statusWind = "Sorry, wind direction not found"
        return statusWind.toString()
    }
}
