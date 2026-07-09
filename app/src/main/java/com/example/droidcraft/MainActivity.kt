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
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NotePadApp()
                }
            }
        }
    }
}

@Composable
fun NotePadApp() {
    var isLocked by remember { mutableStateOf(true) }
    var pinInput by remember { mutableStateOf("") }
    val correctPin = "1234"

    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Lock, contentDescription = "Lock", modifier = Modifier.size(64.dp))
            Text("App Locked", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(
                value = pinInput,
                onValueChange = { pinInput = it },
                label = { Text("Enter PIN (1234)") },
                singleLine = true
            )
            Button(onClick = { if (pinInput == correctPin) isLocked = false }) {
                Text("Unlock")
            }
        }
    } else {
        NoteScreen()
    }
}

@Composable
fun NoteScreen() {
    val categories = listOf("All", "Work", "Personal", "Ideas")
    var selectedCategory by remember { mutableStateOf("All") }
    val notes = remember {
        mutableStateListOf(
            Note(1, "Meeting", "Discuss project", "Work"),
            Note(2, "Gym", "Leg day", "Personal"),
            Note(3, "App Idea", "Compose note app", "Ideas")
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Notes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(notes.filter { selectedCategory == "All" || it.category == selectedCategory }) { note ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold)
                        Text(note.content, style = MaterialTheme.typography.bodyMedium)
                        Text(note.category, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}