package com.example.petschedule.entities

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(
    context: Context,
    userParameters: WorkerParameters
): Worker(context, userParameters) {
    override fun doWork(): Result {
        return try {
            Log.d("MyLog", "notification succeeded")
            Result.success()
        } catch (e : Exception) {
            Result.failure()
        }
    }
}