package com.bartoszdrozd.fitapp.utils

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.floor

class AbbreviationFormatter {
    private val df: DecimalFormat = DecimalFormat("#.##")

    init {
        df.roundingMode = RoundingMode.HALF_UP
    }

    fun format(number: Float): String {
        if (number <= 999) {
            return df.format(number)
        }

        if (number <= 999999) {
            val rounded = floor(
                ((number / 1000) * 100).toDouble()
            ) / 100.0;

            return df.format(rounded) + "k"
        }

        return "NaN"
    }
}