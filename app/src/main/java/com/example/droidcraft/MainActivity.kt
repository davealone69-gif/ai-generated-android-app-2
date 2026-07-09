package com.example.droidcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
    var password by remember { mutableStateOf("") }
    
    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("App Locked", style = MaterialTheme.typography.headlineMedium)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter Password (try '123')") },
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Button(onClick = { if (password == "123") isLocked = false }) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Unlock")
            }
        }
    } else {
        NoteScreen()
    }
}

@Composable
fun NoteScreen() {
    val notes = remember { mutableStateListOf(
        Note(1, "Shopping", "Buy milk", "Personal"),
        Note(2, "Work", "Submit report", "Work"),
        Note(3, "Gym", "Leg day", "Personal")
    ) }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Personal", "Work")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Notes", style = MaterialTheme.typography.headlineSmall)
        
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(notes.filter { selectedCategory == "All" || it.category == selectedCategory }) { note ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(note.title, style = MaterialTheme.typography.titleMedium)
                        Text(note.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        FloatingActionButton(onClick = {}, modifier = Modifier.align(Alignment.End)) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}