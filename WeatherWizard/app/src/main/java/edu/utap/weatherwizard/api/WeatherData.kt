package edu.cs371m.reddit.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WeatherTemp(
    @SerializedName("day")
    val day: Float,
    @SerializedName("min")
    val min: Float,
    @SerializedName("max")
    val max: Float,
    @SerializedName("night")
    val night: Float,
    @SerializedName("eve")
    val eve: Float,
    @SerializedName("morn")
    val morn: Float
)

data class WeatherFeelsLike(
    @SerializedName("day")
    val day: Float,
    @SerializedName("night")
    val night: Float,
    @SerializedName("eve")
    val eve: Float,
    @SerializedName("morn")
    val morn: Float
)

data class WeatherWeather(
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String
)

data class WeatherDaily (
    @SerializedName("dt")
    val dt: Int,
    @SerializedName("sunrise")
    val sunrise: Int,
    @SerializedName("sunset")
    val sunset: Int,
    @SerializedName("moonrise")
    val moonrise: Int,
    @SerializedName("moonset")
    val moonset: String,
    @SerializedName("moon_phase")
    val moon_phase: Float,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("temp")
    val temp: WeatherTemp,
    @SerializedName("feels_like")
    val feels_like : WeatherFeelsLike,
    @SerializedName("pressure")
    val pressure : Int,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("dew_point")
    val dew_point: Float,
    @SerializedName("wind_speed")
    val wind_speed: Float?,
    @SerializedName("wind_deg")
    val wind_deg: Int,
    @SerializedName("wind_gust")
    val wind_gust: Float,
    @SerializedName("weather")
    val weather: WeatherWeather,
    @SerializedName("clouds")
    val clouds: Int,
    @SerializedName("pop")
    val pop: Float,
    @SerializedName("rain")
    val rain: Float,
    @SerializedName("uvi")
    val uvi: Float
): Serializable {
}
