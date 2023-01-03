package com.example.petschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.petschedule.services.NotificationWorker
import com.example.petschedule.ui.theme.PetScheduleTheme
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {

    private val workManager = WorkManager.getInstance(application)

    internal fun applyNotifications() {
        workManager.enqueue(PeriodicWorkRequestBuilder<NotificationWorker>(30, TimeUnit.SECONDS).build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetScheduleTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .paint(
                            painter = painterResource(id = R.drawable.background1),
                            contentScale = ContentScale.Crop
                        )
                )
                Navigation()
                applyNotifications()
            }
        }
    }
}