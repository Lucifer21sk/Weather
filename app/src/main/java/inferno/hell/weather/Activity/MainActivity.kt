package inferno.hell.weather.Activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.matteobattilana.weather.PrecipType
import eightbitlab.com.blurview.RenderScriptBlur
import inferno.hell.weather.Adapter.ForecastAdapter
import inferno.hell.weather.Model.CurrentResponseApi
import inferno.hell.weather.Model.ForecastResponseApi
import inferno.hell.weather.R
import inferno.hell.weather.ViewModel.WeatherViewModel
import inferno.hell.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val weatherViewModel:WeatherViewModel by viewModels()
    private val calendar by lazy { Calendar.getInstance() }
    private val forecastAdapter by lazy { ForecastAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor= Color.TRANSPARENT
        }

        binding.apply {
            var lat=intent.getDoubleExtra("lat",0.0)
            var lon=intent.getDoubleExtra("lon",0.0)
            var name=intent.getStringExtra("name")

            if(lat==0.0) {
                 lat = 48.14
                 lon = 17.10
                 name = "Bratislava"
            }

            addCity.setOnClickListener{
                startActivity(Intent(this@MainActivity, CityListActivity::class.java))
            }
            //current Temp etc
            cityTxt.text = name
            progressBar.visibility = View.VISIBLE
            weatherViewModel.loadCurrentWeather(lat,lon,"metric").enqueue(object :
                retrofit2.Callback<CurrentResponseApi> {
                override fun onResponse(
                    call: Call<CurrentResponseApi>,
                    response: Response<CurrentResponseApi>
                ) {
                    if(response.isSuccessful){
                        val data=response.body()
                        progressBar.visibility=View.GONE
                        detailLayout.visibility=View.VISIBLE
                        data?.let {
                            statusTxt.text= it.weather?.get(0)?.main ?: "-"
                            windMetric.text= it.wind?.speed?.let{ Math.round(it).toString()}+ "KM"
                            humidityMetric.text=it.main?.humidity?.toString()+"%"
                            currentTempTxt.text=it.main?.temp?.let { Math.round(it).toString() } + "°"
                            maxTempTxt.text=it.main?.tempMax?.let { Math.round(it).toString() } + "°"
                            minTempTxt.text=it.main?.tempMin?.let { Math.round(it).toString() } + "°"

                            val drawable=if(isNightNow()) R.drawable.night_bg
                            else{
                                 setDynamicallyWallpaper(it.weather?.get(0)?.icon?:"-")
                            }
                            bgImage.setImageResource(drawable)
                            setEffectRainSnow(it.weather?.get(0)?.icon?:"-")

                        }
                    }
                }

                override fun onFailure(call: Call<CurrentResponseApi>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                }

            })


            //setting Blue View
            var radius =10f
            val decorView=window.decorView
            var rootView=(decorView.findViewById(android.R.id.content) as ViewGroup?)
            val windowBackground=decorView.background

            rootView?.let {
                blueView.setupWith(it,RenderScriptBlur(this@MainActivity))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
                blueView.outlineProvider = ViewOutlineProvider.BACKGROUND
                blueView.clipToOutline = true
            }


            //forecast temp
            weatherViewModel.loadForecastWeather(lat,lon, "metric").enqueue(object : retrofit2.Callback<ForecastResponseApi>{
                override fun onResponse(
                    call: Call<ForecastResponseApi>,
                    response: Response<ForecastResponseApi>
                ) {
                    if(response.isSuccessful){
                        val data=response.body()
                        blueView.visibility=View.VISIBLE

                        data?.let {
                            forecastAdapter.differ.submitList(it.list)
                            forecastView.apply {
                                layoutManager=LinearLayoutManager(this@MainActivity,LinearLayoutManager.HORIZONTAL,false)
                                adapter=forecastAdapter
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ForecastResponseApi>, t: Throwable) {

                }

            })
        }

    }

    private fun isNightNow():Boolean{
        return calendar.get(Calendar.HOUR_OF_DAY)>= 18
    }
    //based on the icons
    private fun setDynamicallyWallpaper(icon:String):Int{
        return when(icon.dropLast(1)){
            "01"->{
                initWeatherView(PrecipType.CLEAR)
                R.drawable.sunny_bg
            }
            "02","03","04"->{
                initWeatherView(PrecipType.CLEAR)
                R.drawable.cloudy_bg
            }
            "09","10","11"->{
                initWeatherView(PrecipType.RAIN)
                R.drawable.rainy_bg
            }
            "13"->{
                initWeatherView(PrecipType.SNOW)
                R.drawable.snow_bg
            }
            "50"->{
                initWeatherView(PrecipType.CLEAR)
                R.drawable.haze_bg
            }
            else ->0
        }
    }
    private fun setEffectRainSnow(icon:String){
        when(icon.dropLast(1)){
            "01"->{
                initWeatherView(PrecipType.CLEAR)
            }
            "02","03","04"->{
                initWeatherView(PrecipType.CLEAR)
            }
            "09","10","11"->{
                initWeatherView(PrecipType.RAIN)
            }
            "13"->{
                initWeatherView(PrecipType.SNOW)
            }
            "50"-> {
                initWeatherView(PrecipType.CLEAR)
            }
        }
    }
    private fun initWeatherView(type: PrecipType){
        binding.weatherView.apply {
            setWeatherData(type)
            angle=-20
            emissionRate=100.0f
        }
    }
}