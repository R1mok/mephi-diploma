package com.example.petschedule.composables


import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.R
import com.example.petschedule.entities.Notification
import com.example.petschedule.services.NotificationReceiver
import org.json.JSONArray
import org.json.JSONObject

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
    val context = LocalContext.current
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
                text = "Информация об аккаунте",
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
        NotificationReceiver().scheduleServiceUpdates(context, token)
    }
}

fun showNotifications(
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
                    jsonNotif.getString("petName"),
                    jsonNotif.getString("groupName"),
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