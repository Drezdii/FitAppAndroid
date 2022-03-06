package com.bartoszdrozd.fitapp.utils

import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class RoomTypeConverters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(date: String): LocalDate = LocalDate.parse(date)

    @TypeConverter
    fun toInstant(dateTime: String?): Instant? {
        return if (dateTime == null) {
            null
        } else {
            Instant.parse(dateTime)
        }
    }

    @TypeConverter
    fun fromLocalDateTime(dateTime: Instant?): String? = dateTime?.toString()
}