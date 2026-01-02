package com.example.pillalarm.alarm

import android.content.Context

/**
 * AlarmRepository acts as a data access layer between the ViewModel/UI and the Room database.
 * It encapsulates database operations and provides a clean API for managing alarms.
 */
class AlarmRepository(context: Context) {
    private val db = AlarmDatabase.getInstance(context) // Get the singleton instance of the Room database
    private val dao = db.alarmDao() // Access the DAO (Data Access Object) for alarm operations


     // Fetches all alarms associated with a specific medicine id.
    suspend fun getAlarmsForMedicine(medicineId: String): List<AlarmEntity> =
        dao.getAlarmsForMedicine(medicineId)

    // To Insert a new alarm into the database.
    suspend fun addAlarm(alarm: AlarmEntity): Long = dao.insert(alarm)

    // To Delete an existing alarm from the database.
    suspend fun deleteAlarm(alarm: AlarmEntity) = dao.delete(alarm)
}
