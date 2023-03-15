package com.example.petschedule.composables

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
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
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.MainActivity
import com.example.petschedule.R
import com.example.petschedule.entities.Group
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview
@Composable
fun WalkingSchedulePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background1),
                contentScale = ContentScale.Crop
            )
    ) {
        WalkingSchedule(navController = rememberNavController(), "")
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun WalkingSchedule(navController: NavController, token: String) {
    if (false) {
        navController.navigate(Screen.WalkingSchedule.withArgs(token))
    }
    val context = LocalContext.current
    var groups = remember {
        mutableStateOf(mutableListOf<Group>())
    }
    //groups.value.add(0, Group("1", "group1", "0"))
    getGroups(token, context, groups)
    Column(
        modifier = Modifier
            .padding(vertical = 50.dp)
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Группы для выгула на сегодня",
            style = TextStyle(fontSize = 25.sp, color = Color.DarkGray),
            modifier = Modifier
                .align(CenterHorizontally)
        )
        Spacer(modifier = Modifier.padding(vertical = 10.dp))
        if (groups.value.size != 0) {
            Button(
                onClick = { saveGroups(token, context, groups) },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .align(CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Gray
                )
            ) {
                Text(
                    text = "Сохранить",
                    style = TextStyle(fontSize = 18.sp, color = Color.DarkGray),
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }
            LazyColumn(
                horizontalAlignment = CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp)
            ) {
                items(groups.value) { group ->
                    val walkingCount = remember { mutableStateOf(group.walkingCount) }
                    Button(
                        onClick = {
                            navController.navigate(
                                Screen.GroupScreen.withArgs(
                                    token,
                                    group.id,
                                    group.name
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Gray
                        )
                    ) {
                        Column {
                            Button(
                                onClick = {
                                    walkingCount.value =
                                        (walkingCount.value.toInt() + 1).toString()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.White,
                                    contentColor = Color.Gray
                                )
                            ) {
                                Text(
                                    text = "+", color = Color.Black, fontSize = 30.sp
                                )
                            }
                            Button(
                                onClick = {
                                    walkingCount.value =
                                        (walkingCount.value.toInt() - 1).toString()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.White,
                                    contentColor = Color.Gray
                                )
                            ) {
                                Text(
                                    text = "-", color = Color.Black, fontSize = 30.sp
                                )
                            }
                        }
                        Column {
                            Text(
                                modifier = Modifier.align(CenterHorizontally),
                                text = group.name,
                                color = Color.DarkGray,
                                fontSize = 30.sp
                            )
                            Text(
                                modifier = Modifier.align(CenterHorizontally),
                                text = walkingCount.value.toString(),
                                color = Color.DarkGray,
                                fontSize = 30.sp
                            )
                        }
                    }
                    group.walkingCount = walkingCount.value
                }
            }
        } else {
            Text(
                text = "Группы не добавлены",
                style = TextStyle(fontSize = 18.sp, color = Color.DarkGray),
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(vertical = 5.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun saveGroups(
    token: String,
    context: Context,
    groups: MutableState<MutableList<Group>>
) {
    val url = MainActivity.prefixUrl + "/groups/walking"
    val queue = Volley.newRequestQueue(context)
    val jsonBody = StringBuffer()
    jsonBody.append("[")
    for (i in 0 until groups.value.size) {
        jsonBody.append(
            "{" +
                "\"id\":${groups.value[i].id}," +
                " \"name\":\"${groups.value[i].name}\"," +
                " \"walkingCount\":${groups.value[i].walkingCount}" +
                "}")
        if (i != groups.value.size - 1) {
            jsonBody.append(",")
        }
        /*jsonObject.append("id", groups.value[i].id.toLong())
        jsonObject.append("name", groups.value[i].name)
        jsonObject.append("walkingCount", groups.value[i].walkingCount.toInt())
        val params = HashMap<String, String>()
        params["id"] = groups.value[i].id
        params["name"] = groups.value[i].name
        params["walkingCount"] = groups.value[i].walkingCount
        array.put(jsonObject)*/
    }
    jsonBody.append("]")
    /*val stringRequest = object : JsonArrayRequest(
        Method.PUT,
        url,
        array,
        {

        },
        { error ->
            Log.d("MyLogError", "Error is $error")
        }
    ) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = token
            return headers
        }

        override fun getBodyContentType(): String {
            return "application/json"
        }
    }*/
    val stringRequest = object : StringRequest(
        Method.PUT,
        url,
        {
            getGroups(token, context, groups)
        },
        { error ->
            Log.d("MyLog", "Error $error")
        }) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = token
            return headers
        }

        override fun getBodyContentType(): String {
            return "application/json"
        }

        override fun getBody(): ByteArray {
            return jsonBody.toString().toByteArray()
        }
    }
    queue.add(stringRequest)
}