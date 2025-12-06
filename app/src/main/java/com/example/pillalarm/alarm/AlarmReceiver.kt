package com.example.pillalarm.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medName = intent.getStringExtra("name") ?: "Medicine Reminder"
        Toast.makeText(context, "Time to take: $medName", Toast.LENGTH_LONG).show()
    }
}
