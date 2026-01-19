package com.example.owlgo.model


import androidx.room.TypeConverter
import java.time.Instant

class Converters {

    @TypeConverter
    fun instantToString(instant: Instant?): String? = instant?.toString()

    @TypeConverter
    fun stringToInstant(string: String?): Instant? = string?.let(Instant::parse)

    @TypeConverter
    fun listToString(list: List<String>): String = list.joinToString("\u0001")

    @TypeConverter
    fun stringToList(string: String): List<String> = string.split("\u0001")
}