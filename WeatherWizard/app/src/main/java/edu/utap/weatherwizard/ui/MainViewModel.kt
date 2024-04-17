package edu.utap.weatherwizard.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import edu.utap.weatherwizard.User
import edu.utap.weatherwizard.ViewModelDBHelper
import edu.utap.weatherwizard.model.CityMeta
import edu.utap.weatherwizard.invalidUser
import edu.utap.weatherwizard.api.WeatherDaily
import edu.utap.weatherwizard.api.WeatherApi
import edu.utap.weatherwizard.api.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng


class MainViewModel : ViewModel() {

    // LiveData for entire note list, all images
    private var cityMetaList = MutableLiveData<List<CityMeta>>()

    // Track current authenticated user
    private var currentAuthUser = invalidUser
    // Database access
    private val dbHelp = ViewModelDBHelper()
    private val weatherApi = WeatherApi.create()
    private val repository = WeatherRepository(weatherApi)

    private var latlon = MutableLiveData<LatLng>()
    private var city = MutableLiveData<String>()
    private var state = MutableLiveData<String>()
    private var unit = MutableLiveData<String>()

//    private var lon = MutableLiveData<Double>()


    private var netWeatherDaily = MediatorLiveData<List<WeatherDaily>>().apply {
        addSource(latlon) {latlon: LatLng ->
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.Default
            ) {
                Log.d("XXX", "netweatherdaily fetch")
                postValue(repository.getWeather(latlon.latitude.toString(), latlon.longitude.toString(), unit.value!!))
            }
        }
        addSource(unit) {unit: String ->
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.Default
            ) {
                Log.d("XXX", "netweatherdaily fetch")
                postValue(repository.getWeather(latlon.value?.latitude.toString(), latlon.value?.longitude.toString(), unit))
            }
        }
    }

    init {
        fetchCityMeta {
            setHome()
            repoFetch()
        }
    }

    fun repoFetch() {
        Log.d("XXX", "in repo fetch")
        val fetch = latlon.value!!
        latlon.value = fetch
    }

    fun observeNetWeatherDaily(): LiveData<List<WeatherDaily>> {
        return netWeatherDaily
    }


    /////////////////////////////////////////////////////////////
    // Notes, memory cache and database interaction
    fun fetchCityMeta(resultListener:()->Unit) {
        dbHelp.fetchCityMeta() {
            cityMetaList.postValue(it)
            resultListener.invoke()
        }
    }

    fun setHome(){
        if(cityMetaList.value.isNullOrEmpty()){
            createCityMeta("Austin", "Texas", "Fahrenheit", true,"30.2672", "97.7431")

        }
        else{
            for(record in cityMetaList.value!!){
                if(record.home){
                    setCity(record.city)
                    setState(record.state)
                    setLatLon(LatLng(record.latitude.toDouble(), record.longitude.toDouble()))
                    setUnit(record.units)
                    break
                }
            }
        }
    }
    fun observeCityMeta(): LiveData<List<CityMeta>> {
        return cityMetaList
    }


    // MainActivity gets updates on this via live data and informs view model
    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }

//    fun setLon(longitude: Double){
//        lon.value = longitude
//    }
//    fun setLat(latitude: Double){
//        lat.value = latitude
//    }
    fun setLatLon(latLon: LatLng){
        latlon.value = latLon
    }

    fun setCity(newcity: String){
        city.value = newcity
    }

    fun setState(newstate: String){
        state.value = newstate
    }

    fun observeState(): LiveData<String> {
        return state
    }

    fun observeCity(): LiveData<String> {
        return city
    }

    fun removePhotoAt(position: Int) {
        // XXX Deletion requires two different operations.  What are they?
        val cityMeta = getCityMeta(position)
        dbHelp.removeCityMeta(cityMeta){
            cityMetaList.postValue(it)
        }
    }

    // Get a note from the memory cache
    fun getCityMeta(position: Int) : CityMeta {
        val note = cityMetaList.value?.get(position)
        return note!!
    }

    fun observeUnits(): LiveData<String> {
        return unit
    }

    fun setUnit(newUnit: String){
        unit.value = newUnit
    }

    private fun createCityMeta(city: String, state: String, units: String, home: Boolean, latitude: String, longitude: String) {
        val currentUser = currentAuthUser
        val cityMeta = CityMeta(
            ownerName = currentUser.name,
            ownerUid = currentUser.uid,
            city = city,
            state = state,
            units = units,
            favorite = !home,
            home = home,
            latitude = latitude,
            longitude = longitude
        )
        dbHelp.createCityMeta(cityMeta) {
            cityMetaList.postValue(it)
        }
    }

}
