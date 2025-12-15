package com.example.pillalarm.alarm

import androidx.room.TypeConverter

object DayConverters {
    @TypeConverter
    fun fromList(list: List<String>?): String = list?.joinToString(",") ?: ""

    @TypeConverter
    fun toList(data: String?): List<String> =
        if (data.isNullOrEmpty()) emptyList() else data.split(",")
}
