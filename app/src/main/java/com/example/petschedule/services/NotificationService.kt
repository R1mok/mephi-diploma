package com.example.petschedule.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.petschedule.composables.setOneTimeNotification
import com.example.petschedule.composables.showNotifications
import com.example.petschedule.entities.Notification

class NotificationService(context: Context, token: String) : Service() {
    private var TAG = "NotificationService"
    private var context = context
    private var token = token
    override fun onBind(intent: Intent?): IBinder? {
        checkNotifications(context, token)
        return null
    }

    @SuppressLint("MutableCollectionMutableState")
    fun checkNotifications(
        context: Context,
        token: String
    ) {
        var notifs : MutableState<MutableList<Notification>> = mutableStateOf(mutableListOf())
        showNotifications(context, token)
        for (i in 0 until notifs.value.size) {
            val curNotif = notifs.value.get(i)
            setOneTimeNotification(context, curNotif.groupName, curNotif.petName, curNotif.comment)
        }
    }
}