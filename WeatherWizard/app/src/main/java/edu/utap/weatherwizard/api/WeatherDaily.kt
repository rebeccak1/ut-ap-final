package edu.utap.weatherwizard.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WeatherTemp(
    @SerializedName("day")
    val day: Double,
    @SerializedName("min")
    var min: Double,
    @SerializedName("max")
    var max: Double,
    @SerializedName("night")
    val night: Double,
    @SerializedName("eve")
    val eve: Double,
    @SerializedName("morn")
    val morn: Double
)

data class WeatherFeelsLike(
    @SerializedName("day")
    val day: Double,
    @SerializedName("night")
    val night: Double,
    @SerializedName("eve")
    val eve: Double,
    @SerializedName("morn")
    val morn: Double
)

data class WeatherWeatherData(
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
    val moonset: Int,
    @SerializedName("moon_phase")
    val moon_phase: Double,
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
    val dew_point: Double,
    @SerializedName("wind_speed")
    val wind_speed: Double,
    @SerializedName("wind_deg")
    val wind_deg: Int,
    @SerializedName("wind_gust")
    val wind_gust: Double,
    @SerializedName("weather")
    val weather: List<WeatherWeatherData>,
    @SerializedName("clouds")
    val clouds: Int,
    @SerializedName("pop")
    val pop: Double,
//    @SerializedName("rain")
//    val rain: Double =0.0,
    @SerializedName("uvi")
    val uvi: Double
): Serializable {
}
