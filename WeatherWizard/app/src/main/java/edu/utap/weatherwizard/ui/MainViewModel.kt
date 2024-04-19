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
    private var favorite = MutableLiveData<Boolean>()
    private var home = MutableLiveData<Boolean>()
    private var pos = MutableLiveData<Int>()

    private var currentCityMeta = MutableLiveData<CityMeta>()


    private var netWeatherDaily = MediatorLiveData<List<WeatherDaily>>().apply {
//        addSource(latlon) {latlon: LatLng ->
//            viewModelScope.launch(
//                context = viewModelScope.coroutineContext
//                        + Dispatchers.Default
//            ) {
//                Log.d("XXX", "netweatherdaily fetch")
//                postValue(repository.getWeather(latlon.latitude.toString(), latlon.longitude.toString(), unit.value!!))
//            }
//        }
        addSource(unit) {unit: String ->
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.Default
            ) {
                Log.d("XXX", "netweatherdaily fetch")
                postValue(repository.getWeather(latlon.value?.latitude.toString(), latlon.value?.longitude.toString(), unit))
            }
        }
        addSource(currentCityMeta){
            viewModelScope.launch (
                context = viewModelScope.coroutineContext
                        + Dispatchers.Default
            ) {
               Log.d("XXX", "netweatherdaily fetch")
               postValue(repository.getWeather(currentCityMeta.value?.latitude.toString(), currentCityMeta.value?.longitude.toString(), unit.value!!))

            }
        }
    }

    init {
//        fetchCityMeta {
//            setHome()
//            repoFetch()
//        }
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
        Log.d("XXX", "view model fetching city meta")
        dbHelp.fetchCityMeta(currentAuthUser.uid) {

            cityMetaList.value = it
            Log.d("XXX", "view model fetching city meta: posting")

            resultListener.invoke()
        }
    }

    fun setHome(){
        if(cityMetaList.value.isNullOrEmpty()){
            Log.d("XXX", "in set home city meta empty")
            var cm = createCityMeta("Austin", "Texas", "Fahrenheit", true, "30.2672", "-97.7431")
            setCityMeta(cm)
        }
        else{
            Log.d("XXX", "in set home city meta NOT empty")
            for(c in 0..cityMetaList.value!!.size) {
                val record = cityMetaList.value!![c]
                if(record.home){
                    setCityMeta(record)
                    setCity(record.city)
                    setState(record.state)
                    setLatLon(LatLng(record.latitude.toDouble(), record.longitude.toDouble()))
                    setUnit(record.units)
                    setPos(c)
                    Log.d("XXX", "in set home, home record found")

                    break
                }
            }
        }
    }
    fun observeCityMeta(): LiveData<List<CityMeta>> {
        return cityMetaList
    }

    fun observeLatLng(): LiveData<LatLng> {
        return latlon
    }


    // MainActivity gets updates on this via live data and informs view model
    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }

    fun setLatLon(latLon: LatLng){
        latlon.value = latLon
    }

    fun setCity(newcity: String){
        city.value = newcity
    }

    fun setPos(newPos: Int){
        pos.value = newPos
    }

    fun setState(newstate: String){
        state.value = newstate
    }

    fun setHomeBool(newhome: Boolean){
        home.value = newhome
    }

    fun setFavBool(newfav: Boolean){
        favorite.value = newfav
    }


    fun observeState(): LiveData<String> {
        return state
    }

    fun observeFavorite(): LiveData<Boolean> {
        return favorite
    }

    fun observeCurrentCM(): LiveData<CityMeta>{
        return currentCityMeta
    }

    fun observeHome(): LiveData<Boolean> {
        return home
    }

    fun observeCity(): LiveData<String> {
        return city
    }

    fun remove(){
        var position = 0
        for(i in 0..< cityMetaList.value!!.size){
            val cm = cityMetaList.value!![i]
            if(cm.equals(currentCityMeta)){
                Log.d("XXX", "removing position found")
                position = i
                break
            }
        }
        removeCityMeta(position)
    }

    fun setCityMeta(cm: CityMeta){
        currentCityMeta.value = cm
    }

    fun removeCityMeta(position: Int) {
        val cityMeta = getCityMeta(position)
        dbHelp.removeCityMeta(currentAuthUser.uid, cityMeta){
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

    fun createCityMeta(city: String, state: String, units: String, home: Boolean, latitude: String, longitude: String): CityMeta {
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
        dbHelp.createCityMeta(currentUser.uid, cityMeta) {
            cityMetaList.postValue(it)
        }
        return cityMeta
    }

}
