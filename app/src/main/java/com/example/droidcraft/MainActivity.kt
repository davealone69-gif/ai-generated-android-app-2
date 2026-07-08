package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Note(val id: Int, val title: String, val content: String, val category: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    var isLocked by remember { mutableStateOf(true) }
    var pinInput by remember { mutableStateOf("") }
    val notes = remember {
        mutableStateListOf(
            Note(1, "Groceries", "Milk, Eggs, Bread", "Personal"),
            Note(2, "Meeting", "Discuss Q4 strategy", "Work"),
            Note(3, "Workout", "Leg day session", "Health")
        )
    }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Personal", "Work", "Health")

    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
            Text("Enter PIN (1234)", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = pinInput,
                onValueChange = { pinInput = it },
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = { if (pinInput == "1234") isLocked = false }) {
                Text("Unlock")
            }
        }
    } else {
        Scaffold(
            topBar = { TopAppBar(title = { Text("My Notepad") }) },
            floatingActionButton = { FloatingActionButton(onClick = {}) { Icon(Icons.Default.Add, null) } }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
                LazyColumn {
                    items(notes.filter { selectedCategory == "All" || it.category == selectedCategory }) { note ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(note.title, fontWeight = FontWeight.Bold)
                                Text(note.content)
                                Text(note.category, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}