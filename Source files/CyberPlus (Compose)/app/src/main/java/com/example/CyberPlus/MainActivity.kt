package com.example.myapplication

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.CyberPlus.ui.theme.MyApplicationTheme
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
                    var reading by remember { mutableStateOf(0.0) }
                    var weight by remember { mutableStateOf(0.0) }

                    reading = (100..160).random().toDouble()

                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row { Text("Live reading:   ${reading} g")}
                        Button(onClick = { weight = reading }) {
                            Text(text = "Capture reading")
                        }
                        Text(text = "Bird mass:\t$weight g")

                        var birdID by remember { mutableStateOf("PMBY") }
                        TextField(
                            value = birdID,
                            onValueChange = { birdID = it },
                            label = { Text("Bird ID") }
                        )

//        data class WeightRecord
//
//        val weightRecords = MutableList<>()


                        fun saveData(text: String) {
//            val path = assets.
                            val filename = "weight records (filesDir).csv"
                            val file = File(applicationContext.filesDir, filename)
                            file.appendText("\n$text")
//                            file.appendText("\n$text" + "\n------------------")
                            val data = file.readText()
//                            android.widget.Toast.makeText(applicationContext, "$data")
                        }

                        fun readData(): String {
                            val filename = "weight records (filesDir).csv"
                            val file = File(applicationContext.filesDir, filename)
                            return file.readText()
                        }

                        var dataLines by remember { mutableStateOf("") }

                        Button(onClick = {
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
                            val ringType = "Colour"
                            val sex = "Male"
                            saveData(listOf(FID, date, time, birdID, ringType, sex, weight).joinToString(separator = ", "))
                            //saveData("FID, Date, Time, ID, Ring type, Sex, Mass")
                            dataLines = readData()
                        }) {
                            Text(text = "Save data")
                        }

                        fun openFileInExternalApp(fileUri: Uri) {
                            val mimeType = "text/csv"
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(fileUri, mimeType)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)

                            try {
                                applicationContext.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                // Handle the case where no app can open the file
                                e.printStackTrace()
                            }
                        }

                        var exportedFilePath by remember {mutableStateOf("")}

                        fun exportData() {

                            val data = readData()

                            try {
                                val externalFilesDir = getExternalFilesDir(null)
                                val path = File(externalFilesDir, "Cyber")
                                if (! path.exists()) {
                                    val res = path.mkdirs()
                                    //Toast.makeText(applicationContext, "${path.path} doesn't exist", Toast.LENGTH_SHORT).show()

                                }

                                val filename = "exported_data.csv"

                                val file = File(path, filename)
                                exportedFilePath = file.path.split("0/")[1]
                                file.writeText(data)

                                val fileSize = file.length()
                                if (fileSize > 0) {
                                    Toast.makeText(applicationContext, "Successful", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()

                                }

//                                val fileUri = Uri.fromFile(file)
//                                openFileInExternalApp(fileUri)




    //if api is higher or equal to android 10 (api 29)|
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//                                    val contentValues = ContentValues().apply {
//                                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
//                                        put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
//                                        put(MediaStore.MediaColumns. RELATIVE_PATH, "Documents/CyberPlus/exports")
//                                    }
//                                        applicationContext.contentResolver.insert(MediaStore. Downloads.EXTERNAL_CONTENT_URI, contentValues)
//                                } else {
//                                    Toast.makeText(applicationContext,
//                                    "Error Exporting Data. File size is 0.",
//                                        Toast.LENGTH_SHORT)
//                                    .show()
//
//                                }



                            } catch (e: Exception) {
                                e.printStackTrace()
                                Log.d("MyActivity", "Failed to make directory?")
                                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                            }

                        }

                        Button(onClick = { exportData() }) {
                            Text(text = "Export data")
                        }

                        Text(text = "CSV file path:\n$exportedFilePath")


                        Button(onClick = {dataLines = readData()}) {
                            Text(text = "Show data")
                        }


                        Text(text = dataLines)

                        //Clear data
                        Button(onClick = {
                            val filename = "weight records (filesDir).csv"
                            val file = File(applicationContext.filesDir, filename)
                            file.writeText("FID, Date, Time, ID, Ring type, Sex, Mass")
                            dataLines = readData()
                        }) {
                            Text("Clear Data")
                        }

                    }

                }
            }
        }
    }


    private fun fileCreate() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain"
        val fileName = "Cyber_data"
        intent.putExtra(Intent.EXTRA_TITLE, "${fileName}.txt")
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)
    }
}



@Composable
fun CyberPlus(modifier: Modifier = Modifier) {






}



@Preview(showBackground = true)
@Composable
fun CyberPlusPreview() {
    MyApplicationTheme {
        CyberPlus(
            Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center))
    }
}