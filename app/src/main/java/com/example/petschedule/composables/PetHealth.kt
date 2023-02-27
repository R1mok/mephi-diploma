package com.example.petschedule.composables

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Paint
import android.graphics.PointF
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.petschedule.MainActivity
import org.json.JSONArray
import org.json.JSONObject
import java.math.RoundingMode
import java.util.*

@SuppressLint("MutableCollectionMutableState")
@Composable
fun PetHealth(token: String, petId: String, petName: String) {

    val petParametersList = remember {
        mutableStateOf(mutableListOf<PetParameters>())
    }

    val context = LocalContext.current
    getPetParameters(context, token, petId, petParametersList)

    // test points
    // val hpoints = listOf(120f, 130f, 150f, 100f, 230f, 122f, 170f, 200f)
    // val wpoints = listOf(130f, 100f, 150f, 200f, 250f, 200f, 150f, 170f)
    //
    var heights = mutableListOf<Double>()
    for (elem in petParametersList.value) {
        heights.add(elem.height)
    }

    var weights = mutableListOf<Double>()
    for (elem in petParametersList.value) {
        weights.add(elem.weight)
    }

    Column(
        modifier = Modifier
            .padding(vertical = 50.dp)
            .padding(horizontal = 10.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        Text(
            text = petName,
            style = TextStyle(fontSize = 25.sp, color = Color.DarkGray),
            modifier = Modifier
                .background(color = Color.Transparent)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.padding(vertical = 20.dp))
        // weights
        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            fontSize = 18.sp,
            text = "Вес питомца"
        )
        if (petParametersList.value.size != 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                contentAlignment = Center
            ) {
                val yStep = weights.max() / weights.size
                var xValues = (0..9).map { it + 1 }
                var yValues = (weights.indices).map {
                    ((it + 1) * yStep)
                        .toBigDecimal()
                        .setScale(2, RoundingMode.DOWN)
                        .toFloat()
                }
                var verticalStep = yStep
                var paddingSpace = 16.dp

                val controlPoints1 = mutableListOf<PointF>()
                val controlPoints2 = mutableListOf<PointF>()
                val coordinates = mutableListOf<PointF>()
                val density = LocalDensity.current
                val textPaint = remember(density) {
                    Paint().apply {
                        color = android.graphics.Color.BLACK
                        textAlign = Paint.Align.CENTER
                        textSize = density.run { 12.sp.toPx() }
                    }
                }
                Canvas(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val xAxisSpace = (size.width - paddingSpace.toPx()) / xValues.size
                    val yAxisSpace = size.height / yValues.size
                    //placing x axis points
                    for (i in xValues.indices) {
                        drawContext.canvas.nativeCanvas.drawText(
                            "${xValues[i]}",
                            xAxisSpace * (i + 1),
                            size.height - 30,
                            textPaint
                        )
                    }
                    //placing y axis points
                    for (i in yValues.indices) {
                        drawContext.canvas.nativeCanvas.drawText(
                            "${yValues[i]}",
                            paddingSpace.toPx() / 2f,
                            size.height - yAxisSpace * (i + 1),
                            textPaint
                        )
                    }
                    //placing our x axis points
                    for (i in weights.indices) {
                        val x1 = xAxisSpace * xValues[i]
                        val y1 =
                            size.height - (yAxisSpace * (weights[i] / verticalStep.toFloat()))
                        coordinates.add(PointF(x1, y1.toFloat()))
                        //drawing circles to indicate all the points
                        drawCircle(
                            color = Color.Red,
                            radius = 10f,
                            center = Offset(x1, y1.toFloat())
                        )
                    }
                    //calculating the connection points
                    for (i in 1 until coordinates.size) {
                        controlPoints1.add(
                            PointF(
                                (coordinates[i].x + coordinates[i - 1].x) / 2,
                                coordinates[i - 1].y
                            )
                        )
                        controlPoints2.add(
                            PointF(
                                (coordinates[i].x + coordinates[i - 1].x) / 2,
                                coordinates[i].y
                            )
                        )
                    }
                    //drawing the path
                    val stroke = Path().apply {
                        reset()
                        moveTo(coordinates.first().x, coordinates.first().y)
                        for (i in 0 until coordinates.size - 1) {
                            cubicTo(
                                controlPoints1[i].x, controlPoints1[i].y,
                                controlPoints2[i].x, controlPoints2[i].y,
                                coordinates[i + 1].x, coordinates[i + 1].y
                            )
                        }
                    }

                    //filling the area under the path
                    val fillPath = android.graphics.Path(stroke.asAndroidPath())
                        .asComposePath()
                        .apply {
                            lineTo(xAxisSpace * xValues.last(), size.height - yAxisSpace)
                            lineTo(xAxisSpace, size.height - yAxisSpace)
                            close()
                        }
                    drawPath(
                        fillPath,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Cyan,
                                Color.Transparent,
                            ),
                            endY = size.height - yAxisSpace
                        ),
                    )
                    drawPath(
                        stroke,
                        color = Color.Black,
                        style = Stroke(
                            width = 5f,
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 20.dp))
            // heights
            Text(
                modifier = Modifier.padding(vertical = 10.dp),
                fontSize = 18.sp,
                text = "Рост питомца"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                contentAlignment = Center
            ) {
                val yStep = heights.max() / heights.size
                var xValues = (0..9).map { it + 1 }
                var yValues = (heights.indices).map {
                    ((it + 1) * yStep)
                        .toBigDecimal()
                        .setScale(2, RoundingMode.DOWN)
                        .toFloat()
                }
                var verticalStep = yStep
                var paddingSpace = 16.dp

                val controlPoints1 = mutableListOf<PointF>()
                val controlPoints2 = mutableListOf<PointF>()
                val coordinates = mutableListOf<PointF>()
                val density = LocalDensity.current
                val textPaint = remember(density) {
                    Paint().apply {
                        color = android.graphics.Color.BLACK
                        textAlign = Paint.Align.CENTER
                        textSize = density.run { 12.sp.toPx() }
                    }
                }
                Canvas(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val xAxisSpace = (size.width - paddingSpace.toPx()) / xValues.size
                    val yAxisSpace = size.height / yValues.size
                    // placing x axis points
                    for (i in xValues.indices) {
                        drawContext.canvas.nativeCanvas.drawText(
                            "${xValues[i]}",
                            xAxisSpace * (i + 1),
                            size.height - 30,
                            textPaint
                        )
                    }
                    //placing y axis points
                    for (i in yValues.indices) {
                        drawContext.canvas.nativeCanvas.drawText(
                            "${yValues[i]}",
                            paddingSpace.toPx() / 2f,
                            size.height - yAxisSpace * (i + 1),
                            textPaint
                        )
                    }
                    //placing our x axis points
                    for (i in heights.indices) {
                        val x1 = xAxisSpace * xValues[i]
                        val y1 =
                            size.height - (yAxisSpace * (heights[i] / verticalStep.toFloat()))
                        coordinates.add(PointF(x1, y1.toFloat()))
                        //drawing circles to indicate all the points
                        drawCircle(
                            color = Color.Red,
                            radius = 10f,
                            center = Offset(x1, y1.toFloat())
                        )
                    }
                    //calculating the connection points
                    for (i in 1 until coordinates.size) {
                        controlPoints1.add(
                            PointF(
                                (coordinates[i].x + coordinates[i - 1].x) / 2,
                                coordinates[i - 1].y
                            )
                        )
                        controlPoints2.add(
                            PointF(
                                (coordinates[i].x + coordinates[i - 1].x) / 2,
                                coordinates[i].y
                            )
                        )
                    }
                    //drawing the path
                    val stroke = Path().apply {
                        reset()
                        moveTo(coordinates.first().x, coordinates.first().y)
                        for (i in 0 until coordinates.size - 1) {
                            cubicTo(
                                controlPoints1[i].x, controlPoints1[i].y,
                                controlPoints2[i].x, controlPoints2[i].y,
                                coordinates[i + 1].x, coordinates[i + 1].y
                            )
                        }
                    }

                    //filling the area under the path
                    val fillPath = android.graphics.Path(stroke.asAndroidPath())
                        .asComposePath()
                        .apply {
                            lineTo(xAxisSpace * xValues.last(), size.height - yAxisSpace)
                            lineTo(xAxisSpace, size.height - yAxisSpace)
                            close()
                        }
                    drawPath(
                        fillPath,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Cyan,
                                Color.Transparent,
                            ),
                            endY = size.height - yAxisSpace
                        ),
                    )
                    drawPath(
                        stroke,
                        color = Color.Black,
                        style = Stroke(
                            width = 5f,
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
        }
        //var height by rememberSaveable { mutableStateOf("") }
        //var weight by rememberSaveable { mutableStateOf("") }
        //var date by rememberSaveable { mutableStateOf("") }

        var isExpandedAddParameters by remember {
            mutableStateOf(false)
        }
        Button(
            onClick = {
                isExpandedAddParameters = !isExpandedAddParameters
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(vertical = 5.dp)
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.Gray
            )
        ) {
            Text(
                text = "Добавить рост и вес питомца",
                style = TextStyle(color = Color.Black, fontSize = 18.sp)
            )
        }
        var newWeight by remember { mutableStateOf("") }
        var newHeight by remember { mutableStateOf("") }
        var mDate = remember { mutableStateOf("") }

        val mCalendar = Calendar.getInstance()
        val mYear = mCalendar.get(Calendar.YEAR)
        val mMonth = mCalendar.get(Calendar.MONTH)
        val mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
        val mDatePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                mDate.value = "${dayOfMonth+1}.${month + 1}.$year"
            }, mYear, mMonth, mDay
        )
        val focusManager = LocalFocusManager.current
        if (isExpandedAddParameters) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = newWeight,
                    onValueChange = { newWeight = it },
                    label = { Text("Вес питомца") },
                    textStyle = TextStyle(fontSize = 25.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray,
                        cursorColor = Color.Black,
                        focusedBorderColor = Color.DarkGray,
                        backgroundColor = Color.White,
                        unfocusedBorderColor = Color.DarkGray,
                        textColor = Color.DarkGray
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(0.9f)
                        .align(Alignment.CenterHorizontally)
                )
                OutlinedTextField(
                    value = newHeight,
                    onValueChange = { newHeight = it },
                    label = { Text("Рост питомца") },
                    textStyle = TextStyle(fontSize = 25.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color.DarkGray,
                        unfocusedLabelColor = Color.DarkGray,
                        cursorColor = Color.Black,
                        focusedBorderColor = Color.DarkGray,
                        backgroundColor = Color.White,
                        unfocusedBorderColor = Color.DarkGray,
                        textColor = Color.DarkGray
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .fillMaxWidth(0.9f)
                        .align(Alignment.CenterHorizontally)
                )
                Button(
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.9f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Gray
                    ),
                    onClick = {
                        mDatePickerDialog.show()
                    }
                ) {
                    Text(
                        text = "Дата измерения ${mDate.value}",
                        style = TextStyle(color = Color.Black, fontSize = 18.sp)
                    )
                }
                Button(
                    onClick = {
                        addPetParameters(
                            context,
                            token,
                            petId,
                            newWeight,
                            newHeight,
                            mDate,
                            petParametersList
                        )
                        isExpandedAddParameters = false
                        newWeight = ""
                        newHeight = ""
                        mDate.value = ""
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.9f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Gray
                    )
                ) {
                    Text(
                        text = "Добавить",
                        style = TextStyle(color = Color.Black, fontSize = 18.sp)
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier.size(300.dp)
        )
        {
            items(petParametersList.value) { elem ->
                Text(
                    text = "Дата: ${elem.date}, вес: ${elem.weight}, рост: ${elem.height}",
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
            }
        }
    }
}

fun addPetParameters(
    context: Context,
    token: String,
    petId: String,
    newWeight: String,
    newHeight: String,
    mDate: MutableState<String>,
    petParametersList: MutableState<MutableList<PetParameters>>
) {

    val url = MainActivity.prefixUrl + "/pets/parameters/add/$petId?" +
            "weight=${newWeight.toDouble()}&height=${newHeight.toDouble()}&date=${mDate.value}"
    val queue = Volley.newRequestQueue(context)
    val stringRequest = object : StringRequest(
        Method.POST,
        url,
        {
            Log.d("MyLog", "Add pet parameters for pet: $petId with weight: $newWeight height: $newHeight date: $mDate")
            getPetParameters(context, token, petId, petParametersList)
        },
        { error ->
            Log.d("MyLog", "Add pet parameters error $error")
        }) {

        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = token
            return headers
        }
    }
    queue.add(stringRequest)
}

@Preview
@Composable
fun GraphView() {
    /*val random = Random
val points = (0..9).map {
    var num = random.nextInt(350)
    if (num <= 50)
        num += 100
    num.toFloat()
}*/
    // val points = listOf(150f,100f,250f,200f,330f,300f,90f,120f,285f,199f)
    PetHealth("", "", "Barsik")
}

fun getPetParameters(
    context: Context,
    token: String,
    petId: String,
    petParametersList: MutableState<MutableList<PetParameters>>
) {
    val url = MainActivity.prefixUrl + "/pets/parameters/get/$petId"
    val queue = Volley.newRequestQueue(context)
    var newPetParametersList = mutableListOf<PetParameters>()
    val stringRequest = object : StringRequest(
        Method.GET,
        url,
        { response ->
            val obj = JSONArray(response)
            Log.d("MyLog", "obj length = ${obj.length()}")
            for (i in 0 until obj.length()) {
                Log.d("MyLog", "i = $i, value = ${obj.getString(i)}")
                val jsonElem = JSONObject(obj.getString(i))
                val weight = jsonElem.getString("weight").toDouble()
                val height = jsonElem.getString("height").toDouble()
                val date = jsonElem.getString("date").toString().substring(0, 10)
                newPetParametersList.add(
                    PetParameters(weight, height, date)
                )
            }
            petParametersList.value = newPetParametersList
        },
        { error ->
            Log.d("MyLog", "Get pet parameters error $error")
        }) {
        override fun getHeaders(): MutableMap<String, String> {
            val headers = HashMap<String, String>()
            headers["Authorization"] = token
            return headers
        }
    }
    queue.add(stringRequest)
}

data class PetParameters(
    val weight: Double,
    val height: Double,
    val date: String
)