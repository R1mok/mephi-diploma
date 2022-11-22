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
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.R
import com.example.petschedule.entities.User
import com.example.petschedule.entities.UserRegister
import org.json.JSONObject

@Preview
@Composable
fun RegPagePreview() {
    Box(modifier = Modifier
        .fillMaxSize()
        .paint(
            painter = painterResource(id = R.drawable.background),
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
    var user = remember {
        mutableStateOf(UserRegister(login, password, name))
    }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(vertical = 50.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Регистрация",
            style = TextStyle(fontSize = 25.sp, color = Color.Blue),
            modifier = Modifier
                .background(color = Color.White)
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
                style = TextStyle(fontSize = 25.sp, color = Color.Blue),
                modifier = Modifier
                    .background(color = Color.White)
            )
        }
    }
}


private fun register(
    user: MutableState<UserRegister>,
    context: Context,
    navController: NavController
) {
    val url = "http://localhost:8091/user/register"
    val queue = Volley.newRequestQueue(context)
    var status = "400"
    val stringRequest = object : StringRequest(
        Method.POST,
        url,
        { _ ->
            if (status == "200")
                navController.navigate(route = Screen.MainScreen.route)
            Log.d("MyLog", "Response")
        },
        {
            error ->
            Log.d("MyLog", "Error: $error")
        }
    ) {
        override fun getParams(): Map<String, String> {
            val params: MutableMap<String, String> = HashMap()
            params["name"] = user.value.name
            params["login"] = user.value.login
            params["password"] = user.value.password
            return params
        }
        override fun parseNetworkResponse(response: NetworkResponse): Response<String>? {
            status = response.statusCode.toString()
            return super.parseNetworkResponse(response)
        }
    }
    queue.add(stringRequest)
}
