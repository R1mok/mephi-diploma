package com.example.petschedule


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.petschedule.composables.*

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.LoginPage.route
    ) {
        composable(route = Screen.MainScreen.route) {
            BackHandler(true) {
            }
            MainScreen(navController = navController)
        }
        composable(route = Screen.LoginPage.route) {
            LoginPage(navController = navController)
        }
        composable(route = Screen.MyGroups.route) {
            MyGroups()
        }
        composable(route = Screen.RegPage.route) {
            RegPage(navController = navController)
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
        ) {
            entry -> entry.arguments?.getString("login")
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
        ) {
            entry -> val status = entry.arguments?.getString("status")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White),
            ) {
                val text : String
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