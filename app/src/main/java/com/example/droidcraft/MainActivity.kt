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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Note(val id: Int, val title: String, val content: String, val category: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    NoteApp()
                }
            }
        }
    }
}

@Composable
fun NoteApp() {
    var isLocked by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    val categories = listOf("All", "Work", "Personal", "Ideas")
    var selectedCategory by remember { mutableStateOf("All") }
    val notes = remember { mutableStateListOf(
        Note(1, "Project Deadline", "Complete the documentation", "Work"),
        Note(2, "Buy Milk", "Don't forget the grocery list", "Personal"),
        Note(3, "Compose Article", "Ideas for Android development", "Ideas")
    )}

    if (isLocked) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter Password") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { if (password == "1234") isLocked = false }) {
                Text("Unlock App")
            }
        }
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {}) { Icon(Icons.Default.Add, contentDescription = "Add") }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("My Notes", style = MaterialTheme.typography.headlineMedium)
                
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

                LazyColumn {
                    items(notes.filter { selectedCategory == "All" || it.category == selectedCategory }) { note ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(note.title, fontWeight = FontWeight.Bold)
                                Text(note.content, style = MaterialTheme.typography.bodyMedium)
                                Text(note.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}