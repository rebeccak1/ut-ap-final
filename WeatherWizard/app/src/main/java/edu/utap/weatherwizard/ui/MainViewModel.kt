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
import edu.utap.weatherwizard.model.Rating
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

    private var currentCityMeta = MutableLiveData<CityMeta>()
    private var currentUnitsMeta = MutableLiveData<UnitsMeta>()

    private var ratingsList = MutableLiveData<List<Rating>>()

    private var netWeatherDaily = MediatorLiveData<List<WeatherDaily>>().apply {

        addSource(currentUnitsMeta) {
            viewModelScope.launch(
                context = viewModelScope.coroutineContext
                        + Dispatchers.Default
            ) {
                Log.d("XXX", "netweatherdaily fetch from current units")

                if(it != null && currentCityMeta.value!=null){
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
                if(it != null && currentUnitsMeta.value !=null) {
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

    fun fetchInitialNotes(callback: ()->Unit) {
        dbHelp.fetchInitiaRatings(ratingsList, callback)
    }
    fun observeRatings(): LiveData<List<Rating>> {
        return ratingsList
    }

    fun createRating(rating: Int) {
        val currentUser = currentAuthUser
        val note = Rating(
            name = currentUser.name,
            ownerUid = currentUser.uid,
            city = currentCityMeta.value!!.city,
            state = currentCityMeta.value!!.state,
            rating = rating
            // database sets firestoreID
        )
        dbHelp.createRating(note, ratingsList)
    }

    fun observeNetWeatherDaily(): LiveData<List<WeatherDaily>> {
        return netWeatherDaily
    }

    fun observeMinMaxTemp(): Double{
        val nwd = netWeatherDaily.value

        var min = 0.0
        for (i in nwd!!) {
            if (i.temp.min > min) {
                min = i.temp.min
            }
        }
        return min
    }

    fun observeMinMinTemp(): Double{
        val nwd = netWeatherDaily.value

        var min = 200.0
        for (i in nwd!!) {
            if (i.temp.min < min) {
                min = i.temp.min
            }
        }
        return min
    }



    fun observeMaxMaxTemp(): Double{
        val nwd = netWeatherDaily.value

        var max = 0.0
        for (i in nwd!!) {
            if (i.temp.max > max) {
                max = i.temp.max
            }
        }
        return max
    }

    fun observeMaxMinTemp(): Double{
        val nwd = netWeatherDaily.value

        var max = 200.0
        for (i in nwd!!) {
            if (i.temp.max < max) {
                max = i.temp.max
            }
        }
        return max
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
                setCityMeta(record)
                return
            }

            var cm = createCityMeta("Austin", "Texas", "Fahrenheit", false,true, "30.2672", "-97.7431")
            saveCityMeta(cm)
            setCityMeta(cm)
        }
    }
    fun observeCityMeta(): LiveData<List<CityMeta>> {
        return cityMetaList
    }

    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
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
