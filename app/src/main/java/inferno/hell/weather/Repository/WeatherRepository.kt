package inferno.hell.weather.Repository

import inferno.hell.weather.Server.ApiServices

class WeatherRepository(val api: ApiServices) {

    fun getCurrentWeather(lat: Double, lng: Double, unit: String) =
        api.getCurrentWeather(lat,lng,unit,"e24fa887105eb578da9aaf28b0b48e59")

    fun getForecastWeather(lat: Double, lng: Double, unit: String) =
        api.getForecastWeather(lat,lng,unit,"e24fa887105eb578da9aaf28b0b48e59")
}