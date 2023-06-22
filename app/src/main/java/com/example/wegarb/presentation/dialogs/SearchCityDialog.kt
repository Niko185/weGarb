package com.example.wegarb.presentation.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.wegarb.databinding.DialogCityBinding


object SearchCityDialog {

        fun start(context: Context, handlerRequest: HandlerRequest){
            val builder = AlertDialog.Builder(context)
            val binding = DialogCityBinding.inflate(LayoutInflater.from(context), null, false)
            builder.setView(binding.root)
            val dialog = builder.create()

           binding.bPositive.setOnClickListener {
                handlerRequest.getWeatherForCityRealization(binding.edCityName.text.toString())
                dialog.dismiss()
            }

            binding.bNegative.setOnClickListener {
                     dialog.dismiss()
            }

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        interface HandlerRequest {
            fun getWeatherForCityRealization(cityName: String?)
        }
}
