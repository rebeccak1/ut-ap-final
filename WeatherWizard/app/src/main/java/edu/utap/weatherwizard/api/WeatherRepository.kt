package edu.cs371m.reddit.api

import android.util.Log

class WeatherRepository(private val weatherApi: WeatherApi) {

//    private fun unpackPosts(response: RedditApi.ListingResponse): List<RedditPost> {
//        // XXX Write me.
//        val posts = mutableListOf<RedditPost>()
//        for (i in 0..<response.data.children.size){
//            posts.add(response.data.children[i].data)
//        }
//        return posts.toList()
//    }

    suspend fun getWeather(lat: String, lon: String): WeatherApi.WeatherResponse {
        val response : WeatherApi.WeatherResponse?

        response = weatherApi.getWeather() //lat, lon

        return response
//        return unpackPosts(response!!)
    }

}
