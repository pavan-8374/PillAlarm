package com.example.pillalarm.ui.screen

data class AlarmModel(
    val hour: Int,
    val minute: Int,
    val isPM: Boolean,
    val days: List<String>
)
data class Medicine(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val alarms: List<AlarmModel> = emptyList()
)
