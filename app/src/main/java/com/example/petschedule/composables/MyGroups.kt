package com.example.petschedule.composables

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.R
import com.example.petschedule.entities.Group
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


@Preview
@Composable
fun MyGroupsPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background),
                contentScale = ContentScale.Crop
            )
    ) {
        MyGroups(navController = rememberNavController(), "")
    }
}


@Composable
fun MyGroups(navController: NavController, token: String) {
    if (false)
        navController.navigate(Screen.MyGroups.route)
    val context = LocalContext.current
    val groups = remember {
        getGroups(token, context)
    }
    val state = remember {
        mutableStateOf(Group("", ""))
    }
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
        items(groups) { group ->
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
                    text = "${group.id}:${group.name}",
                    color = Color.Blue,
                    fontSize = 30.sp
                )
            }
        }
    }
}

private fun getGroups(
    token: String,
    context: Context
): MutableList<Group> {
    val url = "http://localhost:8091/groups/"
    val groups = mutableListOf<Group>()
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.GET,
        url,
        { response ->
            val obj = JSONArray(response)
            Log.d("MyLog", "obj length = ${obj.length()}")
            for (i in 0 until obj.length()) {
                Log.d("MyLog", "i = $i, value = ${obj.getString(i)}")
                val jsonGroup = JSONObject(obj.getString(i))
                groups.add(Group(jsonGroup.get("id").toString(), jsonGroup.get("name").toString()))
            }
        },
        { error ->
            Log.d("MyLog", "Error $error")
        }) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = token
            return headers
        }
    }
    queue.add(stringRequest)
    return groups
}

private fun createGroup(
    token: String,
    name: String,
    groupState: MutableState<Group>,
    context: Context
) {
    val url = "http://localhost:8091/groups/create" +
            "?name=$name"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.POST,
        url,
        { response ->
            var obj = JSONObject(response)
            groupState.value = Group(obj.get("id").toString(), obj.get("name").toString())
        },
        { error ->
            Log.d("MyLog", "Error $error")
        }) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = token
            return headers
        }
    }
    queue.add(stringRequest)
}