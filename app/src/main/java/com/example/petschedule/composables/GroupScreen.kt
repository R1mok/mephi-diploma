package com.example.petschedule.composables

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.petschedule.entities.Group
import com.example.petschedule.entities.Pet
import org.json.JSONArray
import org.json.JSONObject

@Preview
@Composable
fun GroupScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.Crop
            )
    ) {
        GroupScreen(navController = rememberNavController(), "", "", "")
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun GroupScreen(navController: NavController, token: String, id: String, name: String) {
    if (false) {
        navController.navigate(Screen.GroupScreen.withArgs(token, id, name))
    }
    val context = LocalContext.current
    var petName by rememberSaveable { mutableStateOf("") }
    var petType by rememberSaveable {
        mutableStateOf("")
    }
    var petGender by rememberSaveable {
        mutableStateOf("")
    }
    var pets = remember {
        mutableStateOf(mutableListOf<Pet>())
    }
    getPetsFromGroup(token, id, pets, context)

    var isExpandedCreatePet by remember {
        mutableStateOf(false)
    }
    var isExpandedPetGender by remember {
        mutableStateOf(false)
    }
    var isExpandedPetType by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .padding(vertical = 50.dp)
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = name,
            style = TextStyle(fontSize = 25.sp, color = Color.Blue),
            modifier = Modifier
                .background(color = Color.White)
                .align(Alignment.CenterHorizontally)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 10.dp),
            onClick = {
                      isExpandedCreatePet = !isExpandedCreatePet
            },
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.Gray
            ),

            ) {
            Text(
                text = "Добавить нового питомца",
                style = TextStyle(fontSize = 20.sp, color = Color.Blue)
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        if (isExpandedCreatePet) {
            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                label = { Text(text = "Имя питомца") },
                textStyle = TextStyle(fontSize = 25.sp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color.Blue,
                    unfocusedLabelColor = Color.Blue,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.Blue,
                    backgroundColor = Color.White,
                    unfocusedBorderColor = Color.Blue,
                    textColor = Color.Blue)
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { isExpandedPetType = !isExpandedPetType },
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Gray
                    )
                ) {
                    Text(
                        text = "Выбрать тип питомца: ",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Blue
                        )
                    )
                    Text(
                        text = petType,
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Blue
                        ),
                        modifier = Modifier
                            .background(color = Color.White)
                            .padding(horizontal = 5.dp)
                    )
                    DropdownMenu(
                        expanded = isExpandedPetType,
                        onDismissRequest = { isExpandedPetType = false },
                        modifier = Modifier
                            .background(color = Color.White)
                            .fillMaxWidth(0.2f)
                    ) {
                        Text(
                            text = "Dog",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.Blue
                            ),
                            modifier = Modifier
                                .clickable(onClick = {
                                    petType = "Dog"
                                    isExpandedPetType = false
                                })
                        )
                        Text(
                            text = "Cat",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.Blue
                            ),
                            modifier = Modifier
                                .clickable(onClick = {
                                    petType = "Cat"
                                    isExpandedPetType = false
                                })
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { isExpandedPetGender = !isExpandedPetGender },
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Gray
                    )
                ) {
                    Text(
                        text = "Выбрать пол питомца: ",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Blue
                        )
                    )
                    Text(
                        text = petGender,
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.Blue
                        ),
                        modifier = Modifier
                            .background(color = Color.White)
                            .padding(horizontal = 5.dp)
                    )
                    DropdownMenu(
                        expanded = isExpandedPetGender,
                        onDismissRequest = { isExpandedPetGender = false },
                        modifier = Modifier
                            .background(color = Color.White)
                            .fillMaxWidth(0.2f)
                    ) {
                        Text(
                            text = "Male",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.Blue
                            ),
                            modifier = Modifier
                                .clickable(onClick = {
                                    petGender = "Male"
                                    isExpandedPetGender = false
                                })
                        )
                        Text(
                            text = "Female",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.Blue
                            ),
                            modifier = Modifier
                                .clickable(onClick = {
                                    petGender = "Female"
                                    isExpandedPetGender = false
                                })
                        )
                    }
                }
            }

            Button(
                onClick = {
                    createPet(pets, context, token, id, petName, petType, petGender)
                    isExpandedCreatePet = false
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
                    color = Color.Blue,
                    fontSize = 25.sp,
                )
            }
        }
        Text(
            text = "Список питомцев",
            style = TextStyle(fontSize = 25.sp, color = Color.Blue),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(color = Color.White)
        )
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 40.dp)
        ) {
            items(pets.value) { pet ->
                Button(
                    onClick = {
                        //navController.navigate(Screen.GroupScreen.withArgs(token, group.id, group.name))
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.padding(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Gray
                    )
                ) {
                    Text(
                        text = "${pet.id}: ${pet.name}",
                        color = Color.Blue,
                        fontSize = 30.sp
                    )
                }
            }
        }
    }
}


fun createPet(
    pets : MutableState<MutableList<Pet>>,
    context: Context,
    token: String,
    id: String,
    name: String,
    petType: String,
    petGender: String
) {
    val url = "http://localhost:8091/pets/createPet?" +
            "groupId=$id" +
            "&name=$name" +
            "&gender=${petGender.uppercase()}" +
            "&petType=${petType.uppercase()}"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.POST,
        url,
        { response ->
            val obj = JSONObject(response)
            Log.d("MyLog", "obj length = ${obj.length()}")
            getPetsFromGroup(token, id, pets, context)

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

fun getPetsFromGroup(
    token: String,
    id: String,
    pets: MutableState<MutableList<Pet>>,
    context: Context
) {
    val url = "http://localhost:8091/pets/byGroup/$id"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.GET,
        url,
        { response ->
            val obj = JSONArray(response)
            Log.d("MyLog", "obj length = ${obj.length()}")
            val newPets = mutableListOf<Pet>()
            for (i in 0 until obj.length()) {
                Log.d("MyLog", "i = $i, value = ${obj.getString(i)}")
                val jsonPet = JSONObject(obj.getString(i))
                newPets.add(
                    Pet(
                        jsonPet.get("id").toString(),
                        jsonPet.get("name").toString(),
                        jsonPet.get("type").toString(),
                        jsonPet.get("gender").toString(),
                        null,
                        null
                    )
                )
            }
            pets.value = newPets
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