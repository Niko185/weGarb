package com.example.wegarb.domain.models.newvariant.searching.response

data class SysInfo(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
)

