package com.example.lab_week_09

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import com.example.lab_week_09.ui.theme.LAB_WEEK_09Theme

data class Student(val name: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LAB_WEEK_09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { Home(navController) }
        composable("resultContent/?listData={listData}") { backStackEntry ->
            val listData = backStackEntry.arguments?.getString("listData")
            ResultContent(listData)
        }
    }
}

@Composable
fun Home(navController: NavController) {
    val nameState = remember { mutableStateOf("") }
    val studentList = remember {
        mutableStateListOf(
            Student("Tanu"),
            Student("Tina"),
            Student("Tono")
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text("Enter Student Name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (nameState.value.isNotBlank()) {
                    studentList.add(Student(nameState.value))
                    nameState.value = ""
                }
            }
        ) {
            Text("Submit")
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(studentList) { item ->
                Text(text = item.name)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val type = Types.newParameterizedType(List::class.java, Student::class.java)
            val adapter = moshi.adapter<List<Student>>(type)
            val json = adapter.toJson(studentList)
            val encoded = Uri.encode(json)

            navController.navigate("resultContent/?listData=$encoded")
        }) {
            Text("Go to Result Content")
        }
    }
}

@Composable
fun ResultContent(listData: String?) {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val type = Types.newParameterizedType(List::class.java, Student::class.java)
    val adapter = moshi.adapter<List<Student>>(type)

    val decodedList = remember {
        try {
            if (listData.isNullOrBlank()) emptyList()
            else adapter.fromJson(Uri.decode(listData)) ?: emptyList()
        } catch (e: Exception) {
            emptyList<Student>()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Result Content", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(decodedList) { item ->
                Text(text = item.name)
            }
        }
    }
}
