package com.example.petschedule

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetScheduleMain()
        }
    }
}

@Preview
@Composable
private fun PetScheduleMain() {
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
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .offset(y = 200.dp),
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
                modifier = Modifier.offset(y = 400.dp),
                onClick = {
                    context.startActivity(Intent(context, GroupActivity::class.java))
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
}

private fun getGroups() {

}
