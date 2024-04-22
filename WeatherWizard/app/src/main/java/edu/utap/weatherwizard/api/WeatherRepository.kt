package edu.utap.weatherwizard.api

class WeatherRepository(private val weatherApi: WeatherApi) {

    suspend fun getWeather(lat: String, lon: String, unit: String): List<WeatherDaily> {
        val response : WeatherApi.WeatherDailyResponse?
        val units = when(unit){
            "Fahrenheit"->"imperial"
            "Celsius"->"metric"
            else->"imperial"
        }
        response = weatherApi.getWeather(lat, lon, units)

        return response.daily
    }

}
