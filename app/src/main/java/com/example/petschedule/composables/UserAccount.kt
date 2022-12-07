package com.example.petschedule.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.petschedule.R
import com.example.petschedule.entities.User

@Preview
@Composable
fun UserAccountPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background1),
                contentScale = ContentScale.Crop
            )
    ) {
        UserAccount(navController = rememberNavController(), "")
    }
}


@Composable
fun UserAccount(navController: NavController, token : String) {
    if (false)
        navController.navigate(Screen.UserAccount.withArgs(token))

}