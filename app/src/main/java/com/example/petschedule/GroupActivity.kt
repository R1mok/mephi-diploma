package com.example.petschedule

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.Header
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.Response
import org.json.JSONObject

class GroupActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Groups()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Preview
@Composable
private fun Groups() {
    val token =
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyMW1vayIsImlhdCI6MTY2ODQ0MjA4NCwiZXhwIjoxNjY5MDQ2ODg0fQ.DDLUxOwbD1KhIo6Gv-HINZ6iZGOCtEbZcC9hjWM5zr0";
    val context = LocalContext.current
    val state = remember {
        mutableStateOf("Unknown")
    }
    Box(
        modifier = Modifier
            .paint(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.Crop
            )
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
        ) {
            Button(
                onClick = { createGroup(token, "groupName", state, context) },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Gray
                )
            ) {
                Text(
                    text = "Создать группу",
                    color = Color.Blue,
                    fontSize = 30.sp,
                )
            }
        }
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 100.dp)
        ) {
            itemsIndexed(
                listOf(state.value)
            ) { index, item ->
                Button(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.padding(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Gray
                    )
                ) {
                    Text(
                        text = "$index: $item",
                        color = Color.Blue,
                        fontSize = 30.sp,
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
private fun createGroup(
    token: String,
    name: String,
    mState: MutableState<String>,
    context: Context
) {
    var url = "https://localhost:8091/groups/create" +
            "?name=$name"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        Request.Method.POST,
        url,
        { response ->
            mState.value = response
        },
        {
            error ->
            Log.d("MyLog", "Error $error")
        })
    /*{
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = token
            return headers
        }
    }*/
    stringRequest.headers
    queue.add(stringRequest)
}