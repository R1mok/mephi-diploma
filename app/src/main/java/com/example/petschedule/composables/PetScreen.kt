package com.example.petschedule.composables

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.R
import com.example.petschedule.entities.FeedNote
import com.example.petschedule.entities.Pet
import org.json.JSONArray
import org.json.JSONObject


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
        PetScreen(navController = rememberNavController(), "", "", "Barsik")
    }
}


@OptIn(ExperimentalTextApi::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun PetScreen(navController: NavController, token: String, petId: String, petName: String) {
    if (false) {
        PetScreen(navController, token, petId, petName)
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
        var year: String;
        var month: String;
        var day: String
        var date = ""
        if (petBornDate.value.isNotEmpty()) {
            year = petBornDate.value.substring(0, 4)
            month = petBornDate.value.substring(5, 7)
            day = petBornDate.value.substring(8, 10)
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
        var comment by rememberSaveable { mutableStateOf("") }
        if (isExpandedCreateNote) {
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Уведомление") },
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
                        createFeedNote(notes, token, petId, context, comment)
                        isExpandedCreateNote = false
                        focusManager.clearFocus()
                        comment = ""
                    }
                ),
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
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
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
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