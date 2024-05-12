package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.BirdApp.ui.theme.MyApplicationTheme
import java.io.File
import java.lang.Exception
import java.lang.NumberFormatException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val CREATE_FILE_REQUEST_CODE = 1


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                        var reading by remember { mutableStateOf("346.9") }  // reading is hard-coded for unit test
                        Text("Live reading:   $reading g")

                        var weight by remember { mutableStateOf("") }
                        Button(onClick = { weight = reading }) {
                            Text(text = "Capture reading")
                        }

                        TextField(
                            value = weight,
                            onValueChange = { weight = it },
                            label = { Text(text = "Bird mass") },
                            singleLine = true
                        )

                        var birdID by remember { mutableStateOf("") }
                        TextField(
                            value = birdID,
                            onValueChange = { birdID = it },
                            label = { Text("Bird ID") },
                            singleLine = true
                        )


                        fun readData(): String {
                            val filename = "weight records (filesDir).csv"
                            val file = File(applicationContext.filesDir, filename)
                            return file.readText()
                        }

                        var dataLines by remember { mutableStateOf(readData()) }

                        fun saveData() {
                            val data = readData()
                            val lastLine = data.split("\n").last()
                            var lastFID = -1
                            try {
                                lastFID = lastLine.split(",")[0].toInt()
                            } catch (e: NumberFormatException) {
                                println("First record")
                            }
                            val FID = lastFID + 1;
                            val current = LocalDateTime.now()
                            val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                            val text = listOf(FID, date, time, birdID, weight).joinToString(separator = ", ")
                            val filename = "weight records (filesDir).csv"
                            val file = File(applicationContext.filesDir, filename)
                            file.appendText("\n$text")
                            dataLines = readData()
                        }

                        fun clearData() {
                            val filename = "weight records (filesDir).csv"
                            val file = File(applicationContext.filesDir, filename)
                            file.writeText("FID, Date, Time, Bird ID, Mass")
                            dataLines = readData()
                        }

                        Button(onClick = { saveData() }) {Text(text = "Save data")}


                        var csvPathText by remember {mutableStateOf("")}

                        fun exportData() {
                            val data = readData()
                            try {
                                val externalFilesDir = getExternalFilesDir(null)
                                val filename = "exported_data.csv"
                                val file = File(externalFilesDir, filename)
                                file.writeText(data)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(applicationContext, "Failed to export data", Toast.LENGTH_SHORT).show()
                            }
                            Toast.makeText(applicationContext, "Successfully exported data", Toast.LENGTH_SHORT).show()
                        }
                        Button(onClick = { exportData() }) {Text(text = "Export data")}

                        val externalFilesDir = getExternalFilesDir(null)
                        val filename = "exported_data.csv"
                        val file = File(externalFilesDir, filename)
                        val csvPath = file.path.split("/0")[1]  // show path from /storage/emulated/0/ directory
                        csvPathText = "CSV file path:\n$csvPath\n"
                        Text(text = csvPathText)

                        Button(onClick = {clearData()}) {Text("Clear Data")}
                        Text(text = dataLines)

                    }
                }
            }
        }
    }
}