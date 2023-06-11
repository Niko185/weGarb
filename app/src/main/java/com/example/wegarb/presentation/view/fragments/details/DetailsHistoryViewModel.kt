package com.example.wegarb.presentation.view.fragments.details

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsHistoryViewModel @Inject constructor() : ViewModel() {
    fun getWindDirectionName(windDirection: Int): String {
        val statusWind: String = when (windDirection) {
            in 349 ..361, in 0 .. 11 -> "North"
            in 12 .. 56 -> "North/East"
            in 57 .. 123 -> "East"
            in 124 .. 168 -> "South/East"
            in 169 .. 213 -> "South"
            in 214 .. 258 -> "South/West"
            in 259 .. 303 -> "West"
            in 304 .. 348 -> "North/West"
            else -> "Sorry, wind direction not found"
        }
        return statusWind
    }
}