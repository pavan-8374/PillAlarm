package com.example.pillalarm.alarm

import android.content.Context

class AlarmRepository(context: Context) {

    private val db = AlarmDatabase.getInstance(context)
    private val dao = db.alarmDao()

    suspend fun getAlarmsForMedicine(medicineId: String): List<AlarmEntity> =
        dao.getAlarmsForMedicine(medicineId)

    suspend fun addAlarm(alarm: AlarmEntity): Long = dao.insert(alarm)

    suspend fun deleteAlarm(alarm: AlarmEntity) = dao.delete(alarm)
}
