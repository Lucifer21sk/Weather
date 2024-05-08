package inferno.hell.weather.ViewModel

import androidx.lifecycle.ViewModel
import inferno.hell.weather.Repository.CityRepository
import inferno.hell.weather.Server.ApiClient
import inferno.hell.weather.Server.ApiServices

class CityViewModel(val repository: CityRepository): ViewModel() {
    constructor():this(CityRepository(ApiClient().getClient().create(ApiServices::class.java)))

    fun loadCity(q:String,limit: Int) =
        repository.getCities(q, limit)
}