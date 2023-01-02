package com.example.petschedule.services

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.petschedule.composables.showNotifications
import com.example.petschedule.entities.Notification
import java.util.*

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, NotificationService::class.java))
    }

    @SuppressLint("UnspecifiedImmutableFlag", "ShortAlarm")
    fun scheduleServiceUpdates(context: Context, token: String) {
        var intent = Intent(context, NotificationService::class.java)
        var pendingIntent: PendingIntent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 10)
        val trigger: Long = calendar.getTimeInMillis()

        var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, trigger, 1000, pendingIntent)

        showNotifications(context, token)
    }

}