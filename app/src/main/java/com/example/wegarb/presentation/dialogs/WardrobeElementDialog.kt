package com.example.wegarb.presentation.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.wegarb.databinding.DialogClothBinding
import com.example.wegarb.domain.models.cloth.single_wardrobe_element.WardrobeElement

object WardrobeElementDialog {

    fun start(context: Context, wardrobeElement: WardrobeElement) {
        val builder = AlertDialog.Builder(context)
        val binding = DialogClothBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialog = builder.create()

        binding.apply {
            imageCloth.setImageResource(wardrobeElement.image)
            tvNameCloth.text = wardrobeElement.name
            tvDescriptionCloth.text = getDescription(context)
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun getDescription(context: Context): String {
        val builder = AlertDialog.Builder(context)
        val binding = DialogClothBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        binding.tvDescriptionCloth.text = "Okay, great!"
        return binding.tvDescriptionCloth.text.toString()
    }
}