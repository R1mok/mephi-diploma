package com.example.petschedule.composables

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.petschedule.entities.FeedNote
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.LocalTime


@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
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
                navController.navigate(Screen.PetHealth.withArgs(token, petId, petName))
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
                        isExpandedCreateSchedule = false
                        isExpandedCreateTimeout = !isExpandedCreateTimeout
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.4f),
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
                        isExpandedCreateTimeout = false
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
            var isExpandedTimeoutUnits by remember {
                mutableStateOf(false)
            }
            var timeUnitInt by rememberSaveable {
                mutableStateOf(0)
            }
            var timeUnitString by rememberSaveable {
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

                Row(
                    modifier = Modifier
                        .fillMaxHeight(0.4f)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = elapsed,
                        onValueChange = { elapsed = it },
                        label = { Text("Время") },
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
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .fillMaxHeight()
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    Button(
                        onClick = {
                            isExpandedTimeoutUnits = !isExpandedTimeoutUnits
                        },
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Единицы времени: $timeUnitString",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.DarkGray
                            )
                        )
                        DropdownMenu(
                            expanded = isExpandedTimeoutUnits,
                            onDismissRequest = { isExpandedTimeoutUnits = false },
                            modifier = Modifier
                                .background(color = Color.White)
                        ) {
                            Text(
                                text = "Секунды",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    color = Color.DarkGray
                                ),
                                modifier = Modifier
                                    .clickable(onClick = {
                                        timeUnitInt = 1
                                        timeUnitString = "Секунды"
                                        isExpandedTimeoutUnits = false
                                    })
                            )
                            Text(
                                text = "Минуты",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    color = Color.DarkGray
                                ),
                                modifier = Modifier
                                    .clickable(onClick = {
                                        timeUnitInt = 60
                                        timeUnitString = "Минуты"
                                        isExpandedTimeoutUnits = false
                                    })
                            )
                            Text(
                                text = "Часы",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    color = Color.DarkGray
                                ),
                                modifier = Modifier
                                    .clickable(onClick = {
                                        timeUnitInt = 3600
                                        timeUnitString = "Часы"
                                        isExpandedTimeoutUnits = false
                                    })
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        createTimeout(
                            context,
                            token,
                            groupId,
                            timeoutComment,
                            petId,
                            elapsed,
                            timeUnitInt
                        )
                        isExpandedCreateTimeout = false
                        isExpandedCreateNotification = false
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 30.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Gray
                    ),
                ) {
                    Text(
                        text = "Добавить",
                        style = TextStyle(color = Color.DarkGray, fontSize = 20.sp)
                    )
                }
            }
            var scheduleComment by rememberSaveable { mutableStateOf("") }
            if (isExpandedCreateSchedule) {
                OutlinedTextField(
                    value = scheduleComment,
                    onValueChange = { scheduleComment = it },
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
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )
                val dialogTimeState = rememberMaterialDialogState()
                val timeString = rememberSaveable { mutableStateOf("") }
                MaterialDialog(
                    dialogState = dialogTimeState,
                    buttons = {
                        positiveButton("Ок")
                        negativeButton("Отмена")
                    }
                ) {
                    timepicker(title = "Выбрать время уведомления") { time ->
                        //mTime.value = "${time.hour}:${time.minute}"
                        timeString.value = time.toString()
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            dialogTimeState.show()
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
                            text = "Добавить время: ${timeString.value}",
                            style = TextStyle(fontSize = 20.sp, color = Color.DarkGray)
                        )
                    }
                    Button(
                        onClick = {
                            isExpandedCreateSchedule = false
                            isExpandedCreateNotification = false
                            createSchedule(context, MainActivity.token, groupId, petId, scheduleComment, timeString.value)
                            timeString.value = ""
                            scheduleComment = ""
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
                            text = "Добавить",
                            style = TextStyle(fontSize = 20.sp, color = Color.DarkGray)
                        )
                    }
                }
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
                    border = BorderStroke(2.dp, Color.Black),
                    backgroundColor = Color.White
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
    val url = MainActivity.prefixUrl + "/pets/${petId}"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.GET,
        url,
        { response ->
            var obj = JSONObject(response)
            petType.value = if (obj.getString("type").toString() == "DOG") "Собака" else "Кошка"
            petGender.value = if (obj.getString("gender").toString() == "MALE") "Самец" else "Самка"
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
    val url = MainActivity.prefixUrl + "/pets/$petId/feedNotes"
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

fun createFeedNote(
    notes: MutableState<MutableList<FeedNote>>,
    token: String,
    petId: String,
    context: Context,
    comment: String,
) {
    val url = MainActivity.prefixUrl + "/pets/createFeedNote?" +
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
    elapsed: String,
    timeUnit: Int
) {
    val splitedComment = comment.replace(" ", "%20")
    val newElapsed = elapsed.toInt() * timeUnit
    val url = MainActivity.prefixUrl + "/notifications/timeout/?" +
            "groupId=$groupId&comment=$splitedComment&petId=$petId&elapsed=$newElapsed"
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
fun createSchedule(
    context: Context,
    token: String,
    groupId: String,
    petId: String,
    comment: String,
    time: String
) {
    val splitedComment = comment.replace(" ", "%20")
    val url = MainActivity.prefixUrl + "/notifications/schedule/?" +
            "groupId=$groupId&comment=$splitedComment&petId=$petId&time=$time"
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
