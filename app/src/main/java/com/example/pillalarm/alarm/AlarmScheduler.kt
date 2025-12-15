package com.example.pillalarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

object AlarmScheduler {

    fun schedule(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // FIX 1: Use the list directly (AlarmEntity now uses List<String>, not a CSV string)
        val days = alarm.days

        // Compute next trigger time
        val next = calculateNextAlarmTime(alarm.hour, alarm.minute, alarm.pm, days)
            ?: return // If no valid time found (e.g. empty days), do nothing

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarm.id)
            putExtra("medicineName", alarm.medicineId)
        }

        // FIX 2: Simplified flags (minSdk 24 means FLAG_IMMUTABLE is always available)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val pi = PendingIntent.getBroadcast(context, alarm.id, intent, flags)

        // FIX 3: Handle SecurityException for Android 12+ (API 31+)
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pi)
                } else {
                    Log.w("AlarmScheduler", "Permission for exact alarms denied. Alarm not scheduled.")
                    // Optional: Trigger a notification or UI callback to ask for permission
                }
            } else {
                // For Android 11 and below, this permission is granted automatically
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pi)
            }
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Failed to schedule alarm: Permission denied", e)
        }
    }

    fun cancel(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val intent = Intent(context, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(context, alarm.id, intent, flags)

        alarmManager.cancel(pi)
        pi.cancel() // Good practice to cancel the PendingIntent itself too
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

                // Convert 12-hour format to 24-hour for Calendar
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

            // FIX 4: Removed unnecessary '!!' assertion.
            // If 'best' is not null (checked by smart cast), we compare it directly.
            if (best == null || candidate < best) {
                best = candidate
            }
        }

        return best
    }
}