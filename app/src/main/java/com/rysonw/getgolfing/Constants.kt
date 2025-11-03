package com.rysonw.getgolfing

object Constants {
    // API endpoints
    const val BASE_WEATHER_URL = "https://api.open-meteo.com/v1/forecast?latitude=21.3156&longitude=-158.0072&current=rain,temperature_2m,precipitation,wind_speed_10m,wind_gusts_10m&timezone=auto&wind_speed_unit=mph&temperature_unit=fahrenheit&precipitation_unit=inch"
    const val BASE_GOLF_URL = "https://example.com/golf_conditions"

    // Query Defaults TODO: Make settable values for these, add also importance value from 0 (Not Important) to 2 (Very Important)
    const val LATITUDE = 47.6062
    const val LONGITUDE = -122.3321
    const val DEFAULT_PARAMS = "temperature_2m,precipitation_probability"

    // Timeouts, delays, etc.
    const val NETWORK_TIMEOUT_MS = 5000L
}