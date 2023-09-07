package com.example.wegarb.presentation.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.wegarb.R
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
            tvDescriptionCloth.text = getDescription(context, wardrobeElement)
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun getDescription(context: Context, wardrobeElement: WardrobeElement): String {
        val builder = AlertDialog.Builder(context)
        val binding = DialogClothBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        when(wardrobeElement.image) {
            R.drawable.garb_balaclava -> binding.tvDescriptionCloth.text = "It looks like will not be superfluous."
            R.drawable.garb_beanie -> binding.tvDescriptionCloth.text = "To be warm and in a good mood."
            R.drawable.garb_bomber -> binding.tvDescriptionCloth.text = "A cape won't hurt."
            R.drawable.garb_cap -> binding.tvDescriptionCloth.text = "Classic cap, why not?"
            R.drawable.garb_denim_jacket -> binding.tvDescriptionCloth.text = "A denim jacket or light cape will come in handy."
            R.drawable.garb_fleece -> binding.tvDescriptionCloth.text = "A reliable option."
            R.drawable.garb_gloves -> binding.tvDescriptionCloth.text = "Why be a little cold?"
            R.drawable.garb_hoodie -> binding.tvDescriptionCloth.text = "Hoodie will not let you freeze."
            R.drawable.garb_jacket -> binding.tvDescriptionCloth.text = "It looks like it's needed today."
            R.drawable.garb_jeans -> binding.tvDescriptionCloth.text = "Classic jeans are now a good choice."
            R.drawable.garb_light_beanie -> binding.tvDescriptionCloth.text = "Maybe it's time to put this on."
            R.drawable.garb_light_windbreaker -> binding.tvDescriptionCloth.text = "Wind...Wind...Wind..."
            R.drawable.garb_long_winter_jacket -> binding.tvDescriptionCloth.text = "Is it possible to play snowballs?"
            R.drawable.garb_mittens -> binding.tvDescriptionCloth.text = "Nice and warm."
            R.drawable.garb_neck_gaiter -> binding.tvDescriptionCloth.text = "Simple but effective variant."
            R.drawable.garb_oversize_tie_dye -> binding.tvDescriptionCloth.text = "Freedom"
            R.drawable.garb_puffer_coat -> binding.tvDescriptionCloth.text = "A classic option for a cold weather."
            R.drawable.garb_rain_boots -> binding.tvDescriptionCloth.text = "As a rule, they will not let you down."
            R.drawable.garb_raincoat -> binding.tvDescriptionCloth.text = "You can enjoy the rain in this."
            R.drawable.garb_rainshoes -> binding.tvDescriptionCloth.text = "Stay dry."
            R.drawable.garb_sandals -> binding.tvDescriptionCloth.text = "Light sneakers are also suitable."
            R.drawable.garb_shorts -> binding.tvDescriptionCloth.text = "Base and comments are not needed."
            R.drawable.garb_sneakers -> binding.tvDescriptionCloth.text = "The choice is yours."
            R.drawable.garb_snow_boot -> binding.tvDescriptionCloth.text = "Will there be a crunch of snow?"
            R.drawable.garb_snow_pants -> binding.tvDescriptionCloth.text = "It's better not to freeze today"
            R.drawable.garb_summer_pants -> binding.tvDescriptionCloth.text = "Maybe comfortable variant."
            R.drawable.garb_sunglasses -> binding.tvDescriptionCloth.text = "If it suits you."
            R.drawable.garb_sunscreen -> binding.tvDescriptionCloth.text = "Yes or no?"
            R.drawable.garb_super_show_boots -> binding.tvDescriptionCloth.text = "Oh wow."
            R.drawable.garb_super_winter_coat -> binding.tvDescriptionCloth.text = "Where are you?"
            R.drawable.garb_thermal_kit -> binding.tvDescriptionCloth.text = "Base for cold weather."
            R.drawable.garb_thermo_socks -> binding.tvDescriptionCloth.text = "Socks can be cute."
            R.drawable.garb_thermos -> binding.tvDescriptionCloth.text = "It will help to warm up and relax a little."
            R.drawable.garb_tight_sweater -> binding.tvDescriptionCloth.text = "It will warm you up and give you a little cute comfort."
            R.drawable.garb_tight_windbreaker -> binding.tvDescriptionCloth.text = "Looks like it's windy today."
            R.drawable.garb_tshirt -> binding.tvDescriptionCloth.text = "How can we do without this?"
            R.drawable.garb_turtleneck -> binding.tvDescriptionCloth.text = "Strictly and warmly or not?"
            R.drawable.garb_umbrella -> binding.tvDescriptionCloth.text = "Perhaps it will not be superfluous today."
            R.drawable.garb_water_bottle -> binding.tvDescriptionCloth.text = "To stay in good tone."
            R.drawable.garb_white_summer_hat -> binding.tvDescriptionCloth.text = "Oh, Really hot to day?"
            R.drawable.garb_windbreaker -> binding.tvDescriptionCloth.text = "Wind...Wind...Wind..."
            R.drawable.garb_winter_ointment -> binding.tvDescriptionCloth.text = "Brrrr...Really cold to day?"
            R.drawable.garb_winter_scarf -> binding.tvDescriptionCloth.text = "It will complement the image and warm you up in the cold."
            else -> binding.tvDescriptionCloth.text = "Oh, for some reason there is no description here :("
        }
        return binding.tvDescriptionCloth.text.toString()
    }
}