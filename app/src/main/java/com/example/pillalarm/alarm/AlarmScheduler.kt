package com.example.pillalarm.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

object AlarmScheduler {

    @SuppressLint("ScheduleExactAlarm", "ObsoleteSdkInt")
    fun schedule(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        // This calculates the exact millisecond for the next alarm
        val triggerTime = calculateNextAlarmTime(
            alarm.hour,
            alarm.minute,
            alarm.pm,
            alarm.days
        )

        // If triggerTime is null (e.g., no days selected), we can't schedule anything
        if (triggerTime == null) {
            Log.e("AlarmScheduler", "Skipping alarm: No valid time found.")
            return
        }

        Log.d("AlarmScheduler", "Scheduling alarm for: ${Calendar.getInstance().apply { timeInMillis = triggerTime }.time}")

        // Prepare the Intent
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("medicineName", alarm.medicineName)
            putExtra("medicineImageUrl", alarm.medicineImageUrl)
            putExtra("alarmId", alarm.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the Alarm using 'triggerTime'
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancel(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
    private fun calculateNextAlarmTime(hour12: Int, minute: Int, isPm: Boolean, days: List<String>): Long? {
        if (days.isEmpty()) return null

        val dayMap = mapOf(
            "Sun" to Calendar.SUNDAY,
            "Mon" to Calendar.MONDAY,
            "Tue" to Calendar.TUESDAY,
            "Wed" to Calendar.WEDNESDAY,
            "Thu" to Calendar.THURSDAY,
            "Fri" to Calendar.FRIDAY,
            "Sat" to Calendar.SATURDAY
        )

        val now = Calendar.getInstance()
        var best: Long? = null

        days.mapNotNull { dayMap[it] }.forEach { targetDayOfWeek ->
            val c = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, targetDayOfWeek)

                // This convert 12-hour format to 24-hour for Calendar
                var hour24 = hour12 % 12
                if (isPm) hour24 += 12

                set(Calendar.HOUR_OF_DAY, hour24)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // If the calculated time is in the past, move it to the next week
            if (c.timeInMillis <= now.timeInMillis) {
                c.add(Calendar.DAY_OF_YEAR, 7)
            }

            val candidate = c.timeInMillis

            if (best == null || candidate < best) {
                best = candidate
            }
        }
        return best
    }
}