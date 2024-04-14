package edu.cs371m.reddit.api

import android.text.SpannableString
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type


interface WeatherApi {
    @GET("/data/3.0/onecall?lat=33.44&lon=-94.04&exclude=current,minutely,hourly,alerts&appid=1e014bfae9d273d95b456a0e8b290034")
    suspend fun getWeather() : WeatherResponse
    //@Query("lat") lat: String, @Query("lon") lon: String

    class WeatherResponse(val data: WeatherData)

    class WeatherData(
        val lat: Double,
        val lon: Double,
        val timezone: String,
        val timezone_offset: Int,
//        val daily: List<WeatherDailyResponse>,
    )
    data class WeatherDailyResponse(val data: WeatherDaily)

    companion object {
        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("api.openweathermap.org")
            .build()
        fun create(): WeatherApi = create(httpurl)
        private fun create(httpUrl: HttpUrl): WeatherApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApi::class.java)
        }
    }
}

//@GET("3/movie/{id}")
//fun getMovieById(
//    @Path("id") id: String,
//    @Query("api_key") apiKey: String = BuildConfig.MOVIE_TOKEN
//):Call<SearchDetailMovieResponse>