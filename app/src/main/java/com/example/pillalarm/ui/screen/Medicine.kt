package com.example.pillalarm.ui.screen

data class Medicine(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0,
    val alarmTime: Long = 0,
    val userId: String = ""
)