package com.example.nasa

import com.example.nasa.util.getHumanReadableTime
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals("29, sep 2023", "2023-09-29".getHumanReadableTime())
    }
}