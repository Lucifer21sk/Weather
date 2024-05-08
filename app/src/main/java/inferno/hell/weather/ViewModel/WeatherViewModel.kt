package inferno.hell.weather.ViewModel

import androidx.lifecycle.ViewModel
import inferno.hell.weather.Repository.WeatherRepository
import inferno.hell.weather.Server.ApiClient
import inferno.hell.weather.Server.ApiServices

class WeatherViewModel(val repository: WeatherRepository):ViewModel() {

    constructor():this(WeatherRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCurrentWeather(lat:Double, lng:Double, unit:String)=
        repository.getCurrentWeather(lat, lng, unit)

    fun loadForecastWeather(lat:Double, lng:Double, unit:String)=
        repository.getForecastWeather(lat, lng, unit)
}