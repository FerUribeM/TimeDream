package com.ferbajoo.timedream

import android.support.v4.util.TimeUtils
import com.ferbajoo.timedream.core.utils.hmsTimeFormatter
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
        assertEquals(4, 2 + 2)
    }

    @Test
    fun formatTime(){
        assertEquals("01:00:00",hmsTimeFormatter(6000))
    }
}
