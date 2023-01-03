package com.example.petschedule.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.R
import com.example.petschedule.entities.Notification
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private const val TAG = "NotificationWorker"
class NotificationWorker(
    private val context: Context,
    workerParameters: WorkerParameters): CoroutineWorker (context, workerParameters) {
    override suspend fun doWork(): Result {
        return try {
            showNotifications(context, "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIiLCJpYXQiOjE2NzI3MzQ2ODYsImV4cCI6MTY3MzMzOTQ4Nn0.EDFL1Tx9WlndAsaM1J41u3n6p4N4ccR4jdr3IG8KFac")
            Result.success()
        } catch (t: Throwable) {
            Log.e(TAG, "Error to show notification")
            Result.failure()
        }
    }
    private fun showNotifications(
        context: Context,
        token: String
    ) {

        val url = "http://localhost:8091/notifications/show"
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.GET,
            url,
            { response ->
                val obj = JSONArray(response)
                for (i in 0 until obj.length()) {
                    val jsonNotif = JSONObject(obj.getString(i))
                    val notif = Notification(
                        jsonNotif.getString("groupName"),
                        jsonNotif.getString("petName"),
                        jsonNotif.getString("comment")
                    )
                    setOneTimeNotification(context, notif.groupName, notif.petName, notif.comment)
                }
            },
            { error ->
                Log.d("MyLog", "Error $error")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(stringRequest)
    }
    fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "NotificationChannelName"
            val descriptionText = "NotificationChannelDescriptionText"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun setOneTimeNotification(context: Context, groupName: String, petName: String, comment: String) {
        val workManager = WorkManager.getInstance(context)
        val constraint = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(0, TimeUnit.SECONDS)
            .setConstraints(constraint)
            .build()

        workManager.enqueue(notificationWorker)

        workManager.getWorkInfoByIdLiveData(notificationWorker.id)
            .observeForever { workInfo ->
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    createSuccessNotification(context, groupName, petName, comment)
                }
            }
    }

    private fun createSuccessNotification(context: Context, groupName: String, petName: String, comment: String) {
        val notificationId = 1
        val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Группа: $groupName Питомец: $petName")
            .setContentText(comment)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

}