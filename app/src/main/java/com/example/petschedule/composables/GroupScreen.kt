package com.example.petschedule.composables

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.R
import org.json.JSONObject
import java.util.Objects

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
    getPetsFromGroup(token, id)
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
        Text(
            text = "Добавить нового питомца",
            style = TextStyle(fontSize = 25.sp, color = Color.Blue),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(color = Color.White)
                .clickable { isExpandedCreatePet = !isExpandedCreatePet }
        )
        Spacer(modifier = Modifier.padding(vertical = 20.dp))
        if (isExpandedCreatePet) {
            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                label = { Text(text = "Имя питомца") },
                textStyle = TextStyle(fontSize = 25.sp),
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
                    createPet(context, token, id, petName, petType, petGender)
                    isExpandedCreatePet = false
                    // подгрузить список питомцев TODO
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
    }
}

fun createPet(context : Context,
              token : String,
              id : String,
              name : String,
              petType : String,
              petGender : String) {
    val url = "http://localhost:8091/pets/createPet?" +
            "groupId=$id" +
            "&name=$name" +
            "&gender=${petGender.uppercase()}" +
            "&petType=${petType.uppercase()}"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.POST,
        url,
        { },
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
fun getPetsFromGroup(token: String, id: String) {
    if (false) {
        getPetsFromGroup(token, id)
    }
}