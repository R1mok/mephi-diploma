package com.example.petschedule

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petschedule.ui.theme.PetScheduleTheme
import kotlin.math.log

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Login()
        }
    }
}

@Preview
@Composable
private fun Login() {
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

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
            Row(modifier = Modifier
                .padding(vertical = 50.dp)
                .align(Alignment.CenterHorizontally)) {
                OutlinedTextField(
                    value = login,
                    label = {Text(text = "Введите логин")},
                    textStyle = TextStyle(fontSize = 25.sp),
                    onValueChange = {
                        login = it
                    },
                )
            }
            Row(modifier = Modifier
                .align(Alignment.CenterHorizontally)) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    singleLine = true,
                    placeholder = { Text("Пароль") },
                    visualTransformation =
                    if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        // Please provide localized description for accessibility services
                        val description =
                            if (passwordVisible)
                                "Скрыть пароль"
                            else "Показать пароль"

                        IconButton(onClick = {passwordVisible = !passwordVisible}){
                            Icon(imageVector  = image, description)
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
                    Log.d("MyLog", "Login: ${login}," +
                            " password: ${password}")
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

