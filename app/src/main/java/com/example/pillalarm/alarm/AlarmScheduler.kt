package com.example.pillalarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

object AlarmScheduler {

   @RequiresApi(Build.VERSION_CODES.S)
    fun schedule(context: Context, name: String, time: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("name", name)
        }

        val pending = PendingIntent.getBroadcast(
            context,
            time.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pending)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pending) // fallback, not exact
        }

    }
}
