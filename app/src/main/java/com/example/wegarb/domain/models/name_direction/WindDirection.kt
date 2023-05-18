package com.example.wegarb.domain.models.name_direction

enum class WindDirection(direction: String) {
    NORTH("North"),
    NORTH_EAST("North/East"),
    EAST("East"),
    SOUTH_EAST("South/East"),
    SOUTH("South"),
    SOUTH_WEST("South/West"),
    WEST("West"),
    NORTH_WEST("North/West"),
    ERROR("Sorry, wind direction not found")
}

