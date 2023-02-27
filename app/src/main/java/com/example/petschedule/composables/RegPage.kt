package com.example.petschedule.composables

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.MainActivity
import com.example.petschedule.R
import com.example.petschedule.entities.User

@Preview
@Composable
fun RegPagePreview() {
    Box(modifier = Modifier
        .fillMaxSize()
        .paint(
            painter = painterResource(id = R.drawable.background1),
            contentScale = ContentScale.Crop
        )) {
        RegPage(navController = rememberNavController())
    }
}

@Composable
fun RegPage(navController: NavController) {
    var login by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val user = remember {
        mutableStateOf(User(login, password, name, ""))
    }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(vertical = 50.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Регистрация",
            style = TextStyle(fontSize = 25.sp, color = Color.DarkGray),
            modifier = Modifier
                .background(color = Color.Transparent)
                .align(Alignment.CenterHorizontally)
        )
        Row(
            modifier = Modifier
                .padding(vertical = 25.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            OutlinedTextField(
                value = login,
                label = { Text(text = "Введите логин") },
                textStyle = TextStyle(fontSize = 25.sp),
                onValueChange = {
                    login = it
                },
            )
        }
        Row(
            modifier = Modifier
                .padding(vertical = 25.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            OutlinedTextField(
                value = name,
                label = { Text(text = "Введите имя") },
                textStyle = TextStyle(fontSize = 25.sp),
                onValueChange = {
                    name = it
                },
            )
        }
        Row(
            modifier = Modifier
                .padding(vertical = 25.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                singleLine = true,
                placeholder = { Text("Пароль") },
                textStyle = TextStyle(fontSize = 25.sp),
                visualTransformation =
                if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    // Please provide localized description for accessibility services
                    val description =
                        if (passwordVisible)
                            "Скрыть пароль"
                        else
                            "Показать пароль"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                }
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 50.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.Gray
            ),
            onClick = {
                Log.d(
                    "MyLog", "Login: ${login}," +
                            " password: ${password}"
                )
                user.value.login = login
                user.value.password = password
                user.value.name = name
                register(user, context, navController)
            }
        ) {
            Text(
                text = "Зарегистрироваться",
                style = TextStyle(fontSize = 25.sp, color = Color.DarkGray),
                modifier = Modifier
                    .background(color = Color.Transparent)
            )
        }
    }
}


private fun register(
    user: MutableState<User>,
    context: Context,
    navController: NavController
) {
    val url = MainActivity.prefixUrl + "/user/register"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.POST,
        url,
        { _ ->
            authLogin(user, context, navController)
            Log.d("MyLog", "Registered user: ${user.value.login}")
        },
        {
            error ->
            if (error.networkResponse == null)
                navController.navigate(Screen.WrongCredentials.withArgs("503"))
            else if (error.networkResponse.statusCode == 400)
                navController.navigate(Screen.LoginBusy.withArgs(user.value.login))
            Log.d("MyLog", "Error: $error login: ${user.value.login}")
        }
    ) {
        override fun getParams(): Map<String, String> {
            val params: MutableMap<String, String> = HashMap()
            params["name"] = user.value.name
            params["login"] = user.value.login
            params["pass"] = user.value.password
            return params
        }
    }
    queue.add(stringRequest)
}

