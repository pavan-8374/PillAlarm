package com.example.pillalarm.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "pill_alarm_channel"
    }

    @SuppressLint("FullScreenIntentPolicy")
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val medName = intent.getStringExtra("medicineName") ?: "Medicine"
        val medImage = intent.getStringExtra("medicineImageUrl")
        val alarmId = intent.getIntExtra("alarmId", -1)

        // Create the Full Screen Intent (AlarmActivity)
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
            putExtra("medicineName", medName)
            putExtra("medicineImageUrl", medImage)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            alarmId,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the Channel (Fix for 'Unresolved reference')
        val channelId = "pill_alarm_channel"
        createNotificationChannel(context)

        // Build the Notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Pill Alarm")
            .setContentText("Time to take $medName")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setFullScreenIntent(fullScreenPendingIntent, true) // Wakes screen

        // Show Notification
        val notificationManager = NotificationManagerCompat.from(context)

        // Check permission for POST_NOTIFICATIONS (Android 13+)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(alarmId, notificationBuilder.build())
        }
    }


    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Medicine Alarm"
            val descriptionText = "High priority alarm for medicine reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH // Must be HIGH/MAX for full screen intent

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}