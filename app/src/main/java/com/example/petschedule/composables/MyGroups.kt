package com.example.petschedule.composables

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import androidx.compose.ui.ExperimentalComposeUiApi
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
import com.example.petschedule.entities.Group
import org.json.JSONArray
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset


@Preview
@Composable
fun MyGroupsPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background1),
                contentScale = ContentScale.Crop
            )
    ) {
        MyGroups(navController = rememberNavController(), "")
    }
}


@SuppressLint("MutableCollectionMutableState")
@Composable
fun MyGroups(navController: NavController, token: String) {
    if (false)
        navController.navigate(Screen.MyGroups.route)
    val context = LocalContext.current
    var isCreateGroup by remember {
        mutableStateOf(false)
    }
    var groups = remember {
        mutableStateOf(mutableListOf<Group>())
    }
    var groupName by rememberSaveable { mutableStateOf("") }
    getGroups(token, context, groups)
    Column(
        modifier = Modifier
            .padding(vertical = 50.dp)
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                isCreateGroup = !isCreateGroup
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.Gray
            )
        ) {
            Text(
                text = "Создать группу",
                color = Color.DarkGray,
                fontSize = 25.sp,
            )
        }
        val focusManager = LocalFocusManager.current
        if (isCreateGroup || groups.value.size == 0) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Имя группы") },
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
                            createGroup(token, groupName, context, groups)
                            isCreateGroup = false
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier.padding(vertical = 30.dp)
                        .fillMaxWidth(0.9f)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
        Text(
            text = "Список групп:",
            style = TextStyle(fontSize = 25.sp, color = Color.DarkGray),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        if (groups.value.size == 0) {
            Text(
                text = "Групп пока нет",
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
                .padding(vertical = 20.dp)
        ) {
            items(groups.value) { group ->
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
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.padding(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Gray
                    )
                ) {
                    Text(
                        text = "${group.id}:${group.name}",
                        color = Color.DarkGray,
                        fontSize = 30.sp
                    )
                }
            }
        }
    }
}

private fun getGroups(
    token: String,
    context: Context,
    groups: MutableState<MutableList<Group>>
) {
    val url = MainActivity.prefixUrl + "/groups/"
    val newGroup = mutableListOf<Group>()
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.GET,
        url,
        { response ->
            val obj = JSONArray(response)
            Log.d("MyLog", "obj length = ${obj.length()}")
            for (i in 0 until obj.length()) {
                Log.d("MyLog", "i = $i, value = ${obj.getString(i)}")
                val jsonGroup = JSONObject(obj.getString(i))
                newGroup.add(
                    Group(
                        jsonGroup.get("id").toString(),
                        jsonGroup.get("name").toString()
                    )
                )
            }
            groups.value = newGroup
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
                HttpHeaderParser.parseCharset(response.headers))

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

fun createGroup(
    token: String,
    name: String,
    context: Context,
    groups: MutableState<MutableList<Group>>
) {
    val url = MainActivity.prefixUrl + "/groups/create" +
            "?name=$name"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.POST,
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
    }
    queue.add(stringRequest)
}