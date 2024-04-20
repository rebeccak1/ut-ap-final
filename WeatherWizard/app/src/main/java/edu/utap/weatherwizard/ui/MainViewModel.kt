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

    private var unit = MutableLiveData<String>()
    private var favorite = MutableLiveData<Boolean>()
    private var home = MutableLiveData<Boolean>()

    private var currentCityMeta = MutableLiveData<CityMeta>()


    private var netWeatherDaily = MediatorLiveData<List<WeatherDaily>>().apply {

//        addSource(unit) {unit: String ->
//            viewModelScope.launch(
//                context = viewModelScope.coroutineContext
//                        + Dispatchers.Default
//            ) {
//                Log.d("XXX", "netweatherdaily fetch")
//                postValue(repository.getWeather(latlon.value?.latitude.toString(), latlon.value?.longitude.toString(), unit))
//            }
//        }
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

//    fun repoFetch() {
//        Log.d("XXX", "in repo fetch")
//        val fetch = latlon.value!!
//        latlon.value = fetch
//    }

    fun observeNetWeatherDaily(): LiveData<List<WeatherDaily>> {
        return netWeatherDaily
    }

    fun getCurrentUser(): User{
        return currentAuthUser
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

    fun getHome(): CityMeta?{
        for(c in 0..<cityMetaList.value!!.size) {
            val record = cityMetaList.value!![c]
            if(record.home){

//                    setCity(record.city)
//                    setState(record.state)
//                    setLatLon(LatLng(record.latitude.toDouble(), record.longitude.toDouble()))
//                    setPos(c)
                Log.d("XXX", "in set home, home record found")

                return record
            }
        }
        return null
    }

    fun setHome(){
        if(cityMetaList.value.isNullOrEmpty()){
            Log.d("XXX", "in set home city meta empty")
            var cm = createCityMeta("Austin", "Texas", "Fahrenheit", false, true, "30.2672", "-97.7431")
            setUnit("Fahrenheit")
            if(currentAuthUser != invalidUser) {
                saveCityMeta(cm)
            }
            setCityMeta(cm)
            return
        }
        else{
            Log.d("XXX", "in set home city meta NOT empty")
            val record = getHome()
            if(record != null) {
                setUnit("Fahrenheit")
                setCityMeta(record)
                return
            }

            var cm = createCityMeta("Austin", "Texas", "Fahrenheit", false,true, "30.2672", "-97.7431")
            setUnit("Fahrenheit")
            saveCityMeta(cm)
            setCityMeta(cm)
        }
    }
    fun observeCityMeta(): LiveData<List<CityMeta>> {
        return cityMetaList
    }

    // MainActivity gets updates on this via live data and informs view model
    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }

    fun setFavBool(newfav: Boolean){
        favorite.value = newfav
    }

    fun observeCurrentCM(): LiveData<CityMeta>{
        return currentCityMeta
    }

    fun remove(removeCM: CityMeta){
        var position = 0
        for(i in 0..< cityMetaList.value!!.size){
            val cm = cityMetaList.value!![i]
            if(cm == removeCM){
                Log.d("XXX", "removing position found")
                position = i
                break
            }
        }
        Log.d("XXX", "at end of remove")
        removeCityMeta(position)
    }

    fun setCityMeta(cm: CityMeta){
        currentCityMeta.value = cm
    }

    fun updateCityMeta(cm:CityMeta, newhome: Boolean, newfav: Boolean){
        dbHelp.updateCityMeta(currentAuthUser.uid, cm, newhome, newfav){
            cityMetaList.postValue(it)
        }
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

    fun saveCityMeta(cityMeta: CityMeta){
        val currentUser = currentAuthUser
        dbHelp.createCityMeta(currentUser.uid, cityMeta) {
            cityMetaList.postValue(it)
        }
    }

    fun createCityMeta(city: String, state: String, units: String, favorite: Boolean, home: Boolean, latitude: String, longitude: String): CityMeta {
        val currentUser = currentAuthUser
        val cityMeta = CityMeta(
            ownerName = currentUser.name,
            ownerUid = currentUser.uid,
            city = city,
            state = state,
            units = units,
            favorite = favorite,
            home = home,
            latitude = latitude,
            longitude = longitude
        )

        return cityMeta
    }

}
