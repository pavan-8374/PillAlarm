package com.example.pillalarm.alarm

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val medName = intent.getStringExtra("medicineName") ?: "Medicine"
        val alarmId = intent.getIntExtra("alarmId", -1)

        // Simple quick feedback — replace with proper notification building
        val n = NotificationCompat.Builder(context, "pill_alarm_channel")
            // Using system resources:
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            // Using my own app icon:
            .setSmallIcon(com.example.pillalarm.R.drawable.ic_launcher_foreground)
            .setContentTitle("Pill Reminder")
            .setContentText("Time to take: $medName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context).notify(alarmId.takeIf { it >= 0 } ?: System.currentTimeMillis().toInt(), n)
    }
}
