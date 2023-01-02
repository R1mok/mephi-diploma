package com.example.petschedule.composables

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.from
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.R
import com.example.petschedule.entities.FeedNote
import com.example.petschedule.entities.NotificationWorker
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit


@Preview
@Composable
fun PetScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Yellow)
            .paint(
                painter = painterResource(id = R.drawable.background1),
                contentScale = ContentScale.Crop
            )
    ) {
        PetScreen(navController = rememberNavController(), "", "", "Barsik", "1")
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun PetScreen(
    navController: NavController,
    token: String,
    petId: String,
    petName: String,
    groupId: String
) {
    if (false) {
        PetScreen(navController, token, petId, petName, groupId)
    }
    val context = LocalContext.current
    var petType = remember { mutableStateOf("Cat") }
    var petGender = remember { mutableStateOf("Male") }
    var petDescription = remember { mutableStateOf("") }
    var petBornDate = remember { mutableStateOf("2019.10.20") }
    getPetById(token, petId, petType, petGender, petDescription, petBornDate, context)

    var isExpandedCreateNote by remember {
        mutableStateOf(false)
    }
    var isExpandedCreateNotification by remember {
        mutableStateOf(false)
    }
    var isExpandedCreateTimeout by remember {
        mutableStateOf(false)
    }
    var isExpandedCreateSchedule by remember {
        mutableStateOf(false)
    }

    var notes = remember {
        mutableStateOf(mutableListOf<FeedNote>())
    }
    getPetNotificationsByPetId(notes, token, petId, context)

    createNotificationChannel(context = context)

    Column(
        modifier = Modifier
            .padding(vertical = 50.dp)
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = petName,
            style = TextStyle(fontSize = 25.sp, color = Color.DarkGray),
            modifier = Modifier
                .background(color = Color.Transparent)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.padding(vertical = 20.dp))
        // TODO информация о здоровье питомца, напоминание о кормежке и ветеринарах
        Text(
            text = "Тип питомца: ${petType.value}",
            style = TextStyle(fontSize = 20.sp, color = Color.DarkGray),
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Text(
            text = "Пол питомца: ${petGender.value}",
            style = TextStyle(fontSize = 20.sp, color = Color.DarkGray)
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        var year: String
        var month: String
        var day: String
        var date = ""
        if (petBornDate.value.isNotEmpty()) {
            year = petBornDate.value.substring(0, 4)
            month = petBornDate.value.substring(5, 7)
            day = petBornDate.value.substring(8, 10).toInt().plus(1).toString()
            date = "$day.$month.$year"
        }
        Text(
            text = "Дата рождения питомца: $date",
            style = TextStyle(fontSize = 20.sp, color = Color.DarkGray)
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Button(
            onClick = {
                // TODO переход на страницу статистики здоровья питомца (рост/вес)
                // TODO похода к ветеринару
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Информация о здоровье",
                style = TextStyle(fontSize = 20.sp, color = Color.DarkGray)
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Button(
            onClick = {
                isExpandedCreateNote = !isExpandedCreateNote
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Добавить новую запись",
                style = TextStyle(fontSize = 20.sp, color = Color.DarkGray)
            )
        }
        val focusManager = LocalFocusManager.current
        var noteComment by rememberSaveable { mutableStateOf("") }
        if (isExpandedCreateNote) {
            OutlinedTextField(
                value = noteComment,
                onValueChange = { noteComment = it },
                label = { Text("Запись") },
                textStyle = TextStyle(fontSize = 25.sp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color.DarkGray,
                    unfocusedLabelColor = Color.DarkGray,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.DarkGray,
                    backgroundColor = Color.White,
                    unfocusedBorderColor = Color.DarkGray,
                    textColor = Color.DarkGray
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        createFeedNote(notes, token, petId, context, noteComment)
                        isExpandedCreateNote = false
                        focusManager.clearFocus()
                        noteComment = ""
                    }
                ),
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Button(
            onClick = {
                isExpandedCreateNotification = !isExpandedCreateNotification
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Добавить новое напоминание",
                style = TextStyle(fontSize = 20.sp, color = Color.DarkGray)
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        if (isExpandedCreateNotification) {
            Row {
                Button(
                    onClick = {
                        isExpandedCreateTimeout = !isExpandedCreateTimeout
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Отсчет",
                        style = TextStyle(fontSize = 20.sp, color = Color.DarkGray)
                    )
                }
                Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                Button(
                    onClick = {
                        isExpandedCreateSchedule = !isExpandedCreateSchedule
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Расписание",
                        style = TextStyle(fontSize = 20.sp, color = Color.DarkGray)
                    )
                }
            }
            var timeoutComment by rememberSaveable { mutableStateOf("") }
            var elapsed by rememberSaveable {
                mutableStateOf("")
            }
            if (isExpandedCreateTimeout) {
                OutlinedTextField(
                    value = timeoutComment,
                    onValueChange = { timeoutComment = it },
                    label = { Text("Сообщение в напоминании") },
                    textStyle = TextStyle(fontSize = 25.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray,
                        cursorColor = Color.Black,
                        focusedBorderColor = Color.DarkGray,
                        backgroundColor = Color.White,
                        unfocusedBorderColor = Color.DarkGray,
                        textColor = Color.DarkGray
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )


                OutlinedTextField(
                    value = elapsed,
                    onValueChange = { elapsed = it },
                    label = { Text("Время срабатывания (в секундах)") },
                    textStyle = TextStyle(fontSize = 25.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray,
                        cursorColor = Color.Black,
                        focusedBorderColor = Color.DarkGray,
                        backgroundColor = Color.White,
                        unfocusedBorderColor = Color.DarkGray,
                        textColor = Color.DarkGray
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            createTimeout(context, token, groupId, timeoutComment, petId, elapsed)
                            isExpandedCreateTimeout = false
                            focusManager.clearFocus()
                            elapsed = ""
                        }
                    ),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
            }
            //var scheduleComment by rememberSaveable { mutableStateOf("") }
            if (isExpandedCreateSchedule) {

            }
        }
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Text(
            text = "Список записей",
            style = TextStyle(fontSize = 22.sp, color = Color.DarkGray),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        if (notes.value.size == 0) {
            Text(
                text = "Записей пока нет",
                style = TextStyle(fontSize = 18.sp, color = Color.DarkGray),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 5.dp)
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp)
        ) {
            items(notes.value) { note ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    border = BorderStroke(1.dp, Color.White),
                    backgroundColor = Color.Transparent
                ) {
                    Text(
                        text = note.comment,
                        color = Color.DarkGray,
                        fontSize = 30.sp,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
    }
}

fun getPetById(
    token: String,
    petId: String,
    petType: MutableState<String>,
    petGender: MutableState<String>,
    petDescription: MutableState<String>,
    petBornDate: MutableState<String>,
    context: Context
) {
    val url = "http://localhost:8091/pets/${petId}"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.GET,
        url,
        { response ->
            var obj = JSONObject(response)
            petType.value = obj.getString("type").toString()
            petGender.value = obj.getString("gender").toString()
            petDescription.value = obj.getString("description").toString()
            petBornDate.value = obj.getString("bornDate").toString()
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

fun getPetNotificationsByPetId(
    notes: MutableState<MutableList<FeedNote>>,
    token: String,
    petId: String,
    context: Context
) {
    val url = "http://localhost:8091/pets/$petId/feedNotes"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.GET,
        url,
        { response ->
            val obj = JSONArray(response)
            Log.d("MyLog", "obj length = ${obj.length()}")
            val newNotes = mutableListOf<FeedNote>()
            for (i in 0 until obj.length()) {
                Log.d("MyLog", "i = $i, value = ${obj.getString(i)}")
                val jsonNote = JSONObject(obj.getString(i))
                newNotes.add(
                    FeedNote(
                        "",
                        "",
                        jsonNote.get("comment").toString()
                    )
                )
            }
            notes.value = newNotes
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

fun createFeedNote(
    notes: MutableState<MutableList<FeedNote>>,
    token: String,
    petId: String,
    context: Context,
    comment: String,
) {
    val url = "http://localhost:8091/pets/createFeedNote?" +
            "petId=$petId&comment=$comment"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.POST,
        url,
        {
            getPetNotificationsByPetId(notes, token, petId, context)

        },
        { }) {
        override fun getBodyContentType(): String {
            return "application/json"
        }

        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = token
            return headers
        }
    }
    queue.add(stringRequest)
}

fun createTimeout(
    context: Context,
    token: String,
    groupId: String,
    comment: String,
    petId: String,
    elapsed: String
) {
    val url = "http://localhost:8091/notifications/timeout/?" +
            "groupId=$groupId&comment=$comment&petId=$petId&elapsed=$elapsed"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.POST,
        url,
        { },
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

private fun createNotificationChannel(context: Context) {
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
        .setContentTitle("Группа: $groupName Питомце: $petName")
        .setContentText(comment)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}
