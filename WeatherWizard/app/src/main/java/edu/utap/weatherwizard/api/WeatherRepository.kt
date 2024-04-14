package edu.utap.weatherwizard.api

import android.util.Log

class WeatherRepository(private val weatherApi: WeatherApi) {

//    private fun unpackPosts(response: WeatherApi.WeatherResponse): List<WeatherDaily> {
//        val posts = mutableListOf<WeatherDaily>()
//        for (i in 0..<response.data.daily.size){
//            posts.add(response.data.daily[i].data)
//        }
//        return posts.toList()
//    }

    suspend fun getWeather(lat: String, lon: String): String{//List<WeatherDaily> {
        val response : WeatherApi.WeatherDailyResponse?

        response = weatherApi.getWeather(lat,lon)

        return response.daily[0].summary
//        return unpackPosts(response)
    }

}
