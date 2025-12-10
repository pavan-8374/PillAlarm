package com.example.pillalarm.ui.screen

import android.annotation.SuppressLint

data class AlarmModel(
    val hour: Int,
    val minute: Int,
    val isPM: Boolean,
    val days: List<String>
) {
    val formattedTime: String
        @SuppressLint("DefaultLocale")
        get() = String.format("%02d:%02d %s",
            if (hour == 0 || hour == 12) 12 else hour % 12,
            minute,
            if (isPM) "PM" else "AM"
        )

    val formattedDays: String
        get() = days.joinToString(" ")
}

data class Medicine(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val alarms: List<AlarmModel> = emptyList()
)
