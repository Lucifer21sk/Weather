package inferno.hell.weather.Repository

import inferno.hell.weather.Server.ApiServices

class CityRepository(val api: ApiServices) {
    fun getCities(q:String,limit:Int)=
        api.getCitiesList(q,limit,"e24fa887105eb578da9aaf28b0b48e59")
}