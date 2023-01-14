package com.bartoszdrozd.fitapp

import com.bartoszdrozd.fitapp.utils.AbbreviationFormatter
import junit.framework.Assert.assertEquals
import org.junit.Test

class AbbreviationFormatterTest {
    private val formatter = AbbreviationFormatter()

    @Test
    fun under1k_isCorrect() {
        assertEquals("999", formatter.format(999f))
    }

    @Test
    fun exactly1k_isCorrect(){
        assertEquals("1k", formatter.format(1000f))
    }

    @Test
    fun over1k_isCorrect(){
        assertEquals("1.29k", formatter.format(1290f))
    }

    @Test
    fun over10k_isCorrect(){
        assertEquals("10.5k", formatter.format(10500f))
    }

    @Test
    fun exactly_one_before_10k_isCorrect(){
        assertEquals("9.99k", formatter.format(9999f))
    }

    @Test
    fun over100k_isCorrect(){
        assertEquals("120k", formatter.format(120000f))
    }

    @Test
    fun exactly_one_before_1kk_isCorrect(){
        assertEquals("999.99k", formatter.format(999999f))
    }
}