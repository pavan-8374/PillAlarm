package com.example.pillalarm.alarm

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters // Importing the converters


/**
* AlarmDatabase is the main database class for the app.
* It uses Room to provide an abstraction layer over SQLite.
* This database stores Alarm schedules and uses DayConverters for custom type conversion.
**/
@Database(entities = [AlarmEntity::class], version = 1)
@TypeConverters(DayConverters::class) // Converts custom data types (list of days) for Room
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao // This Provides access to the DAO (Data Access Object) for alarms.
    companion object {
        @Volatile // Volatile ensures that the INSTANCE is always up-to-date across threads.
        private var INSTANCE: AlarmDatabase? = null
        fun getInstance(context: Context): AlarmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder( // If the instance doesn't exist, it creates one using Room's databaseBuilder.

                    context.applicationContext,
                    AlarmDatabase::class.java,
                    "alarm_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}