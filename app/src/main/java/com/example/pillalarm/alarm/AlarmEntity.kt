package com.example.pillalarm.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicineId: String,  // Firestore document id for the medicine
    val medicineName: String,  // Name of the medicine
    val medicineImageUrl: String, // URL of the medicine's image
    val hour: Int,               // Time in 12-hour format
    val minute: Int,            // Time in Minutes (0-59)
    val pm: Boolean,            // true if PM, false if AM
    val days: List<String>   // stored as a list of days internally
)
