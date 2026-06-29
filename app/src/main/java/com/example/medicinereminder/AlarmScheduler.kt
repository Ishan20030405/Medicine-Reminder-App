package com.example.mad

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

object AlarmScheduler {

    fun scheduleAlarm(context: Context, medicine: Medicine) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return
            }
        }

        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

        try {
            val date = if (medicine.startDate == "Start Date") Date() else dateFormatter.parse(medicine.startDate)
            val time = timeFormatter.parse(medicine.time)

            if (date != null && time != null) {
                val timeCal = Calendar.getInstance()
                timeCal.time = time

                calendar.time = date
                calendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                // If time has already passed today, schedule for tomorrow
                if (calendar.timeInMillis <= System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }

                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("MEDICINE_NAME", medicine.name)
                    putExtra("MEDICINE_QUANTITY", medicine.quantity)
                    putExtra("MEDICINE_UNIT", medicine.unit)
                    putExtra("MEDICINE_TYPE", medicine.type)
                    flags = Intent.FLAG_RECEIVER_FOREGROUND
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    medicine.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
                
                Toast.makeText(context, "Alarm set for ${SimpleDateFormat("hh:mm a, dd MMM").format(calendar.time)}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
