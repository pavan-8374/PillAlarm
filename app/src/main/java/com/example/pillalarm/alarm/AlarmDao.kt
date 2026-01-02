package com.example.pillalarm.alarm

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/*
 * Using Room, I have created a Data Access Object (DAO) for the AlarmEntity.
 * This interface defines the methods that can be used to interact with the database.
 * By using DAO objects we can insert, update, delete, and query the database.
 */
@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarms WHERE medicineId = :medicineId")
    suspend fun getAlarmsForMedicine(medicineId: String): List<AlarmEntity>

    @Query("SELECT * FROM alarms")
    suspend fun getAll(): List<AlarmEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmEntity): Long

    @Delete
    suspend fun delete(alarm: AlarmEntity)
}
