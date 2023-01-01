package com.example.petschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.petschedule.ui.theme.PetScheduleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetScheduleTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .paint(
                        painter = painterResource(id = R.drawable.background1),
                        contentScale = ContentScale.Crop
                    )
                ) {
                }
                Navigation()
            }
        }
    }
}
