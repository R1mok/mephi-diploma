package com.example.petschedule.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.MainActivity
import com.example.petschedule.R
import com.example.petschedule.entities.Notification
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

private const val TAG = "NotificationWorker"

class NotificationWorker(
    context: Context,
    workerParameters: WorkerParameters): CoroutineWorker (context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val token =  inputData.getString("token")
            if (token != null) {
                if (token == "") {
                    return Result.success()
                }
                showNotifications(context = applicationContext, token)
            }
            Result.success()
        } catch (t: Throwable) {
            Log.e(TAG, "Error to show notification")
            Result.failure()
        }
    }
    private fun deleteReceivedNotification(context: Context, id: String, token: String) {
        val url = "http://localhost:8091/notifications/$id"
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.DELETE,
            url,
            {
                Log.d("MyLog", "Notification deleted, id: $id")
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
    private fun showNotifications(context: Context, token: String) {
        val url = "http://localhost:8091/notifications/show"
        val queue = Volley.newRequestQueue(context)
        val stringRequest = object : StringRequest(
            Method.GET,
            url,
            { response ->
                val obj = JSONArray(response)
                for (i in 0 until obj.length()) {
                    val jsonNotif = JSONObject(obj.getString(i))
                    val groupName = jsonNotif.getString("groupName")
                    val petName = jsonNotif.getString("petName")
                    val comment = jsonNotif.getString("comment")
                    Log.d("MyLog", "Notification received: groupName: $groupName, petName: $petName, comment: $comment")
                    createNotificationChannel()
                    setOneTimeNotification(groupName, petName, comment)
                    deleteReceivedNotification(context, jsonNotif.getString("id"), token)
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
            override fun parseNetworkResponse(
                response: NetworkResponse
            ): Response<String> {
                var parsed: String

                val encoding = charset(
                    HttpHeaderParser.parseCharset(response.headers))

                try {
                    parsed = String(response.data, encoding)
                    val bytes = parsed.toByteArray(encoding)
                    parsed = String(bytes, Charset.forName("UTF-8"))

                    return Response.success(
                        parsed,
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
                } catch (e: UnsupportedEncodingException) {
                    return Response.error(ParseError(e))
                }
            }
        }
        queue.add(stringRequest)
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

        val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
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