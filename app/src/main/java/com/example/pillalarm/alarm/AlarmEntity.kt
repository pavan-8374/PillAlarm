package com.example.pillalarm.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: String,  // Firestore document id for the medicine
    val hour: Int,
    val minute: Int,
    val pm: Boolean,
    val days: List<String>   // stored as a list of days internally
)
