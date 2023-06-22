package com.example.wegarb.domain.models.cloth.kits

import com.example.wegarb.R
import com.example.wegarb.domain.models.cloth.single_wardrobe_element.WardrobeElement

class BaseClothesKit {

     val kitHardCold = listOf(
        WardrobeElement("Thermal kit", R.drawable.garb_thermal_kit),
        WardrobeElement("Thermal socks", R.drawable.garb_thermo_socks),
        WardrobeElement("Long snow boots", R.drawable.garb_super_show_boots),
        WardrobeElement("Super winter coat", R.drawable.garb_super_winter_coat),
        WardrobeElement("Beanie", R.drawable.garb_beanie),
        WardrobeElement("Fleece jacket", R.drawable.garb_fleece),
        WardrobeElement("Tight sweater", R.drawable.garb_tight_sweater),
        WardrobeElement("Balaclava", R.drawable.garb_balaclava),
        WardrobeElement("Winter scarf", R.drawable.garb_winter_scarf),
        WardrobeElement("Neck gaiter", R.drawable.garb_neck_gaiter),
        WardrobeElement("Snow pants", R.drawable.garb_snow_pants),
        WardrobeElement("Mittens", R.drawable.garb_mittens),
        WardrobeElement("Gloves", R.drawable.garb_gloves),
        WardrobeElement("Thermos", R.drawable.garb_thermos),
        WardrobeElement("Winter ointment", R.drawable.garb_winter_ointment)
    )

     val kitSuperCold = listOf(
        WardrobeElement("Thermal kit", R.drawable.garb_thermal_kit),
        WardrobeElement("Snow pants", R.drawable.garb_snow_pants),
        WardrobeElement("Long snow boots", R.drawable.garb_super_show_boots),
        WardrobeElement("Turtleneck", R.drawable.garb_turtleneck),
        WardrobeElement("Hoodie", R.drawable.garb_hoodie),
        WardrobeElement("Long winter jacket", R.drawable.garb_long_winter_jacket),
        WardrobeElement("Beanie", R.drawable.garb_beanie),
        WardrobeElement("Balaclava", R.drawable.garb_balaclava),
        WardrobeElement("Mittens", R.drawable.garb_mittens),
        WardrobeElement("Thermos", R.drawable.garb_thermos)
    )

     val kitVeryCold = listOf(
        WardrobeElement("Thermal kit", R.drawable.garb_thermal_kit),
        WardrobeElement("Snow pants", R.drawable.garb_snow_pants),
        WardrobeElement("Show boots", R.drawable.garb_snow_boot),
        WardrobeElement("Hoodie", R.drawable.garb_hoodie),
        WardrobeElement("Winter scarf", R.drawable.garb_winter_scarf),
        WardrobeElement("Mittens", R.drawable.garb_mittens),
        WardrobeElement("Beanie", R.drawable.garb_beanie),
        WardrobeElement("Long winter jacket", R.drawable.garb_long_winter_jacket),
        WardrobeElement("Thermos", R.drawable.garb_thermos)
    )

     val kitNormalCold = listOf(
        WardrobeElement("Thermal kit", R.drawable.garb_thermal_kit),
        WardrobeElement("Beanie", R.drawable.garb_beanie),
        WardrobeElement("Show boots", R.drawable.garb_snow_boot),
        WardrobeElement("Tight sweater", R.drawable.garb_tight_sweater),
        WardrobeElement("Winter scarf", R.drawable.garb_winter_scarf),
        WardrobeElement("Jeans", R.drawable.garb_jeans),
        WardrobeElement("Winter jacket", R.drawable.garb_puffer_coat),
        WardrobeElement("Gloves", R.drawable.garb_gloves)
    )

     val kitTransitCold = listOf(
        WardrobeElement("Jeans", R.drawable.garb_jeans),
        WardrobeElement("Light beanie", R.drawable.garb_light_beanie),
        WardrobeElement("Rain boots", R.drawable.garb_rain_boots),
        WardrobeElement("T-shirt", R.drawable.garb_tshirt),
        WardrobeElement("Turtleneck", R.drawable.garb_turtleneck),
        WardrobeElement("Winter jacket", R.drawable.garb_puffer_coat),
        WardrobeElement("Gloves", R.drawable.garb_gloves)
    )

     val kitTransitHot = listOf(
        WardrobeElement("Jeans", R.drawable.garb_jeans),
        WardrobeElement("Sneakers", R.drawable.garb_sneakers),
        WardrobeElement("T-shirt", R.drawable.garb_tshirt),
        WardrobeElement("Tight sweater", R.drawable.garb_tight_sweater),
        WardrobeElement("Bomber", R.drawable.garb_bomber),
        WardrobeElement("Cap", R.drawable.garb_cap)
    )

     val kitNormalHot = listOf(
        WardrobeElement("Light pants", R.drawable.garb_summer_pants),
        WardrobeElement("Sneakers", R.drawable.garb_sneakers),
        WardrobeElement("T-shirt", R.drawable.garb_tshirt),
        WardrobeElement("Denim jacket", R.drawable.garb_denim_jacket),
        WardrobeElement("Cap", R.drawable.garb_cap)
    )

     val kitVeryHot = listOf(
        WardrobeElement("Sneakers", R.drawable.garb_sneakers),
        WardrobeElement("Shorts", R.drawable.garb_shorts),
        WardrobeElement("T-shirt", R.drawable.garb_tshirt),
        WardrobeElement("Cap", R.drawable.garb_cap),
        WardrobeElement("Sunglasses", R.drawable.garb_sunglasses)
    )
     val kitSuperHot = listOf(
        WardrobeElement("Sandals", R.drawable.garb_sandals),
        WardrobeElement("Shorts", R.drawable.garb_shorts),
        WardrobeElement("Oversize t-shirt", R.drawable.garb_oversize_tie_dye),
        WardrobeElement("White summer hat", R.drawable.garb_white_summer_hat),
        WardrobeElement("Water bottle", R.drawable.garb_water_bottle),
        WardrobeElement("Sunglasses", R.drawable.garb_sunglasses)
    )
     val kitHardHot = listOf(
        WardrobeElement("Sandals", R.drawable.garb_sandals),
        WardrobeElement("Shorts", R.drawable.garb_shorts),
        WardrobeElement("Oversize t-shirt", R.drawable.garb_oversize_tie_dye),
        WardrobeElement("White summer hat", R.drawable.garb_white_summer_hat),
        WardrobeElement("Water bottle", R.drawable.garb_water_bottle),
        WardrobeElement("Sunglasses", R.drawable.garb_sunglasses),
        WardrobeElement("Sunscreen", R.drawable.garb_sunscreen),
    )

   // for Rain
   val kitRainHardCold = listOf(
      WardrobeElement("Raincoat", R.drawable.garb_raincoat),
      WardrobeElement("Thermal kit", R.drawable.garb_thermal_kit),
      WardrobeElement("Thermal socks", R.drawable.garb_thermo_socks),
      WardrobeElement("Long snow boots", R.drawable.garb_super_show_boots),
      WardrobeElement("Super winter coat", R.drawable.garb_super_winter_coat),
      WardrobeElement("Beanie", R.drawable.garb_beanie),
      WardrobeElement("Fleece jacket", R.drawable.garb_fleece),
      WardrobeElement("Tight sweater", R.drawable.garb_tight_sweater),
      WardrobeElement("Balaclava", R.drawable.garb_balaclava),
      WardrobeElement("Winter scarf", R.drawable.garb_winter_scarf),
      WardrobeElement("Neck gaiter", R.drawable.garb_neck_gaiter),
      WardrobeElement("Snow pants", R.drawable.garb_snow_pants),
      WardrobeElement("Mittens", R.drawable.garb_mittens),
      WardrobeElement("Gloves", R.drawable.garb_gloves),
      WardrobeElement("Thermos", R.drawable.garb_thermos),
      WardrobeElement("Winter ointment", R.drawable.garb_winter_ointment)
   )

   val kitRainSuperCold = listOf(
      WardrobeElement("Raincoat", R.drawable.garb_raincoat),
      WardrobeElement("Thermal kit", R.drawable.garb_thermal_kit),
      WardrobeElement("Snow pants", R.drawable.garb_snow_pants),
      WardrobeElement("Long snow boots", R.drawable.garb_super_show_boots),
      WardrobeElement("Turtleneck", R.drawable.garb_turtleneck),
      WardrobeElement("Hoodie", R.drawable.garb_hoodie),
      WardrobeElement("Long winter jacket", R.drawable.garb_long_winter_jacket),
      WardrobeElement("Beanie", R.drawable.garb_beanie),
      WardrobeElement("Balaclava", R.drawable.garb_balaclava),
      WardrobeElement("Mittens", R.drawable.garb_mittens),
      WardrobeElement("Thermos", R.drawable.garb_thermos)
   )

   val kitRainVeryCold = listOf(
      WardrobeElement("Umbrella", R.drawable.garb_umbrella),
      WardrobeElement("Thermal kit", R.drawable.garb_thermal_kit),
      WardrobeElement("Snow pants", R.drawable.garb_snow_pants),
      WardrobeElement("Show boots", R.drawable.garb_snow_boot),
      WardrobeElement("Hoodie", R.drawable.garb_hoodie),
      WardrobeElement("Winter scarf", R.drawable.garb_winter_scarf),
      WardrobeElement("Mittens", R.drawable.garb_mittens),
      WardrobeElement("Beanie", R.drawable.garb_beanie),
      WardrobeElement("Long winter jacket", R.drawable.garb_long_winter_jacket),
      WardrobeElement("Thermos", R.drawable.garb_thermos)
   )

   val kitRainNormalCold = listOf(
      WardrobeElement("Umbrella", R.drawable.garb_umbrella),
      WardrobeElement("Thermal kit", R.drawable.garb_thermal_kit),
      WardrobeElement("Beanie", R.drawable.garb_beanie),
      WardrobeElement("Show boots", R.drawable.garb_snow_boot),
      WardrobeElement("Tight sweater", R.drawable.garb_tight_sweater),
      WardrobeElement("Winter scarf", R.drawable.garb_winter_scarf),
      WardrobeElement("Jeans", R.drawable.garb_jeans),
      WardrobeElement("Winter jacket", R.drawable.garb_puffer_coat),
      WardrobeElement("Gloves", R.drawable.garb_gloves)
   )

   val kitRainTransitCold = listOf(
      WardrobeElement("Umbrella", R.drawable.garb_umbrella),
      WardrobeElement("Jeans", R.drawable.garb_jeans),
      WardrobeElement("Light beanie", R.drawable.garb_light_beanie),
      WardrobeElement("Rain boots", R.drawable.garb_rain_boots),
      WardrobeElement("T-shirt", R.drawable.garb_tshirt),
      WardrobeElement("Turtleneck", R.drawable.garb_turtleneck),
      WardrobeElement("Winter jacket", R.drawable.garb_puffer_coat),
      WardrobeElement("Gloves", R.drawable.garb_gloves)
   )

   val kitRainTransitHot = listOf(
      WardrobeElement("Umbrella", R.drawable.garb_umbrella),
      WardrobeElement("Rainshoes", R.drawable.garb_rainshoes),
      WardrobeElement("Jeans", R.drawable.garb_jeans),
      WardrobeElement("T-shirt", R.drawable.garb_tshirt),
      WardrobeElement("Tight sweater", R.drawable.garb_tight_sweater),
      WardrobeElement("Bomber", R.drawable.garb_bomber),
      WardrobeElement("Cap", R.drawable.garb_cap)
   )

   val kitRainNormalHot = listOf(
      WardrobeElement("Umbrella", R.drawable.garb_umbrella),
      WardrobeElement("Rainshoes", R.drawable.garb_rainshoes),
      WardrobeElement("Light pants", R.drawable.garb_summer_pants),
      WardrobeElement("T-shirt", R.drawable.garb_tshirt),
      WardrobeElement("Denim jacket", R.drawable.garb_denim_jacket),
      WardrobeElement("Cap", R.drawable.garb_cap)
   )

   val kitRainVeryHot = listOf(
      WardrobeElement("Umbrella", R.drawable.garb_umbrella),
      WardrobeElement("Rainshoes", R.drawable.garb_rainshoes),
      WardrobeElement("Shorts", R.drawable.garb_shorts),
      WardrobeElement("T-shirt", R.drawable.garb_tshirt),
      WardrobeElement("Cap", R.drawable.garb_cap),
      WardrobeElement("Sunglasses", R.drawable.garb_sunglasses)
   )
   val kitRainSuperHot = listOf(
      WardrobeElement("Raincoat", R.drawable.garb_raincoat),
      WardrobeElement("Sandals", R.drawable.garb_sandals),
      WardrobeElement("Shorts", R.drawable.garb_shorts),
      WardrobeElement("Oversize t-shirt", R.drawable.garb_oversize_tie_dye),
      WardrobeElement("White summer hat", R.drawable.garb_white_summer_hat),
      WardrobeElement("Water bottle", R.drawable.garb_water_bottle),
      WardrobeElement("Sunglasses", R.drawable.garb_sunglasses)
   )
   val kitRainHardHot = listOf(
      WardrobeElement("Raincoat", R.drawable.garb_raincoat),
      WardrobeElement("Sandals", R.drawable.garb_sandals),
      WardrobeElement("Shorts", R.drawable.garb_shorts),
      WardrobeElement("Oversize t-shirt", R.drawable.garb_oversize_tie_dye),
      WardrobeElement("White summer hat", R.drawable.garb_white_summer_hat),
      WardrobeElement("Water bottle", R.drawable.garb_water_bottle),
      WardrobeElement("Sunglasses", R.drawable.garb_sunglasses),
      WardrobeElement("Sunscreen", R.drawable.garb_sunscreen),
   )
}