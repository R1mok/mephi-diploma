package com.example.petschedule.composables

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.navArgument
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.R
import com.example.petschedule.User
import org.json.JSONException
import org.json.JSONObject


@Composable
fun LoginPage(navController: NavController) {

    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var token by rememberSaveable {
        mutableStateOf("")
    }
    var user = remember {
        mutableStateOf(User(login, password, token))
    }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .paint(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.Crop
            )
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 50.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Вход в аккаунт",
                style = TextStyle(fontSize = 25.sp, color = Color.Blue),
                modifier = Modifier
                    .background(color = Color.White)
                    .align(Alignment.CenterHorizontally)
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 50.dp)
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
                    authLogin(user, context, navController)
                }
            ) {
                Text(
                    text = "Войти",
                    style = TextStyle(fontSize = 25.sp, color = Color.Blue),
                    modifier = Modifier
                        .background(color = Color.White)
                )
            }
        }
    }
}

private fun authLogin(
    user: MutableState<User>,
    context: Context,
    navController: NavController
) {
    val url = "http://localhost:8091/auth/login"

    var status = "403"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.POST,
        url,
        { response ->
            var obj = JSONObject(response)
            user.value.token = obj.getString("token")
            Log.d("MyLog", "Token: ${user.value.token}")
            if (status == "200")
                navController.navigate(route = Screen.MainScreen.route)
        },
        { error ->
            Log.d("MyLog", "Error: $error")
            if (status != "200")
                navController.navigate(Screen.WrongCredentials.withArgs(status))
        }) {
        override fun getBodyContentType(): String {
            return "application/json"
        }

        @Throws(AuthFailureError::class)
        override fun getBody(): ByteArray {
            val params2 = HashMap<String, String>()
            params2["login"] = user.value.login
            params2["password"] = user.value.password
            return JSONObject(params2 as Map<*, *>).toString().toByteArray()
        }

        override fun parseNetworkResponse(response: NetworkResponse): Response<String>? {
            status = response.statusCode.toString()
            return super.parseNetworkResponse(response)
        }
    }
    queue.add(stringRequest)
}