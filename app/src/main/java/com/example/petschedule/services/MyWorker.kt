package com.example.petschedule.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.petschedule.R
import java.util.concurrent.TimeUnit

class MyWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        createNotificationChannel()
        /*setOneTimeNotification("Группа 1", "Питомец 1", "Комментарий")
        */
        return Result.success()
    }

    fun createNotificationChannel() {
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
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun setOneTimeNotification(groupName: String, petName: String, comment: String) {
        val workManager = WorkManager.getInstance(applicationContext)
        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val notificationWorker = OneTimeWorkRequestBuilder<MyWorker>()
            .setInitialDelay(0, TimeUnit.SECONDS)
            .setConstraints(constraint)
            .build()

        workManager.enqueue(notificationWorker)

        workManager.getWorkInfoByIdLiveData(notificationWorker.id)
            .observeForever { workInfo ->
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    createSuccessNotification(groupName, petName, comment)
                }
            }
    }

    private fun createSuccessNotification(groupName: String, petName: String, comment: String) {
        val notificationId = 1
        val builder = NotificationCompat.Builder(applicationContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Группа: $groupName Питомец: $petName")
            .setContentText(comment)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(notificationId, builder.build())
        }
    }
}
