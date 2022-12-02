package com.example.petschedule.composables


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.petschedule.R
import com.example.petschedule.entities.User

@Preview
@Composable
fun MainScreenPreview() {
    Box(modifier = Modifier
        .fillMaxSize()
        .paint(
            painter = painterResource(id = R.drawable.background),
            contentScale = ContentScale.Crop
        )) {
        MainScreen(navController = rememberNavController(), "")
    }
}

@Composable
fun MainScreen(navController: NavController, token : String) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .offset(y = 100.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(
                text = "Управелние питомцами",
                style = TextStyle(fontSize = 25.sp, color = Color.Blue),
                modifier = Modifier
                    .background(color = Color.White)
                    .padding(5.dp)
            )
        }
        Button(
            modifier = Modifier
                .offset(y = 400.dp)
                .fillMaxWidth(0.9f),
            onClick = {
                navController.navigate(Screen.UserAccount.withArgs(token))
            },
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.Gray
            ),

            ) {
            Text(
                text = "Информация об аккаунте",
                style = TextStyle(fontSize = 25.sp, color = Color.Blue)
            )
        }
        Button(
            modifier = Modifier
                .offset(y = 500.dp)
                .fillMaxWidth(0.9f),
            onClick = {
                navController.navigate(Screen.MyGroups.withArgs(token))
            },
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.Gray
            ),

            ) {
            Text(
                text = "Посмотреть свои группы",
                style = TextStyle(fontSize = 25.sp, color = Color.Blue)
            )
        }
    }
}