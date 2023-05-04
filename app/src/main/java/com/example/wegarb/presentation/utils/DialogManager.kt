package com.example.wegarb.presentation.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.wegarb.domain.models.old.AdditionalWeatherForecast
import com.example.wegarb.domain.models.old.WardrobeElement
import com.example.wegarb.databinding.DialogClothBinding
import com.example.wegarb.databinding.DialogHeadBinding
import com.example.wegarb.databinding.DialogSaveBinding
import com.example.wegarb.domain.models.show.CurrentWeather

object DialogManager {
    fun showClothDialog(context: Context, wardrobeElement: WardrobeElement) {
        val builder = AlertDialog.Builder(context)
        val binding = DialogClothBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialog = builder.create()

        binding.apply {
            imageCloth.setImageResource(wardrobeElement.image)
            tvNameCloth.text = wardrobeElement.name
            tvDescriptionCloth.text = getDescriptionCloth(context, wardrobeElement)
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    private fun getDescriptionCloth(context: Context, wardrobeElement: WardrobeElement): String {
        val builder = AlertDialog.Builder(context)
        val binding = DialogClothBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)


        when (wardrobeElement.name) {
            "Beanie" -> binding.tvDescriptionCloth.text = "Beanie cloth"
            "Cap" -> binding.tvDescriptionCloth.text = "Cap cloth"
            "Gloves" -> binding.tvDescriptionCloth.text = "Gloves cloth"
            "Hoodie" -> binding.tvDescriptionCloth.text = "Hoodie cloth"
            "Jacket" -> binding.tvDescriptionCloth.text = "Jacket cloth"
            "Jeans" -> binding.tvDescriptionCloth.text = "Jeans cloth"
            "Mittens" -> binding.tvDescriptionCloth.text = "Mittens cloth"
            "Raincoat" -> binding.tvDescriptionCloth.text = "Raincoat cloth"
            "Shorts" -> binding.tvDescriptionCloth.text = "Shorts cloth"
            "Sunglasses" -> binding.tvDescriptionCloth.text = "Sunglasses cloth"
            "Thermal kit" -> binding.tvDescriptionCloth.text = "Thermal kit cloth"
            "Tight sweater" -> binding.tvDescriptionCloth.text = "Tight sweater cloth"
            "Tight windbreaker" -> binding.tvDescriptionCloth.text = "Tight windbreaker cloth"
            "T-shirt" -> binding.tvDescriptionCloth.text = "T-shirt cloth"
            "Turtleneck" -> binding.tvDescriptionCloth.text = "Turtleneck cloth"
            "Umbrella" -> binding.tvDescriptionCloth.text = "Umbrella cloth"
            "Windbreaker" -> binding.tvDescriptionCloth.text = "Windbreaker cloth"
            "Winter scarf" -> binding.tvDescriptionCloth.text = "Winter scarf cloth"
            "Balaclava" -> binding.tvDescriptionCloth.text = "Balaclava cloth"
            "Bomber" -> binding.tvDescriptionCloth.text = "Bomber cloth"
            "Denim jacket" -> binding.tvDescriptionCloth.text = "Denim jacket cloth"
            "Fleece jacket" -> binding.tvDescriptionCloth.text = "Fleece jacket cloth"
            "Light beanie" -> binding.tvDescriptionCloth.text = "Light beanie cloth"
            "Long winter jacket" -> binding.tvDescriptionCloth.text = "Long winter jacket cloth"
            "Neck gaiter" -> binding.tvDescriptionCloth.text = "Neck gaiter cloth"
            "Oversize t-shirt" -> binding.tvDescriptionCloth.text = "Oversize t-shirt cloth"
            "Winter jacket" -> binding.tvDescriptionCloth.text = "Winter jacket cloth"
            "Rain boots" -> binding.tvDescriptionCloth.text = "Rain boots cloth"
            "Sandals" -> binding.tvDescriptionCloth.text = "Sandals cloth"
            "Sneakers" -> binding.tvDescriptionCloth.text = "Sneakers cloth"
            "Show boots" -> binding.tvDescriptionCloth.text = "Show boots cloth"
            "Snow pants" -> binding.tvDescriptionCloth.text = "Snow pants cloth"
            "Light pants" -> binding.tvDescriptionCloth.text = "Light pants cloth"
            "Sunscreen" -> binding.tvDescriptionCloth.text = "Sunscreen cloth"
            "Long snow boots" -> binding.tvDescriptionCloth.text = "Long snow boots cloth"
            "Super winter coat" -> binding.tvDescriptionCloth.text = "Super winter coat cloth"
            "Thermal socks" -> binding.tvDescriptionCloth.text = "Thermal socks cloth"
            "Thermos" -> binding.tvDescriptionCloth.text = "Thermos cloth"
            "Water bottle" -> binding.tvDescriptionCloth.text = "Water bottle cloth"
            "White summer hat" -> binding.tvDescriptionCloth.text = "White summer hat cloth"
            "Light windbreaker" -> binding.tvDescriptionCloth.text = "Light windbreaker cloth"
            "Winter ointment" -> binding.tvDescriptionCloth.text = "Winter ointment cloth"
            "Rainshoes" -> binding.tvDescriptionCloth.text = "Rainshoes cloth"
        }
        return binding.tvDescriptionCloth.text.toString()
    }




     fun showSaveDialog(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val binding = DialogSaveBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialog = builder.create()

        binding.bPositiveOkey.setOnClickListener {
            listener.onClickComfort()
            dialog.dismiss()
            }

       binding.bNegativeSaveCold.setOnClickListener {
           listener.onClickCold()
           dialog.dismiss()
       }

        binding.bNegativeSaveHot.setOnClickListener {
            listener.onClickHot()
            dialog.dismiss()
             }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }


    fun showHeadDialog(context: Context, currentWeather: CurrentWeather) {

        val builder = AlertDialog.Builder(context)
        val binding = DialogHeadBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialog = builder.create()

        val fLike =  currentWeather.feltTemperature
        val cTemperature =  currentWeather.temperature

            binding.cTemp.text = cTemperature.toString()
            binding.feellsLike.text = fLike
            binding.wind.text = currentWeather.windSpeed
            binding.windVariant.text = currentWeather.windDirection
            binding.humidity.text = currentWeather.humidity

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
















    interface Listener {

        fun onClickComfort()
        fun onClickCold()
        fun onClickHot()
    }

}
