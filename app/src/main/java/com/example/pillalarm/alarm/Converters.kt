package com.example.pillalarm.alarm

import androidx.room.TypeConverter


/**
* DayConverters provides custom type conversion for Room database.
* It converts a List<String> (days of the week) to a single String for storage,
* and back to a List<String> when reading from the database.
* This is necessary because Room does not support complex types like List<String> by default.
*/
object DayConverters {

    /**
    * Converts a List<String> into a comma-separated String for storage in the database.
    * @return A single string representing the days of the week ("Mon,Tue").
    * Returns an empty string if the list is null.
    */
    @TypeConverter
    fun fromList(list: List<String>?): String = list?.joinToString(",") ?: ""


    /**
    * Converts a comma-separated String back into a List<String>.
    * @return A list of strings split by commas.
    * Returns an empty list if the string is null or empty.
    */

    @TypeConverter
    fun toList(data: String?): List<String> =
        if (data.isNullOrEmpty()) emptyList() else data.split(",")
}
