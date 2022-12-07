package com.example.petschedule.composables

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import com.example.petschedule.entities.FeedNote
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


@SuppressLint("MutableCollectionMutableState")
@Composable
fun PetScreen(navController: NavController, token: String, petId: String, petName : String) {
    if (false) {
        PetScreen(navController, token, petId, petName)
    }
    val context = LocalContext.current
    var petType = remember { mutableStateOf("") }
    var petGender = remember { mutableStateOf("") }
    var petDescription = remember { mutableStateOf("") }
    var petBornDate = remember { mutableStateOf("") }
    var notes = remember {
        mutableStateOf(mutableListOf<FeedNote>())
    }
    getPetById(token, petId, petType, petGender, petDescription, petBornDate, context)

    Column(
        modifier = Modifier
            .padding(vertical = 50.dp)
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = petName,
            style = TextStyle(fontSize = 25.sp, color = Color.Blue),
            modifier = Modifier
                .background(color = Color.Transparent)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.padding(vertical = 20.dp))
        // TODO информация о здоровье питомца, напоминание о кормежке и ветеринарах
        Text(
            text = "Тип питомца: ${petType.value}",
            style = TextStyle(fontSize = 20.sp, color = Color.Blue),
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Text(
            text = "Пол питомца: ${petGender.value}",
            style = TextStyle(fontSize = 20.sp, color = Color.Blue)
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Text(
            text = "Дата рождения питомца: ${petBornDate.value}",
            style = TextStyle(fontSize = 20.sp, color = Color.Blue)
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Button(
            onClick = {
                // TODO переход на страницу статистики здоровья питомца (рост/вес)
            },
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.White
            )) {
            Text(
                text = "Информация о здоровье питомца",
                style = TextStyle(fontSize = 20.sp, color = Color.Blue)
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        Text(
            text = "Список ближайших уведомлений",
            style = TextStyle(fontSize = 20.sp, color = Color.Blue),
        )
        LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp)
        ) {
            items(notes.value) {
                // TODO сделать ближайшие уведомления питомца
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
    petBornDate : MutableState<String>,
    context: Context) {
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
            petBornDate.value = obj.getString("age").toString()
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