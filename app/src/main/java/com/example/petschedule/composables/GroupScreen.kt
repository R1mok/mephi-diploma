package com.example.petschedule.composables

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
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
import com.example.petschedule.entities.Pet
import com.example.petschedule.entities.User
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*


@Preview
@Composable
fun GroupScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Yellow)
            .paint(
                painter = painterResource(id = R.drawable.background1),
                contentScale = ContentScale.Crop
            )
    ) {
        GroupScreen(navController = rememberNavController(), "", "", "")
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun GroupScreen(navController: NavController, token: String, groupId: String, name: String) {
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
    var members = remember {
        mutableStateOf(mutableListOf<User>())
    }
    getPetsFromGroup(token, groupId, pets, context)

    var isExpandedCreatePet by remember {
        mutableStateOf(false)
    }
    var isExpandedAddPerson by remember {
        mutableStateOf(false)
    }
    var groupMembersList by remember {
        mutableStateOf(false)
    }
    var isExpandedPetGender by remember {
        mutableStateOf(false)
    }
    var isExpandedPetType by remember {
        mutableStateOf(false)
    }

    val mYear: Int
    val mMonth: Int
    val mDay: Int

    val mCalendar = Calendar.getInstance()

    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring a string value to
    // store date in string format
    val mDate = rememberSaveable { mutableStateOf("") }
    getGroupMembersList(context, groupId, token, members)
    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            mDate.value = "$dayOfMonth.${month + 1}.$year"
        }, mYear, mMonth, mDay
    )

    Column(
        modifier = Modifier
            .padding(vertical = 50.dp)
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = name,
            style = TextStyle(fontSize = 25.sp, color = Color.DarkGray),
            modifier = Modifier
                .background(color = Color.Transparent)
                .align(Alignment.CenterHorizontally)
        )
        Button(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 20.dp),
            onClick = {
                isExpandedCreatePet = !isExpandedCreatePet
            },
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Добавить нового питомца",
                style = TextStyle(fontSize = 23.sp, color = Color.DarkGray)
            )
        }
        if (isExpandedCreatePet || pets.value.size == 0) {
            OutlinedTextField(
                value = petName,
                onValueChange = { petName = it },
                label = { Text(text = "Имя питомца") },
                textStyle = TextStyle(fontSize = 25.sp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color.DarkGray,
                    unfocusedLabelColor = Color.DarkGray,
                    cursorColor = Color.Black,
                    focusedBorderColor = Color.DarkGray,
                    backgroundColor = Color.White,
                    unfocusedBorderColor = Color.DarkGray,
                    textColor = Color.DarkGray
                )
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
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
                            color = Color.DarkGray
                        )
                    )
                    Text(
                        text = petType,
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.DarkGray
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
                            text = "Собака",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.DarkGray
                            ),
                            modifier = Modifier
                                .clickable(onClick = {
                                    petType = "Dog"
                                    isExpandedPetType = false
                                })
                        )
                        Text(
                            text = "Кошка",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.DarkGray
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
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
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
                            color = Color.DarkGray
                        )
                    )
                    Text(
                        text = petGender,
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color.DarkGray
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
                            text = "Мужской",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.DarkGray
                            ),
                            modifier = Modifier
                                .clickable(onClick = {
                                    petGender = "Male"
                                    isExpandedPetGender = false
                                })
                        )
                        Text(
                            text = "Женский",
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = Color.DarkGray
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
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            Button(
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Gray
                ),
                onClick = {
                    mDatePickerDialog.show()
                }
            ) {
                Text(
                    text = "Введите дату рождения питомца ${mDate.value}",
                    style = TextStyle(color = Color.DarkGray, fontSize = 20.sp)
                )
            }
            Button(
                onClick = {
                    createPet(pets, context, token, groupId, petName, petType, petGender, mDate)
                    isExpandedCreatePet = false
                    petName = ""
                    petType = ""
                    petGender = ""
                    mDate.value = ""
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
        Button(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 20.dp),
            onClick = {
                isExpandedAddPerson = !isExpandedAddPerson
            },
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Добавить участника",
                style = TextStyle(fontSize = 23.sp, color = Color.DarkGray)
            )
        }
        var requestedUserId = remember {
            mutableStateOf("")
        }
        val focusManager = LocalFocusManager.current
        var userLogin by rememberSaveable { mutableStateOf("") }
        if (isExpandedAddPerson) {
            OutlinedTextField(
                value = userLogin,
                onValueChange = { userLogin = it },
                label = { Text("Логин пользлователя") },
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
                        getUserByLogin(token, userLogin, requestedUserId, context, groupId)
                        isExpandedAddPerson = false
                        focusManager.clearFocus()
                        userLogin = ""
                    }
                ),
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 20.dp),
            onClick = {
                groupMembersList = !groupMembersList
            },
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Посмотреть список участников",
                style = TextStyle(fontSize = 23.sp, color = Color.DarkGray)
            )
        }
        if (groupMembersList) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 40.dp)
            ) {
                items(members.value) { member ->
                    Button(
                        onClick = {
                        },
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.padding(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Gray
                        )
                    ) {
                        Text(
                            text = member.login,
                            color = Color.DarkGray,
                            fontSize = 30.sp
                        )
                    }
                }
            }
        }

        Text(
            text = "Список питомцев",
            style = TextStyle(fontSize = 25.sp, color = Color.DarkGray),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        if (pets.value.size == 0) {
            Text(
                text = "Питомцев пока нет",
                style = TextStyle(fontSize = 18.sp, color = Color.DarkGray),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 5.dp)
            )
        }
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 40.dp)
        ) {
            items(pets.value) { pet ->
                Button(
                    onClick = {
                        navController.navigate(
                            Screen.PetScreen.withArgs(
                                token,
                                pet.id,
                                pet.name,
                                groupId
                            )
                        )
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
                        color = Color.DarkGray,
                        fontSize = 30.sp
                    )
                }
            }
        }
    }
}


fun createPet(
    pets: MutableState<MutableList<Pet>>,
    context: Context,
    token: String,
    id: String,
    name: String,
    petType: String,
    petGender: String,
    bornDate: MutableState<String>
) {
    val url = MainActivity.prefixUrl + "/pets/createPet?" +
            "groupId=$id" +
            "&name=$name" +
            "&gender=${petGender.uppercase()}" +
            "&petType=${petType.uppercase()}" +
            "&bornDate=${bornDate.value}"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.POST,
        url,
        { response ->
            val obj = JSONObject(response)
            Log.d(
                "MyLog", "Create pet:" +
                        " petName:${obj.get("name")}," +
                        " petGender:${obj.get("gender")}, " +
                        " petType:${obj.get("type")}," +
                        " bornDate:${obj.get("bornDate")}"
            )
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

fun getUserByLogin(
    token: String,
    userLogin: String,
    userId: MutableState<String>,
    context: Context,
    groupId: String
) {
    val url = MainActivity.prefixUrl + "/user/getUserByLogin/${userLogin}"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.GET,
        url,
        { response ->
            userId.value = response
            Log.d("MyLog", "UserId for login $userLogin is ${userId.value}")
            inviteToGroup(token, userId.value, context, groupId)
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

fun inviteToGroup(
    token: String,
    userId: String,
    context: Context,
    groupId: String
) {
    val url = MainActivity.prefixUrl + "/groups/$groupId/members/$userId"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.POST,
        url,
        { response ->
            val obj = JSONObject(response)
            val status = obj.getString("status")
            Log.d("MyLog", "Status of inviting userId:$userId in groupId:$groupId is $status")
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

fun getPetsFromGroup(
    token: String,
    id: String,
    pets: MutableState<MutableList<Pet>>,
    context: Context
) {
    val url = MainActivity.prefixUrl + "/pets/byGroup/$id"
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
                        null,
                        jsonPet.get("bornDate").toString()
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

fun getGroupMembersList(
    context: Context,
    groupId: String,
    token: String,
    members: MutableState<MutableList<User>>,
) {
    val url = MainActivity.prefixUrl + "/groups/membersList/$groupId"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.GET,
        url,
        { response ->
            val obj = JSONArray(response)
            Log.d("MyLog", "obj length = ${obj.length()}")
            val membersList = mutableListOf<User>()
            for (i in 0 until obj.length()) {
                Log.d("MyLog", "i = $i, value = ${obj.getString(i)}")
                val jsonUser = JSONObject(obj.getString(i))
                membersList.add(
                    User(jsonUser.getString("id"), jsonUser.getString("login"), "", "", "")
                )
            }
            members.value = membersList
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