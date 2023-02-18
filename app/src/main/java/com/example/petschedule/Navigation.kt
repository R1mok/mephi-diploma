package com.example.petschedule


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.petschedule.composables.*

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
            route = Screen.PetHealth.route + "/{token}" + "/{petId}" + "/{petName}",
            arguments = listOf(
                navArgument("token") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                },
                navArgument("petId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                },
                navArgument ("petName") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                },
            )
        ) { entry ->
            val token = entry.arguments?.getString("token").toString()
            val petId = entry.arguments?.getString("petId").toString()
            val petName = entry.arguments?.getString("petName").toString()
            PetHealth(token, petId, petName)
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
        composable(
            route = Screen.PetScreen.route + "/{token}" + "/{pet_id}" + "/{pet_name}" + "/{group_id}",
            arguments = listOf(
                navArgument("token") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                },
                navArgument("pet_id") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                },
                navArgument("pet_name") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                },
                navArgument("group_id") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                }
            ),
        )
        { entry ->
            val token = entry.arguments?.getString("token").toString()
            val petId = entry.arguments?.getString("pet_id").toString()
            val petName = entry.arguments?.getString("pet_name").toString()
            val groupId = entry.arguments?.getString("group_id").toString()
            PetScreen(navController = navController, token, petId, petName, groupId)
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