package edu.cs371m.weatherwizard

import android.util.Log
import edu.cs371m.reddit.api.WeatherApi
import edu.cs371m.reddit.api.WeatherRepository
import kotlinx.coroutines.runBlocking
import org.bouncycastle.util.test.SimpleTest.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


class ExampleUnitTest {


    @Test
    fun start0() = runBlocking {
        val weatherApi = WeatherApi.create()
        val repository = WeatherRepository(weatherApi)
        val response = repository.getWeather("33.44", "-94.04")
        assertEquals("1", response.data.toString())
    }

}

//@RunWith(
//    RobolectricTestRunner::class
//)