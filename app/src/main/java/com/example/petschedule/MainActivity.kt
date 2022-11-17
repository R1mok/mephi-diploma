package com.example.petschedule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.petschedule.composables.LoginPage
import com.example.petschedule.ui.theme.PetScheduleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetScheduleTheme {
                Navigation()
            }
        }
    }
}
