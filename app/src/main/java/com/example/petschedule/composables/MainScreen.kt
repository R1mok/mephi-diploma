package com.example.petschedule.composables


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.petschedule.MainActivity
import com.example.petschedule.R
import com.example.petschedule.services.NotificationWorker
import java.util.concurrent.TimeUnit

@Preview
@Composable
fun MainScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background1),
                contentScale = ContentScale.Crop
            )
    ) {
        MainScreen(navController = rememberNavController(), "")
    }
}

@Composable
fun MainScreen(navController: NavController, token: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Управелние питомцами",
            style = TextStyle(fontSize = 25.sp, color = Color.DarkGray),
            modifier = Modifier
                .padding(vertical = 50.dp)
        )
        Button(
            modifier = Modifier
                .offset(y = 300.dp)
                .fillMaxWidth(0.9f),
            onClick = {
                navController.navigate(Screen.UserAccount.withArgs(token))
            },
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.Gray
            ),

            ) {
            Text(
                text = "Управление аккаунтом",
                style = TextStyle(fontSize = 25.sp, color = Color.DarkGray)
            )
        }
        Button(
            modifier = Modifier
                .offset(y = 500.dp)
                .fillMaxWidth(0.9f),
            onClick = {
                navController.navigate(Screen.MyGroups.withArgs(token))
            },
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.Gray
            ),

            ) {
            Text(
                text = "Посмотреть свои группы",
                style = TextStyle(fontSize = 25.sp, color = Color.DarkGray)
            )
        }
        applyNotifications(token)
    }
}
@SuppressLint("RestrictedApi")
fun applyNotifications(token: String) {
    val data = Data.Builder()
    data.putString("token", token)
    val work = PeriodicWorkRequestBuilder<NotificationWorker>(5, TimeUnit.SECONDS)
        .setInputData(data.build()).build()
    workManager.enqueue(work)
}

private val workManager = WorkManager.getInstance(MainActivity.applicationContext())
