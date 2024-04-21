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
import edu.utap.weatherwizard.model.UnitsMeta

class MainViewModel : ViewModel() {

    // LiveData for entire note list, all images
    private var cityMetaList = MutableLiveData<List<CityMeta>>()
    private var unitsMetaList = MutableLiveData<List<UnitsMeta>>()


    // Track current authenticated user
    private var currentAuthUser = invalidUser
    // Database access
    private val dbHelp = ViewModelDBHelper()
    private val weatherApi = WeatherApi.create()
    private val repository = WeatherRepository(weatherApi)

//    private var favorite = MutableLiveData<Boolean>()

    private var currentCityMeta = MutableLiveData<CityMeta>()
    private var currentUnitsMeta = MutableLiveData<UnitsMeta>()


    private var netWeatherDaily = MediatorLiveData<List<WeatherDaily>>().apply {

        addSource(currentUnitsMeta) {
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.Default
            ) {
                Log.d("XXX", "netweatherdaily fetch from current units")
//                postValue(repository.getWeather(latlon.value?.latitude.toString(), latlon.value?.longitude.toString(), unit))
//                val newNetWeatherDaily = mutableListOf<WeatherDaily>()
//                if (currentNetWeatherDaily != null) {
//                    Log.d("XXX", "current net weather daily is not null")
//                    for(wd in currentNetWeatherDaily){
//                        Log.d("XXX", "looping in change units")
//                        val newwd = wd.copy()
//                        if(it.units == "Fahrenheit") {
//                            newwd.temp.max = (newwd.temp.max * 9.0/5.0) + 32
//                            newwd.temp.min = (newwd.temp.min * 9.0/5.0) + 32
//                        }
//                        else{
//                            newwd.temp.max = (newwd.temp.max - 32 ) * 5.0/9.0
//                            newwd.temp.min = (newwd.temp.min - 32 ) * 5.0/9.0
//                        }
//                        newNetWeatherDaily.add(newwd)
//                    }
//                }
//                Log.d("XXX", "new net weatherdaily size " + newNetWeatherDaily.size)
                if(it != null){
                    postValue(
                        repository.getWeather(
                            currentCityMeta.value?.latitude.toString(),
                            currentCityMeta.value?.longitude.toString(),
                            currentUnitsMeta.value!!.units
                        )
                    )
                }
            }
        }
        addSource(currentCityMeta){
            viewModelScope.launch (
                context = viewModelScope.coroutineContext
                        + Dispatchers.Default
            ) {
               Log.d("XXX", "netweatherdaily fetch from current city " + currentUnitsMeta.value?.units + currentCityMeta.value?.latitude.toString())
                if(it != null) {
                    postValue(
                        repository.getWeather(
                            currentCityMeta.value?.latitude.toString(),
                            currentCityMeta.value?.longitude.toString(),
                            currentUnitsMeta.value!!.units
                        )
                    )
                }
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

    fun fetchUnitsMeta(resultListener:()->Unit) {
        Log.d("XXX", "view model fetching units meta")
        dbHelp.fetchUnitsMeta(currentAuthUser.uid) {

            unitsMetaList.value = it
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

    fun getCityState(city: String, state:String): CityMeta?{
        for(c in 0..<cityMetaList.value!!.size) {
            val record = cityMetaList.value!![c]
            if((record.city == city) && (record.state==state)){
                return record
            }
        }
        return null
    }

    fun setUnits(){
        if(unitsMetaList.value.isNullOrEmpty()){
            Log.d("XXX", "in set units, is empty")
            var um = createUnitsMeta("Fahrenheit")
            if(currentAuthUser != invalidUser) {
                saveUnitsMeta(um)
            }
            setUnitsMeta(um)
            return
        }
        else{
            Log.d("XXX", "in set units not empty")
            val record = unitsMetaList.value!![0]
            setUnitsMeta(record)
            return
        }
    }

    fun setHome(){
        if(cityMetaList.value.isNullOrEmpty()){
            Log.d("XXX", "in set home city meta empty")
            var cm = createCityMeta("Austin", "Texas", "Fahrenheit", false, true, "30.2672", "-97.7431")
//            setUnit("Fahrenheit")
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
//                setUnit("Fahrenheit")
                setCityMeta(record)
                return
            }

            var cm = createCityMeta("Austin", "Texas", "Fahrenheit", false,true, "30.2672", "-97.7431")
//            setUnit("Fahrenheit")
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

//    fun setFavBool(newfav: Boolean){
//        favorite.value = newfav
//    }

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

    fun updateUnitsMeta(um: UnitsMeta, newunits: String){
        dbHelp.updateUnitsMeta(currentAuthUser.uid, um, newunits){
            unitsMetaList.postValue(it)
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

    fun observeCurrentUM(): LiveData<UnitsMeta>{
        return currentUnitsMeta
    }

    fun setUnitsMeta(um: UnitsMeta){
        currentUnitsMeta.value = um
    }

    fun saveCityMeta(cityMeta: CityMeta){
        val currentUser = currentAuthUser
        dbHelp.createCityMeta(currentUser.uid, cityMeta) {
            cityMetaList.postValue(it)
        }
    }

    fun saveUnitsMeta(unitsMeta: UnitsMeta){
        val currentUser = currentAuthUser
        dbHelp.createUnitsMeta(currentUser.uid, unitsMeta) {
            unitsMetaList.postValue(it)
        }
    }

    fun createCityMeta(city: String, state: String, units: String, favorite: Boolean, home: Boolean, latitude: String, longitude: String): CityMeta {
        val currentUser = currentAuthUser
        val cityMeta = CityMeta(
            ownerName = currentUser.name,
            ownerUid = currentUser.uid,
            city = city,
            state = state,
//            units = units,
            favorite = favorite,
            home = home,
            latitude = latitude,
            longitude = longitude
        )

        return cityMeta
    }

    fun createUnitsMeta(units: String): UnitsMeta {
        val currentUser = currentAuthUser
        val unitsMeta = UnitsMeta(
            ownerName = currentUser.name,
            ownerUid = currentUser.uid,
            units = units
        )

        return unitsMeta
    }

}
