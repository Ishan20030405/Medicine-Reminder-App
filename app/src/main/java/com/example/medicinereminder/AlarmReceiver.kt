package com.example.mad

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("MEDICINE_NAME") ?: "Medicine"
        val medicineQuantity = intent.getStringExtra("MEDICINE_QUANTITY") ?: "1"
        val medicineUnit = intent.getStringExtra("MEDICINE_UNIT") ?: "Tablet"
        val medicineType = intent.getStringExtra("MEDICINE_TYPE") ?: "Tablet"
        
        // Show Notification and Launch Alarm Activity via Full Screen Intent
        showNotification(context, medicineName, medicineQuantity, medicineUnit, medicineType)
        
        // Start Alarm Sound
        AlarmSoundManager.play(context)
    }

    private fun showNotification(
        context: Context, 
        medicineName: String, 
        quantity: String, 
        unit: String, 
        type: String
    ) {
        val channelId = "medicine_reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Medicine Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(null, null) 
                enableLights(true)
                enableVibration(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent for the Full Screen Intent (Triggers MainActivity)
        val fullScreenIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("MEDICINE_NAME", medicineName)
            putExtra("MEDICINE_QUANTITY", quantity)
            putExtra("MEDICINE_DETAILS", "$quantity $unit ($type)")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            medicineName.hashCode(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Medicine Reminder")
            .setContentText("It's time to take your $medicineName")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()

        notificationManager.notify(1001, notification)
    }
}
