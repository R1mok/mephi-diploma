package com.example.petschedule


import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.petschedule.composables.*
import com.example.petschedule.entities.User

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.LoginPage.route
    ) {
        composable(
            route = Screen.MainScreen.route + "/{token}",
            arguments = listOf(
                navArgument("token") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                }
            ),
        ) { entry ->
            val token = entry.arguments?.getString("token").toString()
            MainScreen(navController = navController, token)
            BackHandler(true) {
            }
        }
        composable(route = Screen.LoginPage.route) {
            LoginPage(navController = navController)
        }
        composable(
            route = Screen.MyGroups.route + "/{token}",
            arguments = listOf(
                navArgument("token") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                }
            ),
        ) { entry ->
            val token = entry.arguments?.getString("token").toString()
            MyGroups(navController = navController, token)
        }
        composable(route = Screen.RegPage.route) {
            RegPage(navController = navController)
        }
        composable(
            route = Screen.GroupScreen.route + "/{token}" + "/{id}" + "/{name}",
            arguments = listOf(
                navArgument("token") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                },
                navArgument("id") {
                    type = NavType.StringType
                    defaultValue = "0"
                    nullable = false
                },
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                }
            )
        ) { entry ->
            val token = entry.arguments?.getString("token").toString()
            val id = entry.arguments?.getString("id").toString()
            val name = entry.arguments?.getString("name").toString()
            GroupScreen(navController = navController, token, id, name)
        }
        composable(route = Screen.UserAccount.route + "/{token}",
            arguments = listOf(
                navArgument("token") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                }
            )
        ) { entry ->
            val token = entry.arguments?.getString("token").toString()
            UserAccount(navController = navController, token)
        }
        dialog(
            route = Screen.LoginBusy.route + "/{login}",
            arguments = listOf(
                navArgument("login") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            ),
            dialogProperties = DialogProperties(
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            )
        ) { entry ->
            entry.arguments?.getString("login")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White),
            ) {
                val text = "Такой логин уже существует"
                Text(
                    text = text,
                    fontSize = 22.sp
                )
            }
        }
        dialog(
            route = Screen.CreateGroup.route + "/{token}",
            arguments = listOf(
                navArgument("token") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                }
            )
        ) { entry ->
            val token = entry.arguments?.getString("token")
            var groupName by rememberSaveable { mutableStateOf("") }
            val context = LocalContext.current
            Column(
                modifier = Modifier
                    .padding(vertical = 50.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Введите имя группы",
                    style = TextStyle(fontSize = 25.sp, color = Color.Blue),
                    modifier = Modifier
                        .background(color = Color.White)
                        .align(Alignment.CenterHorizontally)
                )
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Имя группы") },
                    placeholder = { Text("Имя группы") },
                    textStyle = TextStyle(fontSize = 25.sp),
                )
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
                        createGroup(token.toString(), groupName, context)
                        navController.navigate(Screen.MyGroups.withArgs(token.toString()))
                    }
                ) {
                    Text(
                        text = "Создать группу",
                        style = TextStyle(fontSize = 25.sp, color = Color.Blue),
                        modifier = Modifier
                            .background(color = Color.White)
                    )
                }
            }
        }
        dialog(
            route = Screen.WrongCredentials.route + "/{status}",
            arguments = listOf(
                navArgument("status") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            ),
            dialogProperties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) { entry ->
            val status = entry.arguments?.getString("status")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White),
            ) {
                val text: String
                if (status.equals("403")) {
                    text = "Неверные логин или пароль"
                } else if (status.equals("503")) {
                    text = "Сервер недоступен"
                } else {
                    text = "Пожалуйста, повторите попытку позже"
                }
                Text(
                    text = text,
                    fontSize = 22.sp
                )
            }
        }
    }
}