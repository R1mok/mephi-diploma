package com.example.petschedule.composables

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.MainActivity
import com.example.petschedule.R
import com.example.petschedule.entities.InvitationInGroup
import com.example.petschedule.entities.Notification
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun UserAccountPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background1),
                contentScale = ContentScale.Crop
            )
    ) {
        UserAccount(navController = rememberNavController(), "")
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserAccount(navController: NavController, token: String) {
    if (false)
        navController.navigate(Screen.UserAccount.withArgs(token))

    var groupsInvitation = remember {
        mutableStateOf(mutableListOf<InvitationInGroup>())
    }
    var notificationList = remember {
        mutableStateOf(mutableListOf<Notification>())
    }

    val context = LocalContext.current
    getInvitationInGroup(context, token, groupsInvitation)
    getNotificationList(context, token, notificationList)

    Column(
        modifier = Modifier
            .padding(vertical = 30.dp)
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        val (showDeleteNotificationDialog, setShowDeleteNotificationDialog) = remember {
            mutableStateOf(false)
        }
        Column(modifier = Modifier.fillMaxHeight(0.7f)) {
            Text(
                text = "Текущие уведомления",
                style = TextStyle(fontSize = 25.sp, color = Color.DarkGray),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            var deletedIndex = remember { mutableStateOf(0) }
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 40.dp)
            ) {
                itemsIndexed(notificationList.value) { index, notification ->
                    Button(
                        onClick = {
                            setShowDeleteNotificationDialog(true)
                            deletedIndex.value = index
                        },
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.padding(5.dp).fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Gray
                        )
                    ) {
                        Text(
                            text = "Группа: ${notification.groupName}\n" +
                                    "Питомец: ${notification.petName}\n" +
                                    "Тип уведомления: ${notification.notificationType}\n" +
                                    "Комментарий: ${notification.comment}\n" +
                                    "Время: ${notification.time}",
                            color = Color.DarkGray,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    showDeleteNotificationDialog(
                        showDeleteNotificationDialog,
                        setShowDeleteNotificationDialog,
                        context,
                        token,
                        deletedIndex.value,
                        notificationList
                    )
                }
            }
        }
        Column {
            Text(
                text = "Список приглашений в группы",
                style = TextStyle(fontSize = 25.sp, color = Color.DarkGray),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            if (groupsInvitation.value.size == 0) {
                Text(
                    text = "Нет активных приглашений",
                    style = TextStyle(fontSize = 18.sp, color = Color.DarkGray),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 5.dp)
                )
            }
            val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 40.dp)
            ) {
                items(groupsInvitation.value) { invitation ->
                    Button(
                        onClick = {
                            setShowDialog(true)
                        },
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.padding(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Gray
                        )
                    ) {
                        Text(
                            text = invitation.name,
                            color = Color.DarkGray,
                            fontSize = 30.sp
                        )
                    }
                    showInviteDialog(showDialog, setShowDialog, context, token, invitation.id)
                }
            }
        }
    }
}

fun getInvitationInGroup(
    context: Context,
    token: String,
    groupsInvitation: MutableState<MutableList<InvitationInGroup>>,
) {
    val url = MainActivity.prefixUrl + "/user/invitations"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.GET,
        url,
        { response ->
            val obj = JSONArray(response)
            Log.d("MyLog", "obj length = ${obj.length()}")
            val newInvitations = mutableListOf<InvitationInGroup>()
            for (i in 0 until obj.length()) {
                Log.d("MyLog", "i = $i, value = ${obj.getString(i)}")
                val jsonInvitation = JSONObject(obj.getString(i))
                val groupInfo = jsonInvitation.getString("group")
                val jsonGroup = JSONObject(groupInfo)
                val groupId = jsonGroup.getString("id");
                val groupName = jsonGroup.getString("name");
                newInvitations.add(InvitationInGroup(groupId, groupName))
            }
            groupsInvitation.value = newInvitations
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
                HttpHeaderParser.parseCharset(response.headers)
            )

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

fun getNotificationList(
    context: Context,
    token: String,
    notificationList: MutableState<MutableList<Notification>>
) {
    val url = MainActivity.prefixUrl + "/notifications/show"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = @RequiresApi(Build.VERSION_CODES.O)
    object : StringRequest(
        Method.GET,
        url,
        { response ->
            val obj = JSONArray(response)
            Log.d("MyLog", "obj length = ${obj.length()}")
            val newNotification = mutableListOf<Notification>()
            for (i in 0 until obj.length()) {
                val jsonObject = JSONObject(obj.getString(i))
                Log.d("MyLog", "i = $i, value = $jsonObject")
                val id = jsonObject.getString("id")
                val isTimeout = jsonObject.getString("enabled")
                val notifType = if (isTimeout.equals("true")) "Отсчет" else "Расписание"
                val groupName = jsonObject.getString("groupName")
                val petName = jsonObject.getString("petName")
                val comment = jsonObject.getString("comment")
                val alarmTime = jsonObject.getString("alarmTime")
                var formatter = DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.ENGLISH
                )
                var date = LocalDateTime.parse(alarmTime, formatter)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                newNotification.add(
                    Notification(
                        id, groupName, petName, comment, date.toString(),
                        notifType
                    )
                )
            }
            notificationList.value = newNotification
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
                HttpHeaderParser.parseCharset(response.headers)
            )

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

@Composable
fun showDeleteNotificationDialog(
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    context: Context,
    token: String,
    index: Int,
    notificationList: MutableState<MutableList<Notification>>
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text("Удалить уведомление?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        setShowDialog(false)
                        deleteNotification(context, token, index, notificationList)
                    },
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        setShowDialog(false)
                    },
                ) {
                    Text("Нет")
                }
            },
        )
    }
}

fun deleteNotification(
    context: Context,
    token: String,
    index: Int,
    notificationList: MutableState<MutableList<Notification>>
) {
    val url = MainActivity.prefixUrl + "/notifications/${notificationList.value.get(index).id}"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.DELETE,
        url,
        {
            Log.d("MyLog", "Notification with notificationId:$index and user token: $token deleted")
            getNotificationList(context, token, notificationList)
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

@Composable
fun showInviteDialog(
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    context: Context,
    token: String,
    groupId: String,
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
            },
            title = {
                Text("Приглашение в группу")
            },
            confirmButton = {
                Button(
                    onClick = {
                        setShowDialog(false)
                        acceptInvitation(context, token, groupId)
                    },
                ) {
                    Text("Принять")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        setShowDialog(false)
                    },
                ) {
                    Text("Отклонить")
                }
            },
        )
    }
}

fun acceptInvitation(
    context: Context,
    token: String,
    groupId: String,
) {
    val url = MainActivity.prefixUrl + "/user/accept/$groupId"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.PUT,
        url,
        {
            Log.d("MyLog", "Invitation with groupId:$groupId and user token: $token is accepted")
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